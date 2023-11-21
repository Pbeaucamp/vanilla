package bpm.fd.web.client.dialogs;

import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.panels.CreationPanel;
import bpm.fd.web.client.panels.properties.DashboardOptionsPanel;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class DashboardOptionsDialog extends AbstractDialogBox {

	private static RepositoryDialogUiBinder uiBinder = GWT.create(RepositoryDialogUiBinder.class);

	interface RepositoryDialogUiBinder extends UiBinder<Widget, DashboardOptionsDialog> {
	}

	@UiField
	HTMLPanel mainPanel;
	
	private CreationPanel parent;
	private DashboardOptionsPanel options;

	public DashboardOptionsDialog(CreationPanel parent) {
		super(Labels.lblCnst.PropertiesOf() + " " + parent.getDashboardName(), true, true);
		this.parent = parent;
		
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		this.options = new DashboardOptionsPanel();
		this.options.loadOptions(parent.getDashboardName(), parent.getDashboardDescription());
		mainPanel.add(options);
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			String name = options.getName();
			String description = options.getDescription();
			
			parent.refreshDashboardOptions(name, description);

			hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
