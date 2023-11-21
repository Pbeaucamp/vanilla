package bpm.gwt.aklabox.commons.client.utils;

import java.util.List;

import bpm.aklabox.workflow.core.model.resources.FormCell;
import bpm.aklabox.workflow.core.model.resources.FormCellLink;
import bpm.document.management.core.model.FormField;
import bpm.gwt.aklabox.commons.client.panels.OCRFormPanel;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class CellTableItem extends Composite {

	private static CellTableItemUiBinder uiBinder = GWT.create(CellTableItemUiBinder.class);

	interface CellTableItemUiBinder extends UiBinder<Widget, CellTableItem> {
	}

	@UiField
	Label lblCellName, lblCellXAxis, lblCellYAxis, lblCellWidth, lblCellHeight, btnRemove;
	@UiField
	ListBox lstFormFields;
	
	private OCRFormPanel addResourceDialog;
	private FormCell cell;
	
	private int formId;
	
	public CellTableItem(FormCell cell, OCRFormPanel addResourceDialog) {
		initWidget(uiBinder.createAndBindUi(this));
		lblCellName.setText(cell.getName());
		lblCellXAxis.setText(String.valueOf(cell.getxAxis()));
		lblCellYAxis.setText(String.valueOf(cell.getyAxis()));
		lblCellWidth.setText(String.valueOf(cell.getWidth()));
		lblCellHeight.setText(String.valueOf(cell.getHeight()));
		this.cell = cell;
		this.addResourceDialog = addResourceDialog;
		lstFormFields.setVisible(false);
	}
	
	public CellTableItem(FormCell cell, OCRFormPanel addResourceDialog,/* int aklaBoxServerId,*/ int formId) {
		initWidget(uiBinder.createAndBindUi(this));
		lblCellName.setText(cell.getName());
		lblCellXAxis.setText(String.valueOf(cell.getxAxis()));
		lblCellYAxis.setText(String.valueOf(cell.getyAxis()));
		lblCellWidth.setText(String.valueOf(cell.getWidth()));
		lblCellHeight.setText(String.valueOf(cell.getHeight()));
		this.cell = cell;
		this.formId = formId;
		getAllAklaBoxFormFields(/*aklaBoxServerId,*/ formId);
		this.addResourceDialog = addResourceDialog;
	}
	
	@UiHandler("btnRemove")
	void onRemoveCell(ClickEvent e){
		this.removeFromParent();
		addResourceDialog.getFormCells().remove(cell);
		addResourceDialog.checkSelectedForm();
	}
	
	private void getAllAklaBoxFormFields(/*int aklaBoxServerId,*/ int formId) {
		
		lstFormFields.clear();
		lstFormFields.addItem("", "0");
		
		if(formId != 0){
			WaitDialog.showWaitPart(true);
			AklaCommonService.Connect.getService().getAllAklaBoxFormFields(/*aklaBoxServerId,*/ formId, new AsyncCallback<List<FormField>>() {
				
				@Override
				public void onSuccess(final List<FormField> fields) {
					WaitDialog.showWaitPart(false);
					
					for(FormField field : fields){
						lstFormFields.addItem(field.getLabel(), field.getId()+"");
					}
					
					if(cell.getId() != 0){
						AklaCommonService.Connect.getService().getAllFormCellLinksbyFormCell(cell, new AsyncCallback<List<FormCellLink>>() {
							
							@Override
							public void onSuccess(List<FormCellLink> links) {
								if(!links.isEmpty()){
									FormCellLink link = links.get(0);
									for(FormField field : fields){
										if(link.getIdAklaFormField() == field.getId()){
											lstFormFields.setSelectedIndex(fields.indexOf(field)+1);
										}
									}
								}
								
							}
							
							@Override
							public void onFailure(Throwable caught) {
								WaitDialog.showWaitPart(false);
								new DefaultResultDialog(caught.getMessage(), "failure").show();
							}
						});
					}
					
				}
				
				@Override
				public void onFailure(Throwable caught) {
					WaitDialog.showWaitPart(false);
					new DefaultResultDialog(caught.getMessage(), "failure").show();
				}
			});
		}
		
	}

	public int getFormId() {
		return formId;
	}
	
	public int getFormFieldId() {
		return Integer.parseInt(lstFormFields.getValue(lstFormFields.getSelectedIndex()));
	}
}
