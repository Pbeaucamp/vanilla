package bpm.fd.design.ui.component.chart;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.datas.ColumnDescriptor;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.viewer.contentproviders.ListContentProvider;



public class MultiSeriesTableViewer extends TableViewer{
	public static final String AGGREGATION_ADDED = "bpm.fd.design.ui.component.chart.MultiSeriesTableViewer.added"; //$NON-NLS-1$
	public static final String AGGREGATION_REMOVED = "bpm.fd.design.ui.component.chart.MultiSeriesTableViewer.removed"; //$NON-NLS-1$
	public static final String AGGREGATION_CHANEGD = "bpm.fd.design.ui.component.chart.MultiSeriesTableViewer.changed"; //$NON-NLS-1$
	
	private DataSetEditSupport aggregationEdition;
	private DataSet dataSet;
	private PropertyChangeSupport actionsListeners = new PropertyChangeSupport(this);
	
	
	public MultiSeriesTableViewer(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	
	public void addActionListener(PropertyChangeListener listener){
		actionsListeners.addPropertyChangeListener(listener);
		
	}
	
	public void removeActionListener(PropertyChangeListener listener){
		actionsListeners.removePropertyChangeListener(listener);
	}


	private void createColumns(){
		setContentProvider(new ListContentProvider<DataAggregation>());
		
		TableViewerColumn colFunction = new TableViewerColumn(this, SWT.NONE);
		colFunction.getColumn().setText(Messages.MultiSeriesTableViewer_0);
		colFunction.getColumn().setWidth(200);
		colFunction.setLabelProvider(new ColumnLabelProvider(){
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				return DataAggregation.AGGREGATORS_NAME[((DataAggregation)element).getAggregator()];
			}
		});
		colFunction.setEditingSupport(new EditingSupport(this){
			ComboBoxCellEditor editor = new ComboBoxCellEditor((Composite)getControl(), DataAggregation.AGGREGATORS_NAME, SWT.READ_ONLY);
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				return ((DataAggregation)element).getAggregator();
			}

			@Override
			protected void setValue(Object element, Object value) {
				((DataAggregation)element).setAggregator((Integer)value);
				actionsListeners.firePropertyChange(AGGREGATION_CHANEGD,null, value);
				refresh();
			}
			
		});
		
		
		
		TableViewerColumn colValue = new TableViewerColumn(this, SWT.NONE);
		colValue.getColumn().setText(Messages.MultiSeriesTableViewer_4);
		colValue.getColumn().setWidth(200);
		colValue.setLabelProvider(new ColumnLabelProvider(){
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				try{
					return dataSet.getDataSetDescriptor().getColumnsDescriptors().get(((DataAggregation)element).getValueFieldIndex() -1).getName();
				}catch(Exception e){
					return ""; //$NON-NLS-1$
				}
				
			}
		});
		aggregationEdition = new DataSetEditSupport(this);
		colValue.setEditingSupport(aggregationEdition);
		
		
		TableViewerColumn colName = new TableViewerColumn(this, SWT.NONE);
		colName.getColumn().setText(Messages.MultiSeriesTableViewer_6);
		colName.getColumn().setWidth(200);
		colName.setLabelProvider(new ColumnLabelProvider(){
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				return ((DataAggregation)element).getMeasureName();
			}
		});
		colName.setEditingSupport(new EditingSupport(this){
			TextCellEditor editor = new TextCellEditor((Composite)getControl());
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				return ((DataAggregation)element).getMeasureName();
			}

			@Override
			protected void setValue(Object element, Object value) {
				((DataAggregation)element).setMeasureName((String)value);
				actionsListeners.firePropertyChange(AGGREGATION_CHANEGD,null, value);
				refresh();
			}
			
		});
		
		TableViewerColumn applySerie = new TableViewerColumn(this, SWT.NONE);
		applySerie.getColumn().setText(Messages.MultiSeriesTableViewer_7);
		applySerie.getColumn().setWidth(200);
		applySerie.setLabelProvider(new ColumnLabelProvider(){
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				return ((DataAggregation)element).isApplyOnDistinctSerie() + ""; //$NON-NLS-1$
			}
		});
		applySerie.setEditingSupport(new EditingSupport(this){
			ComboBoxCellEditor editor = new ComboBoxCellEditor((Composite)getControl(), new String[]{"true", "false"}, SWT.READ_ONLY); //$NON-NLS-1$ //$NON-NLS-2$
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				
				if (((DataAggregation)element).isApplyOnDistinctSerie()){
					return 0;
				}
				return 1;
			}

			@Override
			protected void setValue(Object element, Object value) {
				
				if (((Integer)value).intValue() == 0){
					((DataAggregation)element).setApplyOnDistinctSerie(true);
				}
				else{
					((DataAggregation)element).setApplyOnDistinctSerie(false);
				}
				actionsListeners.firePropertyChange(AGGREGATION_CHANEGD,null, value);
				refresh();
			}
			
		});
		createMenu();

	}
	
	public void setDataSet(DataSet dataSet){
		this.dataSet = dataSet;
		aggregationEdition.resetEditorContent(dataSet);
	}
	
	private void createMenu(){
		MenuManager menuManager = new MenuManager();
		
		final Action delete = new Action(Messages.MultiSeriesTableViewer_1){
			public void run(){
				DataAggregation a = (DataAggregation)((IStructuredSelection)getSelection()).getFirstElement();
				((List<?>)getInput()).remove(a);
				actionsListeners.firePropertyChange(AGGREGATION_REMOVED, a, null);
				refresh();
			}
		};
		
		
		menuManager.add(delete);
		menuManager.add(new Action(Messages.MultiSeriesTableViewer_12){
			public void run(){
				DataAggregation a = new DataAggregation();
				((List<DataAggregation>)getInput()).add(a);
				actionsListeners.firePropertyChange(AGGREGATION_ADDED, null, a);
				refresh();
			}
		});
		menuManager.addMenuListener(new IMenuListener(){

			public void menuAboutToShow(IMenuManager manager) {
				if (getSelection().isEmpty()){
					delete.setEnabled(false);
					return;
				}
				delete.setEnabled(true);
				
				
			}
			
		});
		Menu m = menuManager.createContextMenu(getControl());
		
		getTable().setMenu(m);
	}
	
	
	private class DataSetEditSupport extends EditingSupport{

		private ComboBoxCellEditor editor = new ComboBoxCellEditor((Composite)getControl(), new String[]{}, SWT.READ_ONLY);
		
		public void resetEditorContent(DataSet dataSet){
			List<String> l = new ArrayList<String>();
			
			for(ColumnDescriptor c : dataSet.getDataSetDescriptor().getColumnsDescriptors()){
				l.add(c.getColumnName());
			}
			editor.setItems(l.toArray(new String[l.size()]));
		}
		
		public DataSetEditSupport(ColumnViewer viewer) {
			super(viewer);
			
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		@Override
		protected Object getValue(Object element) {
			return ((DataAggregation)element).getValueFieldIndex()-1;
		}

		@Override
		protected void setValue(Object element, Object value) {
			((DataAggregation)element).setValueFieldIndex((Integer)value + 1);
			actionsListeners.firePropertyChange(AGGREGATION_CHANEGD,null, value);
			refresh();
		}
		
	}

	
	
}
