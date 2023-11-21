package bpm.vanilla.server.ui.views.actions.gateway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import bpm.vanilla.server.client.communicators.gateway.GatewayServerClient;
import bpm.vanilla.server.ui.Activator;

public class DialogStepsInfos extends Dialog{
	
	
	private static Color ERROR = new Color(Display.getDefault(), 250, 25, 0);
	private static Color OK = new Color(Display.getDefault(), 0, 250, 0);
	private static Color WARN = new Color(Display.getDefault(), 0, 128, 255);
	
	private class Monitor extends Thread{
		private boolean active = true;
		
		public void run(){
			while (active){
				
				try {
					datas = ((GatewayServerClient)Activator.getDefault().getServerRemote()).getGatewayStepsInfos(taskId);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				Display.getDefault().asyncExec(new Runnable(){
					public void run(){
						viewer.setInput(datas);
						viewer.refresh();
					}
				});
				
				try{
					Thread.sleep(2000);
				}catch(Exception ex){
					
				}
			}
		}
	};
	
	
	private List<String> cols = new ArrayList<String>();
	private List<Properties> datas;
	private int taskId;
	
	private TableViewer viewer;
	private Monitor monitor;
	
	public DialogStepsInfos(Shell parentShell, int taskId, List<Properties> props) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		datas = props;
		this.taskId = taskId;
		
		for(Properties p : datas){
			Enumeration en = p.propertyNames();
			while(en.hasMoreElements()){
				boolean found = false;
				String _p = (String)en.nextElement();
				for(String s : cols){
					if (s.equals(_p)){
						found = true;
						break;
					}
				}
				if (!found){
					cols.add(_p);
				}
			}
		}
		
		Collections.sort(cols, new Comparator<String>() {
			String[] order = new String[]{"stepName", "stepReadedRows", "stepProcessedRows", "stepBufferedRows", "stepState", "stepStartTime", "stepStopTime", "stepDuration", 
					"stepWarningsNumber", "stepErrorsNumber"};
			public int compare(String o1, String o2) {
				Integer i1 = null; 
				Integer i2 = null;
				if (i1 == null){
					return 1;
				}
				for(int i = 0; i < order.length; i++){
					if (order[i].equals(o1)){
						i1 = i;
					}
					if (order[i].equals(o2)){
						i2 = i;
					}
				}
				
				return i1.compareTo(i2);
			}
		});
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		viewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
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
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		
		for(String s : cols){
			TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
			col.getColumn().setWidth(100);
			String txt = new String(s);
			if (txt.startsWith("step")){
				txt = txt.substring(4);
			}
			col.getColumn().setText(txt);
			final String propName = s;
			col.setLabelProvider(new ColumnLabelProvider(){
				
				/* (non-Javadoc)
				 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
				 */
				@Override
				public String getText(Object element) {
				
					String s = ((Properties)element).getProperty(propName);
					return s != null ? s : "";
				}

				/* (non-Javadoc)
				 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getBackground(java.lang.Object)
				 */
				@Override
				public Color getBackground(Object element) {
					if (!((Properties)element).getProperty("stepWarningsNumber").equals("0")){
						return WARN;
					}
					if (!((Properties)element).getProperty("stepErrorsNumber").equals("0")){
						return ERROR;
					}
					return OK;
				}
				
				
				
			});
		}
		
		
		return viewer.getTable();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
	 */
	@Override
	protected void initializeBounds() {
		getShell().setSize(1000, 300);
		getShell().setText("Gateway Execution Details");
		viewer.setInput(datas);
		monitor = new Monitor();
		
		monitor.start();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		monitor.active = false;
		try {
			monitor.join();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		super.okPressed();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
	}
	
	
	
}
