package bpm.vanilla.repository.ui.connection;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.designer.ui.common.preferences.PreferencesConstants;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.repository.ui.Activator;
import bpm.vanilla.repository.ui.Messages;
import bpm.vanilla.repository.ui.viewers.RepositoryLabelProvider;

public class DialogConnect extends Dialog {
	private Text host, login, password;
	private static Font font = new Font(Display.getCurrent(), "Times New Roman", 12, SWT.ITALIC); //$NON-NLS-1$
	private ComboViewer repositories;
	private ComboViewer groups;

	private ModifyListener lst = new ModifyListener() {

		@Override
		public void modifyText(ModifyEvent e) {
			groups.setInput(Collections.EMPTY_LIST);
			repositories.setInput(Collections.EMPTY_LIST);
			Button b = getButton(IDialogConstants.OK_ID);
			b.setEnabled(false);
		}
	};

	private IRepositoryContext context;

	private Button save;
	private BaseVanillaContext vanillaCtx;
	private Button loadGroups;
	private ISelectionChangedListener listener = new ISelectionChangedListener() {

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			Button b = getButton(IDialogConstants.OK_ID);
			if (b != null) {
				if (groups.getSelection().isEmpty() || repositories.getSelection().isEmpty()) {
					b.setEnabled(false);
				}
				else {
					b.setEnabled(true);
				}
			}

		}
	};

	protected void initializeBounds() {

		this.getShell().setText("Vanilla Platform Connection"); //$NON-NLS-1$
		super.initializeBounds();
		this.getShell().setSize(400, 300);

	}

	public DialogConnect(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);

	}

	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.DialogConnect_0);
		l3.setFont(font);

		host = new Text(container, SWT.BORDER);
		host.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l6 = new Label(container, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l6.setText(Messages.DialogConnect_1);
		l6.setFont(font);

		login = new Text(container, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l7 = new Label(container, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.DialogConnect_2);
		l7.setFont(font);

		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		loadGroups = new Button(container, SWT.PUSH);
		loadGroups.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 2, 1));
		loadGroups.setText(Messages.DialogConnect_3);

		loadGroups.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					connect(false);
				} catch (Exception ex) {
					ex.printStackTrace();
					groups.setInput(Collections.EMPTY_LIST);
					repositories.setInput(Collections.EMPTY_LIST);
					vanillaCtx = null;
					MessageDialog.openError(getShell(), Messages.DialogConnect_4, Messages.DialogConnect_5 + ex.getMessage());
				}
			}
		});

		l7 = new Label(container, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.DialogConnect_6);
		l7.setFont(font);

		groups = new ComboViewer(container, SWT.READ_ONLY);
		groups.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groups.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Group) element).getName();
			}
		});
		groups.setContentProvider(new ArrayContentProvider());

		l7 = new Label(container, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.DialogConnect_7);
		l7.setFont(font);

		repositories = new ComboViewer(container, SWT.READ_ONLY);
		repositories.setLabelProvider(new RepositoryLabelProvider());
		repositories.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		repositories.setContentProvider(new ArrayContentProvider());

		save = new Button(container, SWT.CHECK);
		save.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		save.setText(Messages.DialogConnect_8);
		save.setSelection(true);

		groups.addSelectionChangedListener(listener);
		repositories.addSelectionChangedListener(listener);

		fill(true);
		return container;

	}

	private void connect(boolean isTimedOut) throws Exception {
		vanillaCtx = new BaseVanillaContext(host.getText(), login.getText(), password.getText());

		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);

		User user = vanillaApi.getVanillaSecurityManager().authentify("", vanillaCtx.getLogin(), vanillaCtx.getPassword(), isTimedOut);
		repositories.setInput(vanillaApi.getVanillaRepositoryManager().getUserRepositories(login.getText()));
		groups.setInput(vanillaApi.getVanillaSecurityManager().getGroups(user));

	}

	private void fill(boolean isTimedOut) {
		if (Activator.getDefault().getDesignerActivator() != null && Activator.getDefault().getDesignerActivator().getRepositoryContext() != null) {
			IRepositoryContext ctx = Activator.getDefault().getDesignerActivator().getRepositoryContext();

			login.setText(ctx.getVanillaContext().getLogin());
			password.setText(ctx.getVanillaContext().getPassword());
			host.setText(ctx.getVanillaContext().getVanillaUrl());

			try {
				connect(isTimedOut);
				if (groups.getInput() != null) {
					for (Group g : (List<Group>) groups.getInput()) {
						if (g.getId() == ctx.getGroup().getId()) {
							groups.setSelection(new StructuredSelection(g));
							break;
						}
					}
				}

				if (repositories.getInput() != null) {
					for (Repository g : (List<Repository>) repositories.getInput()) {
						if (g.getId() == ctx.getRepository().getId()) {
							repositories.setSelection(new StructuredSelection(g));
							break;
						}
					}
				}
			} catch (Exception ex) {

			}
		}
		else {
			IPreferenceStore s = bpm.vanilla.designer.ui.common.Activator.getDefault().getPreferenceStore();

			login.setText(s.getString(PreferencesConstants.P_VANILLA_LOGIN));
			password.setText(s.getString(PreferencesConstants.P_VANILLA_PASSWORD));
			host.setText(s.getString(PreferencesConstants.P_VANILLA_URL));

			try {
				connect(isTimedOut);
				if (groups.getInput() != null) {
					for (Group g : (List<Group>) groups.getInput()) {
						if (g.getId() == s.getInt(PreferencesConstants.P_VANILLA_GROUP_ID)) {
							groups.setSelection(new StructuredSelection(g));
							break;
						}
					}
				}

				if (repositories.getInput() != null) {
					for (Repository g : (List<Repository>) repositories.getInput()) {
						if (g.getId() == s.getInt(PreferencesConstants.P_VANILLA_REP_ID)) {
							repositories.setSelection(new StructuredSelection(g));
							break;
						}
					}
				}
			} catch (Exception ex) {

			}
		}

		login.addModifyListener(lst);
		password.addModifyListener(lst);
		host.addModifyListener(lst);

	}

	@Override
	protected void okPressed() {
		try {
			repositories.removeSelectionChangedListener(listener);
			groups.removeSelectionChangedListener(listener);
			
			Group selectedGroup = (Group) (((IStructuredSelection) groups.getSelection()).getFirstElement());
			
			RemoteVanillaPlatform vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
			
			if(vanillaApi.getVanillaSecurityManager().canAccessApp(selectedGroup.getId(), Activator.getDefault().getDesignerActivator().getApplicationId())) {
				context = new BaseRepositoryContext(vanillaCtx, selectedGroup, (Repository) (((IStructuredSelection) repositories.getSelection()).getFirstElement()));

				try {
					Activator.getDefault().getDesignerActivator().setRepositoryContext(context);
				} catch (Exception ex) {

				}

				if (save.getSelection()) {
					bpm.vanilla.designer.ui.common.Activator.getDefault().getPreferenceStore().setValue(PreferencesConstants.P_VANILLA_GROUP_ID, context.getGroup().getId());
					bpm.vanilla.designer.ui.common.Activator.getDefault().getPreferenceStore().setValue(PreferencesConstants.P_VANILLA_LOGIN, context.getVanillaContext().getLogin());
					bpm.vanilla.designer.ui.common.Activator.getDefault().getPreferenceStore().setValue(PreferencesConstants.P_VANILLA_PASSWORD, context.getVanillaContext().getPassword());
					bpm.vanilla.designer.ui.common.Activator.getDefault().getPreferenceStore().setValue(PreferencesConstants.P_VANILLA_REP_ID, context.getRepository().getId());
					bpm.vanilla.designer.ui.common.Activator.getDefault().getPreferenceStore().setValue(PreferencesConstants.P_VANILLA_URL, context.getVanillaContext().getVanillaUrl());
				}
				super.okPressed();
				MessageDialog.openInformation(getShell(), Messages.DialogConnect_9, Messages.DialogConnect_10);
			}
			else {
				MessageDialog.openError(getShell(), "User Rights", "You are not allowed to access this application.");
			}
		} catch (Exception ex) {
			MessageDialog.openError(getShell(), Messages.DialogConnect_11, ex.getMessage());
			Activator.getDefault().getDesignerActivator().setRepositoryContext(null);
			ex.printStackTrace();
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.DialogConnect_12, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false); //$NON-NLS-1$
	}

	public IRepositoryContext getRepositoryContext() {
		return context;
	}
}
