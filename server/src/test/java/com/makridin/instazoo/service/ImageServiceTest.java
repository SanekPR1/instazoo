package com.makridin.instazoo.service;

import com.makridin.instazoo.entity.ImageModel;
import com.makridin.instazoo.entity.Post;
import com.makridin.instazoo.entity.User;
import com.makridin.instazoo.exceptions.ImageNotFoundException;
import com.makridin.instazoo.exceptions.PostNotFoundException;
import com.makridin.instazoo.repository.ImageRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
public class ImageServiceTest {

    @InjectMocks
    private ImageService service;

    @Mock
    private ImageRepository imageRepository;
    @Mock
    private UserService userService;
    @Mock
    private Principal principal;
    @Mock
    private ImageModel image;

    @BeforeEach
    private void setUp() {
        when(userService.getUserByPrincipal(principal)).thenReturn(getUser());
        when(imageRepository.save(any())).thenReturn(image);
    }

    @Test
    public void testUploadProfileImage() throws IOException {
        when(imageRepository.findByUserId(1L)).thenReturn(Optional.empty());

        service.uploadProfileImage(getFile(), principal);

        verify(userService, times(1)).getUserByPrincipal(principal);
        verify(imageRepository, times(1)).findByUserId(1L);
        verify(imageRepository, times(1)).save(any());
        verify(imageRepository, times(0)).delete(any());
    }

    @Test
    public void testUploadProfileImageWhenThereWasImage() throws IOException {
        when(imageRepository.findByUserId(1L)).thenReturn(Optional.of(image));
        doNothing().when(imageRepository).delete(image);

        service.uploadProfileImage(getFile(), principal);

        verify(userService, times(1)).getUserByPrincipal(principal);
        verify(imageRepository, times(1)).findByUserId(1L);
        verify(imageRepository, times(1)).save(any());
        verify(imageRepository, times(1)).delete(any());
    }

    @Test
    public void testUploadPostImage() throws IOException {
        service.uploadPostImage(getFile(), principal, 1L);

        verify(userService, times(1)).getUserByPrincipal(principal);
        verify(imageRepository, times(1)).save(any());
    }

    @Test
    public void testUploadPostImageWrongPostId() {
        Assertions.assertThrows(PostNotFoundException.class, () -> service.uploadPostImage(getFile(), principal, 2L));

        verify(userService, times(1)).getUserByPrincipal(principal);
        verify(imageRepository, times(0)).save(any());
    }

    @Test
    public void testUploadPostImageNoPosts() {
        when(userService.getUserByPrincipal(principal)).thenReturn(getUser(Collections.emptyList()));
        Assertions.assertThrows(PostNotFoundException.class, () -> service.uploadPostImage(getFile(), principal, 2L));

        verify(userService, times(1)).getUserByPrincipal(principal);
        verify(imageRepository, times(0)).save(any());
    }

    @Test
    public void testGetUserProfileImage() {
        when(imageRepository.findByUserId(1L)).thenReturn(Optional.of(getImage()));

        ImageModel image = service.getUserProfileImage(principal);

        verify(userService, times(1)).getUserByPrincipal(principal);
        verify(imageRepository, times(1)).findByUserId(1L);
        Assertions.assertNotNull(image);
        Assertions.assertEquals("Hello world", new String(image.getImageBytes(), StandardCharsets.UTF_8));
    }

    @Test
    public void testGetUserProfileImageNoImage() {
        when(imageRepository.findByUserId(1L)).thenReturn(Optional.empty());

        ImageModel image = service.getUserProfileImage(principal);

        verify(userService, times(1)).getUserByPrincipal(principal);
        verify(imageRepository, times(1)).findByUserId(1L);
        Assertions.assertNull(image);
    }

    @Test
    public void testGetPostImage() {
        when(imageRepository.findByPostId(1L)).thenReturn(Optional.of(getImage()));

        ImageModel image = service.getPostImage(1L);

        verify(imageRepository, times(1)).findByPostId(1L);
        Assertions.assertNotNull(image);
        Assertions.assertEquals("Hello world", new String(image.getImageBytes(), StandardCharsets.UTF_8));
    }

    @Test
    public void testGetPostImageNoImage() {
        when(imageRepository.findByPostId(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ImageNotFoundException.class, () -> service.getPostImage(1L));

        verify(imageRepository, times(1)).findByPostId(1L);
    }

    private User getUser() {
        return getUser(List.of(getPost()));
    }

    private User getUser(List<Post> posts) {
        return User.builder()
                .id(1L)
                .posts(posts)
                .username("Test")
                .build();
    }

    private Post getPost() {
        return Post.builder()
                .id(1L)
                .build();
    }

    private MultipartFile getFile() {
        return new MockMultipartFile("Test", "Test.jpg", "image/jpg", "Hello world".getBytes());
    }

    private ImageModel getImage() {
        byte[] bytes = {120, -100, -13, 72, -51, -55, -55, 87, 40, -49, 47, -54, 73, 1, 0, 24, -85, 4, 61};
        ImageModel model = new ImageModel();
        model.setImageBytes(bytes);
        model.setName("Test");
        model.setUserId(1L);
        model.setPostId(1L);
        return model;
    }
}
