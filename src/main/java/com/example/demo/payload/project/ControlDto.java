package com.example.demo.payload.project;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ControlDto {

    @NotNull(message = "kelgan sana bo'sh bo'lmasian!")
    private Date comeDate;                 //kelgan sana
    private Date controlPeriod;            //nazorat muddati
    @NotNull(message = "Nazorat tanlangan bo'lsin!")
    private Boolean b_control;             //nazoratdami
    @NotNull(message = "Bo'linma nomi bo'sh bo'lmasin!")
    private String otdName;                 //hajjat qayerdan kelgan(bo'linma nomi)
    @NotNull(message = "Qayd raqami bo'sh bo'lmasin!")
    private String regNum;                  //qayd raqami
    @NotNull(message = "Qayd raqam sanasi bo'sh bo'lmasin!")
    private Date docDate;                   //qayd raqami sanasi
    private String regNumCome;              //qayd raqami kelgan
    private Date regNumComeDate;            //qayd raqami kelgan sanasi
    @NotNull(message = "Hujjatning qisqacha mazmuni bo'sh bo'lmasin!")
    private String doc_name;                 //hujjatning qisqacha mazmuni
    @NotNull(message = "rezolyutsiyalovchi shaxs bo'sh bo'lmasin!")
    private Long resPersonId;               //rezolyutsiyalovchi shaxs
    private Date resDate;                   //rezolyutsiyalovchi shaxs sanasi
    private List<ChargerDto> chargerList;           //rezolyutsiyaga qo'yilgan shaxhlar ro'yxati
    @NotNull(message = "resolyutsiyasi bo'sh bo'lmasin!")
    private String resolution;              //resoyutsiyasi
    private String controllerPerson;        //nazoratchi shaxs
    private String tel;                     //nazoratchi telefon raqami
    private String titleFirst;              //blanka ning birinchi qatori
    private String titleSecond;              //blanka ning ikkinchi qatori
}
