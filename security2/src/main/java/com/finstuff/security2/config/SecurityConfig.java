package com.finstuff.security2.config;

import com.finstuff.security2.component.JwtConverter;
import com.finstuff.security2.service.CustomOidcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtConverter converter;
    @Autowired
    private CustomOidcUserService customOidcUserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Отключить CSRF для API
                .authorizeHttpRequests(request ->{request
                        .requestMatchers("/finstuff/v1/keycloak/token").permitAll()
                        .anyRequest().authenticated();
                })
                .oauth2Login(oauth2 ->
                    oauth2.userInfoEndpoint(userInfo ->
                            userInfo.oidcUserService(customOidcUserService))
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwt -> {
                            jwt.jwtAuthenticationConverter(converter);
                        }
                ))
                .build();
    }
}
