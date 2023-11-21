package bpm.vanilla.wopi;

import java.io.InputStream;
import java.security.MessageDigest;

import com.thoughtworks.xstream.core.util.Base64Encoder;

public class Utils {

	public static String getFileSHA256(InputStream bis) throws Exception {
	    byte[] buffer= new byte[8192];
	    int count;
	    MessageDigest digest = MessageDigest.getInstance("SHA-256");
	    while ((count = bis.read(buffer)) > 0) {
	        digest.update(buffer, 0, count);
	    }
	    byte[] hash = digest.digest();
	    return new Base64Encoder().encode(hash);
	}
	
}
