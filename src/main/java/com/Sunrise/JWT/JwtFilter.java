package com.Sunrise.JWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final List<String> excludedPaths = com.Sunrise.Configurations.PublicEndpoints.ENDPOINTS;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        // Пропускаем публичные endpoints
        if (excludedPaths.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "MISSING_TOKEN");
            return;
        }

        String username = null;
        Long userId = null;
        String jwt = null;

        try {
            jwt = authorizationHeader.substring(7);

            if (jwt.trim().isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "EMPTY_TOKEN");
                return;
            }

            username = jwtUtil.extractUsername(jwt);
            userId = jwtUtil.extractUserId(jwt);
        }
        catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "INVALID_TOKEN");
            return;
        }

        if (username == null || username.trim().isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "INVALID_TOKEN_PAYLOAD");
            return;
        }

        if (!jwtUtil.validateToken(jwt, username)) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_VALIDATION_FAILED");
            return;
        }

        try
        {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, List.of());

            Map<String, Object> details = new HashMap<>();
            details.put("userId", userId);
            details.put("webAuthenticationDetails", new WebAuthenticationDetailsSource().buildDetails(request));
            auth.setDetails(details);

            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "AUTHENTICATION_ERROR");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String errorCode) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/text");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(errorCode);
        response.getWriter().flush();
    }
}