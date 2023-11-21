package bpm.workflow.ui.views.property.sections;

import java.io.File;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.freemetrics.api.manager.FactoryManager;
import bpm.freemetrics.api.manager.IManager;
import bpm.freemetrics.api.utils.Tools;
import bpm.metadata.layer.physical.sql.FactorySQLConnection;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.workflow.runtime.resources.servers.DataBaseServer;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.servers.FreemetricServer;
import bpm.workflow.runtime.resources.servers.Server;
import bpm.workflow.runtime.resources.servers.ServerMail;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.views.ResourceViewPart;

/**
 * Section for the general definition of a server
 * 
 * @author MARTIN
 * 
 */
public class GeneralResourcesSection extends AbstractPropertySection {
	private Text txtName;
	private Text txtUrl;
	private Text txtPort;
	private Text txtDatabase;
	private Text txtUser;
	private Text txtPwd;
	private Button validation, testServer;

	private Server server;
	
	private CLabel lblDb, lblPort;

	public GeneralResourcesSection() {
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		CLabel lblName = getWidgetFactory().createCLabel(composite, Messages.GeneralResourcesSection_1);
		lblName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtName = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		CLabel lblUrl = getWidgetFactory().createCLabel(composite, Messages.GeneralResourcesSection_7);
		lblUrl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtUrl = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		lblPort = getWidgetFactory().createCLabel(composite, Messages.GeneralResourcesSection_36);
		lblPort.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtPort = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtPort.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		lblDb = getWidgetFactory().createCLabel(composite, Messages.GeneralResourcesSection_35);
		lblDb.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtDatabase = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtDatabase.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		CLabel lblUser = getWidgetFactory().createCLabel(composite, Messages.GeneralResourcesSection_10);
		lblUser.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtUser = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtUser.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		CLabel lblPwd = getWidgetFactory().createCLabel(composite, Messages.GeneralResourcesSection_13);
		lblPwd.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtPwd = new Text(composite, SWT.BORDER | SWT.PASSWORD); //$NON-NLS-1$
		txtPwd.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		testServer = new Button(composite, SWT.PUSH);
		testServer.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		testServer.setText(Messages.GeneralResourcesSection_0);
		testServer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {

					if (server instanceof FreemetricServer) {
						IManager fmMgr = null;
						SQLConnection connection = FactorySQLConnection.getInstance().createConnection(((FreemetricServer) server).getJdbcDriver(), txtUrl.getText(), ((FreemetricServer) server).getPort(), ((FreemetricServer) server).getDataBaseName(), txtUser.getText(), txtPwd.getText(), ((FreemetricServer) server).getDataBaseName());

						FactoryManager.init("", Tools.OS_TYPE_WINDOWS); //$NON-NLS-1$

						fmMgr = FactoryManager.getInstance(/* prop, "resources/freeMetricsContext.xml" */).getManager();

						fmMgr.getUserByNameAndPass(((FreemetricServer) server).getFmLogin(), ((FreemetricServer) server).getFmPassword());

						MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.GeneralResourcesSection_2, Messages.GeneralResourcesSection_3);
					}

					if (server instanceof DataBaseServer) {
						SQLConnection connection = FactorySQLConnection.getInstance().createConnection(((DataBaseServer) server).getJdbcDriver(), txtUrl.getText(),txtPort.getText(), txtDatabase.getText(), txtUser.getText(), txtPwd.getText(), ((DataBaseServer) server).getSchemaName());
						connection.test();
						MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.GeneralResourcesSection_4, Messages.GeneralResourcesSection_5);
					}
					if (server instanceof FileServer) {
						if (((FileServer) server).getTypeServ().equalsIgnoreCase(Messages.GeneralResourcesSection_6)) {
							String url = server.getUrl();
							if (url.contains("{$VANILLA_HOME}")) { //$NON-NLS-1$
								MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.GeneralResourcesSection_8, Messages.GeneralResourcesSection_9);

							}
							if (url.contains("{$VANILLA_FILES}")) { //$NON-NLS-1$
								MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.GeneralResourcesSection_11, Messages.GeneralResourcesSection_12);

							}
							else {
								StringBuffer cheminbuf = new StringBuffer();
								cheminbuf.append(url);

								if (((FileServer) server).getRepertoireDef() != null) {
									cheminbuf.append(((FileServer) server).getRepertoireDef() + "\\"); //$NON-NLS-1$
								}
								String testchemin = cheminbuf.toString();

								File test = new File(testchemin);
								if (test.exists()) {
									MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.GeneralResourcesSection_14, Messages.GeneralResourcesSection_15);
								}

								else {
									MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.GeneralResourcesSection_16, Messages.GeneralResourcesSection_17);

								}
							}

						}
						if (((FileServer) server).getTypeServ().equalsIgnoreCase(Messages.GeneralResourcesSection_18)) {

							FTPClient ftp = new FTPClient();
							ftp.connect(txtUrl.getText());
							ftp.login(txtUser.getText(), txtPwd.getText());
							ftp.setFileType(FTP.BINARY_FILE_TYPE);
							if (((FileServer) server).getRepertoireDef() != null) {
								ftp.changeWorkingDirectory(((FileServer) server).getRepertoireDef());
							}
							ftp.logout();
							ftp.disconnect();
							MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.GeneralResourcesSection_21, Messages.GeneralResourcesSection_22);

						}
						if (((FileServer) server).getTypeServ().equalsIgnoreCase(Messages.GeneralResourcesSection_23)) {
							SMTPClient client = new SMTPClient();
							client.connect(txtUrl.getText(), Integer.parseInt(((FileServer) server).getPort()));
							int reply = client.getReplyCode();
							if (!SMTPReply.isPositiveCompletion(reply)) {
								client.disconnect();

								MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.GeneralResourcesSection_24, Messages.GeneralResourcesSection_25);

							}
							else {
								MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.GeneralResourcesSection_26, Messages.GeneralResourcesSection_27);
							}
						}
					}
					if (server instanceof ServerMail) {
						SMTPClient client = new SMTPClient();
						client.connect(server.getUrl(), ((ServerMail) server).getPort());
						int reply = client.getReplyCode();
						if (!SMTPReply.isPositiveCompletion(reply)) {
							client.disconnect();

							MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.GeneralResourcesSection_28, Messages.GeneralResourcesSection_29);

						}
						else {
							MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.GeneralResourcesSection_30, Messages.GeneralResourcesSection_31);
						}
					}

				} catch (Exception eserver) {
					MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.GeneralResourcesSection_32, Messages.GeneralResourcesSection_33);

				}
			}

		});

		validation = new Button(composite, SWT.PUSH);
		validation.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		validation.setText(Messages.GeneralResourcesSection_34);
		validation.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (!txtName.getText().equalsIgnoreCase("")) { //$NON-NLS-1$
					server.setName(txtName.getText());
				}
				if (!txtUser.getText().equalsIgnoreCase("")) { //$NON-NLS-1$
					server.setLogin(txtUser.getText());
				}
				if (!txtPwd.getText().equalsIgnoreCase("")) { //$NON-NLS-1$
					server.setPassword(txtPwd.getText());
				}
				if (!txtUrl.getText().equalsIgnoreCase("")) { //$NON-NLS-1$
					server.setUrl(txtUrl.getText());
				}
				if (server instanceof DataBaseServer) {
					((DataBaseServer) server).setDataBaseName(txtDatabase.getText());
					((DataBaseServer) server).setPort(txtPort.getText());
				}

				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				ResourceViewPart v = (ResourceViewPart) page.findView(ResourceViewPart.ID);
				if (v != null) {
					v.refresh();
				}

				MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.GeneralResourcesSection_39, Messages.GeneralResourcesSection_40);

			}

		});
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof Server);
		this.server = (Server) input;
	}

	@Override
	public void refresh() {

		if (server.getLogin() != null)
			txtUser.setText(server.getLogin());
		else
			txtUser.setText(""); //$NON-NLS-1$

		if (server.getPassword() != null)
			txtPwd.setText(server.getPassword());
		else
			txtPwd.setText(""); //$NON-NLS-1$

		if (server.getUrl() != null)
			txtUrl.setText(server.getUrl());
		else
			txtUrl.setText(""); //$NON-NLS-1$
		
		if (server instanceof DataBaseServer) {
			txtDatabase.setText(((DataBaseServer) server).getDataBaseName());
			txtPort.setText(((DataBaseServer) server).getPort());
		}

		txtName.setText(server.getName());
		
		if (!(server instanceof DataBaseServer)) {
			lblPort.setVisible(false);
			txtPort.setVisible(false);
			lblDb.setVisible(false);
			txtDatabase.setVisible(false);
		}
		else {
			lblPort.setVisible(true);
			txtPort.setVisible(true);
			lblDb.setVisible(true);
			txtDatabase.setVisible(true);
		}
	}

	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();
	}

	@Override
	public void aboutToBeHidden() {
		super.aboutToBeHidden();
	}

}
