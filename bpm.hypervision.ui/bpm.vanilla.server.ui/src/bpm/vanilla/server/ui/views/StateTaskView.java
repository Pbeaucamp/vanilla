package bpm.vanilla.server.ui.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.ViewPart;

import bpm.vanilla.server.client.ServerType;
import bpm.vanilla.server.client.communicators.TaskInfo;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.icons.Icons;
import bpm.vanilla.server.ui.views.actions.gateway.ActionStepsInfos;

public class StateTaskView extends ViewPart {
	private TableViewer viewer ;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdfLight = new SimpleDateFormat("HH:mm:ss,SSS");
	
	private HashMap<Integer, TaskInfo> tasks = new HashMap<Integer, TaskInfo>();
	
	private static final int COL_ID = 0;
	private static final int COL_PRIORITY = 1;
	private static final int COL_STATE = 2;
	private static final int COL_RESULT = 3;
	private static final int COL_FAILURE = 4;
	private static final int COL_START = 5;
	private static final int COL_CREATED = 6;
	private static final int COL_STOPED = 7;
	private static final int COL_ELASPED = 8;
	private static final int COL_DURATION = 9;
	private static final int COL_GROUP = 10;
	
	private class InternalColComparator extends ViewerComparator{

		int columIndex = COL_ID;
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (e1 != null && e2 != null){
				switch(columIndex){
				case COL_ID:
					return ((Integer)e1).compareTo((Integer)e2);
				case COL_PRIORITY:
					return super.compare(viewer, tasks.get(((Integer)e1)).getPriority(), tasks.get(((Integer)e2)).getPriority());
				case COL_CREATED:
					return super.compare(viewer, tasks.get(((Integer)e1)).getCreationDate(), tasks.get(((Integer)e2)).getCreationDate());
				case COL_DURATION:
					return super.compare(viewer, tasks.get(((Integer)e1)).getDurationTime(), tasks.get(((Integer)e2)).getDurationTime());
				case COL_ELASPED:
					return super.compare(viewer, tasks.get(((Integer)e1)).getElapsedTime(), tasks.get(((Integer)e2)).getElapsedTime());
				case COL_FAILURE:
					return super.compare(viewer, tasks.get(((Integer)e1)).getFailureCause(), tasks.get(((Integer)e2)).getFailureCause());
				case COL_RESULT:
					return super.compare(viewer, tasks.get(((Integer)e1)).getResult(), tasks.get(((Integer)e2)).getResult());
				case COL_START:
					return super.compare(viewer, tasks.get(((Integer)e1)).getStartedDate(), tasks.get(((Integer)e2)).getStartedDate());
				case COL_STATE:
					return super.compare(viewer, tasks.get(((Integer)e1)).getState() , tasks.get(((Integer)e2)).getState());
				case COL_STOPED:
					return super.compare(viewer, tasks.get(((Integer)e1)).getStoppedDate(), tasks.get(((Integer)e2)).getStoppedDate());
				case COL_GROUP:
					return super.compare(viewer, tasks.get(((Integer)e1)).getGroupName(), tasks.get(((Integer)e2)).getGroupName());

				}
			}
			return super.compare(viewer, e1, e2);
		}

		
		
	}
	
	protected InternalColComparator comparator = new InternalColComparator();
	protected class Monitor extends Thread{
		int refresh = 5000;
		String servetUrl;
		boolean active = true;
		public void run(){
			while(active){
				
				if (Activator.getDefault().getServerRemote() == null){
					active = false;
					tasks.clear();
					Display.getDefault().asyncExec(new Runnable(){
						public void run(){
							refreshItem.setSelection(false);
							viewer.refresh();
						}
					});
					
					
				}
				
				final List<TaskInfo> l = new ArrayList<TaskInfo>();
				try{
					l.addAll(Activator.getDefault().getServerRemote().getTaskInfos());
				
					
					Integer key = null;
					for(TaskInfo t : l){
						boolean found = false;
						for(Integer k : tasks.keySet()){
							if (t.getId().equals(k.toString())){
								tasks.put(k, t);
								found = true;
								break;
							}
						}
						if (! found){
							tasks.put(Integer.parseInt(t.getId()), t);
						}
					}
					
					List<Integer> toRemove = new ArrayList<Integer>();
					
					for(Integer i : tasks.keySet()){
						boolean found = false;
						for(TaskInfo ti : l ){
							if (Integer.parseInt(ti.getId()) == i.intValue()){
								found = true;
								break;
							}
						}
						if (! found){
							toRemove.add(i);
						}
					}
					
					
					for(Integer i : toRemove){
						tasks.remove(i);
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
						viewer.refresh();

					}
				});
				
			}
		}
	}
	
	private Monitor monitor;
	
	private ToolItem refreshItem;
	public StateTaskView() {
		
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createToolBar(main);
		createViewer(main);
		createContextMenu();
		
		getSite().setSelectionProvider(viewer);
	}
	
	private void createContextMenu(){
		MenuManager mgr = new MenuManager("Actions", "bpm.vanilla.server.ui.views.StateTaskView.contextMenu");

		mgr.add(new ActionStepsInfos());
		mgr.add(new Separator());
		mgr.addMenuListener(new IMenuListener() {
			
			public void menuAboutToShow(IMenuManager manager) {
				
				for(IContributionItem i : manager.getItems()){
					if (VisualConstants.ACTION_GATEWAY_STEPS_INFOS_ID.equals(i.getId()) ){
						i.setVisible(Activator.getDefault().getServerRemote() != null & Activator.getDefault().getServerRemote().getServerType() == ServerType.GATEWAY);
					}
				}
				
				
			}
		});
		
		viewer.getTable().setMenu(mgr.createContextMenu(viewer.getControl()));
		getSite().registerContextMenu("bpm.vanilla.server.ui.views.StateTaskView.contextMenu", mgr, viewer);
		
	}
	
	
	private void createViewer(Composite parent){
		viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				
				return tasks.keySet().toArray(new Integer[tasks.keySet().size()]);
			}
		});
		
		TableViewerColumn id = new TableViewerColumn(viewer, SWT.NONE);
		id.getColumn().setText("Task Id");
		id.getColumn().setWidth(50);
		id.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				return tasks.get(((Integer)element)).getId();
			}
			
		});
		
		
		TableViewerColumn priority = new TableViewerColumn(viewer, SWT.NONE);
		priority.getColumn().setText("Priority");
		priority.getColumn().setWidth(50);
		priority.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				return tasks.get(((Integer)element)).getPriority()+"";
			}
			
		});
		

		
		TableViewerColumn state = new TableViewerColumn(viewer, SWT.NONE);
		state.getColumn().setText("Task State");
		state.getColumn().setWidth(100);
		state.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				switch(tasks.get(((Integer)element)).getState()){
				case TaskInfo.STATE_ENDED:
					return "Finished";
				case TaskInfo.STATE_RUNNING:
					return "Running";
				case TaskInfo.STATE_WAITING:	
					return "Waiting";
				default:
					return "Unknown";	
				}
			}
			
		});
		
		
		TableViewerColumn result = new TableViewerColumn(viewer, SWT.NONE);
		result.getColumn().setText("Task result");
		result.getColumn().setWidth(100);
		result.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				switch(tasks.get(((Integer)element)).getResult()){
				case TaskInfo.RESULT_FAILED:
					return "Failed";
				case TaskInfo.RESULT_SUCCEED:
					return "Succeeded";
				case TaskInfo.RESULT_UNDEFINED:	
					return "Undefined";
				default:
					return "Unknown";	
				}
			}
			
		});
		
		TableViewerColumn failure = new TableViewerColumn(viewer, SWT.NONE);
		failure.getColumn().setText("Failure cause");
		failure.getColumn().setWidth(100);
		failure.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				if (tasks.get(((Integer)element)).getFailureCause() != null){
					return tasks.get(((Integer)element)).getFailureCause();
				}
				return "";
			}
			
		});
		
		
		TableViewerColumn start = new TableViewerColumn(viewer, SWT.NONE);
		start.getColumn().setText("Started At");
		start.getColumn().setWidth(100);
		start.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {

				try{
					return sdf.format(tasks.get(((Integer)element)).getStartedDate());
				}catch(Exception ex){
					return "";
				}
				
			}
			
		});
		
		TableViewerColumn created = new TableViewerColumn(viewer, SWT.NONE);
		created.getColumn().setText("Created At");
		created.getColumn().setWidth(100);
		created.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				try{
					return sdf.format(tasks.get(((Integer)element)).getCreationDate());
				}catch(Exception ex){
					return "";
				}
				
			}
			
		});
		
		TableViewerColumn stoped = new TableViewerColumn(viewer, SWT.NONE);
		stoped.getColumn().setText("Stopped At");
		stoped.getColumn().setWidth(100);
		stoped.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				try{
					return sdf.format(tasks.get(((Integer)element)).getStoppedDate());
				}catch(Exception ex){
					return "";
				}
				
			}
			
		});
		
		TableViewerColumn elapsed = new TableViewerColumn(viewer, SWT.NONE);
		elapsed.getColumn().setText("ElapsedTime");
		elapsed.getColumn().setWidth(100);
		elapsed.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				try{
					return sdfLight.format(tasks.get(((Integer)element)).getElapsedTime());
				}catch(Exception ex){
					return "";
				}
			}
			
		});
		
		TableViewerColumn duration = new TableViewerColumn(viewer, SWT.NONE);
		duration.getColumn().setText("Total Runtime Time");
		duration.getColumn().setWidth(100);
		duration.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				try{
					return sdfLight.format(tasks.get(((Integer)element)).getDurationTime());
				}catch(Exception ex){
					return "";
				}
				
			}
			
		});
		
		TableViewerColumn group = new TableViewerColumn(viewer, SWT.NONE);
		group.getColumn().setText("Profil");
		group.getColumn().setWidth(100);
		group.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				try{
					return tasks.get(((Integer)element)).getGroupName() + "";
				}catch(Exception ex){
					return "";
				}
				
			}
			
		});
		
		viewer.setComparator(comparator);
		viewer.setInput(tasks);
		
		 
		
		for(TableColumn col : viewer.getTable().getColumns()){
			col.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
					
					
				}

				public void widgetSelected(SelectionEvent e) {
					comparator.columIndex = viewer.getTable().indexOf((TableColumn)e.widget);
					viewer.refresh();
				}
				
			});
		}
		
	}

	@Override
	public void setFocus() {
		

	}
	
	
	protected void createToolBar(Composite parent) {
		ToolBar bar = new ToolBar(parent, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		refreshItem = new ToolItem(bar, SWT.CHECK);
		refreshItem.setToolTipText("Refresh");
		refreshItem.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				tasks.clear();
				viewer.refresh();
				
				if (refreshItem.getSelection()){
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
		refreshItem.setImage(Activator.getDefault().getImageRegistry().get(Icons.REFRESH));
		
	

	}

	public TaskInfo getTaskInfo(Integer i){
		return tasks.get(i);
	}
}
