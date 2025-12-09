# AI 集成方案设计

## 📋 文档说明

**项目名称**：Interest Tracker AI 能力集成  
**版本**：v1.0  
**创建日期**：2025-12-07  
**最后更新**：2025-12-07

本文档详细规划了 Interest Tracker 项目的 AI 能力集成方案，包括 AI 应用场景、技术选型、实现方案和实施步骤。

**重要说明**：本文档优先推荐使用**免费 API 方案**，每个场景都提供了免费实现方案，只有在免费方案无法满足需求时才考虑付费方案。

---

## 一、AI 能力规划总览

### 1.1 核心目标

通过集成 AI 能力，提升用户体验，实现智能化内容管理和个性化推荐。

### 1.2 免费方案优先策略

**实施原则**：
1. **优先使用免费 API**：Hugging Face、Google Gemini 等
2. **规则方案作为基础**：基于规则的实现完全免费
3. **付费 API 作为备选**：仅在免费方案无法满足时使用
4. **降级机制**：免费 API → 付费 API → 本地模型 → 规则方案

**免费方案优势**：
- ✅ **零成本**：完全免费使用
- ✅ **充足额度**：免费 API 额度通常足够个人项目使用
- ✅ **效果可接受**：免费 API 的效果通常能满足需求
- ✅ **灵活切换**：可以随时切换到付费方案

### 1.2 AI 应用场景

| 场景 | AI能力 | 优先级 | 预期效果 |
|------|--------|--------|---------|
| 照片自动分类 | 图像识别 | ⭐⭐⭐⭐⭐ | 自动识别照片内容，生成标签 |
| 照片质量评估 | 图像分析 | ⭐⭐⭐⭐ | 评估照片质量，提供改进建议 |
| 智能标签生成 | NLP | ⭐⭐⭐⭐ | 自动生成照片/影视/音乐的标签 |
| 内容推荐 | 推荐算法 | ⭐⭐⭐⭐⭐ | 基于用户行为智能推荐 |
| 评价生成 | 文本生成 | ⭐⭐⭐ | 自动生成影视/音乐评价 |
| 兴趣画像分析 | 数据分析 | ⭐⭐⭐⭐ | 分析用户兴趣偏好和趋势 |
| 照片描述生成 | 图像理解 | ⭐⭐⭐ | 自动生成照片描述 |

---

## 二、AI 技术选型

### 2.1 方案对比

#### 方案一：免费 API 方案（优先推荐）⭐⭐⭐⭐⭐

**推荐顺序**：优先使用免费 API，降低成本。

##### 2.1.1 Hugging Face Inference API（推荐）⭐⭐⭐⭐⭐

**优势**：
- ✅ **完全免费**：个人项目免费使用
- ✅ **模型丰富**：数千个开源模型可选
- ✅ **中文支持好**：支持中文模型（如 Qwen、ChatGLM）
- ✅ **图像识别**：支持图像分类、图像描述等
- ✅ **无需注册**：可直接使用（注册后额度更高）

**免费额度**：
- 未注册：有限制
- 注册后：每月免费额度充足

**适用场景**：
- 照片自动分类和标签
- 智能标签生成
- 文本生成
- 图像理解

**官网**：https://huggingface.co/inference-api

##### 2.1.2 Google Gemini API（推荐）⭐⭐⭐⭐⭐

**优势**：
- ✅ **免费额度充足**：每月 60 次免费请求（Gemini Pro）
- ✅ **多模态支持**：支持文本、图像、视频
- ✅ **中文支持好**：原生支持中文
- ✅ **API 稳定**：Google 官方维护

**免费额度**：
- Gemini Pro：每月 60 次免费请求
- 注册后可能有更多免费额度

**适用场景**：
- 照片自动分类和描述
- 智能标签生成
- 文本生成
- 多模态分析

**官网**：https://ai.google.dev/

##### 2.1.3 其他免费 API

- **Cohere API**：有免费额度
- **Anthropic Claude API**：有免费试用
- **百度文心一言 API**：可能有免费额度
- **阿里通义千问 API**：可能有免费额度

#### 方案二：OpenAI API（付费，效果好）⭐⭐⭐⭐

**优势**：
- ✅ API 成熟稳定，文档完善
- ✅ 支持多种模型（GPT-4、GPT-3.5、DALL-E、Whisper等）
- ✅ 中文支持好
- ✅ 图像识别能力强（GPT-4 Vision）
- ✅ 文本生成质量高

**劣势**：
- ❌ 需要付费（按使用量计费）
- ❌ 依赖网络连接
- ❌ 数据隐私考虑（数据会发送到 OpenAI）

**适用场景**：
- 照片自动分类和描述
- 智能标签生成
- 评价生成
- 文本分析

**成本估算**：
- GPT-3.5-turbo：$0.0015/1K tokens（输入），$0.002/1K tokens（输出）
- GPT-4 Vision：$0.01/1K tokens（输入），$0.03/1K tokens（输出）
- 预计每月成本：$10-50（取决于使用量）

#### 方案三：本地开源模型

**优势**：
- ✅ 完全免费
- ✅ 数据隐私安全
- ✅ 可离线使用
- ✅ 无 API 调用限制

**劣势**：
- ❌ 需要服务器资源（GPU）
- ❌ 模型效果可能不如商业 API
- ❌ 部署和维护复杂
- ❌ 中文支持可能有限

**适用场景**：
- 对隐私要求高的场景
- 大量数据处理
- 预算有限的项目

**推荐模型**：
- **文本生成**：ChatGLM-6B、Qwen-7B
- **图像识别**：CLIP、BLIP
- **图像分类**：ResNet、EfficientNet

#### 方案四：混合方案（推荐）⭐⭐⭐⭐⭐

**策略**：
- **优先使用**：免费 API（Hugging Face、Gemini）
- **备选方案**：付费 API（OpenAI，效果更好）
- **降级方案**：本地模型（完全免费）
- **缓存机制**：减少 API 调用

**优势**：
- ✅ 最大化利用免费资源
- ✅ 平衡效果和成本
- ✅ 灵活切换
- ✅ 降低风险

---

## 三、AI 应用场景详细设计

### 3.1 场景一：照片自动分类和标签生成 ⭐⭐⭐⭐⭐

#### 3.1.1 功能描述

用户上传照片后，AI 自动识别照片内容，生成分类和标签。

#### 3.1.2 实现方案

##### 方案 A：免费 API 方案（优先推荐）

**使用技术 1：Hugging Face Inference API（推荐）**

**优势**：
- ✅ 完全免费
- ✅ 支持图像分类模型（如 ResNet、EfficientNet）
- ✅ 支持图像描述模型（如 BLIP、CLIP）

**实现步骤**：
1. 使用图像分类模型识别照片内容
2. 使用图像描述模型生成描述
3. 从描述中提取标签

**代码示例**：
```java
@Service
public class PhotoAIService {
    
    @Value("${huggingface.api.token}")
    private String hfToken;
    
    private static final String IMAGE_CLASSIFICATION_MODEL = "google/vit-base-patch16-224";
    private static final String IMAGE_CAPTION_MODEL = "Salesforce/blip-image-captioning-base";
    
    /**
     * 使用 Hugging Face API 分析照片
     */
    public PhotoAnalysisResult analyzePhotoWithHF(String imageUrl) {
        // 1. 图像分类
        List<String> categories = classifyImage(imageUrl);
        
        // 2. 图像描述
        String description = generateImageCaption(imageUrl);
        
        // 3. 提取标签
        List<String> tags = extractTagsFromDescription(description, categories);
        
        return PhotoAnalysisResult.builder()
            .categories(categories)
            .description(description)
            .tags(tags)
            .build();
    }
    
    private List<String> classifyImage(String imageUrl) {
        String url = "https://api-inference.huggingface.co/models/" + IMAGE_CLASSIFICATION_MODEL;
        // 调用 Hugging Face Inference API
        // 返回分类结果
    }
    
    private String generateImageCaption(String imageUrl) {
        String url = "https://api-inference.huggingface.co/models/" + IMAGE_CAPTION_MODEL;
        // 调用图像描述模型
        // 返回描述文本
    }
}
```

**使用技术 2：Google Gemini API（推荐）**

**优势**：
- ✅ 免费额度充足（每月 60 次）
- ✅ 多模态支持（文本+图像）
- ✅ 中文支持好

**代码示例**：
```java
/**
 * 使用 Gemini API 分析照片
 */
public PhotoAnalysisResult analyzePhotoWithGemini(String imageUrl) {
    String prompt = buildPhotoAnalysisPrompt();
    
    GeminiResponse response = geminiClient.analyzeImage(imageUrl, prompt);
    
    return parseGeminiResponse(response);
}
```

##### 方案 B：付费 API 方案（备选）

**使用技术**：GPT-4 Vision API

**流程**：
1. 用户上传照片
2. 调用 GPT-4 Vision API 分析照片
3. 提取关键信息：场景、物体、人物、地点、时间等
4. 生成标签和分类
5. 自动填充到照片信息中

**Prompt 设计**：
```
请分析这张照片，提取以下信息：
1. 主要场景（如：自然风景、城市建筑、人物肖像等）
2. 主要物体和元素
3. 拍摄地点类型（如：室内、室外、海边、山区等）
4. 时间特征（如：白天、夜晚、黄昏等）
5. 情感氛围（如：宁静、热闹、神秘等）
6. 建议的标签（3-5个关键词）

请以JSON格式返回：
{
  "scene": "自然风景",
  "objects": ["山", "云", "树"],
  "location_type": "山区",
  "time_feature": "白天",
  "mood": "宁静",
  "tags": ["自然", "风景", "山", "云", "宁静"]
}
```

**代码示例**：
```java
@Service
public class PhotoAIService {
    
    @Resource
    private OpenAIClient openAIClient;
    
    /**
     * 分析照片并生成标签
     */
    public PhotoAnalysisResult analyzePhoto(String imageUrl) {
        // 1. 构建 Prompt
        String prompt = buildPhotoAnalysisPrompt();
        
        // 2. 调用 GPT-4 Vision API
        VisionResponse response = openAIClient.analyzeImage(
            imageUrl, 
            prompt
        );
        
        // 3. 解析结果
        PhotoAnalysisResult result = parseResponse(response);
        
        return result;
    }
    
    /**
     * 自动填充照片信息
     */
    public void autoFillPhotoInfo(PhotoDO photoDO, PhotoAnalysisResult analysis) {
        // 自动填充标签
        photoDO.setTags(String.join(",", analysis.getTags()));
        
        // 自动填充分类
        photoDO.setCategory(analysis.getScene());
        
        // 自动填充描述（可选）
        if (StringUtils.isBlank(photoDO.getDescription())) {
            photoDO.setDescription(generateDescription(analysis));
        }
    }
}
```

#### 3.1.3 接口设计

```
POST /api/photo/analyze/{photoId}     # 分析照片并生成标签
POST /api/photo/batch-analyze         # 批量分析照片
```

#### 3.1.4 成本优化

- **缓存机制**：相似照片使用缓存结果
- **批量处理**：批量上传时合并分析
- **用户选择**：用户可选择是否启用 AI 分析

---

### 3.2 场景二：照片质量评估和建议 ⭐⭐⭐⭐

#### 3.2.1 功能描述

AI 分析照片质量，提供改进建议（构图、曝光、色彩等）。

#### 3.2.2 实现方案

##### 方案 A：免费方案（优先推荐）

**使用技术 1：基于图像分析的规则评估（完全免费）**

**实现思路**：
- 使用图像处理库（如 OpenCV、ImageIO）分析照片
- 基于规则评估质量（亮度、对比度、清晰度等）
- 生成改进建议

**代码示例**：
```java
@Service
public class PhotoQualityService {
    
    /**
     * 基于规则的图片质量评估（免费）
     */
    public PhotoQualityAssessment assessPhotoQualityFree(String imagePath) {
        // 1. 读取图片
        BufferedImage image = ImageIO.read(new File(imagePath));
        
        // 2. 分析亮度
        double brightness = calculateBrightness(image);
        String brightnessAdvice = getBrightnessAdvice(brightness);
        
        // 3. 分析对比度
        double contrast = calculateContrast(image);
        String contrastAdvice = getContrastAdvice(contrast);
        
        // 4. 分析清晰度（边缘检测）
        double sharpness = calculateSharpness(image);
        String sharpnessAdvice = getSharpnessAdvice(sharpness);
        
        // 5. 分析构图（基于三分法）
        String compositionAdvice = analyzeComposition(image);
        
        // 6. 综合评分
        double overallScore = calculateOverallScore(brightness, contrast, sharpness);
        
        return PhotoQualityAssessment.builder()
            .brightness(brightness)
            .brightnessAdvice(brightnessAdvice)
            .contrast(contrast)
            .contrastAdvice(contrastAdvice)
            .sharpness(sharpness)
            .sharpnessAdvice(sharpnessAdvice)
            .compositionAdvice(compositionAdvice)
            .overallScore(overallScore)
            .suggestions(Arrays.asList(brightnessAdvice, contrastAdvice, sharpnessAdvice))
            .build();
    }
    
    private double calculateBrightness(BufferedImage image) {
        // 计算平均亮度
        long sum = 0;
        int width = image.getWidth();
        int height = image.getHeight();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                sum += (r + g + b) / 3;
            }
        }
        
        return sum / (double)(width * height) / 255.0;
    }
    
    private String getBrightnessAdvice(double brightness) {
        if (brightness < 0.3) {
            return "照片偏暗，建议增加曝光或使用补光";
        } else if (brightness > 0.7) {
            return "照片偏亮，建议减少曝光或使用遮光";
        } else {
            return "曝光正常";
        }
    }
    
    // 其他分析方法...
}
```

**使用技术 2：Google Gemini API（免费额度）**

如果规则评估不够准确，可以使用 Gemini API 进行更智能的评估。

##### 方案 B：付费方案（备选）

**使用技术**：GPT-4 Vision API

**Prompt 设计**：
```
请评估这张照片的拍摄质量，从以下维度分析：
1. 构图（三分法、对称性、引导线等）
2. 曝光（过曝、欠曝、正常）
3. 色彩（饱和度、对比度、色调）
4. 清晰度
5. 整体评分（1-10分）
6. 改进建议（2-3条）

请以JSON格式返回评估结果。
```

**代码示例**：
```java
/**
 * 评估照片质量
 */
public PhotoQualityAssessment assessPhotoQuality(String imageUrl) {
    String prompt = buildQualityAssessmentPrompt();
    
    VisionResponse response = openAIClient.analyzeImage(imageUrl, prompt);
    
    return parseQualityAssessment(response);
}
```

#### 3.2.3 接口设计

```
POST /api/photo/assess/{photoId}      # 评估照片质量
GET  /api/photo/{photoId}/suggestions  # 获取改进建议
```

---

### 3.3 场景三：智能标签生成 ⭐⭐⭐⭐

#### 3.3.1 功能描述

为影视、音乐自动生成个性化标签。

#### 3.3.2 实现方案

##### 方案 A：免费 API 方案（优先推荐）

**使用技术 1：Hugging Face Inference API（推荐）**

**优势**：
- ✅ 完全免费
- ✅ 支持中文文本生成模型（如 Qwen、ChatGLM）
- ✅ 支持文本分类模型

**实现思路**：
1. 使用文本生成模型根据影视/音乐信息生成标签
2. 或使用文本分类模型自动分类

**代码示例**：
```java
@Service
public class TagGenerationService {
    
    private static final String TEXT_GENERATION_MODEL = "Qwen/Qwen-7B-Chat";
    
    /**
     * 使用 Hugging Face API 生成标签
     */
    public List<String> generateTagsWithHF(MovieDO movie) {
        String prompt = String.format(
            "根据以下影视信息，生成5-8个个性化标签（用逗号分隔）：\n" +
            "标题：%s\n类型：%s\n简介：%s\n评分：%s",
            movie.getTitle(),
            movie.getType(),
            movie.getDescription(),
            movie.getRating()
        );
        
        // 调用 Hugging Face Inference API
        String response = callHFAPI(TEXT_GENERATION_MODEL, prompt);
        
        // 解析标签
        return parseTags(response);
    }
}
```

**使用技术 2：Google Gemini API（推荐）**

**优势**：
- ✅ 免费额度充足
- ✅ 中文支持好
- ✅ 文本生成质量高

**代码示例**：
```java
/**
 * 使用 Gemini API 生成标签
 */
public List<String> generateTagsWithGemini(MovieDO movie) {
    String prompt = buildTagGenerationPrompt(movie);
    
    GeminiResponse response = geminiClient.generateText(prompt);
    
    return parseTags(response);
}
```

**使用技术 3：基于规则的标签生成（完全免费）**

**实现思路**：
- 从影视/音乐信息中提取关键词
- 基于类型、评分等规则生成标签
- 使用 TF-IDF 提取重要词汇

**代码示例**：
```java
/**
 * 基于规则的标签生成（免费）
 */
public List<String> generateTagsByRule(MovieDO movie) {
    List<String> tags = new ArrayList<>();
    
    // 1. 从类型提取标签
    if (movie.getType() == 1) {
        tags.add("电影");
    } else {
        tags.add("电视剧");
    }
    
    // 2. 从类型（genre）提取
    if (StringUtils.isNotBlank(movie.getGenre())) {
        String[] genres = movie.getGenre().split(",");
        tags.addAll(Arrays.asList(genres));
    }
    
    // 3. 从评分提取
    if (movie.getRating() != null) {
        if (movie.getRating() >= 8.0) {
            tags.add("高分");
        } else if (movie.getRating() >= 6.0) {
            tags.add("中等");
        }
    }
    
    // 4. 从简介提取关键词（使用简单的关键词提取）
    List<String> keywords = extractKeywords(movie.getDescription());
    tags.addAll(keywords);
    
    return tags.stream().distinct().limit(8).collect(Collectors.toList());
}
```

##### 方案 B：付费 API 方案（备选）

**使用技术**：GPT-3.5-turbo API

**Prompt 设计**：
```
根据以下影视信息，生成5-8个个性化标签：
标题：{title}
类型：{type}
简介：{description}
评分：{rating}

标签应该：
1. 反映影视的核心特征
2. 便于用户分类和检索
3. 包含类型、风格、主题等维度

请返回JSON格式的标签列表。
```

**代码示例**：
```java
/**
 * 为影视生成标签
 */
public List<String> generateMovieTags(MovieDO movie) {
    String prompt = String.format(
        "根据以下影视信息，生成5-8个个性化标签：\n" +
        "标题：%s\n类型：%s\n简介：%s\n评分：%s",
        movie.getTitle(),
        movie.getType(),
        movie.getDescription(),
        movie.getRating()
    );
    
    ChatResponse response = openAIClient.chat(prompt);
    
    return parseTags(response);
}
```

---

### 3.4 场景四：智能推荐系统 ⭐⭐⭐⭐⭐

#### 3.4.1 功能描述

基于用户行为和偏好，智能推荐影视、音乐、照片主题等。

#### 3.4.2 实现方案

##### 方案 A：免费方案（优先推荐）

**使用技术 1：基于规则的推荐算法（完全免费）**

**实现思路**：
- 基于内容相似度推荐（类型、评分、标签）
- 基于协同过滤（用户相似度）
- 基于热门度推荐

**代码示例**：
```java
@Service
public class RecommendationService {
    
    /**
     * 基于内容的推荐（免费）
     */
    public List<MovieRecommendation> recommendMoviesByContent(Long userId) {
        // 1. 获取用户观影历史
        List<MovieRecordDO> records = getWatchedMovies(userId);
        
        // 2. 分析用户偏好
        UserPreference preference = analyzePreference(records);
        
        // 3. 查找相似影视
        List<MovieDO> similarMovies = findSimilarMovies(preference);
        
        // 4. 生成推荐理由（基于规则）
        return similarMovies.stream()
            .map(movie -> MovieRecommendation.builder()
                .movie(movie)
                .reason(generateReasonByRule(movie, preference))
                .score(calculateSimilarityScore(movie, preference))
                .build())
            .sorted(Comparator.comparing(MovieRecommendation::getScore).reversed())
            .limit(5)
            .collect(Collectors.toList());
    }
    
    private String generateReasonByRule(MovieDO movie, UserPreference preference) {
        StringBuilder reason = new StringBuilder();
        
        // 基于类型相似
        if (preference.getFavoriteGenres().contains(movie.getGenre())) {
            reason.append("与您喜欢的类型相似；");
        }
        
        // 基于评分相似
        if (movie.getRating() != null && Math.abs(movie.getRating() - preference.getAverageRating()) < 1.0) {
            reason.append("评分与您的偏好相近；");
        }
        
        // 基于标签相似
        if (hasCommonTags(movie, preference)) {
            reason.append("标签与您的兴趣匹配；");
        }
        
        return reason.toString();
    }
}
```

**使用技术 2：Hugging Face / Gemini API（生成推荐理由）**

如果需要更智能的推荐理由，可以使用免费 API 生成。

##### 方案 B：付费 API 方案（备选）

**使用技术**：GPT-3.5-turbo API + 数据分析

**推荐策略**：
1. **基于内容推荐**：分析用户已看/已听的影视/音乐特征，推荐相似内容
2. **基于行为推荐**：分析用户评分、观看频率等行为模式
3. **混合推荐**：结合多种推荐策略

**Prompt 设计**：
```
根据用户的观影历史，推荐5部相似的影视：

用户已看影视：
{watched_movies}

用户评分偏好：
{rating_pattern}

请推荐5部影视，并说明推荐理由。
```

**代码示例**：
```java
/**
 * 推荐影视
 */
public List<MovieRecommendation> recommendMovies(Long userId) {
    // 1. 获取用户观影历史
    List<MovieRecordDO> records = movieRecordMapper.selectList(
        new LambdaQueryWrapper<MovieRecordDO>()
            .eq(MovieRecordDO::getUserId, userId)
            .eq(MovieRecordDO::getWatchStatus, WatchStatusEnum.WATCHED.getCode())
    );
    
    // 2. 分析用户偏好
    UserPreference preference = analyzeUserPreference(records);
    
    // 3. 构建推荐 Prompt
    String prompt = buildRecommendationPrompt(preference);
    
    // 4. 调用 AI 生成推荐
    ChatResponse response = openAIClient.chat(prompt);
    
    // 5. 解析推荐结果
    return parseRecommendations(response);
}
```

---

### 3.5 场景五：评价生成 ⭐⭐⭐

#### 3.5.1 功能描述

用户输入简单评价，AI 扩展生成详细评价。

#### 3.5.2 实现方案

##### 方案 A：免费 API 方案（优先推荐）

**使用技术 1：Hugging Face Inference API（推荐）**

**代码示例**：
```java
/**
 * 使用 Hugging Face API 扩展评价
 */
public String expandCommentWithHF(String userComment, MovieDO movie) {
    String prompt = String.format(
        "用户对《%s》的简短评价：\"%s\"\n\n" +
        "请基于这个评价，扩展生成一段100-200字的详细评价。",
        movie.getTitle(),
        userComment
    );
    
    String response = callHFAPI(TEXT_GENERATION_MODEL, prompt);
    
    return response;
}
```

**使用技术 2：Google Gemini API（推荐）**

**代码示例**：
```java
/**
 * 使用 Gemini API 扩展评价
 */
public String expandCommentWithGemini(String userComment, MovieDO movie) {
    String prompt = buildCommentExpansionPrompt(userComment, movie);
    
    GeminiResponse response = geminiClient.generateText(prompt);
    
    return response.getText();
}
```

**使用技术 3：基于模板的评价扩展（完全免费）**

**实现思路**：
- 使用模板填充评价
- 基于关键词扩展

**代码示例**：
```java
/**
 * 基于模板扩展评价（免费）
 */
public String expandCommentByTemplate(String userComment, MovieDO movie) {
    // 分析用户评价的情感
    String sentiment = analyzeSentiment(userComment);
    
    // 使用模板扩展
    String template = getTemplate(sentiment);
    
    return String.format(template, 
        movie.getTitle(),
        userComment,
        movie.getRating(),
        movie.getGenre()
    );
}
```

##### 方案 B：付费 API 方案（备选）

**使用技术**：GPT-3.5-turbo API

**Prompt 设计**：
```
用户对《{title}》的简短评价："{user_comment}"

请基于这个评价，扩展生成一段100-200字的详细评价，包括：
1. 对影视的整体感受
2. 具体亮点或不足
3. 推荐理由或建议

请保持用户的原始观点和语气。
```

---

### 3.6 场景六：兴趣画像分析 ⭐⭐⭐⭐

#### 3.6.1 功能描述

分析用户的兴趣偏好，生成个性化画像。

#### 3.6.2 实现方案

##### 方案 A：免费方案（优先推荐）

**使用技术 1：基于数据分析 + 模板生成（完全免费）**

**实现思路**：
- 使用 SQL 和数据分析统计用户兴趣
- 使用模板生成画像描述

**代码示例**：
```java
@Service
public class InterestProfileService {
    
    /**
     * 生成兴趣画像（免费方案）
     */
    public InterestProfile generateProfile(Long userId) {
        // 1. 收集用户数据
        UserInterestData data = collectUserData(userId);
        
        // 2. 分析兴趣特征
        InterestFeatures features = analyzeFeatures(data);
        
        // 3. 生成画像描述（基于模板）
        String description = generateDescriptionByTemplate(features);
        
        // 4. 生成标签
        List<String> tags = generateTags(features);
        
        return InterestProfile.builder()
            .description(description)
            .tags(tags)
            .features(features)
            .build();
    }
    
    private String generateDescriptionByTemplate(InterestFeatures features) {
        StringBuilder desc = new StringBuilder();
        
        desc.append(String.format("您是一位%s爱好者，", features.getMainInterest()));
        desc.append(String.format("在过去一年中，您%s。", features.getActivitySummary()));
        desc.append(String.format("您的兴趣偏好主要集中在%s领域。", features.getPreferredGenres()));
        desc.append(String.format("您倾向于给%s类型的作品更高评分。", features.getHighRatedTypes()));
        
        return desc.toString();
    }
}
```

**使用技术 2：Hugging Face / Gemini API（生成更自然的描述）**

如果需要更自然的描述，可以使用免费 API。

##### 方案 B：付费 API 方案（备选）

**使用技术**：GPT-3.5-turbo API + 数据分析

**分析维度**：
- 兴趣类型偏好
- 时间分布特征
- 评分模式
- 内容偏好（类型、风格等）

**Prompt 设计**：
```
根据用户的兴趣活动数据，生成个性化画像：

摄影数据：{photo_stats}
影视数据：{movie_stats}
音乐数据：{music_stats}
球赛数据：{match_stats}

请分析用户的兴趣特征，生成：
1. 兴趣画像描述（100-200字）
2. 核心兴趣标签（5-8个）
3. 兴趣发展趋势
4. 个性化建议
```

---

## 四、技术实现架构

### 4.1 架构设计

```
┌─────────────────────────────────────────┐
│         业务层                           │
│  - PhotoService                         │
│  - MovieService                         │
│  - MusicService                         │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│         AI 服务层                        │
│  - PhotoAIService                       │
│  - MovieAIService                       │
│  - RecommendationAIService              │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│       AI 客户端层                        │
│  - OpenAIClient                        │
│  - LocalModelClient（可选）              │
│  - AICacheService（缓存）                │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│       AI 服务提供商                       │
│  - OpenAI API                           │
│  - 本地模型（可选）                       │
└──────────────────────────────────────────┘
```

### 4.2 核心组件

#### 4.2.1 HuggingFaceClient（免费，优先使用）

```java
@Service
public class HuggingFaceClient {
    
    @Value("${huggingface.api.token:}")
    private String apiToken;
    
    private static final String BASE_URL = "https://api-inference.huggingface.co/models";
    
    @Resource
    private RestTemplate restTemplate;
    
    /**
     * 文本生成
     */
    public String generateText(String model, String prompt) {
        String url = BASE_URL + "/" + model;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (StringUtils.isNotBlank(apiToken)) {
            headers.setBearerAuth(apiToken);
        }
        
        Map<String, Object> body = new HashMap<>();
        body.put("inputs", prompt);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        
        Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
        
        return parseResponse(response);
    }
    
    /**
     * 图像分类
     */
    public List<String> classifyImage(String model, String imageUrl) {
        // 实现图像分类 API 调用
    }
    
    /**
     * 图像描述
     */
    public String describeImage(String model, String imageUrl) {
        // 实现图像描述 API 调用
    }
}
```

#### 4.2.2 GeminiClient（免费额度）

```java
@Service
public class GeminiClient {
    
    @Value("${gemini.api.key}")
    private String apiKey;
    
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta";
    
    @Resource
    private RestTemplate restTemplate;
    
    /**
     * 文本生成
     */
    public GeminiResponse generateText(String prompt) {
        String url = BASE_URL + "/models/gemini-pro:generateContent?key=" + apiKey;
        
        Map<String, Object> body = new HashMap<>();
        body.put("contents", Arrays.asList(
            Map.of("parts", Arrays.asList(Map.of("text", prompt)))
        ));
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body);
        
        return restTemplate.postForObject(url, request, GeminiResponse.class);
    }
    
    /**
     * 图像分析（多模态）
     */
    public GeminiResponse analyzeImage(String imageUrl, String prompt) {
        // 实现多模态 API 调用
    }
}
```

#### 4.2.3 OpenAIClient（付费，备选）

```java
@Service
public class OpenAIClient {
    
    @Value("${openai.api.key}")
    private String apiKey;
    
    @Value("${openai.api.base-url:https://api.openai.com/v1}")
    private String baseUrl;
    
    @Resource
    private RestTemplate restTemplate;
    
    /**
     * 文本对话
     */
    public ChatResponse chat(String prompt) {
        // 实现 OpenAI Chat API 调用
    }
    
    /**
     * 图像分析（GPT-4 Vision）
     */
    public VisionResponse analyzeImage(String imageUrl, String prompt) {
        // 实现 Vision API 调用
    }
}
```

#### 4.2.2 AI 缓存服务

```java
@Service
public class AICacheService {
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 缓存 AI 分析结果
     */
    public <T> T getCachedResult(String cacheKey, Class<T> type) {
        return (T) redisTemplate.opsForValue().get(cacheKey);
    }
    
    /**
     * 设置缓存
     */
    public void setCache(String cacheKey, Object value, long ttl, TimeUnit unit) {
        redisTemplate.opsForValue().set(cacheKey, value, ttl, unit);
    }
    
    /**
     * 生成缓存键（基于图片哈希）
     */
    public String generateImageCacheKey(String imageUrl) {
        // 使用图片 URL 或哈希值生成缓存键
        return "ai:image:" + DigestUtils.md5Hex(imageUrl);
    }
}
```

---

## 五、配置和依赖

### 5.1 Maven 依赖

```xml
<!-- OpenAI Java SDK（可选，也可以直接用 HTTP 调用）-->
<dependency>
    <groupId>com.theokanning.openai-gpt3-java</groupId>
    <artifactId>service</artifactId>
    <version>0.18.2</version>
</dependency>

<!-- 或者使用 HTTP 客户端直接调用 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### 5.2 配置文件

```yaml
# AI 配置（优先使用免费 API）
ai:
  # 免费 API 配置（优先）
  huggingface:
    api-token: ${HUGGINGFACE_API_TOKEN:}  # 可选，注册后获取更高额度
    base-url: https://api-inference.huggingface.co/models
    timeout: 30000
    max-retries: 3
  
  gemini:
    api-key: ${GEMINI_API_KEY:}  # 免费额度：每月60次
    base-url: https://generativelanguage.googleapis.com/v1beta
    timeout: 30000
    max-retries: 3
  
  # 付费 API 配置（备选）
  openai:
    api-key: ${OPENAI_API_KEY:}  # 可选，付费
    base-url: https://api.openai.com/v1
    model:
      chat: gpt-3.5-turbo
      vision: gpt-4-vision-preview
    timeout: 30000
    max-retries: 3
  
  # API 优先级配置
  provider:
    priority: huggingface,gemini,openai  # 按优先级顺序尝试
    fallback: true  # 如果免费 API 失败，是否降级到付费 API
  
  # 功能开关
  features:
    photo-auto-tag: true      # 照片自动标签
    photo-quality-assessment: true  # 照片质量评估
    smart-recommendation: true      # 智能推荐
    auto-comment: false            # 自动评价生成（默认关闭）
  
  # 缓存配置
  cache:
    enabled: true
    ttl: 86400  # 24小时
```

### 5.3 环境变量

```bash
# 免费 API（优先）
HUGGINGFACE_API_TOKEN=your_huggingface_token_here  # 可选
GEMINI_API_KEY=your_gemini_api_key_here  # 推荐，免费额度充足

# 付费 API（备选）
OPENAI_API_KEY=your_openai_api_key_here  # 可选
```

---

## 六、成本优化策略

### 6.1 缓存策略

- **照片分析结果**：相同照片只分析一次，缓存 24 小时
- **推荐结果**：用户偏好变化不频繁，缓存 6 小时
- **标签生成**：相似内容使用缓存标签

### 6.2 批量处理

- **批量上传照片**：合并分析请求，减少 API 调用
- **定时任务**：非实时功能使用定时任务批量处理

### 6.3 用户选择

- **功能开关**：用户可选择是否启用 AI 功能
- **免费额度**：限制免费用户的 AI 使用次数
- **付费升级**：高级用户享受更多 AI 功能

### 6.4 模型选择

- **简单任务**：使用 GPT-3.5-turbo（成本低）
- **复杂任务**：使用 GPT-4（效果好）
- **图像分析**：仅在必要时使用 Vision API

---

## 七、实施步骤

### 7.1 第一阶段：基础 AI 能力（1-2周）

1. **免费 API 集成（优先）**
   - [ ] 注册 Hugging Face 账号（可选，获取更高额度）
   - [ ] 注册 Google Gemini 账号，获取 API Key
   - [ ] 创建 HuggingFaceClient
   - [ ] 创建 GeminiClient
   - [ ] 实现基础的文本生成功能
   - [ ] 实现图像分析功能

2. **照片自动标签（使用免费 API）**
   - [ ] 使用 Hugging Face 或 Gemini API 实现照片分析
   - [ ] 实现照片分析接口
   - [ ] 集成到照片上传流程
   - [ ] 添加缓存机制
   - [ ] 测试和优化

3. **付费 API 集成（备选）**
   - [ ] 如果需要，注册 OpenAI 账号，获取 API Key
   - [ ] 创建 OpenAIClient（作为备选）
   - [ ] 实现降级机制（免费 API 失败时使用）

### 7.2 第二阶段：智能推荐（1-2周）

1. **推荐系统**
   - [ ] 实现用户偏好分析
   - [ ] 实现基于内容的推荐
   - [ ] 实现推荐理由生成
   - [ ] 集成到前端展示

### 7.3 第三阶段：高级功能（1-2周）

1. **照片质量评估**
   - [ ] 实现质量评估接口
   - [ ] 生成改进建议
   - [ ] 集成到照片详情页

2. **兴趣画像分析**
   - [ ] 实现数据分析接口
   - [ ] 生成个性化画像
   - [ ] 可视化展示

---

## 八、风险与应对

### 8.1 技术风险

| 风险 | 影响 | 应对措施 |
|------|------|---------|
| API 调用失败 | 中 | 实现重试机制和降级方案 |
| API 成本过高 | 高 | 实施缓存和限流策略 |
| 响应时间慢 | 中 | 异步处理，后台任务 |

### 8.2 业务风险

| 风险 | 影响 | 应对措施 |
|------|------|---------|
| 用户隐私担忧 | 中 | 明确告知数据使用方式，提供关闭选项 |
| AI 结果不准确 | 中 | 允许用户手动修正，持续优化 Prompt |

---

## 九、后续扩展

### 9.1 更多 AI 能力

- [ ] 语音转文字（Whisper API）
- [ ] 图像生成（DALL-E，用于生成封面）
- [ ] 多语言支持
- [ ] 情感分析

### 9.2 本地模型集成

- [ ] 评估开源模型效果
- [ ] 部署本地模型（如果需要）
- [ ] 实现模型切换机制

---

## 十、总结

### 10.1 核心价值

- ✅ **提升用户体验**：自动化处理，减少手动输入
- ✅ **智能化推荐**：个性化内容推荐
- ✅ **数据分析**：深度洞察用户兴趣

### 10.2 推荐方案

- **优先使用**：免费 API（Hugging Face + Gemini）
  - Hugging Face Inference API：完全免费，模型丰富
  - Google Gemini API：免费额度充足（每月60次）
- **备选方案**：付费 API（OpenAI，效果更好但需付费）
- **降级策略**：免费 API → 付费 API → 本地模型 → 规则方案
- **成本控制**：优先免费 API + 缓存 + 批量处理 + 用户选择
- **实施策略**：分阶段实施，先免费方案后付费方案

### 10.3 注意事项

- ⚠️ **优先使用免费 API**：最大化利用免费资源
- ⚠️ **API 额度管理**：注意免费 API 的调用限制
- ⚠️ **降级方案**：免费 API 失败时自动降级
- ⚠️ **保护用户隐私**：注意数据隐私和安全
- ⚠️ **持续优化**：优化 Prompt 和缓存策略

---

**文档版本**：v1.0  
**最后更新**：2025-12-07

**下一步**：更新开发计划，整合 AI 功能到各开发阶段。

