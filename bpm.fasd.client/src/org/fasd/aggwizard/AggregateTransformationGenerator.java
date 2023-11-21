package org.fasd.aggwizard;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.fasd.i18N.LanguageText;
import org.freeolap.FreemetricsPlugin;

import bpm.gateway.core.Comment;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseFactory;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.transformations.SqlScript;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

public class AggregateTransformationGenerator {
	public static final String LOGIN = "login"; //$NON-NLS-1$
	public static final String PASSWORD = "password"; //$NON-NLS-1$
	public static final String URL = "url"; //$NON-NLS-1$
	public static final String DRIVER_NAME = "driverName"; //$NON-NLS-1$
	public static final String DRIVER_CLASS = "driverClass"; //$NON-NLS-1$

	private String aggregateTableName;
	private String transfoName;
	private String creationScript;
	private String fillScript;
	private Properties sqlConnectionInfo;

	/**
	 * @param aggregateTableName
	 * @param creationScript
	 * @param fillScript
	 * @param sqlConnectionInfo
	 */
	public AggregateTransformationGenerator(String transfoName, String aggregateTableName, String creationScript, String fillScript, Properties sqlConnectionInfo) {
		this.aggregateTableName = aggregateTableName;
		this.creationScript = creationScript;
		this.fillScript = fillScript;
		this.sqlConnectionInfo = sqlConnectionInfo;
		this.transfoName = transfoName;
	}

	private DataBaseServer createDataSource() throws Exception {
		DataBaseConnection socket = new DataBaseConnection();
		socket.setName("SqlConnection"); //$NON-NLS-1$
		socket.setDataBaseName("sampledata"); //$NON-NLS-1$

		/*
		 * The driver Name come from the file driverJdbc.xml
		 */
		socket.setDriverName(sqlConnectionInfo.getProperty(DRIVER_NAME));
		socket.setHost("localhost"); //$NON-NLS-1$
		socket.setFullUrl(sqlConnectionInfo.getProperty(URL));
		socket.setUseFullUrl(true);
		socket.setLogin(sqlConnectionInfo.getProperty(LOGIN));
		socket.setPassword(sqlConnectionInfo.getProperty(PASSWORD));

		// create the DataBaseServer
		DataBaseServer sqlDatabase = DataBaseFactory.create("sqlDatabase", "", socket); //$NON-NLS-1$ //$NON-NLS-2$

		// register the server in the manager, if the connection is not opened,
		// it will be
		return sqlDatabase;
	}

	private Transformation createDrop(String serverName) {
		SqlScript step = new SqlScript();
		step.setName("Drop Aggregate Table"); //$NON-NLS-1$

		step.setServer(serverName);
		StringBuffer def = new StringBuffer();
		def.append("DROP TABLE " + aggregateTableName + ";"); //$NON-NLS-1$ //$NON-NLS-2$
		step.setDefinition(def.toString());

		step.setPositionX(50);
		step.setPositionY(50);

		return step;
	}

	private Transformation createCreateTable(String serverName) {
		SqlScript step = new SqlScript();
		step.setName("Create Aggregate Table"); //$NON-NLS-1$

		step.setServer(serverName);
		StringBuffer def = new StringBuffer();
		def.append(creationScript);
		step.setDefinition(def.toString());

		step.setPositionX(200);
		step.setPositionY(50);

		return step;
	}

	private Transformation createFillTable(String serverName) {
		SqlScript step = new SqlScript();
		step.setName("Fill Aggregate Table"); //$NON-NLS-1$

		step.setServer(serverName);
		StringBuffer def = new StringBuffer();
		def.append(fillScript + ";"); //$NON-NLS-1$
		step.setDefinition(def.toString());

		step.setPositionX(350);
		step.setPositionY(50);
		return step;
	}

	private DocumentGateway createBIG() throws Exception {
		DocumentGateway model = new DocumentGateway();
		model.setAuthor("FASD v" + FreemetricsPlugin.getDefault().getBundle().getVersion().toString()); //$NON-NLS-1$
		model.setName(transfoName);

		DataBaseServer dbServer = null;
		try {
			dbServer = createDataSource();

		} catch (Exception ex) {
			throw new Exception(LanguageText.AggregateTransformationGenerator_17 + ex.getMessage(), ex);
		}
		try {
			ResourceManager.getInstance().addServer(dbServer);
		} catch (Exception ex) {

		}

		Transformation drop = createDrop(dbServer.getName());
		model.addTransformation(drop);

		Transformation create = createCreateTable(dbServer.getName());
		model.addTransformation(create);

		Transformation fill = createFillTable(dbServer.getName());
		model.addTransformation(fill);

		drop.addOutput(create);
		create.addInput(drop);

		create.addOutput(fill);
		fill.addInput(create);

		Comment annote = new Comment();
		annote.setContent(LanguageText.AggregateTransformationGenerator_18 + FreemetricsPlugin.getDefault().getBundle().getVersion().toString());
		annote.setX(5);
		annote.setY(5);
		annote.setWidth(400);
		annote.setHeight(60);

		model.addAnnote(annote);

		return model;
	}

	public void generate(OutputStream stream) throws Exception {
		DocumentGateway model = null;
		try {
			model = createBIG();
		} catch (Exception ex) {
			throw new Exception(LanguageText.AggregateTransformationGenerator_19 + ex.getMessage(), ex);
		}

		try {
			model.write(stream);
		} catch (IOException ex) {
			throw new Exception(LanguageText.AggregateTransformationGenerator_20 + ex.getMessage(), ex);
		}

	}
}
