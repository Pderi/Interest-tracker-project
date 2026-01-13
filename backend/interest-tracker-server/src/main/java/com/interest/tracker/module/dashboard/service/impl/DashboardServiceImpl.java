package com.interest.tracker.module.dashboard.service.impl;

import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.framework.common.util.object.BeanUtils;
import com.interest.tracker.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.interest.tracker.framework.security.core.UserContext;
import com.interest.tracker.module.book.dal.dataobject.BookDO;
import com.interest.tracker.module.book.dal.dataobject.BookRecordDO;
import com.interest.tracker.module.book.dal.mysql.BookMapper;
import com.interest.tracker.module.book.dal.mysql.BookRecordMapper;
import com.interest.tracker.module.concert.dal.dataobject.ConcertDO;
import com.interest.tracker.module.concert.dal.dataobject.ConcertRecordDO;
import com.interest.tracker.module.concert.dal.mysql.ConcertMapper;
import com.interest.tracker.module.concert.dal.mysql.ConcertRecordMapper;
import com.interest.tracker.module.dashboard.controller.app.vo.DashboardSummaryReqVO;
import com.interest.tracker.module.dashboard.controller.app.vo.DashboardSummaryRespVO;
import com.interest.tracker.module.dashboard.controller.app.vo.InsightsRespVO;
import com.interest.tracker.module.dashboard.controller.app.vo.RecentActivityRespVO;
import com.interest.tracker.module.dashboard.controller.app.vo.TimelineItemRespVO;
import com.interest.tracker.module.dashboard.controller.app.vo.TimelinePageReqVO;
import com.interest.tracker.module.dashboard.service.DashboardService;
import com.interest.tracker.module.match.dal.dataobject.MatchRecordDO;
import com.interest.tracker.module.match.dal.mysql.MatchRecordMapper;
import com.interest.tracker.module.movie.dal.dataobject.MovieDO;
import com.interest.tracker.module.movie.dal.dataobject.MovieRecordDO;
import com.interest.tracker.module.movie.dal.mysql.MovieMapper;
import com.interest.tracker.module.movie.dal.mysql.MovieRecordMapper;
import com.interest.tracker.module.music.dal.dataobject.AlbumDO;
import com.interest.tracker.module.music.dal.dataobject.AlbumRecordDO;
import com.interest.tracker.module.music.dal.mysql.AlbumMapper;
import com.interest.tracker.module.music.dal.mysql.AlbumRecordMapper;
import com.interest.tracker.module.photo.dal.dataobject.PhotoDO;
import com.interest.tracker.module.photo.dal.mysql.PhotoMapper;
import com.interest.tracker.module.travel.dal.dataobject.TravelPlaceDO;
import com.interest.tracker.module.travel.dal.dataobject.TravelRecordDO;
import com.interest.tracker.module.travel.dal.mysql.TravelPlaceMapper;
import com.interest.tracker.module.travel.dal.mysql.TravelRecordMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.interest.tracker.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.interest.tracker.framework.common.exception.enums.GlobalErrorCodeConstants.UNAUTHORIZED;

/**
 * Dashboard Service 实现
 *
 * @author interest-tracker
 */
@Service
@Validated
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private PhotoMapper photoMapper;

    @Resource
    private MovieRecordMapper movieRecordMapper;

    @Resource
    private MovieMapper movieMapper;

    @Resource
    private AlbumRecordMapper albumRecordMapper;

    @Resource
    private AlbumMapper albumMapper;

    @Resource
    private BookRecordMapper bookRecordMapper;

    @Resource
    private BookMapper bookMapper;

    @Resource
    private TravelRecordMapper travelRecordMapper;

    @Resource
    private TravelPlaceMapper travelPlaceMapper;

    @Resource
    private ConcertRecordMapper concertRecordMapper;

    @Resource
    private ConcertMapper concertMapper;

    @Resource
    private MatchRecordMapper matchRecordMapper;

    @Override
    public DashboardSummaryRespVO getSummary(DashboardSummaryReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 如果没有传入参数，使用默认值（本周）
        if (reqVO == null) {
            reqVO = new DashboardSummaryReqVO();
            reqVO.setTimeRange("week");
        }

        DashboardSummaryRespVO respVO = new DashboardSummaryRespVO();

        // 计算时间范围
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        
        // 根据时间范围类型计算开始和结束时间
        LocalDateTime rangeStart;
        LocalDateTime rangeEnd = now;
        LocalDateTime previousRangeStart;
        LocalDateTime previousRangeEnd;
        int trendDays = 7; // 趋势图天数
        
        switch (reqVO.getTimeRange() != null ? reqVO.getTimeRange() : "week") {
            case "today":
                rangeStart = todayStart;
                previousRangeStart = todayStart.minusDays(1);
                previousRangeEnd = todayStart;
                trendDays = 1;
                break;
            case "week":
                rangeStart = todayStart.minusDays(7);
                previousRangeStart = todayStart.minusDays(14);
                previousRangeEnd = todayStart.minusDays(7);
                trendDays = 7;
                break;
            case "month":
                rangeStart = todayStart.minusMonths(1);
                previousRangeStart = todayStart.minusMonths(2);
                previousRangeEnd = todayStart.minusMonths(1);
                trendDays = 30;
                break;
            case "year":
                rangeStart = todayStart.minusYears(1);
                previousRangeStart = todayStart.minusYears(2);
                previousRangeEnd = todayStart.minusYears(1);
                trendDays = 365;
                break;
            case "custom":
                if (reqVO.getStartDate() != null && reqVO.getEndDate() != null) {
                    rangeStart = LocalDateTime.of(reqVO.getStartDate(), LocalTime.MIN);
                    rangeEnd = LocalDateTime.of(reqVO.getEndDate(), LocalTime.MAX);
                    long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(reqVO.getStartDate(), reqVO.getEndDate());
                    previousRangeStart = rangeStart.minusDays(daysBetween + 1);
                    previousRangeEnd = rangeStart.minusDays(1);
                    trendDays = (int) Math.min(daysBetween + 1, 90); // 最多90天
                } else {
                    rangeStart = todayStart.minusDays(7);
                    previousRangeStart = todayStart.minusDays(14);
                    previousRangeEnd = todayStart.minusDays(7);
                    trendDays = 7;
                }
                break;
            default:
                rangeStart = todayStart.minusDays(7);
                previousRangeStart = todayStart.minusDays(14);
                previousRangeEnd = todayStart.minusDays(7);
                trendDays = 7;
        }
        
        LocalDateTime monthStart = todayStart.minusMonths(1);

        // 统计总数
        respVO.setPhotoCount(countPhotos(userId, null, null));
        respVO.setMovieCount(countMovieRecords(userId, null, null));
        respVO.setMusicCount(countAlbumRecords(userId, null, null));
        respVO.setBookCount(countBookRecords(userId, null, null));
        respVO.setTravelCount(countTravelRecords(userId, null, null));
        respVO.setConcertCount(countConcertRecords(userId, null, null));
        respVO.setMatchCount(countMatchRecords(userId, null, null));

        // 统计当前时间范围新增
        DashboardSummaryRespVO.WeeklyStats weeklyStats = new DashboardSummaryRespVO.WeeklyStats();
        weeklyStats.setPhoto(countPhotos(userId, rangeStart, rangeEnd));
        weeklyStats.setMovie(countMovieRecords(userId, rangeStart, rangeEnd));
        weeklyStats.setMusic(countAlbumRecords(userId, rangeStart, rangeEnd));
        weeklyStats.setBook(countBookRecords(userId, rangeStart, rangeEnd));
        weeklyStats.setTravel(countTravelRecords(userId, rangeStart, rangeEnd));
        weeklyStats.setConcert(countConcertRecords(userId, rangeStart, rangeEnd));
        weeklyStats.setMatch(countMatchRecords(userId, rangeStart, rangeEnd));
        respVO.setWeeklyStats(weeklyStats);

        // 统计上一时间范围新增（用于计算增长率）
        DashboardSummaryRespVO.WeeklyStats lastWeekStats = new DashboardSummaryRespVO.WeeklyStats();
        lastWeekStats.setPhoto(countPhotos(userId, previousRangeStart, previousRangeEnd));
        lastWeekStats.setMovie(countMovieRecords(userId, previousRangeStart, previousRangeEnd));
        lastWeekStats.setMusic(countAlbumRecords(userId, previousRangeStart, previousRangeEnd));
        lastWeekStats.setBook(countBookRecords(userId, previousRangeStart, previousRangeEnd));
        lastWeekStats.setTravel(countTravelRecords(userId, previousRangeStart, previousRangeEnd));
        lastWeekStats.setConcert(countConcertRecords(userId, previousRangeStart, previousRangeEnd));
        lastWeekStats.setMatch(countMatchRecords(userId, previousRangeStart, previousRangeEnd));
        respVO.setLastWeekStats(lastWeekStats);

        // 统计本月新增
        DashboardSummaryRespVO.MonthlyStats monthlyStats = new DashboardSummaryRespVO.MonthlyStats();
        monthlyStats.setPhoto(countPhotos(userId, monthStart, now));
        monthlyStats.setMovie(countMovieRecords(userId, monthStart, now));
        monthlyStats.setMusic(countAlbumRecords(userId, monthStart, now));
        monthlyStats.setBook(countBookRecords(userId, monthStart, now));
        monthlyStats.setTravel(countTravelRecords(userId, monthStart, now));
        monthlyStats.setConcert(countConcertRecords(userId, monthStart, now));
        monthlyStats.setMatch(countMatchRecords(userId, monthStart, now));
        respVO.setMonthlyStats(monthlyStats);

        // 统计今日新增
        DashboardSummaryRespVO.TodayStats todayStats = new DashboardSummaryRespVO.TodayStats();
        todayStats.setPhoto(countPhotos(userId, todayStart, now));
        todayStats.setMovie(countMovieRecords(userId, todayStart, now));
        todayStats.setMusic(countAlbumRecords(userId, todayStart, now));
        todayStats.setBook(countBookRecords(userId, todayStart, now));
        todayStats.setTravel(countTravelRecords(userId, todayStart, now));
        todayStats.setConcert(countConcertRecords(userId, todayStart, now));
        todayStats.setMatch(countMatchRecords(userId, todayStart, now));
        respVO.setTodayStats(todayStats);

        // 获取状态统计
        respVO.setMovieStatusCounts(movieRecordMapper.countByStatus(userId));
        respVO.setMusicStatusCounts(albumRecordMapper.countByStatus(userId));
        respVO.setBookStatusCounts(bookRecordMapper.countByStatus(userId));

        // 获取趋势数据
        respVO.setTrendData(getTrendData(userId, trendDays));

        return respVO;
    }

    @Override
    public InsightsRespVO getInsights(DashboardSummaryReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 如果没有传入参数，使用默认值（本周）
        if (reqVO == null) {
            reqVO = new DashboardSummaryReqVO();
            reqVO.setTimeRange("week");
        }

        InsightsRespVO insights = new InsightsRespVO();

        // 计算时间范围
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime rangeStart;
        LocalDateTime rangeEnd = now;
        LocalDateTime previousRangeStart;
        LocalDateTime previousRangeEnd;

        switch (reqVO.getTimeRange() != null ? reqVO.getTimeRange() : "week") {
            case "today":
                rangeStart = todayStart;
                previousRangeStart = todayStart.minusDays(1);
                previousRangeEnd = todayStart;
                break;
            case "week":
                rangeStart = todayStart.minusDays(7);
                previousRangeStart = todayStart.minusDays(14);
                previousRangeEnd = todayStart.minusDays(7);
                break;
            case "month":
                rangeStart = todayStart.minusMonths(1);
                previousRangeStart = todayStart.minusMonths(2);
                previousRangeEnd = todayStart.minusMonths(1);
                break;
            case "year":
                rangeStart = todayStart.minusYears(1);
                previousRangeStart = todayStart.minusYears(2);
                previousRangeEnd = todayStart.minusYears(1);
                break;
            case "custom":
                if (reqVO.getStartDate() != null && reqVO.getEndDate() != null) {
                    rangeStart = LocalDateTime.of(reqVO.getStartDate(), LocalTime.MIN);
                    rangeEnd = LocalDateTime.of(reqVO.getEndDate(), LocalTime.MAX);
                    long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(reqVO.getStartDate(), reqVO.getEndDate());
                    previousRangeStart = rangeStart.minusDays(daysBetween + 1);
                    previousRangeEnd = rangeStart.minusDays(1);
                } else {
                    rangeStart = todayStart.minusDays(7);
                    previousRangeStart = todayStart.minusDays(14);
                    previousRangeEnd = todayStart.minusDays(7);
                }
                break;
            default:
                rangeStart = todayStart.minusDays(7);
                previousRangeStart = todayStart.minusDays(14);
                previousRangeEnd = todayStart.minusDays(7);
        }

        // 1. 最活跃类型
        InsightsRespVO.MostActiveType mostActiveType = new InsightsRespVO.MostActiveType();
        long photoCount = countPhotos(userId, rangeStart, rangeEnd);
        long movieCount = countMovieRecords(userId, rangeStart, rangeEnd);
        long musicCount = countAlbumRecords(userId, rangeStart, rangeEnd);
        long bookCount = countBookRecords(userId, rangeStart, rangeEnd);
        long travelCount = countTravelRecords(userId, rangeStart, rangeEnd);
        long concertCount = countConcertRecords(userId, rangeStart, rangeEnd);
        long matchCount = countMatchRecords(userId, rangeStart, rangeEnd);

        Map<String, Long> typeCounts = new HashMap<>();
        typeCounts.put("photo", photoCount);
        typeCounts.put("movie", movieCount);
        typeCounts.put("music", musicCount);
        typeCounts.put("book", bookCount);
        typeCounts.put("travel", travelCount);
        typeCounts.put("concert", concertCount);
        typeCounts.put("match", matchCount);

        long totalCount = photoCount + movieCount + musicCount + bookCount + travelCount + concertCount + matchCount;
        if (totalCount > 0) {
            Map.Entry<String, Long> maxEntry = typeCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);
            if (maxEntry != null) {
                mostActiveType.setType(maxEntry.getKey());
                mostActiveType.setTypeName(getTypeName(maxEntry.getKey()));
                mostActiveType.setCount(maxEntry.getValue());
                mostActiveType.setPercentage(java.math.BigDecimal.valueOf(maxEntry.getValue() * 100.0 / totalCount)
                        .setScale(1, java.math.RoundingMode.HALF_UP));
            }
        }
        insights.setMostActiveType(mostActiveType);

        // 2. 评分趋势（仅统计有评分的记录）
        InsightsRespVO.RatingTrend ratingTrend = calculateRatingTrend(userId, rangeStart, rangeEnd, previousRangeStart, previousRangeEnd);
        insights.setRatingTrend(ratingTrend);

        // 3. 活跃度峰值
        InsightsRespVO.PeakActivity peakActivity = findPeakActivity(userId, rangeStart, rangeEnd);
        insights.setPeakActivity(peakActivity);

        // 4. 完成率（影视、音乐、阅读）
        InsightsRespVO.CompletionRate completionRate = calculateCompletionRate(userId);
        insights.setCompletionRate(completionRate);

        return insights;
    }

    private String getTypeName(String type) {
        Map<String, String> typeMap = Map.of(
                "photo", "照片",
                "movie", "影视",
                "music", "音乐",
                "book", "阅读",
                "travel", "旅游",
                "concert", "演唱会",
                "match", "球赛"
        );
        return typeMap.getOrDefault(type, type);
    }

    private InsightsRespVO.RatingTrend calculateRatingTrend(Long userId, LocalDateTime currentStart, LocalDateTime currentEnd,
                                                             LocalDateTime previousStart, LocalDateTime previousEnd) {
        InsightsRespVO.RatingTrend trend = new InsightsRespVO.RatingTrend();

        // 计算当前时间段的平均评分
        java.math.BigDecimal currentAvg = calculateAverageRating(userId, currentStart, currentEnd);
        java.math.BigDecimal previousAvg = calculateAverageRating(userId, previousStart, previousEnd);

        trend.setCurrentAvgRating(currentAvg);
        trend.setPreviousAvgRating(previousAvg);

        if (previousAvg != null && previousAvg.compareTo(java.math.BigDecimal.ZERO) > 0 && currentAvg != null) {
            java.math.BigDecimal change = currentAvg.subtract(previousAvg)
                    .divide(previousAvg, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(java.math.BigDecimal.valueOf(100));
            trend.setChangePercentage(change.setScale(1, java.math.RoundingMode.HALF_UP));
            
            if (change.compareTo(java.math.BigDecimal.valueOf(1)) > 0) {
                trend.setTrend("up");
            } else if (change.compareTo(java.math.BigDecimal.valueOf(-1)) < 0) {
                trend.setTrend("down");
            } else {
                trend.setTrend("stable");
            }
        } else {
            trend.setChangePercentage(java.math.BigDecimal.ZERO);
            trend.setTrend("stable");
        }

        return trend;
    }

    private java.math.BigDecimal calculateAverageRating(Long userId, LocalDateTime start, LocalDateTime end) {
        List<java.math.BigDecimal> ratings = new ArrayList<>();

        // 影视评分
        LambdaQueryWrapperX<MovieRecordDO> movieWrapper = new LambdaQueryWrapperX<>();
        movieWrapper.eq(MovieRecordDO::getUserId, userId)
                .isNotNull(MovieRecordDO::getPersonalRating)
                .ge(MovieRecordDO::getWatchDate, start.toLocalDate())
                .le(MovieRecordDO::getWatchDate, end.toLocalDate());
        movieRecordMapper.selectList(movieWrapper).forEach(r -> {
            if (r.getPersonalRating() != null) {
                ratings.add(r.getPersonalRating());
            }
        });

        // 音乐评分
        LambdaQueryWrapperX<AlbumRecordDO> musicWrapper = new LambdaQueryWrapperX<>();
        musicWrapper.eq(AlbumRecordDO::getUserId, userId)
                .isNotNull(AlbumRecordDO::getPersonalRating)
                .ge(AlbumRecordDO::getListenDate, start.toLocalDate())
                .le(AlbumRecordDO::getListenDate, end.toLocalDate());
        albumRecordMapper.selectList(musicWrapper).forEach(r -> {
            if (r.getPersonalRating() != null) {
                ratings.add(r.getPersonalRating());
            }
        });

        // 阅读评分
        LambdaQueryWrapperX<BookRecordDO> bookWrapper = new LambdaQueryWrapperX<>();
        bookWrapper.eq(BookRecordDO::getUserId, userId)
                .isNotNull(BookRecordDO::getPersonalRating)
                .ge(BookRecordDO::getReadDate, start.toLocalDate())
                .le(BookRecordDO::getReadDate, end.toLocalDate());
        bookRecordMapper.selectList(bookWrapper).forEach(r -> {
            if (r.getPersonalRating() != null) {
                ratings.add(r.getPersonalRating());
            }
        });

        if (ratings.isEmpty()) {
            return null;
        }

        java.math.BigDecimal sum = ratings.stream()
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        return sum.divide(java.math.BigDecimal.valueOf(ratings.size()), 2, java.math.RoundingMode.HALF_UP);
    }

    private InsightsRespVO.PeakActivity findPeakActivity(Long userId, LocalDateTime start, LocalDateTime end) {
        InsightsRespVO.PeakActivity peak = new InsightsRespVO.PeakActivity();

        // 按天统计活动数量
        Map<LocalDate, Long> dailyCounts = new HashMap<>();
        Map<LocalDate, String> dailyTypes = new HashMap<>();

        LocalDate currentDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();

        while (!currentDate.isAfter(endDate)) {
            LocalDateTime dayStart = LocalDateTime.of(currentDate, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(currentDate, LocalTime.MAX);

            long count = countPhotos(userId, dayStart, dayEnd)
                    + countMovieRecords(userId, dayStart, dayEnd)
                    + countAlbumRecords(userId, dayStart, dayEnd)
                    + countBookRecords(userId, dayStart, dayEnd)
                    + countTravelRecords(userId, dayStart, dayEnd)
                    + countConcertRecords(userId, dayStart, dayEnd)
                    + countMatchRecords(userId, dayStart, dayEnd);

            dailyCounts.put(currentDate, count);

            // 找出当天最多的类型
            String maxType = findMaxTypeForDay(userId, dayStart, dayEnd);
            dailyTypes.put(currentDate, maxType);

            currentDate = currentDate.plusDays(1);
        }

        if (!dailyCounts.isEmpty()) {
            Map.Entry<LocalDate, Long> maxEntry = dailyCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);
            if (maxEntry != null && maxEntry.getValue() > 0) {
                peak.setDate(maxEntry.getKey().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                peak.setCount(maxEntry.getValue());
                String type = dailyTypes.get(maxEntry.getKey());
                peak.setType(type);
                peak.setTypeName(getTypeName(type));
            }
        }

        return peak;
    }

    private String findMaxTypeForDay(Long userId, LocalDateTime dayStart, LocalDateTime dayEnd) {
        Map<String, Long> typeCounts = new HashMap<>();
        typeCounts.put("photo", countPhotos(userId, dayStart, dayEnd));
        typeCounts.put("movie", countMovieRecords(userId, dayStart, dayEnd));
        typeCounts.put("music", countAlbumRecords(userId, dayStart, dayEnd));
        typeCounts.put("book", countBookRecords(userId, dayStart, dayEnd));
        typeCounts.put("travel", countTravelRecords(userId, dayStart, dayEnd));
        typeCounts.put("concert", countConcertRecords(userId, dayStart, dayEnd));
        typeCounts.put("match", countMatchRecords(userId, dayStart, dayEnd));

        return typeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("photo");
    }

    private InsightsRespVO.CompletionRate calculateCompletionRate(Long userId) {
        InsightsRespVO.CompletionRate rate = new InsightsRespVO.CompletionRate();

        // 计算影视完成率
        Map<Integer, Long> movieStatusCounts = movieRecordMapper.countByStatus(userId);
        long movieCompleted = movieStatusCounts.getOrDefault(3, 0L); // 3-已看
        long movieTotal = movieStatusCounts.values().stream().mapToLong(Long::longValue).sum();
        if (movieTotal > 0) {
            rate.setType("movie");
            rate.setTypeName("影视");
            rate.setCompleted(movieCompleted);
            rate.setTotal(movieTotal);
            rate.setPercentage(java.math.BigDecimal.valueOf(movieCompleted * 100.0 / movieTotal)
                    .setScale(1, java.math.RoundingMode.HALF_UP));
            return rate;
        }

        // 如果没有影视记录，尝试音乐
        Map<Integer, Long> musicStatusCounts = albumRecordMapper.countByStatus(userId);
        long musicCompleted = musicStatusCounts.getOrDefault(3, 0L); // 3-已听
        long musicTotal = musicStatusCounts.values().stream().mapToLong(Long::longValue).sum();
        if (musicTotal > 0) {
            rate.setType("music");
            rate.setTypeName("音乐");
            rate.setCompleted(musicCompleted);
            rate.setTotal(musicTotal);
            rate.setPercentage(java.math.BigDecimal.valueOf(musicCompleted * 100.0 / musicTotal)
                    .setScale(1, java.math.RoundingMode.HALF_UP));
            return rate;
        }

        // 如果没有音乐记录，尝试阅读
        Map<Integer, Long> bookStatusCounts = bookRecordMapper.countByStatus(userId);
        long bookCompleted = bookStatusCounts.getOrDefault(3, 0L); // 3-已读
        long bookTotal = bookStatusCounts.values().stream().mapToLong(Long::longValue).sum();
        if (bookTotal > 0) {
            rate.setType("book");
            rate.setTypeName("阅读");
            rate.setCompleted(bookCompleted);
            rate.setTotal(bookTotal);
            rate.setPercentage(java.math.BigDecimal.valueOf(bookCompleted * 100.0 / bookTotal)
                    .setScale(1, java.math.RoundingMode.HALF_UP));
            return rate;
        }

        // 如果都没有，返回空
        return null;
    }

    @Override
    public List<RecentActivityRespVO> getRecentActivities(Integer limit) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        List<RecentActivityRespVO> activities = new ArrayList<>();

        // 获取最近的照片
        LambdaQueryWrapperX<PhotoDO> photoWrapper = new LambdaQueryWrapperX<>();
        photoWrapper.eq(PhotoDO::getUserId, userId)
                .orderByDesc(PhotoDO::getCreateTime)
                .last("LIMIT " + limit);
        List<PhotoDO> photos = photoMapper.selectList(photoWrapper);
        for (PhotoDO photo : photos) {
            RecentActivityRespVO activity = BeanUtils.toBean(photo, RecentActivityRespVO.class);
            activity.setType("photo");
            activity.setTitle(photo.getTitle() != null ? photo.getTitle() : "照片");
            activity.setCover(photo.getThumbnailPath() != null ? photo.getThumbnailPath() : photo.getFilePath());
            activity.setActivityTime(photo.getCreateTime());
            activity.setDetailId(photo.getId());
            activities.add(activity);
        }

        // 获取最近的影视记录
        LambdaQueryWrapperX<MovieRecordDO> movieWrapper = new LambdaQueryWrapperX<>();
        movieWrapper.eq(MovieRecordDO::getUserId, userId)
                .orderByDesc(MovieRecordDO::getCreateTime)
                .last("LIMIT " + limit);
        List<MovieRecordDO> movieRecords = movieRecordMapper.selectList(movieWrapper);
        // 批量查询影视信息
        List<Long> movieIds = movieRecords.stream()
                .map(MovieRecordDO::getMovieId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, MovieDO> movieMap = movieIds.isEmpty() ? new HashMap<>() :
                movieMapper.selectBatchIds(movieIds).stream()
                        .collect(Collectors.toMap(MovieDO::getId, movie -> movie));
        // 关联查询 MovieDO 获取标题和海报
        for (MovieRecordDO record : movieRecords) {
            RecentActivityRespVO activity = BeanUtils.toBean(record, RecentActivityRespVO.class);
            activity.setType("movie");
            MovieDO movie = movieMap.get(record.getMovieId());
            if (movie != null) {
                activity.setTitle(movie.getTitle() != null ? movie.getTitle() : "影视记录");
                activity.setCover(movie.getPosterUrl());
            } else {
                activity.setTitle("影视记录");
            }
            activity.setDescription(record.getComment());
            activity.setActivityTime(record.getCreateTime());
            activity.setRating(record.getPersonalRating());
            activity.setStatus(record.getWatchStatus());
            activity.setRecordId(record.getId());
            activity.setDetailId(record.getMovieId());
            activities.add(activity);
        }

        // 获取最近的音乐记录
        LambdaQueryWrapperX<AlbumRecordDO> albumWrapper = new LambdaQueryWrapperX<>();
        albumWrapper.eq(AlbumRecordDO::getUserId, userId)
                .orderByDesc(AlbumRecordDO::getCreateTime)
                .last("LIMIT " + limit);
        List<AlbumRecordDO> albumRecords = albumRecordMapper.selectList(albumWrapper);
        // 批量查询专辑信息
        List<Long> albumIds = albumRecords.stream()
                .map(AlbumRecordDO::getAlbumId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, AlbumDO> albumMap = albumIds.isEmpty() ? new HashMap<>() :
                albumMapper.selectBatchIds(albumIds).stream()
                        .collect(Collectors.toMap(AlbumDO::getId, album -> album));
        for (AlbumRecordDO record : albumRecords) {
            RecentActivityRespVO activity = BeanUtils.toBean(record, RecentActivityRespVO.class);
            activity.setType("music");
            AlbumDO album = albumMap.get(record.getAlbumId());
            if (album != null) {
                activity.setTitle(album.getTitle() != null ? album.getTitle() : "音乐记录");
                activity.setSubtitle(album.getArtist());
                activity.setCover(album.getCoverUrl());
            } else {
                activity.setTitle("音乐记录");
            }
            activity.setDescription(record.getComment());
            activity.setActivityTime(record.getCreateTime());
            activity.setRating(record.getPersonalRating());
            activity.setStatus(record.getListenStatus());
            activity.setRecordId(record.getId());
            activity.setDetailId(record.getAlbumId());
            activities.add(activity);
        }

        // 获取最近的阅读记录
        LambdaQueryWrapperX<BookRecordDO> bookWrapper = new LambdaQueryWrapperX<>();
        bookWrapper.eq(BookRecordDO::getUserId, userId)
                .orderByDesc(BookRecordDO::getCreateTime)
                .last("LIMIT " + limit);
        List<BookRecordDO> bookRecords = bookRecordMapper.selectList(bookWrapper);
        // 批量查询书籍信息
        List<Long> bookIds = bookRecords.stream()
                .map(BookRecordDO::getBookId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, BookDO> bookMap = bookIds.isEmpty() ? new HashMap<>() :
                bookMapper.selectBatchIds(bookIds).stream()
                        .collect(Collectors.toMap(BookDO::getId, book -> book));
        for (BookRecordDO record : bookRecords) {
            RecentActivityRespVO activity = BeanUtils.toBean(record, RecentActivityRespVO.class);
            activity.setType("book");
            BookDO book = bookMap.get(record.getBookId());
            if (book != null) {
                activity.setTitle(book.getTitle() != null ? book.getTitle() : "阅读记录");
                activity.setSubtitle(book.getAuthor());
                activity.setCover(book.getCoverUrl());
            } else {
                activity.setTitle("阅读记录");
            }
            activity.setDescription(record.getComment());
            activity.setActivityTime(record.getCreateTime());
            activity.setRating(record.getPersonalRating());
            activity.setStatus(record.getReadStatus());
            activity.setRecordId(record.getId());
            activity.setDetailId(record.getBookId());
            activities.add(activity);
        }

        // 获取最近的旅游记录
        LambdaQueryWrapperX<TravelRecordDO> travelWrapper = new LambdaQueryWrapperX<>();
        travelWrapper.eq(TravelRecordDO::getUserId, userId)
                .orderByDesc(TravelRecordDO::getCreateTime)
                .last("LIMIT " + limit);
        List<TravelRecordDO> travelRecords = travelRecordMapper.selectList(travelWrapper);
        // 批量查询地点信息
        List<Long> placeIds = travelRecords.stream()
                .map(TravelRecordDO::getPlaceId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, TravelPlaceDO> placeMap = placeIds.isEmpty() ? new HashMap<>() :
                travelPlaceMapper.selectBatchIds(placeIds).stream()
                        .collect(Collectors.toMap(TravelPlaceDO::getId, place -> place));
        for (TravelRecordDO record : travelRecords) {
            RecentActivityRespVO activity = BeanUtils.toBean(record, RecentActivityRespVO.class);
            activity.setType("travel");
            TravelPlaceDO place = placeMap.get(record.getPlaceId());
            if (place != null) {
                activity.setTitle(place.getName() != null ? place.getName() : "旅游记录");
                String location = "";
                if (place.getCity() != null) {
                    location = place.getCity();
                }
                if (place.getCountry() != null) {
                    location = location.isEmpty() ? place.getCountry() : location + ", " + place.getCountry();
                }
                activity.setSubtitle(location);
                activity.setCover(place.getCoverUrl());
            } else {
                activity.setTitle("旅游记录");
            }
            activity.setDescription(record.getComment());
            activity.setActivityTime(record.getCreateTime());
            activity.setRating(record.getPersonalRating());
            activity.setStatus(record.getTravelStatus());
            activity.setRecordId(record.getId());
            activity.setDetailId(record.getPlaceId());
            activities.add(activity);
        }

        // 获取最近的演唱会记录
        LambdaQueryWrapperX<ConcertRecordDO> concertWrapper = new LambdaQueryWrapperX<>();
        concertWrapper.eq(ConcertRecordDO::getUserId, userId)
                .orderByDesc(ConcertRecordDO::getCreateTime)
                .last("LIMIT " + limit);
        List<ConcertRecordDO> concertRecords = concertRecordMapper.selectList(concertWrapper);
        // 批量查询演唱会信息
        List<Long> concertIds = concertRecords.stream()
                .map(ConcertRecordDO::getConcertId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, ConcertDO> concertMap = concertIds.isEmpty() ? new HashMap<>() :
                concertMapper.selectBatchIds(concertIds).stream()
                        .collect(Collectors.toMap(ConcertDO::getId, concert -> concert));
        for (ConcertRecordDO record : concertRecords) {
            RecentActivityRespVO activity = BeanUtils.toBean(record, RecentActivityRespVO.class);
            activity.setType("concert");
            ConcertDO concert = concertMap.get(record.getConcertId());
            if (concert != null) {
                String title = concert.getTitle() != null ? concert.getTitle() : concert.getArtist();
                activity.setTitle(title != null ? title : "演唱会记录");
                activity.setSubtitle(concert.getArtist());
                activity.setCover(concert.getPosterUrl());
            } else {
                activity.setTitle("演唱会记录");
            }
            activity.setDescription(record.getComment());
            activity.setActivityTime(record.getCreateTime());
            activity.setRating(record.getPersonalRating());
            activity.setStatus(record.getWatchStatus());
            activity.setRecordId(record.getId());
            activity.setDetailId(record.getConcertId());
            activities.add(activity);
        }

        // 获取最近的球赛记录
        LambdaQueryWrapperX<MatchRecordDO> matchWrapper = new LambdaQueryWrapperX<>();
        matchWrapper.eq(MatchRecordDO::getUserId, userId)
                .orderByDesc(MatchRecordDO::getCreateTime)
                .last("LIMIT " + limit);
        List<MatchRecordDO> matchRecords = matchRecordMapper.selectList(matchWrapper);
        for (MatchRecordDO record : matchRecords) {
            RecentActivityRespVO activity = BeanUtils.toBean(record, RecentActivityRespVO.class);
            activity.setType("match");
            activity.setTitle(record.getHomeTeamName() + " vs " + record.getAwayTeamName());
            activity.setDescription(record.getNotes());
            activity.setActivityTime(record.getCreateTime());
            activity.setRecordId(record.getId());
            activities.add(activity);
        }

        // 按时间排序并限制数量
        return activities.stream()
                .sorted(Comparator.comparing(RecentActivityRespVO::getActivityTime).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<TimelineItemRespVO> getTimeline(TimelinePageReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        List<TimelineItemRespVO> items = new ArrayList<>();

        // 解析类型筛选
        Set<String> types = new HashSet<>();
        if (reqVO.getTypes() != null && !reqVO.getTypes().trim().isEmpty()) {
            String[] typeArray = reqVO.getTypes().split(",");
            for (String type : typeArray) {
                types.add(type.trim().toLowerCase());
            }
        }

        // 查询照片
        if (types.isEmpty() || types.contains("photo")) {
            LambdaQueryWrapperX<PhotoDO> photoWrapper = new LambdaQueryWrapperX<>();
            photoWrapper.eq(PhotoDO::getUserId, userId);
            if (reqVO.getStartTime() != null) {
                photoWrapper.ge(PhotoDO::getCreateTime, reqVO.getStartTime());
            }
            if (reqVO.getEndTime() != null) {
                photoWrapper.le(PhotoDO::getCreateTime, reqVO.getEndTime());
            }
            if (reqVO.getTag() != null && !reqVO.getTag().trim().isEmpty()) {
                photoWrapper.like(PhotoDO::getTags, reqVO.getTag());
            }
            if (reqVO.getKeyword() != null && !reqVO.getKeyword().trim().isEmpty()) {
                photoWrapper.and(w -> w.like(PhotoDO::getTitle, reqVO.getKeyword())
                        .or()
                        .like(PhotoDO::getDescription, reqVO.getKeyword()));
            }
            photoWrapper.orderByDesc(PhotoDO::getCreateTime);
            List<PhotoDO> photos = photoMapper.selectList(photoWrapper);
            for (PhotoDO photo : photos) {
                TimelineItemRespVO item = new TimelineItemRespVO();
                item.setId(photo.getId());
                item.setType("photo");
                item.setTitle(photo.getTitle() != null ? photo.getTitle() : "照片");
                item.setDescription(photo.getDescription());
                item.setCover(photo.getThumbnailPath() != null ? photo.getThumbnailPath() : photo.getFilePath());
                item.setActivityTime(photo.getCreateTime());
                item.setTags(photo.getTags());
                item.setDetailId(photo.getId());
                items.add(item);
            }
        }

        // 查询影视记录
        if (types.isEmpty() || types.contains("movie")) {
            LambdaQueryWrapperX<MovieRecordDO> movieWrapper = new LambdaQueryWrapperX<>();
            movieWrapper.eq(MovieRecordDO::getUserId, userId);
            if (reqVO.getStartTime() != null) {
                movieWrapper.ge(MovieRecordDO::getWatchDate, reqVO.getStartTime().toLocalDate());
            }
            if (reqVO.getEndTime() != null) {
                movieWrapper.le(MovieRecordDO::getWatchDate, reqVO.getEndTime().toLocalDate());
            }
            if (reqVO.getTag() != null && !reqVO.getTag().trim().isEmpty()) {
                movieWrapper.like(MovieRecordDO::getTags, reqVO.getTag());
            }
            if (reqVO.getKeyword() != null && !reqVO.getKeyword().trim().isEmpty()) {
                movieWrapper.like(MovieRecordDO::getComment, reqVO.getKeyword());
            }
            movieWrapper.orderByDesc(MovieRecordDO::getWatchDate);
            List<MovieRecordDO> movieRecords = movieRecordMapper.selectList(movieWrapper);
            for (MovieRecordDO record : movieRecords) {
                TimelineItemRespVO item = new TimelineItemRespVO();
                item.setId(record.getId());
                item.setType("movie");
                item.setTitle("影视记录");
                item.setDescription(record.getComment());
                item.setActivityTime(record.getWatchDate() != null ? 
                    LocalDateTime.of(record.getWatchDate(), LocalTime.MIN) : record.getCreateTime());
                item.setTags(record.getTags());
                item.setRecordId(record.getId());
                item.setDetailId(record.getMovieId());
                items.add(item);
            }
        }

        // 查询音乐记录
        if (types.isEmpty() || types.contains("music")) {
            LambdaQueryWrapperX<AlbumRecordDO> albumWrapper = new LambdaQueryWrapperX<>();
            albumWrapper.eq(AlbumRecordDO::getUserId, userId);
            if (reqVO.getStartTime() != null) {
                albumWrapper.ge(AlbumRecordDO::getListenDate, reqVO.getStartTime().toLocalDate());
            }
            if (reqVO.getEndTime() != null) {
                albumWrapper.le(AlbumRecordDO::getListenDate, reqVO.getEndTime().toLocalDate());
            }
            if (reqVO.getTag() != null && !reqVO.getTag().trim().isEmpty()) {
                albumWrapper.like(AlbumRecordDO::getTags, reqVO.getTag());
            }
            if (reqVO.getKeyword() != null && !reqVO.getKeyword().trim().isEmpty()) {
                albumWrapper.like(AlbumRecordDO::getComment, reqVO.getKeyword());
            }
            albumWrapper.orderByDesc(AlbumRecordDO::getListenDate);
            List<AlbumRecordDO> albumRecords = albumRecordMapper.selectList(albumWrapper);
            for (AlbumRecordDO record : albumRecords) {
                TimelineItemRespVO item = new TimelineItemRespVO();
                item.setId(record.getId());
                item.setType("music");
                item.setTitle("音乐记录");
                item.setDescription(record.getComment());
                item.setActivityTime(record.getListenDate() != null ? 
                    LocalDateTime.of(record.getListenDate(), LocalTime.MIN) : record.getCreateTime());
                item.setTags(record.getTags());
                item.setRecordId(record.getId());
                item.setDetailId(record.getAlbumId());
                items.add(item);
            }
        }

        // 查询阅读记录
        if (types.isEmpty() || types.contains("book")) {
            LambdaQueryWrapperX<BookRecordDO> bookWrapper = new LambdaQueryWrapperX<>();
            bookWrapper.eq(BookRecordDO::getUserId, userId);
            if (reqVO.getStartTime() != null) {
                bookWrapper.ge(BookRecordDO::getReadDate, reqVO.getStartTime().toLocalDate());
            }
            if (reqVO.getEndTime() != null) {
                bookWrapper.le(BookRecordDO::getReadDate, reqVO.getEndTime().toLocalDate());
            }
            if (reqVO.getTag() != null && !reqVO.getTag().trim().isEmpty()) {
                bookWrapper.like(BookRecordDO::getTags, reqVO.getTag());
            }
            if (reqVO.getKeyword() != null && !reqVO.getKeyword().trim().isEmpty()) {
                bookWrapper.like(BookRecordDO::getComment, reqVO.getKeyword());
            }
            bookWrapper.orderByDesc(BookRecordDO::getReadDate);
            List<BookRecordDO> bookRecords = bookRecordMapper.selectList(bookWrapper);
            for (BookRecordDO record : bookRecords) {
                TimelineItemRespVO item = new TimelineItemRespVO();
                item.setId(record.getId());
                item.setType("book");
                item.setTitle("阅读记录");
                item.setDescription(record.getComment());
                item.setActivityTime(record.getReadDate() != null ? 
                    LocalDateTime.of(record.getReadDate(), LocalTime.MIN) : record.getCreateTime());
                item.setTags(record.getTags());
                item.setRecordId(record.getId());
                item.setDetailId(record.getBookId());
                items.add(item);
            }
        }

        // 查询旅游记录
        if (types.isEmpty() || types.contains("travel")) {
            LambdaQueryWrapperX<TravelRecordDO> travelWrapper = new LambdaQueryWrapperX<>();
            travelWrapper.eq(TravelRecordDO::getUserId, userId);
            if (reqVO.getStartTime() != null) {
                travelWrapper.ge(TravelRecordDO::getTravelDate, reqVO.getStartTime().toLocalDate());
            }
            if (reqVO.getEndTime() != null) {
                travelWrapper.le(TravelRecordDO::getTravelDate, reqVO.getEndTime().toLocalDate());
            }
            if (reqVO.getTag() != null && !reqVO.getTag().trim().isEmpty()) {
                travelWrapper.like(TravelRecordDO::getTags, reqVO.getTag());
            }
            if (reqVO.getKeyword() != null && !reqVO.getKeyword().trim().isEmpty()) {
                travelWrapper.like(TravelRecordDO::getComment, reqVO.getKeyword());
            }
            travelWrapper.orderByDesc(TravelRecordDO::getTravelDate);
            List<TravelRecordDO> travelRecords = travelRecordMapper.selectList(travelWrapper);
            for (TravelRecordDO record : travelRecords) {
                TimelineItemRespVO item = new TimelineItemRespVO();
                item.setId(record.getId());
                item.setType("travel");
                item.setTitle("旅游记录");
                item.setDescription(record.getComment());
                item.setActivityTime(record.getTravelDate() != null ? 
                    LocalDateTime.of(record.getTravelDate(), LocalTime.MIN) : record.getCreateTime());
                item.setTags(record.getTags());
                item.setRecordId(record.getId());
                item.setDetailId(record.getPlaceId());
                items.add(item);
            }
        }

        // 查询演唱会记录
        if (types.isEmpty() || types.contains("concert")) {
            LambdaQueryWrapperX<ConcertRecordDO> concertWrapper = new LambdaQueryWrapperX<>();
            concertWrapper.eq(ConcertRecordDO::getUserId, userId);
            if (reqVO.getStartTime() != null) {
                concertWrapper.ge(ConcertRecordDO::getWatchDate, reqVO.getStartTime().toLocalDate());
            }
            if (reqVO.getEndTime() != null) {
                concertWrapper.le(ConcertRecordDO::getWatchDate, reqVO.getEndTime().toLocalDate());
            }
            if (reqVO.getTag() != null && !reqVO.getTag().trim().isEmpty()) {
                concertWrapper.like(ConcertRecordDO::getTags, reqVO.getTag());
            }
            if (reqVO.getKeyword() != null && !reqVO.getKeyword().trim().isEmpty()) {
                concertWrapper.like(ConcertRecordDO::getComment, reqVO.getKeyword());
            }
            concertWrapper.orderByDesc(ConcertRecordDO::getWatchDate);
            List<ConcertRecordDO> concertRecords = concertRecordMapper.selectList(concertWrapper);
            for (ConcertRecordDO record : concertRecords) {
                TimelineItemRespVO item = new TimelineItemRespVO();
                item.setId(record.getId());
                item.setType("concert");
                item.setTitle("演唱会记录");
                item.setDescription(record.getComment());
                item.setActivityTime(record.getWatchDate() != null ? 
                    LocalDateTime.of(record.getWatchDate(), LocalTime.MIN) : record.getCreateTime());
                item.setTags(record.getTags());
                item.setRecordId(record.getId());
                item.setDetailId(record.getConcertId());
                items.add(item);
            }
        }

        // 查询球赛记录
        if (types.isEmpty() || types.contains("match")) {
            LambdaQueryWrapperX<MatchRecordDO> matchWrapper = new LambdaQueryWrapperX<>();
            matchWrapper.eq(MatchRecordDO::getUserId, userId);
            if (reqVO.getStartTime() != null) {
                matchWrapper.ge(MatchRecordDO::getMatchDate, reqVO.getStartTime().toLocalDate());
            }
            if (reqVO.getEndTime() != null) {
                matchWrapper.le(MatchRecordDO::getMatchDate, reqVO.getEndTime().toLocalDate());
            }
            if (reqVO.getKeyword() != null && !reqVO.getKeyword().trim().isEmpty()) {
                matchWrapper.and(w -> w.like(MatchRecordDO::getHomeTeamName, reqVO.getKeyword())
                        .or()
                        .like(MatchRecordDO::getAwayTeamName, reqVO.getKeyword())
                        .or()
                        .like(MatchRecordDO::getNotes, reqVO.getKeyword()));
            }
            matchWrapper.orderByDesc(MatchRecordDO::getMatchDate);
            List<MatchRecordDO> matchRecords = matchRecordMapper.selectList(matchWrapper);
            for (MatchRecordDO record : matchRecords) {
                TimelineItemRespVO item = new TimelineItemRespVO();
                item.setId(record.getId());
                item.setType("match");
                item.setTitle(record.getHomeTeamName() + " vs " + record.getAwayTeamName());
                item.setDescription(record.getNotes());
                item.setActivityTime(record.getMatchDate() != null ? 
                    LocalDateTime.of(record.getMatchDate(), LocalTime.MIN) : record.getCreateTime());
                item.setRecordId(record.getId());
                items.add(item);
            }
        }

        // 按时间排序
        items.sort(Comparator.comparing(TimelineItemRespVO::getActivityTime).reversed());

        // 分页处理
        long total = items.size();
        int pageNo = reqVO.getPageNo() != null ? reqVO.getPageNo() : 1;
        int pageSize = reqVO.getPageSize() != null ? reqVO.getPageSize() : 10;
        int start = (pageNo - 1) * pageSize;
        int end = Math.min(start + pageSize, items.size());
        List<TimelineItemRespVO> pagedItems = start < items.size() ? items.subList(start, end) : new ArrayList<>();

        return new PageResult<>(pagedItems, total);
    }

    // ================== 私有方法 ==================

    /**
     * 统计照片数量
     */
    private long countPhotos(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapperX<PhotoDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eq(PhotoDO::getUserId, userId);
        if (startTime != null) {
            wrapper.ge(PhotoDO::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(PhotoDO::getCreateTime, endTime);
        }
        return photoMapper.selectCount(wrapper);
    }

    /**
     * 统计影视记录数量
     */
    private long countMovieRecords(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapperX<MovieRecordDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eq(MovieRecordDO::getUserId, userId);
        if (startTime != null) {
            wrapper.ge(MovieRecordDO::getWatchDate, startTime.toLocalDate());
        }
        if (endTime != null) {
            wrapper.le(MovieRecordDO::getWatchDate, endTime.toLocalDate());
        }
        return movieRecordMapper.selectCount(wrapper);
    }

    /**
     * 统计音乐记录数量
     */
    private long countAlbumRecords(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapperX<AlbumRecordDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eq(AlbumRecordDO::getUserId, userId);
        if (startTime != null) {
            wrapper.ge(AlbumRecordDO::getListenDate, startTime.toLocalDate());
        }
        if (endTime != null) {
            wrapper.le(AlbumRecordDO::getListenDate, endTime.toLocalDate());
        }
        return albumRecordMapper.selectCount(wrapper);
    }

    /**
     * 统计阅读记录数量
     */
    private long countBookRecords(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapperX<BookRecordDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eq(BookRecordDO::getUserId, userId);
        if (startTime != null) {
            wrapper.ge(BookRecordDO::getReadDate, startTime.toLocalDate());
        }
        if (endTime != null) {
            wrapper.le(BookRecordDO::getReadDate, endTime.toLocalDate());
        }
        return bookRecordMapper.selectCount(wrapper);
    }

    /**
     * 统计旅游记录数量
     */
    private long countTravelRecords(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapperX<TravelRecordDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eq(TravelRecordDO::getUserId, userId);
        if (startTime != null) {
            wrapper.ge(TravelRecordDO::getTravelDate, startTime.toLocalDate());
        }
        if (endTime != null) {
            wrapper.le(TravelRecordDO::getTravelDate, endTime.toLocalDate());
        }
        return travelRecordMapper.selectCount(wrapper);
    }

    /**
     * 统计演唱会记录数量
     */
    private long countConcertRecords(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapperX<ConcertRecordDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eq(ConcertRecordDO::getUserId, userId);
        if (startTime != null) {
            wrapper.ge(ConcertRecordDO::getWatchDate, startTime.toLocalDate());
        }
        if (endTime != null) {
            wrapper.le(ConcertRecordDO::getWatchDate, endTime.toLocalDate());
        }
        return concertRecordMapper.selectCount(wrapper);
    }

    /**
     * 统计球赛记录数量
     */
    private long countMatchRecords(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapperX<MatchRecordDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eq(MatchRecordDO::getUserId, userId);
        if (startTime != null) {
            wrapper.ge(MatchRecordDO::getMatchDate, startTime.toLocalDate());
        }
        if (endTime != null) {
            wrapper.le(MatchRecordDO::getMatchDate, endTime.toLocalDate());
        }
        return matchRecordMapper.selectCount(wrapper);
    }

    /**
     * 获取趋势数据（最近N天）
     *
     * @param userId 用户ID
     * @param days   天数
     * @return 趋势数据
     */
    private DashboardSummaryRespVO.TrendData getTrendData(Long userId, int days) {
        DashboardSummaryRespVO.TrendData trendData = new DashboardSummaryRespVO.TrendData();
        
        // 生成日期列表（最近N天）
        List<String> dates = new ArrayList<>();
        List<Long> photoData = new ArrayList<>();
        List<Long> movieData = new ArrayList<>();
        List<Long> musicData = new ArrayList<>();
        List<Long> bookData = new ArrayList<>();
        List<Long> travelData = new ArrayList<>();
        List<Long> concertData = new ArrayList<>();
        List<Long> matchData = new ArrayList<>();
        
        LocalDate today = LocalDate.now();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);
            
            // 格式化日期为 MM-dd
            dates.add(String.format("%02d-%02d", date.getMonthValue(), date.getDayOfMonth()));
            
            // 统计当天的数据
            photoData.add(countPhotos(userId, dayStart, dayEnd));
            movieData.add(countMovieRecords(userId, dayStart, dayEnd));
            musicData.add(countAlbumRecords(userId, dayStart, dayEnd));
            bookData.add(countBookRecords(userId, dayStart, dayEnd));
            travelData.add(countTravelRecords(userId, dayStart, dayEnd));
            concertData.add(countConcertRecords(userId, dayStart, dayEnd));
            matchData.add(countMatchRecords(userId, dayStart, dayEnd));
        }
        
        trendData.setDates(dates);
        trendData.setPhoto(photoData);
        trendData.setMovie(movieData);
        trendData.setMusic(musicData);
        trendData.setBook(bookData);
        trendData.setTravel(travelData);
        trendData.setConcert(concertData);
        trendData.setMatch(matchData);
        
        return trendData;
    }

}

