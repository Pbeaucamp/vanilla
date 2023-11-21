package bpm.fd.design.ui.gef.figures;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.icons.Icons;
import bpm.fd.design.ui.tools.ColorManager;

public class CellFigure extends AbstractBaseFigure {

	static{
		ColorManager.getColorRegistry().put(ColorManager.COLOR_CELL_BORDER, new RGB(0, 255, 0));
	}

	private ImageFigure pict, events;
	public CellFigure(boolean isEmpty){
		super(ColorManager.COLOR_CELL_BORDER);
		setLayoutManager(new GridLayout());
		if (isEmpty){
			setPicture(Activator.getDefault().getImageRegistry().get(Icons.empty_cell));
		}
		Image im = Activator.getDefault().getImageRegistry().get(Icons.empty_cell);
		setMinimumSize(new Dimension(im.getImageData().width, im.getImageData().height));
		events = new ImageFigure(Activator.getDefault().getImageRegistry().get(Icons.events));
		add(events, new GridData(GridData.END, GridData.BEGINNING, false, false));
		events.setVisible(false);

	}


	public void unhighlightBorder() {
		getOuterBorder().setWidth(1);
		getOuterBorder().setColor(ColorManager.getColorRegistry().get(ColorManager.COLOR_CELL_BORDER));
	}


	public void setPicture(Image img){
		if (pict != null && getChildren().contains(pict)){
			remove(pict);
		}
		
		if (img != null){
			pict = new ImageFigure(img);
			add(pict, new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		}
	}


	public void hasEvents(boolean hasEvents) {
		events.setVisible(hasEvents);
		
	}


	
	
	
}
