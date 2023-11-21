package metadata.client.model.dialog;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import metadata.client.i18n.Messages;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Join;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.logical.RelationException;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.logical.sql.SQLRelation;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.layer.physical.sql.SQLTable;

public class DialogForeignKeyImporter extends Dialog{
	private SQLDataSource dataSource;
	private CheckboxTableViewer viewer;
	private Button check,uncheck;
	
	private static String[] colsLabels = new String[]{"LeftSchema", "LeftTable", "LeftColumn", "RightSchema", "RightTable", "RightColumn"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	
	private List<Relation> relations = new ArrayList<Relation>();
	
	public DialogForeignKeyImporter(Shell parentShell, SQLDataSource dataSource) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.dataSource = dataSource;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		check = new Button(main, SWT.PUSH);
		check.setText(Messages.DialogForeignKeyImporter_6);
		check.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		check.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(true);
			}
		});
		
		uncheck = new Button(main, SWT.PUSH);
		uncheck.setText(Messages.DialogForeignKeyImporter_7);
		uncheck.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		uncheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(false);
			}
		});
		
		viewer = CheckboxTableViewer.newCheckList(main, SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		viewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			
			public void dispose() {}
			
			public Object[] getElements(Object inputElement) {
				List l = (List)inputElement;
				return l.toArray(new Object[l.size()]);
			}
		});
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		
		for(int i = 0; i < 6; i++){
			TableViewerColumn c = new TableViewerColumn(viewer, SWT.LEFT);
			c.getColumn().setWidth(100);
			final int k = i;
			c.getColumn().setText(colsLabels[i]);
			c.setLabelProvider(new ColumnLabelProvider(){
				@Override
				public String getText(Object element) {
					return "" + ((List<String>)element).get(k); //$NON-NLS-1$
				}
			});

		}
		
		
				
		return main;
	} 

	
	private void fill() throws Exception{
		List<List<String>> aa = new ArrayList<List<String>>();
		
		VanillaJdbcConnection c = ((SQLConnection)dataSource.getConnection()).getJdbcConnection();

		DatabaseMetaData dbmd = c.getMetaData();
		int[] index = new int[]{2, 3, 4, 6, 7, 8};
		for(IDataStream _table : dataSource.getDataStreams()){
			
			
			ResultSet rs = dbmd.getImportedKeys(c.getCatalog(), ((SQLTable)_table.getOrigin()).getSchemaName(), ((SQLTable)_table.getOrigin()).getShortName());
			ResultSetMetaData rsmd = rs.getMetaData();
		     while (rs.next()) {
		    	 List<String> l = new ArrayList<String>();
		       for(int i = 1; i <= rsmd.getColumnCount(); i++){
		    	   for(int k : index){
		    		   if (k == i){
		    			   l.add(rs.getString(i));
		    		   }
		    	   }
		    	   
		       }
		       aa.add(l);
		     }
		     
		     rs.close();
		    
		}
		 ConnectionManager.getInstance().returnJdbcConnection(c);
	     
	     viewer.setInput(aa);
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogForeignKeyImporter_9);
		getShell().setSize(800, 600);
		try{
			fill();
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogForeignKeyImporter_10, Messages.DialogForeignKeyImporter_11 + ex.getMessage());
		}
		
	}

	
	@Override
	protected void okPressed() {
		createRelations();
		super.okPressed();
	}
	
	public List<Relation> getRelations(){
		return relations;
	}
	
	private void createRelations(){
		relations.clear();
		Object[] datas = (Object[])viewer.getCheckedElements();
		
		for(Object o : datas){
			List<String> l = (List<String>)o;
			String leftSchema = l.get(0);
			String leftTable= l.get(1);
			String leftColumn= l.get(2);
			
			String rightSchema = l.get(3);
			String rightTable= l.get(4);
			String rightColumn= l.get(5);
			
			IDataStreamElement leftDSE = null;
			IDataStreamElement rightDSE = null;
			
			
			for(IDataStream ds : dataSource.getDataStreams()){
				SQLTable ori = (SQLTable)ds.getOrigin();
				if ((ori.getSchemaName() == leftSchema || ori.getSchemaName().equals(leftSchema)) && ori.getShortName().equals(leftTable)){
					for(IDataStreamElement col : ds.getElements()){
						if (col.getOrigin().getName().equals(leftTable + "." + leftColumn) || col.getOrigin().getName().equals(leftSchema + "." + leftTable + "." + leftColumn)){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							leftDSE = col;
							break;
						}
					}
				}
				
				
				if ((ori.getSchemaName() == rightSchema || ori.getSchemaName().equals(rightSchema)) && ori.getShortName().equals(rightTable)){
					for(IDataStreamElement col : ds.getElements()){
						if (col.getOrigin().getName().equals(rightTable + "." + rightColumn) || col.getOrigin().getName().equals(rightSchema + "." + rightTable + "." + rightColumn) ){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							rightDSE = col;
							break;
						}
					}
					
				}
			}
			
			if (rightDSE != null && leftDSE != null){
				
				SQLRelation rel = null;
				for(Relation r : relations){
					if (r.isUsingTable(rightDSE.getDataStream()) && r.isUsingTable(leftDSE.getDataStream())){
						rel = (SQLRelation)r;
						break;
					}
				}
				
				if (rel == null){
					rel = new SQLRelation();
				
					relations.add(rel);
				}
				
				boolean existsAlready = false;
				for(Join j : rel.getJoins()){
					if (j.getLeftElement() == leftDSE && j.getRightElement() == rightDSE ||
						j.getLeftElement() == rightDSE && j.getRightElement() == leftDSE ){
						existsAlready = true;
						break;
					}
					
				}
				
				if (!existsAlready){
					try {
						rel.add(leftDSE, rightDSE, Join.INNER);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (RelationException e) {
						e.printStackTrace();
					}
				
				}
			}
			
			
			
		}
		
		
		
		
	}

	
}
