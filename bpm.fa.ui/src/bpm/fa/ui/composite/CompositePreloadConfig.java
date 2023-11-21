package bpm.fa.ui.composite;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import bpm.fa.api.olap.Hierarchy;
import bpm.fa.ui.Messages;
import bpm.united.olap.api.preload.IPreloadConfig;
import bpm.united.olap.api.preload.PreloadConfig;

public class CompositePreloadConfig extends Composite{
	private TableViewer tableViewer;
	private IPreloadConfig config;
	
	
	public CompositePreloadConfig(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Label lblDefineTheLevel = new Label(this, SWT.NONE);
		lblDefineTheLevel.setBounds(0, 0, 55, 15);
		lblDefineTheLevel.setText(Messages.CompositePreloadConfig_0);
		
		tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.setContentProvider(new ArrayContentProvider());
		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setBounds(0, 0, 85, 85);
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Hierarchy)element).getUniqueName();
			}
		});
		
		TableColumn tblclmnHierarchy = tableViewerColumn.getColumn();
		tblclmnHierarchy.setWidth(200);
		tblclmnHierarchy.setText(Messages.CompositePreloadConfig_1);
		
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				
				return super.getText(element);
			}
		});
		
		TableColumn tblclmnLevelnumber = tableViewerColumn_1.getColumn();
		tblclmnLevelnumber.setWidth(100);
		tblclmnLevelnumber.setText(Messages.CompositePreloadConfig_2);
		
		ListViewer listViewer = new ListViewer(this, SWT.BORDER | SWT.V_SCROLL);
		listViewer.setContentProvider(new ArrayContentProvider());
		Control list = listViewer.getList();
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		list.setBounds(0, 0, 88, 68);
		
		
		setEditingSupport(tableViewerColumn_1);
	}
	
	private void setEditingSupport(TableViewerColumn col){
		EditingSupport sup = new EditingSupport(tableViewer) {
			ComboBoxCellEditor editor = new ComboBoxCellEditor(tableViewer.getTable(), new String[]{});
			@Override
			protected void setValue(Object element, Object value) {
				config.setHierarchyLevel(((Hierarchy)element).getUniqueName(), (Integer)value);
				tableViewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				Integer i = config.getLevelNumber(((Hierarchy)element).getUniqueName());
				if (i == null){
					return 0;
				}
				return i;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				String[] items = new String[((Hierarchy)element).getLevel().size()];
				for(int i = 0; i < items.length; i++){
					items[i] = "" + i; //$NON-NLS-1$
				}
				editor.setItems(items);
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		};
	
		col.setEditingSupport(sup);
	}
	
	public void init(List<Hierarchy> l){
//		List<Hierarchy> l = new ArrayList<Hierarchy>();
//		
//		for(Dimension d : cube.getStructure().getDimensions()){
//			l.addAll(d.getHierarchies());
//		}
		
		tableViewer.setInput(l);
		
		config = new PreloadConfig();
	}
	
	public IPreloadConfig getConfig(){
		return config;
	}
}
