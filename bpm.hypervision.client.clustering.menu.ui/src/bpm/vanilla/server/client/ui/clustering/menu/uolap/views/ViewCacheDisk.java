package bpm.vanilla.server.client.ui.clustering.menu.uolap.views;



import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import bpm.united.olap.api.cache.CacheKey;
import bpm.united.olap.api.cache.ICacheEntry;
import bpm.united.olap.api.cache.impl.MemoryCacheStatistics;
import bpm.united.olap.remote.services.RemoteUnitedOlapCacheManager;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.server.client.ui.clustering.menu.Activator;
import bpm.vanilla.server.client.ui.clustering.menu.Messages;
import bpm.vanilla.server.client.ui.clustering.menu.icons.Icons;

public class ViewCacheDisk extends ViewPart{
	public ViewCacheDisk() {
	}
	public static final String ID = "bpm.vanilla.server.client.ui.clustering.menu.uolap.views.ViewCacheDisk"; //$NON-NLS-1$
	private static enum EntryField{
		FILE, CELLNUMBER, FILESIZE, CREATED, ACCESSED, MDX, GROUP_NAME;
	}
	
	private class CacheEntryLabelProvider extends ColumnLabelProvider{
		EntryField field;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); //$NON-NLS-1$
		DecimalFormat nf = new DecimalFormat("##.00k"); //$NON-NLS-1$
		public CacheEntryLabelProvider(EntryField f){
			field = f;
		}
		@Override
		public String getText(Object element) {
			ICacheEntry entry = null;
			
			try{
				entry = cacheManager.getDiskCacheEntry((CacheKey)element);
			}catch(Exception ex){
				
			}
			if (entry == null){
				return ""; //$NON-NLS-1$
			}
			switch (field) {
			case FILE:
				return entry.getFile().getAbsolutePath();
			case CELLNUMBER:
				return entry.getCellNumbers() + ""; //$NON-NLS-1$
			case FILESIZE:
				return (nf.format((float)entry.getEntrySize() / (1024.)));
			case CREATED:
				return sdf.format(entry.getModificationDate());
			case ACCESSED:
				if (entry.getLastAccess() == null){
					return ""; //$NON-NLS-1$
				}
				return sdf.format(entry.getLastAccess());
			case MDX:
				return entry.getMdx();	
			case GROUP_NAME:
				return entry.getGroupName();	
			}
			return super.getText(element);
		}
	}
	
	private TableViewer cacheDiskViewer;
	
//	private TreeViewer cacheMemoryViewer;
	private Text maxSize, curSize, number, remainingSize, hits, missed;
	
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	
	private RemoteUnitedOlapCacheManager cacheManager;
	
	@Override
	public void createPartControl(Composite parent) {
		
		Form form = formToolkit.createForm(parent);
		form.setImage(Activator.getDefault().getImageRegistry().get(Icons.UOLAP_CACHE));
		formToolkit.paintBordersFor(form);
		form.setText(Messages.ViewCacheDisk_0);
		form.getBody().setLayout(new GridLayout(1, false));
		formToolkit.decorateFormHeading(form);

		
		
		Composite main = formToolkit.createComposite(form.getBody());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout(3, false));
		
	
		
		
		
		
		formToolkit.paintBordersFor(main);
		
		
	
		
		
		
		Label l = formToolkit.createLabel(main, Messages.ViewCacheDisk_6);
		l.setLayoutData(new GridData());
		
		final ComboViewer clusterViewer = new ComboViewer(main, SWT.READ_ONLY);
		clusterViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		formToolkit.paintBordersFor(clusterViewer.getCombo());
		clusterViewer.setContentProvider(new ArrayContentProvider());
		clusterViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof IVanillaComponentIdentifier){
					IVanillaComponentIdentifier id = (IVanillaComponentIdentifier)element;
					if (id.getComponentUrl().equals(Activator.getDefault().getVanillaContext().getVanillaUrl())){
						return Messages.ViewCacheDisk_7 + id.getComponentUrl();
					}
					return id.getComponentUrl();
				}
				return super.getText(element);
			}
		});
		clusterViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()){
					cacheDiskViewer.setInput(Collections.EMPTY_LIST);
					return;
				}
				IVanillaComponentIdentifier id = (IVanillaComponentIdentifier)((IStructuredSelection)event.getSelection()).getFirstElement();
				
				cacheManager = new RemoteUnitedOlapCacheManager();
				cacheManager.init(
						id.getComponentUrl(), 
						Activator.getDefault().getVanillaContext().getLogin(), 
						Activator.getDefault().getVanillaContext().getPassword());
				
			
				fillCacheDiskDatas();
				fillCacheMemoryDatas();
			
;			}
		});
		
		Button cluster = formToolkit.createButton(main, "", SWT.PUSH); //$NON-NLS-1$
		cluster.setImage(Activator.getDefault().getImageRegistry().get(Icons.RESET));
		cluster.setToolTipText(Messages.ViewCacheDisk_9);
		cluster.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					List<IVanillaComponentIdentifier> l = new ArrayList<IVanillaComponentIdentifier>();
					
					for(IVanillaComponentIdentifier id : Activator.getDefault().getVanillaApi().getListenerService().getRegisteredComponents(VanillaComponentType.COMPONENT_UNITEDOLAP, false)){
						
						if (VanillaComponentType.COMPONENT_UNITEDOLAP.equals(id.getComponentNature())){
							l.add(id);
						}
					}
					
					if (clusterViewer != null && clusterViewer.getControl() != null && !clusterViewer.getControl().isDisposed()){
						clusterViewer.setInput(l);
					}
					clusterViewer.setInput(l);
					
					
				} catch (Exception ex) {
					
					ex.printStackTrace();
				}
			}
		});
		cluster.setBackground(formToolkit.getColors().getBackground());
	
		
		CTabFolder folder = new CTabFolder(main, SWT.BOTTOM);
		folder.setLayout(new GridLayout());
		folder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
	
		CTabItem it = new CTabItem(folder, SWT.NONE);
		it.setControl(createCacheDiskTable(folder));
		it.setText(Messages.ViewCacheDisk_10);
		
		it = new CTabItem(folder, SWT.NONE);
		it.setControl(createCacheMemoryTable(folder));
		it.setText(Messages.ViewCacheDisk_11);
		
		folder.setSelection(0);
	}
	
	
	private Control createCacheMemoryTable(Composite parent){
		Composite main = formToolkit.createComposite(parent);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		main.setLayout(new GridLayout(2, false));
		
		
		
		ToolBar toolbar = new ToolBar(main, SWT.HORIZONTAL);
		toolbar.setBackground(parent.getBackground());
		toolbar.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, true, false, 3, 1));
		toolbar.setBackground(main.getBackground());
		ToolItem rest = new ToolItem(toolbar, SWT.PUSH);
		rest.setImage(Activator.getDefault().getImageRegistry().get(Icons.RESET));
		rest.setToolTipText(Messages.ViewCacheDisk_12);
		rest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fillCacheMemoryDatas();
			}
		});
		
		
		ToolItem clear = new ToolItem(toolbar, SWT.PUSH);
		clear.setImage(Activator.getDefault().getImageRegistry().get(Icons.CLEAR_CACHE));
		clear.setToolTipText(Messages.ViewCacheDisk_13);
		clear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try{
					cacheManager.clearMemoryCache();
					fillCacheMemoryDatas();
					MessageDialog.openInformation(getSite().getShell(), Messages.ViewCacheDisk_14, Messages.ViewCacheDisk_15);
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ViewCacheDisk_16, Messages.ViewCacheDisk_17 + ex.getMessage());
				}
				
				
			}
		});
		
		

		formToolkit.paintBordersFor(toolbar);
		
		Label l = formToolkit.createLabel(main, Messages.ViewCacheDisk_18);
		l.setLayoutData(new GridData());
		
		maxSize = formToolkit.createText(main, "", SWT.READ_ONLY); //$NON-NLS-1$
		maxSize.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		maxSize.setEditable(false);
		
		l = formToolkit.createLabel(main, Messages.ViewCacheDisk_20);
		l.setLayoutData(new GridData());
		
		curSize = formToolkit.createText(main, "", SWT.READ_ONLY); //$NON-NLS-1$
		curSize.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		curSize.setEditable(false);
		

		l = formToolkit.createLabel(main, Messages.ViewCacheDisk_22);
		l.setLayoutData(new GridData());
		
		remainingSize = formToolkit.createText(main, "", SWT.READ_ONLY); //$NON-NLS-1$
		remainingSize.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		remainingSize.setEditable(false);
		
		
		l = formToolkit.createLabel(main, Messages.ViewCacheDisk_24);
		l.setLayoutData(new GridData());
		
		number = formToolkit.createText(main, "", SWT.READ_ONLY); //$NON-NLS-1$
		number.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		number.setEditable(false);
		
		l = formToolkit.createLabel(main, Messages.ViewCacheDisk_26);
		l.setLayoutData(new GridData());
		
		hits = formToolkit.createText(main, "", SWT.READ_ONLY); //$NON-NLS-1$
		hits.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		hits.setEditable(false);
		
		l = formToolkit.createLabel(main, Messages.ViewCacheDisk_28);
		l.setLayoutData(new GridData());
		
		missed = formToolkit.createText(main, "", SWT.READ_ONLY); //$NON-NLS-1$
		missed.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		missed.setEditable(false);
		

		return main;
	}
	
	private Control createCacheDiskTable(Composite parent){
		Composite main = formToolkit.createComposite(parent);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		
		
		
		ToolBar toolbar = new ToolBar(main, SWT.HORIZONTAL);
		toolbar.setBackground(parent.getBackground());
		toolbar.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, true, false));
		toolbar.setBackground(main.getBackground());
		ToolItem rest = new ToolItem(toolbar, SWT.PUSH);
		rest.setImage(Activator.getDefault().getImageRegistry().get(Icons.RESET));
		rest.setToolTipText(Messages.ViewCacheDisk_30);
		rest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					cacheDiskViewer.setInput(cacheManager.getDiskCacheKeys());
				} catch (Exception e1) {
					e1.printStackTrace();
					cacheDiskViewer.setInput(Collections.EMPTY_LIST);
				}
			}
		});
		
		
		ToolItem clear = new ToolItem(toolbar, SWT.PUSH);
		clear.setImage(Activator.getDefault().getImageRegistry().get(Icons.CLEAR_CACHE));
		clear.setToolTipText(Messages.ViewCacheDisk_31);
		clear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try{
					cacheManager.clearDiskCache();
					
					try {
						
						cacheDiskViewer.setInput(cacheManager.getDiskCacheKeys());
					} catch (Exception e1) {
						e1.printStackTrace();
						
					}
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ViewCacheDisk_32, Messages.ViewCacheDisk_33 + ex.getMessage());
				}
				
				
			}
		});
		
		

		formToolkit.paintBordersFor(toolbar);
		
		
		cacheDiskViewer = new TableViewer(formToolkit.createTable(main, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL));
		cacheDiskViewer.getTable().setHeaderVisible(true);
		cacheDiskViewer.getTable().setLinesVisible(true);
		cacheDiskViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		cacheDiskViewer.setContentProvider(new ArrayContentProvider());
		cacheDiskViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				Object cacheKey = ((IStructuredSelection)cacheDiskViewer.getSelection()).getFirstElement();
				if(cacheKey instanceof CacheKey) {
					ICacheEntry entry = null;
					try{
						entry = cacheManager.getDiskCacheEntry((CacheKey)cacheKey);
					} catch(Exception ex){ }
					
					MessageDialog.openInformation(getSite().getShell(), Messages.ViewCacheDisk_1, entry.getMdx());
				}
			}
		});
		
		/*
		 * create Columns
		 */
		
		TableViewerColumn col = new TableViewerColumn(cacheDiskViewer, SWT.LEFT);
		col.setLabelProvider(new CacheEntryLabelProvider(EntryField.FILE));
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.ViewCacheDisk_34);
		
		col = new TableViewerColumn(cacheDiskViewer, SWT.LEFT);
		col.setLabelProvider(new CacheEntryLabelProvider(EntryField.FILESIZE));
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.ViewCacheDisk_35);
		
		col = new TableViewerColumn(cacheDiskViewer, SWT.LEFT);
		col.setLabelProvider(new CacheEntryLabelProvider(EntryField.GROUP_NAME));
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.ViewCacheDisk_36);
		
		col = new TableViewerColumn(cacheDiskViewer, SWT.LEFT);
		col.setLabelProvider(new CacheEntryLabelProvider(EntryField.MDX));
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.ViewCacheDisk_37);
		
		col = new TableViewerColumn(cacheDiskViewer, SWT.LEFT);
		col.setLabelProvider(new CacheEntryLabelProvider(EntryField.CELLNUMBER));
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.ViewCacheDisk_38);
		
		col = new TableViewerColumn(cacheDiskViewer, SWT.LEFT);
		col.setLabelProvider(new CacheEntryLabelProvider(EntryField.CREATED));
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.ViewCacheDisk_39);
		
		col = new TableViewerColumn(cacheDiskViewer, SWT.LEFT);
		col.setLabelProvider(new CacheEntryLabelProvider(EntryField.ACCESSED));
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.ViewCacheDisk_40);
		
		
		
		
		createContextMenu();
		
		return main;
	}

	
	private void createContextMenu(){
		MenuManager mm = new MenuManager();
		
		final Action remove = new Action(Messages.ViewCacheDisk_41){
			public void run(){
				IStructuredSelection ss = (IStructuredSelection)cacheDiskViewer.getSelection();
				for(Object o : ss.toList()){
					try{
						cacheManager.removeFromCacheDisk((CacheKey)o);
					}catch(Exception ex){
						ex.printStackTrace();
					}
					
				}
				
				try {
					cacheDiskViewer.setInput(cacheManager.getDiskCacheKeys());
				} catch (Exception e1) {
					e1.printStackTrace();
					cacheDiskViewer.setInput(Collections.EMPTY_LIST);
				}
			}
		};
	
		mm.add(remove);
		mm.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				remove.setEnabled(!cacheDiskViewer.getSelection().isEmpty());
				
			}
		});
	
		cacheDiskViewer.getTable().setMenu(mm.createContextMenu(cacheDiskViewer.getTable()));
	}
	
	@Override
	public void setFocus() {
		
		
	}
	/**
	 * collect datas from UOLAP Cache component and 
	 * fill the widgets
	 */
	private void fillCacheDiskDatas(){
		try{
			cacheDiskViewer.setInput(cacheManager.getDiskCacheKeys());
		}catch(Exception ex){
			ex.printStackTrace();
			cacheDiskViewer.setInput(Collections.EMPTY_LIST);
			MessageDialog.openError(getSite().getShell(), Messages.ViewCacheDisk_42, Messages.ViewCacheDisk_43 + ex.getMessage());
		}
		
	}
	
	private void fillCacheMemoryDatas(){
		MemoryCacheStatistics stats = null;
		
		try{
			stats = cacheManager.getMemoryCacheStatistics();
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getSite().getShell(), Messages.ViewCacheDisk_44, Messages.ViewCacheDisk_45 + ex.getMessage());
		}
		curSize.setText(stats.getUsedSize() + ""); //$NON-NLS-1$
		maxSize.setText(stats.getMaxSize() + ""); //$NON-NLS-1$
		remainingSize.setText((stats.getMaxSize() - stats.getUsedSize()) + ""); //$NON-NLS-1$
		hits.setText(stats.getHits() + ""); //$NON-NLS-1$
		missed.setText(stats.getMissed() + ""); //$NON-NLS-1$
		number.setText(stats.getItemNumbers() + ""); //$NON-NLS-1$

	}

}
