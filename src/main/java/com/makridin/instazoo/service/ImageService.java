package com.makridin.instazoo.service;

import com.makridin.instazoo.entity.ImageModel;
import com.makridin.instazoo.entity.Post;
import com.makridin.instazoo.entity.User;
import com.makridin.instazoo.exceptions.ImageNotFoundException;
import com.makridin.instazoo.exceptions.PostNotFoundException;
import com.makridin.instazoo.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class ImageService {
    public static final Logger LOG = LoggerFactory.getLogger(ImageService.class);

    private final ImageRepository imageRepository;
    private final UserService userService;
    private final PostService postService;

    @Autowired
    public ImageService(ImageRepository imageRepository, UserService userService, PostService postService) {
        this.imageRepository = imageRepository;
        this.userService = userService;
        this.postService = postService;
    }

    public ImageModel uploadProfileImage(MultipartFile file, Principal principal) throws IOException {
        User user = userService.getCurrentUser(principal);

        ImageModel userProfileImage = imageRepository.findByUserId(user.getId()).orElse(null);
        if(!ObjectUtils.isEmpty(userProfileImage)) {
            imageRepository.delete(userProfileImage);
        }

        ImageModel imageModel = new ImageModel();
        imageModel.setUserId(user.getId());
        imageModel.setImageBytes(compressBytes(file.getBytes()));
        imageModel.setName(file.getOriginalFilename());
        LOG.info("Uploading image profile to User {}", user.getUsername());
        return imageRepository.save(imageModel);
    }

    public ImageModel uploadPostImage(MultipartFile file, Principal principal, Long postId) throws IOException {
        User user = userService.getCurrentUser(principal);
        Post post = user.getPosts()
                .stream()
                .filter(p -> p.getId().equals(postId))
                .findFirst().orElseThrow(() -> new PostNotFoundException("Post wasn't found. post id = " + postId));

        ImageModel image = new ImageModel();
        image.setImageBytes(compressBytes(file.getBytes()));
        image.setName(file.getOriginalFilename());
        image.setPostId(post.getId());
        LOG.info("Uploading image for Post {}", post.getId());
        return imageRepository.save(image);
    }

    public ImageModel getUserProfileImage(Principal principal) {
        User user = userService.getCurrentUser(principal);
        LOG.info("Uploading image profile to User {}", user.getUsername());

        ImageModel image = imageRepository.findByUserId(user.getId()).orElse(null);

        if(image != null) {
            image.setImageBytes(decompressBytes(image.getImageBytes()));
        }
        return image;
    }

    public ImageModel getPostImage(Long postId) {
        ImageModel image = imageRepository.findByPostId(postId)
                .orElseThrow(() -> new ImageNotFoundException("Post image wasn't found. Post id = " + postId));

        if(image != null) {
            image.setImageBytes(decompressBytes(image.getImageBytes()));
        }
        return image;
    }

    private byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException ex) {
            LOG.error("Cannot compress bytes");
        }
        System.out.println("Compressed image byte size = " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

    private byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException ex) {
            LOG.error("Cannot decompress bytes");
        }

        System.out.println("Decompressed image byte size = " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

}
