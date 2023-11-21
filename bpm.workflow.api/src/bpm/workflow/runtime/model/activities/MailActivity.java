package bpm.workflow.runtime.model.activities;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.components.system.IMailConfig;
import bpm.vanilla.platform.core.components.system.MailConfig;
import bpm.workflow.runtime.MailAuthenticator;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IAcceptInput;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IMailServer;
import bpm.workflow.runtime.resources.IResource;
import bpm.workflow.runtime.resources.servers.ListServer;
import bpm.workflow.runtime.resources.servers.ServerMail;

/**
 * Send a mail
 * @author CHARBONNIER, MARTIN
 *
 */
public class MailActivity extends AbstractActivity implements IMailServer,IComment, IAcceptInput {
	private String object;
	private String comment;
	private ServerMail mailServer ;
	private String content;
	private String destination;
	private String from;
	private List<String> attachments = new ArrayList<String>();
	private String varRef;
	private String typeact = "debut";
	private static int number = 0;
	
	public String getTypeact() {
		return typeact;
	}

	/**
	 * Set the type of the mail activity (debut, inter ...)
	 * @param typeact
	 */
	public void setTypeact(String typeact) {
		this.typeact = typeact;
	}

	public String getVarRef() {
		return varRef;
	}

	public void setVarRef(String varRef) {
		this.varRef = varRef;
	}

	/**
	 * Create a mail activity with the specified name
	 * @param name
	 */
	public MailActivity(String name){
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
	}
	
	/**
	 * do not use, for XML parsing only
	 */
	public MailActivity(){
		number++;
	}
	
	
	/**
	 * Create a mail activity
	 * @param name of the activity
	 * @param object of the mail
	 * @param smtpServer
	 * @param content of the mail
	 * @param destination address
	 * @param attachments
	 */
	protected MailActivity(String name, String object, ServerMail smtpServer, String content, String destination, List<String> attachments){
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		this.object = object;
		this.mailServer = smtpServer;
		this.content = content;
		this.destination = destination;
		this.attachments = attachments;
	}
	
	
	public ServerMail getServer(){
		return mailServer;
	}
	

	
	/**
	 * 
	 * @return the object of the mail
	 */
	public String getObject() {
		return object;
	}

	/**
	 * Set the object of the mail
	 * @param object
	 */
	public void setObject(String object) {
		this.object = object;
	}

	public String getSmtpAdress() {
		if (mailServer != null){
			return mailServer.getUrl();	
		}
		return "";
		
	}

	
	public void setServer(String name){
		mailServer = (ServerMail)ListServer.getInstance().getServer(name);
	}
	
	public void setServer(ServerMail server){
		mailServer = server;
	}
	


	/**
	 * 
	 * @return the content of the mail
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Set the content of the mail
	 * @param content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 
	 * @return the destination of the mail
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * Set the addresses used to send the mail (separated by ";")
	 * @param destination
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}
	
	/**
	 * 
	 * @return the address mail of the sender
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * Set the address mail of the sender
	 * @param from
	 */
	public void setFrom(String from) {
		this.from = from;
	}
	
	@Override
	public void addSource(IActivity source) {
		super.addSource(source);
	}
	
	public void addInput(String outputpath) {
		attachments.add(outputpath);
	}
	
	public void removeInput(String outputpath) {
		attachments.remove(outputpath);
	}
	public List<String> getInputs() {
		return attachments;
	}
	public void setInputs(List<String> inputs) {
		this.attachments = inputs;
	}
	

	@Override
	public void deleteSource(IActivity source) {
		super.deleteSource(source);
	}

	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("mailActivity");
		
		if(comment != null){
			e.addElement("comment").setText(comment);
		}
		if (object != null){
			e.addElement("object").setText(object);
		}
		if (mailServer != null){
			e.addElement("serverRefName").setText(mailServer.getName());
		}
		if (content != null){
			e.addElement("content").setText(content);
		}
		if (from != null && !from.equalsIgnoreCase("")) {
			e.addElement("from").setText(from);
		}
		

		if (destination != null){
			e.addElement("destination").setText(destination);
		}
		
	
		for(String s : attachments){
			e.addElement("attachment").setText(s);
		}
		if(!this.getTypeact().equalsIgnoreCase("")){
			e.addElement("typeact").setText(this.getTypeact());
		}
		
		return e;
	}

	public IActivity copy() {
		MailActivity a = new MailActivity();
		a.setComment(comment);
		a.setContent(content);
		a.setDescription(description);
		a.setDestination(destination);
		a.setFrom(from);
		a.setObject(object);
		a.setServer(mailServer);
		a.setTypeact(typeact);
		a.setName("copy of " + name);
		return a;
	}

	public Class<?> getServerClass() {
		return ServerMail.class;
	}

	public void setServer(IResource server) {
		this.mailServer = (ServerMail) server;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		if (mailServer == null) {
			buf.append("You have to select a mail server for the activity " + name + "\n");
		}
		if (content == null) {
			buf.append("Your mail activity " + name + ", doesn't have content \n");
		}
		if (object == null) {
			buf.append("Your mail activity " + name + ", doesn't have object \n");
		}
		if (destination == null) {
			buf.append("Your mail activity " + name + ", doesn't have recipient \n");
		}
		
		return buf.toString();
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String text) {
		this.comment = text;
		
	}	
	
	public void decreaseNumber() {
		number--;
	}


	public String getPhrase() {
		return "Select activities which provide attachment";
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {

			try {
				String from = this.from;
				from = workflowInstance.parseString(from);
				if(from == null) {
					from = "vanilla@bpm-conseil.com";
				}
				
				String actualObjet = workflowInstance.parseString(object);
				String actualContent = workflowInstance.parseString(content);
				

				Properties props = new Properties();
				props.put("mail.smtp.host", mailServer.getUrl());
				
				if (mailServer.getLogin() != null && !mailServer.getLogin().equals("")) {
					props.setProperty("mail.user", mailServer.getLogin());
				}
				if (mailServer.getPassword() != null && !mailServer.getPassword().equals("")) {
					props.setProperty("mail.password", mailServer.getPassword());
					if(mailServer.getUrl().startsWith("smtps")) {
						props.setProperty("mail.smtp.starttls.enable", "true");
					}
					else {
						props.setProperty("mail.smtp.starttls.enable", "false");
					}
				    props.setProperty("mail.smtp.auth", "true");
				}   
				props.setProperty("mail.smtp.port", ""+mailServer.getPort());

				List<String> fileAttach = new ArrayList<String>();
				String path = "";
				for(String s : attachments){
					path = s;
					try {
						//For now we modify this, but we have to had a checkbox so the user car decide if he wants multiple values or not
						List<String> values = workflowInstance.parseStringMultiple(path);
						if (values == null) {
							//Old way (check svn)
							path = workflowInstance.parseString(path);

							if (path.indexOf(";") > -1 && path.indexOf("root") == -1) {
								//Delete this case
								String[] all = path.split(";");
								for (String a : all) {
									fileAttach.add(a);
								}
							}
							else if (path.equalsIgnoreCase("")) {
								throw new Exception("No available file to attach from previews steps");
							}
							else {
								fileAttach.add(path);
							}
						}
						else {
							for (String value : values) {
								path = value;
								if (path.indexOf(";") > -1 && path.indexOf("root") == -1) {
									//Delete this case
									String[] all = path.split(";");
									for (String a : all) {
										fileAttach.add(a);
									}
								}
								else if (path.equalsIgnoreCase("")) {
									throw new Exception("No available file to attach from previews steps");
								}
								else {
									fileAttach.add(path);
								}
							}
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
						
				}
				
				String desti = workflowInstance.parseString(destination);

				for(String dest : desti.split(";")){				
					try{
						
						IMailConfig config = new MailConfig(dest, from, actualContent, actualObjet, true);
						
						HashMap<String, InputStream> attachement = new HashMap<String, InputStream>();
						try {
							int i =0;
							for(String fileName : fileAttach) {
//							String fileName = fileAttach.get(0);
								if (fileName.contains("/")) {
									fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
								}
								
								attachement.put(fileName, new FileInputStream(fileAttach.get(i)));
								i++;
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
						
						workflowInstance.getVanillaApi().getVanillaSystemManager().sendEmail(config, attachement);
						
//						sendMail(props, from, actualObjet, dest, actualContent, fileAttach);
						Logger.getLogger(getClass()).info("Sent mail to " + dest);
					}catch(Throwable t){
						t.printStackTrace();
						Logger.getLogger(getClass()).warn("Failed to send mail to " + dest + " - " + t.getMessage(), t);
						throw new Exception(t);
					}

				}
				
				activityResult = true;
			} catch(Exception e) {
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
				e.printStackTrace();
			}
			
			super.finishActivity();
		}
	}

	/**
	 * Send a mail
	 * @param properties relative to the mail server
	 * @param from the sender's mail address
	 * @param object of the mail
	 * @param dest of the mail
	 * @param content mail
	 * @param fileAttach
	 * @throws AddressException
	 * @throws MessagingException
	 * @throws IOException
	 */
	private void sendMail(Properties props, String from, String object, String dest, String content, List<String> fileAttach) throws Throwable{
		
		// add handlers for main MIME types
		MailcapCommandMap mc = (MailcapCommandMap)CommandMap.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(mc);
		
		Authenticator auth = null;
		
		if(props.getProperty("mail.smtp.starttls.enable") != null && ((String)props.getProperty("mail.smtp.starttls.enable")).equals("true")) {
			auth = new MailAuthenticator(((String)props.getProperty("mail.user")), ((String)props.getProperty("mail.password"))); 
		}
		
		Session session = Session.getInstance(props, auth);
		session.setDebug(true);
		MimeMessage msg = new MimeMessage(session);
		
		msg.setFrom(new InternetAddress(from));
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(dest));
		
		msg.setSubject(object);
		
		msg.setSentDate(Calendar.getInstance().getTime());
				      
		MimeMultipart mp = new MimeMultipart();
				   
				      
		MimeBodyPart mbp1 = new MimeBodyPart();
		mbp1.setText(content);
		mp.addBodyPart(mbp1);
		
		
		for(String fileName : fileAttach){
			MimeBodyPart mbp2 = new MimeBodyPart();

			
			try{
				File f = new File(fileName);
				
				
				mbp2.setFileName(f.getName());
				ByteArrayDataSource ba = new ByteArrayDataSource(new FileInputStream(f), "application/pdf");
				mbp2.setDataHandler(new DataHandler(ba));
				mp.addBodyPart(mbp2);
				
				msg.setContent(mp);

			}catch(Exception ex){
				ex.printStackTrace();
			}

		}
		msg.setContent(mp);
				      
		if(props.getProperty("mail.smtp.starttls.enable") != null && ((String)props.getProperty("mail.smtp.starttls.enable")).equals("true")) { 
			Transport t = session.getTransport("smtps");
			t.connect (props.getProperty("mail.smtp.host"), Integer.parseInt(props.getProperty("mail.smtp.port")), props.getProperty("mail.user"), props.getProperty("mail.password"));
			t.sendMessage(msg, msg.getAllRecipients());
			t.close();
		}
		else if(props.getProperty("mail.smtp.auth") != null && Boolean.parseBoolean(props.getProperty("mail.smtp.auth"))) { 
			Transport t = session.getTransport("smtp");
			t.connect(
					props.getProperty("mail.smtp.host"), 
					Integer.parseInt(props.getProperty("mail.smtp.port")), 
					props.getProperty("mail.user"),
					props.getProperty("mail.password")
					);
			t.sendMessage(msg, msg.getAllRecipients());
			t.close();
		}
		else {
			Transport.send(msg);
		}

		
	}
	
	
	
	
	
}
