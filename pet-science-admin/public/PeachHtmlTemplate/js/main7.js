/**
 * 中间底部2-右 全国新生报到
 */
function index07() {
    $.getJSON('./data/newcomerStudentSummary.json', function(data) {
        // 基于准备好的dom，初始化echarts实例
        let myChart = echarts.init(document.getElementById('main7_newcomer'));

        // 构造数据
        let mydata = data;
        let color = ["#00ffff", "#00ff99", "#99ff00", "#ffff00", "#ff9900", "#ff6666", "#ff0099", "#9966ff"];

        // 指定图表的配置项和数据
        let option = {
            color: color,
            title: {
                text: '全国新生报到',
                textStyle: {
                    color: '#fff',
                    fontSize: '18'
                },
                top: '0%',
                left: 'center'
            },
            tooltip: {
                trigger: 'item',
                formatter: '{a} <br/>{b} : {c} ({d}%)'
            },
            legend: {
                orient: 'horizontal',
                bottom: '0%',
                left: 'center',
                itemWidth: 15,
                itemHeight: 10,
                data: mydata.map(item => item.name),
                textStyle: {
                    color: '#fff',
                    fontSize: '12'
                }
            },
            series: [{
                name: '专业人数',
                type: 'pie',
                radius: ['30%', '50%'],
                center: ['50%', '45%'],
                roseType: false,
                avoidLabelOverlap: false,
                data: mydata,
                label: {
                    show: true,
                    position: 'outside',
                    formatter: function(params) {
                        return params.value + '\n' + params.percent + '%';
                    },
                    color: '#fff',
                    fontSize: 12,
                    fontWeight: 'bold'
                },
                labelLine: {
                    show: true,
                    length: 10,
                    length2: 10,
                    lineStyle: {
                        color: '#fff'
                    }
                },
                itemStyle: {
                    borderRadius: 0
                }
            }]
        };

        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);

        // 图表动画
        index07_animation(myChart, option);

        // 设置窗口大小变化时图表自适应
        window.addEventListener('resize', function() {
            myChart.resize();
        });
    });
}

/**
 * 图表动画
 * @param {echarts Dom 对象} myChart
 * @param {图表配置项} option
 */
function index07_animation(myChart, option) {
    let currentIndex = -1;
    setInterval(function() {
        let dataLen = option.series[0].data.length;
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
    }, 1000); // 动画间隔时间，单位毫秒
}