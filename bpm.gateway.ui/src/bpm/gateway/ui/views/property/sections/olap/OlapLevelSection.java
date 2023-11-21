package bpm.gateway.ui.views.property.sections.olap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.olap.OlapDimensionExtractor;
import bpm.gateway.core.transformations.olap.OlapHelper;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;

public class OlapLevelSection extends AbstractPropertySection{

	private OlapDimensionExtractor transfo;
	private CheckboxTableViewer viewer;
	
	private ICheckStateListener listener = new ICheckStateListener() {
		
		public void checkStateChanged(CheckStateChangedEvent event) {

			if (event.getChecked()){
				transfo.addLevel((String)event.getElement());
			}
			else{
				transfo.removeLevel((String)event.getElement());
			}

		}

	};
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        this.transfo = (OlapDimensionExtractor)(((Node)((NodePart) input).getModel()).getGatewayModel());
	}
	
	@Override
	public void createControls(Composite parent,TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection<Object> c = (Collection<Object>)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		viewer.setLabelProvider(new LabelProvider());
	}
	
	@Override
	public void refresh() {
		viewer.removeCheckStateListener(listener);
		
		List<String> levels= null;
		try{
			levels= transfo.getDocument().getOlapHelper().getLevelNames(true, transfo);
		}catch(Exception ex){
			levels = new ArrayList<String>();
			ex.printStackTrace();
//			MessageDialog.openError(getPart().getSite().getShell(), "Error When getting Hierarchy's Levels", ex.getMessage());
		}
		
		viewer.setInput(levels);
		for(String s : levels){
			for(String l : transfo.getLevelNames()){
				if (l.equals(s)){
					viewer.setChecked(s, true);
				}
			}
		}
		viewer.refresh();
		viewer.addCheckStateListener(listener);
	}
}
