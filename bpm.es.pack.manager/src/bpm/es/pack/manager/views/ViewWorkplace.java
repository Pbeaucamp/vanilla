package bpm.es.pack.manager.views;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import bpm.birep.admin.client.dialog.DialogText;
import bpm.birep.admin.client.preferences.PreferenceConstants;
import bpm.es.pack.manager.I18N.Messages;
import bpm.es.pack.manager.actions.ActionPlaceRefresh;
import bpm.es.pack.manager.imp.CompositePackageDetails;
import bpm.es.pack.manager.imp.PackageManager;
import bpm.es.pack.manager.utils.PackageCreator;
import bpm.es.pack.manager.utils.PackageImporter;
import bpm.es.pack.manager.wizard.imp.ImportWizard;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.workplace.core.IPackage;
import bpm.vanilla.workplace.core.IProject;
import bpm.vanilla.workplace.core.IUser;
import bpm.vanilla.workplace.core.model.VanillaPackage;
import bpm.vanilla.workplace.remote.impl.RemotePlaceService;

public class ViewWorkplace extends ViewPart {

	public static final String ID = "bpm.es.pack.manager.views.ViewWorkplace"; //$NON-NLS-1$
	//	private static final String PACKAGE_TYPE = ".bpmpackage"; //$NON-NLS-1$
//	private static final String PACKAGE_FOLDER = "VanillaPlace"; //$NON-NLS-1$
	private static final String USER_DEFAULT = "userDefault"; //$NON-NLS-1$
	private static final String PASSWORD_DEFAULT = "userDefault"; //$NON-NLS-1$

	private IUser currentUser;
	private RemotePlaceService workplaceService;

	private Label lblConnectedAs, lblCurrentPackageName;

	private ScrolledForm form;
	private Composite projectContent;
	private CompositePackageDetails compositeDetails;
	private ProgressBar pb2;

//	private ToolItem exportBtn/* , btnConnect */;
	private Button btnImportPackage;

	private FormToolkit toolkit;

	private Color white, gray, cyan;

	// The selected package and composite associate
	private IPackage currentPackage;
	private Composite currentComposite;

	private String packageCustomName;

	public ViewWorkplace() {
		if (!Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BPM_VANILLA_PLACE_URL).isEmpty()) {
			workplaceService = new RemotePlaceService(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BPM_VANILLA_PLACE_URL));
		}
		else {
			// TODO: Error message
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		this.toolkit = new FormToolkit(getSite().getShell().getDisplay());
		this.white = getSite().getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE);
		this.gray = new Color(getSite().getShell().getDisplay(), 230, 230, 230);
		this.cyan = new Color(getSite().getShell().getDisplay(), 204, 240, 231);

		parent.setLayout(new GridLayout());

		ToolBar tb = new ToolBar(parent, SWT.NONE);
		tb.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fillToolbar(tb);

		// DialogWorkplaceConnection dial = new
		// DialogWorkplaceConnection(getSite().getShell(), workplaceService);
		// if (dial.open() == DialogRepository.OK){
		// currentUser = dial.getUser();
		// }

		Composite content = new Composite(parent, SWT.NONE);
		content.setLayout(new GridLayout(2, false));
		content.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		lblConnectedAs = new Label(content, SWT.NONE);
		lblConnectedAs.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		pb2 = new ProgressBar(content, SWT.HORIZONTAL | SWT.INDETERMINATE);
		pb2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pb2.setVisible(false);

		form = toolkit.createScrolledForm(content);
		form.getBody().setLayout(new GridLayout());
		form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		projectContent = new Composite(form.getBody(), SWT.NONE);
		projectContent.setLayout(new GridLayout());
		projectContent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		projectContent.setBackground(white);

		Composite packageContent = new Composite(content, SWT.BORDER);
		packageContent.setLayout(new GridLayout());
		packageContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		packageContent.setBackground(white);

		lblCurrentPackageName = toolkit.createLabel(packageContent, ""); //$NON-NLS-1$
		lblCurrentPackageName.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		btnImportPackage = new Button(packageContent, SWT.PUSH);
		btnImportPackage.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btnImportPackage.setText(Messages.ViewWorkplace_5);
		btnImportPackage.setEnabled(false);
		btnImportPackage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				pb2.setVisible(true);

				importPackage();

				super.mouseDown(e);
			}
		});

		compositeDetails = new CompositePackageDetails(packageContent, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		compositeDetails.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		IToolBarManager mngr = this.getViewSite().getActionBars().getToolBarManager();
		mngr.add(new ActionPlaceRefresh(this));

		try {
			currentUser = workplaceService.authentifyUser(USER_DEFAULT, PASSWORD_DEFAULT);
		} catch (Exception e1) {
			e1.printStackTrace();
			MessageDialog.openError(this.getSite().getShell(), Messages.ViewWorkplace_7, Messages.DialogWorkplaceConnectin_11);
		}

		refreshView();
	}

	private void buildProjectContent(List<IProject> project) {
		projectContent.dispose();

		projectContent = new Composite(form.getBody(), SWT.NONE);
		projectContent.setLayout(new GridLayout());
		projectContent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		projectContent.setBackground(white);

		for (IProject proj : project) {
			Section bar = toolkit.createSection(projectContent, Section.TITLE_BAR | Section.TWISTIE);
			bar.setText(Messages.ViewWorkplace_8 + proj.getName());
			bar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

			Composite composite = new Composite(bar, SWT.NONE);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			composite.setLayout(new GridLayout());
			composite.setBackground(white);

			for (final IPackage pack : proj.getPackages()) {

				GridLayout layout = new GridLayout(2, false);
				layout.marginTop = layout.marginBottom = 5;

				final Composite comp = new Composite(composite, SWT.NONE);
				comp.setLayout(layout);
				comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				comp.setBackground(gray);

				MouseAdapter adapter = new MouseAdapter() {
					@Override
					public void mouseDown(MouseEvent e) {
						setCurrentPackage(comp, pack);
						super.mouseDown(e);
					}
				};

				comp.addMouseListener(adapter);

				Label lblNameVersion = new Label(comp, SWT.UNDERLINE_SINGLE);
				lblNameVersion.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
				lblNameVersion.setText(pack.getName() + " - Version: " + pack.getVersion()); //$NON-NLS-1$
				lblNameVersion.addMouseListener(adapter);

				Label lblCreaDate = new Label(comp, SWT.NONE);
				lblCreaDate.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
				lblCreaDate.setText(Messages.ViewWorkplace_10 + pack.getCreator().getName() + Messages.ViewWorkplace_11 + pack.getCreationDate());
				lblCreaDate.addMouseListener(adapter);

				if (pack.getDocumentationUrl() != null && !pack.getDocumentationUrl().isEmpty()) {
					Label lblDocumentationUrl = new Label(comp, SWT.NONE);
					lblDocumentationUrl.setText(Messages.ViewWorkplace_12);
					lblDocumentationUrl.addMouseListener(adapter);

					Link linkDocUrl = new Link(comp, SWT.NONE);
					linkDocUrl.setText("<a>" + pack.getDocumentationUrl() + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
					linkDocUrl.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event) {
							org.eclipse.swt.program.Program.launch(pack.getDocumentationUrl());
						}
					});
				}

				if (pack.getSiteWebUrl() != null && !pack.getSiteWebUrl().isEmpty()) {
					Label lblSiteWebUrl = new Label(comp, SWT.NONE);
					lblSiteWebUrl.setText(Messages.ViewWorkplace_15);
					lblSiteWebUrl.addMouseListener(adapter);

					Link linkSiteWeb = new Link(comp, SWT.NONE);
					linkSiteWeb.setText("<a>" + pack.getSiteWebUrl() + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
					linkSiteWeb.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event) {
							org.eclipse.swt.program.Program.launch(pack.getSiteWebUrl());
						}
					});
				}

				if (pack.getPrerequisUrl() != null && !pack.getPrerequisUrl().isEmpty()) {
					Label lblPrerequis = new Label(comp, SWT.NONE);
					lblPrerequis.setText(Messages.ViewWorkplace_18);
					lblPrerequis.addMouseListener(adapter);

					Link linkPrerequis = new Link(comp, SWT.NONE);
					linkPrerequis.setText("<a>" + pack.getPrerequisUrl() + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
					linkPrerequis.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event) {
							org.eclipse.swt.program.Program.launch(pack.getPrerequisUrl());
						}
					});
				}

				Label lblCertified = new Label(comp, SWT.NONE);
				lblCertified.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
				if (pack.isCertified()) {
					lblCertified.setText(Messages.ViewWorkplace_21 + pack.getVanillaVersion() + Messages.ViewWorkplace_22);
				}
				else {
					lblCertified.setText(Messages.ViewWorkplace_23 + pack.getVanillaVersion());
				}
				lblCertified.addMouseListener(adapter);
			}

			bar.setClient(composite);
		}

		form.getBody().layout(true);
	}

	public void refreshView() {
		if (currentUser != null) {
			if (currentUser.getName().equals(USER_DEFAULT)) {
				lblConnectedAs.setText(Messages.ViewWorkplace_24);
			}
			else {
				lblConnectedAs.setText(Messages.ViewWorkplace_25 + currentUser.getName());
			}

//			exportBtn.setEnabled(false);
			// btnConnect.setEnabled(false);

			List<IProject> projects = new ArrayList<IProject>();
			try {
				projects = workplaceService.getAvailableProjects(currentUser.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (projects == null) {
				projects = new ArrayList<IProject>();
			}

			buildProjectContent(projects);
			setCurrentPackage(null, null);
		}
		else {
			lblConnectedAs.setText(Messages.ViewWorkplace_26);

//			exportBtn.setEnabled(false);
			// btnConnect.setEnabled(true);
		}
	}

	@Override
	public void setFocus() {

	}

	private void fillToolbar(ToolBar tb) {
		// btnConnect = new ToolItem(tb, SWT.PUSH);
		// btnConnect.setImage(Activator.getDefault().getImageRegistry().get(Icons.CONNECT));
		// btnConnect.setToolTipText(Messages.DialogWorkplaceConnectin_5);
		// btnConnect.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// DialogWorkplaceConnection dial = new
		// DialogWorkplaceConnection(getSite().getShell(), workplaceService);
		// if (dial.open() == DialogRepository.OK){
		// currentUser = dial.getUser();
		// }
		//				
		// refreshView();
		//				
		// super.widgetSelected(e);
		// }
		// });

//		exportBtn = new ToolItem(tb, SWT.PUSH);
//		exportBtn.setImage(Activator.getDefault().getImageRegistry().get(Icons.EXPORT));
//		exportBtn.setToolTipText(Messages.ViewVanillaPlace_0);
//		exportBtn.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				export();
//			}
//		});
//		exportBtn.setEnabled(false);
	}

	private void importPackage() {
		try {
			final Display display = getSite().getShell().getDisplay();
			Runnable longJob = new Runnable() {
				boolean done = false;

				public void run() {
					Thread thread = new Thread(new Runnable() {
						public void run() {
							try {
								packageCustomName = importThePackage();
							} catch (Exception e) {
								e.printStackTrace();
							}
							done = true;
						}
					});
					thread.start();
					while (!done) {
						if (!display.readAndDispatch())
							display.sleep();
					}
				}
			};
			BusyIndicator.showWhile(display, longJob);

			if (packageCustomName != null && !packageCustomName.isEmpty()) {
				final VanillaPackage vanillaPackage = currentPackage.getPackage();
				ImportWizard wizard = new ImportWizard(vanillaPackage, packageCustomName);

				pb2.setVisible(false);

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


				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				String packageFolder = store.getString(PreferenceConstants.P_BPM_PACKAGES_FOLDER);
				try {
					File zipFile = new File(packageFolder + File.separator + packageCustomName + PackageCreator.VANILLA_PACKAGE_EXTENSION);
					zipFile.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}

				refreshView();
			}
			else {
				refreshView();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String importThePackage() throws Exception {
		// We download the package in order to get the datasources from the file
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String packageFolder = store.getString(PreferenceConstants.P_BPM_PACKAGES_FOLDER);
		
		InputStream is = workplaceService.importPackage(currentUser.getId(), currentPackage);
		String fileName = currentPackage.getName() + "_" + new Object().hashCode(); //$NON-NLS-1$ //$NON-NLS-2$

		File ressDir = new File(packageFolder);
		ressDir.mkdir();

		String path = packageFolder + File.separator + fileName + PackageCreator.VANILLA_PACKAGE_EXTENSION;

		FileOutputStream outputStream = new FileOutputStream(path);
		byte[] buffer = new byte[2048];
		int n;
		while ((n = is.read(buffer, 0, 2048)) > -1) {
			outputStream.write(buffer, 0, n);
		}

		outputStream.close();
		is.close();

		return fileName;
	}

//	private void export() {
		// ExportWizard wizard = new ExportWizard(true);
		// wizard.init(Activator.getDefault().getWorkbench(),
		// (IStructuredSelection)
		// Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection());
		//
		// WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
		// dialog.create();
		//
		// wizard.getContainer().getShell().setSize(800,
		// wizard.getContainer().getShell().getSize().y);
		//
		// if (dialog.open() == Dialog.OK) {
		// final ExportDetails infos = wizard.getExportInfo();
		//
		// IRunnableWithProgress r = new IRunnableWithProgress() {
		// public void run(IProgressMonitor monitor) throws
		// InvocationTargetException, InterruptedException {
		//
		//					IProject project = new PlaceProject("VanillaDatasourceTest", "1", currentUser, new Date()); //$NON-NLS-1$ //$NON-NLS-2$
		//					IPackage pack = new PlacePackage("Datasources", "1", "4.0", IPackageType.METADATA_AND_DOCUMENTS, currentUser, new Date(), true, true, "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		//
		// monitor.setTaskName(Messages.ViewWorkplace_44);
		// monitor.beginTask(Messages.ViewWorkplace_45, 1);
		// try {
		//						File zippedPackage = infos.createPackage(Activator.getDefault().getRepositoryApi(), Activator.getDefault().getVanillaApi(), ""); //$NON-NLS-1$
		// monitor.worked(1);
		// workplaceService.exportPackage(currentUser.getId(), project, pack,
		// new FileInputStream(zippedPackage));
		// } catch (Exception e) {
		// e.printStackTrace();
		// MessageDialog.openError(getSite().getShell(),
		// Messages.ApplicationActionBarAdvisor_2, e.getMessage());
		// }
		// }
		// };
		//
		// IProgressService service =
		// PlatformUI.getWorkbench().getProgressService();
		// try {
		// service.run(true, false, r);
		// MessageDialog.openInformation(dialog.getShell(),
		// Messages.ViewWorkplace_47, Messages.ViewWorkplace_48);
		// } catch (Exception e1) {
		// e1.printStackTrace();
		// MessageDialog.openError(dialog.getShell(), Messages.ViewWorkplace_49,
		// Messages.ViewWorkplace_50 + e1.getMessage());
		// }
		// }
//	}

	public void setCurrentPackage(Composite currentComposite, IPackage currentPackage) {
		if (this.currentComposite != null && !this.currentComposite.isDisposed()) {
			this.currentComposite.setBackground(gray);
		}
		this.currentPackage = currentPackage;
		this.currentComposite = currentComposite;
		if (currentComposite != null) {
			this.currentComposite.setBackground(cyan);
		}

		if (currentPackage != null) {
			this.lblCurrentPackageName.setText(currentPackage.getName());
			this.btnImportPackage.setEnabled(true);
			this.compositeDetails.loadDetails(currentPackage.getPackage());
		}
		else {
			this.lblCurrentPackageName.setText(""); //$NON-NLS-1$
			this.btnImportPackage.setEnabled(false);
			this.compositeDetails.loadDetails(null);
		}
	}
}
