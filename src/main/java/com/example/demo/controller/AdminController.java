package com.example.demo.controller;

import com.example.demo.annotation.RoleniTekshirish;
import com.example.demo.entity.Users;
import com.example.demo.payload.dto.AdminDto;
import com.example.demo.payload.dto.AuthenticationResponse;
import com.example.demo.payload.dto.SignInRequest;
import com.example.demo.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "AdminController", description = "Admin uchun")
public class AdminController {

    private AuthenticationService authenticationService;

    @Autowired
    public AdminController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @RoleniTekshirish(role = "ADMIN")
    @Operation(summary = "yangi adminni ro'yxatga olish")
    @PostMapping("/registerAdmin")
    public ResponseEntity<AuthenticationResponse> registerAdmin(@RequestBody AdminDto request) {
        return ResponseEntity.ok(authenticationService.registerAdmin(request));
    }

    @Operation(summary = "administratorni tizimga kirishi")
    @PostMapping("/loginAdmin")
    public ResponseEntity<AuthenticationResponse> loginAdmin(@RequestBody SignInRequest request) {
        return ResponseEntity.ok(authenticationService.loginAdmin(request));
    }

    @RoleniTekshirish(role = "ADMIN")
    @Operation(summary = "foydalanuvchilar sonini qaytaradi")
    @GetMapping("numberOfUsers")
    public HttpEntity<?> numberOfUsers() {
        Map<Integer, String> listStringMap = authenticationService.numberOfUsers();
        return ResponseEntity.ok(listStringMap);
    }

    @RoleniTekshirish(role = "ADMIN")
    @Operation(summary = "foydalanuvchilar ro'yxatini qaytaradi")
    @GetMapping("listOfUsers/{rol}")
    public HttpEntity<?> listOfUsers(@PathVariable String rol) {
        List<Users> listStringMap = authenticationService.listOfUsers(rol);
        return ResponseEntity.ok(listStringMap);
    }
}
