package com.cos.blog.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blog.model.Board;
import com.cos.blog.model.Reply;
import com.cos.blog.model.User;
import com.cos.blog.repository.BoardRepository;
import com.cos.blog.repository.ReplyRepository;

// 스프링이 컴포넌트 스캔을 통해서 Bean에 등록을 해줌.
@Service
public class BoardService {

	private final static String VIEWCOOKIENAME = "alreadyViewCookie" ;
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private ReplyRepository replyRepository;

	@Transactional
	public void 글쓰기(Board board, User user) { // title, content
		board.setCount(0);
		board.setUser(user);
		boardRepository.save(board);
	}

	@Transactional(readOnly = true)
	public Page<Board> 글목록(Pageable pageable) {
		return boardRepository.findAll(pageable);
	}

	@Transactional(readOnly = true)
	public Board 글상세보기(int id) {
		return boardRepository.findById(id).orElseThrow(() -> {
			return new IllegalArgumentException("글 상세보기 실패 : 아이디를 찾을 수 없습니다.");
		});
	}

	@Transactional
	public void 조회수증가(int id, HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		boolean checkCookie = false;
		int result = 0;
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals(VIEWCOOKIENAME + id)) checkCookie = true;
			}
			if(!checkCookie) {
				Cookie newCookie = createCookieForNotOverlap(id);
				response.addCookie(newCookie);
				result = boardRepository.updateCount(id);
			}
		}else {
			Cookie newCookie = createCookieForNotOverlap(id);
			response.addCookie(newCookie);
			result = boardRepository.updateCount(id);
		}
	}
	
	@Transactional
	public void 글삭제하기(int id) {
		boardRepository.deleteById(id);
	}
	
	@Transactional
	public void 글수정하기(int id, Board requestBoard) {
		Board board = boardRepository.findById(id).orElseThrow(() -> {
			return new IllegalArgumentException("글 찾기 실패 : 아이디를 찾을 수 없습니다.");
		});   // 영속화 완료
		board.setTitle(requestBoard.getTitle());
		board.setContent(requestBoard.getContent());
		// 해당 함수 종료시(Service 종료될 때) 트랜잭션이 종료된다. 이 때, 더티체킹 - 자동 업데이트됨. db flush
	}
	
	@Transactional
	public void 댓글쓰기(int boardId, Reply reply, User user) {
		Board board = boardRepository.findById(boardId).orElseThrow(() -> {
			return new IllegalArgumentException("글 찾기 실패 : 아이디를 찾을 수 없습니다.");
		});
		reply.setBoard(board);
		reply.setUser(user);
		replyRepository.save(reply);
		
		/* 네이티브 쿼리를 사용하는 경우
		 * replyRespository.mSave(replySaveRequestDto.getUserId(), replySaveRequestDto.getBoardId(), replySaveRequestDto.getContent());
		 */
	}
	
	@Transactional
	public void 댓글삭제(int replyId) {
		replyRepository.deleteById(replyId);
	}
	
	private Cookie createCookieForNotOverlap(int id) {
		Cookie cookie = new Cookie(VIEWCOOKIENAME + id, String.valueOf(id));
		cookie.setComment("조회수 중복 증가 방지 쿠키");
		cookie.setMaxAge(getRemainSecondForTomorrow());
		cookie.setHttpOnly(true);
		return cookie;
	}
	
	// 다음날 정각까지 남은 시간(초)
	private int getRemainSecondForTomorrow() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime tomorrow = LocalDateTime.now().plusDays(1L).truncatedTo(ChronoUnit.DAYS);
		return (int) now.until(tomorrow, ChronoUnit.SECONDS);
	}

}
