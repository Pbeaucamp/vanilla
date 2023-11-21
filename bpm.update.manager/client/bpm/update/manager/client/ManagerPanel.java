package bpm.update.manager.client;

import java.util.List;

import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.update.manager.client.I18N.Labels;
import bpm.update.manager.client.services.UpdateService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ManagerPanel extends Composite {

	private static ManagerPanelUiBinder uiBinder = GWT.create(ManagerPanelUiBinder.class);

	interface ManagerPanelUiBinder extends UiBinder<Widget, ManagerPanel> {
	}

	@UiField
	HTMLPanel panelApplications;

	public ManagerPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		UpdateService.Connect.getInstance().getApplications(new GwtCallbackWrapper<List<String>>(null, false) {

			@Override
			public void onSuccess(List<String> result) {
				panelApplications.clear();
				if (result != null && !result.isEmpty()) {
					Label lbl = new Label(Labels.lblCnst.ApplicationsRegistered() + " :");
					panelApplications.add(lbl);
					for (String app : result) {
						Label lblApp = new Label(app);
						panelApplications.add(lblApp);
					}
				}
				else {
					Label lbl = new Label(Labels.lblCnst.NoApplicationRegistered());
					panelApplications.add(lbl);
				}
			}
		}.getAsyncCallback());
	}

}
