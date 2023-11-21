package bpm.gateway.ui.views.property.sections.normalisation;

import java.util.ArrayList;
import java.util.Collection;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.FilterDimension;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class FilterNormalizeSection extends AbstractPropertySection {

	
	
	private FilterDimension transfo;
	private TableViewer viewer;
	
	private ComboBoxCellEditor editor;
	
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
	
		viewer = new TableViewer(getWidgetFactory().createTable(parent, SWT.BORDER | SWT.FULL_SELECTION));
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		
		
		TableViewerColumn inputField = new TableViewerColumn(viewer, SWT.LEFT);
		inputField.getColumn().setText(Messages.FilterNormalizeSection_0);
		inputField.getColumn().setWidth(100);
		inputField.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((StreamElement)element).name;
			}
		});
		
		
		
		TableViewerColumn dimField = new TableViewerColumn(viewer, SWT.LEFT);
		dimField.getColumn().setText(Messages.FilterNormalizeSection_1);
		dimField.getColumn().setWidth(100);
		dimField.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				Integer i = transfo.getMapping((StreamElement)element) ;
				if (i == null){
					return editor.getItems()[0];
				}
				else {
					i = i + 1;
				}
				try{
					
					if (i < 0){
						return editor.getItems()[0];
					}
					return editor.getItems()[i];
				}catch(Exception ex){
					ex.printStackTrace();	
				}
				return editor.getItems()[0];
				
			}
		});
		dimField.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				try {
					transfo.setMapping(transfo.getDescriptor(transfo).getStreamElements().indexOf((StreamElement)element), ((Integer)value) - 1);
				} catch (ServerException e) {
					
					e.printStackTrace();
				}
				viewer.refresh();
				
			}
			
			@Override
			protected Object getValue(Object element) {
				try{
					
					int i = transfo.getDescriptor(transfo).getStreamElements().indexOf(element) + 1;
					return i;
				}catch(Exception ex){
					
				}
				return -1;
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
	
	
	
	public void refresh() {
		
		String[] items = null;
		
		try{
			items = new String[transfo.getDimensionValidatorInput().getDescriptor(transfo).getColumnCount() + 1];
			items[0] = "--- None ---"; //$NON-NLS-1$
			int i = 1;
			for(StreamElement e : transfo.getDimensionValidatorInput().getDescriptor(transfo).getStreamElements()){
				items[i++] = e.name;
			}
		}catch(Exception ex){
			items = new String[]{"--- None ---"}; //$NON-NLS-1$
		}
		
		
		
		editor = new ComboBoxCellEditor(viewer.getTable(), items, SWT.READ_ONLY);
		try {
			viewer.setInput(transfo.getDescriptor(transfo).getStreamElements());
			
		} catch (ServerException e) {
			e.printStackTrace();
			viewer.setInput(new ArrayList<StreamElement>());
		}
	} 
	
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.transfo = (FilterDimension)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
}
