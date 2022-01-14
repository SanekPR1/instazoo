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

    public Comment saveComment(CommentDTO commentDto, Principal principal, Long postId) {
        User user = userService.getCurrentUser(principal);
        Post post = postService.getPostById(postId);
        Comment comment = new Comment();
        comment.setMessage(commentDto.getMessage());
        comment.setPost(post);
        comment.setUsername(user.getUsername());
        comment.setUserId(user.getId());
        LOG.info("Saving Comment for Post {}", post.getId());
        return commentRepository.save(comment);
    }

    public List<Comment> getAllCommentForPost(Long postId) {
        Post post = postService.getPostById(postId);
        return commentRepository.findAllByPost(post);
    }

    public void deleteComment(Long commentId, Principal principal) {
        User user = userService.getCurrentUser(principal);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment wasn't found"));
        if(comment.getUserId() == user.getId() || comment.getPost().getUser().getId() == user.getId()) {
            commentRepository.delete(comment);
        } else {
            throw new AccessDeniedException("You cannot delete someone else's comment");
        }
    }
}
