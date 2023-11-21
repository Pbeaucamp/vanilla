package bpm.fd.design.ui.editor.figures;

import java.awt.Point;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.TextFlow;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.link.LinkOptions;


public class HyperlinkFigure extends Figure implements IComponentFigure{
	private FlowPage flow;
	private TextFlow text;	
	
	public HyperlinkFigure(){
		setLayoutManager(new FlowLayout());
		flow = new FlowPage();
		add(flow);
//		setConstraint(flow, new Rectangle(0,0,-1,-1));
		
		text = new TextFlow();
		text.setForegroundColor(ColorConstants.blue);
//		text.getFont().getFontData()

		flow.add(text);
	}
	
	public void update(IComponentDefinition def,Point preferezSIZE){

		text.setText( ((LinkOptions)def.getOptions(LinkOptions.class)).getLabel());
	}
}
