package com.makridin.instazoo.service;

import com.makridin.instazoo.dto.PostDTO;
import com.makridin.instazoo.entity.ImageModel;
import com.makridin.instazoo.entity.Post;
import com.makridin.instazoo.entity.User;
import com.makridin.instazoo.exceptions.PostNotFoundException;
import com.makridin.instazoo.repository.ImageRepository;
import com.makridin.instazoo.repository.PostRepository;
import com.makridin.instazoo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PostService {
    public static final Logger LOG = LoggerFactory.getLogger(PostService.class);

     private final PostRepository postRepository;
     private final UserService userService;
     private final ImageRepository imageRepository;

     @Autowired
    public PostService(PostRepository postRepository, UserService userService, ImageRepository imageRepository) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.imageRepository = imageRepository;
    }

    public Post createPost(PostDTO postDto, Principal principal) {
        User user = userService.getCurrentUser(principal);
        Post post = new Post();
        post.setUser(user);
        post.setCaption(postDto.getCaption());
        post.setLocation(postDto.getLocation());
        post.setTitle(postDto.getTitle());
        post.setLikes(0);

        LOG.info("Saving Post for User {}", user.getUsername());
        return postRepository.save(post);

    }

    public List<Post> getAllPosts() {
         return postRepository.findAllByOrderByCreatedDateDesc();
    }

    public Post getPostById(Long postId, Principal principal) {
        User user = userService.getCurrentUser(principal);
        return postRepository.findPostByIdAndUser(postId, user)
                .orElseThrow(() -> new PostNotFoundException("The post wasn't found for user " + user.getUsername()));
    }

    public List<Post> getAllPostsForUser(Principal principal) {
        User user = userService.getCurrentUser(principal);
        return postRepository.findAllByUserOrderByCreatedDateDesc(user);
    }

    public Post likePost(Long id, String username) {
         Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("The post wasn't found"));
         Set<String> usernames = post.getLikedUsers();
         if(!usernames.remove(username)) {
             usernames.add(username);
         }
         post.setLikes(usernames.size());
        return postRepository.save(post);
    }

    public void deletePost(Long id, Principal principal) {
         Post post = getPostById(id, principal);
         Optional<ImageModel> image = imageRepository.findByPostId(post.getId());
         postRepository.delete(post);
         image.ifPresent(imageRepository::delete);
    }
}
