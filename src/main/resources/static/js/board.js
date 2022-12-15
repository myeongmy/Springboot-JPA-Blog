let index = {
	init: function() {
		$("#btn-save").on("click", () => {
			this.save();
		});

		$("#btn-delete").on("click", () => {
			this.deleteById();
		});
		
		$("#btn-update").on("click", () => {
			this.update();
		});
		
		$("#btn-reply-save").on("click", () => {
			this.replySave();
		});
	},

	save: function() {
		let data = {
			title: $("#title").val(),
			content: $("#content").val()
		};

		$.ajax({
			type: "POST",
			url: "/api/board",
			data: JSON.stringify(data),      //자바스크립트 객체를 JSON으로 변환
			contentType: "application/json; charset=utf-8",    // 요청 http 바디 데이터가 어떤 타입인지(MIME)
			dataType: "json"    // 요청에 대한 응답 데이터는 기본적으로 문자열 => "json"이라고 적으면 자바스크립트 오브젝트로 변환해줌
		}).done(function(resp) {   // resp: 자바스크립트 오브젝트 형태
			alert("글 작성이 완료되었습니다.");
			location.href = "/";
		}).fail(function(error) {    // http 응답상태가 200이 아닌 경우
			alert(JSON.stringify(error));
		});      // ajax 통신을 이용해서 3개의 데이터를 json으로 변경하여 insert 요청
	},

	deleteById: function() {
		let id = $("#id").text();

		$.ajax({
			type: "DELETE",
			url: "/api/board/" + id,
			dataType: "json"
		}).done(function(resp) {
			alert("삭제가 완료되었습니다.");
			location.href = "/";
		}).fail(function(error) {
			alert(JSON.stringify(error));
		});
	},
	
	update: function() {
		let id = $("#id").val();
		
		let data = {
			title: $("#title").val(),
			content: $("#content").val()
		};
		
		$.ajax({
			type: "PUT",
			url: "/api/board/" + id,
			data: JSON.stringify(data), 
			contentType: "application/json; charset=utf-8",   
			dataType: "json"
		}).done(function(resp) {
			alert("글 수정이 완료되었습니다.");
			location.href = "/";
		}).fail(function(error) {
			alert(JSON.stringify(error));
		});
	},
	
	replySave: function() {
		
		let data = {
			content: $("#reply-content").val()
		};
		let boardId = $("#id").text();
		
		$.ajax({
			type: "POST",
			url: `/api/board/${boardId}/reply`,
			data: JSON.stringify(data), 
			contentType: "application/json; charset=utf-8",   
			dataType: "json"
		}).done(function(resp) {
			alert("댓글 작성이 완료되었습니다.");
			location.href = `/board/${boardId}`;
		}).fail(function(error) {
			alert(JSON.stringify(error));
		});
	}
}

index.init();