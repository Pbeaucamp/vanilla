package bpm.gwt.commons.client.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.custom.TextAreaHolderBox;
import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.listeners.CkanManager;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class CkanPackageDialog extends AbstractDialogBox {

	private static final String NEW_RESOURCE = "NEW_RESOURCE";
	
	private static CkanPackageDialogUiBinder uiBinder = GWT.create(CkanPackageDialogUiBinder.class);

	interface CkanPackageDialogUiBinder extends UiBinder<Widget, CkanPackageDialog> {
	}
	
	@UiField
	RadioButton btnExisting, btnNew;

	@UiField
	ListBoxWithButton<CkanPackage> lstPackages;

	@UiField
	ListBoxWithButton<CkanResource> lstResources;
	
	@UiField
	TextHolderBox txtResourceNameFromDataset, txtResourceNameFromNew, txtName;
	
	@UiField
	TextAreaHolderBox txtDescription;
	
	private CkanManager parent;
	
	private String ckanUrl;
	private String org;
	
	private boolean canAddNew;

	public CkanPackageDialog(CkanManager parent, String ckanUrl, String org, boolean canAddNew, CkanPackage ckanPackage) {
		super(LabelsConstants.lblCnst.Ckan(), false, true);
		this.parent = parent;
		this.ckanUrl = ckanUrl;
		this.org = org;
		this.canAddNew = canAddNew;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		updateUi(true);

		txtResourceNameFromDataset.setVisible(canAddNew);
		txtResourceNameFromNew.setVisible(canAddNew);
		
		btnExisting.setVisible(canAddNew);
		btnNew.setVisible(canAddNew);
		
		txtName.setVisible(canAddNew);

		loadPackages(ckanPackage);
	}
	
	@UiHandler("btnExisting")
	public void onExisting(ClickEvent event) {
		updateUi(true);
	}
	
	@UiHandler("btnNew")
	public void onNew(ClickEvent event) {
		updateUi(false);
	}
	
	private void updateUi(boolean isExisting) {
		lstPackages.setEnabled(isExisting);
		lstResources.setEnabled(isExisting);
		updateResourceUi(getSelectedResource());
		
		txtName.setEnabled(!isExisting);
		txtDescription.setEnabled(!isExisting);
		txtResourceNameFromNew.setEnabled(!isExisting);
	}

	private void loadPackages(final CkanPackage selectedCkanPackage) {
		CommonService.Connect.getInstance().getCkanDatasets(ckanUrl, org, new GwtCallbackWrapper<List<CkanPackage>>(this, true, true) {

			@Override
			public void onSuccess(List<CkanPackage> result) {
				lstPackages.clear();
				lstPackages.setList(result, true);
				
				if (selectedCkanPackage != null) {
					
					if (selectedCkanPackage.getId() == null) {
						updateUi(false);
						
						btnNew.setValue(true);
						txtName.setText(selectedCkanPackage.getName());
						loadResources(selectedCkanPackage, selectedCkanPackage.getSelectedResource());
					}
					else {
						for (CkanPackage pack : result) {
							if (pack.getId().equals(selectedCkanPackage.getId())) {
								lstPackages.setSelectedObject(pack);
								
								loadResources(pack, selectedCkanPackage.getSelectedResource());
								break;
							}
						}
					}
				}
			}
		}.getAsyncCallback());
	}
	
	@UiHandler("lstPackages")
	public void onPackageSelected(ChangeEvent event) {
		CkanPackage pack = getSelectedPack();
		loadResources(pack, null);
	}
	
	@UiHandler("lstResources")
	public void onResourceSelected(ChangeEvent event) {
		CkanResource resource = getSelectedResource();
		updateResourceUi(resource);
	}
	
	private void updateResourceUi(CkanResource resource) {
		boolean isExisting = btnExisting.getValue();
		txtResourceNameFromDataset.setEnabled(isExisting && resource != null && resource.getId().equals(NEW_RESOURCE));
	}
	
	private void loadResources(CkanPackage selectedCkanPackage, CkanResource selectedResource) {
		lstResources.clear();

		if (selectedCkanPackage != null) {
			List<CkanResource> resources = selectedCkanPackage.getResources();
			if (resources == null) {
				resources = new ArrayList<CkanResource>();
			}
			
			if (canAddNew) {
				resources.add(0, new CkanResource(NEW_RESOURCE, LabelsConstants.lblCnst.NewResource(), null, null));
			}
			lstResources.setList(selectedCkanPackage.getResources());
			
			if (selectedResource != null) {
				
				if (selectedResource.getId() == null) {
					if (btnExisting.getValue()) {
						txtResourceNameFromDataset.setText(selectedResource.getName());
						updateResourceUi(selectedResource);
					}
					else {
						txtResourceNameFromNew.setText(selectedResource.getName());
					}
				}
				else {
					for (CkanResource res : selectedCkanPackage.getResources()) {
						if (res.getId().equals(selectedResource.getId())) {
							lstResources.setSelectedObject(res);
							break;
						}
					}
				}
			}
			else {
				updateResourceUi(getSelectedResource());
			}
		}
	}

	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			CkanPackage pack = null;
			CkanResource resource = null;
			
			if (btnExisting.getValue()) {
				String resourceName = txtResourceNameFromDataset.getText();
				resource = new CkanResource(null, resourceName, null, null);

				pack = getSelectedPack();
				
				CkanResource tmp = getSelectedResource();
				if (!tmp.getId().equals(NEW_RESOURCE)) {
					resource = tmp;
				}
				else if (resourceName == null || resourceName.isEmpty()) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.TheResourceNameCannotBeEmpty());
					return;
				}
			}
			else {
				String resourceName = txtResourceNameFromNew.getText();
				resource = new CkanResource(null, resourceName, null, null);
				
				if (resourceName == null || resourceName.isEmpty()) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.TheResourceNameCannotBeEmpty());
					return;
				}
				
				String name = txtName.getText();
				String description = txtDescription.getText();
				
				pack = new CkanPackage(null, name, description);
			}
			
			if (pack == null) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.PackageNotValid());
				return;
			}
			
			hide();
			parent.managePackage(pack, resource);
		}
	};
	
	private CkanPackage getSelectedPack() {
		return lstPackages.getSelectedObject();
	}
	
	private CkanResource getSelectedResource() {
		return lstResources.getSelectedObject();
	}
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

}
