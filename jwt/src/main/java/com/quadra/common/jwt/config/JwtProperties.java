package com.quadra.common.jwt.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Holds JWT-related configuration properties.
 *
 * <p>
 * Maps settings with the {@code jwt} prefix in {@code application.yml} or {@code application.properties}.
 * Manages secret key strings and expiration times for both access and refresh token.
 *
 * <p>
 * Example YAML:
 * <pre>
 * jwt:
 *   access:
 *     secret: access-secret-must-be-at-least-32-characters-long
 *     expires: 3600000 # milliseconds
 *   refresh:
 *     secret: refresh-secret-must-be-at-least-32-characters-long
 *     expires: 86400000 # milliseconds
 * </pre>
 *
 * @author <a href="https://github.com/rymph">Wooseong Urm</a>
 * @since 1.0.0
 */
@Slf4j
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private final KeyProperties access;
    private final KeyProperties refresh;

    /**
     * Constructs a new {@code JwtProperties} instance with the provided access and refresh token properties.
     *
     * @param access    the access token properties
     * @param refresh   the refresh token properties
     */
    public JwtProperties(KeyProperties access, KeyProperties refresh) {
        this.access = access;
        this.refresh = refresh;
        log.debug("JwtProperties initialized with token properties below");
        log.debug("Access Properties (secret: {}, expires: {}", access.getSecret(), access.getExpires());
        log.debug("Refresh Properties (secret: {}, expires: {})", refresh.getSecret(), refresh.getExpires());
    }

    /**
     * Returns the access token properties.
     *
     * @return the access token properties
     */
    public KeyProperties getAccess() {
        return this.access;
    }

    /**
     * Returns the refresh token properties.
     *
     * @return the refresh token properties
     */
    public KeyProperties getRefresh() {
        return this.refresh;
    }

    /**
     * Represents Key-related properties for a JWT token.
     *
     * <p>
     * Holds the secret key string and expiration time for a token.
     * The expiration time is expressed in milliseconds.
     *
     * <p>
     * This class is used as a nested property within {@code JwtProperties}
     *
     * @author <a href="https://github.com/rymph">Wooseong Urm</a>
     * @since 1.0.0
     *
     */
    public static class KeyProperties {

        private final String secret;
        private final long expires; // milliseconds

        /**
         * Constructs a new {@code KeyProperties} instance with the provided secret key string and expiration time.
         *
         * @param secret    the secret key string
         * @param expires   the expiration time in milliseconds
         */
        public KeyProperties(String secret, long expires) {
            this.secret = secret;
            this.expires = expires;
        }

        /**
         * Returns the secret key string.
         *
         * @return the secret key string
         */
        public String getSecret() {
            return this.secret;
        }

        /**
         * Returns the expiration time in milliseconds.
         *
         * @return the expiration time in milliseconds.
         */
        public long getExpires() {
            return this.expires;
        }

    }
}
