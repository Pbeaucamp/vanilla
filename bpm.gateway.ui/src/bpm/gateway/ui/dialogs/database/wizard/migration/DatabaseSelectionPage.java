package bpm.gateway.ui.dialogs.database.wizard.migration;

import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.core.Server;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.tools.database.DBConverterManager;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.ApplicationWorkbenchWindowAdvisor;
import bpm.gateway.ui.i18n.Messages;

public class DatabaseSelectionPage extends WizardPage implements IGatewayWizardPage {

	private Button sourceXls, skipFirstRow, btnXlsPath, useMigrationXml, btnTest;

	private Group sourceGroup, xlsGroup;

	private Label errorMessage, errorMessageTarget;
	private Text xlsFilePath, schemaDest;

	private ComboViewer sourceDb, targetDb;
	private Combo xlsEncoding, schemasCbo, migrationXml;

	protected DBConverterManager converterManager;

	protected DatabaseSelectionPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl(mainComposite);
		setPageComplete(false);

	}

	private void createPageContent(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		sourceXls = new Button(main, SWT.CHECK);
		sourceXls.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		sourceXls.setText(Messages.DatabaseSelectionPage_5);

		sourceGroup = new Group(main, SWT.NONE);
		sourceGroup.setLayout(new GridLayout(2, false));
		sourceGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Group targetGroup = new Group(main, SWT.NONE);
		targetGroup.setLayout(new GridLayout(2, false));
		targetGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		xlsGroup = new Group(main, SWT.NONE);
		xlsGroup.setLayout(new GridLayout(3, false));
		xlsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		sourceXls.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setCompositeEnable(sourceXls.getSelection());
			}
		});

		Label l = new Label(sourceGroup, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false));
		l.setText(Messages.DatabaseSelectionPage_0);

		sourceDb = new ComboViewer(sourceGroup, SWT.READ_ONLY);
		sourceDb.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		sourceDb.setContentProvider(new DataBaseServerListContentProvider());
		sourceDb.setLabelProvider(new ServerLabelProvider());
		sourceDb.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
			}
		});
		
		btnTest = new Button(sourceGroup, SWT.PUSH);
		btnTest.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		btnTest.setText(Messages.DatabaseSelectionPage_8);
		btnTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					if(testConnection(false)) {
						errorMessage.setForeground(null);
						errorMessage.setText(Messages.CompositeConnectionSql_10);
					}
				}catch(Exception ex){
					ex.printStackTrace();
					Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.CompositeConnectionSql_11, ex));
					MessageDialog.openError(getShell(), Messages.CompositeConnectionSql_12, ex.getMessage());
					errorMessage.setText(Messages.CompositeConnectionSql_13);
					
					ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
					errorMessage.setForeground(reg.get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY));

				}
			}
		});
		
		errorMessage = new Label(sourceGroup, SWT.NONE);
		errorMessage.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l2 = new Label(targetGroup, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false));
		l2.setText(Messages.DatabaseSelectionPage_1);

		targetDb = new ComboViewer(targetGroup, SWT.READ_ONLY);
		targetDb.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		targetDb.setContentProvider(new DataBaseServerListContentProvider());
		targetDb.setLabelProvider(new ServerLabelProvider());
		targetDb.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
			}
		});

		List<Server> serverList = ResourceManager.getInstance().getServers(DataBaseServer.class);
		targetDb.setInput(serverList);
		sourceDb.setInput(serverList);

		Label l3 = new Label(targetGroup, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.DatabaseSelectionPage_3);

		schemaDest = new Text(targetGroup, SWT.BORDER);
		schemaDest.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		
		Button btnTestTarget = new Button(targetGroup, SWT.PUSH);
		btnTestTarget.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		btnTestTarget.setText(Messages.DatabaseSelectionPage_10);
		btnTestTarget.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					if(testConnection(true)) {
						errorMessageTarget.setForeground(null);
						errorMessageTarget.setText(Messages.CompositeConnectionSql_10);
					}
				}catch(Exception ex){
					ex.printStackTrace();
					Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.CompositeConnectionSql_11, ex));
					MessageDialog.openError(getShell(), Messages.CompositeConnectionSql_12, ex.getMessage());
					errorMessageTarget.setText(Messages.CompositeConnectionSql_13);
					
					ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
					errorMessageTarget.setForeground(reg.get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY));

				}
			}
		});
		
		errorMessageTarget = new Label(targetGroup, SWT.NONE);
		errorMessageTarget.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		useMigrationXml = new Button(main, SWT.CHECK);
		useMigrationXml.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		useMigrationXml.setText(Messages.DatabaseSelectionPage_4);
		useMigrationXml.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				migrationXml.setEnabled(useMigrationXml.getSelection());
			}
		});

		migrationXml = new Combo(main, SWT.READ_ONLY);
		migrationXml.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		migrationXml.setEnabled(false);

		converterManager = new DBConverterManager("resources/conversion/"); //$NON-NLS-1$
		List<String> lst = converterManager.getConvertersNames();
		String[] a = lst.toArray(new String[lst.size()]);
		migrationXml.setItems(a);

		/*
		 * Xls
		 */
		l = new Label(xlsGroup, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DatabaseSelectionPage_6);

		xlsFilePath = new Text(xlsGroup, SWT.BORDER);
		xlsFilePath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		xlsFilePath.setEnabled(false);

		btnXlsPath = new Button(xlsGroup, SWT.PUSH);
		btnXlsPath.setText("..."); //$NON-NLS-1$
		btnXlsPath.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		btnXlsPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
				fd.setFilterExtensions(new String[] { "*.xls" }); //$NON-NLS-1$
				if (!xlsFilePath.getText().isEmpty()) {
					fd.setFilterPath(xlsFilePath.getText());
				}

				String s = fd.open();
				if (s != null) {
					xlsFilePath.setText(s);
				}
				getContainer().updateButtons();
			}
		});

		l = new Label(xlsGroup, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DatabaseSelectionPage_9);

		xlsEncoding = new Combo(xlsGroup, SWT.READ_ONLY);
		xlsEncoding.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		xlsEncoding.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getContainer().updateButtons();
			}
		});

		for (String s : Charset.availableCharsets().keySet()) {
			xlsEncoding.add(s);
		}
		xlsEncoding.setText("UTF-8"); //$NON-NLS-1$

		skipFirstRow = new Button(xlsGroup, SWT.CHECK);
		skipFirstRow.setText(Messages.DatabaseSelectionPage_7);
		skipFirstRow.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		
		setCompositeEnable(false);
	}

	private void setCompositeEnable(boolean isSourceXls) {
		sourceGroup.setEnabled(!isSourceXls);
		sourceDb.getCombo().setEnabled(!isSourceXls);
		btnTest.setEnabled(!isSourceXls);

		xlsGroup.setEnabled(isSourceXls);
		btnXlsPath.setEnabled(isSourceXls);
		xlsEncoding.setEnabled(isSourceXls);
		skipFirstRow.setEnabled(isSourceXls);
	}
	
	public boolean testConnection(boolean target) throws Exception{
		if(!target && sourceDb.getSelection() != null && !sourceDb.getSelection().isEmpty()) {
			DataBaseServer server = (DataBaseServer)((IStructuredSelection)sourceDb.getSelection()).getFirstElement();
			
			DataBaseConnection sock = (DataBaseConnection)server.getCurrentConnection(null);
			sock.connect(null);
			sock.disconnect();
			
			return true;
		}
		else if(target && targetDb.getSelection() != null && !targetDb.getSelection().isEmpty()) {
			DataBaseServer server = (DataBaseServer)((IStructuredSelection)targetDb.getSelection()).getFirstElement();
			
			DataBaseConnection sock = (DataBaseConnection)server.getCurrentConnection(null);
			sock.connect(null);
			sock.disconnect();
			
			return true;
		}
		
		return false;
	}

	private class ServerLabelProvider extends LabelProvider {

		@Override
		public String getText(Object element) {
			return ((Server) element).getName();
		}

	}

	private class DataBaseServerListContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			Collection<DataBaseServer> c = (Collection<DataBaseServer>) inputElement;
			return c.toArray(new DataBaseServer[c.size()]);
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	@Override
	public boolean isPageComplete() {
		return !(sourceDb.getSelection().isEmpty() && targetDb.getSelection().isEmpty());
	}

	public Properties getPageProperties() {
		Properties p = new Properties();

		if (sourceXls.getSelection()) {
			p.setProperty("sourceXlsFile", xlsFilePath.getText()); //$NON-NLS-1$
			p.setProperty("sourceXlsEncoding", xlsEncoding.getText()); //$NON-NLS-1$
			p.setProperty("skipFirtRow", skipFirstRow.getText()); //$NON-NLS-1$
		}
		else {
			p.setProperty("sourceServer", sourceDb.getCombo().getText()); //$NON-NLS-1$
		}

		p.setProperty("targetServer", targetDb.getCombo().getText()); //$NON-NLS-1$

		p.setProperty("targetSchema", schemaDest.getText()); //$NON-NLS-1$

		if (useMigrationXml.getSelection() && !"".equals(migrationXml.getText())) { //$NON-NLS-1$
			p.setProperty("migrationScript", migrationXml.getText()); //$NON-NLS-1$
		}

		return p;
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}
}
