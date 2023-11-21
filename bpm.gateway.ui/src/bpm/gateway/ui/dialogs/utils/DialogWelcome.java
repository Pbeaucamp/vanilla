package bpm.gateway.ui.dialogs.utils;

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
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.internal.wizards.ImportWizardRegistry;

import bpm.gateway.ui.Activator;
import bpm.gateway.ui.actions.ActionOpen;
import bpm.gateway.ui.gatewaywizard.GatewayWizard;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;
import bpm.gateway.ui.preferences.PreferencesConstants;
import bpm.vanilla.designer.ui.common.IDesignerActivator;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.repository.ui.connection.CompositeWelcomeConnectionTab;

public class DialogWelcome extends Dialog {
	private static Font font = new Font(Display.getCurrent(), "Times New Roman", 12, SWT.ITALIC); //$NON-NLS-1$
	private IWorkbenchAction importAction = ActionFactory.IMPORT.create(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow());
	private Button showAtStartup, importDoc;
	private CompositeWelcomeConnectionTab container;
	/*
	 * start Tab
	 */
	private Button newDoc, openDoc;

	/*
	 * Repository Tab
	 */
	// private Text host, login, password;
	// private ComboViewer combo;
	// private HashMap<String, RepositoryConnection> map = new HashMap<String, RepositoryConnection>();

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

		showAtStartup = new Button(main, SWT.CHECK);
		showAtStartup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		showAtStartup.setText(Messages.DialogWelcome_3);
		showAtStartup.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Activator.getDefault().getPreferenceStore().setValue(PreferencesConstants.P_SHOW_MENU_AT_STARTUP, showAtStartup.getSelection());
			}

		});
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
				if (ss.isEmpty()) {
					return;
				}

				String file = (String) ss.getFirstElement();
				ActionOpen a = new ActionOpen(file);
				a.run();

			}

		});

		return c;
	}

	private Control createButtonsTab(TabFolder folder) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		Composite c = new Composite(folder, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));

		newDoc = new Button(c, SWT.PUSH);
		newDoc.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		newDoc.setImage(reg.get("new")); //$NON-NLS-1$
		newDoc.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
				// ActionFactory.NEW.create(window).run();
				GatewayWizard wiz = new GatewayWizard();
				WizardDialog d = new WizardDialog(window.getShell(), wiz);
				d.open();
			}

		});

		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l.setText(Messages.DialogWelcome_5);

		openDoc = new Button(c, SWT.PUSH);
		openDoc.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		openDoc.setImage(reg.get("open")); //$NON-NLS-1$
		openDoc.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ActionOpen a = new ActionOpen(null);
				a.run();

			}

		});

		Label l2 = new Label(c, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l2.setText(Messages.DialogWelcome_7);

		importDoc = new Button(c, SWT.PUSH);
		importDoc.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		importDoc.setImage(importAction.getImageDescriptor().createImage());
		importDoc.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					IWorkbenchWizard wiz = ImportWizardRegistry.getInstance().findWizard("bpm.vanilla.repository.ui.importwizard").createWizard(); //$NON-NLS-1$

					if (wiz.getClass().getName().equals("bpm.vanilla.repository.ui.wizards.page.ExportObjectWizard")) { //$NON-NLS-1$
						try {
							Method m = wiz.getClass().getMethod("setDesignerActivator", IDesignerActivator.class); //$NON-NLS-1$
							m.invoke(wiz, Activator.getDefault());
						} catch (Exception ex) {
							ex.printStackTrace();
							MessageDialog.openError(getShell(), "Problem", "Unable to init Repositoty Wizard : " + ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
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
		l3.setText(Messages.DialogWelcome_9);

		return c;
	}

	private Control createRepositoryTab(TabFolder folder) {
		container = new CompositeWelcomeConnectionTab(folder, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true));

		Button b = new Button(container, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		b.setText(Messages.DialogWelcome_14);
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					IRepositoryContext r = container.getRepositoryContext();
					Activator.getDefault().setRepositoryContext(r);

					MessageDialog.openInformation(getShell(), Messages.DialogWelcome_15, Messages.DialogWelcome_16);
					importDoc.setEnabled(true);

				} catch (Exception ex) {
					ex.printStackTrace();
					Activator.getDefault().setRepositoryContext(null);
					importDoc.setEnabled(false);
					MessageDialog.openError(getShell(), Messages.DialogWelcome_17, ex.getMessage());

				}
			}

		});

		return container;
	}

	private void setDatas() {
		// try {
		//			List<RepositoryConnection> list = new ConnectionDigester("resources/connections.xml").getConnections(); //$NON-NLS-1$
		// for(RepositoryConnection r: list){
		// map.put(r.getName(), r);
		// }
		//
		// if (Activator.getRepositoryConnection() != null){
		//				((RepositoryConnection)Activator.getRepositoryConnection()).setName("Default"); //$NON-NLS-1$
		//				map.put("Default", (RepositoryConnection)Activator.getRepositoryConnection()); //$NON-NLS-1$
		// }
		//
		//
		// } catch (Exception e) {
		// MessageDialog.openError(getShell(), Messages.DialogWelcome_18, e.getMessage());
		// e.printStackTrace();
		// }
		//
		// combo.setInput(map.values());
		//		if (map.get("Default") != null){ //$NON-NLS-1$
		//			combo.setSelection(new StructuredSelection(map.get("Default"))); //$NON-NLS-1$
		// }
		//

		/*
		 * load RecentFiles
		 */
		// recentMenu
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		String[] recent = new String[] { store.getString(PreferencesConstants.P_RECENTFILE1), store.getString(PreferencesConstants.P_RECENTFILE2), store.getString(PreferencesConstants.P_RECENTFILE3), store.getString(PreferencesConstants.P_RECENTFILE4), store.getString(PreferencesConstants.P_RECENTFILE5) };
		List<String> lRecent = new ArrayList<String>();

		for (String s : recent) {
			if (!s.trim().equals("")) { //$NON-NLS-1$
				lRecent.add(s);
			}
		}
		viewer.setInput(lRecent);

		showAtStartup.setSelection(store.getBoolean(PreferencesConstants.P_SHOW_MENU_AT_STARTUP));
	}

	protected void initializeBounds() {
		this.getShell().setText(Messages.DialogWelcome_19);
		setDatas();
		getShell().setSize(400, 500);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, true);
	}

	@Override
	protected void okPressed() {
		IRepositoryContext r = null;
		try {
			r = container.getRepositoryContext();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Activator.getDefault().setRepositoryContext(r);

		super.okPressed();
	}

	@Override
	protected void cancelPressed() {

		super.cancelPressed();
	}
}
