package bpm.vanilla.repository.ui.wizards.page;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.utils.MD5Helper;
import bpm.vanilla.repository.ui.Messages;

public class RepositorySelectionPage extends WizardPage{

	private ComboViewer repository;
	private ComboViewer groups;
	
	
	private Text repositoryPassword;
	private Text repositoryLogin;
	private Text vanillaUrl;
	
	
	
	private IVanillaAPI aa;
	private User user;
	
	
	public RepositorySelectionPage(String pageName) {
		super(pageName);
		setDescription(Messages.RepositorySelectionPage_0);
	}

	public void createControl(Composite parent) {
		Composite connCmp = new Composite(parent, SWT.NONE);
		connCmp.setLayout(new GridLayout(3, false));
		connCmp.setLayoutData(new GridData(GridData.FILL_BOTH));

		
		Label l0 = new Label(connCmp, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l0.setText(Messages.RepositorySelectionPage_1);

		
		
		Label l = new Label(connCmp, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.RepositorySelectionPage_2);
		
		vanillaUrl = new Text(connCmp, SWT.BORDER);
		vanillaUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		vanillaUrl.setText("http://localhost:8080/vanilla"); //$NON-NLS-1$


		l = new Label(connCmp, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.RepositorySelectionPage_4);
		
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
		l.setText(Messages.RepositorySelectionPage_6);
		
		repositoryPassword = new Text(connCmp, SWT.BORDER | SWT.PASSWORD);
		repositoryPassword.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		repositoryPassword.setText("system"); //$NON-NLS-1$
		repositoryPassword.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
		});
		

		Button b = new Button(connCmp, SWT.PUSH);
		b.setText(Messages.RepositorySelectionPage_8);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 3, 1));
		b.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					
					aa = new RemoteVanillaPlatform(vanillaUrl.getText(), repositoryLogin.getText(), repositoryPassword.getText());
					
					user = aa.getVanillaSecurityManager().getUserByLogin(repositoryLogin.getText());
					if (user == null){
						throw new Exception(Messages.RepositorySelectionPage_9);
					}
					
					if (!user.getPassword().equals(MD5Helper.encode(repositoryPassword.getText()))){
						throw new Exception(Messages.RepositorySelectionPage_10);
					}
					
					repository.setInput(aa.getVanillaRepositoryManager().getRepositories());
					
					groups.setInput(aa.getVanillaSecurityManager().getGroups(user));
					
				} catch (Exception e1) {
					e1.printStackTrace();
					repository.setInput(new ArrayList());
					groups.setInput(new ArrayList<Group>());
					MessageDialog.openError(getShell(), Messages.RepositorySelectionPage_11, Messages.RepositorySelectionPage_12 + e1.getMessage());
				}

			}
			
		});
		
		l = new Label(connCmp, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.RepositorySelectionPage_13);
		

		groups = new ComboViewer(connCmp, SWT.READ_ONLY);
		groups.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		groups.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection<?> c = (Collection<?>)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		groups.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		groups.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
				
			}
		});
		
		l = new Label(connCmp, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.RepositorySelectionPage_14);
		
		repository = new ComboViewer(connCmp, SWT.READ_ONLY);
		repository.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		repository.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection<?> c = (Collection<?>)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		repository.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Repository)element).getName();
			}
		});
		repository.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
				
			}
		});
		
		setControl(connCmp);
	}
	
	
	public IRepositoryApi getRepositoryConnection(){
		Group group = (Group)((IStructuredSelection)groups.getSelection()).getFirstElement();
		Repository repDef = (Repository)((IStructuredSelection)repository.getSelection()).getFirstElement();
		IRepositoryApi sock = new RemoteRepositoryApi(
				new BaseRepositoryContext(
						new BaseVanillaContext(repDef.getUrl(), repositoryLogin.getText(), repositoryPassword.getText()), 
						group, repDef)); 
		return sock;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return !repository.getSelection().isEmpty() && ! groups.getSelection().isEmpty();
	}

	public IRepositoryContext getRepositoryContext() {
		Group group = (Group)((IStructuredSelection)groups.getSelection()).getFirstElement();
		Repository repDef = (Repository)((IStructuredSelection)repository.getSelection()).getFirstElement();
	
		BaseVanillaContext vCtx = new BaseVanillaContext(vanillaUrl.getText(), repositoryLogin.getText(), repositoryPassword.getText());
		BaseRepositoryContext ctx = new BaseRepositoryContext(vCtx, group, repDef);
		return ctx;
	}
	
	
	
}
