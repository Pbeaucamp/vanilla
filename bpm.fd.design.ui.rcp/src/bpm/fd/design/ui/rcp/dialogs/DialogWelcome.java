package bpm.fd.design.ui.rcp.dialogs;

import java.lang.reflect.Method;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.jface.wizard.IWizard;
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
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.internal.wizards.ImportWizardRegistry;
import org.eclipse.ui.internal.wizards.NewWizardRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.icons.Icons;
import bpm.fd.design.ui.rcp.Messages;
import bpm.fd.design.ui.rcp.action.ActionOpenFdModel;
import bpm.fd.design.ui.rcp.action.ActionOpenFdProject;
import bpm.fd.design.ui.rcp.preferences.Preferences;
import bpm.vanilla.designer.ui.common.IDesignerActivator;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.repository.ui.connection.CompositeWelcomeConnectionTab;

public class DialogWelcome extends Dialog {
	
	private IWorkbenchAction importAction = ActionFactory.IMPORT.create(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow());
	private Button showAtStartup, importDoc;

	private Button newForm, newPerf, openDoc;
	private Button closeOnOpen;

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
		l.setBackgroundImage(reg.get(Icons.small_splash));

		final TabFolder folder = new TabFolder(main, SWT.NONE);
		folder.setLayout(new GridLayout());
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		folder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button b = getButton(IDialogConstants.OPEN_ID);
				if (b == null) {
					return;
				}
				b.setEnabled(folder.getSelectionIndex() == 1);
			}
		});

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
		showAtStartup.setSelection(true);

		closeOnOpen = new Button(main, SWT.CHECK);
		closeOnOpen.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		closeOnOpen.setText("Close Dialog on Open");
		closeOnOpen.setSelection(true);
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
				return (Object[]) inputElement;
			}

			public void dispose() { }

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

		});
		viewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				IProject p = (IProject) element;
				return p.getName() + "-" + p.getLocation().toOSString(); //$NON-NLS-1$
			}

		});
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				IProject project = (IProject) ss.getFirstElement();
				ActionOpenFdProject a;
				try {
					a = new ActionOpenFdProject(project);
					a.run();
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(getShell(), Messages.DialogWelcome_5, Messages.DialogWelcome_6 + e.getCause().getMessage() + "\n" + e.getMessage()); //$NON-NLS-3$
				}
				if (closeOnOpen.getSelection()) {
					okPressed();
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

		newPerf = new Button(c, SWT.PUSH);
		newPerf.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		try {
			newPerf.setImage(Activator.getDefault().getImageRegistry().get(Icons.fd_16));
		} catch (Exception ex) {
			ex.printStackTrace();
			newPerf.setEnabled(false);
		}
		newPerf.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();

				IWizard wiz;
				try {
					wiz = NewWizardRegistry.getInstance().findWizard("bpm.fd.design.ui.wizard.NewFdMultiPageWizard").createWizard(); //$NON-NLS-1$
					WizardDialog d = new WizardDialog(window.getShell(), wiz);
					d.open();
				} catch (CoreException e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), Messages.DialogWelcome_17, Messages.DialogWelcome_18 + e1.getMessage());
				}

			}

		});

		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l.setText(Messages.DialogWelcome_19);

		newForm = new Button(c, SWT.PUSH);
		newForm.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		newForm.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();

				IWizard wiz;
				try {
					wiz = NewWizardRegistry.getInstance().findWizard("bpm.fd.design.ui.wizard.NewVanillaFormWizard").createWizard(); //$NON-NLS-1$
					WizardDialog d = new WizardDialog(window.getShell(), wiz);
					d.open();
				} catch (CoreException e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), Messages.DialogWelcome_13, Messages.DialogWelcome_14 + e1.getMessage());
				}
			}

		});

		try {
			newForm.setImage(Activator.getDefault().getImageRegistry().get(Icons.fd_16));
		} catch (Exception ex) {
			ex.printStackTrace();
			newForm.setEnabled(false);
		}

		l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l.setText(Messages.DialogWelcome_15);

		openDoc = new Button(c, SWT.PUSH);
		openDoc.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		openDoc.setImage(reg.get(Icons.open));
		openDoc.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ActionOpenFdModel a = new ActionOpenFdModel();
				a.run();

			}

		});

		Label l2 = new Label(c, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l2.setText(Messages.DialogWelcome_20);

		importDoc = new Button(c, SWT.PUSH);
		importDoc.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		importDoc.setImage(importAction.getImageDescriptor().createImage());
		importDoc.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {

					IWorkbenchWizard wiz = ImportWizardRegistry.getInstance().findWizard("bpm.fd.repository.ui.wizard.RepositoryImportWizard").createWizard(); //$NON-NLS-1$

					if (wiz.getClass().getName().equals("bpm.fd.repository.ui.wizard.FdExportWizard")) { //$NON-NLS-1$
						try {
							Method m = wiz.getClass().getMethod("setDesignerActivator", IDesignerActivator.class); //$NON-NLS-1$

							Bundle bundle = Platform.getBundle("bpm.fd.repository.ui"); //$NON-NLS-1$
							String activator = (String) bundle.getHeaders().get(Constants.BUNDLE_ACTIVATOR);

							Class<?> activatorClass = bundle.loadClass(activator);
							Method method = activatorClass.getMethod("getDefault"); //$NON-NLS-1$
							Object activatorInstance = method.invoke(null);

							m.invoke(wiz, activatorInstance);
						} catch (Exception ex) {
							ex.printStackTrace();
							MessageDialog.openError(getShell(), Messages.DialogWelcome_26, Messages.DialogWelcome_27 + ex.getMessage());
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
		l3.setText(Messages.DialogWelcome_28);

		return c;
	}

	private Control createRepositoryTab(TabFolder folder) {
		final CompositeWelcomeConnectionTab container = new CompositeWelcomeConnectionTab(folder, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true));

		Button b = new Button(container, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		b.setText(Messages.DialogWelcome_29);
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Bundle bundle = Platform.getBundle("bpm.fd.repository.ui"); //$NON-NLS-1$
				String activator = (String) bundle.getHeaders().get(Constants.BUNDLE_ACTIVATOR);
				try {
					Class<?> activatorClass = bundle.loadClass(activator);
					Method method = activatorClass.getMethod("getDefault"); //$NON-NLS-1$
					Object activatorInstance = method.invoke(null);

					method = activatorClass.getMethod("setRepositoryContext", IRepositoryContext.class); //$NON-NLS-1$

					try {
						IRepositoryContext r = container.getRepositoryContext();
						method.invoke(activatorInstance, r);

						MessageDialog.openInformation(getShell(), Messages.DialogWelcome_33, Messages.DialogWelcome_34);
						importDoc.setEnabled(true);

					} catch (Exception ex) {
						ex.printStackTrace();
						method.invoke(activatorInstance, null);
						importDoc.setEnabled(false);
						MessageDialog.openError(getShell(), Messages.DialogWelcome_35, ex.getMessage());

					}
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.DialogWelcome_36, ex.getMessage());
				}
			}
		});

		return container;
	}

	private void setDatas() {
		viewer.setInput(ResourcesPlugin.getWorkspace().getRoot().getProjects());
	}

	protected void initializeBounds() {

		this.getShell().setText(Messages.DialogWelcome_37);
		setDatas();
		getShell().setSize(400, 500);

	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, true);
		Button b = createButton(parent, IDialogConstants.OPEN_ID, IDialogConstants.OPEN_LABEL, true);
		b.setEnabled(false);
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				StringBuffer errors = new StringBuffer();

				for (Object o : ss.toList()) {
					IProject project = (IProject) o;
					ActionOpenFdProject a;
					try {
						a = new ActionOpenFdProject(project);
						a.run();
					} catch (Exception ex) {
						ex.printStackTrace();
						errors.append("Could not open project " + project.getName() + " : " + ex.getMessage() + "\n");

					}
				}
				if (errors.length() > 0) {
					MessageDialog.openError(getShell(), Messages.DialogWelcome_5, errors.toString());
				}

				if (closeOnOpen.getSelection()) {
					okPressed();
				}

			}
		});
	}

	@Override
	protected void okPressed() {
		IPreferenceStore store = bpm.fd.design.ui.rcp.Activator.getDefault().getPreferenceStore();
		store.setValue(Preferences.SHOW_WELCOME_AT_STARTUP, showAtStartup.getSelection());

		super.okPressed();
	}

}
