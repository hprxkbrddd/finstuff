package com.finstuff.security2.config;

import com.finstuff.security2.component.JwtConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwkSetUri}")
    private String jwkSetUri;

    @Autowired
    private JwtConverter converter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(request ->{
                            request.anyRequest().authenticated();

                })
                .oauth2Login(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwt -> {
                            jwt.jwkSetUri(jwkSetUri);
                            jwt.jwtAuthenticationConverter(converter);
                        }
                ))
                .build();
    }

//    @Bean
//    public JwtAuthenticationConverter jwtAuthenticationConverter(){
//        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
//        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//        converter.setPrincipalClaimName("preferred_username");
//        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
//            var authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
//            var roles = (List<String>) jwt.getClaimAsMap("realm_access").get("roles");
//            return Stream.concat(authorities.stream(),
//                    roles.stream()
//                            .filter(role -> role.startsWith("ROLE_"))
//                            .map(SimpleGrantedAuthority::new)
//                            .map(GrantedAuthority.class::cast))
//                    .toList();
//        });
//        return converter;
//    }
}
