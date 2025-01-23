package com.example.demo.security.filter;

import com.example.demo.security.tokenGenerator.JwtProvider;
import com.example.demo.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Аннотация для определения класса как компонента Spring
public class JwtFilter extends OncePerRequestFilter {

    /**
     * Класс JwtFilter является фильтром, который проверяет JWT токены в HTTP запросах перед тем, как они будут обработаны.
     * Если токен валиден, пользователь будет аутентифицирован в системе.
     */

    private final JwtProvider jwtProvider; // Внедрение зависимости для работы с JWT токенами
    private final AuthService authService; // Внедрение зависимости для аутентификации пользователей

    @Autowired
    public JwtFilter(JwtProvider jwtProvider, @Lazy AuthService authService) {
        this.jwtProvider = jwtProvider;
        this.authService = authService;
    }

    /**
     * Проверяет JWT токен и аутентифицирует пользователя, если токен валиден.
     *
     * @param request  HTTP запрос
     * @param response HTTP ответ
     * @param filterChain Цепочка фильтров
     * @throws ServletException Исключение, возникающее при ошибках сервлета
     * @throws IOException Исключение, возникающее при ошибках ввода-вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String authorization = request.getHeader("Authorization"); // Получение заголовка Authorization из запроса

            if (authorization != null && authorization.startsWith("Bearer ")) { // Проверка, что заголовок начинается с "Bearer "
                String token = authorization.substring(7); // Удаление префикса "Bearer " из строки
                if (!token.isEmpty()) { // Проверка, что строка не пуста
                    String usernameFromToken = jwtProvider.getUsernameFromToken(token); // Извлечение имени пользователя из токена
                    if (usernameFromToken != null) { // Проверка, что имя пользователя не равно null
                        UserDetails userDetails = authService.loadUserByUsername(usernameFromToken); // Загрузка данных пользователя по имени
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); // Создание объекта аутентификации
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken); // Установка аутентифицированного пользователя в контексте безопасности
                    } else {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Установка статуса 401 Unauthorized
                        response.getWriter().write("Token expired or invalid"); // Запись сообщения об ошибке в ответ
                        return;
                    }
                }
            } else if (request.getQueryString() != null && request.getQueryString().contains("Bearer")) { // Проверка, что строка запроса содержит "Bearer"
                String queryString = request.getQueryString(); // Получение строки запроса
                String token = queryString.substring(queryString.indexOf("Bearer") + 7); // Извлечение части строки из строки запроса
                String usernameFromToken = jwtProvider.getUsernameFromToken(token); // Извлечение имени пользователя из токена
                if (usernameFromToken != null) { // Проверка, что имя пользователя не равно null
                    UserDetails userDetails = authService.loadUserByUsername(usernameFromToken); // Загрузка данных пользователя по имени
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); // Создание объекта аутентификации
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken); // Установка аутентифицированного пользователя в контексте безопасности
                }
            }
        } catch (Exception e) {
            // Закомментированные строки для отладки исключений
            // System.out.println("doFilterInternal da muammo 1- "+e.getMessage());
        } finally {
            try {
                filterChain.doFilter(request, response); // Продолжение цепочки фильтров
            } catch (Exception e) {
                // Закомментированные строки для отладки исключений
                // System.out.println("doFilterInternal da muammo 2 - "+e.getMessage());
            }
        }
    }
}
