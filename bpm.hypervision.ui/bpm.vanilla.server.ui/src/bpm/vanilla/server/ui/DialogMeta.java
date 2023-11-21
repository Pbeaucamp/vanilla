package bpm.vanilla.server.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.vanilla.server.client.communicators.TaskInfo;
import bpm.vanilla.server.client.communicators.TaskMeta;

public class DialogMeta extends Dialog{
	
	private TableViewer viewer;
	private Properties prop;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss,sss");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss");
	
	public DialogMeta(Shell parentShell, TaskMeta meta, TaskInfo info) {
		super(parentShell);
		
		prop = new Properties();
		
		
		prop.setProperty("Task Id", info.getId());
		switch(info.getState()){
		case TaskInfo.STATE_ENDED:
			prop.setProperty("State", "Finished");
			break;
		case TaskInfo.STATE_WAITING:
			prop.setProperty("State", "Waiting");
			break;
		case TaskInfo.STATE_RUNNING:
			prop.setProperty("State", "Running");
			break;
		}
		try{
			prop.setProperty("Creation", sdf.format(info.getCreationDate()));
		}catch(Exception ex){
			prop.setProperty("Creation", "");
		}
		try{
			prop.setProperty("Sarted", sdf.format(info.getStartedDate()));
		}catch(Exception ex){
			prop.setProperty("Sarted", "");
		}
		try{
			prop.setProperty("Stopped", sdf.format(info.getStoppedDate()));
		}catch(Exception ex){
			prop.setProperty("Stopped", "");
		}
		try{
			prop.setProperty("Duration", sdf2.format(info.getDurationTime()));
		}catch(Exception ex){
			prop.setProperty("Duration", "");
		}
		try{
			prop.setProperty("Elapsed", sdf2.format(info.getElapsedTime()));
		}catch(Exception ex){
			prop.setProperty("Elapsed", "");
		}
		
	
		
		
		
		
		prop.setProperty("Profil", info.getGroupName());
		
		switch(info.getResult()){
		case TaskInfo.RESULT_FAILED:
			prop.setProperty("Result", "Failed");
			break;
		case TaskInfo.RESULT_SUCCEED:
			prop.setProperty("State", "Succeeded");
			break;
		case TaskInfo.RESULT_UNDEFINED:
			prop.setProperty("State", "Undefined");
			break;
		}
		

		prop.setProperty("Failure Cause", info.getFailureCause() != null ? info.getFailureCause() : "");
		
		
		
		prop.putAll(meta.getProperties());
		prop.putAll(meta.getParameters());
		
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer = new TableViewer(main, SWT.BORDER | SWT.FULL_SELECTION);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		
		viewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				List<String> l = new ArrayList<String>();
				Enumeration en = prop.propertyNames();
				while(en.hasMoreElements()){
					l.add((String)en.nextElement());
				}
				
				return l.toArray(new String[l.size()]);
			}
		});
		
		TableViewerColumn name = new TableViewerColumn(viewer, SWT.NONE);
		name.getColumn().setText("Name");
		name.getColumn().setWidth(200);
		name.setLabelProvider(new ColumnLabelProvider());
		
		
		TableViewerColumn value = new TableViewerColumn(viewer, SWT.NONE);
		value.getColumn().setText("value");
		value.getColumn().setWidth(200);
		value.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				
				return prop.getProperty((String)element);
			}
			
		});
		
		return main;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
	 */
	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 300);
		getShell().setText("Task Definition");
		viewer.setInput(prop);
	}
	
	
	
}
