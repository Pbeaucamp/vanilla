package metadata.client.wizards.datasources;

import java.util.Collection;

import metadata.client.i18n.Messages;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.DataSource;

public class DialogDataSourcePicker extends Dialog {

	private TableViewer viewer;

	private IRepositoryApi sock;
	private SQLConnection sqlConnection;

	public DialogDataSourcePicker(Shell parentShell, IRepositoryApi sock) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.sock = sock;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		viewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

			public void dispose() {}

			public Object[] getElements(Object inputElement) {
				Collection c = (Collection) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				getButton(IDialogConstants.OK_ID).setEnabled(!viewer.getSelection().isEmpty());

			}
		});

		TableViewerColumn col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.DialogDataSourcePicker_0);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((DataSource) element).getName();
			}
		});

		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.DialogDataSourcePicker_1);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((DataSource) element).getDatasourcePublicProperties().getProperty(DataSource.DATASOURCE_JDBC_URL);
			}
		});

		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.DialogDataSourcePicker_2);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((DataSource) element).getDatasourcePublicProperties().getProperty(DataSource.DATASOURCE_JDBC_DRIVER);
			}
		});

		return viewer.getControl();
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogDataSourcePicker_3);
		getShell().setSize(600, 400);

		if(sock == null) {
			MessageDialog.openInformation(getShell(), Messages.DialogDataSourcePicker_4, Messages.DialogDataSourcePicker_5);
		}
		try {
			viewer.setInput(sock.getImpactDetectionService().getDatasourcesByType(DataSource.DATASOURCE_JDBC));
		} catch(Exception ex) {
			ex.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogDataSourcePicker_6, Messages.DialogDataSourcePicker_7 + ex.getMessage());
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setText(Messages.DialogDataSourcePicker_8);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	@Override
	protected void okPressed() {
		DataSource ds = (DataSource) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
		try {
			createConnection(ds);
		} catch(Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogDataSourcePicker_9, Messages.DialogDataSourcePicker_10 + e.getMessage());
			return;
		}
		super.okPressed();
	}

	public SQLConnection getSQLConnection() {
		return sqlConnection;
	}

	private void createConnection(DataSource ds) throws Exception {
		sqlConnection = new SQLConnection();
		
		sqlConnection.setUsername(ds.getDatasourcePublicProperties().getProperty(DataSource.DATASOURCE_JDBC_USER));
		sqlConnection.setPassword(ds.getDatasourcePublicProperties().getProperty(DataSource.DATASOURCE_JDBC_PASS));

		for(DriverInfo i : ListDriver.getInstance(bpm.studio.jdbc.management.config.IConstants.getJdbcDriverXmlFile()).getDriversInfo()) {
			if(ds.getDatasourcePublicProperties().getProperty(DataSource.DATASOURCE_JDBC_URL).startsWith(i.getUrlPrefix())) {
				String url = ds.getDatasourcePublicProperties().getProperty(DataSource.DATASOURCE_JDBC_URL).substring(i.getUrlPrefix().length());

				String dbName = ""; //$NON-NLS-1$
				if(url.contains("/")) { //$NON-NLS-1$
					dbName = url.substring(url.lastIndexOf("/") + 1); //$NON-NLS-1$
				}
				String port = ""; //$NON-NLS-1$
				String host = ""; //$NON-NLS-1$

				if(url.contains(":")) { //$NON-NLS-1$
					port = url.substring(url.indexOf(":") + 1, url.indexOf("/")); //$NON-NLS-1$ //$NON-NLS-2$
					host = url.substring(0, url.indexOf(":")); //$NON-NLS-1$
				}
				else {
					host = url.substring(0, url.lastIndexOf("/")); //$NON-NLS-1$
				}
				sqlConnection.setName(ds.getName());
				sqlConnection.setPortNumber(port);
				sqlConnection.setHost(host);
				sqlConnection.setDataBaseName(dbName);

			}
			if(ds.getDatasourcePublicProperties().getProperty(DataSource.DATASOURCE_JDBC_DRIVER).equals(i.getClassName())) {
				sqlConnection.setDriverName(i.getName());
			}
		}

	}
}
