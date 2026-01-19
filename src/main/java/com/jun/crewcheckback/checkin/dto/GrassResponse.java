package com.jun.crewcheckback.checkin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class GrassResponse {
    private LocalDate date;
    private long count;
}
