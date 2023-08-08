package com.main.controller;
import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.main.dao.ContactRepo;
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

	@Autowired
	private ContactRepo cr;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

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
	public String processContact(@Valid @ModelAttribute Contact contact, BindingResult bindingResult,
			@RequestParam("image") MultipartFile file, Principal principal, HttpSession session) {
		try {
			System.out.println(bindingResult);
			System.out.println(contact);
			String name = principal.getName();
			User user = this.u.getUserByUserName(name);
			if (file.isEmpty()) {
				System.out.println("file is empty");
				contact.setImage("contact.png");
			} else {
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/image/").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + "//" + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is uploaded");
				contact.setUser(user);
				user.getContracts().add(contact);
				this.u.save(user);
				System.out.println("Data added");
				session.setAttribute("message",
						new com.main.helper.Message("Your Contact Is Added !! Add More..", "success"));

			}
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message",
					new com.main.helper.Message("Something Went Wrong !! Try Again..", "danger"));
		}
		return "addcontactform";

	}
	/* show contact */

	@GetMapping("/show-Contact/{page}")
	public String showContact(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title", "Show Contact - Smart Contact Manager");
		String username = principal.getName();
		User userByUserName = this.u.getUserByUserName(username);
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.cr.findContactsByUser(userByUserName.getUid(), pageable);
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		return "showContact";
	}
	/* single contact profile */

	@GetMapping("/{cid}/contact")
	public String singleProfile(@PathVariable("cid") Integer cid, Model m, Principal principal) {
		System.out.println(cid);
		Optional<Contact> findById = this.cr.findById(cid);
		Contact contact2 = findById.get();
		String username = principal.getName();
		User user = this.u.getUserByUserName(username);
		if (user.getUid() == contact2.getUser().getUid()) {
			m.addAttribute("contact2", contact2);
			m.addAttribute("title", contact2.getName());
		}

		return "contact_detail";

	}

	/* deleting single contact */
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, Model m, Principal principal,HttpSession session) {
		
		Optional<Contact> findById = this.cr.findById(cid);
		Contact contact = findById.get();
			User user = this.u.getUserByUserName(principal.getName());
			user.getContracts().remove(contact);
			this.u.save(user);
			this.cr.delete(contact);
			System.out.println("delete");
			session.setAttribute("message",new com.main.helper.Message("Your Contact Is Deleted Succcessfully..", "success"));
		
		return "redirect:/user/show-Contact/0";
	}

	/* handing update form */
	@PostMapping("/update-from/{cid}")
	public String updateFrom(@PathVariable("cid") Integer cid,Model m)
	{
		m.addAttribute("title", "Update Contact - Smart Contact Manager");
		Optional<Contact> contactId = this.cr.findById(cid);
		Contact contact = contactId.get();
		m.addAttribute("contact3", contact);
		return "updateForm";
	}
	
	/* updating contact */
	@PostMapping("/update-contact")
	public String UpdateContact(@Valid @ModelAttribute Contact contact,BindingResult bindingResult ,@RequestParam("image") MultipartFile file, Principal principal, HttpSession session)
	{
		
		try {
			//old contact detail
			Contact oldContact = this.cr.findById(contact.getCid()).get();			
			if(!file.isEmpty())
			{
				//delete
				File deleteFile = new ClassPathResource("static/image/").getFile();
				File file1=new File(deleteFile, oldContact.getImage());
				file1.delete();
				
					
				//rewrite
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/image/").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + "//" + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			else {
				contact.setImage(oldContact.getImage());
			}
			User user = this.u.getUserByUserName(principal.getName());
			contact.setUser(user);
			 this.cr.save(contact);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "redirect:/user/"+contact.getCid()+"/contact";
		
	}

	/* user profile handler */
	@GetMapping("/profile")
	public String profile(Model m)
	{
		m.addAttribute("title", "Profile - Smart Contact Manager");
		return "profile";	
	}
	
	/* user update form handler */
	@PostMapping("/profile-update/{uid}")
	public String updateProfileForm(@PathVariable("uid")Integer uid,Model m)
	{
		m.addAttribute("title", "Update User - Smart Contact Manager");
		Optional<User> findById = this.u.findById(uid);
		User user = findById.get();
		System.out.println("newww"+user);
		m.addAttribute("user", user);
		
		return "userUpdateForm";
	}
	
	/* update user */
	
	@PostMapping("/update-user")
	public String updateUser(@Valid @ModelAttribute User newUser,BindingResult bindingResult, @RequestParam("imageUrl") MultipartFile file, Principal principal, HttpSession session)
	{
		try {
			//old contact detail
			User oldUser = this.u.getUserByUserName(principal.getName());
			if(!file.isEmpty())
			{
				//rewrite
				newUser.setImageUrl(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/image/").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + "//" + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			else {
				newUser.setImageUrl(oldUser.getImageUrl());
			}
			this.u.save(newUser);
			
	}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:profile";
}
	
/* delete User handler */
	
	@GetMapping("/deleteUser/{uid}")
	public String deleteUser(@PathVariable("uid")Integer uid)
	{
		Optional<User> findById = this.u.findById(uid);
		User user1 = findById.get();
		this.u.delete(user1);
		return "deletePage";
		
	}
	/* setting handler */
	
	@GetMapping("/setting")
	public String setting(Model m)
	{
		m.addAttribute("title", "Settings - Smart Contact Manager");
		return "setting";
	}
	/* change password handler */
	
	@PostMapping("/change-Password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword")String newPassword,Principal principal,HttpSession session)
	{
		System.out.println("old ="+oldPassword);
		System.out.println("new ="+newPassword);
		User user = this.u.getUserByUserName(principal.getName());
		System.out.println("current password ="+user.getPassword());
		if(this.bCryptPasswordEncoder.matches( oldPassword,user.getPassword()))
		{
			user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.u.save(user);
			session.setAttribute("message",new com.main.helper.Message("Your Password Is Changed...", "success"));
		}
		else {
			session.setAttribute("message",new com.main.helper.Message("Your Old Password Is Wrong", "danger"));
		}
		return "setting";
		
	}
}
