package bpm.fwr.client.draggable.widgets;

import bpm.fwr.client.dialogs.OptionsColumnPopupPanel;
import bpm.fwr.client.utils.ColumnType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTMLPanel;

public class DraggableHTMLPanel extends HTMLPanel {

	private DraggableColumnHTML column;
	public DraggableHTMLPanel(String html, DraggableColumnHTML column) {
		super(html);
		this.setColumn(column);
		this.column.addClickHandlerToMenuOptions(menuOptionsClickHandler);
	}

	public void setColumn(DraggableColumnHTML column) {
		this.column = column;
	}

	public DraggableColumnHTML getColumn() {
		return column;
	}

	private ClickHandler menuOptionsClickHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			int x = event.getClientX();
			int y = event.getClientY();
			
			boolean isGroup = false;
			if(column.getColumnType() == ColumnType.GROUP){
				isGroup = true;
			}
			  
			OptionsColumnPopupPanel menuPopup = new OptionsColumnPopupPanel(true, true, DraggableHTMLPanel. this, isGroup);
			menuPopup.setPopupPosition(x, y);
			menuPopup.show();
		}
	};

}
