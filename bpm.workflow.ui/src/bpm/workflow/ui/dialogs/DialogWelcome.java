package bpm.workflow.ui.dialogs;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.internal.wizards.ImportWizardRegistry;

import bpm.vanilla.designer.ui.common.IDesignerActivator;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.repository.ui.connection.CompositeWelcomeConnectionTab;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.actions.ActionOpen;
import bpm.workflow.ui.icons.IconsNames;
import bpm.workflow.ui.preferences.PreferencesConstants;

/**
 * Welcome dialog of the BiWorkflow
 * 
 * @author CAMUS, MARTIN
 * 
 */
public class DialogWelcome extends Dialog {

	private Button importDoc;
	private boolean isConnect = false;

	private Button newDoc, openDoc;

	private ListViewer viewer;

	public DialogWelcome(Shell parentShell) {
		super(parentShell);
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

		l.setBackgroundImage(reg.get(IconsNames.small_splash));

		TabFolder folder = new TabFolder(main, SWT.NONE);
		folder.setLayout(new GridLayout());
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabItem start = new TabItem(folder, SWT.NONE);
		start.setText(Messages.DialogWelcome_0);
		start.setControl(createButtonsTab(folder));

		TabItem recent = new TabItem(folder, SWT.NONE);
		recent.setText(Messages.DialogWelcome_1);
		recent.setControl(createRecentTab(folder));

		TabItem repository = new TabItem(folder, SWT.NONE);
		repository.setText(Messages.DialogWelcome_2);
		repository.setControl(createRepositoryTab(folder));

		return folder;
	}

	private Control createRecentTab(TabFolder folder) {

		Composite c = new Composite(folder, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));

		viewer = new ListViewer(c, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<String> l = (List<String>) inputElement;
				return l.toArray(new String[l.size()]);
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});
		viewer.setLabelProvider(new LabelProvider());
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if(ss.isEmpty()) {
					return;
				}

				try {
					String file = (String) ss.getFirstElement();
					ActionOpen a = new ActionOpen(file);
					a.run();
				} catch(Exception eto) {
					eto.printStackTrace();
				}

			}

		});

		return c;
	}

	private Control createButtonsTab(TabFolder folder) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		Composite c = new Composite(folder, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));

		if(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow() != null) {
			newDoc = new Button(c, SWT.PUSH);
			newDoc.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			newDoc.setImage(reg.get(IconsNames.NEW));
			newDoc.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					DialogNewWorkflow dialwk = new DialogNewWorkflow(getParentShell());
					dialwk.open();
				}

			});
		}

		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l.setText(Messages.DialogWelcome_3);

		openDoc = new Button(c, SWT.PUSH);
		openDoc.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		openDoc.setImage(reg.get(IconsNames.OPEN_16));
		openDoc.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ActionOpen a = new ActionOpen(null);
				a.run();

			}

		});

		Label l2 = new Label(c, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l2.setText(Messages.DialogWelcome_4);

		importDoc = new Button(c, SWT.PUSH);
		importDoc.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		importDoc.setImage(reg.get(IconsNames.BIW));
		importDoc.addSelectionListener(new SelectionAdapter() {

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
							MessageDialog.openError(getShell(), Messages.DialogWelcome_11, Messages.DialogWelcome_12 + ex.getMessage());
						}
						
						
					}
					
					
					
					WizardDialog dizl = new WizardDialog(getShell(), wiz);
					dizl.open();
				} catch (CoreException e1) {
					e1.printStackTrace();
				}
				
				
				

//				DialOpenRepositoryBIW dialbiw = new DialOpenRepositoryBIW(getParentShell());
//				dialbiw.open();

			}

		});
		importDoc.setEnabled(isConnect);

		Label l3 = new Label(c, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l3.setText(Messages.DialogWelcome_5);

		return c;
	}

	private Control createRepositoryTab(TabFolder folder) {
		final CompositeWelcomeConnectionTab container = new CompositeWelcomeConnectionTab(folder, SWT.NONE);
		container.setLayoutData(new GridData());

		Button b = new Button(container, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		b.setText(Messages.DialogWelcome_6);
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					IRepositoryContext r = container.getRepositoryContext();
					Activator.getDefault().setRepositoryContext(r);

					MessageDialog.openInformation(getShell(), Messages.DialogWelcome_7, Messages.DialogWelcome_8);

					importDoc.setEnabled(true);

				} catch(Exception ex) {
					ex.printStackTrace();
					Activator.getDefault().setRepositoryConnection(null);
					MessageDialog.openError(getShell(), Messages.DialogWelcome_9, ex.getMessage());

				}
			}

		});

		return container;
	}

	private void setDatas() {
		/*
		 * load RecentFiles
		 */
		// recentMenu
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		String[] recent = new String[] { store.getString(PreferencesConstants.P_RECENTFILE1), store.getString(PreferencesConstants.P_RECENTFILE2), store.getString(PreferencesConstants.P_RECENTFILE3), store.getString(PreferencesConstants.P_RECENTFILE4), store.getString(PreferencesConstants.P_RECENTFILE5) };
		List<String> lRecent = new ArrayList<String>();

		for(String s : recent) {
			if(!s.trim().equals("")) { //$NON-NLS-1$
				lRecent.add(s);
			}
		}
		viewer.setInput(lRecent);

	}

	protected void initializeBounds() {

		this.getShell().setText(Messages.DialogWelcome_10);
		setDatas();
		getShell().setSize(400, 500);

	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, true);
	}
}
