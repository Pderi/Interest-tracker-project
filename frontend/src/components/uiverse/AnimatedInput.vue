<template>
  <div class="uiverse-input-wrapper">
    <input
      :type="type"
      :value="modelValue"
      :placeholder="placeholder"
      :disabled="disabled"
      :class="['uiverse-input', { 'uiverse-input-focused': isFocused }]"
      @input="handleInput"
      @focus="isFocused = true"
      @blur="isFocused = false"
    />
    <span class="uiverse-input-border"></span>
  </div>
</template>

<script setup lang="ts">
interface Props {
  modelValue?: string | number
  type?: string
  placeholder?: string
  disabled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  disabled: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: string | number]
}>()

const isFocused = ref(false)

function handleInput(event: Event) {
  const target = event.target as HTMLInputElement
  emit('update:modelValue', target.value)
}
</script>

<style scoped>
.uiverse-input-wrapper {
  position: relative;
  width: 100%;
}

.uiverse-input {
  width: 100%;
  padding: 12px 16px;
  font-size: 14px;
  color: #fff;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  outline: none;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.uiverse-input::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.uiverse-input:focus {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(0, 212, 255, 0.5);
}

.uiverse-input:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.uiverse-input-border {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #00d4ff, #00ffcc);
  transition: width 0.3s ease;
  border-radius: 0 0 12px 12px;
}

.uiverse-input-focused ~ .uiverse-input-border {
  width: 100%;
}
</style>

