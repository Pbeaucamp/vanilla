package bpm.gwt.commons.client.dataset;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.datasource.DatasourceDialog;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DatasetDefPage extends Composite implements IGwtPage {

	private static DatasetDefPageUiBinder uiBinder = GWT.create(DatasetDefPageUiBinder.class);

	interface DatasetDefPageUiBinder extends UiBinder<Widget, DatasetDefPage> {
	}

	@UiField
	ListBox lstDatasource, lstTypeDatasource;
	
	@UiField 
	TextBox txtName;
	
	@UiField 
	Button btnAddDatasource;
	
	@UiField 
	HTMLPanel datasourcePanel;
	
	@UiField 
	Label lblName, lblTypeDatasource, lblDatasource;

	private IGwtWizard parent;
	private Dataset dataset;
	
	private List<Datasource> datasourceList;
	
	private int initialDatasource = 0;

	public DatasetDefPage(IGwtWizard parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		fillTypes();
		this.dataset = new Dataset();
		btnAddDatasource.addStyleName(VanillaCSS.COMMONS_BUTTON);
		fillDatasources();
	}

	public DatasetDefPage(IGwtWizard parent, Dataset dataset) {
		this.parent = parent;
		this.dataset = dataset;
		
		initWidget(uiBinder.createAndBindUi(this));
		
		fillTypes();
		
		txtName.setText(dataset.getName());
		initialDatasource = dataset.getDatasourceId();
		btnAddDatasource.addStyleName(VanillaCSS.COMMONS_BUTTON);
		fillDatasources();
	}
	
	private void fillTypes() {
		int index = 0;
		for(DatasourceType type : DatasourceType.values()) {		
			lstTypeDatasource.addItem(type.getType(), type.toString());
			if(dataset != null && dataset.getId() > 0) {
				if(dataset.getDatasource().getType() == type) {
					lstTypeDatasource.setSelectedIndex(index);
				}
			}
			index++;
		}
	}
	
	@Override
	public boolean canGoBack() {
		return false;
	}

	@Override
	public boolean isComplete() {
		if(txtName.getText().equals("")){
			return false;
		}
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return isComplete();
	}

	@Override
	public int getIndex() {
		return 0;
	}

	@UiHandler("txtName")
	public void onNameKeyup(KeyUpEvent event) {
		
		parent.updateBtn();
	}
	
	@UiHandler("lstTypeDatasource")
	public void onTypeChange(ChangeEvent e) {
		

		lstDatasource.clear();
		int i = 0;
		for(Datasource dts : datasourceList) {
			if(dts.getType() == getSelectedType()) {
				lstDatasource.addItem(dts.getName(), dts.getId() + "");		
				if(dataset != null && dataset.getDatasourceId() == dts.getId()) {
					lstDatasource.setSelectedIndex(i);
				}		
				i++;
			}
		}

	}
	
	@UiHandler("btnAddDatasource")
	public void onAddDatasourceClick(ClickEvent event) {
		DatasourceDialog dial = new DatasourceDialog(DatasetDefPage.this);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				fillDatasources();
				
			}
		});
		
	}
	
	public DatasourceType getSelectedType() {
		return DatasourceType.valueOf(lstTypeDatasource.getValue(lstTypeDatasource.getSelectedIndex()));
	}
	
	public Datasource getSelectedDatasource() {

		int id = Integer.parseInt(lstDatasource.getValue(lstDatasource.getSelectedIndex()));
		for(Datasource ds : datasourceList) {
			if(ds.getId() == id) {
				return ds;
			}
		}
		return null;
	}
	
	public Dataset getDataset() {
		String name = getName();
		if (name == null || name.isEmpty()) {
			return null;
		}
		
		if(getSelectedType().equals(DatasourceType.R)){
			dataset.setName(getName());

			if(dataset.getMetacolumns() == null){
				dataset.setMetacolumns(new ArrayList<DataColumn>());
			}
			
		} else {
			dataset.setName(getName());
			if(dataset.getDatasourceId() != initialDatasource){
				dataset.setRequest("");
				dataset.setMetacolumns(new ArrayList<DataColumn>());
			}
			
		}
		Datasource dts = getSelectedDatasource();
		dataset.setDatasourceId(dts.getId());
		dataset.setDatasource(dts);
		return dataset;
	}
	
	public String getName() {
		return txtName.getText();
	}
	
	public void refresh(){
		initialDatasource = getSelectedDatasource().getId();
	}
	

	
	@UiHandler("lstDatasource")
	public void onDataSourceChange(ChangeEvent event) {

		parent.updateBtn();
	}
	private void fillDatasources() {
		lstDatasource.clear();
		lstDatasource.addItem(LabelsConstants.lblCnst.ChooseDatasource());
		if(parent instanceof DatasetWizard && ((DatasetWizard) parent).getUser() != null){
			CommonService.Connect.getInstance().getPermittedDatasources(new AsyncCallback<List<Datasource>>() {	
				@Override
				public void onSuccess(List<Datasource> result) {
					int i = 0;
					for(Datasource dts : result) {		

						lstDatasource.addItem(dts.getName(), String.valueOf(dts.getId()));		
						if(dataset != null && dataset.getDatasourceId() == dts.getId()) {
							lstDatasource.setSelectedIndex(i+1);
						}		
						i++;
					}
					datasourceList = result;
					onTypeChange(null);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), caught.getMessage(), LabelsConstants.lblCnst.GetDatasourceError(), caught);
					dial.center();
				}
			});
		} else {
			CommonService.Connect.getInstance().getDatasources(new AsyncCallback<List<Datasource>>() {	
				@Override
				public void onSuccess(List<Datasource> result) {
					int i = 0;
					for(Datasource dts : result) {		

						lstDatasource.addItem(dts.getName(), String.valueOf(dts.getId()));		
						if(dataset != null && dataset.getDatasourceId() == dts.getId()) {
							lstDatasource.setSelectedIndex(i+1);
						}		
						i++;
					}
					datasourceList = result;
					onTypeChange(null);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), caught.getMessage(), LabelsConstants.lblCnst.GetDatasourceError(), caught);
					dial.center();
				}
			});
		}
		
		
	}

	public IGwtWizard getWizardParent() {
		return parent;
	}

	


	
}
