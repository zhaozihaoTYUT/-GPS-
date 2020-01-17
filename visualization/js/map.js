
$(function(){


    initMap();





})
//地图界面高度设置



//加载地图
function initMap(){
// 百度地图API功能
    var map = new BMap.Map("map_div");    // 创建Map实例
    //map.centerAndZoom(new BMap.Point(116.404, 39.915), 11);  // 初始化地图,设置中心点坐标和地图级别
    //添加地图类型控件
    var size1 = new BMap.Size(10, 50);
    map.addControl(new BMap.MapTypeControl({
        offset: size1,
        mapTypes:[
            BMAP_NORMAL_MAP,
            BMAP_HYBRID_MAP,

        ]}));
//    // 编写自定义函数,创建标注
//    function addMarker(point){
//        var marker = new BMap.Marker(point);
//        map.addOverlay(marker);
//    }
    map.setCurrentCity("成都");          // 设置地图显示的城市 此项是必须设置的
    map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
    //设备地图颜色
    var mapStyle={
        style:"midnight"
    };
    map.setMapStyle(mapStyle);


    var PointArr = json;
	//var PointArr2 = <?php echo $json2 ?>;
	//var PointArr3 = <?php echo $json3 ?>;

	// 经纬度数据

	map.centerAndZoom(PointArr, 13);// 根据经纬度显示地图的范围
	map.setViewport(PointArr);// 根据提供的地理区域或坐标设置地图视野

	addStartMarker(new BMap.Point(PointArr[0].lng, PointArr[0].lng),'起点', map);
	var carMk;//先将终点坐标展示的mark对象定义
	//小车行驶图标
	var drivingPoint = new BMap.Icon('./point.jpg', new BMap.Size(52,26), {
		anchor : new BMap.Size(27, 13),
		imageSize:new BMap.Size(1, 1)
	});
	//终点图标
	var terminalPoint = new BMap.Icon('http://bpic.588ku.com/element_origin_min_pic/00/91/63/8556f178dc85f1e.jpg', new BMap.Size(45,45), {
		anchor : new BMap.Size(20, 45),
		imageSize:new BMap.Size(45, 45)
	});
	var i = 0;
	var interval = setInterval(function () {
		if (i >= PointArr.length) {
			clearInterval(interval);
			return;
		}
		drowLine(map,PointArr[i],PointArr[i+1]);//画线调用
		i = i + 1;
	}, 800);

	var interval = setInterval(function () {
		if (i >= PointArr.length) {
			clearInterval(interval);
			return;
		}
		drowLine(map, PointArr[i], PointArr[i+1], 'red');//画线调用
		//drowLine(map, PointArr2[i], PointArr2[i+1], 'green');//画线调用
		//drowLine(map, PointArr3[i], PointArr3[i+1], 'blue');//画线调用
		i = i + 1;
	}, 100);


	// 划线
	function drowLine(map,PointArr,PointArrNext, color) {
		if(PointArrNext!=undefined) {
			var polyline = new BMap.Polyline(
				[
					new BMap.Point(PointArr.lng, PointArr.lat),
					new BMap.Point(PointArrNext.lng, PointArrNext.lat)
				],
				{
					strokeColor: color,
					strokeWeight: 10,
					strokeOpacity: 0.2
				});   //创建折线
			
			map.addOverlay(polyline);
			addMarkerEnd(new BMap.Point(PointArrNext.lng, PointArrNext.lat), '小车行驶', map, PointArrNext, new BMap.Point(PointArr.lng, PointArr.lat));//添加图标
		}else {
			addMarkerEnd(new BMap.Point(PointArr.lng, PointArr.lat), '终点', map);//添加终点图标
		}
	}
	//添加起始图标
	function addStartMarker(point, name,mapInit) {
		if(name=="起点"){
			var myIcon = new BMap.Icon("http://bpic.588ku.com/element_origin_min_pic/00/91/63/8556f178dc85f1e.jpg", new BMap.Size(45,45),{
				anchor: new BMap.Size(20, 45),//这句表示图片相对于所加的点的位置mapStart
				imageSize:new BMap.Size(45, 45)//图标所用的图片的大小，此功能的作用等同于CSS中的background-size属性。可用于实现高清屏的高清效果
				// offset: new BMap.Size(-10, 45), // 指定定位位置
				// imageOffset: new BMap.Size(0, 0 - 10 * 25) // 设置图片偏移
			});
			window.marker = new BMap.Marker(point,{icon:myIcon});  // 创建标注
			mapInit.addOverlay(marker);               // 将标注添加到地图中
			//marker.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
		}
	}
	//添加行驶和终点图标
	function addMarkerEnd(point, name,mapInit,trackUnit,prePoint) {
		if(name=="小车行驶"){
			if(carMk){//先判断第一次进来的时候这个值有没有定义，有的话就清除掉上一次的。然后在进行画图标。第一次进来时候没有定义也就不走这块，直接进行画图标
				mapInit.removeOverlay(carMk);
			}
			carMk = new BMap.Marker(point,{icon:drivingPoint});  // 创建标注
			carMk.setRotation(trackUnit.route);//trackUnit.route
			//getAngle(point,prePoint);// js求解两点之间的角度
			carMk.setRotation(getAngle(point,prePoint)-90);// 旋转的角度
			mapInit.addOverlay(carMk);               // 将标注添加到地图中
			//carMk.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
		}else {
			mapInit.removeOverlay(carMk);
			carMk = new BMap.Marker(point,{icon:terminalPoint});  // 创建标注
			mapInit.addOverlay(carMk);
		}
	}
	//获得角度的函数
	function getAngle(n,next){
		var ret
		var w1 = n.lat/180 * Math.PI
		var j1 = n.lng/180 * Math.PI

		var w2 = next.lat/180 * Math.PI
		var j2 = next.lng/180 * Math.PI

		ret = 4 * Math.pow(Math.sin((w1 - w2) / 2), 2) - Math.pow(Math.sin((j1 - j2) / 2) * (Math.cos(w1) - Math.cos(w2)), 2);
		ret = Math.sqrt(ret);

		// var temp = Math.sin(Math.abs(j1 - j2) / 2) * (Math.cos(w1) + Math.cos(w2));
		var temp = Math.sin((j1 - j2) / 2) * (Math.cos(w1) + Math.cos(w2));
		//console.log(temp)
		ret = ret/temp;

		ret = Math.atan(ret) / Math.PI * 180 ;
		ret += 90;

		// 这里用如此臃肿的if..else是为了判定追踪单个点的具体情况,从而调整ret的值
		if(j1-j2 < 0){
			// console.log('j1<j2')
			if(w1-w2 < 0){
				// console.log('w1<w2')
				ret;
			}else{
				// console.log('w1>w2')
				ret = -ret+180;
			}
		}else{
			// console.log('j1>j2')
			if(w1-w2 < 0){
				// console.log('w1<w2')
				ret = 180+ret;
			}else{
				// console.log('w1>w2')
				ret = -ret;
			}
		}
		return ret ;
	}





//加载城市控件
    var size = new BMap.Size(10, 50);
    map.addControl(new BMap.CityListControl({
        anchor: BMAP_ANCHOR_TOP_LEFT,
        offset: size,


    }));
}

