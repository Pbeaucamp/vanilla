package bpm.birep.admin.client.composites;

import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.dialog.DialogUpdatePass;
import bpm.birep.admin.client.views.ViewGroup;
import bpm.birep.admin.client.views.ViewRole;
import bpm.birep.admin.client.views.ViewUser;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.utils.MD5Helper;

public class CompositeUser extends Composite {

	private User user;
	private boolean buttonBar = false;

	private Text businessMail, cellular, function, name, password, confirm, passwordDays, login;
	private Text privateMail, skypeName, skypeNum, surname, phone;
	private Text fmUser;

	private Button passwordChangeButton, passwordValidity;

	private Button superUser, updatepass;

//	private HashMap<Integer, FmUser> fmUsers;
	private ListViewer groups;
	private Button ok, cancel;
	private Boolean goodUser = true;

	private ViewGroup viewG;
	private ViewRole viewR;
	private ViewUser viewU;

	private boolean newuser;

	public CompositeUser(Composite parent, int style, User user, boolean buttonBar) {
		super(parent, style);
		this.user = user;

		this.buttonBar = buttonBar;
//		initFmUSers();
		createContent();
		viewG = (ViewGroup) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewGroup.ID);
		viewR = (ViewRole) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewRole.ID);
		viewU = (ViewUser) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewUser.ID);
		fillData();
	}

//	private void initFmUSers() {
//		FactoryManager.init("", Tools.OS_TYPE_WINDOWS);
//		try {
//			IManager manager = FactoryManager.getManager();
//			List<FmUser> users = manager.getUsers();
//			
//			fmUsers = new HashMap<Integer, FmUser>();
//			if(users != null) {
//				for(FmUser user : users) {
//					fmUsers.put(user.getId(), user);
//				}
//			}
//		} catch (FactoryManagerException e) {
//			e.printStackTrace();
//		}
//	}

	private void createContent() {
		this.setLayout(new GridLayout(2, false));

		Label l111 = new Label(this, SWT.NONE);
		l111.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l111.setText(Messages.CompositeUser_19);

		login = new Text(this, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l.setText(Messages.CompositeUser_0);

		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l2.setText(Messages.CompositeUser_3);

		surname = new Text(this, SWT.BORDER);
		surname.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Composite buttonBarPass = new Composite(this, SWT.NONE);
		buttonBarPass.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		buttonBarPass.setLayout(new GridLayout(2, false));

		superUser = new Button(buttonBarPass, SWT.CHECK);
		superUser.setText(Messages.CompositeUser_4);
		superUser.setLayoutData(new GridData());

		passwordChangeButton = new Button(buttonBarPass, SWT.CHECK);
		passwordChangeButton.setText(Messages.CompositeUser_6);
		passwordChangeButton.setLayoutData(new GridData());

		if (user == null || (user.getPassword() == null) || user.getPassword().trim().equals("")) { //$NON-NLS-1$

			Label l9 = new Label(this, SWT.NONE);
			l9.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
			l9.setText(Messages.CompositeUser_7);

			password = new Text(this, SWT.BORDER | SWT.PASSWORD);
			password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
			
			Label l12 = new Label(this, SWT.NONE);
			l12.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
			l12.setText(Messages.CompositeUser_9);

			confirm = new Text(this, SWT.BORDER | SWT.PASSWORD);
			confirm.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
			newuser = true;
		}
		
		passwordValidity = new Button(buttonBarPass, SWT.CHECK);
		passwordValidity.setText(Messages.CompositeUser_1);
		passwordValidity.setLayoutData(new GridData());
		passwordValidity.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(passwordValidity.getSelection()) {
					passwordDays.setEnabled(true);
				}
				else {
					passwordDays.setEnabled(false);
				}
			}
		});
		
		passwordDays = new Text(buttonBarPass, SWT.BORDER);
		passwordDays.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		passwordDays.setText("0"); //$NON-NLS-1$
		passwordDays.setEnabled(false);

		Label l10 = new Label(this, SWT.NONE);
		l10.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l10.setText(Messages.CompositeUser_10);

		Composite _c = new Composite(this, SWT.NONE);
		_c.setLayout(new GridLayout(2, false));
		_c.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));

		fmUser = new Text(_c, SWT.BORDER);
		fmUser.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		fmUser.setEnabled(false);

		Button b = new Button(_c, SWT.PUSH);
		b.setText(Messages.CompositeUser_11);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setEnabled(false);
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

//				DialogFmUser dial = new DialogFmUser(getShell(), fmUsers);
//				if (dial.open() == DialogFmUser.OK) {
//					fmUser.setText(fmUsers.get(dial.getSelected()).getName());
//					CompositeUser.this.user.setFmUserId(dial.getSelected());
//				}

			}

		});

		Label l1 = new Label(this, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l1.setText(Messages.CompositeUser_12);

		function = new Text(this, SWT.BORDER);
		function.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l3.setText(Messages.CompositeUser_13);

		cellular = new Text(this, SWT.BORDER);
		cellular.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l4 = new Label(this, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l4.setText(Messages.CompositeUser_14);

		phone = new Text(this, SWT.BORDER);
		phone.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l5 = new Label(this, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l5.setText(Messages.CompositeUser_15);

		businessMail = new Text(this, SWT.BORDER);
		businessMail.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l6 = new Label(this, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l6.setText(Messages.CompositeUser_16);

		privateMail = new Text(this, SWT.BORDER);
		privateMail.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l7 = new Label(this, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l7.setText(Messages.CompositeUser_17);

		skypeName = new Text(this, SWT.BORDER);
		skypeName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l8 = new Label(this, SWT.NONE);
		l8.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l8.setText(Messages.CompositeUser_18);

		skypeNum = new Text(this, SWT.BORDER);
		skypeNum.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		if (!(user == null || (user.getPassword() == null) || user.getPassword().trim().equals(""))) { //$NON-NLS-1$
			newuser = false;
			updatepass = new Button(this, SWT.PUSH);
			updatepass.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
			updatepass.setText(Messages.CompositeUser_20);
			updatepass.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					DialogUpdatePass d = new DialogUpdatePass(getShell(), user);

					if (d.open() == Dialog.OK) {
						try {
							Activator.getDefault().getVanillaApi().getVanillaSecurityManager().updateUser(d.getUserUpdate());
							if (viewU != null) {
								viewU.refresh();
							}
							if (viewG != null) {
								viewG.refresh();
							}
							if (viewR != null) {
								viewR.refresh();
							}

						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			});
		}

		Label l11 = new Label(this, SWT.NONE);
		l11.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		l11.setText(Messages.CompositeUser_21);

		groups = new ListViewer(this, SWT.BORDER | SWT.V_SCROLL);
		groups.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		groups.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Group) element).getName();
			}

		});
		groups.setContentProvider(new IStructuredContentProvider() {

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<Group> l = (List<Group>) inputElement;
				return l.toArray(new Group[l.size()]);
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});

		if (buttonBar) {
			Composite c = new Composite(this, SWT.NONE);
			c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
			c.setLayout(new GridLayout(2, true));

			ok = new Button(c, SWT.PUSH);
			ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
			ok.setText(Messages.CompositeUser_22);
			ok.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					setUser();

					try {
						if (goodUser == true) {
							if (newuser || (password != null && !password.getText().matches("[0-9a-f]{32}"))) { //$NON-NLS-1$
								String pwd = MD5Helper.encode(password.getText());
								user.setPassword(pwd);
								user.setDatePasswordModification(new Date());
							}
							user.setPasswordEncrypted(true);
							Activator.getDefault().getVanillaApi().getVanillaSecurityManager().updateUser(user);
							newuser = false;
						}

						if (viewU != null) {
							viewU.refresh();
						}
						if (viewG != null) {
							viewG.refresh();
						}
						if (viewR != null) {
							viewR.refresh();
						}

					} catch (Exception ex) {
						MessageDialog.openInformation(getShell(), Messages.CompositeUser_23, Messages.CompositeUser_24);
						ex.printStackTrace();
					}

				}

			});

			cancel = new Button(c, SWT.PUSH);
			cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
			cancel.setText(Messages.CompositeUser_25);
			cancel.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					fillData();
				}

			});
		}
	}

	private void fillWidget(String value, Text widget) {
		if (value == null) {
			widget.setText(""); //$NON-NLS-1$
		}
		else {
			widget.setText(value);
		}
	}

	private void fillData() {

		if (user == null) {
			return;
		}
		fillWidget(user.getBusinessMail(), businessMail);
		fillWidget(user.getCellular(), cellular);
		fillWidget(user.getFunction(), function);
		fillWidget(user.getName(), name);
		fillWidget(user.getSurname(), surname);
		fillWidget(user.getSkypeName(), skypeName);
		fillWidget(user.getSkypeNumber(), skypeNum);
		fillWidget(user.getPrivateMail(), privateMail);
		fillWidget(user.getTelephone(), phone);
		fillWidget(user.getLogin(), login);

		if (user.isSuperUser() == null) {
			superUser.setSelection(false);
		}
		else {
			superUser.setSelection(user.isSuperUser());
		}
		
		if (user.getNbDayPasswordValidity() > 0) {
			passwordValidity.setSelection(true);
			passwordDays.setText(String.valueOf(user.getNbDayPasswordValidity()));
			passwordDays.setEnabled(true);
		}
		else {
			passwordValidity.setSelection(false);
			passwordDays.setText("0"); //$NON-NLS-1$
		}

//		if (fmUsers != null && user.getFmUserId() != null && fmUsers.get(user.getFmUserId()) != null) {
//			fmUser.setText("" + fmUsers.get(user.getFmUserId()).getName()); //$NON-NLS-1$
//		}

		if (user.getPasswordChange() == 1) {
			passwordChangeButton.setSelection(true);
		}

		try {
			groups.setInput(Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups(user));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void setUser() {
		goodUser = true;
		if (user == null) {
			user = new User();
		}
		if (password != null && (password.getText().equals("") || (confirm != null && confirm.getText().equals("")))) { //$NON-NLS-1$ //$NON-NLS-2$
			MessageDialog.openError(getShell(), Messages.CompositeUser_30, Messages.CompositeUser_31);
			goodUser = false;
			return;
		}
		else if (confirm != null && !password.getText().equals(confirm.getText())) {
			MessageDialog.openError(getShell(), Messages.CompositeUser_32, Messages.CompositeUser_33);
			goodUser = false;
			return;
		}

		try {
			if (!user.getLogin().equals(login.getText())) {
				for (User user : Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUsers()) {
					if (user.getLogin().equals(login.getText())) {
						MessageDialog.openError(getShell(), Messages.CompositeUser_2, Messages.CompositeUser_8);
						goodUser = false;
						return;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (!(user.getId() != null && user.getId() > 0)) {
			user.setPassword(password.getText());
		}
		user.setName(name.getText());
		user.setBusinessMail(businessMail.getText());
		user.setCellular(cellular.getText());
		user.setFunction(function.getText());
		user.setPrivateMail(privateMail.getText());
		user.setSkypeName(skypeName.getText());
		user.setSkypeNumber(skypeNum.getText());
		user.setSurname(surname.getText());
		user.setTelephone(phone.getText());
		user.setSuperUser(superUser.getSelection());
		user.setLogin(login.getText());
		try {
			user.setNbDayPasswordValidity(Integer.parseInt(passwordDays.getText()));
		} catch(Exception e) {
			user.setNbDayPasswordValidity(0);
		}
		if(user.getDatePasswordModification() == null) {
			user.setDatePasswordModification(new Date());
		}

		user.setPasswordChange(passwordChangeButton.getSelection() ? 1 : 0);
	}

	public User getUser() {
		return user;
	}
}
