import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Encoder {

	public static void main(String[] args) {
		String originalInput = "4855ee319c8f04c89705f41bcde1bb16a7365ffd9f7aa57be21588dd48bf3e63";
		encoder(originalInput);
	}
	
	private static void encoder(String value) {
		String encodedString = Base64.getEncoder().encodeToString(value.getBytes());
		System.out.println(encodedString);
	}
	
	private static void decoder(String value) {
		String encodedString = new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);;
		System.out.println(encodedString);
	}

}
