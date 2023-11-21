package bpm.vanilla.platform.core.runtime.components;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.ComponentContext;

import bpm.vanilla.platform.core.IArchiveManager;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.ArchiveType;
import bpm.vanilla.platform.core.beans.ArchiveTypeItem;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.runtime.dao.platform.ArchiveThreadManager;

public class ArchiveManager extends AbstractVanillaManager implements IArchiveManager {

	private IVanillaContext rootVanillaCtx;
	private ArchiveThreadManager thread;
	
	public void activate(ComponentContext ctx) {
		try {
			super.activate(ctx);
			
			String delay = ConfigurationManager.getProperty(VanillaConfiguration.P_ARCHIVE_THREAD_DELAY);
			int delayVal = 1;
			if(delay != null && !delay.isEmpty()) {
				delayVal = Integer.parseInt(delay);
			}
			
			thread = new ArchiveThreadManager(delayVal * 60 * 1000, this);
			thread.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ArchiveType addArchiveType(ArchiveType type) throws Exception {
		int id = (Integer) getDao().getDocumentDao().save(type);
		type.setId(id);
		return type;
	}

	@Override
	public void deleteArchiveType(ArchiveType type) throws Exception {
		getDao().getDocumentDao().delete(type);
	}

	@Override
	public ArchiveType updateArchiveType(ArchiveType type) throws Exception {
		getDao().getDocumentDao().update(type);
		return type;
	}

	@Override
	public List<ArchiveType> getArchiveTypes() throws Exception {
		List<ArchiveType> res = getDao().getDocumentDao().find("from ArchiveType");
		if(res == null) {
			return new ArrayList<ArchiveType>();
		}
		return res;
	}

	@Override
	protected void init() throws Exception {
		this.rootVanillaCtx = getRootVanillaContext();
	}

	@Override
	public String getComponentName() {
		return getClass().getName();
	}
	
	public IVanillaContext getRootVanillaContext() {
		if(rootVanillaCtx == null) {
			VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
			String vanillaUrl = config.getVanillaServerUrl();
			String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
			String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
			rootVanillaCtx = new BaseVanillaContext(vanillaUrl, login, password);
		}
		return rootVanillaCtx;
	}

	@Override
	public ArchiveTypeItem linkItemArchiveType(int archiveTypeId, int itemId, int repositoryId, boolean isDirectory) throws Exception {
		
		ArchiveTypeItem archiveItem = new ArchiveTypeItem();
		archiveItem.setArchiveTypeId(archiveTypeId);
		archiveItem.setDirectory(isDirectory);
		archiveItem.setItemId(itemId);
		archiveItem.setRepositoryId(repositoryId);
		
		int id = (Integer) getDao().getDocumentDao().save(archiveItem);
		archiveItem.setId(id);
		return archiveItem;
	}

	@Override
	public ArchiveTypeItem getArchiveTypeByItem(int itemId, int repositoryId, boolean isDirectory) throws Exception {
		List<ArchiveTypeItem> ress = (List<ArchiveTypeItem>) getDao().getDocumentDao().find("from ArchiveTypeItem where itemId = " + itemId + " and repositoryId = " + repositoryId + " and directory = " + isDirectory );
		if(ress != null && !ress.isEmpty()) {
			return ress.get(0);
		}
		return null;
		
		
	}

	@Override
	public List<ArchiveTypeItem> getArchiveTypeByArchive(int archiveTypeId) throws Exception {
		List<ArchiveTypeItem> links = getDao().getDocumentDao().find("From ArchiveTypeItem where archiveTypeId = " + archiveTypeId);
		return links;
	}

}
