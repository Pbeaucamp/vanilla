package bpm.smart.web.client.dialogs;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.panels.resources.NavigationPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ImportProjectDialog extends AbstractDialogBox {
	private static ImportProjectUiBinder uiBinder = GWT.create(ImportProjectUiBinder.class);

	interface ImportProjectUiBinder extends UiBinder<Widget, ImportProjectDialog> {
	}

	interface MyStyle extends CssResource {
		String fileInput();
	}

	@UiField
	HTMLPanel panelNewName, panelForm;

	@UiField
	TextBox txtNewName;

	@UiField
	Button btnImport;
	@UiField
	FormPanel formCode;
	
	@UiField
	Label lblFileName;
	
	@UiField
	SimplePanel panelUpload;

	@UiField
	MyStyle style;

	private FileUpload fileUpload = new FileUpload();
	private boolean isSuccessed = false;

	public static LabelsConstants lblCnst = (LabelsConstants) GWT.create(LabelsConstants.class);

	public ImportProjectDialog(NavigationPanel parent) {
		super(lblCnst.ImportProject(), false, true);
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(lblCnst.Ok(), okHandler, lblCnst.Cancel(), cancelHandler);
		txtNewName.setName("newName");
		//fileUpload.setStyleName(style.fileInput());
		fileUpload.setName("file");
		fileUpload.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				fileUpload.getFilename();
				formCode.submit();
			}
		});
		panelUpload.add(fileUpload);
//		panelForm.add(panelUpload);
//		panelForm.add(panelNewName);

		formCode.setAction(GWT.getHostPageBaseURL() + "bpm_smart_web/ImportProjectServlet");
		formCode.setEncoding(FormPanel.ENCODING_MULTIPART);
		formCode.setMethod(FormPanel.METHOD_POST);
		formCode.addSubmitCompleteHandler(submitCompleteHandler);
		formCode.setWidget(panelForm);

		lblFileName.setText(lblCnst.NoFileSelected());
		
		this.panelNewName.setVisible(false);
	}

	@UiHandler("btnImport")
	public void onImportClick(ClickEvent event) {
		fileUpload.getElement().<InputElement> cast().click();
	}

	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (isSuccessed) {
				ImportProjectDialog.this.hide();
			}
			else {
				formCode.submit();
			}

		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			isSuccessed = false;
			ImportProjectDialog.this.hide();
		}
	};

	private SubmitCompleteHandler submitCompleteHandler = new SubmitCompleteHandler() {

		public void onSubmitComplete(SubmitCompleteEvent event) {
			String res = event.getResults();
//			res = res.subSequence(5, res.length() - 6).toString();

			if (res.split(";")[0].equals("success")) {
				isSuccessed = true;
				lblFileName.setText(res.split(";")[1]);
				btnImport.getElement().getStyle().setMarginTop(40, Unit.PX);
				panelNewName.setVisible(false);
				MessageHelper.openMessageDialog(lblCnst.Information(), lblCnst.ImportProjectSuccessfull1() + res.split(";")[1] + lblCnst.ImportProjectSuccessfull2());
				okHandler.onClick(null);
			}
			else if (res.split(";")[0].equals("existentName")) {
				isSuccessed = false;
				btnImport.getElement().getStyle().setMarginTop(5, Unit.PX);
				panelNewName.setVisible(true);
				MessageHelper.openMessageDialog(lblCnst.Information(), lblCnst.ImportProjectError1() + res.split(";")[1] + lblCnst.ImportProjectError2());
				txtNewName.setText("");
				txtNewName.setFocus(true);
			}
			else if (res.split(";")[0].equals("errorType")) {
				isSuccessed = false;
				panelNewName.setVisible(false);
				MessageHelper.openMessageDialog(lblCnst.Information(), lblCnst.ErrorFileType());
			}

		}
	};

	public boolean isSuccessed() {
		return isSuccessed;
	}

}
