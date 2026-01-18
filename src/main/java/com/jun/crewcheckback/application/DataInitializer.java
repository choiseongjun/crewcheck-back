package com.jun.crewcheckback.application;

import com.jun.crewcheckback.checkin.domain.CheckIn;
import com.jun.crewcheckback.checkin.domain.CheckInApproval;
import com.jun.crewcheckback.checkin.repository.CheckInApprovalRepository;
import com.jun.crewcheckback.checkin.repository.CheckInRepository;
import com.jun.crewcheckback.team.domain.Team;
import com.jun.crewcheckback.team.domain.TeamMember;
import com.jun.crewcheckback.team.repository.TeamMemberRepository;
import com.jun.crewcheckback.team.repository.TeamRepository;
import com.jun.crewcheckback.user.domain.Gender;
import com.jun.crewcheckback.user.domain.User;
import com.jun.crewcheckback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

        private final UserRepository userRepository;
        private final TeamRepository teamRepository;
        private final TeamMemberRepository teamMemberRepository;
        private final CheckInRepository checkInRepository;
        private final CheckInApprovalRepository checkInApprovalRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        @Transactional
        public void run(String... args) throws Exception {
                if (userRepository.count() > 0) {
                        log.info("Data already exists. Skipping initialization.");
                        return;
                }

                log.info("Initializing sample data...");

                // 1. Create Users
                User user1 = User.builder()
                                .email("alice@example.com")
                                .password(passwordEncoder.encode("password123"))
                                .nickname("Alice")
                                .bio("Loves coding")
                                .birthDate(LocalDate.of(1990, 5, 20))
                                .gender(Gender.FEMALE)
                                .build();
                User user2 = User.builder()
                                .email("bob@example.com")
                                .password(passwordEncoder.encode("password123"))
                                .nickname("Bob")
                                .bio("Fitness enthusiast")
                                .birthDate(LocalDate.of(1992, 8, 15))
                                .gender(Gender.MALE)
                                .build();
                User user3 = User.builder()
                                .email("charlie@example.com")
                                .password(passwordEncoder.encode("password123"))
                                .nickname("Charlie")
                                .bio("Bookworm")
                                .birthDate(LocalDate.of(1995, 2, 10))
                                .gender(Gender.MALE)
                                .build();

                userRepository.save(user1);
                userRepository.save(user2);
                userRepository.save(user3);

                // 2. Create Team
                Team team = Team.builder()
                                .owner(user1)
                                .name("Morning Miracle")
                                .description("Wake up early and study!")
                                .category("Study")
                                .frequency("daily")
                                .memberLimit(5)
                                .isPublic(true)
                                .build();
                teamRepository.save(team);

                // 3. Create Team Memberships
                TeamMember member1 = TeamMember.builder()
                                .team(team)
                                .user(user1)
                                .role("leader")
                                .status("active")
                                .build();
                TeamMember member2 = TeamMember.builder()
                                .team(team)
                                .user(user2)
                                .role("member")
                                .status("active")
                                .build();
                TeamMember member3 = TeamMember.builder()
                                .team(team)
                                .user(user3)
                                .role("member")
                                .status("active")
                                .build();

                teamMemberRepository.save(member1);
                teamMemberRepository.save(member2);
                teamMemberRepository.save(member3);

                // 4. Create CheckIns
                CheckIn checkIn1 = CheckIn.builder()
                                .team(team)
                                .user(user2)
                                .content("Woke up at 6am!")
                                .status("pending")
                                .build();
                checkInRepository.save(checkIn1);

                // 5. Create Approval
                CheckInApproval approval = CheckInApproval.builder()
                                .checkIn(checkIn1)
                                .approver(user1)
                                .build();
                checkInApprovalRepository.save(approval);

                log.info("Sample data initialized successfully.");
        }
}
