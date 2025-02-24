package com.example.user_profile.repositories;

import com.example.user_profile.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void clearDatabase() {
        userRepository.deleteAll();
    }

    private User createUser(Long userId) {
        User user = new User();
        if (userId != null) user.setId(userId);
        user.setName("Firstname Lastname");
        user.setEmail("fullname@test.com");
        user.setGender("male");
        user.setBirthDate(LocalDate.of(2005, 1, 1));
        user.setAge(20);
        user.setRole("user");
        return user;
    }

    @Test
    public void testFindUserByEmail() {
        User user = createUser(null);
        userRepository.save(user);
        Optional<User> result = userRepository.findByEmail(user.getEmail());
        assertTrue(result.isPresent());
    }

    @Test
    public void testFindUsersByName() {
        String username = "Firstname Lastname";
        for (int i = 1; i <= 5; i++) {
            User user = createUser(null);
            String[] email = user.getEmail().split("@");
            user.setEmail(email[0] + i + "@" + email[1]);
            if (i == 1) user.setRole("admin");
            userRepository.save(user);
        }

        Pageable pageable = PageRequest.of(0, 3);
        Page<User> result = userRepository.findByName(username, pageable);
        assertEquals(3, result.getContent().size());
        assertEquals(2, result.getTotalPages());
        assertEquals(5, result.getTotalElements());
    }
}
