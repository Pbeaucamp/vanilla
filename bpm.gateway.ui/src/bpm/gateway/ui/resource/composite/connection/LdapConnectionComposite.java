package bpm.gateway.ui.resource.composite.connection;


import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.core.server.ldap.LdapConnection;
import bpm.gateway.ui.i18n.Messages;

public class LdapConnectionComposite extends Composite{

	private Text host, port, name, userDn, password;
	private Text base;
	
	private LdapConnection connection;
	private Button test;
	
	
	private ModifyListener listener = new ModifyListener(){

		public void modifyText(ModifyEvent e) {
			notifyListeners(SWT.Modify, new Event());
			if (!test.isDisposed()){
				test.setEnabled(isFilled());
			}
			if (connection != null){
				connection.setName(name.getText());
				connection.setBase(base.getText());
				connection.setHost(host.getText());
				connection.setPassword(password.getText());
				connection.setPort(port.getText());
				connection.setUserDn(userDn.getText());
			}
		}
		
	};
	
	
	public LdapConnectionComposite(Composite parent, int style) {
		super(parent, style);
		createContent();
		
		setBackground(parent);
	}
	
	private void setBackground(Composite parent) {

		this.setBackground(parent.getBackground());
		for(Control c : this.getChildren()){
			c.setBackground(getBackground());
		}

		Color white = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);

		name.setBackground(white);
		host.setBackground(white);
		port.setBackground(white);
		userDn.setBackground(white);
		password.setBackground(white);
		base.setBackground(white);
	}
	
	public boolean isFilled(){
		return !(base.getText().equals("") || host.getText().equals("") || port.getText().equals(""));  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
		
	private void createContent(){
		this.setLayout(new GridLayout(2, false));
		
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.LdapConnectionComposite_3);
		
		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(listener);
		
		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.LdapConnectionComposite_4);
		
		host = new Text(this, SWT.BORDER);
		host.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		host.addModifyListener(listener);
		
		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.LdapConnectionComposite_5);
		
		port = new Text(this, SWT.BORDER);
		port.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		port.addModifyListener(listener);
		
		Label l4 = new Label(this, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.LdapConnectionComposite_6);
		
		base = new Text(this, SWT.BORDER);
		base.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		base.addModifyListener(listener);
		
		Label l5 = new Label(this, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l5.setText(Messages.LdapConnectionComposite_7);
		
		userDn = new Text(this, SWT.BORDER);
		userDn.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		userDn.addModifyListener(listener);
		
		Label l6 = new Label(this, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l6.setText(Messages.LdapConnectionComposite_8);
		
		password = new Text(this, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		password.addModifyListener(listener);
		
		
		test = new Button(this, SWT.PUSH);
		test.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		test.setText(Messages.LdapConnectionComposite_9);
		test.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				
				try{
					if (connection != null){
						connection.connect(null);
						
						connection.getListNode(""); //$NON-NLS-1$
					}
					else{
						LdapConnection c = new LdapConnection();
						c.setBase(base.getText());
						c.setHost(host.getText());
						c.setPassword(password.getText());
						c.setPort(port.getText());
						c.setUserDn(userDn.getText());
						
						c.connect(null);
						c.getListNode(""); //$NON-NLS-1$
					}
					MessageDialog.openInformation(getShell(), Messages.LdapConnectionComposite_12, Messages.LdapConnectionComposite_13);
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.LdapConnectionComposite_14, Messages.LdapConnectionComposite_15 + ex.getMessage());
				}
				
			}
			
		});
	}
	
	public Properties getProperties(){
		Properties p = new Properties();
		
		p.setProperty("name", name.getText()); //$NON-NLS-1$
		p.setProperty("host", host.getText()); //$NON-NLS-1$
		p.setProperty("port", port.getText()); //$NON-NLS-1$
		p.setProperty("base", base.getText()); //$NON-NLS-1$
		p.setProperty("userDn", userDn.getText()); //$NON-NLS-1$
		p.setProperty("password", password.getText()); //$NON-NLS-1$
		
		return p;
	}


	public void fillDatas(LdapConnection connection) {
		this.connection = connection;
		name.setText(this.connection.getName());
		host.setText(this.connection.getHost());
		port.setText(this.connection.getPort());
		base.setText(this.connection.getBase());
		userDn.setText(this.connection.getUserDn());
		password.setText(this.connection.getPassword());
		
	}


	public void attachListener() {
		name.addModifyListener(listener);
		host.addModifyListener(listener);
		port.addModifyListener(listener);
		base.addModifyListener(listener);
		userDn.addModifyListener(listener);
		password.addModifyListener(listener);
		
	}


	public void releaseListener() {
		name.removeModifyListener(listener);
		host.removeModifyListener(listener);
		port.removeModifyListener(listener);
		base.removeModifyListener(listener);
		userDn.removeModifyListener(listener);
		password.removeModifyListener(listener);
		
	}

	
}
