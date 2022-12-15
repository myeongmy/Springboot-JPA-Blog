package com.cos.blog.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.cos.blog.model.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Integer>{
	
	// 댓글 작성이 네이티브 쿼리 사용하기
	@Modifying
	@Query(value="INSERT INTO REPLY (userId, boardId, content, createDate) values (?1, ?2, ?3, now())", nativeQuery = true)
	int mSave(int userId, int boardId, String content);  // 업데이트된 행의 개수를 리턴해줌.
}
