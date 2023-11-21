package bpm.vanilla.platform.core.config;

/**
 * General exception for the configuration package
 * 
 * @author manu
 * 
 */
public class ConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 3074528302497394754L;

	public ConfigurationException(String msg, Exception ex) {
		super(msg, ex);		
	}

}
