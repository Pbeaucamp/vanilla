package bpm.smart.web.client.dialogs;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.panels.ScriptPanel;
import bpm.smart.web.client.services.SmartAirService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class VersionDialog extends AbstractDialogBox {
	private static VersionUiBinder uiBinder = GWT
			.create(VersionUiBinder.class);

	interface VersionUiBinder extends
			UiBinder<Widget, VersionDialog> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	Label lblName, lblDate, lblNum;
	
	@UiField
	TextArea txtScript;

	@UiField
	MyStyle style;

	private ScriptPanel parent;
	private RScript script;
	private RScriptModel model;

	public static LabelsConstants lblCnst = (LabelsConstants) GWT
			.create(LabelsConstants.class);

	public VersionDialog(ScriptPanel parent, RScript script, RScriptModel model) {
		super(lblCnst.VersionViewer(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.script = script;
		this.model = model;
		loadScript();
		txtScript.setReadOnly(true);
		createButtonBar(lblCnst.Downgrade(), okHandler, lblCnst.Cancel(), cancelHandler);
		createButton(lblCnst.Delete(), deleteHandler);
	}

	
	private void loadScript() {
		
		lblName.setText(script.getName());
		DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
		lblDate.setText(lblCnst.Date() + " :   " + format.format(model.getDateVersion()));
		lblNum.setText(lblCnst.Version() + " :   " + model.getNumVersion());
		txtScript.setText(model.getScript());
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			
			final InformationsDialog dial = new InformationsDialog(lblCnst.Error(), lblCnst.Ok(), lblCnst.Cancel(), lblCnst.ConfirmDowngrade(), false);
			dial.center();
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if(dial.isConfirm()){
						parent.refresh(parent.getCurrentProject(), script, model, parent.getCurrentVersions());
						VersionDialog.this.hide();
					}
					
				}
			});
			
		
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			VersionDialog.this.hide();
		}
	};
	
	private ClickHandler deleteHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			SmartAirService.Connect.getInstance().deleteRScriptModel(model, new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					//showWaitPart(false);
					InformationsDialog dial = new InformationsDialog(lblCnst.Error(), lblCnst.Ok(), lblCnst.Cancel(), lblCnst.DeleteScriptSuccessfull(), false);
					dial.center();
				}

				@Override
				public void onFailure(Throwable caught) {
					//showWaitPart(false);
					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, lblCnst.UnableToExecuteScript());
				}
			});
			VersionDialog.this.hide();
		}
	};
}
