package com.example.demo.utils;

// Определение пакета, к которому принадлежит интерфейс

public interface AppConstants {
    /**
     * Класс AppConstants представляет собой интерфейс, который содержит константы для ролей пользователей.
     * Он используется для того, чтобы избежать повторного написания строковых значений ролей SUPERADMIN, ADMIN и USER.
     */
    // Определение публичного интерфейса AppConstants

    /**
     * ADMIN и USER используются в качестве строковых констант, чтобы не писать их каждый раз вручную
     */

     String ADMIN = "ADMIN";
    // Константа для роли ADMIN

    String RAIS = "RAIS";
    // Константа для роли RAIS

    String HELPER = "HELPER";
    // Константа для роли HELPER

    String USER = "USER";
    // Константа для роли USER

}
