package bpm.fd.design.ui.structure.gef.editparts;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.api.core.model.structure.Table;
import bpm.fd.design.ui.gef.figures.TableFigure;

public class TablePart extends AbstractStructureElementEditPart{

	
	public TablePart() {
		super();
		this.addEditPartListener(new EditPartListener(){

			public void childAdded(EditPart child, int index) {
				
				
			}

			public void partActivated(EditPart editpart) {
				
				
			}

			public void partDeactivated(EditPart editpart) {
				
				
			}

			public void removingChild(EditPart child, int index) {
				
				
			}

			public void selectedStateChanged(EditPart editpart) {
				
//				System.out.println();
			}
			
		});
		
	}

	@Override
	protected IFigure createFigure() {
		TableFigure fig = new TableFigure();
		fig.setLayout(((Table)getModel()).getColumnNumber());
//		fig.setName(((IStructureElement)getModel()).getName());
		return fig;
	}

	
	
	/* (non-Javadoc)
	 * @see bpm.fd.design.ui.structure.gef.editparts.AbstractStructureElementEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		
		super.refreshVisuals();
		((TableFigure)getFigure()).hasEvents(((IBaseElement)getModel()).hasEvents());
//		((TableFigure)getFigure()).setLayout(((Table)getModel()).getColumnNumber());
//		refreshChildren();
		
	}

	public void propertyChange(PropertyChangeEvent evt){

		if (evt.getPropertyName().equals(Table.P_COLUMN_CHANGED)){
			((TableFigure)getFigure()).setLayout(((Table)getModel()).getColumnNumber());
			getFigure().repaint();
			refreshChildren();
			for(CellPart e : (List<CellPart>)getChildren()){
				e.refreshVisuals();
			}
		}
		if (evt.getPropertyName().equals(Table.P_HORIZONTAL_MERGE)){
			refreshChildren();
		}
		if (evt.getPropertyName().equals(Table.P_VERTICAL_MERGE)){
			refreshChildren();
		}
		if (evt.getPropertyName().equals(IBaseElement.EVENTS_CHANGED)){
			refreshVisuals();
		}
		
		super.propertyChange(evt);
	}
	
	@Override
	protected List getModelChildren() {
//		return ((Table)getModel()).getDetailsRows();
		Table model = ((Table)getModel());
		List<IStructureElement> childs = new ArrayList<IStructureElement>();
		for(List<Cell> r : model.getDetailsRows()){
			for(Cell c : r){
				if (c != null){
					childs.add(c);
				}
				
			}
		}
		return childs;
	}
	

}
