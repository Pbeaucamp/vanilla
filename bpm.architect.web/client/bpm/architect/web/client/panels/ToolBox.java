package bpm.architect.web.client.panels;

import bpm.architect.web.client.utils.ToolHelper;
import bpm.vanilla.platform.core.beans.forms.FormField.TypeField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ToolBox extends Composite {
	
	private static ToolBoxUiBinder uiBinder = GWT.create(ToolBoxUiBinder.class);

	interface ToolBoxUiBinder extends UiBinder<Widget, ToolBox> {
	}
	
	interface MyStyle extends CssResource  {
		String imgTool();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	FocusPanel tool;
	
	@UiField
	Image imgTool;
	
	@UiField
	Label lblTool;
	
	private TypeField type;

	public ToolBox(TypeField type) {
		initWidget(uiBinder.createAndBindUi(this));
		this.type = type;
		
		imgTool.setResource(ToolHelper.getImage(type));
		imgTool.addStyleName(style.imgTool());
		lblTool.setText(ToolHelper.getLabel(type));
		
		tool.getElement().setDraggable(Element.DRAGGABLE_TRUE);
		tool.addDragStartHandler(dragStartHandler);
	}
	
	private DragStartHandler dragStartHandler = new DragStartHandler() {
		
		@Override
		public void onDragStart(DragStartEvent event) {
			event.setData("FieldType", String.valueOf(type.getType()));
			event.getDataTransfer().setDragImage(getElement(), 10, 10);
		}
	};

}
