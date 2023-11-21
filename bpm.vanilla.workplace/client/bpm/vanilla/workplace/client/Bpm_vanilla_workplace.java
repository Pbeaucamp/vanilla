package bpm.vanilla.workplace.client;

import bpm.vanilla.workplace.client.admin.panel.ConnectionPanel;
import bpm.vanilla.workplace.client.i18n.LabelsConstants;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Bpm_vanilla_workplace implements EntryPoint {
	
	public final static LabelsConstants LBL = (LabelsConstants) GWT.create(LabelsConstants.class);
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel.get("root").add(new ConnectionPanel());
		
	}
}
