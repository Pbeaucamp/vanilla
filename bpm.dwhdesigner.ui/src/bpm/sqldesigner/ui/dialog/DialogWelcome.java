package bpm.sqldesigner.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.action.ActionOpenWorkspace;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.preferences.PreferenceConstants;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.repository.ui.connection.CompositeWelcomeConnectionTab;




public class DialogWelcome extends Dialog{
	
	private IWorkbenchAction importAction = ActionFactory.IMPORT.create(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow());
	private Button showAtStartup, importDoc;
	private CompositeWelcomeConnectionTab container;
	/*
	 * start Tab
	 */
	private Button newDoc, openDoc;
	
//	/*
//	 * Repository Tab
//	 */
//	private Text host, login, password;
//	private ComboViewer combo;
//	private HashMap<String, RepositoryConnection> map = new HashMap<String, RepositoryConnection>();
	
	/*
	 * Recent Tab
	 */
	private ListViewer viewer;
	
	public DialogWelcome(Shell parentShell) {
		super(parentShell);
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
		
		
//		TabItem repository = new TabItem(folder, SWT.NONE);
//		repository.setText("Repository");
//		repository.setControl(createRepositoryTab(folder));
		
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
		
		viewer = new ListViewer(c, SWT.H_SCROLL | SWT.V_SCROLL  | SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<String> l = (List<String>)inputElement;
				return l.toArray(new String[l.size()]);
			}

			public void dispose() {	}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
		});
		viewer.setLabelProvider(new LabelProvider());
		viewer.addDoubleClickListener(new IDoubleClickListener(){

			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				if (ss.isEmpty()){
					return;
				}
				
				
				String file = (String)ss.getFirstElement();
				ActionOpenWorkspace a = new ActionOpenWorkspace(file);
				a.run();
				
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
				IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
				ActionFactory.NEW.create(window).run();
				okPressed();
			}
			
		});
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l.setText(Messages.DialogWelcome_6);
		
		
		openDoc = new Button(c, SWT.PUSH);
		openDoc.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		openDoc.setImage(reg.get("open")); //$NON-NLS-1$
		openDoc.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell());
				fd.setFilterExtensions(new String[]{"*bidwh", "*.dwh", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				
				ActionOpenWorkspace a = new ActionOpenWorkspace(fd.open());
				a.run();
				okPressed();
				
			}
			
		});
		
		Label l2 = new Label(c, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l2.setText(Messages.DialogWelcome_11);

	
		
		importDoc = new Button(c, SWT.PUSH);
		importDoc.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		importDoc.setImage(importAction.getImageDescriptor().createImage());
		importDoc.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				importAction.run();
				okPressed();
			}
			
		});
		importDoc.setEnabled(false);
		
		Label l3 = new Label(c, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l3.setText(Messages.DialogWelcome_12);
		
		return c;
	}

	private Control createRepositoryTab(TabFolder folder){
		container = new CompositeWelcomeConnectionTab(folder, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true));

		
		Button b = new Button(container, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		b.setText(Messages.DialogWelcome_13);
		b.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					IRepositoryContext r = container.getRepositoryContext();
					Activator.getDefault().setRepositoryContext(r);

					MessageDialog.openInformation(getShell(), Messages.DialogWelcome_14, Messages.DialogWelcome_15);
					importDoc.setEnabled(true);
				}catch(Exception ex){
					ex.printStackTrace();
					Activator.getDefault().setRepositoryContext(null);
					importDoc.setEnabled(false);
					MessageDialog.openError(getShell(), Messages.DialogWelcome_16, ex.getMessage()); 
					
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
		this.getShell().setText(Messages.DialogWelcome_17);
		setDatas();
		getShell().setSize(400, 500);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
			createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, true);
	}
}

