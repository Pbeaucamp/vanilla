package bpm.odata.service;

import org.osgi.service.http.HttpService;

import bpm.odata.service.data.DataManager;
import bpm.odata.service.servlets.DownloadDataServlet;
import bpm.odata.service.servlets.KpiServlet;
import bpm.odata.service.servlets.MetadataServlet;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanilla.platform.logging.IVanillaLoggerService;

public class OpenDataComponent {

	public static final String PLUGIN_ID = "bpm.odata.service";

	private IVanillaLogger logger;
	private HttpService httpService;

	private DataManager dataManager;

	public OpenDataComponent() {
	}

	public void bind(IVanillaLoggerService service) {
		this.logger = service.getLogger(PLUGIN_ID);
		this.logger.info("IVanillaLoggerService binded");

	}

	public void unbind(IVanillaLoggerService service) {
		this.logger.info("IVanillaLoggerService unbinded");
		this.logger = null;
	}

	public void bind(HttpService httpService) {
		this.httpService = httpService;
		try {
			registerServlets();
			logger.info("HttpService binded");
		} catch (Exception ex) {
			logger.error("Unable  to bind servlets " + ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}
	}

	public void unbind(HttpService service) {
		unregisterServlets();
		logger.info("Servlet Released");
		this.httpService = null;
		logger.info("HttpService unbinded");
	}

	private void registerServlets() {
		try {
			httpService.registerServlet(MetadataServlet.OPEN_DATA_SERVLET, new MetadataServlet(this), null, null);
			httpService.registerServlet(DownloadDataServlet.DOWNLOAD_DATA_SERVLET, new DownloadDataServlet(this), null, null);
			httpService.registerServlet(KpiServlet.OPEN_DATA_SERVLET, new KpiServlet(this), null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void unregisterServlets() {
		httpService.unregister(MetadataServlet.OPEN_DATA_SERVLET);
		httpService.unregister(DownloadDataServlet.DOWNLOAD_DATA_SERVLET);
		httpService.unregister(KpiServlet.OPEN_DATA_SERVLET);
	}

	public void activate() {
	}

	public void deactivate() {
	}

	public DataManager getDataManager() {
		if (dataManager == null) {
			this.dataManager = new DataManager();
		}
		return dataManager;
	}

	public IVanillaLogger getLogger() {
		return logger;
	}
}
