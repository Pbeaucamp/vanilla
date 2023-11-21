package bpm.vanilla.workplace.server.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class MailSender {
	
	synchronized public static void sendMail(String url, String to, String subject, String text) 
			throws AddressException, MessagingException, IOException, DocumentException {
		String path = url + "conf/mail.setup.xml";
		
		FileInputStream fis = new FileInputStream(new File(path));
		String infos = IOUtils.toString(fis, "UTF-8");
		Document document = DocumentHelper.parseText(infos);
		Element root = document.getRootElement();
		Element smtp = root.element("smtp");
		Element auth = root.element("authentification");
		Element port = root.element("port");
		Element elemFrom = root.element("mailing");
		
		
		Properties props = System.getProperties();
		props.put("mail.smtp.host", smtp.getText());
		
		if (port != null) {
			props.put("mail.smtp.port", port.getText());
		}
		
		if (auth != null && auth.getName().equals("true")) {
			Element user = root.element("user");
			Element pwd = root.element("password");
			
			props.setProperty("mail.user", user.getText());
		    props.setProperty("mail.password", pwd.getText());
		}
			
		Session session = Session.getInstance(props, null);
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(elemFrom.getText()));
	    msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
	   
	    msg.setSubject(subject);

	    msg.setText(text);
	    msg.setSentDate(new Date());
	    
	    Transport.send(msg);
	}
		
}


