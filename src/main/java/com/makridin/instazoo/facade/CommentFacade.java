package com.makridin.instazoo.facade;

import com.makridin.instazoo.dto.CommentDTO;
import com.makridin.instazoo.dto.PostDTO;
import com.makridin.instazoo.entity.Comment;
import com.makridin.instazoo.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class CommentFacade {

    public CommentDTO userToUserDto(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .username(comment.getUsername())
                .build();
    }
}
