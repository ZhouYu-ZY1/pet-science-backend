// /**
// * 右内中容
// */
// function index10() {
//     $.getJSON('./data/salarySummary.json', function (data) {
//         $.each(data.list, function (index, item) {
//             $("#main10_salary").append('<li><div class="fontInner clearfix"></div></li>');
//             $("#main10_salary").children('li').eq(index).children("div").append("<span><b>"
//                 + item.collegeName + "</b></span>");
//             $("#main10_salary").children('li').eq(index).children("div").append("<span>"
//                 + item.majorsName + "</span>");
//             $("#main10_salary").children('li').eq(index).children("div").append("<span>"
//                 + item.name + "</span>");
//             $("#main10_salary").children('li').eq(index).children("div").append("<span>"
//                 + item.salary + "</span>");
//         });
//     });
// }

/**
* 右内中容 - 产品销售额和占比
*/
function loadProductCategorySales() {
    $.ajax({
        url: '/api/dataVisual/productCategorySales',
        type: 'GET',
        dataType: 'json',
        success: function (result) {
            if (result.code === 200) {
                var data = result.data;
                // 清空现有内容
                $("#main10_salary").empty();
                
                // 添加表头
                $("#main10_salary").append('<li class="header"><div class="fontInner clearfix"></div></li>');
                $("#main10_salary").children('li.header').children("div").append("<span>产品类别</span>");
                $("#main10_salary").children('li.header').children("div").append("<span>销售额(元)</span>");
                $("#main10_salary").children('li.header').children("div").append("<span>订单数量</span>");
                $("#main10_salary").children('li.header').children("div").append("<span>占比</span>");

                // 添加数据行
                $.each(data, function (index, item) {
                    $("#main10_salary").append('<li><div class="fontInner clearfix"></div></li>');
                    $("#main10_salary").children('li').eq(index + 1).children("div").append("<span><b>"
                        + item.name + "</b></span>");
                    $("#main10_salary").children('li').eq(index + 1).children("div").append("<span>"
                        + formatCurrency(item.value) + "</span>");
                    $("#main10_salary").children('li').eq(index + 1).children("div").append("<span>"
                        + formatNumber(item.orderCount) + "</span>");
                    $("#main10_salary").children('li').eq(index + 1).children("div").append("<span>"
                        + item.proportion + "</span>");
                });
            } else {
                console.error('获取数据失败:', result.msg);
            }
        },
        error: function (xhr, status, error) {
            console.error('请求失败:', error);
        }
    });
}