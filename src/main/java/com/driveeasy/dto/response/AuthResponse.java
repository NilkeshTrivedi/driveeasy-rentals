package com.driveeasy.dto.response;

/**
 * Returned on successful login. The client stores the token
 * and sends it as: Authorization: Bearer <token>
 */
public class AuthResponse {

    private String token;
    private String username;
    private String role;
    private long expiresInMs;

    public AuthResponse(String token, String username, String role, long expiresInMs) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.expiresInMs = expiresInMs;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public long getExpiresInMs() { return expiresInMs; }
}