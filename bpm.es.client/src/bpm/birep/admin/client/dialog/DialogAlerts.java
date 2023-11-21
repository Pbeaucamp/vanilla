package bpm.birep.admin.client.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.alerts.Alert;

public class DialogAlerts extends Dialog {

	private IRepositoryApi sock;
	private int repositoryId;

	private ComboViewer combo;
	private Alert selectedAlert;

	public DialogAlerts(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);

		this.sock = Activator.getDefault().getRepositoryApi();
		this.repositoryId = Activator.getDefault().getCurrentRepository().getId();
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		getShell().setText(Messages.DialogAlerts_0);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));

		createToolbar(c);

		combo = new ComboViewer(c, SWT.READ_ONLY);
		combo.setContentProvider(new IStructuredContentProvider() {

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<Alert> l = (List<Alert>) inputElement;
				return l.toArray(new Object[l.size()]);
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		combo.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Alert) element).getName();
			}

		});
		combo.setComparator(new ViewerComparator() {

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				return ((Alert) e1).getName().compareTo(((Alert) e2).getName());
			}

		});
		combo.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		loadAlerts();

		return c;
	}

	private void createToolbar(Composite parent) {
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayout(new GridLayout());

//		ToolItem btnAdd = new ToolItem(toolbar, SWT.PUSH);
//		btnAdd.setToolTipText(Messages.DialogAlerts_1);
//		btnAdd.setImage(Activator.getDefault().getImageRegistry().get(Icons.ADD)); //$NON-NLS-1$
//		btnAdd.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				CreateNewAlert wiz = new CreateNewAlert(sock, repositoryId);
//
//				WizardDialog dial = new WizardDialog(getShell(), wiz);
//				dial.setMinimumPageSize(600, 400);
//				dial.create();
//
//				if (dial.open() == Dialog.OK) {
//					Alert alert = wiz.getAlert();
//					try {
//						sock.getAlertService().createAlert(alert);
//					} catch (Exception _e) {
//						_e.printStackTrace();
//					}
//
//					loadAlerts();
//				}
//			}
//		});
//
//		ToolItem btnEdit = new ToolItem(toolbar, SWT.PUSH);
//		btnEdit.setToolTipText(Messages.DialogAlerts_2);
//		btnEdit.setImage(Activator.getDefault().getImageRegistry().get(Icons.EDIT)); //$NON-NLS-1$
//		btnEdit.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				Alert selectedAlert = getAlert();
//				if (selectedAlert != null) {
//
//					CreateNewAlert wiz = new CreateNewAlert(sock, selectedAlert, repositoryId);
//
//					WizardDialog dial = new WizardDialog(getShell(), wiz);
//					dial.setMinimumPageSize(600, 400);
//					dial.create();
//
//					if (dial.open() == Dialog.OK) {
//						Alert alert = wiz.getAlert();
//						try {
//							sock.getAlertService().updateAlert(alert);
//						} catch (Exception _e) {
//							_e.printStackTrace();
//						}
//
//						loadAlerts();
//					}
//				}
//			}
//		});
//
//		ToolItem btnDelete = new ToolItem(toolbar, SWT.PUSH);
//		btnDelete.setToolTipText(Messages.DialogAlerts_3);
//		btnDelete.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL)); //$NON-NLS-1$
//		btnDelete.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				Alert selectedAlert = getAlert();
//
//				if (MessageDialog.openConfirm(getShell(), Messages.DialogAlerts_4, Messages.DialogAlerts_5)) {
//					try {
//						sock.getAlertService().removeAlert(selectedAlert);
//					} catch (Exception _e) {
//						_e.printStackTrace();
//					}
//
//					loadAlerts();
//				}
//			}
//		});
	}

	private void loadAlerts() {
		try {
			combo.setInput(Activator.getDefault().getRepositoryApi().getAlertService().getAlertsWhitoutEvent());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Alert getAlert() {
		return ((IStructuredSelection) combo.getSelection()).getFirstElement() != null ? (Alert) ((IStructuredSelection) combo.getSelection()).getFirstElement() : null;
	}

	@Override
	protected void okPressed() {
		this.selectedAlert = getAlert();
		if (selectedAlert == null) {
			return;
		}
		super.okPressed();
	}

	public Alert getSelectedAlert() {
		return selectedAlert;
	}
}
