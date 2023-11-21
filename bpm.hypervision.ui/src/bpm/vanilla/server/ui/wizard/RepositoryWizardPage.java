//package bpm.vanilla.server.ui.wizard;
//
//import java.util.List;
//import java.util.Properties;
//
//import org.eclipse.jface.viewers.ComboViewer;
//import org.eclipse.jface.viewers.ISelectionChangedListener;
//import org.eclipse.jface.viewers.IStructuredContentProvider;
//import org.eclipse.jface.viewers.IStructuredSelection;
//import org.eclipse.jface.viewers.LabelProvider;
//import org.eclipse.jface.viewers.SelectionChangedEvent;
//import org.eclipse.jface.viewers.Viewer;
//import org.eclipse.jface.wizard.WizardPage;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.ModifyEvent;
//import org.eclipse.swt.events.ModifyListener;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Label;
//import org.eclipse.swt.widgets.Text;
//
//import bpm.vanilla.platform.core.IRepositoryApi;
//import bpm.vanilla.platform.core.IRepositoryContext;
//import bpm.vanilla.platform.core.IVanillaAPI;
//import bpm.vanilla.platform.core.beans.Group;
//import bpm.vanilla.platform.core.beans.Repository;
//import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
//import bpm.vanilla.platform.core.impl.BaseVanillaContext;
//import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
//import bpm.vanilla.server.ui.Activator;
//import bpm.vanilla.server.ui.Messages;
//
//public class RepositoryWizardPage extends WizardPage {
//
//	private ComboViewer repository;
//	private ComboViewer groups;
//	private Text repositoryPassword;
//	private Text repositoryLogin;
//	private String vanillaUrl;
//
//	public RepositoryWizardPage(String pageName) throws Exception {
//		super(pageName);
//		vanillaUrl = Activator.getDefault().getVanillaUrl(); //$NON-NLS-1$
//		this.setDescription(Messages.RepositoryWizardPage_1);
//	}
//
//	public void createControl(Composite parent) {
//
//		setControl(connCmp);
//
//		/*
//		 * load Repository
//		 */
//		IVanillaAPI api = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaApi();
//		try {
//			repository.setInput(api.getVanillaRepositoryManager().getRepositories());
//			groups.setInput(api.getVanillaSecurityManager().getGroups(bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getUser()));
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//	}
//
//	@Override
//	public boolean isPageComplete() {
//		return !("".equals(repository.getCombo().getText().trim()) || "".equals(repositoryLogin.getText())); //$NON-NLS-1$ //$NON-NLS-2$
//	}
//
//	public IRepositoryApi getRepositoryConnection() {
//
//		IRepositoryContext ctx = new BaseRepositoryContext(new BaseVanillaContext(vanillaUrl, repositoryLogin.getText(), repositoryPassword.getText()), ((Group) ((IStructuredSelection) groups.getSelection()).getFirstElement()), ((Repository) ((IStructuredSelection) repository.getSelection()).getFirstElement()));
//		return new RemoteRepositoryApi(ctx);
//
//	}
//
//	public Properties getProperties() {
//		Properties p = new Properties();
//		p.setProperty("repositoryId", ((Repository) ((IStructuredSelection) repository.getSelection()).getFirstElement()).getId() + ""); //$NON-NLS-1$ //$NON-NLS-2$
//		p.setProperty("login", repositoryLogin.getText()); //$NON-NLS-1$
//		p.setProperty("password", repositoryPassword.getText()); //$NON-NLS-1$
//		p.setProperty("encrypted", "false"); //$NON-NLS-1$ //$NON-NLS-2$
//		p.setProperty("groupId", ((Group) ((IStructuredSelection) groups.getSelection()).getFirstElement()).getId() + ""); //$NON-NLS-1$ //$NON-NLS-2$
//
//		return p;
//	}
//
//}
