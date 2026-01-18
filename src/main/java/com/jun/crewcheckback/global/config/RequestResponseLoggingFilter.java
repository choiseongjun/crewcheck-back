package com.jun.crewcheckback.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logRequest(requestWrapper);
            logResponse(responseWrapper, duration);
            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String queryString = request.getQueryString() == null ? "" : "?" + request.getQueryString();
        String body = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
        log.info("Request: {} {}{} | Body: {}", request.getMethod(), request.getRequestURI(), queryString, body);
    }

    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        String body = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        log.info("Response: {} | Duration: {}ms | Body: {}", response.getStatus(), duration, body);
    }
}
