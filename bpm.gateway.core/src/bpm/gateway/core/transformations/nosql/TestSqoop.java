package bpm.gateway.core.transformations.nosql;

import java.util.List;

import org.apache.sqoop.client.SqoopClient;
import org.apache.sqoop.model.MConnection;
import org.apache.sqoop.model.MConnectionForms;
import org.apache.sqoop.model.MForm;
import org.apache.sqoop.model.MInput;
import org.apache.sqoop.model.MJob;
import org.apache.sqoop.model.MJobForms;
import org.apache.sqoop.model.MSubmission;
import org.apache.sqoop.submission.counter.Counter;
import org.apache.sqoop.submission.counter.CounterGroup;
import org.apache.sqoop.submission.counter.Counters;
import org.apache.sqoop.validation.Status;

public class TestSqoop {

	private static final String SQOOP_URL = "http://cloudera5.home:12000/sqoop/";

	private enum TypeAction {
		IMPORT_HDFS, EXPORT_HDFS
	}

	private static TypeAction action = TypeAction.IMPORT_HDFS;

	public static void main(String[] args) {
		SqoopClient client = new SqoopClient(SQOOP_URL);

		try {
			long xid = createConnexion(client);

			switch (action) {
			case EXPORT_HDFS:
				long exportJobId = createJobExport(client, xid);
				runJob(client, exportJobId);
				break;
			case IMPORT_HDFS:
				long importJobId = createJobImport(client, xid);
				runJob(client, importJobId);
				break;

			default:
				break;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static long createConnexion(SqoopClient client) {
		MConnection newCon = client.newConnection(1);

		// Get connection and framework forms. Set name for connection
		MConnectionForms conForms = newCon.getConnectorPart();
		MConnectionForms frameworkForms = newCon.getFrameworkPart();
		newCon.setName("MyConnection");

		// Set connection forms values
		conForms.getStringInput("connection.connectionString").setValue("jdbc:mysql://192.168.1.102:3306/sampledata");
		conForms.getStringInput("connection.jdbcDriver").setValue("com.mysql.jdbc.Driver");
		conForms.getStringInput("connection.username").setValue("root");
		conForms.getStringInput("connection.password").setValue("root");

		frameworkForms.getIntegerInput("security.maxConnections").setValue(0);

		Status statusConnection = client.createConnection(newCon);
		if (statusConnection.canProceed()) {
			System.out.println("Created. New Connection ID : " + newCon.getPersistenceId());
		}
		else {
			System.out.println("Check for status and forms error ");
		}

		printMessage(newCon.getConnectorPart().getForms());
		printMessage(newCon.getFrameworkPart().getForms());

		return newCon.getPersistenceId();
	}

	private static long createJobImport(SqoopClient client, long xid) {
		MJob newjob = client.newJob(xid, org.apache.sqoop.model.MJob.Type.IMPORT);

		MJobForms connectorForm = newjob.getConnectorPart();
		MJobForms frameworkForm = newjob.getFrameworkPart();

		newjob.setName("ImportJob");

		// Database configuration
		connectorForm.getStringInput("table.schemaName").setValue("");
		// Input either table name or sql
		connectorForm.getStringInput("table.tableName").setValue("customers");
		// connectorForm.getStringInput("table.sql").setValue("select id,name from table where ${CONDITIONS}");
		connectorForm.getStringInput("table.columns").setValue("customernumber,customername");
		connectorForm.getStringInput("table.partitionColumn").setValue("customernumber");
		// Set boundary value only if required
		// connectorForm.getStringInput("table.boundaryQuery").setValue("");

		// Output configurations
		frameworkForm.getEnumInput("output.storageType").setValue("HDFS");
		frameworkForm.getEnumInput("output.outputFormat").setValue("TEXT_FILE");// Other
		// option:
		// SEQUENCE_FILE
		frameworkForm.getStringInput("output.outputDirectory").setValue("/user/sqoop2/output/Test5");

		// Job resources
		frameworkForm.getIntegerInput("throttling.extractors").setValue(1);
		frameworkForm.getIntegerInput("throttling.loaders").setValue(1);

		Status statusJob = client.createJob(newjob);
		if (statusJob.canProceed()) {
			System.out.println("New Job ID: " + newjob.getPersistenceId());
		}
		else {
			System.out.println("Check for status and forms error ");
		}

		// Print errors or warnings
		printMessage(newjob.getConnectorPart().getForms());
		printMessage(newjob.getFrameworkPart().getForms());

		return newjob.getPersistenceId();
	}

	private static long createJobExport(SqoopClient client, long xid) {
		MJob newjob = client.newJob(xid, org.apache.sqoop.model.MJob.Type.EXPORT);
		MJobForms connectorForm = newjob.getConnectorPart();
		MJobForms frameworkForm = newjob.getFrameworkPart();

		newjob.setName("ExportJob");
		// Database configuration
		connectorForm.getStringInput("table.schemaName").setValue("sampledata");
		// Input either table name or sql
		connectorForm.getStringInput("table.tableName").setValue("newcustomers");
		// connectorForm.getStringInput("table.sql").setValue("select id,name from table where ${CONDITIONS}");
		connectorForm.getStringInput("table.columns").setValue("customernumber,customername");

		// Input configurations
		frameworkForm.getStringInput("input.inputDirectory").setValue("/user/sqoop2/output/Test");

		// Job resources
		frameworkForm.getIntegerInput("throttling.extractors").setValue(1);
		frameworkForm.getIntegerInput("throttling.loaders").setValue(1);

		Status status = client.createJob(newjob);
		if (status.canProceed()) {
			System.out.println("New Job ID: " + newjob.getPersistenceId());
		}
		else {
			System.out.println("Check for status and forms error ");
		}

		// Print errors or warnings
		printMessage(newjob.getConnectorPart().getForms());
		printMessage(newjob.getFrameworkPart().getForms());
		
		return newjob.getPersistenceId();
	}

	private static void runJob(SqoopClient client, long jid) {
		// Job submission start
		MSubmission submission = client.startSubmission(jid);
		System.out.println("Status : " + submission.getStatus());
		if (submission.getStatus().isRunning() && submission.getProgress() != -1) {
			System.out.println("Progress : " + String.format("%.2f %%", submission.getProgress() * 100));
		}
		System.out.println("Hadoop job id :" + submission.getExternalId());
		System.out.println("Job link : " + submission.getExternalLink());
		Counters counters = submission.getCounters();
		if (counters != null) {
			System.out.println("Counters:");
			for (CounterGroup group : counters) {
				System.out.print("\t");
				System.out.println(group.getName());
				for (Counter counter : group) {
					System.out.print("\t\t");
					System.out.print(counter.getName());
					System.out.print(": ");
					System.out.println(counter.getValue());
				}
			}
		}
		if (submission.getExceptionInfo() != null) {
			System.out.println("Exception info : " + submission.getExceptionInfo());
		}

		// Check job status
		MSubmission submissionStatus = client.getSubmissionStatus(jid);
		while (submissionStatus.getStatus().isRunning() && submissionStatus.getProgress() != -1) {
			System.out.println("Progress : " + String.format("%.2f %%", submissionStatus.getProgress() * 100));
			submissionStatus = client.getSubmissionStatus(jid);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void printMessage(List<MForm> formList) {
		for (MForm form : formList) {
			List<MInput<?>> inputlist = form.getInputs();
			if (form.getValidationMessage() != null) {
				System.out.println("Form message: " + form.getValidationMessage());
			}
			for (MInput minput : inputlist) {
				if (minput.getValidationStatus() == Status.ACCEPTABLE) {
					System.out.println("Warning:" + minput.getValidationMessage());
				}
				else if (minput.getValidationStatus() == Status.UNACCEPTABLE) {
					System.out.println("Error:" + minput.getValidationMessage());
				}
			}
		}
	}
}
