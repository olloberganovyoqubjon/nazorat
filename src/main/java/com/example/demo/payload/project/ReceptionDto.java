package com.example.demo.payload.project;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReceptionDto {
    @NotNull(message = "id bo'sh bo'lmasin!")
    private Long idControl;                        //hujjatning Id raqami
    @NotNull(message = "Qabulxonaga berilgan sana bo'sh bo'lmasin!")
    private Date reception_date;            //qabulxonaga berilgan sana
    @NotNull(message = "Ijro bo'yicha qayd raqami bo'sh bo'lmasin!")
    private String executionRegNum;         //ijro bo'yicha qayd raqami
    @NotNull(message = "Ijro sanasi bo'sh bo'lmasin!")
    private Date executionDate;             //ijro sanasi
    private String executorTel;             //ijrochining telefon raqami
    @NotNull(message = "Bajarilgan ishlar bo'sh bo'lmasin!")
    private String workDone;                //bajarilgan ishlar
    private String workbookNum;
    private String workbookPageNum;
}
