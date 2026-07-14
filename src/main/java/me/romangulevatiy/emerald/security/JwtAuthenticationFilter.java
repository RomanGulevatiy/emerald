package me.romangulevatiy.emerald.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.romangulevatiy.emerald.service.TokenBlacklistService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserPrincipalService userPrincipalService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        final String jti;

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);

        try {
            username = jwtService.extractUsername(jwt);

            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                jti = jwtService.extractJti(jwt);

                boolean revoked;
                try {
                    revoked = tokenBlacklistService.isRevoked(jti);
                }
                catch(Exception ex) {
                    log.error("Failed to check token blacklist for jti {}, failing closed", jti, ex);
                    revoked = true;
                }

                if(revoked) {
                    log.warn("Rejected revoked token: {}", jti);
                    filterChain.doFilter(request, response);
                    return;
                }

                UserDetails userDetails = this.userPrincipalService.loadUserByUsername(username);

                if(jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        catch(ExpiredJwtException ex) {
            log.debug("Token expired: {}", ex.getMessage());
        }
        catch(Exception ex) {
            log.warn("JWT authentication failed: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
