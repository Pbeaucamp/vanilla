package bpm.es.clustering.ui.gef.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Image;

import bpm.es.clustering.ui.Activator;
import bpm.es.clustering.ui.icons.Icons;
import bpm.vanilla.platform.core.beans.tasks.ServerType;

public class RuntimeServerFigure extends Figure{
	
	
	public RuntimeServerFigure(String name){
		Image im = null;
		
		for(ServerType t : ServerType.values()){
			if (name.equals(t.name())){
				switch(t){
				case GATEWAY:
					im = Activator.getDefault().getImageRegistry().get(Icons.BIG);
					break;
				case GED:
					im = Activator.getDefault().getImageRegistry().get(Icons.GED);
					break;
				case REPORTING:
					im = Activator.getDefault().getImageRegistry().get(Icons.REPORT);
					break;
				}
			}
		}
		add(new Label(name, im));
		setLayoutManager(new ToolbarLayout());
	}
}
