package com.jun.crewcheckback.feed.controller;

import com.jun.crewcheckback.feed.dto.FeedRequest;
import com.jun.crewcheckback.feed.dto.FeedResponse;
import com.jun.crewcheckback.feed.dto.FeedStatisticsResponse;
import com.jun.crewcheckback.feed.service.FeedService;
import com.jun.crewcheckback.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @PostMapping
    public ResponseEntity<ApiResponse<FeedResponse>> createFeed(
            @RequestBody FeedRequest request,
            @RequestParam String userId) {
        FeedResponse response = feedService.createFeed(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<FeedResponse>>> getFeeds(
            @RequestParam(required = false, defaultValue = "all") String topicId,
            @RequestParam(required = false) String userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<FeedResponse> response = feedService.getFeeds(topicId, userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{feedId}")
    public ResponseEntity<ApiResponse<FeedResponse>> updateFeed(
            @PathVariable UUID feedId,
            @RequestBody FeedRequest request,
            @RequestParam String userId) {
        FeedResponse response = feedService.updateFeed(userId, feedId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{feedId}")
    public ResponseEntity<ApiResponse<Void>> deleteFeed(
            @PathVariable UUID feedId,
            @RequestParam String userId) {
        feedService.deleteFeed(userId, feedId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{feedId}/like")
    public ResponseEntity<ApiResponse<FeedResponse>> likeFeed(
            @PathVariable UUID feedId,
            @RequestBody java.util.Map<String, String> body) {
        String userId = body.get("userId");
        FeedResponse response = feedService.likeFeed(userId, feedId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{feedId}/like")
    public ResponseEntity<ApiResponse<FeedResponse>> unlikeFeed(
            @PathVariable UUID feedId,
            @RequestParam String userId) {
        FeedResponse response = feedService.unlikeFeed(userId, feedId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<FeedStatisticsResponse>> getStatistics(
            @RequestParam String userId) {
        FeedStatisticsResponse response = feedService.getStatistics(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
