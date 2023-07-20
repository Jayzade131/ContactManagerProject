package com.main.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.main.dao.UserRepo;
import com.main.entites.Contact;
import com.main.entites.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepo u;

	/* method for adding common data to response */
	@ModelAttribute
	public void addCommonData(Model m, Principal p) {

		String userName = p.getName();
		System.out.println("username " + userName);
		User user1 = u.getUserByUserName(userName);
		System.out.println("User " + user1);
		m.addAttribute("user", user1);
	}

	/* home controller */
	@GetMapping("/input")
	public String dashboard(Model m, Principal p) {
		m.addAttribute("title", "Dashboard - Smart Contact Manager");
		return "user_dashboard";
	}

	/* add form controller */
	@GetMapping("/add-contact")
	public String addcontactpage(Model m) {
		m.addAttribute("title", "Add Contact - Smart Contact Manager");

		return "addcontactform";
	}

	/* add contact */
	@PostMapping("/do_addcontact")
	public String processContact(@Valid @ModelAttribute Contact contact,BindingResult bindingResult,@RequestParam("image") MultipartFile file,Principal principal,HttpSession session) {
		try {
			System.out.println(bindingResult);
			System.out.println(contact);
			String name = principal.getName();
			User user = this.u.getUserByUserName(name);
			if(file.isEmpty())
			{
				System.out.println("file is empty");
			}
			else {
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/image/").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+"//"+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is uploaded");
				contact.setUser(user);
				user.getContracts().add(contact);
				this.u.save(user);
				System.out.println("Data added");
				session.setAttribute("message",new com.main.helper.Message("Your Contact Is Added !! Add More..", "success"));
		
		} 
		}catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message",new com.main.helper.Message("Something Went Wrong !! Try Again..", "danger"));
		}
		return "addcontactform";

	}
}
