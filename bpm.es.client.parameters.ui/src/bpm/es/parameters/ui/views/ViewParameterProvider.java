package bpm.es.parameters.ui.views;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;

import bpm.birep.admin.client.trees.TreeItem;
import bpm.es.parameters.ui.Activator;
import bpm.es.parameters.ui.Messages;
import bpm.es.parameters.ui.test.DialogPrompt;
import bpm.es.parameters.ui.viewer.DataProviderComboEditor;
import bpm.es.parameters.ui.viewer.DatasProviderColumnComboEditor;
import bpm.es.parameters.ui.viewer.ParameterColumnLableProvider;
import bpm.es.parameters.ui.viewer.ParameterColumnLableProvider.ColumnType;
import bpm.vanilla.platform.core.repository.DatasProvider;
import bpm.vanilla.platform.core.repository.ILinkedParameter;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class ViewParameterProvider extends ViewPart implements ISelectionListener{

	private TreeViewer viewer;
	private Button ok, cancel;
	
	private List<Parameter> dirtyParameters = new ArrayList<Parameter>();
	
	private DatasProviderHelper helper = new DatasProviderHelper();
	
	private RepositoryItem currentItem;
	
	public ViewParameterProvider() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(this);
	}
	
	@Override
	public void dispose() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Button refresh = new Button(main, SWT.PUSH);
		refresh.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 2, 1));
		refresh.setText(Messages.ViewParameterProvider_0);
		refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				IRunnableWithProgress r = new IRunnableWithProgress() {
					
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException,
							InterruptedException {
						Exception error = null;
						try {
							monitor.beginTask(Messages.ViewParameterProvider_1, 1);
							helper.refresh();
						} catch (Exception ex) {
							error = ex;
							
						}
						
						if (error != null){
							error.printStackTrace();
							MessageDialog.openError(getSite().getShell(), Messages.ViewParameterProvider_2, Messages.ViewParameterProvider_3 + error.getMessage());
						}
						else{
							viewer.refresh();
						}
						
						
					}
				};
				
				IProgressService service = PlatformUI.getWorkbench().getProgressService();
			     try {
			    	 service.run(false, false, r);
			    				    	 
			     } catch (InvocationTargetException ex) {
			        ex.printStackTrace();
			     } catch (InterruptedException ex) {
			        ex.printStackTrace();
			     }
			}
		});
		
		viewer = new TreeViewer(main, SWT.BORDER | SWT.VIRTUAL | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.getTree().setLinesVisible(true);
		viewer.getTree().setHeaderVisible(true);
		viewer.setContentProvider(new ITreeContentProvider(){

			@Override
			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof Parameter){
					Collection c = ((Parameter)parentElement).getRequestecParameters();
					return c.toArray(new Object[c.size()]);
				}
				return null;
			}

			@Override
			public Object getParent(Object element) {
				
				if (element instanceof ILinkedParameter){
					if (((ILinkedParameter)element).getProvidedParameterId() > 0){
						
						
						for(Object o : getElements(viewer.getInput())){
							if (((Parameter)o).getId() == ((ILinkedParameter)element).getProvidedParameterId()){
								return o;
							}
						}
					}
				}
				
				return null;
			}

			@Override
			public boolean hasChildren(Object element) {
				if (element instanceof Parameter){
					return ((Parameter)element).getRequestecParameters().size() > 0;
				}
				return false;
			}

			@Override
			public Object[] getElements(Object inputElement) {
				Collection<Parameter> c = (Collection)inputElement;
							
				return c.toArray(new Object[c.size()]);
			}

			@Override
			public void dispose() {
				
				
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		
		TreeViewerColumn name = new TreeViewerColumn(viewer, SWT.LEFT);
		name.getColumn().setText(Messages.ViewParameterProvider_4);
		name.getColumn().setWidth(100);
		name.setLabelProvider(new ParameterColumnLableProvider(ColumnType.Name, helper));
		
		
		TreeViewerColumn dataprovider = new TreeViewerColumn(viewer, SWT.LEFT);
		dataprovider.getColumn().setText(Messages.ViewParameterProvider_5);
		dataprovider.getColumn().setWidth(100);
		dataprovider.setLabelProvider(new ParameterColumnLableProvider(ColumnType.ProviderName, helper));
		dataprovider.setEditingSupport(new EditingSupport(viewer){
			private DataProviderComboEditor editor = new DataProviderComboEditor(viewer.getTree(), helper);
			
			@Override
			protected boolean canEdit(Object element) {
				
				return element instanceof Parameter;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				editor.setItems(editor.getItems());
				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				if (((Parameter)element).getDataProviderId() > 0){
					return editor.getProviderIndexFromId(((Parameter)element).getDataProviderId());
					
				}
				else{
					return -1;
				}
			}

			@Override
			protected void setValue(Object element, Object value) {
				Integer i = (Integer)value;
				DatasProvider p = editor.getProviderFromIndex(i);
				Parameter param = (Parameter)element;

				
				
				if (p != null){
					try{
						IQuery q = helper.getQuery(p.getId());
						
						IParameterMetaData pmd = q.getParameterMetaData();
						
						if (pmd.getParameterCount() == param.getRequestecParameters().size()){
							
							for(int k = 0; k < param.getRequestecParameters().size(); k++){
								String pName = ""; //$NON-NLS-1$
								if (pmd.getParameterName(k + 1) != null && ! "".equals(pmd.getParameterName(k + 1))){ //$NON-NLS-1$
									pName = pmd.getParameterName(k + 1);
								}
								else{
									pName = "parameter_" + k; //$NON-NLS-1$
								}
								
								if (!param.getRequestecParameters().get(k).getInternalParameterName().equals(pName)){
									param.getRequestecParameters().get(k).setInternalParameterName(pName);
								}
							}
							
							
						}
						else if (pmd.getParameterCount() > param.getRequestecParameters().size()){
							for(int k = param.getRequestecParameters().size(); k < pmd.getParameterCount(); k++){
								ILinkedParameter n = new ILinkedParameter();
								if (pmd.getParameterName(k + 1) != null && ! "".equals(pmd.getParameterName(k + 1))){ //$NON-NLS-1$
									n.setInternalParameterName(pmd.getParameterName(k + 1));
								}
								else{
									n.setInternalParameterName("parameter_" + k); //$NON-NLS-1$
								}
								n.setInternalParameterPosition(k + 1);
								param.addRequestedParameter(n);
							}
						}
						else{
							List<ILinkedParameter> lp =  new ArrayList<ILinkedParameter>(param.getRequestecParameters());
							
							for(int k = pmd.getParameterCount(); k < lp.size(); k++){
								param.removeRequestedParameter(lp.get(k));
							}
						}
						
						
					}catch(Exception ex){
						ex.printStackTrace();
					}
					((Parameter)element).setDataProviderId(p.getId());
					((Parameter)element).setDataProviderName(p.getName());
				}
				else{
					((Parameter)element).setDataProviderId(0);
					((Parameter)element).setDataProviderName(""); //$NON-NLS-1$
					((Parameter)element).setLabelColumnIndex(0);
					((Parameter)element).setLabelColumnName(""); //$NON-NLS-1$
					((Parameter)element).setValueColumnIndex(0);
					((Parameter)element).setValueColumnName(""); //$NON-NLS-1$
					((Parameter)element).setAllowMultipleValues(false);
				}
				
				if (!dirtyParameters.contains(element)){
					dirtyParameters.add((Parameter)element);
				}
				
				
				
				
				ok.setEnabled(true);
				cancel.setEnabled(true);
				viewer.refresh();
			}
			
		});
		
		TreeViewerColumn valuecolumn = new TreeViewerColumn(viewer, SWT.LEFT);
		valuecolumn.getColumn().setText(Messages.ViewParameterProvider_14);
		valuecolumn.getColumn().setWidth(100);
		valuecolumn.setLabelProvider(new ParameterColumnLableProvider(ColumnType.ValueColumn, helper));
		valuecolumn.setEditingSupport(new EditingSupport(viewer) {
			
			DatasProviderColumnComboEditor editor = new DatasProviderColumnComboEditor(viewer.getTree(), helper);
			
			@Override
			protected void setValue(Object element, Object value) {
				Integer i = (Integer)value;
				
				
				try{
					if (i >= 0){
						((Parameter)element).setValueColumnIndex(i + 1);
						((Parameter)element).setValueColumnName(editor.getItems()[i]);
					}
					else{
						((Parameter)element).setValueColumnIndex(0);
						((Parameter)element).setValueColumnName(null);
					}
				}catch(Exception ex){
					((Parameter)element).setValueColumnIndex(-1);
					((Parameter)element).setValueColumnName(null);
				}
				if (!dirtyParameters.contains(element)){
					dirtyParameters.add((Parameter)element);
				}
				ok.setEnabled(true);
				cancel.setEnabled(true);
				viewer.refresh();
				
				
			}
			
			@Override
			protected Object getValue(Object element) {
				editor.setDataProvider(((Parameter)element).getDataProviderId());
				
				return ((Parameter)element).getValueColumnIndex(); 
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				editor.setDataProvider(((Parameter)element).getDataProviderId());
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return element instanceof Parameter;
			}
		});
		
		
		TreeViewerColumn labelcolumn = new TreeViewerColumn(viewer, SWT.LEFT);
		labelcolumn.getColumn().setText(Messages.ViewParameterProvider_15);
		labelcolumn.getColumn().setWidth(100);
		labelcolumn.setLabelProvider(new ParameterColumnLableProvider(ColumnType.LabelColumn, helper));
		labelcolumn.setEditingSupport(new EditingSupport(viewer) {
			
			DatasProviderColumnComboEditor editor = new DatasProviderColumnComboEditor(viewer.getTree(), helper);
			
			@Override
			protected void setValue(Object element, Object value) {
				Integer i = (Integer)value;
				
				
				try{
					if (i >= 0){
						((Parameter)element).setLabelColumnIndex(i + 1);
						((Parameter)element).setLabelColumnName(editor.getItems()[i]);
					}
					else{
						((Parameter)element).setLabelColumnIndex(0);
						((Parameter)element).setLabelColumnName(null);
					}
				}catch(Exception ex){
					((Parameter)element).setLabelColumnIndex(-1);
					((Parameter)element).setLabelColumnName(null);
				}
				if (!dirtyParameters.contains(element)){
					dirtyParameters.add((Parameter)element);
				}
				ok.setEnabled(true);
				cancel.setEnabled(true);
				viewer.refresh();
				
				
			}
			
			@Override
			protected Object getValue(Object element) {
				editor.setDataProvider(((Parameter)element).getDataProviderId());
				
				return ((Parameter)element).getLabelColumnIndex(); 
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				editor.setDataProvider(((Parameter)element).getDataProviderId());
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return element instanceof Parameter;
			}
		});
		
		
		TreeViewerColumn allowMultipleColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		allowMultipleColumn.getColumn().setText(Messages.ViewParameterProvider_16);
		allowMultipleColumn.getColumn().setWidth(100);
		allowMultipleColumn.setLabelProvider(new ParameterColumnLableProvider(ColumnType.MultipleValuesColumn, helper));
		allowMultipleColumn.setEditingSupport(new EditingSupport(viewer) {
			
			CheckboxCellEditor editor = new CheckboxCellEditor(null, SWT.CHECK | SWT.READ_ONLY);
			
			@Override
			protected void setValue(Object element, Object value) {
							
				((Parameter)element).setAllowMultipleValues((Boolean)value);
				if (!dirtyParameters.contains(element)){
					dirtyParameters.add((Parameter)element);
				}
				ok.setEnabled(true);
				cancel.setEnabled(true);
				viewer.refresh();
				
				
			}
			
			@Override
			protected Object getValue(Object element) {
						
				return ((Parameter)element).isAllowMultipleValues(); 
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return element instanceof Parameter;
			}
		});
		
		
		TreeViewerColumn paramProvider = new TreeViewerColumn(viewer, SWT.LEFT);
		paramProvider.getColumn().setText(Messages.ViewParameterProvider_17);
		paramProvider.getColumn().setWidth(100);
		paramProvider.setLabelProvider(new ParameterColumnLableProvider(ColumnType.ParentName, helper));
		paramProvider.setEditingSupport(new EditingSupport(viewer) {
			private ComboBoxCellEditor editor = new ComboBoxCellEditor(viewer.getTree(), new String[]{});
			@Override
			protected void setValue(Object element, Object value) {
				Parameter p = null;
				try{
					 p = ((List<Parameter>)viewer.getInput()).get((Integer)value - 1);
					((ILinkedParameter)element).setProviderParameterId(p.getId());
					((ILinkedParameter)element).setProviderParameterName(p.getName());
				}catch(Exception ex){
					((ILinkedParameter)element).setProviderParameterId(0);
					((ILinkedParameter)element).setProviderParameterName(null);
				}
				if (p != null && !dirtyParameters.contains(p)){
					dirtyParameters.add((Parameter)p);
				}
				ok.setEnabled(true);
				cancel.setEnabled(true);
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				int i = ((ILinkedParameter)element).getProviderParameterId();
				
				if (i >= 0){
					List<Parameter> l = (List<Parameter>)viewer.getInput();
					for(int k = 0; k < l.size(); k++){
						if (l.get(k).getId() == i + 1){
							return k;
						}
					}
					
				}
				return 0;
				
				
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				List<Parameter> l = 	(List<Parameter>)viewer.getInput();
				
				String[] itms = new String[l.size() + 1];
				itms[0] = Messages.ViewParameterProvider_18;
				for(int i = 0; i < l.size(); i++){
					itms[i + 1] = l.get(i).getName();
				}
				editor.setItems(itms);
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return element instanceof ILinkedParameter;
			}
		});
		

		ok = new Button(main, SWT.PUSH);
		ok.setText(Messages.ViewParameterProvider_19);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		ok.setEnabled(false);
		ok.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				StringBuffer buf = new StringBuffer();
				for(Parameter p : dirtyParameters){
					try{
						Activator.getDefault().getRepositorySocket().getAdminService().updateParameter(p);
					}catch(Exception ex){
						ex.printStackTrace();
						buf.append(Messages.ViewParameterProvider_20 + p.getName() + ": " + ex.getMessage() + "\n");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
					}
				}
				
				dirtyParameters.clear();
				
				if (!buf.toString().equals("")){ //$NON-NLS-1$
					MessageDialog.openError(getSite().getShell(), Messages.ViewParameterProvider_24, buf.toString());
				}
				
				ok.setEnabled(false);
				cancel.setEnabled(false);
			}
		});
		
		cancel = new Button(main, SWT.PUSH);
		cancel.setText(Messages.ViewParameterProvider_25);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.setEnabled(false);
		cancel.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					viewer.setInput(Activator.getDefault().getRepositorySocket().getRepositoryService().getParameters(currentItem));
					
					
				}catch(Exception ex){
					ex.printStackTrace();
				}
				dirtyParameters.clear();
				ok.setEnabled(false);
				cancel.setEnabled(false);
			}
		});
	
	
		Button run = new Button(main, SWT.PUSH);
		run.setText(Messages.ViewParameterProvider_26);
		run.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		run.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				DialogPrompt d = new DialogPrompt(getSite().getShell(), currentItem);
				d.open();

				
			}
		});
	
		try{
			helper.refresh();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

	@Override
	public void setFocus() {
		
		ISelection s = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		
		selectionChanged(null, s);
	}

	private ISelection lastSelection;
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (lastSelection == null){
			lastSelection = selection;
		}
		else if (lastSelection == selection){
			return;
		}
		dirtyParameters.clear();
		ok.setEnabled(false);
		cancel.setEnabled(false);
		if ( selection == null || selection.isEmpty() || !((((IStructuredSelection)selection).getFirstElement() instanceof RepositoryItem)
			|| (((IStructuredSelection)selection).getFirstElement() instanceof TreeItem))){
			viewer.setInput(null);
			currentItem = null;
			return;
		}
		
		try {
			if ((((IStructuredSelection)selection).getFirstElement() instanceof RepositoryItem)){
				
				currentItem = (RepositoryItem)((IStructuredSelection)selection).getFirstElement();
				
				viewer.setInput(Activator.getDefault().getRepositorySocket().getRepositoryService().getParameters((RepositoryItem)((IStructuredSelection)selection).getFirstElement()));
			}
			else if ((((IStructuredSelection)selection).getFirstElement() instanceof TreeItem)){
				currentItem = ((TreeItem)((IStructuredSelection)selection).getFirstElement()).getItem();
				viewer.setInput(Activator.getDefault().getRepositorySocket().getRepositoryService().getParameters(((TreeItem)((IStructuredSelection)selection).getFirstElement()).getItem()));
			}
			else{
				viewer.setInput(null);
				currentItem = null;
				return;
			}
			
		} catch (Exception e) {
			currentItem = null;
			e.printStackTrace();
			viewer.setInput(null);
			MessageDialog.openError(getSite().getShell(), Messages.ViewParameterProvider_27, Messages.ViewParameterProvider_28 + e.getMessage());
		}
		
	}

}
