package bpm.vanilla.portal.server.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Security {
	
	public static String encodeMD5(String value){
		byte[] uniqueKey = value.getBytes();
		byte[] hash = null;
	
		try {
			// on recupere un objet qui permettra de crypter la chaine
			hash = MessageDigest.getInstance("MD5").digest(uniqueKey);
		}catch (NoSuchAlgorithmException e) {
			throw new Error("no MD5 support in this VM");
		}
		
		StringBuffer hashString = new StringBuffer();
	
		for (int i = 0; i < hash.length; ++i){
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
}

