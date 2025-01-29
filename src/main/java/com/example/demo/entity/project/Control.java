package com.example.demo.entity.project;

import com.example.demo.entity.Users;
import com.example.demo.payload.project.ChargerDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Date;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "doc")
public class Control {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String blankNum;                //blanka raqami
    private Date comeDate;                  //kelgan sana
    private Date controlPeriod;             //nazorat muddati
    private Boolean bControl = false;               //nazoratdami
    private Long outControllerId;           //kim komtrolni yechganligi
    private String otdName;                 //hajjat qayerdan kelgan(bo'linma nomi)
    private String regNum;                  //qayd raqami
    private Date docDate;                   //qayd raqami sanasi
    private String regNumCome;              //qayd raqami kelgan
    private Date regNumComeDate;            //qayd raqami kelgan sanasi
    @Column(length = 10000)
    private String docName;                 //hujjatning qisqacha mazmuni
    @ManyToOne
    private Users resPerson;                //rezolyutsiyalovchi shaxs
    private Date resDate;                   //rezolyutsiyalovchi shaxs sanasi
    @Column(length = 10000)
    private String resolution;              //resoyutsiyasi
    private String controllerPerson;        //nazoratchi shaxs
    private String tel;                     //nazoratchi telefon raqami
    private Date receptionDate;             //qabulxonaga berilgan sana
    private String executionRegNum;         //ijro bo'yicha qayd raqami
    private Date executionDate;             //ijro sanasi
    private String executorTel;             //ijrochining telefon raqami
    @Column(length = 10000)
    private String workDone;                //bajarilgan ishlar
    @ManyToOne
    private Users users;                    //o'zidan yuqorida tuiruvchi hodim
    private Boolean returned = false;       //qaytarildi
    private Boolean seen = false;           //ko'rildi

    private String workbookNum;             //ishjild raqami
    private String workbookPageNum;         //ishjilddagi list raqami
    @Column(length = 10000)
    private String reasonOutControl;        //nazoratdan yechish sababi

    private Date createdDate;
    private Date modifiedDate;

    private String returnReason;            //qaytarish sababi
}
