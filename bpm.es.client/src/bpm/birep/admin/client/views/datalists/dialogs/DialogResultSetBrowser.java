package bpm.birep.admin.client.views.datalists.dialogs;

import java.util.List;

import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import adminbirep.Messages;

public class DialogResultSetBrowser extends Dialog{

	private IResultSetMetaData meta;
	private List<List<Object>> values ;
	
	private TableViewer viewer;
	
	public DialogResultSetBrowser(Shell parentShell, IResultSetMetaData meta, List<List<Object>> values) {
		super(parentShell);
		this.meta = meta;
		this.values = values;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		viewer = new TableViewer(parent, SWT.BORDER);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				List l = (List)inputElement;
				return l.toArray(new Object[l.size()]);
			}
		});
		
		try{
			for(int i = 1; i <= meta.getColumnCount(); i++){
				TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
				col.getColumn().setWidth(100);
				col.getColumn().setText(meta.getColumnLabel(i));
				final int k = i;
				
				col.setLabelProvider(new ColumnLabelProvider(){
					@Override
					public String getText(Object element) {
						Object o = ((List)element).get(k - 1);
						if (o == null){
							return ""; //$NON-NLS-1$
							
						}
						else{
							return o.toString();
						}
					}
				});
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		return viewer.getTable();
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.Client_Views_DialogResultSetBrowser_1);
		getShell().setSize(800, 600);
		viewer.setInput(values);
	}
}
