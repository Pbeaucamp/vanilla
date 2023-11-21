package groupviewer.factory;

import groupviewer.models.ContentModel;
import groupviewer.models.GroupModel;
import groupviewer.models.UserModel;
import groupviewer.parts.ContentPart;
import groupviewer.parts.GroupPart;
import groupviewer.parts.UserPart;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

public class GroupViewFactory implements EditPartFactory {

	/**
	 * Add Model into part and set relation Parent / Child.
	 */
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart createdPart = getPartForElement(model);
		if (context != null)
			createdPart.setParent(context);
		createdPart.setModel(model);
		return createdPart;
	}
	/**
	 * Get the wright Part for a Model.
	 * 
	 * @param model - the data model.
	 * @return EditPart
	 */
	private EditPart getPartForElement(Object model) {
		EditPart ret = null;
		
		if (model instanceof ContentModel){
			ret = new ContentPart();
		}
		if (model instanceof GroupModel)
			ret = new GroupPart();
		
		if (model instanceof UserModel)
			ret = new UserPart();
		return ret;
	}

}
