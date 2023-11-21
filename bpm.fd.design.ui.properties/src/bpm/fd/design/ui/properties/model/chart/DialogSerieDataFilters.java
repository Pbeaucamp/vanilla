package bpm.fd.design.ui.properties.model.chart;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.IChartData;
import bpm.fd.api.core.model.datas.filters.ValueFilter;
import bpm.fd.api.core.model.datas.filters.ValueFilter.Type;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.icons.Icons;
import bpm.fd.design.ui.properties.i18n.Messages;

public class DialogSerieDataFilters extends Dialog{
	private static final Color RED = new Color(Display.getDefault(), 255,0,0);
	private IChartData chartData;
	private DataAggregation agg;

	private TableViewer filtersViewer;
	protected DialogSerieDataFilters(Shell parentShell, DataAggregation agg, IChartData chartData) {
		super(parentShell);
		this.chartData = chartData;
		this.agg = agg;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Composite c = new Composite(main, SWT.NONE);
		c.setLayout(new GridLayout(2, false));		
		c.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		
		ToolBar toolbar = new ToolBar(c, SWT.VERTICAL);
		toolbar.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		
		final ToolItem add = new ToolItem(toolbar, SWT.PUSH);
		add.setToolTipText(Messages.DialogSerieDataFilters_0);
		add.setImage(Activator.getDefault().getImageRegistry().get(Icons.add));
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				ValueFilter v = new ValueFilter();
				agg.addFilter(v);
				filtersViewer.setInput(agg.getFilter());
				filtersViewer.setSelection(new StructuredSelection(v));
			}
		});
		
		
		final ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.DialogSerieDataFilters_1);
		del.setImage(Activator.getDefault().getImageRegistry().get(Icons.delete));
		del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ValueFilter v = (ValueFilter)((IStructuredSelection)filtersViewer.getSelection()).getFirstElement();
				agg.removeFilter(v);
				filtersViewer.setInput(agg.getFilter());

			}
		});
		
		
				
		filtersViewer = new TableViewer(c, SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		filtersViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		filtersViewer.getTable().setHeaderVisible(true);
		filtersViewer.getTable().setLinesVisible(true);
		filtersViewer.setContentProvider(new ArrayContentProvider());
		
		TableViewerColumn col = new TableViewerColumn(filtersViewer, SWT.NONE);
		col.getColumn().setText(Messages.DialogSerieDataFilters_2);
		col.getColumn().setWidth(150);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((ValueFilter)element).getType().getType();
			}
		});
		col.setEditingSupport(new EditingSupport(filtersViewer) {
			
			ComboBoxCellEditor ed ;
			@Override
			protected void setValue(Object element, Object value) {
				if ((Integer)value < 0){
					return;
				}
				((ValueFilter)element).setType(Type.values()[(Integer)value]);
				filtersViewer.refresh();
				Activator.getDefault().getProject().getDictionary().firePropertyChange(Dictionary.PROPERTY_COMPONENT_CHANGED, null, chartData);
			}
			
			@Override
			protected Object getValue(Object element) {
				return ((ValueFilter)element).getType().ordinal();
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				
				if (ed == null){
					String[] items = new String[Type.values().length];
					for(int i = 0; i < items.length; i++){
						items[i] = Type.values()[i].getType();
					}
					ed = new ComboBoxCellEditor(filtersViewer.getTable(), items);
				}
				return ed;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		
	
		col = new TableViewerColumn(filtersViewer, SWT.NONE);
		col.getColumn().setText(Messages.DialogSerieDataFilters_3);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((ValueFilter)element).getValue() + ""; //$NON-NLS-1$
			}
		});
		col.setEditingSupport(new EditingSupport(filtersViewer) {
			TextCellEditor ed =  new TextCellEditor(filtersViewer.getTable(), SWT.NONE);
			@Override
			protected void setValue(Object element, Object value) {
				try{
					((ValueFilter)element).setValue(Double.valueOf((String)value));
					ed.getControl().setBackground(null);
					filtersViewer.refresh();
					Activator.getDefault().getProject().getDictionary().firePropertyChange(Dictionary.PROPERTY_COMPONENT_CHANGED, null, chartData);
				}catch(Exception ex){
					ed.getControl().setBackground(RED);
				}
			}
			
			@Override
			protected Object getValue(Object element) {
				return ((ValueFilter)element).getValue() + ""; //$NON-NLS-1$
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return ed;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		return main;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 300);
		filtersViewer.setInput(agg.getFilter());
	}
}
