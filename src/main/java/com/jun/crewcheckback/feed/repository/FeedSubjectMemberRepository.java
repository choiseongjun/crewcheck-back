package com.jun.crewcheckback.feed.repository;

import com.jun.crewcheckback.feed.domain.FeedSubject;
import com.jun.crewcheckback.feed.domain.FeedSubjectMember;
import com.jun.crewcheckback.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FeedSubjectMemberRepository extends JpaRepository<FeedSubjectMember, UUID> {
    boolean existsByFeedSubjectAndUser(FeedSubject feedSubject, User user);

    Optional<FeedSubjectMember> findByFeedSubjectAndUser(FeedSubject feedSubject, User user);

    java.util.List<FeedSubjectMember> findAllByUser(User user);
}
