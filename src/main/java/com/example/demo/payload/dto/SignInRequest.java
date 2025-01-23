package com.example.demo.payload.dto;

//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.Size;

@Data
@Schema(description = "Запрос на аутентификацию")
public class SignInRequest {

    @Schema(description = "Имя пользователя", example = "977392144")
    @Size(min = 5, max = 50, message = "Имя пользователя должно содержать от 5 до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String username;

    @Schema(description = "Пароль", example = "123")
    @Size(min = 2, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
    @NotBlank(message = "Пароль не может быть пустыми")
    private String password;

//    @Enumerated(value = EnumType.STRING)
//    private String role;
}