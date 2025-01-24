package com.example.demo.payload.project;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsTable {
    private String name;
    private Integer allCountControls;
    private Integer allReturnedAndNotReturnedControls;
    private Integer nowControls;
    private Integer returnedControls;
    private Integer notControls;
}
