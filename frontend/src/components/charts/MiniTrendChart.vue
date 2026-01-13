<template>
  <div ref="chartRef" class="mini-trend-chart" :style="{ width: width, height: height }"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'

interface Props {
  data: number[]
  dates?: string[]
  color?: string
  width?: string
  height?: string
}

const props = withDefaults(defineProps<Props>(), {
  color: '#00d4ff',
  width: '100%',
  height: '40px',
})

const chartRef = ref<HTMLDivElement>()
let chartInstance: echarts.ECharts | null = null

const initChart = () => {
  if (!chartRef.value || !props.data || props.data.length === 0) return
  
  // 如果已经初始化，先销毁
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }

  chartInstance = echarts.init(chartRef.value)
  
  // 确保数据是数字数组
  const data = props.data.map(v => Number(v) || 0)
  
  // 计算Y轴范围
  const maxValue = Math.max(...data, 1) // 至少为1，避免全0时显示异常
  const minValue = Math.min(...data)
  // 如果所有值都是0，设置一个小的范围
  const yMin = minValue === 0 && maxValue === 0 ? 0 : Math.max(0, minValue - (maxValue - minValue) * 0.1)
  const yMax = maxValue === 0 ? 1 : maxValue + (maxValue - minValue) * 0.1 || 1
  
  const option: EChartsOption = {
    grid: {
      left: 0,
      right: 0,
      top: 2,
      bottom: 0,
    },
    xAxis: {
      type: 'category',
      data: props.dates || [],
      show: false,
    },
    yAxis: {
      type: 'value',
      show: false,
      min: yMin,
      max: yMax,
      scale: false, // 不使用自动缩放，保持真实比例
    },
    series: [
      {
        data: data,
        type: 'line',
        smooth: false, // 改为false，显示真实趋势
        symbol: 'none',
        lineStyle: {
          color: props.color,
          width: 2.5,
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {
              offset: 0,
              color: props.color + '66',
            },
            {
              offset: 1,
              color: props.color + '00',
            },
          ]),
        },
      },
    ],
    animation: true,
    animationDuration: 1000,
    animationEasing: 'cubicOut',
  }

  chartInstance.setOption(option)
}

const updateChart = () => {
  if (!chartInstance) return
  
  // 确保数据是数字数组
  const data = props.data.map(v => Number(v) || 0)
  
  // 计算Y轴范围
  const maxValue = Math.max(...data, 1)
  const minValue = Math.min(...data)
  const yMin = minValue === 0 && maxValue === 0 ? 0 : Math.max(0, minValue - (maxValue - minValue) * 0.1)
  const yMax = maxValue === 0 ? 1 : maxValue + (maxValue - minValue) * 0.1 || 1
  
  const option: EChartsOption = {
    xAxis: {
      data: props.dates || [],
    },
    yAxis: {
      min: yMin,
      max: yMax,
    },
    series: [
      {
        data: data,
      },
    ],
  }
  
  chartInstance.setOption(option)
}

watch(() => [props.data, props.dates], (newVal) => {
  if (!newVal[0] || newVal[0].length === 0) return
  
  if (chartInstance) {
    updateChart()
  } else if (chartRef.value) {
    // 如果图表实例不存在，重新初始化
    initChart()
  }
}, { deep: true, immediate: false })

onMounted(() => {
  // 使用 nextTick 确保 DOM 已渲染
  if (props.data && props.data.length > 0) {
    setTimeout(() => {
      initChart()
    }, 100)
  }
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}
</script>

<style scoped>
.mini-trend-chart {
  min-height: 40px;
  width: 100%;
  display: block;
}
</style>

