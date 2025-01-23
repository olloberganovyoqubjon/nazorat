package com.example.demo.controller.project;

import com.example.demo.annotation.CurrentUser;
import com.example.demo.annotation.RoleniTekshirish;
import com.example.demo.entity.Users;
import com.example.demo.payload.ApiResult;
import com.example.demo.service.project.StatisticsService;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/statistics")
@CrossOrigin("*")
public class StatisticsController {


    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }


    /**
     * Kirgan foydalanuvchidan bitta past hodim boshliqlarining barcha nazoratdagi (kechikkan va kechikmagan) hujjatlari soni
     * @param currentUser kirgan foydalanuvchi
     * @return count1 - kechikkanlar soni, count2 - kechikmaganlar soni, name boshliqlar
     */
    @PreAuthorize(value = "hasAuthority('GET_CONTROL')") //permission (huquq bo'yicha tekshirish)
    @RoleniTekshirish(role = "RAIS, HELPER, USER")
    @GetMapping("controls")
    public HttpEntity<?> getControls(@CurrentUser Users currentUser) {
        ApiResult apiResult = statisticsService.getControls(currentUser);
        return ResponseEntity.ok(apiResult);
    }


    /**
     * Kirgan foydalanuvchidan bitta past hodim boshliqlarining barcha nazoratdan yechilgan hujjatlari soni
     * @param currentUser kirgan foydalanuvchi
     * @return count1 - nazoratdan yechilganlar soni, name - boshliqlar
     */
    @PreAuthorize(value = "hasAuthority('GET_CONTROL')") //permission (huquq bo'yicha tekshirish)
    @RoleniTekshirish(role = "RAIS, HELPER, USER")
    @GetMapping("deletedControls")
    public HttpEntity<?> getDeletedControls(@CurrentUser Users currentUser) {
        ApiResult apiResult = statisticsService.getDeletedControls(currentUser);
        return ResponseEntity.ok(apiResult);
    }


    /**
     * Kirgan foydalanuvchining o'zi pastki hodimlar uchun yaratgan nazoratdan kechikkan va kechikmagan hujjatlari soni
     * @param currentUser kirgan foydalanuvchi
     * @return count1 - nazoratdan yechilganlar soni, count2 - nazoratdan yechilmaganlar soni
     */
    @PreAuthorize(value = "hasAuthority('GET_CONTROL')") //permission (huquq bo'yicha tekshirish)
    @RoleniTekshirish(role = "RAIS, HELPER, USER")
    @GetMapping("meinControls")
    public HttpEntity<?> getMeinControls(@CurrentUser Users currentUser) {
        ApiResult apiResult = statisticsService.getMeinControls(currentUser);
        return ResponseEntity.ok(apiResult);
    }

    /**
     * Kirgan foydalanuvchiga tushirilgan nazoratdan kechikkan va kechikmagan hujjatlari soni
     * @param currentUser kirgan foydalanuvchi
     * @return count1 - nazoratdan yechilganlar soni, count2 - nazoratdan yechilmaganlar soni
     */
    @PreAuthorize(value = "hasAuthority('GET_CONTROL')") //permission (huquq bo'yicha tekshirish)
    @RoleniTekshirish(role = "RAIS, HELPER, USER")
    @GetMapping("chiefControls")
    public HttpEntity<?> getChiefControls(@CurrentUser Users currentUser) {
        ApiResult apiResult = statisticsService.getChiefControls(currentUser);
        return ResponseEntity.ok(apiResult);
    }

    /**
     * Kirgan foydalanuvchining rahbarlar, o'z va qo'l ostidagilarning nazoratlari soni
     * @param currentUser kirgan foydalanuvchi
     * @return count1 - o'zining nazoratlari soni, count2 - rahbarning nazoratlari soni, count3 - qo'l ostidagilarniong nazoratlari soni
     *         name - rahbar FIO, dateName - hafta kuni, date - sana
     */
    @PreAuthorize(value = "hasAuthority('GET_CONTROL')") //permission (huquq bo'yicha tekshirish)
    @RoleniTekshirish(role = "RAIS, HELPER, USER")
    @GetMapping("weekControls")
    public HttpEntity<?> getWeekControls(@CurrentUser Users currentUser) {
        ApiResult apiResult = statisticsService.getWeekControls(currentUser);
        return ResponseEntity.ok(apiResult);
    }


    @PreAuthorize(value = "hasAuthority('GET_CONTROL')") //permission (huquq bo'yicha tekshirish)
    @RoleniTekshirish(role = "RAIS, HELPER, USER")
    @GetMapping("tableControls")
    public HttpEntity<?> getTableControls(@CurrentUser Users currentUser) {
        ApiResult apiResult = statisticsService.getTableControls(currentUser);
        return ResponseEntity.ok(apiResult);
    }
}
