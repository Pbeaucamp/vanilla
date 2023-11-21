package bpm.vanillahub.runtime.run;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Parameter.TypeParameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.dao.ResourceDao;
import bpm.vanillahub.runtime.dao.WorkflowDao;
import bpm.vanillahub.runtime.managers.FileManager;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;

public class LaunchManager {

	private IVanillaLogger logger;
	
	private WorkflowDao workflowManager;
	private FileManager fileManager;
	private ResourceDao resourceManager;

	public LaunchManager(IVanillaLogger logger, WorkflowDao workflowManager, FileManager fileManager, ResourceDao resourceManager) {
		this.logger = logger;
		this.workflowManager = workflowManager;
		this.fileManager = fileManager;
		this.resourceManager = resourceManager;
	}

	public void run(WorkflowProgressManager progressManager, Locale locale, Workflow workflow, String uuid, String launcher, List<Parameter> parameters, List<ListOfValues> lovs) throws Exception {
		List<List<Parameter>> parameterToRuns = new ArrayList<List<Parameter>>();
		if (parameters != null && !parameters.isEmpty()) {
			Integer serverId = null;
			for (Parameter param : parameters) {
				if (param.getParameterType() == TypeParameter.DB) {
					if (serverId != null) {
						//We have already manage data from DB, we continue
						continue;
					}
					
					serverId = Integer.parseInt(param.getDataset());
					
					List<HashMap<String, String>> values = getDatabaseValues(resourceManager.getDatabaseServers(), serverId, parameters, null);
					if (values != null) {
						for (HashMap<String, String> value : values) {
							int paramDatabaseServerId = 0;
							try {
								paramDatabaseServerId = Integer.parseInt(param.getDataset());
							} catch (Exception e) { }

							List<Parameter> params = new ArrayList<Parameter>();
							for (Parameter paramTmp : parameters) {
								if (paramTmp.getId() == param.getId()) {
									String paramValue = value.get(paramTmp.getColumn());
									
									Parameter paramToAdd = new Parameter();
									paramToAdd.updateInfo(paramTmp.getName(), paramTmp.getQuestion(), false, paramValue, paramTmp.getDefaultValue(), null);
									params.add(paramToAdd);
								}
								else if (serverId == paramDatabaseServerId) {
									String paramValue = value.get(paramTmp.getColumn());
									
									Parameter paramToAdd = new Parameter();
									paramToAdd.updateInfo(paramTmp.getName(), paramTmp.getQuestion(), false, paramValue, paramTmp.getDefaultValue(), null);
									params.add(paramToAdd);
								}
								else {
									params.add(paramTmp);
								}
							}

							parameterToRuns.add(params);
						}
					}
					
				}
				else if (param.useListOfValues()) {

					ListOfValues lov = getSelectedLov(lovs, param.getListOfValues());
					for (String value : lov.getValues()) {

						List<Parameter> params = new ArrayList<Parameter>();
						for (Parameter paramTmp : parameters) {
							if (paramTmp.getId() == param.getId()) {
								Parameter paramToAdd = new Parameter();
								paramToAdd.updateInfo(param.getName(), param.getQuestion(), false, value, param.getDefaultValue(), null);
								params.add(paramToAdd);
							}
							else {
								params.add(paramTmp);
							}
						}

						parameterToRuns.add(params);
					}
				}
			}

			if (parameterToRuns.isEmpty()) {
				List<Parameter> classicParams = new ArrayList<Parameter>();
				for (Parameter param : parameters) {
					classicParams.add(param);
				}
				parameterToRuns.add(classicParams);
			}
		}
		else {
			parameterToRuns.add(new ArrayList<Parameter>());
		}

		int totalWorkflow = parameterToRuns.size();
		int index = 1;
		for (List<Parameter> params : parameterToRuns) {
			RunManager manager = new RunManager(logger, workflowManager, fileManager, resourceManager);

			WorkflowProgress workflowProgress = progressManager.getRunningProgress(workflow.getId(), uuid);
			if (workflowProgress != null) {
				workflowProgress.setRunManager(manager);
				workflowProgress.setInstance(null);

				workflowProgress.setTotalWorkflow(totalWorkflow);
				workflowProgress.setWorkflowNumber(index);
			}
			else {
				throw new Exception("This workflow has not been initialized correctly. Try to launch it again.");
			}

			WorkflowInstance instance = new WorkflowInstance(uuid, workflow);
			try {
				instance.setResult(Result.RUNNING);

				instance = manager.run(workflowProgress, locale, workflow, uuid, launcher, params);

				List<ActivityLog> activityLogs = getWorkflowRun(instance);
				instance.setActivityLogs(activityLogs);
				instance.setFinish(true);

				workflowProgress.setInstance(instance);
			} catch (Exception e) {
				instance.setResult(Result.ERROR);
				instance.setFinish(true);

				workflowProgress.setInstance(instance);

				throw e;
			}
			index++;
		}
	}

	private ListOfValues getSelectedLov(List<ListOfValues> lovs, ListOfValues selectedLov) {
		if (lovs != null) {
			for (ListOfValues lov : lovs) {
				if (lov.getId() == selectedLov.getId()) {
					return lov;
				}
			}
		}
		return selectedLov;
	}
	
//	private HashMap<Parameter, List<HashMap<String, String>>> loadDBParameters(List<DatabaseServer> servers, List<Parameter> parameters, List<Variable> variables) throws Exception {
//		HashMap<Parameter, List<HashMap<String, String>>> parametersDB = new HashMap<Parameter, List<HashMap<String,String>>>();
//		if (parameters != null) {
//			for (Parameter parameter : parameters) {
//				if (parameter.getParameterType() == TypeParameter.DB) {
//					int serverId = Integer.parseInt(parameter.getDataset());
//					List<HashMap<String, String>> values = getDatabaseValues(servers, serverId, parameters, variables);
//					if (values != null) {
//						parametersDB.put(parameter, values);
//					}
//				}
//			}
//		}
//		return parametersDB;
//	}

	private List<HashMap<String, String>> getDatabaseValues(List<DatabaseServer> servers, int serverId, List<Parameter> parameters, List<Variable> variables) throws Exception {
		DatabaseServer selectedServer = null;
		if (servers != null) {
			for (DatabaseServer server : servers) {
				if (server.getId() == serverId) {
					selectedServer = server;
				}
			}
		}
		
		if (selectedServer == null) {
			return new ArrayList<HashMap<String,String>>();
		}
		
		try {
			DatasourceJdbc datasource = new DatasourceJdbc();
			datasource.setDriver(selectedServer.getDriverJdbc());
			datasource.setFullUrl(true);
			datasource.setUrl(selectedServer.getDatabaseUrlDisplay());
			datasource.setUser(selectedServer.getLogin());
			datasource.setPassword(selectedServer.getPassword());
			
			String query = selectedServer.getQuery(parameters, variables);

			VanillaJdbcConnection jdbcConnection = ConnectionManager.getInstance().getJdbcConnection(datasource);
			VanillaPreparedStatement rs = jdbcConnection.prepareQuery(query);
			ResultSet jdbcResult = rs.executeQuery(query);
			ResultSetMetaData jdbcMeta = jdbcResult.getMetaData();

			
			List<String> columnNames = new ArrayList<String>();
			for (int i = 1; i <= jdbcMeta.getColumnCount(); i++) {
				columnNames.add(jdbcMeta.getColumnLabel(i));
			}
			
			List<HashMap<String, String>> values = new ArrayList<HashMap<String,String>>();
			while (jdbcResult.next()) {
				HashMap<String, String> line = new HashMap<String, String>();
				for (String colName : columnNames) {
					String value = ((String) jdbcResult.getString(colName));
					line.put(colName, value);
				}
				values.add(line);
			}
			
			return values;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Connection not correct : " + e.getMessage());
		}
	}

	public List<ActivityLog> getWorkflowRun(WorkflowInstance instance) throws Exception {
		return workflowManager.getInstanceLogs(instance.getId());
	}
}
