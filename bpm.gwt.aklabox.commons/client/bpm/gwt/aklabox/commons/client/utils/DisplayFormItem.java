package bpm.gwt.aklabox.commons.client.utils;

import bpm.aklabox.workflow.core.model.resources.FormCell;
import bpm.aklabox.workflow.core.model.resources.FormCellResult;
import bpm.document.management.core.model.Documents;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class DisplayFormItem extends Composite {

	private static DisplayFormItemUiBinder uiBinder = GWT.create(DisplayFormItemUiBinder.class);

	interface DisplayFormItemUiBinder extends UiBinder<Widget, DisplayFormItem> {
	}

	@UiField Label lblCellName, lblCellSize, lblCellPoint;
	public @UiField Image imgCellInfo, btnSave;
	@UiField TextArea areaOcrResult;
	@UiField HTMLPanel panel;
	
	private FormCell cell;
	private Documents doc;
	private FormCellResult cellResult;
	
	public DisplayFormItem(Documents doc, FormCell cell, int i) {
		initWidget(uiBinder.createAndBindUi(this));
		btnSave.setVisible(false);
		this.cell = cell;
		this.doc = doc;
		getFormCellResult();
		if(i % 2 == 0){
			panel.getElement().getStyle().setBackgroundColor("aliceblue");
		}
		lblCellName.setText(cell.getName());
		lblCellPoint.setText("( " + String.valueOf(cell.getxAxis()) + " , " + String.valueOf(cell.getyAxis()) + " )");
		lblCellSize.setText(String.valueOf(cell.getWidth()) + "px by " + String.valueOf(cell.getHeight()) + "px");
	}
	
	private void getFormCellResult(){
		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().getFormCellResult(doc, cell, new AsyncCallback<FormCellResult>() {
			
			@Override
			public void onSuccess(FormCellResult result) {
				DisplayFormItem.this.cellResult = result;
				WaitDialog.showWaitPart(false);
				if(result.getOcrResult()!=null){
					areaOcrResult.setText(result.getOcrResult());
					imgCellInfo.setUrl(PathHelper.getRightPath(result.getImageCell()));
				}
				
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				new DefaultResultDialog(caught.getMessage(), "failure").show();
				WaitDialog.showWaitPart(false);
			}
		});
	}
	
	@UiHandler("areaOcrResult")
	void onEditOcrResult(KeyUpEvent e){
		btnSave.setVisible(true);
	}
	
	@UiHandler("btnSave")
	void onUpdateResult(ClickEvent e){
		if(cellResult.getId() != null){
			WaitDialog.showWaitPart(true);
			cellResult.setOcrResult(areaOcrResult.getText());
			AklaCommonService.Connect.getService().updateFormCellResult(cellResult, new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					btnSave.setVisible(false);
					WaitDialog.showWaitPart(false);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					new DefaultResultDialog(caught.getMessage(), "failure").show();
					WaitDialog.showWaitPart(false);
				}
			});
		}
		
	}

	public FormCell getCell() {
		return cell;
	}

	public FormCellResult getCellResult() {
		return cellResult;
	}

	public String getWrittenResult() {
		return areaOcrResult.getText();
	}
	
	public void setWrittenResult(String result) {
		areaOcrResult.setText(result);
	}
}
