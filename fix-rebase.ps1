# 创建 rebase todo 文件
$rebaseFile = $args[0]
$content = "edit 16adcdb feat(photo): 实现后端照片管理和腾讯云COS集成功能`n"
$content += "pick 48a6d39 feat(photo): 实现前端摄影模块与后端对接并优化其它模块封面上传体验`n"
$content += "pick 6ef4099 docs(cos): 更新腾讯云COS配置指南中的占位符`n"
[System.IO.File]::WriteAllText($rebaseFile, $content, [System.Text.UTF8Encoding]::new($false))

