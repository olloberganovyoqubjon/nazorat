package com.example.demo.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllUserResponse {
    private String userName;
    private Boolean active;
}
