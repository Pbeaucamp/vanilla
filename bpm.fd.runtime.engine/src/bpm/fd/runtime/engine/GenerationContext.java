package bpm.fd.runtime.engine;

public class GenerationContext {
	
	private String validationUrl;
	private String invalidationUrl;
	
	
	/**
	 * @param validationUrl
	 * @param invalidationUrl
	 */
	public GenerationContext(String validationUrl, String invalidationUrl) {
		super();
		this.validationUrl = validationUrl;
		this.invalidationUrl = invalidationUrl;
	}
	/**
	 * @return the validationUrl
	 */
	public String getValidationUrl() {
		return validationUrl;
	}
	/**
	 * @return the invalidationUrl
	 */
	public String getInvalidationUrl() {
		return invalidationUrl;
	}
	
	
} 
