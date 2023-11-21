package bpm.gwt.commons.client.viewer.widget;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public interface IListParameter {
	
	public String getName();
	
	public CustomListBoxWithWait getCustomListBox();
	
	public ListBox getListBox();
	
	public Widget getWidget();
}
