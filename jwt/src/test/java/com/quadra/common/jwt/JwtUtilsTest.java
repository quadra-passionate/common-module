package com.quadra.common.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Unit Tests for JwtUtils")
public class JwtUtilsTest {

    private JwtUtils utils;

    @BeforeEach
    void setUp() {
        utils = new JwtUtils("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb", 1800000, 360000000);
    }

    @Test
    @DisplayName("createAccessToken creates a access token string")
    void createAndParseAccessToken() {
        // given
        Long userId = 123L;
        List<String> roles = List.of("USER", "EMPLOYEE");
        // when
        String token = utils.createAccessToken(userId, roles);
        Claims claims = utils.parseAccessToken(token);

        // then
        assertThat(claims.getSubject()).isEqualTo(String.valueOf(userId));
        assertThat(claims.get("roles")).isEqualTo(roles);
    }

    @Test
    @DisplayName("createRefreshToken creates a refresh token string")
    void createAndParseRefreshToken() {
        // given
        Long userId = 123L;

        // when
        String token = utils.createRefreshToken(userId);
        Claims claims = utils.parseRefreshToken(token);

        // then
        assertThat(claims.getSubject()).isEqualTo(String.valueOf(userId));
    }

    @Test
    @DisplayName("validateAccessToken returns true if valid")
    void validateAccessTokenOnSuccess() {
        // given
        Long userId = 1L;
        List<String> roles = List.of("USER");
        String token = utils.createAccessToken(userId, roles);

        // when
        boolean isValid = utils.validateAccessToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("validateAccessToken returns false if invalid")
    void validateAccessTokenOnFailure() {
        // given
        JwtUtils fakeUtils = new JwtUtils("cccccccccccccccccccccccccccccccc", "dddddddddddddddddddddddddddddddd", 3000000, 300000000);
        Long userId = 1L;
        List<String> roles = List.of("USER");
        String token = fakeUtils.createAccessToken(userId, roles);

        // when
        boolean isValid = utils.validateAccessToken(token);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("validateRefreshToken returns true if valid")
    void validateRefreshTokenOnSuccess() {
        // given
        Long userId = 1L;
        String token = utils.createRefreshToken(userId);

        // when
        boolean isValid = utils.validateRefreshToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("validateRefreshToken returns false if invalid")
    void validateRefreshTokenOnFailure() {
        // given
        JwtUtils fakeUtils = new JwtUtils("cccccccccccccccccccccccccccccccc", "dddddddddddddddddddddddddddddddd", 3000000, 300000000);
        Long userId = 1L;
        String token = fakeUtils.createRefreshToken(userId);

        // when
        boolean isValid = utils.validateRefreshToken(token);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("getUserIdFromAccessToken and getRoles must be the same with inputs")
    void getUserIdAndRolesFromAccessTokenRespectively() {
        // given
        Long userId = 123L;
        List<String> roles = List.of("USER", "EMPLOYEE");
        String token = utils.createAccessToken(userId, roles);

        // when
        Long extractedUserId = utils.getUserIdFromAccessToken(token);
        List<String> extractedRoles = utils.getRoles(token);

        assertThat(extractedUserId).isEqualTo(userId);
        assertThat(extractedRoles).isEqualTo(roles);
    }

    @Test
    @DisplayName("parseAccessToken contains the exactly same value with inputs")
    void getUserIdAndRolesFromAccessTokenClaims() {
        // given
        Long userId = 123L;
        List<String> roles = List.of("USER", "EMPLOYEE");
        String token = utils.createAccessToken(userId, roles);

        // when
        Claims claims = utils.parseAccessToken(token);
        Long userIdInClaims = Long.valueOf(claims.getSubject());
        List<String> rolesInClaims = ((List<?>) claims.get("roles"))
                .stream()
                .map(Object::toString)
                .toList();

        assertThat(userIdInClaims).isEqualTo(userId);
        assertThat(rolesInClaims).containsExactlyElementsOf(roles);
    }

    @Test
    @DisplayName("getUserIdFromRefreshTOken must be the same with an input")
    void getUserIdFromRefreshToken() {
        // given
        Long userId = 123L;
        String token = utils.createRefreshToken(userId);

        // when
        Long extractedUserId = utils.getUserIdFromRefreshToken(token);

        // then
        assertThat(extractedUserId).isEqualTo(userId);
    }
}
