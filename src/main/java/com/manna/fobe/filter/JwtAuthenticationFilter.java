package com.manna.fobe.filter;

import com.manna.fobe.common.utils.JwtUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {

    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String authorizationToken = request.getHeader("Authorization");

        if (authorizationToken != null && authorizationToken.startsWith("Bearer ")) {
            // 토큰 검증 후 userId 추출
            if (jwtUtil.validateToken(authorizationToken)) {
                int userId = jwtUtil.getUserIdFromToken(authorizationToken);
                request.setAttribute("userId", userId);
            } else {
                // 유효하지 않은 토큰 처리
                 response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                 return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
