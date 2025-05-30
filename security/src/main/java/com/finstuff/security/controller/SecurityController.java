package com.finstuff.security.controller;

import com.finstuff.security.dto.AuthDTO;
import com.finstuff.security.dto.UsersDTO;
import com.finstuff.security.entity.Users;
import com.finstuff.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/finstuff/security")
public class SecurityController {

    private final UserService service;

    @PostMapping("/register")
    public Users register (@RequestBody UsersDTO dto){
        return service.register(dto.username(), dto.password(), dto.name());
    }

    @GetMapping("/login")
    public String login (@RequestBody AuthDTO dto){
        return service.verify(dto);
    }

    @GetMapping("/getAll")
    public List<Users> getAll(){
        return service.getAll();
    }
}
