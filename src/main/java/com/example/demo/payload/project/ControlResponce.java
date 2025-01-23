package com.example.demo.payload.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ControlResponce {

    private Long userId;
    private Long id;
    private Date comeDate;                 //kelgan sana
    private Date controlPeriod;            //nazorat muddati
    private String blankNum;                //blanka raqami
    private Boolean b_control;             //nazoratdami
    private String otdName;                 //hajjat qayerdan kelgan(bo'linma nomi)
    private String regNum;                  //qayd raqami
    private Date docDate;                   //qayd raqami sanasi
    private String regNumCome;              //qayd raqami kelgan
    private Date regNumComeDate;            //qayd raqami kelgan sanasi
    private String doc_name;                 //hujjatning qisqacha mazmuni
    private String resPerson;               //rezolyutsiyalovchi shaxs
    private String createPerson;            //nazoratni yaratgan shaxs
    private Date resDate;                   //rezolyutsiyalovchi shaxs sanasi
    private List<ChargerDto> chargerList;           //rezolyutsiyaga qo'yilgan shaxhlar ro'yxati
    private String charger;
    private String resolution;              //resoyutsiyasi
    private String controllerPerson;        //nazoratchi shaxs
    private String tel;                     //nazoratchi telefon raqami
    private Date receptionDate;             //qabulxonaga berilgan sana
    private String executionRegNum;         //ijro bo'yicha qayd raqami
    private Date executionDate;             //ijro sanasi
    private String executorTel;             //ijrochining telefon raqami
    private String workDone;                //bajarilgan ishlar
    private String outController;           //kim komtrolni yechganligi
    private boolean returned;
    private String workbookNum;             //hujjat tikilgan ishjild raqami
    private String workbookPageNum;         //ishjilddagi sahifa raqami
}
