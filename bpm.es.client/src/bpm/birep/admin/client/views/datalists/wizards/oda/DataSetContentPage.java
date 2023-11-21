package bpm.birep.admin.client.views.datalists.wizards.oda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import adminbirep.Messages;


public class DataSetContentPage extends WizardPage{
	
	private TableViewer viewer;
	private Composite main;
	private IQuery query;
	public DataSetContentPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		main = new Composite(parent,SWT.NONE);
		main.setLayout(new GridLayout());
		
		
		setControl(main);
		
	}
	
	
	public void createViewer(IQuery query) throws Exception{
		
		if (viewer != null && !viewer.getControl().isDisposed()){
			viewer.getControl().dispose();
		}
		
		viewer = new TableViewer(main, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				IResultSet rs = (IResultSet)inputElement;
				try{
					IResultSetMetaData mtd = rs.getMetaData();
					
					List<List<Object>> datas = new ArrayList<List<Object>>();
					
					while(rs.next()){
						List<Object> row = new ArrayList<Object>();
						
						for(int i = 1; i <= mtd.getColumnCount(); i++){
							row.add(rs.getString(i));
						}
						datas.add(row);
					}
					rs.close();
					
					return datas.toArray(new Object[datas.size()]);
				}catch(Exception e){
					e.printStackTrace();
				}
				return null;
				
				
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
		});
		viewer.setLabelProvider(new ITableLabelProvider(){

			public Image getColumnImage(Object element, int columnIndex) {
				
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				
				return ((List)element).get(columnIndex) + ""; //$NON-NLS-1$
			}

			public void addListener(ILabelProviderListener listener) {
				
				
			}

			public void dispose() {
				
				
			}

			public boolean isLabelProperty(Object element, String property) {
				
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
				
				
			}
			
		});
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		for( int i = 0; i < query.getMetaData().getColumnCount(); i++){
			TableColumn c = new TableColumn(viewer.getTable(), SWT.NONE);
			c.setText(query.getMetaData().getColumnLabel(i + 1));
			c.setWidth(200);
		}
		
		
		
		
		
		if (query.getParameterMetaData().getParameterCount() != 0){
			//TODO
//			DialogParameterValues d = new DialogParameterValues(getShell(), dataSet.getDataSetDescriptor().getParametersDescriptors());
//			if (d.open() == DialogParameterValues.OK){
//				try{
//					viewer.setInput(executeQuery(dataSet, d.getValues()));
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//				
//			}
		}
		else{
			try{
				viewer.setInput(executeQuery(query, null));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		main.layout();
	}
	
	private IResultSet executeQuery(IQuery query, HashMap<Integer, String> pValues) throws Exception{
				
		if (pValues != null){
			query.clearInParameters();
			for(Integer p : pValues.keySet()){
				query.setString(p, pValues.get(p));
			}
		}
		
		
		return query.executeQuery();
	}
}
