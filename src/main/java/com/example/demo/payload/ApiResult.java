package com.example.demo.payload;

import lombok.Data;

import java.io.File;
import java.util.Arrays;

@Data // Аннотация Lombok, которая генерирует геттеры, сеттеры, методы equals(), hashCode() и toString().
public class ApiResult {

    /**
     * Этот класс представляет собой объект ответа API, который может содержать различные данные о результате операции.
     * Класс включает поля для хранения сообщений, статуса успеха, токенов, различных объектов, байтового массива и файла.
     * Он также предоставляет несколько конструкторов для удобного создания объектов результата с различными комбинациями полей.
     * Аннотация @Data от библиотеки Lombok автоматически генерирует геттеры, сеттеры, методы equals(), hashCode(), и toString() для всех полей.
     */

    private String message = "success"; // Сообщение результата

    private boolean success; // Статус успеха операции

    private String token; // Токен авторизации
    private String refreshToken; // Токен обновления

    private Object object; // Произвольный объект результата
    private Object object2; // Второй произвольный объект результата
    private Object object3; // Третий произвольный объект результата
    private Object user; // Пользовательский объект результата
    private Long userId; // Идентификатор пользователя
    private Object role;

    private byte [] bytes; // Байтовый массив результата

    private File file; // Файл результата

    // Конструктор по умолчанию
    public ApiResult() {}

    public ApiResult(boolean success) {
        this.success = success;
    }

    // Конструкторы с различными комбинациями полей
    public ApiResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public ApiResult(String message, boolean success, Object object) {
        this.message = message;
        this.success = success;
        this.object = object;
    }


    public ApiResult(String message, boolean success, Object object, Object object2) {
        this.message = message;
        this.success = success;
        this.object = object;
        this.object2 = object2;
    }

    public ApiResult(String message, boolean success, String token, String refreshToken, Object role) {
        this.message = message;
        this.success = success;
        this.token = token;
        this.refreshToken = refreshToken;
        this.role = role;
    }

    public ApiResult(String message, boolean success, String token, Object role) {
        this.message = message;
        this.success = success;
        this.token = token;
        this.role = role;
    }

    public ApiResult(String message, boolean success, Object object, Object object2, Object object3) {
        this.message = message;
        this.success = success;
        this.object = object;
        this.object2 = object2;
        this.object3 = object3;
    }

    public ApiResult(String message, boolean success, Object object, Object object2, Long userId) {
        this.message = message;
        this.success = success;
        this.object = object;
        this.object2 = object2;
        this.userId = userId;
    }

    public ApiResult(String message, boolean success, String token, Object object, Object object2, Long userId) {
        this.message = message;
        this.success = success;
        this.token = token;
        this.object = object;
        this.object2 = object2;
        this.userId = userId;
    }

    public ApiResult(String message, boolean success, String token, Object object, Object object2, Long userId, Object user) {
        this.message = message;
        this.success = success;
        this.token = token;
        this.object = object;
        this.object2 = object2;
        this.userId = userId;
        this.user = user;
    }

    public ApiResult(String message, boolean success, String token, Object object, Object object2, Long userId, Object user, String refreshToken) {
        this.message = message;
        this.success = success;
        this.token = token;
        this.object = object;
        this.object2 = object2;
        this.userId = userId;
        this.user = user;
        this.refreshToken = refreshToken;
    }

    public ApiResult(byte [] bytes, boolean success) {
        this.bytes = bytes;
        this.success = success;
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "message='" + message + '\'' +
                ", success=" + success +
                ", token='" + token + '\'' +
                ", object=" + object +
                ", object2=" + object2 +
                ", user=" + user +
                ", userId=" + userId +
                ", bytes=" + Arrays.toString(bytes) +
                ", file=" + file +
                '}';
    }
}
