package bpm.vanilla.platform.core.runtime.components;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.dataprovider.odainput.OdaInputDigester;
import bpm.dataprovider.odainput.consumer.QueryHelper;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.xstream.RemoteAdminManager;
import bpm.smart.core.xstream.RemoteSmartManager;
import bpm.smart.core.xstream.RemoteWorkflowManager;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IRepositoryManager;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.OdaInput;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter.TypeParameter;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.VanillaParameterComponent;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.DatasProvider;
import bpm.vanilla.platform.core.repository.ILinkedParameter;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;

/**
 * This class is Responsible for getting the parametersValues for a runnable
 * object.
 * 
 * @author ludo
 * 
 */
public class VanillaParameterComponentImpl extends AbstractVanillaManager implements VanillaParameterComponent {

	private IRepositoryManager repositoryManager;
	private IVanillaSecurityManager securityManager;

	@Override
	protected void init() throws Exception {
		if (repositoryManager == null) {
			throw new Exception("No repository manager!");
		}
		if (securityManager == null) {
			throw new Exception("No security manager!");
		}
	}

	public void bind(IRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

	public void bind(IVanillaSecurityManager securityManager) {
		this.securityManager = securityManager;
	}

	public void unbind(IRepositoryManager repositoryManager) {
		this.repositoryManager = null;
	}

	public void unbind(IVanillaSecurityManager securityManager) {
		this.securityManager = null;
	}

	/**
	 * regroup parameters
	 * 
	 * @param l
	 * @return
	 */
	private HashMap<VanillaGroupParameter, List<Parameter>> getGroups(List<Parameter> l) {
		// unbelivable methods..........
		List<Parameter> roots = new ArrayList<Parameter>();
		List<Parameter> usedParams = new ArrayList<Parameter>();

		HashMap<VanillaGroupParameter, List<Parameter>> map = new LinkedHashMap<VanillaGroupParameter, List<Parameter>>();
		for (Parameter p : l) {
			if (p.getRequestecParameters().isEmpty()) {
				roots.add(p);
				/*
				 * create Group
				 */
				VanillaGroupParameter group = new VanillaGroupParameter();
				group.setDisplayName("Group " + p.getName());
				group.setName("Group " + p.getName());
				group.setPromptText("");

				map.put(group, new ArrayList<Parameter>());
				map.get(group).add(p);
				if (!usedParams.contains(p)) {
					usedParams.add(p);
				}
			}
		}

		/*
		 * fill groups
		 */
		for (Parameter p : l) {
			if (usedParams.contains(p)) {
				continue;
			}

			ILinkedParameter lp = p.getRequestecParameters().get(0);

			for (VanillaGroupParameter g : map.keySet()) {
				boolean providerFound = false;
				boolean providedFound = false;
				for (Parameter up : map.get(g)) {
					if (lp.getProviderParameterId() == up.getId()) {
						providerFound = true;
					}
					if (lp.getProvidedParameterId() == up.getId()) {
						providedFound = true;
					}
				}

				if (providedFound == false) {
					for (Parameter pp : l) {
						if (pp.getId() == lp.getProvidedParameterId()) {
							map.get(g).add(pp);
							if (!usedParams.contains(pp)) {
								usedParams.add(pp);
							}
						}
					}
				}
				if (providerFound == false) {
					for (Parameter pp : l) {
						if (pp.getId() == lp.getProviderParameterId()) {
							map.get(g).add(pp);
							if (!usedParams.contains(pp)) {
								usedParams.add(pp);
							}
						}
					}
				}
			}

		}

		// XXX
		// should reorder groupLists
		return map;

	}

	public List<VanillaGroupParameter> getParameters(RepositoryItem repItem, List<Parameter> repositoryItemParameters, IRepositoryContext repCtx, IRuntimeConfig config) throws Exception {
		/*
		 * find Model Parameters
		 */

		List<VanillaGroupParameter> results = new ArrayList<VanillaGroupParameter>();
		HashMap<VanillaGroupParameter, List<Parameter>> groups;
		if(repItem.getType() == IRepositoryApi.R_MARKDOWN_TYPE){
			groups = getGroupsMarkdown(repositoryItemParameters);
		} else {
			groups = getGroups(repositoryItemParameters);
		}

		for (VanillaGroupParameter g : groups.keySet()) {

			for (Parameter p : groups.get(g)) {
				g.addParameter(createVanillaParameter(p));
			}
			results.add(g);

		}

		// get Values
		for (VanillaGroupParameter g : groups.keySet()) {

			try {
				setParametersValues(g.getParameters().get(0), groups.get(g).get(0), repCtx, config);
			} catch (Exception ex) {
				Logger.getLogger(getClass()).error("Unable to gather Datas for parameter " + g.getParameters().get(0).getName() + " - " + ex.getMessage(), ex);
				g.getParameters().get(0).setControlType(VanillaParameter.TEXT_BOX);
			}

		}

		return results;
	}

	public VanillaParameter refreshDatas(IRepositoryContext repCtx, IRuntimeConfig config, List<Parameter> repositoryItemParameters, String paramNameToRefresh) throws Exception {
		// /*
		// * find Model Parameters
		// */
		// VanillaConfiguration vanillaConfig =
		// ConfigurationManager.getInstance().getVanillaConfiguration();
		// Repository repository =
		// repositoryManager.getRepositoryById(config.getObjectIdentifier().getRepositoryId());
		// AxisRepositoryConnection repSock =
		// (AxisRepositoryConnection)FactoryRepository.getInstance().getConnection(FactoryRepository.AXIS_CLIENT,
		// repository.getUrl(),
		// vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN),
		// vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD),
		// "",
		// config.getVanillaGroupId());

		VanillaGroupParameter group = null;
		VanillaParameter target = null;

		Parameter repParam = null;

		for (VanillaGroupParameter p : config.getParametersValues()) {
			for (VanillaParameter _vp : ((VanillaGroupParameter) p).getParameters()) {
				if (_vp.getName().equals(paramNameToRefresh)) {
					group = (VanillaGroupParameter) p;
					;
					target = _vp;

					for (Parameter rp : repositoryItemParameters) {
						if (_vp.getVanillaParameterId() == null) {
							throw new Exception("VanillaParameter " + _vp.getName() + " is not map with an ItemParameter, it should come from a gone ReportingComponent");
						}
						if (rp.getId() == _vp.getVanillaParameterId()) {
							repParam = rp;
							break;
						}
					}

					break;
				}
			}
		}

		if (group != null) {
			for (VanillaParameter p : group.getParameters()) {

				for (VanillaGroupParameter _p : config.getParametersValues()) {
					for (VanillaParameter _vp : ((VanillaGroupParameter) _p).getParameters()) {
						if (p.getName().equals(_vp.getName())) {
							p.setSelectedValues(((VanillaParameter) _vp).getSelectedValues());
							Logger.getLogger(getClass()).debug("Set Param " + p.getName() + " with : " + p.getSelectedValues());
							break;
						}
					}
				}

			}
		}

		setParametersValues(target, repParam, repCtx, config);

		return target;
	}

	private void setParametersValues(VanillaParameter vanillaParameter, Parameter parameter, IRepositoryContext repCtx, IRuntimeConfig config) throws Exception {

		RepositoryItem item = null;
		IRepositoryApi repApi = new RemoteRepositoryApi(repCtx);

		try {
			item = repApi.getRepositoryService().getDirectoryItem(parameter.getDirectoryItemId());
		} catch (Exception ex) {
			throw new Exception("Unable to load RepositoryItem from repository for parameter " + parameter.getId() + "-" + ex.getMessage(), ex);
		}
		if (parameter.getDataProviderId() <= 0 || item.getType() == IRepositoryApi.R_MARKDOWN_TYPE) {
			
			
			if(item != null && item.getType() == 28){ // markdown
				IVanillaAPI rootVanillaApi = new RemoteVanillaPlatform(repCtx.getVanillaContext());
				RemoteAdminManager admin = new RemoteAdminManager(rootVanillaApi.getVanillaUrl(), null, Locale.getDefault());
				User u = rootVanillaApi.getVanillaSecurityManager().authentify("", rootVanillaApi.getVanillaContext().getLogin(), rootVanillaApi.getVanillaContext().getPassword(), false); //$NON-NLS-1$
				String session = admin.connect(u);
				
				RemoteWorkflowManager smartManager = new RemoteWorkflowManager(rootVanillaApi.getVanillaUrl(), session, Locale.getDefault());
				
				List<bpm.vanilla.platform.core.beans.resources.Parameter> params = (List<bpm.vanilla.platform.core.beans.resources.Parameter>) smartManager.getResources(TypeResource.PARAMETER);
				for(bpm.vanilla.platform.core.beans.resources.Parameter param : params){
					if(param.getName().equals(parameter.getName())){
						if(param.getParameterType().equals(TypeParameter.SIMPLE) || param.getParameterType().equals(TypeParameter.RANGE)){
							vanillaParameter.setControlType(VanillaParameter.TEXT_BOX);
						} else if(param.getParameterType().equals(TypeParameter.LOV)){
							vanillaParameter.setControlType(VanillaParameter.LIST_BOX);
							
							List<ListOfValues> lovs = (List<ListOfValues>) smartManager.getResources(TypeResource.LOV);
							for(ListOfValues lov: lovs){
								vanillaParameter.addValue(lov.getName(), lov.getId()+"");
							}
						} else if(param.getParameterType().equals(TypeParameter.SELECTION)){
							vanillaParameter.setControlType(VanillaParameter.LIST_BOX);
							if(param.getListOfValues() != null){
								for(String elem: param.getListOfValues().getValues()){
									vanillaParameter.addValue(elem, elem);
									if(elem.equals(parameter.getDefaultValue())){
										vanillaParameter.addSelectedValue(elem);
									}
								}
							} else { //from dataset
								if(param.getParentParam() == null){
									for(bpm.vanilla.platform.core.beans.resources.Parameter parent : params){
										if(parent.getId() == param.getIdParentParam()){
											param.setParentParam(parent);
											for(VanillaGroupParameter vgp : config.getParametersValues()){
												for(VanillaParameter p : vgp.getParameters()){
													if(p.getVanillaParameterId() == parameter.getDataProviderId()){
														if(!p.getSelectedValues().isEmpty()){
															parent.setSelectionValue(p.getSelectedValues().get(0));
														}
													}
												}
											}
											
											break;
										}
									}
									
								}
								List<String> values = loadDatasetValues(param, repCtx);
								vanillaParameter.getValues().clear();
								for(String elem: values){
									
									vanillaParameter.addValue(elem, elem);
									if(elem.equals(parameter.getDefaultValue())){
										vanillaParameter.addSelectedValue(elem);
									}
								}		
							}
							
						}
						break;
					}
				}
				
				
			} else {
				vanillaParameter.setControlType(VanillaParameter.TEXT_BOX);
			}
			
			
			return;
		}
		DatasProvider datasProvider = null;

		try {
			//IRepositoryApi repApi = new RemoteRepositoryApi(repCtx);

			datasProvider = repApi.getDatasProviderService().getDatasProvider(parameter.getDataProviderId());
		} catch (Exception ex) {
			throw new Exception("Unable to load DataProvider from repository for parameter " + parameter.getId() + "-" + ex.getMessage(), ex);
		}

		OdaInput odaInput = null;
		Group vanillaGroup = null;
		try {
			vanillaGroup = securityManager.getGroupById(config.getVanillaGroupId());
			OdaInputDigester dig = new OdaInputDigester(datasProvider.getXmlDataSourceDefinition());
			odaInput = dig.getOdaInput();
			// we override some hardcoded datas from the current context
			odaInput.overrideVanillaProperties(repCtx.getRepository().getUrl(), vanillaGroup.getName(), repCtx.getVanillaContext().getLogin(), repCtx.getVanillaContext().getPassword());
		} catch (Exception ex) {
			throw new Exception("Unable to convert DataProvider into OdaInput - " + ex.getMessage(), ex);
		}

		// TODO : use override fucking odaInputXml
		IQuery query = null;
		try {
			query = QueryHelper.buildquery(odaInput);
			query.setAppContext(vanillaGroup);
			int parameterNumber = query.getParameterMetaData().getParameterCount();

			// odaInput.getQueryText()
			if (parameterNumber == 0) {
				IResultSet rs = null;
				try {
					rs = query.executeQuery();
					while (rs.next()) {
						vanillaParameter.addValue(rs.getString(parameter.getLabelColumnIndex()), rs.getString(parameter.getValueColumnIndex()));
					}
				} catch (Exception ex) {
					Logger.getLogger(getClass()).error("Failed to execute IQuery - " + ex.getMessage(), ex);
					vanillaParameter.setControlType(VanillaParameter.TEXT_BOX);
				} finally {
					if (rs != null) {
						try {
							rs.close();
						} catch (Exception ex) {
							Logger.getLogger(getClass()).warn(ex.getMessage(), ex);
						}
					}
				}

			}
			else if (parameterNumber == 1) {
				ILinkedParameter lp = parameter.getRequestecParameters().get(0);
				IParameterMetaData pmd = query.getParameterMetaData();
				for (VanillaGroupParameter p : config.getParametersValues()) {
					for (VanillaParameter vp : ((VanillaGroupParameter) p).getParameters()) {
						if (vp.getVanillaParameterId() == lp.getProviderParameterId()) {
							for (int i = 1; i <= pmd.getParameterCount(); i++) {
								if (i <= vp.getSelectedValues().size()) {
									query.setString(i, vp.getSelectedValues().get(i - 1));
									Logger.getLogger(getClass()).debug("parameter " + i + "=" + vp.getSelectedValues().get(i - 1) + " for " + query.getEffectiveQueryText());
								}
								else {
									query.setNull(i);
									Logger.getLogger(getClass()).debug("parameter " + i + "=NULL  for " + query.getEffectiveQueryText());
								}
							}
						}
					}
				}

				IResultSet rs = null;
				try {
					rs = query.executeQuery();
					while (rs.next()) {
						vanillaParameter.addValue(rs.getString(parameter.getLabelColumnIndex()), rs.getString(parameter.getValueColumnIndex()));
					}
				} catch (Exception ex) {
					Logger.getLogger(getClass()).error("Failed to execute IQuery - " + ex.getMessage(), ex);
					vanillaParameter.setControlType(VanillaParameter.TEXT_BOX);
				} finally {
					if (rs != null) {
						try {
							rs.close();
						} catch (Exception ex) {
							Logger.getLogger(getClass()).warn(ex.getMessage(), ex);
						}
					}
				}

				// does nothing
			}
			else {
				throw new Exception("Vanilla does not support Parameter with more than one parameter in its DataProvider");
			}

		} catch (Exception ex) {
			Logger.getLogger(getClass()).error(ex.getMessage(), ex);
			throw ex;
		} finally {
			if (query != null) {
				try {
					query.close();
				} catch (Exception ex) {
					Logger.getLogger(getClass()).warn(ex.getMessage(), ex);
				}
				QueryHelper.removeQuery(query);
			}
		}

	}

	private VanillaParameter createVanillaParameter(Parameter p) {

		VanillaParameter result = new VanillaParameter();
		result.setControlType(VanillaParameter.TEXT_BOX);
		result.setDataType(VanillaParameter.TYPE_ANY);
		result.setName(p.getName());
		result.setPromptText(p.getName());
		result.setDisplayName(p.getName());
		result.setVanillaParameterId(p.getId());
		result.setDefaultValue(p.getDefaultValue());

		if (p.getDataProviderId() > 0) {
			result.setControlType(VanillaParameter.LIST_BOX);
		}

		if (p.isAllowMultipleValues()) {
			result.setParamType(VanillaParameter.PARAM_TYPE_MULTI);
		}
		else {
			result.setParamType(VanillaParameter.PARAM_TYPE_SIMPLE);
		}

		return result;
	}

	@Override
	public String getComponentName() {
		return getClass().getName();
	}

	@Override
	public List<VanillaGroupParameter> getParameters(IRuntimeConfig config) throws Exception {
		// only implemented for OSGI
		return null;
	}

	@Override
	public VanillaParameter getReportParameterValues(IRuntimeConfig runtimeConfig, String parameterName) throws Exception {
		// only implemented for OSGI
		return null;
	}

	@Override
	public List<VanillaGroupParameter> getParameters(IRuntimeConfig config, InputStream model) throws Exception {
		// only implemented for OSGI
		return null;
	}

	@Override
	public VanillaParameter getReportParameterValues(IRuntimeConfig runtimeConfig, String parameterName, InputStream model) throws Exception {
		// only implemented for OSGI
		return null;
	}
	
	/**
	 * regroup parameters
	 * 
	 * @param l
	 * @return
	 */
	private HashMap<VanillaGroupParameter, List<Parameter>> getGroupsMarkdown(List<Parameter> l) {
		

		HashMap<VanillaGroupParameter, List<Parameter>> map = new LinkedHashMap<VanillaGroupParameter, List<Parameter>>();
		for (Parameter p : l) {
			if (p.getDataProviderId() == 0) {
				/*
				 * create Group
				 */
				VanillaGroupParameter group = new VanillaGroupParameter();
				group.setDisplayName("Group " + p.getName());
				group.setName("Group " + p.getName());
				group.setPromptText("");

				map.put(group, new ArrayList<Parameter>());
				map.get(group).add(p);
				
			} else {
				for (VanillaGroupParameter g : map.keySet()) {
					if(map.get(g).get(0).getId() == p.getDataProviderId()){
						map.get(g).add(p);
						g.setCascadingGroup(true);
					}
				}
			}
		}

		
		return map;

	}
	
	private List<String> loadDatasetValues(bpm.vanilla.platform.core.beans.resources.Parameter parameter, IRepositoryContext repCtx) throws Exception{

		
		String script = "";
		if(parameter.getIdParentParam() == 0){
			script = "manual_result<-" + parameter.getDataset() + "$" + parameter.getColumn();
		} else {
			script = "library(dplyr)\n";
			script += "manual_result <- filter(" + parameter.getDataset() + ", " + parameter.getRequest() + ")"+ "$" + parameter.getColumn();
		}
		

		RScriptModel model = new RScriptModel();
		model.setScript(script);
		model.setOutputs(new String[] { "manual_result" });
		
		IVanillaAPI rootVanillaApi = new RemoteVanillaPlatform(repCtx.getVanillaContext());
		RemoteAdminManager admin = new RemoteAdminManager(rootVanillaApi.getVanillaUrl(), null, Locale.getDefault());
		User u = rootVanillaApi.getVanillaSecurityManager().authentify("", rootVanillaApi.getVanillaContext().getLogin(), rootVanillaApi.getVanillaContext().getPassword(), false); //$NON-NLS-1$
		String session = admin.connect(u);
		
		RemoteSmartManager smartManager = new RemoteSmartManager(rootVanillaApi.getVanillaUrl(), session, Locale.getDefault());
		
		model = smartManager.executeScriptR(model, null);
		String values = model.getOutputVarstoString().get(0);
		List<String> distincts = Arrays.asList(values.trim().split("\\t"));

		HashSet<String> temp = new HashSet<String>(distincts.subList(0, distincts.size()-1));
		distincts = new ArrayList<String>(temp);
		Collections.sort(distincts);

		return distincts;
	}
	
}
