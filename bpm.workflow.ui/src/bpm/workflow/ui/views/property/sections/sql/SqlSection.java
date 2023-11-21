package bpm.workflow.ui.views.property.sections.sql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.metadata.layer.physical.sql.FactorySQLConnection;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.workflow.runtime.databases.DataBaseHelper;
import bpm.workflow.runtime.databases.StreamElement;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.WorkfowModelParameter;
import bpm.workflow.runtime.model.activities.SqlActivity;
import bpm.workflow.runtime.resources.servers.DataBaseServer;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.runtime.resources.variables.VariablesHelper;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.dialogs.DialogBrowseContent;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the definition of the SQL activities
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class SqlSection extends AbstractPropertySection {
	private Text txtQuery;
	private Node node;
	private Button testQuery, browseDatas, apply;

	public SqlSection() {

	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new FillLayout(SWT.VERTICAL));

		Composite c = getWidgetFactory().createFlatFormComposite(composite);
		c.setLayout(new GridLayout(2, false));

		CLabel labelLabel = getWidgetFactory().createCLabel(c, Messages.SqlSection_11);
		labelLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true, 1, 2));

		txtQuery = getWidgetFactory().createText(c, "", SWT.MULTI | SWT.WRAP | SWT.V_SCROLL); //$NON-NLS-1$
		txtQuery.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 2));
		;

		Composite buttonBar = getWidgetFactory().createComposite(composite);
		buttonBar.setLayout(new GridLayout(3, true));

		apply = getWidgetFactory().createButton(buttonBar, Messages.SqlSection_0, SWT.PUSH);
		apply.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true));

		testQuery = getWidgetFactory().createButton(buttonBar, Messages.SqlSection_1, SWT.PUSH);
		testQuery.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true));

		testQuery.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					DataBaseServer server = (DataBaseServer) ((SqlActivity) node.getWorkflowObject()).getServer();
					String query = ((SqlActivity) node.getWorkflowObject()).getQuery();
					if (query == null || query.equals("") || !query.equals(txtQuery.getText())) { //$NON-NLS-1$
						setQuery();
					}
					query = VariablesHelper.parseString(query);

					WorkflowModel model = (WorkflowModel) Activator.getDefault().getCurrentModel();
					List<WorkfowModelParameter> parameters = model.getParameters();
					if (parameters != null) {
						for (WorkfowModelParameter param : parameters) {
							query = query.replace("{$" + param.getName() + "}", param.getDefaultValue()); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}

					boolean test = DataBaseHelper.testConnection(server, query);

					if (!test) {
						MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.SqlSection_2, Messages.SqlSection_3);
					}
					else {
						MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.SqlSection_4, Messages.SqlSection_5);

					}

				} catch (Exception et) {
					et.printStackTrace();
					MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.SqlSection_6, Messages.SqlSection_7 + et.getMessage());

				}

			}

		});

		browseDatas = getWidgetFactory().createButton(buttonBar, Messages.SqlSection_8, SWT.PUSH);
		browseDatas.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true));

		browseDatas.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					DataBaseServer server = (DataBaseServer) ((SqlActivity) node.getWorkflowObject()).getServer();
					String jdbcDriver = server.getJdbcDriver();
					String url = server.getUrl();
					String port = server.getPort();
					String dataBaseName = server.getDataBaseName();
					String login = server.getLogin();
					String password = server.getPassword();
					String schemaName = server.getSchemaName();

					SQLConnection connection;

					connection = FactorySQLConnection.getInstance().createConnection(jdbcDriver, url, port, dataBaseName, login, password, schemaName);
					String query = ((SqlActivity) node.getWorkflowObject()).getQuery();
					if (query == null || query.equals("") || !query.equals(txtQuery.getText())) { //$NON-NLS-1$
						setQuery();
					}
					query = VariablesHelper.parseString(query);

					List<List<String>> res = connection.executeQuery(query, null, null);

					if (res.size() != 0) {
						List<StreamElement> elements = DataBaseHelper.getDescriptor(server, ((SqlActivity) node.getWorkflowObject()).getQuery());
						List<String> colnames = new ArrayList<String>();
						for (StreamElement _e : elements) {
							colnames.add(_e.name);
						}
						DialogBrowseContent dialogBrowse = new DialogBrowseContent(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), res, colnames);
						dialogBrowse.open();
					}
					else {
						MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.SqlSection_12, Messages.SqlSection_13);
					}

				} catch (Exception et) {
					et.printStackTrace();
					MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.SqlSection_9, Messages.SqlSection_10 + et.getMessage());

				}

			}

		});

	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
	}

	@Override
	public void refresh() {
		SqlActivity a = (SqlActivity) node.getWorkflowObject();

		if (a.getQuery() != null) {
			txtQuery.setText(a.getQuery());
		}
		else {
			txtQuery.setText(""); //$NON-NLS-1$
		}

		new ContentProposalAdapter(txtQuery, new TextContentAdapter(), new SimpleContentProposalProvider(ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel())), Activator.getDefault().getKeyStroke(), Activator.getDefault().getAutoActivationCharacters());

	}

	@Override
	public void aboutToBeShown() {
		apply.addSelectionListener(selection);
		super.aboutToBeShown();

	}

	@Override
	public void aboutToBeHidden() {
		apply.removeSelectionListener(selection);

		super.aboutToBeHidden();
	}

	SelectionListener selection = new SelectionListener() {

		public void widgetDefaultSelected(SelectionEvent e) {

		}

		public void widgetSelected(SelectionEvent e) {
			setQuery();
		}

	};

	private void setQuery() {
		((SqlActivity) node.getWorkflowObject()).setQuery(txtQuery.getText());
		if (((SqlActivity) node.getWorkflowObject()).getMapping() == null) {
			((SqlActivity) node.getWorkflowObject()).setMapping(new LinkedHashMap<String, Variable>());
		}
		else {
			try {
				((SqlActivity) node.getWorkflowObject()).updateMapping();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
