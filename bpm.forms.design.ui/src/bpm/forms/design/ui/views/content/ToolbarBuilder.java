package bpm.forms.design.ui.views.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.forms.IManagedForm;

import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IFormDefinition;
import bpm.forms.design.ui.Activator;
import bpm.forms.design.ui.Messages;
import bpm.forms.design.ui.dialogs.DialogVanillaGroupPicker;
import bpm.forms.design.ui.wizards.form.WizardForm;
import bpm.forms.design.ui.wizards.formdefinition.WizardFormDefintion;
import bpm.vanilla.platform.core.beans.Group;

public class ToolbarBuilder implements ISelectionChangedListener{

	private IManagedForm managedForm;
	private TreeViewer viewer;
	private ContentMasterDetailsBlock block; 
	private IAction addForm = new Action(Messages.ToolbarBuilder_0) {
		@Override
		public void run() {
			WizardForm wiz = new WizardForm();
			
			
			WizardDialog dial = new WizardDialog(managedForm.getForm().getShell(), wiz);
			dial.setBlockOnOpen(false);
			dial.setPageSize(800, 600);
			dial.setBlockOnOpen(true);
			if (dial.open() == WizardDialog.OK){
				((Collection)viewer.getInput()).add(wiz.getForm());
				viewer.refresh();
				runRefresh();
			}
		}
		
	};
	
	private IAction refresh = new Action(Messages.ToolbarBuilder_1) {
		@Override
		public void run() {
			
			if (!block.getDirtyObjects().isEmpty()){
				boolean b = MessageDialog.openQuestion(viewer.getControl().getShell(), Messages.ToolbarBuilder_2, Messages.ToolbarBuilder_3);
				if (!b){
					return;
				}
			}
			
			List<IForm> l = null;
			try{
				l = Activator.getDefault().getServiceProvider().getDefinitionService().getForms();
				
				
				for(IForm f : l){
					for(IFormDefinition fd : Activator.getDefault().getServiceProvider().getDefinitionService().getFormDefinitionVersions(f.getId())){
						f.addDefinition(fd);
					}
				}
				viewer.setInput(l);
			}catch(Exception ex){
				ex.printStackTrace();
				if (l == null){
					viewer.setInput(new ArrayList<IForm>());
					MessageDialog.openWarning(viewer.getControl().getShell(), Messages.ToolbarBuilder_4, 
							Messages.ToolbarBuilder_5);
				}
			}
			
		}
	};
	
	private IAction addDefinition = new Action(Messages.ToolbarBuilder_6) {
		@Override
		public void run() {
			IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
			if (ss.isEmpty()){
				return;
			}
			
			if (!(ss.getFirstElement() instanceof IForm)){
				return;
			}
			WizardFormDefintion wiz = new WizardFormDefintion();
			wiz.init(Activator.getDefault().getWorkbench(), (IStructuredSelection)viewer.getSelection());
			
			WizardDialog dial = new WizardDialog(managedForm.getForm().getShell(), wiz);
			dial.setBlockOnOpen(false);
			dial.setPageSize(800, 600);
			
			if (dial.open() == WizardDialog.OK){
				viewer.setExpandedState(((IStructuredSelection)viewer.getSelection()).getFirstElement(), false);
				runRefresh();
				viewer.refresh();
				viewer.setExpandedState(((IStructuredSelection)viewer.getSelection()).getFirstElement(), true);
			}
		}
	};
	
	private IAction del = new Action(Messages.ToolbarBuilder_7){
		@Override
		public void run() {
			IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
			
			StringBuffer errors = new StringBuffer();			
			for(Object o : ss.toList()){
				if (o instanceof IFormDefinition){
					try{
						Activator.getDefault().getServiceProvider().getDefinitionService().delete((IFormDefinition)o);
					}catch(Exception ex){
						errors.append(Messages.ToolbarBuilder_8 + ((IFormDefinition)o).getVersion() + " : " + ex.getMessage() + "\n"); //$NON-NLS-2$ //$NON-NLS-3$
					}
				}
				else if (o instanceof IForm){
					try{
						Activator.getDefault().getServiceProvider().getDefinitionService().delete((IForm)o);
					
					}catch(Exception ex){
						errors.append(Messages.ToolbarBuilder_11 + ((IForm)o).getName() + " : " + ex.getMessage() + "\n"); //$NON-NLS-2$ //$NON-NLS-3$
					}
				}
				
			}
			
			if (!errors.toString().equals("")){ //$NON-NLS-1$
				MessageDialog.openWarning(viewer.getTree().getShell(), Messages.ToolbarBuilder_15, errors.toString());
			}
			else{
				refresh.run();
			}
			
			
		}
		
	};
	
	private IAction commit = new Action("commit") { //$NON-NLS-1$
		public void run(){
			managedForm.commit(true);
		}
	};
	
	private IAction instanciate = new Action(Messages.ToolbarBuilder_17) {
		public void run(){
			
			IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
			
			if (ss.isEmpty()){
				return;
			}
			
			if (ss.getFirstElement() instanceof IForm){
				
				IForm f = (IForm)ss.getFirstElement();
				
				IFormDefinition activeDef = Activator.getDefault().getServiceProvider().getDefinitionService().getActiveFormDefinition(f);
				
				if (activeDef == null){
					MessageDialog.openInformation(viewer.getControl().getShell(), Messages.ToolbarBuilder_18, Messages.ToolbarBuilder_19);
					return;
				}
				
				
				DialogVanillaGroupPicker d = new DialogVanillaGroupPicker(viewer.getControl().getShell(), DialogVanillaGroupPicker.MULTIPLE_GROUPS);
				
				StringBuffer generated = new StringBuffer();
				StringBuffer errors = new StringBuffer();
				if (d.open() == DialogVanillaGroupPicker.OK){
					
					for(Group g : d.getGroup()){
						try{
							Activator.getDefault().getInstanceLauncher().launchForm(f, g.getId());
							generated.append(Messages.ToolbarBuilder_20 + g.getName() + "\n"); //$NON-NLS-2$
							
						}catch(Exception ex){
							ex.printStackTrace();
							errors.append(Messages.ToolbarBuilder_22 + g.getName() + "\n"); //$NON-NLS-2$
							
						}
					}
				}
				
				if (errors.toString().isEmpty()){
					MessageDialog.openInformation(viewer.getControl().getShell(), Messages.ToolbarBuilder_24, generated.toString());
				}
				else{
					MessageDialog.openError(viewer.getControl().getShell(), Messages.ToolbarBuilder_25, generated.toString() + "\n" + errors.toString()); //$NON-NLS-2$
				}
				
				
			}
			
		}
	};
	
	
	private IAction unlockDefinition = new Action(Messages.ToolbarBuilder_27){
		public void run(){
			IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
			
			if (ss.isEmpty()){
				return;
			}
			
			if (ss.getFirstElement() instanceof IFormDefinition){
//				((FormDefinition)ss.getFirstElement());
				
				((IFormDefinition)ss.getFirstElement()).setIsDesigned(false);
				try{
					Activator.getDefault().getServiceProvider().getDefinitionService().update((IFormDefinition)ss.getFirstElement());
					viewer.refresh();
					
				}catch(Exception ex){
					((IFormDefinition)ss.getFirstElement()).setIsDesigned(true);
					ex.printStackTrace();
					MessageDialog.openError(viewer.getControl().getShell(), Messages.ToolbarBuilder_28, Messages.ToolbarBuilder_29 + ex.getMessage());
				}
			}
		}
	};
	
	
	public ToolbarBuilder(IManagedForm form, TreeViewer viewer, ContentMasterDetailsBlock block){
		this.managedForm = form;
		this.block = block;
		this.viewer = viewer;
		
		viewer.addSelectionChangedListener(this);
	}
	
	
	public void runRefresh(){
		refresh.run();
	}
	
	public ToolBarManager createToolbarManager(){
		ToolBarManager tb = new ToolBarManager();
		
		tb.add(addForm);
		tb.add(addDefinition);
		tb.add(del);
		tb.add(refresh);
		tb.add(commit);
		tb.add(instanciate);
		tb.add(unlockDefinition);
		return tb;
	}




	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSelection().isEmpty()){
			addDefinition.setEnabled(false);
			commit.setEnabled(false);
			del.setEnabled(false);
			instanciate.setEnabled(false);
			
		}
		else{
			
			IStructuredSelection ss = (IStructuredSelection)event.getSelection();
			
			if (ss.getFirstElement() instanceof IForm){
				addDefinition.setEnabled(true);
				instanciate.setEnabled(true);
				
			}
			else{
				addDefinition.setEnabled(false);
				
				unlockDefinition.setEnabled(((IFormDefinition)ss.getFirstElement()).isDesigned());
			}
			del.setEnabled(true);
			commit.setEnabled(block.getDirtyObjects().contains(ss.getFirstElement()));
		}
		
		
	}
	
}
