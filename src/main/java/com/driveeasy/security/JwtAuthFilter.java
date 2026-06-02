package com.driveeasy.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Runs once per request (OncePerRequestFilter guarantee).
 *
 * Flow:
 *   1. Read the Authorization header
 *   2. If it starts with "Bearer ", extract the token
 *   3. Extract username from token
 *   4. Load UserDetails from DB
 *   5. Validate token against UserDetails
 *   6. If valid, set authentication in SecurityContext
 *   7. Continue filter chain regardless
 *
 * If any step fails (missing header, expired token, unknown user),
 * the filter simply continues without setting auth — Spring Security
 * will then reject the request at the authorization check.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Skip filter — no Bearer token present
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the token (everything after "Bearer ")
        final String jwt = authHeader.substring(7);
        final String username;

        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            // Malformed token — skip, let Spring Security handle the 401
            filterChain.doFilter(request, response);
            return;
        }

        // Only set auth if not already authenticated (avoid re-processing)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,                          // credentials null after auth
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Publish to SecurityContext — request is now authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}