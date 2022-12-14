package com.cos.blog.model;

import lombok.Data;

@Data
public class NaverToken {
	private String access_token;
	private String refresh_token;
	private String token_type;
	private String expires_in;
}
