package bpm.fd.design.ui.structure.gef.editparts;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.GraphicalEditPart;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.design.ui.gef.figures.FolderFigure;

public class FolderPart extends AbstractStructureElementEditPart{

	@Override
	protected IFigure createFigure() {
		Figure f = new FolderFigure();
		
		
		return f;
	}


	
	@Override
	protected void refreshVisuals() {
		
		
		
		List<String> titles = new ArrayList<String>();
		
		for(IBaseElement p : ((Folder)getModel()).getContent()){
			titles.add(((FolderPage)p).getTitle());
		}
		((FolderFigure)getFigure()).buildTitles(titles);
		((FolderFigure)getFigure()).hasEvents(((IBaseElement)getModel()).hasEvents());
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), new GridData(GridData.FILL, GridData.FILL, true, true));
	}

	

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getFigure()
	 */
	@Override
	public IFigure getFigure() {
		
		return super.getFigure();
	}

	
	
	public void propertyChange(PropertyChangeEvent evt) {
		refreshVisuals();
		
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	@Override
	public void activate() {
		super.activate();
		IStructureElement def = (IStructureElement)getModel();
		def.addPropertyChangeListener(this);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		IStructureElement def = (IStructureElement)getModel();
		def.removePropertyChangeListener(this);
		super.deactivate();
	}

	
}
