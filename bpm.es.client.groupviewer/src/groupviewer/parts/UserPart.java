package groupviewer.parts;

import groupviewer.figure.UserFigure;
import groupviewer.models.UserModel;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;


/**
 * The UserPart contains a UserFigure and a UserModel
 * This part is only created by his own GroupPart.
 * 
 * @author admin
 *
 */
public class UserPart extends AbstractGraphicalEditPart {
	
	private UserModel model;
	private IFigure figure;
	/**
	 * Return the user's figure.
	 */
	@Override
	protected IFigure createFigure() {
		this.model = (UserModel) getModel();
		this.figure = new UserFigure(model.getUserData());
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		
	}

}
