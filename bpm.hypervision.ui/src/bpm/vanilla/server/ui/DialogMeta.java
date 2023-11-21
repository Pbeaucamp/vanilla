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

import bpm.vanilla.platform.core.beans.tasks.TaskInfo;

public class DialogMeta extends Dialog{
	
	private TableViewer viewer;
	private Properties prop;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss,sss"); //$NON-NLS-1$
	private SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss"); //$NON-NLS-1$
	
	public DialogMeta(Shell parentShell, TaskInfo info) {
		super(parentShell);
		
		prop = new Properties();
		
		
		prop.setProperty("Task Id", info.getId()); //$NON-NLS-1$
		switch(info.getState()){
		case ENDED:
			prop.setProperty("State", Messages.DialogMeta_4); //$NON-NLS-1$
			break;
		case WAITING:
			prop.setProperty("State", Messages.DialogMeta_6); //$NON-NLS-1$
			break;
		case RUNNING:
			prop.setProperty("State", Messages.DialogMeta_8); //$NON-NLS-1$
			break;
		}
		try{
			prop.setProperty("Creation", sdf.format(info.getCreationDate())); //$NON-NLS-1$
		}catch(Exception ex){
			prop.setProperty("Creation", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		try{
			prop.setProperty(Messages.DialogMeta_12, sdf.format(info.getStartedDate()));
		}catch(Exception ex){
			prop.setProperty(Messages.DialogMeta_13, Messages.DialogMeta_14);
		}
		try{
			prop.setProperty("Stopped", sdf.format(info.getStoppedDate())); //$NON-NLS-1$
		}catch(Exception ex){
			prop.setProperty("Stopped", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		try{
			prop.setProperty("Duration", sdf2.format(info.getDurationTime())); //$NON-NLS-1$
		}catch(Exception ex){
			prop.setProperty("Duration", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		try{
			prop.setProperty("Elapsed", sdf2.format(info.getElapsedTime())); //$NON-NLS-1$
		}catch(Exception ex){
			prop.setProperty("Elapsed", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
	
		
		
		
		
		prop.setProperty("Profil", info.getGroupName()); //$NON-NLS-1$
		
		switch(info.getResult()){
		case FAILED:
			prop.setProperty("Result", Messages.DialogMeta_26); //$NON-NLS-1$
			break;
		case SUCCEED:
			prop.setProperty("State", Messages.DialogMeta_28); //$NON-NLS-1$
			break;
		case UNDEFINED:
			prop.setProperty("State", Messages.DialogMeta_30); //$NON-NLS-1$
			break;
		}
		

		prop.setProperty("Failure Cause", info.getFailureCause() != null ? info.getFailureCause() : ""); //$NON-NLS-1$ //$NON-NLS-2$
		
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
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
			public void dispose() { }
			
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
		name.getColumn().setText(Messages.DialogMeta_33);
		name.getColumn().setWidth(200);
		name.setLabelProvider(new ColumnLabelProvider());
		
		
		TableViewerColumn value = new TableViewerColumn(viewer, SWT.NONE);
		value.getColumn().setText(Messages.DialogMeta_34);
		value.getColumn().setWidth(200);
		value.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				return prop.getProperty((String)element);
			}
			
		});
		
		return main;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 300);
		getShell().setText(Messages.DialogMeta_35);
		viewer.setInput(prop);
	}
	
	
	
}
