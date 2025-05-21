/**
* 右上内容2-右 基地信息
*/
function index10() {
    $.getJSON('./data/salarySummary.json', function (data) {
        $.each(data.list, function (index, item) {
            $("#main10_salary").append('<li><div class="fontInner clearfix"></div></li>');
            $("#main10_salary").children('li').eq(index).children("div").append("<span><b>"
                + item.collegeName + "</b></span>");
            $("#main10_salary").children('li').eq(index).children("div").append("<span>"
                + item.majorsName + "</span>");
            $("#main10_salary").children('li').eq(index).children("div").append("<span>"
                + item.name + "</span>");
            $("#main10_salary").children('li').eq(index).children("div").append("<span>"
                + item.salary + "</span>");
        });
    });
}