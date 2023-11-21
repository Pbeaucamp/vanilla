package bpm.vanilla.server.client.ui.clustering.menu.uolap.views;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import bpm.united.olap.api.cache.CacheKey;
import bpm.united.olap.api.cache.ICacheEntry;
import bpm.united.olap.api.tools.CacheIndexRestorer;
import bpm.united.olap.remote.services.RemoteUnitedOlapCacheManager;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.UOlapQueryBean;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.utils.MD5Helper;
import bpm.vanilla.server.client.ui.clustering.menu.Activator;
import bpm.vanilla.server.client.ui.clustering.menu.Messages;
import bpm.vanilla.server.client.ui.clustering.menu.icons.Icons;

public class ViewTransfertCache extends ViewPart{
	
	private HashMap<String, String> groupNameMd5Matching = new HashMap<String, String>(); 
	
	public static final String ID = "bpm.vanilla.server.client.ui.clustering.menu.uolap.views.ViewTransfertCache"; //$NON-NLS-1$
	
	private static enum ColumnType{
		SCHEMA, REPOSITORY, ITEM, GROUP, MDX;
	}
	
	private static class ClusterLabelProvider extends LabelProvider{
		@Override
		public String getText(Object element) {
			if (element instanceof IVanillaComponentIdentifier){
				IVanillaComponentIdentifier id = (IVanillaComponentIdentifier)element;
				if (id.getComponentUrl().equals(Activator.getDefault().getVanillaContext().getVanillaUrl())){
					return Messages.ViewTransfertCache_1 + id.getComponentUrl();
				}
				return id.getComponentUrl();
			}
			return super.getText(element);
		}
	}
	
	private class CacheLabelProvider extends ColumnLabelProvider{
		private ColumnType type;
		public CacheLabelProvider(ColumnType type){
			this.type = type;
		}
		
		
		@Override
		public String getText(Object element) {
			CacheKey key = (CacheKey)element;
			
			switch(type){
			case GROUP:
				try{
					String s =  unitedOlapRemoteSource.getDiskCacheEntry(key).getGroupName();
					if (Messages.ViewTransfertCache_2.equals(s)){
						throw new Exception();
					}
					return s;
				}catch(Exception ex){
					
					try{
						if (groupNameMd5Matching.get(key.getGroupId()) == null){
							for(bpm.vanilla.platform.core.beans.Group g : Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups()){
								String mkey = MD5Helper.encode(g.getName());
								groupNameMd5Matching.put(mkey , g.getName());
								if (mkey.equals(key.getGroupId())){
									return g.getName();
								}
							}
						}
						else{
							return groupNameMd5Matching.get(key.getGroupId()) ;
						}
					}catch(Exception e){
						
					}
					return key.getGroupId();
				}
				
			case MDX:
				String s = null;
				try{
					s = unitedOlapRemoteSource.getDiskCacheEntry(key).getMdx();
					if (Messages.ViewTransfertCache_3.equals(s)){
						throw new Exception();
					}
					return s;
				}catch(Exception ex){
					
					try{
						if (groupNameMd5Matching.get(key.getQueryId()) == null){
							for(UOlapQueryBean b : Activator.getDefault().getVanillaApi().getVanillaLoggingManager().getUolapQueries()){
								String mkey = MD5Helper.encode(b.getMdxQuery());
								groupNameMd5Matching.put(mkey , b.getMdxQuery());
								if (mkey.equals(key.getQueryId())){
									return b.getMdxQuery();
								}
							}
						}
						else{
							return groupNameMd5Matching.get(key.getQueryId());
						}
						
					}catch(Exception e){
						e.printStackTrace();
					}
					
					
					return s == null ? key.getQueryId() : s;
				}
				
			case SCHEMA:
				return key.getSchemaId();
			case REPOSITORY:
				try{
//					if (unitedOlapRemoteSource != null){
//					IObjectIdentifier id = unitedOlapSourceApi.getModelProvider().getSchemaObjectIdentifier(key.getSchemaId());
//					if (id  != null){
//						return id.getRepositoryId() + "";
//					}
//				}
					try{
						Integer repId = Integer.parseInt(key.getRepositoryId());
						Repository rep = Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().getRepositoryById(repId);
						return rep.getName() + " (" + rep.getId() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					}catch(Exception ex){
						return ""; //$NON-NLS-1$
					}
					

				}catch(Exception e){
					
				}
				return ""; //$NON-NLS-1$
			case ITEM:
//				try{
//					if (unitedOlapRemoteSource != null){
//						IObjectIdentifier id = unitedOlapSourceApi.getModelProvider().getSchemaObjectIdentifier(key.getSchemaId());
//						if (id  != null){
//							return id.getDirectoryItemId() + "";
//						}
//					}
//				}catch(Exception e){
//					
//				}
				return key.getDirectoryItemId();
				
			}
			
			
			return super.getText(element);
		}
	}
	
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private TableViewer sourceCacheViewer;
	private ComboViewer sourceClusterViewer, targetClusterViewer;
	
	private RemoteUnitedOlapCacheManager unitedOlapRemoteSource;
//	private IServiceProvider unitedOlapSourceApi;
	
	private RemoteUnitedOlapCacheManager unitedOlapRemoteTarget;
	
	private boolean isCluster = true;
	private Composite sourceDefComposite;
	private Composite parentSource;
	private Text folderCache;
	private ISelectionChangedListener sourceSelectionListener;
	
	private HashMap<CacheKey, ICacheEntry> map;
	
	public ViewTransfertCache() {
	}
	
	
	@Override
	public void createPartControl(Composite parent) {
		Form frmUnitedolapModelsManagement = formToolkit.createForm(parent);
		frmUnitedolapModelsManagement.setImage(Activator.getDefault().getImageRegistry().get(Icons.CACHE_TRANSFERT));
		
		formToolkit.paintBordersFor(frmUnitedolapModelsManagement);
		frmUnitedolapModelsManagement.setText(Messages.ViewTransfertCache_8);
		frmUnitedolapModelsManagement.getBody().setLayout(new GridLayout(1, false));
		formToolkit.decorateFormHeading(frmUnitedolapModelsManagement);
		
		Composite compositeForm = new Composite(frmUnitedolapModelsManagement.getBody(), SWT.NONE);
		compositeForm.setLayout(new GridLayout(2, false));
		compositeForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		formToolkit.adapt(compositeForm);
		formToolkit.paintBordersFor(compositeForm);
		
		
		ToolBar tb = createToolbar(compositeForm);
		tb.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, true,1,1));
		tb.setBackground(formToolkit.getColors().getBackground());
		formToolkit.paintBordersFor(tb);
		
		Composite composite = new Composite(compositeForm, SWT.NONE);
		composite.setLayout(new GridLayout(2, true));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		formToolkit.adapt(composite);
//		formToolkit.paintBordersFor(composite);
		
		Section sctnCacheDiskSource = formToolkit.createSection(composite, Section.EXPANDED | Section.TWISTIE | Section.TITLE_BAR);
		sctnCacheDiskSource.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		formToolkit.paintBordersFor(sctnCacheDiskSource);
		sctnCacheDiskSource.setText(Messages.ViewTransfertCache_9);
		
		Composite composite_2 = new Composite(sctnCacheDiskSource, SWT.NONE);
		formToolkit.adapt(composite_2);
		formToolkit.paintBordersFor(composite_2);
		sctnCacheDiskSource.setClient(composite_2);
		composite_2.setLayout(new GridLayout(2, false));
		
		Group grpSourcetype = new Group(composite_2, SWT.NONE);
		grpSourcetype.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));
		grpSourcetype.setText(Messages.ViewTransfertCache_10);
		formToolkit.adapt(grpSourcetype);
		formToolkit.paintBordersFor(grpSourcetype);
		grpSourcetype.setLayout(new GridLayout(1, false));
		
		final Button btnCluster = new Button(grpSourcetype, SWT.RADIO);
		btnCluster.setSelection(true);
		formToolkit.adapt(btnCluster, true, true);
		btnCluster.setText(Messages.ViewTransfertCache_11);
		
		Button btnFilesystem = new Button(grpSourcetype, SWT.RADIO);
//		btnFilesystem.setEnabled(false);
		formToolkit.adapt(btnFilesystem, true, true);
		btnFilesystem.setText(Messages.ViewTransfertCache_12);

		
		/*
		 * dynamicall Source Composite
		 * 
		 */
		parentSource = composite_2;
		sourceDefComposite = new Composite(composite_2, SWT.NONE);
		sourceDefComposite.setLayout(new GridLayout(3, false));
		sourceDefComposite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		
		Label lblSource = new Label(sourceDefComposite, SWT.NONE);
		lblSource.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSource.setBounds(0, 0, 55, 15);
		formToolkit.adapt(lblSource, true, true);
		lblSource.setText(Messages.ViewTransfertCache_13);
		
		sourceClusterViewer = new ComboViewer(sourceDefComposite, SWT.READ_ONLY);
		sourceClusterViewer.setContentProvider(new ArrayContentProvider());
		sourceClusterViewer.setLabelProvider(new ClusterLabelProvider());
		
		Combo combo_1 = sourceClusterViewer.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		combo_1.setBackground(formToolkit.getColors().getBackground());
		formToolkit.paintBordersFor(combo_1);
		
		
		Section sctnTransfertTarget = formToolkit.createSection(composite, Section.EXPANDED | Section.TWISTIE | Section.TITLE_BAR);
		sctnTransfertTarget.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		formToolkit.paintBordersFor(sctnTransfertTarget);
		sctnTransfertTarget.setText(Messages.ViewTransfertCache_14);
		
		Composite composite_1 = new Composite(sctnTransfertTarget, SWT.NONE);
		formToolkit.adapt(composite_1);
		formToolkit.paintBordersFor(composite_1);
		sctnTransfertTarget.setClient(composite_1);
		composite_1.setLayout(new GridLayout(2, false));
		
		Label lblTarget = new Label(composite_1, SWT.NONE);
		formToolkit.adapt(lblTarget, true, true);
		lblTarget.setText(Messages.ViewTransfertCache_15);
		
		targetClusterViewer = new ComboViewer(composite_1, SWT.NONE);
		targetClusterViewer.setContentProvider(new ArrayContentProvider());
		targetClusterViewer.setLabelProvider(new ClusterLabelProvider());
		
		Combo combo = targetClusterViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		combo.setBackground(formToolkit.getColors().getBackground());
		formToolkit.paintBordersFor(combo);

		
		Section sctnTransfert = formToolkit.createSection(frmUnitedolapModelsManagement.getBody(), Section.EXPANDED | Section.TITLE_BAR);
		sctnTransfert.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		formToolkit.paintBordersFor(sctnTransfert);
		sctnTransfert.setText(Messages.ViewTransfertCache_16);
		
		Composite composite_3 = new Composite(sctnTransfert, SWT.NONE);
		formToolkit.adapt(composite_3);
		formToolkit.paintBordersFor(composite_3);
		sctnTransfert.setClient(composite_3);
		composite_3.setLayout(new GridLayout(1, false));
		
		sourceCacheViewer = new TableViewer(composite_3, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		Table table = sourceCacheViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setSize(551, 229);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(table);
		
		
		/*
		 * create TableViewer
		 */
		sourceCacheViewer.setContentProvider(new ArrayContentProvider());
		
		TableViewerColumn col = new TableViewerColumn(sourceCacheViewer, SWT.NONE);
		col.getColumn().setText(Messages.ViewTransfertCache_17);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new CacheLabelProvider(ColumnType.SCHEMA));
		
		col = new TableViewerColumn(sourceCacheViewer, SWT.NONE);
		col.getColumn().setText(Messages.ViewTransfertCache_18);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new CacheLabelProvider(ColumnType.REPOSITORY));
		
		col = new TableViewerColumn(sourceCacheViewer, SWT.NONE);
		col.getColumn().setText(Messages.ViewTransfertCache_19);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new CacheLabelProvider(ColumnType.ITEM));
		
		col = new TableViewerColumn(sourceCacheViewer, SWT.NONE);
		col.getColumn().setText(Messages.ViewTransfertCache_20);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new CacheLabelProvider(ColumnType.GROUP));
		
		col = new TableViewerColumn(sourceCacheViewer, SWT.NONE);
		col.getColumn().setText(Messages.ViewTransfertCache_21);
		col.getColumn().setWidth(500);
		col.setLabelProvider(new CacheLabelProvider(ColumnType.MDX));
		
	
		
		
		/*
		 * listeners
		 */
		
		sourceSelectionListener = new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (sourceClusterViewer.getSelection().isEmpty()){
					sourceCacheViewer.setInput(Collections.EMPTY_LIST);
//					unitedOlapSourceApi = null;
					unitedOlapRemoteSource = null;
				}
				else{
					
					IVanillaComponentIdentifier id = (IVanillaComponentIdentifier)((IStructuredSelection)sourceClusterViewer.getSelection()).getFirstElement();
					unitedOlapRemoteSource = new RemoteUnitedOlapCacheManager();
					unitedOlapRemoteSource.init(id.getComponentUrl(),
							Activator.getDefault().getVanillaContext().getLogin(),
							Activator.getDefault().getVanillaContext().getPassword());
					
//					unitedOlapSourceApi = new RemoteServiceProvider();
//					unitedOlapSourceApi.configure(Activator.getDefault().getVanillaContext());
					
					try{
						sourceCacheViewer.setInput(unitedOlapRemoteSource.getDiskCacheKeys());
					}catch(Exception ex){
						ex.printStackTrace();
						sourceCacheViewer.setInput(Collections.EMPTY_LIST);
						MessageDialog.openError(getSite().getShell(), Messages.ViewTransfertCache_22, Messages.ViewTransfertCache_23 + ex.getMessage());
					}
				}
				
			}
		};
		sourceClusterViewer.addSelectionChangedListener(sourceSelectionListener);
	
		targetClusterViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (targetClusterViewer.getSelection().isEmpty()){
									
				}
				
			}
		});
	
		
		SelectionListener radioListener = new SelectionAdapter() {
					
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean b = btnCluster.getSelection();
				updateSourceComposite(b);
				
			}
			
			
		};
		btnCluster.addSelectionListener(radioListener);
		btnFilesystem.addSelectionListener(radioListener);
	}

	
	
	private void updateSourceComposite(boolean isCluster){
		if (this.isCluster != isCluster){
			if (sourceDefComposite != null &&! sourceDefComposite.isDisposed()){
				sourceDefComposite.dispose();
			}
			
			sourceDefComposite = new Composite(parentSource, SWT.NONE);
			sourceDefComposite.setLayout(new GridLayout(3, false));
			sourceDefComposite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

			if (isCluster){
								
				Label lblSource = new Label(sourceDefComposite, SWT.NONE);
				lblSource.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				lblSource.setBounds(0, 0, 55, 15);
				formToolkit.adapt(lblSource, true, true);
				lblSource.setText(Messages.ViewTransfertCache_24);
				
				sourceClusterViewer = new ComboViewer(sourceDefComposite, SWT.READ_ONLY);
				sourceClusterViewer.setContentProvider(new ArrayContentProvider());
				sourceClusterViewer.setLabelProvider(new ClusterLabelProvider());
				
				Combo combo_1 = sourceClusterViewer.getCombo();
				combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
				formToolkit.paintBordersFor(combo_1);
				
				sourceClusterViewer.addSelectionChangedListener(sourceSelectionListener);
				
				this.isCluster = true;
			}
			else{
				
				Label lblSource = formToolkit.createLabel(sourceDefComposite, Messages.ViewTransfertCache_25, SWT.NONE);
				lblSource.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
				lblSource.setBounds(0, 0, 55, 15);
				formToolkit.adapt(lblSource, true, true);
				
				folderCache = formToolkit.createText(sourceDefComposite, "", SWT.READ_ONLY | SWT.BORDER); //$NON-NLS-1$
				folderCache.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
				
				Button browse = formToolkit.createButton(sourceDefComposite, "...", SWT.PUSH); //$NON-NLS-1$
				browse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
				browse.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						DirectoryDialog fd = new DirectoryDialog(getSite().getShell());
						fd.setFilterPath("uolap"); //$NON-NLS-1$
						String s = fd.open();
						if (s != null){
							
							CacheIndexRestorer restorer = new CacheIndexRestorer();
							
							try{
								map = restorer.restoreFromFolder(new File(s));
								sourceCacheViewer.setInput(map.keySet());
								folderCache.setText(s);
							}catch(Exception ex){
								sourceCacheViewer.setInput(Collections.EMPTY_LIST);
								MessageDialog.openError(getSite().getShell(), Messages.ViewTransfertCache_29, Messages.ViewTransfertCache_30 + ex.getMessage());
								folderCache.setText(""); //$NON-NLS-1$
							}
							
							
						}
					}
				});
				this.isCluster = false;
			}
			
			parentSource.layout();
		}
	}
	
	
	private ToolBar createToolbar(Composite parent){
		
		Action connect = new Action(""){ //$NON-NLS-1$
			public void run(){
				
				if (Activator.getDefault().getVanillaContext() == null){
					MessageDialog.openInformation(getSite().getShell(), Messages.ViewTransfertCache_33, Messages.ViewTransfertCache_34);
					return;
				}
				
				fillViewers();
				
			}
		};
		connect.setToolTipText(Messages.ViewTransfertCache_35);
		connect.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.RESET));
		
		Action append = new Action(""){ //$NON-NLS-1$
			public void run(){
				
				if (targetClusterViewer.getSelection().isEmpty()){
					return;
				}
				
				IVanillaComponentIdentifier id = (IVanillaComponentIdentifier)((IStructuredSelection)targetClusterViewer.getSelection()).getFirstElement();
				unitedOlapRemoteTarget = new RemoteUnitedOlapCacheManager();
				unitedOlapRemoteTarget.init(id.getComponentUrl(),
						Activator.getDefault().getVanillaContext().getLogin(),
						Activator.getDefault().getVanillaContext().getPassword());
				
				
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ZipOutputStream zos = new ZipOutputStream(bos);
				if (isCluster){
					for(Object o : ((IStructuredSelection)sourceCacheViewer.getSelection()).toList()){
						try{
							zos.putNextEntry(new ZipEntry(((CacheKey)o).toString()));
							
							IOWriter.write(unitedOlapRemoteSource.loadCacheEntry((CacheKey)o), zos, true, false);

						}catch(Exception ex){
							ex.printStackTrace();
							MessageDialog.openError(getSite().getShell(), "", Messages.ViewTransfertCache_38 + ex.getMessage()); //$NON-NLS-1$
						}
						
					
					}

					try {
						zos.close();
						
					} catch (Exception e) {
						
						e.printStackTrace();
					}

				}
				else{
					try{
						for(Object o : ((IStructuredSelection)sourceCacheViewer.getSelection()).toList()){
							zos.putNextEntry(new ZipEntry(((CacheKey)o).toString()));
							IOWriter.write(new FileInputStream(map.get(o).getFile()), zos, true, false);
						}
						
						zos.close();
					}catch(Exception ex){
						ex.printStackTrace();
						MessageDialog.openError(getSite().getShell(), "", Messages.ViewTransfertCache_41 + ex.getMessage()); //$NON-NLS-1$
					}
				}
				
				
				try{
					
					unitedOlapRemoteTarget.appendToCacheDisk(new ByteArrayInputStream(bos.toByteArray()), false);
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ViewTransfertCache_42, Messages.ViewTransfertCache_43 + ex.getMessage());
				}
			}
		};
		append.setToolTipText(Messages.ViewTransfertCache_44);
		append.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.CACHE_TRANSFERT));

		ToolBarManager mgr = new ToolBarManager(SWT.FLAT |  SWT.VERTICAL);
		
		
		mgr.add(connect);
		mgr.add(append);
		return mgr.createControl(parent);

		
	}
	
	
	
	private void fillViewers(){
		try {
			List<IVanillaComponentIdentifier> l = new ArrayList<IVanillaComponentIdentifier>();
			
			for(IVanillaComponentIdentifier id : Activator.getDefault().getVanillaApi().getListenerService().getRegisteredComponents(VanillaComponentType.COMPONENT_UNITEDOLAP, false)){
				
				if (VanillaComponentType.COMPONENT_UNITEDOLAP.equals(id.getComponentNature())){
					l.add(id);
				}
			}
			
			if (sourceCacheViewer != null && sourceClusterViewer.getControl() != null && !sourceClusterViewer.getControl().isDisposed()){
				sourceClusterViewer.setInput(l);
			}
			targetClusterViewer.setInput(l);
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	@Override
	public void setFocus() {
		
		
	}
}
