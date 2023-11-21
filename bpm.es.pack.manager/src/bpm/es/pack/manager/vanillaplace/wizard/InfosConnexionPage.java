package bpm.es.pack.manager.vanillaplace.wizard;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import adminbirep.Activator;
import adminbirep.icons.Icons;
import bpm.es.pack.manager.vanillaplace.CompositeDatasourceJDBC;
import bpm.es.pack.manager.vanillaplace.CompositePlaceMapping;
import bpm.es.pack.manager.vanillaplace.ImportItemDatasource;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.workplace.api.datasource.extractor.BIGDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.BIRTDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.BIWDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.FASDDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.FAVDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.FDDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.FDDicoDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.FDModelExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.FMDTDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.FWRDatasourceExtractor;
import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasourceExtractor;
import bpm.vanilla.workplace.core.datasource.IDatasourceType.DatasourceType;
import bpm.vanilla.workplace.core.model.PlaceImportItem;

public class InfosConnexionPage extends WizardPage {
	
	private TableViewer tree, tableItems;
	private Composite compositeDatasource;
	private CompositePlaceMapping dataSourceComp;

	private String path;
	
	private List<PlaceImportItem> importItems;
	private List<ImportItemDatasource> datasources;
	
	protected InfosConnexionPage(String pageName, List<PlaceImportItem> importItems, String path) {
		super(pageName);
		this.importItems = importItems;
		this.path = path;
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2, true));
		
		tree = new TableViewer(mainComposite, SWT.BORDER | SWT.V_SCROLL);
		tree.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		tree.setContentProvider(new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
			@Override
			public void dispose() { }
			
			@Override
			public Object[] getElements(Object inputElement) {
				List<ImportItemDatasource> it = new ArrayList<ImportItemDatasource>();
				for(ImportItemDatasource dsTmp : (Collection<ImportItemDatasource>)inputElement){
					if(dsTmp.getDatasource().getType() == DatasourceType.DATASOURCE_JDBC){
						it.add(dsTmp);
					}
				}
				return it.toArray(new ImportItemDatasource[it.size()]);
			}
		});
		tree.setLabelProvider(new LabelProvider(){
			
			@Override
			public String getText(Object element) {
				return ((ImportItemDatasource)element).getDatasource().getName();
			}
		});
		tree.addSelectionChangedListener(new ISelectionChangedListener(){

			@Override
			public void selectionChanged(SelectionChangedEvent event) {				
				if (dataSourceComp != null && !dataSourceComp.isDisposed()){
					dataSourceComp.dispose();
				}
				
				IStructuredSelection ss = (IStructuredSelection)tree.getSelection();
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof ImportItemDatasource)){
					return;
				}
				
				ImportItemDatasource datasource = (ImportItemDatasource)ss.getFirstElement();
				
				if(datasource.getDatasource().getType() == DatasourceType.DATASOURCE_JDBC){
					dataSourceComp = new CompositeDatasourceJDBC(compositeDatasource, SWT.NONE, 
							datasource);
					dataSourceComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
					compositeDatasource.layout();
					
					List<PlaceImportItem> items = new ArrayList<PlaceImportItem>();
					for(PlaceImportItem item : datasource.getItems().keySet()){
						items.add(item);
					}
					tableItems.setInput(items);
				}
				else {
					compositeDatasource.layout();
				}
			}
			
		});
		
		tableItems = new TableViewer(mainComposite, SWT.BORDER | SWT.V_SCROLL);
		tableItems.getTable().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		tableItems.setContentProvider(new IStructuredContentProvider() {

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				Collection<PlaceImportItem> it = (Collection<PlaceImportItem>)inputElement;
				return it.toArray(new PlaceImportItem[it.size()]);
			}

			public void dispose() { }

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
		});
		tableItems.setLabelProvider(new LabelProvider(){
			
			@Override
			public String getText(Object element) {
				return ((PlaceImportItem)element).getItem().getItemName();
			}

			@Override
			public Image getImage(Object element) {
				int type = ((PlaceImportItem)element).getItem().getType();
				Integer subtype = ((PlaceImportItem)element).getItem().getSubtype();
				ImageRegistry reg = Activator.getDefault().getImageRegistry();
				switch(type){
				case IRepositoryApi.FASD_TYPE:
					return reg.get("fasd"); //$NON-NLS-1$
				case IRepositoryApi.FD_TYPE:
					return reg.get("fd"); //$NON-NLS-1$
				case IRepositoryApi.FD_DICO_TYPE:
					return reg.get("dico"); //$NON-NLS-1$
				case IRepositoryApi.FMDT_TYPE:
					return reg.get("fmdt"); //$NON-NLS-1$
				case IRepositoryApi.GTW_TYPE:
					return reg.get("gtw"); //$NON-NLS-1$
				case IRepositoryApi.GED_TYPE:
					return reg.get(Icons.TEMP_GED);
				case IRepositoryApi.BIW_TYPE:
					return reg.get(Icons.BIW);
				case IRepositoryApi.EXTERNAL_DOCUMENT:
					return reg.get(Icons.EXT_DOC);
				case IRepositoryApi.FWR_TYPE:
					return reg.get(Icons.FWR);
				case IRepositoryApi.FAV_TYPE:
					return reg.get("fav"); //$NON-NLS-1$
				case IRepositoryApi.CUST_TYPE:
					if(subtype != null && subtype == IRepositoryApi.BIRT_REPORT_SUBTYPE){
						return reg.get(Icons.BIRT);
					}
				}
				return reg.get(Icons.DEFAULT); //$NON-NLS-1$
			}
		});
		
		compositeDatasource = new Composite(mainComposite, SWT.NONE);
		compositeDatasource.setLayout(new GridLayout());
		compositeDatasource.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		setControl(mainComposite);
		setPageComplete(true);
		
		try {
			buildDatasources(importItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void buildDatasources(List<PlaceImportItem> importItems) throws Exception{
		datasources = new ArrayList<ImportItemDatasource>();
		for(PlaceImportItem item : importItems){
			IDatasourceExtractor extractor = null;

			int type = item.getItem().getType();
			Integer subtype = item.getItem().getSubtype();
			switch(type){
			case IRepositoryApi.FASD_TYPE:
				extractor = new FASDDatasourceExtractor();
				break;
			case IRepositoryApi.FD_TYPE:
				extractor = new FDDatasourceExtractor();
				break;
			case IRepositoryApi.FD_DICO_TYPE:
				extractor = new FDDicoDatasourceExtractor();
				break;
			case IRepositoryApi.FMDT_TYPE:
				extractor = new FMDTDatasourceExtractor();
				break;
			case IRepositoryApi.GTW_TYPE:
				extractor = new BIGDatasourceExtractor();
				break;
			case IRepositoryApi.GED_TYPE:
				break;
			case IRepositoryApi.BIW_TYPE:
				extractor = new BIWDatasourceExtractor();
				break;
			case IRepositoryApi.EXTERNAL_DOCUMENT:
				break;
			case IRepositoryApi.FAV_TYPE:
				extractor = new FAVDatasourceExtractor();
				break;
			case IRepositoryApi.FWR_TYPE:
				extractor = new FWRDatasourceExtractor();
				break;
			case IRepositoryApi.CUST_TYPE:
				if(subtype != null && subtype == IRepositoryApi.BIRT_REPORT_SUBTYPE){
					extractor = new BIRTDatasourceExtractor();
					break;
				}
			}
			
			if(extractor != null){
				String xml = readItemXML(path, item.getItem().getId());
				List<IDatasource> dsTmp = extractor.extractDatasources(xml);
				
				boolean isMainModel = true;
				if(item.getItem().getType() == IRepositoryApi.FD_TYPE){
					isMainModel = FDModelExtractor.isMainModel(xml);
				}
				item.setXml(xml);
				item.setIsMainModel(isMainModel);
				item.setDatasources(dsTmp);
				
				for(IDatasource ds : dsTmp){
					boolean found = false;
					for(ImportItemDatasource itemDs : datasources){
						if(itemDs.isEqualTo(ds)){
							itemDs.addItemParent(item, ds);
							found = true;
							break;
						}
					}
					if(!found){
						ImportItemDatasource itemDatasource = new ImportItemDatasource(ds, item);
						datasources.add(itemDatasource);
					}
				}
			}
		}
		
		tree.setInput(datasources);
	}
	
	private String readItemXML(String path, int itemId) throws Exception{
		ZipFile zipFile = new ZipFile(path);
		
		ZipEntry zipEntry = zipFile.getEntry(String.valueOf(itemId));
		if(zipEntry != null){
			InputStream is = zipFile.getInputStream(zipEntry);
			return IOUtils.toString(is, "UTF-8"); //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}
	
	public List<ImportItemDatasource> getItemWithDatasource(){
		return datasources;
	}

	@Override
	public boolean isPageComplete() {
		return true;
	}
	
	@Override
	public IWizardPage getNextPage() {
		return super.getNextPage();
	}

}
