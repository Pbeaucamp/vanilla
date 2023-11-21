package bpm.workflow.ui.views.property.sections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.ExcelAggregateActivity;
import bpm.workflow.runtime.model.activities.reporting.ReportActivity;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the Aggregate Excel activity
 * 
 * @author Charles MARTIN
 * 
 */
public class ExcelAggregateSection extends AbstractPropertySection {
	public static Color mainBrown = new Color(Display.getDefault(), 209, 177, 129);
	public static Color secondBrown = new Color(Display.getDefault(), 238, 226, 208);

	private Text select;

	private Node node;

	private Button b;

	private TableViewer table;

	private Button validPath;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());

		org.eclipse.swt.widgets.Group general = new org.eclipse.swt.widgets.Group(parent, SWT.NONE);
		general.setLayout(new GridLayout(4, false));
		general.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		general.setText(Messages.ExcelAggregateSection_0);

		CLabel labelcomP = getWidgetFactory().createCLabel(general, Messages.ExcelAggregateSection_1);
		labelcomP.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		select = getWidgetFactory().createText(general, ""); //$NON-NLS-1$
		select.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		b = new Button(general, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("..."); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
				fd.setFilterExtensions(new String[] { "*.xls", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$

				String path = fd.open();

				if(fd != null) {
					select.setText(path);
				}
			}

		});

		validPath = new Button(general, SWT.PUSH);
		validPath.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		validPath.setText(Messages.ExcelAggregateSection_5);

		validPath.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				((ExcelAggregateActivity) node.getWorkflowObject()).setPath(select.getText());
				if(select.getText().contains("{$")) { //$NON-NLS-1$
					String finalstring = new String(select.getText());

					List<String> varsString = new ArrayList<String>();
					for(Variable variable : ListVariable.getInstance().getVariables()) {
						varsString.add(variable.getName());
					}
					for(String nomvar : varsString) {
						String toto = finalstring.replace("{$" + nomvar + "}", ListVariable.getInstance().getVariable(nomvar).getValues().get(0)); //$NON-NLS-1$ //$NON-NLS-2$
						if(!toto.equalsIgnoreCase(finalstring)) {
							((WorkflowModel) Activator.getDefault().getCurrentModel()).addResource(ListVariable.getInstance().getVariable(nomvar));

						}

						finalstring = toto;
					}

				}
			}

		});

		org.eclipse.swt.widgets.Group tabLinks = new org.eclipse.swt.widgets.Group(parent, SWT.NONE);
		tabLinks.setLayout(new GridLayout(2, false));
		tabLinks.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 4, 1));
		tabLinks.setText(Messages.ExcelAggregateSection_9);

		createTable(tabLinks);

	}

	@Override
	public void refresh() {

		if(((ExcelAggregateActivity) node.getWorkflowObject()).getPath() != null) {

			select.setText(((ExcelAggregateActivity) node.getWorkflowObject()).getPath());
		}

		for(IActivity act : ((ExcelAggregateActivity) node.getWorkflowObject()).getSources()) {
			if(act instanceof ReportActivity) {
				if(((ReportActivity) act).getOutputFormats().contains(2)) {
					boolean toAdd = true;
					for(String report : ((ExcelAggregateActivity) node.getWorkflowObject()).getNameTabs().keySet()) {

						if(report.equalsIgnoreCase(((ReportActivity) act).getOutputName())) {
							toAdd = false;

						}
					}
					if(toAdd) {
						((ExcelAggregateActivity) node.getWorkflowObject()).addMapping(((ReportActivity) act).getOutputName(), ((ReportActivity) act).getOutputName());

					}

				}
			}
		}
		fillTable();

		String[] listeVar = new String[ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel()).length - 2];
		int i = 0;
		for(String string : ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel())) {
			if(!string.equalsIgnoreCase("{$VANILLA_FILES}") && !string.equalsIgnoreCase("{$VANILLA_HOME}")) { //$NON-NLS-1$ //$NON-NLS-2$
				listeVar[i] = string;
				i++;
			}

		}
		new ContentProposalAdapter(select, new TextContentAdapter(), new SimpleContentProposalProvider(listeVar), Activator.getDefault().getKeyStroke(), Activator.getDefault().getAutoActivationCharacters());

	}

	private void fillTable() {

		table.setInput(((ExcelAggregateActivity) node.getWorkflowObject()).getNameTabs().keySet());
		table.refresh();
	}

	private void createTable(Composite composite) {
		table = new TableViewer(getWidgetFactory().createTable(composite, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.FLAT));

		table.setContentProvider(new IStructuredContentProvider() {

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
			 */
			public Object[] getElements(Object inputElement) {
				Set<String> c = (Set<String>) inputElement;
				return c.toArray(new Object[c.size()]);
			}

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
			 */
			public void dispose() {

			}

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});

		table.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		table.getTable().setHeaderVisible(true);

		TableViewerColumn reportName = new TableViewerColumn(table, SWT.NONE);
		reportName.getColumn().setText(Messages.ExcelAggregateSection_12);
		reportName.getColumn().setWidth(150);
		reportName.setLabelProvider(new ColumnLabelProvider() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				return (String) element;
			}
		});

		reportName.setEditingSupport(new EditingSupport(table) {

			TextCellEditor editor;

			@Override
			protected boolean canEdit(Object element) {
				return false;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {

				if(editor == null) {
					editor = new TextCellEditor((Composite) table.getControl());

				}

				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				return (String) element;
			}

			@Override
			protected void setValue(Object element, Object value) {
				element = (String) value;

				getViewer().refresh();
			}

		});

		TableViewerColumn tabName = new TableViewerColumn(table, SWT.NONE);
		tabName.getColumn().setText(Messages.ExcelAggregateSection_13);
		tabName.getColumn().setWidth(150);

		tabName.setLabelProvider(new ColumnLabelProvider() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {

				return (String) ((ExcelAggregateActivity) node.getWorkflowObject()).getNameTabs().get((String) element);
			}
		});
		tabName.setEditingSupport(new EditingSupport(table) {

			TextCellEditor editor;
			ContentProposalAdapter adapter;

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {

				if(editor == null) {
					editor = new TextCellEditor((Composite) table.getControl());
					adapter = new ContentProposalAdapter(editor.getControl(), new TextContentAdapter(), new SimpleContentProposalProvider(new String[] {}), Activator.getDefault().getKeyStroke(), Activator.getDefault().getAutoActivationCharacters());

				}

				List<String> vars = new ArrayList<String>();

				for(String variable : ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel())) {
					if(variable.equalsIgnoreCase("{$VANILLA_FILES}") || variable.equalsIgnoreCase("{$VANILLA_HOME}")) { //$NON-NLS-1$ //$NON-NLS-2$

					}
					else {
						vars.add("{$" + variable + "}"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}

				adapter.setContentProposalProvider(new SimpleContentProposalProvider(vars.toArray(new String[vars.size()])));

				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				return (String) ((ExcelAggregateActivity) node.getWorkflowObject()).getNameTabs().get((String) element);
			}

			@Override
			protected void setValue(Object element, Object value) {
				((ExcelAggregateActivity) node.getWorkflowObject()).getNameTabs().remove((String) element);
				((ExcelAggregateActivity) node.getWorkflowObject()).getNameTabs().put((String) element, (String) value);

				getViewer().refresh();
			}

		});

	}

	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();

	}

	public void aboutToBeHidden() {
		super.aboutToBeHidden();
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();

	}

	SelectionListener selection = new SelectionListener() {

		public void widgetDefaultSelected(SelectionEvent e) {

		}

		public void widgetSelected(SelectionEvent e) {

		}

	};

}
