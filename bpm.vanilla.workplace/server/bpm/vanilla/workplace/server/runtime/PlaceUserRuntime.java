package bpm.vanilla.workplace.server.runtime;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import bpm.vanilla.workplace.core.IUser;
import bpm.vanilla.workplace.core.PlaceConstants;
import bpm.vanilla.workplace.server.config.PlaceConfiguration;
import bpm.vanilla.workplace.server.helper.MailSender;
import bpm.vanilla.workplace.server.helper.TransformObject;
import bpm.vanilla.workplace.server.security.WorkplaceSessionHelper;
import bpm.vanilla.workplace.shared.exceptions.ServiceException;
import bpm.vanilla.workplace.shared.model.PlaceWebUser;

public class PlaceUserRuntime {
	private static final String SUBJECT_CREATE_USER = "User registration";

	private PlaceConfiguration config;
	private Logger logger;
	
	public PlaceUserRuntime() throws ServiceException {
		config = PlaceConfiguration.getInstance();
		logger = Logger.getLogger(PlaceUserRuntime.class);
	}
	
	public void deleteUser(int userId){
		logger.info("Deleting user " + userId);
		PlaceWebUser user = new PlaceWebUser();
		user.setId(userId);
		config.getUserPackageDao().deleteAllPackageForUser(user.getId());
		config.getUserDao().delete(user, config);
	}
	
	public boolean createUserAndSendMail(String path, String url, IUser user, HttpServletRequest request){
		logger.info("Creating user " + user.getName());
		String hashCode = md5encode(String.valueOf(new Object().hashCode()));
		PlaceWebUser userToSave = new PlaceWebUser(user.getName(), md5encode(user.getPassword()), 
				user.getMail(), false, hashCode, false, new Date());
		
		config.getUserDao().save(userToSave, config);

		try {
			MailSender.sendMail(path, user.getMail(), SUBJECT_CREATE_USER, buildMailText(url, user.getName(), hashCode));
		} catch (AddressException e) {
			e.printStackTrace();
			return false;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (DocumentException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public void createUser(PlaceWebUser user){
		logger.info("Creating user " + user.getName());
		PlaceWebUser userToSave = new PlaceWebUser(user.getName(), md5encode(user.getPassword()), 
				user.getMail(), false, "", true, new Date());
		
		config.getUserDao().save(userToSave, config);
	}
	
	public void updateUser(PlaceWebUser user){
		logger.info("Creating user " + user.getName());
		
		config.getUserDao().update(user, config);
	}
	
	public IUser authentifyUser(String name, String password, HttpServletRequest request) 
			throws ServiceException{
		PlaceWebUser user = config.getUserDao().findByNameAndPassword(name, md5encode(password));
		if(user == null){
			throw new ServiceException("The user " + name + " doesn't exist. Please register...");
		}
		
		WorkplaceSessionHelper.createSession(user, request, config);
		
		return TransformObject.transformToCoreUser(user);
	}
	
	public boolean validateUser(String name, String hashCode){
		return config.getUserDao().validateUser(name, hashCode, config);
	}
	
	public static String md5encode(String toEncode){
		byte[] uniqueKey = toEncode.getBytes();
		byte[] hash = null;

		try {

		// on récupère un objet qui permettra de crypter la chaine

			hash = MessageDigest.getInstance("MD5").digest(uniqueKey);

		}catch (NoSuchAlgorithmException e) {
			throw new Error("no MD5 support in this VM");

		}
		StringBuffer hashString = new StringBuffer();

		for (int i = 0; i < hash.length; ++i) {

			String hex = Integer.toHexString(hash[i]);
			if (hex.length() == 1) {
				hashString.append('0');
				hashString.append(hex.charAt(hex.length() - 1));
			}
			else {
				hashString.append(hex.substring(hex.length() - 2));
			}

		}

		return hashString.toString();
	}
	
	private String buildMailText(String url, String name, String hashCode){
		StringBuilder buf = new StringBuilder("To complete your registration, click on the below link\n");
		buf.append(url + "?");
		buf.append(PlaceConstants.LOGIN + "=" + name + "&");
		buf.append(PlaceConstants.HASH_PARAMETER + "=" + hashCode + "\n");
		buf.append("Thank you for registered at Vanilla Workplace");
		return buf.toString();
	}
	
	public List<PlaceWebUser> getAllUser(){
		logger.info("Getting all users.");
		return config.getUserDao().getAllUsers();
	}
}
