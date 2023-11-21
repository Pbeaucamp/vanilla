package bpm.fd.design.ui.structure.gef.commands;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.internal.wizards.NewWizardRegistry;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.wizard.IWizardComponent;

public class BuildComponentInStackableCellCommand extends Command{

	private Object parent;
	private Class<?> newElement;
	private Shell shell;
	
	private IComponentDefinition ceatedComponent;
	
	public BuildComponentInStackableCellCommand(Shell shell){
		this.shell = shell;
	}
	
	public void setParent(Object parent){
		this.parent = parent;
		
	}
	
	
	public void setNewElement(Class<?> element){
		this.newElement = element;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	@Override
	public boolean canExecute() {
		boolean b = parent != null && IComponentDefinition.class.isAssignableFrom(newElement) && parent instanceof StackableCell;
		
		return b;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	@Override
	public boolean canUndo() {
		return parent != null && ceatedComponent != null ;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		IWizard wiz = null;
		
		for(IWizardCategory c : NewWizardRegistry.getInstance().getRootCategory().getCategories()){
			if (c.getId().equals("bpm.fd.design.ui.freedashComponentCategory")){ //$NON-NLS-1$
				for(IWizardDescriptor d : c.getWizards()){
					try {
						IWorkbenchWizard _w = d.createWizard();
						
						if (_w instanceof IWizardComponent){
							if (((IWizardComponent)_w).getComponentClass() == newElement){
								wiz = _w;
								break;
							}
						}
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}			
			}
			
		}
		
		if (wiz == null){
			MessageDialog.openWarning(shell, Messages.BuildComponentInStackableCellCommand_1, Messages.BuildComponentInStackableCellCommand_2);
			return;
		}
		
		

		WizardDialog d = new WizardDialog(shell, wiz);
		d.setMinimumPageSize(800, 600);
		if (d.open() == WizardDialog.OK){
			ceatedComponent = ((IWizardComponent)wiz).getComponent();
			((StackableCell)parent).addBaseElementToContent(ceatedComponent);
			
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		((StackableCell)parent).addBaseElementToContent(ceatedComponent);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		((StackableCell)parent).removeBaseElementToContent(ceatedComponent);
		
	}
	
	


}
