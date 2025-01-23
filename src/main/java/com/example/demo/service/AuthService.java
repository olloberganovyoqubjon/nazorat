package com.example.demo.service;

import com.example.demo.entity.Users;
import com.example.demo.payload.ApiResult;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.tokenGenerator.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthService implements UserDetailsService {

    private final UserRepository repository;

    private final JwtProvider jwtProvider;

    @Autowired
    public AuthService(UserRepository repository, JwtProvider jwtProvider) {
        this.repository = repository;
        this.jwtProvider = jwtProvider;
    }

    public ApiResult updateAccessToken(String token) {
        try {
            boolean tokenExpired = jwtProvider.isTokenExpired(token);
            if (tokenExpired) {
                return new ApiResult("Token expired or invalid!", false);
            }
            String usernameFromToken = jwtProvider.getUsernameFromToken(token);
            Optional<Users> usersOptional = repository.findByUsername(usernameFromToken);
            if (usersOptional.isPresent()) {
                Users user = usersOptional.get();
                String newToken = JwtProvider.generatorToken(user.getUsername(), user.getRole());

                return new ApiResult("Token", true, newToken, user.getRole());
            } else {
                return new ApiResult("User not find!", false);
            }
        } catch (Exception e) {
            return new ApiResult("Token expired or invalid!", false);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /**
         * Usernameni orqali topish
         */

        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " topilmadi!"));
    }

//    public Users findUserByUserName(String username) {
//
//        return repository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException(username + " topilmadi!"));
//    }



}
