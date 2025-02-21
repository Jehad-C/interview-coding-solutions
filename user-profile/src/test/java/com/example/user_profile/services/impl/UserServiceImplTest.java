package com.example.user_profile.services.impl;

import com.example.user_profile.dtos.UserDTO;
import com.example.user_profile.entities.User;
import com.example.user_profile.exceptions.ServiceException;
import com.example.user_profile.repositories.UserRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

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
    public void testConstraintViolation() {
        UserDTO userDTO = new UserDTO();
        ServiceException thrown = assertThrows(ServiceException.class, () -> {
            userService.createUser(userDTO);
        });

        assertEquals(thrown.getMessage(), ConstraintViolationException.class.getSimpleName());
    }

    @Test
    public void testIllegalArgument() {
        UserDTO userDTO = createUserDTO(null);

        when(userRepository.save(any(User.class)))
                .thenThrow(new IllegalArgumentException("Illegal argument"));

        ServiceException thrown = assertThrows(ServiceException.class, () -> {
            userService.createUser(userDTO);
        });

        assertEquals(thrown.getMessage(), IllegalArgumentException.class.getSimpleName());
    }

    @Test
    public void testOptimisticLockingFailure() {
        UserDTO userDTO = createUserDTO(null);

        when(userRepository.save(any(User.class)))
                .thenThrow(new OptimisticLockingFailureException("Optimistic locking failure"));

        ServiceException thrown = assertThrows(ServiceException.class, () -> {
            userService.createUser(userDTO);
        });

        assertEquals(thrown.getMessage(), OptimisticLockingFailureException.class.getSimpleName());
    }

    @Test
    public void testCreateUser() throws ServiceException {
        User user = new User();
        UserDTO userDTO = createUserDTO(null);

        UserServiceImpl.copyUserDtoToUser(user, userDTO);
        when(userRepository.save(any(User.class))).thenReturn(user);

        boolean result = userService.createUser(userDTO);

        assertTrue(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateUser() throws ServiceException {
        Long userId = 1L;
        User user = new User();
        UserDTO userDTO = createUserDTO(userId);

        UserServiceImpl.copyUserDtoToUser(user, userDTO);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        userDTO.setName("Updated Name");
        UserServiceImpl.copyUserDtoToUser(user, userDTO);

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        boolean result = userService.updateUser(userDTO);
        User existingUser = userService.getUserById(userId);

        assertTrue(result);
        assertEquals(userDTO.getName(), existingUser.getName());
        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(2)).findById(userId);
    }

    @Test
    public void testGetUserById() {
        Long userId = 1L;
        User user = new User();
        UserDTO userDTO = createUserDTO(userId);

        UserServiceImpl.copyUserDtoToUser(user, userDTO);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        User result = userService.getUserById(userId);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testUserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        User result = userService.getUserById(userId);

        assertNull(result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testDeleteUserById() {
        Long userId = 1L;
        User user = new User();
        UserDTO userDTO = createUserDTO(userId);

        UserServiceImpl.copyUserDtoToUser(user, userDTO);
        assertDoesNotThrow(() -> userRepository.deleteById(userId));
        verify(userRepository, times(1)).deleteById(userId);
    }
}
