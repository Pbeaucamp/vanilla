package metadata.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import metadata.client.actions.ActionOpen;
import metadata.client.actions.ActionSave;
import metadata.client.i18n.Messages;
import metadata.client.model.dialog.DialogDocumentProperties;
import metadata.client.model.dialog.DialogLanguage;
import metadata.client.preferences.PreferenceConstants;
import metadataclient.Activator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import bpm.metadata.MetaData;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.dialogs.DialogShowDependencies;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private Action save, saveas, load, newModel, close, language, properties, dependencies;
	private IWorkbenchAction exit, about;
	private IWorkbenchAction preferenceAction, importModel, exportModel;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void makeActions(final IWorkbenchWindow window) {
		exit = ActionFactory.QUIT.create(window);
		register(exit);

		about = ActionFactory.ABOUT.create(window);
		register(about);

		importModel = ActionFactory.IMPORT.create(window);
		register(importModel);

		exportModel = ActionFactory.EXPORT.create(window);
		register(exportModel);

		preferenceAction = ActionFactory.PREFERENCES.create(window);
		register(preferenceAction);

		final Shell sh = window.getShell();

		saveas = new Action(Messages.ApplicationActionBarAdvisor_0) { //$NON-NLS-1$
			@Override
			public void run() {
				new ActionSave(Messages.ApplicationActionBarAdvisor_2, null).run(); //$NON-NLS-1$

			}
		};
		saveas.setToolTipText(Messages.ApplicationActionBarAdvisor_3); //$NON-NLS-1$
		saveas.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("saveas"))); //$NON-NLS-1$

		language = new Action(Messages.ApplicationActionBarAdvisor_5) { //$NON-NLS-1$
			public void run() {
				DialogLanguage dial = new DialogLanguage(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
				if (dial.open() == DialogLanguage.OK) {
					MetaData model = Activator.getDefault().getModel();

					for (Locale l : dial.getLocales()) {
						if (!model.getLocales().contains(l)) {
							model.addLocale(l);
						}
					}

					List<Locale> toRemove = new ArrayList<Locale>();
					for (Locale l : model.getLocales()) {
						if (!dial.getLocales().contains(l)) {
							toRemove.add(l);
							break;
						}
					}

					for (Locale l : toRemove) {
						model.removeLocale(l);
					}
				}
			}
		};

		save = new Action(Messages.ApplicationActionBarAdvisor_6) { //$NON-NLS-1$
			@Override
			public void run() {
				String path = Activator.getDefault().getFileName();

				if (path == null || path.trim().equals("")) { //$NON-NLS-1$
					saveas.run();
				}
				else {
					new ActionSave(Messages.ApplicationActionBarAdvisor_8, path).run(); //$NON-NLS-1$
				}
			}
		};
		save.setToolTipText(Messages.ApplicationActionBarAdvisor_9); //$NON-NLS-1$
		save.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("save"))); //$NON-NLS-1$
		save.setAccelerator(SWT.CTRL | 's');

		load = new Action(Messages.ApplicationActionBarAdvisor_11) { //$NON-NLS-1$
			@Override
			public void run() {

				if (Activator.getDefault().hasChanged()) {
					if (MessageDialog.openQuestion(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.ApplicationActionBarAdvisor_1, Messages.ApplicationActionBarAdvisor_7)) {
						new ActionSave(Messages.ApplicationActionBarAdvisor_12, Activator.getDefault().getFileName()).run();
					}
				}

				FileDialog fd = new FileDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OPEN);
				fd.setFilterExtensions(new String[] { "*.freemetadata", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
				String path = fd.open();
				if (path != null) {

					new ActionOpen(path).run();

				}

			}
		};
		load.setToolTipText(Messages.ApplicationActionBarAdvisor_14); //$NON-NLS-1$
		load.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("open"))); //$NON-NLS-1$

		close = new Action(Messages.ApplicationActionBarAdvisor_13) {
			public void run() {
				if (Activator.getDefault().hasChanged()) {
					if (MessageDialog.openQuestion(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.ApplicationActionBarAdvisor_17, Messages.ApplicationActionBarAdvisor_24)) {
						new ActionSave(Messages.ApplicationActionBarAdvisor_25, Activator.getDefault().getFileName()).run();
					}
				}

				MetaData model = new MetaData();
				try {
					model.getProperties().setVersion(Activator.getFeatureVersion());
					Activator.getDefault().setCurrentModel(model.getXml(true), null);

					Activator.getDefault().setFileName(""); //$NON-NLS-1$
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		close.setToolTipText("Close current model"); //$NON-NLS-1$
		close.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("close"))); //$NON-NLS-1$

		newModel = new Action(Messages.ApplicationActionBarAdvisor_16) { //$NON-NLS-1$
			@Override
			public void run() {
				if (Activator.getDefault().hasChanged()) {
					if (MessageDialog.openQuestion(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.ApplicationActionBarAdvisor_26, Messages.ApplicationActionBarAdvisor_27)) {
						new ActionSave(Messages.ApplicationActionBarAdvisor_28, Activator.getDefault().getFileName()).run();
					}
				}
				MetaData model = new MetaData();
				try {
					model.getProperties().setVersion(Activator.getFeatureVersion());
					Activator.getDefault().setCurrentModel(model.getXml(true), null);
					Activator.getDefault().setFileName(""); //$NON-NLS-1$
					Activator.getDefault().getSessionSourceProvider().setCheckedIn(false);
				} catch (Exception e) {
					e.printStackTrace();
				}

				DialogDocumentProperties d = new DialogDocumentProperties(window.getShell());
				d.open();
			}
		};

		newModel.setToolTipText(Messages.ApplicationActionBarAdvisor_18); //$NON-NLS-1$
		newModel.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("new"))); //$NON-NLS-1$

		properties = new Action(Messages.ApplicationActionBarAdvisor_29) {
			public void run() {
				DialogDocumentProperties d = new DialogDocumentProperties(sh);
				d.open();
			}
		};
		properties.setToolTipText(Messages.ApplicationActionBarAdvisor_30);

		dependencies = new Action("Model dependencies") {
			@Override
			public void run() {
				
				try {
					RepositoryItem item = Activator.getDefault().getRepositoryConnection().getRepositoryService().getDirectoryItem(Activator.getDefault().getCurrentModelDirectoryItemId());
					
					DialogShowDependencies dial = new DialogShowDependencies(sh, item, Activator.getDefault().getRepositoryConnection(), DialogShowDependencies.TITLE);
					dial.open();
				} catch (Exception e) {
					
					MessageDialog.openInformation(sh, "Item dependencies", "The item must have been imported from the repository.");
					
				}
				
			}
		};
		dependencies.setToolTipText("Model dependencies");
		dependencies.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("dependencies")));
	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_20, IWorkbenchActionConstants.M_FILE); //$NON-NLS-1$
		MenuManager helpMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_21, IWorkbenchActionConstants.M_HELP); //$NON-NLS-1$
		MenuManager recentMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_22, IWorkbenchActionConstants.MB_ADDITIONS); //$NON-NLS-1$
		MenuManager toolMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_23, IWorkbenchActionConstants.M_WINDOW); //$NON-NLS-1$
		MenuManager editMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_31, IWorkbenchActionConstants.M_EDIT);

		menuBar.add(fileMenu);
		menuBar.add(new Separator());

		menuBar.add(editMenu);

		menuBar.add(toolMenu);
		menuBar.add(new Separator());

		menuBar.add(helpMenu);

		// editMenu
		editMenu.add(properties);
		// toolMenu
		toolMenu.add(language);

		// fileMenu
		fileMenu.add(newModel);
		fileMenu.add(load);
		fileMenu.add(recentMenu);
		fileMenu.add(new Separator());
		fileMenu.add(saveas);
		fileMenu.add(save);
		fileMenu.add(new Separator());
		fileMenu.add(close);
		fileMenu.add(new Separator());

		fileMenu.add(importModel);
		fileMenu.add(exportModel);
		fileMenu.add(dependencies);

		fileMenu.add(new Separator());
		fileMenu.add(preferenceAction);
		fileMenu.add(new Separator());
		fileMenu.add(exit);

		// recentMenu
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		String[] recent = new String[] { store.getString(PreferenceConstants.P_RECENTFILE1), store.getString(PreferenceConstants.P_RECENTFILE2), store.getString(PreferenceConstants.P_RECENTFILE3), store.getString(PreferenceConstants.P_RECENTFILE4), store.getString(PreferenceConstants.P_RECENTFILE5) };

		for (String s : recent) {
			if (!s.trim().equals("")) { //$NON-NLS-1$
				Action a = new ActionOpen(s);
				a.setText(s);
				recentMenu.add(a);
			}
		}

		// helpMenu
		helpMenu.add(about);

	}

	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolbar = new ToolBarManager(SWT.BORDER | SWT.RIGHT);
		coolBar.add(new ToolBarContributionItem(toolbar, "main")); //$NON-NLS-1$
		toolbar.add(new GroupMarker(IWorkbenchActionConstants.OPEN_EXT));

		toolbar.add(newModel);
		toolbar.add(saveas);
		toolbar.add(save);
		toolbar.add(load);
		toolbar.add(dependencies);
	}

}
