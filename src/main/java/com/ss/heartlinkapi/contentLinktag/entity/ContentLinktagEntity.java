package com.ss.heartlinkapi.contentLinktag.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ss.heartlinkapi.comment.entity.CommentEntity;
import com.ss.heartlinkapi.linktag.entity.LinkTagEntity;
import com.ss.heartlinkapi.post.entity.PostEntity;
import com.ss.heartlinkapi.post.entity.Visibility;
import com.ss.heartlinkapi.user.entity.UserEntity;

import lombok.Data;

@Entity
@Data
@Table(name = "content_linktag")
public class ContentLinktagEntity {
	@Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long contentLinktagId;					// 연결 링크 태그 아이디
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id")
	private PostEntity boardId;						// 게시글 아이디
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id", nullable = false)
	private CommentEntity commentId;				// 댓글 아이디
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "linktag_id", nullable = false)
	private LinkTagEntity linktagId;				// 링크태그 아이디

}
