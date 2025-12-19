package com.interest.tracker.module.movie.client.util;

/**
 * 影视 ID 转换工具
 * 用于处理不同数据源的 ID 格式转换
 * 
 * 注意：豆瓣 ID 是字符串格式，TMDB ID 是 Long 格式
 * 为了统一，在搜索时会将豆瓣 ID 转换为 Long，在获取详情时需要转换回字符串
 *
 * @author interest-tracker
 */
public class MovieIdConverter {

    /**
     * 豆瓣 ID 前缀（用于标识豆瓣 ID）
     * 使用负数范围来标识豆瓣 ID，避免与 TMDB ID 冲突
     */
    private static final long DOUBAN_ID_OFFSET = -1000000000L;

    /**
     * 将豆瓣 ID（字符串）转换为 Long
     * 
     * @param doubanId 豆瓣 ID（字符串）
     * @return Long 类型的 ID（如果是数字，直接转换；否则使用哈希值 + 偏移量）
     */
    public static Long doubanIdToLong(String doubanId) {
        if (doubanId == null || doubanId.isEmpty()) {
            return null;
        }
        
        try {
            // 尝试直接解析为 Long
            long id = Long.parseLong(doubanId);
            // 如果 ID 在正常范围内（小于偏移量），直接返回
            // 否则加上偏移量以标识这是豆瓣 ID
            if (id < Math.abs(DOUBAN_ID_OFFSET)) {
                return id + DOUBAN_ID_OFFSET;
            }
            return id;
        } catch (NumberFormatException e) {
            // 如果无法解析，使用哈希值 + 偏移量
            return DOUBAN_ID_OFFSET + Math.abs(doubanId.hashCode());
        }
    }

    /**
     * 将 Long ID 转换回豆瓣 ID（字符串）
     * 
     * @param id Long 类型的 ID
     * @return 豆瓣 ID（字符串）
     */
    public static String longToDoubanId(Long id) {
        if (id == null) {
            return null;
        }
        
        // 如果 ID 是负数且小于偏移量，说明是豆瓣 ID
        if (id < 0 && id <= DOUBAN_ID_OFFSET) {
            // 还原原始 ID（减去偏移量）
            long originalId = id - DOUBAN_ID_OFFSET;
            return String.valueOf(originalId);
        }
        
        // 否则直接转换为字符串（可能是 TMDB ID 或正常的豆瓣 ID）
        return String.valueOf(id);
    }

    /**
     * 判断 ID 是否是豆瓣 ID
     * 
     * @param id Long 类型的 ID
     * @return true 如果是豆瓣 ID
     */
    public static boolean isDoubanId(Long id) {
        return id != null && id < 0 && id <= DOUBAN_ID_OFFSET;
    }

}

