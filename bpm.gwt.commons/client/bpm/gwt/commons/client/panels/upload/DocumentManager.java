package bpm.gwt.commons.client.panels.upload;

import org.moxieapps.gwt.uploader.client.events.FileQueueErrorEvent.ErrorCode;

import com.google.gwt.user.client.ui.Composite;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.loading.ProgressBar;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.client.services.exception.ServiceException.CodeException;
import bpm.gwt.commons.client.utils.Tools;

/**
 * File upload manager
 */
public abstract class DocumentManager extends Composite implements IDocumentManager {

	protected FileUploadManager uploader;
	private ProgressBar progress;

	private String fileId;
	private String fileName;

	private DocumentUploadHandler uploadHandler;
	
	private boolean isFileUploaded;
	
	public DocumentManager(FileUploadManager uploader) {
		this.uploader = uploader;
		this.uploader.setManager(this);
	}
	
	public void setProgress(ProgressBar progress) {
		this.progress = progress;
	}

	@Override
	public void setProgress(double progress) {
		if (this.progress != null) {
			if (progress <= 100) {
				this.progress.setProgress(progress);
			}
		}
	}

	@Override
	public void manageError(ErrorCode errorCode, String message) {
		updateUi(false, true);
		StringBuffer buf = new StringBuffer();
		buf.append(Tools.getLabel(errorCode));
		if (message != null && !message.isEmpty()) {
			buf.append(" (" + message + ")");
		}
		
		ExceptionManager.getInstance().handleException(new ServiceException(CodeException.CODE_UPLOAD_DOCUMENT), buf.toString());
	}

	@Override
	public void manageError(org.moxieapps.gwt.uploader.client.events.UploadErrorEvent.ErrorCode errorCode, String message) {
		updateUi(false, true);
		StringBuffer buf = new StringBuffer();
		buf.append(Tools.getLabel(errorCode));
		if (message != null && !message.isEmpty()) {
			buf.append(" (" + message + ")");
		}
		
		ExceptionManager.getInstance().handleException(new ServiceException(CodeException.CODE_UPLOAD_DOCUMENT), buf.toString());
	}
	
	@Override
	public void manageError(CodeException codeException) {
		updateUi(false, true);
		ExceptionManager.getInstance().handleException(new ServiceException(codeException), "");
	}

	@Override
	public void manageError(String response) {
		updateUi(false, true);
		ExceptionManager.getInstance().handleException(new ServiceException(CodeException.CODE_UNKNOWN), response);
	}

	@Override
	public void setFileInfos(String fileId, String fileName) {
		this.fileId = fileId;
		this.fileName = fileName;
		setFileInfos(fileName);
	}

	@Override
	public void onUploadStart() {
		updateUi(true, false);
		if (progress != null) {
			progress.setVisible(true);
		}
	}

	//If support cancel
	public void onCancel() {
		uploader.cancelUpload(fileId);
		updateUi(false, false);
		if (progress != null) {
			progress.setVisible(false);
		}
	}

	@Override
	public void onUploadSuccess(Integer documentId) {
		this.isFileUploaded = true;
		
		updateUi(false, false);
		if (progress != null) {
			progress.setVisible(false);
		}
		onUploadSuccess(documentId, false);
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setUploadHandler(DocumentUploadHandler uploadHandler) {
		this.uploadHandler = uploadHandler;
	}

	public void onUploadSuccess(Integer documentId, boolean refresh) {
		if (uploadHandler != null) {
			uploadHandler.onUploadSuccess(documentId, getFileName());
		}
	}
	
	public boolean isFileUploaded() {
		return isFileUploaded;
	}
	
	protected abstract void setFileInfos(String fileName);
	
	protected abstract void updateUi(boolean upload, boolean hasError);
	
	public interface DocumentUploadHandler {
		
		public void onUploadSuccess(Integer documentId, String filename);
	}
}
