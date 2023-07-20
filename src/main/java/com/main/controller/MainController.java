package com.main.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.main.dao.UserRepo;
import com.main.entites.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
public class MainController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepo userRepo;
	
	@GetMapping("/home")
	public String home(Model m) {
		m.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}
	
	@GetMapping("/signup")
	public String signUp(Model m) {
		m.addAttribute("title", "Register - Smart Contact Manager");
		m.addAttribute("user",new User());
		return "signup";
	}
	@GetMapping("/about")
	public String aboutpage(Model m)
	{
		m.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}
	
	@PostMapping("/do_register")
	public String register(@Valid @ModelAttribute("user")User user,BindingResult result1, @RequestParam(value = "agreement",defaultValue = "false")boolean agreement, Model m,HttpSession session)
	{
		
		try {
			if(!agreement)
			{
				System.out.println("you have not agreed the terms and conditions");
				throw new Exception("you have not agreed the terms and conditions");
			}
			if(result1.hasErrors()) {
				System.out.println("error"+result1.toString());
				m.addAttribute("user", user);
				return "signup";
			}
			user.setRole("User");
			user.setEnabled(true);
			user.setImageUrl("default.jpg");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			System.out.println("user "+user );
			System.out.println("agreement "+agreement);
			User result = this.userRepo.save(user);
			m.addAttribute("user", new User());
			session.setAttribute("message",new com.main.helper.Message("Successfully Register", "alert-success"));
			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			m.addAttribute("user", user);
			session.setAttribute("message",new com.main.helper.Message("something sent wrong !!"+e.getMessage(), "alert-danger"));
			return "signup";
		}
		
		
		
	}
	
	@GetMapping("/signin")
	public String login(Model m)
	{
		m.addAttribute("title", "Login - Smart Contact Manager");
		return "login";
				
	}

}
