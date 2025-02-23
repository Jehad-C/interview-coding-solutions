package com.example.user_profile.integrations;

import com.example.user_profile.UserProfileApplication;
import com.example.user_profile.dtos.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(
        classes = UserProfileApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserIntegrationTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    private UserDTO createUserDTO(Long userId, String name) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setName(name == null ? "Firstname Lastname" : name);
        userDTO.setEmail("fullname@test.com");
        userDTO.setGender("male");
        userDTO.setBirthDate(LocalDate.of(2005, 1, 1));
        userDTO.setRole("user");
        return userDTO;
    }

    private HttpEntity<UserDTO> createHttpEntity(UserDTO userDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(userDTO, headers);
    }

    private ResponseEntity<Void> createUser(UserDTO userDTO) {
        return testRestTemplate.postForEntity(
                "/api/users",
                createHttpEntity(userDTO),
                Void.class
        );
    }

    private ResponseEntity<UserDTO> getUserByEmail(String email) {
        return testRestTemplate.getForEntity(
                "/api/users/user?email=" + email,
                UserDTO.class
        );
    }

    private ResponseEntity<Void> updateUser(UserDTO userDTO) {
        return testRestTemplate.exchange(
                "/api/users",
                HttpMethod.PUT,
                createHttpEntity(userDTO),
                Void.class
        );
    }

    private ResponseEntity<Void> deleteUser(Long id) {
        return testRestTemplate.exchange(
                "/api/users/user?id=" + id,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    @Test
    public void testCreateUser() {
        UserDTO userDTO = createUserDTO(null, null);
        ResponseEntity<Void> result = createUser(userDTO);
        assertTrue(result.getStatusCode().isSameCodeAs(HttpStatus.CREATED));
    }

    @Test
    public void testGetUser() {
        UserDTO userDTO = createUserDTO(null, null);
        ResponseEntity<Void> createUserResult = createUser(userDTO);
        assertTrue(createUserResult.getStatusCode().isSameCodeAs(HttpStatus.CREATED));

        ResponseEntity<UserDTO> getUserByEmailResult = getUserByEmail(userDTO.getEmail());
        assertTrue(getUserByEmailResult.getStatusCode().isSameCodeAs(HttpStatus.OK));

        UserDTO existingUserDTO = getUserByEmailResult.getBody();
        assertEquals(userDTO.getEmail(), existingUserDTO.getEmail());
    }

    @Test
    public void testUpdateUser() {
        UserDTO userDTO = createUserDTO(null, null);
        ResponseEntity<Void> createUserResult = createUser(userDTO);
        assertTrue(createUserResult.getStatusCode().isSameCodeAs(HttpStatus.CREATED));

        ResponseEntity<UserDTO> getUserByEmailResult = getUserByEmail(userDTO.getEmail());
        assertTrue(getUserByEmailResult.getStatusCode().isSameCodeAs(HttpStatus.OK));

        UserDTO existingUserDTO = getUserByEmailResult.getBody();
        assertEquals(userDTO.getEmail(), existingUserDTO.getEmail());

        String updatedName = "Updated Name";
        existingUserDTO.setName(updatedName);
        ResponseEntity<Void> updateUserResult = updateUser(existingUserDTO);
        assertTrue(updateUserResult.getStatusCode().isSameCodeAs(HttpStatus.OK));

        getUserByEmailResult = getUserByEmail(userDTO.getEmail());
        assertTrue(getUserByEmailResult.getStatusCode().isSameCodeAs(HttpStatus.OK));

        UserDTO updatedUserDTO = getUserByEmailResult.getBody();
        assertEquals(updatedName, updatedUserDTO.getName());
    }

    @Test
    public void testDeleteUser() {
        UserDTO userDTO = createUserDTO(null, null);
        ResponseEntity<Void> createUserResult = createUser(userDTO);
        assertTrue(createUserResult.getStatusCode().isSameCodeAs(HttpStatus.CREATED));

        ResponseEntity<UserDTO> getUserByEmailResult = getUserByEmail(userDTO.getEmail());
        assertTrue(getUserByEmailResult.getStatusCode().isSameCodeAs(HttpStatus.OK));

        UserDTO existingUserDTO = getUserByEmailResult.getBody();
        assertEquals(userDTO.getEmail(), existingUserDTO.getEmail());

        ResponseEntity<Void> updateUserResult = deleteUser(existingUserDTO.getId());
        assertTrue(updateUserResult.getStatusCode().isSameCodeAs(HttpStatus.OK));

        getUserByEmailResult = getUserByEmail(userDTO.getEmail());
        assertTrue(getUserByEmailResult.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND));
    }
}
