package bpm.faweb.client.projection.data;

import java.util.List;

import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * A dataGrid with methods to get the scrollPanel and add a scrollHandler
 * @author Marc Lanquetin
 *
 */
public class DataGridWithScrollEvent extends DataGrid<List<DataField>> {

	public DataGridWithScrollEvent(int i) {
		super(i);
	}

	public ScrollPanel getScrollPanel() {
        HeaderPanel header = (HeaderPanel) getWidget();
        return (ScrollPanel) header.getContentWidget();
	}
	
	public void addScrollHandler(ScrollHandler handler) {
		getScrollPanel().addScrollHandler(handler);
	}
}
