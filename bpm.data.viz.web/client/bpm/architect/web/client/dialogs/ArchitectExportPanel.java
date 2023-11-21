package bpm.architect.web.client.dialogs;

import java.util.ArrayList;
import java.util.List;

import bpm.architect.web.client.services.ArchitectService;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ArchitectExportPanel extends Composite {

	private static ArchitectExportPanelUiBinder uiBinder = GWT.create(ArchitectExportPanelUiBinder.class);

	interface ArchitectExportPanelUiBinder extends UiBinder<Widget, ArchitectExportPanel> {}

	@UiField
	ListBoxWithButton<Supplier> lstSupp;
	
	@UiField
	ListBoxWithButton<Contract> lstCont;
	
	@UiField
	ListBoxWithButton<String> lstFormat;
	
//	@UiField
//	LabelTextBox txtName;
	
	private PreparationExportDialog exportDialog;
	
	public ArchitectExportPanel(PreparationExportDialog ped) {
		initWidget(uiBinder.createAndBindUi(this));
		this.exportDialog = ped;
		
		ArchitectService.Connect.getInstance().getSuppliers(new GwtCallbackWrapper<List<Supplier>>(null, false) {
			@Override
			public void onSuccess(List<Supplier> result) {
				lstSupp.setList(result, true);
			}
			
		}.getAsyncCallback());
		
		addFormat();
		//lstFormat.setVisible(false);
		lstFormat.setEnabled(false);
	}
	
	public ArchitectExportPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		ArchitectService.Connect.getInstance().getSuppliers(new GwtCallbackWrapper<List<Supplier>>(null, false) {
			@Override
			public void onSuccess(List<Supplier> result) {
				lstSupp.setList(result, true);
			}
			
		}.getAsyncCallback());
	}

	@UiHandler("lstSupp")
	public void onChangeSupplier(ChangeEvent event) {
		if(lstSupp.getSelectedObject() != null) {
			lstCont.setList(lstSupp.getSelectedObject().getContracts(), true);
		}
		else {
			lstCont.clear();
		}
	}
	
	@UiHandler("lstFormat")
	public void onChangeFormat(ChangeEvent event) {
		this.exportDialog.setFormat(lstFormat.getSelectedItem());
	}
	
	@UiHandler("lstCont")
	public void onChangeContract(ChangeEvent event) {
		if(lstCont.getSelectedObject().getDocId() == null) {
			//lstFormat.setVisible(true);
			lstFormat.setEnabled(true);
		}
		else {
			ArchitectService.Connect.getInstance().getFormat(lstCont.getSelectedObject(), new GwtCallbackWrapper<String>(null, false) {

				@Override
				public void onSuccess(String result) {
					// TODO Auto-generated method stub
					lstFormat.setSelectedObject(result);
					exportDialog.setFormat(lstFormat.getSelectedItem());
					//exportDialog.setFormat(result);
				}
				
			}.getAsyncCallback());
			
			lstFormat.setEnabled(false);
			//lstFormat.setVisible(false);
		}
	}
	
	public void addFormat() {
		List<String> lstFrmt = new ArrayList<String>();
		lstFrmt.add("csv");
		lstFrmt.add("xlsx");
		lstFormat.setList(lstFrmt);
	}
	
	public Contract getSelectedContract() {
		return lstCont.getSelectedObject();
	}
}
