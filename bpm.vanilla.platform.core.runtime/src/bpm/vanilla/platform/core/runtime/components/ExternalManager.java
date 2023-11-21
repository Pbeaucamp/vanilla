package bpm.vanilla.platform.core.runtime.components;

import java.util.HashMap;
import java.util.List;

import org.osgi.service.component.ComponentContext;

import bpm.vanilla.platform.core.IExternalManager;
import bpm.vanilla.platform.core.beans.resources.D4C;
import bpm.vanilla.platform.core.beans.resources.D4CItem;
import bpm.vanilla.platform.core.beans.resources.D4CItem.TypeD4CItem;
import bpm.vanilla.platform.core.runtime.dao.external.ExternalDao;
import bpm.vanilla.platform.core.utils.D4CHelper;

public class ExternalManager extends AbstractVanillaManager implements IExternalManager {

	private ExternalDao externalDao;

	public void activate(ComponentContext ctx) {
		super.activate(ctx);
	}

	@Override
	protected void init() throws Exception {
		this.externalDao = getDao().getExternalDao();
		if (this.externalDao == null) {
			throw new Exception("Missing ExternalDao");
		}

		getLogger().info("init done!");
	}

	@Override
	public String getComponentName() {
		return getClass().getName();
	}

	@Override
	public List<D4C> getD4CDefinitions() throws Exception {
		return externalDao.getD4CDefinitions();
	}
	
	@Override
	public HashMap<String, HashMap<String, List<D4CItem>>> getD4cItems(int parentId, TypeD4CItem type) throws Exception {
		switch (type) {
		case FROM_D4C:
			D4C data4citizen = externalDao.getD4CDefinition(parentId);
			
			D4CHelper helper = new D4CHelper(data4citizen.getUrl(), "", data4citizen.getLogin(), data4citizen.getPassword());
			return helper.getVisualizations();
		case VISUALIZATION:
			List<D4CItem> d4cItems = externalDao.getD4cItems(parentId, type);
			
			HashMap<String, HashMap<String, List<D4CItem>>> items = new HashMap<String, HashMap<String,List<D4CItem>>>();
			items.put("Global", new HashMap<String, List<D4CItem>>());
			items.get("Global").put("Datasets", d4cItems);
			return items;
		default:
			break;
		}
		
		return new HashMap<String, HashMap<String,List<D4CItem>>>();
	}
}
