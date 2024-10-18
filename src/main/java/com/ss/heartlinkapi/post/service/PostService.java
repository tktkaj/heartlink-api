package com.ss.heartlinkapi.post.service;

import java.lang.System.Logger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.ss.heartlinkapi.post.dto.PostDTO;
import com.ss.heartlinkapi.post.dto.PostFileDTO;
import com.ss.heartlinkapi.post.entity.PostEntity;
import com.ss.heartlinkapi.post.entity.PostFileEntity;
import com.ss.heartlinkapi.post.repository.PostFileRepository;
import com.ss.heartlinkapi.post.repository.PostRepository;
import com.ss.heartlinkapi.user.entity.UserEntity;

import lombok.extern.slf4j.Slf4j;

@Service
public class PostService {

	private final PostRepository postRepository;
	private final PostFileRepository postFileRepository;

	public PostService(PostRepository postRepository, PostFileRepository postFileRepository) {
		this.postRepository = postRepository;
		this.postFileRepository = postFileRepository;
	}

	// 게시글 작성
	@Transactional
	public void savePost(PostDTO postDTO, UserEntity user) {
		
		List<PostFileDTO> fileList = postDTO.getFiles();
		
		
		PostEntity post = new PostEntity();

		post.setUserId(user);
		post.setContent(postDTO.getContent());
		post.setVisibility(postDTO.getVisibility());
		post.setCreatedAt(LocalDateTime.now());
		post.setLikeCount(0);
		post.setCommentCount(0);
		

		postRepository.save(post);

		
			int sortOrder = 1;
			for (PostFileDTO postFileDTO : fileList) {
				PostFileEntity postFile = new PostFileEntity();
				postFile.setPostId(post);
				postFile.setFileUrl(postFileDTO.getFileUrl());
				postFile.setFileType(postFileDTO.getFileType());
				postFile.setSortOrder(sortOrder);

				postFileRepository.save(postFile);
				sortOrder++;
			}

	}
	
	// 내 팔로잉 게시물 조회
	public List<PostDTO> getPublicPostByFollowerId(Long followerId) {
	    List<PostEntity> posts = postRepository.findPublicPostsByFollowerId(followerId);
	    return posts.stream()
	                .map(post -> {
	                    List<PostFileEntity> postFiles = postFileRepository.findByPostId(post.getPostId());
	                    return new PostDTO(
	                            post.getPostId(),
	                            post.getUserId().getLoginId(),
	                            post.getContent(),
	                            post.getCreatedAt(),
	                            post.getVisibility(),
	                            postFiles.stream()
	                                .map(file -> new PostFileDTO(
	                                    post.getPostId(),
	                                    file.getFileUrl(),
	                                    file.getFileType(),
	                                    file.getSortOrder()))
	                                .collect(Collectors.toList()));
	                })
	                .collect(Collectors.toList());
	}



}