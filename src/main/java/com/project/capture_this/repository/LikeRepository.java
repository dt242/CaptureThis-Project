package com.project.capture_this.repository;

import com.project.capture_this.model.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByPostIdAndUserId(Long postId, Long userId);
    void deleteByPostIdAndUserId(Long postId, Long userId);

    List<Like> findByPostId(Long postId);
    List<Like> findAllByPostIdOrderByCreatedAtDesc(Long postId);
}
