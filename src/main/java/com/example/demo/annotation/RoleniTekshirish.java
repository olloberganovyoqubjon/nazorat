package com.example.demo.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleniTekshirish {

    /**
     * Указывает наименование роли, которая необходима для выполнения аннотированного метода.
     *
     * Аннотации мета-данных:
     *
     * @Documented: Указывает, что аннотация должна быть задокументирована в javadoc.
     * @Target(ElementType.METHOD): Указывает, что аннотация может применяться только к методам.
     * @Retention(RetentionPolicy.RUNTIME): Указывает, что аннотация сохраняется во время выполнения и может быть доступна через рефлексию.
     * Аннотация @RoleniTekshirish:
     *
     * Содержит единственный элемент role, который возвращает строку. Этот элемент представляет наименование роли, которая требуется для выполнения аннотированного метода.
     * @return
     */


    String role();

}
