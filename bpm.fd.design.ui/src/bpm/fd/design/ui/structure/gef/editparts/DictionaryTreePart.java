package bpm.fd.design.ui.structure.gef.editparts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.widgets.Widget;

import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.internal.FdComponentType;


public class DictionaryTreePart extends AbstractTreeEditPart{

	
	public static String[] nodes = new String[]{Messages.DictionaryTreePart_0, Messages.DictionaryTreePart_1};
	
	private List nodeLst = new ArrayList();
	
	
	public DictionaryTreePart() {
		super();
		nodeLst.addAll(FdComponentType.getComponentsTypes());
		for(String s : nodes){
			nodeLst.add(s);
		}
		
	}


	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getText()
	 */
	@Override
	protected String getText() {
		
		return ((Dictionary)getModel()).getName();
	}


	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	@Override
	protected List getModelChildren() {
		return nodeLst;
//		List l = new ArrayList(((Dictionary)getModel()).getComponents());
//		l.add(Table.class);
//		return l;
	}


	@Override
	protected void createEditPolicies() {
//		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy(){
//
//			/* (non-Javadoc)
//			 * @see org.eclipse.gef.editpolicies.ComponentEditPolicy#createDeleteCommand(org.eclipse.gef.requests.GroupRequest)
//			 */
//			@Override
//			protected Command createDeleteCommand(GroupRequest deleteRequest) {
//				DeleteStructureElementCommand command = new DeleteStructureElementCommand();
//				command.setNewElement((IStructureElement)getHost().getModel());
//				command.setParent(getHost().getParent().getModel());
//				
//				
//				return command;
//			}
//			
//		});
		
	}


	@Override
	public void setModel(Object model) {
	
		super.setModel(model);
		
	}


	@Override
	public void setWidget(Widget widget) {
		super.setWidget(widget);
		
	}
	
	
}
