package com.example.demo.service;

import com.example.demo.entity.Users;
import com.example.demo.payload.dto.AdminDto;
import com.example.demo.payload.dto.AuthenticationResponse;
import com.example.demo.payload.dto.SignInRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.tokenGenerator.JwtProvider;
import com.example.demo.utils.AppConstants;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
//@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {
//    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AuthenticationService(JwtProvider jwtProvider, PasswordEncoder passwordEncoder, @Lazy AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

//    private final RoleRepository roleRepository;

//    /**
//     * Аутентификация пользователя
//     *
//     * @param request данные пользователя
//     * @return токен
//     */
//    public AuthenticationResponse signIn(SignInRequest request) {
//
//        Optional<Users> optionalUser = userRepository.findByUsername(request.getUsername());
//        if (optionalUser.isPresent()) {
//            Users user = optionalUser.get();
//            if (user.getActive()) {
//                return existUserByUsernameAndPass(request, user);
//            } else {
////                AuthenticationResponse authenticationResponse = smsSender(user);
//
//                return null;
//            }
//        } else {
//            Users user = new Users(
//                    request.getUsername()
//                    , passwordEncoder.encode(request.getPassword())
//                    , false
//                    , new Timestamp(System.currentTimeMillis()));
////            AuthenticationResponse authenticationResponse = smsSender(user);
////            if (authenticationResponse.getMessage().equals("sms sanded")) {
////                authenticationResponse.setMessage(user.getSms().getMessageId() + "/" + user.getSms().getRandNumber());
////                userRepository.save(user);
////            }
//            return null;
//        }
//
//
////        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
////                request.getUsername(),
////                request.getPassword()
////        ));
////
////        var user = userService
////                .userDetailsService()
////                .loadUserByUsername(request.getUsername());
////
////        var jwt = jwtService.generateToken(user);
////        return new AuthenticationResponse(jwt);
//    }

//    private AuthenticationResponse existUserByUsernameAndPass(SignInRequest request, Users user) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getUsername(),
//                        request.getPassword()
//                )
//        );
//        String accessToken = jwtProvider.generatorToken(user.getUsername(), user.getRole());
////        String refreshToken = jwtService.generateRefreshToken(user);
//
////        revokeAllTokenByUser(user);
////        saveUserToken(accessToken, refreshToken, user);
//        return new AuthenticationResponse(accessToken, "successfully");
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " topilmadi!"));

    }


    public AuthenticationResponse registerAdmin(AdminDto request) {
        // check if user already exist. if exist than authenticate the user
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return new AuthenticationResponse(null, "User or Admin already exist");
        }

        Users user = new Users();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setRole(AppConstants.ADMIN);
        userRepository.save(user);
        return new AuthenticationResponse(null, "Admin registration was successful");
    }

    public AuthenticationResponse loginAdmin(SignInRequest request) {
        Optional<Users> optionalUser = userRepository.findByUsername(request.getUsername());
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            String accessToken = jwtProvider.generatorToken(user.getUsername(), user.getRole());
            return new AuthenticationResponse(accessToken, "Admin login was successful");
        }
        return new AuthenticationResponse(null, "error auth");
    }

    public Map<Integer, String> numberOfUsers() {
        Map<Integer, String> listStringMap = new HashMap<>();
        List<Users> userList = userRepository.findAllByRole(AppConstants.USER);
        List<Users> adminList = userRepository.findAllByRole(AppConstants.ADMIN);
        List<Users> raisList = userRepository.findAllByRole(AppConstants.RAIS);
        listStringMap.put(userList.size(), "users");
        listStringMap.put(adminList.size(), "admins");
        listStringMap.put(raisList.size(), "rais");
        return listStringMap;
    }

    public List<Users> listOfUsers(String rol) {
        List<Users> listString = new ArrayList<>();
        if (rol.equals("USER"))
            listString = userRepository.findAllByRole(AppConstants.USER);
        else if (rol.equals("ADMIN")) {
            listString = userRepository.findAllByRole(AppConstants.ADMIN);
        }else if (rol.equals("RAIS")) {
            listString = userRepository.findAllByRole(AppConstants.RAIS);
        }
        return listString;
    }
}
