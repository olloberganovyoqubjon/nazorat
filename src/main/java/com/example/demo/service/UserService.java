package com.example.demo.service;

// Импорт необходимых классов и интерфейсов

import com.example.demo.entity.Role;
import com.example.demo.entity.Users;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.payload.*;
import com.example.demo.payload.project.AllUserResponse;
import com.example.demo.payload.project.ReportDto;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.PasswordValidator;
import com.example.demo.security.tokenGenerator.JwtProvider;
import com.example.demo.service.project.ControlService;
import com.example.demo.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service   // Аннотация для обозначения класса как сервисного компонента Spring
public class UserService {

    private final UserRepository userRepository; // Внедрение зависимости для работы с репозиторием пользователей

    private final PasswordEncoder passwordEncoder; // Внедрение зависимости для кодирования паролей

    private final PasswordValidator passwordValidator; // Внедрение зависимости для проверки паролей

    private final RoleRepository roleRepository; // Внедрение зависимости для работы с репозиторием ролей

    private final JwtProvider jwtProvider; // Внедрение зависимости для работы с JWT-токенами
    private final AuthenticationManager authenticationManager;
    private final ControlService controlService;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, PasswordValidator passwordValidator, RoleRepository roleRepository, JwtProvider jwtProvider, AuthenticationManager authenticationManager, ControlService controlService) {
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
        this.roleRepository = roleRepository;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.controlService = controlService;
    }


    public ApiResult register(RegisterDto dto) {

        // Проверка совпадения паролей
        if (!dto.getPassword().equals(dto.getPrePassword()))
            return new ApiResult("Parollar mos emas!", false);

        // Проверка существования пользователя с таким же именем пользователя
        if (userRepository.findByUsername(dto.getUsername()).isPresent())
            return new ApiResult("Bunday login mavjud!", false);

        Optional<Users> optionalUsers = userRepository.findById(dto.getFatherId());
        if (optionalUsers.isEmpty()) {
            return new ApiResult("Bunday foydalanuvchi mavjud!", false);
        }

        // Проверка существования роли с указанным идентификатором
        Optional<Role> optionalRole = roleRepository.findById(dto.getRoleId());
        if (optionalRole.isEmpty())
            return new ApiResult("Bunday rol mavjud emas!", false);


        if (AppConstants.ADMIN.equals(optionalRole.get().getName())) {
            if (passwordValidator.isValid(dto.getPassword()))
                return new ApiResult("Parol yetarli darajada murakkab emas!", false);
        }


        // Создание нового пользователя
        Users user = new Users();
        user.setFirstName(dto.getFirstName()); // Установка фамилии пользователя
        user.setLastName(dto.getLastName()); // Установка фамилии пользователя
        user.setPatronym(dto.getPatronym());
        user.setUsername(dto.getUsername()); // Установка имени пользователя
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // Кодирование и установка пароля
        user.setRole(roleRepository.findById(dto.getRoleId()).orElseThrow(() -> new ResourceNotFoundException(
                "role", "id", dto.getRoleId()
        ))); // Установка роли пользователя
        user.setEnabled(dto.isActive()); // Установка активности пользователя
        Users vatherUsers = optionalUsers.get();
        user.setStage(vatherUsers.getStage() + 1);
        user.setFatherUsers(vatherUsers);
        user.setChiefName(dto.getChiefName());
        user.setFirstReport(dto.getTitleFirst());
        user.setSecondReport(dto.getTitleSecond());
        user.setManagement(dto.getManagement());

        int i = 0;
        for (Users users : userRepository.findByFatherUsers_Id(vatherUsers.getId())) {
            if (users.getSortUser() != null) {
                if (users.getSortUser() > i) {
                    i = users.getSortUser();
                }
            }
        }
        user.setSortUser(i);

        // Сохранение пользователя в репозитории
        userRepository.save(user);
        // Возвращение успешного результата регистрации
        return new ApiResult("Muvoffaqiyatli ro'yxatdan o'tkazildi!", true, userRepository.findAllWhereDeleteIsNotTrue());
    }


    public ApiResult edit(Long id, RegisterDto dto) {
        // Поиск пользователя по идентификатору
        Optional<Users> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty())
            return new ApiResult("Foydalanuvchi ma'lumotlari to'liq emas!", true);

        // Проверка существования пользователя с таким же именем пользователя, но другим идентификатором
        if (userRepository.doesUserExistByUsernameAndIdNotEqual(dto.getUsername(), id))
            return new ApiResult("Bunday login mavjud!", false);

        // Проверка существования роли с указанным идентификатором

        Optional<Role> optionalRole = roleRepository.findById(dto.getRoleId());
        if (optionalRole.isEmpty())
            return new ApiResult("Bunday toifa mavjud emas!", true);


        if (AppConstants.ADMIN.equals(optionalRole.get().getName())) {
            if (passwordValidator.isValid(dto.getPassword()))
                return new ApiResult("Parol yetarli darajada murakkab emas!", false);
        }


        // Создание нового пользователя
        Users user = optionalUser.get();
        user.setFirstName(dto.getFirstName()); // Установка фамилии пользователя
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername()); // Установка имени пользователя
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // Кодирование и установка пароля
        user.setRole(roleRepository.findById(dto.getRoleId()).orElseThrow(() -> new ResourceNotFoundException(
                "role", "id", dto.getRoleId()
        ))); // Установка роли пользователя

        user.setEnabled(dto.isActive()); // Установка активности пользователя


        // Сохранение пользователя в репозитории
        userRepository.save(user);

        // Возвращение успешного результата редактирования
        return new ApiResult("Muvoffaqiyatli tahrirlandi!", true, userRepository.findAllWhereDeleteIsNotTrue());
    }


    public ApiResult editUserForUser(Users userinSystem, EditUserDto dto) {

        try {
            // Проверка текущего пароля пользователя
            if (!passwordEncoder.matches(dto.getPasswordNow(), userinSystem.getPassword())) {
                return new ApiResult("Foydalanuvchi paroli to'g'ri emas!", false);
            }
            // Проверка совпадения нового пароля и его подтверждения
            if (!dto.getPassword().equals(dto.getPrePassword()))
                return new ApiResult("Parollar bir biriga mos emas!", false);


            // Проверка валидности нового пароля
            if (userinSystem.getRole().getName().equals(AppConstants.ADMIN)) {
                if (passwordValidator.isValid(dto.getPassword()))
                    return new ApiResult("Parol mustahkam emas!", false);
            }

            // Установка нового пароля
            userinSystem.setPassword(passwordEncoder.encode(dto.getPassword()));

            // Сохранение изменений в базе данных
            Users user = userRepository.save(userinSystem);

            // Генерация нового токена
            String token = jwtProvider.generatorToken(user.getUsername(), user.getRole());
            // Генерация нового токена обновления

            // Возвращение успешного результата с новыми токенами
            return new ApiResult("Token", true, token);
        } catch (Exception e) {
            // Возвращение результата в случае ошибки
            return new ApiResult("Token", false, null, null);
        }
    }


    /**
     * Получает список всех пользователей, у которых не установлен флаг удаления.
     *
     * @return Список объектов Users.
     */
    public List<Users> getAll() {
        List<Users> usersList = userRepository.findAllWhereDeleteIsNotTrue();
        for (Users users : usersList) {
            users.setPassword(null);
//            users.setRole(null);
            users.setEnabled(false);
            users.setFatherUsers(null);
            users.setUpdatedBy(null);
            users.setCreatedBy(null);
        }
        return usersList;
    }

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя.
     * @return Объект Users, если найден, или null, если не найден.
     */
    public Users getOne(Long id) {
        return userRepository.findById(id).orElse(null);
    }


    /**
     * Удаляет пользователя по его идентификатору, устанавливая флаги отключения и удаления.
     *
     * @param id Идентификатор пользователя.
     * @return Результат операции в виде ApiResult.
     */
    public ApiResult delete(Long id) {

        try {
            // Проверка существования пользователя по идентификатору
            boolean existsById = userRepository.existsById(id);
            if (!existsById)
                return new ApiResult("Bunday foydalanuvchi mavjud emas", false);

            // Получение пользователя по идентификатору
            Users users = userRepository.findById(id).get();
            // Установка флага отключения пользователя
            users.setEnabled(false);
            // Установка флага неактивности пользователя
            users.setDelete(true);

            // Сохранение изменений в базе данных
            userRepository.save(users);

            // Возвращение успешного результата удаления
            return new ApiResult("O'chirildi", true, userRepository.findAllWhereDeleteIsNotTrue());
        } catch (Exception e) {
            // Возвращение результата в случае ошибки
            return new ApiResult("Xatolik", false);
        }
    }


    /**
     * Активирует или деактивирует пользователя по его идентификатору.
     *
     * @param id     Идентификатор пользователя.
     * @param active Флаг активности (true для активации, false для деактивации).
     * @return Результат операции в виде ApiResult.
     */
    public ApiResult active(Long id, boolean active) {

        try {
            // Проверка существования пользователя по идентификатору
            boolean existsById = userRepository.existsById(id);
            if (!existsById)
                return new ApiResult("Bunday foydalanuvchi mavjud emas", false);

            // Получение пользователя по идентификатору
            Users users = userRepository.findById(id).get();
            // Установка флага активности пользователя
            users.setEnabled(active);

            // Сохранение изменений в базе данных
            userRepository.save(users);

            // Возвращение успешного результата изменения активности
            return new ApiResult("O'zgartirildi", true, userRepository.findAll());

        } catch (Exception e) {
            // Возвращение результата в случае ошибки
            return new ApiResult("Xatolik", false);
        }
    }


    /**
     * Выполняет выход пользователя, устанавливая его подразделение как офлайн.
     *
     * @param user Объект пользователя, выполняющего выход.
     */
    public ApiResult logout(Users user) {
        // Поиск подразделения пользователя по его идентификатору
        if (user != null) {
            // Установка статуса подразделения как офлайн
            user.setOnline(false);
            // Сохранение изменений в базе данных
            userRepository.save(user);
            return new ApiResult(true);
        } else {
            return new ApiResult("Xatolik", false);
        }
    }

    public ApiResult login(LoginDto dto) {
        try {
            Optional<Users> optional = userRepository.findByUsername(dto.getUsername());
            if (optional.isEmpty() || !passwordEncoder.matches(dto.getPassword(), optional.get().getPassword())) {
                return new ApiResult("Invalid username or password!", false);
            }

            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    dto.getUsername(),
                    dto.getPassword()
            ));

            Users user = (Users) authenticate.getPrincipal();
            if (!user.isEnabled()) {
                return new ApiResult("Tizimga kirish taqiqlangan!", false);
            }

            String token = JwtProvider.generatorToken(user.getUsername(), user.getRole());
            String refreshToken = JwtProvider.generateRefreshToken(user.getUsername(), user.getRole());

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            if (!user.getRole().getName().equals(AppConstants.ADMIN)) {
                user.setOnline(true);
                userRepository.save(user);
            }
            user.setEntryTime(timestamp);
            userRepository.save(user);
            user.setPassword(null);
            ApiResult apiResult = new ApiResult();
            apiResult.setSuccess(true);
            apiResult.setToken(token);
            apiResult.setRefreshToken(refreshToken);
            apiResult.setMessage("Token");
            apiResult.setObject(user);

            return apiResult;
        } catch (BadCredentialsException e) {
            return new ApiResult("Login yoki parol xato!", false);
        }
    }

    public ApiResult registerReport(Users users, ReportDto reportDto) {
        Optional<Users> optionalUsers = userRepository.findById(users.getId());
        if (optionalUsers.isPresent()) {
            Users users1 = optionalUsers.get();
            users1.setFirstReport(reportDto.getFirst());
            users1.setSecondReport(reportDto.getSecond());
            userRepository.save(users1);
            return new ApiResult("Hisobot nomi muvaffaqiyatli kiritildi!", true, users1);
        }
        return new ApiResult("nimadir xatolik yuz berdi!", false);
    }

    public ApiResult getAllUsers(Users users) {
        List<Users> usersList = userRepository.findAll();
        List<AllUserResponse> allUserResponses = new ArrayList<>();
        for (Users user : usersList) {
            String name = controlService.getName(user);
            String fatherUser = null;
            if (user.getFatherUsers() != null)
                fatherUser = controlService.getName(user.getFatherUsers());
            allUserResponses.add(new AllUserResponse(user.getId(), user.getUsername(), name, fatherUser));
        }
        List<Role> roleList = roleRepository.findAll();
        return new ApiResult("Hamma foydalanuvchilar", true, allUserResponses, roleList, null);
    }

    public ApiResult changePassword(Users users, PasswordDto passwordDto) {
        Optional<Users> optionalUsers = userRepository.findById(users.getId());
        if (optionalUsers.isPresent()) {
            Users users1 = optionalUsers.get();
            if (!passwordDto.getPassword().equals(passwordDto.getPrePassword())) {
                return new ApiResult("Parollar mos kelmaydi!", false);
            }
            users1.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
            userRepository.save(users1);
        }
        return new ApiResult("Parol muvaffaqiyatli almashtirildi!", true);
    }
}
