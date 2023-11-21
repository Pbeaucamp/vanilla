package bpm.fmloader.client.panel;

import bpm.fmloader.client.constante.Constantes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoadingPanel extends VerticalPanel {

	public LoadingPanel() {
		super();

		Image imgLoad = new Image(GWT.getHostPageBaseURL() + "large-loading.gif");
		Label lblWait = new Label(Constantes.LBL.loading());
		
		HorizontalPanel wait = new HorizontalPanel();
		wait.setSpacing(10);
		wait.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		wait.add(imgLoad);
		wait.add(lblWait);
		
		this.add(wait);
		
	}

	
	
}
