package com.example.demo.annotation;

import com.example.demo.entity.Users;
import com.example.demo.exceptions.ForbiddenException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class CheckPermissionExecutor {

    /**
     Аннотации:

     @Component указывает, что этот класс является компонентом Spring и будет управляться контейнером Spring.
     @Aspect указывает, что этот класс является аспектом, используемым в аспектно-ориентированном программировании (AOP).
     @Before указывает, что метод huquqniTekshirish должен выполняться перед методами, аннотированными @HuquqniTekshirish.
     Метод huquqniTekshirish:

     Принимает параметр HuquqniTekshirish, который является аннотацией, указывающей на требуемое право доступа.
     Получает текущего аутентифицированного пользователя из контекста безопасности через SecurityContextHolder.
     Проверяет, есть ли у пользователя необходимое право доступа, просматривая его полномочия.
     Если у пользователя нет нужного права, выбрасывает исключение ForbiddenException с соответствующим сообщением.
     */

    @Before(value = "@annotation(huquqniTekshirish)")
    public void huquqniTekshirish(HuquqniTekshirish huquqniTekshirish) {
        // Получение текущего аутентифицированного пользователя
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Переменная для отслеживания наличия требуемого права доступа
        boolean exist = false;

        // Проверка, есть ли у пользователя необходимое право доступа
        for (GrantedAuthority authority : user.getAuthorities()) {
            if (authority.getAuthority().equals(huquqniTekshirish.huquq())) {
                exist = true;
                break;
            }
        }

        // Если необходимого права доступа нет, выбросить исключение ForbiddenException
        if (!exist)
            throw new ForbiddenException(huquqniTekshirish.huquq(), "Ruxsat yo'q!");
    }
}