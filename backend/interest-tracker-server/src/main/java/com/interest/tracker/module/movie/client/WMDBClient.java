package com.interest.tracker.module.movie.client;

import com.interest.tracker.module.movie.client.dto.WMDBMovieDetail;
import com.interest.tracker.module.movie.client.dto.WMDBSearchResponse;
import com.interest.tracker.module.movie.config.DomesticApiProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * WMDB 客户端（无需 key 的公开接口）
 */
@Component
@Slf4j
public class WMDBClient {

    @Resource
    private DomesticApiProperties domesticApiProperties;

    @Resource
    private RestTemplate restTemplate;

    private static final String DEFAULT_BASE = "https://api.wmdb.tv";

    private String base() {
        String cfg = domesticApiProperties.getBaseUrl();
        return (cfg == null || cfg.isEmpty()) ? DEFAULT_BASE : cfg;
    }

    /**
     * 搜索
     */
    public WMDBSearchResponse search(String keyword) {
        try {
            String url = base() + "/api/v1/movie/search?q=" +
                    URLEncoder.encode(keyword, StandardCharsets.UTF_8) +
                    "&lang=Cn";

            log.debug("调用 WMDB 搜索接口: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0");
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<WMDBSearchResponse.WMDBItem[]> resp = restTemplate.exchange(
                    url, HttpMethod.GET, entity, WMDBSearchResponse.WMDBItem[].class);

            WMDBSearchResponse response = new WMDBSearchResponse();
            List<WMDBSearchResponse.WMDBItem> items = resp.getBody() != null
                    ? List.of(resp.getBody()) : Collections.emptyList();
            response.setItems(items);
            response.setTotal(items.size());
            return response;
        } catch (RestClientException e) {
            log.error("WMDB 搜索失败: keyword={}", keyword, e);
            throw new RuntimeException("WMDB API 调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 详情
     */
    public WMDBMovieDetail getMovieDetail(Integer wmdbId) {
        try {
            String url = base() + "/api/v1/movie/info/" + wmdbId + "?lang=Cn";
            log.debug("调用 WMDB 详情接口: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0");
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<WMDBMovieDetail> resp = restTemplate.exchange(
                    url, HttpMethod.GET, entity, WMDBMovieDetail.class);
            return resp.getBody();
        } catch (RestClientException e) {
            log.error("WMDB 获取详情失败: wmdbId={}", wmdbId, e);
            throw new RuntimeException("WMDB API 调用失败: " + e.getMessage(), e);
        }
    }
}

