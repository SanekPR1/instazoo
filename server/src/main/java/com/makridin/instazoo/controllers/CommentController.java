package com.makridin.instazoo.controllers;

import com.makridin.instazoo.dto.CommentDTO;
import com.makridin.instazoo.payload.response.MessageResponse;
import com.makridin.instazoo.service.CommentService;
import com.makridin.instazoo.validators.ResponseErrorValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController( CommentService commentService)
    {
        this.commentService = commentService;
    }

    @PostMapping("/{postId}/create")
    public ResponseEntity<CommentDTO> createComment(
            @Valid @RequestBody CommentDTO commentDTO,
            Principal principal,
            @PathVariable("postId") Long postId
    ) {

        return ResponseEntity.ok(
                commentService.saveComment(commentDTO, principal, postId));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getAllPostComments(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(commentService.getAllCommentForPost(postId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<MessageResponse> deleteComment(
            @PathVariable("commentId") Long commentId,
            Principal principal
    ) {
        commentService.deleteComment(commentId, principal);
        return ResponseEntity.ok(new MessageResponse("Comment was deleted"));
    }
}
