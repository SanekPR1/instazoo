package com.makridin.instazoo.controllers;

import com.makridin.instazoo.dto.PostDTO;
import com.makridin.instazoo.entity.ImageModel;
import com.makridin.instazoo.payload.response.MessageResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.transaction.Transactional;
import java.util.Map;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ImageControllerTest {
    private static final String PHOTO_PATH = "src/test/resources/image/flowers.jpg";

    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();
    HttpHeaders userHeaders = new HttpHeaders();

    @BeforeEach
    private void setUp() {
        headers = new HttpHeaders();
        userHeaders = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        userHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    public void testUploadUserImage() {
        ResponseEntity<MessageResponse> response = uploadProfilePhoto(PHOTO_PATH);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Image was uploaded successfully", response.getBody().getMessage());
    }

    @Test
    public void testUploadUserImageUnauthorized() {
        headers.add("Authorization", "");

        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("file", new FileSystemResource("src/test/resources/image/flowers.jpg"));

        HttpEntity<MultiValueMap> entity = new HttpEntity<>(parameters, headers);

        ResponseEntity<MessageResponse> response = restTemplate.exchange(
                createURLWithPort("/image/upload"),
                HttpMethod.POST, entity, MessageResponse.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testUploadPostImage() {
        String token = getToken();
        headers.add("Authorization", token);
        userHeaders.add("Authorization", token);
        long postId = createPost();

        ResponseEntity<MessageResponse> response = uploadPostPhoto(postId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Image was uploaded successfully", response.getBody().getMessage());
    }

    @Test
    public void testUploadPostImageWrongPostId() {
        String token = getToken();
        headers.add("Authorization", token);
        ResponseEntity<MessageResponse> response = uploadPostPhoto(222L);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUploadPostImageUnauthorized() {
        String token = getToken();
        userHeaders.add("Authorization", token);

        long postId = createPost();

        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("file", new FileSystemResource("src/test/resources/image/flowers.jpg"));

        HttpEntity<MultiValueMap> entity = new HttpEntity<>(parameters, headers);

        ResponseEntity<MessageResponse> response = restTemplate.exchange(
                createURLWithPort("/image/" + postId + "/upload"),
                HttpMethod.POST, entity, MessageResponse.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetProfileImageUnauthorized() {
        headers.add("Authorization", "token");

        HttpEntity<MultiValueMap> entity = new HttpEntity<>(null, headers);

        ResponseEntity<MessageResponse> response = restTemplate.exchange(
                createURLWithPort("/image/profile"),
                HttpMethod.GET, entity, MessageResponse.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetProfileImageNoImage() {
        String token = getToken();
        headers.add("Authorization", token);

        HttpEntity<MultiValueMap> entity = new HttpEntity<>(null, headers);

        ResponseEntity<MessageResponse> response = restTemplate.exchange(
                createURLWithPort("/image/profile"),
                HttpMethod.GET, entity, MessageResponse.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    public void testGetProfileImage() {
        uploadProfilePhoto(PHOTO_PATH);

        HttpEntity<MultiValueMap> entity = new HttpEntity<>(null, headers);

        ResponseEntity<ImageModel> response = restTemplate.exchange(
                createURLWithPort("/image/profile"),
                HttpMethod.GET, entity, ImageModel.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        ImageModel model = response.getBody();
        Assertions.assertNotNull(model);
        Assertions.assertNotNull(model.getUserId());
        Assertions.assertNull(model.getPostId());
        Assertions.assertEquals("flowers.jpg", model.getName());
        Assertions.assertNotNull(model.getImageBytes());
    }

    @Test
    public void testGetPostImageUnauthorized() {
        headers.add("Authorization", "token");

        HttpEntity<MultiValueMap> entity = new HttpEntity<>(null, headers);

        ResponseEntity<MessageResponse> response = restTemplate.exchange(
                createURLWithPort("/image/" + 1 + "/image"),
                HttpMethod.GET, entity, MessageResponse.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetPostImageNoImage() {
        String token = getToken();
        headers.add("Authorization", token);
        userHeaders.add("Authorization", token);
        long postId = createPost();

        HttpEntity<MultiValueMap> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/image/" + postId + "/image"),
                HttpMethod.GET, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testGetPostImageNoPost() {
        String token = getToken();
        headers.add("Authorization", token);
        userHeaders.add("Authorization", token);

        HttpEntity<MultiValueMap> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/image/" + 222 + "/image"),
                HttpMethod.GET, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testGetPostImage() {
        String token = getToken();
        headers.add("Authorization", token);
        userHeaders.add("Authorization", token);
        long postId = createPost();
        uploadPostPhoto(postId);

        HttpEntity<MultiValueMap> entity = new HttpEntity<>(null, headers);

        ResponseEntity<ImageModel> response = restTemplate.exchange(
                createURLWithPort("/image/" + postId + "/image"),
                HttpMethod.GET, entity, ImageModel.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        ImageModel model = response.getBody();
        Assertions.assertNotNull(model);
        Assertions.assertNotNull(model.getPostId());
        Assertions.assertNull(model.getUserId());
        Assertions.assertEquals("flowers.jpg", model.getName());
        Assertions.assertNotNull(model.getImageBytes());
    }

    private ResponseEntity<MessageResponse> uploadProfilePhoto(String path) {
        String token = getToken();
        headers.add("Authorization", token);

        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("file", new FileSystemResource(path));

        HttpEntity<MultiValueMap> entity = new HttpEntity<>(parameters, headers);

        ResponseEntity<MessageResponse> response = restTemplate.exchange(
                createURLWithPort("/image/upload"),
                HttpMethod.POST, entity, MessageResponse.class);
        return response;
    }

    private ResponseEntity<MessageResponse> uploadPostPhoto(long postId) {

        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("file", new FileSystemResource("src/test/resources/image/flowers.jpg"));

        HttpEntity<MultiValueMap> entity = new HttpEntity<>(parameters, headers);

        ResponseEntity<MessageResponse> response = restTemplate.exchange(
                createURLWithPort("/image/" + postId + "/upload"),
                HttpMethod.POST, entity, MessageResponse.class);
        return response;
    }

    private long createPost() {
        PostDTO body = PostDTO.builder()
                .title("Title")
                .location("location")
                .caption("caption")
                .build();

        HttpEntity<PostDTO> entity = new HttpEntity<>(body, userHeaders);

        ResponseEntity<PostDTO> response = restTemplate.exchange(
                createURLWithPort("/post/create"),
                HttpMethod.POST, entity, PostDTO.class);

        return response.getBody().getId();
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
        HttpEntity<String> entity = new HttpEntity<>(body, userHeaders);

        ResponseEntity<MessageResponse> response = restTemplate.exchange(
                createURLWithPort("/auth/signup"),
                HttpMethod.POST, entity, MessageResponse.class);
        return response;
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + "/api" + uri;
    }

    private String getToken () {
        createUser();
        String requestBody = "{\n" +
                "    \"username\": \"Test\",\n" +
                "    \"password\": \"test\"\n" +
                "}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, userHeaders);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/auth/signin"),
                HttpMethod.POST, entity, Map.class);

        return (String)response.getBody().get("token");
    }
}
