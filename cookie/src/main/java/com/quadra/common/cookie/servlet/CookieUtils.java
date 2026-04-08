package com.quadra.common.cookie.servlet;

import java.util.Arrays;
import java.util.Optional;

import com.quadra.common.cookie.model.CookieAttributes;
import com.quadra.common.cookie.config.CookieProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for managing HTTP cookies in servlet-based applications.
 *
 * <p>
 * Provides convenience methods for retrieving, adding, and deleting cookies
 * using {@link HttpServletRequest} and {@link HttpServletResponse}.
 * Supports both direct cookie attributes and the {@link CookieAttributes}.
 *
 * @author <a href="https://github.com/rymph">Wooseong Urm</a>
 * @see HttpServletRequest
 * @see HttpServletResponse
 * @see CookieAttributes
 * @since 1.0.0
 */
@Slf4j
@Getter
public class CookieUtils {

    private final Boolean defaultSecure;
    private final String defaultSameSite;
    private final String defaultDomain;

    /**
     * Constructs a new {@code CookieUtils} instance with the provided default security and same-site settings.
     *
     * @param defaultSecure   whether cookie should be secure by default
     * @param defaultSameSite the same-site policy cookies should follow by default
     */
    public CookieUtils(String defaultDomain, Boolean defaultSecure, String defaultSameSite) {
        this.defaultDomain = defaultDomain;
        this.defaultSecure = defaultSecure;
        this.defaultSameSite = defaultSameSite;

        log.debug("CookieUtils initialized with (domain: {}, secure: {}, sameSite: {})", defaultDomain, defaultSecure, defaultSameSite);
    }

    /**
     * Constructs a new {@code CookieUtils} instance with the provided {@link CookieProperties}.
     *
     * <p>
     * Internally extracts the secure flag and same-site policy for cookies
     * and delegates to another constructor.
     *
     * @param cookieProperties the cookie configuration properties
     * @see CookieProperties
     * @see #CookieUtils(String, Boolean, String)
     */
    public CookieUtils(CookieProperties cookieProperties) {
        this(cookieProperties.getDomain(), cookieProperties.getSecure(), cookieProperties.getSameSite());
    }

    /**
     * Retrieves a cookie by name from the given request.
     *
     * @param request the HTTP servlet request
     * @param name    the name of the cookie to retrieve
     * @return an {@link Optional} containing the cookie if found, {@link Optional#empty()} otherwise
     */
    public Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Optional<Cookie> optionalCookie = Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> cookie.getName().equals(name))
                        .findFirst());

        optionalCookie.ifPresentOrElse(
                cookie -> log.debug("Cookie found with (name: {}, value: {})",
                        cookie.getName(),
                        cookie.getValue()),
                () -> log.debug("No cookie found with (name: {})", name)
        );

        return optionalCookie;
    }

    /**
     * Adds a cookie to the response using the provided {@link CookieAttributes}.
     *
     * @param response         the HTTP servlet response
     * @param cookieAttributes the cookie attributes to apply
     */
    public void addCookie(HttpServletResponse response, CookieAttributes cookieAttributes) {
        response.addHeader("Set-Cookie", cookieAttributes.toString());
    }

    /**
     * Adds a cookie to the response with the full attribute customization.
     *
     * @param response the HTTP servlet response
     * @param name     the name of the cookie
     * @param value    the value of the cookie
     * @param domain   the domain of the cookie
     * @param path     the path of the cookie
     * @param maxAge   the maximum age in seconds
     * @param httpOnly whether the cookie is HTTP-only
     * @param secure   whether the cookie is secure
     * @param sameSite the same-site policy
     * @throws IllegalArgumentException if {@code name} is null or {@code sameSite} is invalid
     * @see CookieAttributes#CookieAttributes(String, String, Integer, String, String, Boolean, Boolean, String)
     */
    public void addCookie(HttpServletResponse response, String name, String value, String domain, String path, Integer maxAge, Boolean httpOnly, Boolean secure, String sameSite) {
        CookieAttributes cookieAttributes = CookieAttributes.builder()
                .name(name)
                .value(value)
                .domain(domain)
                .path(path)
                .maxAge(maxAge)
                .secure(secure)
                .httpOnly(httpOnly)
                .sameSite(sameSite)
                .build();
        addCookie(response, cookieAttributes);
    }

    /**
     * Adds a cookie to the response using default secure flag and same-site policy.
     *
     * @param response the HTTP servlet response
     * @param name     the name of the cookie
     * @param value    the value of the cookie
     * @param path     the path of the cookie
     * @param maxAge   the maximum age in seconds
     * @param httpOnly whether the cookie is HTTP-only
     * @throws IllegalArgumentException if {@code name} is null or {@code sameSite} is invalid
     * @see CookieAttributes#CookieAttributes(String, String, Integer, String, String, Boolean, Boolean, String)
     */
    public void addCookie(HttpServletResponse response, String name, String value, String path, Integer maxAge, Boolean httpOnly) {
        addCookie(response, name, value, defaultDomain, path, maxAge, httpOnly, defaultSecure, defaultSameSite);
    }


    /**
     * Deletes a cookie with the provided name, domain, and path.
     *
     * @param response the HTTP servlet response
     * @param name     the name of the cookie to delete
     * @param domain   the domain of the cookie
     * @param path     the path of the cookie
     * @throws IllegalArgumentException if {@code name} is null
     * @see CookieAttributes#CookieAttributes(String, String, Integer, String, String, Boolean, Boolean, String)
     */
    public void deleteCookie(HttpServletResponse response, String name, String domain, String path) {
        CookieAttributes cookie = CookieAttributes.builder()
                .name(name)
                .domain(domain)
                .path(path)
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * Deletes a cookie with the provided name, path, and the default domain.
     *
     * @param response  the HTTP servlet response
     * @param name      the name of the cookie to delete
     * @param path      the path of the cookie
     */
    public void deleteCookie(HttpServletResponse response, String name, String path) {
        CookieAttributes cookie = CookieAttributes.builder()
                .name(name)
                .domain(defaultDomain)
                .path(path)
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * Returns the default Secure flag.
     *
     * @return the default Secure flag
     */
    // notice this can be null.
    public Boolean getDefaultSecure() {
        return this.defaultSecure;
    }

    /**
     * Returns the default SameSite attribute value.
     *
     * @return the default SameSite attribute value
     */
    public String getDefaultSameSite() {
        return this.defaultSameSite;
    }

    /**
     * Returns the default Domain attribute value.
     *
     * @return the default Domain attribute value
     */
    public String getDefaultDomain() {
        return this.defaultDomain;
    }
}
