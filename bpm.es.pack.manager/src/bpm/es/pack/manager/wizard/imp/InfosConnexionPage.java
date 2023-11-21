package bpm.es.pack.manager.wizard.imp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.swt.widgets.Label;

import adminbirep.Activator;
import bpm.birep.admin.client.preferences.PreferenceConstants;
import bpm.es.pack.manager.I18N.Messages;
import bpm.es.pack.manager.utils.ItemImage;
import bpm.es.pack.manager.utils.PackageCreator;
import bpm.es.pack.manager.vanillaplace.CompositeDatasourceJDBC;
import bpm.es.pack.manager.vanillaplace.CompositePlaceMapping;
import bpm.es.pack.manager.vanillaplace.ImportItemDatasource;
import bpm.vanilla.platform.core.IRepositoryApi;
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
import bpm.vanilla.workplace.core.model.PlaceImportDirectory;
import bpm.vanilla.workplace.core.model.PlaceImportItem;
import bpm.vanilla.workplace.core.model.VanillaPackage;

public class InfosConnexionPage extends WizardPage {

	private TableViewer tree, tableItems;
	private Composite compositeDatasource;
	private CompositePlaceMapping dataSourceComp;

	private VanillaPackage vanillaPackage;
	private String packageCustomName;
	private String packageLocation;

	private List<ImportItemDatasource> datasources;

	@Override
	public boolean isCurrentPage() {
		return super.isCurrentPage();
	}

	@Override
	public boolean canFlipToNextPage() {
		return true;
	}

	protected InfosConnexionPage(String pageName, VanillaPackage vanillaPackage, String packageCustomName) {
		super(pageName);
		this.vanillaPackage = vanillaPackage;
		this.packageCustomName = packageCustomName;

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		this.packageLocation = store.getString(PreferenceConstants.P_BPM_PACKAGES_FOLDER);
		if (packageLocation == null || packageLocation.isEmpty()) {
			this.packageLocation = "Packages"; //$NON-NLS-1$
		}
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2, true));

		Label lblDatasources = new Label(mainComposite, SWT.NONE);
		lblDatasources.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		lblDatasources.setText(Messages.InfosConnexionPage_0);

		Label lblConnections = new Label(mainComposite, SWT.NONE);
		lblConnections.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		lblConnections.setText(Messages.InfosConnexionPage_1);

		tree = new TableViewer(mainComposite, SWT.BORDER | SWT.V_SCROLL);
		tree.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		tree.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<ImportItemDatasource> it = new ArrayList<ImportItemDatasource>();
				for (ImportItemDatasource dsTmp : (Collection<ImportItemDatasource>) inputElement) {
					if (dsTmp.getDatasource().getType() == DatasourceType.DATASOURCE_JDBC) {
						it.add(dsTmp);
					}
				}
				return it.toArray(new ImportItemDatasource[it.size()]);
			}
		});
		tree.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((ImportItemDatasource) element).getDatasource().getName();
			}
		});
		tree.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (dataSourceComp != null && !dataSourceComp.isDisposed()) {
					dataSourceComp.dispose();
				}

				IStructuredSelection ss = (IStructuredSelection) tree.getSelection();
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof ImportItemDatasource)) {
					return;
				}

				ImportItemDatasource datasource = (ImportItemDatasource) ss.getFirstElement();

				if (datasource.getDatasource().getType() == DatasourceType.DATASOURCE_JDBC) {
					dataSourceComp = new CompositeDatasourceJDBC(compositeDatasource, SWT.NONE, datasource);
					dataSourceComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
					compositeDatasource.layout();

					List<PlaceImportItem> items = new ArrayList<PlaceImportItem>();
					for (PlaceImportItem item : datasource.getItems().keySet()) {
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
				Collection<PlaceImportItem> it = (Collection<PlaceImportItem>) inputElement;
				return it.toArray(new PlaceImportItem[it.size()]);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		});
		tableItems.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((PlaceImportItem) element).getItem().getItemName();
			}

			@Override
			public Image getImage(Object element) {
				ImageRegistry reg = Activator.getDefault().getImageRegistry();

				int type = ((PlaceImportItem) element).getItem().getType();
				return reg.get(ItemImage.getKeyForType(type, ((PlaceImportItem) element).getItem()));
			}
		});

		compositeDatasource = new Composite(mainComposite, SWT.NONE);
		compositeDatasource.setLayout(new GridLayout());
		compositeDatasource.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		setControl(mainComposite);
		setPageComplete(true);

		try {
			List<PlaceImportItem> items = getItems(vanillaPackage);
			buildDatasources(items);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<PlaceImportItem> getItems(VanillaPackage vanillaPackage) {
		List<PlaceImportItem> items = new ArrayList<PlaceImportItem>();
		if (vanillaPackage.getItems() != null) {
			items.addAll(vanillaPackage.getItems());
		}

		if (vanillaPackage.getDirectories() != null) {
			for (PlaceImportDirectory dir : vanillaPackage.getDirectories()) {
				items.addAll(getItems(dir));
			}
		}
		return items;
	}

	private List<PlaceImportItem> getItems(PlaceImportDirectory dir) {
		List<PlaceImportItem> items = new ArrayList<PlaceImportItem>();
		if (dir.getChildsItems() != null) {
			items.addAll(dir.getChildsItems());
		}

		if (dir.getChildsDir() != null) {
			for (PlaceImportDirectory d : dir.getChildsDir()) {
				items.addAll(getItems(d));
			}
		}
		return items;
	}

	public void buildDatasources(List<PlaceImportItem> items) throws Exception {
		datasources = new ArrayList<ImportItemDatasource>();
		for (PlaceImportItem item : items) {
			IDatasourceExtractor extractor = null;

			int type = item.getItem().getType();
			Integer subtype = item.getItem().getSubtype();
			switch (type) {
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
					if (subtype != null && subtype == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
						extractor = new BIRTDatasourceExtractor();
						break;
					}
			}

			if (extractor != null) {
				String packageName = packageCustomName != null ? packageCustomName : vanillaPackage.getName();
				
				String xml = readItemXML(packageName, item.getItem().getId());
				List<IDatasource> dsTmp = extractor.extractDatasources(xml);

				boolean isMainModel = true;
				if (item.getItem().getType() == IRepositoryApi.FD_TYPE) {
					isMainModel = FDModelExtractor.isMainModel(xml);
				}
				item.setXml(xml);
				item.setIsMainModel(isMainModel);
				item.setDatasources(dsTmp);

				for (IDatasource ds : dsTmp) {
					boolean found = false;
					for (ImportItemDatasource itemDs : datasources) {
						if (itemDs.isEqualTo(ds)) {
							itemDs.addItemParent(item, ds);
							found = true;
							break;
						}
					}
					if (!found) {
						ImportItemDatasource itemDatasource = new ImportItemDatasource(ds, item);
						datasources.add(itemDatasource);
					}
				}
			}
			else {
				String packageName = packageCustomName != null ? packageCustomName : vanillaPackage.getName();
				
				String xml = readItemXML(packageName, item.getItem().getId());
				item.setXml(xml);
			}
		}

		tree.setInput(datasources);
	}

	private String readItemXML(String packageName, int itemId) throws Exception {
		ZipFile zipFile = new ZipFile(packageLocation + File.separator + packageName + PackageCreator.VANILLA_PACKAGE_EXTENSION);

		ZipEntry zipEntry = zipFile.getEntry(String.valueOf(itemId));
		if (zipEntry != null) {
			InputStream is = zipFile.getInputStream(zipEntry);
			return IOUtils.toString(is, "UTF-8"); //$NON-NLS-1$
		}
		return null; //$NON-NLS-1$
	}

	public static final void copyInputStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}

	public List<ImportItemDatasource> getItemWithDatasource() {
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
