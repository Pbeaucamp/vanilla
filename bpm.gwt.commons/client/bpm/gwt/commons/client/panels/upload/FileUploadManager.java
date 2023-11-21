package bpm.gwt.commons.client.panels.upload;

import org.moxieapps.gwt.uploader.client.Uploader;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteEvent;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteHandler;
import org.moxieapps.gwt.uploader.client.events.FileDialogStartEvent;
import org.moxieapps.gwt.uploader.client.events.FileDialogStartHandler;
import org.moxieapps.gwt.uploader.client.events.FileQueueErrorEvent;
import org.moxieapps.gwt.uploader.client.events.FileQueueErrorHandler;
import org.moxieapps.gwt.uploader.client.events.FileQueuedEvent;
import org.moxieapps.gwt.uploader.client.events.FileQueuedHandler;
import org.moxieapps.gwt.uploader.client.events.UploadCompleteEvent;
import org.moxieapps.gwt.uploader.client.events.UploadCompleteHandler;
import org.moxieapps.gwt.uploader.client.events.UploadErrorEvent;
import org.moxieapps.gwt.uploader.client.events.UploadErrorHandler;
import org.moxieapps.gwt.uploader.client.events.UploadProgressEvent;
import org.moxieapps.gwt.uploader.client.events.UploadProgressHandler;
import org.moxieapps.gwt.uploader.client.events.UploadSuccessEvent;
import org.moxieapps.gwt.uploader.client.events.UploadSuccessHandler;

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.FileUpload;

import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.exception.ServiceException.CodeException;

/**
 * Manager d'upload de fichier
 */
public class FileUploadManager implements FileQueuedHandler, UploadProgressHandler, UploadCompleteHandler, FileDialogStartHandler, FileDialogCompleteHandler, FileQueueErrorHandler, UploadErrorHandler, UploadSuccessHandler {

	private IWait waitPanel;
	private IDocumentManager manager;
	
	private Uploader uploader;
	private FakeUploadComposite fake;
	
	private JSONObject parameters;

	public FileUploadManager(IWait waitPanel, String uploadURL, JSONObject parameters) {
		this.waitPanel = waitPanel;
		this.parameters = parameters;
		
		buildContent(uploadURL);
	}
	
	private void buildContent(String uploadURL) {
		this.uploader = new Uploader();
		uploader.setUploadURL(uploadURL);
		uploader.setFileQueuedHandler(this);
		uploader.setUploadProgressHandler(this);
		uploader.setUploadCompleteHandler(this);
		uploader.setFileDialogStartHandler(this);
		uploader.setFileDialogCompleteHandler(this);
		uploader.setFileQueueErrorHandler(this);
		uploader.setUploadErrorHandler(this);
		uploader.setUploadSuccessHandler(this);
	}

	public void setManager(IDocumentManager manager) {
		this.manager = manager;
	}

	public void chooseFile() {
		setParameters(parameters);

		// We need to create a fake composite to attach the FileInput to the DOM
		// We don't display it and we remove it after we are done
		this.fake = new FakeUploadComposite(uploader);
		fake.center();

		FileUpload lastUpload = null;
		for (int i = 0; i < uploader.getWidgetCount(); i++) {
			if (uploader.getWidget(i) instanceof FileUpload) {
				lastUpload = (FileUpload) uploader.getWidget(i);
			}
		}
		
		if (lastUpload != null) {
			openFileDialog(lastUpload, false);
		}
	}

	private void setParameters(JSONObject parameters) {
//		JSONObject params = new JSONObject();
//		params.put(DocumentUploaderServlet.PARAM_ACTION, new JSONString(String.valueOf(process.getType())));
//		if (elementId != null) {
//			params.put(DocumentUploaderServlet.PARAM_ELEMENT_ID, new JSONString(String.valueOf(elementId)));
//		}
		uploader.setPostParams(parameters);
	}

	private void openFileDialog(FileUpload fileUpload, boolean multipleFiles) {
		if (multipleFiles) {
			fileUpload.getElement().setAttribute("multiple", "true");
		}
		else {
			fileUpload.getElement().removeAttribute("multiple");
		}

		onFileDialogStartEvent(new FileDialogStartEvent());

		InputElement.as(fileUpload.getElement()).click();
	}

	public int getUploadsInProgress() {
		return uploader.getStats().getUploadsInProgress();
	}

	public void cancelUpload(String fileId) {
		uploader.cancelUpload(fileId, false);
	}

	@Override
	public boolean onUploadProgress(UploadProgressEvent uploadProgressEvent) {
		if (uploadProgressEvent.getBytesComplete() > 0) {
			manager.setProgress(((double) uploadProgressEvent.getBytesComplete() / uploadProgressEvent.getBytesTotal()) * 100);
		}
		else {
			manager.setProgress(0);
		}
		return true;
	}

	@Override
	public boolean onFileDialogStartEvent(FileDialogStartEvent fileDialogStartEvent) {
		return true;
	}

	@Override
	public boolean onFileDialogComplete(FileDialogCompleteEvent fileDialogCompleteEvent) {
		if (fileDialogCompleteEvent.getTotalFilesInQueue() > 0) {
			manager.onUploadStart();
			if (uploader.getStats().getUploadsInProgress() <= 0) {
				if (waitPanel != null) {
					waitPanel.showWaitPart(true);
				}
				uploader.startUpload();
			}
		}
		return true;
	}

	@Override
	public boolean onFileQueued(FileQueuedEvent fileQueuedEvent) {
		String fileId = fileQueuedEvent.getFile().getId();
		String fileName = fileQueuedEvent.getFile().getName();
		
		manager.setFileInfos(fileId, fileName);
		
		return true;
	}

	@Override
	public boolean onFileQueueError(FileQueueErrorEvent fileQueueErrorEvent) {
		manager.manageError(fileQueueErrorEvent.getErrorCode(), fileQueueErrorEvent.getMessage());
		return true;
	}

	@Override
	public boolean onUploadError(UploadErrorEvent uploadErrorEvent) {
		manager.manageError(uploadErrorEvent.getErrorCode(), uploadErrorEvent.getMessage());
		return true;
	}

	@Override
	public boolean onUploadComplete(UploadCompleteEvent uploadCompleteEvent) {
		fake.removeFromParent();
		if (waitPanel != null) {
			waitPanel.showWaitPart(false);
		}
		return true;
	}

	@Override
	public boolean onUploadSuccess(UploadSuccessEvent uploadSuccessEvent) {
		String response = uploadSuccessEvent.getServerData();
		if (response.contains("<error>")) {
			try {
				response = response.replace(response.substring(0, response.indexOf("<error>") + 7), "").replace("</error></response>", "");
				response = response.trim();
				int code = Integer.parseInt(response);
				CodeException codeException = CodeException.valueOf(code);
				manager.manageError(codeException);
			} catch(Exception e) {
				manager.manageError(response);
			}
		}
		else {
			response = response.substring(response.indexOf("![CDATA[") + 8, response.indexOf("]]"));
			manager.onUploadSuccess(Integer.parseInt(response));
		}
		return true;
	}
}
