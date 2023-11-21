package bpm.gateway.runtime2.transformation.veolia;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.xml.sax.InputSource;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.server.file.MdmFileServer;
import bpm.gateway.core.veolia.ConnectorAbonneXML;
import bpm.gateway.core.veolia.DateChargement;
import bpm.gateway.core.veolia.ReflectionHelper;
import bpm.gateway.core.veolia.abonnes.AbonnesDAO;
import bpm.gateway.core.veolia.abonnes.GRC;
import bpm.gateway.core.veolia.db.VeoliaAbonnesDaoComponent;
import bpm.gateway.core.veolia.db.VeoliaConnectionManager;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class RunConnectorAbonne extends RuntimeStep {

	private IVanillaAPI vanillaApi;

	private VeoliaAbonnesDaoComponent veoliaDaoComp;
//	private GRC grc;
	private ClassDefinition classDef;
	
	private InputStream fis;
	private InputSource source;
//	private XMLReader reader;

	private PrintWriter writer;
//	private Marshaller jaxbMarshaller;

//	private String beginDate, endDate;
	private String query;
	private String query2;
	private boolean isInput;
	private boolean isODS;

	private DateChargement dateChargement;
	private String fileName = "";

	public RunConnectorAbonne(IRepositoryContext repositoryContext, Transformation transformation, int bufferSize) throws Exception {
		super(repositoryContext, transformation, bufferSize);
		if (getRepositoryContext() == null) {
			throw new Exception("Cannot use a ConnectorAbonne step without a VanillaContext. You must be connected to Vanilla.");
		}
	}

	@Override
	public void init(Object adapter) throws Exception {
		ConnectorAbonneXML transfo = (ConnectorAbonneXML) getTransformation();
		this.isInput = transfo.isInput();
		this.vanillaApi = new RemoteVanillaPlatform(getRepositoryContext().getVanillaContext());

		if (isInput) {
			initInput(adapter, transfo);
		}
		else {
			initOutput(adapter, transfo);
		}
	}

	private void initInput(Object adapter, ConnectorAbonneXML transfo) throws Exception {
//		JAXBContext jaxbContext = null;
//		try {
//			jaxbContext = JAXBContext.newInstance(GRC.class);
//		} catch (JAXBException e1) {
//			e1.printStackTrace();
//		}
//
//		Unmarshaller jaxbUnmarshaller = null;
//		try {
//			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		}
//
		try {
			this.fis = ((AbstractFileServer) transfo.getFileServer()).getInpuStream(transfo);
		} catch (Exception e) {
			error("unable to open file ", e);
			throw e;
		}
//
		if (fis != null) {
//			grc = (GRC) jaxbUnmarshaller.unmarshal(fis);
//			fis.close();
			
			this.source = new InputSource(fis);
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
			veoliaDaoComp = manager.getAbonneDao(fullUrl, c.getLogin(), c.getPassword(), driverInfo.getClassName());
		}
		else {
			throw new Exception("Unable to get Full URL");
		}
		
		String etlName = transfo.getDocument().getName();
		if (transfo.useMdm() && transfo.getSelectedContract() != null) {
			fileName = ((MdmFileServer) transfo.getFileServer()).getDocumentName(transfo);
		}
		else {
			fileName = transfo.getDefinition().substring(transfo.getDefinition().replace("\\", "/").lastIndexOf("/") + 1, transfo.getDefinition().length());
		}

		String mainClassIdentifiant = new GRC().getClass().getName();
		List<ClassRule> classRules = vanillaApi.getResourceManager().getClassRules(mainClassIdentifiant);

		this.classDef = ReflectionHelper.loadClass(mainClassIdentifiant);
		ReflectionHelper.buildClassDefinitionWithRules(vanillaApi, classDef, classRules, true);

		this.dateChargement = new DateChargement(etlName, fileName, "ve_ods_abonnes");
	}

	private void initOutput(Object adapter, ConnectorAbonneXML transfo) throws Exception {
//		JAXBContext jaxbContext = null;
//		try {
//			jaxbContext = JAXBContext.newInstance(GRC.class);
//		} catch (JAXBException e1) {
//			e1.printStackTrace();
//		}
//
//		try {
//			jaxbMarshaller = jaxbContext.createMarshaller();
//			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		}

//		this.beginDate = transfo.getBeginDate();
//		this.endDate = transfo.getEndDate();
		this.query = transfo.getQuery();
		this.query2 = transfo.getQuery2();
		this.isODS = transfo.isODS();

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
			veoliaDaoComp = manager.getAbonneDao(fullUrl, c.getLogin(), c.getPassword(), driverInfo.getClassName());
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
//		boolean fileCreated = false;
		f = new File(fileName);
		if (!f.exists()) {
			try {
				f.createNewFile();
//				fileCreated = true;
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
				AbonnesDAO abonnesDao = veoliaDaoComp.getAbonnesDao();
				abonnesDao.save(source, dateChargement, classDef, fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			setEnd();
			throw new Exception("Unable to load the file : " + e.getMessage());
		}
	}

	private void performRowOutput() throws JAXBException {
//		GRC grc = veoliaDaoComp.getAbonnesDao().getGRC(beginDate, endDate, query, query2);
//		jaxbMarshaller.marshal(grc, writer);
		veoliaDaoComp.getAbonnesDao().buildXMLFromDB(writer, isODS, query, query2, -1);
	}

	@Override
	public void releaseResources() {
		this.veoliaDaoComp = null;

		if (writer != null) {
			this.writer.close();
		}
		this.writer = null;
//		this.jaxbMarshaller = null;
		
		if (fis != null) {
			try {
				this.fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.fis = null;
		this.source = null;

		this.isInput = false;
		this.isODS = false;
//		this.beginDate = null;
//		this.endDate = null;
		this.query = null;
		this.query2 = null;

		this.vanillaApi = null;
		this.classDef = null;
	}

}
