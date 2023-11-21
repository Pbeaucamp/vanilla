package bpm.gwt.commons.client.panels.upload;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.v2.Button;
import bpm.gwt.commons.client.loading.ProgressBar;

/**
 * Widget d'upload de Document
 * 
 * Bouton de sélection de fichier et Progress bar
 */
public class FileUploadWidget extends DocumentManager {

	private static CheckInUiBinder uiBinder = GWT.create(CheckInUiBinder.class);

	interface CheckInUiBinder extends UiBinder<Widget, FileUploadWidget> {
	}

	@UiField
	HTMLPanel panelOptions;

	@UiField
	LabelTextBox txtFile;

	@UiField
	Button btnChooseFile;

	@UiField
	SimplePanel progressPanel;
	
	@UiField
	Label lblError;

	public FileUploadWidget(String uploadUrl, JSONObject parameters) {
		super(new FileUploadManager(null, uploadUrl, parameters));
		initWidget(uiBinder.createAndBindUi(this));
		
		ProgressBar progress = new ProgressBar(18);
		setProgress(progress);
		progressPanel.setWidget(progress);
	}

	public void setLabel(String label) {
		this.txtFile.setLabel(label);
	}

//	public void setLabelWidth(int width) {
//		this.txtFile.setLabelWidth(width);
//	}
	
	public void setError(String message) {
		lblError.setText(message);
		lblError.setVisible(message != null && !message.isEmpty());
	}

	protected void updateUi(boolean upload, boolean hasError) {
		btnChooseFile.setVisible(!upload);
		progressPanel.setVisible(upload);
		
		if (hasError) {
			setFileInfos("");
		}
	}

	@UiHandler("btnChooseFile")
	public void onChooseFile(ClickEvent event) {
		uploader.chooseFile();
	}

	@Override
	protected void setFileInfos(String fileName) {
		txtFile.setText(fileName);
	}

//	@Override
//	public void onUploadSuccess(IDocument document, boolean refresh) {
//		txtFile.setValue(document.getTitre());
//	}
}
