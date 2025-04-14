package com.spoteditor.backend.config.oauth.service;

import com.spoteditor.backend.config.oauth.auth.PrincipalDetails;
import com.spoteditor.backend.config.oauth.provider.GoogleUserInfo;
import com.spoteditor.backend.config.oauth.provider.KakaoUserInfo;
import com.spoteditor.backend.config.oauth.provider.OAuth2UserInfo;
import com.spoteditor.backend.user.entity.User;
import com.spoteditor.backend.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.spoteditor.backend.user.entity.Role.USER;

@Slf4j
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

	@Autowired private UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(request);
		return processOAuth2User(request, oAuth2User);
	}

	private OAuth2User processOAuth2User(OAuth2UserRequest request, OAuth2User oAuth2User) {

		OAuth2UserInfo oAuth2UserInfo = null;

		if (request.getClientRegistration().getRegistrationId().equals("google")) {
			log.info("구글 로그인 요청");
			oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
		} else if (request.getClientRegistration().getRegistrationId().equals("kakao")) {
			log.info("카카오 로그인 요청");
			oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
		} else {
			log.info("지원하지 않는 OAuth");
		}

		// 동일한 메일로 등록된 소셜 로그인 계정이 존재하는 경우
		Optional<User> oauth2User = userRepository.findByEmail(oAuth2UserInfo.getEmail());
		User user = null;

		if (oauth2User.isPresent()) {
			user = oauth2User.get();
			user.updateOauthInfo(oAuth2UserInfo.getProvider(), oAuth2UserInfo.getProviderId());
		} else {
			user = User.builder()
					.name(oAuth2UserInfo.getName())
					.email(oAuth2UserInfo.getEmail())
					.role(USER)
					.provider(oAuth2UserInfo.getProvider())
					.providerId(oAuth2UserInfo.getProviderId())
					.build();
		}
		userRepository.save(user);
		return new PrincipalDetails(user, oAuth2User.getAttributes());
	}
}
