package com.main.service;

import java.util.Properties;

import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	public boolean sendEmail(String to, String subject, String message) {

		String from = "jayzade0131@gmail.com";
		boolean flag = false;

		// create properties class obj
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", true);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.host", "smtp.gmail.com");

		final String username = "jayzade0131";
		final String password = "vvflpnursfmegisq";
		Session session = Session.getInstance(properties, new Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);

			}
		});

		try {

			Message message2 = new MimeMessage(session);
			message2.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message2.setFrom(new InternetAddress(from));
			message2.setSubject(subject);
			message2.setText(message);

			Transport.send(message2);

			flag = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;

	}

}
