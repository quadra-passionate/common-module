package com.quadra.common.contract.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.common.contract.handler.GlobalControllerAdvice;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for the {@code GlobalControllerAdvice}.
 *
 * <p>
 * This configuration registers a {@code GlobalControllerAdvice} bean when no other
 * {@code GlobalControllerAdvice} bean is already defined in the application context.
 *
 * @author <a href="https://github.com/taeyong98">Taeyong Jang</a>
 * @author <a href="https://github.com/rymph">Wooseong Urm</a>
 * @see GlobalControllerAdvice
 * @since 1.0.0
 */
@AutoConfiguration(after = JacksonAutoConfiguration.class)
@ConditionalOnBean(ObjectMapper.class)
public class ContractAutoConfiguration {

    /**
     * Creates a {@link GlobalControllerAdvice} bean if no bean is present.
     *
     * @param objectMapper the Jackson object mapper
     * @return a {@link GlobalControllerAdvice} instance
     */
    @Bean
    @ConditionalOnMissingBean(GlobalControllerAdvice.class)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public GlobalControllerAdvice globalControllerAdvice(ObjectMapper objectMapper) {
        return new GlobalControllerAdvice(objectMapper);
    }
}
