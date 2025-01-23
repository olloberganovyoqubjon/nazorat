package com.example.demo.entity;

import com.example.demo.entity.enums.Huquq;
import com.example.demo.entity.template.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true) // Lombok: генерирует методы equals и hashCode на основе всех полей, включая унаследованные.
@AllArgsConstructor // Lombok: генерирует конструктор с аргументами для всех полей класса.
@NoArgsConstructor // Lombok: генерирует конструктор без параметров.
@Getter // Lombok: генерирует методы getter для всех полей класса.
@Setter // Lombok: генерирует методы setter для всех полей класса.
@Entity // Указывает, что класс является сущностью JPA, которая будет сохраняться в базе данных.
public class Role extends AbstractEntity {

    @Column(nullable = false) // Определение свойств колонки в базе данных: не может быть null.
    private String name; // Название роли (например, ADMIN, USER и т.д.).

    @Enumerated(EnumType.STRING) // Говорит JPA, что поле huquqList должно быть представлено в базе данных как строка, соответствующая перечислению.
    @ElementCollection(fetch = FetchType.LAZY) // Определение коллекции элементов (в данном случае, Enum-значений) с отложенной загрузкой.
    private List<Huquq> huquqList; // Список прав (Huquq), связанных с данной ролью.

    @Column(columnDefinition = "text") // Определение свойств колонки в базе данных: тип текстового поля.
    private String description; // Описание роли и её функций в системе.

}
