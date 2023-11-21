package bpm.birep.admin.client.dialog;

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

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.preferences.PreferenceConstants;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
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
		getShell().setText(Messages.DialogRepository_0);
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
		l3.setText(Messages.DialogRepository_1);

		userName = new Text(content, SWT.BORDER);
		userName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l4 = new Label(content, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.DialogRepository_2);

		password = new Text(content, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l2 = new Label(content, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.DialogRepository_3);

		url = new Text(content, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		loadRepositories = new Button(content, SWT.PUSH);
		loadRepositories.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		loadRepositories.setText(Messages.DialogRepository_13);
		loadRepositories.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				initRepositories();
			}
		});

		Label l5 = new Label(content, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l5.setText(Messages.DialogRepository_4);

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
		repositories.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Repository) element).getName();
			}
		});

		// fill texts
		userName.setText(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BI_REPOSITIRY_USER));
		password.setText(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BI_REPOSITIRY_PASSWORD));
		url.setText(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.p_BI_SECURITY_SERVER));

		Label _tip1 = new Label(content, SWT.NONE);
		_tip1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		_tip1.setText(""); //$NON-NLS-1$

		Label tip1 = new Label(content, SWT.NONE);
		tip1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		tip1.setText(Messages.DialogRepository_6);

		tip1 = new Label(content, SWT.NONE);
		tip1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		tip1.setText(Messages.DialogRepository_7);

		Label tip2 = new Label(content, SWT.NONE);
		tip2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		tip2.setText(Messages.DialogRepository_8);

		return content;
	}

	private void updateConnectButton() {
		try {
			loadRepositories.setEnabled(!(userName.getText().isEmpty() || url.getText().isEmpty()));
			getButton(IDialogConstants.OK_ID).setEnabled(authentified);
		} catch (NullPointerException e) {

		}

	}

	private void initRepositories() {
		try {

			RemoteVanillaPlatform tmp = new RemoteVanillaPlatform(url.getText(), userName.getText(), password.getText());
			User user = tmp.getVanillaSecurityManager().authentify("", userName.getText(), password.getText(), false); //$NON-NLS-1$

			if (!user.isSuperUser()) {
				throw new Exception(Messages.DialogRepository_5);
			}

			List<Repository> list = tmp.getVanillaRepositoryManager().getRepositories();
			String[] items = new String[list.size()];
			int i = 0;
			int toSelect = -1;

			for (Repository c : list) {
				if (c.getName().equals(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_CURRENT_REPOSITORY))) {
					toSelect = i;
				}
				items[i++] = c.getName();
			}

			repositories.setInput(list);

			if (toSelect != -1) {
				repositories.setSelection(new StructuredSelection(list.get(toSelect)));
				authentified = true;
			}
			else {
				authentified = false;
			}

		} catch (Throwable e) {
			repositories.setInput(Collections.EMPTY_LIST);
			e.printStackTrace();
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.DialogRepository_9, e));
			MessageDialog.openError(getShell(), Messages.DialogRepository_10, e.getMessage());
			authentified = false;
		}
		updateConnectButton();
	}

	protected void initManager() {
		try {
			Repository repDef = (Repository) ((IStructuredSelection) repositories.getSelection()).getFirstElement();
			IVanillaContext ctx = new BaseVanillaContext(url.getText(), userName.getText(), password.getText());
			Group dummy = new Group();
			dummy.setId(-1);
			RemoteVanillaPlatform vanillaApi = new RemoteVanillaPlatform(ctx);
			
			if(vanillaApi.getVanillaSecurityManager().canAccessApp(-1, Activator.SOFT_ID)) {
				Activator.getDefault().initManagers(new BaseRepositoryContext(ctx, dummy, repDef), vanillaApi);
			}
			else {
				MessageDialog.openError(getShell(), Messages.DialogRepository_15, Messages.DialogRepository_16);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogRepository_12, e.getMessage());
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button connect = createButton(parent, IDialogConstants.OK_ID, Messages.DialogRepository_14, true);
		connect.setEnabled(false);
	}

	@Override
	protected void okPressed() {
		initManager();
		if (!repositories.getSelection().isEmpty()) {
			String repNam = ((Repository) ((IStructuredSelection) repositories.getSelection()).getFirstElement()).getName();
			Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_CURRENT_REPOSITORY, repNam);
		}

		Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_BI_REPOSITIRY_USER, userName.getText());
		Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_BI_REPOSITIRY_PASSWORD, password.getText());
		Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.p_BI_SECURITY_SERVER, url.getText());

		super.okPressed();
	}

}
