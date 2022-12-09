<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ include file="../layout/header.jsp"%>

<div class="container">
	<form action="/auth/loginProc" method="post">
		<div class="form-group">
			<label for="username">Username</label>
			<input type="text" name="username" class="form-control" placeholder="Enter username" id="username">
		</div>

		<div class="form-group">
			<label for="password">Password</label>
			<input type="password" name="password" class="form-control" placeholder="Enter password" id="password">
		</div>
		<button id="btn-login" class="btn btn-primary">로그인</button>
		<a href="https://kauth.kakao.com/oauth/authorize?client_id=5c3c14dd9b727874ca9dd269670816cc&redirect_uri=http://localhost:8000/auth/kakao/callback&response_type=code"><img height="38px" src="/image/kakao_login_button.png"/></a>
		<a href="https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=HU0TkhbXfbZ0soHwXpW5&state=STATE_STRING&redirect_uri=http://localhost:8000/auth/naver/callback"><img width = "76px" height="38px" src="/image/naver_login_button.png"/></a>
	</form>

</div>

<%@ include file="../layout/footer.jsp"%>




