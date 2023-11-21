package bpm.gwt.commons.client.tree;

import bpm.gwt.commons.client.images.CommonImages;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.IDatabaseObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
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

public class DatabaseObjectWidget extends Composite implements DragStartHandler {

	private static DatabaseObjectWidgetUiBinder uiBinder = GWT.create(DatabaseObjectWidgetUiBinder.class);

	interface DatabaseObjectWidgetUiBinder extends UiBinder<Widget, DatabaseObjectWidget> {
	}

	interface MyStyle extends CssResource {
		String imgObject();
	}

	@UiField
	FocusPanel dragPanel;

	@UiField
	Image imgObject;

	@UiField
	Label lblObject;

	@UiField
	MyStyle style;

	private IDatabaseObject databaseObject;
	
	public DatabaseObjectWidget(IDatabaseObject databaseObject, boolean dragAndDrop) {
		initWidget(uiBinder.createAndBindUi(this));
		this.databaseObject = databaseObject;

		imgObject.setResource(findResource(databaseObject));
		imgObject.addStyleName(style.imgObject());

		lblObject.setText(databaseObject.getCustomName());

		if (databaseObject instanceof DatabaseTable && dragAndDrop) {
			getElement().setDraggable(Element.DRAGGABLE_TRUE);
			dragPanel.addDragStartHandler(this);
		}
	}

	public IDatabaseObject getDatabaseObject() {
		return databaseObject;
	}

	private ImageResource findResource(IDatabaseObject databaseObject) {
		if (databaseObject instanceof DatabaseTable) {
			return CommonImages.INSTANCE.folder();
		}
		else if (databaseObject instanceof DatabaseColumn) {
			return CommonImages.INSTANCE.object();
		}

		return CommonImages.INSTANCE.object();
	}

	@Override
	public void onDragStart(DragStartEvent event) {
		event.setData("name", databaseObject.getName());
		event.getDataTransfer().setDragImage(getElement(), 10, 10);
	}
}
