package bpm.gateway.ui.composites.normalisation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.transformations.normalisation.Denormalisation;
import bpm.gateway.core.transformations.normalisation.NormaliserField;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;

public class CompositeNormalisation extends Composite{

	private TableViewer viewer;
	private ComboBoxCellEditor comboEditor = new ComboBoxCellEditor(); 
	private Denormalisation transfo;
	private String[] itemNames;
	
	private PropertyChangeListener listener = new PropertyChangeListener(){

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(Denormalisation.PROPERTY_CONTENTS)){
				fillComboEditor();
			}
			
		}
		
	};
	
	public CompositeNormalisation(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		buildToolbar(this);
		buildViewer(this);
	}

	
	private void buildToolbar(Composite parent){
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false,2, 1));
		
		ToolItem add = new ToolItem(toolbar, SWT.PUSH);
		add.setToolTipText(Messages.CompositeNormalisation_0);
		add.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.add_16));
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				NormaliserField field = new NormaliserField();
				field.setFieldName(Messages.CompositeNormalisation_1);
				transfo.addField(field);
				viewer.setInput(transfo.getFields());
			}
			
		});
		
		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.CompositeNormalisation_2);
		del.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.del_16));
		del.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss= (IStructuredSelection)viewer.getSelection();
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof NormaliserField)){
					return;
				}
				
				transfo.removeField((NormaliserField)ss.getFirstElement());
				viewer.setInput(transfo.getFields());
			}
			
		});
	}
	
	private void buildViewer(Composite parent){
		viewer = new TableViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List l = (List)inputElement;
				return l.toArray(new Object[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		viewer.setLabelProvider(new ITableLabelProvider(){

			public Image getColumnImage(Object element, int columnIndex) {
				
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch(columnIndex){
				case 0:
					return ((NormaliserField)element).getFieldName();
				case 1:
					try{
						return transfo.getDescriptor(transfo).getStreamElements().get(((NormaliserField)element).getInputFieldValueIndex()).className;
					}catch(Exception e){
						return Messages.CompositeNormalisation_3;
					}
					
				case 2:
					try{
						return itemNames[((NormaliserField)element).getInputFieldValueIndex() + 1];
					}catch(Exception e){
						return itemNames[0];
					}
					
				case 3:
					return ((NormaliserField)element).getValue();
				}
				return null;
			}

			public void addListener(ILabelProviderListener listener) {
				
				
			}

			public void dispose() {
				
				
			}

			public boolean isLabelProperty(Object element, String property) {
				
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
				
				
			}
			
		});
		viewer.getTable().setHeaderVisible(true);
		
		TableColumn name = new TableColumn(viewer.getTable(), SWT.NONE);
		name.setText(Messages.CompositeNormalisation_4);
		name.setWidth(100);
		
		
		TableColumn type = new TableColumn(viewer.getTable(), SWT.NONE);
		type.setText(Messages.CompositeNormalisation_5);
		type.setWidth(100);
		
		TableColumn valueField = new TableColumn(viewer.getTable(), SWT.NONE);
		valueField .setText(Messages.CompositeNormalisation_6);
		valueField .setWidth(150);
		
		TableColumn value = new TableColumn(viewer.getTable(), SWT.NONE);
		value.setText(Messages.CompositeNormalisation_7);
		value.setWidth(100);
		
		viewer.setColumnProperties(new String[]{"Field Name", "Field Type", "Value Field", "Value"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		viewer.setCellModifier(new FieldCellModifier());

		viewer.setCellEditors(new CellEditor[]{
				new TextCellEditor(viewer.getTable()),
				null,
				comboEditor,
				new TextCellEditor(viewer.getTable())
				});
		
	}
	
	public class FieldCellModifier implements ICellModifier{

		public boolean canModify(Object element, String property) {
			if (property.equals("Field Type")){ //$NON-NLS-1$
				return false;
			}
			return true;
		}

		public Object getValue(Object element, String property) {
			if (property.equals("Field Name")){ //$NON-NLS-1$
				return ((NormaliserField)element).getFieldName();
			}
			else if (property.equals("Field Type")){ //$NON-NLS-1$
				try{
					return transfo.getDescriptor(transfo).getStreamElements().get(((NormaliserField)element).getInputFieldValueIndex()).className;
				}catch(Exception e){
					return Messages.CompositeNormalisation_9;
				}
			}
			
			else if (property.equals("Value Field")){ //$NON-NLS-1$
				Integer i = ((NormaliserField)element).getInputFieldValueIndex();
				if (i == null){
					return 0;
				}
				return i+1;
			}
			else if (property.equals("Value")){ //$NON-NLS-1$
				return ((NormaliserField)element).getValue();
			}
			
			return null;
		}

		public void modify(Object element, String property, Object value) {
			if (property.equals("Field Name")){ //$NON-NLS-1$
				((NormaliserField)((TableItem)element).getData()).setFieldName((String)value);
			}
			
			else if (property.equals("Value Field")){ //$NON-NLS-1$
				if ((Integer)value >0){
					((NormaliserField)((TableItem)element).getData()).setInputFieldValueIndex(-1 + (Integer)value);
				}
				else{
					((NormaliserField)((TableItem)element).getData()).setInputFieldValueIndex(null);
				}
				
			}
			else if (property.equals("Value")){ //$NON-NLS-1$
				((NormaliserField)((TableItem)element).getData()).setValue((String)value);
			}
			viewer.refresh();
		}
		
	}

	public void setTransformation(Denormalisation t) {
		if (this.transfo != null){
			this.transfo.removePropertyChangeListener(listener);
		}
		this.transfo = t;
		t.addPropertyChangeListener(listener);
		
		fillComboEditor();
		viewer.setInput(t.getFields());
	}
	
	private void fillComboEditor(){
		try{
			StreamDescriptor desc = transfo.getInputs().get(0).getDescriptor(transfo);
			
			itemNames = new String[desc.getColumnCount() + 1];
			itemNames[0] = "--- None ---"; //$NON-NLS-1$
			for(int i = 0; i <desc.getColumnCount(); i++){
				itemNames[i+1] = new String (desc.getStreamElements().get(i).name);
			}
		}catch(Exception e){
			itemNames = new String[1];
			itemNames[0] = "--- None ---"; //$NON-NLS-1$
		}
		
		comboEditor = new ComboBoxCellEditor(viewer.getTable(), itemNames);
		viewer.setCellEditors(new CellEditor[]{
				new TextCellEditor(viewer.getTable()),
				new ComboBoxCellEditor(viewer.getTable(), Variable.VARIABLES_TYPES),
				comboEditor,
				new TextCellEditor(viewer.getTable())
				});
		
		
	}
}
