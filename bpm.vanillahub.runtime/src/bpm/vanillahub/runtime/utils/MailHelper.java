package bpm.vanillahub.runtime.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.MailcapCommandMap;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.log4j.Logger;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.protocol.FLAGS;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanillahub.core.beans.resources.ServerMail;
import bpm.vanillahub.runtime.mail.MailAuthenticator;
import bpm.vanillahub.runtime.mail.MailConfig;

/**
 * this is the platforms mailer.
 * 
 * Fetches information from vanilla-conf/vanilla.properties to set :
 * bpm.vanilla.mailer.smtp.host, ex smtp.orange.fr bpm.vanilla.mailer.smtp.port,
 * ex 25
 * 
 * @author manu
 * 
 */
public class MailHelper {

	private static final String JAVA_SMTP_HOST = "mail.smtp.host";
	private static final String JAVA_SMTP_PORT = "mail.smtp.port";

	private static final String JAVA_SMTP_AUTH = "mail.smtp.auth";
	private static final String JAVA_SMTP_TLS = "mail.smtp.starttls.enable";
	private static final String JAVA_SMTP_USER = "mail.smtp.user";
	private static final String JAVA_SMTP_PASS = "mail.smtp.password";

	private Logger logger = Logger.getLogger(MailHelper.class);

	private Properties mailProperties;

	public MailHelper() {
	}

	public MailHelper(ServerMail serverMail, List<Parameter> parameters, List<Variable> variables) throws Exception {
		this.mailProperties = initProperties(serverMail, parameters, variables);
	}

	private Properties initProperties(ServerMail server, List<Parameter> parameters, List<Variable> variables) throws Exception {
		logger.info("MailHelper is initing...");

		Properties props = new Properties();

		String smtpHost = server.getSmtpHost(parameters, variables);
		if (smtpHost == null || smtpHost.isEmpty()) {
			throw new Exception("Mailer configuration error, property 'Host SMTP' is not define.");
		}
		logger.info("MailHelper is using 'Host SMTP' = " + smtpHost + "'");
		props.setProperty(JAVA_SMTP_HOST, smtpHost);

		int smtpPort = server.getPort(parameters, variables);
		if (smtpPort <= 0) {
			throw new Exception("Mailer configuration error, property Port is invalid");
		}
		logger.info("MailHelper is using 'Port' = '" + smtpPort + "'");
		props.setProperty(JAVA_SMTP_PORT, String.valueOf(smtpPort));

		String smtpFrom = server.getFromEmail(parameters, variables);
		if (smtpFrom == null || smtpFrom.isEmpty()) {
			throw new Exception("Mailer configuration error, property 'From' is invalid");
		}
		logger.info("MailHelper is using send mail from = '" + smtpFrom + "'");

		boolean secured = server.isSecured();
		props.setProperty(JAVA_SMTP_AUTH, String.valueOf(secured));
		logger.info("MailHelper is using 'Authentication' = '" + secured + "'");

		boolean tls = server.isTls();
		props.setProperty(JAVA_SMTP_TLS, String.valueOf(tls));
		logger.info("MailHelper is using 'TLS' = '" + tls + "'");

		String login = server.getLogin();
		if (login == null || login.isEmpty()) {
			logger.warn("Mailer configuration warning : property 'Login' was not set.");
		}
		else {
			logger.info("MailHelper is using 'Login' = '" + login + "'");
			props.setProperty(JAVA_SMTP_USER, login);
		}

		String password = server.getPassword();
		if (password == null || password.isEmpty()) {
			logger.warn("Mailer configuration warning : property 'Password' was not set.");
		}
		else {
			props.setProperty(JAVA_SMTP_PASS, password);
		}

		return props;
	}

	public synchronized void sendEmail(ServerMail serverMail, MailConfig config, String textMail, HashMap<String, InputStream> attachements, List<Parameter> parameters, List<Variable> variables) throws Throwable {
		MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(mc);

		Authenticator auth = null;
		if (serverMail.isSecured()) {
			auth = new MailAuthenticator(serverMail.getLogin(), serverMail.getPassword());
		}

		Session session = Session.getInstance(mailProperties, auth);
		session.setDebug(true);
		MimeMessage msg = new MimeMessage(session);

		msg.setFrom(new InternetAddress(serverMail.getFromEmail(parameters, variables)));
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(config.getRecipient()));

		msg.setSubject(config.getTitle());

		msg.setSentDate(Calendar.getInstance().getTime());

		MimeMultipart mp = new MimeMultipart();

		MimeBodyPart mbp1 = new MimeBodyPart();
		mbp1.setHeader("Content-Type", "text/html");
		mbp1.setText(textMail, "UTF-8", "html");
		mp.addBodyPart(mbp1);

		for (String attachementName : attachements.keySet()) {
			MimeBodyPart mbp2 = new MimeBodyPart();

			String mimeType = new MimetypesFileTypeMap().getContentType(attachementName);
			try {
				mbp2.setFileName(attachementName);
				ByteArrayDataSource ba = new ByteArrayDataSource(attachements.get(attachementName), mimeType);
				mbp2.setDataHandler(new DataHandler(ba));
				mp.addBodyPart(mbp2);

				msg.setContent(mp);

			} catch (Exception ex) {
				throw ex;
			}
		}
		msg.setContent(mp);

		msg.saveChanges();

		if (serverMail.isTls()) {
			Transport t = session.getTransport("smtps");
			t.connect(serverMail.getSmtpHost(parameters, variables), serverMail.getPort(parameters, variables), serverMail.getLogin(), serverMail.getPassword());
			t.sendMessage(msg, msg.getAllRecipients());
			t.close();
		}
		else if (serverMail.isSecured()) {
			Transport t = session.getTransport("smtp");
			t.connect(serverMail.getSmtpHost(parameters, variables), serverMail.getPort(parameters, variables), serverMail.getLogin(), serverMail.getPassword());
			t.sendMessage(msg, msg.getAllRecipients());
			t.close();
		}
		else {
			Transport.send(msg);
		}

		logger.info("Mail Accepted.");
	}

	public List<MailAttachment> browseAttachments(String host, String login, String password, String folder, boolean rejectNonMatchingMail, boolean copyMail, String rejectedFolder, String filter, String attachmentFilter) throws Exception {
		List<MailAttachment> attachments = new ArrayList<MailHelper.MailAttachment>();
		
		Properties properties = new Properties();
		properties.setProperty("mail.store.protocol", "imaps");

		Session session = Session.getDefaultInstance(properties, null);

		try {
			Store store = session.getStore("imaps");
		    store.connect(host, login, password);

			// opens the inbox folder
			Folder folderInbox = store.getFolder(folder);
			if (folderInbox == null) {
				throw new Exception("Unable to find mail folder.");
			}
			folderInbox.open(Folder.READ_WRITE);

			// fetches new messages from server
			Message[] arrayMessages = folderInbox.getMessages();

			for (int i = 0; i < arrayMessages.length; i++) {
				Message message = arrayMessages[i];
//				Address[] fromAddress = message.getFrom();
//				String from = fromAddress[0].toString();
				String subject = message.getSubject();
				
				if (!checkRegex(subject, filter)) {
					if (rejectNonMatchingMail) {
						manageMailAfterTreatment(store, rejectedFolder, message, true, copyMail);
					}
					continue;
				}
				
				String keyMail = new Object().hashCode() + "";

				List<MailAttachment> messageAttachements = new ArrayList<MailHelper.MailAttachment>();
				String contentType = message.getContentType();
				if (contentType.contains("multipart")) {
					// content may contain attachments
					Multipart multiPart = (Multipart) message.getContent();
					int numberOfParts = multiPart.getCount();
					for (int partCount = 0; partCount < numberOfParts; partCount++) {
						MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
						if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
							// this part is attachment
							String fileName = part.getFileName();
							
							if (!checkRegex(fileName, attachmentFilter)) {
								continue;
							}
							
							messageAttachements.add(new MailAttachment(keyMail, fileName));
						}
					}
				}
				
				if ((messageAttachements == null || messageAttachements.isEmpty()) && rejectNonMatchingMail) {
					manageMailAfterTreatment(store, rejectedFolder, message, true, copyMail);
				}
				
				attachments.addAll(messageAttachements);
			}
			
			folderInbox.close(true);
			store.close();
		} catch (NoSuchProviderException ex) {
			ex.printStackTrace();
			throw ex;
		} catch (MessagingException ex) {
			ex.printStackTrace();
			throw ex;
		} catch (IOException ex) {
			ex.printStackTrace();
			throw ex;
		}
		
		return attachments;
	}

	private boolean checkRegex(String name, String filter) {
		return filter == null || filter.isEmpty() || Pattern.matches(filter, name);
	}

	public ByteArrayInputStream getAttachmentFile(String host, String login, String password, String folder, boolean manageMail, boolean copyMail, String treatedFolder, String filter, String attachmentName) throws Exception {
		Properties properties = new Properties();
		properties.setProperty("mail.store.protocol", "imaps");

		Session session = Session.getDefaultInstance(properties, null);

		try {
			Store store = session.getStore("imaps");
		    store.connect(host, login, password);

			// opens the inbox folder
		    IMAPFolder folderInbox = (IMAPFolder) store.getFolder(folder);
			if (folderInbox == null) {
				throw new Exception("Unable to find mail folder.");
			}
			folderInbox.open(Folder.READ_WRITE);

			// fetches new messages from server
			Message[] arrayMessages = folderInbox.getMessages();

			for (int i = 0; i < arrayMessages.length; i++) {
				Message message = arrayMessages[i];
//				Address[] fromAddress = message.getFrom();
//				String from = fromAddress[0].toString();
				String subject = message.getSubject();
				
				if (!checkRegex(subject, filter)) {
					continue;
				}

				String contentType = message.getContentType();
				if (contentType.contains("multipart")) {
					// content may contain attachments
					Multipart multiPart = (Multipart) message.getContent();
					int numberOfParts = multiPart.getCount();
					for (int partCount = 0; partCount < numberOfParts; partCount++) {
						MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
						if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
							// this part is attachment
							String fileName = part.getFileName();
							
							if (fileName.equals(attachmentName)) {
								try (InputStream is = part.getInputStream()) {
									byte[] buff = new byte[8000];

									int bytesRead = 0;

									ByteArrayOutputStream bao = new ByteArrayOutputStream();
									while ((bytesRead = is.read(buff)) != -1) {
										bao.write(buff, 0, bytesRead);
									}

									byte[] data = bao.toByteArray();
									manageMailAfterTreatment(store, treatedFolder, message, manageMail, copyMail);
									
									folderInbox.close(true);
									store.close();
									return new ByteArrayInputStream(data);
								} catch (Exception e) {
									e.printStackTrace();

									throw e;
								}
							}
						}
					}
				}
			}
			folderInbox.close(true);
			store.close();
		} catch (NoSuchProviderException ex) {
			ex.printStackTrace();
			throw ex;
		} catch (MessagingException ex) {
			ex.printStackTrace();
			throw ex;
		} catch (IOException ex) {
			ex.printStackTrace();
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		
		throw new Exception("Unable to find attachment.");
	}

	private void manageMailAfterTreatment(Store store, String destinationFolder, Message message, boolean manageMail, boolean copy) {
		try {
			IMAPFolder folder = (IMAPFolder) store.getFolder(destinationFolder);
			if (folder != null) {
				folder.open(Folder.READ_ONLY);
				folder.addMessages(new Message[] { message });

				//This means that it is the last attachment and we can delete the mail
				if (manageMail && !copy) {
					message.setFlag(FLAGS.Flag.DELETED, true);  
				}
				
				folder.close(true);
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
	}

	public void getEmail(String host, String login, String password, String saveDirectory) throws Exception {
		Properties properties = new Properties();

		// server setting
//		properties.put("mail.pop3.host", host);
//		properties.put("mail.pop3.port", port);
		properties.setProperty("mail.store.protocol", "imaps");

		// SSL setting
//		properties.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//		properties.setProperty("mail.pop3.socketFactory.fallback", "false");
//		properties.setProperty("mail.pop3.socketFactory.port", String.valueOf(port));

		Session session = Session.getDefaultInstance(properties, null);

		try {
			// connects to the message store
//			Store store = session.getStore("pop3");
//			store.connect(login, password);
			
			Store store = session.getStore("imaps");
		    store.connect(host, login, password);

			// opens the inbox folder
//			Folder folderInbox = store.getFolder("INBOX");
			Folder folderInbox = store.getFolder("Hub");
//			for (Folder folder : folders) {
//				if ((folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) {
//		            System.out.println(folder.getFullName() + ": " + folder.getMessageCount());
//		        }
//			}
			folderInbox.open(Folder.READ_ONLY);
//
			// fetches new messages from server
			Message[] arrayMessages = folderInbox.getMessages();

			for (int i = 0; i < arrayMessages.length; i++) {
				Message message = arrayMessages[i];
				Address[] fromAddress = message.getFrom();
				String from = fromAddress[0].toString();
				String subject = message.getSubject();
				String sentDate = message.getSentDate().toString();

				String contentType = message.getContentType();
				String messageContent = "";

				// store attachment file name, separated by comma
				String attachFiles = "";

				if (contentType.contains("multipart")) {
					// content may contain attachments
					Multipart multiPart = (Multipart) message.getContent();
					int numberOfParts = multiPart.getCount();
					for (int partCount = 0; partCount < numberOfParts; partCount++) {
						MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
						if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
							// this part is attachment
							String fileName = part.getFileName();
							attachFiles += fileName + ", ";
							part.saveFile(saveDirectory + File.separator + fileName);
						}
						else {
							// this part may be the message content
							messageContent = part.getContent().toString();
						}
					}

					if (attachFiles.length() > 1) {
						attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
					}
				}
				else if (contentType.contains("text/plain") || contentType.contains("text/html")) {
					Object content = message.getContent();
					if (content != null) {
						messageContent = content.toString();
					}
				}

				// print out details of each message
				System.out.println("Message #" + (i + 1) + ":");
				System.out.println("\t From: " + from);
				System.out.println("\t Subject: " + subject);
				System.out.println("\t Sent Date: " + sentDate);
				System.out.println("\t Message: " + messageContent);
				System.out.println("\t Attachments: " + attachFiles);
			}

			// disconnect
			folderInbox.close(false);
			store.close();
		} catch (NoSuchProviderException ex) {
			System.out.println("No provider for pop3.");
			ex.printStackTrace();
		} catch (MessagingException ex) {
			System.out.println("Could not connect to the message store");
			ex.printStackTrace();
		}
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
	}
	
	public class MailAttachment {
		
		private String keyMail;
		private String name;

		public MailAttachment(String keyMail, String name) {
			this.keyMail = keyMail;
			this.name = name;
		}
		
		public String getKeyMail() {
			return keyMail;
		}
		
		public String getName() {
			return name;
		}
	}
}
