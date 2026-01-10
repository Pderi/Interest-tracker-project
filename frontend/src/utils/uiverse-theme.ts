/**
 * Uiverse.io 组件主题适配工具
 * 确保所有 Uiverse.io 组件使用项目统一的主题色
 */

export const UIVERSE_THEME = {
  // 主色调
  primary: '#00d4ff',
  primaryHover: '#00ffcc',
  
  // 背景色
  bgDark: '#1a1a2e',
  bgGradient: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%)',
  
  // 玻璃态效果
  glassBg: 'linear-gradient(to bottom right, rgba(255, 255, 255, 0.05), rgba(255, 255, 255, 0.03), rgba(255, 255, 255, 0.05))',
  glassBorder: 'rgba(255, 255, 255, 0.1)',
  
  // 阴影
  shadowPrimary: '0 4px 15px rgba(0, 212, 255, 0.3)',
  shadowPrimaryHover: '0 6px 20px rgba(0, 212, 255, 0.5)',
  shadowGlow: '0 0 20px rgba(0, 212, 255, 0.4)',
  
  // 动画时长
  transitionFast: '0.2s',
  transitionNormal: '0.3s',
  transitionSlow: '0.5s',
  
  // 边框圆角
  borderRadius: '12px',
  borderRadiusLarge: '16px',
} as const

/**
 * 获取渐变背景
 */
export function getGradientBg(direction: 'horizontal' | 'vertical' | 'diagonal' = 'diagonal') {
  const directions = {
    horizontal: 'to right',
    vertical: 'to bottom',
    diagonal: '135deg',
  }
  
  return `linear-gradient(${directions[direction]}, ${UIVERSE_THEME.primary} 0%, ${UIVERSE_THEME.primaryHover} 100%)`
}

/**
 * 获取玻璃态样式
 */
export function getGlassStyle(opacity: number = 0.05) {
  return {
    background: `linear-gradient(to bottom right, rgba(255, 255, 255, ${opacity}), rgba(255, 255, 255, ${opacity * 0.6}))`,
    backdropFilter: 'blur(20px)',
    border: `1px solid ${UIVERSE_THEME.glassBorder}`,
  }
}

/**
 * 获取霓虹发光效果
 */
export function getNeonGlow(intensity: 'low' | 'medium' | 'high' = 'medium') {
  const intensities = {
    low: {
      shadow: `0 0 8px rgba(0, 212, 255, 0.3), 0 0 15px rgba(0, 212, 255, 0.2)`,
    },
    medium: {
      shadow: `0 0 12px rgba(0, 212, 255, 0.4), 0 0 25px rgba(0, 212, 255, 0.3), 0 0 40px rgba(0, 255, 204, 0.2)`,
    },
    high: {
      shadow: `0 0 20px rgba(0, 212, 255, 0.6), 0 0 40px rgba(0, 212, 255, 0.4), 0 0 60px rgba(0, 255, 204, 0.3)`,
    },
  }
  
  return intensities[intensity]
}

/**
 * CSS 变量注入（用于全局样式）
 */
export function injectThemeVariables() {
  const root = document.documentElement
  
  root.style.setProperty('--uiverse-primary', UIVERSE_THEME.primary)
  root.style.setProperty('--uiverse-primary-hover', UIVERSE_THEME.primaryHover)
  root.style.setProperty('--uiverse-bg-dark', UIVERSE_THEME.bgDark)
  root.style.setProperty('--uiverse-transition-normal', UIVERSE_THEME.transitionNormal)
  root.style.setProperty('--uiverse-border-radius', UIVERSE_THEME.borderRadius)
}

