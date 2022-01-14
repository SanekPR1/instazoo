package com.makridin.instazoo.controllers;

import com.makridin.instazoo.entity.ImageModel;
import com.makridin.instazoo.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/image")
@CrossOrigin
public class ImageController {
    private static final String SUCCESS_MESSAGE = "Image was uploaded successfully";

    private final ImageService imageService;


    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadUserImage(
            @RequestParam MultipartFile file,
            Principal principal
    ) throws IOException {
        imageService.uploadProfileImage(file, principal);
        return ResponseEntity.ok(SUCCESS_MESSAGE);
    }

    @PostMapping("/{postId}/upload")
    public ResponseEntity<String> uploadPostImage(
            @PathVariable("postId") Long postId,
            @RequestParam MultipartFile file,
            Principal principal
    ) throws IOException {
        imageService.uploadPostImage(file, principal, postId);
        return ResponseEntity.ok(SUCCESS_MESSAGE);
    }

    @GetMapping("/profile")
    public ResponseEntity<ImageModel> getProfileImage(Principal principal) {
        return ResponseEntity.ok(imageService.getUserProfileImage(principal));
    }

    @GetMapping("/{postId}/image")
    public ResponseEntity<ImageModel> getPostImage(
            @PathVariable("postId") Long postId)
    {
        return ResponseEntity.ok(imageService.getPostImage(postId));
    }
}
