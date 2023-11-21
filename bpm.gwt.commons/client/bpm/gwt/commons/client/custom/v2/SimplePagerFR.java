package bpm.gwt.commons.client.custom.v2;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.Range;

import bpm.gwt.commons.client.I18N.LabelsConstants;

public class SimplePagerFR extends SimplePager {

	public SimplePagerFR(TextLocation location, Resources resources,
		      boolean showFastForwardButton, final int fastForwardRows,
		      boolean showLastPageButton) {
		super(location, resources, showFastForwardButton, fastForwardRows, showLastPageButton);
	}

	@Override
	protected String createText() {
	    // Default text is 1 based.
	    NumberFormat formatter = NumberFormat.getFormat("#,###");
	    HasRows display = getDisplay();
	    Range range = display.getVisibleRange();
	    int pageStart = range.getStart() + 1;
	    int pageSize = range.getLength();
	    int dataSize = display.getRowCount();
	    int endIndex = Math.min(dataSize, pageStart + pageSize - 1);
	    endIndex = Math.max(pageStart, endIndex);
	    boolean exact = display.isRowCountExact();
	    return formatter.format(pageStart) + "-" + formatter.format(endIndex)
	        + (exact ? " " + LabelsConstants.lblCnst.of() + " " : " " + LabelsConstants.lblCnst.inOver() + " ") + formatter.format(dataSize);
	}
	
	@Override
	public void setPageStart(int index) {
		if (this.getDisplay() != null) {
			Range range = this.getDisplay().getVisibleRange();
			int pageSize = range.getLength();
			// if (isRangeLimited && display.isRowCountExact()) {
			// index = Math.min(index, display.getRowCount() - pageSize);
			// }
			index = Math.max(0, index);
			if (index != range.getStart()) {
				this.getDisplay().setVisibleRange(index, pageSize);
			}
		}
	}
}
