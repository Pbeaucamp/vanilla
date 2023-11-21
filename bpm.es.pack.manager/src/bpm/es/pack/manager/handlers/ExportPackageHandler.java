package bpm.es.pack.manager.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import adminbirep.Activator;
import bpm.birep.admin.client.preferences.PreferenceConstants;
import bpm.es.pack.manager.I18N.Messages;
import bpm.es.pack.manager.utils.PackageCreator;
import bpm.es.pack.manager.wizard.exp.ExportWizard;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.workplace.core.model.PlaceImportDirectory;
import bpm.vanilla.workplace.core.model.PlaceImportItem;
import bpm.vanilla.workplace.core.model.VanillaPackage;

public class ExportPackageHandler extends AbstractHandler {

	public static final String COMMAND_ID = "bpm.es.pack.manager.commands.ImportPackageHandler"; //$NON-NLS-1$

	private String packageLocation;
	
	public ExportPackageHandler() {
		super.setBaseEnabled(true);
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
		final Repository repository;
		try {
			repository = new Repository(Activator.getDefault().getRepositoryApi());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}


		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		packageLocation = store.getString(PreferenceConstants.P_BPM_PACKAGES_FOLDER);
		if(packageLocation == null || packageLocation.isEmpty()){
			packageLocation = "Packages"; //$NON-NLS-1$
		}

		ExportWizard wizard = new ExportWizard(false, repository);

		wizard.init(Activator.getDefault().getWorkbench(), (IStructuredSelection) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection());
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);

		dialog.create();
		wizard.getContainer().getShell().setSize(800, wizard.getContainer().getShell().getSize().y);

		if (dialog.open() == Dialog.OK) {
			final VanillaPackage vanillaPackage = wizard.getVanillaPackage();
			final boolean isFullExport = wizard.isFullExport();
			final List<PlaceImportDirectory> selectedDirectories = wizard.getSelectedDirectories();
			final List<PlaceImportItem> selectedItems = wizard.getSelectedItems();

			IRunnableWithProgress r = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

					monitor.setTaskName(Messages.ExportPackageHandler_0);
					monitor.beginTask(Messages.ExportPackageHandler_1, 1);
					try {
						
						IRepositoryApi repositoryApi = Activator.getDefault().getRepositoryApi();

						List<RepositoryDirectory> dirs = repository.getRootDirectories();
						PackageCreator.getChilds(repositoryApi, vanillaPackage, null, dirs, repository, selectedDirectories, selectedItems, isFullExport, new HashMap<Integer, Object>());
						
						List<Group> grps = new RemoteVanillaPlatform(repositoryApi.getContext().getVanillaContext()).getVanillaSecurityManager().getGroups();
						
						vanillaPackage.setGroups(grps);
						if(vanillaPackage.isIncludeGrants()) {
							PackageCreator.getSecurity(repositoryApi, vanillaPackage.getDirectories(), vanillaPackage.getItems(), grps);
						}
						if(vanillaPackage.isIncludeRoles()) {
							PackageCreator.getRoles(repositoryApi, grps, vanillaPackage);
						}
						
						if(vanillaPackage.isIncludeHistorics()) {
							PackageCreator.getHistorics(repositoryApi, vanillaPackage, selectedItems);
						}
						
						PackageCreator.createPackage(vanillaPackage, Activator.getDefault().getRepositoryApi(), packageLocation); //$NON-NLS-1$
						monitor.worked(1);
					} catch (Exception e) {
						e.printStackTrace();
						throw new InterruptedException(e.getMessage());
					}
				}
			};

			IProgressService service = PlatformUI.getWorkbench().getProgressService();
			try {
				service.run(true, false, r);
				MessageDialog.openInformation(dialog.getShell(), Messages.ExportPackageHandler_2, Messages.ExportPackageHandler_3);
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(dialog.getShell(), Messages.ExportPackageHandler_4, Messages.ExportPackageHandler_5 + e.getMessage());
			}
		}
		return null;
	}

	@Override
	public void setEnabled(Object evaluationContext) {
		super.setBaseEnabled(true);
	}
}
