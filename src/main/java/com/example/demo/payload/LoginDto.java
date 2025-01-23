package com.example.demo.payload;

import jakarta.validation.constraints.NotNull;
import lombok.*;

//import javax.validation.constraints.NotNull;


// Аннотация Lombok для автоматического создания конструктора с аргументами для всех полей
@AllArgsConstructor
// Аннотация Lombok для автоматического создания конструктора без аргументов
@NoArgsConstructor
// Аннотации Lombok для автоматического создания методов getter и setter для всех полей
@Getter
@Setter
// Аннотация Lombok для автоматического создания метода toString
@ToString
public class LoginDto {

    /**
     *
     * Класс LoginDto представляет собой Data Transfer Object (DTO), который используется для передачи данных при входе
     * пользователя в систему. Он включает в себя следующие поля:
     *
     * username (имя пользователя)
     * password (пароль)
     * fingerprint (уникальный отпечаток для аутентификации)
     * socketClient (UUID сокет-клиента)
     * ipAddress (IP-адрес)
     * macAddress (MAC-адрес)
     * Класс использует аннотации Lombok для автоматического создания методов getter, setter, конструктора без аргументов,
     * конструктора со всеми аргументами и метода toString. Аннотации @NotNull от Bean Validation используются для указания обязательных полей.
     *
     */

    // Поле для хранения имени пользователя, должно быть не null
    @NotNull(message = "Username bo'sh bo'lmasin!")
    private String username;

    // Поле для хранения пароля пользователя, должно быть не null
    @NotNull(message = "Password bo'sh bo'lmasin!")
    private String password;


}
