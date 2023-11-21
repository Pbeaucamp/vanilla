package bpm.fd.web.client.panels.properties;

import com.google.gwt.user.client.ui.Composite;

public abstract class CompositeProperties<T> extends Composite {

	public abstract void buildProperties(T component);
	
	public void refreshOptions(T component) { }
}
