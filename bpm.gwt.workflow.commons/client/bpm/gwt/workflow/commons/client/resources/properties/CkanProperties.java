package bpm.gwt.workflow.commons.client.resources.properties;

import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.dialog.CkanPackageDialog;
import bpm.gwt.commons.client.listeners.CkanManager;
import bpm.gwt.commons.client.loading.IWait;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class CkanProperties extends Composite implements CkanManager {

	private static CkanPropertiesUiBinder uiBinder = GWT.create(CkanPropertiesUiBinder.class);

	interface CkanPropertiesUiBinder extends UiBinder<Widget, CkanProperties> {
	}

	@UiField
	TextHolderBox txtUrl, txtOrg, txtApiKey, txtSelectedDataset, txtSelectedResource;
	
	@UiField
	SimplePanel panelBtn;

	private CkanPackage ckanPackage;
	private boolean canAddNew;

	public CkanProperties(IWait waitPanel, String org, String apiKey) {
		this(waitPanel, null, null, org, apiKey, false);

		txtUrl.setVisible(false);
		
		panelBtn.setVisible(false);
		txtSelectedDataset.setVisible(false);
		txtSelectedResource.setVisible(false);
	}

	public CkanProperties(IWait waitPanel, CkanPackage ckanPackage, String ckanUrl, String org, String apiKey, boolean canAddNew) {
		initWidget(uiBinder.createAndBindUi(this));
		this.ckanPackage = ckanPackage;
		this.canAddNew = canAddNew;
		
		if (ckanUrl != null && !ckanUrl.isEmpty()) {
			txtUrl.setText(ckanUrl);
		}
		
		if (org != null && !org.isEmpty()) {
			txtOrg.setText(org);
		}

		if (apiKey != null && !apiKey.isEmpty()) {
			txtApiKey.setText(apiKey);
		}
		
		refreshPackage(ckanPackage);
	}

	private void refreshPackage(CkanPackage ckanPackage) {
		if (ckanPackage != null) {
			txtSelectedDataset.setText(ckanPackage.toString());
			
			if (ckanPackage.getSelectedResource() != null) {
				txtSelectedResource.setText(ckanPackage.getSelectedResource().toString());
			}
		}
	}

	public boolean isValid() {
		return ckanPackage != null && ckanPackage.getSelectedResource() != null;
	}
	
	@UiHandler("btnLoad")
	public void onLoad(ClickEvent event) {
		String ckanUrl = txtUrl.getText();
		String org = txtOrg.getText();

		CkanPackageDialog dial = new CkanPackageDialog(this, ckanUrl, org, canAddNew, ckanPackage);
		dial.center();
	}
	
	public String getUrl() {
		return txtUrl.getText();
	}
	
	public String getOrg() {
		return txtOrg.getText();
	}
	
	public String getApiKey() {
		return txtApiKey.getText();
	}
	
	public CkanPackage getCkanPackage() {
		return ckanPackage;
	}

	public void managePackage(CkanPackage pack, CkanResource resource) {
		if (pack != null) {
			pack.setSelectedResource(resource);
		}
		this.ckanPackage = pack;
		
		refreshPackage(ckanPackage);
	}
}
