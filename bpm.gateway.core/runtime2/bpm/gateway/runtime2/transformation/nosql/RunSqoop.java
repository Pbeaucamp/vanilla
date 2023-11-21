package bpm.gateway.runtime2.transformation.nosql;

import java.util.List;

import org.apache.sqoop.client.SqoopClient;
import org.apache.sqoop.model.MConnection;
import org.apache.sqoop.model.MConnectionForms;
import org.apache.sqoop.model.MForm;
import org.apache.sqoop.model.MInput;
import org.apache.sqoop.model.MJob;
import org.apache.sqoop.model.MJobForms;
import org.apache.sqoop.model.MSubmission;
import org.apache.sqoop.validation.Status;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.gateway.core.transformations.nosql.SqoopTransformation;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.studio.jdbc.management.model.DriverInfo;

public class RunSqoop extends RuntimeStep {

	private SqoopClient sqoopClient;
	private Long jobId;

	public RunSqoop(Transformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		SqoopTransformation sqoopTransfo = (SqoopTransformation) getTransformation();

		DataBaseServer server = (DataBaseServer) sqoopTransfo.getServer();
		DataBaseConnection c = (DataBaseConnection) server.getCurrentConnection(adapter);

		if (sqoopTransfo.getSqoopUrl() == null || sqoopTransfo.getSqoopUrl().isEmpty()) {
			throw new Exception("The URL of Sqoop has not been set.");
		}

		sqoopClient = new SqoopClient(sqoopTransfo.getSqoopUrl());

		long connexionId = createConnexion(sqoopClient, c);
		if (sqoopTransfo.isImport()) {
			jobId = createJobImport(sqoopClient, sqoopTransfo, connexionId);
		}
		else {
			jobId = createJobExport(sqoopClient, sqoopTransfo, connexionId);
		}

		isInited = true;
	}

	@Override
	public void performRow() throws Exception {
		if (jobId != null) {
			runJob(sqoopClient, jobId);
			setEnd();
		}
		else {
			throw new Exception("The job has not been successfully created.");
		}
	}

	@Override
	public void releaseResources() {
		if (sqoopClient != null) {
			sqoopClient = null;
			info(" closed sqoop client");
		}

		if (jobId != null) {
			jobId = null;
		}
	}

	private long createConnexion(SqoopClient client, DataBaseConnection c) throws Exception {
		String url = null;
		if (!c.isUseFullUrl()) {
			url = JdbcConnectionProvider.createUrlConnection(c.getDriverName(), c.getHost(), c.getPort(), c.getDataBaseName());
		}
		else {
			url = c.getFullUrl();
		}
		
		DriverInfo driver = JdbcConnectionProvider.getDriver(c.getDriverName());

		MConnection newCon = client.newConnection(1);

		// Get connection and framework forms. Set name for connection
		MConnectionForms conForms = newCon.getConnectorPart();
		MConnectionForms frameworkForms = newCon.getFrameworkPart();
		newCon.setName("GatewayConnection");

		// Set connection forms values
		conForms.getStringInput("connection.connectionString").setValue(url);
		conForms.getStringInput("connection.jdbcDriver").setValue(driver.getClassName());

		if (c.getLogin() != null && !c.getLogin().isEmpty()) {
			conForms.getStringInput("connection.username").setValue(c.getLogin());
			conForms.getStringInput("connection.password").setValue(c.getPassword());
		}

		frameworkForms.getIntegerInput("security.maxConnections").setValue(0);

		Status statusConnection = client.createConnection(newCon);
		if (!statusConnection.canProceed()) {
			printMessage(newCon.getConnectorPart().getForms());
			printMessage(newCon.getFrameworkPart().getForms());

			throw new Exception("Unable to create a connection.");
		}

		return newCon.getPersistenceId();
	}

	private long createJobImport(SqoopClient client, SqoopTransformation sqoopTransfo, long xid) throws Exception {
		MJob newjob = client.newJob(xid, org.apache.sqoop.model.MJob.Type.IMPORT);

		MJobForms connectorForm = newjob.getConnectorPart();
		MJobForms frameworkForm = newjob.getFrameworkPart();

		newjob.setName("ImportJob");

		connectorForm.getStringInput("table.tableName").setValue(sqoopTransfo.getTableName());
		connectorForm.getStringInput("table.columns").setValue(sqoopTransfo.getColumnsAsString());
		connectorForm.getStringInput("table.partitionColumn").setValue(sqoopTransfo.getPartitionColumnStream().name);

		// Output configurations
		frameworkForm.getEnumInput("output.storageType").setValue("HDFS");
		frameworkForm.getEnumInput("output.outputFormat").setValue("TEXT_FILE");

		frameworkForm.getStringInput("output.outputDirectory").setValue(sqoopTransfo.getHdfsDirectory());

		// Job resources
		frameworkForm.getIntegerInput("throttling.extractors").setValue(1);
		frameworkForm.getIntegerInput("throttling.loaders").setValue(1);

		Status statusJob = client.createJob(newjob);
		if (!statusJob.canProceed()) {
			printMessage(newjob.getConnectorPart().getForms());
			printMessage(newjob.getFrameworkPart().getForms());

			throw new Exception("Unable to create a job.");
		}

		return newjob.getPersistenceId();
	}

	private long createJobExport(SqoopClient client, SqoopTransformation sqoopTransfo, long xid) throws Exception {
		MJob newjob = client.newJob(xid, org.apache.sqoop.model.MJob.Type.EXPORT);
		MJobForms connectorForm = newjob.getConnectorPart();
		MJobForms frameworkForm = newjob.getFrameworkPart();

		newjob.setName("ExportJob");

		connectorForm.getStringInput("table.tableName").setValue(sqoopTransfo.getTableName());
		connectorForm.getStringInput("table.columns").setValue(sqoopTransfo.getColumnsAsString());

		// Input configurations
		frameworkForm.getStringInput("input.inputDirectory").setValue(sqoopTransfo.getHdfsDirectory());
		
		// Job resources
		frameworkForm.getIntegerInput("throttling.extractors").setValue(1);
		frameworkForm.getIntegerInput("throttling.loaders").setValue(1);

		Status status = client.createJob(newjob);
		if (!status.canProceed()) {
			printMessage(newjob.getConnectorPart().getForms());
			printMessage(newjob.getFrameworkPart().getForms());

			throw new Exception("Unable to create a job.");
		}

		return newjob.getPersistenceId();
	}

	private void runJob(SqoopClient client, long jid) {
		// Job submission start
		MSubmission submission = client.startSubmission(jid);
		info("Status : " + submission.getStatus());
		info("Hadoop job id :" + submission.getExternalId());
		info("Job link : " + submission.getExternalLink());

		// Check job status
		MSubmission submissionStatus = client.getSubmissionStatus(jid);
		while (submissionStatus.getStatus().isRunning() && submissionStatus.getProgress() != -1) {
			info("Progress : " + String.format("%.2f %%", submissionStatus.getProgress() * 100));
			submissionStatus = client.getSubmissionStatus(jid);

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void printMessage(List<MForm> formList) {
		for (MForm form : formList) {
			List<MInput<?>> inputlist = form.getInputs();
			if (form.getValidationMessage() != null) {
				info("Form message: " + form.getValidationMessage());
			}
			for (MInput minput : inputlist) {
				if (minput.getValidationStatus() == Status.ACCEPTABLE) {
					warn("Warning:" + minput.getValidationMessage());
				}
				else if (minput.getValidationStatus() == Status.UNACCEPTABLE) {
					error("Error:" + minput.getValidationMessage());
				}
			}
		}
	}

}
