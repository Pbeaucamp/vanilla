package bpm.vanilla.server.reporting.server.viewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.DynamicPrompt;
import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.fwr.api.beans.dataset.GroupPrompt;
import bpm.fwr.api.beans.dataset.IResource;
import bpm.metadata.MetaDataBuilder;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.server.commons.pool.PoolableModel;
import bpm.vanilla.server.commons.server.Server;
import bpm.vanilla.server.commons.server.commands.ServerCommand;
import bpm.vanilla.server.reporting.pool.FwrPoolableModel;

public class VanillaViewerFWRCommand extends ServerCommand {

	private static final long serialVersionUID = -1689219186651784L;
	private Logger logger = Logger.getLogger(VanillaViewerFWRCommand.class);

	private IReportRuntimeConfig reportConfig;
	private User user;

	public VanillaViewerFWRCommand(Server server, IReportRuntimeConfig reportConfig, User user) {
		super(server);
		this.reportConfig = reportConfig;
		this.user = user;
	}

	public VanillaParameter getParameterValuesWithCascading(String paramName, List<VanillaParameter> parameters) throws Exception {
		logger.info("Start Getting Parameter For Cascading");

		logger.info("Start Getting Parameters");
		PoolableModel<?> poolableModel;
		try {
			poolableModel = getPoolableModel();
		} catch (Throwable t) {
			logger.error("Failed to obtain poolable model : " + t.getMessage(), t);
			throw new Exception(t);
		}
		logger.info("Got poolable model");

		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(ConfigurationManager.getInstance().getVanillaConfiguration());
		Group group = vanillaApi.getVanillaSecurityManager().getGroupById(reportConfig.getVanillaGroupId());
		reportConfig.getObjectIdentifier().getRepositoryId();

		if (poolableModel instanceof FwrPoolableModel) {
			FwrPoolableModel fwrModel = (FwrPoolableModel) poolableModel;
			FWRReport report = fwrModel.getModel();
			
			List<IResource> resources = report.getPrompts();
			for (IResource resource : resources) {
				if (resource instanceof GroupPrompt) {
					
					GroupPrompt grpPrompt = (GroupPrompt) resource;
					for(FwrPrompt prt : grpPrompt.getCascadingPrompts()) {
						if(prt.getName().equals(paramName)) {
							
							try {
								HashMap<String, String> parentValues = new HashMap<String, String>();
								getParentValues(parentValues, grpPrompt.getCascadingPrompts(), parameters, prt, true);
								

								int metadataId = prt.getDataset().getDatasource().getItemId();
								String fmdtmodel = prt.getDataset().getDatasource().getBusinessModel();
								String fmdtpack = prt.getDataset().getDatasource().getBusinessPackage();
								
								Collection<IBusinessModel> models = ((FwrPoolableModel) poolableModel).loadFMDTModel(metadataId, group.getName(), false);
								IBusinessModel bmodel = null;

								for (IBusinessModel m : models) {
									if (fmdtmodel.equalsIgnoreCase(m.getName())) {
										bmodel = m;
										break;
									}
								}

								if (bmodel == null)
									throw new Exception("Model: " + fmdtmodel + " doesn't exist in fmdt: " + metadataId);

								IBusinessPackage bpack = bmodel.getBusinessPackage(fmdtpack, group.getName());
								
								if (bpack == null)
									throw new Exception("Package: " + fmdtpack + " doesn't exist in model: " + fmdtmodel);

								Prompt p = (Prompt) bpack.getResourceByName(group.getName(), prt.getName());

								for(VanillaParameter param : parameters) {
									if(param.getName().equals(paramName)) {

										if(param.getValues() != null) {
											param.getValues().clear();
										}
										
										List<String> values = p.getOrigin().getDistinctValues(parentValues);
										if(values != null) {
											for(String value : values) {
												param.addValue(value, value);
											}
										}
										
										return param;
									}
								}

							} catch (Exception e) {
								e.printStackTrace();
								return null;
							}
						}
					}
				}
			}


			return null;
		}
		else {
			String errMsg = poolableModel.getClass() + " is not recognised, aborting";
			logger.error(errMsg);
			throw new Exception(errMsg);
		}
	}
	
	private void getParentValues(HashMap<String, String> parentValues, List<FwrPrompt> prompts, List<VanillaParameter> parameters, FwrPrompt prompt, boolean lastChild) {
		if(prompt.isChildPrompt()) {
			for(FwrPrompt prt : prompts) {
				if(prt.getName().equals(prompt.getParentPromptName())) {
					getParentValues(parentValues, prompts, parameters, prt, false);
					break;
				}
			}
			
			if(!lastChild) {
				String value = "";
				for(VanillaParameter param : parameters) {
					if(prompt.getName().equals(param.getName())) {
						value = param.getSelectedValues() != null ? param.getSelectedValues().get(0) != null ? param.getSelectedValues().get(0) : "" : "";
					}
				}
				
				parentValues.put(prompt.getOriginDataStreamName(), value);
			}
		}
		else {
			String value = "";
			for(VanillaParameter param : parameters) {
				if(prompt.getName().equals(param.getName())) {
					value = param.getSelectedValues() != null ? param.getSelectedValues().get(0) != null ? param.getSelectedValues().get(0) : "" : "";
				}
			}
			
			parentValues.put(prompt.getOriginDataStreamName(), value);
		}
	}

	public List<VanillaGroupParameter> getReportParameters() throws Exception {
		logger.info("Start Getting Parameters");
		PoolableModel<?> poolableModel;
		try {
			poolableModel = getPoolableModel();
		} catch (Throwable t) {
			logger.error("Failed to obtain poolable model : " + t.getMessage(), t);
			throw new Exception(t);
		}
		logger.info("Got poolable model");

		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(ConfigurationManager.getInstance().getVanillaConfiguration());
		Group group = vanillaApi.getVanillaSecurityManager().getGroupById(reportConfig.getVanillaGroupId());

		List<VanillaParameter> birtParams = new ArrayList<VanillaParameter>();
		List<VanillaGroupParameter> birtGroupParams = new ArrayList<VanillaGroupParameter>();

		if (poolableModel instanceof FwrPoolableModel) {
			FwrPoolableModel fwrModel = (FwrPoolableModel) poolableModel;

			FWRReport report = fwrModel.getModel();

			List<IResource> resources = report.getPrompts() != null ? report.getPrompts() : new ArrayList<IResource>();
			for (IResource resource : resources) {
				if (resource instanceof GroupPrompt) {
					
					GroupPrompt grpPrompt = (GroupPrompt) resource;

					VanillaGroupParameter groupParam = new VanillaGroupParameter();
					groupParam.setName(grpPrompt.getName() != null ? grpPrompt.getName() : "");
					groupParam.setCascadingGroup(true);
					
					List<VanillaParameter> params = new ArrayList<VanillaParameter>();
					for(FwrPrompt prompt : grpPrompt.getCascadingPrompts()) {
						DataSet ds = prompt.getDataset();

						logger.debug("Checking dataset " + ds.getName() + " for parameter " + prompt.getName());
						
						int metadataId = ds.getDatasource().getItemId();
						String fmdtmodel = ds.getDatasource().getBusinessModel();
						String fmdtpack = ds.getDatasource().getBusinessPackage();
						
						logger.info("Loading fmdt with id= " + metadataId + " with group " + group.getName());
						Collection<IBusinessModel> models = ((FwrPoolableModel) poolableModel).loadFMDTModel(metadataId, group.getName());
						IBusinessModel bmodel = null;

						for (IBusinessModel m : models) {
							if (fmdtmodel.equalsIgnoreCase(m.getName())) {
								bmodel = m;
								break;
							}
						}

						if (bmodel == null)
							throw new Exception("Model: " + fmdtmodel + " doesn't exist in fmdt: " + metadataId);

						IBusinessPackage bpack = bmodel.getBusinessPackage(fmdtpack, group.getName());

						if (bpack == null)
							throw new Exception("Package: " + fmdtpack + " doesn't exist in model: " + fmdtmodel);

						logger.info("Found question = " + prompt.getQuestion());
						logger.info("Found name = " + prompt.getName());
						Prompt _p = (Prompt) bpack.getResourceByName(group.getName(), prompt.getName());

						VanillaParameter tempParam = new VanillaParameter();
						tempParam.setName(_p.getName());
						tempParam.setPromptText(_p.getQuestion() == null || _p.getQuestion().equals("null") ? null : _p.getQuestion());
						tempParam.setDisplayName(_p.getQuestion() == null || _p.getQuestion().equals("null") ? null : _p.getQuestion());
						tempParam.setDefaultValue("");

						tempParam.setDataType(1);
						tempParam.setControlType(prompt.getType());
						tempParam.setParamType(prompt.getParamType());

						if (_p.getOrigin().getOrigin() == null) {
							Logger.getLogger(getClass()).warn("Seems that FMDT model has not been plugged to database");

							MetaDataBuilder b = new MetaDataBuilder(null);
							b.build(bmodel.getModel(), new RemoteRepositoryApi(getPoolableModel().getItemKey().getRepositoryContext()), group.getName());
						}

						for (String val : _p.getOrigin().getDistinctValues()) {
							logger.info("Found value " + val);
							tempParam.addValue(val, val);
						}

						params.add(tempParam);
					}
					groupParam.setParameters(params);
					
					birtGroupParams.add(groupParam);
				}
				else if (resource instanceof DynamicPrompt) {
					DynamicPrompt dynPrompt = (DynamicPrompt) resource;
					
					DataSet ds = dynPrompt.getDataset();

					logger.debug("Checking dataset " + ds.getName() + " for parameter " + dynPrompt.getName());
					
					int metadataId = ds.getDatasource().getItemId();
					String fmdtmodel = ds.getDatasource().getBusinessModel();
					String fmdtpack = ds.getDatasource().getBusinessPackage();
					
					logger.info("Loading fmdt with id= " + metadataId + " with group " + group.getName());
					Collection<IBusinessModel> models = ((FwrPoolableModel) poolableModel).loadFMDTModel(metadataId, group.getName(), false);
					IBusinessModel bmodel = null;

					for (IBusinessModel m : models) {
						if (fmdtmodel.equalsIgnoreCase(m.getName())) {
							bmodel = m;
							break;
						}
					}

					if (bmodel == null)
						throw new Exception("Model: " + fmdtmodel + " doesn't exist in fmdt: " + metadataId);

					IBusinessPackage bpack = bmodel.getBusinessPackage(fmdtpack, group.getName());

					if (bpack == null)
						throw new Exception("Package: " + fmdtpack + " doesn't exist in model: " + fmdtmodel);

					IDataStreamElement selectedColumn = getSelectedColumns(bpack, dynPrompt.getColumn().getBusinessTableParent(), dynPrompt.getColumn().getName(), ds.getDatasource().getGroup());

					logger.info("Found question = " + dynPrompt.getQuestion());
					logger.info("Found name = " + dynPrompt.getName());
					
					VanillaParameter tempParam = new VanillaParameter();
					tempParam.setName(dynPrompt.getName());
					tempParam.setPromptText(dynPrompt.getQuestion() == null || dynPrompt.getQuestion().equals("null") ? null : dynPrompt.getQuestion());
					tempParam.setDisplayName(dynPrompt.getQuestion() == null || dynPrompt.getQuestion().equals("null") ? null : dynPrompt.getQuestion());
					tempParam.setDefaultValue("");

					tempParam.setDataType(1);
					tempParam.setControlType(dynPrompt.getType());
					tempParam.setParamType(dynPrompt.getParamType());

					for (String val : selectedColumn.getDistinctValues()) {
						logger.info("Found value " + val);
						tempParam.addValue(val, val);
					}

					birtParams.add(tempParam);
				}
				else if (resource instanceof FwrPrompt) {
					FwrPrompt prompt = (FwrPrompt) resource;
					
					DataSet ds = prompt.getDataset();

					logger.debug("Checking dataset " + ds.getName() + " for parameter " + prompt.getName());
					
					int metadataId = ds.getDatasource().getItemId();
					String fmdtmodel = ds.getDatasource().getBusinessModel();
					String fmdtpack = ds.getDatasource().getBusinessPackage();
					
					logger.info("Loading fmdt with id= " + metadataId + " with group " + group.getName());
					Collection<IBusinessModel> models = ((FwrPoolableModel) poolableModel).loadFMDTModel(metadataId, group.getName());
					IBusinessModel bmodel = null;

					for (IBusinessModel m : models) {
						if (fmdtmodel.equalsIgnoreCase(m.getName())) {
							bmodel = m;
							break;
						}
					}

					if (bmodel == null)
						throw new Exception("Model: " + fmdtmodel + " doesn't exist in fmdt: " + metadataId);

					IBusinessPackage bpack = bmodel.getBusinessPackage(fmdtpack, group.getName());

					if (bpack == null)
						throw new Exception("Package: " + fmdtpack + " doesn't exist in model: " + fmdtmodel);

					logger.info("Found question = " + prompt.getQuestion());
					logger.info("Found name = " + prompt.getName());
					Prompt _p = (Prompt) bpack.getResourceByName(group.getName(), prompt.getName());

					VanillaParameter tempParam = new VanillaParameter();
					tempParam.setName(_p.getName());
					tempParam.setPromptText(_p.getQuestion() == null || _p.getQuestion().equals("null") ? null : _p.getQuestion());
					tempParam.setDisplayName(_p.getQuestion() == null || _p.getQuestion().equals("null") ? null : _p.getQuestion());
					tempParam.setDefaultValue("");

					tempParam.setDataType(1);
					tempParam.setControlType(prompt.getType());
					tempParam.setParamType(prompt.getParamType());

					if (_p.getOrigin().getOrigin() == null) {
						Logger.getLogger(getClass()).warn("Seems that FMDT model has not been plugged to database");

						MetaDataBuilder b = new MetaDataBuilder(null);
						b.build(bmodel.getModel(), new RemoteRepositoryApi(getPoolableModel().getItemKey().getRepositoryContext()), group.getName());
					}

					for (String val : _p.getOrigin().getDistinctValues()) {
						logger.info("Found value " + val);
						tempParam.addValue(val, val);
					}

					birtParams.add(tempParam);
				}
			}
		}
		else {
			String errMsg = poolableModel.getClass() + " is not recognised, aborting";
			logger.error(errMsg);
			throw new Exception(errMsg);
		}

		if (birtParams != null && !birtParams.isEmpty()) {
			VanillaGroupParameter noGroupParam = new VanillaGroupParameter();
			noGroupParam.setCascadingGroup(false);
			noGroupParam.setName("");
			noGroupParam.setParameters(birtParams);
			birtGroupParams.add(noGroupParam);
		}

		logger.info("End of Parameter Recuperation");
		return birtGroupParams;
	}

	private static IDataStreamElement getSelectedColumns(IBusinessPackage bpackage, String selectedTableName, String selectedColumnName, String group) throws Exception {
		Collection<IBusinessTable> tables = bpackage.getBusinessTables(group);
		for (IBusinessTable btable : tables) {
			if (btable.getName().equals(selectedTableName)) {
				Collection<IDataStreamElement> columns = btable.getColumns(group);
				for (IDataStreamElement element : columns) {
					if (element.getName().equals(selectedColumnName)) {
						return element;
					}
				}
			}
		}
		return null;
	}

	protected PoolableModel<?> getPoolableModel() throws Exception {
		/*
		 * create the IRepositoryContext
		 */
		BaseVanillaContext vCtx = new BaseVanillaContext(getServer().getConfig().getVanillaUrl(), user.getLogin(), user.getPassword());
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vCtx);

		Repository repository = vanillaApi.getVanillaRepositoryManager().getRepositoryById(reportConfig.getObjectIdentifier().getRepositoryId());
		Group group = vanillaApi.getVanillaSecurityManager().getGroupById(reportConfig.getVanillaGroupId());

		BaseRepositoryContext repCtx = new BaseRepositoryContext(vCtx, group, repository);

		PoolableModel<?> poolModel = null;
		Object model = null;

		try {
			// every borrow MUST be released as soon as possible whatever can
			// happen
			poolModel = getServer().getPool().borrow(repCtx, reportConfig.getObjectIdentifier().getDirectoryItemId());
			model = poolModel.getModel();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (poolModel != null) {
				try {
					// every borrow MUST be released as soon as possible
					// whatever can happen
					getServer().getPool().returnObject(repCtx, reportConfig.getObjectIdentifier().getDirectoryItemId(), poolModel);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (model == null) {
			throw new Exception("error when interpreting message");
		}

		return poolModel;
	}

}
