package com.geraudwilling.webscraping.util;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import com.geraudwilling.webscraping.bean.ScrapingResult;

public class AppHelper {
	
	static final Logger logger = Logger.getLogger(AppHelper.class);


	public static int isRdvAvailable(List<ScrapingResult> results) {
		for(int i=0; i<results.size(); i++) {
			if(results.get(i).isRdvAvailable()) {
				return i+1;
			}
		}
		return 0;		
	}
	
	
	public static void sendMail(List<ScrapingResult> input) throws IOException, AddressException, MessagingException, ConfigurationException {
		logger.info("Sending Mail ...");

		// Step 1
		logger.debug("\n 1st ===> setup Mail Server Properties..");
		Properties props = System.getProperties();
		props.put("mail.smtp.port", PropertiesReader.INSTANCE.getIntProperty("mail.smtp.port"));
		props.put("mail.smtp.auth", PropertiesReader.INSTANCE.getProperties("mail.smtp.auth"));
		props.put("mail.smtp.starttls.enable", "true");
		logger.debug("Mail Server Properties have been setup successfully..");

	    // Step 2
		logger.debug("\n\n 2nd ===> get Mail Session..");
		Session session =Session.getDefaultInstance(props, new Authenticator() {
			// Set the account information session, transport will send mail
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				try {
					return new PasswordAuthentication(
							PropertiesReader.INSTANCE.decryptPropertyValue("mail.smtp.user"), 
							PropertiesReader.INSTANCE.decryptPropertyValue("mail.smtp.password"));
				} catch (ConfigurationException | IOException e) {
					logger.fatal("Cannot set session object...Mail will not been sent ",e);
				}
				return null;
			}
		}); 
		Message message = new MimeMessage(session);
		message.addRecipients(Message.RecipientType.TO,InternetAddress.parse(PropertiesReader.INSTANCE.decryptPropertyValue("mail.to")));	
		message.setSubject(PropertiesReader.INSTANCE.getProperties("mail.subject"));
		StringBuilder body = new StringBuilder("Dear,<br/><br/> A rendez-vous has been found for <br/>");
		for(int i=0; i< input.size(); i++ ) {
			if (input.get(i).isRdvAvailable()) {
				body.append("Guichet numero <b>").append(i + 1).append("<b> <br/>");
			}
		}
		body.append("<br/>Regards,<br/>Admin.");
		message.setContent(body.toString(), "text/html");
		logger.debug("Mail Session has been created successfully..");

	
		// Step3
		logger.debug("\n\n 3rd ===> Get Session and Send mail");
		Transport transport = session.getTransport("smtp");
		transport.connect (PropertiesReader.INSTANCE.getProperties("mail.smtp.host"), 
				PropertiesReader.INSTANCE.decryptPropertyValue("mail.smtp.user"), 
				PropertiesReader.INSTANCE.decryptPropertyValue("mail.smtp.password"));
		transport.sendMessage(message,message.getAllRecipients());
		transport.close(); 
		logger.info("Mail Successfuly Sent");

	}
	
	

}
