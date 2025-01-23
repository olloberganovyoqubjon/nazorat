package com.example.demo.component;

import com.example.demo.entity.Role;
import com.example.demo.entity.Users;
import com.example.demo.entity.enums.Huquq;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataLoder implements CommandLineRunner {

    @Autowired
    RoleRepository roleRepository; // Инъекция зависимости RoleRepository для работы с данными ролей
    @Autowired
    UserRepository userRepository; // Инъекция зависимости UserRepository для работы с данными пользователей
    @Autowired
    PasswordEncoder passwordEncoder; // Инъекция зависимости PasswordEncoder для шифрования паролей

    @Value("${spring.sql.init.mode}") // Внедрение значения из application.properties в переменную initModeType
    private String initModeType;

    @Override
    public void run(String... args) throws Exception {

        /**
         * http://localhost:9999/swagger-ui/index.html#/
         * swaggerni olish uchun manzil
         */

        /**
         * Boshlang'ich userlarni yaratish
         *
         * Основное назначение:
         * Класс DataLoder используется для инициализации начальных данных в приложении при его запуске в режиме "always".
         * Он проверяет, если база данных пользователей пуста, то создаёт начального пользователя с ролью super administrator.
         * Пароль пользователя шифруется с использованием PasswordEncoder для безопасного хранения.
         *
         * Примечание:
         * Huquq[] huquqs = Huquq.values();: Получение всех значений перечисления Huquq, которые представляют различные права доступа.
         * roleRepository.save(new Role(...));: Сохранение созданной роли в базе данных через roleRepository.
         * userRepository.save(user);: Сохранение созданного пользователя в базе данных через userRepository.
         * Ловушка try-catch используется для обработки исключений, которые могут возникнуть в процессе инициализации данных,
         * с выводом сообщения об ошибке в случае их возникновения.
         *
         *
         */

        try {
            if (initModeType.equals("always")) { // Проверка режима инициализации (всегда)

                if (userRepository.findAll().isEmpty()) { // Если база данных пользователей пуста

//                    Huquq[] huquqs = Huquq.values(); // Получение всех доступных прав (permissions)
                    Huquq[] huquqsAdmin = {Huquq.ADD_USER, Huquq.DELETE_USER, Huquq.EDIT_USER, Huquq.GET_USER};
                    Huquq[] huquqsRais = {Huquq.DELETE_CONTROL,Huquq.GET_CONTROL};
                    Huquq[] huquqshelper = {Huquq.ADD_CONTROL, Huquq.DELETE_CONTROL,Huquq.GET_CONTROL};
                    Huquq[] huquqsUser = {Huquq.ADD_CONTROL, Huquq.GET_CONTROL};

                    // Создание роли super administrator и сохранение её в базе данных
                    Role adminRole = roleRepository.save(
                            new Role(
                                    AppConstants.ADMIN, // Название роли "superadmin"
                                    Arrays.asList(huquqsAdmin), // Список всех доступных прав для этой роли
                                    "administrator" // Описание роли
                            )
                    );

                    Role raisRole = roleRepository.save(
                            new Role(
                                    AppConstants.RAIS, // Название роли "superadmin"
                                    Arrays.asList(huquqsRais), // Список всех доступных прав для этой роли
                                    "rais" // Описание роли
                            )
                    );

                    Role helperRol = roleRepository.save(
                            new Role(
                                    AppConstants.HELPER, // Название роли "superadmin"
                                    Arrays.asList(huquqshelper), // Список всех доступных прав для этой роли
                                    "helper" // Описание роли
                            )
                    );

                    Role userRole = roleRepository.save(
                            new Role(
                                    AppConstants.USER, // Название роли "superadmin"
                                    Arrays.asList(huquqsUser), // Список всех доступных прав для этой роли
                                    "user" // Описание роли
                            )
                    );

                    try {
                        userRepository.deleteAll();
                        // Создание пользователя с ролью super administrator и сохранение его в базе данных
                        Users superadmin = new Users();
                        superadmin.setFirstName("superadmin"); // Фамилия пользователя
                        superadmin.setUsername("superadmin"); // Логин пользователя
                        superadmin.setPassword(passwordEncoder.encode("Super+Admin")); // Шифрование и установка пароля
                        superadmin.setRole(adminRole); // Установка роли пользователя
                        userRepository.save(superadmin); // Сохранение пользователя в базе данных
                        Users raisUser = new Users();
                        raisUser.setFirstName("Qozoqboy");
                        raisUser.setLastName("Botirov");
                        raisUser.setPatronym("Norboyevich");
                        raisUser.setUsername("bqn");
                        raisUser.setChiefName("T.T.Asrorov");
                        raisUser.setPassword(passwordEncoder.encode("123"));
                        raisUser.setRole(helperRol);
                        raisUser.setStage(1);
                        raisUser.setFirstReport("O'ZBEKISTON RESPUBLIKASI");
                        raisUser.setSecondReport("DAVLAT XAVFSIZLIK XIZMATI RAISI");
                        userRepository.save(raisUser);
                    } catch (Exception e) {
                        System.out.println("DataLoder - " + e.getMessage()); // Обработка и вывод ошибок инициализации данных
                    }

                }
            }
        } catch (Exception e) {
            System.out.println("DataLoder - " + e.getMessage()); // Обработка и вывод ошибок инициализации данных
        }
    }
}
