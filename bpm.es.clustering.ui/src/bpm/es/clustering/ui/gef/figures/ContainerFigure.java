package bpm.es.clustering.ui.gef.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.widgets.MultiLineLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import bpm.es.clustering.ui.Messages;

public class ContainerFigure extends Figure{
	private static final Font titleFont = new Font(Display.getDefault(), "Arial", 12, SWT.BOLD | SWT.ITALIC); //$NON-NLS-1$

	private RoundedRectangle main;
	private Label title;
	private Figure holder;
	private Polyline line;
	
	public ContainerFigure(String title){
		setLayoutManager(new ToolbarLayout());
		
		main = new RoundedRectangle();
		add(main);
		main.setLayoutManager(new GridLayout());
		
		this.title = new Label(title);
		this.title.setFont(titleFont);
		main.add(this.title, new GridData(GridData.CENTER, GridData.BEGINNING, true, false));
		
		line = new Polyline();
		line.setLineWidth(1);
		
		main.add(line, new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		holder = new Figure();
		holder.setLayoutManager(new ToolbarLayout(true));
		
		main.add(holder, new GridData(GridData.FILL_BOTH));
		
	}
	
	public void setTitleColor(Color col){
		title.setBackgroundColor(col);
	}
	
	@Override
	public void add(IFigure figure, Object constraint, int index) {
		if (figure != main && figure != title){
			holder.add(figure, constraint, index);
		}
		else{
			super.add(figure, constraint, index);
		}
		
	}
	
	public Figure getClient(){
		return holder;
	}
	
	public Figure getMain(){
		
		return main;
	}
	
	@Override
	public void paint(Graphics graphics) {
		
		line.removeAllPoints();
		Rectangle r = title.getBounds();
		Rectangle r2 = main.getBounds();
		line.addPoint(new Point(r2.x, r.y + r.height));
		line.addPoint(new Point(r2.x + r2.width, r.y + r.height));
		super.paint(graphics);
		
		
	}
	
	public void setUrl(String url){
		setToolTip(new Label(Messages.ContainerFigure_1 + url));
	}
	
}
