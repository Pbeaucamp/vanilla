package bpm.gateway.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.views.IViewDescriptor;

import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.ui.actions.ActionCopy;
import bpm.gateway.ui.actions.ActionNewWizard;
import bpm.gateway.ui.actions.ActionOpen;
import bpm.gateway.ui.actions.ActionPrepareRuntime;
import bpm.gateway.ui.actions.ActionSave;
import bpm.gateway.ui.dialogs.database.wizard.migration.DBMigrationWizard;
import bpm.gateway.ui.dialogs.database.wizard.transfert.FmdtTransfertWizard;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.editors.GatewayEditorPart;
import bpm.gateway.ui.gatewaywizard.GatewayWizard;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;
import bpm.gateway.ui.icons.StepsImagesUrl;
import bpm.gateway.ui.perspectives.ErrorPerspective;
import bpm.gateway.ui.perspectives.Perspective;
import bpm.gateway.ui.perspectives.RuntimePerspective;
import bpm.gateway.ui.preferences.PreferencesConstants;
import bpm.gateway.ui.utils.PDFGenerator;
import bpm.gateway.ui.views.ResourceViewPart;
import bpm.gateway.ui.views.RuntimeErrorViewPart;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction exit, about, importModel, exportModel, preferenceAction;
	private Action newDoc, open, save, run, saveAs, close, documentProperties, databaseMigration;
	private Action runtimeMode, designMode, generateAutodoc, transfertAssistant, errorLogs;
	private IPartListener listener;

	private Action select, link;

	/*
	 * editions actions
	 */
	private Action copy, paste;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(final IWorkbenchWindow window) {
		exit = ActionFactory.QUIT.create(window);
		register(exit);

		preferenceAction = ActionFactory.PREFERENCES.create(window);
		register(preferenceAction);

		newDoc = new ActionNewWizard();
		// newDoc.setText("New Object");

		newDoc.setImageDescriptor(ActionFactory.NEW.create(window).getImageDescriptor());
		register(newDoc);

		importModel = ActionFactory.IMPORT.create(window);
		register(importModel);

		exportModel = ActionFactory.EXPORT.create(window);
		register(exportModel);
		exportModel.setEnabled(false);

		ActionFactory.COPY.create(window);

		open = new Action(Messages.ApplicationActionBarAdvisor_0) {
			public void run() {

				Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

				FileDialog fd = new FileDialog(sh);
				fd.setFilterExtensions(new String[] { "*.*" }); //$NON-NLS-1$
				String fileName = fd.open();

				if (fileName == null) {
					return;
				}

				if (!fileName.endsWith(".gateway")) { //$NON-NLS-1$
					fileName = fileName + ".gateway"; //$NON-NLS-1$
				}

				try {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					page.openEditor(new GatewayEditorInput(new File(fileName)), GatewayEditorPart.ID, false);
					addFileToList(fileName);

					ResourceViewPart v = (ResourceViewPart) page.findView(ResourceViewPart.ID);
					if (v != null) {
						v.refresh();
					}

					Activator.getDefault().getSessionSourceProvider().setDirectoryItemId(null);
					Activator.getDefault().getSessionSourceProvider().setModelOpened(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		open.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.open_16));

		generateAutodoc = new Action(Messages.ApplicationActionBarAdvisor_3) {
			public void run() {
				FileDialog fd = new FileDialog(window.getShell(), SWT.SAVE);
				fd.setFilterExtensions(new String[] { "*.*" }); //$NON-NLS-1$

				String s = fd.open();
				if (s != null) {
					GatewayEditorPart editor = null;

					IEditorInput input = Activator.getDefault().getCurrentInput();
					for (IEditorReference r : Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences()) {

						try {
							if (r.getEditorInput() == input) {
								editor = ((GatewayEditorPart) r.getEditor(false));

								break;
							}
						} catch (Exception ex) {

						}

					}

					try {
						OutputStream os = new FileOutputStream(s);

						PDFGenerator.generatePdf(Activator.getDefault().getCurrentInput().getDocumentGateway(), os, StepsImagesUrl.getImages(), editor);

						os.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}
			}
		};

		close = new Action(Messages.ApplicationActionBarAdvisor_5) {
			public void run() {

				try {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

					IEditorPart editor = page.getActiveEditor();

					for (Parameter p : ((GatewayEditorInput) editor.getEditorInput()).getDocumentGateway().getParameters()) {
						ResourceManager.getInstance().deleteParameter(p);
					}

					page.closeEditor(editor, true);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		close.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.close_16));
		close.setEnabled(false);

		run = new ActionPrepareRuntime();
		run.setToolTipText(run.getText());
		run.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.prepare_16));
		run.setEnabled(false);

		save = new ActionSave();
		save.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.save_16));
		save.setEnabled(false);

		saveAs = new Action(Messages.ApplicationActionBarAdvisor_6) {
			public void run() {
				Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

				GatewayEditorInput doc = Activator.getDefault().getCurrentInput();

				String path = null;
				FileDialog fd = new FileDialog(sh, SWT.SAVE);
				fd.setFilterExtensions(new String[] { "*.gateway", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
				path = fd.open();

				if (path != null) {
					File f = new File(path);

					try {
						doc.getDocumentGateway().checkWellFormed();
						doc.getDocumentGateway().write(new FileOutputStream(f));

						// MessageDialog.openInformation(sh, "Save", "File saved.");
						addFileToList(path);
						doc.setFile(f);
						// new ActionOpen(path).run();

						// GatewayEditorPart part = (GatewayEditorPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart();
						IEditorPart part = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
						if (part != null && part instanceof GatewayEditorPart) {
							((GatewayEditorPart) part).setPartName(doc);
						}
					} catch (Exception e) {
						e.printStackTrace();
						MessageDialog.openError(sh, Messages.ApplicationActionBarAdvisor_7, Messages.ApplicationActionBarAdvisor_8 + e.getMessage());
					}

				}
			}
		};
		saveAs.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.save_as_16));
		saveAs.setEnabled(false);

		// schedule = new Action("Schedule runtime"){
		// public void run(){
		// Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		// DialogSchedule dial = new DialogSchedule(sh);
		// if (dial.open() == DialogSchedule.OK){
		// MessageDialog.openInformation(sh, "Scheduling", "This feature is not yet implemented.");
		// }
		// }
		// };
		// schedule.setEnabled(false);

		about = ActionFactory.ABOUT.create(window);
		register(about);

		documentProperties = new Action(Messages.ApplicationActionBarAdvisor_9) {

			@Override
			public void run() {

				Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

				IEditorPart part = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				if (part == null) {
					return;
				}

				GatewayEditorInput doc = (GatewayEditorInput) part.getEditorInput();

				if (doc == null) {
					return;
				}

				GatewayWizard wizard = new GatewayWizard(doc);

				wizard.init(Activator.getDefault().getWorkbench(), (IStructuredSelection) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection());

				WizardDialog dialog = new WizardDialog(sh, wizard);
				dialog.create();
				dialog.getShell().setSize(800, 600);
				dialog.getShell().setText(""); //$NON-NLS-1$

				if (dialog.open() == WizardDialog.OK) {

				}

			}

		};
		documentProperties.setEnabled(false);

		copy = new ActionCopy();
		copy.setEnabled(false);

		window.addPageListener(new IPageListener() {

			public void pageActivated(IWorkbenchPage page) {

			}

			public void pageClosed(IWorkbenchPage page) {
				page.removePartListener(listener);

			}

			public void pageOpened(IWorkbenchPage page) {

				listener = new IPartListener() {

					public void partActivated(IWorkbenchPart part) {
						if (part instanceof GatewayEditorPart) {
							run.setEnabled(true);
							save.setEnabled(true);
							saveAs.setEnabled(true);
							close.setEnabled(true);
							copy.setEnabled(true);
							exportModel.setEnabled(true);
							documentProperties.setEnabled(true);
						}

					}

					public void partBroughtToTop(IWorkbenchPart part) {
						if (part instanceof GatewayEditorPart) {
							run.setEnabled(true);
							save.setEnabled(true);
							saveAs.setEnabled(true);
							close.setEnabled(true);
							copy.setEnabled(true);
							exportModel.setEnabled(true);
							documentProperties.setEnabled(true);
							GatewayEditorPart e = (GatewayEditorPart) part;

							Iterator it = e.getMyActionRegistry().getActions();
							Object o = null;
							while (it.hasNext()) {
								o = it.next();
							}
						}

					}

					public void partClosed(IWorkbenchPart part) {
						if (part.getSite().getWorkbenchWindow().getActivePage().getActiveEditor() == null) {
							run.setEnabled(false);
							save.setEnabled(false);
							saveAs.setEnabled(false);
							close.setEnabled(false);
							copy.setEnabled(false);
							exportModel.setEnabled(false);
							documentProperties.setEnabled(false);

						}

						if (part instanceof GatewayEditorPart) {

							Activator.getDefault().fireEventModelOpened(((GatewayEditorInput) ((GatewayEditorPart) part).getEditorInput()).getName());
						}

						if (part.getSite().getWorkbenchWindow().getActivePage().getEditorReferences().length == 0) {
							designMode.run();
						}

					}

					public void partDeactivated(IWorkbenchPart part) {
						if (part.getSite().getWorkbenchWindow().getActivePage().getActiveEditor() == null) {
							run.setEnabled(false);
							save.setEnabled(false);
							saveAs.setEnabled(false);
							close.setEnabled(false);
							copy.setEnabled(false);
							exportModel.setEnabled(false);
							documentProperties.setEnabled(false);
						}

					}

					public void partOpened(IWorkbenchPart part) {

					}

				};
				page.addPartListener(listener);
			}

		});

		window.addPerspectiveListener(new IPerspectiveListener() {

			public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {

			}

			public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
				if (perspective.getId().equals(Perspective.PERSPECTIVE_ID)) {
					run.setEnabled(page.getEditorReferences().length > 0);
					designMode.setEnabled(false);
					errorLogs.setEnabled(true);
				}
				else if (perspective.getId().equals(RuntimePerspective.PERSPECTIVE_ID)) {
					run.setEnabled(false);
					designMode.setEnabled(true);
					errorLogs.setEnabled(true);
				}
				else if (perspective.getId().equals(ErrorPerspective.PERSPECTIVE_ID)) {
					run.setEnabled(true);
					designMode.setEnabled(true);
					errorLogs.setEnabled(false);
				}
				else if (perspective.getId().equals("bpm.mdm.ui.perspectives.SupplierManagementPerspective")) { //$NON-NLS-1$
					run.setEnabled(true);
					designMode.setEnabled(true);
					errorLogs.setEnabled(true);
				}

			}
		});

		databaseMigration = new Action(Messages.ApplicationActionBarAdvisor_10) {
			public void run() {
				DBMigrationWizard wiz = new DBMigrationWizard();

				WizardDialog dial = new WizardDialog(window.getShell(), wiz) {

					@Override
					protected void createButtonsForButtonBar(Composite parent) {
						super.createButtonsForButtonBar(parent);
						getButton(IDialogConstants.FINISH_ID).setText(Messages.ApplicationActionBarAdvisor_11);
					}

					@Override
					protected void initializeBounds() {
						super.initializeBounds();
						this.getShell().setSize(800, 600);
					}

				};

				if (dial.open() == WizardDialog.OK) {

				}
			}
		};

		transfertAssistant = new Action(Messages.ApplicationActionBarAdvisor_16) {
			public void run() {
				if (Activator.getDefault().getRepositoryContext() != null) {
					FmdtTransfertWizard wiz = new FmdtTransfertWizard();

					WizardDialog dial = new WizardDialog(window.getShell(), wiz) {

						@Override
						protected void createButtonsForButtonBar(Composite parent) {
							super.createButtonsForButtonBar(parent);
							getButton(IDialogConstants.FINISH_ID).setText(Messages.ApplicationActionBarAdvisor_11);
						}

						@Override
						protected void initializeBounds() {
							super.initializeBounds();
							this.getShell().setSize(800, 600);
						}
					};

					if (dial.open() == WizardDialog.OK) {
						// Do nothing
					}
				}
				else {
					MessageDialog.openError(window.getShell(), Messages.ApplicationActionBarAdvisor_18, Messages.ApplicationActionBarAdvisor_19);
				}
			}
		};

		errorLogs = new Action(Messages.ShowErrorLogs) {
			@Override
			public void run() {
				for (IPerspectiveDescriptor pd : Activator.getDefault().getWorkbench().getPerspectiveRegistry().getPerspectives()) {
					if (pd.getId().equals(ErrorPerspective.PERSPECTIVE_ID)) {
						Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);
						try {
							RuntimeErrorViewPart errorView = (RuntimeErrorViewPart) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(RuntimeErrorViewPart.ID);
							if (errorView != null) {
								errorView.refresh();
							}
						} catch (Exception e) {
						}
					}
				}
			}
		};
		errorLogs.setEnabled(false);
		errorLogs.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.LOGS_ERROR_16));

		designMode = new Action(Messages.ApplicationActionBarAdvisor_12) {
			public void run() {
				for (IPerspectiveDescriptor pd : Activator.getDefault().getWorkbench().getPerspectiveRegistry().getPerspectives()) {
					if (pd.getId().equals(Perspective.PERSPECTIVE_ID)) {
						Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);
					}
				}
			}
		};
		designMode.setEnabled(false);
		designMode.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.design_16));
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager recentMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_1, IWorkbenchActionConstants.MB_ADDITIONS);

		MenuManager fileMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_2, IWorkbenchActionConstants.M_FILE);
		//        MenuManager helpMenu = new MenuManager("Help", IWorkbenchActionConstants.M_HELP); //$NON-NLS-1$
		fileMenu.add(newDoc);
		fileMenu.add(open);

		fileMenu.add(recentMenu);
		fileMenu.add(new Separator());
		fileMenu.add(close);
		fileMenu.add(save);
		fileMenu.add(saveAs);
		fileMenu.add(new Separator());

		fileMenu.add(importModel);
		fileMenu.add(exportModel);

		fileMenu.add(new Separator());
		fileMenu.add(generateAutodoc);
		fileMenu.add(preferenceAction);
		fileMenu.add(exit);

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		String[] recent = new String[] { store.getString(PreferencesConstants.P_RECENTFILE1), store.getString(PreferencesConstants.P_RECENTFILE2), store.getString(PreferencesConstants.P_RECENTFILE3), store.getString(PreferencesConstants.P_RECENTFILE4), store.getString(PreferencesConstants.P_RECENTFILE5) };

		for (String s : recent) {
			if (!s.trim().equals("")) { //$NON-NLS-1$
				Action a = new ActionOpen(s);
				a.setText(s);
				recentMenu.add(a);
			}
		}

		menuBar.add(fileMenu);
		//

		MenuManager editMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_4, IWorkbenchActionConstants.M_EDIT);
		editMenu.add(copy);
		editMenu.add(new Separator());
		editMenu.add(documentProperties);
		menuBar.add(editMenu);

		MenuManager runtimeMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_13, IWorkbenchActionConstants.M_WINDOW);
		runtimeMenu.add(run);
		menuBar.add(runtimeMenu);

		MenuManager toolsMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_14, IWorkbenchActionConstants.MB_ADDITIONS);
		toolsMenu.add(databaseMigration);
		toolsMenu.add(transfertAssistant);
		// toolsMenu.add(errorLogs);
		// toolsMenu.add(new Separator());
		// toolsMenu.add(generateAutodoc);
		menuBar.add(toolsMenu);

		MenuManager helpMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_15, IWorkbenchActionConstants.M_HELP);
		helpMenu.add(about);
		menuBar.add(helpMenu);

		menuBar.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

			}
		});
	}

	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		coolBar.add(new ToolBarContributionItem(toolbar, "main")); //$NON-NLS-1$
		toolbar.add(new GroupMarker(IWorkbenchActionConstants.OPEN_EXT));

		toolbar.add(newDoc);
		toolbar.add(saveAs);
		toolbar.add(save);
		toolbar.add(open);
		toolbar.add(close);

		toolbar.add(new Separator());
		toolbar.add(designMode);
		toolbar.add(run);
		toolbar.add(errorLogs);

	}

	private void addFileToList(String path) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String[] list = { store.getString(PreferencesConstants.P_RECENTFILE1), store.getString(PreferencesConstants.P_RECENTFILE2), store.getString(PreferencesConstants.P_RECENTFILE3), store.getString(PreferencesConstants.P_RECENTFILE4), store.getString(PreferencesConstants.P_RECENTFILE5) };

		boolean isEverListed = false;
		for (int i = 0; i < list.length; i++) {
			if (list[i].equals(path))
				isEverListed = true;
		}

		if (!isEverListed) {
			list[4] = list[3];
			list[3] = list[2];
			list[2] = list[1];
			list[1] = list[0];
			list[0] = path;
		}

		store.setValue(PreferencesConstants.P_RECENTFILE1, list[0]);
		store.setValue(PreferencesConstants.P_RECENTFILE2, list[1]);
		store.setValue(PreferencesConstants.P_RECENTFILE3, list[2]);
		store.setValue(PreferencesConstants.P_RECENTFILE4, list[3]);
		store.setValue(PreferencesConstants.P_RECENTFILE5, list[4]);

	}
}
