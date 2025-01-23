package com.example.demo.payload.project;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;


@Data
@AllArgsConstructor
public class ReturnControlDto {

    private Date receptionDate;             //qabulxonaga berilgan sana
    private String executionRegNum;         //ijro bo'yicha qayd raqami
    private Date executionDate;             //ijro sanasi
    private String executorTel;             //ijrochining telefon raqami
    private String workbookNum;             //hujjat tikilgan ishjild raqami
    private String workbookPageNum;         //ishjilddagi sahifa raqami
    private String workDone;                //bajarilgan ishlar

}
