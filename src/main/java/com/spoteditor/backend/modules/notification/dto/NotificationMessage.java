package com.spoteditor.backend.modules.notification.dto;

public class NotificationMessage {

	public static final String FOLLOW = "%s님이 팔로우했습니다.";

	public static String formatFollowMessage(String followerName) {
		return String.format(FOLLOW, followerName);
	}
}
