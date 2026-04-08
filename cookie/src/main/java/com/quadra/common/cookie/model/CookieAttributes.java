package com.quadra.common.cookie.model;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * Represents the attributes of an HTTP cookie.
 *
 * <p>
 * This class provides a structured way to define cookie properties such as
 * name, value, domain, path, expiration, security flags, and same-site policy.
 * In addition, it ensures validation of the {@code sameSite}.
 *
 * <hr>
 *
 * <p>
 * Typical usage:
 * <pre>{@code
 * CookieAttributes cookie = CookieAttributes.builder()
 *     .name("SESSIONID")
 *     .value("abc123")
 *     .domain("example.com")
 *     .path("/")
 *     .httpOnly(true)
 *     .secure(true)
 *     .sameSite("Strict")
 *     .maxAge(3600)
 *     .build();
 *
 * System.out.println(cookie.toString());
 * }
 * </pre>
 *
 * @author <a href="https://github.com/rymph">Wooseong Urm</a>
 * @since 1.0.0
 */
@Slf4j
public class CookieAttributes {
    private final String name;
    private final String value;
    private final String domain;
    private final String path;
    private final int maxAge;
    private final boolean secure;
    private final boolean httpOnly;
    private final String sameSite;
    private static final Set<String> VALID_SAME_SITES = Set.of("strict", "lax", "none");

    /**
     * Constructs a new {@code CookieAttributes} instance with the provided properties.
     *
     * @param name      the name of the cookie; must not be {@code null}
     * @param value     the value of the cookie; defaults to an empty string if {@code null}
     * @param maxAge    the maximum age in seconds; defaults to {@code -1} (session cookie) if {@code null}
     * @param domain    the domain of the cookie; if null, it delegates to the servlet
     * @param path      the path of the cookie; if null, it delegates to the servlet
     * @param httpOnly  whether the cookie is HTTP-only; defaults to {@code false}
     * @param secure    whether the cookie is secure; defaults to {@code false}
     * @param sameSite  the same-site policy; must be {@code Strict}, {@code Lax}, or {@code None} (case-insensitive), or {@code null}
     * @throws IllegalArgumentException if {@code name} is null or {@code sameSite} is invalid
     */
    @Builder
    public CookieAttributes(String name,
                            String value,
                            Integer maxAge,
                            String domain,
                            String path,
                            Boolean httpOnly,
                            Boolean secure,
                            String sameSite) {

        Assert.notNull(name, "Name must not be null");
        Assert.isTrue(validateSameSite(sameSite), "SameSite must be one of Strict, Lax, or None, case-insensitively. Found: " + sameSite);

        this.name = name;
        this.value = value != null ? value : "";
        this.maxAge = maxAge != null ? maxAge : -1; // default: -1, session cookie.
        this.domain = domain; // if null, it delegates to a servlet.
        this.path = path; // if null, it delegates to a servlet.
        this.httpOnly = httpOnly != null ? httpOnly : false; // default: false, can be accessed through JavaScript.
        this.secure = secure != null ? secure : false; // default: false, can be sent through HTTP.
        this.sameSite = capitalizeSameSite(sameSite); // if null, it delegates to a browser. in most cases, Lax.

        log.debug("CookieAttributes created with (name: {}, maxAge: {}, domain: {}, path: {}, httpOnly: {}, secure: {}, sameSite: {})",
                this.name,
                this.maxAge,
                this.domain,
                this.path,
                this.httpOnly,
                this.secure,
                this.sameSite
        );
    }

    /**
     * Capitalizes the {@code sameSite} attribute value.
     *
     * <p>
     * Ensures the first character is uppercase and the rest are lowercase.
     * If {@code sameSite} is {@code null}, returns {@code null}.
     *
     * @param sameSite the same-site policy
     * @return the capitalized sameSite value, or {@code null} if input is {@code null}
     */
    // This method must be used after the validation.
    private String capitalizeSameSite(String sameSite) {

        String capitalizedSameSite = sameSite == null ? null : sameSite.substring(0, 1).toUpperCase() + sameSite.substring(1).toLowerCase();
        log.trace("Capitalized sameSie string: {} -> {}", sameSite, capitalizedSameSite);

        return capitalizedSameSite;
    }

    /**
     * Validates the {@code sameSite} attribute.
     *
     * <p>
     * For cookies, the {@code sameSite} attribute value must be {@code Lax}, {@code Strict}, or {@code None}.
     * This method validates that the provided {@code sameSite} can be capitalized or delegated to a browser.
     *
     * @param sameSite the SameSite attribute value to validate
     * @return {@code true} if valid, {@code false} otherwise
     */
    private boolean validateSameSite(String sameSite) {

        return sameSite == null || VALID_SAME_SITES.contains(sameSite.toLowerCase());
    }

    /**
     * Returns a string representation of the cookie in the standard HTTP format.
     *
     * @return the formatted cookie string
     */
    @Override
    public String toString() {
        StringBuilder cookieString = new StringBuilder();
        cookieString.append(this.name).append("=").append(this.value);
        if (this.domain != null) cookieString.append("; Domain=").append(this.domain);
        if (this.path != null) cookieString.append("; Path=").append(this.path);
        if (this.maxAge >= 0) cookieString.append("; Max-Age=").append(this.maxAge);
        if (this.secure) cookieString.append("; Secure");
        if (this.httpOnly) cookieString.append("; HttpOnly");
        if (this.sameSite != null) cookieString.append("; SameSite=").append(this.sameSite);

        String formattedCookie = cookieString.toString();
        log.trace("Formatted cookie string: {}", formattedCookie);

        return formattedCookie;
    }
}
