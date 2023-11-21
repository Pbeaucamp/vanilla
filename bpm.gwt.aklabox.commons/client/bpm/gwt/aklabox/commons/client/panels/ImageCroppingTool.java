package bpm.gwt.aklabox.commons.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.aklabox.workflow.core.model.resources.FormCell;
import bpm.aklabox.workflow.core.model.resources.IResource;
import bpm.aklabox.workflow.core.model.resources.StandardForm;
import bpm.document.management.core.model.Form;
import bpm.gwt.aklabox.commons.client.dialogs.OCRModelViewer;
import bpm.gwt.aklabox.commons.client.utils.AklaTextBox;
import bpm.gwt.aklabox.commons.client.utils.CellTableItem;
import bpm.gwt.aklabox.commons.client.utils.ChildDialogComposite;
import bpm.gwt.aklabox.commons.client.utils.DefaultDialog;
import bpm.gwt.aklabox.commons.client.utils.FileUploadInterface;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ImageCroppingTool extends ChildDialogComposite {

	private static ImageCroppingToolUiBinder uiBinder = GWT
			.create(ImageCroppingToolUiBinder.class);

	interface ImageCroppingToolUiBinder extends
			UiBinder<Widget, ImageCroppingTool> {
	}

	@UiField FileUploadInterface imageUpload;
	@UiField HTMLPanel panel, cellPanel, celLFieldPanel;
	@UiField AklaTextBox txtCellName;
	@UiField Button btnUpdate, btnSave;
	@UiField ListBox lstForms;
	@UiField SimplePanel imgForm;
	
	private OCRFormPanel addResourceDialog;
	private DefaultDialog dialog;
	private String scannedDoc;
	private StandardForm resource;
	
	private OCRModelViewer ocrviewer;
	
	public ImageCroppingTool(OCRFormPanel addResourceDialog, DefaultDialog dialog, IResource resource, List<Form> forms) {
		initWidget(uiBinder.createAndBindUi(this));
		imageUpload.setImageTool(this);
		celLFieldPanel.setVisible(false);
		this.addResourceDialog = addResourceDialog;  
		this.dialog = dialog;
		this.setResource((StandardForm) resource);
		
		
		lstForms.clear();
		lstForms.addItem("", "0");
		
		if(forms != null && !forms.isEmpty()){
			for(Form form : forms){
				lstForms.addItem(form.getFormName(), form.getId()+"");
			}
		}
		
		if(resource.getId()!=0){
			btnSave.removeFromParent();
			if(((StandardForm) resource).getBaseImage()!=null){
				imageUpload.addCropPanel(((StandardForm) resource).getBaseImage());
			}
			for(int i=0; i<lstForms.getItemCount(); i++){
				if(Integer.parseInt(lstForms.getValue(i)) == addResourceDialog.getSelectedAklaboxForm()){
					lstForms.setSelectedIndex(i);
				}
			}
			lstForms.setEnabled(false);
			onInitFormCell();
			String path = ((StandardForm) resource).getBaseImage();
			if(path != null && !path.isEmpty()){
				imgForm.setVisible(true);
				ocrviewer = new OCRModelViewer((StandardForm) resource, addResourceDialog.getFormCells());
				imgForm.clear();
				imgForm.add(ocrviewer);
			}
		}else{
			btnUpdate.removeFromParent();
			imgForm.setVisible(false);
		}
		
		
	}

	@UiHandler("btnAddCell")
	void onAddCell(ClickEvent e){
		int x = imageUpload.getCrop().getSelectionXCoordinate();
		int y = imageUpload.getCrop().getSelectionYCoordinate();
		int w = imageUpload.getCrop().getSelectionWidth();
		int h = imageUpload.getCrop().getSelectionHeight();
		
//		CellTableItem item = new CellTableItem(new FormCell(txtCellName.getText(), x, y, w, h), addResourceDialog, 
//				addResourceDialog.getSelectedAklaboxServer(), Integer.parseInt(lstForms.getValue(lstForms.getSelectedIndex())));
		addResourceDialog.getFormCells().add(new FormCell(txtCellName.getText(), x, y, w, h));
		if(/*addResourceDialog.getSelectedAklaboxServer() != 0 && */lstForms.getSelectedIndex() != 0){
			cellPanel.add(new CellTableItem(new FormCell(txtCellName.getText(), x, y, w, h), addResourceDialog, 
					/*addResourceDialog.getSelectedAklaboxServer(),*/ Integer.parseInt(lstForms.getValue(lstForms.getSelectedIndex()))));
		} else {
			cellPanel.add(new CellTableItem(new FormCell(txtCellName.getText(), x, y, w, h), addResourceDialog));
		}
		//cellPanel.add(item);
		
		checkSelectedForm();
//		if(ocrviewer != null){
//			ocrviewer.updateCells(addResourceDialog.getFormCells());
//		}
	}
	
	@UiHandler("btnSave")
	void onSaveFromCrop(ClickEvent e){
		addResourceDialog.updateTableCells(getCellItems());
		addResourceDialog.setResource(resource);
		addResourceDialog.onSaveResource(e);
		dialog.hide();
	}
	
	@UiHandler("btnUpdate")
	void onUpdateResource(ClickEvent e){
		addResourceDialog.updateTableCells(getCellItems());
		addResourceDialog.onUpdateResource(e);
		dialog.hide();
	}

	@UiHandler("btnCancel")
	void onCancelFromCrop(ClickEvent e){
		addResourceDialog.updateTableCells(getCellItems());
		dialog.hide();
	}

	public HTMLPanel getCelLFieldPanel() {
		return celLFieldPanel;
	}

	public void setCelLFieldPanel(HTMLPanel celLFieldPanel) {
		this.celLFieldPanel = celLFieldPanel;
	}

	public String getScannedDoc() {
		return scannedDoc;
	}

	public void setScannedDoc(String scannedDoc) {
		this.scannedDoc = scannedDoc;
	}
	
	private void onInitFormCell(){
		for(FormCell cell : addResourceDialog.getFormCells()){
			if(/*addResourceDialog.getSelectedAklaboxServer() != 0 &&*/ lstForms.getSelectedIndex() != 0){
				cellPanel.add(new CellTableItem(cell, addResourceDialog, /*addResourceDialog.getSelectedAklaboxServer(), */
						Integer.parseInt(lstForms.getValue(lstForms.getSelectedIndex()))));
			} else {
				cellPanel.add(new CellTableItem(cell, addResourceDialog));
			}
		}
		checkSelectedForm();
	}

	public StandardForm getResource() {
		return resource;
	}

	public void setResource(StandardForm resource) {
		this.resource = resource;
	}
	
	private List<CellTableItem> getCellItems(){
		List<CellTableItem> result = new ArrayList<>();
		for(Widget w : cellPanel){
			if(w instanceof CellTableItem){
				result.add((CellTableItem)w);
			}
		}
		return result;
	}
	
	@UiHandler("lstForms")
	public void onChangeForm(ClickEvent e){
		addResourceDialog.setSelectedAklaboxForm(lstForms.getSelectedIndex());
	}
	
	public void checkSelectedForm() {
		lstForms.setEnabled(true);
		for(Widget w : cellPanel){
			if(w instanceof CellTableItem){
				if(((CellTableItem) w).getFormId() != 0){
					lstForms.setEnabled(false);
					break;
				}
			}
		}
		
	}
	
	public void updateUI(){
		String path = ((StandardForm) resource).getBaseImage();
		if(path != null && !path.isEmpty()){
			imgForm.setVisible(true);
			ocrviewer = new OCRModelViewer((StandardForm) resource, addResourceDialog.getFormCells());
			imgForm.clear();
			imgForm.add(ocrviewer);
		} else {
			imgForm.setVisible(false);
		}
	}
}
