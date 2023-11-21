package bpm.vanilla.platform.core.runtime.components;

import java.util.List;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IUnitedOlapPreloadManager;
import bpm.vanilla.platform.core.beans.UOlapPreloadBean;
import bpm.vanilla.platform.core.runtime.dao.unitedolap.UnitedOlapPreloadDAO;

public class UnitedOlapPreloadManager extends AbstractVanillaManager implements IUnitedOlapPreloadManager{

	private UnitedOlapPreloadDAO unitedOlapPreloadDao;
	
	@Override
	protected void init() throws Exception {
		this.unitedOlapPreloadDao = getDao().getUnitedOlapPreloadDao();
		if (this.unitedOlapPreloadDao == null){
			throw new Exception("Missing UnitedOlapPreloadDAO!");
		}
	}

	@Override
	public String getComponentName() {
		return getClass().getName();
	}

	@Override
	public void addPreload(UOlapPreloadBean bean) throws Exception {
		if (bean == null){
			throw new Exception("Cannot save a null UnitedOlapPreload");
		}

		try{
			unitedOlapPreloadDao.save(bean);
		}catch(Exception ex){
			throw new Exception("Unable to save UOlapPreloadBean - " + ex.getMessage(), ex);
		}
		
	}

	@Override
	public List<UOlapPreloadBean> getPreloadForIdentifier(IObjectIdentifier identifier) throws Exception {

		try{
			return unitedOlapPreloadDao.list(identifier.getRepositoryId(), identifier.getDirectoryItemId());
		}catch(Exception ex){
			throw new Exception("Unable to find UOlapPreloadBean for {" + identifier.getRepositoryId() + "," + identifier.getDirectoryItemId() +"} - " + ex.getMessage(), ex);
		}
	}

	@Override
	public void removePreload(UOlapPreloadBean bean) throws Exception {
		try{
			unitedOlapPreloadDao.delete(bean);
		}catch(Exception ex){
			throw new Exception("Unable to delete UOlapPreloadBean - " + ex.getMessage(), ex);
		}
		
	}
}
