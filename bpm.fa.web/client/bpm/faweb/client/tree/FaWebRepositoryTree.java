package bpm.faweb.client.tree;

import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.shared.IDirectoryDTO;
import bpm.faweb.shared.IDirectoryItemDTO;
import bpm.faweb.shared.ItemCube;
import bpm.faweb.shared.TreeParentDTO;
import bpm.vanilla.platform.core.IRepositoryApi;

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class FaWebRepositoryTree extends Tree {

	private MainPanel mainCompParent;
	private Image images[];

	public FaWebRepositoryTree(MainPanel mainCompParent, Image[] img, TreeParentDTO parent) {
		super();
		this.mainCompParent = mainCompParent;
		this.images = img;

		for (int i = 0; i < parent.getChildren().length; i++) {
			IDirectoryDTO servApp = (IDirectoryDTO) parent.getChildren()[i];

			Label lbl = new HTML(images[0] + " " + servApp.getName());

			FaWebTreeItem main = new FaWebTreeItem(lbl, "IDirectory", servApp);

			builChildDir(servApp, main);

			this.addItem(main);
		}

		this.addOpenHandler(openHandler);
	}

	private void builChildDir(IDirectoryDTO servApp, TreeItem app) {

		if (servApp.getChildren() != null && servApp.getChildren().length > 0) {
			for (int j = 0; j < servApp.getChildren().length; j++) {

				if (servApp.getChildren()[j] != null && servApp.getChildren()[j] instanceof IDirectoryItemDTO) {

					IDirectoryItemDTO focus = (IDirectoryItemDTO) servApp.getChildren()[j];

					Label lbl = new HTML(images[1] + " " + focus.getName());

					FaWebTreeItem ti = new FaWebTreeItem(lbl, "IDirectoryItem", focus);
					if (focus.getType().equals(IRepositoryApi.FASD)) {

						HTML htmlTempItem = new HTML(new Image(FaWebImage.INSTANCE.loading()) + " " + FreeAnalysisWeb.LBL.Wait());
						TreeItem tempItem = new TreeItem(htmlTempItem);
						tempItem.setTitle(FreeAnalysisWeb.LBL.Wait());
						ti.addItem(tempItem);

						// ti.addItem(isItem);
						// buildCubes(focus, app);
					}
					app.addItem(ti);
				}
				else if (servApp.getChildren()[j] != null && servApp.getChildren()[j] instanceof IDirectoryDTO) {
					IDirectoryDTO focus = (IDirectoryDTO) servApp.getChildren()[j];

					Label lbl = new HTML(images[0] + " " + focus.getName());

					FaWebTreeItem child = new FaWebTreeItem(lbl, "IDirectory", focus);

					app.addItem(child);

					builChildDir((IDirectoryDTO) servApp.getChildren()[j], child);

					app.addItem(child);

				}
			}
		}
	}

	private void buildCubes(IDirectoryItemDTO focus, TreeItem ti) {
		if (focus.getCubes() != null) {
			for (ItemCube cube : focus.getCubes()) {
				Label lbl = new HTML(images[2] + " " + cube.getName());

				FaWebTreeItem child = new FaWebTreeItem(lbl, "Cube", cube);
				ti.addItem(child);
			}
		}
	}

	public String getSelectedValue() {
		TreeItem it = getSelectedItem();
		FaWebTreeItem fti = (FaWebTreeItem) it;
		return fti.getUname();
	}

	public int getSelectedId() {
		TreeItem it = getSelectedItem();
		if (it == null) {
			return -1;
		}

		FaWebTreeItem fti = (FaWebTreeItem) it;
		return fti.getId();
	}

	private OpenHandler<TreeItem> openHandler = new OpenHandler<TreeItem>() {

		@Override
		public void onOpen(final OpenEvent<TreeItem> event) {
			if (event.getTarget() != null && event.getTarget() instanceof FaWebTreeItem) {
				final FaWebTreeItem treeItem = (FaWebTreeItem) event.getTarget();
				if (treeItem.isCube() && !treeItem.isLoad()) {
					FaWebService.Connect.getInstance().browseFASDModel(mainCompParent.getKeySession(), treeItem.getId(), new AsyncCallback<List<String>>() {

						@Override
						public void onSuccess(List<String> result) {
							treeItem.removeItems();
							treeItem.setLoad(true);

							IDirectoryItemDTO item = (IDirectoryItemDTO) treeItem.getItem();

							for (String name : result) {
								ItemCube cube = new ItemCube(name);
								item.addCube(cube);
							}

							buildCubes(item, treeItem);
						}

						@Override
						public void onFailure(Throwable ex) {
							ex.printStackTrace();
						}
					});
				}
			}
		}
	};
}
