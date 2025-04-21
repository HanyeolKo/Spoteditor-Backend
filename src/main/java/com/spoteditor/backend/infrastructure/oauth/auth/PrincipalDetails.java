package com.spoteditor.backend.infrastructure.oauth.auth;

import com.spoteditor.backend.modules.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter
public class PrincipalDetails implements OAuth2User {

	private final User user;
	private final Map<String, Object> attributes;

	public PrincipalDetails(User user, Map<String, Object> attributes) {
		this.user = user;
		this.attributes = attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collet = new ArrayList<GrantedAuthority>();
		collet.add(()->{ return String.valueOf(user.getRole());});
		return collet;
	}

	@Override
	public String getName() {
		return user.getId()+"";
	}
}
