package com.makridin.instazoo.repository;

import com.makridin.instazoo.entity.Post;
import com.makridin.instazoo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUserOrderByCreatedDateDesc(User user);
    List<Post> findAllOrderByCreatedDateDesc(User user);
    Optional<Post> findPostByIdAndUser(Long id, User user);
}
