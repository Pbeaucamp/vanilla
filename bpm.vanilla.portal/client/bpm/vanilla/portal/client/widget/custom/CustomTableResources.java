package bpm.vanilla.portal.client.widget.custom;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.portal.client.images.PortalImage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTable.Style;

public class CustomTableResources implements CellTable.Resources{
	
    private final CustomStyle style;

    public CustomTableResources() {
    	CellTable.Resources resource = GWT.create(CellTable.Resources.class);
    	this.style = new CustomStyle(resource.cellTableStyle());
    }

	@Override
	public ImageResource cellTableFooterBackground() {
		return PortalImage.INSTANCE.cellBackground();
	}

	@Override
	public ImageResource cellTableHeaderBackground() {
		return PortalImage.INSTANCE.cellTableHeaderBackground();
	}

	@Override
	public ImageResource cellTableLoading() {
		return PortalImage.INSTANCE.cellTableLoading();
	}

	@Override
	public ImageResource cellTableSelectedBackground() {
		return PortalImage.INSTANCE.cellBackground();
	}

	@Override
	public ImageResource cellTableSortAscending() {
		return PortalImage.INSTANCE.sortAscending();
	}

	@Override
	public ImageResource cellTableSortDescending() {
		return PortalImage.INSTANCE.sortDescending();
	}

	@Override
	public Style cellTableStyle() {
		return style;
	}

	/**
	 * A ClientBundle that provides images for this widget.
	 */
	public interface Resources extends ClientBundle {

	    /**
	     * The styles used in this widget.
	     */
	    @Source("CustomTable.css")
	    Style cellTableStyle();

		ImageResource cellListSelectedBackground();
	}
	
	private class CustomStyle implements CellTable.Style {
		
		private final CellTable.Style style;

	    public CustomStyle(CellTable.Style style) {
	    	this.style = style;
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

		@Override
		public String cellTableCell() {
			return style.cellTableCell();
		}

		@Override
		public String cellTableEvenRow() {
			return style.cellTableEvenRow();
		}

		@Override
		public String cellTableEvenRowCell() {
			return style.cellTableEvenRowCell();
		}

		@Override
		public String cellTableFirstColumn() {
			return style.cellTableFirstColumn();
		}

		@Override
		public String cellTableFirstColumnFooter() {
			return style.cellTableFirstColumnFooter();
		}

		@Override
		public String cellTableFirstColumnHeader() {
			return style.cellTableFirstColumnHeader();
		}

		@Override
		public String cellTableFooter() {
			return style.cellTableFooter();
		}

		@Override
		public String cellTableHeader() {
			return style.cellTableHeader();
		}

		@Override
		public String cellTableHoveredRow() {
			return style.cellTableHoveredRow();
		}

		@Override
		public String cellTableHoveredRowCell() {
			return style.cellTableHoveredRowCell();
		}

		@Override
		public String cellTableKeyboardSelectedCell() {
			return style.cellTableKeyboardSelectedCell();
		}

		@Override
		public String cellTableKeyboardSelectedRow() {
			return style.cellTableKeyboardSelectedRow();
		}

		@Override
		public String cellTableKeyboardSelectedRowCell() {
			return style.cellTableKeyboardSelectedRowCell();
		}

		@Override
		public String cellTableLastColumn() {
			return style.cellTableLastColumn();
		}

		@Override
		public String cellTableLastColumnFooter() {
			return style.cellTableLastColumnFooter();
		}

		@Override
		public String cellTableLastColumnHeader() {
			return style.cellTableLastColumnHeader();
		}

		@Override
		public String cellTableLoading() {
			return style.cellTableLoading();
		}

		@Override
		public String cellTableOddRow() {
			return VanillaCSS.DATA_GRID_ODD_ROW;
		}

		@Override
		public String cellTableOddRowCell() {
			return style.cellTableOddRowCell();
		}

		@Override
		public String cellTableSelectedRow() {
			return VanillaCSS.DATA_GRID_SELECTED_ROW;
		}

		@Override
		public String cellTableSelectedRowCell() {
			return style.cellTableSelectedRowCell();
		}

		@Override
		public String cellTableSortableHeader() {
			return style.cellTableSortableHeader();
		}

		@Override
		public String cellTableSortedHeaderAscending() {
			return style.cellTableSortedHeaderAscending();
		}

		@Override
		public String cellTableSortedHeaderDescending() {
			return style.cellTableSortedHeaderDescending();
		}

		@Override
		public String cellTableWidget() {
			return style.cellTableWidget();
		}
	}

}
