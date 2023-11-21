package bpm.gateway.ui.views.property.sections.vanilla;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.MappingException;
import bpm.gateway.core.transformations.vanilla.IVanillaDirectMappable;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class VanillaCreateUserSection extends AbstractPropertySection{
	private IVanillaDirectMappable transfo;
	private ComboBoxCellEditor editor;
	
	private TableViewer viewer;
	private Integer[] tab ;
	@Override
	public void createControls(Composite parent,TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		
		viewer = new TableViewer(getWidgetFactory().createTable(parent, SWT.BORDER | SWT.FULL_SELECTION));
//		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				return (Integer[])inputElement;
			}
		});
		
		TableViewerColumn userField = new TableViewerColumn(viewer, SWT.NONE);
		userField.getColumn().setWidth(200);
		userField.getColumn().setText(Messages.VanillaCreateUserSection_0);
		userField.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				try {
					return transfo.getDescriptor(transfo).getStreamElements().get((Integer)element).name;
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				return ""; //$NON-NLS-1$
			}
		});

		TableViewerColumn inputField = new TableViewerColumn(viewer, SWT.NONE);
		inputField.getColumn().setWidth(200);
		inputField.getColumn().setText(Messages.VanillaCreateUserSection_2);
		inputField.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				Integer inputIndice = transfo.getMappingValueForThisNum((Integer)element);
				if (inputIndice == null){
					return ""; //$NON-NLS-1$
				}
				try {
					return transfo.getInputs().get(0).getDescriptor(transfo).getStreamElements().get(inputIndice).name;
				} catch (Exception e) {
				}
				return ""; //$NON-NLS-1$
				
			}
		});
		editor = new ComboBoxCellEditor(viewer.getTable(), new String[]{});
		inputField.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				if ((Integer)value >= 0){
					Integer i = transfo.getMappingValueForThisNum((Integer)element);
					if (i != null){
						transfo.deleteMapping((Integer)element);
					}
					try {
						transfo.createMapping((Integer)element, ((Integer)value));
					} catch (MappingException e) {
						
						e.printStackTrace();
					}
				}
				else{
					Integer i = transfo.getMappingValueForInputNum((Integer)element);
					transfo.deleteMapping((Integer)element);
				}
				
				viewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				try{
					
					Integer a =  transfo.getMappingValueForThisNum( (Integer)element);
					if (a == null){
						return 0;
					}
					return a;
				}catch(Exception e){
					return -1;
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
		
	}

	@Override
	public void refresh() {
		viewer.setInput(tab);
		
		List<String> l = new ArrayList<String>();
		try{
			for(StreamElement e : transfo.getInputs().get(0).getDescriptor(transfo).getStreamElements()){
				l.add(new String(e.name));
			}
		}catch(Exception ex){
			
		}
			
		
		editor.setItems(l.toArray(new String[l.size()]));
	}
	
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.transfo = (IVanillaDirectMappable)((Node)((NodePart) input).getModel()).getGatewayModel();
        
        /*
         * init tab 
         */
        try{
        	 tab = new Integer[transfo.getDescriptor(transfo).getColumnCount()];
             for(int i = 0; i < transfo.getDescriptor(transfo).getColumnCount(); i++){
             	tab[i] = i;
             }
        }catch(Exception ex){
        	tab = new Integer[]{};
        	ex.printStackTrace();
        }
       
	}
}
