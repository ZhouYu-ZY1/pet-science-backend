/**
 * 左中 宠物类型占比
 */
function loadPetType() {
    $.ajax({
        url: '/api/dataVisual/petTypeProportion', // 修改为新的接口地址
        type: 'GET',
        dataType: 'json',
        success: function(result) {
            if (result.code === 200) {
                let data = result.data; // 获取真实数据

                // 更新数据展示
                $('#totalPets').text(data.totalPets);
                $('#catCount').text(data.catCount);
                $('#dogCount').text(data.dogCount);
                $('#otherCount').text(data.otherCount);

                // 基于准备好的dom，初始化echarts实例
                let myChart = echarts.init(document.getElementById('petTypeChart'));

                // 构造数据
                let ydata = data.list;

                let color = ["#36a2eb", "#97e3ff", "#5ad8a6", "#ffcd56", "#ff9f40", "#ff6384", "#c9cbcf", "#9966ff"];
                // 指定图表的配置项和数据
                let option = {
                    // 更新颜色配置 (保留上一步的颜色)
                    color: color,
                    tooltip: {
                        trigger: 'item',
                        formatter: '{a} <br/>{b} : {c} ({d}%)',
                        // 文字样式（白色）
                        textStyle: {
                            color: '#fff',
                            fontSize: 14
                        },
                        top: 0,
                        // 背景样式（透明黑）
                        backgroundColor: 'rgba(0, 0, 0, 0.6)',
                    },
                    legend: {
                        orient: 'vertical', // 纵向排布
                        right: '10%', // 距离右侧10%
                        top: 'center', // 垂直居中
                        // 将图例位置移到右侧
                        data: ydata.map(item => item.name),
                        textStyle: {
                            color: '#fff',
                            fontSize: '12'
                        }
                    },
                    series: [{
                        name: '宠物数量', // 修改名称
                        type: 'pie',
                        radius: '80%',
                        center: ['20%', '50%'],
                        data: ydata,
                        emphasis: {

                        },
                        label: {
                            // 隐藏默认标签
                            show: false
                        },
                        labelLine: {
                            // 隐藏默认标签线
                            show: false
                        }
                    }]
                };

                // 使用刚指定的配置项和数据显示图表。
                myChart.setOption(option);

                // 图表动画
                index02_animation(myChart, ydata);

                // 设置窗口大小变化时图表自适应
                window.addEventListener('resize', function() {
                    myChart.resize();
                });

            } else {
                console.error('获取宠物类型数据失败:', result.msg);
            }
        },
        error: function(xhr, status, error) {
            console.error('请求宠物类型数据失败:', error);
        }
    });
}



/**
 * 图表动画

 * @param {echarts Dom 对象} myChart
 * @param {图表数据} ydata
 */
function index02_animation(myChart, ydata) {
    let currentIndex = -1;
    setInterval(function() {
        let dataLen = ydata.length;
        // 取消之前高亮的图形
        myChart.dispatchAction({
            type: 'downplay',
            seriesIndex: 0,
            dataIndex: currentIndex
        });
        currentIndex = (currentIndex + 1) % dataLen;
        // 高亮当前图形
        myChart.dispatchAction({
            type: 'highlight',
            seriesIndex: 0,
            dataIndex: currentIndex
        });
        // 显示 tooltip (动画时显示 tooltip，tooltip 内容包含完整信息)
        myChart.dispatchAction({
            type: 'showTip',
            seriesIndex: 0,
            dataIndex: currentIndex
        });
    }, 1000); // 动画间隔时间，单位毫秒
}