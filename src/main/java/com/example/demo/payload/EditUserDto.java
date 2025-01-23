package com.example.demo.payload;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//import javax.validation.constraints.NotNull;

// Аннотация Lombok для автоматического создания конструктора с аргументами для всех полей
@AllArgsConstructor
// Аннотация Lombok для автоматического создания конструктора без аргументов
@NoArgsConstructor
// Аннотации Lombok для автоматического создания методов getter и setter для всех полей
@Getter
@Setter
public class EditUserDto {

    /**
     * Класс EditUserDto представляет собой Data Transfer Object (DTO), который используется для передачи данных при
     * редактировании пользователя. Он включает в себя следующие поля:
     *
     * username (имя пользователя)
     * passwordNow (текущий пароль)
     * password (новый пароль)
     * prePassword (повтор нового пароля)
     * Класс использует аннотации Lombok для автоматического создания методов getter, setter, а также конструктора без
     * аргументов и конструктора со всеми аргументами. Аннотации @NotNull от Bean Validation используются для указания обязательных полей.
     */

    // Поле для хранения имени пользователя, должно быть не null
    @NotNull(message = "Username bo'sh bo'lmasin!")
    private String username;

    // Поле для хранения текущего пароля пользователя, должно быть не null
    @NotNull(message = "PasswordNow bo'sh bo'lmasin!")
    private String passwordNow;

    // Поле для хранения нового пароля пользователя, должно быть не null
    @NotNull(message = "Password bo'sh bo'lmasin!")
    private String password;

    // Поле для хранения подтверждения нового пароля пользователя, должно быть не null
    @NotNull(message = "PrePassword bo'sh bo'lmasin!")
    private String prePassword;
}
