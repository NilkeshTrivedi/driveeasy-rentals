package com.driveeasy.api.v1;

import com.driveeasy.dto.request.LoginRequest;
import com.driveeasy.dto.request.RegisterRequest;
import com.driveeasy.dto.response.AuthResponse;
import com.driveeasy.model.User;
import com.driveeasy.repository.UserRepository;
import com.driveeasy.security.JwtProperties;
import com.driveeasy.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Login and registration endpoints")
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthApiController(AuthenticationManager authenticationManager,
                             JwtService jwtService,
                             JwtProperties jwtProperties,
                             UserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * POST /api/v1/auth/login
     * Body: { "username": "admin", "password": "admin123" }
     * Returns: JWT token + role
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate and receive a JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // Spring Security authenticates credentials — throws on failure
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Extract role for embedding in token (strip ROLE_ prefix for clean API response)
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("STAFF");

        String token = jwtService.generateToken(userDetails, role);

        return ResponseEntity.ok(new AuthResponse(
                token,
                userDetails.getUsername(),
                role,
                jwtProperties.getExpirationMs()
        ));
    }

    /**
     * POST /api/v1/auth/register
     * Admin-only in production — creates a new staff/admin user.
     * For Phase 3b this will be extended for customer self-registration.
     */
    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Create a new staff or admin account (admin only in production)")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(java.util.Map.of("error", "Username already taken"));
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole(),
                request.getFullName()
        );
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(java.util.Map.of(
                        "message", "User created successfully",
                        "username", user.getUsername(),
                        "role", user.getRole().name()
                ));
    }
}