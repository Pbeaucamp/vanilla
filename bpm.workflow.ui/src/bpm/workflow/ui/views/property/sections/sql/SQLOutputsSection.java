package bpm.workflow.ui.views.property.sections.sql;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

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
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

public class SQLOutputsSection extends AbstractPropertySection {
	private Node node;
	private TableViewer tableViewer;
	private List<StreamElement> fields;

	public SQLOutputsSection() {}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new FillLayout(SWT.VERTICAL));

		Composite c = getWidgetFactory().createFlatFormComposite(composite);
		c.setLayout(new GridLayout(1, false));

		tableViewer = new TableViewer(c, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		tableViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 2));
		tableViewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<StreamElement> l = (List<StreamElement>) inputElement;
				return l.toArray(new StreamElement[l.size()]);
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});
		tableViewer.setLabelProvider(new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch(columnIndex) {
					case 0:
						return ((StreamElement) element).tableName;
					case 1:
						return ((StreamElement) element).name;

					case 2:
						return ((StreamElement) element).typeName;

					case 3:
						String sqlfield = ((StreamElement) element).name;
						SqlActivity act = (SqlActivity) node.getWorkflowObject();
						Variable var = act.getVariableFillByField(sqlfield);
						if(var == null) {
							return ""; //$NON-NLS-1$

						}
						else
							return var.getName();

				}
				return ""; //$NON-NLS-1$
			}

			public void addListener(ILabelProviderListener listener) {

			}

			public void dispose() {

			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {

			}

		});

		TableColumn table = new TableColumn(tableViewer.getTable(), SWT.NONE);
		table.setText(Messages.SQLOutputsSection_2);
		table.setWidth(150);

		TableColumn column = new TableColumn(tableViewer.getTable(), SWT.NONE);
		column.setText(Messages.SQLOutputsSection_3);
		column.setWidth(150);

		TableColumn type = new TableColumn(tableViewer.getTable(), SWT.NONE);
		type.setText(Messages.SQLOutputsSection_4);
		type.setWidth(150);

		TableColumn variable = new TableColumn(tableViewer.getTable(), SWT.NONE);
		variable.setText(Messages.SQLOutputsSection_5);
		variable.setWidth(200);

		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		tableViewer.getTable().setHeaderVisible(true);
		// tableViewer.getTable().setLinesVisible(true);

		tableViewer.setColumnProperties(new String[] { "table", "name", "type", "variable" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		tableViewer.setCellModifier(new VariableCellModifier());

		CellEditor[] edi = new CellEditor[] { null, null, null, new ComboBoxCellEditor(tableViewer.getTable(), ListVariable.getInstance().getNoEnvironementVariable()) };

		tableViewer.setCellEditors(edi);

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

		try {
			String query = prepareQuery(a.getQuery());
			
			fields = DataBaseHelper.getDescriptor((DataBaseServer) a.getServer(), query);
			tableViewer.setInput(fields);
			tableViewer.getCellEditors()[3] = new ComboBoxCellEditor(tableViewer.getTable(), ListVariable.getInstance().getNoEnvironementVariable());
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	private String prepareQuery(String query) {
		query = VariablesHelper.parseString(query);

		WorkflowModel model = (WorkflowModel) Activator.getDefault().getCurrentModel();
		List<WorkfowModelParameter> parameters = model.getParameters();
		if (parameters != null) {
			for (WorkfowModelParameter param : parameters) {
				query = query.replace("{$" + param.getName() + "}", param.getDefaultValue()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return query;
	}

	@Override
	public void aboutToBeShown() {

		super.aboutToBeShown();

	}

	@Override
	public void aboutToBeHidden() {

		super.aboutToBeHidden();
	}

	private class VariableCellModifier implements ICellModifier {

		public boolean canModify(Object element, String property) {
			if(property.equals("variable")) { //$NON-NLS-1$
				return true;
			}

			return false;
		}

		public Object getValue(Object element, String property) {
			if(property.equals("name")) { //$NON-NLS-1$
				return ((StreamElement) element).name;
			}
			else if(property.equals("table")) { //$NON-NLS-1$
				return ((StreamElement) element).tableName;
			}
			else if(property.equals("type")) { //$NON-NLS-1$
				return ((StreamElement) element).type;
			}
			else if(property.equals("variable")) { //$NON-NLS-1$
				String sqlfield = ((StreamElement) element).name;
				SqlActivity act = (SqlActivity) node.getWorkflowObject();
				Variable var = act.getVariableFillByField(sqlfield);
				Integer index = new Integer(-1);
				for(int i = 0; i < ListVariable.getInstance().getNoEnvironementVariable().length; i++) {
					String name = ListVariable.getInstance().getNoEnvironementVariable()[i];
					if(name.equals(var.getName())) {
						index = new Integer(i);
						break;
					}

				}
				return index;
			}
			return null;
		}

		public void modify(Object element, String property, Object value) {
			if(property.equals("variable") && (Integer) value != -1) { //$NON-NLS-1$
				String sqlfield = ((StreamElement) ((TableItem) element).getData()).name;
				SqlActivity act = (SqlActivity) node.getWorkflowObject();
				String variablename = ListVariable.getInstance().getNoEnvironementVariable()[(Integer) value];
				((WorkflowModel) Activator.getDefault().getCurrentModel()).addResource(ListVariable.getInstance().getVariable(variablename));
				act.linkFieldToVariable(sqlfield, variablename);
				tableViewer.refresh();
			}
			else if(property.equals("variable")) { //$NON-NLS-1$
				String sqlfield = ((StreamElement) ((TableItem) element).getData()).name;
				SqlActivity act = (SqlActivity) node.getWorkflowObject();
				act.deleteLink(sqlfield);
				tableViewer.refresh();
			}
		}
	}

}
