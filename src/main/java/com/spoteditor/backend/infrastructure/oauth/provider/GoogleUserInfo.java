package com.spoteditor.backend.infrastructure.oauth.provider;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import static com.spoteditor.backend.modules.user.entity.Provider.GOOGLE;

@RequiredArgsConstructor
public class GoogleUserInfo implements OAuth2UserInfo {

	private final Map<String, Object> attributes;

	@Override
	public String getProviderId() {
		return (String) attributes.get("sub");
	}

	@Override
	public String getProvider() {
		return String.valueOf(GOOGLE);
	}

	@Override
	public String getEmail() {
		return (String) attributes.get("email");
	}

	@Override
	public String getName() {
		return (String) attributes.get("name");
	}

	@Override
	public String getProfileImage() {
		return (String) attributes.get("picture");
	}
}
