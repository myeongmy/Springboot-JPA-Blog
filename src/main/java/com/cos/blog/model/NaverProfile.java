package com.cos.blog.model;

import lombok.Data;

@Data
public class NaverProfile {

	public String resultcode;
	public String message;
	public Response response;

	@Data
	public class Response {

		public String id;
		public String profile_image;
		public String email;
		public String name;

	}
}
