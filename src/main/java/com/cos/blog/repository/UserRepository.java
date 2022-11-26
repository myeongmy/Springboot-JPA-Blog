package com.cos.blog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.blog.model.User;

// DAO
// 자동으로 bean 등록이 되므로 @Repository 어노테이션 생략 가능
public interface UserRepository extends JpaRepository<User, Integer>{
	// JPA Naming 쿼리 전략
	// select * from USER where username = ?1 and password = ?2;
	// User findByUsernameAndPassword(String username, String password);
		
	// select * from USER where username = ?1;
	Optional<User> findByUsername(String username);
	
}
