package bpm.vanilla.repository.ui.wizards.page;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.designer.ui.common.preferences.PreferencesConstants;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.repository.ui.Activator;
import bpm.vanilla.repository.ui.Messages;
import bpm.vanilla.repository.ui.viewers.RepositoryLabelProvider;


public class PageConnectionDefinition extends WizardPage {

	private ComboViewer repositories;
	private ComboViewer groups;
	
	private BaseVanillaContext vanillaCtx;
	private Text host, login, password;
	
	private IStatus status;
	
	private IRepositoryContext ctx ;
	
	private ModifyListener modifyListener = new ModifyListener() {
		
		@Override
		public void modifyText(ModifyEvent e) {
//			getContainer().updateButtons();
			
		}
	};
	
	private ISelectionChangedListener selectionListener = new ISelectionChangedListener() {
		
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			getContainer().updateButtons();
			
		}
	};
	
	public PageConnectionDefinition(String pageName) {
		super(pageName);
		status = Status.OK_STATUS;
		
	}
	
	public IRepositoryContext getRepositoryContext(){
		ctx = new BaseRepositoryContext(vanillaCtx, (Group)(((IStructuredSelection)groups.getSelection()).getFirstElement()), (Repository)(((IStructuredSelection)repositories.getSelection()).getFirstElement()));
		/*
		 * store the prefereces
		 */
		IPreferenceStore s = bpm.vanilla.designer.ui.common.Activator.getDefault().getPreferenceStore();
		s.setValue(PreferencesConstants.P_VANILLA_LOGIN, login.getText());
		s.setValue(PreferencesConstants.P_VANILLA_URL, host.getText());
		s.setValue(PreferencesConstants.P_VANILLA_PASSWORD, password.getText());
		s.setValue(PreferencesConstants.P_VANILLA_GROUP_ID, ctx.getGroup().getId());
		s.setValue(PreferencesConstants.P_VANILLA_REP_ID, ctx.getRepository().getId());
		return ctx;
	}
	
	public void createControl(Composite parent){
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new FillLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl( mainComposite );
		setPageComplete(false);
	}

	public void createPageContent(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		
		Label l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.PageConnectionDefinition_0); 

		
		host = new Text(container, SWT.BORDER);
		host.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		Label l6 = new Label(container, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l6.setText(Messages.PageConnectionDefinition_1); 

		
		login= new Text(container, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l7 = new Label(container, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.PageConnectionDefinition_2); 

		
		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		Button loadGroups = new Button(container, SWT.PUSH);
		loadGroups.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 2, 1));
		loadGroups.setText(Messages.PageConnectionDefinition_3);
		
		loadGroups.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				vanillaCtx = new BaseVanillaContext(host.getText(), login.getText(), password.getText());
				
				
				IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
				
				try{
					User user = vanillaApi.getVanillaSecurityManager().authentify("", vanillaCtx.getLogin(), vanillaCtx.getPassword(), false);
					repositories.setInput(vanillaApi.getVanillaRepositoryManager().getUserRepositories(login.getText()));
					groups.setInput(vanillaApi.getVanillaSecurityManager().getGroups(user));
					
				}catch(Exception ex){
					ex.printStackTrace();
					groups.setInput(Collections.EMPTY_LIST);
					repositories.setInput(Collections.EMPTY_LIST);
					vanillaCtx = null;
					MessageDialog.openError(getShell(), Messages.PageConnectionDefinition_4, Messages.PageConnectionDefinition_5 + ex.getMessage());
				}
				
			}
		});

		l7 = new Label(container, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.PageConnectionDefinition_6); 
		
		
		groups = new ComboViewer(container, SWT.READ_ONLY);
		groups.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groups.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		groups.setContentProvider(new ArrayContentProvider());
		
		
		l7 = new Label(container, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.PageConnectionDefinition_7); 

	
		repositories = new ComboViewer(container, SWT.READ_ONLY);
		repositories.setLabelProvider(new RepositoryLabelProvider());
		repositories.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		repositories.setContentProvider(new ArrayContentProvider());
		
		
		fill(true);
		host.addModifyListener(modifyListener);
		login.addModifyListener(modifyListener);	
		password.addModifyListener(modifyListener);	
		groups.addSelectionChangedListener(selectionListener);
		repositories.addSelectionChangedListener(selectionListener);
	}
	
	private void connect(boolean isTimedOut) throws Exception{
		vanillaCtx = new BaseVanillaContext(host.getText(), login.getText(), password.getText());
		
		
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);

		User user = vanillaApi.getVanillaSecurityManager().authentify("", vanillaCtx.getLogin(), vanillaCtx.getPassword(), isTimedOut);
		repositories.setInput(vanillaApi.getVanillaRepositoryManager().getUserRepositories(login.getText()));
		groups.setInput(vanillaApi.getVanillaSecurityManager().getGroups(user));
		
		/*
		 * store the prefereces
		 */
		IPreferenceStore s = bpm.vanilla.designer.ui.common.Activator.getDefault().getPreferenceStore();
		s.setValue(PreferencesConstants.P_VANILLA_LOGIN, login.getText());
		s.setValue(PreferencesConstants.P_VANILLA_URL, host.getText());
		s.setValue(PreferencesConstants.P_VANILLA_PASSWORD, password.getText());
		

	}
	private void fill(boolean isTimedOut){
		if (Activator.getDefault().getDesignerActivator() != null && Activator.getDefault().getDesignerActivator().getRepositoryContext() != null){
			IRepositoryContext ctx = Activator.getDefault().getDesignerActivator().getRepositoryContext();
			
			login.setText(ctx.getVanillaContext().getLogin());
			password.setText(ctx.getVanillaContext().getPassword());
			host.setText(ctx.getVanillaContext().getVanillaUrl());
			
			try{
				connect(isTimedOut);
				if (groups.getInput() != null){
					for(Group g : (List<Group>)groups.getInput()){
						if (g.getId() == ctx.getGroup().getId()){
							groups.setSelection(new StructuredSelection(g));
							break;
						}
					}
				}
				
				if (repositories.getInput() != null){
					for(Repository g : (List<Repository>)repositories.getInput()){
						if (g.getId() == ctx.getRepository().getId()){
							repositories.setSelection(new StructuredSelection(g));
							break;
						}
					}
				}
			}catch(Exception ex){
				
			}
		}
		else{
			IPreferenceStore s = bpm.vanilla.designer.ui.common.Activator.getDefault().getPreferenceStore();
			
			login.setText(s.getString(PreferencesConstants.P_VANILLA_LOGIN));
			password.setText(s.getString(PreferencesConstants.P_VANILLA_PASSWORD));
			host.setText(s.getString(PreferencesConstants.P_VANILLA_URL));
			
			try{
				connect(isTimedOut);
				if (groups.getInput() != null){
					for(Group g : (List<Group>)groups.getInput()){
						if (g.getId() == s.getInt(PreferencesConstants.P_VANILLA_GROUP_ID)){
							groups.setSelection(new StructuredSelection(g));
							break;
						}
					}
				}
				
				if (repositories.getInput() != null){
					for(Repository g : (List<Repository>)repositories.getInput()){
						if (g.getId() == s.getInt(PreferencesConstants.P_VANILLA_REP_ID)){
							repositories.setSelection(new StructuredSelection(g));
							break;
						}
					}
				}
			}catch(Exception ex){
				
			}
		}
		
		
		
		
	}

	@Override
	public boolean isPageComplete() {
		status = new Status(IStatus.OK, Activator.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
		
		if (host.getText().trim().equals("")){ //$NON-NLS-1$
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, Messages.PageConnectionDefinition_10, null);
		}
		if (login.getText().trim().equals("")){ //$NON-NLS-1$
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, Messages.PageConnectionDefinition_12, null);			
		}
		
		if (password.getText().trim().equals("")){ //$NON-NLS-1$
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, Messages.PageConnectionDefinition_14, null);
		}
		
		if (groups.getSelection().isEmpty()){
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, Messages.PageConnectionDefinition_15, null);
		}
		
		if (repositories.getSelection().isEmpty()){
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, Messages.PageConnectionDefinition_16, null);
		}
		
		if (status.getSeverity() == IStatus.OK){
			setErrorMessage(null);
			return true;
		}
		
		else{
			setErrorMessage(status.getMessage());
			return false;
		}
	
	}


	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
		//return true;
	}
		
}
