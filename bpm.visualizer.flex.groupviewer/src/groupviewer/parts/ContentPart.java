package groupviewer.parts;

import groupviewer.figure.ContentFigure;
import groupviewer.models.ContentModel;
import groupviewer.models.GroupModel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
/**
 * Content Part
 * This Part is the main Parent of all other EditPart.
 * It only contains Group Figure.
 * It display group's figures  in 3 colum's grid, with the same width.
 * 
 * @author admin
 *
 */
public class ContentPart extends AbstractGraphicalEditPart {

	//TODO verifier les donn�es du root model
	@SuppressWarnings("unused")
	private ContentModel model;
	private ContentFigure figure;
		 
    public ContentPart() {	
    }    
    /**
     * Set Model & Create Figure
     */
	@Override
	protected IFigure createFigure() {
		if (getModel() instanceof ContentModel)
			this.model = (ContentModel) getModel();
		figure = new ContentFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		//installEditPolicy(EditPolicy.LAYOUT_ROLE, new EditLayoutPolicy());   
	}
	/**
	 * Adding the Group Figure the content part displays
	 */
	@Override
	protected void addChildVisual(EditPart childEditPart, int index) {
		if (childEditPart instanceof AbstractGraphicalEditPart) {
			AbstractGraphicalEditPart part = (AbstractGraphicalEditPart) childEditPart;
			IFigure child = part.getFigure();
			getContentPane().add(child, new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		}	
	}
	/**
	 * Add a new group 
	 * If the GroupPart doesn't exist than it will be Created.
	 * @param group
	 */
	public void addGroup(Group group){
		if (findGroupPart(group) == null){
			addNewGroup(group);
		}	
	}
	/**
	 * Add a List of users to the specified group.
	 * 
	 * @param grp - the group where the users will be added
	 * @param users - the users List
	 */
	public void addUsersToGroup(Group grp, List<User> users) {
		GroupPart part = findGroupPart(grp);
		if (part == null){
			addNewGroup(grp);
			part = findGroupPart(grp);
		}
				
		for (User user : users){
			addNewUser(part, user);
		}	
	}
	/**
	 * Add user to the specified group.
	 * 
	 * @param grp - the group where the user will be added
	 * @param users - the users to be add
	 */
	public void addUserToGroup(Group grp, User user) {
		GroupPart part = findGroupPart(grp);
		if (part == null){
			addNewGroup(grp);
			part = findGroupPart(grp);
		}
	}
	/**
	 * Return the GroupPart for a Group in parameter, or null
	 * if GroupPart doesn't exist
	 * 
	 * @param group - the groupPart to be find
	 * @return GroupPart or null
	 */
	private GroupPart findGroupPart(Group group)
	{
		return findGroupPartByID(group.getId());
	}
	/**
	 * Return the GroupPart for a Group ID in parameter, or null
	 * if GroupPart doesn't exist
	 * 
	 * @param group - the groupPart to be find
	 * @return GroupPart or null
	 */
	private GroupPart findGroupPartByID(int groupID){
		GroupPart retour = null;
		for (Object o : getChildren()){
			if (o instanceof GroupPart) {
				GroupPart child = (GroupPart) o;
				if (((GroupModel)child.getModel()).getGroupID() == groupID)
					retour = child;	
			}
		}
		return retour;
	}
	/**
	 * Add a new GroupEditPart as Child of this figure.
	 * 
	 * @param group - The Group Data
	 * @return a new GroupPart
	 */
	private void addNewGroup(Group group) {
		GroupModel model = new GroupModel(group);
		EditPart part = getViewer().getEditPartFactory().createEditPart(this, model);
		addChild(part, getChildren().size());
		if (group.getParentId() != null){
			EditPart parent = findGroupPartByID(group.getParentId());
			RelationPart relation = new RelationPart();
			
			relation.setSource(parent);
			relation.setTarget(part);
			relation.setParent(this);
		}
	}
	/**
	 * Call AddNewUser function of the specified GroupPart
	 * 
	 * @param editPart - The GroupEdit part where the new user will be added.
	 * @param user - The new User to be add.
	 */
	private void addNewUser(GroupPart editPart, User user){
		editPart.addNewUser(user);
	}
	/**
	 * Remove all the child figure.
	 */
	@SuppressWarnings("unchecked")
	public void removeAll(){	
		ArrayList<Object> l =new ArrayList<Object>(getChildren());
		for (Object o : l) {
			removeChild((EditPart) o);
		}		
	}
}
