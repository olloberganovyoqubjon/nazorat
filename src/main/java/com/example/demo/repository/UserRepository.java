package com.example.demo.repository;

// Импортируем сущность Users для использования в методах репозитория

import com.example.demo.entity.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

// Объявляем интерфейс UserRepository, который наследует JpaRepository для сущности Users с типом ID Long
public interface UserRepository extends JpaRepository<Users, Long> {

    /**
     *
     * Этот код определяет интерфейс UserRepository, который является частью пакета fan.company.filetransfer.repository.
     * Он используется для управления сущностью Users (предположительно, сущность, представляющую пользователей системы)
     * в базе данных с помощью JPA (Java Persistence API). Интерфейс наследует JpaRepository, что предоставляет стандартные
     * методы для работы с базой данных (например, сохранение, удаление, поиск по ID и т.д.).
     *
     * В интерфейсе определены следующие методы:
     *
     * findByUsername(String username): находит пользователя по его имени пользователя.
     * countAllByDivisionId(Long id): подсчитывает количество пользователей с указанным идентификатором подразделения.
     * findAllWhereDeleteIsNotTrue(): выполняет кастомный запрос для поиска всех пользователей, у которых значение поля delete
     * не равно true и имя пользователя не равно "superadmin".
     * doesUserExistByUsernameAndIdNotEqual(String username, Long id): выполняет кастомный запрос для проверки существования
     * пользователя по имени пользователя и идентификатору пользователя, исключая указанный идентификатор.
     * findAllByDivisionId(Long divisionId): находит всех пользователей по идентификатору подразделения.
     *
     *
     */

    // Метод для поиска пользователя по имени пользователя
    Optional<Users> findByUsername(String username);


    // Кастомный запрос для поиска всех пользователей, у которых значение поля delete не равно true и имя пользователя не равно "superadmin"
    @Query("SELECT e FROM Users e WHERE e.delete <> true and e.username <> 'admin'")
    List<Users> findAllWhereDeleteIsNotTrue();

    // Кастомный запрос для проверки существования пользователя по имени пользователя и идентификатору пользователя, исключая указанный идентификатор
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Users e WHERE e.username = :username AND e.id <> :id")
    boolean doesUserExistByUsernameAndIdNotEqual(String username, Long id);

    @Query("select Users from Users where role = :role")
    List<Users> findAllByRole(String role);


    List<Users> findAllByRoleName(String name, Pageable pageable);

    List<Users> findByFatherUsers_Id(Long id);

    List<Users> findByStageOrderBySortUser(Integer stage);

    List<Users> findByStage(Integer stage);



}
