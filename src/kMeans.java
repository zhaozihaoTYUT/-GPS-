import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
  
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DefaultStringifier;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.MultipleOutputFormat;
import org.apache.hadoop.mapred.lib.MultipleOutputs;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.LineReader;
import org.apache.hadoop.util.Progressable;
  
class Help {
  
    static final boolean DEBUG = false;
  
    public static void debug(Object o, String s) {
        if (DEBUG) {
            System.out.println(s + ":" + o.toString());
        }
    }
  
    public static List<ArrayList<Double>> getOldCenters(String inputPath) {
        List<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
        Configuration conf = new Configuration();
        try {
            FileSystem hdfs = FileSystem.get(conf);
            Path inPath = new Path(inputPath);
            FSDataInputStream fsIn = hdfs.open(inPath);
            LineReader lineIn = new LineReader(fsIn, conf);
            Text line = new Text();
            while (lineIn.readLine(line) > 0) {
  
                String record = line.toString();
                String[] fields = record.split(",");
                List<Double> tmpList = new ArrayList<Double>();
                for (int i = 0; i < fields.length; i++)
                    tmpList.add(Double.parseDouble(fields[i]));
                result.add((ArrayList<Double>) tmpList);
            }
            fsIn.close();
        } catch (IOException e) {
  
            e.printStackTrace();
        }
  
        return result;
    }
  
    public static void deleteLastResult(String path) {
        Configuration conf = new Configuration();
        try {
            FileSystem hdfs = FileSystem.get(conf);
            Path inPath = new Path(path);
            hdfs.delete(inPath);
        } catch (IOException e) {
  
        }
    }
  
    public static void copyOriginalCenters(String src, String dst) {
        Configuration conf = new Configuration();
        try {
            FileSystem hdfs = FileSystem.get(conf);
            hdfs.copyFromLocalFile(new Path(src), new Path(dst));
        } catch (IOException e) {
  
        }
    }
  
    public static boolean isFinished(String oldPath, String newPath,
            String KPath, String dtBegIdxPath, double threshold)
            throws IOException {
  
        int dataBeginIndex = Integer.parseInt(dtBegIdxPath);
        int K = Integer.parseInt(KPath);
        List<ArrayList<Double>> oldCenters = Help.getOldCenters(oldPath);
        List<ArrayList<Double>> newCenters = new ArrayList<ArrayList<Double>>();
        Configuration conf = new Configuration();
        FileSystem hdfs = FileSystem.get(conf);
  
        for (int t = 0; t < K; t++) {
            Path inPath = new Path(newPath + t);
            if (!hdfs.exists(inPath))
                break;
            FSDataInputStream fsIn = hdfs.open(inPath);
            LineReader lineIn = new LineReader(fsIn, conf);
            Text line = new Text();
            while (lineIn.readLine(line) > 0) {
                String tmp = line.toString();
                Help.debug("tmp", tmp);
                 
                if(tmp.length()<5)//处理在集群上出现的key与value不在一行的情况
                {
                    lineIn.readLine(line);
                    tmp = line.toString();
                    String []fields = tmp.split(",");
                    List<Double> tmpList = new ArrayList<Double>();
                    for (int i = 0; i < fields.length; i++)
                        tmpList.add(Double.parseDouble(fields[i]));
                    newCenters.add((ArrayList<Double>) tmpList);
                    continue;
                }
                     
                String[] tmpLine = tmp.split("  ");
                Help.debug(tmpLine[1].toString(), tmpLine.toString());
                String record = tmpLine[1];
                String[] fields = record.split(",");
                List<Double> tmpList = new ArrayList<Double>();
                for (int i = 0; i < fields.length; i++)
                    tmpList.add(Double.parseDouble(fields[i]));
                newCenters.add((ArrayList<Double>) tmpList);
            }
            fsIn.close();
        }
  
        // System.out.println("oldCenter size:"+oldCenters.size()+"\nnewCenters size:"+newCenters.size());
  
        double distance = 0;
        for (int i = 0; i < K; i++) {
            for (int j = dataBeginIndex; j < oldCenters.get(0).size(); j++) {
                double t1 = Math.abs(oldCenters.get(i).get(j));
                double t2 = Math.abs(newCenters.get(i).get(j));
                distance += Math.pow((t1 - t2) / (t1 + t2), 2);
            }
        }
        if (distance <= threshold) {
            return true;
        }
  
        Help.deleteLastResult(oldPath);
        FSDataOutputStream os = hdfs.create(new Path(oldPath));
  
        for (int i = 0; i < newCenters.size(); i++) {
            String text = "";
            for (int j = 0; j < newCenters.get(i).size(); j++) {
                if (j == 0)
                    text += newCenters.get(i).get(j);
                else
                    text += "," + newCenters.get(i).get(j);
            }
            text += "\n";
            os.write(text.getBytes(), 0, text.length());
        }
        os.close();
        // ///////////////////////////
        return false;
    }
}
  
public class Kmeans {
  
    // static List<ArrayList<Double>> centers ;
    // static int K;
    // static int dataBeginIndex;
  
    public static class KmeansMapper extends
            Mapper<Object, Text, IntWritable, Text> {
  
        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
  
            String line = value.toString();
            String[] fields = line.split(",");
  
            List<ArrayList<Double>> centers = Help.getOldCenters(context
                    .getConfiguration().get("centersPath"));
            int dataBeginIndex = Integer.parseInt(context.getConfiguration()
                    .get("dtBegIdxPath"));
            int K = Integer.parseInt(context.getConfiguration().get("KPath"));
  
            double minDistance = 99999999;
            int centerIndex = K;
            for (int i = 0; i < K; i++) {
                double currentDistance = 0;
                for (int j = dataBeginIndex; j < fields.length; j++) {
                    double t1 = Math.abs(centers.get(i).get(j));
                    double t2 = Math.abs(Double.parseDouble(fields[j]));
                    currentDistance += Math.pow((t1 - t2) / (t1 + t2), 2);
                }
                Help.debug(currentDistance, "currentDistance");
                if (minDistance > currentDistance) {
                    minDistance = currentDistance;
                    centerIndex = i;
                }
            }
            IntWritable centerId = new IntWritable(centerIndex+1);
            Text tValue = new Text();
            tValue.set(value);
            context.write(centerId, tValue);
        }
    }
  
    public static class KmeansReducer extends
            Reducer<IntWritable, Text, IntWritable, Text> {
  
        public void reduce(IntWritable key, Iterable<Text> values,
                Context context) throws IOException, InterruptedException {
            List<ArrayList<Double>> helpList = new ArrayList<ArrayList<Double>>();
            String tmpResult = "";
            for (Text val : values) {
                String line = val.toString();
                String[] fields = line.split(",");
                List<Double> tmpList = new ArrayList<Double>();
                for (int i = 0; i < fields.length; i++) {
                    tmpList.add(Double.parseDouble(fields[i]));
                }
                helpList.add((ArrayList<Double>) tmpList);
            }
  
            // System.out.println(helpList.size());
            // for(int i=0;i<helpList.size();i++)
            // System.out.println(helpList.get(i));
  
            for (int i = 0; i < helpList.get(0).size(); i++) {
                double sum = 0;
                for (int j = 0; j < helpList.size(); j++) {
                    sum += helpList.get(j).get(i);
                }
                double t = sum / helpList.size();
                if (i == 0)
                    tmpResult += t;
                else
                    tmpResult += "," + t;
            }
            Text result = new Text();
            result.set(tmpResult);
            int tmpKey = Integer.parseInt(key.toString());
            context.write(new IntWritable(tmpKey), result);
        }
    }
  
    static void runKmeans(String[] args, boolean isReduce) throws IOException,
            ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
  
        String[] otherArgs = new GenericOptionsParser(conf, args)
                .getRemainingArgs();
        if (otherArgs.length != 7) {
            System.err
                    .println("Usage: Kmeans <in> <out> <localOriginalCentersPath> <oldCentersPath> <newCentersPath> <dataBeginIndex> <K>");
            System.exit(2);
        }
  
        conf.setStrings("centersPath", otherArgs[3]);
        conf.setStrings("dtBegIdxPath", otherArgs[5]);
        conf.setStrings("KPath", otherArgs[6]);
  
        Job job = new Job(conf, "kmeans");
        job.setJarByClass(Kmeans.class);
        job.setMapperClass(KmeansMapper.class);
        job.setNumReduceTasks(Integer.parseInt(args[6]));
        // 判断是否需要执行Reduce
        if (isReduce) {
            job.setReducerClass(KmeansReducer.class);
        }
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
  
        // delete last result
        Help.deleteLastResult(otherArgs[1]);
  
        // System.exit(job.waitForCompletion(true)?0:1);
        job.waitForCompletion(true);
  
    }
  
    /**
     * 
     * @param in
     *            - args[0] out - args[1]   localOriginalCentersPath - args[2]
     *            oldCentersPath - args[3]  newCentersPath - args[4]
     *            dataBeginIndex - args[5]  K - args[6]
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
  
        Help.deleteLastResult(args[3]);
        Help.copyOriginalCenters(args[2], args[3]);
  
        int count=1;
//      runKmeans(args, true);
  
        while (true) {
            System.out.println("迭代的轮次： "+count++);
            runKmeans(args, true);
            if (Help.isFinished(args[3], args[4], args[6], args[5], 0.0)) {
                runKmeans(args, false);
                break;
            }
        }
    }
}