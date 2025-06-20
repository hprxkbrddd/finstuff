package com.finstuff.security2.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest){
        OidcUser user = super.loadUser(userRequest);
        List<String> authorities = user.getAttribute("authorities");

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        if (authorities!=null){
            authorities.forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role)));
        }

        return new DefaultOidcUser(grantedAuthorities, userRequest.getIdToken(), user.getUserInfo());
    }
}
