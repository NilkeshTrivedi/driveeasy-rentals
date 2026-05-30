package com.driveeasy.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /*
     * BCryptPasswordEncoder is the industry standard for hashing passwords.
     * Strength 12 means 2^12 = 4096 hashing rounds — slow enough to be
     * secure against brute force, fast enough for login.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /*
     * DaoAuthenticationProvider connects:
     *   - our UserDetailsService (loads user from DB)
     *   - our PasswordEncoder (verifies BCrypt hash)
     * Spring Security uses this during every login attempt.
     */
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

    /*
     * After successful login, redirect based on role:
     *   ADMIN → /admin/cars
     *   STAFF → /staff/reservations
     *   anyone else → /
     */
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (isAdmin) {
                response.sendRedirect("/admin/cars");
            } else {
                response.sendRedirect("/staff/reservations");
            }
        };
    }

    /*
     * THE MAIN SECURITY RULEBOOK
     * This defines what URLs are public, what requires which role,
     * and how login/logout works.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth
                        // Public resources — no login needed
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/login", "/access-denied").permitAll()

                        // Admin-only routes
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Staff + Admin routes
                        .requestMatchers("/staff/**").hasAnyRole("ADMIN", "STAFF")

                        // Dashboard — both roles
                        .requestMatchers("/").hasAnyRole("ADMIN", "STAFF")

                        // Everything else requires login
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")           // our custom login page
                        .loginProcessingUrl("/login")  // Spring processes POST to this URL
                        .successHandler(successHandler())
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
}