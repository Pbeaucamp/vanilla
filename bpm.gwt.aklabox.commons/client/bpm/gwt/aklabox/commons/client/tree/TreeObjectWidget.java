package bpm.gwt.aklabox.commons.client.tree;

import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Enterprise;
import bpm.document.management.core.model.FolderHierarchy;
import bpm.document.management.core.model.FolderHierarchyDocument;
import bpm.document.management.core.model.FolderHierarchyItem;
import bpm.document.management.core.model.Group;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.IObject.ItemTreeType;
import bpm.document.management.core.model.IObject.ItemType;
import bpm.document.management.core.model.Tree;
import bpm.document.management.core.utils.AklaboxConstant;
import bpm.document.management.core.utils.DocumentUtils;
import bpm.gwt.aklabox.commons.client.images.CommonImages;
import bpm.gwt.aklabox.commons.client.panels.IContentManager;
import bpm.gwt.aklabox.commons.client.utils.DragAndDropHelper;
import bpm.gwt.aklabox.commons.client.utils.PathHelper;
import bpm.gwt.aklabox.commons.client.utils.ImageHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.HasContextMenuHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TreeObjectWidget<T> extends Composite implements HasContextMenuHandlers, DragStartHandler, DragEndHandler, DragOverHandler, DragLeaveHandler, DropHandler {

	public static String TREE_OBJECT_WIDGET = "TreeObjectWidget";

	private static TreeObjectWidgetUiBinder uiBinder = GWT.create(TreeObjectWidgetUiBinder.class);

	interface TreeObjectWidgetUiBinder extends UiBinder<Widget, TreeObjectWidget<?>> {
	}

	interface MyStyle extends CssResource {
		String imgObject();

		String selected();
		String dragOver();
	}

	@UiField
	FocusPanel dragPanel;

	@UiField
	Image imgObject;

	@UiField
	Label lblObject;

	@UiField
	MyStyle style;

	private IDropManager dropManager;
	private IDragListener dragListener;
	private T item;
	private IContentManager docsManager;

	private HandlerManager manager = new HandlerManager(this);

	public TreeObjectWidget(T item, DocumentUtils docUtils) {
		buildContent(item, null, docUtils, false);
	}

	public TreeObjectWidget(IDropManager dropManager, IDragListener dragListener, T item, ItemTreeType type, DocumentUtils docUtils, boolean displayCustomLogo, IContentManager docsManager) {
		this.dropManager = dropManager;
		this.dragListener = dragListener;
		this.docsManager = docsManager;
		buildContent(item, type, docUtils, displayCustomLogo);
	}
	
	private void buildContent(T item, ItemTreeType type, DocumentUtils docUtils, boolean displayCustomLogo) {
		initWidget(uiBinder.createAndBindUi(this));
		this.item = item;

		String imageUrl = findResource(item, type, docUtils, displayCustomLogo);
		if (imageUrl != null) {
			imgObject.setUrl(imageUrl);
			imgObject.addStyleName(style.imgObject());
		}
		else {
			imgObject.removeFromParent();
		}

		lblObject.setText(item.toString());
		this.sinkEvents(Event.ONCONTEXTMENU);

		if (dragListener != null) {
			dragPanel.getElement().setDraggable(Element.DRAGGABLE_TRUE);
			dragPanel.addDragStartHandler(this);
			dragPanel.addDragEndHandler(this);
			
			if (item instanceof Tree) {
				dragPanel.addDragOverHandler(this);
				dragPanel.addDragLeaveHandler(this);
				dragPanel.addDropHandler(this);
			}
		}
	}

	public T getItem() {
		return item;
	}

	private String findResource(T item, ItemTreeType type, DocumentUtils docUtils, boolean displayCustomLogo) {
		if (item instanceof FolderHierarchy) {
			return CommonImages.INSTANCE.hierarchy_16().getSafeUri().asString();
		}
		else if (item instanceof FolderHierarchyItem) {
			return CommonImages.INSTANCE.folder_16().getSafeUri().asString();
		}
		else if (item instanceof FolderHierarchyDocument) {
			return CommonImages.INSTANCE.document_16().getSafeUri().asString();
		}
		else if (item instanceof Group) {
			return CommonImages.INSTANCE.ic_userGroups().getSafeUri().asString();
		}
//		else if (item instanceof GroupSharedName) {
//			return CommonImages.INSTANCE.ic_userGroups().getSafeUri().asString();
//		}
		else if (item instanceof Enterprise) {
			Enterprise enterprise = (Enterprise) item;
			if (displayCustomLogo && enterprise.getLogo() != null && !enterprise.getLogo().isEmpty()) {
				return PathHelper.getRightPath(enterprise.getLogo());
			}
			else {
				return CommonImages.INSTANCE.ic_enterprise_16().getSafeUri().asString();
			}
		}
		else if (item instanceof IObject) {
			if (item instanceof Tree) {
				Tree folder = (Tree) item;

				if (folder.getTreeType().equals(AklaboxConstant.FOLDER_WORKFLOW)) {
					return CommonImages.INSTANCE.ic_folder_workflow().getSafeUri().asString();
				}
				else if (folder.getTreeType().equals(AklaboxConstant.FOLDER_PESV2)) {
					return CommonImages.INSTANCE.ic_folder_pesv2().getSafeUri().asString();
				}
				else if (folder.getTreeType().equals(AklaboxConstant.FOLDER_PROJECT)) {
					return CommonImages.INSTANCE.ic_folder_project().getSafeUri().asString();
				}
				else if (folder.getTreeType().equals(AklaboxConstant.FOLDER_MARKET)) {
					return CommonImages.INSTANCE.ic_folder_market().getSafeUri().asString();
				}
				else if (folder.getTreeType().equals(AklaboxConstant.FOLDER_MARKET_FINAL)) {
					return CommonImages.INSTANCE.ic_folder_market_complete().getSafeUri().asString();
				}
				else {
					if (folder.getBackgroundImage() != null && !folder.getBackgroundImage().isEmpty()) {
						return PathHelper.getRightPath(folder.getBackgroundImage());
					}
					else {
						return CommonImages.INSTANCE.folder_16().getSafeUri().asString();
					}
				}
			}
			else if (item instanceof Documents) {
				return ImageHelper.findResource(item, true, docUtils).getSafeUri().asString();
			}
		}
		return CommonImages.INSTANCE.object().getSafeUri().asString();
	}

	public void setSelected(boolean selected) {
		if (!selected) {
			removeStyleName(style.selected());
		}
		else {
			addStyleName(style.selected());
		}
	}

	@Override
	public HandlerRegistration addContextMenuHandler(ContextMenuHandler handler) {
		return manager.addHandler(ContextMenuEvent.getType(), handler);
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		manager.fireEvent(event);
		super.fireEvent(event);
	}

	@Override
	public void onDragStart(DragStartEvent event) {
		event.stopPropagation();

		event.setData(TREE_OBJECT_WIDGET, "true");
		
/*		if(docsManager != null && docsManager.getSelectedItems() != null && !docsManager.getSelectedItems().isEmpty()){
			String data = "";
			for(IObject it : docsManager.getSelectedItems()){
				if (item instanceof Documents) {
					data += String.valueOf(((Documents) it).getId()) + ";" + ItemType.DOCUMENT.toString() + "__";
				}
				else if (item instanceof Tree) {
					data += String.valueOf(((Tree) it).getId()) + ";" + ItemType.FOLDER.toString() + "__";
				}
				data = data.substring(0, data.length()-2);
				event.setData(DragAndDropHelper.DATA, data);
			}
		} else {
*/			if (item instanceof Documents) {
				event.setData(DragAndDropHelper.DATA, String.valueOf(((Documents) item).getId()) + ";" + ItemType.DOCUMENT.toString());
//				event.setData(DragAndDropHelper.DATA_ID, String.valueOf(((Documents) item).getId()));
//				event.setData(DragAndDropHelper.DATA_TYPE, ItemType.DOCUMENT.toString());
			}
			else if (item instanceof Tree) {
				event.setData(DragAndDropHelper.DATA, String.valueOf(((Tree) getItem()).getId()) + ";" + ItemType.FOLDER.toString());
//				event.setData(DragAndDropHelper.DATA_ID, String.valueOf(((Tree) item).getId()));
//				event.setData(DragAndDropHelper.DATA_TYPE, ItemType.FOLDER.toString());
			}
/*		}*/
		
		
		
		event.getDataTransfer().setDragImage(getElement(), 10, 10);
		//event.preventDefault();
		dragListener.setDragItem(item);
	}

	@Override
	public void onDragOver(DragOverEvent event) {
		addStyleName(style.dragOver());
	}

	@Override
	public void onDragLeave(DragLeaveEvent event) {
		removeStyleName(style.dragOver());
	}

	@Override
	public void onDrop(DropEvent event) {
//		String type = event.getData(DragAndDropHelper.DATA_TYPE);
		String myData = event.getData(DragAndDropHelper.DATA);
		if (dropManager != null && myData != null && !myData.isEmpty()) {
			for(String data: myData.split("__")){
				String[] infos = data.split(";");
				//event.setData(DragAndDropHelper.DATA, "");
				event.stopPropagation();event.preventDefault();
				ItemType itemType = ItemType.DOCUMENT;
				if (infos[1].equalsIgnoreCase(ItemType.FOLDER.toString())) {
					itemType = ItemType.FOLDER;
				}
				
	//			int itemId = Integer.parseInt(event.getData(DragAndDropHelper.DATA_ID));
				int itemId = Integer.parseInt(infos[0]);
				dropManager.onDrop(itemId, itemType, ((Tree) item).getId());
			}
		}
	}

	@Override
	public void onDragEnd(DragEndEvent event) {
		event.stopPropagation();
		//event.setData(DragAndDropHelper.DATA, "");
		dragListener.removeDragItem();
	}

	public interface IDragListener {

		public Object getDragItem();

		public void setDragItem(Object item);

		public void removeDragItem();
	}

	public interface IDropManager {

		public void onDrop(int itemId, ItemType type, int parentId);
	}
}
