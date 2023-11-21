/**
 * 
 */
package bpm.freemetrics.api.digester.beans;

/**
 * @author Belgarde
 *
 */
public class FmDigAction{

	/**
	 * 
	 */
	public FmDigAction() {
	}
	
	private String component;
	private FmDigActionConfig config;
	/**
	 * @return the component
	 */
	public String getComponent() {
		return component;
	}
	/**
	 * @param component the component to set
	 */
	public void setComponent(String component) {
		this.component = component;
	}
	/**
	 * @return the config
	 */
	public FmDigActionConfig getConfig() {
		return config;
	}
	/**
	 * @param config the config to set
	 */
	public void setConfig(FmDigActionConfig config) {
		this.config = config;
	}
	
}
