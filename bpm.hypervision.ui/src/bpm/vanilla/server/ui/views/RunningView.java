package bpm.vanilla.server.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.vanilla.platform.core.beans.tasks.TaskInfo;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;

public class RunningView extends ServerContent{

	
	protected class Monitor extends Thread{
		int refresh = 5000;
		String servetUrl;
		boolean active = true;
		public void run(){
			while(active){
				final List<TaskInfo> l = new ArrayList<TaskInfo>();
				try{
					if (Activator.getDefault().getServerType() != null) {
						l.addAll(Activator.getDefault().getRemoteServerManager().getRunningTasks());
					}
					try{
						Thread.sleep(1000);
					}catch(Exception ex){
						
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
				Display.getDefault().asyncExec(new Runnable(){
					public void run(){
						getTableViewer().setInput(l);
					}
				});
				
			}
		}
	}
	
	private Monitor monitor;
	@Override
	protected void createToolBar(Composite parent) {
		ToolBar bar = new ToolBar(parent, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		final ToolItem i = new ToolItem(bar, SWT.CHECK);
		i.setText(Messages.RunningView_0);
		i.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (i.getSelection()){
					monitor = new Monitor();
					monitor.start();
				}
				else{
					monitor.active = false;
					try {
						monitor.join();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					monitor = null;
				}
			}
		});
	}
}
