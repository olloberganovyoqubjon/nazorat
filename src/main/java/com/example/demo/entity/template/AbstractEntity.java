package com.example.demo.entity.template;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;

@MappedSuperclass
// Определяет, что этот класс является mapped superclass, т.е. он не является отдельной сущностью, но может содержать поля для наследования другими сущностями.
@Getter // Lombok: генерирует методы getter для всех полей класса.
@Setter // Lombok: генерирует методы setter для всех полей класса.
@EntityListeners(AuditingEntityListener.class) // Указывает на слушатель событий для этой сущности, который будет отслеживать события жизненного цикла.
public abstract class AbstractEntity {

    /**
     * Этот код представляет абстрактный класс AbstractEntity, который используется в качестве базового класса для всех
     * сущностей базы данных в приложении. Вот комментарии к каждой строке кода:
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Генерация ID с помощью базы данных.
    private Long id;

    @Column(updatable = false, nullable = false)
    @CreationTimestamp // Помечает поле для автоматического установления времени создания записи.
    private Timestamp createdAt;

    @UpdateTimestamp // Помечает поле для автоматического обновления времени при изменении записи.
    private Timestamp updatedAt;

    @CreatedBy // Помечает поле для хранения идентификатора пользователя, создавшего запись.
    @JoinColumn(updatable = false) // Связывает это поле с внешним ключом в базе данных.
    private Long createdBy;

    @LastModifiedBy // Помечает поле для хранения идентификатора пользователя, последний раз изменившего запись.
    private Long updatedBy;
}
