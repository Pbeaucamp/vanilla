package bpm.vanilla.server.client.ui.clustering.menu.views.massreport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.MassReportState;
import bpm.vanilla.platform.core.beans.WorkflowRunInstance;
import bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState;
import bpm.vanilla.platform.core.remote.impl.components.RemoteWorkflowComponent;
import bpm.vanilla.server.client.ui.clustering.menu.Activator;
import bpm.vanilla.server.client.ui.clustering.menu.Messages;
import bpm.vanilla.server.client.ui.clustering.menu.icons.Icons;

public class MassReportView extends ViewPart{
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); //$NON-NLS-1$
	public static final String ID = "bpm.vanilla.server.client.ui.clustering.menu.views.massreport.MassReportView"; //$NON-NLS-1$
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	
	private TableViewer workflowViewer;
	private TableViewer reportViewer;
	
	private class WkfItem{
		WorkflowRunInstance  instance;
		WorkflowInstanceState wkfState;
		public WkfItem(WorkflowRunInstance  instance, WorkflowInstanceState wkfState){
			this.instance = instance;
			this.wkfState = wkfState;
		}
	}
	
	
	@Override
	public void createPartControl(Composite parent) {
		Form frm = formToolkit.createForm(parent);
		frm.setImage(Activator.getDefault().getImageRegistry().get(Icons.MASS_REPORT));
		formToolkit.paintBordersFor(frm);
		frm.setText(Messages.MassReportView_2);
		frm.getBody().setLayout(new GridLayout(1, false));
		formToolkit.decorateFormHeading(frm);
		
		Composite composite = new Composite(frm.getBody(), SWT.NONE);
		composite.setLayout(new GridLayout(2, true));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.adapt(composite);
		formToolkit.paintBordersFor(composite);

		Button refresh = formToolkit.createButton(composite, "", SWT.PUSH); //$NON-NLS-1$
		refresh.setToolTipText(Messages.MassReportView_4);
		refresh.setLayoutData(new GridData());
		refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
		});
		refresh.setImage(Activator.getDefault().getImageRegistry().get(Icons.RESET));
		
		refresh = formToolkit.createButton(composite, "", SWT.PUSH); //$NON-NLS-1$
		refresh.setToolTipText(Messages.MassReportView_6);
		refresh.setLayoutData(new GridData());
		refresh.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshDetails();
			}
		});
		refresh.setImage(Activator.getDefault().getImageRegistry().get(Icons.RESET));
		
		createWorkflowViewer(composite);
		createReportViewer(composite);
		
	}
	
	private void createWorkflowViewer(Composite parent){
		workflowViewer = new TableViewer(formToolkit.createTable(parent, SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.V_SCROLL | SWT.BORDER));
		workflowViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		workflowViewer.getTable().setHeaderVisible(true);
		workflowViewer.getTable().setLinesVisible(true);
		workflowViewer.setContentProvider(new ArrayContentProvider());
		
		TableViewerColumn col = new TableViewerColumn(workflowViewer, SWT.NONE);
		col.getColumn().setText(Messages.MassReportView_7);
		col.getColumn().setWidth(150);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return RepositoryLabelHelper.getModelName((((WkfItem)element)).instance.getWorkflowId());
			}
		});
		
		
		col = new TableViewerColumn(workflowViewer, SWT.NONE);
		col.getColumn().setText(Messages.MassReportView_8);
		col.getColumn().setWidth(150);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if ((WkfItem)element == null){
					return "?"; //$NON-NLS-1$
				}
				else{
					try{
						return sdf.format(((WkfItem)element).wkfState.getStartedDate());
					}catch(Exception e){
						return Messages.MassReportView_10;
					}
				}
			}
		});
		
		col = new TableViewerColumn(workflowViewer, SWT.NONE);
		col.getColumn().setText(Messages.MassReportView_11);
		col.getColumn().setWidth(150);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if ((WkfItem)element == null){
					return "?"; //$NON-NLS-1$
				}
				else{
					
					try{
						Date d = ((WkfItem)element).wkfState.getStoppedDate();
						return sdf.format(d);
					}catch(Exception ex){
						return Messages.MassReportView_13;
					}
					
				}
			
			}
		});
		
		workflowViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				refreshDetails();
				
				
			}
		});
		
		createWorkflowMenu();
	}
	
	private void createWorkflowMenu(){
		MenuManager mgr = new MenuManager();
		mgr.add(new Action(Messages.MassReportView_14){
			public void run(){
				try{
					List<WorkflowRunInstance> l = new ArrayList<WorkflowRunInstance>();
					for(WkfItem i : (List<WkfItem>)workflowViewer.getInput()){
						l.add(i.instance);
					}
					Activator.getDefault().getVanillaApi().getMassReportMonitor().delete(l);
					refresh();
				}catch(Exception ex){
					
				}
			}
		});
		
		final Action de = new Action(Messages.MassReportView_15){
			public void run(){
				try{
					List<WorkflowRunInstance> l = new ArrayList<WorkflowRunInstance>();
					for(WkfItem i : (List<WkfItem>)((IStructuredSelection)workflowViewer.getSelection()).toList()){
						l.add(i.instance);
					}
					Activator.getDefault().getVanillaApi().getMassReportMonitor().delete(l);
					refresh();
				}catch(Exception ex){
					
				}
			}
		};
		mgr.add(de);
		mgr.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				de.setEnabled(!workflowViewer.getSelection().isEmpty());
				
			}
		});
		
		workflowViewer.getTable().setMenu(mgr.createContextMenu(workflowViewer.getTable()));
	}
	
	private void refresh(){
		BusyIndicator.showWhile(Display.getDefault(), new Runnable(){
			public void run(){
				try {
					List<WorkflowRunInstance> l = Activator.getDefault().getVanillaApi().getMassReportMonitor().getWorklowsUsingMassReporting();
					List res = new ArrayList(l.size());
					RemoteWorkflowComponent remote = new RemoteWorkflowComponent(Activator.getDefault().getVanillaContext());
					
					for(WorkflowRunInstance  i : l){
//						WorkflowInstanceState st = null; 
//						try{
//							st = remote.getState(new SimpleRunIdentifier(i.getProcessInstanceUuid()));
//						}catch(Exception ex){
//							
//						}
						res.add(new WkfItem(i, i.getState()));
					}
					workflowViewer.setInput(res);
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
			}
		});
		
	}
	
	private void refreshDetails(){
		BusyIndicator.showWhile(Display.getDefault(), new Runnable(){
			public void run(){
				IStructuredSelection ss = (IStructuredSelection)workflowViewer.getSelection();
				if (ss.isEmpty()){
					reportViewer.setInput(Collections.EMPTY_LIST);
				}
				else{
					WkfItem item = (WkfItem)ss.getFirstElement();
					
					WorkflowRunInstance run = item.instance;
					List<MassReportState> states = new ArrayList<MassReportState>();
					for(IObjectIdentifier reportId : run.getReportsIdentifier()){
						try {
							states.add(Activator.getDefault().getVanillaApi().getMassReportMonitor().getMassReportState(run.getWorkflowId(), reportId, run.getProcessInstanceUuid()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					reportViewer.setInput(states);
				}
			}
		});
		
	}
	
	private void createReportViewer(Composite parent){
		reportViewer = new TableViewer(formToolkit.createTable(parent, SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.V_SCROLL | SWT.BORDER));
		reportViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		reportViewer.getTable().setHeaderVisible(true);
		reportViewer.getTable().setLinesVisible(true);
		reportViewer.setContentProvider(new ArrayContentProvider());
		
		TableViewerColumn col = new TableViewerColumn(reportViewer, SWT.NONE);
		col.getColumn().setText(Messages.MassReportView_16);
		col.getColumn().setWidth(150);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				
				return RepositoryLabelHelper.getModelName(((MassReportState)element).getReportIdentifier());
			}
		});
		
		
		col = new TableViewerColumn(reportViewer, SWT.NONE);
		col.getColumn().setText(Messages.MassReportView_17);
		col.getColumn().setWidth(150);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return "" + ((MassReportState)element).getReportTaskNumber(); //$NON-NLS-1$
				
			}
		});
		
		col = new TableViewerColumn(reportViewer, SWT.NONE);
		col.getColumn().setText(Messages.MassReportView_19);
		col.getColumn().setWidth(150);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return "" + ((MassReportState)element).getReportGenerationNumber(); //$NON-NLS-1$
				
			}
		});
	}

	@Override
	public void setFocus() {
		
		
	}

}
