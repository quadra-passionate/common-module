package com.quadra.common.cookie;

import com.quadra.common.cookie.servlet.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for CookieUtils")
public class CookieUtilsTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private CookieUtils utils;

    @BeforeEach
    void setUp() {
        utils = new CookieUtils("example.com", true, "Lax");
    }

    @Test
    @DisplayName("getCookie returns the existing cookie when present")
    void getCookieShouldReturnExistingCookie() {
        // given
        Cookie target = new Cookie("sessionId", "abc123");
        when(request.getCookies()).thenReturn(new Cookie[] {target});

        // when
        Optional<Cookie> result = utils.getCookie(request, "sessionId");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo("abc123");
    }

    @Test
    @DisplayName("getCookie returns Optional.empty() when cookie name does not exist")
    void getCookie() {
        // given
        when(request.getCookies()).thenReturn(new Cookie[] {});

        // when
        Optional<Cookie> result = utils.getCookie(request, "sessionId");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("addCookie adds a Set-Cookie header to the response")
    void addCookieShouldAddHeader() {
        // when
        utils.addCookie(response, "sessionId", "abc123", "/", 3600, true);

        // then
        verify(response).addHeader(eq("Set-Cookie"), contains("sessionId=abc123"));
        verify(response).addHeader(eq("Set-Cookie"), contains("Domain=example.com"));
        verify(response).addHeader(eq("Set-Cookie"), contains("Path=/"));
    }

    @Test
    @DisplayName("deleteCookie adds a Set-Cookie header with Max-Age=0")
    void deleteCookieShouldAddExpiredHeader() {
        // when
        utils.deleteCookie(response, "sessionId", "example.com", "/");

        // then
        verify(response).addHeader(eq("Set-Cookie"), contains("sessionId="));
        verify(response).addHeader(eq("Set-Cookie"), contains("Domain=example.com"));
        verify(response).addHeader(eq("Set-Cookie"), contains("Path=/"));
        verify(response).addHeader(eq("Set-Cookie"), contains("Max-Age=0"));
    }
}
