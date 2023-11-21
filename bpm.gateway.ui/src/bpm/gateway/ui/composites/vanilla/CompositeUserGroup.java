package bpm.gateway.ui.composites.vanilla;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.vanilla.VanillaGroupUser;
import bpm.gateway.ui.i18n.Messages;


public class CompositeUserGroup extends Composite {

	private TableViewer table;
	private ComboBoxCellEditor comboEditor = new ComboBoxCellEditor(); 
	private VanillaGroupUser transfo;
	private String[] itemNames = null;
	
	public CompositeUserGroup(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		buildTable();
	}
	
	
	private void buildTable(){
		Label l = new Label(this, SWT.NONE);
		l.setText(Messages.CompositeUserGroup_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		table = new TableViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		table.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<StreamElement> st = (List<StreamElement>)inputElement;
				return st.toArray(new StreamElement[st.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		table.setLabelProvider(new ITableLabelProvider(){

			public void addListener(ILabelProviderListener listener) {
				
				
			}

			public void dispose() {
				
				
			}

			public boolean isLabelProperty(Object element, String property) {
				
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
				
				
			}

			public Image getColumnImage(Object element, int columnIndex) {
				
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				if (columnIndex == 0){
					return ((StreamElement)element).name;
				}
				else{
					try{
						int i = transfo.getDescriptor(transfo).getStreamElements().indexOf((StreamElement)element);
						Integer x = transfo.getMappingValueForThisNum(i);
						return transfo.getInputs().get(0).getDescriptor(transfo).getStreamElements().get(x).name;
					}catch(Exception e){
						return "--- None ---"; //$NON-NLS-1$
					}
					
					
				}
			
			}

			
			
		});
		table.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.getTable().setHeaderVisible(true);
		
		TableColumn targetFields = new TableColumn(table.getTable(), SWT.NONE);
		targetFields.setWidth(200);
		targetFields.setText(Messages.CompositeUserGroup_1);
		
		TableColumn inputFields = new TableColumn(table.getTable(), SWT.NONE);
		inputFields.setWidth(200);
		inputFields.setText(Messages.CompositeUserGroup_3);
		
		table.setColumnProperties(new String[]{"Target Fields", "Input Fields"}); //$NON-NLS-1$ //$NON-NLS-2$
		table.setCellModifier(new DescriptorCellModifier());

		table.setCellEditors(new CellEditor[]{
				new TextCellEditor(table.getTable()),comboEditor
				});
		
		
	}
	
	public void setTransformation(VanillaGroupUser transfo){
		this.transfo = transfo;
		
		try{
			StreamDescriptor desc = transfo.getInputs().get(0).getDescriptor(transfo);
			
			itemNames = new String[desc.getColumnCount() + 1];
			itemNames[0] = Messages.CompositeUserGroup_6;
			for(int i = 0; i <desc.getColumnCount(); i++){
				itemNames[i+1] = new String (desc.getStreamElements().get(i).name);
			}
		}catch(Exception e){
			itemNames = new String[1];
			itemNames[0] = Messages.CompositeUserGroup_7;
		}
		
		comboEditor = new ComboBoxCellEditor(table.getTable(), itemNames);
		table.setCellEditors(new CellEditor[]{
				new TextCellEditor(table.getTable()),comboEditor});

		try {
			table.setInput(transfo.getDescriptor(transfo).getStreamElements());
		} catch (ServerException e) {
			e.printStackTrace();
		}

	}
	
	
	public class DescriptorCellModifier implements ICellModifier{

		public boolean canModify(Object element, String property) {
			if (property.equals("Target Fields")){ //$NON-NLS-1$
				return false;
			}
			return true;
		}

		public Object getValue(Object element, String property) {
			if (property.equals("Input Fields")){ //$NON-NLS-1$
				try{
					int index = transfo.getDescriptor(transfo).getStreamElements().indexOf((StreamElement)element);
					Integer a =  transfo.getMappingValueForThisNum( index);
					if (a == null){
						return 0;
					}
					return a;
				}catch(Exception e){
					return 0;
				}
			}
			else if (property.equals("Target Fields")){ //$NON-NLS-1$
				return ((StreamElement)element).name;
			}
			
			return null;
		}

		public void modify(Object element, String property, Object value) {
			if (property.equals("Input Fields")){ //$NON-NLS-1$
				try{
					
					if ((Integer)value > 0){
						int index = transfo.getDescriptor(transfo).getStreamElements().indexOf((StreamElement)((TableItem)element).getData());
						Integer i = transfo.getMappingValueForThisNum(index);
						if (i != null){
							transfo.deleteMapping(index);
						}
						transfo.createMapping(index, ((Integer)value)-1);
					}
					else{
						int index = transfo.getDescriptor(transfo).getStreamElements().indexOf((StreamElement)((TableItem)element).getData());
						Integer i = transfo.getMappingValueForInputNum(index);
						transfo.deleteMapping(index);
					}
					
					table.refresh();
				}catch(Exception e){
					
				}
							
				
			}
			
		}
		
	}

}
