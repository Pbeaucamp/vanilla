package bpm.birep.admin.client.dialog;

import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
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
import bpm.vanilla.workplace.core.IUser;
import bpm.vanilla.workplace.core.model.PlaceUser;
import bpm.vanilla.workplace.remote.impl.RemotePlaceService;

public class DialogWorkplaceConnection extends Dialog {

	private Composite content;
	private Label lblMail, lblConfirmation, lblError;
	private Text userName, password, confirmPassword, txtMail;
	private Button btnNewUser, connect;
	
	private IUser user;
	
	private RemotePlaceService workplaceService;
	
	private boolean isNewUser = false;
	
	public DialogWorkplaceConnection(Shell parentShell, RemotePlaceService workplaceService) {
		super(parentShell);
		this.workplaceService = workplaceService;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(350, 240);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText(Messages.DialogAddRepUser_0);
		
		content = new Composite(parent, SWT.NONE);
		content.setLayoutData(new GridData(GridData.FILL_BOTH));
		content.setLayout(new GridLayout(2, false));

		Label lblUsername = new Label(content, SWT.NONE);
		lblUsername.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblUsername.setText(Messages.DialogRepository_1);
		
		userName = new Text(content, SWT.BORDER);
		userName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		userName.setText(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BPM_VANILLA_PLACE_LOGIN));
		
		Label lblPass = new Label(content, SWT.NONE);
		lblPass.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblPass.setText(Messages.DialogWorkplaceConnectin_6);
		
		password = new Text(content, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		password.setText(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BPM_VANILLA_PLACE_PASSWORD));
		
		lblConfirmation = new Label(content, SWT.NONE);
		lblConfirmation.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblConfirmation.setText(Messages.DialogWorkplaceConnectin_7);
		lblConfirmation.setVisible(false);
//		((GridData) lblConfirmation.getLayoutData()).exclude = true;
		
		confirmPassword = new Text(content, SWT.BORDER | SWT.PASSWORD);
		confirmPassword.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		confirmPassword.setVisible(false);
//		((GridData) confirmPassword.getLayoutData()).exclude = true;
		
		lblMail = new Label(content, SWT.NONE);
		lblMail.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblMail.setText(Messages.DialogWorkplaceConnectin_4);
		lblMail.setVisible(false);
//		((GridData) lblMail.getLayoutData()).exclude = true;
		
		txtMail = new Text(content, SWT.BORDER);
		txtMail.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtMail.setVisible(false);
//		((GridData) txtMail.getLayoutData()).exclude = true;
		
		btnNewUser = new Button(content, SWT.PUSH);
		btnNewUser.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		btnNewUser.setText(Messages.DialogWorkplaceConnectin_2);
		btnNewUser.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				changeDialog(false);
			}
		});

	    Color red = getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
	    
		lblError = new Label(content, SWT.BOLD);
		lblError.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		lblError.setForeground(red);
		
		return content;		
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.DialogWorkplaceConnectin_1, true);
		connect = createButton(parent, IDialogConstants.OK_ID, Messages.DialogWorkplaceConnectin_5,	true);
	}
	
	@Override
	protected void cancelPressed() {
		if(isNewUser){
			changeDialog(true);
			return;
		}
		super.cancelPressed();
	}
	
	@Override
	protected void okPressed() {
		String login = userName.getText();
		String pass = password.getText();
		String confirmPass = confirmPassword.getText();
		String mail = txtMail.getText();
		
		if(!login.isEmpty() && !pass.isEmpty()){
			if(!isNewUser){
				try {
					user = workplaceService.authentifyUser(login, pass);
				} catch (Exception e1) {
					e1.printStackTrace();
					lblError.setText(Messages.DialogWorkplaceConnectin_11);
					return;
				}
				
				if(user == null){
					lblError.setText(Messages.DialogWorkplaceConnectin_11);
					return;
				}
			}
			else if(!pass.equals(confirmPass)){
				lblError.setText(Messages.DialogWorkplaceConnectin_9);
				return;
			}
			else if(mail.isEmpty()){
				lblError.setText(Messages.DialogWorkplaceConnectin_10);
				return;
			}
			else {
				IUser user = new PlaceUser(login, pass, mail, false, new Date());
				boolean success = false;
				try {
					success = workplaceService.createUserAndSendMail(user);
				} catch (Exception e1) {
					e1.printStackTrace();
					lblError.setText(Messages.DialogWorkplaceConnectin_8);
					return;
				}
				
				if(success){
					changeDialog(true);
					return;
				}
				else {
					lblError.setText(Messages.DialogWorkplaceConnectin_8);
					return;
				}
			}
		}
		else {
			lblError.setText(Messages.DialogWorkplaceConnectin_8);
			return;
		}
		super.okPressed();
	}
	
	public IUser getUser(){
		return user;
	}
	
	private void changeDialog(boolean connectMode) {
		lblError.setText(""); //$NON-NLS-1$
		if(connectMode){
			isNewUser = false;
			
			connect.setText(Messages.DialogWorkplaceConnectin_5);
			btnNewUser.setVisible(true);
			lblConfirmation.setVisible(false);
			confirmPassword.setVisible(false);
			lblMail.setVisible(false);
			txtMail.setVisible(false);
		}
		else {
			isNewUser = true;
			
			connect.setText(Messages.DialogWorkplaceConnectin_3);
			btnNewUser.setVisible(false);
			lblConfirmation.setVisible(true);
			confirmPassword.setVisible(true);
			lblMail.setVisible(true);
			txtMail.setVisible(true);
		}
	}
	
	
}
