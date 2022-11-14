package com.cos.blog.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// ORM -> Java(다른 언어) Object -> 테이블로 자동매핑
@Entity   //User 클래스가 MySQL에 자동으로 테이블이 생성된다.
// @DynamicInsert     // insert 시에 null인 필드 제외
public class User {
	
	@Id     //primary key
	@GeneratedValue(strategy = GenerationType.IDENTITY)   // 프로젝트에서 연결된 DB의 넘버링 전략을 따라간다.
	private int id;       //시퀀스(오라클), auto_increment(MySQL)
	
	@Column(nullable = false, length = 30)
	private String username;  //아이디
	
	@Column(nullable = false, length = 100)     // 123456 => 해쉬 (비밀번호 암호화하여 DB 저장)
	private String password;  //비밀번호
	
	@Column(nullable = false, length = 100)
	private String email;        //이메일
	
	// @ColumnDefault("user")
	@Enumerated(EnumType.STRING)   //DB는 RoleType이란 자료형이 없기 때문에 해당 type이 string이라는 것을 명시해주어야함
	private  RoleType role;     // Enum을 쓰는게 좋다. //admin, user, manager (Enum을 활용하면 데이터의 도메인을 설정할 수 있다)
	
	@CreationTimestamp       // 시간이 자동 입력
	private Timestamp createDate;
}
