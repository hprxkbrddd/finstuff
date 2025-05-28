package com.finstuff.security.service;

import com.finstuff.security.dto.UsersDTO;
import com.finstuff.security.entity.Users;
import com.finstuff.security.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository repo;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public Users register (String username, String password, String name){
        password = new BCryptPasswordEncoder(12).encode(password);
        return repo.save(new Users(username, password, name, List.of("USER")));
    }

    public String verify (UsersDTO users){
        Authentication auth =
                authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                users.username(),
                                users.password()
                        )
                );
        return auth.isAuthenticated() ? jwtService.generateToken(users.username()) : "Failure";
    }
}

