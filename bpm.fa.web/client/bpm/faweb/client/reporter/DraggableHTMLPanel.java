package bpm.faweb.client.reporter;

import com.google.gwt.user.client.ui.HTMLPanel;

public class DraggableHTMLPanel extends HTMLPanel {

	private DraggableColumnHTML column;
	
	public DraggableHTMLPanel(String html, DraggableColumnHTML column) {
		super(html);
		this.setColumn(column);
	}

	public void setColumn(DraggableColumnHTML column) {
		this.column = column;
	}

	public DraggableColumnHTML getColumn() {
		return column;
	}

}
