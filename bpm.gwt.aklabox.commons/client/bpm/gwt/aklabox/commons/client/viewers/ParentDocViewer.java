package bpm.gwt.aklabox.commons.client.viewers;

import java.util.List;

import bpm.document.management.core.model.Comments;
import bpm.gwt.aklabox.commons.shared.OCRSearchResult;

public interface ParentDocViewer{

	public void onValidateImageTreatment();
	
	public void onReturnToOriginal();
	
	public void onCommentUpdated(List<Comments> comments);
	
	public void previewPage(String path);
	
	public void onSearchResult(List<OCRSearchResult> result);
}
