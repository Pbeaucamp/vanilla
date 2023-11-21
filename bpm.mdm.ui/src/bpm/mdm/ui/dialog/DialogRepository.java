package bpm.mdm.ui.dialog;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.mdm.ui.Activator;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class DialogRepository extends Dialog {
	private ComboViewer repositories;
	private Text userName, password;
	private Text url;
	private Button loadRepositories;
	
	private boolean authentified = false;
	
	public DialogRepository(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void initializeBounds() {
		getShell().setText("Connection");
		getShell().setSize(500, 300);
		updateConnectButton();
		url.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				updateConnectButton();
				
			}
		});
		userName.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				updateConnectButton();
				
			}
		});

	}





	@Override
	protected Control createDialogArea(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		content.setLayout(new GridLayout(3, false));
		content.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Label l3 = new Label(content, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText("Username");
		
		userName = new Text(content, SWT.BORDER);
		userName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		Label l4 = new Label(content, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText("Password");
		
		password = new Text(content, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		Label l2 = new Label(content, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText("Vanilla url");
		
		url = new Text(content, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		loadRepositories = new Button(content, SWT.PUSH);
		loadRepositories.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		loadRepositories.setText("Validate");
		loadRepositories.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				initRepositories();
			}
		});
		
		Label l5 = new Label(content, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l5.setText("Repositories");
		
		repositories = new ComboViewer(content, SWT.READ_ONLY);
		repositories.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		repositories.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				authentified = !event.getSelection().isEmpty();
				updateConnectButton();
				
			}
		});
		repositories.setContentProvider(new ArrayContentProvider());
		repositories.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Repository)element).getName();
			}
		});
		
		
		//fill texts
		userName.setText("system");
		password.setText("system");
		url.setText("http://localhost:7171/VanillaRuntime");
		
		
		Label _tip1 = new Label(content, SWT.NONE);
		_tip1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		_tip1.setText(""); //$NON-NLS-1$
		
		Label tip1 = new Label(content, SWT.NONE);
		tip1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		tip1.setText("Step 1 : Fill user, password and vanilla server's Url then click on Validate");
		
		tip1 = new Label(content, SWT.NONE);
		tip1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		tip1.setText("(VanillaUrl: http://localhost:7171/VanillaRuntime)");

		Label tip2 = new Label(content, SWT.NONE);
		tip2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		tip2.setText("Step 2 : Select your Repository then click on Select");
		
		return content;
	}
	
	private void updateConnectButton(){
		try{
			loadRepositories.setEnabled(!(userName.getText().isEmpty() || url.getText().isEmpty()));
			getButton(IDialogConstants.OK_ID).setEnabled(authentified);
		}catch(NullPointerException e){
			
		}
		
	}


	private void initRepositories() {
		try {
			
			RemoteVanillaPlatform tmp = new RemoteVanillaPlatform(url.getText(),userName.getText(), password.getText());
			User user = tmp.getVanillaSecurityManager().authentify("", userName.getText(), password.getText(), false);
			
			if (!user.isSuperUser()){
				throw new Exception("Not a super user");
			}
			
			List<Repository> list = tmp.getVanillaRepositoryManager().getRepositories();
			String[] items = new String[list.size()];
			int i = 0;
			int toSelect = -1;
			
			for(Repository c : list){
				if (c.getName().equals("")){
					toSelect = i;
				}
				items[i++] = c.getName();
			}
						
			repositories.setInput(list);
			
			
			
			if (toSelect != -1){
				repositories.setSelection(new StructuredSelection(list.get(toSelect)));
				authentified = true;
			}
			else{
				authentified = false;
			}
			
		} catch (Throwable e) {
			repositories.setInput(Collections.EMPTY_LIST);
			e.printStackTrace();
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "",e));
			MessageDialog.openError(getShell(), "Error while loading repositories", e.getMessage());
			authentified = false;
		}
		updateConnectButton();
	}




	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button connect = createButton(parent, IDialogConstants.OK_ID, "Select",	true);
		connect.setEnabled(false);
	}

	@Override
	protected void okPressed() {
		try {
			Group group = new Group();
			group.setId(-1);
			
			IVanillaContext vanillaCtx = new BaseVanillaContext(url.getText(), userName.getText(), password.getText());
			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
			
			IRepositoryApi sock = new RemoteRepositoryApi(new BaseRepositoryContext(
					vanillaCtx, 
					group, 
					(Repository) ((IStructuredSelection)repositories.getSelection()).getFirstElement()));
			
			if(vanillaApi.getVanillaSecurityManager().canAccessApp(-1, Activator.SOFT_ID)) {
				Activator.setRepositoryApi(sock);
				super.okPressed();
			}
			else {
				MessageDialog.openError(getShell(), "User Rights", "You are not allowed to access this application.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Problem", e.getMessage());
		}
	}

}
