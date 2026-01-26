package com.jun.crewcheckback.feed.controller;

import com.jun.crewcheckback.feed.dto.FeedSubjectRequest;
import com.jun.crewcheckback.feed.dto.FeedSubjectResponse;
import com.jun.crewcheckback.feed.service.FeedSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feed-subjects")
@RequiredArgsConstructor
public class FeedSubjectController {

    private final FeedSubjectService feedSubjectService;

    @PostMapping
    public ResponseEntity<FeedSubjectResponse> createFeedSubject(@RequestBody FeedSubjectRequest request) {
        return ResponseEntity.ok(feedSubjectService.createFeedSubject(request));
    }

    @GetMapping
    public ResponseEntity<List<FeedSubjectResponse>> getAllFeedSubjects(
            @RequestParam(required = false) String userId) {
        return ResponseEntity.ok(feedSubjectService.getAllFeedSubjects(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedSubjectResponse> getFeedSubject(@PathVariable UUID id,
            @RequestParam(required = false) String userId) {
        return ResponseEntity.ok(feedSubjectService.getFeedSubject(id, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedSubjectResponse> updateFeedSubject(@PathVariable UUID id,
            @RequestBody FeedSubjectRequest request) {
        return ResponseEntity.ok(feedSubjectService.updateFeedSubject(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedSubject(@PathVariable UUID id) {
        feedSubjectService.deleteFeedSubject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Void> joinFeedSubject(@PathVariable UUID id,
            @RequestBody java.util.Map<String, String> body) {
        String userId = body.get("userId");
        feedSubjectService.joinFeedSubject(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/leave")
    public ResponseEntity<Void> leaveFeedSubject(@PathVariable UUID id, @RequestParam String userId) {
        feedSubjectService.leaveFeedSubject(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<FeedSubjectResponse>> getMyFeedSubjects(@PathVariable String userId) {
        return ResponseEntity.ok(feedSubjectService.getMyFeedSubjects(userId));
    }
}
