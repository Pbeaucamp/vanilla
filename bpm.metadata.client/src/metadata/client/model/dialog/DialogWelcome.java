package metadata.client.model.dialog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import metadata.client.actions.ActionOpen;
import metadata.client.i18n.Messages;
import metadata.client.preferences.PreferenceConstants;
import metadataclient.Activator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.internal.wizards.ImportWizardRegistry;

import bpm.metadata.MetaData;
import bpm.vanilla.designer.ui.common.IDesignerActivator;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.repository.ui.connection.CompositeWelcomeConnectionTab;

public class DialogWelcome extends Dialog{
	private Button showAtStartup;
	/*
	 * start Tab
	 */
	private Button newDoc, openDoc, importDoc;
	
	/*
	 * Repository Tab
	 */

	private IWorkbenchAction importAction = ActionFactory.IMPORT.create(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow());
	/*
	 * Recent Tab
	 */
	private ListViewer viewer;
	
	public DialogWelcome(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Canvas l = new Canvas(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		l.setBackgroundImage(reg.get("small_splash")); //$NON-NLS-1$
		
		
		TabFolder folder = new TabFolder(main, SWT.NONE);
		folder.setLayout(new GridLayout());
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
	
		
		TabItem start = new TabItem(folder, SWT.NONE);
		start.setText(Messages.DialogWelcome_1);
		start.setControl(createButtonsTab(folder));
		
		TabItem recent = new TabItem(folder, SWT.NONE);
		recent.setText(Messages.DialogWelcome_2);
		recent.setControl(createRecentTab(folder));
		
		
		TabItem repository = new TabItem(folder, SWT.NONE);
		repository.setText(Messages.DialogWelcome_3);
		repository.setControl(createRepositoryTab(folder));
		
		showAtStartup = new Button(main, SWT.CHECK);
		showAtStartup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		showAtStartup.setText(Messages.DialogWelcome_4);
		showAtStartup.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_SHOW_MENU_AT_STARTUP, showAtStartup.getSelection());
			}
			
		});
		return folder;
	}
	
	
	
	private Control createRecentTab(TabFolder folder){
		
		Composite c = new Composite(folder, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer = new ListViewer(c, SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<String> l = (List<String>)inputElement;
				return l.toArray(new String[l.size()]);
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {}
			
		});
		viewer.setLabelProvider(new LabelProvider());
		viewer.addDoubleClickListener(new IDoubleClickListener(){

			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				if (ss.isEmpty()){
					return;
				}
				
				
				String file = (String)ss.getFirstElement();
				ActionOpen a = new ActionOpen(file);
				a.run();
				close();
			}
			
		});
		
		return c;
	}
	
	private Control createButtonsTab(TabFolder folder){
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		Composite c = new Composite(folder, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		newDoc = new Button(c, SWT.PUSH);
		newDoc.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		newDoc.setImage(reg.get("new")); //$NON-NLS-1$
		newDoc.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				MetaData model = new MetaData();
    			try{
    				model.getProperties().setVersion(Activator.getFeatureVersion());
    				Activator.getDefault().setCurrentModel(model.getXml(true), null);
        			Activator.getDefault().setFileName("");  //$NON-NLS-1$
        			
        			DialogDocumentProperties d = new DialogDocumentProperties(getShell());
        			d.open();
        			close();
    			}catch(Exception ex){
    				ex.printStackTrace();
    				
    			}
    			
    			
			}
			
		});
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l.setText(Messages.DialogWelcome_7);
		
		
		openDoc = new Button(c, SWT.PUSH);
		openDoc.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		openDoc.setImage(reg.get("open")); //$NON-NLS-1$
		openDoc.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				ActionOpen a = new ActionOpen();
				a.run();
				close();
			}
			
		});
		
		Label l2 = new Label(c, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l2.setText(Messages.DialogWelcome_9);

		
		
		importDoc = new Button(c, SWT.PUSH);
		importDoc.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		importDoc.setImage(importAction.getImageDescriptor().createImage());
		importDoc.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					
					IWorkbenchWizard wiz = ImportWizardRegistry.getInstance().findWizard("bpm.vanilla.repository.ui.importwizard").createWizard(); //$NON-NLS-1$
					
					
					if (wiz.getClass().getName().equals("bpm.vanilla.repository.ui.wizards.page.ExportObjectWizard")){ //$NON-NLS-1$
						try{
							Method m = wiz.getClass().getMethod("setDesignerActivator", IDesignerActivator.class); //$NON-NLS-1$
							m.invoke(wiz, Activator.getDefault());
						}catch(Exception ex){
							ex.printStackTrace();
							MessageDialog.openError(getShell(), Messages.DialogWelcome_13, Messages.DialogWelcome_14 + ex.getMessage());
						}
						
						
					}
					
					
					
					WizardDialog dizl = new WizardDialog(getShell(), wiz);
					dizl.open();
				} catch (CoreException e1) {
					e1.printStackTrace();
				}
				
			}
			
		});
		importDoc.setEnabled(false);
		
		Label l3 = new Label(c, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l3.setText(Messages.DialogWelcome_15);
		
		return c;
	}

	private Control createRepositoryTab(TabFolder folder){

		final CompositeWelcomeConnectionTab container = new CompositeWelcomeConnectionTab(folder, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true));

		Button b = new Button(container, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		b.setText(Messages.DialogWelcome_16);
		b.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{

					IRepositoryContext r = container.getRepositoryContext();
					Activator.getDefault().setRepositoryContext(r);

					
										
					MessageDialog.openInformation(getShell(), Messages.DialogConnect_11, Messages.DialogConnect_12); //$NON-NLS-1$ //$NON-NLS-2$
					importDoc.setEnabled(true);
					
					IViewPart v = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("org.fasd.views.cubeView"); //$NON-NLS-1$
					if ( v != null){
						if (Activator.getDefault().getRepositoryContext() != null){
							v.getViewSite().getActionBars().getStatusLineManager().setMessage(Messages.DialogConnect_14 + Activator.getDefault().getRepositoryContext().getRepository().getName()); //$NON-NLS-1$
						}
						else{
							v.getViewSite().getActionBars().getStatusLineManager().setMessage(Messages.DialogConnect_15); //$NON-NLS-1$
						}
					}
					
					
				}catch(Exception ex){
					MessageDialog.openError(getShell(), Messages.DialogConnect_16, ex.getMessage()); //$NON-NLS-1$
					Activator.getDefault().setRepositoryContext(null);
					ex.printStackTrace();
				}
			}
			
		});
		
		
		return container;
	}
	
	private void setDatas(){
				
		
		/*
		 * load RecentFiles
		 */
		 //recentMenu
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    	
        String[] recent = new String[]{
        		store.getString(PreferenceConstants.P_RECENTFILE1),
        		store.getString(PreferenceConstants.P_RECENTFILE2),
            	store.getString(PreferenceConstants.P_RECENTFILE3),
            	store.getString(PreferenceConstants.P_RECENTFILE4),
            	store.getString(PreferenceConstants.P_RECENTFILE5)
        };
        List<String> lRecent = new ArrayList<String>();
        
        for(String s : recent){
        	if (!s.trim().equals("")){ //$NON-NLS-1$
        		lRecent.add(s);
        	}
        }
        viewer.setInput(lRecent);
		
        showAtStartup.setSelection(store.getBoolean(PreferenceConstants.P_SHOW_MENU_AT_STARTUP));
	}
	protected void initializeBounds() {
		
		this.getShell().setText(Messages.DialogWelcome_20); //$NON-NLS-1$
		setDatas();
		getShell().setSize(400, 500);
		
		
	}
}

