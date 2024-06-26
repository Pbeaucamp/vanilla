package bpm.vanilla.workplace.remote.internal;

import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.workplace.core.PlaceConstants;

/**
 * A base class to contact WorkplaceServlet throught an HttpURLConnection
 * 
 * The method writeAuthentication write in the HttpHeader the Authentication String or the sessionId
 * if it exists
 * 
 * The method extractSessionId should be use to extract the sessionId once Vanilla returned its response
 * 
 * @author ludo
 *
 */
public abstract class AbstractRemoteAuthentifier {
	private String login;
	private String password;
	
	private String sessionId;
	
	public void init(String login, String password) {
		if (login == null || password == null) {
			throw new NullPointerException("Login or password provided were null");
		}
		this.login = login;
		this.password = password;
		this.sessionId = null;
	}
	
	protected String getMd5Password(){
		if (!password.matches("[0-9a-f]{32}")){
			return md5encode(password);
		}
		return password;
		
	}
	
	protected String getSessionId() {
		return sessionId;
	}
	
	protected String getLogin(){
		return login;
	}
	
	/**
	 * write a requestProperty named VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID with the sessionId if it exists
	 * or Authaurization Header fro Basic Base64 Encoded
	 * @param sock
	 */
	protected void writeAuthentication(HttpURLConnection sock){
		if (sessionId == null){
			String authentication = new String(Base64.encodeBase64(new String(login + ":" + getMd5Password()).getBytes()));
			sock.setRequestProperty( "Authorization", "Basic " + authentication );
		}
		else{
			sock.setRequestProperty( PlaceConstants.HTTP_HEADER_WORKPLACE_SESSION_ID, sessionId );
		}
	}
	
	/**
	 * extract the sessionId from the from the HttpHeader and keep it
	 * @param sock
	 */
	protected void extractSessionId(HttpURLConnection sock){
		if (sessionId == null){
			sessionId = sock.getHeaderField(PlaceConstants.HTTP_HEADER_WORKPLACE_SESSION_ID);
		}
	}
	
	
	
	public static String md5encode(String password){
		byte[] uniqueKey = password.getBytes();
		byte[] hash = null;

		try {

		// on r�cup�re un objet qui permettra de crypter la chaine

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
}
