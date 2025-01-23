package com.example.demo.controller;

import com.example.demo.annotation.RoleniTekshirish;
import com.example.demo.entity.Role;
import com.example.demo.service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Аннотация, указывающая, что этот класс является REST-контроллером
@RestController
// Указание базового пути для всех эндпоинтов в этом контроллере
@RequestMapping("/api/role")
// Аннотация Swagger для документации API
@Tag(name = "RoleController", description = "Barcha rolelar ro'yxatini olish uchun ishlatiladi")
@CrossOrigin("*")
public class RoleController {

    /**
     * Этот класс представляет собой контроллер для работы с ролями в REST API. Он обрабатывает HTTP-запросы, связанные с ролями пользователей.
     */

    // Внедрение зависимости RoleService
    private final RoleService service;

    @Autowired
    public RoleController(RoleService service) {
        this.service = service;
    }

    // Метод для получения всех ролей
    @RoleniTekshirish(role = "ADMIN")
    @GetMapping
    public HttpEntity<?> getAll() {
        // Получение списка всех ролей через сервис
        List<Role> all = service.getAll();
        // Возврат ответа с кодом статуса OK, если список не пуст, иначе BAD_REQUEST
        return ResponseEntity.status(!all.isEmpty() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(all);
    }

    // Метод для получения одной роли по ID
    @RoleniTekshirish(role = "ADMIN, USER")
    @GetMapping("/{id}")
    public HttpEntity<?> getOne(@PathVariable Long id) {
        // Получение роли по ID через сервис
        Role one = service.getOne(id);
        // Возврат ответа с кодом статуса OK, если роль найдена, иначе CONFLICT
        return ResponseEntity.status(one != null ? HttpStatus.OK : HttpStatus.CONFLICT).body(one);
    }
}