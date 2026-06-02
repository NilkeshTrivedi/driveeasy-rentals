package com.driveeasy.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * TWO security filter chains, evaluated by @Order:
 *
 *   Chain 1 (@Order(1)) — matches /api/** only
 *     - Stateless (no session, no CSRF cookies)
 *     - JWT filter runs before UsernamePasswordAuthenticationFilter
 *     - Returns 401/403 JSON, never redirects to login page
 *
 *   Chain 2 (@Order(2)) — matches everything else (Thymeleaf MVC)
 *     - Session-based (Phase 2 behaviour, unchanged)
 *     - Form login + logout work exactly as before
 *
 * Spring Security picks the FIRST chain whose requestMatcher matches.
 * Because Chain 1 matches /api/** specifically, all other URLs fall through
 * to Chain 2. The two chains share the same AuthenticationProvider and
 * PasswordEncoder beans.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
                          JwtAuthFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ── Chain 1: REST API (stateless, JWT) ──────────────────────────────────

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                // Only this chain handles /api/** requests
                .securityMatcher("/api/**")
                .authenticationProvider(authenticationProvider())

                // Stateless — no HttpSession, no CSRF tokens needed
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        // Auth endpoints — public (login, register)
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Swagger/OpenAPI — public
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api-docs/**"
                        ).permitAll()

                        // Admin-only API routes
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // Staff + Admin routes
                        .requestMatchers("/api/v1/staff/**").hasAnyRole("ADMIN", "STAFF")

                        // Everything else under /api/** requires authentication
                        .anyRequest().authenticated()
                )

                // JWT filter runs before Spring's username/password filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // Return 401 JSON on missing/invalid auth — never redirect
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\":\"Unauthorized\",\"message\":\"" +
                                            authException.getMessage() + "\"}"
                            );
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\":\"Forbidden\",\"message\":\"" +
                                            accessDeniedException.getMessage() + "\"}"
                            );
                        })
                );

        return http.build();
    }

    // ── Chain 2: MVC / Thymeleaf (session-based, Phase 2 unchanged) ─────────

    @Bean
    @Order(2)
    public SecurityFilterChain mvcFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/login", "/access-denied").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/staff/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/").hasAnyRole("ADMIN", "STAFF")
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(mvcSuccessHandler())
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied")
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler mvcSuccessHandler() {
        return (request, response, authentication) -> {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            response.sendRedirect(isAdmin ? "/admin/cars" : "/staff/reservations");
        };
    }
}