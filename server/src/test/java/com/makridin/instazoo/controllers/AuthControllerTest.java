package com.makridin.instazoo.controllers;

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
public class AuthControllerTest {

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
    public void testSignIn () {
        setUp();
        createUser();
        String requestBody = "{\n" +
                "    \"username\": \"Test\",\n" +
                "    \"password\": \"test\"\n" +
                "}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/signin"),
                HttpMethod.POST, entity, Map.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        Assertions.assertNotNull(body.get("token"));
        Assertions.assertTrue(((String)body.get("token")).contains("Bearer "));
        Assertions.assertEquals(true, body.get("success"));
    }

    @Test
    public void testSignInWrongCredentials () {
        setUp();
        String body = "{\n" +
                "    \"username\": \"Test1\",\n" +
                "    \"password\": \"test\"\n" +
                "}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/signin"),
                HttpMethod.POST, entity, Map.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        Assertions.assertEquals("Invalid username", errors.get("username"));
        Assertions.assertEquals("Invalid password", errors.get("password"));
    }

    @Test
    public void testSignInEmptyUsername () {
        setUp();
        String body = "{\n" +
                "    \"password\": \"test\"\n" +
                "}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/signin"),
                HttpMethod.POST, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        Assertions.assertEquals(2, response.getBody().size());
        Assertions.assertEquals("User name cannot be empty", errors.get("username"));
        Assertions.assertEquals("User name cannot be empty", errors.get("NotEmpty"));
    }

    @Test
    public void testSignInEmptyPassword () {
        setUp();
        String body = "{\n" +
                "    \"username\": \"test\"\n" +
                "}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/signin"),
                HttpMethod.POST, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        Assertions.assertEquals(2, response.getBody().size());
        Assertions.assertEquals("Password cannot be empty", errors.get("password"));
        Assertions.assertEquals("Password cannot be empty", errors.get("NotEmpty"));
    }

    @Test
    public void testSignUp () {
        setUp();
        ResponseEntity<MessageResponse> response = createUser();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("User was registered successfully", response.getBody().getMessage());
    }

    @Test
    public void testSignUpWrongPassword () {
        setUp();
        String body = "{\n" +
                "    \"email\": \"test@test.io\",\n" +
                "    \"firstname\": \"Test\",\n" +
                "    \"lastname\": \"Test\",\n" +
                "    \"username\": \"Test\",\n" +
                "    \"password\": \"test\",\n" +
                "    \"confirmPassword\": \"test1\"\n" +
                "}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/signup"),
                HttpMethod.POST, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().size());
        Assertions.assertEquals("Passwords don't match", response.getBody().get("PasswordMatches"));
    }

    @Test
    public void testSignUpWrongEmail () {
        setUp();
        String body = "{\n" +
                "    \"email\": \"testtest.io\",\n" +
                "    \"firstname\": \"Test\",\n" +
                "    \"lastname\": \"Test\",\n" +
                "    \"username\": \"Test\",\n" +
                "    \"password\": \"test\",\n" +
                "    \"confirmPassword\": \"test\"\n" +
                "}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/signup"),
                HttpMethod.POST, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(3, response.getBody().size());
        Assertions.assertEquals("It should have email format", response.getBody().get("Email"));
    }

    @Test
    public void testSignUpWrongFirstName() {
        setUp();
        String body = "{\n" +
                "    \"email\": \"test@test.io\",\n" +
                "    \"lastname\": \"Test\",\n" +
                "    \"username\": \"Test\",\n" +
                "    \"password\": \"test\",\n" +
                "    \"confirmPassword\": \"test\"\n" +
                "}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/signup"),
                HttpMethod.POST, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(2, response.getBody().size());
        Assertions.assertEquals("Please enter your name", response.getBody().get("firstname"));
    }

    @Test
    public void testSignUpWrongLastName() {
        setUp();
        String body = "{\n" +
                "    \"email\": \"test@test.io\",\n" +
                "    \"firstname\": \"Test\",\n" +
                "    \"username\": \"Test\",\n" +
                "    \"password\": \"test\",\n" +
                "    \"confirmPassword\": \"test\"\n" +
                "}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/signup"),
                HttpMethod.POST, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(2, response.getBody().size());
        Assertions.assertEquals("Please enter your lastname", response.getBody().get("lastname"));
    }

    @Test
    public void testSignUpWrongUsername() {
        setUp();
        String body = "{\n" +
                "    \"email\": \"test@test.io\",\n" +
                "    \"firstname\": \"Test\",\n" +
                "    \"lastname\": \"Test\",\n" +
                "    \"password\": \"test\",\n" +
                "    \"confirmPassword\": \"test\"\n" +
                "}";

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/signup"),
                HttpMethod.POST, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(2, response.getBody().size());
        Assertions.assertEquals("Please enter your username", response.getBody().get("username"));
    }

    @Test
    public void testSignUpEmptyPassword() {
        setUp();
        String body = "{\n" +
                "    \"email\": \"test@test.io\",\n" +
                "    \"firstname\": \"Test\",\n" +
                "    \"lastname\": \"Test\",\n" +
                "    \"username\": \"Test\",\n" +
                "    \"confirmPassword\": \"test\"\n" +
                "}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/signup"),
                HttpMethod.POST, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(3, response.getBody().size());
        Assertions.assertEquals("Please enter your password ", response.getBody().get("password"));
    }

    @Test
    public void testSignUpEmptyConfirmPassword() {
        setUp();
        String body = "{\n" +
                "    \"email\": \"test@test.io\",\n" +
                "    \"firstname\": \"Test\",\n" +
                "    \"lastname\": \"Test\",\n" +
                "    \"username\": \"Test\",\n" +
                "    \"password\": \"test\"\n" +
                "}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/signup"),
                HttpMethod.POST, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().size());
        Assertions.assertEquals("Passwords don't match", response.getBody().get("PasswordMatches"));
    }

    @Test
    public void testSignUpUniqueEmail() {
        setUp();
        createUser();
        String body = "{\n" +
                "    \"email\": \"test@test.io\",\n" +
                "    \"firstname\": \"Test\",\n" +
                "    \"lastname\": \"Test\",\n" +
                "    \"username\": \"TestNew\",\n" +
                "    \"password\": \"test\",\n" +
                "    \"confirmPassword\": \"test\"\n" +
                "}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/signup"),
                HttpMethod.POST, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        System.out.println("THIS IS RESPONSE " + response.getBody());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(2, response.getBody().size());
        Assertions.assertEquals("This email is already used", response.getBody().get("UniqueEmail"));
        Assertions.assertEquals("This email is already used", response.getBody().get("email"));
    }

    @Test
    public void testSignUpUniqueUserName() {
        setUp();
        createUser();
        String body = "{\n" +
                "    \"email\": \"test1@test.io\",\n" +
                "    \"firstname\": \"Test\",\n" +
                "    \"lastname\": \"Test\",\n" +
                "    \"username\": \"Test\",\n" +
                "    \"password\": \"test\",\n" +
                "    \"confirmPassword\": \"test\"\n" +
                "}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/signup"),
                HttpMethod.POST, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        System.out.println("THIS IS RESPONSE " + response.getBody());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(2, response.getBody().size());
        Assertions.assertEquals("This username is already used", response.getBody().get("UniqueUsername"));
        Assertions.assertEquals("This username is already used", response.getBody().get("username"));
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + "/api/auth" + uri;
    }

    private ResponseEntity<MessageResponse> createUser() {
        String body = "{\n" +
                "    \"email\": \"test@test.io\",\n" +
                "    \"firstname\": \"Test\",\n" +
                "    \"lastname\": \"Test\",\n" +
                "    \"username\": \"Test\",\n" +
                "    \"password\": \"test\",\n" +
                "    \"confirmPassword\": \"test\"\n" +
                "}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<MessageResponse> response = restTemplate.exchange(
                createURLWithPort("/signup"),
                HttpMethod.POST, entity, MessageResponse.class);
        return response;
    }
}
