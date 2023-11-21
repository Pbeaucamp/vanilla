package bpm.fd.design.ui.gef.figures;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.icons.Icons;
import bpm.fd.design.ui.tools.ColorManager;

public class TableFigure extends AbstractBaseFigure {
	
	
	
	static{
		ColorManager.getColorRegistry().put(ColorManager.COLOR_TABLE_BORDER, new RGB(0, 0, 255));
	}
	
	private ImageFigure events;
	public TableFigure(){
		super(ColorManager.COLOR_TABLE_BORDER);
		Image im = Activator.getDefault().getImageRegistry().get(Icons.empty_cell);
		
		events = new ImageFigure(Activator.getDefault().getImageRegistry().get(Icons.events));
		add(events, new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		events.setVisible(false);
	}
	
	
	
	public void unhighlightBorder() {
		getOuterBorder().setWidth(1);
		getOuterBorder().setColor(ColorManager.getColorRegistry().get(ColorManager.COLOR_TABLE_BORDER));
	}

	public void setLayout(int colNumbers){
		layout = new GridLayout(colNumbers, true);
		setLayoutManager(layout);
	}



	@Override
	public void setConstraint(IFigure child, Object constraint) {
		
		super.setConstraint(child, constraint);
	}



	public void hasEvents(boolean hasEvents) {
		events.setVisible(hasEvents);
		
	}

}
