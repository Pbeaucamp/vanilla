package bpm.gateway.ui.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.ISCD;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.ui.i18n.Messages;

public class SCDMappingComposite extends Composite {
	
	private TableViewer viewer;
	private ISCD scd;
	
	private List<String> intputFields = new ArrayList<String>();
	private boolean isField;
	
	public SCDMappingComposite(Composite parent, int style, boolean isField) {
		super(parent, style);
		this.isField = isField;
		this.setLayout(new GridLayout());
		this.setBackground(parent.getBackground());
		
		buildContent();
	}
	
	 
	public void fillDatas(ISCD scd){
		this.scd = scd;
		intputFields.clear();
		if (!((Transformation)scd).getInputs().isEmpty()){
			try{
				intputFields.add(""); //$NON-NLS-1$
				for(StreamElement e : ((Transformation)scd).getInputs().get(0).getDescriptor((Transformation)scd).getStreamElements()){
					intputFields.add(e.name);
				}
			}catch(Exception e){ }
		}
		viewer.setCellEditors(new CellEditor[]{new TextCellEditor(viewer.getTable())
		, new ComboBoxCellEditor(viewer.getTable(), intputFields.toArray(new String[intputFields.size()]))});

		
		try{
			viewer.setInput(((Transformation)scd).getDescriptor((Transformation)scd));
		}catch(Exception e){ }
		
		if(isField) {
			try {
//				StreamDescriptor desc = ((Transformation)scd).getDescriptor((Transformation)scd);
				int i = 0;
				for(StreamElement elem : ((Transformation)scd).getInputs().get(0).getDescriptor((Transformation)scd).getStreamElements()) {
					if(scd.isIgnoreField(i)) {
						int index = scd.getTargetIndexFieldForInputIndex(i);
						StreamElement e = ((Transformation)scd).getDescriptor((Transformation)scd).getStreamElements().get(index);
						((CheckboxTableViewer)viewer).setChecked(e, true);
					}
					i++;
				}
			} catch(ServerException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	protected void buildContent() {
		if(isField) {
			viewer = CheckboxTableViewer.newCheckList(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		}
		else {
			viewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		}
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.getTable().setBackground(getBackground());
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		
		TableColumn target = new TableColumn(viewer.getTable(), SWT.NONE);
		target.setText(Messages.SCDMappingComposite_2);
		target.setWidth(150);
		
		
		TableColumn input = new TableColumn(viewer.getTable(), SWT.NONE);
		input.setText(Messages.SCDMappingComposite_3);
		input.setWidth(150);
		
		if(isField) {
			((CheckboxTableViewer)viewer).addCheckStateListener(new ICheckStateListener() {
				@Override
				public void checkStateChanged(CheckStateChangedEvent event) {
					try {
						StreamElement elem = (StreamElement) event.getElement();
						String inputColumn = elem.name;
						
						int i = 0;
						for(StreamElement e : ((Transformation)scd).getDescriptor((Transformation)scd).getStreamElements()) {
								if(e.name.equals(inputColumn)) {
									break;
								}
								i++;
						}
						
						try {
							int index = scd.getInputIndexFieldFortargetIndex(i);
							StreamElement se = ((Transformation)scd).getInputs().get(0).getDescriptor((Transformation)scd).getStreamElements().get(index);
							
							if(event.getChecked()) {
								scd.createIgnoreField(se.name, true);
							}
							else {
								scd.createIgnoreField(se.name, false);
							}
						} catch(Exception e1) {
						}
					} catch(ServerException e) {
						e.printStackTrace();
					}
					
				}
			});
		}
		
		viewer.setColumnProperties(new String[]{"target", "input"}); //$NON-NLS-1$ //$NON-NLS-2$

		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				DefaultStreamDescriptor d = (DefaultStreamDescriptor)inputElement;
				return d.getStreamElements().toArray(new StreamElement[d.getStreamElements().size()]);
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
				if (columnIndex == 0){
					return ((StreamElement)element).name;
				}
				else{
					
					try{
						int i = ((Transformation)scd).getDescriptor((Transformation)scd).getStreamElements().indexOf((StreamElement)element);
						Integer k = null;
						if (isField){
							k = scd.getInputIndexFieldFortargetIndex(i) + 1;
						}
						else{
							k = scd.getInputIndexKeyFortargetIndex(i) + 1;
						}
						
						
						if (k != null){
							return intputFields.get(k);
						}
						return intputFields.get(0);
					}catch(Exception e){
						return ""; //$NON-NLS-1$
					}
					
				}
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
		
		
		viewer.setCellModifier(new ICellModifier(){

			public boolean canModify(Object element, String property) {
				return (property.equals("input")); //$NON-NLS-1$
			}

			public Object getValue(Object element, String property) {
				if (property.equals("target")){ //$NON-NLS-1$
					return ((StreamElement)element).name;
				}
				else{
					return -1;
				}
				
			}

			public void modify(Object element, String property, Object value) {
				StreamElement m = (StreamElement)((TableItem)element).getData();

				if((Integer)value != -1){
					if (isField){
						scd.createFieldMapping(m, ((Integer)value)-1);
					}
					else{
						scd.createKeyMapping(m, ((Integer)value)-1);
					}
				}
				
				viewer.refresh();
			}
			
		});
		
	}
}
