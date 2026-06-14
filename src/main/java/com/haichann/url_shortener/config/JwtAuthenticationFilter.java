package com.haichann.url_shortener.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haichann.url_shortener.base.ErrorResponse;
import com.haichann.url_shortener.exception.AppException;
import com.haichann.url_shortener.helper.JwtHelper;
import com.haichann.url_shortener.model.enums.UserStatus;
import com.haichann.url_shortener.repository.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    static String BEARER_PREFIX = "Bearer ";

    JwtHelper jwtHelper;
    UserRepo userRepo;
    ObjectMapper objectMapper;
    MessageSource messageSource;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        String email;
        try {
            email = jwtHelper.validateToken(token);
        } catch (AppException exception) {
            response.setStatus(exception.getErrorCode().getStatusCode().value());
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(ErrorResponse.builder()
                    .message(resolveMessage(exception.getMessage()))
                    .error(exception.getErrorCode().name())
                    .timestamp(Instant.now())
                    .path(request.getRequestURI())
                    .build()));
            return;
        }

        userRepo.getUserByEmail(email)
                .filter(user -> UserStatus.ACTIVE.equals(user.getStatus()))
                .ifPresent(user -> {
                    String roleName = user.getRole() == null ? "" : user.getRole().getRoleName();
                    var authorities = roleName.isBlank()
                            ? List.<SimpleGrantedAuthority>of()
                            : List.of(new SimpleGrantedAuthority(roleName));

                    var authentication = new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            null,
                            authorities
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });

        filterChain.doFilter(request, response);
    }

    private String resolveMessage(String messageKey) {
        try {
            return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException exception) {
            return messageKey;
        }
    }
}
