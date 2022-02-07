package com.makridin.instazoo.controllers;

import com.makridin.instazoo.dto.PostDTO;
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
import java.util.List;
import java.util.Map;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class PostControllerTest {

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
    public void testCreatePostUnautharized() {
        headers.add("Authorization", "token");

        ResponseEntity<PostDTO> response = createPost();

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testCreatePostAllFieldsNull() {
        headers.add("Authorization", getToken());
        PostDTO body = PostDTO.builder().build();
        HttpEntity<PostDTO> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/post/create"),
                HttpMethod.POST, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals(2, response.getBody().size());
        Assertions.assertEquals("Title is required", response.getBody().get("NotEmpty"));
        Assertions.assertEquals("Title is required", response.getBody().get("title"));
    }

    @Test
    public void testCreatePost() {
        headers.add("Authorization", getToken());
        PostDTO body = PostDTO.builder()
                .title("Title")
                .location("location")
                .caption("caption")
                .build();

        ResponseEntity<PostDTO> response = createPost(body);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getId());
        Assertions.assertEquals("Test", response.getBody().getUsername());
        Assertions.assertEquals(body.getTitle(), response.getBody().getTitle());
        Assertions.assertEquals(body.getCaption(), response.getBody().getCaption());
        Assertions.assertEquals(body.getLocation(), response.getBody().getLocation());
        Assertions.assertEquals(0, response.getBody().getUserLiked().size());
    }

    @Test
    public void testGetAllPostsNoPosts() {
        headers.add("Authorization", getToken());
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List> response = restTemplate.exchange(
                createURLWithPort("/post/all"),
                HttpMethod.GET, entity, List.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void testGetAllPostsUnauthorized() {
        headers.add("Authorization", "token");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/post/all"),
                HttpMethod.GET, entity, Map.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetAllPosts() {
        headers.add("Authorization", getToken());
        PostDTO body = PostDTO.builder()
                .title("Title")
                .location("location")
                .caption("caption")
                .build();
        createPost(body);
        createPost(body);

        createUser("Test1", "test1@test.io");
        headers.remove("Authorization");
        headers.add("Authorization", getUserToken("Test1", "test"));
        createPost();

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List> response = restTemplate.exchange(
                createURLWithPort("/post/all"),
                HttpMethod.GET, entity, List.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(3, response.getBody().size());
        Map<String, Object> one = (Map<String, Object>) response.getBody().get(0);
        Map<String, Object> two = (Map<String, Object>) response.getBody().get(1);
        Map<String, Object> three = (Map<String, Object>) response.getBody().get(2);
        Assertions.assertNotEquals(one.get("id"), two.get("id"));
        Assertions.assertNotEquals(one.get("id"), three.get("id"));
        Assertions.assertNotEquals(two.get("id"), three.get("id"));
        int nameTest = 0;
        int nameTest1 = 0;
        for (Map<String, Object> post : List.of(one, two, three)) {
            if("Test".equals(post.get("username"))) {
                nameTest++;
            } else if("Test1".equals(post.get("username"))) {
                nameTest1++;
            }
            Assertions.assertEquals(body.getTitle(), post.get("title"));
            Assertions.assertEquals(body.getCaption(), post.get("caption"));
            Assertions.assertEquals(body.getLocation(), post.get("location"));
            Assertions.assertEquals(0, ((List)post.get("userLiked")).size());
        }
        Assertions.assertEquals(2, nameTest);
        Assertions.assertEquals(1, nameTest1);
    }

    @Test
    public void testGetAllUserPostsNoPosts() {
        headers.add("Authorization", getToken());
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List> response = restTemplate.exchange(
                createURLWithPort("/post/user/posts"),
                HttpMethod.GET, entity, List.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void testGetAllUserPostsUnauthorized() {
        headers.add("Authorization", "token");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/post/user/posts"),
                HttpMethod.GET, entity, Map.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetAllUserPosts() {
        headers.add("Authorization", getToken());
        PostDTO body = PostDTO.builder()
                .title("Title")
                .location("location")
                .caption("caption")
                .build();
        createPost(body);
        createPost(body);

        createUser("Test1", "test1@test.io");
        headers.remove("Authorization");
        headers.add("Authorization", getUserToken("Test1", "test"));
        createPost();

        headers.remove("Authorization");
        headers.add("Authorization", getUserToken("Test", "test"));

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List> response = restTemplate.exchange(
                createURLWithPort("/post/user/posts"),
                HttpMethod.GET, entity, List.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(2, response.getBody().size());
        Map<String, Object> one = (Map<String, Object>) response.getBody().get(0);
        Map<String, Object> two = (Map<String, Object>) response.getBody().get(1);
        Assertions.assertNotEquals(one.get("id"), two.get("id"));
        for (Map<String, Object> post : List.of(one, two)) {
            Assertions.assertEquals("Test", post.get("username"));
            Assertions.assertEquals(body.getTitle(), post.get("title"));
            Assertions.assertEquals(body.getCaption(), post.get("caption"));
            Assertions.assertEquals(body.getLocation(), post.get("location"));
            Assertions.assertEquals(0, ((List)post.get("userLiked")).size());
        }
    }

    @Test
    public void testLikePostUnauthorized() {
        headers.add("Authorization", "token");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/post/" + 1 + "/like"),
                HttpMethod.PUT, entity, Map.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testLikePostWrongPostId() {
        headers.add("Authorization", getToken());
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/post/" + 1 + "/like"),
                HttpMethod.PUT, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testLikePost() {
        headers.add("Authorization", getToken());
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<PostDTO> postOne = createPost();
        ResponseEntity<PostDTO> postTwo = createPost();

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/post/" + postOne.getBody().getId() + "/like"),
                HttpMethod.PUT, entity, Map.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<List> userPosts = restTemplate.exchange(
                createURLWithPort("/post/user/posts"),
                HttpMethod.GET, entity, List.class);

        Assertions.assertEquals(2, userPosts.getBody().size());
        for (Object body : userPosts.getBody()) {
            Map<String, Object> post = (Map<String, Object>) body;
            if(postOne.getBody().getId().equals(post.get("id"))) {
                Assertions.assertEquals(1, ((List)post.get("userLiked")).size());
            } else if(postTwo.getBody().getId().equals(post.get("id"))) {
                Assertions.assertEquals(0, ((List)post.get("userLiked")).size());
            }
        }
    }

    @Test
    public void testLikePostUnlike() {
        headers.add("Authorization", getToken());
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<PostDTO> postOne = createPost();

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/post/" + postOne.getBody().getId() + "/like"),
                HttpMethod.PUT, entity, Map.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<List> userPosts = restTemplate.exchange(
                createURLWithPort("/post/user/posts"),
                HttpMethod.GET, entity, List.class);

        Map<String, Object> post = (Map<String, Object>)userPosts.getBody().get(0);
        Assertions.assertEquals(1, ((List)post.get("userLiked")).size());

        //UNLIKE
        response = restTemplate.exchange(
                createURLWithPort("/post/" + postOne.getBody().getId() + "/like"),
                HttpMethod.PUT, entity, Map.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        userPosts = restTemplate.exchange(
                createURLWithPort("/post/user/posts"),
                HttpMethod.GET, entity, List.class);

        post = (Map<String, Object>)userPosts.getBody().get(0);
        Assertions.assertEquals(0, ((List)post.get("userLiked")).size());
    }

    @Test
    public void testDeletePostUnauthorized() {
        headers.add("Authorization", "token");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/post/" + 1),
                HttpMethod.DELETE, entity, Map.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testDeletePostWrongPostId() {
        headers.add("Authorization", getToken());
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/post/" + 1),
                HttpMethod.DELETE, entity, Map.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeletePost() {
        headers.add("Authorization", getToken());
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<PostDTO> postOne = createPost();

        ResponseEntity<List> userPosts = restTemplate.exchange(
                createURLWithPort("/post/user/posts"),
                HttpMethod.GET, entity, List.class);

        Map<String, Object> post = (Map<String, Object>)userPosts.getBody().get(0);
        Assertions.assertEquals(postOne.getBody().getId(), ((Integer)post.get("id")).longValue());

        ResponseEntity<MessageResponse> response = restTemplate.exchange(
                createURLWithPort("/post/" + postOne.getBody().getId()),
                HttpMethod.DELETE, entity, MessageResponse.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Post was deleted", response.getBody().getMessage());

        userPosts = restTemplate.exchange(
                createURLWithPort("/post/user/posts"),
                HttpMethod.GET, entity, List.class);
        Assertions.assertTrue(userPosts.getBody().isEmpty());
    }

    @Test
    public void testDeletePostAnotherUsersPost() {
        headers.add("Authorization", getToken());
        createUser("Test1", "test1@test.io");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<PostDTO> postOne = createPost();

        ResponseEntity<List> userPosts = restTemplate.exchange(
                createURLWithPort("/post/user/posts"),
                HttpMethod.GET, entity, List.class);

        Map<String, Object> post = (Map<String, Object>)userPosts.getBody().get(0);
        Assertions.assertEquals(postOne.getBody().getId(), ((Integer)post.get("id")).longValue());

        headers.remove("Authorization");
        headers.add("Authorization", getUserToken("Test1", "test"));

        ResponseEntity<Map> response = restTemplate.exchange(
                createURLWithPort("/post/" + postOne.getBody().getId()),
                HttpMethod.DELETE, entity, Map.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());


        headers.remove("Authorization");
        headers.add("Authorization", getToken());
        userPosts = restTemplate.exchange(
                createURLWithPort("/post/user/posts"),
                HttpMethod.GET, entity, List.class);
        post = (Map<String, Object>)userPosts.getBody().get(0);
        Assertions.assertEquals(postOne.getBody().getId(), ((Integer)post.get("id")).longValue());
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

    private ResponseEntity<PostDTO> createPost() {
        PostDTO body = PostDTO.builder()
                .title("Title")
                .location("location")
                .caption("caption")
                .build();

        return createPost(body);
    }

    private ResponseEntity<PostDTO> createPost(PostDTO body ) {
        HttpEntity<PostDTO> entity = new HttpEntity<>(body, headers);

        return restTemplate.exchange(
                createURLWithPort("/post/create"),
                HttpMethod.POST, entity, PostDTO.class);
    }
}
