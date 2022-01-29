package com.makridin.instazoo.service;

import com.makridin.instazoo.dto.PostDTO;
import com.makridin.instazoo.entity.ImageModel;
import com.makridin.instazoo.entity.Post;
import com.makridin.instazoo.entity.User;
import com.makridin.instazoo.exceptions.PostNotFoundException;
import com.makridin.instazoo.repository.ImageRepository;
import com.makridin.instazoo.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
public class PostServiceTest {

    @InjectMocks
    private PostService service;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserService userService;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private Principal principal;
    private User user = getUser();

    @BeforeEach
    private void setUp() {
        when(userService.getUserByPrincipal(principal)).thenReturn(user);
        when(postRepository.save(any())).thenReturn(getPost());
    }

    @Test
    public void testCreatePost() {
        PostDTO expected = getPostDto();
        User expectedUser = getUser();

        PostDTO dto = service.createPost(getPostDto(), principal);

        verify(userService, times(1)).getUserByPrincipal(principal);
        verify(postRepository, times(1)).save(any());
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(1L, dto.getId());
        Assertions.assertEquals(expected.getCaption(), dto.getCaption());
        Assertions.assertEquals(expected.getTitle(), dto.getTitle());
        Assertions.assertEquals(expected.getLocation(), dto.getLocation());
        Assertions.assertEquals(expectedUser.getUsername(), dto.getUsername());
        Assertions.assertEquals(0, dto.getUserLiked().size());
    }

    @Test
    public void testGetAllPosts() {
        when(postRepository.findAllByOrderByCreatedDateDesc()).thenReturn(List.of(getPost()));

        List<PostDTO> dto = service.getAllPosts();

        verify(postRepository, times(1)).findAllByOrderByCreatedDateDesc();
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(1, dto.size());
    }

    @Test
    public void testGetPostByIdAndUser() {
        when(postRepository.findPostByIdAndUser(1L, user)).thenReturn(Optional.of(getPost()));

        Post post = service.getPostByIdAndUser(1L, principal);

        verify(postRepository, times(1)).findPostByIdAndUser(1L, user);
        verify(userService, times(1)).getUserByPrincipal(principal);
        Assertions.assertNotNull(post);
        Assertions.assertEquals(1L, post.getId());
    }

    @Test
    public void testGetPostByIdAndUserNoPost() {
        when(postRepository.findPostByIdAndUser(2L, user)).thenReturn(Optional.empty());

        Assertions.assertThrows(PostNotFoundException.class, () -> service.getPostByIdAndUser(2L, principal));

        verify(postRepository, times(1)).findPostByIdAndUser(2L, user);
        verify(userService, times(1)).getUserByPrincipal(principal);
    }

    @Test
    public void testGetPostById() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(getPost()));

        Post post = service.getPostById(1L);

        verify(postRepository, times(1)).findById(1L);
        Assertions.assertNotNull(post);
        Assertions.assertEquals(1L, post.getId());
    }

    @Test
    public void testGetPostByIdNoPost() {
        when(postRepository.findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(PostNotFoundException.class, () -> service.getPostById(2L));

        verify(postRepository, times(1)).findById(2L);
    }

    @Test
    public void testGetAllPostsForUser() {
        when(postRepository.findAllByUserOrderByCreatedDateDesc(user)).thenReturn(List.of(getPost()));

        List<PostDTO> dto = service.getAllPostsForUser(principal);

        verify(postRepository, times(1)).findAllByUserOrderByCreatedDateDesc(user);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(1, dto.size());
        Assertions.assertEquals(1L, dto.get(0).getId());
    }

    @Test
    public void testLikePost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(getPost()));

        PostDTO dto = service.likePost(1L, principal);

        verify(postRepository, times(1)).findById(1L);
        verify(userService, times(1)).getUserByPrincipal(principal);
        Assertions.assertNotNull(dto);
    }

    @Test
    public void testLikePostNoPost() {
        when(postRepository.findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(PostNotFoundException.class, () -> service.likePost(1L, principal));

        verify(postRepository, times(1)).findById(1L);
        verify(userService, times(1)).getUserByPrincipal(principal);
    }

    @Test
    public void testDeletePostNoImage() {
        Post post = getPost();
        when(postRepository.findPostByIdAndUser(1L, user)).thenReturn(Optional.of(post));
        when(imageRepository.findByPostId(1L)).thenReturn(Optional.empty());
        doNothing().when(postRepository).delete(post);

        service.deletePost(1L, principal);

        verify(postRepository, times(1)).findPostByIdAndUser(1L, user);
        verify(userService, times(1)).getUserByPrincipal(principal);
        verify(postRepository, times(1)).delete(post);
        verify(imageRepository, times(0)).delete(any());
    }

    @Test
    public void testDeletePostWithImage() {
        Post post = getPost();
        ImageModel image = new ImageModel();
        when(postRepository.findPostByIdAndUser(1L, user)).thenReturn(Optional.of(post));
        when(imageRepository.findByPostId(1L)).thenReturn(Optional.of(image));
        doNothing().when(postRepository).delete(post);
        doNothing().when(imageRepository).delete(image);

        service.deletePost(1L, principal);

        verify(postRepository, times(1)).findPostByIdAndUser(1L, user);
        verify(userService, times(1)).getUserByPrincipal(principal);
        verify(postRepository, times(1)).delete(post);
        verify(imageRepository, times(1)).delete(any());
    }

    private PostDTO getPostDto() {
        return PostDTO.builder()
                .caption("Caption")
                .location("Location")
                .title("Title")
                .build();
    }

    private User getUser() {
        return User.builder()
                .id(1L)
                .username("Test")
                .build();
    }

    private Post getPost() {
        return Post.builder()
                .id(1L)
                .title("Title")
                .location("Location")
                .caption("Caption")
                .likedUsers(new HashSet<>())
                .user(getUser())
                .build();
    }
}
