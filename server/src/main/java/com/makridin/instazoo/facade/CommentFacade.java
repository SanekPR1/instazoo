package com.makridin.instazoo.facade;

import com.makridin.instazoo.dto.CommentDTO;
import com.makridin.instazoo.entity.Comment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentFacade {

    public CommentDTO commentToCommentDto(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .username(comment.getUsername())
                .build();
    }

    public List<CommentDTO> commenstToCommentDtos(List<Comment> comments) {
        return comments.stream()
                .map(this::commentToCommentDto)
                .collect(Collectors.toList());
    }
}
