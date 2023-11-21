package bpm.vanilla.platform.core.runtime.components;

import java.io.Serializable;

import org.osgi.service.component.ComponentContext;

import bpm.vanilla.platform.core.IGlobalManager;
import bpm.vanilla.platform.core.ManageAction;
import bpm.vanilla.platform.core.runtime.dao.GlobalDAO;

public class GlobalManager extends AbstractVanillaManager implements IGlobalManager {

	private GlobalDAO globalDao;

	public void activate(ComponentContext ctx) {
		super.activate(ctx);
	}

	@Override
	protected void init() throws Exception {
		this.globalDao = getDao().getGlobalDAO();
		if (this.globalDao == null) {
			throw new Exception("Missing GlobalDao");
		}

		getLogger().info("init done!");
	}

	@Override
	public String getComponentName() {
		return getClass().getName();
	}

	@Override
	public Serializable manageItem(Serializable item, ManageAction action) throws Exception {
		return globalDao.manageItem(item, action);
	}

}
