package com.example.demo.exceptions;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Указывает Spring обработчику, чтобы он возвращал HTTP статус 404 в случае этого исключения.
@AllArgsConstructor // Генерирует конструктор с аргументами для всех полей класса.
public class ResourceNotFoundException extends RuntimeException {

    private final String resurceName; // lavozim

    private final String resurceField; // name

    private final Object object; // USER,ADMIN ...

}
