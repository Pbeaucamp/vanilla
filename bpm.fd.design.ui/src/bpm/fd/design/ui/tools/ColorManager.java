package bpm.fd.design.ui.tools;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorManager {
	public static final String COLOR_TABLE_BORDER = "bpm.fd.design.ui.gef.figures.TableFigure.border"; //$NON-NLS-1$
	public static final String COLOR_ROW_BORDER = "bpm.fd.design.ui.gef.figures.RowFigure.border"; //$NON-NLS-1$
	public static final String COLOR_CELL_BORDER = "bpm.fd.design.ui.gef.figures.CellFigure.border"; //$NON-NLS-1$
	public static final String COLOR_STACKABLECELL_BORDER = "bpm.fd.design.ui.gef.figures.StackableCellFigure.border"; //$NON-NLS-1$
	public static final String COLOR_HOVER_BORDER = "bpm.fd.design.ui.gef.figures.hover.border"; //$NON-NLS-1$
	public static final String COLOR_WRONG_VALUE_FIELD = "bpm.fd.design.ui.text.errorcontent"; //$NON-NLS-1$
	public static final String EVENT_USED = "bpm.fd.design.ui.eventused"; //$NON-NLS-1$
	public static final String COLOR_DRILL_DRIVEN_CELL_BORDER = "bpm.fd.design.ui.gef.figures.DrillDrivenCellFigure.border";
	
	
	private static ColorManager instance = null;
	
	private ColorRegistry colorRegistry;
	
	private ColorManager(){
		colorRegistry = new ColorRegistry(Display.getDefault());
		colorRegistry.put(COLOR_HOVER_BORDER, new RGB(120, 50, 70));
		colorRegistry.put(COLOR_WRONG_VALUE_FIELD, new RGB(200, 20, 20));
		colorRegistry.put(EVENT_USED, new RGB(239, 118, 236));
	}
	
	private static ColorManager getInstance(){
		if (instance == null){
			instance = new ColorManager();
		}
		return instance;
	}
	
	
	public static ColorRegistry getColorRegistry(){
		return getInstance().colorRegistry;
	}
}
