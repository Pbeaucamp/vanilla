package bpm.vanilla.server.reporting.server.viewer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IParameterDefn;
import org.eclipse.birt.report.engine.api.IParameterDefnBase;
import org.eclipse.birt.report.engine.api.IParameterGroupDefn;
import org.eclipse.birt.report.engine.api.IParameterSelectionChoice;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;
import org.eclipse.birt.report.engine.api.impl.CascadingParameterGroupDefn;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationException;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.server.commons.pool.PoolableModel;
import bpm.vanilla.server.commons.server.Server;
import bpm.vanilla.server.commons.server.commands.ServerCommand;
import bpm.vanilla.server.reporting.pool.BirtPoolableModel;

public class VanillaViewerBirtCommand extends ServerCommand {

	private static final long serialVersionUID = -5596832234625466490L;
	private Logger logger = Logger.getLogger(VanillaViewerBirtCommand.class);

	private IReportRuntimeConfig runtimeConfig;
	private User user;
	private IReportEngine engine;

	public VanillaViewerBirtCommand(Server server, IReportRuntimeConfig runtimeConfig, User user) throws ConfigurationException {
		super(server);
		this.runtimeConfig = runtimeConfig;
		this.user = user;
	}

	public VanillaParameter getParameterValuesWithCascading(String paramName, List<VanillaParameter> parameters) throws Exception {
		logger.info("Start Getting Parameter For Cascading");
		// We create a new BirtParameter which is going to contains the values
		VanillaParameter parameter = new VanillaParameter();

		PoolableModel<?> poolableModel = getPoolableModel();

		if (poolableModel instanceof BirtPoolableModel) {
			BirtPoolableModel birtModel = (BirtPoolableModel) poolableModel;
			engine = birtModel.getBirtEngine();

			// Open a report design
			IReportRunnable design = birtModel.getModel();
			IGetParameterDefinitionTask task = engine.createGetParameterDefinitionTask(design);

			Collection params = task.getParameterDefns(true);
			Iterator iter = params.iterator();
			// Iterate over all parameters
			while (iter.hasNext()) {
				IParameterDefnBase param = (IParameterDefnBase) iter.next();
				// Group section found
				if (param instanceof IParameterGroupDefn) {
					// Get Group Name
					IParameterGroupDefn group = (IParameterGroupDefn) param;

					// Get the parameters within a group
					Iterator i2 = group.getContents().iterator();
					while (i2.hasNext()) {
						IScalarParameterDefn scalar = (IScalarParameterDefn) i2.next();
						logger.debug("Looking for param " + paramName + " got " + scalar.getName());
						if (scalar.getName().equals(paramName)) {
							logger.debug("Found matching parameter with name " + paramName);
							parameter.setName(scalar.getName());
							parameter.setControlType(scalar.getControlType());
							parameter.setDataType(scalar.getDataType());
							parameter.setHidden(scalar.isHidden());
							parameter.setParamType(scalar.getScalarParameterType());
							parameter.setRequired(scalar.isRequired());
							parameter.setAllowNewValues(scalar.allowNewValues());
							
							VanillaParameter paramParent = null;

							List<Object> values = new ArrayList<Object>();

							/*
							 * this is to build the birt param request, it s
							 * expecting : {} to get Countries {"US"} to get
							 * states from US {"US", "CA"} to get cites from Ca,
							 * US.
							 */

							for (VanillaParameter paramTemp : parameters) {
								if (paramTemp.getName().equals(paramName)) {
									logger.debug("Now parameter match in provided params, breaking");
									break;
								}
								// XXX ere taken out while testing
								// if (paramTemp.getSelectedValue() != null){
								// values.add(paramTemp.getSelectedValue());
								// }
								// if(!paramTemp.getName().equals(paramName)){
								if (paramTemp.getSelectedValues() != null) {
									logger.debug("paramTemp selected values size " + paramTemp.getSelectedValues().size());
									if (!paramTemp.getSelectedValues().isEmpty()) {
										for (String selVal : paramTemp.getSelectedValues()) {
											logger.debug("Found selected values : " + selVal);
										}
										values.add(paramTemp.getSelectedValues().get(0));
									}
									else {
										logger.debug("No values to set");
									}
								}
								
								paramParent = paramTemp;
							}
							
							getValueForCascadingParam(group, paramParent, parameter, scalar, task, values.toArray(new Object[values.size()]));
						}
					}
				}
			}

			task.close();
		}
		else {
			String errMsg = poolableModel.getClass() + " is not recognised, aborting";
			logger.error(errMsg);
			throw new Exception(errMsg);
		}

		return parameter;
	}

	public List<VanillaGroupParameter> getReportParameters() throws Exception {
		logger.info("Start Getting Parameters");
		PoolableModel<?> poolableModel = getPoolableModel();
		logger.info("Got poolable model");

		List<VanillaParameter> birtParams = new ArrayList<VanillaParameter>();
		List<VanillaGroupParameter> birtGroupParams = new ArrayList<VanillaGroupParameter>();

		if (poolableModel instanceof BirtPoolableModel) {
			logger.info("Model is a birt poolable");
			BirtPoolableModel birtModel = (BirtPoolableModel) poolableModel;

			engine = birtModel.getBirtEngine();

			// Open a report design
			IReportRunnable design = birtModel.getModel();

			IGetParameterDefinitionTask task = engine.createGetParameterDefinitionTask(design);
			Collection params = task.getParameterDefns(true);

			Iterator iter = params.iterator();
			// Iterate over all parameters
			while (iter.hasNext()) {
				IParameterDefnBase param = (IParameterDefnBase) iter.next();
				// Group section found
				if (param instanceof IParameterGroupDefn) {

					if (param instanceof CascadingParameterGroupDefn) {

						// Get Group Name
						IParameterGroupDefn group = (IParameterGroupDefn) param;

						VanillaGroupParameter groupParamTemp = new VanillaGroupParameter();
						groupParamTemp.setName(group.getName());
						groupParamTemp.setPromptText(group.getPromptText());
						groupParamTemp.setDisplayName(group.getDisplayName());
						groupParamTemp.setCascadingGroup(true);

						// For a group in cascade we only take the first value
						// because the followers depend
						// on each other
						boolean isFirst = true;

						// Get the parameters within a group
						Iterator i2 = group.getContents().iterator();
						while (i2.hasNext()) {
							IScalarParameterDefn scalar = (IScalarParameterDefn) i2.next();
							
							String displayFormat = scalar.getDisplayFormat();

							Object defaultValue = task.getDefaultValue(scalar);
							if (displayFormat != null && !displayFormat.isEmpty() && defaultValue instanceof Date) {
								try {
									defaultValue = new SimpleDateFormat(displayFormat).format(defaultValue);
								} catch (Exception e) { }
							}

							// We create a new Birt Parameter
							VanillaParameter tempParam = new VanillaParameter();
							tempParam.setName(scalar.getName());
							tempParam.setPromptText(scalar.getPromptText());
							tempParam.setDisplayName(scalar.getDisplayName());
							tempParam.setDefaultValue(defaultValue != null ? defaultValue.toString() : "");
							tempParam.setControlType(scalar.getControlType());
							tempParam.setDataType(scalar.getDataType());
							tempParam.setRequired(scalar.isRequired());
							tempParam.setHidden(scalar.isHidden());
							tempParam.setAllowNewValues(scalar.allowNewValues());
							tempParam.setParamType(scalar.getScalarParameterType());

							if (isFirst) {
								getValueForCascadingParam(group, null, tempParam, scalar, task, new Object[] {});
								logger.info("Added a param " + scalar.getName() + " and filled its values");
								isFirst = false;
							}
							else {
								logger.info("Added a param " + scalar.getName() + " and did not fill values");
							}
							groupParamTemp.addParameter(tempParam);
						}

						birtGroupParams.add(groupParamTemp);

						logger.info("Add Group Parameters");
					}
					else {
						// Get Group Name
						IParameterGroupDefn group = (IParameterGroupDefn) param;

						VanillaGroupParameter groupParamTemp = new VanillaGroupParameter();
						groupParamTemp.setName(group.getName());
						groupParamTemp.setPromptText(group.getPromptText());
						groupParamTemp.setDisplayName(group.getDisplayName());
						groupParamTemp.setCascadingGroup(false);

						// Get the parameters within a group
						Iterator i2 = group.getContents().iterator();
						while (i2.hasNext()) {
							IScalarParameterDefn scalar = (IScalarParameterDefn) i2.next();

							String displayFormat = scalar.getDisplayFormat();
							
							Object defaultValue = task.getDefaultValue(scalar);
							if (displayFormat != null && !displayFormat.isEmpty() && defaultValue instanceof Date) {
								try {
									defaultValue = new SimpleDateFormat(displayFormat).format(defaultValue);
								} catch (Exception e) { }
							}

							// We create a new Birt Parameter
							VanillaParameter tempParam = new VanillaParameter();
							tempParam.setName(scalar.getName());
							tempParam.setPromptText(scalar.getPromptText());
							tempParam.setDisplayName(scalar.getDisplayName());
							tempParam.setDefaultValue(defaultValue != null ? defaultValue.toString() : "");
							tempParam.setControlType(scalar.getControlType());
							tempParam.setDataType(scalar.getDataType());
							tempParam.setRequired(scalar.isRequired());
							tempParam.setHidden(scalar.isHidden());
							tempParam.setAllowNewValues(scalar.allowNewValues());
							tempParam.setParamType(scalar.getScalarParameterType());

							getValueForParam(tempParam, scalar, task);
							logger.info("Added a param " + scalar.getName() + " and filled its values");

							groupParamTemp.addParameter(tempParam);
						}

						birtGroupParams.add(groupParamTemp);

						logger.info("Add Group Parameters");
					}
				}
				else {
					// Parameters are not in a group
					IScalarParameterDefn scalar = (IScalarParameterDefn) param;
					String displayFormat = scalar.getDisplayFormat();
					
					Object defaultValue = task.getDefaultValue(scalar);
					if (displayFormat != null && !displayFormat.isEmpty() && defaultValue instanceof Date) {
						try {
							defaultValue = new SimpleDateFormat(displayFormat).format(defaultValue);
						} catch (Exception e) { }
					}
					
					if (displayFormat != null && !displayFormat.isEmpty() && defaultValue instanceof Date) {
						try {
							defaultValue = new SimpleDateFormat(displayFormat).format(defaultValue);
						} catch (Exception e) { }
					}
					
					VanillaParameter birtParameterTemp = new VanillaParameter();
					birtParameterTemp.setName(scalar.getName());
					birtParameterTemp.setDisplayName(scalar.getDisplayName());
					birtParameterTemp.setControlType(scalar.getControlType());
					birtParameterTemp.setDataType(scalar.getDataType());
					birtParameterTemp.setParamType(scalar.getScalarParameterType());
					birtParameterTemp.setDefaultValue(defaultValue != null ? defaultValue.toString() : "");
					birtParameterTemp.setPromptText(scalar.getPromptText());
					birtParameterTemp.setRequired(scalar.isRequired());
					birtParameterTemp.setHidden(scalar.isHidden());
					birtParameterTemp.setAllowNewValues(scalar.allowNewValues());
					getValueForParam(birtParameterTemp, scalar, task);

					birtParams.add(birtParameterTemp);

					logger.info("Add Parameter");
				}
			}

			task.close();
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

	private void getValueForCascadingParam(IParameterGroupDefn group, VanillaParameter parameterParent, VanillaParameter parameter, IScalarParameterDefn scalar, IGetParameterDefinitionTask task, Object[] paramsValues) {
		// Parameter is a List Box
		logger.info("Getting values for cascading param " + scalar.getName());
		if (scalar.getControlType() == IScalarParameterDefn.LIST_BOX) {
			logger.debug("Executing request for group " + group.getName() + " with vals = " + paramsValues.length);
			List<Object> valuesToSet = new ArrayList<Object>();
			for (Object val : paramsValues) {
				logger.debug("Found set val = " + val);
				Object valueToSet;
				if (val instanceof String) {
					try {
						if (parameterParent.getDataType() == IParameterDefn.TYPE_BOOLEAN) {
							valueToSet = Boolean.valueOf(val.toString());
						}
						else if (parameterParent.getDataType() == IParameterDefn.TYPE_DATE) {
							logger.warn("Parameter of type date not supported on cascading parameter. Please choose an other type.");
							valueToSet = val;
						}
						else if (parameterParent.getDataType() == IParameterDefn.TYPE_DATE_TIME) {
							logger.warn("Parameter of type DateTime not supported on cascading parameter. Please choose an other type.");
							valueToSet = val;
						}
						else if (parameterParent.getDataType() == IParameterDefn.TYPE_DECIMAL) {
							valueToSet = Float.valueOf(val.toString());
						}
						else if (parameterParent.getDataType() == IParameterDefn.TYPE_FLOAT) {
							valueToSet = Float.valueOf(val.toString());
						}
						else if (parameterParent.getDataType() == IParameterDefn.TYPE_INTEGER) {
							valueToSet = Integer.valueOf(val.toString());
						}
						else if (parameterParent.getDataType() == IParameterDefn.TYPE_TIME) {
							logger.warn("Parameter of type Time not supported on cascading parameter. Please choose an other type.");
							valueToSet = val;
						}
						else if (parameterParent.getDataType() == IParameterDefn.TYPE_ANY) {
							logger.warn("Parameter of type Any handle like a String.");
							valueToSet = val;
						}
						else {
							valueToSet = val;
						}
					} catch (Exception e) {
						e.printStackTrace();
						logger.warn("Unable to parse the parameter value of type " + parameterParent.getDataType() + ": " + e.getMessage());
						valueToSet = val;
					}
				}
				else {
					valueToSet = val;
				}
				valuesToSet.add(valueToSet);
			}
			Collection selectionList = task.getSelectionListForCascadingGroup(group.getName(), valuesToSet.toArray(new Object[valuesToSet.size()]));
			// Selection contains data
			if (selectionList != null) {
				if (!selectionList.iterator().hasNext()) {
					logger.info("Request result returned an empty list");
				}
				else {
					logger.info("Request result returned data.");
				}
				for (Iterator sliter = selectionList.iterator(); sliter.hasNext();) {
					// Print out the selection choices
					IParameterSelectionChoice selectionItem = (IParameterSelectionChoice) sliter.next();
					if (selectionItem != null && selectionItem.getLabel() != null && selectionItem.getValue() != null) {
						logger.debug("got data : " + selectionItem.getLabel() + "/" + selectionItem.getValue().toString());
						parameter.addValue(selectionItem.getLabel(), selectionItem.getValue().toString());
					}
				}
			}
			else {
				logger.warn("Request result selection is null");
			}
		}
		else {
			logger.warn("ScalarParam " + scalar.getName() + " is not a list box, ignoring");
		}
	}

	private void getValueForParam(VanillaParameter parameter, IScalarParameterDefn scalar, IGetParameterDefinitionTask task) {

		// Parameter is a List Box
		if (scalar.getControlType() == IScalarParameterDefn.LIST_BOX) {
			Collection selectionList = task.getSelectionList(scalar.getName());
			// Selection contains data
			if (selectionList != null) {
				for (Iterator sliter = selectionList.iterator(); sliter.hasNext();) {
					// Print out the selection choices
					IParameterSelectionChoice selectionItem = (IParameterSelectionChoice) sliter.next();
					if (selectionItem != null && selectionItem.getLabel() != null && selectionItem.getValue() != null) {
						parameter.addValue(selectionItem.getLabel(), selectionItem.getValue().toString());
					}
				}
			}
		}
		else if (scalar.getControlType() == IScalarParameterDefn.TEXT_BOX) {
			Collection selectionList = task.getSelectionList(scalar.getName());
			if (selectionList != null) {
				for (Iterator sliter = selectionList.iterator(); sliter.hasNext();) {
					// Print out the selection choices
					IParameterSelectionChoice selectionItem = (IParameterSelectionChoice) sliter.next();
					if (selectionItem != null && selectionItem.getLabel() != null && selectionItem.getValue() != null) {
						parameter.addValue(selectionItem.getLabel(), selectionItem.getValue().toString());
					}
				}
			}
		}
		else if (scalar.getControlType() == IScalarParameterDefn.RADIO_BUTTON) {
			Collection selectionList = task.getSelectionList(scalar.getName());
			if (selectionList != null) {
				for (Iterator sliter = selectionList.iterator(); sliter.hasNext();) {
					// Print out the selection choices
					IParameterSelectionChoice selectionItem = (IParameterSelectionChoice) sliter.next();
					if (selectionItem != null && selectionItem.getLabel() != null && selectionItem.getValue() != null) {
						parameter.addValue(selectionItem.getLabel(), selectionItem.getValue().toString());
					}
				}
			}
		}
	}

	protected PoolableModel<?> getPoolableModel() throws Exception {

		BaseVanillaContext vCtx = new BaseVanillaContext(getServer().getConfig().getVanillaUrl(), user.getLogin(), user.getPassword());
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vCtx);

		Repository repository = vanillaApi.getVanillaRepositoryManager().getRepositoryById(runtimeConfig.getObjectIdentifier().getRepositoryId());
		Group group = vanillaApi.getVanillaSecurityManager().getGroupById(runtimeConfig.getVanillaGroupId());

		BaseRepositoryContext repCtx = new BaseRepositoryContext(vCtx, group, repository);

		PoolableModel<?> poolModel = null;
		Object model = null;

		try {
			// every borrow MUST be released as soon as possible whatever can
			// happen
			poolModel = getServer().getPool().borrow(repCtx, runtimeConfig.getObjectIdentifier().getDirectoryItemId());
			model = poolModel.getModel();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (poolModel != null) {
				try {
					// every borrow MUST be released as soon as possible
					// whatever can happen
					getServer().getPool().returnObject(repCtx, runtimeConfig.getObjectIdentifier().getRepositoryId(), poolModel);
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
