package bpm.fd.design.ui.editor.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.design.ui.editor.figures.StackCellFigure;
import bpm.fd.design.ui.editor.model.IFdObjectProvider;
import bpm.fd.design.ui.editor.policies.ContainerEditPolicy;
import bpm.fd.design.ui.editor.policies.FreeLayoutPolicy;

public class StackableCellPart extends AbstractGraphicalEditPart implements PropertyChangeListener, IFdObjectProvider{
	public static final String selectedContentChanged = "bpm.fd.design.ui.editor.editparts.StackableCellPart.selectedContentChanged";
	@Override
	protected IFigure createFigure() {
		StackableCell model = (StackableCell)getModel();
		StackCellFigure f =  new StackCellFigure();
		f.setLayout(new Rectangle(
				model.getPosition().x, 
				model.getPosition().y, 
				model.getSize().x, 
				model.getSize().y));
		FdModel fdModel = (FdModel)((EditPart)getRoot().getChildren().get(0)).getModel();
		for(int i = 0; i < model.getContent().size(); i++){
			if (model.getContent().get(i) instanceof IComponentDefinition){
				boolean configured = model.isComponentDefined(fdModel, (IComponentDefinition)model.getContent().get(i));
				f.decorate(i, configured);
			}
		}
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
		StackCellFigure fig = (StackCellFigure)getFigure();
		StackableCell model = (StackableCell)getModel();
		if (evt.getPropertyName() == CellWrapper.EVENT_LAYOUT && evt.getNewValue() != null){
			
			Rectangle rect = new Rectangle(
					model.getPosition().x, 
					model.getPosition().y, 
					model.getSize().x, 
					model.getSize().y);
			fig.setLayout(rect);
			
			
			for(EditPart e : (List<EditPart>)getChildren()){
				((CellWrapper)e.getModel()).setSize(model.getSize().x, model.getSize().y);
			}
			
			for(int i = 0; i < StackableCell.BUTTONS_TYPES.length; i++){
				if (StackableCell.BUTTONS_TYPES[i].equals(model.getType())){
					fig.setType(i);
				}
			}
			
			refreshVisuals();
			return;
		}
		else if (evt.getPropertyName() == selectedContentChanged){
			FdModel fdModel = (FdModel)((EditPart)getRoot().getChildren().get(0)).getModel();
			for(int i = 0; i < model.getContent().size(); i++){
				if (model.getContent().get(i) instanceof IComponentDefinition){
					boolean configured = model.isComponentDefined(fdModel, (IComponentDefinition)model.getContent().get(i));
					fig.decorate(i, configured);
				}
			}
			
		}
		else if (evt.getPropertyName().equals(IComponentDefinition.PARAMETER_CHANGED)){
			refreshChildren();
		}
		else{
			refreshChildren();
			refreshVisuals();
		}
		
	}

	@Override
	public IBaseElement getFdObject() {
		return (StackableCell)getModel();
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
