package bpm.gwt.commons.server;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.api.RemoteFreeMetricsManager;
import bpm.fm.api.model.HasItemLinked;
import bpm.fm.api.model.Observatory;
import bpm.gwt.commons.client.services.FreeMetricService;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.mdm.model.supplier.Contract;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FreeMetricServiceImpl extends RemoteServiceServlet implements FreeMetricService {

	private static final long serialVersionUID = 1L;

	private CommonSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), CommonSession.class);
	}

	@Override
	public HashMap<Group, List<Observatory>> getObservatories() throws Exception {
		
		HashMap<Group, List<Observatory>> result = new LinkedHashMap<Group, List<Observatory>>();
		
		List<Group> groups = getSession().getVanillaApi().getVanillaSecurityManager().getGroups(getSession().getUser());
		
		IFreeMetricsManager fmApi = new RemoteFreeMetricsManager(getSession().getVanillaContext());
		
		for(Group g: groups) {
			List<Observatory> obs = fmApi.getObservatoriesByGroup(g.getId());
			result.put(g, obs);
		}
		
		return result;
	}
	
	@Override
	public String createAxeETL(String etlName, HasItemLinked hasItemLinked, Contract selectedContract, HashMap<String, Integer> columnsIndex) throws Exception {
		try {
			IRepositoryContext ctx = getSession().getRepositoryConnection().getContext();
			IFreeMetricsManager fmApi = new RemoteFreeMetricsManager(getSession().getVanillaContext());
			return fmApi.createAxeETL(etlName, ctx, hasItemLinked, selectedContract, columnsIndex);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	@Override
	public String createAxeETL(String etlName, HasItemLinked hasItemLinked, RepositoryItem metadata, String queryName, HashMap<String, Integer> columnsIndex) throws Exception {
		try {
			IRepositoryContext ctx = getSession().getRepositoryConnection().getContext();
			IFreeMetricsManager fmApi = new RemoteFreeMetricsManager(getSession().getVanillaContext());
			return fmApi.createAxeETL(etlName, ctx, hasItemLinked, metadata, queryName, columnsIndex);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
}
