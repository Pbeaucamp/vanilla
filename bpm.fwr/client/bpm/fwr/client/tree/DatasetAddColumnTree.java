package bpm.fwr.client.tree;

import java.util.List;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.JoinDataSet;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.draggable.widgets.DraggableColumn;
import bpm.fwr.client.draggable.widgets.DraggableTable;
import bpm.fwr.client.images.WysiwygImage;
import bpm.fwr.client.services.FwrServiceConnection;
import bpm.fwr.client.services.FwrServiceMetadata;
import bpm.fwr.shared.models.metadata.FwrBusinessModel;
import bpm.fwr.shared.models.metadata.FwrBusinessPackage;
import bpm.fwr.shared.models.metadata.FwrBusinessTable;
import bpm.fwr.shared.models.metadata.FwrMetadata;
import bpm.gwt.commons.shared.InfoUser;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class DatasetAddColumnTree extends Tree {

	private PickupDragController dragController;

	private DataSet dataset;
	private String defaultLanguage;

	public DatasetAddColumnTree(DataSet dataset, PickupDragController dragController) {
		this.dataset = dataset;
		this.dragController = dragController;

		// We create a RootTreeItem to support the right click and show a
		// personalized ContextMenu
		HTML rootItem = new HTML(new Image(WysiwygImage.INSTANCE.table()) + " " + Bpm_fwr.LBLW.Metadata());

		// We create a wait item to informe that is loading
		TreeItem tempItem = new TreeItem(new HTML(new Image(GWT.getHostPageBaseURL() + "images/wait.gif") + " " + Bpm_fwr.LBLW.Wait()));

		TreeItem root = new TreeItem(rootItem);
		root.addItem(tempItem);

		if (dataset instanceof JoinDataSet) {
			buildTreeForJoinDataset(root);
		}
		else {
			buildTreeForDataset(root);
		}

		refreshTreePar(root);
	}

	private void refreshTreePar(TreeItem root) {
		this.clear();
		this.addItem(root);
	}

	private void buildTreeForDataset(final TreeItem root) {
		InfoUser infoUser = Bpm_fwr.getInstance().getInfoUser();
		FwrServiceConnection.Connect.getInstance().getMetadatas(infoUser.getGroup().getName(), new AsyncCallback<List<FwrMetadata>>() {
			public void onSuccess(List<FwrMetadata> metadatas) {

				if (metadatas != null && !metadatas.isEmpty()) {
					for (FwrMetadata metadata : metadatas) {
						if (dataset.getDatasource().getItemId() == metadata.getId()) {
							browseMetadataContent(root, metadata);
							return;
						}
					}

					// If we don't find the good metadata we say it
					TreeItem item = new TreeItem(new HTML(Bpm_fwr.LBLW.NoColumnAvailable()));
					root.addItem(item);

					refreshTreePar(root);
				}
				else {
					TreeItem item = new TreeItem(new HTML(Bpm_fwr.LBLW.NoColumnAvailable()));
					root.addItem(item);

					refreshTreePar(root);
				}
			}

			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				TreeItem item = new TreeItem(new HTML(Bpm_fwr.LBLW.NoColumnAvailable()));
				root.addItem(item);

				refreshTreePar(root);
			}
		});
	}

	private void browseMetadataContent(final TreeItem root, final FwrMetadata metadata) {
		InfoUser infoUser = Bpm_fwr.getInstance().getInfoUser();
		FwrServiceMetadata.Connect.getInstance().getMetadataContent(metadata.getId(), infoUser.getGroup().getName(), false, new AsyncCallback<List<FwrBusinessModel>>() {

			public void onSuccess(List<FwrBusinessModel> businessModels) {
				if (businessModels != null && !businessModels.isEmpty()) {

					metadata.setBusinessModels(businessModels);
					if (businessModels.size() > 0) {
						metadata.setLocales(businessModels.get(0).getLocales());
					}
					for (String key : metadata.getLocales().keySet()) {
						setDefaultLanguage(key);
						break;
					}
					root.removeItems();

					TreeItem item = new TreeItem(new HTML(new Image(WysiwygImage.INSTANCE.metadata()) + " " + metadata.getName()));
					item.setUserObject(metadata);
					root.addItem(item);

					for (FwrBusinessModel businessModel : businessModels) {
						if (businessModel.getName().equals(dataset.getDatasource().getBusinessModel())) {

							HTML packmod = new HTML(new Image(WysiwygImage.INSTANCE.table()) + " " + businessModel.getTitles().get(defaultLanguage));
							TreeItem itemModel = new TreeItem(packmod);
							itemModel.setUserObject(businessModel);
							item.addItem(itemModel);

							for (FwrBusinessPackage businessPackage : businessModel.getBusinessPackages()) {
								if (businessPackage.getName().equals(dataset.getDatasource().getBusinessPackage())) {

									HTML packpan = new HTML(new Image(WysiwygImage.INSTANCE.table()) + " " + businessPackage.getTitles().get(defaultLanguage));
									TreeItem itemPack = new TreeItem(packpan);
									itemPack.setUserObject(businessPackage);
									itemModel.addItem(itemPack);

									for (FwrBusinessTable table : businessPackage.getBusinessTables()) {
										HTML tab = new HTML(new Image(WysiwygImage.INSTANCE.object()) + " " + table.getTitles().get(defaultLanguage));
										TreeItem itemTable = new TreeItem(tab);
										itemTable.setUserObject(table);
										itemPack.addItem(itemTable);

										if (table.getChilds() != null && table.getChilds().size() > 0) {
											fillChilds(table, itemTable, metadata, item);
										}

										for (Column column : table.getColumns()) {
											// We set the metadata informations
											// to the column that we can create
											// datasource and dataset later
											column.setBusinessPackageParent(businessPackage.getName());
											column.setBusinessModelParent(businessModel.getName());
											column.setMetadataParent(metadata.getName());
											column.setMetadataId(metadata.getId());

											DraggableColumn drgColumn = new DraggableColumn(column, defaultLanguage, "");
											TreeItem itemCol = new TreeItem(drgColumn);
											itemCol.setUserObject(column);
											itemTable.addItem(itemCol);
											dragController.makeDraggable(drgColumn);
										}
									}

									refreshTreePar(root);
									return;
								}
							}
						}
					}
				}

				// If we don't find the good metadata we say it
				TreeItem item = new TreeItem(new HTML(Bpm_fwr.LBLW.NoColumnAvailable()));
				root.removeItems();
				root.addItem(item);

				refreshTreePar(root);
			}

			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				// If we don't find the good metadata we say it
				TreeItem item = new TreeItem(new HTML(Bpm_fwr.LBLW.NoColumnAvailable()));
				root.addItem(item);

				refreshTreePar(root);
			}
		});
	}

	private void buildTreeForJoinDataset(final TreeItem root) {
		// TODO: build for join dataset
	}

	private void fillChilds(FwrBusinessTable parenttable, TreeItem parentdrgTable, FwrMetadata metadata, TreeItem item) {
		for (FwrBusinessTable table : parenttable.getChilds()) {
			DraggableTable drgTable = new DraggableTable(table, defaultLanguage);
			TreeItem itemTable = new TreeItem();
			itemTable.setWidget(drgTable);
			parentdrgTable.addItem(itemTable);

			if (table.getChilds() != null && table.getChilds().size() > 0) {
				fillChilds(table, itemTable, metadata, item);
			}

			for (Column column : table.getColumns()) {
				DraggableColumn drgColumn = new DraggableColumn(column, defaultLanguage, "");
				TreeItem itemCol = new TreeItem(drgColumn);
				itemCol.setUserObject(column);
				itemTable.addItem(itemCol);
			}
		}
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}
}
