package com.university.facility.config;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:https://university-identity-service.onrender.com/.well-known/jwks.json}")
    private String jwkSetUri;

    @Value("${facility-resource-service.jwt.required-issuer:university-identity-service}")
    private String requiredIssuer;

    @Value("${facility-resource-service.jwt.roles-claim:roles}")
    private String rolesClaim;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/h2-console/**",
                    "/actuator/**",
                    "/api/resources/*/validate",
                    "/api/resources/*/validate/**",
                    "/api/resources/code/*/validate",
                    "/api/resources/check-availability"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtDecoder jwtDecoder(Environment environment) {
        if (StringUtils.hasText(jwkSetUri) && !environment.matchesProfiles("dev", "test")) {
            NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
            OAuth2TokenValidator<Jwt> withIssuer = new JwtIssuerValidator(requiredIssuer);
            OAuth2TokenValidator<Jwt> withTimestamp = new JwtTimestampValidator();
            OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(withTimestamp, withIssuer);
            jwtDecoder.setJwtValidator(validator);
            return jwtDecoder;
        }

        // Fallback for local development / testing
        return token -> {
            Instant now = Instant.now();
            String sub = (StringUtils.hasText(token) && !token.equalsIgnoreCase("bearer")) ? token : "dev-user";
            Map<String, Object> headers = Map.of("alg", "none");
            Map<String, Object> claims = Map.of(
                    "sub", sub,
                    rolesClaim, List.of("STUDENT", "RESOURCE_MANAGER", "ADMIN"),
                    "iss", requiredIssuer,
                    "iat", now,
                    "exp", now.plusSeconds(3600));
            return new Jwt(token, now, now.plusSeconds(3600), headers, claims);
        };
    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<String> roles = jwt.getClaimAsStringList(rolesClaim);
            if (roles == null) {
                return List.of();
            }
            return roles.stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });
        return converter;
    }
}
