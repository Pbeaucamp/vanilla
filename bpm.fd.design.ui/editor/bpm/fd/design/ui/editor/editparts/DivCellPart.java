package bpm.fd.design.ui.editor.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.structure.DivCell;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.design.ui.editor.figures.DivCellFigure;
import bpm.fd.design.ui.editor.model.IFdObjectProvider;
import bpm.fd.design.ui.editor.policies.ContainerEditPolicy;
import bpm.fd.design.ui.editor.policies.FreeLayoutPolicy;

public class DivCellPart extends AbstractGraphicalEditPart implements PropertyChangeListener, IFdObjectProvider {
	public static final String selectedContentChanged = "bpm.fd.design.ui.editor.editparts.DivCellPart.selectedContentChanged";
	
	private DivCell model;
	
	@Override
	protected IFigure createFigure() {
		this.model = (DivCell)getModel();
		DivCellFigure f =  new DivCellFigure();
//		f.setName(model.getName());
		f.setLayout(new Rectangle(model.getPosition().x, model.getPosition().y, model.getSize().x, model.getSize().y));
		f.addPropertyChangeListener(this);
		return f;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ContainerEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE,new FreeLayoutPolicy());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		DivCellFigure fig = (DivCellFigure)getFigure();
		DivCell model = (DivCell)getModel();
		if (evt.getPropertyName() == CellWrapper.EVENT_LAYOUT && evt.getNewValue() != null){
			
			Rectangle rect = new Rectangle(
					model.getPosition().x, 
					model.getPosition().y, 
					model.getSize().x, 
					model.getSize().y);
			fig.setLayout(rect);
			
			refreshVisuals();
			return;
		}
		
		refreshVisuals();
	}

	@Override
	public IBaseElement getFdObject() {
		return model;
	}
	
	@Override
	public void activate() {
		getFdObject().addPropertyChangeListener(this);
		super.activate();
	}

	@Override
	public void deactivate() {
		getFdObject().removePropertyChangeListener(this);
		super.deactivate();
	}
	
	@Override
	protected List getModelChildren() {
		return ((IStructureElement)getFdObject()).getContent();
	}
	
	@Override
	protected void fireSelectionChanged() {
		super.fireSelectionChanged();
	}

}
