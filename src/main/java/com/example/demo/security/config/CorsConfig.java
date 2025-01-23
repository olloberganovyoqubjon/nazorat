// Импортируем необходимые классы и интерфейсы
package com.example.demo.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

// Объявляем класс CorsConfig и аннотируем его как @Configuration
@Configuration
public class CorsConfig {

    /**
     *
     * Этот класс конфигурирует политику CORS с помощью CorsFilter и разрешает доступ с любого IP-адреса, любых методов
     * и заголовков HTTP-запросов. Кроме того, он разрешает отправку cookies при кросс-доменных запросах.
     *
     */

    // Создаем бин CorsFilter для настройки политики CORS
    @Bean
    public CorsFilter corsFilter() {
        // Создаем экземпляр CorsConfiguration
        CorsConfiguration config = new CorsConfiguration();
        // Разрешаем отправку cookies при кросс-доменных запросах
        config.setAllowCredentials(true);
        // Разрешаем доступ с любого IP-адреса
        config.addAllowedOriginPattern("*");
        // Разрешаем все методы HTTP-запросов
        config.addAllowedMethod("*");
        // Разрешаем все заголовки HTTP-запросов
        config.addAllowedHeader("*");

        // Создаем экземпляр UrlBasedCorsConfigurationSource
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Регистрируем конфигурацию CORS для всех URL-адресов
        source.registerCorsConfiguration("/**", config);

        // Возвращаем новый экземпляр CorsFilter с настроенной политикой CORS
        return new CorsFilter(source);
    }
}
