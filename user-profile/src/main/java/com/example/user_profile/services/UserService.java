package com.example.user_profile.services;

import com.example.user_profile.dtos.UserDTO;
import com.example.user_profile.entities.User;
import com.example.user_profile.exceptions.ServiceException;

import java.util.List;

public interface UserService {
    boolean createUser(UserDTO userDTO) throws ServiceException;
    User getUserById(Long id);
    List<User> getUsers();
    boolean updateUser(UserDTO userDTO) throws ServiceException;
    boolean deleteUserById(Long id) throws ServiceException;
}
