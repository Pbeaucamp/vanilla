package bpm.gateway.ui.resource.server.wizard;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.gateway.core.Server;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.views.ResourceViewPart;

public class ServerWizard extends Wizard implements INewWizard {

	protected static final String GENERAL_PAGE_NAME = "General Informations"; //$NON-NLS-1$
	protected static final String SQL_CONNECTION_PAGE_NAME = "Sql Connection"; //$NON-NLS-1$
	protected static final String CASSANDRA_CONNECTION_PAGE_NAME = "Cassandra Connection"; //$NON-NLS-1$
//	protected static final String REPOSITORY_CONNECTION_PAGE_NAME = "Repository Connection"; //$NON-NLS-1$
	protected static final String LDAP_CONNECTION_PAGE_NAME = "Ldap Connection"; //$NON-NLS-1$
	public static final String VANILLA_SECURITY_CONNECTION_PAGE_NAME = "VanillaSecurity"; //$NON-NLS-1$
	public static final String  VANILLA_FILE_CONNECTION_PAGE_NAME = "VanillaFile Connection"; //$NON-NLS-1$
	public static final String HBASE_CONNECTION_PAGE_NAME = "HBase Connection"; //$NON-NLS-1$
	public static final String MONGODB_CONNECTION_PAGE_NAME = "MongoDB Connection"; //$NON-NLS-1$
	public static final String D4C_CONNECTION_PAGE_NAME = "D4C Connection"; //$NON-NLS-1$
	
	private ServerTypePage typePage;
	private ConnectionPage connectionPage;
//	private RepositoryPage repositoryPage;
	private LdapPage ldapPage;
	private VanillaRepositoryFiilePage vanillaFilePage;
	private CassandraConnectionPage cassandraPage;
	private HBaseConnectionPage hbasePage;
	private MongoDbConnectionPage mogoDbPage;
	private D4CConnectionPage d4cPage;
	
	/*
	 * to create alternateConnections
	 */
	private Server server;
	
	public ServerWizard() {
		
	}
	
	
	@Override
	public boolean canFinish() {
		try{
			String type = typePage.getType();
			
			if(type == null){
				return false;
			}
			
			if (type.equals(Server.DATABASE_TYPE) || type.equals(Server.FREEMETRICS_TYPE)){
				return connectionPage.isPageComplete();
			}
			if (type.equals(Server.LDAP_TYPE)){
				return ldapPage.isPageComplete();
			}
			if (type.equals(Server.FILE_TYPE)){
				return vanillaFilePage.isPageComplete();
			}
			if (type.equals(Server.CASSANDRA_TYPE)){
				return cassandraPage.isPageComplete();
			}
			if(type.equals(Server.HBASE_TYPE)) {
				return hbasePage.isPageComplete();
			}
			if(type.equals(Server.MONGODB_TYPE)){
				return mogoDbPage.isPageComplete();
			}
			if(type.equals(Server.D4C_TYPE)){
				return d4cPage.isPageComplete();
			}
		}catch(Exception e){
			e.printStackTrace();
			return connectionPage.isPageComplete();
		}
		
		
		return false;
	}


	public ServerWizard(Server s) {
		this.server = s;
	}

	@Override
	public boolean performFinish() {
		try {
			
			//server creation
			if (server == null){
				server = ServerWizardHelper.createServer(this);
				try {
					ResourceManager.getInstance().addServer(server);
				} catch (Exception e) {
					MessageDialog.openInformation(getShell(), Messages.ServerWizard_6, e.getMessage());
				}
				
			}
			//alternate connection creation
			else{
				server.addConnection(ServerWizardHelper.createConnection(this));
			}
			
			ResourceViewPart view = (ResourceViewPart)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ResourceViewPart.ID);
			
			if (view != null){
				view.refresh();
			}
			
		} catch (ServerException e) {
			e.printStackTrace();
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			MessageDialog.openError(getShell(), Messages.ServerWizard_7, e.getMessage());
			return false;
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
			typePage.setDescription(Messages.ServerWizard_0);
			typePage.setTitle(Messages.ServerWizard_9);
			addPage(typePage);
		}
		
		
		connectionPage = new ConnectionPage(SQL_CONNECTION_PAGE_NAME);
		connectionPage.setDescription(Messages.ServerWizard_10);
		connectionPage.setTitle(Messages.ServerWizard_11);
		addPage(connectionPage);
		
//		if (server == null){
//			repositoryPage = new RepositoryPage(REPOSITORY_CONNECTION_PAGE_NAME);
//			repositoryPage.setDescription(Messages.ServerWizard_12);
//			repositoryPage.setTitle(Messages.ServerWizard_13);
//			addPage(repositoryPage);
//		}
		
		ldapPage = new LdapPage(LDAP_CONNECTION_PAGE_NAME);
		ldapPage.setDescription(Messages.ServerWizard_14);
		ldapPage.setTitle(Messages.ServerWizard_15);
		addPage(ldapPage);
		
		
		cassandraPage = new CassandraConnectionPage(CASSANDRA_CONNECTION_PAGE_NAME);
		cassandraPage.setDescription(Messages.ServerWizard_3);
		cassandraPage.setTitle(Messages.ServerWizard_4);
		addPage(cassandraPage);
		
		hbasePage = new HBaseConnectionPage(HBASE_CONNECTION_PAGE_NAME);
		hbasePage.setDescription(Messages.ServerWizard_5);
		hbasePage.setTitle(HBASE_CONNECTION_PAGE_NAME);
		addPage(hbasePage);
		
		mogoDbPage = new MongoDbConnectionPage(MONGODB_CONNECTION_PAGE_NAME);
		mogoDbPage.setDescription(Messages.ServerWizard_8);
		mogoDbPage.setTitle(MONGODB_CONNECTION_PAGE_NAME);
		addPage(mogoDbPage);
		
		d4cPage = new D4CConnectionPage(D4C_CONNECTION_PAGE_NAME);
		d4cPage.setDescription(Messages.ServerWizard_1);
		d4cPage.setTitle(D4C_CONNECTION_PAGE_NAME);
		addPage(d4cPage);
		
		vanillaFilePage = new VanillaRepositoryFiilePage(VANILLA_FILE_CONNECTION_PAGE_NAME);
		vanillaFilePage.setDescription(Messages.ServerWizard_16);
		vanillaFilePage.setTitle(Messages.ServerWizard_17);
		addPage(vanillaFilePage);
		
//		vanillaSecuPage = new RepositoryPage(VANILLA_SECURITY_CONNECTION_PAGE_NAME){
//
//			/* (non-Javadoc)
//			 * @see bpm.gateway.ui.resource.server.wizard.RepositoryPage#registerTestListener()
//			 */
//			@Override
//			protected void registerTestListener() {
//				
//				
//			}
//
//			/* (non-Javadoc)
//			 * @see bpm.gateway.ui.resource.server.wizard.RepositoryPage#createControl(org.eclipse.swt.widgets.Composite)
//			 */
//			@Override
//			public void createControl(Composite parent) {
//				super.createControl(parent);
//				url.removeModifyListener(listener);
//				url.setText("http://localhost:8080/Preferences/");
//				url.addModifyListener(listener);
//				
//			}
//			
//		};
//		vanillaSecuPage.setDescription("VanillaSecurity Server Properties");
//		vanillaSecuPage.setTitle("VanillaSecurity Server");
//		
//		addPage(vanillaSecuPage);
	}


	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		return super.getNextPage(page);
	}

	
}
