package com.finstuff.security.service;

import com.finstuff.security.entity.UserDetailsImpl;
import com.finstuff.security.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UsersRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserDetailsImpl(repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with name "+username+" is not found :(")));
    }
}
