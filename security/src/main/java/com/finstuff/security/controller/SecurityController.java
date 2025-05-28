package com.finstuff.security.controller;

import com.finstuff.security.dto.UsersDTO;
import com.finstuff.security.entity.Users;
import com.finstuff.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/security")
public class SecurityController {

    private final UserService service;

    @PostMapping("/register")
    public Users register (@RequestBody UsersDTO dto){
        return service.register(dto.username(), dto.password(), dto.name());
    }

    @PostMapping("/login")
    public String login (@RequestBody UsersDTO dto){
        return service.verify(dto);
    }
}
