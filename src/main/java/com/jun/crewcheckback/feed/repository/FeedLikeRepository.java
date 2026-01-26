package com.jun.crewcheckback.feed.repository;

import com.jun.crewcheckback.feed.domain.Feed;
import com.jun.crewcheckback.feed.domain.FeedLike;
import com.jun.crewcheckback.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeedLikeRepository extends JpaRepository<FeedLike, UUID> {
    boolean existsByFeedAndUser(Feed feed, User user);

    void deleteByFeedAndUser(Feed feed, User user);
}
