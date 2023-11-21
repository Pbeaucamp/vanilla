package bpm.entrepriseservices.externalobject.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.entrepriseservices.externalobject.Messages;
import bpm.vanilla.platform.core.repository.Parameter;

public class ParametersPage extends WizardPage {

	private TableViewer table;
	protected HashMap<Parameter, String> values = new HashMap<Parameter, String>();

	protected ParametersPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(1, false));

		table = new TableViewer(main, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.FLAT);

		table.setContentProvider(new IStructuredContentProvider() {

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<Parameter> c = (List<Parameter>) inputElement;
				return c.toArray(new Object[c.size()]);
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		table.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Parameter) element).getName();
			}
		});

		table.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		table.getTable().setHeaderVisible(true);

		TableViewerColumn reportName = new TableViewerColumn(table, SWT.NONE);
		reportName.getColumn().setText(Messages.ParametersPage_1);
		reportName.getColumn().setWidth(150);
		reportName.setLabelProvider(new ColumnLabelProvider() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang
			 * .Object)
			 */
			@Override
			public String getText(Object element) {
				return ((Parameter) element).getName();
			}
		});

		TableViewerColumn tabName = new TableViewerColumn(table, SWT.NONE);
		tabName.getColumn().setText(Messages.ParametersPage_2);
		tabName.getColumn().setWidth(150);

		tabName.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				String s = values.get((Parameter) element);
				return s;
			}
		});
		tabName.setEditingSupport(new EditingSupport(table) {

			TextCellEditor editor;

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {

				if (editor == null) {
					editor = new TextCellEditor((Composite) table.getControl());
				}

				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				String s = values.get((Parameter) element);
				return s;
			}

			@Override
			protected void setValue(Object element, Object value) {
				values.put((Parameter) element, (String) value);
				getViewer().refresh();
			}

		});
		setControl(main);
	}

	public void loadParameters(List<Parameter> parameters) {
		values.clear();
		if (parameters != null) {
			for (Parameter p : parameters) {
				values.put(p, ""); //$NON-NLS-1$
			}

			table.setInput(parameters);
		}
		else {
			table.setInput(new ArrayList<Parameter>());
		}
	}
}
