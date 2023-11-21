package bpm.gwt.commons.client.datasource;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomCheckbox;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.dialog.LaunchContractETLDialog;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.panels.upload.DocumentManager.DocumentUploadHandler;
import bpm.gwt.commons.client.panels.upload.FileUploadWidget;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceArchitect;
import bpm.vanilla.platform.core.beans.data.IDatasourceObject;

public class DatasourceArchitectPage extends Composite implements IDatasourceObjectPage, IWait, DocumentUploadHandler {

	private static DatasourceArchitectPageUiBinder uiBinder = GWT.create(DatasourceArchitectPageUiBinder.class);

	interface DatasourceArchitectPageUiBinder extends UiBinder<Widget, DatasourceArchitectPage> {
	}

	interface MyStyle extends CssResource {
		String btnBottom();
		String panelButton();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	CustomCheckbox chkOtherRepository, chkNewSupContract;
	
	@UiField
	LabelTextBox txtUrl, txtPass, txtLogin, txtSeparator;
	
	@UiField
	SimplePanel panelButton;
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	ListBoxWithButton<Supplier> supplierListBox;

	@UiField
	ListBoxWithButton<Contract> contractListBox;
	
	@UiField
	Image btnRefresh;
	
	@UiField(provided = true)
	FileUploadWidget uploadPanel;
	
	private DatasourceWizard parent;
	
	private DatasourceArchitect datasource;

	public DatasourceArchitectPage(DatasourceWizard parent, Datasource datasource) {
		uploadPanel = new FileUploadWidget(CommonConstants.DATASOURCE_ARCHITECT_SERVLET, null);
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.datasource = datasource != null ? (DatasourceArchitect) datasource.getObject() : null;
		
		uploadPanel.setUploadHandler(this);
	
		supplierListBox.setEnabled(false);
		contractListBox.setEnabled(false);
		
		txtSeparator.setEnabled(true);
		
		loadDatasource(this.datasource);
	}
	
	private void loadDatasource(DatasourceArchitect datasource) {
		if (datasource != null) {
			chkOtherRepository.setValue(datasource.isOtherRepository());
			txtUrl.setText(datasource.getUrl());
			txtLogin.setText(datasource.getUser());
			txtPass.setText(datasource.getPassword());
			
			loadSuppliers();
		}
		else {
			loadContext();
		}
		
		updateUi();
	}
	
	private void loadContext() {
		parent.showWaitPart(true);
		LoginService.Connect.getInstance().getVanillaContext(new GwtCallbackWrapper<HashMap<String, String>>(this.parent, true) {

			@Override
			public void onSuccess(HashMap<String, String> result) {
				String url = result.get("url");
				String login = result.get("login");
				String password = result.get("password");
						
				txtUrl.setText(url);
				txtLogin.setText(login);
				txtPass.setText(password);
				
				loadSuppliers();
			}
		}.getAsyncCallback());
	}

	private void updateUi() {
		boolean isOtherRepository = chkOtherRepository.getValue();
		txtUrl.setVisible(isOtherRepository);
		txtLogin.setVisible(isOtherRepository);
		txtPass.setVisible(isOtherRepository);
		panelButton.setVisible(isOtherRepository);
		
		boolean isNewSupCont = chkNewSupContract.getValue();
		contractListBox.setVisible(!isNewSupCont);
		supplierListBox.setVisible(true);
		uploadPanel.setVisible(isNewSupCont);
	}
	
	@UiHandler("chkOtherRepository")
	public void onOtherRepository(ClickEvent event) {
		updateUi();
	}
	
	@UiHandler("chkNewSupContract")
	public void onNewSupContract(ClickEvent event) {
		updateUi();
	}
	
	@UiHandler("btnLoadSuppliers")
	public void onLoadSuppliers(ClickEvent event) {
		loadSuppliers();
	}

	private void loadSuppliers() {
		// Recuperation de la liste des suppliers
		AsyncCallback<List<Supplier>> callback = new GwtCallbackWrapper<List<Supplier>>(parent, true) {

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				supplierListBox.clear();
				contractListBox.clear();
				supplierListBox.setEnabled(false);
				contractListBox.setEnabled(false);
			}
			
			@Override
			public void onSuccess(List<Supplier> result) {
				supplierListBox.setList(result, true);
				supplierListBox.setEnabled(true);
				contractListBox.setEnabled(true);
				
				if (datasource != null && datasource.getSupplierId() > 0 && result != null) {
					for (Supplier supplier: result) {
						if (supplier.getId() == datasource.getSupplierId()) {
							supplierListBox.setSelectedObject(supplier);
							
							loadContract(supplier);
							break;
						}
					}
				}
			}

		}.getAsyncCallback();

		parent.showWaitPart(true);
		if (chkOtherRepository.getValue()) {
			CommonService.Connect.getInstance().getSuppliersByServer(txtLogin.getText(), txtPass.getText(), txtUrl.getText(), callback);
		}
		else {
			CommonService.Connect.getInstance().getSuppliers(callback);
		}
	}

	@UiHandler("supplierListBox")
	public void onChangeSupplier(ChangeEvent event) {
		Supplier supplier = supplierListBox.getSelectedObject();
		loadContract(supplier);
	}
	
	private void loadContract(Supplier supplier) {
		contractListBox.clear();
		if (supplier != null) {
			contractListBox.setList(supplier.getContracts(), true);
			
			if (datasource != null && datasource.getContractId() > 0 && supplier.getContracts() != null) {
				for (Contract contract : supplier.getContracts()) {
					if (contract.getId() == datasource.getContractId()) {
						contractListBox.setSelectedObject(contract);
						break;
					}
				}
			}
		}
	}
	
	@UiHandler("contractListBox")
	public void onChangeContract(ChangeEvent event) {
		Contract contract = contractListBox.getSelectedObject();

		btnRefresh.setVisible(contract.hasInput());
		String format = getFormat(contract);
		txtSeparator.setEnabled(format.equalsIgnoreCase("csv"));
	}
	
	private String getFormat(Contract contract) {
		return contract.getFileVersions() != null ? contract.getFileVersions().getCurrentVersion(contract.getVersionId()).getFormat() : "";
	}
	
	@UiHandler("btnRefresh")
	public void onRefreshContract(ClickEvent event) {
		final Contract contract = contractListBox.getSelectedObject();
		
		LaunchContractETLDialog dial = new LaunchContractETLDialog(contract.getId());
		dial.center();
	}
	
	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return !chkNewSupContract.getValue() || (chkNewSupContract.getValue() && uploadPanel.isFileUploaded());
	}

	@Override
	public boolean canGoFurther() {
		return false;
	}

	@Override
	public int getIndex() {
		return 0;
	}

	@Override
	public IDatasourceObject getDatasourceObject() {
		DatasourceArchitect object = null;
		if (datasource != null) {
			object = datasource;
		}
		else {
			object = new DatasourceArchitect();
		}
		
		Supplier supplier = supplierListBox.getSelectedObject();
		Contract contract = contractListBox.getSelectedObject();
		
		object.setSeparator(txtSeparator.getText());
		object.setUrl(txtUrl.getText());
		object.setUser(txtLogin.getText());
		object.setPassword(txtPass.getText());
		object.setSupplierId(supplier.getId());
		object.setContractId(contract.getId());
		object.setHasInput(contract.hasInput());
		return object;
	}

	public void createSupplierContract() {
		final String fileName = uploadPanel.getFileName().substring(0, uploadPanel.getFileName().indexOf("."));
		
		Supplier supplier = supplierListBox.getSelectedObject();
		if (supplier == null) {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.PleaseSelectASupplier());
			return;
		}
		
		Contract contract = new Contract();
		contract.setName(supplier.getName());
		
		CommonService.Connect.getInstance().addContract(supplier, contract, new GwtCallbackWrapper<Contract>(this.parent, true, true) {
			@Override
			public void onSuccess(Contract result) {
				contractListBox.clear();
				contractListBox.addItem("");
				contractListBox.addItem(result);
				contractListBox.setSelectedObject(result);
				
				CommonService.Connect.getInstance().confirmUpload(result, fileName, uploadPanel.getFileName(), new GwtCallbackWrapper<Void>(DatasourceArchitectPage.this.parent, false) {

					@Override
					public void onSuccess(Void result) {
						chkNewSupContract.setValue(false);
						updateUi();
					}
				}.getAsyncCallback());
			}
		}.getAsyncCallback());
		
	}

	@Override
	public void showWaitPart(boolean visible) { }

	@Override
	public void onUploadSuccess(Integer documentId, String filename) {
		createSupplierContract();
	}
}
