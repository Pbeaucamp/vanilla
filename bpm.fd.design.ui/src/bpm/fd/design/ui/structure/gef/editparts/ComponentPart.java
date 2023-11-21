package bpm.fd.design.ui.structure.gef.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProjectDescriptor;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.datas.DataSetDescriptor;
import bpm.fd.api.core.model.datas.ParameterDescriptor;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.editor.model.IComponentDefinitionProvider;
import bpm.fd.design.ui.editor.model.IFdObjectProvider;
import bpm.fd.design.ui.gef.figures.ComponentFigure;
import bpm.fd.design.ui.gef.figures.PictureHelper;
import bpm.fd.design.ui.structure.gef.commands.DeleteComponentCommand;
import bpm.fd.design.ui.structure.gef.commands.DeleteComponentFromStackbleCellCommand;

public class ComponentPart extends AbstractGraphicalEditPart implements PropertyChangeListener, IComponentDefinitionProvider, IFdObjectProvider{

	@Override
	protected IFigure createFigure() {
		IComponentDefinition def = (IComponentDefinition)getModel();
		ComponentFigure fig = new ComponentFigure(PictureHelper.getFullSizePicture(def), def.getName());
		
//		showHideError();
		return fig;
	}

	@Override
	protected void createEditPolicies() {
		
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy(){

			/* (non-Javadoc)
			 * @see org.eclipse.gef.editpolicies.ComponentEditPolicy#createDeleteCommand(org.eclipse.gef.requests.GroupRequest)
			 */
			@Override
			protected Command createDeleteCommand(GroupRequest deleteRequest) {
				Command command = null;
				if(getHost().getParent().getModel() instanceof Cell) {
					command = new DeleteComponentCommand();
					
					((DeleteComponentCommand)command).setNewElement((IComponentDefinition)getHost().getModel());
					((DeleteComponentCommand)command).setParent((Cell)getHost().getParent().getModel());
				}
				else if(getHost().getParent().getModel() instanceof StackableCell) {
					command = new DeleteComponentFromStackbleCellCommand();
					
					((DeleteComponentFromStackbleCellCommand)command).setNewElement((IComponentDefinition)getHost().getModel());
					((DeleteComponentFromStackbleCellCommand)command).setParent((StackableCell)getHost().getParent().getModel());
				}
				
				return command;
			}
			
		});
		
	}
	
	@Override
	protected void refreshVisuals() {
		showHideError();
		((ComponentFigure)getFigure()).hasEvents(((IBaseElement)getModel()).hasEvents());
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		
	}

	

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getFigure()
	 */
	@Override
	public IFigure getFigure() {
		return super.getFigure();
	}

	private boolean isComponentParametersNamesValid(){
		IComponentDefinition def = (IComponentDefinition)getModel();
		
		if (def.getDatas() == null || def.getDatas().getDataSet() == null || Activator.getDefault().getProject() == null){
			return true;
		}
		DataSetDescriptor desc = def.getDatas().getDataSet().getDataSetDescriptor();
		FdProjectDescriptor projectDesc = Activator.getDefault().getProject().getProjectDescriptor();
		
		for(ParameterDescriptor pd : desc.getParametersDescriptors()){
			if (pd.getName().contains(projectDesc.getModelName())){
				return false;
			}
			if (pd.getName().contains(projectDesc.getProjectName())){
				return false;
			}
		}
		return true;
	}
	
	private boolean showHideError(){
		IStructureElement cell = null;
		if(getParent().getModel() instanceof Cell) {
			cell = (Cell)getParent().getModel();
		}
		else if(getParent().getModel() instanceof StackableCell) {
			cell = (StackableCell)getParent().getModel();
		}
//		Cell cell = (Cell)getParent().getModel();
		EditPart parent = getParent();
		
		while(!(parent.getModel() instanceof FdModel)){
			parent = parent.getParent();
		}
		
		boolean flag = false;
		
		/*
		 * check if parameters are sets
		 */
		boolean isCompDef = false;
		if(cell instanceof Cell) {
			isCompDef = ((Cell)cell).isComponentDefined((FdModel)parent.getModel(), (IComponentDefinition)getModel());
		}
		else {
			isCompDef = ((StackableCell)cell).isComponentDefined((FdModel)parent.getModel(), (IComponentDefinition)getModel());
		}
		if (isCompDef){
			if (((ComponentFigure)getFigure()).hasErrorText()){
				((ComponentFigure)getFigure()).hideErrorText();
				flag = true;
			}
		}else{
			
			if (!((ComponentFigure)getFigure()).hasErrorText()){
				((ComponentFigure)getFigure()).showErrorText();
				flag = true;
			}

		}
		
//		if (isComponentParametersNamesValid()){
//			if (((ComponentFigure)getFigure()).hasErrorText()){
//				((ComponentFigure)getFigure()).hideParameterErrorText();
//				flag = true;
//			}
//		}
//		else{
//			if (((ComponentFigure)getFigure()).hasErrorText()){
//				((ComponentFigure)getFigure()).showParameterErrorText();
//				flag = true;
//			}
//		}
		
		return flag;
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		
		if (evt.getPropertyName().equals(ComponentChartDefinition.PROPERTY_NATURE_CHANGED) ||
			evt.getPropertyName().equals(ComponentFilterDefinition.PROPERTY_RENDERER_CHANGED) ){
			IComponentDefinition def = (IComponentDefinition)getModel();
			((ComponentFigure)getFigure()).setPicture(PictureHelper.getFullSizePicture(def));
			refreshVisuals();
		}
		if (evt.getPropertyName().equals(IComponentDefinition.PROPERTY_NAME_CHANGED)){
			IComponentDefinition def = (IComponentDefinition)getModel();
			((ComponentFigure)getFigure()).setName(def.getName());
			
			
			Activator.getDefault().getProject().updateParameterProvider((String)evt.getOldValue(), def);
			
			refreshVisuals();
		}
		
		if (evt.getPropertyName().equals(IComponentDefinition.PARAMETER_CHANGED)){
			if (showHideError()){
				refreshVisuals();
			}
		
		}
		if (evt.getPropertyName().equals(IBaseElement.EVENTS_CHANGED)){
			refreshVisuals();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	@Override
	public void activate() {
		super.activate();
		IComponentDefinition def = (IComponentDefinition)getModel();
		def.addPropertyChangeListener(this);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		IComponentDefinition def = (IComponentDefinition)getModel();
		def.removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	public IComponentDefinition getComponent() {
		return (IComponentDefinition)getModel();
	}

	@Override
	public IBaseElement getFdObject() {
		return getComponent();
	}

	
}
