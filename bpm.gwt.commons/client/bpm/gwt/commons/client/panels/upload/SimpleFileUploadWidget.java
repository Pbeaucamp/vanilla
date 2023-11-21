package bpm.gwt.commons.client.panels.upload;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.loading.IWait;

/**
 * Widget d'upload de fichier simple (sans panel)
 * 
 * Seule la fenêtre de sélection de fichier est affichée
 */
public class SimpleFileUploadWidget extends DocumentManager {

	private static SimpleFileUploadWidgetUiBinder uiBinder = GWT.create(SimpleFileUploadWidgetUiBinder.class);

	interface SimpleFileUploadWidgetUiBinder extends UiBinder<Widget, SimpleFileUploadWidget> {
	}

	public SimpleFileUploadWidget(IWait waitPanel, String uploadUrl, JSONObject parameters) {
		super(new FileUploadManager(waitPanel, uploadUrl, parameters));
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void chooseFile() {
		uploader.chooseFile();
	}

	@Override
	protected void setFileInfos(String fileName) { }

	@Override
	protected void updateUi(boolean upload, boolean hasError) { }
}
