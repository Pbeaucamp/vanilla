package bpm.fwr.client.draggable.dropcontrollers;

import bpm.fwr.client.widgets.TextAreaWidget;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.ui.Widget;

public class VariableDropController extends SimpleDropController {
	private static final String CSS_ENGAGE = "textAreaEngage";

	private TextAreaWidget textArea;
	
	public VariableDropController(TextAreaWidget textArea) {
		super(textArea);
		this.textArea = textArea;
	}

	@Override
	public void onDrop(DragContext context) {
		for (Widget widget : context.selectedWidgets) {
			textArea.manageWidget(widget);
		}
		super.onDrop(context);
	}
	
	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		textArea.addStyleName(CSS_ENGAGE);
	}
	
	@Override
	public void onLeave(DragContext context) {
		textArea.removeStyleName(CSS_ENGAGE);
	    super.onLeave(context);
	}
}
