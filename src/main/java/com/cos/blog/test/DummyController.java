package com.cos.blog.test;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.repository.UserRepository;

@RestController
public class DummyController {
	
	@Autowired       // 의존성 주입(DI)
	private UserRepository userRepository;
	
	@DeleteMapping("/dummy/user/{id}")
	public String deleteUser(@PathVariable int id) {
		try {
		userRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {     // Exception e로 표현해도 된다.
			return "삭제에 실패하였습니다. 해당 id는 DB에 없습니다.";
		}
		return "삭제가 완료되었습니다. id : " + id;
	}
	
	// email, password
	@Transactional    //함수 종료시에 자동 commit
	@PutMapping("/dummy/user/{id}")
	public User updateUser(@PathVariable int id, @RequestBody User requestUser) {  // json 데이터로 요청 => Java Object로 변환해서 받아줌 by MessageConverter의 jackson 라이브러리
		User user = userRepository.findById(id).orElseThrow(new Supplier<IllegalArgumentException>() {
			@Override
			public IllegalArgumentException get() {
				return new IllegalArgumentException("수정에 실패하였습니다.");
			}
		});
		
		user.setEmail(requestUser.getEmail());
		user.setPassword(requestUser.getPassword());
		// userRepository.save(user);
	
		return user;
	}
	
	@GetMapping("/dummy/users")
	public List<User> list(){
		return userRepository.findAll();
	}
	
	// 한페이지당 2건의 데이터를 리턴받아 볼 예정
	@GetMapping("/dummy/user")
	public List<User> pagingList(@PageableDefault(size=2, sort="id", direction=Sort.Direction.DESC) Pageable pageable){
		Page<User> pagingUser = userRepository.findAll(pageable);
		
		return pagingUser.getContent();
	}
	
	// {id}: 주소로 파라미터를 전달받을 수 있음
	// http://localhost:8000/blog/dummy/user/3
	@GetMapping("/dummy/user/{id}")
	public User detail(@PathVariable int id) {
		// 만약, findById 결과가 null이 되면 return되는 값이 null이 되므로 프로그램에 문제가 생김.
		// 따라서, Optional로 User 객체를 감싸서 가져올테니 null인지 아닌지 판단해서 return해!
		User user = userRepository.findById(id).orElseThrow(new Supplier<IllegalArgumentException>() {
			@Override
			public IllegalArgumentException get() {
				return new IllegalArgumentException("해당 유저는 없습니다. id: " + id);
			}
		});
		
		/* 람다식으로도 표현 가능
		 User user = userRepository.findById(id).orElseThrow(() -> {
		 		return new IllegalArgumentException("해당 유저는 없습니다.");
		 });
		 */
		
		// user 객체: java 객체
		// 웹 브라우저에 반환될 때 json으로 변환해서 던져줌
		return user;
	}
	
	@PostMapping("/dummy/join")
	public String join(User user) {    //key-value(약속된 규칙)
		
		System.out.println("username: "+user.getUsername());
		System.out.println("password: "+user.getPassword());
		System.out.println("email: "+user.getEmail());
		
		user.setRole(RoleType.USER);
		userRepository.save(user);
		return "회원가입이 완료되었습니다.";
	}
}
