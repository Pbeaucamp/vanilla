package bpm.es.datasource.analyzer.ui.remapper;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import adminbirep.Activator;
import bpm.es.datasource.analyzer.remapper.FactoryRemapperPerformer;
import bpm.es.datasource.analyzer.remapper.ModelRemapper;
import bpm.es.datasource.analyzer.ui.Messages;
import bpm.es.datasource.analyzer.ui.icons.IconsNames;
import bpm.es.datasource.analyzer.ui.remapper.CompositeItemDetails.RenderType;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.viewers.RepositoryContentProvider;
import bpm.vanilla.repository.ui.viewers.RepositoryLabelProvider;
import bpm.vanilla.repository.ui.viewers.RepositoryViewerFilter;

public class CompositeRemapper {
	private Composite client;
	
	private CompositeItemDetails requestedItems;
	private CompositeItemDetails repacementItems;
	
	private TableViewer operations;
	
	private ToolItem add, del, perform;
	
	private ISelection currentRequestedSelection;
	private ISelection currentReplacementSelection;
	private RepositoryItem currentSelectedRepositoryItem;
	
	/**
	 * index 0 : the item that own the dependant Object
	 * key : the actual requested item
	 * index 1 : the new requested item once perform will be done 
	 */
	private HashMap<RepositoryItem, RepositoryItem[]> currentOperations = new HashMap<RepositoryItem, RepositoryItem[]>(); 
	
	/*
	 * listen the selection changes in requestedItems to filter
	 * the object types in replacementItems
	 * 
	 * it stores the currentSelection in currentRequestedSelection
	 */
	private ISelectionChangedListener selectionListener = new ISelectionChangedListener() {

		public void selectionChanged(SelectionChangedEvent event) {
			int type = -1;
			
			currentRequestedSelection = event.getSelection();
			
			try{
				Object o = ((IStructuredSelection)event.getSelection()).getFirstElement();
				type = ((RepositoryItem)((TreeNode)o).getValue()).getType();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			if (type != -1){
				repacementItems.setViewerFilter(new RepositoryViewerFilter(new int[]{type}, new int []{}));
			}
			else{
				repacementItems.setViewerFilter(null);
			}
			
			updateToolItemStates();
		}
		
		
	};
	
	
	private RepositoryLabelProvider  nodeLabelProvider =  new RepositoryLabelProvider(){
		@Override
		public String getText(Object element) {
			if (element instanceof TreeNode){
				return super.getText(((TreeNode)element).getValue());
			}
			return super.getText(element);
		}
		
		@Override
		public Image getImage(Object element) {
			if (element instanceof TreeNode){
				return super.getImage(((TreeNode)element).getValue());
			}
			return super.getImage(element);
		}
	};
	
	
	
	
	public Composite getClient(){
		return client;
	}
	
	
	public Composite createContent(Composite parent){
		client = new Composite(parent, SWT.NONE);
		client.setLayout(new FillLayout(SWT.VERTICAL));
		
		
		
		requestedItems = new CompositeItemDetails(Messages.CompositeRemapper_0, RenderType.TREE);
		requestedItems.createContent(client, new TreeNodeContentProvider(),nodeLabelProvider);

		
		
		repacementItems = new CompositeItemDetails(Messages.CompositeRemapper_1, RenderType.TREE);
		repacementItems.createContent(client, new RepositoryContentProvider(), new RepositoryLabelProvider());
		repacementItems.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				currentReplacementSelection = event.getSelection();
				updateToolItemStates();
			}
		});
		
		createOperationsUi(client);
		
		return client;
	}
	
	
	
	private void createOperationsUi(Composite parent){
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l.setText(Messages.CompositeRemapper_2);
		
		ToolBar toolbar = new ToolBar(c, SWT.VERTICAL);
		toolbar.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		
		add = new ToolItem(toolbar, SWT.PUSH);
		add.setToolTipText(Messages.CompositeRemapper_3);
		add.setEnabled(false);
		add.setImage(bpm.es.datasource.analyzer.ui.Activator.getDefault().getImageRegistry().get(IconsNames.ADD));
		add.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try{
					RepositoryItem itReplaced = (RepositoryItem)((TreeNode)((IStructuredSelection)currentRequestedSelection).getFirstElement()).getValue();

					RepositoryItem replacement = (RepositoryItem)((IStructuredSelection)currentReplacementSelection).getFirstElement();
			
					RepositoryItem ownerItem = currentSelectedRepositoryItem;
					
					if (((TreeNode)((IStructuredSelection)currentRequestedSelection).getFirstElement()).getParent() != null){
						if (((TreeNode)((IStructuredSelection)currentRequestedSelection).getFirstElement()).getParent().getValue() != null){
							ownerItem = (RepositoryItem)((TreeNode)((IStructuredSelection)currentRequestedSelection).getFirstElement()).getParent().getValue();	
						}
						
					}
					
					
					
					currentOperations.put(itReplaced, new RepositoryItem[]{ownerItem, replacement});
					operations.setInput(currentOperations);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				updateToolItemStates();
			}
		});
		
		
		del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.CompositeRemapper_4);
		del.setEnabled(false);
		del.setImage(bpm.es.datasource.analyzer.ui.Activator.getDefault().getImageRegistry().get(IconsNames.DEL));
		del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(Object o : ((IStructuredSelection)operations.getSelection()).toList()){
					currentOperations.remove(o);
				}
				operations.setInput(currentOperations);
				updateToolItemStates();
			}
		});
		
		
		perform = new ToolItem(toolbar, SWT.PUSH);
		perform.setToolTipText(Messages.CompositeRemapper_5);
		perform.setEnabled(false);
		perform.setImage(bpm.es.datasource.analyzer.ui.Activator.getDefault().getImageRegistry().get(IconsNames.PERFORM));
		perform.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				performUpdates();
				
			}
		});

		
		
		operations = new TableViewer(c, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		operations.getTable().setLinesVisible(true);
		operations.getTable().setHeaderVisible(true);
		operations.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		operations.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			
			public void dispose() {}
			
			public Object[] getElements(Object inputElement) {
				Set s = currentOperations.keySet();
				return s.toArray(new Object[s.size()]);
			}
		});
		operations.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				updateToolItemStates();
				
			}
		});
		
		TableViewerColumn col = new TableViewerColumn(operations, SWT.LEFT);
		col.getColumn().setText(Messages.CompositeRemapper_6);
		col.getColumn().setWidth(150);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				
				return nodeLabelProvider.getText(currentOperations.get(element)[0]);
			}
			
			@Override
			public Image getImage(Object element) {
				return nodeLabelProvider.getImage(currentOperations.get(element)[0]);
			}
		});
		
		col = new TableViewerColumn(operations, SWT.LEFT);
		col.getColumn().setText(Messages.CompositeRemapper_7);
		col.getColumn().setWidth(150);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				
				return nodeLabelProvider.getText(element);
			}
			
			@Override
			public Image getImage(Object element) {
				return nodeLabelProvider.getImage(element);
			}
		});
		
		col = new TableViewerColumn(operations, SWT.LEFT);
		col.getColumn().setText(Messages.CompositeRemapper_8);
		col.getColumn().setWidth(150);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				
				return nodeLabelProvider.getText(currentOperations.get(element)[1]);
			}
			
			@Override
			public Image getImage(Object element) {
				return nodeLabelProvider.getImage(currentOperations.get(element)[1]);
			}
		});
		
	}
	
	
	
	public void setData(RepositoryItem item){
		currentOperations.clear();
		operations.setInput(currentOperations);
		requestedItems.removeSelectionChangedListener(selectionListener);
		try {
			TreeNode root = new TreeNode(null);
			currentSelectedRepositoryItem = item;
			buildRequestedTree(root,Activator.getDefault().getRepositoryApi().getRepositoryService().getNeededItems(item.getId()));
			requestedItems.setDatas(root.getChildren());
			repacementItems.setDatas(new Repository(Activator.getDefault().getRepositoryApi()));
		} catch (Exception e) {
			e.printStackTrace();
			requestedItems.setDatas(new ArrayList());
		}
		requestedItems.addSelectionChangedListener(selectionListener);
	}


	private void buildRequestedTree(TreeNode parent, List<RepositoryItem> neededItemsFor) {
		TreeNode[] roots = new TreeNode[neededItemsFor.size()];
		
		for(int i = 0; i < neededItemsFor.size(); i++){
			roots[i] = new TreeNode(neededItemsFor.get(i));
			roots[i].setParent(parent);
			try {
				buildRequestedTree(roots[i], Activator.getDefault().getRepositoryApi().getRepositoryService().getNeededItems(neededItemsFor.get(i).getId()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (parent != null){
			parent.setChildren(roots);
		}
		
	}
	
	
	private void performUpdates(){
		FactoryRemapperPerformer factory = new FactoryRemapperPerformer();
		
		
		/*
		 * key : Object model to change
		 * value : replacement[0] = originalRef, replacement[1]=newRef
		 */
		HashMap<RepositoryItem, List<RepositoryItem[]>> operationMap = new HashMap<RepositoryItem, List<RepositoryItem[]>>();

		
		/*
		 * we build a HashMap for each source Item we create a List of itemsReplacement
		 */
		for(RepositoryItem source : currentOperations.keySet()){
			if (currentOperations.get(source) != null){
				if (operationMap.get(((RepositoryItem)currentOperations.get(source)[0])) == null){
					operationMap.put(((RepositoryItem)currentOperations.get(source)[0]), new ArrayList<RepositoryItem[]>());
				}
				operationMap.get(((RepositoryItem)currentOperations.get(source)[0])).add(new RepositoryItem[]{
						((RepositoryItem)source),
						((RepositoryItem)currentOperations.get(source)[1])});
			}
		}
		
		
		/*
		 * perform the changes
		 */
		
		List<ModelRemapper> remappers = new ArrayList<ModelRemapper>();
		for (RepositoryItem i : operationMap.keySet()){
			try{
				remappers.add(factory.createPerfomer(Activator.getDefault().getRepositoryApi(),null,  i, operationMap.get(i), null));
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		RunRemappOperationsWithProgress runnable = new RunRemappOperationsWithProgress(remappers);
		
		
		ProgressMonitorDialog d = new ProgressMonitorDialog(getClient().getShell());
		 
	     try {
	    	 d.run(true, false, runnable); 
	    	 
	     } catch (InvocationTargetException e) {
	        e.printStackTrace();
	     } catch (InterruptedException e) {
	        e.printStackTrace();
	     }
	     
	     
	     if (!runnable.getErrors().isEmpty()){
	    	 //dump errors
	    	 StringBuffer errorMessage = new StringBuffer();
	    	 for(Exception e : runnable.getErrors()){
	    		 errorMessage.append(e.getMessage());
	    	 }
	    	 
	    	 MessageDialog.openError(getClient().getShell(), Messages.CompositeRemapper_9, errorMessage.toString());
	     }
	     else{
	    	 if (MessageDialog.openQuestion(getClient().getShell(), Messages.CompositeRemapper_10, Messages.CompositeRemapper_11)){
	    		 UpdateRemappedModelsWithProgress r =  new UpdateRemappedModelsWithProgress(remappers);
	    		 try {
	    	    	 d.run(true, false, r); 
	    	    	 
	    	     } catch (InvocationTargetException e) {
	    	        e.printStackTrace();
	    	     } catch (InterruptedException e) {
	    	        e.printStackTrace();
	    	     }
	    	    
	    	     //dump update model errors
	    	     if (!r.getErrors().isEmpty()){
	    	    	 StringBuffer errorMessage = new StringBuffer();
	    	    	 for(Exception e : r.getErrors()){
	    	    		 errorMessage.append(e.getMessage());
	    	    	 }
	    	    	 
	    	    	 MessageDialog.openError(getClient().getShell(), Messages.CompositeRemapper_12, errorMessage.toString());
	    	     }
	    	 }
	    	//empty the operations
	    	currentOperations.clear();
			operations.setInput(currentOperations); 
	     }
	}
	
	
	
	private void updateToolItemStates(){
		perform.setEnabled(!currentOperations.isEmpty());
		del.setEnabled(!operations.getSelection().isEmpty());
		if(currentRequestedSelection != null && currentReplacementSelection != null){
			add.setEnabled(!currentRequestedSelection.isEmpty() && !currentReplacementSelection.isEmpty() && ((IStructuredSelection)currentReplacementSelection).getFirstElement() instanceof RepositoryItem);
		}
		else {
			add.setEnabled(false);
		}
	}
}
