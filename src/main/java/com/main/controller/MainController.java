package com.main.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.main.dao.UserRepo;
import com.main.entites.User;
import com.main.service.EmailService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class MainController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private EmailService emailservice;
	

	@GetMapping("/home")
	public String home(Model m) {
		m.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	@GetMapping("/signup")
	public String signUp(Model m) {
		m.addAttribute("title", "Register - Smart Contact Manager");
		m.addAttribute("user", new User());
		return "signup";
	}

	@GetMapping("/about")
	public String aboutpage(Model m) {
		m.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}

	@PostMapping("/do_register")
	public String register(@Valid @ModelAttribute("user") User user, BindingResult result1,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model m,
			HttpSession session) {

		try {
			if (!agreement) {
				System.out.println("you have not agreed the terms and conditions");
				throw new Exception("you have not agreed the terms and conditions");
			}
			if (result1.hasErrors()) {
				System.out.println("error" + result1.toString());
				m.addAttribute("user", user);
				return "signup";
			}
			user.setRole("User");
			user.setEnabled(true);
			user.setImageUrl("default.jpg");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			System.out.println("user " + user);
			System.out.println("agreement " + agreement);
			User result = this.userRepo.save(user);
			m.addAttribute("user", new User());
			session.setAttribute("message", new com.main.helper.Message("Successfully Register", "alert-success"));
			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			m.addAttribute("user", user);
			session.setAttribute("message",
					new com.main.helper.Message("something sent wrong !!" + e.getMessage(), "alert-danger"));
			return "signup";
		}

	}

	@GetMapping("/signin")
	public String login(Model m) {
		m.addAttribute("title", "Login - Smart Contact Manager");
		return "login";

	}

	@GetMapping("/forgetpass")
	public String forgetform(Model m) {
		m.addAttribute("title", "Forget Password - Smart Contact Manager");
		return "forgetform";
	}

	@PostMapping("/do_SendOTP")
	public String sendOtp(@RequestParam("emailforget") String emailforget, HttpSession session) {

		System.out.println("Email : " + emailforget);
		Random random = new Random(1000);
		int otp = random.nextInt(999999);
		System.out.println("OTP : " + otp);

		String to = emailforget;
		String subject = "OTP";
		String message = "OTP " + otp + " FOR VERIFYING EMAIL TO FORGET PASSWORD";

		boolean result = this.emailservice.sendEmail(to, subject, message);

		if (result) {
			session.setAttribute("otp1", otp);
			session.setAttribute("emailforget1", emailforget);
			return "verifyotp";
		} else {
			session.setAttribute("message",
					new com.main.helper.Message("something sent wrong !!....check your email", "alert-danger"));
			return "forgetform";
		}

	}

	@PostMapping("/verifyingOTP")
	public String verifyingotp(@RequestParam("otpByEmail") int otpByEmail, HttpSession session) {

		System.out.println("otpByform  :" + otpByEmail);
		int myotp = (int) session.getAttribute("otp1");
		String emailforget1 = (String) session.getAttribute("emailforget1");

		if (myotp == otpByEmail) {
			User userByUserName = this.userRepo.getUserByUserName(emailforget1);
			if (userByUserName == null) {
				session.setAttribute("message",
						new com.main.helper.Message("User does not exist with this Email...", "alert-danger"));
				return "forgetform";
			} else {
				return "change_password_form";
			}

		} else {

			session.setAttribute("message",
					new com.main.helper.Message("Incorrect OTP...try again..!", "alert-danger"));
			return "verifyotp";
		}

	}
	@PostMapping("/change-Password")
	public String newchangePassword(@RequestParam("newpassword1") String newpassword1,@RequestParam("newpassword2") String newpassword2,HttpSession httpSession) {
		
		if(newpassword1.equals(newpassword2))
		{
			String email = (String) httpSession.getAttribute("emailforget1");
			User userByUserName = this. userRepo.getUserByUserName(email);
			userByUserName.setPassword(this.passwordEncoder.encode(newpassword1));
			this.userRepo.save(userByUserName);
			httpSession.setAttribute("message",
					new com.main.helper.Message("Password Change successfully....", "alert-success"));
			return "login";
			
		}else {
			httpSession.setAttribute("message",
					new com.main.helper.Message("Password does not match in both field", "alert-danger"));
			return  "change_password_form";
		}
		
		
		}

}
