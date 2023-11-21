package bpm.vanilla.repository.ui.composites;

import java.util.Collection;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.repository.ui.Messages;
import bpm.vanilla.repository.ui.viewers.RepositoryContentProvider;
import bpm.vanilla.repository.ui.viewers.RepositoryLabelProvider;

public class CompositeRepositorySelecter extends Composite{
	

	private Text vanillaUrl;
	private Text vanillaLogin;
	private Text vanillaPassword;
	private Button loadRepositories;
	
	private ComboViewer repositorySelecter;
	
	
	private IRepositoryApi sock;
	
	public CompositeRepositorySelecter(Composite parent, int style) {
		super(parent, style);
		buildContent();
	}
	
	private void buildContent(){
		this.setLayout( new GridLayout(2, false));
		
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeRepositorySelecter_0);
		
		vanillaUrl = new Text(this, SWT.BORDER);
		vanillaUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		vanillaUrl.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				updateButtonState();
				
			}
			
		});
		
		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeRepositorySelecter_1);
		
		vanillaLogin = new Text(this, SWT.BORDER);
		vanillaLogin.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		vanillaLogin.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				updateButtonState();
				
			}
			
		});
		
		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeRepositorySelecter_2);
		
		vanillaPassword = new Text(this, SWT.BORDER | SWT.PASSWORD);
		vanillaPassword.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		vanillaPassword.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				updateButtonState();
				
			}
			
		});
		
		
		loadRepositories = new Button(this, SWT.PUSH);
		loadRepositories.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		loadRepositories.setText(Messages.CompositeRepositorySelecter_3);
		loadRepositories.setEnabled(false);
		loadRepositories.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					loadRepositoriesDefinition();
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), Messages.CompositeRepositorySelecter_4, e1.getMessage());
				}
				
			}
		});
		
		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeRepositorySelecter_5);
		
		repositorySelecter = new ComboViewer(this, SWT.READ_ONLY);
		repositorySelecter.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		repositorySelecter.setContentProvider(new RepositoryContentProvider());
		repositorySelecter.setLabelProvider(new RepositoryLabelProvider());
		repositorySelecter.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				Event e = new Event();
				e.item = repositorySelecter.getCombo();
				if (!repositorySelecter.getSelection().isEmpty()){
					e.data = ((IStructuredSelection)repositorySelecter.getSelection()).getFirstElement();
				}

				notifyListeners(SWT.Selection, e);
				
			}
		});
		
	}
	
	private void updateButtonState(){
		boolean enabled = !"".equals(vanillaUrl.getText()) && !"".equals(vanillaLogin.getText()) && !"".equals(vanillaPassword.getText()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		loadRepositories.setEnabled(enabled);
	}
	
	public Repository getSelectedRepositoryDefinition(){
		if (!repositorySelecter.getSelection().isEmpty()){
			return (Repository)((IStructuredSelection)repositorySelecter.getSelection()).getFirstElement();
		}
		return null;
	}
	
	public String getVanillaUrl(){
		try{
			return vanillaUrl.getText();
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	public IRepositoryApi getRepositorySocket(){
		boolean hasChanged = sock != null;
		if (hasChanged){
			hasChanged = false;
			IRepositoryContext repCtx = sock.getContext();
			
			hasChanged = !repCtx.getVanillaContext().getLogin().equals(vanillaLogin.getText()) ||
			!repCtx.getVanillaContext().equals(vanillaPassword.getText()) ||
			repCtx.getRepository().getId() != getSelectedRepositoryDefinition().getId() ;
		}
		
		if (sock == null || hasChanged){
			try{
				//XXX : hard coded
				Group dumyy = new Group(); dumyy.setId(-1);
				sock = new RemoteRepositoryApi(
						new BaseRepositoryContext(
								new BaseVanillaContext(vanillaUrl.getText(), vanillaLogin.getText(), vanillaPassword.getText()), 
								dumyy, 
								getSelectedRepositoryDefinition()));  
					
					
			}catch(Exception e){
				return null;
			}
		}
		return sock;
		
	}
	
	public void fill(String login, String password, String url, Integer repositoryId){
		vanillaUrl.setText(url != null ? url : ""); //$NON-NLS-1$
		vanillaLogin.setText(login != null ? login : ""); //$NON-NLS-1$
		vanillaPassword.setText(password != null ? password : ""); //$NON-NLS-1$
		
		
	
		
		try{
			loadRepositoriesDefinition();
			if (repositorySelecter.getInput() != null){
				for(Object o : (Collection)repositorySelecter.getInput()){
					if (((Repository )o).getId() == repositoryId){
						repositorySelecter.setSelection(new StructuredSelection(o));
					}
				}
			}
		}catch(Exception ex){
			
		}
		
	}
	
	private void loadRepositoriesDefinition() throws Exception{
		IVanillaAPI api = new RemoteVanillaPlatform(vanillaUrl.getText(), vanillaLogin.getText(), vanillaPassword.getText());
		
		
		try{
			repositorySelecter.setInput(api.getVanillaRepositoryManager().getUserRepositories(vanillaLogin.getText()));
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception(Messages.CompositeRepositorySelecter_12 + ex.getMessage(), ex);
			
		}
	}

	public void enableConnectionFields(boolean b) {
		vanillaLogin.setEnabled(b);
		vanillaPassword.setEnabled(b);
		vanillaUrl.setEnabled(b);
		
	}
	
	private IVanillaContext vanillaContext;
	
	public IVanillaContext getVanillaContext() {
		if(vanillaContext == null) {
			vanillaContext = new BaseVanillaContext(vanillaUrl.getText(), vanillaLogin.getText(), vanillaPassword.getText());
		}
		return vanillaContext;
	}
}
