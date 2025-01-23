package com.example.demo.payload;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String name;
    private String lastname;
    private String surname;
    private List<String> roles;
}