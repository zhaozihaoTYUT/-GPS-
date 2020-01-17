import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;
import org.apache.log4j.BasicConfigurator;
 
import java.io.IOException;
import java.util.StringTokenizer;
 
public class DualSort {
    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure(); // 使用默认的日志配置，可以在idea运行时显示日志
        Configuration conf = new Configuration();
        // 判断输出路径是否存在，如果存在，则删除
        Path outPath = new Path(args[1]);
        FileSystem hdfs = outPath.getFileSystem(conf);
        if (hdfs.isDirectory(outPath)) {
            hdfs.delete(outPath, true);
        }
 
        // 创建一个任务并配置
        Job job = Job.getInstance(conf, "DualSort");
        job.setJarByClass(DualSort.class);
        job.setMapperClass(DualSortMapper.class);
        job.setPartitionerClass(DualSortPartition.class);
        job.setSortComparatorClass(DualSortCompare.class);
        job.setGroupingComparatorClass(DualGroupingComparator.class);
        job.setReducerClass(DualSortReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);
 
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
 
        // 执行任务
        System.out.println(job.waitForCompletion(true) ? 0 : 1);
    }
}
 
class DualSortMapper extends Mapper<Object, Text, Text, NullWritable> {
    private Text outKey = new Text();
 
    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        StringTokenizer stringTokenizer = new StringTokenizer(value.toString());
        if(stringTokenizer.hasMoreTokens()) {
            // 第一列和第二列共同作为 key，中间用制表符隔开，value为空即可
            outKey.set(stringTokenizer.nextToken() + "\t" + stringTokenizer.nextToken());
            context.write(outKey, NullWritable.get());
        }
    }
}
 
/**
 * 使用自定义分区类决定将key发送给哪个Reducer结点进行处理
 */
class DualSortPartition extends HashPartitioner<Text, Text> {
    @Override
    public int getPartition(Text key, Text value, int numReduceTasks) {
        int key_first = Integer.parseInt(key.toString().split("\t")[0]);
        // key是 1~10 的数，key的空间大小为10，假设key是均匀分布的
        if(key_first < 1) return 0; // not reachable
        if(key_first > 10) return numReduceTasks - 1; // not reachable
        int size = 10 / numReduceTasks; // 每一台机器处理的key值的数量
        // 如果reducer过多size会成为0，key的空间大小为10，一台机器最少处理一个key
        if(size == 0) {
            size = 1;
        }
        // 根据key划分到不同的Reducer结点
        int part = (key_first - 1) / size;
        if(part >= numReduceTasks) {
            part = numReduceTasks - 1;
        }
        return part;
    }
}
 
 
/**
 * 使用自定义类对key进行排序
 * 对key的第一列进行升序，第二列进行降序
 */
class DualSortCompare extends WritableComparator {
    DualSortCompare() {
        super(Text.class, true);
    }
 
    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        String[] a_tokens = a.toString().split("\t");
        String[] b_tokens = b.toString().split("\t");
        int a_first = Integer.parseInt(a_tokens[0]);
        int b_first = Integer.parseInt(b_tokens[0]);
        // 先比较第一列，当第一列相等时比较第二列
        if(a_first != b_first) {
            return Integer.compare(a_first, b_first);
        } else {
            int a_second = Integer.parseInt(a_tokens[1]);
            int b_second = Integer.parseInt(b_tokens[1]);
            return Integer.compare(b_second, a_second);
        }
    }
}
 
/**
 * 在分区、排序之后使用自定义分组类决定将key发送给哪个reduce方法进行处理
 * 由于key由两列组成，虽然使用分区将相同的key分配给相同的结点执行，但是Reducer结点会创建和不同key的数量相同的reduce方法处理不同的key，
 * 为了减少reduce方法的执行次数，我们需要key第一列相同的键值对都由一个reduce方法执行，就需要GroupingComparator类将Reducer结点上的key进行分组。
 * Reduce类中的reduce方法中key一样，values有多个，是什么情况下的key是一样的，能不能自定义，这就是GroupingComparator的作用。
 */
class DualGroupingComparator extends WritableComparator {
    DualGroupingComparator() {
        super(Text.class, true);
    }
 
    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        int a_first = Integer.parseInt(a.toString().split("\t")[0]);
        int b_first = Integer.parseInt(b.toString().split("\t")[0]);
        // key第一列相同的归为一组
        return Integer.compare(a_first, b_first);
    }
}
 
class DualSortReducer extends Reducer<Text, NullWritable, Text, NullWritable> {
    @Override
    protected void reduce(Text key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
        // 对排好序的结果遍历输出即可
        for(NullWritable n : values) {
            context.write(key, NullWritable.get());
        }
    }
}