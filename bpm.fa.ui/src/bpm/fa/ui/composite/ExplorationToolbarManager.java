package bpm.fa.ui.composite;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.fa.ui.Activator;
import bpm.fa.ui.Messages;
import bpm.fa.ui.dialogs.DialogChart;
import bpm.fa.ui.dialogs.DialogMdx;
import bpm.fa.ui.icons.Icons;
import bpm.fa.ui.ktable.CubeModel;
import de.kupzog.ktable.KTable;

public class ExplorationToolbarManager {
	class SwapAxes extends Action{
		public SwapAxes(){super(Messages.ExplorationToolbarManager_0);}
		public void run(){
			BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
				
				@Override
				public void run() {
					getCubeModel().getOLAPCube().swapAxes();
					getCubeModel().performQuery();
					table.redraw();
					table.getParent().layout(true);
					
				}
			});
			
			
			
		}
	}
	
	class ShowHideTotals extends Action{
		private boolean show = true;
		public ShowHideTotals(){super(Messages.ExplorationToolbarManager_1);}
		public void run(){
			BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
				@Override
				public void run() {
					show = !show;
					getCubeModel().getOLAPCube().setShowTotals(show);
					getCubeModel().refreshResult();
					table.redraw();
					table.getParent().layout(true);
					
				}
			});
			
		}
	}
	
	class ShowHideEmpty extends Action{
		private boolean show = true;
		public ShowHideEmpty(){super(Messages.ExplorationToolbarManager_2);}
		public void run(){
			BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
				
				@Override
				public void run() {
					show = !show;
					getCubeModel().getOLAPCube().setshowEmpty(show);
					getCubeModel().performQuery();
					table.redraw();
					table.getParent().layout(true);
					
				}
			});

		}
	}
	
	
	class QueryActive extends Action{
		private boolean active = true;
		public QueryActive(){super(Messages.ExplorationToolbarManager_3);}
		public void run(){
			BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
				
				@Override
				public void run() {
					active = !active;
					getCubeModel().getOLAPCube().setStateActive(active);
					getCubeModel().performQuery();
					table.redraw();
					table.getParent().layout(true);
					
				}
			});

		}
	}
	
	class ResetInitialState extends Action{
		public ResetInitialState(){super(Messages.ExplorationToolbarManager_4);}
		public void run(){
			BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
				
				@Override
				public void run() {
					getCubeModel().getOLAPCube().restore();
					getCubeModel().performQuery();
					table.redraw();
					table.getParent().layout(true);
					
				}
			});

		}
	}
	
	class Undo extends Action{
		public Undo(){super(Messages.ExplorationToolbarManager_5);}
		public void run(){
			BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
				
				@Override
				public void run() {
					getCubeModel().getOLAPCube().undo();
					getCubeModel().performQuery();
					table.redraw();
					table.getParent().layout(true);
				}
			});

		}
	}
	
	class Redo extends Action{
		public Redo(){super(Messages.ExplorationToolbarManager_6);}
		public void run(){
			BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
				
				@Override
				public void run() {
					getCubeModel().getOLAPCube().redo();
					getCubeModel().performQuery();
					table.redraw();
					table.getParent().layout(true);
				}
			});

		}
	}
	
	class ShowHideProperties extends Action{
		private boolean show = true;
		public ShowHideProperties(){super(Messages.ExplorationToolbarManager_7);}
		public void run(){
			show = !show;
			getCubeModel().getOLAPCube().setshowProperties(show);
			getCubeModel().performQuery();
			BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
				@Override
				public void run() {
					
					
					table.redraw();
					table.getParent().layout(true);
					
				}
			});
			
		}
	}

	class SetMdx extends Action{
		public SetMdx(){super(Messages.ExplorationToolbarManager_8);}
		public void run(){
			
			try{
				DialogMdx d = new DialogMdx(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), 
						getCubeModel().getOLAPCube().getMdx().getMDX());
				if (d.open() == DialogMdx.OK){
					getCubeModel().performQuery(d.getMdx());
					table.redraw();
					table.getParent().layout(true);
				}
				
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openWarning(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.ExplorationToolbarManager_9, Messages.ExplorationToolbarManager_10 + ex.getMessage());
			}
			
			
		}
	}
	class Chart extends Action{
		public Chart(){super(Messages.ExplorationToolbarManager_11);}
		public void run(){
			DialogChart d = new DialogChart(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), getCubeModel());
			d.open();
		}
	}
	
	private KTable table;
	private Action showTotal, swapAxes, showEmpty, reset, undo, redo, queryActive, showProperties, chart, mdx;
	private ToolItem itTotal, itSwap, itEmpty, itReset, itUndo, itRedo,itActive , itProperties, itChart, itMdx;

	
	
	private CubeModel getCubeModel(){
		return (CubeModel)table.getModel();
	}
	
	public ExplorationToolbarManager(){
		
		
		
		swapAxes = new SwapAxes();
		showTotal = new ShowHideTotals();
		showEmpty = new ShowHideEmpty();
		showProperties = new ShowHideProperties();
		queryActive = new QueryActive();
		reset = new ResetInitialState();
		undo = new Undo();
		redo = new Redo();
		chart = new Chart();
		mdx = new SetMdx();
	}
	
	public void init(KTable table){
		this.table = table;
	}

	public void initToolBar() {
		itTotal.setSelection(true);
		itEmpty.setSelection(true);
		itActive.setSelection(true);
		
		itChart.setSelection(false);
		itMdx.setSelection(false);
		itProperties.setSelection(false);
		itRedo.setSelection(false);
		itReset.setSelection(false);
		itSwap.setSelection(false);
		itUndo.setSelection(false);
	}
	
	public Control createToolbar(Composite parent) {
		ToolBar tb = new ToolBar(parent, SWT.HORIZONTAL);
		
		
		itReset = new ToolItem(tb, SWT.PUSH);
		itReset.setToolTipText("Refresh");
		itReset.setImage(Activator.getDefault().getImageRegistry().get(Icons.RESET));
		itReset.addSelectionListener(new SelectionAdapter(){@Override
		public void widgetSelected(SelectionEvent e) {
			reset.run(); 
			
		}});
		
		itUndo = new ToolItem(tb, SWT.PUSH);
		itUndo.setToolTipText(itUndo.getText());
		itUndo.setImage(Activator.getDefault().getImageRegistry().get(Icons.UNDO));
		itUndo.addSelectionListener(new SelectionAdapter(){@Override
		public void widgetSelected(SelectionEvent e) {
			undo.run(); 
			
		}});
		
		itRedo = new ToolItem(tb, SWT.PUSH);
		itRedo.setToolTipText(itRedo.getText());
		itRedo.setImage(Activator.getDefault().getImageRegistry().get(Icons.REDO));
		itRedo.addSelectionListener(new SelectionAdapter(){@Override
		public void widgetSelected(SelectionEvent e) {
			redo.run(); 
			
		}});
		
		itSwap = new ToolItem(tb, SWT.PUSH);
		itSwap.setToolTipText(swapAxes.getText());
		itSwap.setImage(Activator.getDefault().getImageRegistry().get(Icons.SWAP));
		itSwap.addSelectionListener(new SelectionAdapter(){@Override
		public void widgetSelected(SelectionEvent e) {
			swapAxes.run(); 
			
		}});
		
		itTotal = new ToolItem(tb, SWT.CHECK);
		itTotal.setToolTipText(Messages.ExplorationToolbarManager_12);
		itTotal.setSelection(true);
		itTotal.setImage(Activator.getDefault().getImageRegistry().get(Icons.TOTAL_SHOW));
		itTotal.addSelectionListener(new SelectionAdapter(){@Override
		public void widgetSelected(SelectionEvent e) {
			showTotal.run(); 
			itTotal.setSelection(getCubeModel().getOLAPCube().isShowTotals());
			
			if (itTotal.getSelection()){
				itTotal.setImage(Activator.getDefault().getImageRegistry().get(Icons.TOTAL_HIDE));
				itTotal.setToolTipText(Messages.ExplorationToolbarManager_13);
			}else{
				itTotal.setImage(Activator.getDefault().getImageRegistry().get(Icons.TOTAL_SHOW));
				itTotal.setToolTipText(Messages.ExplorationToolbarManager_14);
			}
			
		}});
		
		
		itEmpty = new ToolItem(tb, SWT.CHECK);
		itEmpty.setToolTipText(Messages.ExplorationToolbarManager_15);
		itEmpty.setImage(Activator.getDefault().getImageRegistry().get(Icons.EMPTY_ON));
		itEmpty.setSelection(true);
		itEmpty.addSelectionListener(new SelectionAdapter(){@Override
		public void widgetSelected(SelectionEvent e) {
			showEmpty.run(); 
			itEmpty.setSelection(getCubeModel().getOLAPCube().getShowEmpty());
			
			if (itEmpty.getSelection()){
				itEmpty.setImage(Activator.getDefault().getImageRegistry().get(Icons.EMPTY_ON));
				itEmpty.setToolTipText(Messages.ExplorationToolbarManager_17);
			}
			else{
				itEmpty.setImage(Activator.getDefault().getImageRegistry().get(Icons.EMPTY_OFF));
				itEmpty.setToolTipText(Messages.ExplorationToolbarManager_16);
			}
			
		}});
		
		itProperties = new ToolItem(tb, SWT.CHECK);
		itProperties.setToolTipText(Messages.ExplorationToolbarManager_18);
		itProperties.setImage(Activator.getDefault().getImageRegistry().get(Icons.PROPERTIES_SHOW));
		itProperties.setSelection(false);
		itProperties.addSelectionListener(new SelectionAdapter(){@Override
		public void widgetSelected(SelectionEvent e) {
			showProperties.run(); 
			itProperties.setSelection(getCubeModel().getOLAPCube().getShowProperties());
			
			if (itProperties.getSelection()){
				itProperties.setImage(Activator.getDefault().getImageRegistry().get(Icons.PROPERTIES_HIDE));
				itProperties.setToolTipText(Messages.ExplorationToolbarManager_19);
			}
			else{
				itProperties.setImage(Activator.getDefault().getImageRegistry().get(Icons.PROPERTIES_SHOW));
				itProperties.setToolTipText(Messages.ExplorationToolbarManager_20);
			}
			
		}});
		
		itActive = new ToolItem(tb, SWT.CHECK);
		itActive.setSelection(true);
		itActive.setToolTipText(Messages.ExplorationToolbarManager_21);
		itActive.setImage(Activator.getDefault().getImageRegistry().get(Icons.QUERY_ON));
		itActive.addSelectionListener(new SelectionAdapter(){@Override
		public void widgetSelected(SelectionEvent e) {
			queryActive.run(); 
			itActive.setSelection(getCubeModel().getOLAPCube().getStateActive());
			
			if (itActive.getSelection()){
				itActive.setImage(Activator.getDefault().getImageRegistry().get(Icons.QUERY_ON));
				itActive.setToolTipText(Messages.ExplorationToolbarManager_22);
			}
			else{
				itActive.setImage(Activator.getDefault().getImageRegistry().get(Icons.QUERY_OFF));
				itActive.setToolTipText(Messages.ExplorationToolbarManager_23);
			}
			
		}});

		
		
		itMdx = new ToolItem(tb, SWT.PUSH);
		itMdx.setToolTipText(Messages.ExplorationToolbarManager_24);
		itMdx.setImage(Activator.getDefault().getImageRegistry().get(Icons.MDX));
		itMdx.addSelectionListener(new SelectionAdapter(){@Override
		public void widgetSelected(SelectionEvent e) {
			mdx.run(); 
		}
		});
		
		itChart = new ToolItem(tb, SWT.PUSH);
		itChart.setToolTipText(Messages.ExplorationToolbarManager_25);
		itChart.setImage(Activator.getDefault().getImageRegistry().get(Icons.CHART));
		itChart.addSelectionListener(new SelectionAdapter(){@Override
		public void widgetSelected(SelectionEvent e) {
			chart.run(); 
		}
		});

		
		return tb;
	}
	
	
}
