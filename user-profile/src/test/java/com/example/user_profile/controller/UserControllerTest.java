package com.example.user_profile.controller;

import com.example.user_profile.controllers.UserController;
import com.example.user_profile.dtos.UserDTO;
import com.example.user_profile.entities.User;
import com.example.user_profile.services.UserService;
import com.example.user_profile.services.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO createUserDTO(Long userId) {
        UserDTO userDTO = new UserDTO();
        if (userId != null) userDTO.setId(userId);
        userDTO.setName("Firstname Lastname");
        userDTO.setEmail("fullname@test.com");
        userDTO.setGender("male");
        userDTO.setBirthDate(LocalDate.of(2005, 1, 1));
        userDTO.setRole("user");
        return userDTO;
    }

    @Test
    public void testCreateUser() throws Exception {
        UserDTO userDTO = createUserDTO(null);

        when(userService.createUser(any(UserDTO.class))).thenReturn(true);

        mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetUserById() throws Exception {
        Long userId = 1L;
        User user = new User();
        UserDTO userDTO = createUserDTO(userId);
        UserServiceImpl.copyUserDtoToUser(user, userDTO);

        when(userService.getUserById(any(Long.class))).thenReturn(user);

        mockMvc.perform(get("/api/users/user?id=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Firstname Lastname"));
    }

    @Test
    public void testGetUsers() throws Exception {
        when(userService.getUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserDTO userDTO = createUserDTO(null);

        when(userService.updateUser(any(UserDTO.class))).thenReturn(true);

        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteUser() throws Exception {
        when(userService.deleteUserById(any(Long.class))).thenReturn(true);

        mockMvc.perform(delete("/api/users/user?id=1"))
                .andExpect(status().isOk());
    }
}
