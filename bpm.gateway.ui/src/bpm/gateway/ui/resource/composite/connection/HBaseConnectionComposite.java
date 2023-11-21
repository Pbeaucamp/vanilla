package bpm.gateway.ui.resource.composite.connection;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.core.server.database.nosql.hbase.HBaseConnection;
import bpm.gateway.core.server.database.nosql.hbase.HBaseHelper;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.resource.server.wizard.HBaseConnectionPage;

public class HBaseConnectionComposite extends Composite {

	private Text txtName;
	private Text txtConfigFile;
	private Button btnConfigFile;
	private Button btnTestConnection;
	
	private HBaseConnection connection;
	private HBaseConnectionPage page;
	
	public HBaseConnectionComposite(Composite parent, int style, HBaseConnectionPage page) {
		super(parent, style);
		this.page = page;
		createPageContent(parent);
		
		setBackground(parent);
	}
	
	public HBaseConnectionComposite(Composite parent, int style, HBaseConnectionPage page, HBaseConnection connection) {
		super(parent, style);
		this.page = page;
		this.connection = connection;
		createPageContent(parent);

		setBackground(parent);
	}
	
	private void setBackground(Composite parent) {

		this.setBackground(parent.getBackground());
		for(Control c : this.getChildren()){
			c.setBackground(getBackground());
		}

		Color white = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);

		txtName.setBackground(white);
		txtConfigFile.setBackground(white);
	}

	private void createPageContent(Composite parent) {
		this.setLayout(new GridLayout(3, false));
		
		Label lblName = new Label(this, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		lblName.setText(Messages.HBaseConnectionComposite_0);
		
		txtName = new Text(this, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		txtName.addListener(SWT.SELECTED, listener);
		txtName.addListener(SWT.Selection, listener);
		
		Label lblConfigFile = new Label(this, SWT.NONE);
		lblConfigFile.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		lblConfigFile.setText(Messages.HBaseConnectionComposite_1);
		
		txtConfigFile = new Text(this, SWT.BORDER);
		txtConfigFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		btnConfigFile = new Button(this, SWT.PUSH);
		btnConfigFile.setText("..."); //$NON-NLS-1$
		btnConfigFile.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		btnConfigFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dial = new FileDialog(getShell());
				String configFile = dial.open();
				try {
					configFile = configFile.replaceAll("\\\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
					txtConfigFile.setText(configFile);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				if(page != null) {
					page.updateWizardButtons();
				}
				
			}
		});
		
		btnTestConnection = new Button(this, SWT.PUSH);
		btnTestConnection.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		btnTestConnection.setText(Messages.HBaseConnectionComposite_5);
		btnTestConnection.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				connection = getConnection();
				
				try {
					if(HBaseHelper.testConnection(connection)){
						MessageDialog.openInformation(getShell(), 
								Messages.HBaseConnectionComposite_6, Messages.HBaseConnectionComposite_7);
					}
					else {
						MessageDialog.openInformation(getShell(), 
								Messages.HBaseConnectionComposite_8, Messages.HBaseConnectionComposite_9);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), 
							Messages.HBaseConnectionComposite_10, Messages.HBaseConnectionComposite_11 + e1.getMessage());
				}
			}
		});
		
		fillData();
		
	}
	
	Listener listener = new Listener(){

		public void handleEvent(Event event) {
			if (event.widget == txtConfigFile || event.widget == txtName){
				if(page != null) {
					page.updateWizardButtons();
				}
			}
		}
	};
	
	private void fillData() {
		if(connection != null) {
			if(connection.getConfigurationFileUrl() != null) {
				txtConfigFile.setText(connection.getConfigurationFileUrl());
			}
			if(connection.getName() != null) {
				txtName.setText(connection.getName());
			}
		}
	}

	public HBaseConnection getConnection() {
		if(connection == null) {
			connection = new HBaseConnection();
		}
		connection.setName(txtName.getText());
		connection.setConfigurationFileUrl(txtConfigFile.getText());
		return connection;
	}

	public void fillData(HBaseConnection dataBaseConnection) {
		connection = dataBaseConnection;
		fillData();
	}

}
