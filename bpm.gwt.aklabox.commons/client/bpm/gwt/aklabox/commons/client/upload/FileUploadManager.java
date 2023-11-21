package bpm.gwt.aklabox.commons.client.upload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import bpm.document.management.core.IDocumentManager;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.TypeProcess;
import bpm.document.management.core.utils.AklaboxConstant;
import bpm.gwt.aklabox.commons.client.upload.FileUploadComposite.IUploadDocumentManager;

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FileUpload;

public class FileUploadManager implements FileQueuedHandler, UploadProgressHandler, UploadCompleteHandler, FileDialogStartHandler, FileDialogCompleteHandler, FileQueueErrorHandler, UploadErrorHandler, UploadSuccessHandler {
	
	private IUploadDocumentManager manager;
	private ComplexPanel parent;
	private Uploader uploader;
	
	private TypeProcess process;
	
	private FileUploadManagerPopup popup;
	private boolean isPopup;

	private HashMap<String, FileUploadItem> items = new HashMap<>();
	private FakeUploadComposite fake;

	/**
	 * This class manage all the upload of documents
	 * 
	 * @param manager The implementation of the manager (to be use in the webapps)
	 * @param parent The panel to add the progress upload panel
	 * @param process
	 * @param isPopup If the panel which display the progress is a poup (false if included in a composite)
	 */
	public FileUploadManager(IUploadDocumentManager manager, ComplexPanel parent, TypeProcess process, boolean isPopup) {
		this.manager = manager;
		this.parent = parent;
		this.process = process;
		this.isPopup = isPopup;
		
		buildUploader();
	}

	public void refreshUploader() {
		buildUploader();
		
		this.items = new HashMap<>();
		if (fake != null) {
			fake.removeFromParent();
		}
	}
	
	private void buildUploader() {
		this.uploader = new Uploader();
		uploader.setUploadURL("*.gupld?" + AklaboxConstant.UPLOADER_VERSION + "=" + AklaboxConstant.UPLOADER_V2);
		uploader.setFileQueuedHandler(this);
		uploader.setUploadProgressHandler(this);
		uploader.setUploadCompleteHandler(this);
		uploader.setFileDialogStartHandler(this);
		uploader.setFileDialogCompleteHandler(this);
		uploader.setFileQueueErrorHandler(this);
		uploader.setUploadErrorHandler(this);
		uploader.setUploadSuccessHandler(this);
	}

	public void dropFile(DropEvent event, int parentId) {
		setDocumentParam(process, parentId);

		uploader.addFilesToQueue(Uploader.getDroppedFiles(event.getNativeEvent()));
		event.preventDefault();event.stopPropagation();
	}

	public void chooseFile(int itemId, boolean isMajor, boolean multipleFiles) {
		switch (process) {
		case UPLOAD_DOCUMENTS:
		case UPLOAD_ONE_DOCUMENT:
			setDocumentParam(process, itemId);
			break;
		case CHECKIN:
			setCheckinParam(process, itemId, isMajor);
			break;

		default:
			break;
		}

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
			openFileDialog(lastUpload, multipleFiles);
		}
	}

	private void setDocumentParam(TypeProcess process, int parentId) {
		JSONObject params = new JSONObject();
		params.put(IDocumentManager.PARAM_ACTION, new JSONString(String.valueOf(process.getType())));
		params.put(IDocumentManager.PARAM_PARENT_ID, new JSONString(String.valueOf(parentId)));
		uploader.setPostParams(params);
	}

	private void setCheckinParam(TypeProcess process, int itemId, boolean isMajor) {
		JSONObject params = new JSONObject();
		params.put(IDocumentManager.PARAM_ACTION, new JSONString(String.valueOf(process.getType())));
		params.put(IDocumentManager.PARAM_ITEM_ID, new JSONString(String.valueOf(itemId)));
		params.put(IDocumentManager.PARAM_VERSION_MAJOR, new JSONString(String.valueOf(isMajor)));
		uploader.setPostParams(params);
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

	@Override
	public boolean onFileQueued(FileQueuedEvent fileQueuedEvent) {
		String fileId = fileQueuedEvent.getFile().getId();
		String fileName = fileQueuedEvent.getFile().getName();

		addUpload(fileId, fileName);
		return true;
	}

	private void addUpload(String fileId, String fileName) {
		if (popup == null || !popup.isAttached()) {
			this.popup = new FileUploadManagerPopup(this, manager, isPopup);
		}
		if (parent != null) {
			parent.add(popup);
		}

		FileUploadItem item = popup.addUpload(fileId, fileName);
		items.put(fileId, item);
	}

	public int getUploadsInProgress() {
		return uploader.getStats().getUploadsInProgress();
	}

	public void cancelUpload(String fileId) {
		uploader.cancelUpload(fileId, false);
	}

	@Override
	public boolean onUploadProgress(UploadProgressEvent uploadProgressEvent) {
		FileUploadItem item = items.get(uploadProgressEvent.getFile().getId());
		if (uploadProgressEvent.getBytesComplete() > 0) {
			item.setProgress(((double) uploadProgressEvent.getBytesComplete() / uploadProgressEvent.getBytesTotal()) * 100);
		}
		else {
			item.setProgress(0);
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
			manager.onStartUpload();
			if (uploader.getStats().getUploadsInProgress() <= 0) {
				uploader.startUpload();
			}
		}
		return true;
	}

	@Override
	public boolean onFileQueueError(FileQueueErrorEvent fileQueueErrorEvent) {
		FileUploadItem item = items.get(fileQueueErrorEvent.getFile().getId());
		item.manageError(fileQueueErrorEvent.getErrorCode(), fileQueueErrorEvent.getMessage());
		return true;
	}

	@Override
	public boolean onUploadError(UploadErrorEvent uploadErrorEvent) {
		FileUploadItem item = items.get(uploadErrorEvent.getFile().getId());
		item.manageError(uploadErrorEvent.getErrorCode(), uploadErrorEvent.getMessage());
		return true;
	}

	@Override
	public boolean onUploadComplete(UploadCompleteEvent uploadCompleteEvent) {
		FileUploadItem item = items.get(uploadCompleteEvent.getFile().getId());
		item.setUploadComplete();

		uploader.startUpload();
		return true;
	}

	@Override
	public boolean onUploadSuccess(UploadSuccessEvent uploadSuccessEvent) {
		FileUploadItem item = items.get(uploadSuccessEvent.getFile().getId());
		String response = uploadSuccessEvent.getServerData();
		if (response.contains("<error>")) {
			response = response.replace(response.substring(0, response.indexOf("<error>") + 7), "").replace("</error></response>", "");
			item.manageError(response);
		}
		else {
			response = response.substring(response.indexOf("![CDATA[") + 8, response.indexOf("]]"));
			item.manageInfos(response, process);
		}
		return true;
	}

	private List<FileUploadItem> getUploadedItems() {
		return new ArrayList<FileUploadItem>(items.values());
	}

	public boolean isComplete() {
		List<FileUploadItem> items = getUploadedItems();
		for (FileUploadItem item : items) {
			if (!item.isComplete()) {
				return false;
			}
		}
		return true;
	}

	public List<Documents> getDocuments() {
		List<Documents> documents = new ArrayList<>();

		List<FileUploadItem> items = getUploadedItems();
		if (items != null) {
			for (FileUploadItem item : items) {
				if (item.getUploadedDocument() != null) {
					documents.add(item.getUploadedDocument());
				}
			}
		}
		return documents;
	}

	public void removeFileUpload(Documents document) {
		String fileId = popup.removeFileUpload(document);
		items.remove(fileId);
	}
}
