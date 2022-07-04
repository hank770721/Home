package com.hkma.home.system.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("index")
public class IndexController {
	@GetMapping({"/","/index"})
	public String indexGet(@RequestParam(required=false, value="userid") String userId, Model model, Principal principal){
		//if (principal != null){
		//	model.addAttribute("userName", principal.getName());
		//}
		//
		//if (userId == null) {
		//	userId = "mia";
		//}
		
		model.addAttribute("userId", userId);
		
		return "index";
	}
	
	@PostMapping({"/","/index"})
	public String indexPost(@RequestParam(required=false, value="userid") String userId, Model model, Principal principal){
		//if (principal != null){
		//	model.addAttribute("userName", principal.getName());
		//}
		//
		//if (userId == null) {
		//	userId = "mia";
		//}
		//
		//model.addAttribute("userId", userId);
		
		return "index";
	}
	
	@GetMapping("/login")
	public String login(HttpServletRequest request){
		return "login";
	}
}
