package com.example.demo.annotation;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal
public @interface CurrentUser {

    /**
     * Аннотации мета-данных:
     * @Documented: Указывает, что аннотация должна быть задокументирована в javadoc.
     * @Target({ElementType.FIELD, ElementType.PARAMETER}): Указывает, что аннотация может применяться к полям и параметрам метода.
     * @Retention(RetentionPolicy.RUNTIME): Указывает, что аннотация сохраняется во время выполнения и может быть доступна через рефлексию.
     * @AuthenticationPrincipal: Специальная аннотация Spring Security, которая разрешает прямое получение текущего
     * аутентифицированного пользователя.
     */

    /**
     * sistemaga kirgan foydalanuvchini olish uchun kerak
     */

}
