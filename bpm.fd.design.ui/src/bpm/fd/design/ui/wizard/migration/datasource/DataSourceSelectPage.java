package bpm.fd.design.ui.wizard.migration.datasource;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.design.ui.Messages;

public class DataSourceSelectPage extends WizardPage{

	private TableViewer viewer, detailsViewer;
	
	protected DataSourceSelectPage(String pageName) {
		super(pageName);
		
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent,SWT.NONE);
		main.setLayout(new GridLayout());
		createViewer(main);
		setControl(main);
	}

	
	private void createViewer(Composite parent){
		viewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection<?> c = (Collection<?>)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				if (viewer.getSelection().isEmpty()){
					detailsViewer.setInput(new Properties());	
				}
				else{
					detailsViewer.setInput(((DataSource)((IStructuredSelection)viewer.getSelection()).getFirstElement()).getProperties());
				}
				
				
			}
		});
		
		TableViewerColumn dsName = new TableViewerColumn(viewer, SWT.NONE);
		dsName.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((DataSource)element).getName();
			}
		});
		
		dsName.getColumn().setWidth(200);
		dsName.getColumn().setText(Messages.DataSourceSelectPage_0);
		
		
		createPropertiesViewer(parent);
		
	
	}
	
	protected void setInput(Collection<DataSource> dataSources){
		viewer.setInput(dataSources);
	}
	
	private void createPropertiesViewer(Composite parent){
		detailsViewer = new TableViewer(parent, SWT.BORDER | SWT. FULL_SELECTION);
		detailsViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		detailsViewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Properties p = (Properties)inputElement;
				return p.keySet().toArray(new Object[p.keySet().size()]);
			}
		});
	
		TableViewerColumn pname = new TableViewerColumn(detailsViewer, SWT.NONE);
		pname.getColumn().setText(Messages.DataSourceSelectPage_1);
		pname.getColumn().setWidth(200);
		pname.setLabelProvider(new ColumnLabelProvider());
		
		
		TableViewerColumn pvalue = new TableViewerColumn(detailsViewer, SWT.NONE);
		pvalue.getColumn().setText(Messages.DataSourceSelectPage_2);
		pvalue.getColumn().setWidth(300);
		pvalue.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				Properties p =(Properties) detailsViewer.getInput();
				
				if (p.getProperty((String)element) == null){
					return ""; //$NON-NLS-1$
				}
				else{
					return p.getProperty((String)element);
				}
			}
		});
	}
	
	public List<DataSource> getDataSourceToMigrate(){
		List<DataSource> l = new ArrayList<DataSource>();
		
		for(Object o : ((CheckboxTableViewer)viewer).getCheckedElements()){
			l.add((DataSource)o);
		}
		return l;
	}
	
	@Override
	public boolean canFlipToNextPage() {
		
		return false;
	}
}
