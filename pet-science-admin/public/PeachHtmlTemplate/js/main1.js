/**
 * 左1 芯云学院介绍
 */
function index01() {
    $.ajax({
        url: '/api/dataVisual/intro',
        type: 'GET',
        dataType: 'json',
        success: function (result) {
            if (result.code === 200) {
                var data = result.data;
                // 将获取的数据加载到dom中
                $('#main1_introduction1').text(data.introduction);
                $('#main1_introduction2').text(data.registeredUsers);
                $('#main1_introduction3').text(data.totalPets);
                $('#main1_introduction4').text(data.totalOrders);
                $('#main1_introduction5').text(data.annualTransactionValue);
                $('#main1_introduction6').text(data.activeDailyUsers);
                $('#main1_introduction7').text(data.activeWeeklyUsers);
                $('#main1_introduction8').text(data.activeMonthlyUsers);

                // 修改左上角标题
                $('.left-top .title span').text('萌宠视界简介');
            } else {
                console.error('获取数据失败:', result.msg);
            }
        },
        error: function (xhr, status, error) {
            console.error('请求失败:', error);
        }
    });
}
