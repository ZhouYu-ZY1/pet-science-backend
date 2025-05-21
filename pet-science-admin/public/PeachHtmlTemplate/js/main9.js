/**
* 右上内容2-右 基地信息
*/
function index09() {
    $.getJSON('./data/baseInfo.json', function (data) {
        // 循环获取的数据（数组）
        $.each(data, function (index, item) {
            $("#main9_baseInfo").append("<span>" + item.name + "：</span>");
            $("#main9_baseInfo").children('span').eq(index).append("<strong>" + item.value + "</strong>");
            $("#main9_baseInfo").children('span').eq(index).append(item.unity);
        });
    });
}