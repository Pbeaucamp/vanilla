package bpm.vanilla.repository.ui.wizards.page;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import bpm.vanilla.designer.ui.common.IDesignerActivator;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.repository.ui.Activator;
import bpm.vanilla.repository.ui.Messages;

public class ExportObjectWizard extends Wizard implements IImportWizard{


	
//	private RepositorySelectionPage connectionPage;
//	private PageConnection selectConnectionPage;
	private PageConnectionDefinition defPage;
	protected RepositoryExportPage contentPage;
	
	
	private IRepositoryApi sock;
	private IVanillaAPI vanillaApi;
	private IDesignerActivator<?> designerActivator;
	
	public ExportObjectWizard(IDesignerActivator<?> designerActivator){
		this.designerActivator = designerActivator;
	}
	public ExportObjectWizard(){
		designerActivator = Activator.getDefault().getDesignerActivator();
	}
	
	public void setDesignerActivator(IDesignerActivator<?> designer){
		this.designerActivator = designer;
	}
	
	@Override
	public boolean performFinish() {
		return true;
	}
	
	@Override
	public void addPages() {
		if (Activator.getDefault().getDesignerActivator() == null || Activator.getDefault().getDesignerActivator().getRepositoryContext() == null){
//			selectConnectionPage = new PageConnection("Connection");
//			selectConnectionPage.setTitle("Select a Repository Connection");
//			addPage(selectConnectionPage);
			
			defPage = new PageConnectionDefinition(Messages.ExportObjectWizard_0);
			defPage.setTitle(Messages.ExportObjectWizard_1);
			addPage(defPage);
			
			
		}
		
		
		addContentPage();
		
		
	}
	
	protected void addContentPage(){
		contentPage = new RepositoryExportPage(Messages.ExportObjectWizard_2);
		contentPage.setTitle(Messages.ExportObjectWizard_3);
		addPage(contentPage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
				
		IWizardPage p = super.getNextPage(page);
		IRepositoryContext ctx = null;
		
		
		if (defPage != null){
			ctx = defPage.getRepositoryContext();
			designerActivator.setRepositoryContext(ctx);
			sock = designerActivator.getRepositoryConnection();
		}
		else{
			ctx = designerActivator.getRepositoryContext();
			sock = designerActivator.getRepositoryConnection();
		}
		
		if (p == contentPage){
			try {
				contentPage.fillContent(ctx, sock);
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), Messages.ExportObjectWizard_4, Messages.ExportObjectWizard_5 + e.getMessage());
			}
			
		}

		
		
		return p;
		

		
		
	}
	
	public IRepositoryApi getRepositoryConnection(){
		return sock;
	}
	
	public IVanillaAPI getVanillaApi(){
		if(vanillaApi == null){
			IRepositoryContext ctx = Activator.getDefault().getDesignerActivator().getRepositoryContext();
			IVanillaContext vanillaCtx = new BaseVanillaContext(ctx.getVanillaContext().getVanillaUrl(), 
					ctx.getVanillaContext().getLogin(), ctx.getVanillaContext().getPassword());
			
			vanillaApi = new RemoteVanillaPlatform(vanillaCtx);	
		}
		return vanillaApi;
	}
	
	protected void setVanillaApi(IVanillaAPI vanillaApi){
		this.vanillaApi = vanillaApi;
	}
	
	protected void setRepositoryConnection(IRepositoryApi sock){
		this.sock = sock;

	}
	
	public IDesignerActivator<?> getDesignerActivator(){
		return designerActivator;
	}

	@Override
		public boolean canFinish() {

			return true;
		}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}
}
