package bpm.es.datasource.analyzer.ui.views;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.viewers.RepositoryContentProvider;
import bpm.vanilla.repository.ui.viewers.RepositoryTreeViewer;

public class AnalysisView extends ViewPart {

	public static final String ID = "bpm.es.datasource.analyzer.ui.views.AnalysisView"; //$NON-NLS-1$
	private RepositoryTreeViewer viewer;
	boolean showDependantItems = false;
	
	public AnalysisView() {}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new RepositoryTreeViewer(parent, SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		getSite().getPage().addPartListener(new IPartListener() {
			
			public void partOpened(IWorkbenchPart part) {}
			
			public void partDeactivated(IWorkbenchPart part) {}
			
			public void partClosed(IWorkbenchPart part) {}
			
			public void partBroughtToTop(IWorkbenchPart part) {}
			
			public void partActivated(IWorkbenchPart part) {
				if (part == AnalysisView.this){
					getSite().setSelectionProvider(viewer);
				}
				
			}
		});
	}

	@Override
	public void setFocus() {}
	
	public void setInput(Collection<RepositoryItem> items, boolean showDependantItems){
		
		if (showDependantItems != this.showDependantItems){
			this.showDependantItems = showDependantItems;
			
			if (this.showDependantItems){
				 
				viewer.setContentProvider(new RepositoryContentProvider(){
					HashMap<RepositoryItem, List<RepositoryItem>> childs = new HashMap<RepositoryItem, List<RepositoryItem>>();
					
					@Override
					public boolean hasChildren(Object element) {
						if (element instanceof RepositoryItem){
							
							try {
								if (childs.get((RepositoryItem)element) == null){
									childs.put((RepositoryItem)element, Activator.getDefault().getRepositoryApi().getRepositoryService().getDependantItems((RepositoryItem)element));
								}
								return !childs.get((RepositoryItem)element).isEmpty();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						return super.hasChildren(element);
					}
					
					@Override
					public Object[] getChildren(Object parentElement) {
						if (parentElement instanceof RepositoryItem){
							try {
								List l = childs.get((RepositoryItem)parentElement);
								return l.toArray(new Object[l.size()]);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						return super.getChildren(parentElement);
					}
					
					@Override
					public Object getParent(Object element) {
						if (element instanceof RepositoryItem){
							for(RepositoryItem p : childs.keySet()){
								if (childs.get(p).contains(element)){
									return p;
								}
							}
						}
						return super.getParent(element);
					}
				});
			}
			else{
				viewer.setContentProvider(new RepositoryContentProvider());
			}
		}
		
		
		
		viewer.setInput(items);
		
		
	}

}
