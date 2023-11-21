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

public class ColorPalette extends Composite implements TableListener
{
	private float hue;
	private float saturation;
	private float value;
	
	private Grid grid = new Grid(16, 16);
	
	private ChangeListenerCollection listeners = new ChangeListenerCollection();
	
	public ColorPalette()
	{
		super();
		initWidget(grid);
		grid.setCellPadding(0);
		grid.setCellSpacing(0);
		grid.addStyleName("agilar-colorpicker-popup-palette");
		setHue(0.2f);
		
		grid.addTableListener(this);
	}
	
	public float getHue()
	{
		return hue;
		
	}
	public void setHue(float hue)
	{
		this.hue = hue;
		float [] hsv = new float[]{hue, 0.0f, 0.0f};
		int [] irgb = new int[3];
		float [] rgb = new float[3];
		
		CellFormatter formatter = grid.getCellFormatter();
		for (int row=0; row<16; row++)
		{
			hsv[2] = 1.0f - (float)row / 15.0f;
			for (int col=0; col<16; col++)
			{
				hsv[1] = (float)col / 15.0f;
				Color.HSVToRGB(hsv, rgb);
				Color.toInt(rgb, irgb);
				formatter.setWidth(row, col, "6.25%");
				formatter.setHeight(row, col, "6.25%");
				DOM.setStyleAttribute(formatter.getElement(row, col), "backgroundColor", "#"+Color.toHex(irgb));
				grid.setText(row, col, "");
			}
		}
	}

	public void onCellClicked(SourcesTableEvents table, int row, int col)
	{
		setSaturationAndValue((float)col / 15.0f, 1.0f - (float)row / 15.0f);
		listeners.fireChange(this);
	}
	
	public float getSaturation()
	{
		return saturation;
	}

	public float getValue()
	{
		return value;
	}

	public void setSaturationAndValue(float saturation, float value)
	{
		// deselect old selected cell
		int col = Math.round(this.saturation * 15.0f);
		int row = Math.round((1.0f - this.value) * 15.0f);

		CellFormatter formatter = grid.getCellFormatter();
		formatter.removeStyleName(row, col, "agilar-colorpicker-popup-palette-selected");

		this.saturation = saturation;
		this.value = value;
		
		col = Math.round(this.saturation * 15.0f);
		row = Math.round((1.0f - this.value) * 15.0f);
		formatter.addStyleName(row, col, "agilar-colorpicker-popup-palette-selected");
		// select new cell
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
