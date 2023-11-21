package bpm.es.sessionmanager.gef.figures;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.LineAttributes;

import bpm.es.sessionmanager.Activator;
import bpm.es.sessionmanager.gef.model.ServerModel;
import bpm.es.sessionmanager.icons.IconsName;

public class ServerFigure extends RoundedRectangle { 
//RectangleFigure {

	//public static Color classColor = new Color(null,255,255,206);

	private Label title, host;
	//private ImageFigure
	
	public ServerFigure(ServerModel model){
//		ToolbarLayout layout = new ToolbarLayout();
//		this.setLayoutManager(layout);
//
//		this.setLineStyle(Graphics.LINE_SOLID);
//		this.setLineWidth(1);
//		this.setForegroundColor(ColorConstants.black);
//		this.setLayoutManager(new ToolbarLayout());
//
//		this.setBackgroundColor(ColorConstants.tooltipBackground);

	    ToolbarLayout layout = new ToolbarLayout();
	    layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
	    setLayoutManager(layout);	
//	    setBorder(new LineBorder(ColorConstants.black,0));
//	    setBackgroundColor(ColorConstants.tooltipBackground);
	    setOpaque(true);

		
//		RectangleFigure rect = new RectangleFigure();
//		rect.setBackgroundColor(ColorConstants.lightBlue);
//		rect.setLineWidth(1);
//		rect.setLineStyle(Graphics.LINE_SOLID);
//		rect.setLayoutManager(new ToolbarLayout());
//		this.add(rect);
//		title = new Label(name);
//		rect.add(title);
		
		//general
		//RoundedRectangle holder = new RoundedRectangle();
	    //RectangleFigure holder = new RectangleFigure();
		//holder.setBackgroundColor(ColorConstants.lightBlue);
		this.setBackgroundColor(ColorConstants.tooltipBackground);
		this.setLineWidth(1);
		this.setLineStyle(Graphics.LINE_SOLID);
		this.setLayoutManager(new ToolbarLayout());
		//this.add(holder);
		
		//title
		title = new Label("Vanilla");
		title.setLabelAlignment(PositionConstants.CENTER);
		title.setIconAlignment(PositionConstants.LEFT);
		title.setIcon(Activator.getDefault().getImageRegistry().get(IconsName.SERVER));
		this.add(title);
		
		host = new Label("  " + model.getServerHost() + "  ");
		host.setLabelAlignment(PositionConstants.CENTER);
		//host.setIconAlignment(PositionConstants.LEFT);
		//host.setIcon(Activator.getDefault().getImageRegistry().get(IconsName.SERVER));
		this.add(host);
	}
	
	public class CompartmentFigureBorder extends AbstractBorder {
		public Insets getInsets(IFigure figure) {
			return new Insets(1,0,0,0);
		}

		public void paint(IFigure figure, Graphics graphics, Insets insets) {
				graphics.drawLine(getPaintRectangle(figure, insets).getTopLeft(), tempRect.getTopRight());
		}
	}	
}
