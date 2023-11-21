package bpm.workflow.ui.views.property.sections;

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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.resources.servers.Server;
import bpm.workflow.runtime.resources.servers.ServerMail;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;

/**
 * Section for the definition of the port (Mail server)
 * 
 * @author CAMUS,CHARBONNIER, MARTIN
 * 
 */
public class PortResourcesSection extends AbstractPropertySection {
	private Text txtPort;
	private Server server;
	private Button validation, testServer;

	public PortResourcesSection() {}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		CLabel lblObject = getWidgetFactory().createCLabel(composite, Messages.PortResourcesSection_8);
		lblObject.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtPort = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtPort.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		testServer = new Button(composite, SWT.PUSH);
		testServer.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		testServer.setText(Messages.PortResourcesSection_0);
		testServer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if(server instanceof ServerMail) {
						SMTPClient client = new SMTPClient();
						client.connect(server.getUrl(), Integer.parseInt(txtPort.getText()));
						int reply = client.getReplyCode();
						if(!SMTPReply.isPositiveCompletion(reply)) {
							client.disconnect();

							MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.PortResourcesSection_1, Messages.PortResourcesSection_2);

						}
						else {
							MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.PortResourcesSection_3, Messages.PortResourcesSection_4);
						}
					}
				} catch(Exception eserver) {
					MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.PortResourcesSection_5, Messages.PortResourcesSection_6);

				}
			}

		});

		validation = new Button(composite, SWT.PUSH);
		validation.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		validation.setText(Messages.PortResourcesSection_7);
		validation.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(server instanceof ServerMail) {
					if(!txtPort.getText().equalsIgnoreCase("")) { //$NON-NLS-1$
						((ServerMail) server).setPort(txtPort.getText());

					}
				}

				MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.PortResourcesSection_9, Messages.PortResourcesSection_10);

			}

		});
	}

	@Override
	public void refresh() {
		txtPort.setText(((ServerMail) server).getPort() + ""); //$NON-NLS-1$
	}

	@Override
	public void aboutToBeShown() {

		super.aboutToBeShown();

	}

	@Override
	public void aboutToBeHidden() {

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

}
