package bpm.gwt.aklabox.commons.client.tree;

import java.util.HashMap;
import java.util.List;

import bpm.document.management.core.model.Enterprise;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.utils.AklaboxConstant;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.aklabox.commons.client.utils.EnterpriseTreeItem;
import bpm.gwt.aklabox.commons.client.utils.IsLoaded;
import bpm.gwt.aklabox.commons.shared.AklaboxConnection;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class AklaboxTreeManager {
	
	private static HashMap<Integer, IsLoaded> loads = new HashMap<>();

	public static Integer loadMail(AklaboxConnection server, int folderId, final Tree tree, final TreeItem treeItem) {
		final Integer key = new Object().hashCode();
		loads.put(key, new IsLoaded());
		
		AklaCommonService.Connect.getService().getDirectoryContent(server, folderId, new GwtCallbackWrapper<List<IObject>>(null, false, false) {
			@Override
			public void onSuccess(List<IObject> result) {
				if (treeItem != null) {
					treeItem.removeItems();
				}

				for (IObject obj : result) {
					if (obj instanceof bpm.document.management.core.model.Tree) {
						DirectoryTreeItem it = new DirectoryTreeItem((bpm.document.management.core.model.Tree) obj, AklaboxConstant.MAIL);
						if (treeItem != null) {
							treeItem.addItem(it);
						}
						else {
							tree.addItem(it);
						}

						loads.get(key).setLoaded(true);

						it.addItem(new TreeItem(new Label("...")));
					}
				}
			}
		}.getAsyncCallback());
		
		return key;
	}

	public static Integer loadEnterprise(final AklaboxConnection server, String login, final Tree tree) {
		tree.clear();
		
		final Integer key = new Object().hashCode();
		loads.put(key, new IsLoaded());

		AklaCommonService.Connect.getService().getEnterprisePerUser(server, login, new GwtCallbackWrapper<List<Enterprise>>(null, false, false) {

			@Override
			public void onSuccess(List<Enterprise> result) {
				int totalLoad = result.size();
				loads.get(key).setLoaded(true);
				loads.get(key).setTotalLoad(totalLoad);
				
				for (Enterprise e : result) {
					final EnterpriseTreeItem branch = new EnterpriseTreeItem(e, false);
					tree.addItem(branch);

					try {
						AklaCommonService.Connect.getService().getFoldersPerEnterprise(server, e.getEnterpriseId(), new GwtCallbackWrapper<List<bpm.document.management.core.model.Tree>>(null, false, false) {
							@Override
							public void onFailure(Throwable caught) {
								loads.get(key).incrementLoadFinish();
								super.onFailure(caught);
							}

							@Override
							public void onSuccess(final List<bpm.document.management.core.model.Tree> result) {
								if (result.isEmpty()) {
									loads.get(key).incrementLoadFinish();
									return;
								}
								createTree(branch, (bpm.document.management.core.model.Tree) result.get(0), true);
								loads.get(key).incrementLoadFinish();
							}
						}.getAsyncCallback());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		}.getAsyncCallback());
		
		return key;
	}

	public static void createTree(EnterpriseTreeItem branch, bpm.document.management.core.model.Tree rootFolder, boolean isExpanded) {
		DirectoryTreeItem branchItems = new DirectoryTreeItem(rootFolder, AklaboxConstant.DEFAULT);
		branch.addItem(branchItems);
		addFolderContent(rootFolder.getChildren(), branchItems, isExpanded);
		branchItems.setState(isExpanded);
	}

	private static void addFolderContent(List<IObject> elements, TreeItem parent, boolean isExpanded) {
		if (elements != null && !elements.isEmpty()) {
			for (IObject element : elements) {

				if (element instanceof bpm.document.management.core.model.Tree) {
					DirectoryTreeItem item = new DirectoryTreeItem((bpm.document.management.core.model.Tree) element, AklaboxConstant.DEFAULT);

					List<IObject> children = ((bpm.document.management.core.model.Tree) element).getChildren();
					if (children != null && !children.isEmpty()) {
						addFolderContent(((bpm.document.management.core.model.Tree) element).getChildren(), item, isExpanded);
					}
					parent.addItem(item);
					item.setState(isExpanded);
				}
			}
		}
	}
	
	public static IsLoaded getLoaded(Integer key) {
		return loads.get(key);
	}
}
