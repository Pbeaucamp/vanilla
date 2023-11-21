package bpm.gateway.ui.views.property.sections.transformations;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.calcul.GeoFilter;
import bpm.gateway.core.transformations.calcul.GeoFilter.Polygon;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;

public class GeoFilterManualPolygonSection extends AbstractPropertySection {
	
	private Node node;
	private NodePart nodePart;
	
	private TableViewer table;
	private TableViewerColumn colName;
	private TableViewerColumn colPoints;

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
		this.nodePart = (NodePart) input;

	}
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout());
		
		Label lblColumn = getWidgetFactory().createLabel(composite, Messages.GeoFilterManualPolygonSection_0, SWT.NONE);
		lblColumn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		ToolBar toolbar = new ToolBar(composite, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

		ToolItem add = new ToolItem(toolbar, SWT.PUSH);
		add.setToolTipText(Messages.FilterSection_0);
		add.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.add_16));
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				((GeoFilter) node.getGatewayModel()).addPolygon(new Polygon());
				table.setInput(((GeoFilter) node.getGatewayModel()).getPolygons());
				table.refresh();
			}
		});
		// add.setEnabled(false);

		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.FilterSection_1);
		del.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.del_16));
		del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) table.getSelection();
				for(Object o : ss.toList()) {
					if(o instanceof Polygon) {
						((GeoFilter) node.getGatewayModel()).getPolygons().remove(o);
					}
				}
				table.setInput(((GeoFilter) node.getGatewayModel()).getPolygons());
				table.refresh();
			}
		});
		// del.setEnabled(false);

		table = new TableViewer(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		table.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		table.setContentProvider(new ArrayContentProvider());
		table.getTable().setHeaderVisible(true);
		table.getTable().setLinesVisible(true);
		
		colName = new TableViewerColumn(table, SWT.NONE);
		colName.getColumn().setWidth(150);
		colName.getColumn().setText(Messages.GeoFilterManualPolygonSection_1);
		colName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Polygon) element).getName();
			}
		});
		colName.setEditingSupport(new EditingSupport(table) {
			TextCellEditor editor = new TextCellEditor(table.getTable());
			@Override
			protected void setValue(Object element, Object value) {
				try {
					((Polygon)element).setName(value.toString());
				} catch(Exception e) {
				}
				table.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				return ((Polygon)element).getName();
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

		colPoints = new TableViewerColumn(table, SWT.NONE);
		colPoints.getColumn().setWidth(150);
		colPoints.getColumn().setText(Messages.GeoFilterManualPolygonSection_2);
		colPoints.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Polygon)element).getPointsAsString();
			}
		});
		colPoints.setEditingSupport(new EditingSupport(table) {
			TextCellEditor editor = new TextCellEditor(table.getTable());
			@Override
			protected void setValue(Object element, Object value) {
				try {
					((Polygon)element).setPointsFromString(value.toString());
				} catch(Exception e) {
				}
				table.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				return ((Polygon)element).getPointsAsString();
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
	}
	
	@Override
	public void refresh() {
		table.setInput(((GeoFilter) node.getGatewayModel()).getPolygons());
		table.refresh();
		super.refresh();
	}

}
