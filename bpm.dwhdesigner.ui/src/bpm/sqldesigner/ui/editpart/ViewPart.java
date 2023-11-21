package bpm.sqldesigner.ui.editpart;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;

import bpm.sqldesigner.api.model.Node;

public class ViewPart extends AppAbstractEditPart {

	@Override
	protected IFigure createFigure() {
				
		return new Figure();
	}

	@Override
	protected void setFigure(IFigure figure) {

	}

	@Override
	protected void refreshVisuals() {

	}

	public void propertyChange(PropertyChangeEvent evt) {
	}

	@Override
	protected void createEditPolicies() {
		
	}
}