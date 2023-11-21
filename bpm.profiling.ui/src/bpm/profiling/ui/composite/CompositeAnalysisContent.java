package bpm.profiling.ui.composite;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.profiling.database.bean.AnalysisContentBean;
import bpm.profiling.database.bean.AnalysisInfoBean;
import bpm.profiling.database.bean.RuleSetBean;
import bpm.profiling.runtime.core.Column;
import bpm.profiling.runtime.core.Connection;
import bpm.profiling.runtime.core.Table;
import bpm.profiling.ui.Activator;
import bpm.profiling.ui.dialogs.DialogCondition;
import bpm.profiling.ui.dialogs.DialogRuleSet;

public class CompositeAnalysisContent extends Composite {


	private CheckboxTreeViewer columns;
	
	private AnalysisInfoBean infos;
	
	private boolean hasChanged = false;;
	
	private ToolItem conditionIt, ruleSetIt;
	private ToolItem deleteIt;
	
	private Action updateRuleSet;
	
	public CompositeAnalysisContent(Composite parent, int style) {
		super(parent, style);
		
		this.setLayout(new GridLayout(2, false));
		
		buildContent();
		createAction();
		createContextMenu();
	}
	
	
	public boolean hasChanged(){
		return hasChanged;
	}
	
	
	
	public void setContent(AnalysisInfoBean analysisInfo){
		
		
		this.infos = analysisInfo;
		hasChanged = false;
		if (analysisInfo == null){
			columns.setInput(new Connection());
			return;
		}
		
		
		Connection c = null;
		try {
			c = Activator.getDefault().getConnection(infos.getConnectionId());
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Error when creatin content", e.getMessage());
		}
		
		
		if (c != null){
			columns.setInput(c);
			
			
			for(AnalysisContentBean content : Activator.helper.getAnalysisManager().getAllAnalysisContentFor(analysisInfo)){
				
				for(Table t : c.getTables()){
					if (t.getLabel().equals(content.getTableName())){
						
						for(Column col : t.getColumns()){
							if (col.getLabel().equals(content.getColumnName())){
								columns.setChecked(col, true);	
								columns.setExpandedState(col.getTable(), true);
								columns.setGrayChecked(col.getTable(), true);
							}
						}
						
					}
				}
				
				
				
			}
			
			
		}
		else{
			columns.setInput(null);
		}
	
	}
	
	
	public List<Column> getCheckedColumns(){
		List<Column> l = new ArrayList<Column>();
		
		for(Object o : columns.getCheckedElements()){
			if (o instanceof Column){
				l.add((Column)o);
			}
		}

		return l;
	}
	
	
	private void buildContent(){
		ToolBar bar = new ToolBar(this, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		ruleSetIt = new ToolItem(bar, SWT.PUSH);
		ruleSetIt.setToolTipText("createRuleSet");
		ruleSetIt.setImage(Activator.getDefault().getImageRegistry().get("ruleSet"));
		ruleSetIt.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				IStructuredSelection ss = (IStructuredSelection)columns.getSelection();
				if (ss.getFirstElement() instanceof Column && columns.getChecked(ss.getFirstElement())){

					DialogRuleSet d = new DialogRuleSet(getShell());
					if (d.open() == DialogRuleSet.OK){
						try {
							Activator.helper.getAnalysisManager().createRuleSet((Column)ss.getFirstElement(), infos, d.getRuleSetBean());
							columns.refresh();
						} catch (Exception e1) {
							e1.printStackTrace();
							MessageDialog.openWarning(getShell(), "Unable to create RuleSet", e1.getMessage());
						}
						
					}
				}
					
			}
			
		});
		ruleSetIt.setEnabled(false);

		conditionIt = new ToolItem(bar, SWT.PUSH);
		conditionIt.setToolTipText("createConditions");
		conditionIt.setImage(Activator.getDefault().getImageRegistry().get("condition"));
		conditionIt.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				IStructuredSelection ss = (IStructuredSelection)columns.getSelection();
				if (ss.getFirstElement() instanceof RuleSetBean){

					RuleSetBean ruleSet = (RuleSetBean)ss.getFirstElement();
					
					DialogCondition d = new DialogCondition(getShell(), ruleSet);
					if (d.open() == DialogCondition.OK){
						
					}
				}
					
			}

		});
		conditionIt.setEnabled(false);
		
		deleteIt = new ToolItem(bar, SWT.PUSH);
		deleteIt.setToolTipText("delete");
		deleteIt.setImage(Activator.getDefault().getImageRegistry().get("delete"));
		deleteIt.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				IStructuredSelection ss = (IStructuredSelection)columns.getSelection();
				if (ss.getFirstElement() instanceof RuleSetBean){

					RuleSetBean ruleSet = (RuleSetBean)ss.getFirstElement();
					Activator.helper.getAnalysisManager().deleteRuleSet(ruleSet);
					setContent(infos);					
				}
				
					
			}

		});
		deleteIt.setEnabled(false);
		
		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		l2.setText("Columns");
		
		columns = new CheckboxTreeViewer(this, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		columns.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		columns.setContentProvider(new TreeContentProvider());
		columns.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				Object o = ((IStructuredSelection)columns.getSelection()).getFirstElement();
				
				if (o instanceof RuleSetBean){
					conditionIt.setEnabled(true);
					deleteIt.setEnabled(true);
					return;
				}
				else if (o instanceof Column){
					
					ruleSetIt.setEnabled(columns.getChecked(o));
				}
				deleteIt.setEnabled(false);
			}
			
		});
		
		
		TreeViewerColumn fields = new TreeViewerColumn(columns, SWT.NONE);
		fields.getColumn().setText("Fields");
		fields.getColumn().setWidth(200);
		fields.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof Table){
					return ((Table)element).getName();
				}else if (element instanceof Column){
					return ((Column)element).getName();
				}
				return "";
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof Table){
					return Activator.getDefault().getImageRegistry().get("table");
				}else if (element instanceof Column){
					return Activator.getDefault().getImageRegistry().get("column");
				}
				return null;
			}
			
			
		});
		
		
		TreeViewerColumn ruleSets = new TreeViewerColumn(columns, SWT.NONE);
		ruleSets.getColumn().setText("RuleSets");
		ruleSets.getColumn().setWidth(100);
		ruleSets.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof RuleSetBean){
					return ((RuleSetBean)element).getName();
				}
				return "";
			}
		});
		
		columns.getTree().setHeaderVisible(true);

		
		columns.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				hasChanged = true;
				if (event.getElement() instanceof Table){
					for(Column c : ((Table)event.getElement()).getColumns()){
						columns.setChecked(c, event.getChecked());
					}
					columns.setExpandedState(event.getElement(), true);
					
				}
				
				else if (event.getElement() instanceof Column){
					Column col = (Column)event.getElement();
					
					if (event.getChecked()){
						columns.setGrayChecked(col.getTable(), true);
						return;
					}
					else{
						for(Column c : col.getTable().getColumns()){
							if (columns.getChecked(c)){
								columns.setGrayChecked(col.getTable(), true);
								
								return;
							}
						}
					}
					columns.setGrayChecked(col.getTable(), false);
				}
				
			}
			
		});

	}
	
	
	private void createContextMenu() {
		final MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager mgr) {
            	IStructuredSelection ss =  (IStructuredSelection)columns.getSelection();
            	Object o = ss.getFirstElement();
            	
            	if (o instanceof RuleSetBean){
            		menuMgr.add(updateRuleSet);
            	}
            }
        });
        
        columns.getControl().setMenu(menuMgr.createContextMenu(columns.getControl()));
        
	}

	
	private void createAction(){
		updateRuleSet = new Action("Change RuleSet Defintion"){
			public void run(){
				IStructuredSelection ss =  (IStructuredSelection)columns.getSelection();
            	RuleSetBean o = (RuleSetBean)ss.getFirstElement();
            	
            	DialogRuleSet d = new DialogRuleSet(getShell(), o);
            	if (d.open() == DialogRuleSet.OK){
            		Activator.helper.getAnalysisManager().updateRuleSet(d.getRuleSetBean());
            	}
			}
		};
	}
	
	public class TreeContentProvider implements IStructuredContentProvider, ITreeContentProvider{

		public Object[] getElements(Object inputElement) {
			if (inputElement == null){
				return new Object[]{};
			}
			List<Table> t = null;
			
			if (((Connection)inputElement).getTables().isEmpty()){
				try {
					t = ((Connection)inputElement).connect();
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(getShell(), "Problem when getting tables from connection", e.getMessage());
					return null;
				}
			}
			else{
				t = ((Connection)inputElement).getTables();
			}
			return t.toArray(new Table[t.size()]);
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof Table){
				List l = ((Table)parentElement).getColumns();
				return l.toArray(new Object[l.size()]);
			}
			else if (parentElement instanceof Column){
				List l = Activator.helper.getAnalysisManager().getRuleSetsFor((Column)parentElement, infos);
				return l.toArray(new Object[l.size()]);
			}
			else if (parentElement instanceof RuleSetBean){
				//TODO
			}
			return null;
		}

		public Object getParent(Object element) {
			if (element instanceof Column){
				return ((Column)element).getTable();
			}
			else if (element instanceof RuleSetBean){
				
			}
//			else if (element instanceof RuleSet){
//				
//			}
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof Table){
				return ((Table)element).getColumns().size() > 0;
			}
			else if (element instanceof Column){
				return !Activator.helper.getAnalysisManager().getRuleSetsFor((Column)element, infos).isEmpty();
			}
			else if (element instanceof RuleSetBean){
				
			}
			return false;
		}
		
	}
}
