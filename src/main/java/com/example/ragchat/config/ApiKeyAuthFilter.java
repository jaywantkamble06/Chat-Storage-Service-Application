package com.example.ragchat.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(1)
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${app.api-keys}")
    private String configuredApiKeysCsv;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator/health") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/swagger-ui") ||
               path.equals("/");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String apiKey = request.getHeader("X-API-KEY");
        boolean allowed = false;
        if (apiKey != null && configuredApiKeysCsv != null) {
            for (String k : configuredApiKeysCsv.split(",")) {
                if (apiKey.equals(k.trim())) {
                    allowed = true;
                    break;
                }
            }
        }
        if (!allowed) {
            log.warn("unauthorized_request path={} reason={}", request.getRequestURI(), "missing_or_invalid_api_key");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Unauthorized\"}");
            return;
        }
        log.debug("authorized_request path={} api_key_present=true", request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}


