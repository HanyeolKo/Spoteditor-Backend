package com.spoteditor.backend.config.oauth.provider;

// OAuth2.0 제공자마다 속성값이 달라서 공통으로 만들어둔다.
public interface OAuth2UserInfo {

	String getProviderId();
	String getProvider();
	String getEmail();
	String getName();
	String getProfileImage();
}
