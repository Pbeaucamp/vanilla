package bpm.architect.web.server.security;

import java.io.InputStream;
import java.util.HashMap;

import bpm.architect.web.server.utils.UndoRedoPreparation;
import bpm.data.viz.core.IDataVizComponent;
import bpm.data.viz.core.RemoteDataVizComponent;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.remote.MdmRemote;
import bpm.vanilla.map.core.design.IMapDefinitionService;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.Customer;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class ArchitectSession extends CommonSession {

	private IMdmProvider mdmRemote;

	private Contract currentContract;
	private InputStream pendingNewVersion;
	private IDataVizComponent datavizRemote;
	private IMapDefinitionService mapRemote;

	private HashMap<DataPreparation, UndoRedoPreparation> undoRedos = new HashMap<>();
	private HashMap<String, DataPreparation> datapreps = new HashMap<>();

	public ArchitectSession() {

	}

	@Override
	public String getApplicationId() {
		return IRepositoryApi.ARCHITECT_WEB;
	}

	public IDataVizComponent getDatavizRemote() {
		if (datavizRemote == null) {
			this.datavizRemote = new RemoteDataVizComponent(getRepositoryConnection());
		}
		return datavizRemote;
	}

	public IMdmProvider getMdmRemote() {
		if (mdmRemote == null) {
			String login = getUser().getLogin();
			String password = getUser().getPassword();
			String vanillaUrl = getVanillaRuntimeUrl();
			this.mdmRemote = new MdmRemote(login, password, vanillaUrl);
		}
		return mdmRemote;
	}

	public IMapDefinitionService getRemoteMap() {
		if (mapRemote == null) {
			RemoteMapDefinitionService mapRemote = new RemoteMapDefinitionService();
			mapRemote.setVanillaRuntimeUrl(getVanillaRuntimeUrl());
			this.mapRemote = mapRemote;
		}
		return mapRemote;
	}

	public Contract getCurrentContract() {
		return currentContract;
	}

	public void setCurrentContract(Contract currentContract) {
		this.currentContract = currentContract;
	}

	public InputStream getPendingNewVersion() {
		return pendingNewVersion;
	}

	public void setPendingNewVersion(InputStream pendingNewVersion) {
		this.pendingNewVersion = pendingNewVersion;
	}

	public Customer getCustomer() {
		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
		return conf.getCustomer();
	}

	public UndoRedoPreparation getUndoRedo(DataPreparation dp) {
		if (undoRedos.get(dp) == null) {
			undoRedos.put(dp, new UndoRedoPreparation());
		}
		return undoRedos.get(dp);
	}

	public HashMap<String, DataPreparation> getDatapreps() {
		return datapreps;
	}

	public void setDatapreps(HashMap<String, DataPreparation> datapreps) {
		this.datapreps = datapreps;
	}
}
