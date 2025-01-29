package com.example.demo.payload.project;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OutControlDto {
    private Long idControl;
    private String returnReason;        //nazoratdan yechish sababi
}
