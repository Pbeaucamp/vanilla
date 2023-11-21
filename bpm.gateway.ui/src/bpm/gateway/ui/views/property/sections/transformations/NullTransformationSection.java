package bpm.gateway.ui.views.property.sections.transformations;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.NullTransformation;
import bpm.gateway.core.transformations.utils.ConditionNull;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.composites.StreamComposite;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;

public class NullTransformationSection extends AbstractPropertySection {

	private ISelectionChangedListener selectionListener;
	private TableViewer tableviewer;
	private Table table;
	protected StreamComposite streamComposite;
	private ToolItem add, del;
	private Node node;
	private String[] col = new String[0];
	private ArrayList<ConditionNull> cd = new ArrayList<ConditionNull>();
	private NullTransformation transfo;

	private ComboBoxCellEditor combo;
	private TextCellEditor txt;

	public NullTransformationSection() {
		selectionListener = new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				if (tableviewer.getSelection() instanceof Transformation) {
					add.setEnabled(true);
					del.setEnabled(false);
				}
				else if (tableviewer.getSelection() instanceof ConditionNull) {
					add.setEnabled(false);
					del.setEnabled(true);
				}
			}
		};
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout());

		ToolBar toolbar = new ToolBar(composite, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		tableviewer = new TableViewer(composite);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableviewer, SWT.BORDER);

		TableColumn tblclmn = tableViewerColumn.getColumn();
		tblclmn.setWidth(400);
		tblclmn.setText(Messages.NullTransformationSection_0);

		TableViewerColumn tableViewerValue = new TableViewerColumn(tableviewer, SWT.NONE);

		TableColumn tblvalue = tableViewerValue.getColumn();
		tblvalue.setWidth(400);
		tblvalue.setText(Messages.NullTransformationSection_1);

		table = tableviewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);

		combo = new ComboBoxCellEditor(table, col);

		EditingSupport edit = new EditingSupport(tableviewer) {

			@Override
			protected void setValue(Object element, Object value) {
				Integer val = (Integer) value;
				ConditionNull cond = (ConditionNull) element;
				cond.setStreamElementNumber(String.valueOf(value));
				cond.setStreamElementName(combo.getItems()[val]);
				tableviewer.refresh();
			}

			@Override
			protected Object getValue(Object element) {
				ConditionNull cond = (ConditionNull) element;
				int i = 0;
				for (String ste : combo.getItems()) {
					if (cond.getStreamElementName().equals(ste)) {
						return i;
					}
					i++;
				}
				return 0;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return combo;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		};
		txt = new TextCellEditor(table);

		EditingSupport txtEdit = new EditingSupport(tableviewer) {

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return txt;
			}

			@Override
			protected Object getValue(Object element) {
				return ((ConditionNull) element).getValue();
			}

			@Override
			protected void setValue(Object element, Object value) {
				ConditionNull cond = (ConditionNull) element;

				cond.setValue(String.valueOf(value));
				tableviewer.refresh();
			}
		};

		tableViewerColumn.setEditingSupport(edit);
		tableViewerValue.setEditingSupport(txtEdit);

		tableviewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

			public void dispose() {}

			public Object[] getElements(Object inputElement) {
				Collection c = (Collection) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});

		tableviewer.setLabelProvider(new ITableLabelProvider() {

			@Override
			public void removeListener(ILabelProviderListener listener) { }

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void dispose() { }

			@Override
			public void addListener(ILabelProviderListener listener) { }

			@Override
			public String getColumnText(Object element, int columnIndex) {
				ConditionNull cond = (ConditionNull) element;
				switch (columnIndex) {
				case 0:
					return cond.getStreamElementName();

				case 1:
					return cond.getValue();

				default:
					break;
				}
				return null;
			}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
		});

		tableviewer.addSelectionChangedListener(selectionListener);

		add = new ToolItem(toolbar, SWT.PUSH);
		add.setToolTipText(Messages.FilterSection_0);
		add.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.add_16));
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ConditionNull cdr = new ConditionNull();
				cdr.setValue(Messages.NullTransformationSection_2);
				cd.add(cdr);

				transfo.addCondition(cdr);

				tableviewer.setInput(transfo.getConditions());
			}

		});

		del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.FilterSection_1);
		del.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.del_16));
		del.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection jde = (StructuredSelection) tableviewer.getSelection();
				ConditionNull cdToRemove = (ConditionNull) jde.getFirstElement();
				cd.remove(cdToRemove);
				transfo.removeCondition(cdToRemove);
				tableviewer.refresh();
			}

		});

	}

	public ArrayList<ConditionNull> getCd() {
		return cd;
	}

	public void setCd(ArrayList<ConditionNull> cd) {
		this.cd = cd;
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);

		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
		transfo = (NullTransformation) node.getGatewayModel();

		ArrayList<StreamElement> l = new ArrayList<StreamElement>();
		for (Transformation t : transfo.getInputs()) {
			try {
				l.addAll(t.getDescriptor(t).getStreamElements());
			} catch (ServerException e) {
				e.printStackTrace();
			}
		}
		col = new String[l.size()];
		int i = 0;
		for (StreamElement st : l) {
			col[i] = st.getFullName().replace(":", ""); //$NON-NLS-1$ //$NON-NLS-2$

			i++;
		}
		combo.setItems(col);
		tableviewer.setInput(transfo.getConditions());
	}

	@Override
	public void refresh() {
		super.refresh();
		tableviewer.setInput(transfo.getConditions());
	}
}
