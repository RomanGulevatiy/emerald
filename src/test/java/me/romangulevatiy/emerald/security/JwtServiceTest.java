package me.romangulevatiy.emerald.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import me.romangulevatiy.emerald.entity.UserEntity;
import me.romangulevatiy.emerald.entity.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for JWT service")
class JwtServiceTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8));

    private final JwtService jwtService = new JwtService();
    private final SecretKey signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));

    private UserEntity userEntity;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "accessExpiration", 900_000L);

        userEntity = UserEntity.builder()
                .id(67L)
                .username("SuperUser")
                .password("encoded-password")
                .role(UserRole.ADMIN)
                .build();
        userPrincipal = new UserPrincipal(userEntity);
    }

    @DisplayName("createExtraClaims should return user id and role when user is provided")
    @Test
    void createExtraClaims_ShouldReturnClaims_WhenUserIsProvided() {
        Map<String, Object> claims = jwtService.createExtraClaims(userEntity);

        assertEquals(67L, claims.get("userId"));
        assertEquals("ADMIN", claims.get("role"));
    }

    @DisplayName("extractJti should return the token id when token is valid")
    @Test
    void extractJti_ShouldReturnTokenId_WhenTokenIsValid() {
        String token = buildToken("token-id", userPrincipal.getUsername(), Map.of("userId", 67L, "role", "ADMIN"), new Date(System.currentTimeMillis() + 900_000L));

        assertEquals("token-id", jwtService.extractJti(token));
    }

    @DisplayName("extractUserRole should return role when token contains role claim")
    @Test
    void extractUserRole_ShouldReturnRole_WhenTokenContainsRoleClaim() {
        String token = buildToken("token-id", userPrincipal.getUsername(), Map.of("userId", 67L, "role", "ADMIN"), new Date(System.currentTimeMillis() + 900_000L));

        assertEquals("ADMIN", jwtService.extractUserRole(token));
    }

    @DisplayName("extractUserId should return user id when token contains userId claim")
    @Test
    void extractUserId_ShouldReturnUserId_WhenTokenContainsUserIdClaim() {
        String token = buildToken("token-id", userPrincipal.getUsername(), Map.of("userId", 67L, "role", "ADMIN"), new Date(System.currentTimeMillis() + 900_000L));

        assertEquals(67L, jwtService.extractUserId(token));
    }

    @DisplayName("extractUsername should return username when token is valid")
    @Test
    void extractUsername_ShouldReturnUsername_WhenTokenIsValid() {
        String token = jwtService.generateAccessToken(userPrincipal);

        assertEquals("SuperUser", jwtService.extractUsername(token));
    }

    @DisplayName("extractUsername should throw an exception when token is malformed")
    @Test
    void extractUsername_ShouldThrowException_WhenTokenIsMalformed() {
        assertThrows(RuntimeException.class, () -> jwtService.extractUsername("not-a-jwt"));
    }

    @DisplayName("extractExpiration should return expiration when token is valid")
    @Test
    void extractExpiration_ShouldReturnExpiration_WhenTokenIsValid() {
        Date expiration = new Date(System.currentTimeMillis() + 900_000L);
        String token = buildToken("token-id", userPrincipal.getUsername(), Map.of("userId", 67L, "role", "ADMIN"), expiration);

        assertEquals(expiration.getTime() / 1000, jwtService.extractExpiration(token).getTime() / 1000);
    }

    @DisplayName("generateAccessToken should return a signed token with claims when extra claims are provided")
    @Test
    void generateAccessToken_ShouldReturnTokenWithClaims_WhenExtraClaimsAreProvided() {
        Map<String, Object> extraClaims = new HashMap<>(jwtService.createExtraClaims(userEntity));
        extraClaims.put("tenant", "emerald");

        String token = jwtService.generateAccessToken(extraClaims, userPrincipal);

        assertNotNull(token);
        assertEquals("SuperUser", jwtService.extractUsername(token));
        assertEquals(67L, jwtService.extractUserId(token));
        assertEquals("ADMIN", jwtService.extractUserRole(token));
        assertEquals("emerald", parseClaims(token).get("tenant", String.class));
    }

    @DisplayName("generateAccessToken should return a signed token when no extra claims are provided")
    @Test
    void generateAccessToken_ShouldReturnToken_WhenNoExtraClaimsAreProvided() {
        String token = jwtService.generateAccessToken(userPrincipal);

        assertNotNull(token);
        assertEquals("SuperUser", jwtService.extractUsername(token));
    }

    @DisplayName("isTokenValid should return true when token matches user and is not expired")
    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenMatchesUserAndIsNotExpired() {
        String token = jwtService.generateAccessToken(userPrincipal);

        assertTrue(jwtService.isTokenValid(token, userPrincipal));
    }

    @DisplayName("isTokenValid should return false when token username does not match user details")
    @Test
    void isTokenValid_ShouldReturnFalse_WhenUsernameDoesNotMatchUserDetails() {
        String token = jwtService.generateAccessToken(userPrincipal);
        UserPrincipal otherPrincipal = new UserPrincipal(UserEntity.builder()
                .id(67L)
                .username("DuperUser")
                .password("encoded-password")
                .role(UserRole.USER)
                .build());

        assertFalse(jwtService.isTokenValid(token, otherPrincipal));
    }

    @DisplayName("isTokenValid should throw an exception when token is expired")
    @Test
    void isTokenValid_ShouldThrowException_WhenTokenIsExpired() {
        String expiredToken = buildToken("token-id", userPrincipal.getUsername(), Map.of("userId", 67L, "role", "ADMIN"), new Date(System.currentTimeMillis() - 1_000L));

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken, userPrincipal));
    }

    @DisplayName("isTokenValid should throw an exception when token is malformed")
    @Test
    void isTokenValid_ShouldThrowException_WhenTokenIsMalformed() {
        assertThrows(RuntimeException.class, () -> jwtService.isTokenValid("not-a-jwt", userPrincipal));
    }

    private String buildToken(String jti, String subject, Map<String, Object> claims, Date expiration) {
        return Jwts.builder()
                .id(jti)
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expiration)
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}