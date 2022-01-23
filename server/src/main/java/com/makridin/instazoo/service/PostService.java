package com.makridin.instazoo.service;

import com.makridin.instazoo.dto.PostDTO;
import com.makridin.instazoo.entity.ImageModel;
import com.makridin.instazoo.entity.Post;
import com.makridin.instazoo.entity.User;
import com.makridin.instazoo.exceptions.PostNotFoundException;
import com.makridin.instazoo.repository.ImageRepository;
import com.makridin.instazoo.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public PostDTO createPost(PostDTO postDto, Principal principal) {
        User user = userService.getCurrentUser(principal);
        LOG.info("Saving Post for User {}", user.getUsername());
        return postToPostDto(postRepository.save(postDTOToPost(postDto, user)));

    }

    public List<PostDTO> getAllPosts() {
         return postsToPostDtos(postRepository.findAllByOrderByCreatedDateDesc());
    }

    public Post getPostByIdAndUser(Long postId, Principal principal) {
        User user = userService.getCurrentUser(principal);
        return postRepository.findPostByIdAndUser(postId, user)
                .orElseThrow(() -> new PostNotFoundException("The post wasn't found for user " + user.getUsername()));
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("The post wasn't found"));
    }

    public List<PostDTO> getAllPostsForUser(Principal principal) {
        User user = userService.getCurrentUser(principal);
        return postsToPostDtos(postRepository.findAllByUserOrderByCreatedDateDesc(user));
    }

    public PostDTO likePost(Long postId, Principal principal) {
        User user = userService.getCurrentUser(principal);
         Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("The post wasn't found"));
         Set<String> usernames = post.getLikedUsers();
         if(!usernames.remove(user.getUsername())) {
             usernames.add(user.getUsername());
         }
        return postToPostDto(postRepository.save(post));
    }

    public void deletePost(Long id, Principal principal) {
         Post post = getPostByIdAndUser(id, principal);
         Optional<ImageModel> image = imageRepository.findByPostId(post.getId());
         postRepository.delete(post);
         image.ifPresent(imageRepository::delete);
    }

    private PostDTO postToPostDto(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .caption(post.getCaption())
                .location(post.getLocation())
                .title(post.getTitle())
                .username(post.getUser().getUsername())
                .userLiked(post.getLikedUsers())
                .build();
    }

    private List<PostDTO> postsToPostDtos(List<Post> posts) {
        return posts.stream()
                .map(this::postToPostDto)
                .collect(Collectors.toList());
    }

    private Post postDTOToPost(PostDTO dto, User user) {
         return Post.builder()
                 .user(user)
                 .caption(dto.getCaption())
                 .location(dto.getLocation())
                 .title(dto.getTitle())
                 .build();
    }
}
