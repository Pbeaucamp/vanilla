package bpm.es.datasource.analyzer.ui.gef.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.es.datasource.analyzer.ui.gef.model.Diagram;
import bpm.es.datasource.analyzer.ui.gef.model.Link;
import bpm.vanilla.platform.core.repository.DataSource;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class DiagramEditPartFactory implements EditPartFactory{

	public EditPart createEditPart(EditPart context, Object model) {
		AbstractGraphicalEditPart part = null;  
		
		if (model instanceof RepositoryItem){
			part = new ItemPart();
			part.setModel(model);
		}
		else if (model instanceof DataSource){
			part = new DataSourcePart();
			part.setModel(model);
		}
		else if (model instanceof Diagram){
			part = new DiagramPart();
			part.setModel(model);
		}
		else if (model instanceof Link){
			part = new TransitionPart();
			part.setModel(model);
		}
		return part;
	}

}
