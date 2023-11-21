package bpm.fd.design.ui.editor.figures;

import java.awt.Point;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.icons.Icons;


public class ComponentFigure extends Figure {
	private static final Font font = new Font(Display.getDefault(), new FontData("Arial", 12, SWT.ITALIC));
	private static final Color nameColor = new Color(Display.getDefault(), 227, 246, 206);
	public static final Color errorColor = new Color(Display.getDefault(), 250, 2, 35);
//	public static final Color whiteColor = new Color(Display.getDefault(), 0, 0, 0);
	
	private Label name ;
	private ImageFigure javaScriptEnabled;
	
	private IFigure content;
	private XYLayout layout;
	
	public ComponentFigure(IFigure content){
		layout = new XYLayout();
		 name = new Label();name.setOpaque(false);
		setLayoutManager(layout);
		
		add(name);
		name.setFont(font);
		name.setOpaque(true);
		name.setBorder(new LineBorder(ColorConstants.black, 1, Graphics.LINE_SOLID));
		name.setBackgroundColor(nameColor);
		setConstraint(name, new Rectangle(0, 0, -1, -1));
		
		javaScriptEnabled = new ImageFigure(Activator.getDefault().getImageRegistry().get(Icons.events), PositionConstants.RIGHT);
		add(javaScriptEnabled);
		
		setConstraint(javaScriptEnabled, new Rectangle(-1, 0, -1, -1));
		
//		if (pict != null){
//			image = new ImageFigure(pict);
//		}
		if (content != null){
			this.content = content;
			add(content);
			setConstraint(content, new Rectangle(0, 0, -1, -1));
		}
		
		
		this.setForegroundColor(ColorConstants.black);
		this.setBorder(new LineBorder(1));    
	}
	

	public void setLayout(Rectangle rect) {
        Rectangle r = new Rectangle(rect.x, rect.y, rect.width, rect.height);
        setBounds(r);
        setConstraint(name, new Rectangle(0,0, getBounds().width, name.getPreferredSize().height));
        setConstraint(javaScriptEnabled, new Rectangle(getBounds().width - 16,0, 16, 16));
	}
	public void setName(String text) {
		name.setText(text);
		try{
			
			setConstraint(content, new Rectangle(0,name.getPreferredSize().height, -1, -1));
			//name.setConstraint(javaScriptEnabled, new Rectangle(name.getPreferredSize().width,name.getPreferredSize().height, -1, -1));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void update(IComponentDefinition def, Point preferedSize){
		if (def == null){
			return;
		}
		if (content != null && content instanceof IComponentFigure){
			setName(def.getName());
			((IComponentFigure)content).update(def, preferedSize);

			
		}
	}

	
	public void decorate(Image img, boolean javaScriptActive){
		name.setIcon(img);
		if (img != null){
			name.setBackgroundColor(errorColor);
		}
		else{
			name.setBackgroundColor(nameColor);
		}
		javaScriptEnabled.setVisible(javaScriptActive);
	}
}
