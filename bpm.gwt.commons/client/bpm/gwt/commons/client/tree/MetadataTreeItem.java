package bpm.gwt.commons.client.tree;

import bpm.gwt.commons.client.tree.TreeObjectWidget.IDragListener;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.fmdt.metadata.ColumnJoin;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataChart;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataModel;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataPackage;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataQueries;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataQuery;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataRelation;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataResource;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataResources;
import bpm.gwt.commons.shared.fmdt.metadata.TableRelation;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.Datasource;

import com.google.gwt.user.client.ui.TreeItem;

public class MetadataTreeItem<T> extends TreeItem {

	private IDragListener dragListener;
	private TreeObjectWidget<T> objectWidget;
	private boolean full;
	
	private boolean loaded = false;

	public MetadataTreeItem(IDragListener dragListener, TreeObjectWidget<T> objectWidget, boolean full) {
		super(objectWidget);
		this.dragListener = dragListener;
		this.full = full;
		
		buildItem(objectWidget);
	}
	
	public void buildItem(TreeObjectWidget<T> objectWidget) {
		this.objectWidget = objectWidget;
		
		removeItems();
		
		T item = objectWidget.getItem();
		if (item instanceof Metadata) {
			Metadata model = (Metadata) item;
			if (full && model.getDatasource() != null) {
				this.addItem(new MetadataTreeItem<Datasource>(dragListener, new TreeObjectWidget<Datasource>(dragListener, model.getDatasource()), full));
			}
			if (model.getModels() != null) {
				this.loaded = true;
				for (MetadataModel object : model.getModels()) {
					this.addItem(new MetadataTreeItem<MetadataModel>(dragListener, new TreeObjectWidget<MetadataModel>(dragListener, object), full));
				}
			}

			if (loaded) {
				setState(true);
			}
			else {
				this.addItem(new WaitTreeItem());
			}
		}
		else if (item instanceof MetadataModel) {
			MetadataModel model = (MetadataModel) item;
			if (model.getPackages() != null) {
				for (MetadataPackage object : model.getPackages()) {
					this.addItem(new MetadataTreeItem<MetadataPackage>(dragListener, new TreeObjectWidget<MetadataPackage>(dragListener, object), full));
				}
			}
			if (full && model.getRelation() != null) {
				this.addItem(new MetadataTreeItem<MetadataRelation>(dragListener, new TreeObjectWidget<MetadataRelation>(dragListener, model.getRelation()), full));
			}

			setState(true);
		}
		else if (item instanceof MetadataPackage) {
			MetadataPackage pack = (MetadataPackage) item;
			if (pack.getTables() != null) {
				for (DatabaseTable object : pack.getTables()) {
					this.addItem(new MetadataTreeItem<DatabaseTable>(dragListener, new TreeObjectWidget<DatabaseTable>(dragListener, object), full));
				}
			}
			if (pack.getResources() != null) {
				this.addItem(new MetadataTreeItem<MetadataResources>(dragListener, new TreeObjectWidget<MetadataResources>(dragListener, pack.getResources()), full));
			}
			if (pack.getQueries() != null) {
				this.addItem(new MetadataTreeItem<MetadataQueries>(dragListener, new TreeObjectWidget<MetadataQueries>(dragListener, pack.getQueries()), full));
			}

			setState(true);
		}
		else if (item instanceof MetadataResources) {
			MetadataResources resources = (MetadataResources) item;
			if (resources.getResources() != null) {
				for (MetadataResource object : resources.getResources()) {
					this.addItem(new MetadataTreeItem<MetadataResource>(dragListener, new TreeObjectWidget<MetadataResource>(dragListener, object), full));
				}
			}
		}
		else if (item instanceof MetadataQueries) {
			MetadataQueries queries = (MetadataQueries) item;
			if (queries.getQueries() != null) {
				for (MetadataQuery object : queries.getQueries()) {
					this.addItem(new MetadataTreeItem<MetadataQuery>(dragListener, new TreeObjectWidget<MetadataQuery>(dragListener, object), full));
				}
			}
		}
		else if (item instanceof MetadataQuery) {
			MetadataQuery queries = (MetadataQuery) item;
			if (queries.getCharts() != null) {
				for (MetadataChart object : queries.getCharts()) {
					this.addItem(new MetadataTreeItem<MetadataChart>(dragListener, new TreeObjectWidget<MetadataChart>(dragListener, object), full));
				}
			}
		}
		else if (item instanceof MetadataRelation) {
			MetadataRelation relation = (MetadataRelation) item;
			if (relation.getRelations() != null) {
				for (TableRelation object : relation.getRelations()) {
					this.addItem(new MetadataTreeItem<TableRelation>(dragListener, new TreeObjectWidget<TableRelation>(dragListener, object), full));
				}
			}
		}
		else if (item instanceof DatabaseTable) {
			DatabaseTable table = (DatabaseTable) item;
			if (table.getColumns() != null) {
				for (DatabaseColumn object : table.getColumns()) {
					this.addItem(new MetadataTreeItem<DatabaseColumn>(dragListener, new TreeObjectWidget<DatabaseColumn>(dragListener, object), full));
				}
			}
		}
		else if (item instanceof TableRelation) {
			TableRelation relation = (TableRelation) item;
			if (relation.getJoins() != null) {
				for (ColumnJoin object : relation.getJoins()) {
					this.addItem(new MetadataTreeItem<ColumnJoin>(dragListener, new TreeObjectWidget<ColumnJoin>(dragListener, object), full));
				}
			}
		}
	}

	@Override
	public void setSelected(boolean selected) {
		if (!selected) {
			this.objectWidget.removeStyleName(VanillaCSS.TREE_ITEM_SELECTED);
		}
		else {
			this.objectWidget.addStyleName(VanillaCSS.TREE_ITEM_SELECTED);
		}
		super.setSelected(selected);
	}

	public T getItem() {
		return objectWidget.getItem();
	}
	
	public boolean isLoaded() {
		return loaded;
	}
}
