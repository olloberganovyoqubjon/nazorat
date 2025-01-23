package com.example.demo.payload.dto;

//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на регистрацию")
public class SignUpRequest {

    @Schema(description = "Имя пользователя", example = "Jon")
    @Size(min = 5, max = 50, message = "Имя пользователя должно содержать от 5 до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String username;

    @Schema(description = "firstName", example = "firstName")
    @Size(min = 5, max = 255, message = "firstName должен содержать от 5 до 255 символов")
    @NotBlank(message = "firstName не может быть пустыми")
//    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String firstName;

    @Schema(description = "lastName", example = "lastName")
    @Size(min = 5, max = 255, message = "lastName должен содержать от 5 до 255 символов")
    @NotBlank(message = "lastName не может быть пустыми")
//    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String lastName;

    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(max = 255, message = "Длина пароля должна быть не более 255 символов")
    private String password;

    private Integer districtId;
}