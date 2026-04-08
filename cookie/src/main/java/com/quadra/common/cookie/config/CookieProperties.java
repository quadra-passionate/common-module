package com.quadra.common.cookie.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Holds cookie-related configuration properties.
 *
 * <p>
 * Maps settings with the {@code cookie} prefix in {@code application.yml} or {@code application.properties}.
 * Manages the secure flag and same-site policy of cookies.
 *
 * @author <a href="https://github.com/rymph">Wooseong Urm</a>
 * @since 1.0.0
 */
@Slf4j
@ConfigurationProperties(prefix = "cookie")
public class CookieProperties {

    private final String domain;
    private final Boolean secure;
    private final String sameSite;

    /**
     * Constructs a new {@code CookieProperties} instance with the provided secure and sameSite values.
     *
     * @param secure   the Secure flag
     * @param sameSite the SameSite attribute value
     */
    public CookieProperties(String domain, Boolean secure, String sameSite) {
        this.domain = domain;
        this.secure = secure;
        this.sameSite = sameSite;
        log.debug("CookieProperties initialized with (domain: {}, secure: {}, sameSite: {})", domain, secure, sameSite);
    }

    public String getDomain() {
        return this.domain;
    }

    /**
     * Returns the Secure flag.
     *
     * @return the Secure flag
     */
    // notice that this can be null.
    public Boolean getSecure() {
        return this.secure;
    }

    /**
     * Returns the SameSite attribute value.
     *
     * @return the SameSite attribute value
     */
    public String getSameSite() {
        return this.sameSite;
    }

}
