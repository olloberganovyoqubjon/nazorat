package com.example.demo.payload.project;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsTable {
    private String name;
    private Integer allCountControls;
    private Integer nowCountControls;
    private Integer returnedCountControls;
    private Integer notReturnedCountControls;
    private Integer notCountControls;
}
