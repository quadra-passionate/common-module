package com.quadra.common.jwt.autoconfigure;

import com.quadra.common.jwt.JwtUtils;
import com.quadra.common.jwt.config.JwtProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for the {@link JwtUtils}.
 *
 * <p>
 * This configuration registers a {@link JwtUtils} bean when no other
 * {@link JwtUtils} bean is already defined in the application context.
 *
 * @author <a href="https://github.com/rymph">Wooseong Urm</a>
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtAutoConfiguration {

    /**
     * Creates a {@link JwtUtils} bean if no bean is present.
     *
     * @param jwtProperties the JWT configuration properties
     * @return a {@link JwtUtils} instance
     * @see JwtUtils
     * @see JwtProperties
     */
    @Bean
    @ConditionalOnMissingBean(JwtUtils.class)
    public JwtUtils jwtUtils(JwtProperties jwtProperties) {
        return new JwtUtils(jwtProperties);
    }
}
