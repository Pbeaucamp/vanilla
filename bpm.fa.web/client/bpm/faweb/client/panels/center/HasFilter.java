package bpm.faweb.client.panels.center;

import java.util.List;

import bpm.faweb.client.utils.FaWebFilterHTML;
import bpm.faweb.shared.infoscube.GridCube;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public interface HasFilter {

	public List<FaWebFilterHTML> getFilters();
	
	public void addFilterItem(String filter);
	
	public boolean charging();
	
	public void trashFilter(Widget widget);
	
	public void addRow(String row);
	
	public void addCol(String col);

	public void addRowCol(AbsolutePanel dropTarget, String uname);

	public void setRowsCols(GridCube gc);
}
