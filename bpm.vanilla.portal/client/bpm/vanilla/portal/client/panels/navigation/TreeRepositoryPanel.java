package bpm.vanilla.portal.client.panels.navigation;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.panels.MainPanel;
import bpm.vanilla.portal.client.panels.center.RepositoryContentPanel;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.tree.TreeDirectory;
import bpm.vanilla.portal.client.tree.TreeDirectoryItem;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;

public class TreeRepositoryPanel extends AbstractRepositoryStackPanel implements OpenHandler<TreeItem> {

	public TreeRepositoryPanel(MainPanel mainPanel) {
		super(mainPanel, TypeViewer.REPOSITORY, biPortal.get().getInfoUser().getRepository().getName());
	}

	@Override
	public void refresh(boolean refresh, final boolean showInDocumentPanel) {
		showWaitPart(true);

		if (refresh) {
			BiPortalService.Connect.getInstance().getRepositoryContents(new AsyncCallback<PortailRepositoryDirectory>() {

				@Override
				public void onFailure(Throwable caught) {
					showWaitPart(false);

					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
				}

				@Override
				public void onSuccess(PortailRepositoryDirectory result) {
					if (result != null) {

						biPortal.get().setContentRepository(result);

						displayData(result, getSearchValue());
						setOpenHandler(TreeRepositoryPanel.this);

						if (showInDocumentPanel) {
							showInDirectoryView(result);
						}
					}

					showWaitPart(false);
				}
			});
		}
		else {
			displayData(biPortal.get().getContentRepository(), getSearchValue());

			if (showInDocumentPanel) {
				showInDirectoryView(biPortal.get().getContentRepository());
			}

			showWaitPart(false);
		}
	}

	@Override
	public String getSearchValue() {
		String search = txtSearch.getText();
		return search;
	}

	@Override
	public void onOpen(OpenEvent<TreeItem> event) {
		if (event.getTarget() instanceof TreeDirectory) {
			TreeDirectory item = (TreeDirectory) event.getTarget();
			loadChild(item);
		}
		else if (event.getTarget() instanceof TreeDirectoryItem) {
			((TreeDirectoryItem)event.getTarget()).open();
		}
	}
	
	@Override
	public void loadChild(final TreeDirectory item) {
		final PortailRepositoryDirectory dir = item.getDirectory();
		if (!dir.isChildLoad()) {
			showWaitPart(true);
			BiPortalService.Connect.getInstance().getDirectoryContent(dir, new AsyncCallback<List<IRepositoryObject>>() {

				@Override
				public void onFailure(Throwable caught) {
					showWaitPart(false);

					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
				}

				@Override
				public void onSuccess(List<IRepositoryObject> result) {
					if (result != null && !result.isEmpty()) {
						dir.setItems(result);
					}
					else {
						dir.setItems(new ArrayList<IRepositoryObject>());
					}
					dir.setChildLoad(true);

					item.removeItems();

					buildChildDir(dir, item, getSearchValue());
					showInDirectoryView(dir);

					showWaitPart(false);
				}
			});
		}
	}

	@Override
	public void removeSearch() {
		clearSearch();
		displayData(biPortal.get().getContentRepository(), getSearchValue());
		
		showInDirectoryView(biPortal.get().getContentRepository());
	}

	@Override
	public void search() {
		setSearch(true);
		
		showWaitPart(true);
		
		String search = getSearchValue();
		
		BiPortalService.Connect.getInstance().searchRepository(search, new AsyncCallback<PortailRepositoryDirectory>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
			}

			@Override
			public void onSuccess(PortailRepositoryDirectory result) {
				if (result != null) {
					displayData(result, "");

					RepositoryContentPanel d = mainPanel.openRepositoryContentPanel();
					if (d != null) {
						showInDirectoryView(result);
					}
				}

				showWaitPart(false);
			}
		});
	}

}
