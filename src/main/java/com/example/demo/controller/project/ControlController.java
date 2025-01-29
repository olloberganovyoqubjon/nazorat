package com.example.demo.controller.project;

import com.example.demo.annotation.CurrentUser;
import com.example.demo.annotation.RoleniTekshirish;
import com.example.demo.entity.Users;
import com.example.demo.payload.ApiResult;
import com.example.demo.payload.project.ChargerDto;
import com.example.demo.payload.project.ControlDto;
import com.example.demo.payload.project.OutControlDto;
import com.example.demo.payload.project.ReceptionDto;
import com.example.demo.service.project.ControlService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("api/control")
@CrossOrigin("*")
public class ControlController {

    private final ControlService controlService;

    public ControlController(ControlService controlService) {
        this.controlService = controlService;
    }


    /**
     * Yangi nazorat kiritish
     *
     * @param control     kontrolDto ma'lumotlarini to'ldirish
     * @param currentUser kirgan foydalanuvchi
     * @return Ijobiy holatda "Yangi nazorat muivaffaqiyatli kiritildi" aks holda xato habari qaytadi
     */
    @PreAuthorize(value = "hasAuthority('ADD_CONTROL')") //permission (huquq bo'yicha tekshirish)
    @RoleniTekshirish(role = "USER, HELPER")
    @PostMapping("createControl")
    public HttpEntity<?> createControl(@RequestBody ControlDto control, @CurrentUser Users currentUser) {
        ApiResult apiResult = controlService.createControl(control, currentUser, 1, 0L);
        return ResponseEntity.status(apiResult.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(apiResult);
    }


    /**
     * Nazoratni o'zgartirish
     *
     * @param control     kontrolDto ma'lumotlarini to'ldirish
     * @param currentUser kirgan foydalanuvchi
     * @return Ijobiy holatda "Yangi nazorat muivaffaqiyatli kiritildi" aks holda xato habari qaytadi
     */
    @RoleniTekshirish(role = "USER, HELPER, RAIS")
    @PutMapping("updateControl/{controlId}")
    public HttpEntity<?> updateControl(@RequestBody ControlDto control, @CurrentUser Users currentUser, @PathVariable Long controlId) {
        ApiResult apiResult = controlService.updateControl(control, currentUser, controlId);
        return ResponseEntity.status(apiResult.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(apiResult);
    }


    /**
     * kelgan nazoratni yana kimgadir biriktirish
     *
     * @param controlId   nazorat id raqami
     * @param chargerDtos nazoratga qo'yilganlar
     * @param currentUser kirgan foydalanuvchi
     * @return
     */
    @PreAuthorize(value = "hasAuthority('ADD_CONTROL')") //permission (huquq bo'yicha tekshirish)
    @RoleniTekshirish(role = "USER, HELPER")
    @PostMapping("addControl/{controlId}")
    public HttpEntity<?> addControl(@PathVariable Long controlId, @RequestBody List<ChargerDto> chargerDtos, @CurrentUser Users currentUser) {
        ApiResult apiResult = controlService.addControl(controlId, chargerDtos, currentUser);
        return ResponseEntity.status(apiResult.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(apiResult);
    }

    /**
     * Foydalanuvchi o'zining, o'zidan yuqori va o'zidan pastki qismdagi nazoratlarini olish
     *
     * @param currentUser kirgan foydalanuvchi
     * @return olingan nazoratlarni, message, va success ni qaytaradi
     */
//    @PreAuthorize(value = "hasAuthority('GET_CONTROL')") //permission (huquq bo'yicha tekshirish)
    @RoleniTekshirish(role = "USER, HELPER, RAIS")
    @GetMapping("getAllControls/{sortNum}/{userId}")
    public HttpEntity<?> getAllControls(@CurrentUser Users currentUser, @PathVariable Integer sortNum, @PathVariable Long userId) {
        ApiResult apiResult = controlService.getAllControls(currentUser, sortNum, userId);
        return ResponseEntity.status(apiResult.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(apiResult);
    }


    /**
     * Foydalanuvchi o'zining va o'zidan pastki qismdagi foydalanuvchilarni ismi va otasining ismini bosh harfi va familiyasini qaytaradi
     *
     * @param currentUser kirgan foydalanuvchi
     * @return shakllantirilga familiyalarni, message, va success ni qaytaradi
     */
//    @PreAuthorize(value = "hasAuthority('ADD_CONTROL, GET_CONTROL')") //permission (huquq bo'yicha tekshirish)
    @RoleniTekshirish(role = "USER, HELPER, RAIS")
    @GetMapping("getAllControlUsers")
    public HttpEntity<?> getAllControlUsers(@CurrentUser Users currentUser) {
        ApiResult apiResult = controlService.getAllControlUsers(currentUser);
        return ResponseEntity.status(apiResult.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(apiResult);
    }


    @RoleniTekshirish(role = "USER, HELPER, RAIS")
    @GetMapping("getFatherUsers")
    public HttpEntity<?> getFatherUsers(@CurrentUser Users currentUser) {
        ApiResult apiResult = controlService.getFatherUsers(currentUser);
        return ResponseEntity.status(apiResult.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(apiResult);
    }


    /**
     * Hujjat qaytarilganqanda uni hisobga olib qo'yish
     *
     * @param currentUser  kirgan foydalanuvchi
     * @param receptionDto qaytariligan hujjat qiymatlari
     * @return muvaffaqiyatli saqlanganligi haqida belgi va success
     */
    @PreAuthorize(value = "hasAuthority('ADD_CONTROL')") //permission (huquq bo'yicha tekshirish)
    @RoleniTekshirish(role = "USER, HELPER")
    @PostMapping("returnToReception")
    public HttpEntity<?> returnToReception(@CurrentUser Users currentUser, @RequestBody ReceptionDto receptionDto) {
        ApiResult apiResult = controlService.returnToReception(currentUser, receptionDto);
        return ResponseEntity.status(apiResult.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(apiResult);
    }





    /**
     * nazoratdan qaytarilgan hujjatni qabul qilish
     *
     * @param currentUser kirgan foydalanuvchi
     * @return muvaffaqiyatli saqlanganligi haqida belgi va success
     */
    @RoleniTekshirish(role = "USER, HELPER, RAIS")
    @PostMapping("returnToUser")
    public HttpEntity<?> updateReturn(@RequestBody OutControlDto outControlDto, @CurrentUser Users currentUser) {
        ApiResult apiResult = controlService.updateReturn(currentUser, outControlDto);
        return ResponseEntity.status(apiResult.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(apiResult);
    }





    /**
     * Hujjatni qaytarib olishni bekor qilish
     *
     * @param currentUser  kirgan foydalanuvchi
     * @return muvaffaqiyatli saqlanganligi haqida belgi va success
     */
    @PreAuthorize(value = "hasAuthority('ADD_CONTROL')") //permission (huquq bo'yicha tekshirish)
    @RoleniTekshirish(role = "USER, HELPER")
    @GetMapping("refuseReturnToReception/{controlId}")
    public HttpEntity<?> refuseReturnToReception(@CurrentUser Users currentUser, @PathVariable Long controlId) {
        ApiResult apiResult = controlService.refuseReturnToReception(currentUser, controlId);
        return ResponseEntity.status(apiResult.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(apiResult);
    }




    /**
     * hujjatni nazoratdan yechish
     *
     * @param currentUser kirgan foydalanuvchi
     * @return
     */
//    @PreAuthorize(value = "hasAuthority('DELETE_CONTROL')") //permission (huquq bo'yicha tekshirish)
    @RoleniTekshirish(role = "HELPER, RAIS")
    @PutMapping("deleteControl")
    public HttpEntity<?> deleteControl(@CurrentUser Users currentUser, @RequestBody OutControlDto outControlDto) {
        ApiResult apiResult = controlService.deleteControl(currentUser, outControlDto);
        return ResponseEntity.status(apiResult.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(apiResult);
    }


    /**
     * yangi nazoratlar bor yoki yo'qligini kuratib turadi
     *
     * @param currentUser kirgan foydalanuvchi
     * @return agar nazorat da yangi paydo bo'lgan bo'lsa true ak holda false qaytaradi
     */
    @PreAuthorize(value = "hasAuthority('GET_CONTROL')") //permission (huquq bo'yicha tekshirish)
    @RoleniTekshirish(role = "RAIS, HELPER, USER")
    @GetMapping("monitoringControl")
    public HttpEntity<?> monitoringControl(@CurrentUser Users currentUser) {
        ApiResult apiResult = controlService.monitoringControl(currentUser);
        return ResponseEntity.ok(apiResult);
    }


    /**
     * yangi nazoratlar bor yoki yo'qligini kuratib turadi
     *
     * @return agar nazorat da yangi paydo bo'lgan bo'lsa true ak holda false qaytaradi
     */
    @PreAuthorize(value = "hasAuthority('GET_CONTROL')") //permission (huquq bo'yicha tekshirish)
    @RoleniTekshirish(role = "RAIS, HELPER, USER")
    @GetMapping("seen/{controlId}")
    public HttpEntity<?> seen(@PathVariable Long controlId) {
        ApiResult apiResult = controlService.seen(controlId);
        return ResponseEntity.ok(apiResult);
    }


    /**
     * bitta nazorat qiymatlarini olish
     *
     * @param controlId   nazorat id raqami
     * @param currentUser kirgan foydalanuvchi
     * @return
     */
    @PreAuthorize(value = "hasAuthority('GET_CONTROL')") //permission (huquq bo'yicha tekshirish)
    @RoleniTekshirish(role = "RAIS, HELPER, USER")
    @GetMapping("getOneControl/{controlId}")
    public HttpEntity<?> getOneControl(@PathVariable Long controlId, @CurrentUser Users currentUser) {
        ApiResult apiResult = controlService.getOneControl(controlId, currentUser);
        return ResponseEntity.status(apiResult.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(apiResult);
    }






    /**
     * ijro natijalarini kiritishda beriladigan ma'lumotlar
     *
     * @param controlId   nazorat id raqami
     * @param currentUser kirgan foydalanuvchi
     * @return
     */
    @PreAuthorize(value = "hasAuthority('GET_CONTROL')") //permission (huquq bo'yicha tekshirish)
    @RoleniTekshirish(role = "RAIS, HELPER, USER")
    @GetMapping("getReturnData/{controlId}")
    public HttpEntity<?> getReturnData(@PathVariable Long controlId, @CurrentUser Users currentUser) {
        ApiResult apiResult = controlService.getReturnData(controlId, currentUser);
        return ResponseEntity.status(apiResult.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(apiResult);
    }



    /**
     * nazorat kartasini chop etish
     *
     * @param controlId nazorat id raqami
     * @param response  fayl uzatish
     * @return fayl ko'rinishida qaytadi
     */
    @PreAuthorize(value = "hasAuthority('GET_CONTROL')") //permission (huquq bo'yicha tekshirish)
    @RoleniTekshirish(role = "RAIS, HELPER, USER")
    @GetMapping("getReport/{controlId}")
    public HttpEntity<?> getReport(@PathVariable Long controlId, HttpServletResponse response) {
        ApiResult apiResult = controlService.getReport(controlId, response);
        return ResponseEntity.ok(apiResult);
    }


    /**
     * pdf yoriqnoma yaratish
     * @param response fayl uzatish
     * @return fayl ko'rinishida qaytadi
     */
    @GetMapping("help")
    public HttpEntity<?> help(HttpServletResponse response) {
        ApiResult apiResult = controlService.helpPdf(response);
        return ResponseEntity.ok(apiResult);
    }



    /**
     * video yoriqnoma yaratish
     * @param response fayl uzatish
     * @return fayl ko'rinishida qaytadi
     */
    @GetMapping("helpVideo")
    public HttpEntity<?> helpVideo(HttpServletResponse response) {
        ApiResult apiResult = controlService.helpVideo(response);
        return ResponseEntity.ok(apiResult);
    }
}
