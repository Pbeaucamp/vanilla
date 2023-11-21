package bpm.es.clustering.ui.gef.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;

import bpm.es.clustering.ui.Activator;
import bpm.es.clustering.ui.icons.Icons;

public class RepositoryFigure extends Figure{
	
	public RepositoryFigure(String name){
		Label l = new Label(name, Activator.getDefault().getImageRegistry().get(Icons.REP_BIG));
		add(l);
		
		setLayoutManager(new GridLayout());
	}

}
