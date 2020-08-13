package me.glory.springboot.config.auth;

import lombok.RequiredArgsConstructor;
import me.glory.springboot.config.auth.dto.OAuthAttributes;
import me.glory.springboot.config.auth.dto.SessionUser;
import me.glory.springboot.domain.user.User;
import me.glory.springboot.domain.user.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 현재 로그인 진행 중인 서비스를 구분하는 코드. 네이버 로그인과 구글 로그인을 구분하는 데 사용
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        /*
            OAuth2 로그인 진행할 때 키가 되는 필드 값. Primary Key와 같은 의미.
            구글의 경우 기본적으로 코드를 지원하지만, 네이버와 카카오는 기본 지원을 하지 않습니다.
            구글의 기본 코드는 sub입니다.
            네이버 로그인과 구글 로그인을 동시 지원할 때 사용됩니다.
        */
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 다음 클래스
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);
        httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())), attributes.getAttributes(), attributes.getNameAttributeKey());

    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail()).map(entity -> entity.update(attributes.getName(), attributes.getPicture())).orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}
