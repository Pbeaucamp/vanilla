package bpm.metadata.ui.birtreport.wizards;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import bpm.metadata.ui.birtreport.tools.ConnectionHelper;
import bpm.vanilla.platform.core.IRepositoryContext;

public class BirtReportWizard extends Wizard {
	private BirtReportPage1 birtReportPage1;
	private BirtReportPreviewPage birtReportPreviewPage;
	private BirtReportExportPage contentPage;
	
//	private IRepositoryConnection sock;
//	private MetaData model;
	//private String vanillaUrl;
	private Integer itemId;
	
		
	//Error dialog parameters
	private final int ERROR_DIALOG = 1;
//	private final int WARNING_DIALOG = 2;
	private String errorModelInRepository = "Please, export your metadata to repository and then import it.";
	private String errorRepositoryConnection = "Please, connect to the repository and try again.";
//	private String warningModelInRepository = "If you have made any change in your model, please update it.";
	
	
	public BirtReportWizard(){	
		ConnectionHelper.getInstance().setSock(metadataclient.Activator.getDefault().getRepositoryConnection());
		this.itemId = metadataclient.Activator.getDefault().getCurrentModelDirectoryItemId();
		
		if (this.itemId == null){
			createErrorDialog(ERROR_DIALOG, errorRepositoryConnection);
		}
		else{
			IRepositoryContext ctx = metadataclient.Activator.getDefault().getRepositoryContext();
			ConnectionHelper.getInstance().setRepositoryUrl(ctx.getRepository().getUrl());
			ConnectionHelper.getInstance().setVanillaRuntimeUrl(ctx.getVanillaContext().getVanillaUrl());
			ConnectionHelper.getInstance().setGroupName(ctx.getGroup().getName());
			ConnectionHelper.getInstance().setLogin(ctx.getVanillaContext().getLogin());
			ConnectionHelper.getInstance().setPassword(ctx.getVanillaContext().getPassword());
//			ConnectionHelper.getInstance().setIsEncrypted(isEncrypted);
			ConnectionHelper.getInstance().setRepositoryId(ctx.getRepository().getId());
			ConnectionHelper.getInstance().setItemId(itemId);
		}
		
		
		
//		this.model = Activator.getDefault().getModel();
//		Boolean isRepositoryConnectionDefined = Activator.getDefault().isSetRepositoryConnection();
//		Boolean isModelInRepository = Activator.getDefault().isModelInRepository();
		
//		if(isRepositoryConnectionDefined){
//			sock = Activator.getDefault().getSock();
//			ConnectionHelper.getInstance().setSock(sock);
//			
//			String repositoryUrl = ((AxisRepositoryConnection)sock).getHost();
//			String login = ((AxisRepositoryConnection)sock).getUsername();
//			Boolean isEncrypted = (((AxisRepositoryConnection)sock).isEncrypted());
//			String password = ((AxisRepositoryConnection)sock).getPassword();
//			int groupId = ((AxisRepositoryConnection)sock).getGroupId();
//
//			//As long as we can't get the vanilla Url
//			InputDialog vanillaDial = new InputDialog(getShell(), "Vanilla Url : ", 
//					"Please enter the vanilla Url:", "http://localhost:8080/vanilla", null);
//			vanillaDial.open();
//			vanillaUrl = vanillaDial.getValue();
//			
//			Activator.getDefault().getVanillaUrl();
//			
//			IVanillaAPI api = new RemoteVanillaPlatform(vanillaUrl, login, password);
//			Repository rep = new Repository();
//			String groupName = "";
//			String vanillaRuntimeUrl = "";
//			int repositoryId = -1;
//			try {
//				groupName = ((AxisRepositoryConnection)sock).getGroupById(groupId);
//				rep = api.getVanillaRepositoryManager().getRepositoryFromUrl(repositoryUrl);
//				vanillaRuntimeUrl = vanillaUrl;
//				repositoryId = rep.getId();
//			} catch (Exception e) {
//				
//				e.printStackTrace();
//			}
//			
//			if(isModelInRepository){
//				this.itemId = Activator.getDefault().getModelDirectoryItemId();
//				
//				setConnectionHelperContent(repositoryUrl, vanillaRuntimeUrl, groupName, login, password, isEncrypted, repositoryId, itemId);
//			}
//			else{
//				createErrorDialog(ERROR_DIALOG, errorModelInRepository);
//			}
//		}
//		else{
//			createErrorDialog(ERROR_DIALOG, errorRepositoryConnection);
//		}
		
//		createErrorDialog(WARNING_DIALOG, warningModelInRepository);
	}
	
//	@Override
//	public void init(IWorkbench workbench, IStructuredSelection selection) {
//		this.setForcePreviousAndNextButtons(true);
//		this.setWindowTitle("Application wizard"); //$NON-NLS-1$
//		this.setForcePreviousAndNextButtons(false);
//		
//	}
	
	@Override
	public void addPages(){
		birtReportPage1 = new BirtReportPage1("Metadata", metadataclient.Activator.getDefault().getCurrentModel());
		birtReportPage1.setDescription("Metadata");
		birtReportPage1.setTitle("Metadata");
		addPage(birtReportPage1);
		
		birtReportPreviewPage = new BirtReportPreviewPage("Preview", itemId);
		birtReportPreviewPage.setDescription("This is the preview of the birt report");
		birtReportPreviewPage.setTitle("Preview Report");
		addPage(birtReportPreviewPage);

		contentPage = new BirtReportExportPage("Content");
		contentPage.setTitle("Repository Content");
		addPage(contentPage);
	}
	
	private void createErrorDialog(int type, String error){
		if(type == 1){
			MessageDialog.openError(getShell(), "Error", error);
			getShell().close();
		}
		else{
			MessageDialog.openInformation(getShell(), "Warning", error);
		}
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if(birtReportPage1.isPageComplete()){
		    birtReportPreviewPage.fillInformations(birtReportPage1.getGroupViewerInput(), 
		    		birtReportPage1.getColumnViewerInput(),
		    		birtReportPage1.getRessourcesViewerInput(),
		    		birtReportPage1.getBusinessPackageName(),
		    		birtReportPage1.getBusinessModelName());
		}
		if(birtReportPreviewPage.isPageComplete()){
				contentPage.fillContent(
						birtReportPreviewPage.getReport(), 
						metadataclient.Activator.getDefault().getRepositoryConnection());
		}
		return super.getNextPage(page);
	}
	
//	private void setConnectionHelperContent(String repositoryUrl, String vanillaRuntimeUrl, String groupName, 
//			String login, String password, Boolean isEncrypted, int repositoryId, int itemId){
//		ConnectionHelper.getInstance().setRepositoryUrl(repositoryUrl);
//		ConnectionHelper.getInstance().setVanillaRuntimeUrl(vanillaRuntimeUrl);
//		ConnectionHelper.getInstance().setGroupName(groupName);
//		ConnectionHelper.getInstance().setLogin(login);
//		ConnectionHelper.getInstance().setPassword(password);
//		ConnectionHelper.getInstance().setIsEncrypted(isEncrypted);
//		ConnectionHelper.getInstance().setRepositoryId(repositoryId);
//		ConnectionHelper.getInstance().setItemId(itemId);
//		
//	}
	
	
	@Override
	public boolean performFinish() {
		return true;
	}
}
