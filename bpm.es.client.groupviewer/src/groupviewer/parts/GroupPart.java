package groupviewer.parts;

import groupviewer.figure.GroupFigure;
import groupviewer.figure.UserFigure;
import groupviewer.models.GroupModel;
import groupviewer.models.UserModel;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.vanilla.platform.core.beans.User;
/**
 * The group part contains UserPart.
 * it displays users figures into Group container.
 * 
 * @author admin
 */
public class GroupPart extends AbstractGraphicalEditPart{

	
	private GroupModel model;
	private GroupFigure figure;
	/**
	 * Add a new user with UserData
	 * @param user - the user to be add.
	 */
	public void addNewUser(User user) {
			((GroupModel) getModel()).addUser(user);
			UserModel userMod = new UserModel(user);
			userMod.setParentId(model.getGroupID());
			EditPart part = getViewer().getEditPartFactory().createEditPart(this, userMod);
			addChild(part, getChildren().size());		
	}
	/**
	 * Create the Group Figure with the model's data.
	 */
	@Override
	protected IFigure createFigure() {	
		model = (GroupModel) getModel();
		figure = new GroupFigure(model.getGroup());
		return figure;	
	}
	/**
	 * Add User Figure into the Group Figure.
	 */
	@Override
	protected void addChildVisual(EditPart childEditPart, int index) {
		IFigure child = ((GraphicalEditPart)childEditPart).getFigure();
		figure.addUser((UserFigure)child);
	}
	
	@Override
	protected void createEditPolicies() {
		//installEditPolicy(EditPolicy.LAYOUT_ROLE, new EditLayoutPolicy());   
	}
	/**
	 * TEST Function.
	 */
	@Override
	protected void refreshVisuals() {
		((GroupFigure)getFigure()).setTitle(model.getGroupName());
		super.refreshVisuals();
	}
}
