package bpm.fd.design.ui.structure.gef.editparts.outline;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.api.core.model.structure.Row;
import bpm.fd.api.core.model.structure.Table;

public class TreeStructurePart extends AbstractTreeEditPart implements PropertyChangeListener {

	public void propertyChange(PropertyChangeEvent evt) {
		refreshChildren();
		refreshVisuals();
		
	}
	
	public void refreshVisuals(){
        IStructureElement model = (IStructureElement)getModel();
        setWidgetText(model.getName()  + "- " + model.getId()); //$NON-NLS-1$
        setWidgetImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT));
	}
	
	protected List getModelChildren() {
		if (getModel() instanceof Table){
			List<Row> lst = new ArrayList<Row>();
			for(List<Cell> l : ((Table)getModel()).getDetailsRows()){
				lst.add(new Row(l));
			}
			return lst;
		}
		else{
			
		}
		
        return ((IStructureElement)getModel()).getContent();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#activate()
	 */
	@Override
	public void activate() {
		super.activate();
		((IStructureElement)getModel()).addPropertyChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		((IStructureElement)getModel()).removePropertyChangeListener(this);
		super.deactivate();
	}
	
	

}
