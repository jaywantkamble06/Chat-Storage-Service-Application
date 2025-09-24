package com.example.ragchat.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(2)
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Window> clientWindows = new ConcurrentHashMap<>();

    @Value("${app.rate-limit.window-seconds}")
    private int windowSeconds;

    @Value("${app.rate-limit.max-requests}")
    private int maxRequests;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator/health");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientKey = resolveClientKey(request);
        long now = Instant.now().getEpochSecond();

        Window window = clientWindows.computeIfAbsent(clientKey, k -> new Window(now, 0));
        synchronized (window) {
            if (now - window.start >= windowSeconds) {
                window.start = now;
                window.count = 0;
            }
            window.count++;
            if (window.count > maxRequests) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Too Many Requests\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveClientKey(HttpServletRequest request) {
        String apiKey = request.getHeader("X-API-KEY");
        String ip = request.getRemoteAddr();
        return apiKey != null ? apiKey : ip;
    }

    private static class Window {
        long start;
        int count;
        Window(long start, int count) { this.start = start; this.count = count; }
    }
}


