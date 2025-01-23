package com.example.demo.security.tokenGenerator; // Пакет, в котором находится данный класс

import com.example.demo.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component // Аннотация для определения класса как компонента Spring
public class JwtProvider {

    /**
     * Класс JwtProvider предоставляет методы для генерации и валидации JWT токенов. Этот класс позволяет создавать токены
     * для аутентификации пользователей и проверять их на валидность.
     */

    private static final String keyForToken = "WEftg45534ssgfsg445dfg$%^e_transfer_app_$@Jk+IEK397WEftg45534ssgfsg445dfg$%^e_transfer_app_$@Jk+IEK397";  // Секретный ключ для генерации токена
    private static final long expireTimeRefreshToken = 1000 * 60 * 60 * 24 * 14; // Время жизни токена обновления (14 дней)

    /**
     * Генерация токена на основе имени пользователя, роли и ключа для токена
     *
     * @param username Имя пользователя
     * @param role Роль пользователя
     * @return Сгенерированный токен
     */
    public static String generatorToken(String username, Role role) {

        // Время жизни токена (7 дней)
//        long expireTime = 1000 * 60 * 60 * 24 * 7;
        Date expireDate = new Date(System.currentTimeMillis() + expireTimeRefreshToken/2); // Установка даты истечения срока действия токена

        try {
            // Generate the signing key from the existing key string
            SecretKey key = Keys.hmacShaKeyFor(keyForToken.getBytes()); // Ensure keyForToken is at least 64 bytes long

            return Jwts
                    .builder()
                    .setSubject(username) // Set the username as the subject of the token
                    .setIssuedAt(new Date()) // Set the current date as the issued date of the token
                    .setExpiration(expireDate) // Set the expiration date of the token
                    .claim("roles", role.getName()) // Add the user's role to the token
                    .signWith(key, SignatureAlgorithm.HS512) // Sign the token using the SecretKey and HS512 algorithm
                    .compact(); // Return the generated token
        } catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace(); // Print stack trace or use a logger
            throw new RuntimeException("Error generating token: " + e.getMessage()); // Rethrow or handle the exception
        }
    }

    public static String generateRefreshToken(String username, Role role) {
        Date expireDate = new Date(System.currentTimeMillis() + expireTimeRefreshToken); // Установка даты истечения срока действия токена обновления
        // Секретный ключ для генерации токена обновления
        String keyForTokenRefresh = "FDSgfghy674^4sdassdaAasdf498_transfer_app_$@Jk+IEK397FDSgfghy674^4sdassdaAasdf498_transfer_app_$@Jk+IEK397";
        SecretKey key = Keys.hmacShaKeyFor(keyForTokenRefresh.getBytes());
        return Jwts
                .builder()
                .setSubject(username) // Установка имени пользователя в качестве субъекта токена обновления
                .setIssuedAt(new Date()) // Установка текущей даты как даты выдачи токена обновления
                .setExpiration(expireDate) // Установка даты истечения срока действия токена обновления
                .claim("roles", role.getName()) // Добавление роли пользователя в токен обновления
                .signWith(key, SignatureAlgorithm.HS512) // Подпись токена обновления с использованием алгоритма HS512 и ключа для токена обновления
                .compact(); // Возврат сгенерированного токена обновления
    }

    /**
     * Извлечение имени пользователя из токена
     *
     * @param token Токен
     * @return Имя пользователя или null, если токен истек
     */
    public String getUsernameFromToken(String token) {
        try {
            boolean tokenExpired = isTokenExpired(token); // Check if the token is expired
            if (tokenExpired) {
                return null; // Return null if the token is expired
            }

            // Generate the signing key from the existing key string
            SecretKey key = Keys.hmacShaKeyFor(keyForToken.getBytes()); // Ensure keyForToken is at least 64 bytes long

            // Parse the JWT and extract the claims
            Claims claims = Jwts
                    .parserBuilder()
                    .setSigningKey(key) // Set the key for parsing the token
                    .build()
                    .parseClaimsJws(token) // Parse the token
                    .getBody();

            return claims.getSubject(); // Return the username
        } catch (Exception e) {
            System.out.println(e.getMessage()); // Log the error message
            return null; // Return null in case of an error
        }
    }

    /**
     * Проверка, истек ли токен
     *
     * @param token Токен
     * @return true, если токен истек, иначе false
     */
    public boolean isTokenExpired(String token) {
        try {
            // Generate the signing key from the existing key string
            SecretKey key = Keys.hmacShaKeyFor(keyForToken.getBytes()); // Ensure keyForToken is at least 64 bytes long

            // Parse the JWT and extract the claims
            Claims claims = Jwts.parserBuilder() // Use parserBuilder instead of parser
                    .setSigningKey(key) // Set the key for parsing the token
                    .build() // Build the parser
                    .parseClaimsJws(token) // Parse the token
                    .getBody();

            return claims.getExpiration().before(new Date()); // Check if the token has expired
        } catch (Exception e) {
            System.out.println(e.getMessage()); // Log the error message
            return true; // Return true in case of parsing errors (indicating the token is expired)
        }
    }

//    public static void main(String[] args) {
//        String username = "user1"; // Пример имени пользователя
//        Role role = new Role(); // Создание объекта роли
//        role.setName("Oddiy foydalanuvchi"); // Установка имени роли
//
//        String s = generatorToken(username, role); // Генерация токена
//        System.out.println(s); // Вывод токена в консоль
//    }
}
