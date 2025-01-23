package com.example.demo.security; // Пакет, в котором находится данный класс

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {

    /**
     * Метод для проверки надежности пароля
     */

    // Регулярное выражение для проверки наличия цифр, строчных и прописных букв, знаков пунктуации и специальных символов
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";

    // Компиляция регулярного выражения в объект Pattern
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    /**
     * Метод для проверки, соответствует ли пароль требованиям
     *
     * @param password Пароль для проверки
     * @return true, если пароль соответствует требованиям, иначе false
     */
    public static boolean isValid(final String password) {
        Matcher matcher = pattern.matcher(password); // Создание объекта Matcher для сопоставления пароля с шаблоном
        return !matcher.matches(); // Возврат true, если пароль соответствует шаблону, иначе false
    }
}
