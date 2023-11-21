package bpm.fwr.client.draggable.dropcontrollers;

import bpm.fwr.client.widgets.TextBoxWidget;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.ui.Widget;

public class TextDropController extends SimpleDropController{
	private TextBoxWidget widget;
	
	public TextDropController(TextBoxWidget widget) {
		super(widget);
		this.widget = widget;
	}

	@Override
	public void onDrop(DragContext context) {
		for(Widget w : context.selectedWidgets) {
			widget.manageWidget(w);
		}
	}
}
