package bpm.es.dndserver.api.migrator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import bpm.es.datasource.analyzer.remapper.FactoryRemapperPerformer;
import bpm.es.datasource.analyzer.remapper.IRemapperPerformer;
import bpm.es.datasource.analyzer.remapper.ModelRemapper;
import bpm.es.dndserver.Messages;
import bpm.es.dndserver.api.DNDOProject;
import bpm.es.dndserver.api.Message;
import bpm.es.dndserver.api.ProjectMessenger;
import bpm.es.dndserver.api.fmdt.FMDTMigration;
import bpm.es.dndserver.api.fmdt.FMDTReplace;
import bpm.es.dndserver.api.fmdt.replacers.IFMDTReplacer;
import bpm.es.dndserver.api.repository.AxisDirectoryItemWrapper;
import bpm.es.dndserver.tools.OurLogger;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.server.client.ui.clustering.menu.Activator;

/**
 * 
 * @author ludo
 * 
 */
public class Migrator implements IRunnableWithProgress {

	private List<RepositoryItem[]> pushed = new ArrayList<RepositoryItem[]>();
	private boolean hasRun = false;
	private DNDOProject project;

	private List<AxisDirectoryItemWrapper> items;
	private IRepositoryApi input;
	private IRepositoryApi output;
	private List<Integer> groups;
	private ProjectMessenger messenger;

	public Migrator(DNDOProject project) {
		this.project = project;
	}

	protected RepositoryItem getPushed(RepositoryItem item) {

		for (RepositoryItem[] it : pushed) {
			if (it[0].getId() == item.getId()) {
				return it[0];
			}
		}
		return null;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		if (hasRun) {
			throw new InterruptedException(Messages.Migrator_0);
		}

		int k = 0;
		monitor.beginTask(Messages.Migrator_1, items.size());

		for (AxisDirectoryItemWrapper i : items) {
			monitor.subTask(Messages.Migrator_2 + i.getAxisItem().getItemName());

			try {
				replicateObjectAndDependencies(i, input, output, groups);

			} catch (Exception ex) {
				ex.printStackTrace();
				messenger.addMessage(new Message(i.getAxisItem().getId(), i.getAxisItem().getItemName(), Message.ERROR, Messages.Migrator_3 + ex.getMessage()));
			}
			monitor.worked(k++);
		}

		hasRun = true;

	}

	public void migrate(List<AxisDirectoryItemWrapper> items, IRepositoryApi input, IRepositoryApi output, List<Integer> groups, ProjectMessenger messenger) throws Exception {
		this.input = input;
		this.items = items;
		this.output = output;
		this.groups = groups;
		this.messenger = messenger;
	}

	private RepositoryItem replicateObjectAndDependencies(AxisDirectoryItemWrapper item, IRepositoryApi input, IRepositoryApi output, List<Integer> groupsId) throws Exception {

		/*
		 * we check if the given item has already been pushed if it has been, we
		 * just return the pushed one
		 */
		for (RepositoryItem[] p : pushed) {
			if (p[0].getId() == item.getAxisItem().getId()) {
				OurLogger.info(Messages.Migrator_4 + IRepositoryApi.TYPES_NAMES[item.getAxisItem().getType()] + " " + item.getAxisItem().getItemName() + Messages.Migrator_6 + item.getAxisItem().getId() + Messages.Migrator_7); //$NON-NLS-1$
				return p[1];
			}
		}

		OurLogger.info(Messages.Migrator_8 + IRepositoryApi.TYPES_NAMES[item.getAxisItem().getType()] + " " + item.getAxisItem().getItemName() + Messages.Migrator_10 + item.getAxisItem().getId()); //$NON-NLS-1$
		RepositoryDirectory directory = item.getDirectory();

		FactoryRemapperPerformer factory = new FactoryRemapperPerformer();

		List<FMDTMigration> replacements = new ArrayList<FMDTMigration>();

		for (AxisDirectoryItemWrapper wrapped : item.getDependencies()) {
			wrapped.setDirectory(directory);
			if (wrapped.getAxisItem().getType() == IRepositoryApi.FMDT_TYPE) {
				FMDTMigration mig = item.getExistingMigration(wrapped.getAxisItem().getId());
				if (mig != null && mig.getSource() != null && mig.getTarget() != null) {
					OurLogger.info(Messages.Migrator_11 + IRepositoryApi.TYPES_NAMES[wrapped.getAxisItem().getType()] + " " + wrapped.getAxisItem().getItemName() + Messages.Migrator_13 + wrapped.getAxisItem().getId() + Messages.Migrator_14); //$NON-NLS-1$
					pushed.add(new RepositoryItem[] { wrapped.getAxisItem(), output.getRepositoryService().getDirectoryItem(mig.getTarget().getDirItemId()) });
					replacements.add(mig);
					continue;
				}
			}
			RepositoryItem pushed = replicateObjectAndDependencies(wrapped, input, output, groupsId);
			this.pushed.add(new RepositoryItem[] { wrapped.getAxisItem(), pushed });
		}

		RepositoryItem axisItem = item.getAxisItem();

		// output.add
		ModelRemapper remapper = factory.createPerfomer(input, output, item.getAxisItem(), this.pushed, project.getOutputRepository().getGroupName());
		for (IRemapperPerformer p : remapper.getPerformers()) {
			p.performModification();
			OurLogger.info(Messages.Migrator_15 + IRepositoryApi.TYPES_NAMES[item.getAxisItem().getType()] + " " + item.getAxisItem().getItemName() + Messages.Migrator_17 + item.getAxisItem().getId() + " " + p.getTaskPerfomed()); //$NON-NLS-1$ //$NON-NLS-2$
		}

		try {

			RepositoryItem pushed = getPushed(axisItem);

			if (pushed == null) {
				RepositoryItem p = null;
				if (axisItem.getType() == IRepositoryApi.EXTERNAL_DOCUMENT) {
					// load the file
					File file = new File("externalDocTmp_" + new Object().hashCode()); //$NON-NLS-1$
					FileOutputStream fos = new FileOutputStream(file);

					input.getDocumentationService().importExternalDocument(axisItem, fos);

					p = output.getRepositoryService().addExternalDocumentWithDisplay(directory, axisItem.getItemName() + Messages.Migrator_21, axisItem.getComment(), "", "", new FileInputStream(file), axisItem.isDisplay(), ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

					/*
					 * apply Secu
					 */
					for (Integer s : groupsId) {

						/*
						 * set Item accessible
						 */
						try {

							output.getAdminService().addGroupForItem(s, p.getId());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					pushed = p;
					file.delete();
				}
				else {
					String xml = remapper.getModelXmlDocument("UTF-8"); //$NON-NLS-1$

					/*
					 * perform FMDTReplacements
					 */
					IFMDTReplacer replacer = FMDTReplace.getReplacer(item);
					boolean modified = false;
					if (replacer != null) {
						for (FMDTMigration m : replacements) {
							xml = replacer.replace(xml, m.getSource(), m.getTarget());

						}
					}
					pushed = pushDirectoryItem(output, item, directory, xml, groupsId);
				}

				OurLogger.info(Messages.Migrator_27 + IRepositoryApi.TYPES_NAMES[item.getAxisItem().getType()] + " " + item.getAxisItem().getItemName() + Messages.Migrator_29 + item.getAxisItem().getId() + "=" + pushed.getId()); //$NON-NLS-2$ //$NON-NLS-1$
				this.pushed.add(new RepositoryItem[] { axisItem, pushed });
			}

			hasRun = true;
			return pushed;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

		// output.updateModel(item.getAxisItem(), bos.toString("UTF-8").trim());
		// OurLogger.info("updated " + axisItem.getName());
	}

	protected RepositoryItem pushDirectoryItem(IRepositoryApi output, AxisDirectoryItemWrapper item, RepositoryDirectory target, String xmlModel, List<Integer> groups) throws Exception {

		String xml = xmlModel.trim();
		// XXX : fucking Marc!!!!!
		if (item.getAxisItem().getType() == IRepositoryApi.FAV_TYPE) {
			xml = "<root>" + xml + "</root>"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		RepositoryItem p = output.getRepositoryService().addDirectoryItemWithDisplay(item.getAxisItem().getType(), item.getAxisItem().getSubtype(), target, //$NON-NLS-1$
				item.getAxisItem().getItemName() + Messages.Migrator_34, item.getAxisItem().getComment(), "", //$NON-NLS-1$
				"", xml, item.getAxisItem().isDisplay()); //$NON-NLS-1$

		/*
		 * apply secu on Repository
		 */
		for (Integer s : groups) {

			/*
			 * set Item accessible
			 */
			try {
				output.getAdminService().addGroupForItem(s, p.getId());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			/*
			 * if required set Item Runnable
			 */
			if (item.getAxisItem().getType() == IRepositoryApi.FD_TYPE || item.getAxisItem().getType() == IRepositoryApi.FWR_TYPE || (item.getAxisItem().getType() == IRepositoryApi.CUST_TYPE)) {

				try {
					Group group = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroupById(s);
					output.getAdminService().setObjectRunnableForGroup(group.getId(), p);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		RepositoryItem i = p;
		return i;
	}

	// private FMDTDataSource copyFDMTDatasource(FMDTDataSource source) {
	// FMDTDataSource data = new FMDTDataSource();
	//		
	// data.setDataSourceName(source.getDataSourceName());
	// data.setUser(source.getUser());
	// data.setPass(source.getPass());
	// data.setUrl(source.getUrl());
	// data.setDirItemId(source.getDirItemId());
	// data.setBusinessModel(source.getBusinessModel());
	// data.setBusinessPackage(source.getBusinessPackage());
	// data.setConnectionName(source.getConnectionName());
	// data.setGroupName(source.getGroupName());
	//		
	// return data;
	// }
}
