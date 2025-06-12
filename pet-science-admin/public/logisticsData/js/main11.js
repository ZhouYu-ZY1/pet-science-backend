// 月度运输趋势模块 - main11.js
// 负责处理月度趋势数据的加载和堆叠面积图渲染

// 加载月度运输趋势数据
function loadTrendsData() {
    $.getJSON('./data/trends.json', function(data) {
        renderTrendsChart(data.monthlyTrends);
    }).fail(function() {
        console.error('Failed to load trends data');
    });
}

// 渲染月度运输趋势图表（堆叠面积图）
function renderTrendsChart(data) {
    const chart = echarts.init(document.getElementById('trendsChart'), 'logisticsTheme');

    const option = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                crossStyle: {
                    color: '#00d4ff'
                }
            },
            formatter: function(params) {
                let content = `<div style="text-align: left;">
                    <div style="color: #00d4ff; font-size: 16px; font-weight: bold; margin-bottom: 8px;">
                        ${params[0].axisValue}
                    </div>`;
                
                let total = 0;
                params.forEach(param => {
                    total += param.value;
                    content += `<div style="font-size: 14px; margin-bottom: 4px;">
                        <span style="display: inline-block; width: 10px; height: 10px; background: ${param.color}; margin-right: 8px;"></span>
                        ${param.seriesName}: <span style="color: #00ff88; font-weight: bold;">${param.value}</span> 吨
                    </div>`;
                });
                
                content += `<div style="font-size: 14px; margin-top: 8px; padding-top: 8px; border-top: 1px solid rgba(0, 212, 255, 0.3);">
                    总计: <span style="color: #ffaa00; font-weight: bold;">${total}</span> 吨
                </div></div>`;
                
                return content;
            },
            backgroundColor: 'rgba(0, 30, 60, 0.95)',
            borderColor: '#00d4ff',
            borderWidth: 2,
            textStyle: {
                color: '#ffffff',
                fontSize: 14
            },
            extraCssText: 'box-shadow: 0 0 25px rgba(0, 212, 255, 0.6); border-radius: 8px; padding: 15px;'
        },
        legend: {
            data: data.series.map(item => item.name),
            top: '3%',
            textStyle: {
                color: '#88ccff',
                fontSize: 12
            },
            itemGap: 20
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            top: '35%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: data.months,
            axisLabel: {
                color: '#88ccff',
                fontSize: 11
            },
            axisLine: {
                lineStyle: {
                    color: 'rgba(0, 150, 255, 0.3)'
                }
            },
            axisTick: {
                show: false
            }
        },
        yAxis: {
            type: 'value',
            axisLabel: {
                color: '#88ccff',
                formatter: '{value}吨'
            },
            axisLine: {
                lineStyle: {
                    color: 'rgba(0, 150, 255, 0.3)'
                }
            },
            splitLine: {
                lineStyle: {
                    color: 'rgba(0, 150, 255, 0.1)'
                }
            }
        },
        series: data.series.map(item => ({
            name: item.name,
            type: 'line',
            stack: '总量',
            areaStyle: {
                opacity: 0.6
            },
            emphasis: {
                focus: 'series'
            },
            data: item.data,
            itemStyle: {
                color: item.color
            },
            lineStyle: {
                color: item.color,
                width: 2
            },
            symbol: 'circle',
            symbolSize: 6,
            smooth: true,
            animation: false  // 禁用动画，直接显示最终状态
        }))
    };

    chart.setOption(option);

    // 响应式
    window.addEventListener('resize', function() {
        chart.resize();
    });
}
