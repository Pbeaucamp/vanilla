package bpm.studio.jdbc.management.ui;

import java.util.Collection;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.studio.jdbc.management.ui.dialogs.DialogDriverInfo;

public class JdbcDriverRegistrationPage extends PreferencePage implements IWorkbenchPreferencePage {
	
	private TableViewer viewer;
	private Button add, del;
	
	public JdbcDriverRegistrationPage() {
		
	}

	public JdbcDriverRegistrationPage(String title) {
		super(title);
		
	}

	public JdbcDriverRegistrationPage(String title, ImageDescriptor image) {
		super(title, image);
		
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		add = new Button(main, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		add.setText("Add jdbc driver");
		add.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogDriverInfo dial = new DialogDriverInfo(getShell());
				if (dial.open() == DialogDriverInfo.OK){
					DriverInfo info = dial.getDriverInfo();
					if (info != null){
										
						
						try {
							
							ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).addDriver(info, true);
							viewer.refresh();
						} catch (Exception e1) {
							
							e1.printStackTrace();
						}	
					}
					
				}
			}
		});
		
		del = new Button(main, SWT.PUSH);
		del.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		del.setText("Remove jdbc driver");
		del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				if (ss.isEmpty()){
					return;
				}
				try {
					ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).removeDriver((DriverInfo)ss.getFirstElement());
					viewer.refresh();
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}	
			}
		});
		
		viewer = new TableViewer(main, SWT.BORDER | SWT. V_SCROLL);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
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
		
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("Driver Name");
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((DriverInfo)element).getName();
			}
		});
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("Driver Class");
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((DriverInfo)element).getClassName();
			}
		});
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText("Jdbc Jar File");
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((DriverInfo)element).getFile();
			}
		});
		
		
		try{
			viewer.setInput(ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversInfo());
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), "Problem", "Error when getting the registered drivers : " + ex.getMessage());
		}
		return main;
	}

	public void init(IWorkbench workbench) {

	}

}
