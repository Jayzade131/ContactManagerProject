package com.main.controller;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.main.dao.ContactRepo;
import com.main.dao.UserRepo;
import com.main.entites.Contact;
import com.main.entites.User;

@RestController
public class SearchController {
	@Autowired
	private ContactRepo contactRepo;
	@Autowired
	private UserRepo userRepo;
	
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal)
	{
		System.out.println(query);
		User user = this.userRepo.getUserByUserName(principal.getName());
		List<Contact> contact = this.contactRepo.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(contact);
		
	}
}
