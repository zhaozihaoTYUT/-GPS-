<?php
$dbhost = '';  // mysql服务器主机地址
$dbuser = '';            // mysql用户名
$dbpass = '';          // mysql用户名密码
$conn = mysqli_connect($dbhost, $dbuser, $dbpass);

class coordinate{
	public $lng;
	public $lat;
}
if(! $conn )
{
    die('连接失败: ' . mysqli_error($conn));
}
// 设置编码，防止中文乱码
mysqli_query($conn , "set names utf8");

// 表1
$sql = 'SELECT lat, lng
        FROM 1vwholeDay';
 
mysqli_select_db( $conn, 'gps_test' );
$retval = mysqli_query( $conn, $sql );
if(! $retval )
{
    die('无法读取数据: ' . mysqli_error($conn));
}

while($row = mysqli_fetch_array($retval, MYSQLI_ASSOC))
{
    $coordinate = new coordinate();
    $lat_str = $row['lat'];
    $lng_str = $row['lng'];
    $coordinate->lat = (float)$lat_str;
    $coordinate->lng = (float)$lng_str;
    $data[] = $coordinate;
    $json = json_encode($data);//把数据转换为JSON数据.
    //echo $data;
    //var gpsPoint = new BMap.Point(xx,yy);
}

//
// 表 2
$sql2 = 'SELECT lat, lng
        FROM 2vwholeDay';

$retval = mysqli_query($conn, $sql2); 
 
if (!$retval) {
    printf("Error: %s\n", mysqli_error($conn));
    exit();
}
while($row = mysqli_fetch_array($retval, MYSQLI_ASSOC))
{
    $coordinate2 = new coordinate();
    $lat_str = $row['lat'];
    $lng_str = $row['lng'];
    $coordinate2->lat = (float)$lat_str;
    $coordinate2->lng = (float)$lng_str;
    $data2[] = $coordinate2;
    $json2 = json_encode($data2);//把数据转换为JSON数据.
    //echo $data;
    //var gpsPoint = new BMap.Point(xx,yy);
}
//

// 表 3
$sql3 = 'SELECT lat, lng
        FROM 3vwholeDay';

$retval = mysqli_query($conn, $sql3);

while($row = mysqli_fetch_array($retval, MYSQLI_ASSOC))
{
    $coordinate3 = new coordinate();
    $lat_str = $row['lat'];
    $lng_str = $row['lng'];
    $coordinate3->lat = (float)$lat_str;
    $coordinate3->lng = (float)$lng_str;
    $data3[] = $coordinate3;
    $json3 = json_encode($data3);//把数据转换为JSON数据.
    //echo $data;
    //var gpsPoint = new BMap.Point(xx,yy);
}

// 表 4
$sql4 = 'SELECT lat, lng
        FROM 5vwholeDay';

$retval = mysqli_query($conn, $sql4);

while($row = mysqli_fetch_array($retval, MYSQLI_ASSOC))
{
    $coordinate4 = new coordinate();
    $lat_str = $row['lat'];
    $lng_str = $row['lng'];
    $coordinate4->lat = (float)$lat_str;
    $coordinate4->lng = (float)$lng_str;
    $data4[] = $coordinate4;
    $json4 = json_encode($data4);//把数据转换为JSON数据.
    //echo $data;
    //var gpsPoint = new BMap.Point(xx,yy);
}


        
/*echo '<table border="1"><tr><td>lng</td><td>lat</td></tr>';

while($row = mysqli_fetch_array($retval, MYSQLI_ASSOC))
{
    echo "<tr><td> {$row['lat']}</td> ".
         "<td>{$row['lng']} </td> ".
         "</tr>";
         $coordinate = new coordinate();
         $coordinate->lng = $row['lat'];
         $coordinate->lat = $row['lng'];
         $data[] = $coordinate;
         $json = json_encode($data);//把数据转换为JSON数据.
    echo "$json";
}
echo '</table>';*/
mysqli_close($conn);
?>



<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="UTF-8">
		<title>数据可视化分析</title>
		<link rel="stylesheet" href="css/base.css">
		<script src="js/jquery/jQuery-2.2.0.min.js"></script>
		<script src="js/echarts-all.js"></script>
		<script src="js/base.js"></script>
		<script src="js/index.js"></script>
		<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=5ieMMexWmzB9jivTq6oCRX9j&callback"></script>
	</head>
	<body>
		<!--顶部-->
		<header class="header left">
			<div class="left nav">
				<ul>
					<li class="nav_active"><i class="nav_1"></i><a href="index.html">数据概览</a> </li>
					<!--            <li><i class="nav_2"></i><a href="carContrl.html">车辆监控</a> </li>-->
					<li><i class="nav_3"></i><a href="hot_map.html">热力地图</a></li>
				</ul>
			</div>
			<div class="header_center left">
				<h2><strong>出租车GPS数据可视化分析</strong></h2>
				<p class="color_font"><small>大数据技术</small></p>
			</div>
		</header>
		<div class="con left">
			<div class="select_time">
				<div class="static_top left">
					<i></i><span>总体概况</span>
				</div>
			</div>
			<div class="con_div">
				<div class="con_div_text left">
					<div class="con_div_text01 left">
						<img src="img/info_1.png" class="left text01_img" />
						<div class="left text01_div">
							<p>车辆总数(辆)</p>
							<p style="cursor:poiner;">15378</p>
						</div>
					</div>
					<div class="con_div_text01 right">
						<img src="img/info_2.png" class="left text01_img" />
						<div class="left text01_div">
							<p>车辆使用数(辆)</p>
							<p style="cursor:poiner;">13826</p>
						</div>
					</div>
				</div>
				<div class="con_div_text left">
					<div class="con_div_text01 left">
						<img src="img/info_4.png" class="left text01_img" />
						<div class="left text01_div">
							<p>行驶里程总数(km)</p>
							<p style="cursor:poiner;" class="sky">4547520</p>
						</div>
					</div>
					<div class="con_div_text01 right">
						<img src="img/info_5.png" class="left text01_img" />
						<div class="left text01_div">
							<p>行驶里程平均数(km)</p>
							<p style="cursor:poiner;" class="sky">362.8</p>
						</div>
					</div>
				</div>
				<div class="con_div_text left">

					<div class="con_div_text01 left">
						<img src="img/info_6.png" class="left text01_img" />
						<div class="left text01_div">
							<p>行驶时长总数(s)</p>
							<p style="cursor:poiner;" class="org">101056</p>
						</div>
					</div>
					<div class="con_div_text01 right">
						<img src="img/info_7.png" class="left text01_img" />
						<div class="left text01_div">
							<p>行驶时长平均数(h)</p>
							<p class="org">10.3</p>
						</div>
					</div>
				</div>
			</div>
			<!--统计分析图-->
			<div class="div_any">
				<div class="left div_any01">
					<div class="div_any_child">
						<div class="div_any_title"><img src="img/title_1.png">行驶区域统计 </div>
						<p id="char1" class="p_chart"></p>
					</div>
					<div class="div_any_child">
						<div class="div_any_title"><img src="img/title_2.png">车辆状态统计 </div>
						<p id="char2" class="p_chart"></p>
					</div>
				</div>
				<div class="div_any02 left ">
					<div class="div_any_child div_height">
						<div class="div_any_title any_title_width"><img src="img/title_3.png">车辆行驶地图 </div>
						<div id="map_div"></div>

						<script type="text/javascript">
							var map = new BMap.Map("map_div"); // 创建Map实例
							//map.centerAndZoom(new BMap.Point(116.404, 39.915), 11);  // 初始化地图,设置中心点坐标和地图级别
							//添加地图类型控件
							var size1 = new BMap.Size(10, 50);
							map.addControl(new BMap.MapTypeControl({
								offset: size1,
								mapTypes: [
									BMAP_NORMAL_MAP,
									BMAP_HYBRID_MAP,
								]
							}));
							//    // 编写自定义函数,创建标注
							//    function addMarker(point){
							//        var marker = new BMap.Marker(point);
							//        map.addOverlay(marker);
							//    }
							map.setCurrentCity("成都"); // 设置地图显示的城市 此项是必须设置的
							map.enableScrollWheelZoom(true); //开启鼠标滚轮缩放
							//设备地图颜色
							var mapStyle = {
								style: "midnight"
							};
							map.setMapStyle(mapStyle);


							var PointArr = <?php echo $json ?>;
							var PointArr2 = <?php echo $json2 ?>;
							var PointArr3 = <?php echo $json3 ?>;
							var PointArr4 = <?php echo $json4 ?>;

							// 经纬度数据

							map.centerAndZoom(PointArr, 13); // 根据经纬度显示地图的范围
							map.setViewport(PointArr); // 根据提供的地理区域或坐标设置地图视野

							addStartMarker(new BMap.Point(PointArr[0].lng, PointArr[0].lng), '起点', map);
							var carMk; //先将终点坐标展示的mark对象定义
							//小车行驶图标
							var drivingPoint = new BMap.Icon('./point.jpg', new BMap.Size(52, 26), {
								anchor: new BMap.Size(27, 13),
								imageSize: new BMap.Size(1, 1)
							});
							//终点图标
							var terminalPoint = new BMap.Icon('http://bpic.588ku.com/element_origin_min_pic/00/91/63/8556f178dc85f1e.jpg',
								new BMap.Size(1, 1), {
									anchor: new BMap.Size(20, 45),
									imageSize: new BMap.Size(45, 45)
								});
							var i = 0;
							var interval = setInterval(function() {
								if (i >= PointArr.length) {
									clearInterval(interval);
									return;
								}
								drowLine(map, PointArr[i], PointArr[i + 1]); //画线调用
								i = i + 1;
							}, 800);

							var interval = setInterval(function() {
								if (i >= PointArr.length) {
									clearInterval(interval);
									return;
								}
								drowLine(map, PointArr[i], PointArr[i + 1], 'white'); //画线调用
								drowLine(map, PointArr2[i], PointArr2[i + 1], 'yellow'); //画线调用
								drowLine(map, PointArr3[i], PointArr3[i + 1], 'aqua'); //画线调用
								drowLine(map, PointArr4[i], PointArr4[i + 1], 'lime'); //画线调用
								i = i + 1;
							}, 80);


							// 划线
							function drowLine(map, PointArr, PointArrNext, color) {
								if (PointArrNext != undefined) {
									var polyline = new BMap.Polyline(
										[
											new BMap.Point(PointArr.lng, PointArr.lat),
											new BMap.Point(PointArrNext.lng, PointArrNext.lat)
										], {
											strokeColor: color,
											strokeWeight: 5,
											strokeOpacity: 0.4
										}); //创建折线

									map.addOverlay(polyline);
									addMarkerEnd(new BMap.Point(PointArrNext.lng, PointArrNext.lat), '小车行驶', map, PointArrNext, new BMap.Point(
										PointArr.lng, PointArr.lat)); //添加图标
								} else {
									addMarkerEnd(new BMap.Point(PointArr.lng, PointArr.lat), '终点', map); //添加终点图标
								}
							}
							//添加起始图标
							function addStartMarker(point, name, mapInit) {
								if (name == "起点") {
									var myIcon = new BMap.Icon("http://bpic.588ku.com/element_origin_min_pic/00/91/63/8556f178dc85f1e.jpg", new BMap
										.Size(1, 1), {
											anchor: new BMap.Size(20, 45), //这句表示图片相对于所加的点的位置mapStart
											imageSize: new BMap.Size(45, 45) //图标所用的图片的大小，此功能的作用等同于CSS中的background-size属性。可用于实现高清屏的高清效果
											// offset: new BMap.Size(-10, 45), // 指定定位位置
											// imageOffset: new BMap.Size(0, 0 - 10 * 25) // 设置图片偏移
										});
									window.marker = new BMap.Marker(point, {
										icon: myIcon
									}); // 创建标注
									mapInit.addOverlay(marker); // 将标注添加到地图中
									//marker.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
								}
							}
							//添加行驶和终点图标
							function addMarkerEnd(point, name, mapInit, trackUnit, prePoint) {
								if (name == "小车行驶") {
									if (carMk) { //先判断第一次进来的时候这个值有没有定义，有的话就清除掉上一次的。然后在进行画图标。第一次进来时候没有定义也就不走这块，直接进行画图标
										mapInit.removeOverlay(carMk);
									}
									carMk = new BMap.Marker(point, {
										icon: drivingPoint
									}); // 创建标注
									carMk.setRotation(trackUnit.route); //trackUnit.route
									//getAngle(point,prePoint);// js求解两点之间的角度
									carMk.setRotation(getAngle(point, prePoint) - 90); // 旋转的角度
									mapInit.addOverlay(carMk); // 将标注添加到地图中
									//carMk.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
								} else {
									mapInit.removeOverlay(carMk);
									carMk = new BMap.Marker(point, {
										icon: terminalPoint
									}); // 创建标注
									mapInit.addOverlay(carMk);
								}
							}
							//获得角度的函数
							function getAngle(n, next) {
								var ret
								var w1 = n.lat / 180 * Math.PI
								var j1 = n.lng / 180 * Math.PI

								var w2 = next.lat / 180 * Math.PI
								var j2 = next.lng / 180 * Math.PI

								ret = 4 * Math.pow(Math.sin((w1 - w2) / 2), 2) - Math.pow(Math.sin((j1 - j2) / 2) * (Math.cos(w1) - Math.cos(w2)),
									2);
								ret = Math.sqrt(ret);

								// var temp = Math.sin(Math.abs(j1 - j2) / 2) * (Math.cos(w1) + Math.cos(w2));
								var temp = Math.sin((j1 - j2) / 2) * (Math.cos(w1) + Math.cos(w2));
								//console.log(temp)
								ret = ret / temp;

								ret = Math.atan(ret) / Math.PI * 180;
								ret += 90;
								if (j1 - j2 < 0) {
									if (w1 - w2 < 0) {
										ret;
									} else {
										ret = -ret + 180;
									}
								} else {
									if (w1 - w2 < 0) {
										ret = 180 + ret;
									} else {
										ret = -ret;
									}
								}
								return ret;
							}

							//加载城市控件
							var size = new BMap.Size(10, 50);
							map.addControl(new BMap.CityListControl({
								anchor: BMAP_ANCHOR_TOP_LEFT,
								offset: size,
							}));
						</script>
					</div>
				</div>
				<div class="right div_any01">
					<div class="div_any_child">
						<div class="div_any_title"><img src="img/title_4.png">车辆速度统计 </div>
						<p id="char3" class="p_chart"></p>
					</div>
					<div class="div_any_child">
						<div class="div_any_title"><img src="img/title_5.png">载客车辆统计 </div>
						<p id="char4" class="p_chart"></p>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
