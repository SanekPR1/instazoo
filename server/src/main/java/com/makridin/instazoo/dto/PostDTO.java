package com.makridin.instazoo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDTO {
    private Long id;
    private String title;
    private String caption;
    private String location;
    private String username;
    private Integer likes;
    private Set<String> userLiked;
}
