package bpm.workflow.ui.resources.server.wizard;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.workflow.runtime.resources.servers.FactoryServer;
import bpm.workflow.runtime.resources.servers.FactoryServerException;
import bpm.workflow.runtime.resources.servers.ListServer;
import bpm.workflow.runtime.resources.servers.Server;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.views.ResourceViewPart;

/**
 * Page for the creation of a new server
 * @author CHARBONNIER, MARTIN
 *
 */
public class ServerWizard extends Wizard implements INewWizard {

	protected static final String GENERAL_PAGE_NAME = "General Informations"; //$NON-NLS-1$
	protected static final String SQL_CONNECTION_PAGE_NAME = "Sql Connection"; //$NON-NLS-1$
	
	protected static final String MAIL_SERVER_PAGE_NAME = "Mail Server"; //$NON-NLS-1$
	protected static final String FREEMETRICS_SERVER_PAGE_NAME = "FreeMetrics Server"; //$NON-NLS-1$
	protected static final String FILE_SERVER_PAGE_NAME = "Files Server"; //$NON-NLS-1$

	
	private ServerTypePage typePage;
	private DataBasePage dbPage;
	private FreeMetricsServerPage freemetricsPage;
	private MailPage mailPage;
	

	private FilesServerPage fileServerPage;
	/*
	 * to create alternateConnections
	 */
	private Server server;
	
	public ServerWizard() {
		
	}

	@Override
	public boolean performFinish() {
		if (server == null){
			try {
				server = ServerWizardHelper.createServer(this);
				ListServer.getInstance().addServer(server);
				
				ResourceViewPart view = (ResourceViewPart)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ResourceViewPart.ID);
				
				if (view != null){
					view.refresh();
				}
				
			} catch (FactoryServerException e) {
				e.printStackTrace();
				Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
				MessageDialog.openError(getShell(), Messages.ServerWizard_4, e.getMessage());
				return false;
			}
		}
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		
		if (server == null){
			typePage = new ServerTypePage(GENERAL_PAGE_NAME);
			typePage.setDescription(Messages.ServerWizard_5);
			typePage.setTitle(Messages.ServerWizard_6);
			addPage(typePage);
			

			
			mailPage = new MailPage(MAIL_SERVER_PAGE_NAME);
			mailPage.setDescription(Messages.ServerWizard_7);
			mailPage.setTitle(MAIL_SERVER_PAGE_NAME);
			addPage(mailPage);
			
			fileServerPage = new FilesServerPage(FILE_SERVER_PAGE_NAME);
			fileServerPage.setDescription(Messages.ServerWizard_8);
			fileServerPage.setTitle(FILE_SERVER_PAGE_NAME);
			addPage(fileServerPage);
			
			freemetricsPage = new FreeMetricsServerPage(FREEMETRICS_SERVER_PAGE_NAME);
			freemetricsPage.setDescription(Messages.ServerWizard_1);
			freemetricsPage.setTitle(FREEMETRICS_SERVER_PAGE_NAME);
			addPage(freemetricsPage);

			dbPage = new DataBasePage(SQL_CONNECTION_PAGE_NAME);
			dbPage.setDescription(Messages.ServerWizard_9);
			dbPage.setTitle(SQL_CONNECTION_PAGE_NAME);
			addPage(dbPage);
			
	
		}
	
	}
	
	@Override
	public boolean canFinish() {
		try{
			String type = typePage.getType()+""; //$NON-NLS-1$
			
			if (type.equals(FactoryServer.MAIL_SERVER+"")){ //$NON-NLS-1$
				return mailPage.isPageComplete();
			}
			if (type.equals(FactoryServer.FILE_SERVER+"")){ //$NON-NLS-1$
				return fileServerPage.isPageComplete();
			}

			if (type.equals(FactoryServer.DATABASE_SERVER+"")) { //$NON-NLS-1$
				return dbPage.isPageComplete();
			}
			if(type.equals(FactoryServer.FREEMETRICS_SERVER+"")){ //$NON-NLS-1$
				return freemetricsPage.isPageComplete();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return false;
	}
}
