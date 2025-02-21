package com.example.user_profile.controllers;

import com.example.user_profile.dtos.UserDTO;
import com.example.user_profile.entities.User;
import com.example.user_profile.exceptions.ServiceException;
import com.example.user_profile.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Boolean> createUser(@RequestBody UserDTO userDTO) {
        try {
            boolean result = userService.createUser(userDTO);
            HttpStatus httpStatus = result ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(httpStatus).body(true);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUserById(@RequestParam(name = "id")Long id) {
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            return ResponseEntity.ok(existingUser);
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PutMapping
    public ResponseEntity<Boolean> updateUser(@RequestBody UserDTO userDTO) {
        try {
            boolean result = userService.updateUser(userDTO);
            HttpStatus httpStatus = result ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(httpStatus).body(true);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<Boolean> deleteUserById(@RequestParam(name = "id")Long id) {
        try {
            boolean result = userService.deleteUserById(id);
            HttpStatus httpStatus = result ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(httpStatus).body(true);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }
}
