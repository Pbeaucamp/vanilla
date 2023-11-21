/*
	Copyright 2008 Marco Mustapic
	
    This file is part of Agilar GWT Widgets.

    Agilar GWT Widgets is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Agilar GWT Widgets is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Agilar GWT Widgets.  If not, see <http://www.gnu.org/licenses/>.
*/

package bpm.map.viewer.web.client.utils;


import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ChangeListenerCollection;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;

public class HueSelector extends Composite implements TableListener
{
	private Grid grid = new Grid(18, 1);
	private float hue;
	
	private ChangeListenerCollection listeners = new ChangeListenerCollection();
	
	public HueSelector()
	{
		super();
		initWidget(grid);
		grid.setCellPadding(0);
		grid.setCellSpacing(0);
		grid.addStyleName("agilar-colorpicker-popup-hueselector");
		
		grid.addTableListener(this);
				
		buildUI();
		setHue(0);
	}
	
	private void buildUI()
	{
		float [] hsv = new float[]{0.0f, 1.0f, 1.0f};
		float [] rgb = new float[3];
		int [] irgb = new int[3];
		CellFormatter formatter = grid.getCellFormatter();
		for (int row=0; row<18; row++)
		{
			Color.HSVToRGB(hsv, rgb);
			Color.toInt(rgb, irgb);
			DOM.setStyleAttribute(formatter.getElement(row, 0), "backgroundColor", "#"+Color.toHex(irgb));
			formatter.setHeight(row, 0, "5.555%");
			formatter.setWidth(row, 0, "100%");
			grid.setText(row, 0, "");
			hsv[0] = (float)row / 18.0f * 360.0f;
		}
	}

	public void onCellClicked(SourcesTableEvents table, int row, int col)
	{
		setHue((float)row / 18.0f * 360.0f);
		listeners.fireChange(this);
	}

	public void setHue(float hue)
	{
		int row = (int)(this.hue * 18.0f / 360.0f);
		CellFormatter formatter = grid.getCellFormatter();
		formatter.removeStyleName(row, 0, "agilar-colorpicker-popup-hueselector-selected");
		this.hue = hue;
		row = (int)(this.hue * 18.0f / 360.0f);
		formatter.addStyleName(row, 0, "agilar-colorpicker-popup-hueselector-selected");
	}

	public float getHue()
	{
		return hue;
	}
	
	public void addChangeListener(ChangeListener listener)
	{
		listeners.add(listener);
	}

	public void removeTableListener(ChangeListener listener)
	{
		listeners.remove(listener);
	}
}
