package com.github.simaodiazz.schola.backbone.server.security.configuration;

import com.github.simaodiazz.schola.backbone.server.security.service.UserChargeService;
import com.github.simaodiazz.schola.backbone.server.security.service.UserPasswrChangeService;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableRedisHttpSession
public class SecurityConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);
    private final @NotNull UserChargeService userChargeService;
    private final @NotNull UserPasswrChangeService userPasswrChangeService;
    private final @NotNull PasswordEncoder passwordEncoder;

    public SecurityConfiguration(@NotNull UserChargeService userChargeService, @NotNull UserPasswrChangeService userPasswrChangeService, @NotNull PasswordEncoder passwordEncoder) {
        this.userChargeService = userChargeService;
        this.userPasswrChangeService = userPasswrChangeService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilter(final @NotNull HttpSecurity security) throws Exception {
        final AuthenticationProvider authenticationProvider = authenticationProvider();
        return security
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(this.corsConfigurationSource()))
                .authorizeHttpRequests((api) -> api.requestMatchers("api/**").permitAll())
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(logout -> logout.deleteCookies("SESSION"))
                .authenticationProvider(authenticationProvider)
                .build();
    }

    @Contract(" -> new")
    @Bean
    public @NotNull DaoAuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userChargeService);
        provider.setUserDetailsPasswordService(userPasswrChangeService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
