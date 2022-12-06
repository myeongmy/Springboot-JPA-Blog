let index = {
	init: function(){
		$("#btn-save").on("click", ()=>{   // function(){}말고 ()=>{} 사용한 이유는 this를 바인딩하기 위해서
			this.save();
		});
		$("#btn-login").on("click", ()=>{
			this.login();
		});
		$("#btn-update").on("click", ()=>{
			this.update();
		});
	},
	
	save: function(){
		//alert("user의 save 함수 호출됨");
		let data ={
			username: $("#username").val(),
			password: $("#password").val(),
			email: $("#email").val()
		};
		
		//console.log(data);
		
		// ajax 호출 시 default가 비동기 호출
		$.ajax({
			// 회원가입 수행 요청
			type: "POST",
			url: "/auth/joinProc",
			data: JSON.stringify(data),      //자바스크립트 객체를 JSON으로 변환
			contentType: "application/json; charset=utf-8",    // 요청 http 바디 데이터가 어떤 타입인지(MIME)
			dataType: "json"    // 요청에 대한 응답 데이터는 기본적으로 문자열 => "json"이라고 적으면 자바스크립트 오브젝트로 변환해줌
		}).done(function(resp){   // resp: 자바스크립트 오브젝트 형태
			alert("회원가입이 완료되었습니다.");
			location.href = "/";
		}).fail(function(error){    // http 응답상태가 200이 아닌 경우
			alert(JSON.stringify(error));
		});      // ajax 통신을 이용해서 3개의 데이터를 json으로 변경하여 insert 요청
	},
	
	login: function(){
		//alert("user의 save 함수 호출됨");
		let data ={
			username: $("#username").val(),
			password: $("#password").val()
		};
		
		//console.log(data);
		
		// ajax 호출 시 default가 비동기 호출
		$.ajax({
			// 회원가입 수행 요청
			type: "POST",
			url: "/api/user/login",
			data: JSON.stringify(data),      //자바스크립트 객체를 JSON으로 변환
			contentType: "application/json; charset=utf-8",     // 요청 http 바디 데이터가 어떤 타입인지(MIME)
			dataType: "json"    // 요청에 대한 응답 데이터는 기본적으로 문자열 => "json"이라고 적으면 자바스크립트 오브젝트로 변환해줌
		}).done(function(resp){   // resp: 자바스크립트 오브젝트 형태
			alert("로그인이 완료되었습니다.");
			location.href = "/";
		}).fail(function(error){    // http 응답상태가 200이 아닌 경우
			alert(JSON.stringify(error));
		});      // ajax 통신을 이용해서 3개의 데이터를 json으로 변경하여 insert 요청
	},
	
	update: function(){
		let data ={
			id: $("#id").val(),
			username: $("#username").val(),
			password: $("#password").val(),
			email: $("#email").val()
		};
		
		$.ajax({
			type: "PUT",
			url: "/user",
			data: JSON.stringify(data),     
			contentType: "application/json; charset=utf-8",     
			dataType: "json"    
		}).done(function(resp){   
			alert("회원수정이 완료되었습니다.");
			location.href = "/";
		}).fail(function(error){   
			alert(JSON.stringify(error));
		});      
	}
}

index.init();