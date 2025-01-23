package com.example.demo.entity;

import com.example.demo.entity.enums.Huquq;
import com.example.demo.entity.template.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true) // Lombok: генерирует переопределенные методы equals() и hashCode(), включая поля родительского класса.
@AllArgsConstructor // Lombok: генерирует конструктор с аргументами для всех полей класса.
@NoArgsConstructor // Lombok: генерирует конструктор без параметров.
@Getter // Lombok: генерирует методы getter для всех полей класса.
@Setter // Lombok: генерирует методы setter для всех полей класса.
@ToString // Lombok: генерирует метод toString() для вывода информации об объекте в виде строки.
@Entity // Указывает, что класс является сущностью JPA, которая будет сохраняться в базе данных.
public class Users extends AbstractEntity implements UserDetails {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String patronym;

    @JsonIgnore
    @Column(nullable = false, unique = true)
    private String username; // Уникальное имя пользователя для входа в систему.

    @JsonIgnore // Игнорирует это поле при сериализации/десериализации JSON.
    @Column(nullable = false)
    private String password; // Пароль пользователя.

    @ManyToOne(fetch = FetchType.EAGER, optional = false) // Отношение "многие к одному": пользователь имеет одну роль.
    private Role role; // Роль пользователя.

    private boolean delete = false; // Флаг указывает, удален ли пользователь.

    private boolean accountNonExpired = true; // Флаг указывает, истек ли срок действия аккаунта.

    private boolean accountNonLocked = true; // Флаг указывает, заблокирован ли аккаунт.

    private boolean credentialsNonExpired = true; // Флаг указывает, истек ли срок действия учетных данных.

    private boolean enabled = true; // Флаг указывает, активен ли аккаунт.

    @Column(name = "is_active")
    private Boolean active = false;

    @Column(name = "date_joined")
    private Timestamp createTime = new Timestamp(System.currentTimeMillis());

    @Column(nullable = true)
    private boolean online = false; // Флаг указывает, online ли пользователь.

    private Timestamp entryTime; // Время входа в систему.

    @ManyToOne
    private Users fatherUsers;

    private String firstReport;

    private String secondReport;

    private String chiefName;

    private Integer stage;

    private Integer sortUser;

    private Boolean boldUser = false;
    private String management;                                                  //boshqarmasi


    // Конструктор для инициализации объекта пользователя.
    public Users(String surName, String name, String lastName, String username, String password, String fingerprint, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Users(String firstName, String lastName, String username, String password, Role role, Boolean active, Timestamp createTime) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.active = active;
        this.createTime = createTime;
    }


    public Users(String username, String password, Boolean active, Timestamp createTime) {
        this.username = username;
        this.password = password;
        this.active = active;
        this.createTime = createTime;
    }

    // Метод из интерфейса UserDetails, возвращает коллекцию прав доступа пользователя.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<Huquq> huquqList = this.role.getHuquqList(); // Получаем список прав доступа из роли пользователя.
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(); // Создаем список объектов GrantedAuthority.

        // Преобразуем каждое право доступа в объект SimpleGrantedAuthority и добавляем в список grantedAuthorities.
        for (Huquq huquq : huquqList) {
            grantedAuthorities.add(new SimpleGrantedAuthority(huquq.name()));
        }
        return grantedAuthorities; // Возвращаем список прав доступа.
    }
}
