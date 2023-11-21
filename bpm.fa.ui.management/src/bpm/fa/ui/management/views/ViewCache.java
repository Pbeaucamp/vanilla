package bpm.fa.ui.management.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import bpm.fa.ui.management.Activator;
import bpm.fa.ui.management.Messages;
import bpm.fa.ui.management.icons.Icons;
import bpm.united.olap.api.cache.IUnitedOlapCacheManager;
import bpm.united.olap.api.cache.impl.MemoryCacheStatistics;
import bpm.united.olap.api.model.Cube;
import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.runtime.model.ICubeInstance;

public class ViewCache extends ViewPart{
	public static final String ID = "bpm.fa.ui.management.views.ViewCache"; //$NON-NLS-1$
	
	
	private TreeViewer viewer;
	private Text maxSize, curSize, number, remainingSize, hits, missed;
	private IUnitedOlapCacheManager cacheService;
	
	private FormToolkit toolkit = new FormToolkit(Display.getDefault());
	
	@Override
	public void createPartControl(Composite parent) {
		Composite main = toolkit.createComposite(parent);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		main.setLayout(new GridLayout(2, false));
		
		ToolBar toolbar = new ToolBar(main, SWT.HORIZONTAL);
		toolbar.setBackground(main.getBackground());
		
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		toolkit.paintBordersFor(toolbar);
		
		ToolItem rest = new ToolItem(toolbar, SWT.PUSH);
		rest.setImage(Activator.getDefault().getImageRegistry().get(Icons.RESET));
		rest.setToolTipText(Messages.ViewCache_1);
		rest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fill();
			}
		});
		

		ToolItem clear = new ToolItem(toolbar, SWT.PUSH);
		clear.setImage(Activator.getDefault().getImageRegistry().get(Icons.CLEAR_CACHE));
		clear.setToolTipText(Messages.ViewCache_2);
		clear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (MessageDialog.openQuestion(getSite().getShell(), Messages.ViewCache_3, Messages.ViewCache_4)){
					try {
						cacheService.clearMemoryCache();
						fill();
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(getSite().getShell(), Messages.ViewCache_5, Messages.ViewCache_6 + e1.getMessage());
					}
					
				}
				
			}
		});
		
		Label l = toolkit.createLabel(main, Messages.ViewCache_7);
		l.setLayoutData(new GridData());
		
		maxSize = toolkit.createText(main, "", SWT.READ_ONLY); //$NON-NLS-1$
		maxSize.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		l = toolkit.createLabel(main, Messages.ViewCache_9);
		l.setLayoutData(new GridData());
		
		curSize = toolkit.createText(main, "", SWT.READ_ONLY); //$NON-NLS-1$
		curSize.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		

		l = toolkit.createLabel(main, Messages.ViewCache_11);
		l.setLayoutData(new GridData());
		
		remainingSize = toolkit.createText(main, "", SWT.READ_ONLY); //$NON-NLS-1$
		remainingSize.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		
		
		l = toolkit.createLabel(main, Messages.ViewCache_13);
		l.setLayoutData(new GridData());
		
		number = toolkit.createText(main, "", SWT.READ_ONLY); //$NON-NLS-1$
		number.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		l = toolkit.createLabel(main, Messages.ViewCache_15);
		l.setLayoutData(new GridData());
		
		hits = toolkit.createText(main, "", SWT.READ_ONLY); //$NON-NLS-1$
		hits.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		
		l = toolkit.createLabel(main, Messages.ViewCache_17);
		l.setLayoutData(new GridData());
		
		missed = toolkit.createText(main, "", SWT.READ_ONLY); //$NON-NLS-1$
		missed.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		
		viewer = new TreeViewer(toolkit.createTree(main, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL));
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				String s = ""; //$NON-NLS-1$
				if (element instanceof Schema){
					s = ((Schema)element).getName();
				}
				else if (element instanceof ICubeInstance){
					s = ((ICubeInstance)element).getCube().getName();
				}
				else if (element instanceof Dimension){
					s = ((Dimension)element).getName();
				}
				else if (element instanceof Hierarchy){
					s = ((Hierarchy)element).getName();
				}
				else if (element instanceof Level){
					Level l = (Level)element;
					
					s = l.getParentHierarchy().getParentDimension().getName() + " " + l.getParentHierarchy().getName() + " " + l.getName(); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else if (element instanceof Cube){
					s = ((Cube)element).getName();
				}
				else{
					s = element.toString();
				}
				return s;
			}
			
			@Override
			public Image getImage(Object element) {
				if (element instanceof Schema){
					return Activator.getDefault().getImageRegistry().get(Icons.SCHEMA);
				}
				else if (element instanceof ICubeInstance){
					return Activator.getDefault().getImageRegistry().get(Icons.CUBE);
				}
				else if (element instanceof Level){
					return Activator.getDefault().getImageRegistry().get(Icons.LEVEL);
				}
				else if (element instanceof Cube){
					return Activator.getDefault().getImageRegistry().get(Icons.CUBE);
				}
				else{
					return Activator.getDefault().getImageRegistry().get(Icons.ROOT);
				}
				
			}
		});
		viewer.setContentProvider(new ITreeContentProvider() {
//			private static final String dimensions = "Dimensions";
			private static final String schemas = "Schemas";
//			private static final String datas = "Datas";
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			@Override
			public void dispose() {
				
				
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				return new Object[]{schemas};
			}
			
			@Override
			public boolean hasChildren(Object element) {
				if (element instanceof String){
					return true;
				}
				else if (element instanceof Level){
					return false;
				}
				
				return true;
			}
			
			@Override
			public Object getParent(Object element) {
				
				
				return null;
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				List l = new ArrayList();
				
				try{
					if (parentElement == schemas){
						l.addAll(Activator.getDefault().getModelService().getLoadedSchema());
					}
					else if (parentElement instanceof Schema){
						
						l.addAll(((Schema)parentElement).getCubes());
						
						for(Dimension d : ((Schema)parentElement).getDimensions()){
							for(Hierarchy h : d.getHierarchies()){
								l.addAll(h.getLevels());
							}
						}
						
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
						
				
				return l.toArray(new Object[l.size()]);
			}
		});
		
		setMenu();
	}

	@Override
	public void setFocus() {
		
		
	}

	
	private void fill(){
		
		IUnitedOlapCacheManager cache = Activator.getDefault().getCacheService();
		this.cacheService = cache;
		if (cache == null){
			return;
		}
		MemoryCacheStatistics stats = null;
		
		try{
			stats = cache.getMemoryCacheStatistics();
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getSite().getShell(), Messages.ViewCache_23, Messages.ViewCache_24 + ex.getMessage());
		}
		curSize.setText(stats.getUsedSize() + ""); //$NON-NLS-1$
		maxSize.setText(stats.getMaxSize() + ""); //$NON-NLS-1$
		remainingSize.setText((stats.getMaxSize() - stats.getUsedSize()) + ""); //$NON-NLS-1$
		hits.setText(stats.getHits() + ""); //$NON-NLS-1$
		missed.setText(stats.getMissed() + ""); //$NON-NLS-1$
		number.setText(stats.getItemNumbers() + ""); //$NON-NLS-1$
		
		viewer.setInput(new Object());
		
	}
	
	private void setMenu(){
//		final Action removeFromCache = new Action("Remove"){
//			public void run(){
//				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
//				
//				Object o = ss.getFirstElement();
//			
//				try{
//					if (cacheService.unloadObject(o)){
//						MessageDialog.openInformation(getSite().getShell(), "Remove Object From Cache", "The object has been removed from Cache");
//						fill();
//					}
//					else{
//						MessageDialog.openInformation(getSite().getShell(), "Remove Object From Cache", "Nothing cached for this object.");
//					}
//				}catch(Exception ex){
//					ex.printStackTrace();
//					MessageDialog.openWarning(getSite().getShell(), "Remove Object From Cache", "Unable to uncache this object : \n" + ex.getMessage());
//				}
//				
//				
//				
//			}
//		};
//	
//		MenuManager mgr = new MenuManager();
//		mgr.add(removeFromCache);
//		mgr.addMenuListener(new IMenuListener() {
//			
//			@Override
//			public void menuAboutToShow(IMenuManager manager) {
//				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
//				
//				if (ss.isEmpty()){
//					removeFromCache.setEnabled(false);
//				}
//				
//				else{
//					removeFromCache.setEnabled(true);
//				}
//				
//			}
//		});
//	
//	
//		viewer.getControl().setMenu(mgr.createContextMenu(viewer.getControl()));
	}
}
