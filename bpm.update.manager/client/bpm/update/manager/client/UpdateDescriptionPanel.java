package bpm.update.manager.client;

import bpm.update.manager.api.beans.Update;
import bpm.update.manager.client.I18N.Labels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class UpdateDescriptionPanel extends Composite {

	private static UpdateDescriptionPanelUiBinder uiBinder = GWT.create(UpdateDescriptionPanelUiBinder.class);

	interface UpdateDescriptionPanelUiBinder extends UiBinder<Widget, UpdateDescriptionPanel> {
	}
	
	@UiField
	HTMLPanel panelProperties, panelScripts;

	@UiField
	HTML txtDescription, txtProperties, txtScripts;
	
	public UpdateDescriptionPanel(Update update) {
		initWidget(uiBinder.createAndBindUi(this));
		
		StringBuffer buf = new StringBuffer(update.getDescription());
		if (update.getAppNames() != null && !update.getAppNames().isEmpty()) {
			buf.append("\n");
			buf.append(Labels.lblCnst.ApplicationsUpdated() + " : \n");
			for (String appName : update.getAppNames()) {
				buf.append("- " + appName + "\n");
			}
		}
		
		txtDescription.setHTML(new SafeHtmlBuilder().appendEscapedLines(buf.toString()).toSafeHtml());
		
		if (update.getProperties() != null && !update.getProperties().isEmpty()) {
			txtProperties.setHTML(new SafeHtmlBuilder().appendEscapedLines(update.getProperties()).toSafeHtml());
		}
		else {
			panelProperties.setVisible(false);
		}
		
		if (update.getScripts() != null && !update.getScripts().isEmpty()) {
			txtScripts.setHTML(new SafeHtmlBuilder().appendEscapedLines(update.getScripts()).toSafeHtml());
		}
		else {
			panelScripts.setVisible(false);
		}
	}

}
