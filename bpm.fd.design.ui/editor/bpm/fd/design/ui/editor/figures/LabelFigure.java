package bpm.fd.design.ui.editor.figures;

import java.awt.Point;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.TextFlow;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.text.LabelComponent;
import bpm.fd.api.core.model.components.definition.text.LabelOptions;


public class LabelFigure extends Figure implements IComponentFigure{
	private FlowPage flow;
	private TextFlow text;	

	
	public LabelFigure(){
		setLayoutManager(new FlowLayout());
		flow = new FlowPage();
		add(flow);
//		setConstraint(flow, new Rectangle(0,0,-1,-1));
		
		text = new TextFlow();
		flow.add(text);
	}
	
	public void update(IComponentDefinition def,Point preferezSIZE){
		text.setText(((LabelOptions)((LabelComponent)def).getOptions(LabelOptions.class)).getText());
		
		
	}
}
