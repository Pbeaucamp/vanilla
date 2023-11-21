package bpm.gateway.ui.gef.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import bpm.gateway.ui.Activator;

public class FigureNode extends RoundedRectangle {

	private Label name = new Label();
	private ImageFigure image = new ImageFigure(Activator.getDefault().getImageRegistry().get("folder_16")); //$NON-NLS-1$
	
	private XYLayout layout;
	
	public FigureNode(Image pict){
		layout = new XYLayout();
		this.setCornerDimensions(new Dimension(8, 8));

		
		if (pict != null){
			image = new ImageFigure(pict);
		}
		
		layout = new XYLayout();
		setLayoutManager(layout);
		
		add(image);
		
		setConstraint(image, new Rectangle(0, 0, -1, -1));
		
		add(name);

		setConstraint(name, new Rectangle(2,image.getPreferredSize().height, -1, -1));
		
		
	
		image.setForegroundColor(ColorConstants.black);
       // image.setBorder(new LineBorder(1));    
	}
	
	public IFigure getFigure(){
		return this;
	}
	
	public void setLayout(Rectangle rect) {
       
        Rectangle r = new Rectangle(rect.x, rect.y, getPreferredSize().width, getPreferredSize().height);
       
        
       
        setConstraint(image, new Rectangle((r.width - 16)/2, 2, -1, -1));
        setBounds(r);
	}

	public void setName(String text) {
		name.setText(" " + text + " "); //$NON-NLS-1$ //$NON-NLS-2$
		setConstraint(name, new Rectangle(0,image.getPreferredSize().height, -1, -1));
		setConstraint(name, new Rectangle(2,image.getPreferredSize().height + 2, -1, -1));
	}
}
