package bpm.birep.admin.client.views.validation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import adminbirep.Messages;
import adminbirep.icons.Icons;
import bpm.birep.admin.client.content.views.ViewTree;
import bpm.birep.admin.client.dialog.DialogAlerts;
import bpm.birep.admin.client.dialog.DialogPickupItem;
import bpm.birep.admin.client.dialog.DialogPickupUsers;
import bpm.birep.admin.client.dialog.DilaogPickupUser;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.validation.UserValidation;
import bpm.vanilla.platform.core.beans.validation.UserValidation.UserValidationType;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ViewValidation extends ViewPart implements ISelectionListener {
	public static final String ID = "bpm.birep.admin.client.viewvalidation"; //$NON-NLS-1$

	private Text txtStartEtl, txtEndEtl, txtAction, txtAdminUser;
	private ToolBar toolbarCommentators, toolbarValidators;
	private TableViewer viewerCommentators, viewerValidators;
	private Button checkActivate, btnSelectStartEtl, btnSelectEndEtl, btnDefineAction, btnSelectAdminUser, btnApply;

	private Validation validation;
	private User adminUser;
	private List<User> commentators;
	private List<User> validators;

	private Repository repository;

	private RepositoryItem startEtl, endEtl;
	private Alert alert;

	public ViewValidation() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(this);
		try {
			repository = new Repository(Activator.getDefault().getRepositoryApi(), IRepositoryApi.GTW_TYPE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().removeSelectionListener(this);
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		buildContent(container);

		final RepositoryItem selectedObject = ViewTree.selectedObject != null && ViewTree.selectedObject instanceof TreeItem ? ((TreeItem) ViewTree.selectedObject).getItem() : null;
		if (selectedObject != null && selectedObject.getType() == IRepositoryApi.CUST_TYPE || selectedObject.getType() == IRepositoryApi.FD_TYPE) {
			BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
				@Override
				public void run() {
					try {
						loadValidation(selectedObject.getId());
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
		}
		else {
			checkActivate.setEnabled(false);
			btnApply.setEnabled(false);
			updateUi(false);
			clearUi();
		}
	}

	private void buildContent(Composite parent) {
		checkActivate = new Button(parent, SWT.CHECK);
		checkActivate.setText(Messages.ViewValidation_0);
		checkActivate.setEnabled(false);
		checkActivate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateUi(checkActivate.getSelection());
			}
		});

		Composite compositeOptions = new Composite(parent, SWT.NONE);
		compositeOptions.setLayout(new GridLayout(3, false));
		compositeOptions.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		Label lblStartEtl = new Label(compositeOptions, SWT.NONE);
		lblStartEtl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblStartEtl.setText(Messages.ViewValidation_1);

		txtStartEtl = new Text(compositeOptions, SWT.BORDER);
		txtStartEtl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtStartEtl.setEnabled(false);

		btnSelectStartEtl = new Button(compositeOptions, SWT.PUSH);
		btnSelectStartEtl.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		btnSelectStartEtl.setText("..."); //$NON-NLS-1$
		btnSelectStartEtl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickupItem d = new DialogPickupItem(getSite().getShell(), repository);
				if (d.open() == DilaogPickupUser.OK) {
					startEtl = d.getSelectedItem();
					txtStartEtl.setText(startEtl.getName());
				}
			}
		});

		Label lblEndEtl = new Label(compositeOptions, SWT.NONE);
		lblEndEtl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblEndEtl.setText(Messages.ViewValidation_3);

		txtEndEtl = new Text(compositeOptions, SWT.BORDER);
		txtEndEtl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtEndEtl.setEnabled(false);

		btnSelectEndEtl = new Button(compositeOptions, SWT.PUSH);
		btnSelectEndEtl.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		btnSelectEndEtl.setText("..."); //$NON-NLS-1$
		btnSelectEndEtl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickupItem d = new DialogPickupItem(getSite().getShell(), repository);
				if (d.open() == DilaogPickupUser.OK) {
					endEtl = d.getSelectedItem();
					txtEndEtl.setText(endEtl.getName());
				}
			}
		});

		Label lblAction = new Label(compositeOptions, SWT.NONE);
		lblAction.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblAction.setText(Messages.ViewValidation_5);

		txtAction = new Text(compositeOptions, SWT.BORDER);
		txtAction.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtAction.setEnabled(false);

		btnDefineAction = new Button(compositeOptions, SWT.PUSH);
		btnDefineAction.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		btnDefineAction.setText("..."); //$NON-NLS-1$
		btnDefineAction.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogAlerts d = new DialogAlerts(getSite().getShell());
				if (d.open() == DialogAlerts.OK) {
					alert = d.getSelectedAlert();
					txtAction.setText(alert.getName());
				}
			}
		});

		Label lblAdminUser = new Label(compositeOptions, SWT.NONE);
		lblAdminUser.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblAdminUser.setText(Messages.ViewValidation_2);

		txtAdminUser = new Text(compositeOptions, SWT.BORDER);
		txtAdminUser.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtAdminUser.setEnabled(false);

		btnSelectAdminUser = new Button(compositeOptions, SWT.PUSH);
		btnSelectAdminUser.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		btnSelectAdminUser.setText("..."); //$NON-NLS-1$
		btnSelectAdminUser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DilaogPickupUser d = new DilaogPickupUser(getSite().getShell());
				if (d.open() == DialogAlerts.OK) {
					adminUser = d.getSelectedUser();
					txtAdminUser.setText(adminUser.getLogin());
				}
			}
		});

		Composite compositeUsers = new Composite(parent, SWT.NONE);
		compositeUsers.setLayout(new GridLayout(2, true));
		compositeUsers.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lblCommentators = new Label(compositeUsers, SWT.NONE);
		lblCommentators.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblCommentators.setText(Messages.ViewValidation_9);

		Label lblValidators = new Label(compositeUsers, SWT.NONE);
		lblValidators.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblValidators.setText(Messages.ViewValidation_10);

		createCommentatorToolbar(compositeUsers);
		createValidatorToolbar(compositeUsers);

		Table tableCommentators = new Table(compositeUsers, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		tableCommentators.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		viewerCommentators = new TableViewer(tableCommentators);
		viewerCommentators.getTable().setHeaderVisible(true);
		viewerCommentators.getTable().setLinesVisible(true);
		viewerCommentators.setContentProvider(new UserStructuredProvider());
		viewerCommentators.setLabelProvider(new UserLabelProvider());

		TableColumn name = new TableColumn(viewerCommentators.getTable(), SWT.NONE);
		name.setText(Messages.ViewValidation_11);
		name.setWidth(100);

		TableColumn order = new TableColumn(viewerCommentators.getTable(), SWT.NONE);
		order.setText(Messages.ViewValidation_12);
		order.setWidth(80);

		Table tableValidators = new Table(compositeUsers, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		tableValidators.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		viewerValidators = new CheckboxTableViewer(tableValidators);
		viewerValidators.getTable().setHeaderVisible(true);
		viewerValidators.getTable().setLinesVisible(true);
		viewerValidators.setContentProvider(new UserStructuredProvider());
		viewerValidators.setLabelProvider(new UserLabelProvider());

		TableColumn nameValidator = new TableColumn(viewerValidators.getTable(), SWT.NONE);
		nameValidator.setText(Messages.ViewValidation_13);
		nameValidator.setWidth(200);

		btnApply = new Button(parent, SWT.PUSH);
		btnApply.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false));
		btnApply.setText(Messages.ViewValidation_14);
		btnApply.setEnabled(false);
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isValid() || !checkActivate.getSelection()) {
					validation.setDateBegin(new Date());
					validation.setStartEtlId(startEtl != null ? startEtl.getId() : 0);
					validation.setEndEtlId(endEtl != null ? endEtl.getId() : 0);
					validation.setAlertId(alert != null ? alert.getId() : 0);
					validation.setAdminUserId(adminUser != null ? adminUser.getId() : 0);
					validation.setActif(checkActivate.getSelection());

					List<UserValidation> commentators = getSelectedCommentators(validation);
					List<UserValidation> validators = getSelectedValidators(validation);

					validation.setCommentators(commentators);
					validation.setValidators(validators);

					IRepositoryApi repositoryApi = Activator.getDefault().getRepositoryApi();
					try {
						validation = repositoryApi.getRepositoryService().addOrUpdateValidation(validation);
						MessageDialog.openInformation(getSite().getShell(), Messages.ViewValidation_15, Messages.ViewValidation_16);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				else {
					MessageDialog.openInformation(getSite().getShell(), Messages.ViewValidation_17, Messages.ViewValidation_4 + "\n" //$NON-NLS-1$
							+ Messages.ViewValidation_7 + "\n" //$NON-NLS-1$
							+ Messages.ViewValidation_18 + "\n" //$NON-NLS-1$
							+ Messages.ViewValidation_20 + "\n" //$NON-NLS-1$
							+ Messages.ViewValidation_22 + "\n" //$NON-NLS-1$
							+ Messages.ViewValidation_26 + "\n"); //$NON-NLS-1$
				}
			}
		});

		updateUi(false);
	}

	private void createCommentatorToolbar(Composite parent) {
		toolbarCommentators = new ToolBar(parent, SWT.NONE);
		toolbarCommentators.setLayout(new GridLayout());

		ToolItem btnAddCommentators = new ToolItem(toolbarCommentators, SWT.PUSH);
		btnAddCommentators.setToolTipText(Messages.ViewValidation_24);
		btnAddCommentators.setImage(Activator.getDefault().getImageRegistry().get(Icons.ADD)); //$NON-NLS-1$
		btnAddCommentators.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickupUsers d = new DialogPickupUsers(getSite().getShell(), commentators);
				if (d.open() == DilaogPickupUser.OK) {
					List<User> users = d.getSelectedUser();

					commentators = new ArrayList<User>();
					commentators.addAll(users);

					viewerCommentators.setInput(commentators);
				}
			}
		});

		ToolItem btnRemoveCommentors = new ToolItem(toolbarCommentators, SWT.PUSH);
		btnRemoveCommentors.setToolTipText(Messages.ViewValidation_25);
		btnRemoveCommentors.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL)); //$NON-NLS-1$
		btnRemoveCommentors.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) viewerCommentators.getSelection();
				if (!ss.isEmpty()) {
					for (Object obj : ss.toList()) {
						if (obj instanceof User) {
							commentators.remove(obj);
						}
					}

					viewerCommentators.setInput(commentators);
				}
			}
		});

		ToolItem btnUp = new ToolItem(toolbarCommentators, SWT.PUSH);
		btnUp.setToolTipText(Messages.ViewValidation_29);
		btnUp.setImage(Activator.getDefault().getImageRegistry().get(Icons.ARROW_UP)); //$NON-NLS-1$
		btnUp.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) viewerCommentators.getSelection();
				if (!ss.isEmpty()) {
					List<User> toMoveUp = new ArrayList<User>();
					for (Object obj : ss.toList()) {
						if (obj instanceof User) {
							toMoveUp.add((User) obj);
						}
					}

					for (User toMove : toMoveUp) {
						int index = commentators.indexOf(toMove);
						if (index > 0) {
							commentators.remove(toMove);
							commentators.add(index - 1, toMove);
						}
					}

					viewerCommentators.setInput(commentators);
				}
			}
		});

		ToolItem btnDown = new ToolItem(toolbarCommentators, SWT.PUSH);
		btnDown.setToolTipText(Messages.ViewValidation_27);
		btnDown.setImage(Activator.getDefault().getImageRegistry().get(Icons.ARROW_DOWN)); //$NON-NLS-1$
		btnDown.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) viewerCommentators.getSelection();
				if (!ss.isEmpty()) {
					List<User> toMoveDown = new ArrayList<User>();
					for (Object obj : ss.toList()) {
						if (obj instanceof User) {
							toMoveDown.add((User) obj);
						}
					}

					for (int i = toMoveDown.size() - 1; i >= 0; i--) {
						int index = commentators.indexOf(toMoveDown.get(i));
						if (index < commentators.size() - 1) {
							commentators.remove(toMoveDown.get(i));
							commentators.add(index + 1, toMoveDown.get(i));
						}
					}

					viewerCommentators.setInput(commentators);
				}
			}
		});
	}

	private void createValidatorToolbar(Composite parent) {
		toolbarValidators = new ToolBar(parent, SWT.NONE);
		toolbarValidators.setLayout(new GridLayout());

		ToolItem btnAddValidators = new ToolItem(toolbarValidators, SWT.PUSH);
		btnAddValidators.setToolTipText(Messages.ViewValidation_30);
		btnAddValidators.setImage(Activator.getDefault().getImageRegistry().get(Icons.ADD)); //$NON-NLS-1$
		btnAddValidators.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickupUsers d = new DialogPickupUsers(getSite().getShell(), validators);
				if (d.open() == DilaogPickupUser.OK) {
					List<User> users = d.getSelectedUser();

					validators = new ArrayList<User>();
					validators.addAll(users);

					viewerValidators.setInput(validators);
				}
			}
		});

		ToolItem btnRemoveValidators = new ToolItem(toolbarValidators, SWT.PUSH);
		btnRemoveValidators.setToolTipText(Messages.ViewValidation_31);
		btnRemoveValidators.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL)); //$NON-NLS-1$
		btnRemoveValidators.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) viewerValidators.getSelection();
				if (!ss.isEmpty()) {
					for (Object obj : ss.toList()) {
						if (obj instanceof User) {
							validators.remove(obj);
						}
					}

					viewerValidators.setInput(validators);
				}
			}
		});
	}

	private void updateUi(boolean enabled) {
		btnSelectStartEtl.setEnabled(enabled);
		btnSelectEndEtl.setEnabled(enabled);
		btnDefineAction.setEnabled(enabled);
		btnSelectAdminUser.setEnabled(enabled);

		toolbarCommentators.setEnabled(enabled);
		toolbarValidators.setEnabled(enabled);

		viewerCommentators.getTable().setEnabled(enabled);
		viewerValidators.getTable().setEnabled(enabled);
	}

	private void clearUi() {
		validation = null;

		startEtl = null;
		txtStartEtl.setText(""); //$NON-NLS-1$

		endEtl = null;
		txtEndEtl.setText(""); //$NON-NLS-1$

		alert = null;
		txtAction.setText(""); //$NON-NLS-1$

		adminUser = null;
		txtAdminUser.setText(""); //$NON-NLS-1$

		commentators = new ArrayList<User>();
		viewerCommentators.setInput(commentators);
		validators = new ArrayList<User>();
		viewerValidators.setInput(validators);
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IStructuredSelection ss = (IStructuredSelection) selection;

		if (!ss.isEmpty() && ss.getFirstElement() instanceof TreeItem) {
			final RepositoryItem selectedObject = ((TreeItem) ss.getFirstElement()).getItem();
			if (selectedObject.getType() == IRepositoryApi.CUST_TYPE || selectedObject.getType() == IRepositoryApi.FD_TYPE) {
				BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
					@Override
					public void run() {
						try {
							loadValidation(selectedObject.getId());
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});
			}
			else {
				checkActivate.setEnabled(false);
				btnApply.setEnabled(false);
				updateUi(false);
				clearUi();
			}
		}
		else {
			checkActivate.setEnabled(false);
			btnApply.setEnabled(false);
			updateUi(false);
			clearUi();
		}
	}

	private void loadValidation(int itemId) throws Exception {
		if (validation != null && validation.getItemId() == itemId) {
			return;
		}

		IRepositoryApi repositoryApi = Activator.getDefault().getRepositoryApi();
		IVanillaAPI vanillaApi = Activator.getDefault().getVanillaApi();

		this.validation = repositoryApi.getRepositoryService().getValidation(itemId);
		if (validation != null) {
			checkActivate.setEnabled(true);
			btnApply.setEnabled(true);
			updateUi(validation.isActif());

			this.startEtl = repositoryApi.getRepositoryService().getDirectoryItem(validation.getStartEtlId());
			this.endEtl = repositoryApi.getRepositoryService().getDirectoryItem(validation.getEndEtlId());

			this.alert = getAlert(repositoryApi, validation.getAlertId());
			this.adminUser = getUser(vanillaApi, validation.getAdminUserId());

			this.commentators = new ArrayList<User>();
			if (validation.getCommentators() != null) {
				for (UserValidation userVal : validation.getCommentators()) {
					commentators.add(getUser(vanillaApi, userVal.getUserId()));
				}
			}

			this.validators = new ArrayList<User>();
			if (validation.getValidators() != null) {
				for (UserValidation userVal : validation.getValidators()) {
					validators.add(getUser(vanillaApi, userVal.getUserId()));
				}
			}

			checkActivate.setSelection(validation.isActif());
			txtStartEtl.setText(startEtl != null ? startEtl.getName() : Messages.ViewValidation_34);
			txtEndEtl.setText(endEtl != null ? endEtl.getName() : Messages.ViewValidation_35);
			txtAction.setText(alert != null ? alert.getName() : Messages.ViewValidation_36);
			txtAdminUser.setText(adminUser != null ? adminUser.getLogin() : Messages.ViewValidation_37);

			viewerCommentators.setInput(commentators);
			viewerValidators.setInput(validators);
		}
		else {
			updateUi(false);
			clearUi();

			this.validation = new Validation();
			validation.setItemId(itemId);

			checkActivate.setEnabled(true);
			checkActivate.setSelection(false);
			btnApply.setEnabled(true);
		}
	}

	private Alert getAlert(IRepositoryApi repositoryApi, int alertId) throws Exception {
		return repositoryApi.getAlertService().getAlert(alertId);
	}

	private User getUser(IVanillaAPI vanillaApi, int userId) throws Exception {
		return userId > 0 ? vanillaApi.getVanillaSecurityManager().getUserById(userId) : null;
	}

	private int getOrder(User user) {
		int order = 0;
		for (User commentator : commentators) {
			if (commentator.getId().equals(user.getId())) {
				return order;
			}
			order++;
		}
		return -1;
	}

	private List<UserValidation> getSelectedCommentators(Validation validation) {
		List<UserValidation> coms = new ArrayList<UserValidation>();
		for (User com : commentators) {
			UserValidation userVal = new UserValidation();
			userVal.setUserId(com.getId());
			userVal.setType(UserValidationType.COMMENTATOR);
			userVal.setUserOrder(getOrder(com));
			// userVal.setValidation(validation);

			coms.add(userVal);
		}
		return coms;
	}

	private List<UserValidation> getSelectedValidators(Validation validation) {
		List<UserValidation> vals = new ArrayList<UserValidation>();
		for (User val : validators) {
			UserValidation userVal = new UserValidation();
			userVal.setUserId(val.getId());
			userVal.setType(UserValidationType.VALIDATOR);
			userVal.setUserOrder(0);
			// userVal.setValidation(validation);

			vals.add(userVal);
		}
		return vals;
	}

	private boolean isValid() {
		return startEtl != null && endEtl != null && alert != null && adminUser != null && (commentators != null && !commentators.isEmpty()) && (validators != null && !validators.isEmpty());
	}

	private class UserStructuredProvider implements IStructuredContentProvider {

		@Override
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			List<User> l = (List<User>) inputElement;
			return l.toArray(new User[l.size()]);
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private class UserLabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return ((User) element).getName();
			case 1:
				return getOrder((User) element) + ""; //$NON-NLS-1$
			}
			return ""; //$NON-NLS-1$
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}
	};
}
