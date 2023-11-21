package bpm.gwt.workflow.commons.client.workflow;

import bpm.gwt.workflow.commons.client.IWorkflowAppManager;
import bpm.gwt.workflow.commons.shared.Constants;
import bpm.workflow.commons.beans.TypeActivity;

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
	
	private TypeActivity type;

	public ToolBox(IWorkflowAppManager appManager, TypeActivity type) {
		initWidget(uiBinder.createAndBindUi(this));
		this.type = type;
		
		imgTool.setResource(appManager.getImage(type, true));
		imgTool.addStyleName(style.imgTool());
		lblTool.setText(appManager.getLabel(type));
		
		tool.getElement().setDraggable(Element.DRAGGABLE_TRUE);
		tool.addDragStartHandler(dragStartHandler);
	}
	
	private DragStartHandler dragStartHandler = new DragStartHandler() {
		
		@Override
		public void onDragStart(DragStartEvent event) {
			event.setData(Constants.TYPE_TOOL, String.valueOf(type.getType()));
			event.getDataTransfer().setDragImage(getElement(), 10, 10);
		}
	};

}
