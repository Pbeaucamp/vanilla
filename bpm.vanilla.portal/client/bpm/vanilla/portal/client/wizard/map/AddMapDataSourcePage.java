package bpm.vanilla.portal.client.wizard.map;

import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.vanilla.map.core.design.MapDataSource;
import bpm.vanilla.map.core.design.MapDatasourceKML;
import bpm.vanilla.map.core.design.MapDatasourceWFS;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AddMapDataSourcePage extends Composite implements IGwtPage {
	private static AddMapDataSourcePageUiBinder uiBinder = GWT.create(AddMapDataSourcePageUiBinder.class);

	interface AddMapDataSourcePageUiBinder extends UiBinder<Widget, AddMapDataSourcePage> {}

	interface MyStyle extends CssResource {
		
	}
	
	@UiField
	Label lblDataSource, lblNomDataSource, lblUrl, lblDriver, lblLogin, lblMdp, lblNewDataSource;
	
	@UiField
	TextBox txtNomDataSource, txtUrl, txtLogin;
	
	@UiField
	PasswordTextBox txtMdp;
	
	@UiField
	ListBox lstDataSource, lstDriver;
	
	@UiField
	MyStyle style;
	
	@UiField
	LabelTextBox txtLayer, txtWfsUrl, txtField;
	
	@UiField
	ListBoxWithButton<Supplier> supplierListBox;

	@UiField
	ListBoxWithButton<Contract> contractListBox;
	
	@UiField
	HTMLPanel contentPanel, sqlPanel, kmlPanel, wfsPanel;
	
	private IGwtWizard parent;
	private int index;
	
	
	public AddMapDataSourcePage(IGwtWizard parent, int index) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;
		
		if(((AddMapWizard)parent).getType().equals("SQL")) {
			kmlPanel.getElement().getStyle().setDisplay(Display.NONE);
			wfsPanel.getElement().getStyle().setDisplay(Display.NONE);
			lblDataSource.setText(ToolsGWT.lblCnst.MapDataSource());
			lblNewDataSource.setText(ToolsGWT.lblCnst.MapNewDataSource());
			lblNomDataSource.setText(ToolsGWT.lblCnst.MapDataSourceNom());
			lblUrl.setText(ToolsGWT.lblCnst.MapDataSourceUrl());
			lblDriver.setText(ToolsGWT.lblCnst.MapDataSourceDriver());
			lblLogin.setText(ToolsGWT.lblCnst.MapDataSourceLogin());
			lblMdp.setText(ToolsGWT.lblCnst.MapDataSourceMdp());
			lstDataSource.addItem(ToolsGWT.lblCnst.chooseMapDataSource());
			lstDriver.addItem(ToolsGWT.lblCnst.chooseMapDriver());
			lstDataSource.setSelectedIndex(0);
			lstDriver.setSelectedIndex(0);
		}
		else if(((AddMapWizard)parent).getType().equals("KML")) {
			sqlPanel.getElement().getStyle().setDisplay(Display.NONE);
			wfsPanel.getElement().getStyle().setDisplay(Display.NONE);
			loadSuppliers();
		}
		else {
			sqlPanel.getElement().getStyle().setDisplay(Display.NONE);
			kmlPanel.getElement().getStyle().setDisplay(Display.NONE);
			if(((AddMapWizard)parent).getEditedMap() != null) {
				MapDatasourceWFS wfs = (MapDatasourceWFS) ((AddMapWizard)parent).getEditedMap().getDataSetList().get(0).getDataSource();
				txtWfsUrl.setText(wfs.getUrl());
				txtLayer.setText(wfs.getLayer());
				txtField.setText(wfs.getField());
			}
		}
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
				
				if(((AddMapWizard)parent).getEditedMap() != null) {
					MapDatasourceKML datasource = (MapDatasourceKML) ((AddMapWizard)parent).getEditedMap().getDataSetList().get(0).getDataSource();
//					if (datasource != null && datasource.getSupplierId() > 0 && result != null) {
						for (Supplier supplier: result) {
							for(Contract c : supplier.getContracts()) {
								if(c.getId() == datasource.getContractId()) {
									supplierListBox.setSelectedObject(supplier);
									loadContract(supplier);
									break;
								}
							}
						}
//					}
				}
			}

		}.getAsyncCallback();
		CommonService.Connect.getInstance().getSuppliers(callback);
	}
	
	public void checkAllBox() {
		if(lstDataSource.getItemText(lstDataSource.getSelectedIndex()).equals(ToolsGWT.lblCnst.chooseMapDataSource())) {
			txtNomDataSource.setEnabled(true);
			txtUrl.setEnabled(true);
			lstDriver.setEnabled(true);
			txtLogin.setEnabled(true);
			txtMdp.setEnabled(true);
		}
		else {
			txtNomDataSource.setEnabled(false);
			txtUrl.setEnabled(false);
			lstDriver.setEnabled(false);
			txtLogin.setEnabled(false);
			txtMdp.setEnabled(false);
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
			if(((AddMapWizard)parent).getEditedMap() != null) {
				MapDatasourceKML datasource = (MapDatasourceKML) ((AddMapWizard)parent).getEditedMap().getDataSetList().get(0).getDataSource();
				for(Contract c : supplier.getContracts()) {
					if(c.getId() == datasource.getContractId()) {
						contractListBox.setSelectedObject(c);
						break;
					}
				}
			}
		}
	}
	
	public boolean newDataSource() {
		boolean existingDataSource = false;
		for(int i = 0; i < lstDataSource.getItemCount(); i++) {
			if(lstDataSource.getItemText(i).equals(txtNomDataSource.getText())) {
				existingDataSource = true;
			}
		}
		return existingDataSource ? false : txtUrl.getText().equals("") ? false : txtLogin.getText().equals("") ? false : txtMdp.getText().equals("") ? false : 
			lstDriver.getValue(lstDriver.getSelectedIndex()).equals(ToolsGWT.lblCnst.chooseMapDriver()) ? false : true;
	}
	
	public boolean isComplete() {
		if(((AddMapWizard)parent).getType().equals("SQL")) {
			checkAllBox();
			if(newDataSource()) {
				return true;
			}
			else if(!lstDataSource.getItemText(lstDataSource.getSelectedIndex()).equals(ToolsGWT.lblCnst.chooseMapDataSource())) {
				String dataSourceName = lstDataSource.getItemText(lstDataSource.getSelectedIndex());
				getDataSourceByName(dataSourceName);
				return true;
			}
			else {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean canGoFurther() {
		if(((AddMapWizard)parent).getType().equals("SQL")) {
			checkAllBox();
			return isComplete();
		}
		return false;
	}
	
	@UiHandler("lstDataSource")
	public void onDataSourceChange(ChangeEvent event) {
		parent.updateBtn();
	}

	public String getDataSource() {
		return lstDataSource.getItemText(lstDataSource.getSelectedIndex());
	}

	public void setDataSource(String dataSource) {
		lstDataSource.addItem(dataSource);
	}
	
	@UiHandler("txtNomDataSource")
	public void onNomDataSourceChange(ValueChangeEvent<String> event) {
		parent.updateBtn();
	}
	
	public String getNomDataSource() {
		return txtNomDataSource.getText();
	}

	public void setNomDataSource(String nomDataSource) {
		txtNomDataSource.setText(nomDataSource);
	}
	
	@UiHandler("txtUrl")
	public void onUrlChange(ValueChangeEvent<String> event) {
		parent.updateBtn();
	}
	
	public String getUrl() {
		return txtUrl.getText();
	}

	public void setUrl(String url) {
		txtUrl.setText(url);
	}

	@UiHandler("lstDriver")
	public void onDriverChange(ChangeEvent event) {
		parent.updateBtn();
	}

	public String getDriver() {
		return lstDriver.getValue(lstDriver.getSelectedIndex());
	}

	public void setDriver(String driver, String label) {
		if(label == null || label.isEmpty()) {
			label = driver;
		}
		lstDriver.addItem(label, driver);
	}

	@UiHandler("txtLogin")
	public void onLoginChange(ValueChangeEvent<String> event) {
		parent.updateBtn();
	}
	
	public String getLogin() {
		return txtLogin.getText();
	}

	public void setLogin(String login) {
		txtLogin.setText(login);
	}

	@UiHandler("txtMdp")
	public void onMdpChange(ValueChangeEvent<String> event) {
		parent.updateBtn();
	}
	
	public String getMdp() {
		return txtMdp.getText();
	}

	public void setMdp(String mdp) {
		txtMdp.setText(mdp);
	}

	@Override
	public int getIndex() {
		return index;
	}
	
	public void setSelectedDataSource(final String nameDataSource) {
		BiPortalService.Connect.getInstance().getLesDrivers(new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableToLoadDrivers());
			}

			@Override
			public void onSuccess(List<String> result) {
				List<String> lesDrivers = result;
				for(String driver : lesDrivers) {
					setDriver(driver, CommonConstants.jdbcLabels.get(driver));
				}
				
			}
		});
		
		BiPortalService.Connect.getInstance().getMapsDataSource(new AsyncCallback<List<MapDataSource>>() {

			@Override
			public void onFailure(Throwable caught) {

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableToLoadDataSource());
			}

			@Override
			public void onSuccess(List<MapDataSource> result) {
				List<MapDataSource> lesDataSource = result;
				for(MapDataSource dtS : lesDataSource) {
					setDataSource(dtS.getNomDataSource());
				}
				
				for(int i = 0; i < lstDataSource.getItemCount(); i++) {
					if(lstDataSource.getItemText(i).equals(nameDataSource)) {
						lstDataSource.setSelectedIndex(i);
					}
				}
			}
		});
	}
	
	private void getDataSourceByName(String name) {

		BiPortalService.Connect.getInstance().getDataSourceByName(name, new AsyncCallback<List<MapDataSource>>()  {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(List<MapDataSource> result) {
				txtNomDataSource.setText("");
				txtNomDataSource.setText(result.get(0).getNomDataSource());
				txtUrl.setText(result.get(0).getUrl());
				
				for(int i = 0; i < lstDriver.getItemCount(); i++) {
					if(lstDriver.getValue(i).equals(result.get(0).getDriver())) {
						lstDriver.setSelectedIndex(i);
					}
				}
				
				txtLogin.setText(result.get(0).getLogin());
				txtMdp.setText(result.get(0).getMdp());
			}
		});
	}

	public String getWfsUrl() {
		return txtWfsUrl.getText();
	}

	public String getWfsLayer() {
		return txtLayer.getText();
	}

	public String getField() {
		return txtField.getText();
	}
}
