package com.main.dao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.main.entites.Contact;

public interface ContactRepo extends JpaRepository<Contact , Integer> {
	
	@Query("from Contact as c where c.user.uid =:userId")
	public Page<Contact> findContactsByUser(@Param("userId") int userId,Pageable  pageable);
	
}
