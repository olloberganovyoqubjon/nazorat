package com.example.demo.payload.project;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChargerDto {

    private Long id;

    private Long userId;

    private String chargerName;

    private Boolean bold;

    private Integer sort;
}
