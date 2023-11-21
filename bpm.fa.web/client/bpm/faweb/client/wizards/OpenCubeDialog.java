package bpm.faweb.client.wizards;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.tree.FaWebRepositoryTree;
import bpm.faweb.client.tree.FaWebTreeItem;
import bpm.faweb.shared.IDirectoryDTO;
import bpm.faweb.shared.IDirectoryItemDTO;
import bpm.faweb.shared.ItemCube;
import bpm.faweb.shared.TreeParentDTO;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

public class OpenCubeDialog extends AbstractDialogBox {

	private static OpenCubeDialogUiBinder uiBinder = GWT.create(OpenCubeDialogUiBinder.class);

	interface OpenCubeDialogUiBinder extends UiBinder<Widget, OpenCubeDialog> {
	}

	interface MyStyle extends CssResource {
		String background();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelContent;

//	@UiField
//	Image imgTypeViewer;

	private MainPanel mainCompParent;
	private int keySession;

	private FaWebRepositoryTree tree;
	private CustomDatagrid<ItemCube> gridCube;
	private SingleSelectionModel<ItemCube> selectionModel;

	private List<ItemCube> availableCubes;

	private boolean isLoad = false;
	private boolean isDatagrid = false;

	private ItemCube selectedCube;

	public OpenCubeDialog(MainPanel mainCompParent, int keySession, int fasdId) {
		super(FreeAnalysisWeb.LBL.WelcomeFa(), false, true);
		this.mainCompParent = mainCompParent;
		this.keySession = keySession;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		selectionModel = new SingleSelectionModel<ItemCube>();
		gridCube = new CustomDatagrid<ItemCube>(selectionModel, 200, FreeAnalysisWeb.LBL.NoCube(), FreeAnalysisWeb.LBL.SelCube());

		panelContent.setWidget(gridCube);

		if (fasdId != -1) {
			browseFASDModel(fasdId);
//			imgTypeViewer.setVisible(false);
		}
		else {
			browseRepositorie();
		}
	}

	private void loadCubes(List<ItemCube> availableCubes) {
		this.availableCubes = availableCubes;

		if (availableCubes.size() == 1) {
			hide();

			mainCompParent.showWaitPart(true);
			getInfosCube(mainCompParent, availableCubes.get(0));
		}

		if (isDatagrid) {
			panelContent.setWidget(gridCube);
			gridCube.loadItems(availableCubes);

			panelContent.removeStyleName(style.background());
		}
		else {
			panelContent.setWidget(tree);

			panelContent.addStyleName(style.background());
		}
	}

	private void getInfosCube(final MainPanel parent, final ItemCube selectedCube) {
		AsyncCallback<InfosReport> callback = new AsyncCallback<InfosReport>() {

			@Override
			public void onSuccess(InfosReport result) {
				parent.setInfosReport(result);
				try {
					parent.getDisplayPanel().getCubeViewerTab().clearFilters();
				} catch (Exception e) {
					e.printStackTrace();
				}

				parent.getDisplayPanel().clearDrill();
				parent.getNavigationPanel().refreshDataPanels(parent.getInfosReport().getCubeName());
				parent.setGridFromRCP(result);

				parent.getDisplayPanel().getCubeViewerTab().loadComment(null, selectedCube.getFASDParentId());

				parent.showWaitPart(false);
			}

			@Override
			public void onFailure(Throwable ex) {
				ex.printStackTrace();

				parent.showWaitPart(false);
			}
		};

		if (parent.getInfosReport() != null) {
			FaWebService.Connect.getInstance().getInfosCubeService(parent.getKeySession(), selectedCube, null, callback);
		}
		else {
			FaWebService.Connect.getInstance().getInfosCubeService(parent.getKeySession(), selectedCube, null, callback);
		}
	}

	@UiHandler("imgRefresh")
	public void onRefreshClick(ClickEvent event) {
		this.isLoad = false;
		browseRepositorie();
	}

	private void browseRepositorie() {
		if (!isLoad) {
			showWaitPart(true);

			FaWebService.Connect.getInstance().getRepositories(keySession, FaWebService.FASD, new AsyncCallback<TreeParentDTO>() {

				@Override
				public void onSuccess(TreeParentDTO result) {
					if (result != null) {
						fillFASDModel(result);
					}

					isLoad = true;
					showWaitPart(false);
				}

				@Override
				public void onFailure(Throwable ex) {
					showWaitPart(false);

					ex.printStackTrace();
				}
			});
		}
	}

	private void browseFASDModel(final int fasdId) {
		showWaitPart(true);

		FaWebService.Connect.getInstance().browseFASDModel(mainCompParent.getKeySession(), fasdId, new AsyncCallback<List<String>>() {

			@Override
			public void onSuccess(List<String> result) {
				showWaitPart(false);

				List<ItemCube> availableCubes = new ArrayList<ItemCube>();
				if (result != null) {
					for (String cube : result) {
						IDirectoryItemDTO fasd = new IDirectoryItemDTO();
						fasd.setId(fasdId);

						ItemCube item = new ItemCube(cube);
						item.setParent(fasd);

						availableCubes.add(item);
					}
				}

				loadCubes(availableCubes);
			}

			@Override
			public void onFailure(Throwable ex) {
				ex.printStackTrace();

				showWaitPart(false);

				loadCubes(new ArrayList<ItemCube>());
			}
		});
	}

	private void fillFASDModel(TreeParentDTO treeParent) {
		Image folder = new Image(FaWebImage.INSTANCE.folder());
		Image model = new Image(FaWebImage.INSTANCE.ApplFASDWeb());
		Image cube = new Image(FaWebImage.INSTANCE.mdx());
		Image[] i = { folder, model, cube };

		if (treeParent != null) {
			tree = new FaWebRepositoryTree(mainCompParent, i, treeParent);
			tree.addSelectionHandler(selectionHandler);

			List<ItemCube> availableCubes = new ArrayList<ItemCube>();
			buildListCube(treeParent, availableCubes);

			loadCubes(availableCubes);
		}
	}

	private void buildListCube(TreeParentDTO result, List<ItemCube> cubes) {
		for (Object item : result.getChildren()) {
			if (item instanceof IDirectoryDTO) {
				buildListCube((IDirectoryDTO) item, cubes);
			}
			else if (item instanceof IDirectoryItemDTO && ((IDirectoryItemDTO) item).getCubes() != null) {
				for (ItemCube cube : ((IDirectoryItemDTO) item).getCubes()) {
					cubes.add(cube);
				}
			}
		}
	}

	private SelectionHandler<TreeItem> selectionHandler = new SelectionHandler<TreeItem>() {

		@Override
		public void onSelection(SelectionEvent<TreeItem> event) {
			FaWebTreeItem it = (FaWebTreeItem) event.getSelectedItem();

			if (it.getItem() != null && it.getItem() instanceof ItemCube) {
				selectedCube = (ItemCube) it.getItem();
			}
			else {
				selectedCube = null;
			}
		}
	};

//	@UiHandler("imgTypeViewer")
//	public void onTypeViewerClick(ClickEvent event) {
//		isDatagrid = !isDatagrid;
//
//		loadCubes(availableCubes);
//	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			ItemCube selectedItem = selectedCube;
			if (isDatagrid) {
				selectedItem = selectionModel.getSelectedObject();
			}

			if (selectedItem == null) {
				MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), FreeAnalysisWeb.LBL.NeedSelectCube());
				return;
			}

			hide();

			mainCompParent.showWaitPart(true);
			getInfosCube(mainCompParent, selectedItem);
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

}
