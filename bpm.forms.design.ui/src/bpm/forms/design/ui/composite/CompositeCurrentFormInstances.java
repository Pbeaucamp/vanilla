package bpm.forms.design.ui.composite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;

import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.design.IFormFieldMapping;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.core.runtime.IFormInstanceFieldState;
import bpm.forms.design.ui.Activator;
import bpm.forms.design.ui.Messages;

public class CompositeCurrentFormInstances implements ISelectionChangedListener{

	private static Color DIRTY_COLOR = new Color(Display.getDefault(), 200, 100, 100);
	
	private Composite client;
	private TableViewer  state;
	private FormToolkit toolkit;
	
	private HashMap<IFormFieldMapping, IFormInstanceFieldState> fieldsValues = new HashMap<IFormFieldMapping, IFormInstanceFieldState>();
	
	private List<IFormInstanceFieldState> dirtyFields = new ArrayList<IFormInstanceFieldState>();
	private ISelection lastSelection;
	
	public CompositeCurrentFormInstances(FormToolkit toolkit){
		this.toolkit = toolkit;
	}
	
	
	
	public Composite getClient(){
		return client;
	}
	
	public void createContent(Composite parent){
		if (toolkit == null ){
			toolkit = new FormToolkit(Display.getDefault());
		}
		createTable(parent);
		client = state.getTable();
		
	}
	
	private void createTable(Composite parent){
		state = new TableViewer(toolkit.createTable(parent, SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL));
//		state.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		state.getTable().setLinesVisible(true);
		state.getTable().setHeaderVisible(true);
		state.setContentProvider(new ArrayContentProvider());
		

		TableViewerColumn c = new TableViewerColumn(state, SWT.LEFT);
		c.getColumn().setText(Messages.CompositeCurrentFormInstances_0);
		c.getColumn().setWidth(150);
		c.setLabelProvider(new FieldLabelProvider(FieldLabelProvider.FORM_FIELD_NAME));
		
		c = new TableViewerColumn(state, SWT.LEFT);
		c.getColumn().setText(Messages.CompositeCurrentFormInstances_1);
		c.getColumn().setWidth(150);
		c.setLabelProvider(new FieldLabelProvider(FieldLabelProvider.TARGET_FIELD_NAME));
		
		c = new TableViewerColumn(state, SWT.LEFT);
		c.getColumn().setText(Messages.CompositeCurrentFormInstances_2);
		c.getColumn().setWidth(300);
		c.setLabelProvider(new FieldLabelProvider(FieldLabelProvider.FIELD_VALUE));
		
		c.setEditingSupport(new EditingSupport(state) {
			TextCellEditor editor = new TextCellEditor(state.getTable());
			@Override
			protected void setValue(Object element, Object value) {
				try{
					 IFormInstanceFieldState fState = fieldsValues.get(((IFormFieldMapping)element));
					 fState.setValue((String)value);
					 addToDirty(fState);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				state.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				try{
					return fieldsValues.get(((IFormFieldMapping)element)).getValue();
				}catch(Exception ex){
					return ""; //$NON-NLS-1$
				}
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

	
		createMenu();
	}
	
	private void createMenu(){
		MenuManager mgr = new MenuManager();
		
		final Action updateFieldValues = new Action(Messages.CompositeCurrentFormInstances_4){
			public void run(){
				
				StringBuffer errors = new StringBuffer();
				for(IFormInstanceFieldState s : new ArrayList<IFormInstanceFieldState>(dirtyFields)){
					try {
						Activator.getDefault().getServiceProvider().getInstanceService().update(s);
						removeFromDirty(s);
					} catch (Exception e) {
						e.printStackTrace();
						errors.append("Error when updating FieldSate " + s.getId() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				
				if (errors.toString().isEmpty()){
					MessageDialog.openInformation(getClient().getShell(), Messages.CompositeCurrentFormInstances_7, Messages.CompositeCurrentFormInstances_8);
				}
				else{
					MessageDialog.openInformation(getClient().getShell(), Messages.CompositeCurrentFormInstances_9, Messages.CompositeCurrentFormInstances_10 + errors.toString());
				}
				state.refresh();
			}

			
		};
		
		mgr.add(updateFieldValues);
		
		mgr.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				updateFieldValues.setEnabled(!dirtyFields.isEmpty());
				
			}
		});
		
		state.getTable().setMenu(mgr.createContextMenu(state.getTable()));
	}
	
	
	
	
	
	private class FieldLabelProvider extends ColumnLabelProvider{
		static final int TARGET_FIELD_NAME = 0;
		static final int FORM_FIELD_NAME = 1;
		static final int FIELD_VALUE = 2;
		private int type;
		
		
		public FieldLabelProvider(int type){
			this.type = type;
		}
		
		@Override
		public Color getBackground(Object element) {
			if (dirtyFields.contains(fieldsValues.get(((IFormFieldMapping)element)))){
				return DIRTY_COLOR;
			}
			return null;
		}
		
		@Override
		public String getText(Object element) {
			switch(type){
			case TARGET_FIELD_NAME:
				return ((IFormFieldMapping)element).getDatabasePhysicalName();
			case FIELD_VALUE:
				try{
					return fieldsValues.get(((IFormFieldMapping)element)).getValue();
				}catch(Exception ex){
					return ""; //$NON-NLS-1$
				}
				
			case FORM_FIELD_NAME:
				return ((IFormFieldMapping)element).getFormFieldId();
			}
			return ""; //$NON-NLS-1$
		}
	}


	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		
		
		
		fieldsValues.clear();
		dirtyFields.clear();
		for(Object o : ((IStructuredSelection)event.getSelection()).toList()){
			
			if (o instanceof IFormInstance){
				if (!(((IFormInstance)o).hasBeenSubmited() && !((IFormInstance)o).hasBeenValidated())){
					continue;
				}
				try{
					IFormDefinition def = Activator.getDefault().getServiceProvider().getDefinitionService().getFormDefinition(((IFormInstance)o).getFormDefinitionId());
					List<IFormInstanceFieldState> states = Activator.getDefault().getServiceProvider().getInstanceService().getFieldsState(((IFormInstance)o).getId());
					
					for(IFormFieldMapping m : def.getIFormFieldMappings()){
						for(IFormInstanceFieldState s : states){
							if (s.getFormFieldMappingId() == m.getId()){
								fieldsValues.put(m, s);
								break;
							}
							else{
								fieldsValues.put(m, null);
							}
						}
					}
					
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
				
			}
			
		}
		state.setInput(fieldsValues.keySet());
		lastSelection = event.getSelection();
		return;
	}
	
	



	private void addToDirty(IFormInstanceFieldState state){
		if (dirtyFields.contains(state)){
			return;
		}
		dirtyFields.add(state);
	}
	
	private void removeFromDirty(IFormInstanceFieldState state) {
		dirtyFields.remove(state);
		
	}
}
