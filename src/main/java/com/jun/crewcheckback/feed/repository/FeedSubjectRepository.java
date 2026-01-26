package com.jun.crewcheckback.feed.repository;

import com.jun.crewcheckback.feed.domain.FeedSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeedSubjectRepository extends JpaRepository<FeedSubject, UUID> {
    List<FeedSubject> findAllByDeletedYn(String deletedYn);

    Optional<FeedSubject> findByIdAndDeletedYn(UUID id, String deletedYn);
}
