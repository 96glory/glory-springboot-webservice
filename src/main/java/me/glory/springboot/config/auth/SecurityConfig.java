package me.glory.springboot.config.auth;

import lombok.RequiredArgsConstructor;
import me.glory.springboot.domain.user.Role;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                    // CSRF 공격 방어. POST 방식의 모든 전송에 hidden 값을 추가함
                    .csrf().disable()
                    // h2-console 화면을 사용하기 위해 해당 옵션들을 disabled
                    .headers().frameOptions().disable()
                .and()
                    // URL 별 권한 관리를 설정하는 옵션의 시작점, antMatchers를 사용하기 위함
                    .authorizeRequests()
                    // antMatchers : 권한 관리 대상을 지정하는 옵션. URL, HTTP 메소드 별로 관리가 가능하다.
                    .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/").permitAll()
                    // POST 메소드이면서, /api/v1/** 주소를 가진 API에는 USER 권한을 가진 사람만 접근할 수 있음.
                    .antMatchers("/api/v1/**").hasRole(Role.USER.name())
                    // anyRequest : antMatchers 이외의 요청들
                    // authenticated : 나머지 URL들은 모두 인증된 사용자들(로그인 된 사용자들)에게만 허용하게 하였음.
                    .anyRequest().authenticated()
                .and()
                    // 로그아웃 성공 시 "/"로 이동
                    .logout().logoutSuccessUrl("/")
                .and()
                    // OAuth2 로그인 기능에 대한 여러 설정의 진입점
                    .oauth2Login()
                    // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정들을 담당
                    .userInfoEndpoint()
                    // 소셜 로그인 성공 시 후속 조치를 진행할 UserService 인터페이스의 구현체를 등록합니다. 소셜 서비스 서버에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능을 명시할 수 있습니다.
                    .userService(customOAuth2UserService);
    }
}
