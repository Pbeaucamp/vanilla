package bpm.gwt.aklabox.commons.client.utils;

import bpm.gwt.aklabox.commons.client.images.CommonImages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Resources;
import com.google.gwt.user.cellview.client.DataGrid.Style;

public class CustomResources implements Resources{
	
    private final CustomStyle style;

    public CustomResources() {
    	Resources resource = GWT.create(Resources.class);
    	this.style = new CustomStyle(resource.dataGridStyle());
    }

	@Override
	public ImageResource dataGridLoading() {
		return CommonImages.INSTANCE.cellTableLoading();
	}

	@Override
	public ImageResource dataGridSortAscending() {
		return CommonImages.INSTANCE.sortAscending();
	}

	@Override
	public ImageResource dataGridSortDescending() {
		return CommonImages.INSTANCE.sortDescending();
	}

	@Override
	public Style dataGridStyle() {
		return style;
	}

	/**
	 * A ClientBundle that provides images for this widget.
	 */
	public interface Resources extends ClientBundle {

	    /**
	     * The styles used in this widget.
	     */
	    @Source("CustomDataGrid.css")
	    Style dataGridStyle();
	}
	
	private class CustomStyle implements Style {
		
		private final DataGrid.Style style;

	    public CustomStyle(DataGrid.Style style) {
	    	this.style = style;
	    }
		
		@Override
		public String dataGridCell() {
			return style.dataGridCell();
		}

		@Override
		public String dataGridEvenRow() {
			return style.dataGridEvenRow();
		}

		@Override
		public String dataGridEvenRowCell() {
			return style.dataGridEvenRowCell();
		}

		@Override
		public String dataGridFirstColumn() {
			return style.dataGridFirstColumn();
		}

		@Override
		public String dataGridFirstColumnFooter() {
			return style.dataGridFirstColumnFooter();
		}

		@Override
		public String dataGridFirstColumnHeader() {
			return style.dataGridFirstColumnHeader();
		}

		@Override
		public String dataGridFooter() {
			return style.dataGridFooter();
		}

		@Override
		public String dataGridHeader() {
			return style.dataGridHeader();
		}

		@Override
		public String dataGridHoveredRow() {
			return style.dataGridHoveredRow();
		}

		@Override
		public String dataGridHoveredRowCell() {
			return style.dataGridHoveredRowCell();
		}

		@Override
		public String dataGridKeyboardSelectedCell() {
			return "keyboard";
		}

		@Override
		public String dataGridKeyboardSelectedRow() {
			return style.dataGridKeyboardSelectedRow();
		}

		@Override
		public String dataGridKeyboardSelectedRowCell() {
			return style.dataGridKeyboardSelectedRowCell();
		}

		@Override
		public String dataGridLastColumn() {
			return style.dataGridLastColumn();
		}

		@Override
		public String dataGridLastColumnFooter() {
			return style.dataGridLastColumnFooter();
		}

		@Override
		public String dataGridLastColumnHeader() {
			return style.dataGridLastColumnHeader();
		}

		@Override
		public String dataGridOddRow() {
//			return VanillaCSS.DATA_GRID_ODD_ROW;
			return "";
		}

		@Override
		public String dataGridOddRowCell() {
//			return VanillaCSS.DATA_GRID_ODD_ROW_CELL;
			return "";
		}

		@Override
		public String dataGridSelectedRow() {
//			return VanillaCSS.DATA_GRID_SELECTED_ROW;
			return "";
		}

		@Override
		public String dataGridSelectedRowCell() {
//			return VanillaCSS.DATA_GRID_SELECTED_ROW_CELL;
			return "";
		}

		@Override
		public String dataGridSortableHeader() {
			return style.dataGridSortableHeader();
		}

		@Override
		public String dataGridSortedHeaderAscending() {
			return style.dataGridSortedHeaderAscending();
		}

		@Override
		public String dataGridSortedHeaderDescending() {
			return style.dataGridSortedHeaderDescending();
		}

		@Override
		public String dataGridWidget() {
			return style.dataGridWidget();
		}

		@Override
		public boolean ensureInjected() {
			return style.ensureInjected();
		}

		@Override
		public String getText() {
			return style.getText();
		}

		@Override
		public String getName() {
			return style.getName();
		}
	}

}
