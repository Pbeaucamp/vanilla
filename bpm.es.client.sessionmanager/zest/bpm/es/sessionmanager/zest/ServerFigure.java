package bpm.es.sessionmanager.zest;

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
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.LineAttributes;

import bpm.es.sessionmanager.Activator;
import bpm.es.sessionmanager.Messages;
import bpm.es.sessionmanager.api.server.VanillaServer;
import bpm.es.sessionmanager.icons.IconsName;

public class ServerFigure extends RoundedRectangle { 

	private Label title, host;
	
	public ServerFigure(VanillaServer server){

	    ToolbarLayout layout = new ToolbarLayout();
	    layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
	    setLayoutManager(layout);	
	    setOpaque(true);

		this.setBackgroundColor(ColorConstants.tooltipBackground);
		this.setLineWidth(1);
		this.setLineStyle(Graphics.LINE_SOLID);
		this.setLayoutManager(new ToolbarLayout());
		//this.add(holder);
		
		//title
		title = new Label("Vanilla"); //$NON-NLS-1$
		title.setLabelAlignment(PositionConstants.CENTER);
		title.setIconAlignment(PositionConstants.LEFT);
		title.setIcon(Activator.getDefault().getImageRegistry().get(IconsName.SERVER));
		this.add(title);
		
		host = new Label("  " + server.getHost() + "  "); //$NON-NLS-1$ //$NON-NLS-2$
		host.setLabelAlignment(PositionConstants.CENTER);
		this.add(host);
	}

	@Override
	public Dimension getMinimumSize(int wHint, int hHint) {
		Dimension dim = new Dimension(100, 50);
		return dim;
		//return super.getMinimumSize(wHint, hHint);
	}
	
	@Override
	public Dimension getMaximumSize() {
		Dimension dim = new Dimension(100, 50);
		return dim;
	}
}
