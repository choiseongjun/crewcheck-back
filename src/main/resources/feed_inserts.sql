INSERT INTO feed_subjects (id, created_at, updated_at, deleted_yn, subject_name, content, participant_count) 
VALUES 
(UUID(), NOW(), NOW(), 'N', '오늘의 운동 인증', '오늘 완료한 운동을 사진으로 인증해주세요! 함께 건강해져요.', 0),
(UUID(), NOW(), NOW(), 'N', '주말 러닝 챌린지', '이번 주말 5km 러닝 도전! 인증샷을 남겨주세요.', 0),
(UUID(), NOW(), NOW(), 'N', '건강한 식단 공유', '오늘 먹은 건강한 한 끼를 공유해보세요.', 0),
(UUID(), NOW(), NOW(), 'N', '기상 인증', '미라클 모닝! 아침 기상 시간을 인증해주세요.', 0),
(UUID(), NOW(), NOW(), 'N', '물 마시기 챌린지', '하루 물 2리터 마시기 도전 중입니다. 같이 해요!', 0);
