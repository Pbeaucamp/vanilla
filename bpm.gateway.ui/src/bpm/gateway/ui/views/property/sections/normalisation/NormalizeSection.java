package bpm.gateway.ui.views.property.sections.normalisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
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

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.transformations.Normalize;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class NormalizeSection extends AbstractPropertySection {

	
	
	private Normalize transfo;
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
		inputField.getColumn().setText(Messages.NormalizeSection_0);
		inputField.getColumn().setWidth(100);
		inputField.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				
				try{
					Integer pos = ((List)viewer.getInput()).indexOf((Integer)element);
					Integer val = (Integer)element;
					if (val != null && val > -1 && !transfo.getInputs().isEmpty()){
						return transfo.getInputs().get(0).getDescriptor(transfo).getStreamElements().get(val).name;
					}
				}catch(Exception ex){
					
				}
				
				return Messages.NormalizeSection_1;
			}
		});
		inputField.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				int pos = transfo.getLevelsIndex().indexOf(element);
				transfo.setInputLevelIndex(pos, ((Integer)value) - 1);
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				Integer val = (Integer)element;
				
				return val + 1;
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
		
		
		
		createContextMenu();

	}
	
	private void createContextMenu(){
		MenuManager mgr = new MenuManager();
		
		Action addLevel = new Action(Messages.NormalizeSection_2){
			public void run(){
				transfo.addLevel();
				viewer.refresh();
			}
		};
		
		final Action delLevel = new Action(Messages.NormalizeSection_3){
			public void run(){
				
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				
				if (ss.isEmpty()){
					return; 
				}
				
				transfo.removeLevel(transfo.getLevelsIndex().indexOf((Integer)ss.getFirstElement()));
				viewer.refresh();
			}
		};
		
		mgr.add(addLevel);
		mgr.add(delLevel);
		
		mgr.addMenuListener(new IMenuListener() {
			
			public void menuAboutToShow(IMenuManager manager) {
				delLevel.setEnabled(!viewer.getSelection().isEmpty());
				
			}
		});
	
		viewer.getTable().setMenu(mgr.createContextMenu(viewer.getControl()));
		
	}
	
	public void refresh() {
		viewer.setInput(transfo.getLevelsIndex());
		
		List<String> items = new ArrayList<String>();
		items.add(Messages.NormalizeSection_4);
		
		if (!transfo.getInputs().isEmpty()){
			
			try{
				StreamDescriptor desc = transfo.getInputs().get(0).getDescriptor(transfo);
				for(int i = 0; i < desc.getColumnCount(); i++){
					items.add(desc.getColumnName(i));
				}
				
			}catch(Exception ex){
				
			}
			
		}
		
		
		editor = new ComboBoxCellEditor(viewer.getTable(), items.toArray(new String[items.size()]),SWT.READ_ONLY);
	} 
	
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.transfo = (Normalize)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
}
