package bpm.fd.design.ui.gef.figures;

import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;

import bpm.fd.design.ui.tools.ColorManager;

public abstract class AbstractBaseFigure extends Figure implements HoverableFigure{

	protected GridLayout layout;
	private LineBorder outerBorder;
	private MarginBorder innerBorder;
	private CompoundBorder border;
	
	public AbstractBaseFigure(String colorKey){
		layout = new GridLayout(2, false);
		
		this.setLayoutManager(layout);
	
		outerBorder = new LineBorder(1);
		outerBorder.setColor(ColorManager.getColorRegistry().get(colorKey));
		
		innerBorder = new MarginBorder(3);
		
		border = new CompoundBorder(outerBorder, innerBorder);
		
		
		this.setBorder(border);
	}
	
	public LineBorder getOuterBorder(){
		return outerBorder;
	}
	
	public void highlightBorder() {
		getOuterBorder().setWidth(2);
		getOuterBorder().setColor(ColorManager.getColorRegistry().get(ColorManager.COLOR_HOVER_BORDER));
		
	}
	
	
}
