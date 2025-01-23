package com.example.demo.security.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class AuditniSozlash {

    /**
     * Audit qilishuchun zaruriy sozlamalar Long bu userni id si qaysi tipda ekanligi
     * agar UUID bo'lsa UUID ishlatiladi
     * @return
     */

    @Bean
    public AuditorAware<Long> auditProvider(){
        return new KimYozganiniAuditQilish();
    }

}
