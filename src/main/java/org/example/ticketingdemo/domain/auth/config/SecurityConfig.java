package org.example.ticketingdemo.domain.auth.config;

import org.example.ticketingdemo.domain.auth.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration // SecurityFilterChain과 PasswordEncoder를 Bean으로 등록하기 위해 "Spring이 관리하는 설정 클래스" 임을 표시.
@EnableWebSecurity // Spring Security 활성화
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // 의존성 주입

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) { // 생성자 주입
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // PasswordEncoder을 빈으로 등록
    // Spring Security가 비밀번호 암호화/검증할 때 자동으로 가져다 쓰도록 하기 위해 Bean으로 등록함
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt(비밀번호 해싱(Hashing) 알고리즘) 사용
    }

    // Security 필터 체인을 빈으로 등록
    // HTTP 요청 보안 처리 로직(SecurityFilterChain)을 Spring이 자동으로 적용하도록 Bean으로 등록함
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {
                })  // 기본 CORS 설정. 다른 사이트에서 우리 서버에 요청할 수 있게 허용하는 설정.
                .csrf(csrf -> csrf.disable())
                // CSRF 비활성화. CSRF는 기본적으로 브라우저 기반 공격을 막는 기능. Docker로 서버 테스트를 위해 비활성화 시켜둠.
                .formLogin(form -> form.disable())  // 기본 로그인 폼 비활성화
                .httpBasic(basic -> basic.disable()) // HTTP Basic 인증 비활성화
                .authorizeHttpRequests(auth -> auth // 요청 권한 설정
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll() // 회원가입/로그인 허용
                        .anyRequest().authenticated() // 나머지 요청 인증 필요
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
        //http.build() → build()는 Spring Security 라이브러리에서 제공하는 메서드
        //설정을 모은 빌더 객체를 최종 객체로 만드는 메서드.
        //위 설정들을 실제 작동하는 SecurityFilterChain 객체로 만들어 반환.

    }
}


/**
 * SecurityFilterChain이 하는 역할
 * Spring Security에서 HTTP 요청을 처리하는 필터들의 체인
 * 각 요청이 들어오면 필터 하나씩 거쳐서 인증/인가, CSRF, CORS, 로그인 등 처리
 * 상황과 목적에 따라 선택하여 필터 체인 구현 가능.
 */