package bpm.document.management.remote.internal;

import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;



/**
 * A base class to contact VodsServlets throught an HttpURLConnection
 * 
 * The method writeAuthentication write in the HttpHeader the Authentication String or the sessionId
 * if it exists
 * 
 * The method extractSessionId should be use to extract the sessionId once Vanilla returned its response
 * 
 *
 */
public abstract class AbstractRemoteAuthentifier {

	private static final String JSESSIONID = "JSESSIONID";
	
	private String login;
	private String password;
	
	private String sessionId;
	
	public void init(String login, String password, String sessionId) {
		if (login == null || password == null) {
			throw new NullPointerException("Login or password provided were null");
		}
		this.login = login;
		this.password = password;
		this.sessionId = sessionId;
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
	
	abstract protected void writeAdditionalHttpHeader(HttpURLConnection sock);
	
	/**
	 * write a requestProperty named VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID with the sessionId if it exists
	 * or Authaurization Header fro Basic Base64 Encoded
	 * @param sock
	 */
//	protected void writeAuthentication(HttpURLConnection sock){
//		/*if (sessionId != null){
//			sock.setRequestProperty( IVdmConstant.HTTP_HEADER_VDM_SESSION_ID, sessionId );
//		}*/
//		if (sessionId == null){
//			String authentication = new String(Base64.encodeBase64(new String(login + ":" + password).getBytes()));
//			sock.setRequestProperty( "Authorization", "Basic " + authentication );
//		}
////		else{
////			sock.setRequestProperty( VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID, sessionId );
////		}
//	}
	
	/**
	 * extract the sessionId from the from the HttpHeader and keep it
	 * @param sock
	 */
//	protected void extractSessionId(HttpURLConnection sock){
//		if (sessionId == null){
//			sessionId = sock.getHeaderField(AklaboxConstant.HTTP_HEADER_VDM_SESSION_ID);
//		}
//	}
	
	protected void extractSessionId(HttpURLConnection con) {
		List<String> cookies = con.getHeaderFields().get("Set-Cookie");
		if (cookies != null) {
			for (String cookie : cookies) {
				String[] fields = cookie.split(";\\s*");
				if (fields != null && fields.length > 0) {
					for (int j = 0; j < fields.length; j++) {
						String[] value = fields[j].split("=");
						if (value != null && value.length > 0) {
							if (value[0].equals(JSESSIONID)) {
								this.sessionId = value[1];
								break;
							}
						}
					}
				}
			}
		}
	}

	protected void writeAuthentication(HttpURLConnection con) {
		if (sessionId != null) {
			con.setRequestProperty("Cookie", JSESSIONID + "=" + sessionId);
		}
//		else {
			String authentication = new String(Base64.encodeBase64(new String(login + ":" + password).getBytes()));
			con.setRequestProperty( "Authorization", "Basic " + authentication );
//		}
	}
	
	
	
	public static String md5encode(String password){
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
}
