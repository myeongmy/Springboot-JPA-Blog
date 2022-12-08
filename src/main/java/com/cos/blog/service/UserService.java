package com.cos.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.repository.UserRepository;

// 스프링이 컴포넌트 스캔을 통해서 Bean에 등록을 해줌.
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Transactional
	public void 회원가입(User user) {
		String rawPassword = user.getPassword();
		String encPassword = encoder.encode(rawPassword);
		user.setPassword(encPassword);
		user.setRole(RoleType.USER);
		userRepository.save(user); // Exception이 일어나면 GlobalExceptionHandler에서 처리해주므로 따로 에러 처리할 필요 없다.
	}

	@Transactional
	public void 회원수정(User user) {
		User persistence = userRepository.findById(user.getId()).orElseThrow(() -> {
			return new IllegalArgumentException("회원 찾기 실패");
		});

		// Validate 체크
		if (persistence.getOauth() == null || persistence.getOauth() == "") {
			persistence.setPassword(encoder.encode(user.getPassword()));
			persistence.setEmail(user.getEmail());
		}

		// 회원수정 함수 종료시 = 서비스 종료 = 트랜잭션 종료 = commit이 자동으로 된다.
		// 영속화된 persistence 객체의 변화가 감지되면 더티체킹이 되어 DB flush 일어난다.
	}

	@Transactional(readOnly = true)
	public User 회원찾기(String username) {
		User user = userRepository.findByUsername(username).orElseGet(() -> {
			return null;
		});
		return user;
	}
//	@Transactional(readOnly = true)  //select 할 때 트랜잭션 시작, 서비스 종료시에 트랜잭션 종료 (데이터 정합성 유지)
//	public User 로그인(User user) {
//		return userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
//	}
}
