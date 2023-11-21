package bpm.fd.design.ui.structure.gef.editparts;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;

import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.api.core.model.structure.Row;
import bpm.fd.design.ui.gef.figures.RowFigure;

public class RowPart extends AbstractStructureElementEditPart{

	
	public RowPart() {
		super();
		
	}

	@Override
	protected IFigure createFigure() {
		RowFigure fig = new RowFigure();
//		fig.setName(((IStructureElement)getModel()).getName());
		return fig;
	}

	public void propertyChange(PropertyChangeEvent evt){
		RowFigure fig = (RowFigure)getFigure();
		
		if (evt.getPropertyName().equals(IStructureElement.P_CONTENT_ADDED)){
			fig.setLayoutManager(new GridLayout(getModelChildren().size(), true));
			
			for(AbstractStructureElementEditPart a : (List<AbstractStructureElementEditPart>)getChildren()){
				 setLayoutConstraint(this, a.getFigure(), new GridData(GridData.FILL, GridData.FILL, true, true));
			}
			
			super.propertyChange(evt);
		}
		else if (evt.getPropertyName().equals(IStructureElement.P_CONTENT_REMOVED)) {
			fig.setLayoutManager(new GridLayout(getModelChildren().size(), true));
			for(AbstractStructureElementEditPart a : (List<AbstractStructureElementEditPart>)getChildren()){
				 setLayoutConstraint(this, a.getFigure(), new GridData(GridData.FILL, GridData.FILL, true, true));
			}
			super.propertyChange(evt);
        }
		else{
			super.propertyChange(evt);
		}
		
		
	}
	
	
	@Override
	protected List getModelChildren() {
		return((Row)getModel()).getCells();
	}
	

}
