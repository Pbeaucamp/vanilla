package bpm.fwr.client.widgets;

import bpm.fwr.client.draggable.widgets.DraggableVariableHTML;

import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class TextAreaWidget extends TextArea{
	
	public enum TextAreaType {
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT
	}
	
	private TextAreaType type;
	
	public TextAreaWidget(TextAreaType type) { 
		super();
		this.setType(type);
	}
	
	public void manageWidget(Widget widget){
		if(widget instanceof DraggableVariableHTML){
			String textContent = getText();
			textContent += ((DraggableVariableHTML)widget).getType().getValue();
			setText(textContent);
		}
	}

	public void setType(TextAreaType type) {
		this.type = type;
	}

	public TextAreaType getType() {
		return type;
	}
}
