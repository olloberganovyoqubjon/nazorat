package com.example.demo.payload.dto;

import jakarta.persistence.Table;
import lombok.*;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class AdminDto {

    private String firstName;

    private String lastName;

    private String username;

    private String password;

    private Boolean active = true;

    private Timestamp createTime = new Timestamp(System.currentTimeMillis());

    private Integer districtiD;
}
