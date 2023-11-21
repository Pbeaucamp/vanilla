package bpm.fd.design.ui.gef.figures;

import org.eclipse.swt.graphics.RGB;

import bpm.fd.design.ui.tools.ColorManager;

public class DrillDrivenCellFigure extends CellFigure{
	static{
		ColorManager.getColorRegistry().put(ColorManager.COLOR_DRILL_DRIVEN_CELL_BORDER, new RGB(150, 0, 200));
	}
	public DrillDrivenCellFigure(boolean isEmpty) {
		super(isEmpty);
		getOuterBorder().setColor(ColorManager.getColorRegistry().get(ColorManager.COLOR_DRILL_DRIVEN_CELL_BORDER));
	}
	public void unhighlightBorder() {
		getOuterBorder().setWidth(1);
		getOuterBorder().setColor(ColorManager.getColorRegistry().get(ColorManager.COLOR_DRILL_DRIVEN_CELL_BORDER));
	}
}
