package com.makridin.instazoo.controllers;

import com.makridin.instazoo.dto.UserDTO;
import com.makridin.instazoo.payload.request.LoginRequest;
import com.makridin.instazoo.payload.request.SignupRequest;
import com.makridin.instazoo.payload.response.MessageResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.Map;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserControllerTest {

    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    @BeforeEach
    private void setUp() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    public void testGetCurrentUserUnauthorized() {
        headers.add("Authorization", "token");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/user/"),
                HttpMethod.GET, entity, Map.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetCurrentUser() {
        headers.add("Authorization", getToken());
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<UserDTO> response = restTemplate.exchange(
                createURLWithPort("/user/"),
                HttpMethod.GET, entity, UserDTO.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getId());
        Assertions.assertNull(response.getBody().getEmail());
        Assertions.assertNull(response.getBody().getBio());
        Assertions.assertEquals("Test", response.getBody().getUsername());
        Assertions.assertEquals("Test", response.getBody().getFirstname());
        Assertions.assertEquals("Test", response.getBody().getLastname());
    }

    @Test
    public void testGetUserByIdUserUnauthorized() {
        headers.add("Authorization", "token");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/user/" + 1),
                HttpMethod.GET, entity, Map.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetUserByIdNoUser() {
        headers.add("Authorization", getToken());
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/user/" + 222),
                HttpMethod.GET, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testGetUserById() {
        headers.add("Authorization", getToken());
        createUser("Test1", "test1@test.io");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<UserDTO> response = restTemplate.exchange(
                createURLWithPort("/user/" + 2),
                HttpMethod.GET, entity, UserDTO.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(2L, response.getBody().getId());
        Assertions.assertNull(response.getBody().getEmail());
        Assertions.assertNull(response.getBody().getBio());
        Assertions.assertEquals("Test1", response.getBody().getUsername());
        Assertions.assertEquals("Test", response.getBody().getFirstname());
        Assertions.assertEquals("Test", response.getBody().getLastname());
    }

    @Test
    public void testUpdateUserUnauthorized() {
        headers.add("Authorization", "token");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/user/update"),
                HttpMethod.PUT, entity, Map.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testUpdateUserNullBody() {
        headers.add("Authorization", getToken());
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/user/update"),
                HttpMethod.PUT, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Bad Request", response.getBody().get("error"));
    }

    @Test
    public void testUpdateUserWithoutUsername() {
        headers.add("Authorization", getToken());
        UserDTO body = UserDTO.builder()
                .lastname("Test")
                .firstname("Test")
                .email("test@test.io")
                .build();
        HttpEntity<UserDTO> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/user/update"),
                HttpMethod.PUT, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals(2, response.getBody().size());
        Assertions.assertEquals("Username is required", response.getBody().get("NotEmpty"));
        Assertions.assertEquals("Username is required", response.getBody().get("username"));
    }

    @Test
    public void testUpdateUserWithoutFirstName() {
        headers.add("Authorization", getToken());
        UserDTO body = UserDTO.builder()
                .lastname("Test")
                .username("Test")
                .email("test@test.io")
                .build();
        HttpEntity<UserDTO> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/user/update"),
                HttpMethod.PUT, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals(2, response.getBody().size());
        Assertions.assertEquals("Firstname is required", response.getBody().get("NotEmpty"));
        Assertions.assertEquals("Firstname is required", response.getBody().get("firstname"));
    }

    @Test
    public void testUpdateUserWithoutLastName() {
        headers.add("Authorization", getToken());
        UserDTO body = UserDTO.builder()
                .username("Test")
                .firstname("Test")
                .email("test@test.io")
                .build();
        HttpEntity<UserDTO> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/user/update"),
                HttpMethod.PUT, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals(2, response.getBody().size());
        Assertions.assertEquals("Lastname is required", response.getBody().get("NotEmpty"));
        Assertions.assertEquals("Lastname is required", response.getBody().get("lastname"));
    }

    @Test
    public void testUpdateUser() {
        headers.add("Authorization", getToken());
        UserDTO body = UserDTO.builder()
                .username("Test2")
                .firstname("Test2")
                .lastname("Test2")
                .bio("bio")
                .email("test123@test.io")
                .id(123L)
                .build();
        HttpEntity<UserDTO> entity = new HttpEntity<>(body, headers);

        ResponseEntity<UserDTO> response = restTemplate.exchange(
                createURLWithPort("/user/update"),
                HttpMethod.PUT, entity, UserDTO.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1L, response.getBody().getId());
        Assertions.assertNull(response.getBody().getEmail());
        Assertions.assertEquals("bio", response.getBody().getBio());
        Assertions.assertEquals("Test", response.getBody().getUsername());
        Assertions.assertEquals("Test2", response.getBody().getFirstname());
        Assertions.assertEquals("Test2", response.getBody().getLastname());
    }


    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + "/api" + uri;
    }

    private String getToken () {
        createUser();
        return getUserToken("Test", "test");
    }

    private String getUserToken(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setPassword(password);
        request.setUsername(username);
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/auth/signin"),
                HttpMethod.POST, entity, Map.class);

        return (String)response.getBody().get("token");
    }

    private ResponseEntity<MessageResponse> createUser() {
        return createUser("Test", "test@test.io");
    }

    private ResponseEntity<MessageResponse> createUser(String username, String email) {
        SignupRequest request = SignupRequest.builder()
                .email(email)
                .firstname("Test")
                .lastname("Test")
                .username(username)
                .password("test")
                .confirmPassword("test")
                .build();
        HttpEntity<SignupRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<MessageResponse> response = restTemplate.exchange(
                createURLWithPort("/auth/signup"),
                HttpMethod.POST, entity, MessageResponse.class);
        return response;
    }
}
