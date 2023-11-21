package bpm.gwt.aklabox.commons.client.viewers;

import bpm.gwt.aklabox.commons.client.utils.PathHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PageResult extends Composite {

	private static PageResultUiBinder uiBinder = GWT.create(PageResultUiBinder.class);

	interface PageResultUiBinder extends UiBinder<Widget, PageResult> {
	}

	@UiField
	Image imgPage;
	@UiField
	public Image blueStar;
	@UiField
	Label lblPageNumber;

	private DocumentViewer parent;
	private String pageImagePath;
	private String status;
	private int pageNumber;

	public PageResult(DocumentViewer parent, String pageImagePath, int pageNumber, String status) {
		initWidget(uiBinder.createAndBindUi(this));

		imgPage.setUrl(PathHelper.getRightPath(pageImagePath));

		lblPageNumber.setText(String.valueOf(pageNumber));
		this.parent = parent;
		this.pageImagePath = pageImagePath;
		this.setPageNumber(pageNumber);
		//this.groupDocument = groupDocument;
		//this.xaklDocPreview = xaklDoc;
		this.status = status;
		blueStar.setVisible(false);
	}

	@UiHandler("imgPage")
	void onChoosePage(ClickEvent e) {
		if (parent != null) {
			parent.changePage(status, pageImagePath, pageNumber);
//			if (parent instanceof OpenDocument) {
//				if (status.equals("office")) {
//					if (parent != null) {
//						((OpenDocument) parent).previewPage(DocumentManagement.getRightPath(pageImagePath));
//					}
//					else {
//						if (groupDocument != null) {
//							groupDocument.previewPage(DocumentManagement.getRightPath(pageImagePath));
//						}
//						else {
//							xaklDocPreview.previewPage(DocumentManagement.getRightPath(pageImagePath));
//						}
//					}
//				}
//				((OpenDocument) parent).setDocPageCounter(pageNumber);
//				((OpenDocument) parent).updateRightUIData();
//			}
//			else if (parent instanceof DocumentViewer) {
//	
//				if (status.equals("office")) {
//					if (parent != null) {
//						((DocumentViewer) parent).previewPage(DocumentManagement.getRightPath(pageImagePath));
//					}
//					else {
//						if (groupDocument != null) {
//							groupDocument.previewPage(DocumentManagement.getRightPath(pageImagePath));
//						}
//						else {
//							xaklDocPreview.previewPage(DocumentManagement.getRightPath(pageImagePath));
//						}
//					}
//				}
//				((DocumentViewer) parent).setDocPageCounter(pageNumber);
//			}
		}
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
}