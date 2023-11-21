package bpm.vanilla.platform.core.runtime.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Security {
	/*

	* Encode la chaine passé en paramètre avec l’algorithme MD5

	* @param key : la chaine à encoder

	* @return la valeur (string) hexadécimale sur 32 bits

	*/

	public static String encode(String password){
		byte[] uniqueKey = password.getBytes();
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
	
	
//	public static User authentify(Element authentify, WebApplicationContext ctx) throws UserNotFoundException, PasswordException {
//        UserManager userMgr = (UserManager) ctx.getBean("userManager");      
//		User user = userMgr.authentify(authentify.element("login").getStringValue(), authentify.element("password").getStringValue());
//		
//		return user;
//	}
	
//	public static boolean checkUser(String login, WebApplicationContext ctx) throws UserNotFoundException {
//		UserManager userMgr = (UserManager) ctx.getBean("userManager");
//		User u = userMgr.getUserByName(login);
//		if (u == null)
//			throw new UserNotFoundException(login);
//		
//		return u.isSuperUser();
//	}



}
