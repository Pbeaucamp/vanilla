package bpm.gateway.ui.gatewaywizard;

import java.util.Properties;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.editors.GatewayEditorPart;
import bpm.gateway.ui.i18n.Messages;

public class GatewayWizard extends Wizard implements INewWizard {

	private static final String INFORMATION_PAGE_NAME = Messages.GatewayWizard_0;
	private static final String INFORMATION_PAGE_DESCRIPTION = Messages.GatewayWizard_1;
	private static final String INFORMATION_PAGE_TITLE = Messages.GatewayWizard_2;
	
	
	private GatewayInformationPage informationPage;
	
	protected GatewayEditorInput input;
	
	public GatewayWizard(){}
	public GatewayWizard(GatewayEditorInput input) {
		this.input = input;
	}

	@Override
	public boolean performFinish() {
		
		Properties p = informationPage.getProperties();
		DocumentGateway doc = null;
		
			
		if (input != null){
			 doc = input.getDocumentGateway();
			 doc.setAuthor((String)p.get("author")); //$NON-NLS-1$
			doc.setDescription((String)p.get("description")); //$NON-NLS-1$
				doc.setName((String)p.get("name")); //$NON-NLS-1$
				doc.setMode((String)p.get("mode")); //$NON-NLS-1$
				Activator.getDefault().getSessionSourceProvider().setDirectoryItemId(null);
				Activator.getDefault().getSessionSourceProvider().setCheckedIn(false);
			return  true;
		}
		else{
			doc = new DocumentGateway();
			doc.setRepositoryContext(bpm.gateway.ui.Activator.getDefault().getRepositoryContext());
			doc.setAuthor((String)p.get("author")); //$NON-NLS-1$
			doc.setDescription((String)p.get("description")); //$NON-NLS-1$
			doc.setName((String)p.get("name")); //$NON-NLS-1$
			doc.setProjectName(doc.getName());
			if (!doc.getName().endsWith(".gateway")){ //$NON-NLS-1$
				doc.setName(doc.getName() + ".gateway"); //$NON-NLS-1$
			}
			doc.setMode((String)p.get("mode")); //$NON-NLS-1$
			
			GatewayEditorInput input = new GatewayEditorInput((String)p.get("fileName"), doc); //$NON-NLS-1$
			
			try {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				page.openEditor(input, GatewayEditorPart.ID, true);
				Activator.getDefault().getSessionSourceProvider().setDirectoryItemId(null);
				Activator.getDefault().getSessionSourceProvider().setModelOpened(true);
				Activator.getDefault().getSessionSourceProvider().setCheckedIn(false);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		

	}
	
	
	@Override
	public void addPages() {
		
		informationPage = new GatewayInformationPage(INFORMATION_PAGE_NAME);
		informationPage.setDescription(INFORMATION_PAGE_DESCRIPTION);
		informationPage.setTitle(INFORMATION_PAGE_TITLE);
		
		addPage(informationPage);
	
	}

}
