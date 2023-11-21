package bpm.fd.design.ui.editor.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.api.core.model.structure.IContainer;
import bpm.fd.design.ui.editor.model.IFdObjectProvider;
import bpm.fd.design.ui.editor.policies.ContainerEditPolicy;
import bpm.fd.design.ui.editor.policies.FreeLayoutPolicy;
import bpm.fd.design.ui.gef.figures.FolderFigure;

public class FolderPart extends AbstractGraphicalEditPart implements PropertyChangeListener, IFdObjectProvider{

	@Override
	protected IFigure createFigure() {
		Figure f = new FolderFigure();
		f.setBorder(new LineBorder(1));
		((LineBorder)f.getBorder()).setColor(ColorConstants.orange);
		
		return f;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new FreeLayoutPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ContainerEditPolicy());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		refresh();
		
	}

	@Override
	public void activate() {
		Folder model = (Folder)getModel();
		model.addPropertyChangeListener(this);
		super.activate();
	}
	@Override
	public void deactivate() {
		Folder model = (Folder)getModel();
		model.removePropertyChangeListener(this);
		super.deactivate();
	}
	@Override
	protected void refreshVisuals() {
		List<String> titles = new ArrayList<String>();
		
		for(IBaseElement p : ((Folder)getModel()).getContent()){
			titles.add(((FolderPage)p).getTitle());
		}
		((FolderFigure)getFigure()).buildTitles(titles);
		((FolderFigure)getFigure()).hasEvents(((IBaseElement)getModel()).hasEvents());

		IFigure parentFigure = ((GraphicalEditPart) getParent()).getFigure();

		Rectangle parentBounds = parentFigure.getBounds();
		IContainer container = (IContainer)getModel();
		if (parentBounds.width == 0 && parentBounds.height == 0){
			getFigure().setBounds(new Rectangle(0, container.getPosition().y , 800, 150));
		}
		else{
			parentFigure.setConstraint(getFigure(), new Rectangle(0, container.getPosition().y , parentBounds.width, container.getSize().y));
		}
	}

	@Override
	public IBaseElement getFdObject() {
		return (IBaseElement)getModel();
	}
}
