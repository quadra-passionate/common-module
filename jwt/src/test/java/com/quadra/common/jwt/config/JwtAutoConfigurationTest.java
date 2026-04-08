package com.quadra.common.jwt.config;

import com.quadra.common.jwt.JwtUtils;
import com.quadra.common.jwt.autoconfigure.JwtAutoConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for JwtAutoConfiguration")
public class JwtAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withPropertyValues(
                    "jwt.access.secret=aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                    "jwt.access.expires=3600000",
                    "jwt.refresh.secret=bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
                    "jwt.refresh.expires=180000000"
            )
            .withConfiguration(AutoConfigurations.of(JwtAutoConfiguration.class));

    @Test
    @DisplayName("should register JwtUtils and JwtProperties when no other bean exists")
    void shouldRegisterJwtUtilsBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(JwtProperties.class);
            assertThat(context).hasSingleBean(JwtUtils.class);
        });
    }

    @Test
    @DisplayName("should not register JwtUtils if already defined")
    void shouldNotRegisterJwtUtilsIfAlreadyDefined() {
        JwtUtils jwtUtils = new JwtUtils("cccccccccccccccccccccccccccccccc", "dddddddddddddddddddddddddddddddd", 3600000, 180000000);

        contextRunner
                .withBean(JwtUtils.class, () -> jwtUtils)
                .run(context -> {
                    assertThat(context).hasSingleBean(JwtUtils.class);
                    assertThat(context.getBean(JwtUtils.class)).isSameAs(jwtUtils);
                });
    }
}
