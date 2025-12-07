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
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '查看次数',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞次数',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_shoot_time` (`shoot_time`),
    KEY `idx_create_time` (`create_time`)
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
CREATE TABLE IF NOT EXISTS `movie` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '影视ID',
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
    KEY `idx_release_year` (`release_year`)
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
-- 音乐相关表
-- ========================================

-- 歌曲表
CREATE TABLE IF NOT EXISTS `song` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '歌曲ID',
    `title` VARCHAR(255) NOT NULL COMMENT '歌曲名',
    `artist` VARCHAR(128) DEFAULT NULL COMMENT '歌手',
    `album` VARCHAR(255) DEFAULT NULL COMMENT '专辑',
    `duration` INT DEFAULT NULL COMMENT '时长（秒）',
    `genre` VARCHAR(128) DEFAULT NULL COMMENT '类型',
    `source_url` VARCHAR(512) DEFAULT NULL COMMENT '来源URL',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    KEY `idx_artist` (`artist`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='歌曲表';

-- 歌单表
CREATE TABLE IF NOT EXISTS `playlist` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '歌单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `name` VARCHAR(128) NOT NULL COMMENT '歌单名称',
    `description` TEXT DEFAULT NULL COMMENT '歌单描述',
    `cover_url` VARCHAR(512) DEFAULT NULL COMMENT '封面URL',
    `song_count` INT NOT NULL DEFAULT 0 COMMENT '歌曲数量',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否 1-是',
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='歌单表';

-- 歌单歌曲关联表
CREATE TABLE IF NOT EXISTS `playlist_song` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    `playlist_id` BIGINT NOT NULL COMMENT '歌单ID',
    `song_id` BIGINT NOT NULL COMMENT '歌曲ID',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `add_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    UNIQUE KEY `uk_playlist_song` (`playlist_id`, `song_id`),
    KEY `idx_playlist_id` (`playlist_id`),
    KEY `idx_song_id` (`song_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='歌单歌曲关联表';

-- 播放记录表
CREATE TABLE IF NOT EXISTS `play_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `song_id` BIGINT NOT NULL COMMENT '歌曲ID',
    `play_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '播放时间',
    `duration` INT DEFAULT NULL COMMENT '播放时长（秒）',
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_song_id` (`song_id`),
    KEY `idx_play_time` (`play_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='播放记录表';

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
-- 标签系统（通用）
-- ========================================

-- 标签表
CREATE TABLE IF NOT EXISTS `tag` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `tag_name` VARCHAR(64) NOT NULL COMMENT '标签名称',
    `tag_type` VARCHAR(32) NOT NULL COMMENT '标签类型：photo/movie/music/match',
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

