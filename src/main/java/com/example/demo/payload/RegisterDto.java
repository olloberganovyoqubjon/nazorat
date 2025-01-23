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
public class RegisterDto {

    /**
     * Описание класса RegisterDto
     * Класс RegisterDto представляет собой Data Transfer Object (DTO), который используется для передачи данных при
     * регистрации пользователя. Он включает в себя следующие поля:
     *
     * surName (фамилия)
     * name (имя)
     * lastName (отчество)
     * username (имя пользователя)
     * password (пароль)
     * prePassword (подтверждение пароля)
     * ipAddress (IP-адрес)
     * macAddress (MAC-адрес)
     * divisionId (идентификатор подразделения)
     * active (флаг активности)
     * roleId (идентификатор роли)
     * Класс использует аннотации Lombok для автоматического создания методов getter, setter, конструктора без аргументов
     * и конструктора со всеми аргументами. Аннотации @NotNull от Bean Validation используются для указания обязательных полей.
     *
     */


    // Поле для хранения имени пользователя, должно быть не null
    @NotNull(message = "Ismi bo'sh bo'lmasin!")
    private String firstName;

    @NotNull(message = "Familyasi bo'sh bo'lmasin!")
    private String lastName;

    private String patronym;

      // Поле для хранения имени пользователя (username), должно быть не null
    @NotNull(message = "Username bo'sh bo'lmasin!")
    private String username;

    // Поле для хранения пароля пользователя, должно быть не null
    @NotNull(message = "Password bo'sh bo'lmasin!")
    private String password;

    // Поле для хранения подтверждения пароля пользователя, должно быть не null
    @NotNull(message = "PrePassword bo'sh bo'lmasin!")
    private String prePassword;

    // Поле для указания активности пользователя, должно быть не null
    @NotNull(message = "Active bo'sh bo'lmasin!")
    private boolean active;

    // Поле для хранения идентификатора роли пользователя
    @NotNull(message = "Roli bo'sh bo'lmasligi kerak!")
    private Long roleId;

    @NotNull(message = "Boshliq id bo'sh bo'lmasligi kerak!")
    private Long fatherId;

    @NotNull(message = "To'g'ridan-to'gri boshlig'ingiz id bo'sh bo'lmasligi kerak!")
    private String chiefName;

    private String management;                                                  //boshqarmasi

    private String titleFirst = "O'ZBEKISTON RESPUBLIKASI";              //blanka ning birinchi qatori
    private String titleSecond = "DAVLAT XAVFSIZLIK XIZMATI RAISI";              //blanka ning ikkinchi qatori

}
