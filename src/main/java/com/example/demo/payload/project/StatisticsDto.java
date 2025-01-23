package com.example.demo.payload.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsDto {

    private Integer count1;         //o'zining nazoratlari soni
    private Integer count2;         //rahbarning nazoratlari soni
    private Integer count3;         //qo'l ostidagilarniong nazoratlari soni
    private String name;            //rahbar FIO
    private Date date;              //sana
    private String dateName;        //hafta kuni
    private Object data;
}
