package bpm.fd.design.ui.editor.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.DivCell;
import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.editor.figures.ComponentFigure;
import bpm.fd.design.ui.editor.figures.DivCellFigure;
import bpm.fd.design.ui.editor.figures.FigureBuilder;
import bpm.fd.design.ui.editor.model.IComponentDefinitionProvider;
import bpm.fd.design.ui.editor.model.IFdObjectProvider;
import bpm.fd.design.ui.editor.policies.ComponentSelectionEditPolicy;
import bpm.fd.design.ui.editor.policies.ContainerEditPolicy;
import bpm.fd.design.ui.editor.policies.FreeLayoutPolicy;
import bpm.fd.design.ui.icons.Icons;

public class ComponentPart extends AbstractGraphicalEditPart implements PropertyChangeListener, IComponentDefinitionProvider, IFdObjectProvider {
	private FactoryStructure strutureFactory;

	public ComponentPart(FactoryStructure strutureFactory) {
		this.strutureFactory = strutureFactory;
	}

	@Override
	protected IFigure createFigure() {
		CellWrapper model = (CellWrapper) getModel();

		ComponentFigure c = new ComponentFigure(FigureBuilder.createFigure(((IComponentDefinition) model.getCell().getContent().get(0)), model.getSize()));
		c.setName(model.getCell().getContent().get(0).getName());

		if(getParent() instanceof DivCellPart) {
			c.setLayout(new Rectangle(
					model.getCell().getPosition().x + ((DivCell)((DivCellPart)getParent()).getFdObject()).getPosition().x, 
					model.getCell().getPosition().y + ((DivCell)((DivCellPart)getParent()).getFdObject()).getPosition().y, 
					model.getCell().getSize().x, 
					model.getCell().getSize().y));
		}
		else {
			c.setLayout(new Rectangle(model.getCell().getPosition().x, model.getCell().getPosition().y, model.getCell().getSize().x, model.getCell().getSize().y));
		}
		

		FdModel fdmodel = (FdModel) ((EditPart) getRoot().getChildren().get(0)).getModel();
		if(getParent().getModel() instanceof StackableCell) {
			if(((StackableCell) ((StackableCellPart) getParent()).getModel()).isComponentDefined(fdmodel, (IComponentDefinition) model.getCell().getContent().get(0))) {
				c.decorate(null, getComponent().hasEvents());
			}
			else {
				c.decorate(Activator.getDefault().getImageRegistry().get(Icons.error_16), getComponent().hasEvents());
			}
		}
		else {
			if(model.getCell().isComponentDefined(fdmodel, (IComponentDefinition) model.getCell().getContent().get(0))) {

				c.decorate(null, getComponent().hasEvents());
			}
			else {
				c.decorate(Activator.getDefault().getImageRegistry().get(Icons.error_16), getComponent().hasEvents());
			}
		}

		return c;
	}

	@Override
	protected void createEditPolicies() {
		// layout policy
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new FreeLayoutPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ContainerEditPolicy());
		// installEditPolicy(EditPolicy.COMPONENT_ROLE,);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new ComponentSelectionEditPolicy());

	}

	@Override
	protected void refreshVisuals() {
		ComponentFigure fig = (ComponentFigure) getFigure();
		CellWrapper w = (CellWrapper) getModel();
		FdModel model = (FdModel) ((EditPart) getRoot().getChildren().get(0)).getModel();

		if(getParent().getModel() instanceof StackableCell) {
			if(((StackableCell) ((StackableCellPart) getParent()).getModel()).isComponentDefined(model, (IComponentDefinition) w.getCell().getContent().get(0))) {
				fig.decorate(null, getComponent().hasEvents());
			}
			else {
				fig.decorate(Activator.getDefault().getImageRegistry().get(Icons.error_16), getComponent().hasEvents());
			}
		}
		else {
			if(!w.getCell().getContent().isEmpty() && w.getCell().isComponentDefined(model, (IComponentDefinition) w.getCell().getContent().get(0))) {
				fig.decorate(null, getComponent().hasEvents());
			}
			else {
				fig.decorate(Activator.getDefault().getImageRegistry().get(Icons.error_16), getComponent().hasEvents());
			}
		}
		super.refreshVisuals();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		ComponentFigure fig = (ComponentFigure) getFigure();
		CellWrapper w = (CellWrapper) getModel();
		if(evt.getPropertyName() == CellWrapper.EVENT_LAYOUT && evt.getNewValue() != null) {
			
			if(fig.getParent() instanceof DivCellFigure) {
				fig.setLayout(new Rectangle(
						w.getCell().getPosition().x + ((DivCellFigure)fig.getParent()).getLocation().x, 
						w.getCell().getPosition().y + ((DivCellFigure)fig.getParent()).getLocation().y, 
						w.getCell().getSize().x, 
						w.getCell().getSize().y));
			}
			else {
				fig.setLayout(new Rectangle(w.getCell().getPosition().x, w.getCell().getPosition().y, w.getCell().getSize().x, w.getCell().getSize().y));
			}

		}
		fig.update(getComponent(), w.getCell().getSize());

		refreshVisuals();

	}

	@Override
	public void setModel(Object model) {
		Cell cell = null;

		if(model instanceof IComponentDefinition) {
			FactoryStructure f = this.strutureFactory == null ? Activator.getDefault().getProject().getFdModel().getStructureFactory() : this.strutureFactory;
			cell = f.createCell("", 1, 1);
			cell.addBaseElementToContent((IComponentDefinition) model);
		}
		else if(model instanceof Cell) {
			cell = (Cell) model;
		}
		super.setModel(new CellWrapper(cell));
	}

	@Override
	public void activate() {
		super.activate();
		((CellWrapper) getModel()).addListener(this);

	}

	@Override
	public void deactivate() {
		super.activate();
		((CellWrapper) getModel()).removeListener(this);
	}

	@Override
	public IComponentDefinition getComponent() {
		try {
			return (IComponentDefinition) ((CellWrapper) getModel()).getCell().getContent().get(0);
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	@Override
	public IBaseElement getFdObject() {
		return getComponent();
	}

}
