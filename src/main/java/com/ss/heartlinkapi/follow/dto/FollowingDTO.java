package com.ss.heartlinkapi.follow.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowingDTO {
	private Long followingUserId;
	private String followingLoginId;
	private String followingImg;
	private boolean status;
}
