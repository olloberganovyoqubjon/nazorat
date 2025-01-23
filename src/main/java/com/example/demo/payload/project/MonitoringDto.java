package com.example.demo.payload.project;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonitoringDto {

    private Long ControlId;

    private String regNum;
}
