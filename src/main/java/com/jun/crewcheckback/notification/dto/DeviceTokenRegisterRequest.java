package com.jun.crewcheckback.notification.dto;

import com.jun.crewcheckback.notification.domain.DeviceType;

public record DeviceTokenRegisterRequest(
        String fcmToken,
        DeviceType deviceType,
        String deviceId
) {}
