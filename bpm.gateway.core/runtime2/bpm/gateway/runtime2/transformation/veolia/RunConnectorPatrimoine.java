package bpm.gateway.runtime2.transformation.veolia;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.server.file.MdmFileServer;
import bpm.gateway.core.veolia.ConnectorPatrimoineXML;
import bpm.gateway.core.veolia.DateChargement;
import bpm.gateway.core.veolia.LogXML;
import bpm.gateway.core.veolia.ReflectionHelper;
import bpm.gateway.core.veolia.db.VeoliaConnectionManager;
import bpm.gateway.core.veolia.db.VeoliaPatrimoineDaoComponent;
import bpm.gateway.core.veolia.patrimoine.Patrimoine;
import bpm.gateway.core.veolia.patrimoine.PatrimoineDAO;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXls;
import bpm.gateway.core.veolia.patrimoine.xls.PatrimoineXlsHelper;
import bpm.gateway.core.veolia.patrimoine.xls.XlsRuntimeLog;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class RunConnectorPatrimoine extends RuntimeStep {

	private IVanillaAPI vanillaApi;

	private VeoliaPatrimoineDaoComponent veoliaDaoComp;
	private Patrimoine patrimoine;
	private PatrimoineXls patrimoineXls;
	private ClassDefinition classDef;

	private PrintWriter writer;
	private Marshaller jaxbMarshaller;

	private String beginDate, endDate;
	private String query;
	private boolean isInput;
	private boolean isFromXLS;

	private DateChargement dateChargement;
	private String fileName = "";

	private List<LogXML> patrimoineLogs;

	public RunConnectorPatrimoine(IRepositoryContext repositoryContext, Transformation transformation, int bufferSize) throws Exception {
		super(repositoryContext, transformation, bufferSize);
		if (getRepositoryContext() == null) {
			throw new Exception("Cannot use a ConnectorPatrimoine step without a VanillaContext. You must be connected to Vanilla.");
		}
	}

	@Override
	public void init(Object adapter) throws Exception {
		ConnectorPatrimoineXML transfo = (ConnectorPatrimoineXML) getTransformation();
		this.vanillaApi = new RemoteVanillaPlatform(getRepositoryContext().getVanillaContext());
		this.isInput = transfo.isInput();

		if (isInput) {
			initInput(adapter, transfo);
		}
		else {
			initOutput(adapter, transfo);
		}
	}

	private void initInput(Object adapter, ConnectorPatrimoineXML transfo) throws Exception {
		JAXBContext jaxbContext = null;
		try {
			jaxbContext = JAXBContext.newInstance(Patrimoine.class);
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}

		Unmarshaller jaxbUnmarshaller = null;
		try {
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		String etlName = transfo.getDocument().getName();
		if (transfo.useMdm() && transfo.getSelectedContract() != null) {
			fileName = ((MdmFileServer) transfo.getFileServer()).getDocumentName(transfo);
		}
		else {
			fileName = transfo.getDefinition().substring(transfo.getDefinition().replace("\\", "/").lastIndexOf("/") + 1, transfo.getDefinition().length());
		}

		String format = null;
		if (transfo.useMdm() && transfo.getSelectedContract() != null) {
			format = ((MdmFileServer) transfo.getFileServer()).getFormat(Integer.parseInt(transfo.getDefinition()));
		}
		else {
			format = getFormat(fileName);
		}

		if (format != null && format.equalsIgnoreCase("xls")) {
			isFromXLS = true;
		}

		String mainClassIdentifiant = null;
		try (InputStream fis = ((AbstractFileServer) transfo.getFileServer()).getInpuStream(transfo)) {
			if (isFromXLS) {
				patrimoineLogs = new ArrayList<>();

				List<XlsRuntimeLog> logsRuntime = new ArrayList<>();
				patrimoineXls = PatrimoineXlsHelper.buildPatrimoineXls(logsRuntime, patrimoineLogs, fis, fileName);

				if (logsRuntime != null && !logsRuntime.isEmpty()) {
					for (XlsRuntimeLog log : logsRuntime) {
						if (log.isError()) {
							error(log.getMessage());
						}
						else {
							info(log.getMessage());
						}
					}
				}

				mainClassIdentifiant = patrimoineXls.getClass().getName();
			}
			else {
				patrimoine = (Patrimoine) jaxbUnmarshaller.unmarshal(fis);

				mainClassIdentifiant = patrimoine.getClass().getName();
			}

			DataBaseServer dbServer = transfo.getServer() != null ? (DataBaseServer) transfo.getServer() : null;
			if (dbServer == null) {
				throw new Exception("Please, define database server before running.");
			}

			DataBaseConnection c = (DataBaseConnection) dbServer.getCurrentConnection(adapter);
			DriverInfo driverInfo = JdbcConnectionProvider.getDriver(c.getDriverName());

			String fullUrl = "";
			if (c.isUseFullUrl()) {
				fullUrl = c.getFullUrl();
			}
			else {
				fullUrl = JdbcConnectionProvider.getFullUrl(c.getDriverName(), c.getHost(), c.getPort(), c.getDataBaseName());
			}

			if (fullUrl != null && !fullUrl.isEmpty()) {
				VeoliaConnectionManager manager = VeoliaConnectionManager.getInstance();
				veoliaDaoComp = manager.getPatrimoineDao(fullUrl, c.getLogin(), c.getPassword(), driverInfo.getClassName());
			}
			else {
				throw new Exception("Unable to get Full URL");
			}
		}

		List<ClassRule> classRules = vanillaApi.getResourceManager().getClassRules(mainClassIdentifiant);

		this.classDef = ReflectionHelper.loadClass(mainClassIdentifiant);
		ReflectionHelper.buildClassDefinitionWithRules(vanillaApi, classDef, classRules, true);

		this.dateChargement = new DateChargement(etlName, fileName, "ve_ods_patrimoine");
	}

	private void initOutput(Object adapter, ConnectorPatrimoineXML transfo) throws Exception {
		JAXBContext jaxbContext = null;
		try {
			jaxbContext = JAXBContext.newInstance(Patrimoine.class);
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}

		try {
			jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		this.beginDate = transfo.getBeginDate();
		this.endDate = transfo.getEndDate();
		this.query = transfo.getQuery();

		DataBaseServer dbServer = transfo.getServer() != null ? (DataBaseServer) transfo.getServer() : null;
		if (dbServer == null) {
			throw new Exception("Please, define database server before running.");
		}

		DataBaseConnection c = (DataBaseConnection) dbServer.getCurrentConnection(adapter);
		DriverInfo driverInfo = JdbcConnectionProvider.getDriver(c.getDriverName());

		String fullUrl = "";
		if (c.isUseFullUrl()) {
			fullUrl = c.getFullUrl();
		}
		else {
			fullUrl = JdbcConnectionProvider.getFullUrl(c.getDriverName(), c.getHost(), c.getPort(), c.getDataBaseName());
		}

		if (fullUrl != null && !fullUrl.isEmpty()) {
			VeoliaConnectionManager manager = VeoliaConnectionManager.getInstance();
			veoliaDaoComp = manager.getPatrimoineDao(fullUrl, c.getLogin(), c.getPassword(), driverInfo.getClassName());
		}
		else {
			throw new Exception("Unable to get Full URL");
		}

		String fileName = null;
		try {
			fileName = transfo.getDocument().getStringParser().getValue(getTransformation().getDocument(), transfo.getDefinition());
		} catch (Exception e) {
			error(" error when getting/parsing fileName", e);
			throw e;
		}

		File f = new File(fileName);
		if (transfo.isDelete() && f.exists()) {
			f.delete();
			info(" delete file " + f.getAbsolutePath());
		}

		// flag to decide if the Headers Should be Writed or not
		boolean fileCreated = false;
		f = new File(fileName);
		if (!f.exists()) {
			try {
				f.createNewFile();
				fileCreated = true;
				info(" file " + f.getAbsolutePath() + " created");
			} catch (Exception e) {
				error(" cannot create file " + f.getName(), e);
				throw e;
			}
		}

		try {
			writer = new PrintWriter(f, transfo.getEncoding());
			info(" Writer created");
		} catch (Exception e) {
			error(" cannot create writer", e);
		}
	}

	@Override
	public void performRow() throws Exception {
		if (isInput) {
			performRowInput();
		}
		else {
			performRowOutput();
		}

		setEnd();
	}

	private void performRowInput() throws Exception {
		try {
			if (veoliaDaoComp != null) {
				PatrimoineDAO patrimoineDao = veoliaDaoComp.getPatrimoineDao();
				if (isFromXLS) {
					save(patrimoineDao, patrimoineXls);
				}
				else {
					save(patrimoineDao, patrimoine);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			setEnd();
			throw new Exception("Unable to load the file : " + e.getMessage());
		}
	}

	private void performRowOutput() throws JAXBException {
		Patrimoine patrimoine = veoliaDaoComp.getPatrimoineDao().getPatrimoine(beginDate, endDate, query);

		jaxbMarshaller.marshal(patrimoine, writer);
	}

	private void save(PatrimoineDAO patrimoineDao, Patrimoine patrimoine) {
		patrimoineDao.save(dateChargement, patrimoine, classDef, fileName);
	}

	private void save(PatrimoineDAO patrimoineDao, PatrimoineXls patrimoineXls) {
		patrimoineDao.saveXls(dateChargement, patrimoineXls, classDef, fileName, patrimoineLogs);
	}

	private String getFormat(String newFileName) {
		int index = newFileName.lastIndexOf(".") + 1;
		return newFileName.substring(index);
	}

	@Override
	public void releaseResources() {
		this.veoliaDaoComp = null;
		this.patrimoine = null;
		this.patrimoineXls = null;

		if (writer != null) {
			this.writer.close();
		}
		this.writer = null;
		this.jaxbMarshaller = null;

		this.isInput = false;
		this.beginDate = null;
		this.endDate = null;
		this.query = null;

		this.vanillaApi = null;
		this.classDef = null;
		this.patrimoineLogs = null;
	}
}
