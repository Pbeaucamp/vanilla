package bpm.gateway.ui.editors;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.SharedImages;
import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.ActionFactory;

import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;
import bpm.gateway.ui.views.ViewPalette;

public class GatewayEditorContributor extends ActionBarContributor {

	
	private Action alignSelectedHorizontalLeft;
	private Action alignSelectedHorizontalRight;
	private Action alignSelectedVerticalTop;
	private Action alignSelectedVerticalBottom;
	
	private Action activatePaletteSelection;
	private Action activatePaletteLink;
	
	@Override
	protected void buildActions() {
		
		 addRetargetAction(new ZoomInRetargetAction());
         addRetargetAction(new ZoomOutRetargetAction());
         addRetargetAction(new UndoRetargetAction());
         addRetargetAction(new RedoRetargetAction());
       
         
         ImageRegistry reg = Activator.getDefault().getImageRegistry();
         
         alignSelectedHorizontalLeft = new Action(Messages.GatewayEditorContributor_0){
        	 public void run(){
        		 
        		 IStructuredSelection s = (IStructuredSelection)((GatewayEditorPart)getPage().getActiveEditor()).getSite().getSelectionProvider().getSelection();
        		 
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
         alignSelectedHorizontalLeft.setToolTipText(Messages.GatewayEditorContributor_1);
         alignSelectedHorizontalLeft.setImageDescriptor(reg.getDescriptor(IconsNames.align_left_16));
         
         alignSelectedHorizontalLeft.setId("bpm.gateway.editor.alignHorizontalLeft"); //$NON-NLS-1$
         addAction(alignSelectedHorizontalLeft);
         
         
         alignSelectedHorizontalRight = new Action(Messages.GatewayEditorContributor_3){
        	 public void run(){
        		 
        		 IStructuredSelection s = (IStructuredSelection)((GatewayEditorPart)getPage().getActiveEditor()).getSite().getSelectionProvider().getSelection();
        		 
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
         alignSelectedHorizontalRight.setToolTipText(Messages.GatewayEditorContributor_5);
         alignSelectedHorizontalRight.setImageDescriptor(reg.getDescriptor(IconsNames.align_rigth_16));
         addAction(alignSelectedHorizontalRight);
         
         
         
         
         alignSelectedVerticalBottom = new Action(Messages.GatewayEditorContributor_6){
        	 public void run(){
        		 
        		 IStructuredSelection s = (IStructuredSelection)((GatewayEditorPart)getPage().getActiveEditor()).getSite().getSelectionProvider().getSelection();
        		 
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
         alignSelectedVerticalBottom.setToolTipText(Messages.GatewayEditorContributor_8);
         alignSelectedVerticalBottom.setImageDescriptor(reg.getDescriptor(IconsNames.align_botttom_16));

         addAction(alignSelectedVerticalBottom);
         
         
         
         alignSelectedVerticalTop = new Action(Messages.GatewayEditorContributor_9){
        	 public void run(){
        		 
        		 IStructuredSelection s = (IStructuredSelection)((GatewayEditorPart)getPage().getActiveEditor()).getSite().getSelectionProvider().getSelection();
        		 
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
         alignSelectedVerticalTop.setToolTipText(Messages.GatewayEditorContributor_11);
         alignSelectedVerticalTop.setImageDescriptor(reg.getDescriptor(IconsNames.align_top_16));

         addAction(alignSelectedVerticalTop);
         
         
         
         activatePaletteSelection =  new Action(Messages.GatewayEditorContributor_12){
        	 public void run(){
        		 ViewPalette v = (ViewPalette)getPage().findView(ViewPalette.ID);
        		 v.activateToolEntry(ViewPalette.TOOL_ENTRY_SELECT);
        	 }
         };
         activatePaletteSelection.setId("bpm.gateway.editor.selectPaletteSelection"); //$NON-NLS-1$
         activatePaletteSelection.setToolTipText(Messages.GatewayEditorContributor_14);
         activatePaletteSelection.setImageDescriptor(SharedImages.DESC_SELECTION_TOOL_16);
         addAction(activatePaletteSelection);
	
	
         activatePaletteLink =  new Action(Messages.GatewayEditorContributor_15){
        	 public void run(){
        		 ViewPalette v = (ViewPalette)getPage().findView(ViewPalette.ID);
        		 v.activateToolEntry(ViewPalette.TOOL_ENTRY_LINK);
        	 }
         };
         activatePaletteLink.setId("bpm.gateway.editor.selectPaletteLink"); //$NON-NLS-1$
         activatePaletteLink.setToolTipText(Messages.GatewayEditorContributor_17);
         activatePaletteLink.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.relation_16));
         addAction(activatePaletteLink);
         
	}
	
	
	
	

	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
	 */
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		
		super.contributeToToolBar(toolBarManager);
		 toolBarManager.add(new Separator());
		 toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
		 toolBarManager.add(getAction(ActionFactory.REDO.getId()));
		 
		 toolBarManager.add(new Separator());
         toolBarManager.add(getAction(GEFActionConstants.ZOOM_IN));
         toolBarManager.add(getAction(GEFActionConstants.ZOOM_OUT));
         toolBarManager.add(new ZoomComboContributionItem(getPage()));
         
         toolBarManager.add(new Separator());
         toolBarManager.add(getAction(alignSelectedHorizontalLeft.getId()));
         toolBarManager.add(getAction(alignSelectedHorizontalRight.getId()));
         toolBarManager.add(getAction(alignSelectedVerticalBottom.getId()));
         toolBarManager.add(getAction(alignSelectedVerticalTop.getId()));
         
         toolBarManager.add(new Separator());
         toolBarManager.add(getAction(activatePaletteSelection.getId()));
         toolBarManager.add(getAction(activatePaletteLink.getId()));
         
	}



	@Override
	protected void declareGlobalActionKeys() {
		
		
	}

}
