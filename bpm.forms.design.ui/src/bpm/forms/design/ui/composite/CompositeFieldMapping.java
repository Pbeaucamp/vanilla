package bpm.forms.design.ui.composite;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.forms.widgets.FormToolkit;

import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.design.IFormFieldMapping;
import bpm.forms.core.design.ITargetTable;
import bpm.forms.design.ui.Activator;
import bpm.forms.design.ui.Messages;
import bpm.forms.design.ui.composite.celledtitors.TargetTableDialogCellEditor;
import bpm.forms.design.ui.composite.viewer.FieldMappingLabelProvider;
import bpm.forms.design.ui.composite.viewer.FieldMappingLabelProvider.FieldType;
import bpm.forms.design.ui.tools.fieldsloader.FieldsLoaderFactory;

public class CompositeFieldMapping {
	private Composite client;
	private Button loadFields;
	
	private TableViewer fieldsViewer;
	
	private FormToolkit toolkit;
	private IFormDefinition formDef;
	
	
	public CompositeFieldMapping(FormToolkit toolkit){
		this.toolkit = toolkit;
	}
	
	public void createContent(Composite parent){
		
		if (toolkit == null){
			this.toolkit = new FormToolkit(Display.getDefault());
		}
		
		
		client = toolkit.createComposite(parent, SWT.NONE);
		client.setLayout(new GridLayout());
		client.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
				toolkit = null;
				
			}
		});
	
		loadFields = toolkit.createButton(client, Messages.CompositeFieldMapping_0, SWT.PUSH);
		loadFields.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		loadFields.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (formDef.getFormUI() != null){
				
//					BusyIndicator.showWhile(Display.getDefault(), new Runnable(){
//						public void run() {
							try{
								FieldsLoaderFactory.getFieldLoader(formDef.getFormUI()).loadFields(formDef);
								setInput(formDef);
							}catch(Exception ex){
								ex.printStackTrace();
								MessageDialog.openError(client.getShell(), Messages.CompositeFieldMapping_1, Messages.CompositeFieldMapping_2 + ex.getMessage());
							}
//						};
//					});

				}
			}
		});
		
		
		fieldsViewer = new TableViewer(toolkit.createTable(client, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL));
		fieldsViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		fieldsViewer.getTable().setLinesVisible(true);
		fieldsViewer.getTable().setHeaderVisible(true);
		fieldsViewer.setContentProvider(new ObservableListContentProvider());
		
		
		TableViewerColumn col = new TableViewerColumn(fieldsViewer, SWT.LEFT);
		col.getColumn().setText(Messages.CompositeFieldMapping_3);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new FieldMappingLabelProvider(FieldType.UiFieldId));
		
		col = new TableViewerColumn(fieldsViewer, SWT.LEFT);
		col.getColumn().setText(Messages.CompositeFieldMapping_4);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new FieldMappingLabelProvider(FieldType.TargetTable));
		col.setEditingSupport(new EditingSupport(fieldsViewer){
			
			DialogCellEditor editor = new TargetTableDialogCellEditor(fieldsViewer.getTable(), toolkit);
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}

			@Override
			protected Object getValue(Object element) {
				Long id = ((IFormFieldMapping)element).getTargetTableId();
				return id;
			}

			@Override
			protected void setValue(Object element, Object value) {
				try{
					((IFormFieldMapping)element).setTargetTableId(((ITargetTable)value).getId());
					client.notifyListeners(SWT.Modify, new Event());
					fieldsViewer.refresh();
				}catch(Exception ex) {
					ex.printStackTrace();
				}
				
				
			}
			
		});
		
		
		col = new TableViewerColumn(fieldsViewer, SWT.LEFT);
		col.getColumn().setText(Messages.CompositeFieldMapping_5);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new FieldMappingLabelProvider(FieldType.DatabaseFieldId));
		col.setEditingSupport(new EditingSupport(fieldsViewer) {
			private ComboBoxViewerCellEditor editor = new ComboBoxViewerCellEditor(fieldsViewer.getTable());
			
			{
				editor.getViewer().setLabelProvider(new LabelProvider(){
					public String getText(Object element) {
						return super.getText(element);
					};
				});
				editor.getViewer().setContentProvider(new ObservableListContentProvider());
				
			}
			@Override
			protected void setValue(Object element, Object value) {
				IFormFieldMapping ffm = (IFormFieldMapping)element;
				if (value == null){
					String s = editor.getViewer().getCCombo().getText();
					if (!"".equals(s.trim())){ //$NON-NLS-1$
						ffm.setDatabasePhysicalName(s);
						((WritableList)editor.getViewer().getInput()).add(s);
					}
					else{
						ffm.setDatabasePhysicalName(null);
					}
					
				}
				else{
					ffm.setDatabasePhysicalName((String)value);
				}
				client.notifyListeners(SWT.Modify, new Event());
				fieldsViewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				String s = ((IFormFieldMapping)element).getDatabasePhysicalName();
				if (s == null){
					return ""; //$NON-NLS-1$
				}
				return s;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				IFormFieldMapping ffm = (IFormFieldMapping)element;
				
				WritableList l = new WritableList(Activator.getDefault().getServiceProvider().getDefinitionService().getColumnsForTargetTable(ffm.getTargetTableId()), String.class);
				editor.getViewer().setInput(l);
				
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return ((IFormFieldMapping)element).getTargetTableId() != null && ((IFormFieldMapping)element).getTargetTableId() > 0;
			}
		});
		
		col = new TableViewerColumn(fieldsViewer, SWT.LEFT);
		col.getColumn().setText(Messages.CompositeFieldMapping_8);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new FieldMappingLabelProvider(FieldType.Multivalued));
		col.setEditingSupport(new EditingSupport(fieldsViewer) {
			private ComboBoxCellEditor editor = new ComboBoxCellEditor(fieldsViewer.getTable(), new String[]{"true", "false"}); //$NON-NLS-1$ //$NON-NLS-2$
			@Override
			protected void setValue(Object element, Object value) {
				Integer i = (Integer)value;
				IFormFieldMapping model = (IFormFieldMapping)element;
				
				if ( i == 0){
					model.setIsMultivalued(true);
				}
				else{
					model.setIsMultivalued(false);
				}
				
				fieldsViewer.refresh();
				client.notifyListeners(SWT.Modify, new Event());
			}
			
			@Override
			protected Object getValue(Object element) {
				IFormFieldMapping model = (IFormFieldMapping)element;
				
				if (model.isMultiValued()){
					return 0;
				}
				return 1;
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
		
		col = new TableViewerColumn(fieldsViewer, SWT.LEFT);
		col.getColumn().setText(Messages.CompositeFieldMapping_11);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new FieldMappingLabelProvider(FieldType.Description));
		col.setEditingSupport(new EditingSupport(fieldsViewer) {
			private TextCellEditor editor = new TextCellEditor(fieldsViewer.getTable());
			
			@Override
			protected void setValue(Object element, Object value) {
				IFormFieldMapping model = (IFormFieldMapping)element;
				model.setDescription((String)value);
				fieldsViewer.refresh();
				client.notifyListeners(SWT.Modify, new Event());
			}
			
			@Override
			protected Object getValue(Object element) {
				IFormFieldMapping model = (IFormFieldMapping)element;
				if (model.getDescription() == null){
					return ""; //$NON-NLS-1$
				}
				return model.getDescription();
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
	
		col = new TableViewerColumn(fieldsViewer, SWT.LEFT);
		col.getColumn().setText(Messages.CompositeFieldMapping_13);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new FieldMappingLabelProvider(FieldType.SqlDataType));
		col.setEditingSupport(new EditingSupport(fieldsViewer) {
			private TextCellEditor editor = new TextCellEditor(fieldsViewer.getTable());
			
			@Override
			protected void setValue(Object element, Object value) {
				IFormFieldMapping model = (IFormFieldMapping)element;
				model.setSqlDataType((String)value);
				fieldsViewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				IFormFieldMapping model = (IFormFieldMapping)element;
				if (model.getSqlDataType() == null){
					return ""; //$NON-NLS-1$
				}
				return model.getSqlDataType();
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
	}
	
	public Composite getClient(){
		return client;
	}
	
	public void setInput(IFormDefinition formDef){
		this.formDef = formDef;
		
		refresh();
		
	}

	public void refresh() {
		WritableList list = new WritableList(formDef.getIFormFieldMappings(), IFormFieldMapping.class);
		fieldsViewer.setInput(list);
		
	}
}
