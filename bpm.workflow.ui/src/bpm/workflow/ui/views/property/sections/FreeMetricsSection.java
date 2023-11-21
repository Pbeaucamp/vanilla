package bpm.workflow.ui.views.property.sections;

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

import bpm.freemetrics.api.manager.FactoryManager;
import bpm.freemetrics.api.manager.IManager;
import bpm.freemetrics.api.utils.Tools;
import bpm.metadata.layer.physical.sql.FactorySQLConnection;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.workflow.runtime.resources.servers.FreemetricServer;
import bpm.workflow.runtime.resources.servers.Server;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;

/**
 * Section for the additional informations concerning a freemetrics server
 * 
 * @author Charles MARTIN
 * 
 */
public class FreeMetricsSection extends AbstractPropertySection {
	private Text fmLogin;
	private Text fmPassword;
	private Server server;
	private Button validation, testServer;

	public FreeMetricsSection() {}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		CLabel lblObject = getWidgetFactory().createCLabel(composite, Messages.FreeMetricsSection_0);
		lblObject.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		fmLogin = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		fmLogin.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		CLabel lblSchema = getWidgetFactory().createCLabel(composite, Messages.FreeMetricsSection_1);
		lblSchema.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		fmPassword = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		fmPassword.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		testServer = new Button(composite, SWT.PUSH);
		testServer.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		testServer.setText(Messages.FreeMetricsSection_2);
		testServer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {

					IManager fmMgr = null;
					SQLConnection connection = FactorySQLConnection.getInstance().createConnection(((FreemetricServer) server).getJdbcDriver(), ((FreemetricServer) server).getUrl(), ((FreemetricServer) server).getPort(), ((FreemetricServer) server).getDataBaseName(), ((FreemetricServer) server).getLogin(), ((FreemetricServer) server).getPassword(), ((FreemetricServer) server).getDataBaseName());

					FactoryManager.init("", Tools.OS_TYPE_WINDOWS); //$NON-NLS-1$

					fmMgr = FactoryManager.getInstance(/* prop, "resources/freeMetricsContext.xml" */).getManager();

					fmMgr.getUserByNameAndPass(fmLogin.getText(), fmPassword.getText());

					MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FreeMetricsSection_4, Messages.FreeMetricsSection_5);

				} catch(Exception eserver) {
					MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FreeMetricsSection_6, Messages.FreeMetricsSection_7);

				}
			}

		});

		validation = new Button(composite, SWT.PUSH);
		validation.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		validation.setText(Messages.FreeMetricsSection_8);
		validation.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(!fmLogin.getText().equalsIgnoreCase("")) { //$NON-NLS-1$
					((FreemetricServer) server).setFmLogin(fmLogin.getText());
				}
				if(!fmPassword.getText().equalsIgnoreCase("")) { //$NON-NLS-1$
					((FreemetricServer) server).setFmPassword(fmPassword.getText());
				}

				MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FreeMetricsSection_3, Messages.FreeMetricsSection_12);

			}

		});
	}

	@Override
	public void refresh() {
		fmLogin.setText(((FreemetricServer) server).getFmLogin() + ""); //$NON-NLS-1$

		if(((FreemetricServer) server).getFmPassword() != null) {
			fmPassword.setText(((FreemetricServer) server).getFmPassword() + ""); //$NON-NLS-1$
		}
		else {
			fmPassword.setText(""); //$NON-NLS-1$
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

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof Server);
		this.server = (Server) input;
	}

}
