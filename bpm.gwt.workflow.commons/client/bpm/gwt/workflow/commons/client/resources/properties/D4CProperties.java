package bpm.gwt.workflow.commons.client.resources.properties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;

public class D4CProperties extends Composite {

	private static D4CPropertiesUiBinder uiBinder = GWT.create(D4CPropertiesUiBinder.class);

	interface D4CPropertiesUiBinder extends UiBinder<Widget, D4CProperties> {
	}

	@UiField
	TextHolderBox txtUrl, txtOrg, txtSelectedDataset/*, txtSelectedResource*/;

//	private CkanPackage ckanPackage;
//	private boolean canAddNew;

	public D4CProperties(IWait waitPanel, String org) {
		this(waitPanel, null, null, org, false);

		txtUrl.setVisible(false);
		
//		panelBtn.setVisible(false);
		txtSelectedDataset.setVisible(false);
//		txtSelectedResource.setVisible(false);
	}

	public D4CProperties(IWait waitPanel, CkanPackage ckanPackage, String ckanUrl, String org, boolean canAddNew) {
		initWidget(uiBinder.createAndBindUi(this));
//		this.ckanPackage = ckanPackage;
//		this.canAddNew = canAddNew;
		
		if (ckanUrl != null && !ckanUrl.isEmpty()) {
			txtUrl.setText(ckanUrl);
		}
		
		if (org != null && !org.isEmpty()) {
			txtOrg.setText(org);
		}
		
		refreshPackage(ckanPackage);
	}

	private void refreshPackage(CkanPackage ckanPackage) {
		if (ckanPackage != null) {
			txtSelectedDataset.setText(ckanPackage.getName());
			
//			if (ckanPackage.getSelectedResource() != null) {
//				txtSelectedResource.setText(ckanPackage.getSelectedResource().toString());
//			}
		}
	}

	public boolean isValid() {
		String url = txtUrl.getValue();
		String org = txtOrg.getValue();
		return url != null && !url.isEmpty() && org != null && !org.isEmpty();
	}
	
//	@UiHandler("btnLoad")
//	public void onLoad(ClickEvent event) {
//		String ckanUrl = txtUrl.getText();
//		String org = txtOrg.getText();
//
//		CkanPackageDialog dial = new CkanPackageDialog(this, ckanUrl, org, canAddNew, ckanPackage);
//		dial.center();
//	}
	
	public String getUrl() {
		return txtUrl.getText();
	}
	
	public String getOrg() {
		return txtOrg.getText();
	}
	
//	public String getApiKey() {
//		return txtApiKey.getText();
//	}
	
	public CkanPackage getCkanPackage() {
		String name = txtSelectedDataset.getText();
//		String description = txtDescription.getText();
		
		return new CkanPackage(null, name, "");
	}
}
