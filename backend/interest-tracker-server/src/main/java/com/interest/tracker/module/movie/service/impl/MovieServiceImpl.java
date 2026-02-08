package com.interest.tracker.module.movie.service.impl;

import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.framework.common.util.object.BeanUtils;
import com.interest.tracker.framework.security.core.UserContext;
import com.interest.tracker.module.movie.controller.app.vo.*;
import com.interest.tracker.module.movie.dal.dataobject.MovieDO;
import com.interest.tracker.module.movie.dal.dataobject.MovieRecordDO;
import com.interest.tracker.module.movie.client.MovieDataService;
import com.interest.tracker.module.movie.client.TMDBClient;
import com.interest.tracker.module.movie.client.dto.TMDBMovieDetail;
import com.interest.tracker.module.movie.client.dto.TMDBSearchResponse;
import com.interest.tracker.module.movie.client.dto.TMDBTVDetail;
import com.interest.tracker.module.movie.dal.mysql.MovieMapper;
import com.interest.tracker.module.movie.dal.mysql.MovieRecordMapper;
import com.interest.tracker.module.movie.enums.WatchStatusEnum;
import com.interest.tracker.module.movie.service.MovieService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.interest.tracker.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.interest.tracker.framework.common.exception.enums.GlobalErrorCodeConstants.UNAUTHORIZED;
import static com.interest.tracker.module.movie.constants.MovieErrorCodeConstants.*;

/**
 * 影视服务实现
 *
 * @author interest-tracker
 */
@Service
@Validated
@Slf4j
public class MovieServiceImpl implements MovieService {

    @Resource
    private MovieMapper movieMapper;

    @Resource
    private MovieRecordMapper movieRecordMapper;

    @Resource
    private TMDBClient tmdbClient;

    @Resource
    private MovieDataService movieDataService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MovieCreateRespVO createMovie(MovieCreateReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 创建或获取 Movie
        MovieDO movieDO;
        if (reqVO.getTmdbId() != null) {
            // 从TMDB获取详细信息
            // 先检查是否已存在相同 tmdbId 的影视
            MovieDO existMovie = movieMapper.selectOne("tmdb_id", reqVO.getTmdbId());
            if (existMovie != null) {
                movieDO = existMovie;
            } else {
                // 调用TMDB API获取详细信息
                movieDO = createMovieFromTMDB(reqVO.getTmdbId(), reqVO.getType());
                movieMapper.insert(movieDO);
            }
        } else {
            // 手动创建
            if (reqVO.getTitle() == null || reqVO.getTitle().isEmpty()) {
                throw exception(MOVIE_NOT_EXISTS);
            }
            movieDO = BeanUtils.toBean(reqVO, MovieDO.class);
            movieMapper.insert(movieDO);
        }

        // 2. 检查是否已存在观看记录
        MovieRecordDO existRecord = movieRecordMapper.selectByUserIdAndMovieId(userId, movieDO.getId());
        if (existRecord != null) {
            throw exception(MOVIE_RECORD_ALREADY_EXISTS);
        }

        // 3. 创建观看记录
        MovieRecordDO recordDO = BeanUtils.toBean(reqVO, MovieRecordDO.class);
        // 处理标签：前端传数组，数据库存竖线分隔的字符串
        if (reqVO.getTags() != null) {
            recordDO.setTags(String.join("|", reqVO.getTags()));
        }
        recordDO.setUserId(userId);
        recordDO.setMovieId(movieDO.getId());
        // 设置默认观看状态
        if (recordDO.getWatchStatus() == null) {
            recordDO.setWatchStatus(1); // 默认"想看"
        }
        // 当状态为“已看”且未填日期时，自动补今天
        if (WatchStatusEnum.WATCHED.getValue().equals(recordDO.getWatchStatus())
                && recordDO.getWatchDate() == null) {
            recordDO.setWatchDate(LocalDate.now());
        }
        movieRecordMapper.insert(recordDO);

        // 4. 返回结果
        MovieCreateRespVO respVO = new MovieCreateRespVO();
        respVO.setMovieId(movieDO.getId());
        respVO.setRecordId(recordDO.getId());
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMovieRecord(MovieRecordUpdateReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验记录存在
        MovieRecordDO recordDO = validateMovieRecordExists(reqVO.getId());

        // 2. 校验权限（只能更新自己的记录）
        if (!recordDO.getUserId().equals(userId)) {
            throw exception(MOVIE_RECORD_NOT_OWNER);
        }

        // 3. 如果请求中携带了海报地址，顺带更新影视基础信息的封面
        if (reqVO.getPosterUrl() != null) {
            MovieDO movieDO = validateMovieExists(recordDO.getMovieId());
            movieDO.setPosterUrl(reqVO.getPosterUrl());
            movieMapper.updateById(movieDO);
        }

        // 4. 应用记录更新字段
        applyRecordUpdate(reqVO, recordDO);
        // 5. 根据业务规则自动填充字段
        autoFillWatchDateIfNeeded(reqVO, recordDO);

        movieRecordMapper.updateById(recordDO);
    }

    /**
     * 将更新请求中的非空字段合并到已有的记录上
     */
    private void applyRecordUpdate(MovieRecordUpdateReqVO reqVO, MovieRecordDO recordDO) {
        MovieRecordDO updateDO = BeanUtils.toBean(reqVO, MovieRecordDO.class);
        if (updateDO.getWatchStatus() != null) {
            recordDO.setWatchStatus(updateDO.getWatchStatus());
        }
        if (updateDO.getPersonalRating() != null) {
            recordDO.setPersonalRating(updateDO.getPersonalRating());
        }
        if (updateDO.getWatchDate() != null) {
            recordDO.setWatchDate(updateDO.getWatchDate());
        }
        if (updateDO.getWatchDuration() != null) {
            recordDO.setWatchDuration(updateDO.getWatchDuration());
        }
        if (updateDO.getProgress() != null) {
            recordDO.setProgress(updateDO.getProgress());
        }
        if (updateDO.getComment() != null) {
            recordDO.setComment(updateDO.getComment());
        }
        // 标签单独处理：前端传数组，数据库存竖线分隔的字符串
        if (reqVO.getTags() != null) {
            recordDO.setTags(String.join("|", reqVO.getTags()));
        }
    }

    /**
     * 状态改为「已看」且未填写观看日期时，自动补当天
     */
    private void autoFillWatchDateIfNeeded(MovieRecordUpdateReqVO reqVO, MovieRecordDO recordDO) {
        if (reqVO.getWatchStatus() != null
                && WatchStatusEnum.WATCHED.getValue().equals(reqVO.getWatchStatus())
                && recordDO.getWatchDate() == null) {
            recordDO.setWatchDate(LocalDate.now());
        }
    }

    @Override
    public MovieRespVO getMovie(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 查询影视信息
        MovieDO movieDO = validateMovieExists(id);

        // 2. 查询观看记录
        MovieRecordDO recordDO = movieRecordMapper.selectByUserIdAndMovieId(userId, id);
        if (recordDO == null) {
            throw exception(MOVIE_RECORD_NOT_EXISTS);
        }

        // 3. 组装返回数据
        MovieRespVO respVO = new MovieRespVO();
        MovieRespVO.MovieInfo movieInfo = BeanUtils.toBean(movieDO, MovieRespVO.MovieInfo.class);
        MovieRespVO.RecordInfo recordInfo = BeanUtils.toBean(recordDO, MovieRespVO.RecordInfo.class);
        // 拆分影视类型、演员为列表（仅在有值时处理）
        movieInfo.setGenre(splitToList(movieDO.getGenre()));
        movieInfo.setActors(splitToList(movieDO.getActors()));
        // 拆分标签为列表
        recordInfo.setTags(splitToList(recordDO.getTags()));
        respVO.setMovie(movieInfo);
        respVO.setRecord(recordInfo);

        return respVO;
    }

    @Override
    public MoviePageWithStatsRespVO getMoviePage(MoviePageReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 设置用户ID
        reqVO.setUserId(userId);

        // 1. 分页查询观看记录（筛选逻辑已在 Mapper 层通过 SQL 实现）
        PageResult<MovieRecordDO> pageResult = movieRecordMapper.selectPage(reqVO);

        // 2. 批量查询影视信息
        List<Long> movieIds = pageResult.getList().stream()
                .map(MovieRecordDO::getMovieId)
                .distinct()
                .collect(Collectors.toList());
        List<MovieDO> movieList = movieIds.isEmpty() ? List.of() : movieMapper.selectBatchIds(movieIds);

        // 3. 构建影视信息Map
        java.util.Map<Long, MovieDO> movieMap = movieList.stream()
                .collect(Collectors.toMap(MovieDO::getId, movie -> movie));

        // 4. 组装返回数据
        List<MoviePageRespVO> voList = pageResult.getList().stream()
                .map(record -> {
                    MoviePageRespVO vo = BeanUtils.toBean(record, MoviePageRespVO.class);
                    // 设置记录ID和影视ID（字段名不同）
                    vo.setRecordId(record.getId());
                    vo.setMovieId(record.getMovieId());

                    // 填充影视信息
                    MovieDO movie = movieMap.get(record.getMovieId());
                    if (movie != null) {
                        vo.setTitle(movie.getTitle());
                        vo.setType(movie.getType());
                        vo.setPosterUrl(movie.getPosterUrl());
                    }
                    // 填充评价
                    vo.setComment(record.getComment());
                    // 拆分标签为列表
                    vo.setTags(splitToList(record.getTags()));

                    return vo;
                })
                .collect(Collectors.toList());

        // 5. 统计各状态数量
        java.util.Map<Integer, Long> statusCounts = movieRecordMapper.countByStatus(userId);

        // 6. 组装响应
        MoviePageWithStatsRespVO respVO = new MoviePageWithStatsRespVO();
        respVO.setPage(new PageResult<>(voList, pageResult.getTotal()));
        respVO.setStatusCounts(statusCounts);
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMovieRecord(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验记录存在
        MovieRecordDO recordDO = validateMovieRecordExists(id);

        // 2. 校验权限（只能删除自己的记录）
        if (!recordDO.getUserId().equals(userId)) {
            throw exception(MOVIE_RECORD_NOT_OWNER);
        }

        // 3. 删除（软删除）
        movieRecordMapper.deleteById(id);
    }

    /* ================== 私有方法 ================== */

    /**
     * 校验影视是否存在
     */
    private MovieDO validateMovieExists(Long id) {
        MovieDO movieDO = movieMapper.selectById(id);
        if (movieDO == null) {
            throw exception(MOVIE_NOT_EXISTS);
        }
        return movieDO;
    }

    @Override
    public PageResult<MovieSearchRespVO> searchMovies(String keyword, Integer page) {
        try {
            // 调用影视数据服务（自动切换 TMDB 和国内 API）
            TMDBSearchResponse response = movieDataService.search(keyword, page != null ? page : 1);

            if (response == null || response.getResults() == null) {
                return new PageResult<>(List.of(), 0L);
            }

            // 转换为 VO
            List<MovieSearchRespVO> voList = response.getResults().stream()
                    .filter(result -> "movie".equals(result.getMediaType()) || "tv".equals(result.getMediaType()))
                    .map(result -> {
                        MovieSearchRespVO vo = new MovieSearchRespVO();
                        vo.setTmdbId(result.getId());
                        // 根据 media_type 设置标题和类型
                        if ("movie".equals(result.getMediaType())) {
                            vo.setTitle(result.getTitle());
                            vo.setOriginalTitle(result.getOriginalTitle());
                            vo.setType(1); // 电影
                            vo.setReleaseYear(extractYear(result.getReleaseDate()));
                        } else if ("tv".equals(result.getMediaType())) {
                            vo.setTitle(result.getName());
                            vo.setOriginalTitle(result.getOriginalName());
                            vo.setType(2); // 电视剧
                            vo.setReleaseYear(extractYear(result.getFirstAirDate()));
                        }
                        vo.setOverview(result.getOverview());
                        vo.setPosterUrl(movieDataService.buildImageUrl(result.getPosterPath()));
                        vo.setRating(result.getVoteAverage() != null ? 
                                java.math.BigDecimal.valueOf(result.getVoteAverage()) : null);
                        return vo;
                    })
                    .collect(Collectors.toList());

            return new PageResult<>(voList, 
                    response.getTotalResults() != null ? (long) response.getTotalResults() : 0L);
        } catch (Exception e) {
            log.error("TMDB 搜索失败: keyword={}, page={}", keyword, page, e);
            throw exception(TMDB_API_ERROR);
        }
    }

    /**
     * 校验观看记录是否存在
     */
    private MovieRecordDO validateMovieRecordExists(Long id) {
        MovieRecordDO recordDO = movieRecordMapper.selectById(id);
        if (recordDO == null) {
            throw exception(MOVIE_RECORD_NOT_EXISTS);
        }
        return recordDO;
    }

    /**
     * 将竖线分隔的字符串拆成非空列表
     */
    private List<String> splitToList(String value) {
        if (value == null || value.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(value.split("\\|"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 从 TMDB 创建影视
     */
    private MovieDO createMovieFromTMDB(Long tmdbId, Integer type) {
        try {
            MovieDO movieDO = new MovieDO();
            movieDO.setTmdbId(tmdbId);

            if (type != null && type == 1) {
                // 获取电影详情（自动切换）
                TMDBMovieDetail detail = movieDataService.getMovieDetail(tmdbId);
                mapMovieDetailToDO(detail, movieDO);
            } else if (type != null && type == 2) {
                // 获取电视剧详情（自动切换）
                TMDBTVDetail detail = movieDataService.getTVDetail(tmdbId);
                mapTVDetailToDO(detail, movieDO);
            } else {
                throw exception(TMDB_MOVIE_NOT_FOUND);
            }

            return movieDO;
        } catch (Exception e) {
            log.error("从 TMDB 创建影视失败: tmdbId={}, type={}", tmdbId, type, e);
            throw exception(TMDB_API_ERROR);
        }
    }

    /**
     * 将 TMDB 电影详情映射到 MovieDO
     */
    private void mapMovieDetailToDO(TMDBMovieDetail detail, MovieDO movieDO) {
        movieDO.setTitle(detail.getTitle());
        movieDO.setType(1); // 电影
        movieDO.setDescription(detail.getOverview());
        movieDO.setReleaseYear(extractYear(detail.getReleaseDate()));
        movieDO.setPosterUrl(movieDataService.buildImageUrl(detail.getPosterPath()));
        movieDO.setRating(detail.getVoteAverage() != null ? 
                java.math.BigDecimal.valueOf(detail.getVoteAverage()) : null);
        movieDO.setDuration(detail.getRuntime());

        // 提取类型（genre）
        if (detail.getGenres() != null && !detail.getGenres().isEmpty()) {
            String genre = detail.getGenres().stream()
                    .map(TMDBMovieDetail.Genre::getName)
                    .collect(Collectors.joining(","));
            movieDO.setGenre(genre);
        }

        // 提取导演
        if (detail.getCredits() != null && detail.getCredits().getCrew() != null) {
            String director = detail.getCredits().getCrew().stream()
                    .filter(crew -> "Director".equals(crew.getJob()))
                    .map(TMDBMovieDetail.CrewMember::getName)
                    .findFirst()
                    .orElse(null);
            movieDO.setDirector(director);
        }

        // 提取演员（前5个主要演员）
        if (detail.getCredits() != null && detail.getCredits().getCast() != null) {
            String actors = detail.getCredits().getCast().stream()
                    .sorted((a, b) -> Integer.compare(
                            a.getOrder() != null ? a.getOrder() : Integer.MAX_VALUE,
                            b.getOrder() != null ? b.getOrder() : Integer.MAX_VALUE))
                    .limit(5)
                    .map(TMDBMovieDetail.CastMember::getName)
                    .collect(Collectors.joining(","));
            movieDO.setActors(actors);
        }
    }

    /**
     * 将 TMDB 电视剧详情映射到 MovieDO
     */
    private void mapTVDetailToDO(TMDBTVDetail detail, MovieDO movieDO) {
        movieDO.setTitle(detail.getName());
        movieDO.setType(2); // 电视剧
        movieDO.setDescription(detail.getOverview());
        movieDO.setReleaseYear(extractYear(detail.getFirstAirDate()));
        movieDO.setPosterUrl(movieDataService.buildImageUrl(detail.getPosterPath()));
        movieDO.setRating(detail.getVoteAverage() != null ? 
                java.math.BigDecimal.valueOf(detail.getVoteAverage()) : null);

        // 提取平均时长（如果有）
        if (detail.getEpisodeRunTime() != null && !detail.getEpisodeRunTime().isEmpty()) {
            // 取第一个时长值
            movieDO.setDuration(detail.getEpisodeRunTime().get(0));
        }

        // 提取类型（genre）
        if (detail.getGenres() != null && !detail.getGenres().isEmpty()) {
            String genre = detail.getGenres().stream()
                    .map(TMDBTVDetail.Genre::getName)
                    .collect(Collectors.joining(","));
            movieDO.setGenre(genre);
        }

        // 提取导演（电视剧可能没有导演，使用创建者）
        if (detail.getCredits() != null && detail.getCredits().getCrew() != null) {
            String director = detail.getCredits().getCrew().stream()
                    .filter(crew -> "Creator".equals(crew.getJob()) || "Director".equals(crew.getJob()))
                    .map(TMDBTVDetail.CrewMember::getName)
                    .findFirst()
                    .orElse(null);
            movieDO.setDirector(director);
        }

        // 提取演员（前5个主要演员）
        if (detail.getCredits() != null && detail.getCredits().getCast() != null) {
            String actors = detail.getCredits().getCast().stream()
                    .sorted((a, b) -> Integer.compare(
                            a.getOrder() != null ? a.getOrder() : Integer.MAX_VALUE,
                            b.getOrder() != null ? b.getOrder() : Integer.MAX_VALUE))
                    .limit(5)
                    .map(TMDBTVDetail.CastMember::getName)
                    .collect(Collectors.joining(","));
            movieDO.setActors(actors);
        }
    }

    /**
     * 从日期字符串中提取年份
     */
    private Integer extractYear(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            // 格式：YYYY-MM-DD
            if (dateStr.length() >= 4) {
                return Integer.parseInt(dateStr.substring(0, 4));
            }
        } catch (Exception e) {
            log.warn("解析日期年份失败: {}", dateStr, e);
        }
        return null;
    }

}

