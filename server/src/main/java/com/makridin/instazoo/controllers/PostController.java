package com.makridin.instazoo.controllers;

import com.makridin.instazoo.dto.PostDTO;
import com.makridin.instazoo.payload.response.MessageResponse;
import com.makridin.instazoo.service.PostService;
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
@RequestMapping("/api/post")
@CrossOrigin
public class PostController {

    private final ResponseErrorValidation errorValidation;
    private final PostService postService;

    @Autowired
    public PostController(ResponseErrorValidation errorValidation, PostService postService) {
        this.errorValidation = errorValidation;
        this.postService = postService;
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createPost(
            @Valid @RequestBody PostDTO psotDto, Principal principal, BindingResult result)
    {
        ResponseEntity<Object> errors = errorValidation.mapValidationService(result);
        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }
        return ResponseEntity.ok(postService.createPost(psotDto, principal));
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/user/posts")
    public ResponseEntity<List<PostDTO>> getAllUserPosts(Principal principal) {
        return ResponseEntity.ok(postService.getAllPostsForUser(principal));
    }

    @PutMapping("/{postId}/like")
    public ResponseEntity<PostDTO> likePost(
            @PathVariable("postId") Long postId,
            Principal principal
    ) {
        return ResponseEntity.ok(postService.likePost(postId, principal));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Object> deletePost(
            @PathVariable("postId") Long postId,
            Principal principal
    ) {
        postService.deletePost(postId, principal);
        return ResponseEntity.ok(new MessageResponse("Post was deleted"));
    }
}
