package bpm.sqldesigner.ui.wizard;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.gef.Request;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;

import bpm.gateway.core.exception.JdbcException;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.command.LoadMarkupPointCommand;
import bpm.sqldesigner.ui.command.OpenClusterCommand;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.view.TreeView;

public class OpenClusterWizard extends Wizard {

	private String host;
	private String port;
	private String login;
	private String pass;
	private String dBName;
	private String driver;
	private OpenClusterPage openClusterPage;
	private String mode;
	private String file;

	private class OpenClusterPage extends WizardPage {
		private Text clusterName;
		private Text textHost;
		private Text fileName;
		private Text textPort;
		private Text textLogin;
		private Text textPass;
		private Text textDBName;
		private Combo comboDriver;
		private Button radioLoad;
		private Text textFile;
		private Button radioExtract;
		

		public OpenClusterPage(String pageName) {
			super(pageName);
			setTitle(Messages.OpenClusterWizard_0);
			setDescription(Messages.OpenClusterWizard_1);
		}

		
		@Override
		public boolean isPageComplete() {
			if (radioExtract.getSelection()){
				if (comboDriver.getSelectionIndex() < 0 || textHost.getText().isEmpty() || fileName.getText().isEmpty() || textLogin.getText().isEmpty() || textDBName.getText().isEmpty()){
					return false;
				}
				
				if (clusterName.getText().isEmpty()){
					return false;
				}
				
				for(DatabaseCluster cluster : Activator.getDefault().getWorkspace().getOpenedClusters()){
					if (cluster.getName().equals(clusterName.getText())){
						setErrorMessage(Messages.OpenClusterWizard_2);
						return false;
					}
				}
				setErrorMessage(null);
				
				return true;
			}
			else{
				if (textFile.getText().isEmpty()){
					return false;
				}
				return true;
			}
		}
		
		public void createControl(Composite parent) {
			Composite compositeMain = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout(1, false);
			compositeMain.setLayout(layout);
			compositeMain.setLayoutData(new GridData(GridData.FILL_BOTH));

			Composite compositeExtract = new Composite(compositeMain, SWT.NONE);
			compositeExtract.setLayout(new GridLayout(1, false));
			compositeExtract.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			radioExtract = new Button(compositeExtract, SWT.RADIO);
			radioExtract.setSelection(true);
			radioExtract.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			radioExtract.setText(Messages.OpenClusterWizard_3);
			radioExtract.addSelectionListener(new SelectionListener() {

				
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				
				public void widgetSelected(SelectionEvent e) {
					radioLoad.setSelection(false);
					radioExtract.setSelection(true);
					getContainer().updateButtons();
				}

			});

			Group groupExtract = new Group(compositeExtract, SWT.NONE);
			groupExtract.setLayout(new GridLayout(3, false));
			groupExtract.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			groupExtract.setText(Messages.OpenClusterWizard_4);

			
			Label l = new Label(groupExtract, SWT.NONE);
			l.setText(Messages.OpenClusterWizard_5);
			l.setLayoutData(new GridData());
			
			clusterName = new Text(groupExtract, SWT.BORDER);
			clusterName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
			clusterName.addModifyListener(new ModifyListener() {
				
				public void modifyText(ModifyEvent e) {
					getContainer().updateButtons();
					
				}
			});
			
			
			l = new Label(groupExtract, SWT.NONE);
			l.setText(Messages.OpenClusterWizard_6);
			l.setLayoutData(new GridData());
			
			fileName = new Text(groupExtract, SWT.BORDER);
			fileName.setEnabled(false);
			fileName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			fileName.addModifyListener(new ModifyListener() {
				
				public void modifyText(ModifyEvent e) {
					getContainer().updateButtons();
					
				}
			});
			Button b = new Button(groupExtract, SWT.PUSH);
			b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
			b.setText("..."); //$NON-NLS-1$
			b.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
					fd.setFilterExtensions(new String[]{"*.mkp"}); //$NON-NLS-1$
					
					String fName = fd.open();
					
					if (fName != null){
						
						
						if ((new File(fName)).exists()){
							if (MessageDialog.openQuestion(getShell(), Messages.OpenClusterWizard_9, Messages.OpenClusterWizard_10)){
								fileName.setText(fName);
							}
						}
						else{
							fileName.setText(fName);
						}
					}
					getContainer().updateButtons();
					
				}
			});
			
			Label labelHost = new Label(groupExtract, SWT.NONE);
			labelHost.setText(Messages.OpenClusterWizard_11);
			labelHost.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

			textHost = new Text(groupExtract, SWT.BORDER);
			textHost.setText(""); //$NON-NLS-1$
			textHost.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
			textHost.addModifyListener(new ModifyListener() {
				
				public void modifyText(ModifyEvent e) {
					getContainer().updateButtons();
					
				}
			});
			Label labelPort = new Label(groupExtract, SWT.NONE);
			labelPort.setText(Messages.OpenClusterWizard_13);
			labelPort.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

			textPort = new Text(groupExtract, SWT.BORDER);
			textPort.setText(""); //$NON-NLS-1$
			textPort.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 2, 1));

			Label labelLogin = new Label(groupExtract, SWT.NONE);
			labelLogin.setText(Messages.OpenClusterWizard_15);
			labelLogin.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

			textLogin = new Text(groupExtract, SWT.BORDER);
			textLogin.setText(""); //$NON-NLS-1$
			textLogin.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 2, 1));
			textLogin.addModifyListener(new ModifyListener() {
				
				public void modifyText(ModifyEvent e) {
					getContainer().updateButtons();
					
				}
			});
			Label labelPass = new Label(groupExtract, SWT.NONE);
			labelPass.setText(Messages.OpenClusterWizard_17);
			labelPass.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

			textPass = new Text(groupExtract, SWT.BORDER | SWT.PASSWORD);
			textPass.setText(""); //$NON-NLS-1$
			textPass.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 2, 1));

			Label labelDBName = new Label(groupExtract, SWT.NONE);
			labelDBName.setText(Messages.OpenClusterWizard_19);
			labelDBName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

			textDBName = new Text(groupExtract, SWT.BORDER);
			textDBName.setText(""); //$NON-NLS-1$
			textDBName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 2, 1));
			textDBName.addModifyListener(new ModifyListener() {
				
				public void modifyText(ModifyEvent e) {
					getContainer().updateButtons();
					
				}
			});
			Label labelDriverName = new Label(groupExtract, SWT.NONE);
			labelDriverName.setText(Messages.OpenClusterWizard_21);
			labelDriverName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			
			comboDriver = new Combo(groupExtract, SWT.BORDER);
			comboDriver.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
			try {
				Collection<String> listDriver = JdbcConnectionProvider
						.getListDriver().getDriversName();
				for (String driverName : listDriver){
					if (!driverName.contains("XLS") && !driverName.toLowerCase().contains("access")){ //$NON-NLS-1$ //$NON-NLS-2$
						comboDriver.add(driverName);
					}
					
				}
					

			} catch (JdbcException e) {
				e.printStackTrace();
			}
			comboDriver.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					getContainer().updateButtons();
				}
			});
			Composite compositeLoad = new Composite(compositeMain, SWT.NONE);
			compositeLoad.setLayout(new GridLayout(1, false));
			compositeLoad.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true));

			radioLoad = new Button(compositeLoad, SWT.RADIO);
			radioLoad.setSelection(false);
			radioLoad.setText(Messages.OpenClusterWizard_24);
			radioLoad.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			radioLoad.addSelectionListener(new SelectionListener() {
			
				
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				
				public void widgetSelected(SelectionEvent e) {
					radioExtract.setSelection(false);
					radioLoad.setSelection(true);
					getContainer().updateButtons();
				}

			});
			
			

			Group groupLoad = new Group(compositeLoad, SWT.NONE);
			groupLoad.setLayout(new GridLayout(3, false));
			groupLoad.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			groupLoad.setText(Messages.OpenClusterWizard_25);

			Label labelFile = new Label(groupLoad, SWT.NONE);
			labelFile.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
			labelFile.setText(Messages.OpenClusterWizard_26);

			textFile = new Text(groupLoad, SWT.BORDER);
			textFile.setText(""); //$NON-NLS-1$
			textFile.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			textFile.addModifyListener(new ModifyListener() {
				
				public void modifyText(ModifyEvent e) {
					getContainer().updateButtons();
					
				}
			});
		

			Button fileBrowse = new Button(groupLoad, SWT.NONE);
			fileBrowse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
			fileBrowse.setImage(Activator.getDefault().getImageRegistry().get(
					"folder")); //$NON-NLS-1$
			fileBrowse.addSelectionListener(new SelectionListener() {

				
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				
				public void widgetSelected(SelectionEvent e) {
					FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
					fd.setText(Messages.OpenClusterWizard_29);
					fd.setFilterPath("C:/"); //$NON-NLS-1$
					fd.setFilterExtensions(new String[] { "*.mkp" }); //$NON-NLS-1$
					String selected = fd.open();
					textFile.setText(selected);
				}

			});

			setControl(compositeMain);
		}

		public void clearAllText() {
			textHost.setText(""); //$NON-NLS-1$
			textPort.setText(""); //$NON-NLS-1$
			textLogin.setText(""); //$NON-NLS-1$
			textPass.setText(""); //$NON-NLS-1$
			textDBName.setText(""); //$NON-NLS-1$
			comboDriver.setText(""); //$NON-NLS-1$
			textFile.setText(""); //$NON-NLS-1$
			clusterName.setText(""); //$NON-NLS-1$
		}
	}

	public OpenClusterWizard() {
		openClusterPage = new OpenClusterPage("OpenClusterPage"); //$NON-NLS-1$
		addPage(openClusterPage);
	}

	
	public boolean performFinish() {
		IViewPart v = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(TreeView.ID);

		OpenClusterPage page = (OpenClusterPage) getPage("OpenClusterPage"); //$NON-NLS-1$

		if (page.radioLoad.getSelection()) {
			mode = "Load"; //$NON-NLS-1$
			file = page.textFile.getText();
			
			
			Request renameReq = new Request("loadMarkupPoint"); //$NON-NLS-1$

			HashMap<String, Object> reqData = new HashMap<String, Object>();
			reqData.put("file", file); //$NON-NLS-1$
			reqData.put("workbenchPart", v); //$NON-NLS-1$
			renameReq.setExtendedData(reqData);

			LoadMarkupPointCommand c =  new LoadMarkupPointCommand(renameReq);
			if (c.canExecute()){
				
				c.execute();
				if (c.getErrors() != null){
					MessageDialog.openError(getShell(), Messages.OpenClusterWizard_7, c.getErrors());
					return false;
				}
			}
			
			
		} else {
			mode = "Extract"; //$NON-NLS-1$
			host = page.textHost.getText();
			port = page.textPort.getText();
			login = page.textLogin.getText();
			pass = page.textPass.getText();
			dBName = page.textDBName.getText();
			driver = page.comboDriver.getText();
			
			
			
			Request renameReq = new Request("openCluster"); //$NON-NLS-1$

			HashMap<String, Object> reqData = new HashMap<String, Object>();
			reqData.put("host", host); //$NON-NLS-1$
			reqData.put("port", port); //$NON-NLS-1$
			reqData.put("login", login); //$NON-NLS-1$
			reqData.put("pass", pass); //$NON-NLS-1$
			reqData.put("dBName", dBName); //$NON-NLS-1$
			reqData.put("driver", driver); //$NON-NLS-1$
			reqData.put("fileName", page.fileName.getText());			 //$NON-NLS-1$
			reqData.put("workbenchPart", v); //$NON-NLS-1$
			reqData.put("clusterName", page.clusterName.getText()); //$NON-NLS-1$
			renameReq.setExtendedData(reqData);

			OpenClusterCommand c =  new OpenClusterCommand(renameReq);
			if (c.canExecute()){
				c.execute();
				if (c.getErrors() != null){
					MessageDialog.openError(getShell(), Messages.OpenClusterWizard_58, c.getErrors());
					return false;
				}
			}
			
			
		}
		return true;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public String getLogin() {
		return login;
	}

	public String getPass() {
		return pass;
	}

	public String getDBName() {
		return dBName;
	}

	public String getDriver() {
		return driver;
	}

	public void clearText() {
		openClusterPage.clearAllText();
	}

	public String getMode() {
		return mode;
	}

	public String getFile() {
		return file;
	}
}