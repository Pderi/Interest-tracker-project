package com.interest.tracker.module.movie.client.provider;

import com.interest.tracker.module.movie.client.DoubanClient;
import com.interest.tracker.module.movie.client.dto.*;
import com.interest.tracker.module.movie.client.util.MovieIdConverter;
import com.interest.tracker.module.movie.config.DomesticApiProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 国内 API 数据提供者实现（豆瓣版本）
 *
 * 说明：
 * - 通过 DoubanClient 调用豆瓣搜索 / 详情接口
 * - 将豆瓣响应结构转换为 TMDB 对应的 DTO，方便上层复用相同的映射逻辑
 * - ID 映射通过 MovieIdConverter 处理（豆瓣字符串 ID ↔ Long）
 *
 * 注意：豆瓣官方 API 已不对个人开放，使用前需要申请 apikey 或自行评估风险。
 *
 * @author interest-tracker
 */
@Component
@Slf4j
public class DomesticProvider implements MovieDataProvider {

    @Resource
    private DomesticApiProperties domesticApiProperties;

    @Resource
    private DoubanClient doubanClient;

    @Override
    public TMDBSearchResponse search(String keyword, Integer page) {
        if (!domesticApiProperties.getEnabled()) {
            throw new RuntimeException("国内 API 未启用");
        }

        try {
            // 调用豆瓣 API 搜索
            DoubanSearchResponse doubanResponse = doubanClient.search(keyword, page);

            if (doubanResponse == null || doubanResponse.getSubjects() == null) {
                TMDBSearchResponse response = new TMDBSearchResponse();
                response.setPage(page != null ? page : 1);
                response.setTotalPages(0);
                response.setTotalResults(0);
                response.setResults(new ArrayList<>());
                return response;
            }

            // 转换为 TMDB 格式
            TMDBSearchResponse response = new TMDBSearchResponse();
            response.setPage(page != null ? page : 1);
            response.setTotalResults(doubanResponse.getTotal() != null ? doubanResponse.getTotal() : 0);
            response.setTotalPages((int) Math.ceil((double) response.getTotalResults() / 20.0)); // 豆瓣每页 20 条

            List<TMDBSearchResponse.TMDBSearchResult> results = doubanResponse.getSubjects().stream()
                    .map(this::convertDoubanToTMDB)
                    .collect(Collectors.toList());

            response.setResults(results);

            return response;
        } catch (Exception e) {
            log.error("豆瓣 API 搜索失败: keyword={}, page={}", keyword, page, e);
            throw new RuntimeException("豆瓣 API 调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将豆瓣搜索结果转换为 TMDB 格式
     */
    private TMDBSearchResponse.TMDBSearchResult convertDoubanToTMDB(
            DoubanSearchResponse.DoubanSearchResult doubanResult) {
        TMDBSearchResponse.TMDBSearchResult result = new TMDBSearchResponse.TMDBSearchResult();

        // ID 转换：豆瓣 ID 是字符串，转换为 Long（使用工具类）
        result.setId(MovieIdConverter.doubanIdToLong(doubanResult.getId()));

        // 媒体类型
        if ("movie".equals(doubanResult.getSubtype())) {
            result.setMediaType("movie");
            result.setTitle(doubanResult.getTitle());
            result.setOriginalTitle(doubanResult.getOriginalTitle());
        } else if ("tv".equals(doubanResult.getSubtype())) {
            result.setMediaType("tv");
            result.setName(doubanResult.getTitle());
            result.setOriginalName(doubanResult.getOriginalTitle());
        }

        // 评分（豆瓣是 0-10，TMDB 也是 0-10）
        if (doubanResult.getRating() != null && doubanResult.getRating().getAverage() != null) {
            result.setVoteAverage(doubanResult.getRating().getAverage());
        }

        // 海报
        if (doubanResult.getImages() != null && doubanResult.getImages().getLarge() != null) {
            result.setPosterPath(doubanResult.getImages().getLarge());
        }

        return result;
    }

    @Override
    public TMDBMovieDetail getMovieDetail(Long tmdbId) {
        if (!domesticApiProperties.getEnabled()) {
            throw new RuntimeException("国内 API 未启用");
        }

        try {
            // 将 Long ID 转回豆瓣字符串 ID
            String doubanId = MovieIdConverter.longToDoubanId(tmdbId);
            if (doubanId == null) {
                throw new RuntimeException("无效的豆瓣 ID: " + tmdbId);
            }

            DoubanMovieDetail doubanDetail = doubanClient.getMovieDetail(doubanId);
            return convertDoubanMovieToTMDB(doubanDetail);
        } catch (Exception e) {
            log.error("豆瓣 API 获取电影详情失败: tmdbId={}", tmdbId, e);
            throw new RuntimeException("豆瓣 API 调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将豆瓣电影详情转换为 TMDB 格式
     */
    private TMDBMovieDetail convertDoubanMovieToTMDB(DoubanMovieDetail doubanDetail) {
        TMDBMovieDetail detail = new TMDBMovieDetail();

        // ID
        detail.setId(MovieIdConverter.doubanIdToLong(doubanDetail.getId()));

        // 基本信息
        detail.setTitle(doubanDetail.getTitle());
        detail.setOriginalTitle(doubanDetail.getOriginalTitle());
        detail.setOverview(doubanDetail.getSummary());

        // 评分
        if (doubanDetail.getRating() != null && doubanDetail.getRating().getAverage() != null) {
            detail.setVoteAverage(doubanDetail.getRating().getAverage());
        }

        // 海报
        if (doubanDetail.getImages() != null && doubanDetail.getImages().getLarge() != null) {
            detail.setPosterPath(doubanDetail.getImages().getLarge());
        }

        // 时长
        if (doubanDetail.getDurations() != null && !doubanDetail.getDurations().isEmpty()) {
            try {
                String durationStr = doubanDetail.getDurations().get(0);
                durationStr = durationStr.replace("分钟", "").trim();
                detail.setRuntime(Integer.parseInt(durationStr));
            } catch (Exception ignore) {
            }
        }

        // 类型
        if (doubanDetail.getGenres() != null && !doubanDetail.getGenres().isEmpty()) {
            List<TMDBMovieDetail.Genre> genres = doubanDetail.getGenres().stream()
                    .map(name -> {
                        TMDBMovieDetail.Genre g = new TMDBMovieDetail.Genre();
                        g.setName(name);
                        return g;
                    })
                    .collect(Collectors.toList());
            detail.setGenres(genres);
        }

        // 导演
        if (doubanDetail.getDirectors() != null && !doubanDetail.getDirectors().isEmpty()) {
            List<TMDBMovieDetail.CrewMember> crew = new ArrayList<>();
            doubanDetail.getDirectors().forEach(director -> {
                TMDBMovieDetail.CrewMember member = new TMDBMovieDetail.CrewMember();
                member.setName(director.getName());
                member.setJob("Director");
                crew.add(member);
            });
            TMDBMovieDetail.Credits credits = new TMDBMovieDetail.Credits();
            credits.setCrew(crew);
            detail.setCredits(credits);
        }

        // 演员
        if (doubanDetail.getCasts() != null && !doubanDetail.getCasts().isEmpty()) {
            List<TMDBMovieDetail.CastMember> cast = new ArrayList<>();
            for (int i = 0; i < Math.min(doubanDetail.getCasts().size(), 5); i++) {
                DoubanSearchResponse.DoubanSearchResult.DoubanPerson person = doubanDetail.getCasts().get(i);
                TMDBMovieDetail.CastMember member = new TMDBMovieDetail.CastMember();
                member.setName(person.getName());
                member.setOrder(i);
                cast.add(member);
            }
            if (detail.getCredits() == null) {
                detail.setCredits(new TMDBMovieDetail.Credits());
            }
            detail.getCredits().setCast(cast);
        }

        return detail;
    }

    @Override
    public TMDBTVDetail getTVDetail(Long tmdbId) {
        if (!domesticApiProperties.getEnabled()) {
            throw new RuntimeException("国内 API 未启用");
        }

        try {
            String doubanId = MovieIdConverter.longToDoubanId(tmdbId);
            if (doubanId == null) {
                throw new RuntimeException("无效的豆瓣 ID: " + tmdbId);
            }

            DoubanTVDetail doubanDetail = doubanClient.getTVDetail(doubanId);
            return convertDoubanTVToTMDB(doubanDetail);
        } catch (Exception e) {
            log.error("豆瓣 API 获取电视剧详情失败: tmdbId={}", tmdbId, e);
            throw new RuntimeException("豆瓣 API 调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将豆瓣电视剧详情转换为 TMDB 格式
     */
    private TMDBTVDetail convertDoubanTVToTMDB(DoubanTVDetail doubanDetail) {
        TMDBTVDetail detail = new TMDBTVDetail();

        detail.setId(MovieIdConverter.doubanIdToLong(doubanDetail.getId()));
        detail.setName(doubanDetail.getTitle());
        detail.setOriginalName(doubanDetail.getOriginalTitle());
        detail.setOverview(doubanDetail.getSummary());

        if (doubanDetail.getRating() != null && doubanDetail.getRating().getAverage() != null) {
            detail.setVoteAverage(doubanDetail.getRating().getAverage());
        }

        if (doubanDetail.getImages() != null && doubanDetail.getImages().getLarge() != null) {
            detail.setPosterPath(doubanDetail.getImages().getLarge());
        }

        if (doubanDetail.getGenres() != null && !doubanDetail.getGenres().isEmpty()) {
            List<TMDBTVDetail.Genre> genres = doubanDetail.getGenres().stream()
                    .map(name -> {
                        TMDBTVDetail.Genre g = new TMDBTVDetail.Genre();
                        g.setName(name);
                        return g;
                    })
                    .collect(Collectors.toList());
            detail.setGenres(genres);
        }

        if (doubanDetail.getDirectors() != null && !doubanDetail.getDirectors().isEmpty()) {
            List<TMDBTVDetail.CrewMember> crew = new ArrayList<>();
            doubanDetail.getDirectors().forEach(director -> {
                TMDBTVDetail.CrewMember member = new TMDBTVDetail.CrewMember();
                member.setName(director.getName());
                member.setJob("Creator");
                crew.add(member);
            });
            TMDBTVDetail.Credits credits = new TMDBTVDetail.Credits();
            credits.setCrew(crew);
            detail.setCredits(credits);
        }

        if (doubanDetail.getCasts() != null && !doubanDetail.getCasts().isEmpty()) {
            List<TMDBTVDetail.CastMember> cast = new ArrayList<>();
            for (int i = 0; i < Math.min(doubanDetail.getCasts().size(), 5); i++) {
                DoubanSearchResponse.DoubanSearchResult.DoubanPerson person = doubanDetail.getCasts().get(i);
                TMDBTVDetail.CastMember member = new TMDBTVDetail.CastMember();
                member.setName(person.getName());
                member.setOrder(i);
                cast.add(member);
            }
            if (detail.getCredits() == null) {
                detail.setCredits(new TMDBTVDetail.Credits());
            }
            detail.getCredits().setCast(cast);
        }

        return detail;
    }

    @Override
    public String getProviderName() {
        return "Douban API";
    }

    @Override
    public boolean isAvailable() {
        return domesticApiProperties.getEnabled();
    }

}

