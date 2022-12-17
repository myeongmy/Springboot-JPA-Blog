package com.cos.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.cos.blog.model.Board;

public interface BoardRepository extends JpaRepository<Board, Integer>{
	
		@Modifying
		@Query(value="UPDATE BOARD SET COUNT = COUNT + 1 WHERE ID = ?1", nativeQuery = true)
		int updateCount(int boardId);  // 업데이트된 행의 개수를 리턴해줌.
}
