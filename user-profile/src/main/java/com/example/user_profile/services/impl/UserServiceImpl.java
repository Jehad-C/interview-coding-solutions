package com.example.user_profile.services.impl;

import com.example.user_profile.dtos.UserDTO;
import com.example.user_profile.entities.User;
import com.example.user_profile.exceptions.ServiceException;
import com.example.user_profile.repositories.UserRepository;
import com.example.user_profile.services.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public boolean createUser(UserDTO userDTO) throws ServiceException {
        try {
            User user = new User();
            copyUserDtoToUser(user, userDTO);
            userRepository.save(user);
            return true;
        } catch (ConstraintViolationException e) {
            throw new ServiceException("ConstraintViolationException");
        } catch (IllegalArgumentException e) {
            throw new ServiceException("IllegalArgumentException");
        } catch (OptimisticLockingFailureException e) {
            throw new ServiceException("OptimisticLockingFailureException");
        }
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public List<User> getUsersByName(String name, Integer page, Integer size) {
        if (page < 0) page = 0;
        if (size < 0) size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findByName(name, pageable);
        return userPage.getContent();
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public boolean updateUser(UserDTO userDTO) throws ServiceException {
        User existingUser = userRepository.findById(userDTO.getId()).orElse(null);
        if (existingUser == null) return false;

        try {
            copyUserDtoToUser(existingUser, userDTO);
            userRepository.save(existingUser);
            return true;
        } catch (ConstraintViolationException e) {
            throw new ServiceException("ConstraintViolationException");
        } catch (IllegalArgumentException e) {
            throw new ServiceException("IllegalArgumentException");
        } catch (OptimisticLockingFailureException e) {
            throw new ServiceException("OptimisticLockingFailureException");
        }
    }

    @Override
    @Transactional
    public boolean deleteUserById(Long id) throws ServiceException {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (IllegalArgumentException e) {
            throw new ServiceException("IllegalArgumentException");
        }
    }

    public static Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) return null;
        return Math.toIntExact(ChronoUnit.YEARS.between(birthDate, LocalDate.now()));
    }

    public static User copyUserDtoToUser(User user, UserDTO userDTO) {
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setGender(userDTO.getGender());
        user.setBirthDate(userDTO.getBirthDate());
        user.setAge(calculateAge(userDTO.getBirthDate()));
        user.setRole(userDTO.getRole());

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        if (!violations.isEmpty()) throw new ConstraintViolationException(violations);

        return user;
    }
}
