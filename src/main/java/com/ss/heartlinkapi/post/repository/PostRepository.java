package com.ss.heartlinkapi.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ss.heartlinkapi.post.entity.PostEntity;

public interface PostRepository extends JpaRepository<PostEntity, Long>{

	// 내 팔로잉 게시물 조회
	@Query("SELECT p FROM PostEntity p " +
		       "JOIN FollowEntity f ON f.following.id = p.userId.userId " +
		       "WHERE f.follower.id = :followerId " +
		       "AND p.visibility = com.ss.heartlinkapi.post.entity.Visibility.PUBLIC " +
		       "ORDER BY p.createdAt DESC")
	List<PostEntity> findPublicPostsByFollowerId(@Param("followerId") Long followerId);


}
