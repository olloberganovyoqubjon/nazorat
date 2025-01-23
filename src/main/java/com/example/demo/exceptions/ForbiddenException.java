package com.example.demo.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@EqualsAndHashCode(callSuper = true) // Генерация методов equals и hashCode, учитывая поля из базового класса.
@ResponseStatus(HttpStatus.FORBIDDEN) // Указывает Spring обработчику, чтобы он возвращал HTTP статус 403 в случае этого исключения.
@Data // Аннотация Lombok для генерации геттеров, сеттеров, toString и hashCode/equals методов.
public class ForbiddenException extends RuntimeException {

    private String type; // Тип ошибки, который может быть полезен для отладки или логирования.
    private String message; // Сообщение об ошибке, которое может содержать детали запрета.

    public ForbiddenException(String type, String message){
        this.type = type;
        this.message = message;
    }

}
