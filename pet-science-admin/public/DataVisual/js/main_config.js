// 设置右部滚动参数 ---START---
$('.myscroll').myScroll({
	speed: 50, //数值越大，速度越慢
	rowHeight: 180 //li的高度
});
// 设置右部滚动参数 ---END---

//顶部时间 ---START---
setInterval(getTime, 1000); // 调用函数

/**
 * 定义获取事件的方法 
 */
function getTime() {
	let myDate = new Date();
	let myYear = myDate.getFullYear(); //获取完整的年份(4位,1970-????)
	let myMonth = myDate.getMonth() + 1; //获取当前月份(0-11,0代表1月)
	let myToday = myDate.getDate(); //获取当前日(1-31)
	let myDay = myDate.getDay(); //获取当前星期X(0-6,0代表星期天)
	let myHour = myDate.getHours(); //获取当前小时数(0-23)
	let myMinute = myDate.getMinutes(); //获取当前分钟数(0-59)
	let mySecond = myDate.getSeconds(); //获取当前秒数(0-59)
	let week = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
	let nowTime;

	nowTime = myYear + '-' + fillZero(myMonth) + '-' + fillZero(myToday) + '&nbsp;&nbsp;' + fillZero(myHour) + ':' +
		fillZero(myMinute) + ':' + fillZero(mySecond) + '&nbsp;&nbsp;' + week[myDay] + '&nbsp;&nbsp;';
	//console.log(nowTime);
	$('#time').html(nowTime);
};

/**
 * 补齐数字2位数
 * @param {日期数字} str 如果个位数，前面补0，例如 7 → 07
 */
function fillZero(str) {
	let realNum;
	if (str < 10) {
		realNum = '0' + str;
	} else {
		realNum = str;
	}
	return realNum;
}
// 顶部时间 ---END---


$('.refresh').click(function() { 
	// 刷新页面
	location.reload();
});
$('.user').click(function() { 
	// 跳转到页面
	location.href = '/dashboard';
});