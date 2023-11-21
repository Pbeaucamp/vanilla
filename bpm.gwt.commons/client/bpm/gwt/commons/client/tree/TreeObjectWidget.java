package bpm.gwt.commons.client.tree;

import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.shared.cmis.CmisDocument;
import bpm.gwt.commons.shared.cmis.CmisFolder;
import bpm.gwt.commons.shared.fmdt.metadata.ColumnJoin;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataChart;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataFilter;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataModel;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataPackage;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataPrompt;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataQuery;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataRelation;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataResources;
import bpm.gwt.commons.shared.fmdt.metadata.TableRelation;
import bpm.mdm.model.supplier.MdmDirectory;
import bpm.vanilla.map.core.design.MapLayer;
import bpm.vanilla.map.core.design.MapServer;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.Datasource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TreeObjectWidget<T> extends Composite implements DragStartHandler, DragEndHandler {
	
	public static String TREE_OBJECT_WIDGET = "TreeObjectWidget";

	private static TreeObjectWidgetUiBinder uiBinder = GWT.create(TreeObjectWidgetUiBinder.class);

	interface TreeObjectWidgetUiBinder extends UiBinder<Widget, TreeObjectWidget<?>> {
	}

	interface MyStyle extends CssResource {
		String imgObject();
		String error();
	}

	@UiField
	FocusPanel dragPanel;

	@UiField
	Image imgObject;

	@UiField
	Label lblObject;

	@UiField
	MyStyle style;

	private IDragListener dragListener;
	private T item;
	
	public TreeObjectWidget(IDragListener dragListener, T item) {
		this(dragListener, item, false);
	}
	
	public TreeObjectWidget(IDragListener dragListener, T item, boolean error) {
		initWidget(uiBinder.createAndBindUi(this));
		this.dragListener = dragListener;
		this.item = item;

		imgObject.setResource(findResource(item));
		imgObject.addStyleName(style.imgObject());
		
		lblObject.setText(item.toString());
		
		if (error) {
			lblObject.addStyleName(style.error());
			imgObject.setResource(CommonImages.INSTANCE.error_16());
		}

		dragPanel.getElement().setDraggable(Element.DRAGGABLE_TRUE);
		dragPanel.addDragStartHandler(this);
	}

	public T getItem() {
		return item;
	}

	private ImageResource findResource(T item) {
		if (item instanceof Metadata) {
			return CommonImages.INSTANCE.directory_item();
		}
		else if (item instanceof Datasource) {
			return CommonImages.INSTANCE.datasource();
		}
		else if (item instanceof MetadataModel) {
			return CommonImages.INSTANCE.model();
		}
		else if (item instanceof MetadataPackage) {
			return CommonImages.INSTANCE.package_16();
		}
		else if (item instanceof DatabaseTable) {
			return CommonImages.INSTANCE.business_table();
		}
		else if (item instanceof MetadataResources) {
			return CommonImages.INSTANCE.object();
		}
		else if (item instanceof MetadataPrompt) {
			return CommonImages.INSTANCE.prompt();
		}
		else if (item instanceof MetadataFilter) {
			return CommonImages.INSTANCE.filter();
		}
		else if (item instanceof MetadataRelation) {
			return CommonImages.INSTANCE.object();
		}
		else if (item instanceof DatabaseColumn) {
			return CommonImages.INSTANCE.metadata_column();
		}
		else if (item instanceof TableRelation) {
			return CommonImages.INSTANCE.object();
		}
		else if (item instanceof ColumnJoin) {
			return CommonImages.INSTANCE.relation();
		}
		else if (item instanceof CmisFolder) {
			return CommonImages.INSTANCE.folder();
		}
		else if (item instanceof CmisDocument) {
			return CommonImages.INSTANCE.log_column();
		}
		else if (item instanceof MetadataQuery) {
			return CommonImages.INSTANCE.view();
		}
		else if (item instanceof MetadataChart) {
			return CommonImages.INSTANCE.chart_item();
		}
		else if (item instanceof MapServer) {
			return CommonImages.INSTANCE.ic_map_black_18dp();
		}
		else if (item instanceof MapLayer) {
			return CommonImages.INSTANCE.ic_menu_view_small();
		}
		else if (item instanceof MdmDirectory) {
			return CommonImages.INSTANCE.folder();
		}

		return CommonImages.INSTANCE.object();
	}

	@Override
	public void onDragStart(DragStartEvent event) {
		event.stopPropagation();
		
		event.setData(TREE_OBJECT_WIDGET, "true");
		event.getDataTransfer().setDragImage(getElement(), 10, 10);
		
		dragListener.setDragItem(item);
	}

	@Override
	public void onDragEnd(DragEndEvent event) {
		event.stopPropagation();
		dragListener.removeDragItem();
	}
	
	public interface IDragListener {
		
		public Object getDragItem();
		
		public void setDragItem(Object item);
		
		public void removeDragItem();
	}
}
