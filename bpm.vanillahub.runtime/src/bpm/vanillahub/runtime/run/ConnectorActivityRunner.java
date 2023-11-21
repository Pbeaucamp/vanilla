package bpm.vanillahub.runtime.run;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Locale;

import bpm.connector.seveneleven.ConnectorDefinition;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.ConnectorXmlActivity;
import bpm.vanillahub.core.beans.managers.TransformManager;
import bpm.vanillahub.core.beans.resources.Connector;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.run.transform.CarburantManager;
import bpm.vanillahub.runtime.run.transform.XMLManager;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.resources.Cible;

public class ConnectorActivityRunner extends ActivityRunner<ConnectorXmlActivity> {

	private List<Cible> cibles;

	private TransformManager runManager;

	public ConnectorActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, ConnectorXmlActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop, List<Cible> cibles) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		this.cibles = cibles;
	}

	@Override
	public void run(Locale locale) {
		// logger.info("Insertion of file '" + result.getFileName() + "'");
		// long startTime = new Date().getTime();
		ByteArrayInputStream parentStream = result.getInputStream();

		Connector connector = activity.getConnector();

		if (connector.getName().equals(ConnectorDefinition.CONNECTOR_NAME)) {
			// Disable for now
			// DatabaseServer databaseServer = (DatabaseServer)
			// activity.getResource(databases);
			// if (runManager == null) {
			// String driver = databaseServer.getDriverJdbc();
			// String databaseUrl = databaseServer.getDatabaseUrl(parameters,
			// variables);
			// String login = databaseServer.getLogin();
			// String password = databaseServer.getPassword();
			//
			// ConnectionManager manager;
			// try {
			// manager = ConnectionManager.getInstance();
			// } catch (Exception e) {
			// e.printStackTrace();
			//
			// addError(e.getMessage());
			// setResult(Result.ERROR);
			//
			// return;
			// }
			//
			// VanillaJdbcConnection connection = null;
			// try {
			// connection =
			// manager.getJdbcConnection(databaseServer.getDatabaseUrlVS().getStringForTextbox(),
			// databaseServer.getLogin(), databaseServer.getPassword(),
			// databaseServer.getDriverJdbc());
			// boolean res = connection != null;
			// if (res) {
			// runManager = new bpm.connector.seveneleven.RunManager(driver,
			// databaseUrl, login, password);
			// }
			// else {
			// addError("The connection is not valid.");
			// setResult(Result.ERROR);
			// }
			// } catch (Exception e) {
			// e.printStackTrace();
			//
			// addError(e.getMessage());
			// setResult(Result.ERROR);
			//
			// return;
			// } finally {
			// try {
			// if (connection != null) {
			// manager.returnJdbcConnection(connection);
			// }
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }
			// }
			//
			// try {
			// if(result.isBigFile()) {
			// throw new Exception("This activity doesn't support files >
			// 500MB");
			// }
			// ConnectorResult connectorResult =
			// ((bpm.connector.seveneleven.RunManager)
			// runManager).insertFile(result.getFileName(),
			// result.getInfoComp(), parentStream, loopEnd);
			// if (connectorResult.getResult() ==
			// bpm.vanillahub.core.beans.managers.ConnectorResult.Result.IGNORED)
			// {
			//// logger.info("Le fichier '" + result.getFileName() + "' a été
			// ignoré.");
			// iterateNumberOfFileIgnored();
			// }
			// else if (connectorResult.getResult() ==
			// bpm.vanillahub.core.beans.managers.ConnectorResult.Result.ERROR)
			// {
			// addError(connectorResult.getError());
			// }
			// else if (connectorResult.getResult() ==
			// bpm.vanillahub.core.beans.managers.ConnectorResult.Result.SUCCESS)
			// {
			// iterateNumberOfFileTraited(connectorResult.getBatch());
			// }
			// } catch (Exception e) {
			// e.printStackTrace();
			//
			// addError(e.getMessage());
			// setResult(Result.ERROR);
			//
			// return;
			// }
		}
		else if (connector.getName().equals(XMLManager.CONNECTOR_NAME)) {
			try {
				runManager = new XMLManager();

				String name = result.getFileName();
				if (name.contains(".")) {
					name = name.substring(0, name.lastIndexOf("."));
				}
				result.setFileName(name + ".json");

				parentStream = ((XMLManager) runManager).buildFile(parentStream);
			} catch (Exception e) {
				e.printStackTrace();

				addError(e.getMessage());
				setResult(Result.ERROR);

				return;
			}
		}
		else if (connector.getName().equals(CarburantManager.CONNECTOR_NAME)) {
			try {
				runManager = new CarburantManager();

				String name = result.getFileName();
				if (name.contains(".")) {
					name = name.substring(0, name.lastIndexOf("."));
				}
				result.setFileName(name + ".csv");

				parentStream = ((CarburantManager) runManager).buildFile(parentStream);
			} catch (Exception e) {
				e.printStackTrace();

				addError(e.getMessage());
				setResult(Result.ERROR);

				return;
			}
		}
		
		result.setInputStream(parentStream);
		// long endTime = new Date().getTime();

		if (loopEnd) {
			addInfo(Labels.getLabel(locale, Labels.NumberFileInserted) + " : " + getNumberOfFileTraited());
			addInfo(Labels.getLabel(locale, Labels.NumberFileIgnored) + " : " + getNumberOfFileIgnored());
		}

		// logger.info("End of file insertion. Time = " + (endTime -
		// startTime));
		setResult(Result.SUCCESS);
	}

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(cibles);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(cibles);
	}

	public void setEnd(boolean loopEnd) {
		this.loopEnd = loopEnd;
	}

	@Override
	protected void clearResources() {
		// runManager.clearConnection();
	}
}
