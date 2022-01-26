package com.makridin.instazoo.service;

import com.makridin.instazoo.dto.CommentDTO;
import com.makridin.instazoo.entity.Comment;
import com.makridin.instazoo.entity.Post;
import com.makridin.instazoo.entity.User;
import com.makridin.instazoo.exceptions.AccessDeniedException;
import com.makridin.instazoo.exceptions.CommentNotFoundException;
import com.makridin.instazoo.repository.CommentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class CommentServiceTest {

    @InjectMocks
    private CommentService service;

    @MockBean
    private UserService userService;
    @MockBean
    private PostService postService;
    @MockBean
    CommentRepository repository;
    @Mock
    private User user;
    @Mock
    private User secondUser;
    @Mock
    private Post post;
    @Mock
    Principal principal;

    @BeforeEach
    private void setUp() {
        when(userService.getUserByPrincipal(principal)).thenReturn(user);
        when(postService.getPostById(1L)).thenReturn(post);
        when(repository.save(any())).thenReturn(getComment());
    }

    @Test
    public void testSaveComment() {
        when(user.getUsername()).thenReturn("Test");
        when(user.getId()).thenReturn(1L);
        when(post.getId()).thenReturn(1L);
        CommentDTO dto = service.saveComment(getCommentDTO(), principal, 1L);
        verify(userService, times(1)).getUserByPrincipal(principal);
        verify(postService, times(1)).getPostById(1L);
        verify(repository, times(1)).save(any());
        checkDtoPopulation(dto, 1L);
    }

    @Test
    public void testGetAllCommentForPost() {
        when(repository.findAllByPost(post)).thenReturn(List.of(getComment(), getComment(2L)));
        List<CommentDTO> dto = service.getAllCommentForPost(1L);
        verify(postService, times(1)).getPostById(1L);
        verify(repository, times(1)).findAllByPost(post);
        Assertions.assertEquals(2, dto.size());
        checkDtoPopulation(dto.get(0), 1L);
        checkDtoPopulation(dto.get(1), 2L);
    }

    @Test
    public void testGetAllCommentForPostEmptyList() {
        when(repository.findAllByPost(post)).thenReturn(Collections.emptyList());
        List<CommentDTO> dto = service.getAllCommentForPost(1L);
        verify(postService, times(1)).getPostById(1L);
        verify(repository, times(1)).findAllByPost(post);
        Assertions.assertTrue(dto.isEmpty());
    }

    @Test
    public void testDeleteComment() {
        Comment comment = getComment();
        when(repository.findById(1L)).thenReturn(Optional.of(comment));
        when(user.getId()).thenReturn(1L);
        doNothing().when(repository).delete(comment);

        service.deleteComment(1L, principal);

        verify(userService, times(1)).getUserByPrincipal(principal);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).delete(comment);
    }

    @Test
    public void testDeleteCommentNotExisted() {
        Comment comment = getComment();
        when(repository.findById(1L)).thenReturn(Optional.empty());
        doNothing().when(repository).delete(comment);

        Assertions.assertThrows(CommentNotFoundException.class, () -> service.deleteComment(1L, principal));

        verify(userService, times(1)).getUserByPrincipal(principal);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(0)).delete(any());
    }

    @Test
    public void testDeleteSomeonesComment() {
        Comment comment = getComment();
        when(repository.findById(1L)).thenReturn(Optional.of(comment));
        when(user.getId()).thenReturn(2L);
        when(post.getUser()).thenReturn(secondUser);
        when(secondUser.getId()).thenReturn(1L);
        doNothing().when(repository).delete(comment);

        Assertions.assertThrows(AccessDeniedException.class, () -> service.deleteComment(1L, principal));

        verify(userService, times(1)).getUserByPrincipal(principal);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(0)).delete(any());
    }

    private void checkDtoPopulation(CommentDTO dto, long id) {
        Assertions.assertEquals(id, dto.getId());
        Assertions.assertEquals("Test", dto.getMessage());
        Assertions.assertEquals("Test", dto.getUsername());
    }

    private Comment getComment() {
        return getComment(1L);
    }

    private Comment getComment(long id) {
        return Comment.builder()
                .id(id)
                .message("Test")
                .post(post)
                .username("Test")
                .userId(1L)
                .build();
    }

    private CommentDTO getCommentDTO() {
        return CommentDTO.builder()
                .id(1L)
                .username("Test")
                .message("Test")
                .build();
    }
}
