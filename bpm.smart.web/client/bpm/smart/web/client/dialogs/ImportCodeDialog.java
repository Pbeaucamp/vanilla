package bpm.smart.web.client.dialogs;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.panels.ScriptPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class ImportCodeDialog extends AbstractDialogBox {
	private static ImportCodeUiBinder uiBinder = GWT
			.create(ImportCodeUiBinder.class);

	interface ImportCodeUiBinder extends
			UiBinder<Widget, ImportCodeDialog> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	Label lblFileName;
	
	@UiField
	TextArea txtScript;
	
	@UiField
	SimplePanel panelUpload;
	
	@UiField
	Button btnSelectAll, btnImport;
	@UiField
	FormPanel formCode;

	@UiField
	MyStyle style;

	private ScriptPanel parent;
	private FileUpload fileUpload = new FileUpload();
	private String selectedCode;

	public static LabelsConstants lblCnst = (LabelsConstants) GWT
			.create(LabelsConstants.class);

	public ImportCodeDialog(ScriptPanel parent) {
		super(lblCnst.ScriptImport(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.selectedCode = null;
		
		createButtonBar(lblCnst.AddSelectedCode(), okHandler, lblCnst.Cancel(), cancelHandler);
		
		fileUpload.setName("file");
		fileUpload.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				fileUpload.getFilename();
				formCode.submit();
			}
		});
		panelUpload.setVisible(false);
		panelUpload.add(fileUpload);
		
		formCode.setAction(GWT.getHostPageBaseURL() + "bpm_smart_web/ImportCodeServlet");
		formCode.setEncoding(FormPanel.ENCODING_MULTIPART);
		formCode.setMethod(FormPanel.METHOD_POST);
		formCode.addSubmitCompleteHandler(submitCompleteHandler);
		formCode.setWidget(panelUpload);
		
		txtScript.setReadOnly(true);
	}
	
	@UiHandler("btnSelectAll")
	public void onSelectAllClick(ClickEvent event) {
		txtScript.setFocus(true);
		txtScript.selectAll();
	}
	
	@UiHandler("btnImport")
	public void onImportClick(ClickEvent event) {
		fileUpload.getElement().<InputElement>cast().click();
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			selectedCode = txtScript.getSelectedText();
			parent.addCode(selectedCode);
			ImportCodeDialog.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			ImportCodeDialog.this.hide();
		}
	};
	
	private SubmitCompleteHandler submitCompleteHandler = new SubmitCompleteHandler() {
		
		public void onSubmitComplete(SubmitCompleteEvent event) {
			String res = event.getResults();
//			res = res.subSequence(5, res.length()-6).toString();
			
			res = new HTML(res).getText();
			
			if(res.equals("")){
				lblFileName.setText("");
				
			} else {
				txtScript.setText(res);
				lblFileName.setText(fileUpload.getFilename());
			}
			
		}
	};
}
