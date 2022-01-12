package com.makridin.instazoo.facade;

import com.makridin.instazoo.dto.PostDTO;
import com.makridin.instazoo.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostFacade {

    public PostDTO userToUserDto(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .caption(post.getCaption())
                .likes(post.getLikes())
                .location(post.getLocation())
                .title(post.getTitle())
                .username(post.getUser().getUsername())
                .userLiked(post.getLikedUsers())
                .build();
    }
}
