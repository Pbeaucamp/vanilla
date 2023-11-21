package bpm.fwr.client.tree;

import java.util.List;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.JoinDataSet;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.draggable.widgets.DraggableColumn;
import bpm.fwr.client.images.WysiwygImage;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class DatasetsTree extends Tree {

	private PickupDragController dragController;

	private List<DataSet> datasets;

	public DatasetsTree(List<DataSet> datasets, PickupDragController dragController) {
		this.datasets = datasets;
		this.dragController = dragController;

		TreeItem rootItem = new TreeItem(new HTML(new Image(WysiwygImage.INSTANCE.table()) + " " + Bpm_fwr.LBLW.DatasetList()));
		rootItem.setTitle(Bpm_fwr.LBLW.Metadata());

		if (datasets != null && !datasets.isEmpty()) {
			for (DataSet dataset : datasets) {
				TreeItem item = buildTreeDataset(dataset, dataset.getLanguage());
				rootItem.addItem(item);
			}
		}

		this.addItem(rootItem);
	}

	public DatasetsTree(DataSet dataset, PickupDragController dragController) {
		this.dragController = dragController;

		TreeItem rootItem = new TreeItem(new HTML(new Image(WysiwygImage.INSTANCE.table()) + " " + Bpm_fwr.LBLW.DatasetList()));
		rootItem.setTitle(Bpm_fwr.LBLW.Metadata());

		TreeItem item = buildTreeDataset(dataset, dataset.getLanguage());
		rootItem.addItem(item);

		this.addItem(rootItem);
	}

	private TreeItem buildTreeDataset(DataSet dataset, String defaultLanguage) {
		if (dataset instanceof JoinDataSet) {
			return buildJoinDataset((JoinDataSet) dataset, defaultLanguage);
		}

		TreeItem item = new TreeItem(new HTML(new Image(WysiwygImage.INSTANCE.metadata()) + " " + dataset.getName()));
		item.setTitle(dataset.getName());
		item.setUserObject(dataset);

		for (Column column : dataset.getColumns()) {
			DraggableColumn drgColumn = new DraggableColumn(column, defaultLanguage, "");

			TreeItem itemCol = new TreeItem(drgColumn);
			itemCol.setUserObject(column);
			itemCol.setTitle(column.getTitle(defaultLanguage));
			dragController.makeDraggable(drgColumn);

			item.addItem(itemCol);
		}

		return item;
	}

	private TreeItem buildJoinDataset(JoinDataSet dataset, String defaultLanguage) {
		TreeItem item = new TreeItem(new HTML(new Image(WysiwygImage.INSTANCE.metadata()) + " " + dataset.getName()));
		item.setTitle(dataset.getName());
		item.setUserObject(dataset);

		for (DataSet dsName : dataset.getChilds()) {
			for (DataSet ds : datasets) {
				if (ds.getName().equals(dsName)) {
					for (Column column : ds.getColumns()) {
						DraggableColumn drgColumn = new DraggableColumn(column, defaultLanguage, "");

						TreeItem itemCol = new TreeItem(drgColumn);
						itemCol.setUserObject(column);
						itemCol.setTitle(column.getTitle(defaultLanguage));
						dragController.makeDraggable(drgColumn);

						item.addItem(itemCol);
					}
					break;
				}
			}
		}

		return item;
	}
}
