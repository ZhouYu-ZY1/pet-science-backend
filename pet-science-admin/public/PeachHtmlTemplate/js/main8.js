function index08() {
    $.getJSON('./data/baseResource.json', function (data) {
        console.log(data);
        // main8_baseResource_top 顶部三个
        for (let i = 0; i < 3; i++) {
            $("#main8_baseResource_top").append("<li></li>");
            $("#main8_baseResource_top").children('li').eq(i).append("<p>" + data[i].name + "</p>");
            $("#main8_baseResource_top").children('li').eq(i).append("<span>" + data[i].value + "</span>");
            $("#main8_baseResource_top").children('li').eq(i).append("<label>" + data[i].unity + "</label > ");
        }

        // main8_baseResource_top 底部内容
        for (let i = 3; i < data.length; i++) {
            $("#main8_baseResource_bottom").append("<li></li>");
            $("#main8_baseResource_bottom").children('li').eq(i - 3).append("<span>" + data[i].value + data[i].unity + "</span>");
            $("#main8_baseResource_bottom").children('li').eq(i - 3).append("<p>" + data[i].name + "</p>");
        }
    });
}