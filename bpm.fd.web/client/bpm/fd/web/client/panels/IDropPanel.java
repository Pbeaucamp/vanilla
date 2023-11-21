package bpm.fd.web.client.panels;

import bpm.fd.web.client.actions.ActionManager;
import bpm.fd.web.client.widgets.WidgetComposite;

public interface IDropPanel {

	public void addWidget(WidgetComposite widget, int positionLeft, int positionTop);
	
	public void removeWidget(WidgetComposite widget, boolean addAction);
	
	public ActionManager getActionManager();
}
