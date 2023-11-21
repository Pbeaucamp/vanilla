package bpm.gwt.aklabox.commons.client.viewers;

import java.util.List;

import bpm.document.management.core.model.Comments;
import bpm.document.management.core.model.Comments.CommentStatus;
import bpm.document.management.core.model.DocPages;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Versions;
import bpm.document.management.core.utils.DocumentUtils;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.utils.ChildDialogComposite;
import bpm.gwt.aklabox.commons.client.utils.CommentBubble;
import bpm.gwt.aklabox.commons.client.utils.PathHelper;
import bpm.gwt.aklabox.commons.client.utils.ToolsGWT;
import bpm.gwt.aklabox.commons.client.viewers.DocumentViewer.TypeViewer;
import bpm.gwt.aklabox.commons.client.viewers.PagesViewer.Orientation;
import bpm.gwt.aklabox.commons.shared.OCRSearchResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class PlaceComment extends ChildDialogComposite implements ParentDocViewer{

	private static PlaceCommentUiBinder uiBinder = GWT.create(PlaceCommentUiBinder.class);

	interface PlaceCommentUiBinder extends UiBinder<Widget, PlaceComment> {
	}

	@UiField
	Image imgPreview;
	
	@UiField
	HTMLPanel previewPanel, preview, pagePreview;

	private Documents document;
	private List<DocPages> docPages;
	private int versionNumber;
	private String currentPath;

	private int selectedPage;
	private double xPos, yPos;
	private boolean isPlaced = false;

	public PlaceComment(Documents document) {
		initWidget(uiBinder.createAndBindUi(this));

		this.document = document;
		
		loadComments();
	}
	
	public PlaceComment(Documents document, Comments commentToPlace) {
		initWidget(uiBinder.createAndBindUi(this));

		this.document = document;

		//imgPreview.setUrl("http://keenthemes.com/preview/metronic/theme/assets/global/plugins/jcrop/demos/demo_files/image1.jpg");
		imgPreview.getElement().getStyle().setCursor(Cursor.CROSSHAIR);
		getVersionDetails(document);
	}
	
	public void getVersionDetails(final Documents doc) {
		
		AklaCommonService.Connect.getService().getVersions(doc, new AsyncCallback<List<Versions>>() {
			@Override
			public void onSuccess(List<Versions> result) {
				
				for (Versions v : result) {
					if (!v.isHide()) {
						setVersionNumber(v.getVersionNumber());
						
					}
				}
				
				previewDocInfo(document, document.getType());

			}

			@Override
			public void onFailure(Throwable caught) {
				
			}
		});

	}

	public void previewDocInfo(final Documents document, final String docType) {
		pagePreview.clear();	
		if (docType.equals(DocumentUtils.IMAGE)) {

			imgPreview.setUrl(ToolsGWT.getRightPath(document.getThumbImage()));
				//imgPreview.setUrl(DocumentManagement.getRightPath(document.getThumbImage()));
//				imageSettings.clear();
//				imageSettings.add(new ImageRotator(imgPreview, UserMain.getInstance(), true));

		}
		else if (docType.equals(DocumentUtils.PDF)) {
			//TODO
		} 
		else if (docType.equals(DocumentUtils.OFFICE)) {
			if (this.document.isFinished()) {
				AklaCommonService.Connect.getService().getPages(document.getId(), getVersionNumber(), new AsyncCallback<List<DocPages>>() {

					@Override
					public void onSuccess(List<DocPages> result) {
						
						PlaceComment.this.docPages = result;
						pagePreview.add(new PagesViewer(new DocumentViewer(document, null, PlaceComment.this, TypeViewer.GROUP_DOCUMENT), result, Orientation.HORIZONTAL));
						
					}

					@Override
					public void onFailure(Throwable caught) {
						
					}
				});
			}
			else {
				imgPreview.setUrl("webapps/aklabox_files/images/ic_thumbnail.png");
			}

		}
		else {
			imgPreview.setUrl(PathHelper.getRightPath(document.getThumbImage()));
		}
		
	}

	public void previewPage(String pageImagePath) {
		imgPreview.getElement().getStyle().setMarginTop(50, Unit.PX);
		imgPreview.setUrl(PathHelper.getRightPath(pageImagePath));
		//imgPreview.setUrl("http://keenthemes.com/preview/metronic/theme/assets/global/plugins/jcrop/demos/demo_files/image1.jpg");
		currentPath = pageImagePath;
	}



	public Documents getDocument() {
		return document;
	}

	public void setDocument(Documents document) {
		this.document = document;
	}

	public void loadComments() {
		AklaCommonService.Connect.getService().getComments(document.getId(), CommentStatus.ALL, new AsyncCallback<List<Comments>>() {

			@Override
			public void onSuccess(List<Comments> result) {
				updateComments(result);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	
	public void updateComments(List<Comments> comments) {
		for(Comments com : comments){
				previewPanel.add(new CommentBubble(com));
		}
	}

	public double getxPos() {
		return xPos;
	}

	public double getyPos() {
		return yPos;
	}

	public boolean isPlaced() {
		return isPlaced;
	}
	
	public int getSelectedPage() {
		return selectedPage;
	}
	
	@UiHandler("imgPreview")
	public void onPreviexClick(ClickEvent event){
		int absLeft = imgPreview.getAbsoluteLeft();
		int absRight = imgPreview.getAbsoluteLeft() + imgPreview.getOffsetWidth();
		int xClick = event.getClientX();
		
		int absTop = imgPreview.getAbsoluteTop();
		int absBottom = imgPreview.getAbsoluteTop() + imgPreview.getOffsetHeight();
		int yClick = event.getClientY();
		
		xPos = ((float)(xClick - absLeft) / (float)(absRight - absLeft)) * 100;
		yPos = ((float)(yClick - absTop) / (float)(absBottom - absTop)) * 100;
		selectedPage = getNumPage();
		if(xPos >= 0 && xPos <= 100 && yPos >= 0 && yPos <= 100){
			isPlaced = true;
			closeParent();
		}
	}

	private int getNumPage() {
		if(docPages == null) return 0;
		for(DocPages page : docPages){
			if(page.getImagePath().equals(currentPath)){
				return docPages.indexOf(page)+1;
			}
		}
		return 0;
	}

	@Override
	public void onValidateImageTreatment() {
		return;
	}

	@Override
	public void onReturnToOriginal() {
		return;
	}

	@Override
	public void onCommentUpdated(List<Comments> comments) {
		return;
	}

	public int getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}

	@Override
	public void onSearchResult(List<OCRSearchResult> result) {
		return;
	}
}