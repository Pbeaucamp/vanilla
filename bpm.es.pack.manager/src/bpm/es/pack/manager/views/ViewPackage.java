package bpm.es.pack.manager.views;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import bpm.birep.admin.client.dialog.DialogText;
import bpm.birep.admin.client.preferences.PreferenceConstants;
import bpm.es.pack.manager.I18N.Messages;
import bpm.es.pack.manager.imp.CompositeImportItem;
import bpm.es.pack.manager.imp.CompositePackageDetails;
import bpm.es.pack.manager.imp.DialogImporter;
import bpm.es.pack.manager.imp.Importer;
import bpm.es.pack.manager.imp.PackageManager;
import bpm.es.pack.manager.utils.PackageImporter;
import bpm.es.pack.manager.wizard.imp.ImportWizard;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.workplace.core.model.ExportDetails;
import bpm.vanilla.workplace.core.model.VanillaPackage;

public class ViewPackage extends ViewPart {

	public static final String ID = "bpm.es.pack.manager.views.ViewPackage"; //$NON-NLS-1$
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

	private PackageManager packageManager;

	private TableViewer viewer;
	private Action importPackage;

	private HashMap<Object, File> details = null;
	private Composite main;
	
	private CompositePackageDetails compositePackageDetails;
	private CompositeImportItem compositeImportItem;

	public ViewPackage() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String historicFileName = store.getString(PreferenceConstants.P_BPM_HISTORIC_IMPORT_FILE);
		String pakagesFolder = store.getString(PreferenceConstants.P_BPM_PACKAGES_FOLDER);
		String temporaryFolder = store.getString(PreferenceConstants.P_BPM_TEMP);

		try {
			this.packageManager = new PackageManager(historicFileName, pakagesFolder, temporaryFolder);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getSite().getShell(), Messages.ViewPackage_3, Messages.ViewPackage_11 + e.getMessage());
		}
	}

	@Override
	public void createPartControl(final Composite parent) {
		
		Composite p = new Composite(parent, SWT.NONE);
		p.setLayout(new GridLayout());
		p.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		
		ToolBar tb = new ToolBar(p, SWT.NONE);
		tb.setLayout(new GridLayout());
		tb.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		ToolItem it = new ToolItem(tb, SWT.PUSH);
		it.setToolTipText(Messages.ViewPackage_12);
		it.setImage(Activator.getDefault().getImageRegistry().get("refresh")); //$NON-NLS-1$
		it.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
		});
		
		main = new Composite(p, SWT.NONE);
		main.setLayout(new GridLayout(2,true));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Label lblPackages = new Label(main, SWT.NONE);
		lblPackages.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false, 2, 1));
		lblPackages.setText(Messages.ViewPackage_21);
		
		viewer = new TableViewer(main, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		viewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				HashMap<Object, File> l = (HashMap<Object, File>) inputElement;
				return l.keySet().toArray(new Object[l.size()]);
			}

		});
		viewer.setLabelProvider(new ITableLabelProvider() {

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			@Override
			@SuppressWarnings("unchecked")
			public String getColumnText(Object element, int columnIndex) {
				if (element instanceof ExportDetails) {
					ExportDetails pack = (ExportDetails) element;

					switch (columnIndex) {
						case 0:
							return pack.getName();
						case 1:
							return ((HashMap<ExportDetails, File>) viewer.getInput()).get(pack).getAbsolutePath();
						case 2:
							return sdf.format(pack.getDate());
						case 3:
							return pack.getDesc();
					}
				}
				else if (element instanceof VanillaPackage) {
					VanillaPackage pack = (VanillaPackage) element;

					switch (columnIndex) {
						case 0:
							return pack.getName();
						case 1:
							return ((HashMap<ExportDetails, File>) viewer.getInput()).get(pack).getAbsolutePath();
						case 2:
							return sdf.format(pack.getDate());
						case 3:
							return pack.getDescription();
					}
				}
				return null;
			}

			@Override
			public void addListener(ILabelProviderListener listener) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener listener) {
			}

		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) event.getSelection();

				if (ss.isEmpty()) {
					buildPackageDetail(null);
				}
				else {
					if (ss.getFirstElement() instanceof ExportDetails) {
						buildExportDetail((ExportDetails) ss.getFirstElement());
					}
					else if (ss.getFirstElement() instanceof VanillaPackage) {
						buildPackageDetail((VanillaPackage) ss.getFirstElement());
					}
				}
			}

		});
		viewer.getTable().setHeaderVisible(true);

		TableColumn colName = new TableColumn(viewer.getTable(), SWT.NONE);
		colName.setText(Messages.ViewPackage_15);
		colName.setWidth(200);

		TableColumn colType = new TableColumn(viewer.getTable(), SWT.NONE);
		colType.setText(Messages.ViewPackage_16);
		colType.setWidth(200);

		TableColumn colDate = new TableColumn(viewer.getTable(), SWT.NONE);
		colDate.setText(Messages.ViewPackage_17);
		colDate.setWidth(200);

		TableColumn colDesc = new TableColumn(viewer.getTable(), SWT.NONE);
		colDesc.setText(Messages.ViewPackage_18);
		colDesc.setWidth(200);

		getSite().getPage().addPartListener(new IPartListener() {

			public void partActivated(IWorkbenchPart part) {
			}

			public void partBroughtToTop(IWorkbenchPart part) {
			}

			public void partClosed(IWorkbenchPart part) {
			}

			public void partDeactivated(IWorkbenchPart part) {
			}

			public void partOpened(IWorkbenchPart part) {
				if (part == ViewPackage.this) {
					initContent();
				}
			}

		});
		getSite().setSelectionProvider(viewer);

		Label lblContent = new Label(main, SWT.NONE);
		lblContent.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));
		lblContent.setText(Messages.ViewPackage_22);

		compositePackageDetails = new CompositePackageDetails(main, SWT.NONE);
		compositePackageDetails.setLayout(new GridLayout());
		compositePackageDetails.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		createActions();
		buildContextMenu();
	}

	protected void buildPackageDetail(VanillaPackage vanillaPackage) {
		if(compositePackageDetails == null || compositePackageDetails.isDisposed()) {
			compositeImportItem.dispose();
			compositePackageDetails = new CompositePackageDetails(main, SWT.NONE);
			compositePackageDetails.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
			compositePackageDetails.loadDetails(vanillaPackage);
			main.layout();
		}
		else {
			compositePackageDetails.loadDetails(vanillaPackage);
		}
	}

	private void buildExportDetail(ExportDetails exportDetails) {
		if(compositeImportItem == null || compositeImportItem.isDisposed()) {
			compositePackageDetails.dispose();
			compositeImportItem = new CompositeImportItem(main, SWT.NONE);
			compositeImportItem.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
			compositeImportItem.loadDetails(exportDetails.getImportItems());
	
			main.layout();
		}
		else {
			compositeImportItem.loadDetails(exportDetails.getImportItems());
		}
	}
	
	private void buildContextMenu() {
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(importPackage);
			}
		});
		viewer.getControl().setMenu(menuMgr.createContextMenu(viewer.getControl()));
	}

	@Override
	public void setFocus() {
	}

	private void initContent() {
		try {
			details = packageManager.getAllPackages();
			viewer.setInput(details);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getSite().getShell(), Messages.ViewPackage_19, e.getMessage());
		}
	}

	private void createActions() {
		importPackage = new Action(Messages.ViewPackage_20) {
			public void run() {
				Object selection = ((IStructuredSelection) viewer.getSelection()).getFirstElement();

				if (selection instanceof ExportDetails) {
					ExportDetails ed = (ExportDetails) selection;
					final File packageFile = details.get(ed);
					if (packageFile.exists()) {
						HashMap<String, File> map = null;

						try {
							map = Importer.unzip(packageFile, Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BPM_TEMP));

							// launch url conigurator
							DialogImporter _d = new DialogImporter(getSite().getShell(), map);
							if (_d.open() == DialogImporter.OK) {
								final StringBuffer s = new StringBuffer();
								final HashMap<String, File> m = map;
								BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
									public void run() {
										try {
											s.append(Importer.importPackage(Activator.getDefault().getVanillaApi(), Activator.getDefault().getRepositoryApi(), m, packageFile));
										} catch (Exception e) {
											e.printStackTrace();
											MessageDialog.openError(getSite().getShell(), "Error while running import on" + packageFile.getAbsolutePath() + " file.", e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
										}
									}
								});

								IPreferenceStore store = Activator.getDefault().getPreferenceStore();
								String historicFileName = store.getString(PreferenceConstants.P_BPM_HISTORIC_IMPORT_FILE);
								String packageFolder = store.getString(PreferenceConstants.P_BPM_PACKAGES_FOLDER);
								String tempFolder = store.getString(PreferenceConstants.P_BPM_TEMP);
								
								PackageManager mgr = new PackageManager(historicFileName, packageFolder, tempFolder);

								DialogText d = new DialogText(getSite().getShell(), s.toString());
								d.open();

								mgr.addImportToHistoric(ed, packageFile.getName(), s.toString());
							}

						} catch (Exception e) {
							e.printStackTrace();
							MessageDialog.openError(getSite().getShell(), "Error while parsing" + packageFile.getAbsolutePath() + " file.", e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$

						} finally {
							if (map != null) {
								for (File f : map.values()) {
									if (f.exists()) {
										f.delete();
									}
								}
							}
						}
					}
				}
				else if (selection instanceof VanillaPackage) {
					final VanillaPackage vanillaPackage = (VanillaPackage)selection;
					ImportWizard wizard = new ImportWizard(vanillaPackage, null);

					wizard.init(Activator.getDefault().getWorkbench(), (IStructuredSelection) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection());
					WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);

					dialog.create();
					wizard.getContainer().getShell().setSize(800, wizard.getContainer().getShell().getSize().y);

					if (dialog.open() == Dialog.OK) {
						
						final boolean replaceOld = wizard.replaceOld();
						final RepositoryDirectory targetDir = wizard.getTargetDir();

						IPreferenceStore store = Activator.getDefault().getPreferenceStore();
						final String packageFolder = store.getString(PreferenceConstants.P_BPM_PACKAGES_FOLDER);
						String historicFileName = store.getString(PreferenceConstants.P_BPM_HISTORIC_IMPORT_FILE);
						String tempFolder = store.getString(PreferenceConstants.P_BPM_TEMP);
						
						final StringBuffer s = new StringBuffer();

						try {
							ProgressMonitorDialog dial = new ProgressMonitorDialog(getSite().getShell());
							dial.run(true, false, new IRunnableWithProgress() {
								
								@Override
								public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
									PackageImporter packImporter = new PackageImporter(Activator.getDefault().getRepositoryApi(), Activator.getDefault().getVanillaApi(), vanillaPackage, replaceOld, packageFolder);
									try {
										monitor.beginTask(bpm.es.pack.manager.I18N.Messages.ViewPackage_1 + vanillaPackage.getName(), 14);
										s.append(packImporter.importPackage(targetDir, monitor));
									} catch (Exception e) {
										e.printStackTrace();
										MessageDialog.openError(getSite().getShell(), "Error while running import on " + vanillaPackage.getName() + " package.", e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
									}
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
							MessageDialog.openError(getSite().getShell(), bpm.es.pack.manager.I18N.Messages.ViewPackage_2 + vanillaPackage.getName() + ". ", e.getMessage());//$NON-NLS-1$
						}
						
						DialogText d = new DialogText(getSite().getShell(), s.toString());
						d.open();
						
						try {
							PackageManager mgr = new PackageManager(historicFileName, packageFolder, tempFolder);
							mgr.addImportToHistoric(vanillaPackage, vanillaPackage.getName(), s.toString());
						} catch (Exception e) {
							e.printStackTrace();
							MessageDialog.openError(getSite().getShell(), bpm.es.pack.manager.I18N.Messages.ViewPackage_10 + vanillaPackage.getName() + ". ", e.getMessage());//$NON-NLS-1$
						}
					}
				}
				
				refresh();
			}
		};

	}
	
	private void refresh(){
		try {
			packageManager.refresh();
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageDialog.openError(getSite().getShell(), Messages.ViewPackage_13, Messages.ViewPackage_14 + ex.getMessage());
		}

		initContent();
	}

	public PackageManager getPackageManager() {
		return packageManager;
	}

}
