package com.example.demo.security.audit;


import com.example.demo.entity.Users;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class KimYozganiniAuditQilish implements AuditorAware<Long> {


    /**
     * Audit qilishuchun zaruriy sozlamalar Long bu userni id si qaysi tipda ekanligi
     * agar UUID bo'lsa UUID ishlatiladi
     *
     * AuditorAware implements qilish kerak
     *
     * Возвращает идентификатор текущего пользователя для использования в аудите.
     *
     *
     * Реализация интерфейса AuditorAware: Класс KimYozganiniAuditQilish реализует интерфейс AuditorAware, который
     * позволяет Spring Data JPA получать текущего пользователя для аудита.
     * getCurrentAuditor(): Метод, предоставляющий текущего пользователя для аудита.
     * Authentication authentication = SecurityContextHolder.getContext().getAuthentication();: Получает текущую
     * аутентификацию пользователя из контекста безопасности.
     * Проверяет, что пользователь аутентифицирован и не является анонимным (!authentication.getPrincipal().equals("anonymousUser")).
     * Если условия выполняются, возвращает Optional с идентификатором пользователя (((UsersRedis) authentication.getPrincipal()).getId()).
     * Если пользователь не аутентифицирован или анонимный, возвращает пустой Optional (Optional.empty()).
     *
     *
     * @return Optional с идентификатором текущего пользователя типа Long, если пользователь аутентифицирован,
     *         и Optional.empty(), если пользователь не аутентифицирован или является анонимным.
     */


    @Override
    public Optional<Long> getCurrentAuditor() {

        // Sistemaga kirgan userni olish uchun ishlatiladi

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Userni idsi qaytadi

        if(        authentication != null
                && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")){
            return Optional.of(((Users) authentication.getPrincipal()).getId());
        }

        return Optional.empty();
    }
}
