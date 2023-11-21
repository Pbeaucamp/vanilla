package bpm.workflow.ui.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import bpm.workflow.runtime.model.Condition;
import bpm.workflow.runtime.model.Transition;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.part.LinkEditPart;

/**
 * Not used any more (for the edition of the conditions before)
 * 
 * @author CAMUS, CHARBONNIER, MARTIN
 * 
 */
public class ConditionEditor extends Composite {

	private TreeViewer viewer;
	private Condition condition;

	private ComboBoxCellEditor fieldEditor;

	public ConditionEditor(Composite parent, int style, LinkEditPart input, Transition transition) {
		super(parent, style);

		this.setLayout(new GridLayout());

		buildViewer();
	}

	public void addSelectionListener(ISelectionChangedListener listener) {
		viewer.addSelectionChangedListener(listener);
	}

	public void removeSelectionListener(ISelectionChangedListener listener) {
		viewer.removeSelectionChangedListener(listener);
	}

	public void setCondition(Condition condition) {
		this.condition = condition;

		viewer.setInput(condition);
		refreshFields();

	}

	private void buildViewer() {

		viewer = new TreeViewer(this, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		viewer.setContentProvider(new ITreeContentProvider() {

			public Object[] getChildren(Object parentElement) {
				return null;
			}

			public Object getParent(Object element) {
				if(element instanceof Condition)
					return element;

				return null;
			}

			public boolean hasChildren(Object element) {
				return false;
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			public Object[] getElements(Object inputElement) {
				Condition c = (Condition) (inputElement);

				return new Condition[] { c };
			}

		});
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

		// viewer.setLabelProvider(new MyLabelProvider(new LabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));

		TreeViewerColumn fieldCol = new TreeViewerColumn(viewer, SWT.NONE);
		fieldCol.getColumn().setText(Messages.ConditionEditor_0);
		fieldCol.getColumn().setWidth(200);

		fieldCol.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if(element instanceof Condition) {
					// Condition c = (Condition)element;
					// return c.getVariable();
					return Messages.ConditionEditor_1;
				}

				return null;
			}

		});

		TreeViewerColumn operatorCol = new TreeViewerColumn(viewer, SWT.NONE);
		operatorCol.getColumn().setText(Messages.ConditionEditor_2);
		operatorCol.getColumn().setWidth(100);
		operatorCol.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if(element instanceof Condition) {
					// return ((Condition)element).getOperator();
					return "="; //$NON-NLS-1$
				}
				return null;
			}

		});

		TreeViewerColumn valueCol = new TreeViewerColumn(viewer, SWT.NONE);
		valueCol.getColumn().setText(Messages.ConditionEditor_4);
		valueCol.getColumn().setWidth(250);
		valueCol.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if(element instanceof Condition) {

					if(((Condition) element).getValue() != null) {
						if(((Condition) element).getValue().equalsIgnoreCase("-1")) { //$NON-NLS-1$
							return "false"; //$NON-NLS-1$
						}
						if(((Condition) element).getValue().equalsIgnoreCase("1")) { //$NON-NLS-1$
							return "true"; //$NON-NLS-1$
						}
					}
					else {
						return ((Condition) element).getValue();
					}

				}
				return null;
			}

		});

		// set the columns properties
		viewer.setColumnProperties(new String[] { "variable", "operator", "value" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		viewer.setCellModifier(new FilterCellModifier());

		viewer.getTree().setHeaderVisible(true);

		refreshFields();

		viewer.setCellEditors(new CellEditor[] { fieldEditor, new ComboBoxCellEditor(viewer.getTree(), Condition.OPERATORS), new TextCellEditor(viewer.getTree()) });

	}

	public void refreshFields() {
		List<String> l = new ArrayList<String>();
		try {
			for(Variable v : ListVariable.getInstance().getVariables()) {
				l.add(v.getName());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		if(fieldEditor == null) {
			fieldEditor = new ComboBoxCellEditor(viewer.getTree(), l.toArray(new String[l.size()]));

			viewer.setCellEditors(new CellEditor[] { fieldEditor, new ComboBoxCellEditor(viewer.getTree(), Condition.OPERATORS), new TextCellEditor(viewer.getTree()) });

		}
		else {
			fieldEditor.setItems(l.toArray(new String[l.size()]));
		}

	}

	public void refreshViewer() {
		viewer.setInput(condition);
	}

	private class FilterCellModifier implements ICellModifier {

		public boolean canModify(Object element, String property) {
			if(!(element instanceof Condition)) {
				return false;
			}

			return true;
		}

		public Object getValue(Object element, String property) {
			if(property.equals("value")) { //$NON-NLS-1$

				return ((Condition) element).getValue();

			}

			return null;
		}

		public void modify(Object element, String property, Object value) {
			if(property.equals("value")) { //$NON-NLS-1$

				((Condition) ((Tree) element).getData()).setValue((String) value);

				viewer.refresh();

			}

		}

	}

	public IStructuredSelection getSelection() {
		return (IStructuredSelection) viewer.getSelection();
	}

}
