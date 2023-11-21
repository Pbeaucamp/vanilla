package bpm.fd.design.ui.wizard.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDriver;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.jface.dialogs.MessageDialog;
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

import bpm.fd.api.core.model.datas.ColumnDescriptor;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.datas.ParameterDescriptor;
import bpm.fd.api.core.model.datas.odaconsumer.ConnectionManager;
import bpm.fd.api.core.model.datas.odaconsumer.DriverManager;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.wizard.dialogs.DialogParameterValues;

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
	
	
	public void createViewer(DataSet dataSet){
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
					query.close();
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
				
				try{
					return ((List)element).get(columnIndex) + ""; //$NON-NLS-1$
				}catch(Exception ex){
					ex.printStackTrace();
					return Messages.DataSetContentPage_1;
				}
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
		for(ColumnDescriptor col : dataSet.getDataSetDescriptor().getColumnsDescriptors()){
			TableColumn c = new TableColumn(viewer.getTable(), SWT.NONE);
			try{
				c.setText(col.getColumnLabel());
			}catch(Exception ex){
				try{
					c.setText(col.getColumnName());
				}catch(Exception ex2){
					ex2.printStackTrace();
				}
			}
			c.setWidth(200);
		}
		
		
		
		
		if (!dataSet.getDataSetDescriptor().getParametersDescriptors().isEmpty()){
			
			DialogParameterValues d = new DialogParameterValues(getShell(), dataSet.getDataSetDescriptor().getParametersDescriptors());
			if (d.open() == DialogParameterValues.OK){
				try{
					viewer.setInput(executeQuery(dataSet, d.getValues()));
				}catch(Exception e){
					e.printStackTrace();
					MessageDialog.openError(getShell(), Messages.DataSetContentPage_2, Messages.DataSetContentPage_3 + e.getMessage());
				}
				
			}
		}
		else{
			try{
				viewer.setInput(executeQuery(dataSet, null));
			}catch(Exception e){
				e.printStackTrace();
				MessageDialog.openError(getShell(), Messages.DataSetContentPage_4, Messages.DataSetContentPage_5 + e.getMessage());
			}
		}
		
		main.getParent().layout(true);
		main.layout();
	}
	
	private IResultSet executeQuery(DataSet ds, HashMap<ParameterDescriptor, String> pValues) throws Exception{
		Dictionary d = Activator.getDefault().getProject().getDictionary();
		
		DataSource dataSource = null;
//		System.out.println();
		for(DataSource s : d.getDatasources()){
			if (s.getName().equals(ds.getDataSourceName())){
				dataSource = s;
			}
		}

		IDriver driverHelper = DriverManager.getOdaDriver(dataSource);
		IConnection c = ConnectionManager.openConnection(dataSource).getOdaConnection();
		
		if(c.getMaxQueries() <= 1) {
			c.close();
			c = ConnectionManager.openConnection(dataSource).getOdaConnection();
		}
		
		query = c.newQuery(""); //$NON-NLS-1$
		query.setMaxRows(100);
		query.prepare(ds.getQueryText());
		
		if (pValues != null){
			for(ParameterDescriptor p : pValues.keySet()){
				query.setString(p.getPosition(), pValues.get(p));
			}
		}
		IResultSet rs = query.executeQuery();
		
//		query.close();
		
		return rs;
	}
}
