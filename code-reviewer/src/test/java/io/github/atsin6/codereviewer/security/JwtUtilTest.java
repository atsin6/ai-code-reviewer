package io.github.atsin6.codereviewer.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    // A securely generated 256-bit key for testing
    private final String testSecret = "my-test-secret-key-that-is-at-least-256-bits-long-1234567890123456";

    @BeforeEach
    void setUp() {
        // 1 hour expiration
        jwtUtil = new JwtUtil(testSecret, 3600000L);
    }

    @Test
    void shouldGenerateAndValidateToken() {
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);

        assertThat(token).isNotBlank();

        String extractedEmail = jwtUtil.extractEmail(token);
        assertThat(extractedEmail).isEqualTo(email);

        boolean isValid = jwtUtil.isTokenValid(token, email);
        assertThat(isValid).isTrue();
    }

    @Test
    void shouldReturnFalseForDifferentUser() {
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);

        boolean isValid = jwtUtil.isTokenValid(token, "other@example.com");
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldFailOnExpiredToken() throws InterruptedException {
        // 1 ms expiration
        JwtUtil fastExpireJwtUtil = new JwtUtil(testSecret, 1L);
        String token = fastExpireJwtUtil.generateToken("test@example.com");

        Thread.sleep(10); // wait for expiration

        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> fastExpireJwtUtil.extractEmail(token));
    }
}
