package bpm.fd.design.ui.properties.model.map;

import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.model.PropertyGroup;
import bpm.fd.design.ui.tools.ColorManager;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class PropertyMapColor extends PropertyGroup{
	private ColorRange range;
	
	private Property min,max, color;
	public PropertyMapColor(ColorRange range, Composite parent) {
		super(Messages.PropertyMapColor_0, new TextCellEditor(parent));
		this.range = range;
		
		min = new Property(Messages.PropertyMapColor_1, new TextCellEditor(parent));
		max = new Property(Messages.PropertyMapColor_2, new TextCellEditor(parent));
		color = new Property(Messages.PropertyMapColor_3, new ColorCellEditor(parent));
		add(color);
		add(min);
		add(max);
		
	}

	public Color getColor(Property p){
		if (p != color){
			return null;
		}
		if (ColorManager.getColorRegistry().get(range.getHex()) == null){
			ColorManager.getColorRegistry().put(range.getHex(), getRGB());
		}
		return ColorManager.getColorRegistry().get(range.getHex());
	}
	
	public String getValueString(Property p){
		if (p == min){
			return range.getMin() + ""; //$NON-NLS-1$
		}
		if (p == max){
			return range.getMax()+ ""; //$NON-NLS-1$
		}
		if (p == color){
			return ""; //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}
	
	public Object getValue(Property p){
		if (p == min){
			return range.getMin() + ""; //$NON-NLS-1$
		}
		if (p == max){
			return range.getMax()+ ""; //$NON-NLS-1$
		}
		if (p == color){
			return getRGB();
		}
		return null;
	}
	
	private RGB getRGB(){
		int r = Integer.parseInt(range.getHex().substring(0, 2), 16);
		int g = Integer.parseInt(range.getHex().substring(2, 4), 16);
		int b = Integer.parseInt(range.getHex().substring(4, 6), 16);
		return new RGB(r, g, b);
	}
	
	public void setRangeName(String name){
		this.range.setName(name);
	}
	
	public void setValue(Property p, Object value){
		if (p == min){
			range.setMin(Integer.valueOf((String)value));
		}
		if (p == max){
			range.setMax(Integer.valueOf((String)value));
		}
		if (p == color){
			String r = Integer.toHexString(((RGB)value).red);
			if (r.length() == 1){
				r = "0" + r; //$NON-NLS-1$
			}
			String b = Integer.toHexString(((RGB)value).blue);
			if (b.length() == 1){
				b = "0" + b; //$NON-NLS-1$
			}
			String g = Integer.toHexString(((RGB)value).green);
			if (g.length() == 1){
				g = "0" + g; //$NON-NLS-1$
			}
			
			
			String s = r + g + b;
			range.setHex(s);
		}
	}

	public ColorRange getRange() {

		return range;
	}
	
}
