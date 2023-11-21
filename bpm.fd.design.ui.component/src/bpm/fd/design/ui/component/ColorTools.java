package bpm.fd.design.ui.component;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class ColorTools {
	private static ColorRegistry colorRegistry = new ColorRegistry();
	public static final String COLOR_ERROR = "bpm.fd.design.ui.component.color.error"; //$NON-NLS-1$
	
	static{
		colorRegistry.put(COLOR_ERROR, new RGB(220, 12, 20));
	}
	
	public static Color getColor(String colorId){
		return colorRegistry.get(COLOR_ERROR);
	}
	
}
