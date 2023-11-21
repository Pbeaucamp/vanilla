package bpm.fwr.client.widgets;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.client.draggable.widgets.DraggableColumn;
import bpm.fwr.client.wizard.IManageTextBoxData;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TextBoxWidget extends TextBox{
	
	public enum TextBoxType {
		DETAIL,
		GROUP,
		ID,
		VALUES,
		HYPERLINK
	}
	
	private IManageTextBoxData pageParent;
	private TextBoxType type;
	private Column column;
	
	public TextBoxWidget(IManageTextBoxData pageParent, TextBoxType type) { 
		super();
		this.pageParent = pageParent;
		this.type = type;
	}
	
	public void manageWidget(Widget widget){
		if(widget instanceof DraggableColumn){
			pageParent.manageWidget((DraggableColumn)widget, type);
		}
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public Column getColumn() {
		return column;
	}
}
