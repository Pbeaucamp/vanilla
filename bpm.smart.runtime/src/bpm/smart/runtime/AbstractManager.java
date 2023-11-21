package bpm.smart.runtime;

import java.util.Locale;

import bpm.vanilla.platform.logging.IVanillaLogger;

public abstract class AbstractManager {

	private SmartManagerComponent component;
	private Locale locale;
	
	public AbstractManager(SmartManagerComponent component) {
		this.component = component;
		try {
			init();
		} catch (Throwable ex) {
			getLogger().error("Error when initing - " + ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	protected abstract void init() throws Exception;

	public IVanillaLogger getLogger() {
		return component.getLogger();
	}
	
	public SmartManagerComponent getComponent() {
		return component;
	}
}
