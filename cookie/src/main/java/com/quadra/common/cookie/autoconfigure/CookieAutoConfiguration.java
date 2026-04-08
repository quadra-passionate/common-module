package com.quadra.common.cookie.autoconfigure;

import com.quadra.common.cookie.config.CookieProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for the {@code CookieUtils}.
 *
 * <p>
 * This configuration registers a {@code CookieUtils} bean when no other
 * {@code CookieUtils} bean is already defined in the application context.
 *
 * @author <a href="https://github.com/rymph">Wooseong Urm</a>
 * @see com.quadra.common.cookie.servlet.CookieUtils CookieUtils for MVC
 * @see com.quadra.common.cookie.reactive.CookieUtils CookieUtils for Reactive
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(CookieProperties.class)
public class CookieAutoConfiguration {

    /**
     * Creates a {@link com.quadra.common.cookie.servlet.CookieUtils CookieUtils} bean if no bean is present.
     *
     * @param cookieProperties the cookie configuration properties
     * @return a {@link com.quadra.common.cookie.servlet.CookieUtils CookieUtils} instance
     * @see CookieProperties
     */
    @Bean(name = "cookieUtils")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnMissingBean(name = "cookieUtils")
    public com.quadra.common.cookie.servlet.CookieUtils cookieUtilsServlet(CookieProperties cookieProperties) {
        return new com.quadra.common.cookie.servlet.CookieUtils(cookieProperties);
    }

    /**
     * Creates a {@link com.quadra.common.cookie.reactive.CookieUtils CookieUtils} bean if no bean is present.
     *
     * @param cookieProperties the cookie configuration properties
     * @return a {@link com.quadra.common.cookie.reactive.CookieUtils CookieUtils} instance
     * @see CookieProperties
     */
    @Bean(name = "cookieUtils")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @ConditionalOnMissingBean(name = "cookieUtils")
    public com.quadra.common.cookie.reactive.CookieUtils cookieUtils(CookieProperties cookieProperties) {
        return new com.quadra.common.cookie.reactive.CookieUtils(cookieProperties);
    }
}
