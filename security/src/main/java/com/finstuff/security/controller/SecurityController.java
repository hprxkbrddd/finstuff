package com.finstuff.security.controller;

import com.finstuff.security.dto.AuthDTO;
import com.finstuff.security.dto.UsersDTO;
import com.finstuff.security.entity.Users;
import com.finstuff.security.service.JwtService;
import com.finstuff.security.service.RabbitMQProducer;
import com.finstuff.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("finstuff/v1/security")
public class SecurityController {

    private final UserService service;
    private final RabbitMQProducer producer;

    @PostMapping("/register")
    public Users register (@RequestBody UsersDTO dto){
        return service.register(dto.username(), dto.password(), dto.name());
    }

    @PostMapping("/login")
    public ResponseEntity<String> login (@RequestBody AuthDTO dto){
        producer.sendMsg(service.verify(dto));
        return new ResponseEntity<>(service.verify(dto), HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public List<Users> getAll(){
        producer.sendMsg(service.getAll().toString());
        return service.getAll();
    }
}
