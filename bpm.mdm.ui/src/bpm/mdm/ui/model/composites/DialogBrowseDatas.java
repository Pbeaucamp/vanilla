package bpm.mdm.ui.model.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DialogBrowseDatas extends Dialog{
	private static final String NULL_VALUE = ""; //$NON-NLS-1$
	private class _LabelProvider extends ColumnLabelProvider{
		int index;
		_LabelProvider(int index){
			this.index = index;
		}
		@Override
		public String getText(Object element) {
			String val = ((String[])element)[index];
			if (val == null){
				return NULL_VALUE;
			}
			return val;
		}
	}
	
	private TableViewer viewer;
	private IQuery query;
	private String qText;
	public DialogBrowseDatas(Shell shell, IQuery query, String qText){
		super(shell);
		this.query = query;
		this.qText = qText;
	}
	
	
	@Override
	protected Control createDialogArea(Composite parent) {
		viewer = new TableViewer(parent, SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new ArrayContentProvider());
		
		
		IResultSetMetaData rsmd = null;
		try{
			 rsmd = query.getMetaData();
			
			for (int i = 1; i <= rsmd.getColumnCount(); i++){
				TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
				col.getColumn().setText(rsmd.getColumnName(i));
				col.getColumn().setWidth(200);
				col.setLabelProvider(new _LabelProvider(i - 1));
			}
		}catch(Exception ex){
			
		}
		List<String[]> datas = new ArrayList<String[]>();
		
		IResultSet rs = null;
		
		try{
			rs = query.executeQuery();
			
			while(rs.next()){
				String[] row = new String[viewer.getTable().getColumnCount()];
				for (int i =1 ; i <= viewer.getTable().getColumnCount(); i++){
					try{
						row[i-1] = extractValue(i, rs, rsmd);
					}catch(Exception ex){
						row[i-1]= "" + ex.getMessage(); //$NON-NLS-1$
					}
				}
				datas.add(row);
			}	
		}catch (Exception e) {
			e.printStackTrace();
		
		}
		viewer.setInput(datas);	
		
		return viewer.getTable();
	}
	



	private String extractValue(int i, IResultSet rs, IResultSetMetaData rsmd) {
		
		Object res = null;
		
		try{
			switch (rsmd.getColumnType(i)) {
			case java.sql.Types.BIGINT:
			case java.sql.Types.INTEGER:
			case java.sql.Types.SMALLINT:
			case java.sql.Types.TINYINT:
				res = rs.getInt(i) ;
				break;
			case java.sql.Types.FLOAT:
			case java.sql.Types.DOUBLE:
			case java.sql.Types.DECIMAL:
			case java.sql.Types.NUMERIC:
			case java.sql.Types.REAL:
				res = rs.getDouble(i);
				
				break;
			case java.sql.Types.DATE:
			case java.sql.Types.TIME:
			case java.sql.Types.TIMESTAMP:
				res = rs.getDate(i);
				
				break;
			case java.sql.Types.BOOLEAN:
				res = rs.getBoolean(i);
			default:
				res = rs.getString(i);
				break;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		
		if (res != null){
			return res.toString();
		}
		return null;
	}
}
