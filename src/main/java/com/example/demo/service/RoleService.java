package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления ролями пользователей.
 */
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Возвращает список всех ролей, исключая роль супер-администратора.
     * @return Список объектов Role.
     */
    public List<Role> getAll() {
        return roleRepository.findAllWithoutSuperAdmin();
    }

    /**
     * Возвращает одну роль по её идентификатору.
     * @param id Идентификатор роли.
     * @return Объект Role, если найден, или null, если не найден.
     */
    public Role getOne(Long id) {
        return roleRepository.findById(id).orElse(null);
    }
}
