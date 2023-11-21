package bpm.fd.design.ui.gef.figures;

import org.eclipse.swt.graphics.RGB;

import bpm.fd.design.ui.tools.ColorManager;

public class RowFigure extends AbstractBaseFigure implements HoverableFigure{
		
	
	static{
		ColorManager.getColorRegistry().put(ColorManager.COLOR_ROW_BORDER, new RGB(255, 0, 0));
	}
	
	public RowFigure(){
		super(ColorManager.COLOR_ROW_BORDER);
	
	}
	
	

	public void unhighlightBorder() {
		getOuterBorder().setWidth(1);
		getOuterBorder().setColor(ColorManager.getColorRegistry().get(ColorManager.COLOR_ROW_BORDER));
		
	}

}
