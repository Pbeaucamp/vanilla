package bpm.fwr.client.draggable.widgets;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.client.images.WysiwygImage;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

public class DraggableColumn extends HTML{
	
	private Column column;
	
	public DraggableColumn(Column column, String selectedLanguage, String postFix) {
		super(new Image(WysiwygImage.INSTANCE.column()) + " " + column.getTitle(selectedLanguage) + postFix);
		this.setTitle(column.getDescription());
		this.setColumn(column);
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public Column getColumn() {
		return column;
	}
}
