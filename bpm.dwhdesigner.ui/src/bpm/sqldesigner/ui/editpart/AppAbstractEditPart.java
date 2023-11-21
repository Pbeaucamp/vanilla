package bpm.sqldesigner.ui.editpart;

import java.beans.PropertyChangeListener;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.ui.view.tab.RequestListener;


public abstract class AppAbstractEditPart extends AbstractGraphicalEditPart
		implements PropertyChangeListener {
	public RequestListener requestListener;
	
	@Override
	public void activate() {
		super.activate();
		((Node) getModel()).addPropertyChangeListener(this);
		
	}

	@Override
	public void deactivate() {
		super.deactivate();
		((Node) getModel()).removePropertyChangeListener(this);
	}

}