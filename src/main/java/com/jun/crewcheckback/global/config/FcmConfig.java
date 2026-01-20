//package com.jun.crewcheckback.global.config;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.messaging.FirebaseMessaging;
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//
//import java.io.IOException;
//
//@Slf4j
//@Configuration
//public class FcmConfig {
//
//    @Value("${fcm.certification}")
//    private String fcmCertification;
//
//    @PostConstruct
//    public void initialize() {
//        try {
//            if (FirebaseApp.getApps().isEmpty()) {
//                FirebaseOptions options = FirebaseOptions.builder()
//                        .setCredentials(GoogleCredentials.fromStream(
//                                new ClassPathResource(fcmCertification).getInputStream()))
//                        .build();
//
//                FirebaseApp.initializeApp(options);
//                log.info("FirebaseApp initialized successfully");
//            }
//        } catch (IOException e) {
//            log.error("Failed to initialize FirebaseApp: {}", e.getMessage());
//            throw new RuntimeException("Firebase 초기화에 실패했습니다.", e);
//        }
//    }
//
//    @Bean
//    public FirebaseMessaging firebaseMessaging() {
//        return FirebaseMessaging.getInstance();
//    }
//}
