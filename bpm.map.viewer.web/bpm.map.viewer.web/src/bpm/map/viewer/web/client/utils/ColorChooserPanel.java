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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ColorChooserPanel extends Composite implements ChangeListener, KeyboardListener
{
	private VerticalPanel panel = new VerticalPanel();
	private ColorPalette palette = new ColorPalette();
	private HueSelector hueSelector = new HueSelector();
	private SimplePanel colorPanel = new SimplePanel();
	private TextBox textColor = new TextBox();
	
	private float [] hsv = new float[3];
	private float [] rgb = new float[3];
	
	public ColorChooserPanel()
	{
		HorizontalPanel hpanel = new HorizontalPanel();
		hpanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		hpanel.add(new HTML("#"));
		hpanel.setVerticalAlignment(HorizontalPanel.ALIGN_TOP);
		hpanel.add(textColor);
		hpanel.add(colorPanel);
		
		panel.add(hpanel);
		
		hpanel = new HorizontalPanel();
		hpanel.add(palette);
		hpanel.add(hueSelector);
		
		palette.addChangeListener(this);
		hueSelector.addChangeListener(this);
		
		panel.add(hpanel);
		
		colorPanel.addStyleName("agilar-colorpicker-popup-colorPanel");
		textColor.addStyleName("agilar-colorpicker-popup-text");
		textColor.addKeyboardListener(this);
		textColor.setMaxLength(6);
		
		initWidget(panel);
		addStyleName("agilar-colorpicker");
		
		setRGB(0xff0000);
	}

	public void onChange(Widget widget)
	{
		if (widget == hueSelector)
		{
			palette.setHue(hueSelector.getHue());
		}
		hsv[0] = palette.getHue();
		hsv[1] = palette.getSaturation();
		hsv[2] = palette.getValue();
		
		Color.HSVToRGB(hsv, rgb);
		int [] irgb = new int[3];
		Color.toInt(rgb, irgb);
        DOM.setStyleAttribute(colorPanel.getElement(), "backgroundColor", "#"+Color.toHex(irgb));
		textColor.setText(Color.toHex(irgb));
	}

	public int getRGB()
	{
		int [] irgb = new int[3];
		Color.toInt(rgb, irgb);
		return irgb[0] << 16 + irgb[1] << 8 + irgb[2];
	}
	
	public void setRGB(int color)
	{
		int [] irgb = new int[3];
		irgb[2] = color & 0xff;
		irgb[1] = (color >> 8) & 0xff;
		irgb[0] = (color >> 16) & 0xff;
		
		Color.toFloat(irgb, rgb);
		Color.RGBToHSV(rgb, hsv);

		// set color in all widgets
		hueSelector.setHue(hsv[0]);
		palette.setHue(hsv[0]);
		palette.setSaturationAndValue(hsv[1], hsv[2]);
        DOM.setStyleAttribute(this.colorPanel.getElement(), "backgroundColor", "#"+Color.toHex(irgb));
		textColor.setText(Color.toHex(irgb));
	}

	public void setColor(String text)
	{
		if (isColor(text))
		{
			setRGB(toInt(text));
		}
	}
	
	public int[] getColor()
	{
		int [] irgb = new int[3];
		Color.toInt(rgb, irgb);
//		return Color.toHex(irgb);
		return irgb;
	}
	
	public String getColorTb() {
		int [] irgb = new int[3];
		Color.toInt(rgb, irgb);
		return Color.toHex(irgb);
	}
	
	private int toInt(String text)
	{
		int num = 0;
		for (int i=0; i<text.length(); i++)
		{
			char c = text.charAt(i);
			num <<= 4;
			num |= Character.digit(c, 16);
		}
		return num;
	}

	private boolean isColor(String text)
	{
		if (text.length() != 6)
		{
			return false;
		}
		for (int i=0; i<text.length(); i++)
		{
			char c = text.charAt(i);
			if (Character.digit(c, 16) == -1)
			{
				return false;
			}
		}
		return true;
	}

	public void onKeyDown(Widget w, char keyCode, int modifiers)
	{
	}

	public void onKeyPress(Widget w, char keyCode, int modifiers)
	{
		
	}

	public void onKeyUp(Widget w, char keyCode, int modifiers)
	{
		String text = textColor.getText();
		if (isColor(text))
		{
			setColor(text);
		}
	}
}
