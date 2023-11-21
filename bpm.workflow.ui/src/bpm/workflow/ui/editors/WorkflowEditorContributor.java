package bpm.workflow.ui.editors;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IStructuredSelection;

import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;
import bpm.workflow.ui.icons.IconsNames;

/**
 * Contributor of the editor (zooms...)
 * @author CHARBONNIER, MARTIN
 *
 */
public class WorkflowEditorContributor extends ActionBarContributor {

	private Action alignSelectedHorizontalLeft;
	private Action alignSelectedHorizontalRight;
	private Action alignSelectedVerticalTop;
	private Action alignSelectedVerticalBottom;
	
	@Override
	protected void buildActions() {
		 addRetargetAction(new ZoomInRetargetAction());
         addRetargetAction(new ZoomOutRetargetAction());

         ImageRegistry reg = Activator.getDefault().getImageRegistry();
         alignSelectedHorizontalLeft = new Action(Messages.WorkflowEditorContributor_0){
        	 public void run(){
        		 
        		 IStructuredSelection s = (IStructuredSelection)((WorkflowMultiEditorPart)getPage().getActiveEditor()).getSite().getSelectionProvider().getSelection();
        		 
        		 Integer minX = null;
        		 
        		 for(Object o : s.toList()){
        			 if (o instanceof NodePart){
        				 Node node = (Node)((NodePart)o).getModel();
        				
        				 if (minX == null || node.getX() < minX){
        					 minX = node.getX();
        				 }
        			 }
        		 }
        		 
        		 for(Object o : s.toList()){
        			 if (o instanceof NodePart){
        				 Node node = (Node)((NodePart)o).getModel();
        				 Rectangle r = new Rectangle(node.getLayout());
        				 r.x = minX;
        				 
        				 node.setLayout(r);
        			 }
        			 
        		 }
        	 }
         };
         alignSelectedHorizontalLeft.setToolTipText(Messages.WorkflowEditorContributor_1);
         alignSelectedHorizontalLeft.setImageDescriptor(reg.getDescriptor(IconsNames.align_left_16));
         
         alignSelectedHorizontalLeft.setId("bpm.gateway.editor.alignHorizontalLeft"); //$NON-NLS-1$
         addAction(alignSelectedHorizontalLeft);
         
         
         alignSelectedHorizontalRight = new Action(Messages.WorkflowEditorContributor_2){
        	 public void run(){
        		 
        		 IStructuredSelection s = (IStructuredSelection)((WorkflowMultiEditorPart)getPage().getActiveEditor()).getSite().getSelectionProvider().getSelection();
        		 
        		 Integer maxX = null;
        		 
        		 for(Object o : s.toList()){
        			 if (o instanceof NodePart){
        				 Node node = (Node)((NodePart)o).getModel();
        				
        				 if (maxX == null || node.getX() > maxX){
        					 maxX = node.getX();
        				 }
        			 }
        		 }
        		 
        		 for(Object o : s.toList()){
        			 if (o instanceof NodePart){
        				 Node node = (Node)((NodePart)o).getModel();
        				 Rectangle r = new Rectangle(node.getLayout());
        				 r.x = maxX;
        				 
        				 node.setLayout(r);
        			 }
        			 
        		 }
        	 }
         };
         alignSelectedHorizontalRight.setId("bpm.gateway.editor.alignHorizontalRight"); //$NON-NLS-1$
         alignSelectedHorizontalRight.setToolTipText(Messages.WorkflowEditorContributor_3);
         alignSelectedHorizontalRight.setImageDescriptor(reg.getDescriptor(IconsNames.align_rigth_16));
         addAction(alignSelectedHorizontalRight);
         
         
         
         
         alignSelectedVerticalBottom = new Action(Messages.WorkflowEditorContributor_4){
        	 public void run(){
        		 
        		 IStructuredSelection s = (IStructuredSelection)((WorkflowMultiEditorPart)getPage().getActiveEditor()).getSite().getSelectionProvider().getSelection();
        		 
        		 Integer maxY = null;
        		 
        		 for(Object o : s.toList()){
        			 if (o instanceof NodePart){
        				 Node node = (Node)((NodePart)o).getModel();
        				
        				 if (maxY == null || node.getY() > maxY){
        					 maxY = node.getY();
        				 }
        			 }
        		 }
        		 
        		 for(Object o : s.toList()){
        			 if (o instanceof NodePart){
        				 Node node = (Node)((NodePart)o).getModel();
        				 Rectangle r = new Rectangle(node.getLayout());
        				 r.y = maxY;
        				 
        				 node.setLayout(r);
        			 }
        			 
        		 }
        	 }
         };
         alignSelectedVerticalBottom.setId("bpm.gateway.editor.alignVerticalBottom"); //$NON-NLS-1$
         alignSelectedVerticalBottom.setToolTipText(Messages.WorkflowEditorContributor_5);
         alignSelectedVerticalBottom.setImageDescriptor(reg.getDescriptor(IconsNames.align_botttom_16));

         addAction(alignSelectedVerticalBottom);
         
         
         
         alignSelectedVerticalTop = new Action(Messages.WorkflowEditorContributor_6){
        	 public void run(){
        		 
        		 IStructuredSelection s = (IStructuredSelection)((WorkflowMultiEditorPart)getPage().getActiveEditor()).getSite().getSelectionProvider().getSelection();
        		 
        		 Integer minY = null;
        		 
        		 for(Object o : s.toList()){
        			 if (o instanceof NodePart){
        				 Node node = (Node)((NodePart)o).getModel();
        				
        				 if (minY == null || node.getY() < minY){
        					 minY = node.getY();
        				 }
        			 }
        		 }
        		 
        		 for(Object o : s.toList()){
        			 if (o instanceof NodePart){
        				 Node node = (Node)((NodePart)o).getModel();
        				 Rectangle r = new Rectangle(node.getLayout());
        				 r.y = minY;
        				 
        				 node.setLayout(r);
        			 }
        			 
        		 }
        	 }
         };
         alignSelectedVerticalTop.setId("bpm.gateway.editor.alignVerticalTop"); //$NON-NLS-1$
         alignSelectedVerticalTop.setToolTipText(Messages.WorkflowEditorContributor_7);
         alignSelectedVerticalTop.setImageDescriptor(reg.getDescriptor(IconsNames.align_top_16));

         addAction(alignSelectedVerticalTop);
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
	 */
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		super.contributeToToolBar(toolBarManager);
		 toolBarManager.add(new Separator());
         toolBarManager.add(getAction(GEFActionConstants.ZOOM_IN));
         toolBarManager.add(getAction(GEFActionConstants.ZOOM_OUT));
         toolBarManager.add(new ZoomComboContributionItem(getPage()));
         
         toolBarManager.add(new Separator());
         toolBarManager.add(getAction(alignSelectedHorizontalLeft.getId()));
         toolBarManager.add(getAction(alignSelectedHorizontalRight.getId()));
         toolBarManager.add(getAction(alignSelectedVerticalBottom.getId()));
         toolBarManager.add(getAction(alignSelectedVerticalTop.getId()));
	}



	@Override
	protected void declareGlobalActionKeys() {
		
	}

}
