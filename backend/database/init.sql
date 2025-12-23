-- ========================================
-- 兴趣追踪平台数据库初始化脚本
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `interest_tracker` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `interest_tracker`;

-- ========================================
-- 用户表
-- ========================================
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `password` VARCHAR(128) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
    `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(32) DEFAULT NULL COMMENT '手机号',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ========================================
-- 摄影相关表
-- ========================================

-- 照片表
CREATE TABLE IF NOT EXISTS `photo` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '照片ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(255) DEFAULT NULL COMMENT '照片标题',
    `description` TEXT DEFAULT NULL COMMENT '照片描述',
    `file_path` VARCHAR(512) NOT NULL COMMENT '文件路径',
    `thumbnail_path` VARCHAR(512) DEFAULT NULL COMMENT '缩略图路径',
    `exif_data` JSON DEFAULT NULL COMMENT 'EXIF数据（JSON格式）',
    `location` VARCHAR(255) DEFAULT NULL COMMENT '拍摄地点',
    `tags` VARCHAR(512) DEFAULT NULL COMMENT '标签（逗号分隔）',
    `category` VARCHAR(64) DEFAULT NULL COMMENT '分类',
    `shoot_time` DATETIME DEFAULT NULL COMMENT '拍摄时间',
    `travel_record_id` BIGINT DEFAULT NULL COMMENT '关联的旅游记录ID',
    `concert_record_id` BIGINT DEFAULT NULL COMMENT '关联的观演记录ID',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '查看次数',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞次数',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_shoot_time` (`shoot_time`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_travel_record_id` (`travel_record_id`),
    KEY `idx_concert_record_id` (`concert_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='照片表';

-- 相册表
CREATE TABLE IF NOT EXISTS `photo_album` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '相册ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `name` VARCHAR(128) NOT NULL COMMENT '相册名称',
    `description` TEXT DEFAULT NULL COMMENT '相册描述',
    `cover_photo_id` BIGINT DEFAULT NULL COMMENT '封面照片ID',
    `photo_count` INT NOT NULL DEFAULT 0 COMMENT '照片数量',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='相册表';

-- 相册照片关联表
CREATE TABLE IF NOT EXISTS `photo_album_photo` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    `album_id` BIGINT NOT NULL COMMENT '相册ID',
    `photo_id` BIGINT NOT NULL COMMENT '照片ID',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_album_photo` (`album_id`, `photo_id`),
    KEY `idx_album_id` (`album_id`),
    KEY `idx_photo_id` (`photo_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='相册照片关联表';

-- ========================================
-- 影视相关表
-- ========================================

-- 影视表
DROP TABLE IF EXISTS `movie`;
CREATE TABLE IF NOT EXISTS `movie` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '影视ID',
    `tmdb_id` BIGINT DEFAULT NULL COMMENT 'TMDB ID（用于关联TMDB数据）',
    `title` VARCHAR(255) NOT NULL COMMENT '标题',
    `type` TINYINT NOT NULL COMMENT '类型：1-电影 2-电视剧',
    `genre` VARCHAR(128) DEFAULT NULL COMMENT '类型（动作、科幻等，逗号分隔）',
    `release_year` INT DEFAULT NULL COMMENT '上映年份',
    `director` VARCHAR(128) DEFAULT NULL COMMENT '导演',
    `actors` VARCHAR(512) DEFAULT NULL COMMENT '演员（逗号分隔）',
    `description` TEXT DEFAULT NULL COMMENT '简介',
    `poster_url` VARCHAR(512) DEFAULT NULL COMMENT '海报URL',
    `rating` DECIMAL(3,1) DEFAULT NULL COMMENT '评分（0-10）',
    `duration` INT DEFAULT NULL COMMENT '时长（分钟）',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    KEY `idx_type` (`type`),
    KEY `idx_release_year` (`release_year`),
    KEY `idx_tmdb_id` (`tmdb_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='影视表';
-- 观看记录表
CREATE TABLE IF NOT EXISTS `movie_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `movie_id` BIGINT NOT NULL COMMENT '影视ID',
    `watch_status` TINYINT NOT NULL DEFAULT 1 COMMENT '观看状态：1-想看 2-在看 3-已看 4-弃剧',
    `personal_rating` DECIMAL(3,1) DEFAULT NULL COMMENT '个人评分（0-10）',
    `watch_date` DATE DEFAULT NULL COMMENT '观看日期',
    `watch_duration` INT DEFAULT NULL COMMENT '观看时长（分钟）',
    `progress` DECIMAL(5,2) DEFAULT NULL COMMENT '观看进度（0-100）',
    `comment` TEXT DEFAULT NULL COMMENT '评价',
    `tags` VARCHAR(512) DEFAULT NULL COMMENT '标签（逗号分隔）',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    UNIQUE KEY `uk_user_movie` (`user_id`, `movie_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_movie_id` (`movie_id`),
    KEY `idx_watch_status` (`watch_status`),
    KEY `idx_watch_date` (`watch_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='观看记录表';

-- ========================================
-- 音乐相关表（以专辑为单位）
-- ========================================

-- 专辑表
CREATE TABLE IF NOT EXISTS `album` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '专辑ID',
    `title` VARCHAR(255) NOT NULL COMMENT '专辑名称',
    `artist` VARCHAR(255) NOT NULL COMMENT '艺术家/乐队',
    `release_year` INT DEFAULT NULL COMMENT '发行年份',
    `genre` VARCHAR(128) DEFAULT NULL COMMENT '音乐类型（摇滚、流行等，逗号分隔）',
    `description` TEXT DEFAULT NULL COMMENT '专辑简介',
    `cover_url` VARCHAR(512) DEFAULT NULL COMMENT '封面URL',
    `total_tracks` INT DEFAULT NULL COMMENT '总曲目数',
    `duration` INT DEFAULT NULL COMMENT '总时长（秒）',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    KEY `idx_title` (`title`),
    KEY `idx_artist` (`artist`),
    KEY `idx_release_year` (`release_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专辑表';

-- 听歌记录表
CREATE TABLE IF NOT EXISTS `album_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `album_id` BIGINT NOT NULL COMMENT '专辑ID',
    `listen_status` TINYINT NOT NULL DEFAULT 1 COMMENT '听歌状态：1-想听 2-在听 3-已听 4-弃听',
    `personal_rating` DECIMAL(3,1) DEFAULT NULL COMMENT '个人评分（0-10）',
    `listen_date` DATE DEFAULT NULL COMMENT '听歌日期',
    `listen_count` INT DEFAULT NULL COMMENT '听歌次数',
    `comment` TEXT DEFAULT NULL COMMENT '评价',
    `tags` VARCHAR(512) DEFAULT NULL COMMENT '标签（逗号分隔）',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    UNIQUE KEY `uk_user_album` (`user_id`, `album_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_album_id` (`album_id`),
    KEY `idx_listen_status` (`listen_status`),
    KEY `idx_listen_date` (`listen_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='听歌记录表';

-- ========================================
-- 球赛相关表
-- ========================================

-- 球队表
CREATE TABLE IF NOT EXISTS `team` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '球队ID',
    `name` VARCHAR(128) NOT NULL COMMENT '球队名称',
    `league` VARCHAR(128) DEFAULT NULL COMMENT '联赛',
    `logo_url` VARCHAR(512) DEFAULT NULL COMMENT '队徽URL',
    `country` VARCHAR(64) DEFAULT NULL COMMENT '国家',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    KEY `idx_league` (`league`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='球队表';

-- 用户关注球队表
CREATE TABLE IF NOT EXISTS `user_team` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `team_id` BIGINT NOT NULL COMMENT '球队ID',
    `follow_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    UNIQUE KEY `uk_user_team` (`user_id`, `team_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_team_id` (`team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注球队表';

-- 比赛记录表
CREATE TABLE IF NOT EXISTS `match_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `home_team_id` BIGINT NOT NULL COMMENT '主队ID',
    `away_team_id` BIGINT NOT NULL COMMENT '客队ID',
    `match_date` DATETIME NOT NULL COMMENT '比赛时间',
    `home_score` INT DEFAULT NULL COMMENT '主队得分',
    `away_score` INT DEFAULT NULL COMMENT '客队得分',
    `match_type` TINYINT DEFAULT 1 COMMENT '比赛类型：1-联赛 2-杯赛 3-友谊赛',
    `watch_type` TINYINT DEFAULT 1 COMMENT '观赛方式：1-现场 2-直播 3-回放',
    `venue` VARCHAR(255) DEFAULT NULL COMMENT '比赛场地',
    `notes` TEXT DEFAULT NULL COMMENT '备注',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_match_date` (`match_date`),
    KEY `idx_home_team` (`home_team_id`),
    KEY `idx_away_team` (`away_team_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='比赛记录表';

-- ========================================
-- 图书相关表
-- ========================================

-- 书籍表
CREATE TABLE IF NOT EXISTS `book` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '书籍ID',
    `douban_id` VARCHAR(64) DEFAULT NULL COMMENT '豆瓣ID（用于关联豆瓣数据）',
    `title` VARCHAR(255) NOT NULL COMMENT '书名',
    `author` VARCHAR(255) DEFAULT NULL COMMENT '作者（逗号分隔）',
    `publisher` VARCHAR(128) DEFAULT NULL COMMENT '出版社',
    `publish_year` INT DEFAULT NULL COMMENT '出版年份',
    `isbn` VARCHAR(32) DEFAULT NULL COMMENT 'ISBN',
    `genre` VARCHAR(128) DEFAULT NULL COMMENT '类型（小说、历史等，逗号分隔）',
    `description` TEXT DEFAULT NULL COMMENT '简介',
    `cover_url` VARCHAR(512) DEFAULT NULL COMMENT '封面URL',
    `page_count` INT DEFAULT NULL COMMENT '页数',
    `language` VARCHAR(32) DEFAULT NULL COMMENT '语言',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    KEY `idx_title` (`title`),
    KEY `idx_author` (`author`),
    KEY `idx_publish_year` (`publish_year`),
    KEY `idx_douban_id` (`douban_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍表';

-- 阅读记录表
CREATE TABLE IF NOT EXISTS `book_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `book_id` BIGINT NOT NULL COMMENT '书籍ID',
    `read_status` TINYINT NOT NULL DEFAULT 1 COMMENT '阅读状态：1-想读 2-在读 3-已读 4-弃读',
    `personal_rating` DECIMAL(3,1) DEFAULT NULL COMMENT '个人评分（0-10）',
    `read_date` DATE DEFAULT NULL COMMENT '阅读日期',
    `read_progress` DECIMAL(5,2) DEFAULT NULL COMMENT '阅读进度（0-100）',
    `comment` TEXT DEFAULT NULL COMMENT '评价',
    `tags` VARCHAR(512) DEFAULT NULL COMMENT '标签（逗号分隔）',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    UNIQUE KEY `uk_user_book` (`user_id`, `book_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_book_id` (`book_id`),
    KEY `idx_read_status` (`read_status`),
    KEY `idx_read_date` (`read_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='阅读记录表';

-- ========================================
-- 旅游相关表
-- ========================================

-- 旅游地点表
CREATE TABLE IF NOT EXISTS `travel_place` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '地点ID',
    `name` VARCHAR(255) NOT NULL COMMENT '地点名称',
    `country` VARCHAR(64) DEFAULT NULL COMMENT '国家/地区',
    `city` VARCHAR(128) DEFAULT NULL COMMENT '城市',
    `address` VARCHAR(512) DEFAULT NULL COMMENT '详细地址',
    `latitude` DECIMAL(10,7) DEFAULT NULL COMMENT '纬度',
    `longitude` DECIMAL(10,7) DEFAULT NULL COMMENT '经度',
    `place_type` TINYINT DEFAULT 1 COMMENT '地点类型：1-城市 2-景点 3-国家 4-其他',
    `description` TEXT DEFAULT NULL COMMENT '描述',
    `cover_url` VARCHAR(512) DEFAULT NULL COMMENT '封面URL',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    KEY `idx_name` (`name`),
    KEY `idx_country` (`country`),
    KEY `idx_city` (`city`),
    KEY `idx_place_type` (`place_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='旅游地点表';

-- 旅游记录表
CREATE TABLE IF NOT EXISTS `travel_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `place_id` BIGINT NOT NULL COMMENT '地点ID',
    `travel_status` TINYINT NOT NULL DEFAULT 1 COMMENT '旅游状态：1-想去 2-计划中 3-已去',
    `personal_rating` DECIMAL(3,1) DEFAULT NULL COMMENT '个人评分（0-10）',
    `travel_date` DATE DEFAULT NULL COMMENT '旅游日期',
    `travel_duration` INT DEFAULT NULL COMMENT '旅游天数',
    `expense` DECIMAL(10,2) DEFAULT NULL COMMENT '费用',
    `comment` TEXT DEFAULT NULL COMMENT '评价',
    `tags` VARCHAR(512) DEFAULT NULL COMMENT '标签（逗号分隔）',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    UNIQUE KEY `uk_user_place` (`user_id`, `place_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_place_id` (`place_id`),
    KEY `idx_travel_status` (`travel_status`),
    KEY `idx_travel_date` (`travel_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='旅游记录表';

-- ========================================
-- 演唱会相关表
-- ========================================

-- 演唱会表
CREATE TABLE IF NOT EXISTS `concert` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '演唱会ID',
    `artist` VARCHAR(255) NOT NULL COMMENT '艺术家/乐队',
    `title` VARCHAR(255) DEFAULT NULL COMMENT '演出名称',
    `concert_date` DATETIME DEFAULT NULL COMMENT '演出日期',
    `venue` VARCHAR(255) DEFAULT NULL COMMENT '演出场地',
    `city` VARCHAR(128) DEFAULT NULL COMMENT '城市',
    `country` VARCHAR(64) DEFAULT NULL COMMENT '国家',
    `concert_type` TINYINT DEFAULT 1 COMMENT '演出类型：1-演唱会 2-音乐节 3-演出 4-其他',
    `description` TEXT DEFAULT NULL COMMENT '描述',
    `poster_url` VARCHAR(512) DEFAULT NULL COMMENT '海报URL',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    KEY `idx_artist` (`artist`),
    KEY `idx_title` (`title`),
    KEY `idx_concert_date` (`concert_date`),
    KEY `idx_city` (`city`),
    KEY `idx_concert_type` (`concert_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='演唱会表';

-- 观演记录表
CREATE TABLE IF NOT EXISTS `concert_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `concert_id` BIGINT NOT NULL COMMENT '演唱会ID',
    `watch_status` TINYINT NOT NULL DEFAULT 1 COMMENT '观演状态：1-想看 2-已看',
    `personal_rating` DECIMAL(3,1) DEFAULT NULL COMMENT '个人评分（0-10）',
    `watch_date` DATE DEFAULT NULL COMMENT '观演日期',
    `ticket_price` DECIMAL(10,2) DEFAULT NULL COMMENT '票价',
    `seat_info` VARCHAR(128) DEFAULT NULL COMMENT '座位信息',
    `comment` TEXT DEFAULT NULL COMMENT '评价',
    `tags` VARCHAR(512) DEFAULT NULL COMMENT '标签（逗号分隔）',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    UNIQUE KEY `uk_user_concert` (`user_id`, `concert_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_concert_id` (`concert_id`),
    KEY `idx_watch_status` (`watch_status`),
    KEY `idx_watch_date` (`watch_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='观演记录表';

-- ========================================
-- 标签系统（通用）
-- ========================================

-- 标签表
CREATE TABLE IF NOT EXISTS `tag` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `tag_name` VARCHAR(64) NOT NULL COMMENT '标签名称',
    `tag_type` VARCHAR(32) NOT NULL COMMENT '标签类型：photo/movie/music/match/book/travel/concert',
    `color` VARCHAR(16) DEFAULT NULL COMMENT '标签颜色',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    UNIQUE KEY `uk_user_tag_type` (`user_id`, `tag_name`, `tag_type`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_tag_type` (`tag_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- ========================================
-- 初始化数据
-- ========================================

-- 插入默认管理员用户（密码：admin123，实际项目中应该使用加密后的密码）
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy4pBNe', '管理员', 1);

