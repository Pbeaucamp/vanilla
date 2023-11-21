package bpm.gateway.ui.composites;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.AggregateTransformation;
import bpm.gateway.core.transformations.utils.Aggregate;
import bpm.gateway.ui.i18n.Messages;

public class AggregateComposite extends Composite {

	private static final String notForGroup = Messages.AggregateComposite_0;
	private static final String[] AGGS_FN = new String[Aggregate.FUNCTIONS.length + 1];

	static {
		AGGS_FN[0] = "--- None ---"; //$NON-NLS-1$
		for(int i = 0; i < Aggregate.FUNCTIONS.length; i++) {
			AGGS_FN[i + 1] = Aggregate.FUNCTIONS[i];
		}
	}
	private CheckboxTableViewer viewer;
	private AggregateTransformation transfo;

	private TableViewer order;

	private Integer dragedElement;

	public AggregateComposite(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		buildViewer();
	}

	public void setInputs(AggregateTransformation transfo) {
		this.transfo = transfo;

		if(transfo.getInputs().size() == 0) {
			return;
		}

		try {
			
			//TODO LOAD DESCRIPTOR CORRECTLY
			List<StreamElement> columns = transfo.getInputs().get(0).getDescriptor(transfo).getStreamElements();
			viewer.setInput(columns);

			for(int i = 0; i < columns.size(); i++) {
				StreamElement e = columns.get(i);
				viewer.setChecked(e, transfo.isGroupBy(e));
			}
			// Object o = viewer.getCheckedElements();
			order.setInput(transfo.getGroupBy());
			viewer.refresh();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	private void buildViewer() {

		viewer = CheckboxTableViewer.newCheckList(this, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		viewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<StreamElement> l = (List<StreamElement>) (inputElement);

				return l.toArray(new StreamElement[l.size()]);

			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

		});
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setLabelProvider(new ITableLabelProvider() {

			public void addListener(ILabelProviderListener listener) {}

			public void dispose() {}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {}

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch(columnIndex) {
					case 0:
						// transfo.isGroupBy((StreamElement)element);
						return ""; //$NON-NLS-1$
					case 1:
						return ((StreamElement) element).name;
					case 2:
						Integer k = transfo.isFunctionCode((StreamElement) element);

						if(transfo.isGroupBy((StreamElement) element)) {
							return notForGroup;
						}

						if(k != null && !k.equals(-1)) {
							return Aggregate.FUNCTIONS[k];
						}
						return AGGS_FN[0];
				}
				return ""; //$NON-NLS-1$
			}

		});

		viewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				StreamElement element = (StreamElement) event.getElement();

				if(event.getChecked()) {
					transfo.addGroupBy(element);
				}
				else {
					transfo.removeGroupBy(element);
				}
				order.setInput(transfo.getGroupBy());
				viewer.refresh();
			}

		});

		TableColumn groupCol = new TableColumn(viewer.getTable(), SWT.NONE);
		groupCol.setText(Messages.AggregateComposite_3);
		groupCol.setWidth(100);

		TableColumn fieldCol = new TableColumn(viewer.getTable(), SWT.NONE);
		fieldCol.setText(Messages.AggregateComposite_4);
		fieldCol.setWidth(400);

		TableColumn operatorCol = new TableColumn(viewer.getTable(), SWT.NONE);
		operatorCol.setText(Messages.AggregateComposite_5);
		operatorCol.setWidth(300);

		// set the columns properties
		viewer.setColumnProperties(new String[] { "group", "field", Messages.AggregateComposite_8 }); //$NON-NLS-1$ //$NON-NLS-2$
		viewer.setCellModifier(new AggregateCellModifier());

		viewer.getTable().setHeaderVisible(true);

		viewer.setCellEditors(new CellEditor[] { null, new TextCellEditor(viewer.getTable()), new ComboBoxCellEditor(viewer.getTable(), AGGS_FN) });

		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		l.setText(Messages.AggregateComposite_9);

		order = new TableViewer(this, SWT.V_SCROLL | SWT.BORDER);
		order.getTable().setToolTipText(Messages.AggregateComposite_10);
		order.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		order.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				try {
					return transfo.getInputs().get(0).getDescriptor(transfo).getStreamElements().get((Integer) element).name;
				} catch(Exception e) {
					e.printStackTrace();
					return null;
				}

			}

		});

		order.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<Integer> l = (List<Integer>) (inputElement);

				return l.toArray(new Integer[l.size()]);
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});

		setDnd();
	}

	private class AggregateCellModifier implements ICellModifier {

		public boolean canModify(Object element, String property) {

			if(property.equals("group")) { //$NON-NLS-1$
				return true;
			}
			if(property.equals("function")) { //$NON-NLS-1$
				return !transfo.isGroupBy((StreamElement) element);
			}
			return false;
		}

		public Object getValue(Object element, String property) {
			if(property.equals("field")) { //$NON-NLS-1$
				return ((StreamElement) element).name;
			}
			else if(property.equals(Messages.AggregateComposite_14)) {

				Integer a = transfo.isFunctionCode((StreamElement) element);

				if(a == null) {
					return 0;
				}
				return a + 1;

			}
			else if(property.equals("group")) { //$NON-NLS-1$
				return transfo.isFunctionCode((StreamElement) element);
			}

			return null;
		}

		public void modify(Object element, String property, Object value) {
			if(property.equals("function")) { //$NON-NLS-1$
				Integer v = (Integer) value;
				if(v != null && v.intValue() == 0) {
					for(Aggregate a : transfo.getAggregates()) {
						try {
							if(a.getStreamElementName().equals("::" + ((StreamElement) ((TableItem) element).getData()).name)) { //$NON-NLS-1$
								transfo.removeAggregate(a);
								break;
							}
						} catch(Exception ex) {

						}

					}
				}
				else {
					transfo.addAggregate((StreamElement) ((TableItem) element).getData(), ((Integer) value) - 1);
				}

				viewer.refresh();
			}

		}

	}

	public IStructuredSelection getSelection() {
		return (IStructuredSelection) viewer.getSelection();
	}

	private void setDnd() {
		int ops = DND.DROP_COPY;
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };

		order.addDragSupport(ops, transfers, new DragSourceListener() {

			public void dragFinished(DragSourceEvent event) {}

			public void dragSetData(DragSourceEvent event) {
				IStructuredSelection ss = (IStructuredSelection) order.getSelection();

				if(ss.getFirstElement() instanceof Integer) {
					dragedElement = (Integer) ss.getFirstElement();
					event.data = "TSF"; //$NON-NLS-1$
				}

			}

			public void dragStart(DragSourceEvent event) {}

		});

		order.addDropSupport(ops, transfers, new DropTargetListener() {

			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;

			}

			public void dragLeave(DropTargetEvent event) {}

			public void dragOperationChanged(DropTargetEvent event) {}

			public void dragOver(DropTargetEvent event) {}

			public void drop(DropTargetEvent event) {
				Object o = ((TableItem) event.item).getData();
				// cancel
				if(!(o instanceof Integer)) {
					dragedElement = null;
					return;
				}

				transfo.swapGroupBy(dragedElement, (Integer) o);
				order.setInput(transfo.getGroupBy());
				dragedElement = null;

			}

			public void dropAccept(DropTargetEvent event) {}

		});
	}

}
