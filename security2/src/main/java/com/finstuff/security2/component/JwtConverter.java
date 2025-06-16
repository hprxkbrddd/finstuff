package com.finstuff.security2.component;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt jwtSource) {
        var authorities = (List<String>) jwtSource.getClaim("authorities");
        var grantedAuthorities = new JwtGrantedAuthoritiesConverter().convert(jwtSource);
        if (authorities!=null){
            grantedAuthorities.addAll(processUserAuthorities(authorities));
        }
        return new JwtAuthenticationToken(jwtSource, grantedAuthorities, jwtSource.getSubject());
    }

    private Collection<GrantedAuthority> processUserAuthorities(List<String> authorities){
        return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
