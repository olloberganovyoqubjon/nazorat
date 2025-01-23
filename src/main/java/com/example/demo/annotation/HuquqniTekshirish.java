package com.example.demo.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HuquqniTekshirish {

    /**
     * Аннотации мета-данных:
     *
     * @Documented: Указывает, что аннотация должна быть задокументирована в javadoc.
     * @Target(ElementType.METHOD): Указывает, что аннотация может применяться только к методам.
     * @Retention(RetentionPolicy.RUNTIME): Указывает, что аннотация сохраняется во время выполнения и может быть доступна через рефлексию.
     * Аннотация @HuquqniTekshirish:
     *
     * Содержит единственный элемент huquq, который возвращает строку. Этот элемент представляет имя права, которое
     * требуется для выполнения аннотированного метода.
     * @return
     */

    String huquq();

}
