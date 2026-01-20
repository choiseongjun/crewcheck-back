package com.jun.crewcheckback.checkin.repository;

import com.jun.crewcheckback.checkin.domain.CheckInApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface CheckInApprovalRepository extends JpaRepository<CheckInApproval, UUID> {
    Optional<CheckInApproval> findByIdAndDeletedYn(UUID id, String deletedYn);
    List<CheckInApproval> findByCheckInIdAndDeletedYn(UUID checkInId, String deletedYn);
}
