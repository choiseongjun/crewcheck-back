package com.jun.crewcheckback.user.controller;

import com.jun.crewcheckback.global.common.ApiResponse;
import com.jun.crewcheckback.user.dto.LoginRequest;
import com.jun.crewcheckback.user.dto.TokenResponse;
import com.jun.crewcheckback.user.dto.UserResponse;
import com.jun.crewcheckback.user.dto.UserSignUpRequest;
import com.jun.crewcheckback.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signUp(@RequestBody UserSignUpRequest request) {
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
        TokenResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
