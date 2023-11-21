package bpm.gateway.ui.resource.variables.wizard;

import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.views.ModelViewPart;
import bpm.gateway.ui.views.ResourceViewPart;

public class VariableWizard extends Wizard implements INewWizard {

	protected static final String GENERAL_PAGE_NAME = "Variable Definition"; //$NON-NLS-1$
	
	private VariablePage variablePage;
	public VariableWizard() {
		
	}

	@Override
	public boolean performFinish() {
		Properties p = variablePage.getValues();
		
		
		Variable v = new Variable();
		v.setName(p.getProperty("name")); //$NON-NLS-1$
		v.setValue(p.getProperty("value")); //$NON-NLS-1$
		v.setScope((Integer)p.get("type")); //$NON-NLS-1$
		v.setType((Integer)p.get("dataType")); //$NON-NLS-1$
		
		
		if (v.getScope() == Variable.LOCAL_VARIABLE){
			GatewayEditorInput in = (GatewayEditorInput)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
			try {
				in.getDocumentGateway().addVariable(v);
			} catch (Exception e) {
				ErrorDialog.openError(getShell(), Messages.VariableWizard_5, e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
				return false;
			}
			
//			ModelViewPart view = (ModelViewPart)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ModelViewPart.ID);
			ResourceViewPart view = (ResourceViewPart)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ResourceViewPart.ID);
			
			if (view != null){
				view.refresh();
			}
		}
		else{
			ResourceManager.getInstance().addVariable(v);
			
			ResourceViewPart view = (ResourceViewPart)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ResourceViewPart.ID);
			
			if (view != null){
				view.refresh();
			}
		}
		
		
		return true;
		
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		

	}
	@Override
	public void addPages() {

		variablePage = new VariablePage(GENERAL_PAGE_NAME);
		variablePage.setDescription(Messages.VariableWizard_6);
		variablePage.setTitle(Messages.VariableWizard_7);
		addPage(variablePage);
	}
}
