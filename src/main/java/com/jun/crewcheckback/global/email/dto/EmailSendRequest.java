package com.jun.crewcheckback.global.email.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailSendRequest {
    private String to;
    private String subject;
    private String text;

    public EmailSendRequest(String to, String subject, String text) {
        this.to = to;
        this.subject = subject;
        this.text = text;
    }
}
