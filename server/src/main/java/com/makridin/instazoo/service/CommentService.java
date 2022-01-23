package com.makridin.instazoo.service;

import com.makridin.instazoo.dto.CommentDTO;
import com.makridin.instazoo.entity.Comment;
import com.makridin.instazoo.entity.Post;
import com.makridin.instazoo.entity.User;
import com.makridin.instazoo.exceptions.AccessDeniedException;
import com.makridin.instazoo.exceptions.CommentNotFoundException;
import com.makridin.instazoo.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    public static final Logger LOG = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserService userService, PostService postService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.postService = postService;
    }

    public CommentDTO saveComment(CommentDTO commentDto, Principal principal, Long postId) {
        User user = userService.getUserByPrincipal(principal);
        Post post = postService.getPostById(postId);
        LOG.info("Saving Comment for Post {}", post.getId());
        return commentToCommentDto(commentRepository.save(commentDTOtoComment(commentDto, user, post)));
    }

    public List<CommentDTO> getAllCommentForPost(Long postId) {
        Post post = postService.getPostById(postId);
        return commentsToCommentDtos(commentRepository.findAllByPost(post));
    }

    public void deleteComment(Long commentId, Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment wasn't found"));
        if(comment.getUserId() == user.getId() || comment.getPost().getUser().getId() == user.getId()) {
            commentRepository.delete(comment);
        } else {
            throw new AccessDeniedException("You cannot delete someone else's comment");
        }
    }

    private CommentDTO commentToCommentDto(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .username(comment.getUsername())
                .build();
    }

    private List<CommentDTO> commentsToCommentDtos(List<Comment> comments) {
        return comments.stream()
                .map(this::commentToCommentDto)
                .collect(Collectors.toList());
    }

    private Comment commentDTOtoComment(CommentDTO dto, User user, Post post) {
        return Comment.builder()
                .message(dto.getMessage())
                .post(post)
                .username(user.getUsername())
                .userId(user.getId())
                .build();
    }

}
