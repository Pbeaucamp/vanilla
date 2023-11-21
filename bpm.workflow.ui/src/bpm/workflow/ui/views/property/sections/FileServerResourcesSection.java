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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.servers.Server;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;

/**
 * Section for the definition of the additional informations concerning a file server
 * 
 * @author Charles MARTIN
 * 
 */
public class FileServerResourcesSection extends AbstractPropertySection {
	private Combo txtType;
	private Server server;
	private Text foldertext, txtPort;
	private Button testServer;

	public FileServerResourcesSection() {}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		CLabel lblObject = getWidgetFactory().createCLabel(composite, Messages.FileServerResourcesSection_0);
		lblObject.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtType = new Combo(composite, SWT.READ_ONLY);
		txtType.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		CLabel lblRepertoire = getWidgetFactory().createCLabel(composite, Messages.FileServerResourcesSection_3);
		lblRepertoire.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		foldertext = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		foldertext.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		CLabel lblPort = getWidgetFactory().createCLabel(composite, Messages.FileServerResourcesSection_6);
		lblPort.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtPort = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtPort.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		testServer = new Button(composite, SWT.PUSH);
		testServer.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		testServer.setText(Messages.FileServerResourcesSection_1);
		testServer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if(((FileServer) server).getTypeServ().equalsIgnoreCase(Messages.FileServerResourcesSection_2)) {
						String url = server.getUrl();
						if(url.contains("{$VANILLA_HOME}")) { //$NON-NLS-1$
							MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FileServerResourcesSection_4, Messages.FileServerResourcesSection_5);

						}
						if(url.contains("{$VANILLA_FILES}")) { //$NON-NLS-1$

							MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FileServerResourcesSection_7, Messages.FileServerResourcesSection_8);

						}
						else {
							StringBuffer cheminbuf = new StringBuffer();
							cheminbuf.append(url);

							if(((FileServer) server).getRepertoireDef() != null) {
								cheminbuf.append(((FileServer) server).getRepertoireDef() + "\\"); //$NON-NLS-1$
							}
							String testchemin = cheminbuf.toString();
							File test = new File(testchemin);
							if(test.exists()) {
								MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FileServerResourcesSection_10, Messages.FileServerResourcesSection_11);

							}
							else {
								MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FileServerResourcesSection_12, Messages.FileServerResourcesSection_13);

							}
						}
					}
					if(((FileServer) server).getTypeServ().equalsIgnoreCase(Messages.FileServerResourcesSection_14)) {

						FTPClient ftp = new FTPClient();
						ftp.connect(server.getUrl());
						ftp.login(server.getLogin(), server.getPassword());
						ftp.setFileType(FTP.BINARY_FILE_TYPE);

						if(foldertext.getText() != null) {
							ftp.changeWorkingDirectory(foldertext.getText());
						}
						ftp.logout();
						ftp.disconnect();
						MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FileServerResourcesSection_17, Messages.FileServerResourcesSection_18);

					}
					if(((FileServer) server).getTypeServ().equalsIgnoreCase(Messages.FileServerResourcesSection_19)) {
						SMTPClient client = new SMTPClient();
						client.connect(server.getUrl(), Integer.parseInt(txtPort.getText()));
						int reply = client.getReplyCode();
						if(!SMTPReply.isPositiveCompletion(reply)) {
							client.disconnect();

							MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FileServerResourcesSection_20, Messages.FileServerResourcesSection_21);

						}
						else {
							MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FileServerResourcesSection_22, Messages.FileServerResourcesSection_23);
						}
					}
				} catch(Exception eserver) {
					MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FileServerResourcesSection_24, Messages.FileServerResourcesSection_25);

				}
			}
		});

	}

	@Override
	public void refresh() {
		txtPort.setText(((FileServer) server).getPort() + ""); //$NON-NLS-1$
		foldertext.setText(((FileServer) server).getRepertoireDef() + ""); //$NON-NLS-1$

		txtType.setItems(FileServer.SERVERS_TYPES);

		String selectS = ((FileServer) server).getTypeServ();

		int index2 = 0;
		for(String s : txtType.getItems()) {

			if(s.equalsIgnoreCase(selectS)) {
				txtType.select(index2);
				break;
			}
			index2++;
		}

	}

	@Override
	public void aboutToBeShown() {
		foldertext.addModifyListener(listener);
		txtType.addSelectionListener(adaptder);

		super.aboutToBeShown();

	}

	@Override
	public void aboutToBeHidden() {
		txtType.removeSelectionListener(adaptder);
		foldertext.removeModifyListener(listener);
		super.aboutToBeHidden();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof Server);
		this.server = (Server) input;
	}

	SelectionAdapter adaptder = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {

			if(txtType.getText() != null) {
				((FileServer) server).setTypeServ(txtType.getText());
			}

		}

	};

	private ModifyListener listener = new ModifyListener() {

		public void modifyText(ModifyEvent evt) {
			if(evt.widget == foldertext) {
				((FileServer) server).setRepertoireDef(foldertext.getText());
				try {
					((FileServer) ((WorkflowModel) Activator.getDefault().getCurrentModel()).getResource(((FileServer) server).getName())).setRepertoireDef(foldertext.getText());
				} catch(Exception e) {}
			}

		}
	};

}
