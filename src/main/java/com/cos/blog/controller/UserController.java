package com.cos.blog.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.cos.blog.model.KakaoProfile;
import com.cos.blog.model.NaverProfile;
import com.cos.blog.model.NaverToken;
import com.cos.blog.model.OAuthToken;
import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// 인증이 안된 사용자들이 출입할 수 있는 경로를 /auth/**
// 그냥 주소가 / 이면 index.jsp 허용
// static이하에 있는 /js/**, /css/**, /image/** 도 허용

@Controller
public class UserController {

	@Value("${cos.key}")
	private String cosKey;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@GetMapping("/auth/loginForm")
	public String loginForm() {

		return "user/loginForm";
	}

	@GetMapping("/auth/joinForm")
	public String joinForm() {

		return "user/joinForm";
	}

	@GetMapping("/user/updateForm")
	public String updateForm() {
		return "user/updateForm";
	}

	@GetMapping("/auth/kakao/callback")
	public String kakaoCallback(String code) { 

		// POST 방식으로 key=value 데이터를 요청 (카카오 인증서버 측으로)
		// Retrofit2
		// OkHttp
		// RestTemplate

		RestTemplate rt = new RestTemplate();

		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HttpBody 오브젝트 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "5c3c14dd9b727874ca9dd269670816cc");
		params.add("redirect_uri", "http://localhost:8000/auth/kakao/callback");
		params.add("code", code);

		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

		ResponseEntity<String> response = rt.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST,
				kakaoTokenRequest, String.class // 응답 받는 객체의 타입
		);

		// Gson, Json Simple, ObjectMappaer (json 데이터 파싱)
		ObjectMapper objectMapper = new ObjectMapper();
		OAuthToken oauthToken = null;

		try {
			oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		System.out.println("카카오 엑세스 토큰 : " + oauthToken.getAccess_token());

		// POST 방식으로 토큰을 통해 사용자 정보 조회
		RestTemplate rt2 = new RestTemplate();

		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Authorization", "Bearer " + oauthToken.getAccess_token());
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers2);

		ResponseEntity<String> kakaoProfileResponse = rt2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST,
				kakaoProfileRequest, String.class);

		ObjectMapper objectMapper2 = new ObjectMapper();
		KakaoProfile kakaoProfile = null;

		try {
			kakaoProfile = objectMapper2.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		// User 오브젝트: username, password, email
		System.out.println("카카오 아이디(번호) : " + kakaoProfile.getId());
		System.out.println("카카오 이메일 : " + kakaoProfile.getKakao_account().getEmail());

		System.out.println("블로그서버 유저네임 : " + kakaoProfile.getKakao_account().getEmail() + "_" + kakaoProfile.getId());
		System.out.println("블로그서버 이메일 : " + kakaoProfile.getKakao_account().getEmail());
		System.out.println("블로그서버 패스워드 : " + cosKey);
		System.out.println("블로그서버 권한 : " + RoleType.USER);

		// 회원가입
		User user = User.builder().username(kakaoProfile.getKakao_account().getEmail() + "_" + kakaoProfile.getId())
				.password(cosKey)
				.email(kakaoProfile.getKakao_account().getEmail())
				.oauth("kakao")
				.build();

		// 가입자 혹은 비가입자 체크해서 처리
		User user2 = userService.회원찾기(user.getUsername());
		if (user2 == null) {
			userService.회원가입(user);
		}
		
		// 로그인 처리
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		return "redirect:/";
	}
	
	@GetMapping("/auth/naver/callback")
	public String naverCallback(String code, String state) {
		// callback으로 전달받은 code 정보로 access token 발급 요청
		RestTemplate rt = new RestTemplate();
		
		ResponseEntity<String> naverTokenResponse = rt.exchange(
				"https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&client_id=HU0TkhbXfbZ0soHwXpW5&client_secret=ZfQe3nTmWR&code="+code+"&state="+state, 
				HttpMethod.GET,
				null,
				String.class);
		
		ObjectMapper objectMapper = new ObjectMapper();
		NaverToken naverToken = null;
		
		try {
			naverToken = objectMapper.readValue(naverTokenResponse.getBody(), NaverToken.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		// access token을 이용하여 프로필 api 호출
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", naverToken.getToken_type() + " " + naverToken.getAccess_token());
		
		HttpEntity<MultiValueMap<String, String>> naverProfileRequest = new HttpEntity<>(headers);
		ResponseEntity<String> naverProfileResponse = rt.exchange(
				"https://openapi.naver.com/v1/nid/me",
				HttpMethod.POST,
				naverProfileRequest,
				String.class
		);
		
		ObjectMapper objectMapper2 = new ObjectMapper();
		NaverProfile naverProfile = null;
		try {
			naverProfile = objectMapper2.readValue(naverProfileResponse.getBody(), NaverProfile.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		// naver로 부터 얻은 프로필 정보를 이용한 회원가입
		String username = naverProfile.getResponse().getName() + "_" + naverProfile.getResponse().getId();
		String password = cosKey;
		String email = naverProfile.getResponse().getEmail();
		String oauth = "naver";
		
		User user = userService.회원찾기(username);
		if(user == null) {
			userService.회원가입(User.builder().username(username).password(password).email(email).oauth(oauth).build());
		}
		
		// 로그인 처리
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		return "redirect:/";
	}

}
