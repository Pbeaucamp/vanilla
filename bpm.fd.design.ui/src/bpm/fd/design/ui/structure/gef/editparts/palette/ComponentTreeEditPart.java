package bpm.fd.design.ui.structure.gef.editparts.palette;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.design.ui.gef.figures.PictureHelper;

public class ComponentTreeEditPart extends AbstractTreeEditPart implements PropertyChangeListener{


	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getImage()
	 */
	@Override
	protected Image getImage() {
		
		return PictureHelper.getIcons(getModel()) ;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getText()
	 */
	@Override
	protected String getText() {
		return ((IComponentDefinition)getModel()).getName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getDragTracker(org.eclipse.gef.Request)
	 */
	@Override
	public DragTracker getDragTracker(Request req) {
		return new DragEditPartsTracker(this);
	}

	@Override
    public void performRequest(Request req) {
		if (req.getType().equals(RequestConstants.REQ_CREATE)) {
			
		}
		else if (req.getType().equals(RequestConstants.REQ_SELECTION)){
			try {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                page.showView(IPageLayout.ID_PROP_SHEET);
	        }catch (PartInitException e) {
	                e.printStackTrace();
	        }
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(IComponentDefinition.PROPERTY_NAME_CHANGED)){
			refreshVisuals();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#activate()
	 */
	@Override
	public void activate() {
		super.activate();
		((IComponentDefinition)getModel()).addPropertyChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		super.deactivate();
		((IComponentDefinition)getModel()).removePropertyChangeListener(this);
	}

	@Override
	protected void addChild(EditPart child, int index) {
		
		super.addChild(child, index);
	}

}
