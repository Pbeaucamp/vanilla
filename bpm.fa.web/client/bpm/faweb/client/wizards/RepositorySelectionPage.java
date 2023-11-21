package bpm.faweb.client.wizards;

import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.tree.FaWebRepositoryTree;
import bpm.faweb.client.tree.FaWebTreeItem;
import bpm.faweb.shared.TreeParentDTO;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class RepositorySelectionPage extends Composite implements IGwtPage {

	private static RepositorySelectionPageUiBinder uiBinder = GWT.create(RepositorySelectionPageUiBinder.class);

	interface RepositorySelectionPageUiBinder extends UiBinder<Widget, RepositorySelectionPage> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	SimplePanel panelContent;

	@UiField
	MyStyle style;

	private IGwtWizard parent;
	private int index;
	private int keySession;

	private Integer selectedFasdId;
	
	private boolean isLoad = false;

	public RepositorySelectionPage(IGwtWizard parent, int index, Integer fasdId, int keySession) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;
		this.selectedFasdId = fasdId;
		this.keySession = keySession;
	}

	@UiHandler("imgRefresh")
	public void onRefreshClick(ClickEvent event) {
		this.isLoad = false;
		browseRepositorie();
	}

	public void browseRepositorie() {
		if(!isLoad) {
			parent.showWaitPart(true);
			
			FaWebService.Connect.getInstance().getRepositories(keySession, FaWebService.FASD, new AsyncCallback<TreeParentDTO>() {
	
				@Override
				public void onSuccess(TreeParentDTO result) {
					if (result != null) {
						fillFASDModel(result);
					}
					
					isLoad = true;
					parent.showWaitPart(false);
				}
	
				@Override
				public void onFailure(Throwable ex) {
					parent.showWaitPart(false);
					
					ex.printStackTrace();
				}
			});
		}
	}

	public void fillFASDModel(TreeParentDTO treeParent) {
		Image folder = new Image(FaWebImage.INSTANCE.folder());
		Image leaf = new Image(FaWebImage.INSTANCE.mdx());
		Image[] i = { folder, leaf };

		if (treeParent != null) {
			FaWebRepositoryTree tree = new FaWebRepositoryTree(null, i, treeParent);
			tree.addSelectionHandler(selectionHandler);
			
			panelContent.setWidget(tree);
		}
	}
	
	private SelectionHandler<TreeItem> selectionHandler = new SelectionHandler<TreeItem>() {
		
		@Override
		public void onSelection(SelectionEvent<TreeItem> event) {
			FaWebTreeItem it = (FaWebTreeItem) event.getSelectedItem();

			if (it.getUname().equals("IDirectoryItem")) {
				try {
					selectedFasdId = it.getId();
				} catch (Exception e1) {
					selectedFasdId = null;
				}
			}
			else {
				selectedFasdId = null;
			}
			
			parent.updateBtn();
		}
	};

	@Override
	public boolean canGoBack() {
		return false;
	}

	@Override
	public boolean canGoFurther() {
		return isComplete() ? true : false;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isComplete() {
		return selectedFasdId != null ? true : false;
	}

	public Integer getSelectedFasdId() {
		return selectedFasdId;
	}
}
