package com.ss.heartlinkapi.follow.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowerDTO {
	private Long userId;
	private Long followerUserId;
	private String followerLoginId;
	private String followerImg;
	private boolean status;
}

