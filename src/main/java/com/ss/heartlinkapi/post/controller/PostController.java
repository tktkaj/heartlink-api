package com.ss.heartlinkapi.post.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ss.heartlinkapi.post.dto.PostDTO;
import com.ss.heartlinkapi.post.entity.PostEntity;
import com.ss.heartlinkapi.post.service.PostService;
import com.ss.heartlinkapi.user.entity.UserEntity;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/feed")
public class PostController {
	
	private final PostService postService;
	
	public PostController(PostService postService) {
		this.postService = postService;
	}
	
	// 게시글 작성 a
//	@PostMapping("/write")
//	public ResponseEntity<?> writePost(@RequestBody PostDTO postDTO, @AuthenticationPrincipal UserEntity user){
//		
//		try {
//			postService.savePost(postDTO, user);
//			return ResponseEntity.status(HttpStatus.CREATED).build();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//		}
//		
//	}
	
	// 사용자ID 안받고 하는 임시 게시글 작성 코드
	@PostMapping("/write")
	public ResponseEntity<?> writePost(@RequestBody PostDTO postDTO) {
	    // 임시 UserEntity 생성
	    UserEntity testUser = new UserEntity();
	    testUser.setUserId(1L);  // 임의의 사용자 ID
//	    testUser.setUsername("testUser");  // 임의의 사용자 이름
	    
	    // 첨부파일이 없을 때 예외
	    if (postDTO.getFiles() == null || postDTO.getFiles().isEmpty()) {
	    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("첨부파일이 최소 1개 이상 포함되어야 합니다.");
	    }
	    
	    try {
	        postService.savePost(postDTO, testUser);
	        return ResponseEntity.status(HttpStatus.CREATED).build();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	
	// 내 게시물 조회
	@GetMapping("/{userId}")
	public ResponseEntity<?> getFollowingPublicPosts(@PathVariable Long userId){
		
		List<PostDTO> followingPosts = postService.getPublicPostByFollowerId(userId);
		List<PostDTO> nonFollowedPosts = postService.getNonFollowedAndNonReportedPosts(userId);
		
		return ResponseEntity.ok().body(new PostsResponse(followingPosts, nonFollowedPosts));
		
	}
	
	private static class PostsResponse {
		private List<PostDTO> followingPosts;
		private List<PostDTO> nonFollowedPosts;
		
		public PostsResponse(List<PostDTO> followingPosts, List<PostDTO> nonFollowedPosts) {
			this.followingPosts = followingPosts;
			this.nonFollowedPosts = nonFollowedPosts;
		}
		
		public List<PostDTO> getFollowingPosts(){
			return followingPosts;
		}
		
		public List<PostDTO> getNonFollowedPosts(){
			return nonFollowedPosts;
		}
		
	}
	
	// 게시글 상세보기
	@GetMapping("/details/{postId}")
	public ResponseEntity<PostDTO> getPostWithComments(@PathVariable Long postId){
		PostDTO postDTO = postService.getPostById(postId);
		
		return ResponseEntity.ok(postDTO);
	}


}
