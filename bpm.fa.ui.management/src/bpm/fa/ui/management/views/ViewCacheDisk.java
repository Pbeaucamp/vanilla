package bpm.fa.ui.management.views;



import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.logging.Logger;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import bpm.fa.ui.management.Activator;
import bpm.fa.ui.management.Messages;
import bpm.fa.ui.management.icons.Icons;
import bpm.united.olap.api.cache.CacheKey;
import bpm.united.olap.api.cache.ICacheEntry;

public class ViewCacheDisk extends ViewPart{
	public static final String ID = "bpm.fa.ui.management.views.ViewCacheDisk"; //$NON-NLS-1$
	private static enum EntryField{
		FILE, CELLNUMBER, FILESIZE, CREATED, ACCESSED, MDX, GROUP_NAME;
	}
	
	private static class CacheEntryLabelProvider extends ColumnLabelProvider{
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
				entry = Activator.getDefault().getCacheService().getDiskCacheEntry((CacheKey)element);
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
	
	private TableViewer viewer;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	@Override
	public void createPartControl(Composite parent) {
		Composite main = formToolkit.createComposite(parent);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout());
		
		ToolBar toolbar = new ToolBar(main, SWT.HORIZONTAL);
		toolbar.setBackground(parent.getBackground());
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		toolbar.setBackground(main.getBackground());
		
		formToolkit.paintBordersFor(toolbar);
		formToolkit.paintBordersFor(main);
		
		ToolItem rest = new ToolItem(toolbar, SWT.PUSH);
		rest.setImage(Activator.getDefault().getImageRegistry().get(Icons.RESET));
		rest.setToolTipText(Messages.ViewCacheDisk_6);
		rest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					viewer.setInput(Activator.getDefault().getCacheService().getDiskCacheKeys());
				} catch (Exception e1) {
					e1.printStackTrace();
					viewer.setInput(Collections.EMPTY_LIST);
				}
			}
		});
		
		
		ToolItem clear = new ToolItem(toolbar, SWT.PUSH);
		clear.setImage(Activator.getDefault().getImageRegistry().get(Icons.CLEAR_CACHE));
		clear.setToolTipText(Messages.ViewCacheDisk_7);
		clear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try{
					Activator.getDefault().getCacheService().clearDiskCache();
					
					try {
						
						viewer.setInput(Activator.getDefault().getCacheService().getDiskCacheKeys());
					} catch (Exception e1) {
						e1.printStackTrace();
						
					}
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ViewCacheDisk_8, Messages.ViewCacheDisk_9 + ex.getMessage());
				}
				
				
			}
		});
		
		
		CTabFolder folder = new CTabFolder(main, SWT.BOTTOM);
		folder.setLayout(new GridLayout());
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
	
		CTabItem it = new CTabItem(folder, SWT.NONE);
		it.setControl(createTable(folder));
		it.setText(Messages.ViewCacheDisk_10);
		
		folder.setSelection(0);
	}
	
	private Control createTable(Composite parent){
		viewer = new TableViewer(formToolkit.createTable(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL));
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new ArrayContentProvider());
		
		/*
		 * create Columns
		 */
		
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.LEFT);
		col.setLabelProvider(new CacheEntryLabelProvider(EntryField.FILE));
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.ViewCacheDisk_11);
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.setLabelProvider(new CacheEntryLabelProvider(EntryField.FILESIZE));
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.ViewCacheDisk_12);
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.setLabelProvider(new CacheEntryLabelProvider(EntryField.GROUP_NAME));
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.ViewCacheDisk_13);
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.setLabelProvider(new CacheEntryLabelProvider(EntryField.MDX));
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.ViewCacheDisk_14);
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.setLabelProvider(new CacheEntryLabelProvider(EntryField.CELLNUMBER));
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.ViewCacheDisk_15);
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.setLabelProvider(new CacheEntryLabelProvider(EntryField.CREATED));
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.ViewCacheDisk_16);
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.setLabelProvider(new CacheEntryLabelProvider(EntryField.ACCESSED));
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.ViewCacheDisk_17);
		
		
		
		
		createContextMenu();
		
		return viewer.getTable();
	}

	
	private void createContextMenu(){
		MenuManager mm = new MenuManager();
		
		final Action remove = new Action(Messages.ViewCacheDisk_18){
			public void run(){
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				for(Object o : ss.toList()){
					try{
						Activator.getDefault().getCacheService().removeFromCacheDisk((CacheKey)o);
					}catch(Exception ex){
						ex.printStackTrace();
					}
					
				}
				
				try {
					viewer.setInput(Activator.getDefault().getCacheService().getDiskCacheKeys());
				} catch (Exception e1) {
					e1.printStackTrace();
					viewer.setInput(Collections.EMPTY_LIST);
				}
			}
		};
	
		mm.add(remove);
		mm.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				remove.setEnabled(!viewer.getSelection().isEmpty());
				
			}
		});
	
		viewer.getTable().setMenu(mm.createContextMenu(viewer.getTable()));
	}
	
	@Override
	public void setFocus() {
		
		
	}

}
