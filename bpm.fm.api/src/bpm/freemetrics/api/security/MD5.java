package bpm.freemetrics.api.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.jasypt.intf.service.JasyptStatelessService;
import org.jasypt.util.password.BasicPasswordEncryptor;

public class MD5 {
	
	/**
	* Encode la chaine pass� en param�tre avec l�algorithme MD5
	* @param key : la chaine � encoder
	* @return la valeur (string) hexad�cimale sur 32 bits
	*/
	public static String encode(String password){
		byte[] uniqueKey = password.getBytes();
		byte[] hash = null;

		try {
			
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
			}else {
				hashString.append(hex.substring(hex.length() - 2));
			}
		}
		return hashString.toString();
	}

	// m�thode principale

	public static void main(String[] args) {

//	System.out.println ( "La chaine : P@ssWord, crypt�e via MD5 donne : " + MD5.encode ( "system" ) );
//System.out.println(MD5.encode ( "dfsmgjsdhsjkdlf,hemsgjopzev,tjmzjktazjdfgjd fg djdg jdsuetus ethye" ).length());
		
		
		
		JasyptStatelessService service = new JasyptStatelessService();
		String newpass = service.encrypt("Lw3?1IpQn", "biplatform",                    
					null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
//		String newpass = MD5.encode("biplatform");ENC(fZhkC5M5ZYdbLxZ+yzM+i/bQdDXvqK15)
		
        String res = service.decrypt(
                newpass, 
                "biplatform",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        System.out.println(newpass);
		System.out.println(res);
	}
}
