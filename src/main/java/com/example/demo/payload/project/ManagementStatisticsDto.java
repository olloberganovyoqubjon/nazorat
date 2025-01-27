package com.example.demo.payload.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagementStatisticsDto {

    private String name;
    private Integer notControlled;
    private Integer returnedControl;
    private Integer threeDaysMore;
    private Integer twoDaysLess;
    private Integer Late;
}
