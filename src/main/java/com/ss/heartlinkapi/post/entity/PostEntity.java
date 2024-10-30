package com.ss.heartlinkapi.post.entity;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ss.heartlinkapi.bookmark.entity.BookmarkEntity;
import com.ss.heartlinkapi.comment.entity.CommentEntity;
import com.ss.heartlinkapi.like.entity.LikeEntity;
import com.ss.heartlinkapi.report.entity.ReportEntity;
import com.ss.heartlinkapi.user.entity.UserEntity;

import lombok.Data;

@Entity
@Data
@Table(name = "post")
@EntityListeners(AuditingEntityListener.class)
public class PostEntity {
	@Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long postId; // 게시글 id
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity userId; // 게시글 작성자 id
	
	@Column(name = "content")
	private String content; // 내용
	
	@CreatedDate
	@Column(name = "created_at", nullable= false, updatable = false)
	private LocalDateTime createdAt; // 작성 시간
	
	@LastModifiedDate
	@Column(name = "update_at")
	private LocalDateTime updatedAt; // 수정 시간
	
	@Column(name = "like_count", nullable= false)
	private int likeCount; // 좋아요 수
	
	@Column(name = "comment_count", nullable= false)
	private int commentCount; // 댓글 수
	
	@Enumerated(EnumType.STRING)
	private Visibility visibility; // 공개 범위
	
	
	// Cascade 설정
    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
	@JsonIgnoreProperties({"postId"})
    private List<CommentEntity> comments;

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
	@JsonIgnoreProperties({"postId"})
	private List<LikeEntity> likes;

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
	@JsonIgnoreProperties({"postId"})
	private List<ReportEntity> reports;

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
	@JsonIgnoreProperties({"postId"})
	private List<PostFileEntity> postFiles;

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
	@JsonIgnoreProperties({"postId"})
	private List<BookmarkEntity> bookmarks;
    
    
    // mention도 추가
}
