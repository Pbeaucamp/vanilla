package bpm.fwr.client.draggable.widgets;

import bpm.fwr.client.images.WysiwygImage;
import bpm.fwr.shared.models.metadata.FwrBusinessTable;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

public class DraggableTable extends HTML {
	
	private FwrBusinessTable table;

	public DraggableTable(FwrBusinessTable table, String defaultLanguage) {
		super(new Image(WysiwygImage.INSTANCE.object()) + " " + table.getTitles().get(defaultLanguage));
		this.setTable(table);
	}

	public FwrBusinessTable getTable() {
		return table;
	}

	public void setTable(FwrBusinessTable table) {
		this.table = table;
	}
}
