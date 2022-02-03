package com.makridin.instazoo.controllers;

import com.makridin.instazoo.dto.CommentDTO;
import com.makridin.instazoo.dto.PostDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class CommentControllerTest {

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
    public void testCreateCommentNonAuthorized () {
        CommentDTO requestBody = CommentDTO.builder()
                .message("Hello world")
                .build();
        HttpEntity<CommentDTO> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/comment/1/create"),
                HttpMethod.POST, entity, Map.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testCreateCommentNoSuchPost () {
        String token = getToken();
        headers.add("Authorization", token);
        CommentDTO requestBody = CommentDTO.builder()
                .message("Hello world")
                .build();
        HttpEntity<CommentDTO> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/comment/1/create"),
                HttpMethod.POST, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCreateComment () {
        String token = getToken();
        headers.add("Authorization", token);
        long id = createPost();
        CommentDTO requestBody = CommentDTO.builder()
                .message("Hello world")
                .build();
        ResponseEntity<CommentDTO> response = createComment(id, requestBody);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(requestBody.getMessage(), response.getBody().getMessage());
        Assertions.assertNotNull(response.getBody().getId());
        Assertions.assertEquals("Test", response.getBody().getUsername());
    }

    @Test
    public void testCreateCommentWithoutMessage () {
        String token = getToken();
        headers.add("Authorization", token);
        long id = createPost();
        CommentDTO requestBody = CommentDTO.builder().build();
        HttpEntity<CommentDTO> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/comment/" + id + "/create"),
                HttpMethod.POST, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        Assertions.assertEquals(2, errors.size());
        Assertions.assertEquals("This field is required", errors.get("message"));
        Assertions.assertEquals("This field is required", errors.get("NotEmpty"));
    }

    @Test
    public void testGetAllPostCommentsWhenNoComments () {
        String token = getToken();
        headers.add("Authorization", token);
        long id = createPost();
        HttpEntity<CommentDTO> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List> response = restTemplate.exchange(
                createURLWithPort("/comment/" + id + "/comments"),
                HttpMethod.GET, entity, List.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        List<CommentDTO> dtos = response.getBody();
        Assertions.assertEquals(0, dtos.size());
    }

    @Test
    public void testGetAllPostComments () {
        String token = getToken();
        headers.add("Authorization", token);
        long id = createPost();
        ResponseEntity<CommentDTO> commentOne = createComment(id);
        ResponseEntity<CommentDTO> commentTwo = createComment(id);
        HttpEntity<CommentDTO> entity = new HttpEntity<>(null, headers);

        ResponseEntity<ArrayList> response = restTemplate.exchange(
                createURLWithPort("/comment/" + id + "/comments"),
                HttpMethod.GET, entity, ArrayList.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        ArrayList<Map<String, Object>> dtos = response.getBody();
        Assertions.assertEquals(2, dtos.size());
        Assertions.assertEquals(commentOne.getBody().getMessage(), dtos.get(0).get("message"));
        Assertions.assertEquals(commentTwo.getBody().getMessage(), dtos.get(1).get("message"));
        Assertions.assertNotNull(dtos.get(0).get("id"));
        Assertions.assertNotNull(dtos.get(1).get("id"));
        Assertions.assertNotEquals(dtos.get(0).get("id"), dtos.get(1).get("id"));
        Assertions.assertEquals(commentOne.getBody().getUsername(), dtos.get(0).get("username"));
        Assertions.assertEquals(commentTwo.getBody().getUsername(), dtos.get(1).get("username"));
    }


    @Test
    public void testDeleteComment () {
        String token = getToken();
        headers.add("Authorization", token);
        long id = createPost();
        ResponseEntity<CommentDTO> comment = createComment(id);
        HttpEntity<CommentDTO> entity = new HttpEntity<>(null, headers);

        ResponseEntity<ArrayList> responseComments = restTemplate.exchange(
                createURLWithPort("/comment/" + id + "/comments"),
                HttpMethod.GET, entity, ArrayList.class);
        Assertions.assertEquals(1, responseComments.getBody().size());

        ResponseEntity<MessageResponse> responseDelete = restTemplate.exchange(
                createURLWithPort("/comment/" + comment.getBody().getId()),
                HttpMethod.DELETE, entity, MessageResponse.class);

        Assertions.assertEquals(HttpStatus.OK, responseDelete.getStatusCode());
        Assertions.assertEquals("Comment was deleted", responseDelete.getBody().getMessage());

        responseComments = restTemplate.exchange(
                createURLWithPort("/comment/" + id + "/comments"),
                HttpMethod.GET, entity, ArrayList.class);
        Assertions.assertEquals(0, responseComments.getBody().size());
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
                createURLWithPort("/auth/signup"),
                HttpMethod.POST, entity, MessageResponse.class);
        return response;
    }

    private long createPost() {
        PostDTO body = PostDTO.builder()
                .title("Title")
                .location("location")
                .caption("caption")
                .build();

        HttpEntity<PostDTO> entity = new HttpEntity<>(body, headers);

        ResponseEntity<PostDTO> response = restTemplate.exchange(
                createURLWithPort("/post/create"),
                HttpMethod.POST, entity, PostDTO.class);

        return response.getBody().getId();
    }

    private ResponseEntity<CommentDTO> createComment(long id) {
        CommentDTO requestBody = CommentDTO.builder()
                .message("Hello world")
                .build();
        HttpEntity<CommentDTO> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CommentDTO> response = restTemplate.exchange(
                createURLWithPort("/comment/" + id + "/create"),
                HttpMethod.POST, entity, CommentDTO.class);
        return response;
    }

    private ResponseEntity<CommentDTO> createComment(long id, CommentDTO requestBody) {
        HttpEntity<CommentDTO> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CommentDTO> response = restTemplate.exchange(
                createURLWithPort("/comment/" + id + "/create"),
                HttpMethod.POST, entity, CommentDTO.class);
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
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/auth/signin"),
                HttpMethod.POST, entity, Map.class);

        return (String)response.getBody().get("token");
    }
}
