package bpm.gwt.aklabox.commons.client.tree;

import java.util.List;

import bpm.document.management.core.model.IObject;
import bpm.gwt.aklabox.commons.shared.InfoUser;

public interface IActionManager {

	public enum ItemAction {
		SELECT, EDIT, OPEN, DELETE, DOWNLOAD, RIGHTS, SHARE, UPLOAD, ADD_FOLDER, DOWNLOAD_FOLDER, 
		DOWNLOAD_FOLDER_AS_PDF, WORKFLOW, RESTORE, COPY, PRINT_LIST, DIAPORAMA, EXPORT_PESV2, 
		CLOSE_PROJECT, CREATE_DOC, MAIL, SHOW_VALIDATION, SHOW_STATS, FILTER_DOCUMENT_WITH_TASK, 
		ASSIGN, SAVE_SEARCH, EXPORT_SEARCH, SCAN, CLOUD, CREATE_URL, EDIT_WOPI, LAUNCH_WORKFLOW, 
		ARCHIVE, EDIT_METADATA, EDIT_OFFICE, SHOW_IN_FOLDER;
	}

	public void launchAction(IObject item, ItemAction action);

	public void launchMultipleAction(List<IObject> items, ItemAction action);
	
	public InfoUser getInfoUser();
	
	public void refresh(boolean refreshFull, boolean showInContentPanel, boolean refreshFolder);
}
