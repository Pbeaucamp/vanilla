package bpm.fd.web.client.panels;

import bpm.fd.core.component.ComponentType;
import bpm.fd.web.client.utils.ToolHelper;

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
	
	private ComponentType type;

	public ToolBox(ComponentType type) {
		initWidget(uiBinder.createAndBindUi(this));
		this.type = type;
		
		imgTool.setResource(ToolHelper.getImage(type));
		imgTool.addStyleName(style.imgTool());
		
		String toolName = ToolHelper.getLabel(type);
		lblTool.setText(toolName);
		lblTool.setTitle(toolName);
		
		tool.getElement().setDraggable(Element.DRAGGABLE_TRUE);
		tool.addDragStartHandler(dragStartHandler);
	}
	
	private DragStartHandler dragStartHandler = new DragStartHandler() {
		
		@Override
		public void onDragStart(DragStartEvent event) {
			event.setData(ToolHelper.TYPE_TOOL, String.valueOf(type.getType()));
			event.getDataTransfer().setDragImage(getElement(), 10, 10);
		}
	};

}
