package bpm.fd.design.ui.editors;

import java.util.Locale;

import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.actions.ActionGenerateLocalisationFiles;
import bpm.fd.design.ui.editor.part.LayoutEditor;
import bpm.fd.design.ui.editor.part.actions.ActionAlign;
import bpm.fd.design.ui.editor.part.actions.ActionAlign.Type;
import bpm.fd.design.ui.editor.part.actions.ActionCopy;
import bpm.fd.design.ui.editors.dialogs.DialogLocale;
import bpm.fd.design.ui.editors.dialogs.DialogProjectDescription;
import bpm.fd.design.ui.icons.Icons;

public class FdEditorContributor extends ActionBarContributor	 {
	 
	private Action addLocale;
	private Action editProjectDescriptor;
	private boolean layoutPage = false;
	private ZoomComboContributionItem zoomCbo;
	private Action alignLeft, alignRight, alignTop, alignBottom;
	
	private Action resizeHorizontal;
	private Action resizeVertical;
	
	private Action copyAction;
	private Action pasteAction;
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#getActionRegistry()
	 */
	@Override
	public ActionRegistry getActionRegistry() {
		return super.getActionRegistry();
	}

	public FdEditorContributor() {
	}

	@Override
	protected void buildActions() {
		addRetargetAction(new UndoRetargetAction());
		addRetargetAction(new RedoRetargetAction());
		addRetargetAction(new DeleteRetargetAction());
		addRetargetAction(new ZoomInRetargetAction());
        addRetargetAction(new ZoomOutRetargetAction());
		
		alignBottom = new ActionAlign(Type.BOTTOM);
		addAction(alignBottom);
		alignTop = new ActionAlign(Type.TOP);
		addAction(alignTop);
		alignLeft = new ActionAlign(Type.LEFT);
		addAction(alignLeft);
		alignRight = new ActionAlign(Type.RIGHT);
		addAction(alignRight);
		
		resizeHorizontal = new ActionAlign(Type.RESIZE_H);
		addAction(resizeHorizontal);
		resizeVertical= new ActionAlign(Type.RESIZE_V);
		addAction(resizeVertical);
		
		copyAction = new ActionCopy();
		addAction(copyAction);
		
		
		addLocale = new Action(Messages.FdEditorContributor_0){
			public void run(){
				DialogLocale d = new DialogLocale(getPage().getActivePart().getSite().getShell());
				
				if (d.open() != DialogLocale.OK){
					return;
				}
				
				for(Locale l : d.getLocale()){
					Activator.getDefault().getProject().addLocale(l.toString());
				}
				new ActionGenerateLocalisationFiles().run();
			}
		};
		addLocale.setId("bpm.fd.design.ui.editors.addLocaleAction"); //$NON-NLS-1$
		addLocale.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.locale));
		addAction(addLocale);
		
		editProjectDescriptor = new Action("Project Properties"){ //$NON-NLS-1$
			public void run(){
				DialogProjectDescription d = new DialogProjectDescription(getPage().getActivePart().getSite().getShell(), Activator.getDefault().getProject());
				
				if (d.open() != DialogProjectDescription.OK){
					return;
				}
				
				
			}
		};
		editProjectDescriptor.setId("bpm.fd.design.ui.editors.editProjectProperties"); //$NON-NLS-1$
		editProjectDescriptor.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.fd_16));
		addAction(editProjectDescriptor);
		
		
		
	}

	@Override
	protected void declareGlobalActionKeys() {
		addGlobalActionKey(ActionFactory.UNDO.getId());
		addGlobalActionKey(ActionFactory.REDO.getId());
		addGlobalActionKey(ActionFactory.COPY.getId());
		addGlobalActionKey(ActionFactory.PASTE.getId());
		addGlobalActionKey(ActionFactory.SELECT_ALL.getId());
		
	}
	
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(getAction(GEFActionConstants.ZOOM_IN));
		toolBarManager.add(getAction(GEFActionConstants.ZOOM_OUT));
		zoomCbo = new ZoomComboContributionItem(getPage());
		toolBarManager.add(zoomCbo);
		toolBarManager.add(new Separator());
        toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
        toolBarManager.add(getAction(ActionFactory.REDO.getId()));
        toolBarManager.add(new Separator());
        toolBarManager.add(editProjectDescriptor);
        toolBarManager.add(addLocale);
        toolBarManager.add(new Separator());
        
        toolBarManager.add(alignBottom);
        toolBarManager.add(alignTop);
        toolBarManager.add(alignRight);
        toolBarManager.add(alignLeft);
        toolBarManager.add(resizeHorizontal);
        toolBarManager.add(resizeVertical);
        
        toolBarManager.add(copyAction);
	}
	

	@Override
	public void setActiveEditor(IEditorPart editor) {
		/*
		 * we have to update the contributions(
		 * but take care if we are on the preview panel(meaning editor== null ||FdEdtor.getActiveEditor == null)
		 * 
		 */
		ZoomManager zm = null;
		
		if (editor instanceof FdEditor){
			if (((FdEditor)editor).getActiveEditor() == null){
				getActionBars().clearGlobalActionHandlers();
				layoutPage = false;
			}
			else if (((FdEditor)editor).getActiveEditor() instanceof StructureEditor){
				layoutPage = false;
				zm = ((IEditDomainProvider)((FdEditor)editor).getActiveEditor()).getZoomManager();
			}
			else if (((FdEditor)editor).getActiveEditor() instanceof LayoutEditor){
				layoutPage = true;
				zm = ((IEditDomainProvider)((FdEditor)editor).getActiveEditor()).getZoomManager();
			}
			
		}
		else if (editor == null){
			layoutPage = false;
			getActionBars().clearGlobalActionHandlers();
		}
		else if (editor instanceof LayoutEditor){
			layoutPage = true;
			super.setActiveEditor(editor);
			zm = ((IEditDomainProvider)editor).getZoomManager();
		}
		else{
			layoutPage = false;
			super.setActiveEditor(editor);
		}
		
		updateActions(zm);
		getActionBars().updateActionBars();
	}

	private void updateActions(ZoomManager zoomMgr){
		IToolBarManager m = getActionBars().getToolBarManager();
		m.find(ActionAlign.ALIGN_BOTTOM_ID).setVisible(layoutPage);
		m.find(ActionAlign.ALIGN_TOP_ID).setVisible(layoutPage);
		m.find(ActionAlign.ALIGN_LEFT_ID).setVisible(layoutPage);
		m.find(ActionAlign.ALIGN_RIGHT_ID).setVisible(layoutPage);
		m.find(ActionAlign.RESIZE_HORIZONTAL_ID).setVisible(layoutPage);
		m.find(ActionAlign.RESIZE_VERTICAL_ID).setVisible(layoutPage);
		
		zoomCbo.setZoomManager(zoomMgr);
		zoomCbo.update();
		m.update(true);
	}
	@Override
	public IActionBars getActionBars() {
		return super.getActionBars();
	}

}
