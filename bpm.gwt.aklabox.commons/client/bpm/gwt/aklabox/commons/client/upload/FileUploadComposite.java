package bpm.gwt.aklabox.commons.client.upload;

import org.moxieapps.gwt.uploader.client.Uploader;

import bpm.document.management.core.model.Enterprise;
import bpm.document.management.core.model.IObject;
import bpm.gwt.aklabox.commons.client.utils.DragAndDropHelper;

import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.user.client.ui.FocusPanel;

public class FileUploadComposite extends FocusPanel implements DragOverHandler, DragLeaveHandler, DropHandler {

	private FileUploadManager uploader;
	private IUploadDocumentManager uploadAllowManager;
	private String styleOver;

	public FileUploadComposite() {
		if (Uploader.isAjaxUploadWithProgressEventsSupported()) {
			this.addDragOverHandler(this);
			this.addDragLeaveHandler(this);
			this.addDropHandler(this);
		}
	}
	
	public void initComposite(FileUploadManager uploader, IUploadDocumentManager uploadAllowManager, String styleOver) {
		this.uploader = uploader;
		this.uploadAllowManager = uploadAllowManager;
		this.styleOver = styleOver;
	}

	@Override
	public void onDragOver(DragOverEvent event) {
		if (uploadAllowManager == null) {
			throw new IllegalStateException("IUploadAllowManager must be set");
		}
		
//		String type = event.getData(DragAndDropHelper.DATA_TYPE);
		String myData = event.getData(DragAndDropHelper.DATA);
		if (myData == null && uploadAllowManager.isUploadAllowed() && styleOver != null) {
			addStyleName(styleOver);
		}
	}

	@Override
	public void onDragLeave(DragLeaveEvent event) {
		if (styleOver != null) {
			removeStyleName(styleOver);
		}
	}

	@Override
	public void onDrop(DropEvent event) {
		if (uploadAllowManager == null) {
			throw new IllegalStateException("IUploadAllowManager must be set");
		}
		
		if (uploader == null) {
			throw new IllegalStateException("FileUploadManager must be set");
		}
		
		if (!uploadAllowManager.isUploadAllowed()) {
			return;
		}

		onDragLeave(null);
		uploader.dropFile(event, uploadAllowManager.getParentId());
	}

	public interface IUploadDocumentManager {
		
		public boolean isUploadAllowed();
		
		public int getParentId();
		
		public Enterprise getParentEnterprise();
		
		public void refreshFolder();
		
		public void onOptions();
		
		public void onStartUpload();
		
		public void onUploadComplete(IObject newItem);
		
		public void validateItems();
	}
}
