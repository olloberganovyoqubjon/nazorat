package com.example.demo.payload.project;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AllUserResponse {

    private Long id;

    private String userName;

    private String fio;

    private String boshlogi;
}
