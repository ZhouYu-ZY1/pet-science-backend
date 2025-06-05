/**
 * 词云图 - 视频标题关键词
 */
function loadWordCloud() {
    $.ajax({
        url: '/api/dataVisual/videoKeywords',
        type: 'GET',
        dataType: 'json',
        success: function(result) {
            if (result.code === 200) {
                let data = result.data;
                
                // 基于准备好的dom，初始化echarts实例
                let myChart = echarts.init(document.getElementById('wordCloudChart'));
                
                // 指定图表的配置项和数据
                let option = {
                    tooltip: {
                        show: true,
                        formatter: function(params) {
                            return params.name + ': ' + params.value;
                        }
                    },
                    series: [{
                        type: 'wordCloud',
                        shape: 'circle',
                        left: 'center',
                        top: 'center',
                        width: '90%',
                        height: '90%',
                        right: null,
                        bottom: null,
                        sizeRange: [12, 60],
                        rotationRange: [-90, 90],
                        rotationStep: 45,
                        gridSize: 8,
                        drawOutOfBound: false,
                        textStyle: {
                            fontFamily: 'sans-serif',
                            fontWeight: 'bold',
                            color: function () {
                                // 随机颜色
                                return 'rgb(' + [
                                    Math.round(Math.random() * 160) + 95,
                                    Math.round(Math.random() * 160) + 95,
                                    Math.round(Math.random() * 160) + 95
                                ].join(',') + ')';
                            }
                        },
                        emphasis: {
                            focus: 'self',
                            textStyle: {
                                shadowBlur: 10,
                                shadowColor: '#333'
                            }
                        },
                        data: data
                    }]
                };
                
                // 使用配置项和数据显示图表
                myChart.setOption(option);
                
                // 设置窗口大小变化时图表自适应
                window.addEventListener('resize', function() {
                    myChart.resize();
                });
                
            } else {
                console.error('获取视频关键词数据失败:', result.msg);
            }
        },
        error: function(xhr, status, error) {
            console.error('请求视频关键词数据失败:', error);
        }
    });
}

// 页面加载完成后执行
$(document).ready(function() {
    loadWordCloud();
});