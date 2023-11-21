package bpm.vanilla.server.gateway.server.internal;

import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.GatewayDigester;
import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.gateway.IGatewayRuntimeConfig;
import bpm.vanilla.server.commons.pool.VanillaItemKey;
import bpm.vanilla.server.commons.server.tasks.GatewayState;
import bpm.vanilla.server.gateway.server.GatewayServer;
import bpm.vanilla.server.gateway.server.internal.GatewayRedefinedPropertiesExtractor.PropertiesIdentifier;
import bpm.vanilla.server.gateway.server.internal.GatewayRedefinedPropertiesExtractor.PropertiesObjectType;
import bpm.vanilla.server.gateway.server.tasks.TaskGateway;

public class FactoryRunnableGateway {

	public static RunnableGateway create(IGatewayRuntimeConfig runtimeConfig, VanillaItemKey itemKey, GatewayServer server, TaskGateway task, int maxRows, 
			DocumentGateway document, Logger transfoLogger, Logger serverLoger, HashMap<PropertiesIdentifier, Properties> overridenProperties) {

		serverLoger.info("##############################################################");
		serverLoger.info("Launch ETL " + task.getItemName());
		serverLoger.info("##############################################################");
		
		Logger.getLogger(FactoryRunnableGateway.class).debug("##############################################################");
		Logger.getLogger(FactoryRunnableGateway.class).debug("Launch ETL " + task.getItemName());
		Logger.getLogger(FactoryRunnableGateway.class).debug("##############################################################");
		
		HashMap<RuntimeStep, LogListener> steps = new HashMap<RuntimeStep, LogListener>();

		for (Parameter p : document.getParameters()) {

			if (runtimeConfig.getParametersValues() != null) {
				for (VanillaGroupParameter grpParam : runtimeConfig.getParametersValues()) {

					if (grpParam.getParameters() != null) {
						for (VanillaParameter param : grpParam.getParameters()) {
							if (param.getName().equals(p.getName())) {
								if(param.getSelectedValues() != null && !param.getSelectedValues().isEmpty()) {
									p.setValue(param.getSelectedValues().get(0).toString());
									serverLoger.debug("Task " + task.getId() + " Parameter " + p.getName() + "=" + param.getSelectedValues().get(0).toString());
								}
								break;
							}
						}
					}
				}
			}
		}

		try {
			new GatewayDigester().clean(document.getRepositoryContext(), document);
		} catch(Exception e1) {
			e1.printStackTrace();
		}
		
		/*
		 * get RuntimeSteps
		 */
		for (Transformation t : document.getTransformations()) {
			t.setDocumentGateway(document);
			if (t instanceof DataStream) {
				// here we make sure that the server that will be overriden nor
				// alternatedConnections used are the same that the one will be
				// used at runtime
				Server s = ((DataStream) t).getServer();
				if (s != null) {
					Server ts = document.getResourceManager().getServer(s.getName());
					if (ts != null && s != ts) {
						((DataStream) t).setServer(ts);
					}
				}
			}
			
			t.initDescriptor();

			RuntimeStep r = t.getExecutioner(itemKey.getRepositoryContext(), maxRows);

			r.setLogger(transfoLogger);

			/*
			 * add a listener on log events to insert logs in database INSERT
			 * INTO vanilla_logs
			 * (LOG_DATE,LOG_OBJECT_TYPE,LOG_REPOSITORY_ID,LOG_DIRECTORY_ITEM_ID
			 * ,LOG_STEP_NAME,LOG_MESSAGE,LOG_LEVEL)VALUES
			 */
			int dirItId = itemKey.getDirectoryItemId();
			int rep = itemKey.getRepositoryContext().getRepository().getId();
			LogListener logListener = null;
			try {
				logListener = new LogListener(task, String.valueOf(rep), dirItId, r, server.getUrl(), server.getConfig().getVanillaUrl());
				r.addLogListener(logListener);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			steps.put(r, logListener);
		}

		/*
		 * set RuntimeSteps Inputs/outputs
		 */
		for (RuntimeStep r : steps.keySet()) {
			Transformation t = r.getTransformation();
			for (Transformation out : t.getOutputs()) {
				for (RuntimeStep _r : steps.keySet()) {
					if (_r.getTransformation() == out) {
						r.addOutput(_r);
						_r.addInput(r);
						break;
					}
				}
			}
		}

		/*
		 * we create the alternateConnections from the Properties
		 */
		for (PropertiesIdentifier key : overridenProperties.keySet()) {
			if (key.type == PropertiesObjectType.DATABASE_CONNECTION) {

				Properties prop = overridenProperties.get(key);

				DataBaseServer s = (DataBaseServer) document.getResourceManager().getServer(key.name);

				if (s == null) {
					continue;
				}

				serverLoger.info("The task needs to override the DataBaseConnection on DataBaseServer " + key.name);
				DataBaseConnection defaultConnection = (DataBaseConnection) s.getCurrentConnection(null);

				DataBaseConnection realConnection = defaultConnection.copy();

				if (prop.getProperty(GatewayRedefinedPropertiesExtractor.P_DRIVER_NAME) != null) {
					realConnection.setDriverName(prop.getProperty(GatewayRedefinedPropertiesExtractor.P_DRIVER_NAME));
				}
				if (prop.getProperty(GatewayRedefinedPropertiesExtractor.P_FULL_URL) != null) {
					realConnection.setFullUrl(prop.getProperty(GatewayRedefinedPropertiesExtractor.P_FULL_URL));
				}
				if (prop.getProperty(GatewayRedefinedPropertiesExtractor.P_HOST) != null) {
					realConnection.setHost(prop.getProperty(GatewayRedefinedPropertiesExtractor.P_HOST));
				}
				if (prop.getProperty(GatewayRedefinedPropertiesExtractor.P_LOGIN) != null) {
					realConnection.setLogin(prop.getProperty(GatewayRedefinedPropertiesExtractor.P_LOGIN));
				}
				if (prop.getProperty(GatewayRedefinedPropertiesExtractor.P_PASSWORD) != null) {
					realConnection.setPassword(prop.getProperty(GatewayRedefinedPropertiesExtractor.P_PASSWORD));
				}
				if (prop.getProperty(GatewayRedefinedPropertiesExtractor.P_PORT) != null) {
					realConnection.setPort(prop.getProperty(GatewayRedefinedPropertiesExtractor.P_PORT));
				}
				if (prop.getProperty(GatewayRedefinedPropertiesExtractor.P_DATABASE_NAME) != null) {
					realConnection.setDataBaseName(prop.getProperty(GatewayRedefinedPropertiesExtractor.P_DATABASE_NAME));
				}

				s.addOverridenConnection(task, realConnection);

				serverLoger.info("Overriden DataBaseConnection on DataBaseServer " + key.name + " stored");
			}
			else if (key.type == PropertiesObjectType.ALTERNATE_CONNECTION) {
				Server s = document.getResourceManager().getServer(key.name);
				Properties prop = overridenProperties.get(key);
				String connName = (String) prop.keys().nextElement();
				if (connName != null) {
					for (IServerConnection c : s.getConnections()) {
						if (c.getName().equals(connName)) {
							try {
								synchronized (s) {
									s.addOverridenConnection(task, c);
								}

								Logger.getLogger(FactoryRunnableGateway.class).info("Using alternate Connection " + connName + " on Server " + s.getName());
							} catch (Exception e) {
								e.printStackTrace();
								Logger.getLogger(FactoryRunnableGateway.class).error("Cannot use alternate Connection " + connName + " on Server " + s.getName() + " - " + e.getMessage(), e);
							}
							break;
						}
					}
				}

			}
		}

		RunnableGateway run = new RunnableGateway(task, (GatewayState) task.getTaskState(), steps, serverLoger, document);
		return run;
	}

	public static void restoreDefaultConnections(TaskGateway task, DocumentGateway document) {

		for (Server s : document.getResourceManager().getServers()) {
			synchronized (s) {
				s.removeOverridenConnection(task);
			}

		}
	}

}
