package bpm.es.pack.manager.wizard.exp;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.es.pack.manager.I18N.Messages;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.workplace.core.model.PlaceImportDirectory;
import bpm.vanilla.workplace.core.model.PlaceImportItem;
import bpm.vanilla.workplace.core.model.VanillaPackage;

public class ExportWizard extends Wizard implements INewWizard {

	private Repository repository;

	private InfoPage general;
	private TypePage type;
	private SelectionPage selection;
	private OptionPage option;

	private VanillaPackage vanillaPackage;
	private boolean exportToVanillaPlace = false;

	private boolean isFullExport;
	private List<RepositoryDirectory> selectedDirectories;
	private List<RepositoryItem> selectedItems;

	public ExportWizard(boolean exportToVanillaPlace, Repository repository) {
		this.exportToVanillaPlace = exportToVanillaPlace;
		this.repository = repository;
	}

	@Override
	public boolean performFinish() {
		vanillaPackage = new VanillaPackage();
		vanillaPackage.setName(general.getName());
		vanillaPackage.setDescription(general.getDescription());

		if (type.isFullExport()) {
			isFullExport = true;
		}
		else {
			isFullExport = false;
			selectedDirectories = selection.getSelectedDirectories();
			selectedItems = selection.getSelectedItems();
		}

		if (!exportToVanillaPlace) {
			vanillaPackage.setIncludeGrants(option.includeGrants());
			vanillaPackage.setIncludeGroups(option.includeGroups());
			vanillaPackage.setIncludeHistorics(option.includeHistorics());
			vanillaPackage.setIncludeRoles(option.includeRoles());
		}

		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		general = new InfoPage("General"); //$NON-NLS-1$
		general.setTitle(Messages.ExportWizard_0);
		general.setDescription(Messages.ExportWizard_1);
		addPage(general);

		type = new TypePage(Messages.ExportWizard_2);
		type.setTitle(Messages.ExportWizard_3);
		type.setDescription(Messages.ExportWizard_4);
		addPage(type);

		selection = new SelectionPage(Messages.ExportWizard_5, repository);
		selection.setTitle(Messages.ExportWizard_6);
		selection.setDescription(Messages.ExportWizard_7);
		addPage(selection);

		if (!exportToVanillaPlace) {
			option = new OptionPage(Messages.ExportWizard_8);
			option.setTitle(Messages.ExportWizard_9);
			option.setDescription(Messages.ExportWizard_10);
			addPage(option);
		}
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (option != null) {
			if (page == type) {
				if (option != null) {
					if (type.isFullExport()) {
						return option;
					}
					else {
						return selection;
					}
				}
				else {
					return null;
				}
			}
		}
		return super.getNextPage(page);
	}

	@Override
	public boolean canFinish() {
		return (exportToVanillaPlace && selection.isCurrentPage()) || (!exportToVanillaPlace && option.isCurrentPage());
	}

	public VanillaPackage getVanillaPackage() {
		return vanillaPackage;
	}

	public List<PlaceImportDirectory> getSelectedDirectories() {
		List<PlaceImportDirectory> dirs = new ArrayList<PlaceImportDirectory>();
		if (selectedDirectories != null) {
			for (RepositoryDirectory dir : selectedDirectories) {
				PlaceImportDirectory d = new PlaceImportDirectory(dir);
				dirs.add(d);
			}
		}
		return dirs;
	}

	public List<PlaceImportItem> getSelectedItems() {
		List<PlaceImportItem> dirs = new ArrayList<PlaceImportItem>();
		if (selectedItems != null) {
			for (RepositoryItem dir : selectedItems) {
				PlaceImportItem d = new PlaceImportItem(dir);
				dirs.add(d);
			}
		}
		return dirs;
	}

	public boolean isFullExport() {
		return isFullExport;
	}
}
