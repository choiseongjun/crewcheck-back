package com.jun.crewcheckback.feed.service;

import com.jun.crewcheckback.feed.domain.FeedSubject;
import com.jun.crewcheckback.feed.domain.FeedSubjectMember;
import com.jun.crewcheckback.feed.dto.FeedSubjectRequest;
import com.jun.crewcheckback.feed.dto.FeedSubjectResponse;
import com.jun.crewcheckback.feed.repository.FeedSubjectMemberRepository;
import com.jun.crewcheckback.feed.repository.FeedSubjectRepository;
import com.jun.crewcheckback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedSubjectService {

    private final FeedSubjectRepository feedSubjectRepository;
    private final FeedSubjectMemberRepository feedSubjectMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public FeedSubjectResponse createFeedSubject(FeedSubjectRequest request) {
        FeedSubject feedSubject = FeedSubject.builder()
                .subjectName(request.getSubjectName())
                .content(request.getContent())
                .enName(request.getEnName())
                .coverImage(request.getCoverImage())
                .build();
        FeedSubject savedSubject = feedSubjectRepository.save(feedSubject);
        return new FeedSubjectResponse(savedSubject);
    }

    public List<FeedSubjectResponse> getAllFeedSubjects(String userIdStr) {
        List<FeedSubject> subjects = feedSubjectRepository.findAllByDeletedYn("N");

        if (userIdStr != null && !userIdStr.isEmpty()) {
            UUID userId = UUID.fromString(userIdStr);
            com.jun.crewcheckback.user.domain.User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            return subjects.stream()
                    .map(s -> {
                        boolean isJoined = feedSubjectMemberRepository.existsByFeedSubjectAndUser(s, user);
                        return new FeedSubjectResponse(s, isJoined);
                    })
                    .collect(Collectors.toList());
        }

        return subjects.stream()
                .map(FeedSubjectResponse::new)
                .collect(Collectors.toList());
    }

    public FeedSubjectResponse getFeedSubject(UUID id, String userIdStr) {
        FeedSubject feedSubject = feedSubjectRepository.findByIdAndDeletedYn(id, "N")
                .orElseThrow(() -> new IllegalArgumentException("Feed Topic not found"));

        boolean isJoined = false;
        if (userIdStr != null && !userIdStr.isEmpty()) {
            UUID userId = UUID.fromString(userIdStr);
            com.jun.crewcheckback.user.domain.User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            isJoined = feedSubjectMemberRepository.existsByFeedSubjectAndUser(feedSubject, user);
        }

        return new FeedSubjectResponse(feedSubject, isJoined);
    }

    @Transactional
    public FeedSubjectResponse updateFeedSubject(UUID id, FeedSubjectRequest request) {
        FeedSubject feedSubject = feedSubjectRepository.findByIdAndDeletedYn(id, "N")
                .orElseThrow(() -> new IllegalArgumentException("Feed Topic not found"));

        feedSubject.update(request.getSubjectName(), request.getContent(), request.getEnName(),
                request.getCoverImage());

        return new FeedSubjectResponse(feedSubject);
    }

    @Transactional
    public void deleteFeedSubject(UUID id) {
        FeedSubject feedSubject = feedSubjectRepository.findByIdAndDeletedYn(id, "N")
                .orElseThrow(() -> new IllegalArgumentException("Feed Topic not found"));
        feedSubject.delete();
    }

    @Transactional
    public void joinFeedSubject(UUID feedSubjectId, String userIdStr) {
        UUID userId = UUID.fromString(userIdStr);
        FeedSubject feedSubject = feedSubjectRepository.findByIdAndDeletedYn(feedSubjectId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Feed Topic not found"));
        com.jun.crewcheckback.user.domain.User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (feedSubjectMemberRepository.existsByFeedSubjectAndUser(feedSubject, user)) {
            throw new IllegalArgumentException("Already joined");
        }

        FeedSubjectMember member = FeedSubjectMember.builder()
                .feedSubject(feedSubject)
                .user(user)
                .build();

        feedSubjectMemberRepository.save(member);
        // Note: participantCount is updated via @Formula/DB on next fetch
    }

    @Transactional
    public void leaveFeedSubject(UUID feedSubjectId, String userIdStr) {
        UUID userId = UUID.fromString(userIdStr);
        FeedSubject feedSubject = feedSubjectRepository.findByIdAndDeletedYn(feedSubjectId, "N")
                .orElseThrow(() -> new IllegalArgumentException("Feed Topic not found"));
        com.jun.crewcheckback.user.domain.User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        FeedSubjectMember member = feedSubjectMemberRepository.findByFeedSubjectAndUser(feedSubject, user)
                .orElseThrow(() -> new IllegalArgumentException("Not joined"));

        feedSubjectMemberRepository.delete(member);
        // Note: participantCount is updated via @Formula/DB on next fetch
    }

    public List<FeedSubjectResponse> getMyFeedSubjects(String userIdStr) {
        UUID userId = UUID.fromString(userIdStr);
        com.jun.crewcheckback.user.domain.User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<FeedSubjectMember> members = feedSubjectMemberRepository.findAllByUser(user);

        return members.stream()
                .map(member -> new FeedSubjectResponse(member.getFeedSubject(), true))
                .collect(Collectors.toList());
    }
}
