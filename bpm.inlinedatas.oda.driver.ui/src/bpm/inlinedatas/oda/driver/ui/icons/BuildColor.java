package bpm.inlinedatas.oda.driver.ui.icons;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public abstract class BuildColor {
	
	public static Color getNewColor(int r, int g, int b){
		
		return new Color(Display.getDefault(), new RGB(r,g,b));
		
	}

}
