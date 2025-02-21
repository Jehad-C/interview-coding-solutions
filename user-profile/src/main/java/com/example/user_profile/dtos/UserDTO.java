package com.example.user_profile.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String gender;
    private LocalDate birthDate;
    private String role;
}
