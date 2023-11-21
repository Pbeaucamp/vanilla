package bpm.freemetrics.api.features.actions.engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

import bpm.freemetrics.api.digester.beans.FmDigAction;
import bpm.freemetrics.api.utils.Tools;

public class MailSender{

//	private String subj = 
//	private String strSMTP = "smtp.gmail.com"; //$NON-NLS-1$
//	private final String strSender = "FmRegisteredUser@gmail.com"; //$NON-NLS-1$
//	private String strRecp = ""; //$NON-NLS-1$
//	private String strSubj = ""; //$NON-NLS-1$
//	private String strMess = ""; //$NON-NLS-1$
//	private final String strPass = "FmRegisteredUser20070207"; //$NON-NLS-1$
//	private String filePath;

//	private boolean execute(FmDigAction task, String strSender, String strPass, String smtpUrl, String smtpPort){
//		boolean result = false;
//
//		if(task != null){
//
//			try {
//				String to = task.getConfig().getRecipient();
////				String strSender = task.getConfig().getSender();
//				String obj = task.getConfig().getSubject();
//				String messageText = task.getConfig().getText();
//
//				boolean debug = false;
//
//				//Set the host smtp address
//				Properties props = new Properties();
//
//				props.put("mail.smtp.starttls.enable","true");
//				props.put("mail.smtp.socketFactory.port", smtpPort);
//				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//				props.put("mail.smtp.socketFactory.fallback", "false");
//
//				props.put("mail.smtp.host", smtpUrl);
//				props.put("mail.smtp.auth", "true");
//				props.put("mail.smtp.port", smtpPort);
//
//				Authenticator auth = new SMTPAuthenticator(strSender,strPass);
//				Session session = Session.getDefaultInstance(props, auth);
//
//				session.setDebug(debug);
//
//				// create a message
//				Message msg = new MimeMessage(session);
//
//				// set the from and to address
//				InternetAddress addressFrom = new InternetAddress(strSender);
//				msg.setFrom(addressFrom);
//
//				InternetAddress[] addressTo = {new InternetAddress(to)};
//
//				msg.setRecipients(Message.RecipientType.TO, addressTo);
//
//				// Setting the Subject and Content Type
//				msg.setSubject(obj);
//				msg.setContent(messageText, "text/plain");
//				Transport.send(msg);
//
//				result = true;
//			} catch (AddressException e) {
//				e.printStackTrace();
//			} catch (MessagingException e) {
//				e.printStackTrace();
//			}
//		}
//
//		return result;
//	}

	public boolean execute(FmDigAction task, String propertiesPath){
		boolean result = false;

		if(task != null){

			try {

				Properties oProperties = new Properties();

				FileInputStream oFileInputStream = new FileInputStream(propertiesPath);

				oProperties.load(oFileInputStream);

				oFileInputStream.close();	

				String strSender = oProperties.getProperty("sender");
				String strPass = oProperties.getProperty("sender_PassWord");
				
				String to = task.getConfig().getRecipient();
				
				String obj = Tools.isValid(task.getConfig().getSubject()) ? task.getConfig().getSubject() : ""+oProperties.get("subject_headers") ;
				String messageText = Tools.isValid(task.getConfig().getText()) ? task.getConfig().getText() : ""+oProperties.get("Default_linked_Messaged") ;

				Authenticator auth = null;
				
				if(oProperties.get("subject_headers") != null && Boolean.valueOf(""+oProperties.get("mail.smtp.auth")))
				 auth = new SMTPAuthenticator(strSender,strPass);
				
				Session session = Session.getDefaultInstance(oProperties, auth);

//				session.setDebug(debug);

				// create a message
				Message msg = new MimeMessage(session);

				// set the from and to address
				InternetAddress addressFrom = new InternetAddress(strSender);
				msg.setFrom(addressFrom);

				InternetAddress[] addressTo = {new InternetAddress(to)};

				msg.setRecipients(Message.RecipientType.TO, addressTo);

				// Setting the Subject and Content Type
				msg.setSubject(obj);
				msg.setContent(messageText, "text/plain");
				
				Transport.send(msg);

				result = true;
			} catch (AddressException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}catch (FileNotFoundException e) {e.printStackTrace();}
			catch (IOException e) {e.printStackTrace();}
		}

		return result;
	}

	/**
	 * SimpleAuthenticator is used to do simple authentication
	 * when the SMTP server requires it.
	 */
	private class SMTPAuthenticator extends Authenticator{

		private String username;
		private  String password;

		public SMTPAuthenticator( String username, String password){

			this.username = username;
			this.password = password;

		}

		public PasswordAuthentication getPasswordAuthentication(){
			return new PasswordAuthentication(username, password);
		}
	}

}
