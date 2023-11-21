package bpm.vanillahub.runtime.run;

public class SimpleResultInformation implements IResultInformation {

	private String value;
	
	public SimpleResultInformation(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
