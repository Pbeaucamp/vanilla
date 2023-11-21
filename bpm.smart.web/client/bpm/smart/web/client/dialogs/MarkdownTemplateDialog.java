package bpm.smart.web.client.dialogs;

import java.util.Date;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.smart.core.model.RScript;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.panels.ScriptPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MarkdownTemplateDialog extends AbstractDialogBox {
	private static MarkdownTemplateUiBinder uiBinder = GWT
			.create(MarkdownTemplateUiBinder.class);

	interface MarkdownTemplateUiBinder extends
			UiBinder<Widget, MarkdownTemplateDialog> {
	}

	@UiField
	Label lblFileName, lblTitle, lblAuthor, lblDescription, lblScript;
	
	@UiField
	TextArea txtScript, txtDescription;
	
	@UiField
	TextBox txtAuthor, txtTitle;
	
	@UiField
	SimplePanel panelUpload;
	
	@UiField
	Button btnImport;
	@UiField
	FormPanel formCode;

	private ScriptPanel parent;
	private FileUpload fileUpload = new FileUpload();

	public static LabelsConstants lblCnst = (LabelsConstants) GWT
			.create(LabelsConstants.class);

	public MarkdownTemplateDialog(ScriptPanel parent, String scripttext) {
		super(lblCnst.MarkdownTemplate(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		txtScript.setText(scripttext);
		
		createButtonBar(bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.Validate(), okHandler, lblCnst.Cancel(), cancelHandler);
		
		setLabels();
		
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
		
		formCode.setAction(GWT.getHostPageBaseURL() + "bpm_smart_web/UploadImage");
		formCode.setEncoding(FormPanel.ENCODING_MULTIPART);
		formCode.setMethod(FormPanel.METHOD_POST);
		formCode.addSubmitCompleteHandler(submitCompleteHandler);
		formCode.setWidget(panelUpload);
		
	}
	
	private void setLabels() {
		lblAuthor.setText(lblCnst.Author());
		lblFileName.setText("");
		lblTitle.setText(bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.Title());
		lblDescription.setText(bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.Description());
		lblScript.setText(lblCnst.RCode());
	}

	@UiHandler("btnImport")
	public void onImportClick(ClickEvent event) {
		fileUpload.getElement().<InputElement>cast().click();
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if(!txtTitle.getText().equals("")){
				String result = generateMarkdownCode();
				if(parent.getCurrentScript().getScriptType().equals(RScript.ScriptType.MARKDOWN.name())){
					parent.setText(result.replace("\n", "<br>"));
				} else {
					parent.onNewSave(result, RScript.ScriptType.MARKDOWN.name());
				}
				
				//parent.addCode(result);
				MarkdownTemplateDialog.this.hide();
			} else {
				MessageHelper.openMessageDialog(lblCnst.Information(), lblCnst.MissingInformation());
			}
			
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			MarkdownTemplateDialog.this.hide();
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
				lblFileName.setText(res);
			}
			
		}
	};
	
	private String generateMarkdownCode() {
		StringBuffer markdown = new StringBuffer();
		DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
		
		markdown.append("---\n");
		markdown.append((!txtTitle.getText().equals(""))?"title: '" + txtTitle.getText() + "'\n" : "");
		markdown.append((!txtAuthor.getText().equals(""))?"author: '" + txtAuthor.getText() + "'\n" : "");
		markdown.append("date: '" + format.format(new Date()) + "'\n");
		markdown.append("---\n");
		markdown.append("\n");
		markdown.append((!lblFileName.getText().equals(""))?"!["+ lblCnst.Image() +"]("+ lblFileName.getText() +")\n" : "");
		markdown.append("\n");
		markdown.append(txtDescription.getText() + "\n");
		markdown.append("```{r eval = TRUE, echo = TRUE, warning = TRUE, message=FALSE}\n");
		markdown.append(txtScript.getText()+ "\n");
		markdown.append("```");
		
		return markdown.toString();
	}
}
