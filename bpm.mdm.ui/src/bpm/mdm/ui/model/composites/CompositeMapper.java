package bpm.mdm.ui.model.composites;



import java.util.Collections;

import org.eclipse.datatools.connectivity.oda.design.ColumnDefinition;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Model;
import bpm.mdm.model.Synchronizer;
import bpm.mdm.ui.i18n.Messages;

public class CompositeMapper extends Composite{
	

	private TableViewer table;
	private TableViewerColumn mapCol;
	private Synchronizer synchronizer;
	private MapperEditSupport mapperEditSupport;
	
	public CompositeMapper(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout());
		
		table = new TableViewer(this, SWT.BORDER | SWT.VIRTUAL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		table.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setContentProvider(new ArrayContentProvider());
		
		
		table.getTable().setHeaderVisible(true);
		table.getTable().setLinesVisible(true);

		TableViewerColumn col = new TableViewerColumn(table, SWT.NONE);
		col.getColumn().setText(Messages.CompositeMapper_0);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				
				return ((Attribute)element).getName();
			}
		});
		
		mapCol = new TableViewerColumn(table, SWT.NONE);
		mapCol.getColumn().setText(Messages.CompositeMapper_1);
		mapCol.getColumn().setWidth(200);
		mapCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				
				
				int fieldIndex = synchronizer.getDataSourceField((Attribute)element);
				
								
				try{
					return mapperEditSupport.getFieldName(fieldIndex );
//					ColumnDefinition def = ((Model)synchronizer.eContainer()).getDataSource(synchronizer.getDataSourceName()).getPrimaryResultSet().getResultSetColumns().getResultColumnDefinitions().get(fieldIndex);
//					return def.getAttributes().getName();
				}catch(Exception ex){
					//ex.printStackTrace();
					return fieldIndex + ""; //$NON-NLS-1$
				}
				
			}
		});
		
	}
	
	public void bind(Synchronizer synchronizer){
		this.synchronizer = synchronizer;
		
		if (this.synchronizer == null){
			table.setInput(Collections.EMPTY_LIST);
		}
		else{
			try{
				mapperEditSupport = new MapperEditSupport(table, synchronizer);
				mapCol.setEditingSupport(mapperEditSupport);
			}catch(Exception ex){
				ex.printStackTrace();
//				MessageDialog.openError(getShell(), "Creating EditionSupport", "Failure : " + ex.getMessage());
			}
			try{
				table.setInput(synchronizer.getEntity().getAttributes());
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
	
	}
}
