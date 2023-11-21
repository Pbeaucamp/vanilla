package bpm.vanilla.server.reporting.pool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dom4j.DocumentException;
import org.eclipse.birt.report.engine.api.IReportEngine;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.metadata.MetaDataReader;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.utils.MD5Helper;
import bpm.vanilla.server.commons.pool.PoolableModel;
import bpm.vanilla.server.commons.pool.VanillaItemKey;

import com.thoughtworks.xstream.XStream;

public class FwrPoolableModel extends PoolableModel<FWRReport> {

	private IReportEngine engine;

	public FwrPoolableModel(RepositoryItem directoryItem, String xml, VanillaItemKey itemKey, IReportEngine engine) throws Exception {
		super(directoryItem, xml, itemKey);

		this.engine = engine;
		
		XStream xstream = new XStream();
		FWRReport model = (FWRReport) xstream.fromXML(xml);
		setModel(model);
		overrideFmdtConnection();

	}

	/**
	 * added by ere, so a FWR can easily load a fmdt model
	 * 
	 * @param dirItemId
	 * @param groupName
	 * @return
	 * @throws Exception
	 */
	public Collection<IBusinessModel> loadFMDTModel(int dirItemId, String groupName) throws Exception {
		return loadFMDTModel(dirItemId, groupName, true);
	}

	public Collection<IBusinessModel> loadFMDTModel(int dirItemId, String groupName, boolean isLightWeight) throws Exception {
		IRepositoryApi sock = new RemoteRepositoryApi(getItemKey().getRepositoryContext());

		RepositoryItem fmdtIetm = sock.getRepositoryService().getDirectoryItem(dirItemId);

		String xml = sock.getRepositoryService().loadModel(fmdtIetm);
		InputStream input = IOUtils.toInputStream(xml);
		Collection<IBusinessModel> models = MetaDataReader.read(groupName, input, sock, isLightWeight);

		return models;
	}

	@Override
	protected void buildModel() throws Exception {}

	synchronized private void overrideFmdtConnection() throws DocumentException, IOException {

		VanillaConfiguration vanillaConfig = ConfigurationManager.getInstance().getVanillaConfiguration();

		List<DataSet> datasets = getModel().getAllDatasets();
		for(DataSet ds : datasets) {
			if(!getItemKey().getRepositoryContext().getVanillaContext().getPassword().matches("[0-9a-f]{32}")) {
				ds.getDatasource().setEncrypted(true);
				ds.getDatasource().setPassword(MD5Helper.encode(getItemKey().getRepositoryContext().getVanillaContext().getPassword()));
			}
			else {
				ds.getDatasource().setPassword(getItemKey().getRepositoryContext().getVanillaContext().getPassword());
			}

			ds.getDatasource().setUser(getItemKey().getRepositoryContext().getVanillaContext().getLogin());
			ds.getDatasource().setGroup(getItemKey().getRepositoryContext().getGroup().getName());

			String serverSideUrl = vanillaConfig.translateClientUrlToServer(getItemKey().getRepositoryContext().getRepository().getUrl());
			ds.getDatasource().setUrl(serverSideUrl);
		}
	}

	public IReportEngine getBirtEngine() {
		return engine;
	}
}
