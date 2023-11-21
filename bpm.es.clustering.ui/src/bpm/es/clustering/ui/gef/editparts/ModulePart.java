package bpm.es.clustering.ui.gef.editparts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.es.clustering.ui.Messages;
import bpm.es.clustering.ui.gef.GefModel;
import bpm.es.clustering.ui.gef.figures.ModuleFigure;
import bpm.es.clustering.ui.model.VanillaPlatformModule;

public class ModulePart extends AbstractGraphicalEditPart{

	@Override
	protected IFigure createFigure() {
		VanillaPlatformModule model = (VanillaPlatformModule)getModel();
		
		Figure f = null;
		if (((GefModel)getParent().getModel()).getDefaultModule() == model){
			f = new ModuleFigure(model.getName(), true);
		}
		else{
			f = new ModuleFigure(model.getName(), false);
		}
		
		f.setToolTip(new Label(Messages.ModulePart_0 + model.getUrl()));
		return f;
	}

	@Override
	protected void createEditPolicies() {
		
		
	}
	
	@Override
	protected List getModelChildren() {
		List l = new ArrayList();
		VanillaPlatformModule m = (VanillaPlatformModule)getModel();
		l.addAll(m.getRegisteredModules());
		return l;
	}
	
	

}
