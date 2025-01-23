package com.example.demo.annotation;

import com.example.demo.entity.Users;
import com.example.demo.exceptions.ForbiddenException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class CheckRoleExecutor {

    /**
     * Аннотации:
     *
     * @Component указывает, что этот класс является компонентом Spring и будет управляться контейнером Spring.
     * @Aspect указывает, что этот класс является аспектом, используемым в аспектно-ориентированном программировании (AOP).
     * @Before указывает, что метод roleniTekshirish должен выполняться перед методами, аннотированными @RoleniTekshirish.
     * Метод roleniTekshirish:
     *
     * Принимает параметр RoleniTekshirish, который является аннотацией, указывающей на требуемую роль.
     * Получает текущего аутентифицированного пользователя из контекста безопасности через SecurityContextHolder.
     * Проверяет, если у пользователя нет требуемой роли, выбрасывает исключение ForbiddenException с соответствующим сообщением.
     */

    /**
     * Annatatsiya qachon ishlashini belgilaydi
     *
     * foydalanuvchi rolini tekshirish uchun annatation
     */

    @Before(value = "@annotation(roleniTekshirish)")
    public void roleniTekshirish(RoleniTekshirish roleniTekshirish) {
        // Получение текущего аутентифицированного пользователя
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Проверка, если у пользователя нет требуемой роли, выбросить исключение ForbiddenException
        if (!roleniTekshirish.role().contains(user.getRole().getName())) {
            throw new ForbiddenException(roleniTekshirish.role(), "Ruxsat yo'q!");
        }
    }
}