package bpm.vanilla.platform.core.runtime.tools;

import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.MailcapCommandMap;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.components.system.IMailConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

/**
 * this is the platforms mailer.
 * 
 * Fetches information from vanilla-conf/vanilla.properties to set :
 * bpm.vanilla.mailer.smtp.host, ex smtp.orange.fr
 * bpm.vanilla.mailer.smtp.port, ex 25
 * 
 * @author manu
 *
 */
public class MailHelper {
	
	public static String VANILLA_SMTP_HOST = "bpm.vanilla.mailer.smtp.host";
	public static String VANILLA_SMTP_PORT = "bpm.vanilla.mailer.smtp.port";

	public static String VANILLA_SMTP_FROM = "bpm.vanilla.mailer.smtp.from";
	/*
	bpm.vanilla.mailer.smtp.user=emmanuel.reynard@gmail.com
	bpm.vanilla.mailer.smtp.password=rugby675 
	bpm.vanilla.mailer.smtp.auth=true
	bpm.vanilla.mailer.smtp.starttls.enable=true
	 */
	public static String VANILLA_SMTP_USER = "bpm.vanilla.mailer.smtp.user";
	public static String VANILLA_SMTP_PASS = "bpm.vanilla.mailer.smtp.password";
	public static String VANILLA_SMTP_AUTH = "bpm.vanilla.mailer.smtp.auth";
	public static String VANILLA_SMTP_TLS  = "bpm.vanilla.mailer.smtp.starttls.enable";
	
	/**
	 * email specifics
	 */
	//mandatory:
	public static String JAVA_SMTP_HOST = "mail.smtp.host";
	public static String JAVA_SMTP_PORT = "mail.smtp.port";
	//optional:
	public static String JAVA_SMTP_AUTH = "mail.smtp.auth";
	public static String JAVA_SMTP_TLS = "mail.smtp.starttls.enable";
	public static String JAVA_SMTP_USER = "mail.smtp.user";
	public static String JAVA_SMTP_PASS = "mail.smtp.password";
	/**
	 * end email specifics
	 */
	
	private static Logger logger = Logger.getLogger(MailHelper.class);
	
	private static Properties mailingProperties = null;
	
	private static String fromEmail = "";
	
//	private static String username = null;
//	private static String password = null;
	
	private static boolean isSecured = false;//true is bpm.vanilla.mailer.smtp.auth=true
	
	private static boolean isReady = false;
	
	private static void init() throws Exception {
		logger.info("MailHelper is initing...");
		
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		
		Properties props = new Properties();
		
		String smtpHost = config.getProperty(VANILLA_SMTP_HOST);
		if (smtpHost == null || smtpHost.isEmpty()) {
			throw new Exception("Mailer configuration error, property '" + VANILLA_SMTP_HOST + "' was not found " +
					"or is invalid");
		}
		logger.info("MailHelper is using '" + JAVA_SMTP_HOST + "' = " + smtpHost + "'");
		props.setProperty(JAVA_SMTP_HOST, smtpHost);
		
		String smtpPortString = config.getProperty(VANILLA_SMTP_PORT);
		if (smtpPortString == null || smtpPortString.isEmpty()) {
			throw new Exception("Mailer configuration error, property '" + VANILLA_SMTP_PORT + "' was not found " +
					"or is invalid");
		}
		logger.info("MailHelper is using '" + JAVA_SMTP_PORT + "' = '" + smtpPortString + "'");
		props.setProperty(JAVA_SMTP_PORT, smtpPortString);
		
		String smtpFrom = config.getProperty(VANILLA_SMTP_FROM);
		if (smtpFrom == null || smtpFrom.isEmpty()) {
			throw new Exception("Mailer configuration error, property '" + VANILLA_SMTP_FROM + "' was not found " +
					"or is invalid");
		}
		logger.info("MailHelper is using send mail from = '" + smtpFrom + "'");
		fromEmail = smtpFrom;
		
		/*
		 * VANILLA_SMTP_AUTH
		 */
		String smtpAuth = config.getProperty(VANILLA_SMTP_AUTH);
		if (smtpAuth == null || smtpAuth.isEmpty()) {
			logger.warn("Mailer configuration warning : property '" + VANILLA_SMTP_AUTH + " was not set.");
		}
		else {
			logger.info("MailHelper is using '" + JAVA_SMTP_AUTH + "' = '" + smtpAuth + "'");
			props.setProperty(JAVA_SMTP_AUTH, smtpAuth);
			isSecured = true;
		}
		
		/*
		 * VANILLA_SMTP_TLS
		 */
		String smtpTls = config.getProperty(VANILLA_SMTP_TLS);
		if (smtpTls == null || smtpTls.isEmpty()) {
			logger.warn("Mailer configuration warning : property '" + VANILLA_SMTP_TLS + " was not set.");
		}
		else {
			logger.info("MailHelper is using '" + JAVA_SMTP_TLS + "' = '" + smtpTls + "'");
			props.setProperty(JAVA_SMTP_TLS, smtpTls);
		}
		
		/*
		 * VANILLA_SMTP_USER
		 */
		String smtpUsername = config.getProperty(VANILLA_SMTP_USER);
		if (smtpUsername == null || smtpUsername.isEmpty()) {
			logger.warn("Mailer configuration warning : property '" + VANILLA_SMTP_USER + " was not set.");
		}
		else {
			logger.info("MailHelper is using '" + JAVA_SMTP_USER + "' = '" + smtpUsername + "'");
			props.setProperty(JAVA_SMTP_USER, smtpUsername);
		}
		
		/*
		 * VANILLA_SMTP_PASS
		 */
		String smtpPassword = config.getProperty(VANILLA_SMTP_PASS);
		if (smtpPassword == null || smtpPassword.isEmpty()) {
			logger.warn("Mailer configuration warning : property '" + VANILLA_SMTP_PASS + " was not set.");
		}
		else {
			logger.info("MailHelper is using '" + JAVA_SMTP_PASS + "' = '" + smtpPassword + "'");
			props.setProperty(JAVA_SMTP_PASS, smtpPassword);
		}
		
		mailingProperties = props;
		
		isReady = true;
	}
	
	/**
	 * This is a rich text version (html) for content because of line :
	 * mbp1.setHeader("Content-Type", "text/html");
	 * 
	 * @param config
	 * @param attachements
	 * @return
	 * @throws Exception
	 */
	public static synchronized String sendEmail(IMailConfig config, HashMap<String, InputStream> attachements) 
			throws Throwable {
		
		if (!isReady) {
			init();
		}
		
		MailcapCommandMap mc = (MailcapCommandMap)CommandMap.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(mc);
		
		Authenticator auth = null;
		
		if(mailingProperties.getProperty(JAVA_SMTP_TLS) != null && ((String)mailingProperties.getProperty(JAVA_SMTP_TLS)).equals("true")) {
			auth = new MailAuthenticator(((String)mailingProperties.getProperty(JAVA_SMTP_USER)), ((String)mailingProperties.getProperty(JAVA_SMTP_PASS))); 
		}
	
		//ready
		Session session = Session.getInstance(mailingProperties, auth);
		session.setDebug(true);
		MimeMessage msg = new MimeMessage(session);
		
		msg.setFrom(new InternetAddress(fromEmail));
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(config.getRecipient()));
		
		msg.setSubject(config.getTitle());
		
		msg.setSentDate(Calendar.getInstance().getTime());
				      
		MimeMultipart mp = new MimeMultipart();
				   
				      
		MimeBodyPart mbp1 = new MimeBodyPart();
		if (config.isRichText()) {
			mbp1.setHeader("Content-Type", "text/html");
			mbp1.setText(config.getText(), "UTF-8", "html");
		} else {
			mbp1.setText(config.getText());
		}
		//mbp1.setText(config.getRichText());
		//mbp1.setc
		mp.addBodyPart(mbp1);
		
		for(String attachementName : attachements.keySet()){
			MimeBodyPart mbp2 = new MimeBodyPart();
			
			//"application/pdf"
			//String[] token = attachementName.split("\\.");
			//File f = new File(attachementName);
			String mimeType = new MimetypesFileTypeMap().getContentType(attachementName);
			
			try{
				//File f = new File(attachementName);
				mbp2.setFileName(attachementName);
				ByteArrayDataSource ba = new ByteArrayDataSource(attachements.get(attachementName), mimeType);
				mbp2.setDataHandler(new DataHandler(ba));
				mp.addBodyPart(mbp2);
				
				msg.setContent(mp);

			} catch(Exception ex){
				throw ex;
			}

		}
		msg.setContent(mp);
		
		msg.saveChanges();
				    
		
		if(mailingProperties.getProperty(JAVA_SMTP_TLS) != null && Boolean.parseBoolean(mailingProperties.getProperty(JAVA_SMTP_TLS))) { 
			Transport t = session.getTransport("smtps");
			t.connect(
					mailingProperties.getProperty(JAVA_SMTP_HOST), 
					Integer.parseInt(mailingProperties.getProperty(JAVA_SMTP_PORT)), 
					mailingProperties.getProperty(JAVA_SMTP_USER),
					mailingProperties.getProperty(JAVA_SMTP_PASS)
					);
			t.sendMessage(msg, msg.getAllRecipients());
			t.close();
		}
		else if(mailingProperties.getProperty(JAVA_SMTP_AUTH) != null && Boolean.parseBoolean(mailingProperties.getProperty(JAVA_SMTP_AUTH))) { 
			Transport t = session.getTransport("smtp");
			t.connect(
					mailingProperties.getProperty(JAVA_SMTP_HOST), 
					Integer.parseInt(mailingProperties.getProperty(JAVA_SMTP_PORT)), 
					mailingProperties.getProperty(JAVA_SMTP_USER),
					mailingProperties.getProperty(JAVA_SMTP_PASS)
					);
			t.sendMessage(msg, msg.getAllRecipients());
			t.close();
		}
		else {
			Transport.send(msg);
		}	
		
		logger.info("Mail Accepted.");
		
		return "mail sent";
	}
}
