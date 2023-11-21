package bpm.fd.design.ui.properties.model.chart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.RGB;

import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;

public class PropertyColor extends Property{
	private Color color;
	private DataAggregation agg;
	private int colorIndex;
	
	public static List<CellEditor> colorPickers;
	
	public PropertyColor(int colorIndex, DataAggregation agg, String color, CellEditor cellEditor) {
		super(Messages.PropertyColor_0, cellEditor);
		try {
			if(colorPickers == null) {
				colorPickers = new ArrayList<CellEditor>();
			}
			colorPickers.add(cellEditor);
		} catch(Exception e) {}
		this.color = stringToColor(color);
		this.colorIndex = colorIndex;
		this.agg = agg;
		
	}
	
	public DataAggregation getDataAggregation(){
		return agg;
	}
	
	public int getIndex(){
		return colorIndex;
	}
	public Color getColor(){
		return color;
	}
	
	public void setColor(Color color){
		this.color = color;
		
		agg.setColorCode(colorToString(color), colorIndex);
	}

	public static Color stringToColor(String color){
		int r = Integer.parseInt(color.substring(0, 2), 16);
		int g = Integer.parseInt(color.substring(2, 4), 16);
		int b = Integer.parseInt(color.substring(4, 6), 16);
		
		return  new Color( r, g, b);
	}
	private static String colorToString(Color color){
		String r = Integer.toHexString(color.getRed());
		if (r.length() == 1){
			r = "0" + r; //$NON-NLS-1$
		}
		String b = Integer.toHexString(color.getBlue());
		if (b.length() == 1){
			b = "0" + b; //$NON-NLS-1$
		}
		String g = Integer.toHexString(color.getGreen());
		if (g.length() == 1){
			g = "0" + g; //$NON-NLS-1$
		}
		String s = r + g + b;
		return s;
	}

	public void setColor(RGB value) {
		try {
			setColor(new Color(value.red, value.green, value.blue));
		} catch (Exception e) {
			
		}
		
	}
	
	
}
