package com.cos.blog.model;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Board {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)     //auto_increment
	private int id;
	
	@Column(nullable = false, length = 100)
	private String title;
	
	@Lob    //대용량 데이터
	private String content;   //섬머노트 라이브러리 사용 (<html> 태그가 섞여서 디자인이 됨)
	
	private int count;    //조회수
	
	@ManyToOne            // Many = Board, User = one
	@JoinColumn(name="userId")    // FK 이름을 userId로 설정
	private User user;    // 작성자   (DB는 객체를 저장할 수 없다. 그래서 FK 사용! 그러나 자바에서는 오브젝트 저장 가능)
	
	@OneToMany(mappedBy = "board", fetch = FetchType.EAGER)  // mappedBy는 연관관계의 주인이 아니라는 뜻 (FK 생성 NO...), "board"는 Reply 클래스 안의 해당 필드 값
	private List<Reply> reply;           // 여기는 joinColumn이 필요 없다. (왜냐면, DB에는 FK가 필요 없고 개발 시 편리함을 위해 만든 필드이기 때문에)
	
	@CreationTimestamp
	private Timestamp createDate;
}
