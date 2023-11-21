package bpm.vanilla.platform.core.beans.alerts;

public interface IAlertRuntime {
	
	/**
	 * Return true if the alert have to fire an event
	 * 
	 * @return true if the alert have to be fired
	 * @throws Exception
	 */
	public boolean checkAlert() throws Exception;
}
