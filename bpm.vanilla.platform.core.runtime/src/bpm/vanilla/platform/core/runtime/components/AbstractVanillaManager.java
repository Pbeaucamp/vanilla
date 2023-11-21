package bpm.vanilla.platform.core.runtime.components;

import org.osgi.service.component.ComponentContext;

import bpm.vanilla.platform.core.runtime.dao.IVanillaDaoComponent;
import bpm.vanilla.platform.core.runtime.tools.OSGIHelper;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanilla.platform.logging.IVanillaLoggerService;

public abstract class AbstractVanillaManager {

	// If you want to put a Evaluation Version, please uncomment this line as well as the line relative to this constant.
	// Other code to uncomment:
	//  - VanillaPortail -> TopToolbar (java and ui)
	//  - Gwt common -> ConnexionPanel (java)
//	private static final boolean CHECK_LICENSE = false;

	private IVanillaDaoComponent dao;
	private IVanillaLoggerService logService;
	private IVanillaLogger logger;

	public void bind(IVanillaDaoComponent service) {
		this.dao = service;
		getLogger().info("IVanillaDaoComponent binded");
	}

	public void unbind(IVanillaDaoComponent service) {
		this.dao = null;
		getLogger().info("IVanillaDaoComponent unbinded");
	}

	public void bind(IVanillaLoggerService service) {
		this.logService = service;
	}

	public void unbind(IVanillaLoggerService service) {
		this.logger = null;
		this.logService = null;
	}

	protected IVanillaDaoComponent getDao() {
		return dao;
	}

	public abstract String getComponentName();

	protected abstract void init() throws Exception;

	public IVanillaLogger getLogger() {
		if (logger == null) {
			logger = logService.getLogger(getComponentName());
		}

		return logger;
	}

	public void activate(ComponentContext ctx) {
		try {
//			if (CHECK_LICENSE) {
//				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
//				Date limitDate = df.parse("20180430");
//				if (new Date().after(limitDate)) {
//					throw new RuntimeException("The licence is expired for this version of Vanilla.");
//				}
//			}
			
			init();
		} catch (Throwable ex) {
			getLogger().error("Error when initing - " + ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		}
		OSGIHelper.register(this);
	}

	public void desactivate(ComponentContext ctx) {
		OSGIHelper.unregister(this);
	}
}
