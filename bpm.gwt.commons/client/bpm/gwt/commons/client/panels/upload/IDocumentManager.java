package bpm.gwt.commons.client.panels.upload;

import org.moxieapps.gwt.uploader.client.events.FileQueueErrorEvent.ErrorCode;

import bpm.gwt.commons.client.services.exception.ServiceException.CodeException;

/**
 * Manager interface for uploading a file
 */
public interface IDocumentManager {

	void setProgress(double progress);

	void manageError(ErrorCode errorCode, String message);

	void manageError(org.moxieapps.gwt.uploader.client.events.UploadErrorEvent.ErrorCode errorCode, String message);
	
	void manageError(CodeException codeException);

	void manageError(String response);

	void setFileInfos(String fileId, String fileName);

	void onUploadStart();
	
	void onUploadSuccess(Integer documentId);

}
