package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice // Объявляет класс как глобальный обработчик исключений для контроллеров.
public class ControllerAdvice {

    @ExceptionHandler(ForbiddenException.class) // Аннотация обработчика исключений для типа ForbiddenException.
    public ResponseEntity<?> handleException(ForbiddenException forbiddenException) {
        // Метод для обработки исключения типа ForbiddenException.
        // Создается ResponseEntity с сообщением об ошибке и HTTP статусом 403 Forbidden.
        return new ResponseEntity<>(forbiddenException.getMessage() + " " + forbiddenException.getType(), HttpStatus.FORBIDDEN);
    }
}
