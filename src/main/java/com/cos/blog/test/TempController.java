package com.cos.blog.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

 //파일을 리턴
@Controller        
public class TempController {
	
	@GetMapping("/temp/home")
	public String tempHome() {
		System.out.println("tempHome()");
		
		//파일 리턴 기본 경로 : src/main/resources/static
		return "/home.html";
	}
	
	@GetMapping("/temp/img")
	public String tempImg() {
		return "/zanmangRoopy.jpg";
	}
}
