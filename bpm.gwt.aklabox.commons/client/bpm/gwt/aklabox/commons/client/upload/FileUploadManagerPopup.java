package bpm.gwt.aklabox.commons.client.upload;

import java.util.ArrayList;
import java.util.List;

import bpm.document.management.core.model.Documents;
import bpm.gwt.aklabox.commons.client.upload.FileUploadComposite.IUploadDocumentManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FileUploadManagerPopup extends Composite {

	private static FileUploadManagerPopupUiBinder uiBinder = GWT.create(FileUploadManagerPopupUiBinder.class);

	interface FileUploadManagerPopupUiBinder extends UiBinder<Widget, FileUploadManagerPopup> {
	}
	
	interface MyStyle extends CssResource {
		String popup();
		String mainPanelAbsolute();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	Label lblTitle;
	
	@UiField
	Image btnOptions;
	
	@UiField
	HTMLPanel mainPanel, panelTopBar, contentPanel;
	
	private FileUploadManager uploader;
	private IUploadDocumentManager manager;
	
	private List<FileUploadItem> items = new ArrayList<>();

	public FileUploadManagerPopup(FileUploadManager uploader, IUploadDocumentManager manager, boolean isPopup) {
		initWidget(uiBinder.createAndBindUi(this));
		this.uploader = uploader;
		this.manager = manager;

		addStyleName(style.popup());
		if (isPopup) {
			addStyleName(style.mainPanelAbsolute());
		}
		
		panelTopBar.setVisible(isPopup);
	}

	public FileUploadItem addUpload(String fileId, String fileName) {
		checkUploadAndUpdateUi();
		
		FileUploadItem item = new FileUploadItem(uploader, manager, this, fileId, fileName);
		contentPanel.add(item);
		items.add(item);
		return item;
	}
	
	public void checkUploadAndUpdateUi() {
		if (uploader.isComplete()) {
			btnOptions.setVisible(true);
		}
		else {
			btnOptions.setVisible(false);
		}
	}

	@UiHandler("btnOptions")
	public void onOptions(ClickEvent event) {
		manager.onOptions();
	}

	@UiHandler("btnClose")
	public void onClose(ClickEvent event) {
		if (uploader.isComplete()) {
			manager.validateItems();
			
			uploader.refreshUploader();
			removeFromParent();
		}
	}
	
	public List<FileUploadItem> getItems() {
		return items;
	}

	public String removeFileUpload(Documents document) {
		if (items != null) {
			FileUploadItem itemToDelete = null;
			for (FileUploadItem item : items) {
				if (item.getUploadedDocument().getId() == document.getId()) {
					itemToDelete = item;
					break;
				}
			}
			
			if (itemToDelete != null) {
				items.remove(itemToDelete);
				contentPanel.remove(itemToDelete);
				
				if (items.size() <= 0) {
					removeFromParent();
				}
				
				return itemToDelete.getFileId();
			}
		}
		
		return null;
	}
}
