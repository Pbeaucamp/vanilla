package bpm.fm.designer.web.client.dialog;

import bpm.fm.api.model.Observatory;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ObservatoryDialog extends AbstractDialogBox {

	private static ObservatoryDialogUiBinder uiBinder = GWT.create(ObservatoryDialogUiBinder.class);

	interface ObservatoryDialogUiBinder extends UiBinder<Widget, ObservatoryDialog> {
	}
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	TextBox txtName;
	
	private Observatory observatory;

	public ObservatoryDialog(Observatory observatory) {
		super(Messages.lbl.observatoryAdd(), false, true);
		
		setWidget(uiBinder.createAndBindUi(this));
		
		this.observatory = observatory;
		
		if(observatory != null) {
			txtName.setText(observatory.getName());
		}
		
		createButtonBar(Messages.lbl.Ok(), okHandler, Messages.lbl.Cancel(), cancelHandler);
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if(observatory == null) {
				observatory = new Observatory();
			}
			observatory.setName(txtName.getText());
			
			MetricService.Connection.getInstance().addObservatory(observatory, new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					InformationsDialog dial = new InformationsDialog(Messages.lbl.Error(), Messages.lbl.Ok(), Messages.lbl.problemSaveObs(), caught.getMessage(), caught);
					dial.center();
				}
				@Override
				public void onSuccess(Void result) {
					InformationsDialog dial = new InformationsDialog(Messages.lbl.Success(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.successSaveObs(), false);
					dial.center();
					ObservatoryDialog.this.hide();
				}
			});
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			ObservatoryDialog.this.hide();
		}
	};

}
