package com.example.demo.payload;

import com.example.demo.entity.enums.Huquq;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

// Аннотация Lombok для автоматического создания конструктора с аргументами для всех полей
@AllArgsConstructor
// Аннотация Lombok для автоматического создания конструктора без аргументов
@NoArgsConstructor
// Аннотации Lombok для автоматического создания методов getter и setter для всех полей
@Getter
@Setter
public class RoleDto {

    // Поле для хранения названия роли, должно быть не пустым и не состоять из пробелов
    @NotBlank  // Запрещает пустые строки (не только null, но и строки, состоящие из пробелов)
    private String name; // Например, ADMIN, USER и другие

    // Поле для хранения списка прав, ассоциированных с ролью, должно быть не пустым
    @NotEmpty  // Запрещает пустые коллекции (должен быть хотя бы один элемент)
    private List<Huquq> huquqList;

    // Поле для хранения описания роли и ее задач
    private String description;  // Описание роли, ее задач и обязанностей
}
