package bpm.workflow.ui.views.property.sections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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
import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.workflow.runtime.resources.servers.DataBaseServer;
import bpm.workflow.runtime.resources.servers.FreemetricServer;
import bpm.workflow.runtime.resources.servers.Server;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;

/**
 * Section for the additional definitions of a database server
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class DBSection extends AbstractPropertySection {
	private Text txtPort;
	private Text txtSchema, txtBase;
	private Server server;
	private Combo driver;
	private Button testServer, validation;

	public DBSection() {}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		CLabel lblObject = getWidgetFactory().createCLabel(composite, Messages.DBSection_0);
		lblObject.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtPort = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtPort.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		CLabel l2 = getWidgetFactory().createCLabel(composite, Messages.DBSection_1);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.DBSection_2);

		driver = new Combo(composite, SWT.READ_ONLY);
		driver.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		CLabel lblBase = getWidgetFactory().createCLabel(composite, Messages.DBSection_3);
		lblBase.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtBase = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtBase.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		CLabel lblSchema = getWidgetFactory().createCLabel(composite, Messages.DBSection_4);
		lblSchema.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtSchema = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		txtSchema.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		testServer = new Button(composite, SWT.PUSH);
		testServer.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		testServer.setText(Messages.DBSection_5);
		testServer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					SQLConnection connection;

					connection = FactorySQLConnection.getInstance().createConnection(((DataBaseServer) server).getJdbcDriver(), ((DataBaseServer) server).getUrl(), txtPort.getText(), txtBase.getText(), ((DataBaseServer) server).getLogin(), ((DataBaseServer) server).getPassword(), txtSchema.getText());
					if(server instanceof FreemetricServer) {

						IManager fmMgr = null;

						FactoryManager.init("", Tools.OS_TYPE_WINDOWS); //$NON-NLS-1$

						fmMgr = FactoryManager.getInstance().getManager();

						fmMgr.getUserByNameAndPass(((FreemetricServer) server).getFmLogin(), ((FreemetricServer) server).getPassword());

						MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.DBSection_7, Messages.DBSection_8);

					}
					else {
						connection.test();
						MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.DBSection_9, Messages.DBSection_10);

					}

				} catch(Exception eserver) {
					MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.DBSection_11, Messages.DBSection_12);

				}
			}

		});

		validation = new Button(composite, SWT.PUSH);
		validation.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		validation.setText(Messages.DBSection_13);
		validation.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(!txtPort.getText().equalsIgnoreCase("")) { //$NON-NLS-1$
					((DataBaseServer) server).setPort(txtPort.getText());
				}
				if(!txtSchema.getText().equalsIgnoreCase("")) { //$NON-NLS-1$
					((DataBaseServer) server).setSchemaName(txtSchema.getText());
				}
				if(!txtBase.getText().equalsIgnoreCase("")) { //$NON-NLS-1$
					((DataBaseServer) server).setDataBaseName(txtBase.getText());
				}
				MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.DBSection_17, Messages.DBSection_18);

			}

		});

	}

	@Override
	public void refresh() {
		txtPort.setText(((DataBaseServer) server).getPort() + ""); //$NON-NLS-1$

		if(((DataBaseServer) server).getSchemaName() != null) {
			txtSchema.setText(((DataBaseServer) server).getSchemaName());
		}
		else {
			txtSchema.setText(""); //$NON-NLS-1$
		}
		if(((DataBaseServer) server).getDataBaseName() != null) {
			txtBase.setText(((DataBaseServer) server).getDataBaseName());
		}
		else {
			txtBase.setText(""); //$NON-NLS-1$
		}

		try {
			driver.setItems(ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversName().toArray(new String[ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversName().size()]));
		} catch(Exception e) {
			e.printStackTrace();
		}
		int index = 0;
		for(String s : driver.getItems()) {
			if(s.equalsIgnoreCase(((DataBaseServer) server).getJdbcDriver())) {
				driver.select(index);
				break;
			}
			index++;
		}
	}

	@Override
	public void aboutToBeShown() {

		if(!driver.isListening(SWT.Selection))
			driver.addSelectionListener(selection);

		super.aboutToBeShown();

	}

	@Override
	public void aboutToBeHidden() {
		driver.removeSelectionListener(selection);
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

	private SelectionListener selection = new SelectionListener() {

		public void widgetDefaultSelected(SelectionEvent e) {

		}

		public void widgetSelected(SelectionEvent e) {
			((DataBaseServer) server).setJdbcDriver(driver.getText());

		}

	};

}
