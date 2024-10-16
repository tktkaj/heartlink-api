package com.ss.heartlinkapi.follow.dto;

import java.util.List;

import com.ss.heartlinkapi.post.dto.PostDTO;
import com.ss.heartlinkapi.post.dto.PostFileDTO;
import com.ss.heartlinkapi.post.entity.Visibility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowDTO {
	private String myFollowingId;	// 내 팔로잉 아이디

}
