package com.example.demo.controller;


//import com.example.demo.annotation.CurrentUser;
//import com.example.demo.annotation.RoleniTekshirish;
//import com.example.demo.entity.Users;

import com.example.demo.entity.Users;
import com.example.demo.payload.ApiResult;
import com.example.demo.payload.AuthMeResponse;
import com.example.demo.payload.UserResponse;
import com.example.demo.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//import javax.validation.Valid;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "AuthController", description = "Autentification uchun")
@CrossOrigin("*")
public class AuthController {

    /**
     * Основное назначение:
     *
     * @RestController: Объявление класса как контроллера REST API.
     * @RequestMapping("/api/auth"): Базовый путь для всех запросов, обрабатываемых этим контроллером.
     * @Tag(name = "AuthController", description = "Autentification uchun"): Аннотация Swagger для группировки операций в документации.
     * @Autowired AuthService service: Внедрение зависимости сервиса AuthService.
     * @PostMapping("/login"): Обработчик POST запроса для выполнения входа в систему.
     * @RequestBody LoginDto dto: Параметр запроса, содержащий данные для входа (имя пользователя и пароль).
     * ApiResult apiResult = service.login(dto);: Вызов метода login сервиса AuthService для выполнения аутентификации.
     * return ResponseEntity.status(apiResult.isSuccess() ? HttpStatus.CREATED : HttpStatus.CONFLICT).body(apiResult);:
     * Возврат HTTP ответа с кодом 201 Created в случае успешного входа и 409 Conflict в противном случае.
     * @GetMapping("/refreshToken"): Обработчик GET запроса для обновления токена доступа.
     * @RequestParam String token: Параметр запроса, содержащий токен, который нужно обновить.
     * ApiResult apiResult = service.updateAccessToken(token);: Вызов метода updateAccessToken сервиса AuthService
     * для обновления токена доступа.
     * return ResponseEntity.status(apiResult.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(apiResult);:
     * Возврат HTTP ответа с кодом 200 OK в случае успешного обновления и 409 Conflict в противном случае.
     * Примечания:
     * Контроллер предоставляет два основных HTTP метода для аутентификации и обновления токена.
     * Используется объект ApiResult для упрощения возврата результатов операций.
     * Используются статусы HTTP (например, HttpStatus.CREATED и HttpStatus.OK) для передачи информации о статусе операции.
     * В случае возникновения ошибки, обрабатываемой сервисом, возвращается HTTP статус 409 Conflict.
     */

    private final AuthService service;

//    private final AuthenticationService authenticationService;

    @Autowired
    public AuthController(AuthService service) {
        this.service = service;
//        this.authenticationService = authenticationService;
    }




    @GetMapping("/refreshToken")
    public HttpEntity<?> refreshToken(@RequestParam String token) {
        // Вызов сервисного метода для обновления токена доступа
        ApiResult apiResult = service.updateAccessToken(token);
        // Возврат HTTP ответа в зависимости от успешности операции
        return ResponseEntity.status(apiResult.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(apiResult);
    }


//    @Operation(summary = "Авторизация пользователя")
//    @PostMapping("/sign-in")
//    public HttpEntity<?> signIn(@RequestBody @Valid SignInRequest request) {
//        AuthenticationResponse authenticate = authenticationService.signIn(request);
//        if (authenticate.getAccessToken() == null)
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(authenticate);
//        return ResponseEntity.status(HttpStatus.OK).body(authenticate);
//
//    }

    @GetMapping("/me")
    public HttpEntity<?> loginMe(HttpServletRequest request, HttpServletResponse responses) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Users userDetails = (Users) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            UserResponse userResponse = new UserResponse(
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getFirstName(),
                    userDetails.getLastName(),
                    userDetails.getLastName(),
                    roles
            );
            AuthMeResponse authMeResponse = new AuthMeResponse(userResponse);
            request.getSession().invalidate();
            SecurityContextHolder.clearContext();
            return ResponseEntity.status(HttpStatus.OK).body(authMeResponse);
        } catch (Exception e) {
            request.getSession().invalidate();
            SecurityContextHolder.clearContext();
            e.printStackTrace();
            return ResponseEntity.status(responses.getStatus()).body(responses);
        }
    }
}

