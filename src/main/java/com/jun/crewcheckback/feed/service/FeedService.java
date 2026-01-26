package com.jun.crewcheckback.feed.service;

import com.jun.crewcheckback.feed.domain.Feed;
import com.jun.crewcheckback.feed.domain.FeedLike;
import com.jun.crewcheckback.feed.domain.FeedSubject;
import com.jun.crewcheckback.feed.dto.FeedRequest;
import com.jun.crewcheckback.feed.dto.FeedResponse;
import com.jun.crewcheckback.feed.dto.FeedStatisticsResponse;
import com.jun.crewcheckback.feed.repository.FeedLikeRepository;
import com.jun.crewcheckback.feed.repository.FeedRepository;
import com.jun.crewcheckback.feed.repository.FeedSubjectRepository;
import com.jun.crewcheckback.user.domain.User;
import com.jun.crewcheckback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final FeedRepository feedRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final FeedSubjectRepository feedSubjectRepository;
    private final UserRepository userRepository;

    @Transactional
    public FeedResponse createFeed(String userIdStr, FeedRequest request) {
        UUID userId = UUID.fromString(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UUID subjectId = UUID.fromString(request.getFeedSubjectId());
        FeedSubject feedSubject = feedSubjectRepository.findByIdAndDeletedYn(subjectId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Feed Subject not found"));

        Feed feed = Feed.builder()
                .user(user)
                .feedSubject(feedSubject)
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .mood(request.getMood())
                .build();

        Feed savedFeed = feedRepository.save(feed);
        return new FeedResponse(savedFeed, false);
    }

    public Page<FeedResponse> getFeeds(String topicId, String userIdStr, Pageable pageable) {
        Page<Feed> feedPage;

        if (topicId != null && !topicId.equals("all")) {
            UUID subjectId = UUID.fromString(topicId);
            FeedSubject feedSubject = feedSubjectRepository.findByIdAndDeletedYn(subjectId, "N")
                    .orElseThrow(() -> new IllegalArgumentException("Feed Subject not found"));
            feedPage = feedRepository.findAllByFeedSubjectAndDeletedYn(feedSubject, "N", pageable);
        } else {
            feedPage = feedRepository.findAllByDeletedYn("N", pageable);
        }

        UUID userId = (userIdStr != null && !userIdStr.isEmpty()) ? UUID.fromString(userIdStr) : null;
        User user = (userId != null) ? userRepository.findById(userId).orElse(null) : null;

        List<FeedResponse> responses = feedPage.getContent().stream()
                .map(feed -> {
                    boolean isLiked = false;

                    isLiked = feedLikeRepository.existsByFeedAndUser(feed, user);

                    return new FeedResponse(feed, isLiked);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, feedPage.getTotalElements());
    }

    @Transactional
    public FeedResponse updateFeed(String userIdStr, UUID feedId, FeedRequest request) {
        UUID userId = UUID.fromString(userIdStr);
        Feed feed = feedRepository.findByIdAndDeletedYn(feedId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Feed not found"));

        if (!feed.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to update this feed");
        }

        UUID subjectId = UUID.fromString(request.getFeedSubjectId());
        FeedSubject feedSubject = feedSubjectRepository.findByIdAndDeletedYn(subjectId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Feed Subject not found"));

        feed.update(feedSubject, request.getContent(), request.getImageUrl(), request.getMood());

        return new FeedResponse(feed, false);
    }

    @Transactional
    public void deleteFeed(String userIdStr, UUID feedId) {
        UUID userId = UUID.fromString(userIdStr);
        Feed feed = feedRepository.findByIdAndDeletedYn(feedId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Feed not found"));

        if (!feed.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to delete this feed");
        }

        feed.delete();
    }

    @Transactional
    public FeedResponse likeFeed(String userIdStr, UUID feedId) {
        UUID userId = UUID.fromString(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Feed feed = feedRepository.findByIdAndDeletedYn(feedId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Feed not found"));

        if (feedLikeRepository.existsByFeedAndUser(feed, user)) {
            throw new IllegalArgumentException("Already liked");
        }

        FeedLike like = FeedLike.builder()
                .feed(feed)
                .user(user)
                .build();

        feedLikeRepository.save(like);

        return new FeedResponse(feed, true);
    }

    @Transactional
    public FeedResponse unlikeFeed(String userIdStr, UUID feedId) {
        UUID userId = UUID.fromString(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Feed feed = feedRepository.findByIdAndDeletedYn(feedId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Feed not found"));

        if (!feedLikeRepository.existsByFeedAndUser(feed, user)) {
            throw new IllegalArgumentException("Not liked");
        }

        feedLikeRepository.deleteByFeedAndUser(feed, user);

        return new FeedResponse(feed, false);
    }

    public FeedStatisticsResponse getStatistics(String userIdStr) {
        UUID userId = UUID.fromString(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        int todayCount = feedRepository.countByUserAndDeletedYnAndCreatedAtBetween(user, "N", startOfDay, endOfDay);
        int totalCount = feedRepository.countByUserAndDeletedYn(user, "N");
        int streakDays = calculateStreak(user);

        LocalDateTime weekAgo = today.minusDays(7).atStartOfDay();
        List<FeedStatisticsResponse.DailyCount> dailyCounts = feedRepository.countByUserGroupByDate(user, weekAgo)
                .stream()
                .map(row -> FeedStatisticsResponse.DailyCount.builder()
                        .date(row[0].toString())
                        .count(((Long) row[1]).intValue())
                        .build())
                .collect(Collectors.toList());

        List<FeedStatisticsResponse.MonthlyCount> monthlyCounts = feedRepository.countByUserGroupByMonth(user, weekAgo)
                .stream()
                .map(row -> FeedStatisticsResponse.MonthlyCount.builder()
                        .month((String) row[0])
                        .count(((Long) row[1]).intValue())
                        .build())
                .collect(Collectors.toList());

        return FeedStatisticsResponse.builder()
                .todayCount(todayCount)
                .streakDays(streakDays)
                .totalCount(totalCount)
                .dailyCounts(dailyCounts)
                .monthlyCounts(monthlyCounts)
                .build();
    }

    private int calculateStreak(User user) {
        List<LocalDate> dates = feedRepository.findDistinctDatesByUser(user);

        if (dates.isEmpty()) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        if (!dates.contains(today) && !dates.contains(yesterday)) {
            return 0;
        }

        int streak = 0;
        LocalDate checkDate = dates.contains(today) ? today : yesterday;

        for (LocalDate date : dates) {
            if (date.equals(checkDate)) {
                streak++;
                checkDate = checkDate.minusDays(1);
            } else if (date.isBefore(checkDate)) {
                break;
            }
        }

        return streak;
    }
}
