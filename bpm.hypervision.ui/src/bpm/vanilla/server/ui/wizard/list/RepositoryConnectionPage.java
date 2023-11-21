package bpm.vanilla.server.ui.wizard.list;

import java.util.List;
import java.util.Properties;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;

public class RepositoryConnectionPage extends WizardPage {

	private boolean isListPage;
	
	private Text listName;
	private ComboViewer repository;
	private ComboViewer groups;
	private Text repositoryPassword;
	private Text repositoryLogin;
	
	private String vanillaUrl;

	public RepositoryConnectionPage(String pageName, boolean isListPage) {
		super(pageName);
		this.setTitle(Messages.RepositoryConnectionPage_0);
		this.setDescription(Messages.RepositoryConnectionPage_1);
		this.isListPage = isListPage;
		this.vanillaUrl = Activator.getDefault().getVanillaUrl();
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout());

		if(isListPage) {
			Label lblName = new Label(main, SWT.NONE);
			lblName.setText(Messages.ListInfoPage_0);
			lblName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
	
			listName = new Text(main, SWT.BORDER);
			listName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			listName.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					getContainer().updateButtons();
				}
			});
		}

		
		org.eclipse.swt.widgets.Group groupConnection = new org.eclipse.swt.widgets.Group(main, SWT.NONE);
		groupConnection.setText(Messages.RepositoryConnectionPage_2);
		groupConnection.setLayout(new GridLayout());
		groupConnection.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite connCmp = new Composite(groupConnection, SWT.NONE);
		connCmp.setLayout(new GridLayout(3, false));
		connCmp.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = new Label(connCmp, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.RepositoryWizardPage_2);

		repositoryLogin = new Text(connCmp, SWT.BORDER);
		repositoryLogin.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		repositoryLogin.setText("system"); //$NON-NLS-1$
		repositoryLogin.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
			}
		});

		l = new Label(connCmp, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.RepositoryWizardPage_4);

		repositoryPassword = new Text(connCmp, SWT.BORDER | SWT.PASSWORD);
		repositoryPassword.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		repositoryPassword.setText("system"); //$NON-NLS-1$
		repositoryPassword.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
			}
		});

		l = new Label(connCmp, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.RepositoryWizardPage_6);

		groups = new ComboViewer(connCmp, SWT.BORDER | SWT.READ_ONLY);
		groups.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		groups.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

			public void dispose() { }

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<Group> c = (List<Group>) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		groups.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Group) element).getName();
			}
		});
		groups.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();

			}
		});

		l = new Label(connCmp, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.RepositoryWizardPage_7);

		repository = new ComboViewer(connCmp, SWT.BORDER | SWT.READ_ONLY);
		repository.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		repository.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<Repository> c = (List<Repository>) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		repository.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Repository) element).getName();
			}
		});

		repository.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();

			}
		});
		
		setControl(main);

		/*
		 * load Repository
		 */
		IVanillaAPI api = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaApi();
		try {
			repository.setInput(api.getVanillaRepositoryManager().getRepositories());
			groups.setInput(api.getVanillaSecurityManager().getGroups(bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getUser()));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public String getListName() {
		return listName.getText();
	}
	
	@Override
	public boolean isPageComplete() {
		return !((listName != null && listName.getText().isEmpty()) || repository.getCombo().getText().trim().isEmpty() || repositoryLogin.getText().isEmpty()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public IRepositoryApi getRepositoryConnection() {
		IRepositoryContext ctx = new BaseRepositoryContext(new BaseVanillaContext(vanillaUrl, repositoryLogin.getText(), repositoryPassword.getText()), ((Group) ((IStructuredSelection) groups.getSelection()).getFirstElement()), ((Repository) ((IStructuredSelection) repository.getSelection()).getFirstElement()));
		return new RemoteRepositoryApi(ctx);
	}

	public Properties getProperties() {
		Properties p = new Properties();
		p.setProperty("repositoryId", ((Repository) ((IStructuredSelection) repository.getSelection()).getFirstElement()).getId() + ""); //$NON-NLS-1$ //$NON-NLS-2$
		p.setProperty("login", repositoryLogin.getText()); //$NON-NLS-1$
		p.setProperty("password", repositoryPassword.getText()); //$NON-NLS-1$
		p.setProperty("encrypted", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		p.setProperty("groupId", ((Group) ((IStructuredSelection) groups.getSelection()).getFirstElement()).getId() + ""); //$NON-NLS-1$ //$NON-NLS-2$

		return p;
	}

}
