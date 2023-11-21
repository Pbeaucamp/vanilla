package org.fasd.views.composites;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.internal.wizards.ImportWizardRegistry;
import org.fasd.i18N.LanguageText;
import org.fasd.preferences.PreferenceConstants;
import org.freeolap.FreemetricsPlugin;

import bpm.vanilla.designer.ui.common.IDesignerActivator;
import bpm.vanilla.repository.ui.connection.CompositeWelcomeConnectionTab;

public class DialogNew extends Dialog {
	private static Font font = new Font(Display.getCurrent(), "Times New Roman", 12, SWT.ITALIC); //$NON-NLS-1$
	/*
	 * 0 = new FA pro 1 = Open existing 2 = Open recent file 3 = Import from
	 * mondrian 4 = Create an XMLA schema
	 */
	private int action = 0;
	private Button b1, b2, b4, checkShowAtStartup, importDoc;
	private String path = null;
	private IWorkbenchAction importAction = ActionFactory.IMPORT.create(FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow());

	public DialogNew(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * 
	 * @return 0 = new FA pro 1 = Open existing 2 = Import from mondrian 3 =
	 *         cube wizard not implemented
	 */
	public int getAction() {
		return action;
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			super.buttonPressed(buttonId);
		} else {
			super.buttonPressed(buttonId);
		}

	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 500);
		Shell shell = this.getShell();
		Monitor primary = shell.getMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();

		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		shell.setLocation(x, y);

	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(LanguageText.DialogNew_0);
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, false);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
	
	protected Control getTabStartControl(TabFolder tabFolder) {
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		b1 = new Button(composite, SWT.PUSH);
		b1.setImage(new Image(tabFolder.getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/new.png")); //$NON-NLS-1$
		b1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		b1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				action = 0;
				buttonPressed(IDialogConstants.OK_ID);
			}
		});

		Label l1 = new Label(composite, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l1.setText(LanguageText.DialogNew_2);

		b2 = new Button(composite, SWT.PUSH);
		b2.setImage(new Image(tabFolder.getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/Open.png")); //$NON-NLS-1$
		b2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		b2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				action = 1;
				buttonPressed(IDialogConstants.OK_ID);
			}
		});

		Label l2 = new Label(composite, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l2.setText(LanguageText.DialogNew_4);

		b4 = new Button(composite, SWT.PUSH);
		b4.setImage(new Image(tabFolder.getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/dataset_wizard16.png")); //$NON-NLS-1$
		b4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		b4.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				action = 4;
				buttonPressed(IDialogConstants.OK_ID);
			}
		});

		Label l4 = new Label(composite, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l4.setText(LanguageText.DialogNew_21);

		importDoc = new Button(composite, SWT.PUSH);
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
							m.invoke(wiz, FreemetricsPlugin.getDefault());
						} catch (Exception ex) {
							ex.printStackTrace();
							MessageDialog.openError(getShell(), LanguageText.DialogNew_8, LanguageText.DialogNew_9 + ex.getMessage());
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

		Label l9 = new Label(composite, SWT.NONE);
		l9.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l9.setText(LanguageText.DialogNew_10);

		return composite;
	}

	protected Control createDialogArea(Composite parent) {
		ImageRegistry reg = FreemetricsPlugin.getDefault().getImageRegistry();

		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		// logo
		Composite logoComposite = new Composite(main, SWT.NONE);
		logoComposite.setLayout(new RowLayout(SWT.BORDER));
		logoComposite.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		Label logo = new Label(logoComposite, SWT.NONE);
		logo.setImage(reg.get("small_splash")); //$NON-NLS-1$

		// tabfolder
		TabFolder tabFolder = new TabFolder(main, SWT.NONE);
		tabFolder.setLayout(new GridLayout());
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabItem itemStart = new TabItem(tabFolder, SWT.NONE, 0);
		itemStart.setText(LanguageText.DialogNew_12);
		itemStart.setControl(getTabStartControl(tabFolder));

		TabItem itemRecent = new TabItem(tabFolder, SWT.NONE);
		itemRecent.setText(LanguageText.DialogNew_13);
		itemRecent.setControl(getTabRecentControl(tabFolder));

		TabItem repository = new TabItem(tabFolder, SWT.NONE);
		repository.setText(LanguageText.DialogNew_14);
		repository.setControl(getRepositoryControl(tabFolder));

		// checkbox
		checkShowAtStartup = new Button(main, SWT.CHECK);
		GridData g = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		g.verticalIndent = 15;
		checkShowAtStartup.setLayoutData(g);
		checkShowAtStartup.setText(LanguageText.DialogNew_15);

		FreemetricsPlugin plugin = FreemetricsPlugin.getDefault();
		IPreferenceStore store = plugin.getPreferenceStore();
		boolean checked = store.getBoolean(PreferenceConstants.P_SHOWNEWATSTARTUP);
		checkShowAtStartup.setSelection(checked);

		applyDialogFont(tabFolder);
		return tabFolder;
	}

	private Control getRepositoryControl(TabFolder tabFolder) {

		final CompositeWelcomeConnectionTab container = new CompositeWelcomeConnectionTab(tabFolder, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true));

		Button b = new Button(container, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		b.setText(LanguageText.DialogNew_16);
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					FreemetricsPlugin.getDefault().setRepositoryContext(container.getRepositoryContext());

					MessageDialog.openInformation(getShell(), LanguageText.DialogNew_17, LanguageText.DialogNew_18);
					importDoc.setEnabled(true);

				} catch (Exception ex) {
					MessageDialog.openError(getShell(), LanguageText.DialogNew_19, ex.getMessage());
					FreemetricsPlugin.getDefault().setRepositoryConnection(null);
					ex.printStackTrace();
				}
			}

		});

		return container;
	}

	protected Control getTabRecentControl(TabFolder tabFolder) {
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		final List listRecent = new List(composite, SWT.BORDER | SWT.H_SCROLL);
		listRecent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		IPreferenceStore store = FreemetricsPlugin.getDefault().getPreferenceStore();

		listRecent.add(store.getString(PreferenceConstants.P_RECENTFILE1));
		listRecent.add(store.getString(PreferenceConstants.P_RECENTFILE2));
		listRecent.add(store.getString(PreferenceConstants.P_RECENTFILE3));
		listRecent.add(store.getString(PreferenceConstants.P_RECENTFILE4));
		listRecent.add(store.getString(PreferenceConstants.P_RECENTFILE5));

		listRecent.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
				List list = (List) e.getSource();
				path = list.getSelection()[0];
				action = 2;
				buttonPressed(IDialogConstants.OK_ID);
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseUp(MouseEvent e) {
			}

		});
		Button ok = new Button(composite, SWT.PUSH);
		ok.setLayoutData(new GridData());
		ok.setText(LanguageText.DialogNew_20);
		ok.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				path = listRecent.getSelection()[0];
				action = 2;
				buttonPressed(IDialogConstants.OK_ID);
			}

		});

		return composite;
	}

	public String getPath() {
		return path;
	}

	@Override
	public boolean close() {
		saveInfos();
		return super.close();
	}

	private void saveInfos() {
		FreemetricsPlugin plugin = FreemetricsPlugin.getDefault();
		IPreferenceStore store = plugin.getPreferenceStore();

		store.setValue(PreferenceConstants.P_SHOWNEWATSTARTUP, checkShowAtStartup.getSelection());
	}
}