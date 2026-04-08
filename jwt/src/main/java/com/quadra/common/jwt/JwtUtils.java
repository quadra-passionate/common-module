package com.quadra.common.jwt;

import com.quadra.common.jwt.config.JwtProperties;
import com.quadra.common.jwt.exception.JwtErrorCode;
import com.quadra.common.jwt.exception.JwtValidationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

/**
 * Utility class for creating, parsing, and validating JWT tokens.
 *
 * <p>
 * Provides methods to create and validate JWT tokens, and parse JWT claims.
 *
 * @author <a href="https://github.com/rymph">Wooseong Urm</a>
 * @since 1.0.0
 */
@Slf4j
public class JwtUtils {

    private final Key accessKey;
    private final Key refreshKey;
    private final long accessExpires; // milliseconds
    private final long refreshExpires; // milliseconds

    /**
     * Constructs a new {@code JwtUtils} instance with the provided secret key strings and expiration times.
     *
     * <p>
     * Validates inputs and initializes signing keys for access and refresh tokens.
     *
     * @param accessSecret   the secret key string for signing access tokens
     * @param refreshSecret  the secret key string for signing refresh tokens
     * @param accessExpires  the expiration time of access tokens in milliseconds
     * @param refreshExpires the expiration time of refresh tokens in milliseconds
     */
    public JwtUtils(String accessSecret, String refreshSecret, long accessExpires, long refreshExpires) {
        Assert.hasText(accessSecret, "jwt.access.secret must be set to a non-null string value.");
        Assert.hasText(refreshSecret, "jwt.refresh.secret must be set to a non-null string value.");
        Assert.isTrue(accessExpires > 0, "jwt.access.expires must be set to an integer greater than 0");
        Assert.isTrue(refreshExpires > 0, "jwt.refresh.expires must be set to an integer greater than 0");

        this.accessKey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
        this.accessExpires = accessExpires;
        this.refreshExpires = refreshExpires;

        log.debug("JwtUtils initialized with (accessExpires: {}ms, refreshExpires: {}ms)", accessExpires, refreshExpires);
    }

    /**
     * Constructs a new {@code JwtUtils} instance using the provided {@link JwtProperties}.
     *
     * <p>
     * Internally extracts the secrets and expiration times for both access and refresh tokens
     * and delegates to another constructor.
     *
     * @param jwtProperties the JWT configuration properties
     * @see JwtProperties
     * @see #JwtUtils(String, String, long, long)
     */
    public JwtUtils(JwtProperties jwtProperties) {
        this(jwtProperties.getAccess().getSecret(),
                jwtProperties.getRefresh().getSecret(),
                jwtProperties.getAccess().getExpires(),
                jwtProperties.getRefresh().getExpires());
    }

    /**
     * Creates a new access token for a user with roles and issued timestamp.
     *
     * <p>
     * Generates a signed JWT access token including user ID as a subject and roles.
     *
     * @param userId   the user ID
     * @param roles    the roles assigned to the user
     * @param issuedAt the issued timestamp in milliseconds
     * @return the signed JWT access token
     */
    public String createAccessToken(Long userId, Collection<String> roles, long issuedAt) {
        Assert.notNull(userId, "userId must not be null.");
        Assert.notEmpty(roles, "roles must not be null or empty.");

        String token = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("roles", roles)
                .setIssuedAt(new Date(issuedAt))
                .setExpiration(new Date(issuedAt + accessExpires))
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();

        log.debug("Access token generated for (user: {})", userId);
        log.debug("Access token: {}", token);

        return token;
    }

    /**
     * Creates a new access token for a user using the current time.
     *
     * @param userId the user ID
     * @param roles  the roles assigned to the user
     * @return the signed JWT access token
     * @see #createAccessToken(Long, Collection, long)
     */
    public String createAccessToken(Long userId, Collection<String> roles) {
        long now = System.currentTimeMillis();
        return createAccessToken(userId, roles, now);
    }

    /**
     * Creates a new refresh token for a user with issued timestamp.
     *
     * <p>
     * Generates a signed JWT refresh token including user ID as a subject.
     *
     * @param userId   the user ID
     * @param issuedAt the issued timestamp in milliseconds
     * @return the signed JWT refresh token
     */
    public String createRefreshToken(Long userId, long issuedAt) {
        Assert.notNull(userId, "userId must not be null.");

        String token = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(issuedAt))
                .setExpiration(new Date(issuedAt + refreshExpires))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();

        log.debug("Refresh token generated for (user: {})", userId);
        log.debug("Refresh token: {}", token);

        return token;
    }

    /**
     * Creates a new refresh token for a user with the current timestamp.
     *
     * @param userId the user ID
     * @return the signed JWT refresh token
     * @see #createRefreshToken(Long, long)
     */
    public String createRefreshToken(Long userId) {
        long now = System.currentTimeMillis();
        return createRefreshToken(userId, now);
    }

    /**
     * Parses a JWT token using the provided signing key.
     *
     * @param token      the JWT token string
     * @param signingKey the key used to verify the signature
     * @return parsed claims
     * @throws JwtValidationException if the token is invalid or expired
     */
    private Claims parseToken(String token, Key signingKey) {
        Assert.hasText(token, "token must not be empty");
        Claims claims = null;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.debug("JWT parsed successfully for (user: {})", claims.getSubject());
        } catch (ExpiredJwtException e) {
            log.debug("JWT expired");
            throw new JwtValidationException(JwtErrorCode.EXPIRED_JWT);
        } catch (SignatureException e) {
            log.debug("Invalid JWT signature");
            throw new JwtValidationException(JwtErrorCode.INVALID_SIGNATURE);
        } catch (JwtException e) {
            log.debug("Unsupported or Malformed JWT accepted");
            throw new JwtValidationException(JwtErrorCode.UNSUPPORTED_OR_MALFORMED);
        }
        return claims;
    }

    /**
     * Parses an access token using the configured access key.
     *
     * @param token the JWT access token
     * @return parsed claims
     * @throws JwtValidationException if the token is invalid or expired
     * @see #parseToken(String, Key)
     */
    public Claims parseAccessToken(String token) {
        return parseToken(token, accessKey);
    }

    /**
     * Parses a refresh token using the configured refresh key.
     *
     * @param token the JWT refresh token
     * @return parsed claims
     * @throws JwtValidationException if the token is invalid or expired
     * @see #parseToken(String, Key)
     */
    public Claims parseRefreshToken(String token) {
        return parseToken(token, refreshKey);
    }

    /**
     * Validates a JWT token using the provided signing key.
     *
     * @param token      the JWT token string
     * @param signingKey the key used to verify the signature
     * @return {@code true} if valid, {@code false} otherwise
     * @see #parseToken(String, Key)
     */
    private boolean validateToken(String token, Key signingKey) {
        try {
            parseToken(token, signingKey);
            return true;
        } catch (JwtValidationException e) {
            return false;
        }
    }

    /**
     * Validates an access token using the configured access key.
     *
     * @param token the JWT access token
     * @return {@code true} if valid, {@code false} otherwise
     * @see #validateToken(String, Key)
     */
    public boolean validateAccessToken(String token) {
        return validateToken(token, accessKey);
    }

    /**
     * Validates a refresh token using the configured refresh key.
     *
     * @param token the JWT refresh token
     * @return {@code true} if valid, {@code false} otherwise
     * @see #validateToken(String, Key)
     */
    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshKey);
    }

    /**
     * Returns the configured expiration time of access tokens in seconds.
     *
     * @return the expiration time of access tokens in seconds.
     */
    public Long getAccessExpiresInSeconds() {
        return this.accessExpires / 1000;
    }

    /**
     * Returns the configured expiration time of refresh tokens in seconds.
     *
     * @return the expiration time of refresh tokens in seconds.
     */
    public Long getRefreshExpiresInSeconds() {
        return this.refreshExpires / 1000;
    }

    /**
     * Extracts the user ID from a JWT token string using the provided signing key.
     *
     * @param token      the JWT token string
     * @param signingKey the key used to verify the signature
     * @return the user ID
     * @throws JwtValidationException if the token is invalid or expired
     * @see #parseToken(String, Key)
     */
    private Long getUserId(String token, Key signingKey) {
        return Long.parseLong(parseToken(token, signingKey).getSubject());
    }

    /**
     * Extracts the user ID from an access token.
     *
     * @param token the JWT access token
     * @return the user ID
     * @throws JwtValidationException if the token is invalid or expired
     * @see #getUserId(String, Key)
     */
    public Long getUserIdFromAccessToken(String token) {
        return getUserId(token, accessKey);
    }

    /**
     * Extracts the user ID from a refresh token.
     *
     * @param token the JWT refresh token
     * @return the user ID
     * @throws JwtValidationException if the token is invalid or expired
     * @see #getUserId(String, Key)
     */
    public Long getUserIdFromRefreshToken(String token) {
        return getUserId(token, refreshKey);
    }

    /**
     * Retrieves the roles assigned to the user from an access token.
     *
     * @param token the JWT access token
     * @return a list of role names
     * @throws JwtValidationException if the token is invalid or expired
     * @see #parseToken(String, Key)
     */
    public List<String> getRoles(String token) {
        return ((List<?>) parseAccessToken(token).get("roles"))
                .stream()
                .map(Object::toString)
                .toList();
    }
}
