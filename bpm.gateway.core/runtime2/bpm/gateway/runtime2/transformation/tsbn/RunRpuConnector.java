package bpm.gateway.runtime2.transformation.tsbn;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.IOUtils;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.tsbn.rpu.RpuConnector;
import bpm.gateway.core.tsbn.rpu.beans.ACTE;
import bpm.gateway.core.tsbn.rpu.beans.DIAGNOSTIC;
import bpm.gateway.core.tsbn.rpu.beans.DIAGNOSTIC.Type;
import bpm.gateway.core.tsbn.rpu.beans.ETABLISSEMENT;
import bpm.gateway.core.tsbn.rpu.beans.LISTEACTES;
import bpm.gateway.core.tsbn.rpu.beans.LISTEDA;
import bpm.gateway.core.tsbn.rpu.beans.OSCOUR;
import bpm.gateway.core.tsbn.rpu.beans.ObjectFactory;
import bpm.gateway.core.tsbn.rpu.beans.PASSAGES;
import bpm.gateway.core.tsbn.rpu.beans.PATIENT;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.utils.IOWriter;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class RunRpuConnector extends RuntimeStep {

	private static final String DIAG_CHG_ID = "dgn_chg_id";
	private static final String DIAG_ENT_ID = "dgn_ent_id";
	private static final String DIAG_CODE = "dgn_code";
	private static final String DIAG_PRINCIPAL = "dgn_principal";

	private static final String ACTE_CHG_ID = "act_chg_id";
	private static final String ACTE_ENT_ID = "act_ent_id";
	private static final String ACTE_CODE = "act_code";

	private static final String PATIENT_CP = "sau_patient_cp_residence";
	private static final String PATIENT_COMMUNE = "sau_patient_commune";
	private static final String PATIENT_DATE_NAISSANCE = "sau_patient_dt_naiss";
	private static final String PATIENT_SEXE = "sau_patient_sexe";
	private static final String PATIENT_TIME_PREM_CONTACT = "sau_date_time_prem_contact";
	private static final String PATIENT_MODE_ENTREE = "sau_i_md_entree";
	private static final String PATIENT_PROV = "sau_patient_prov";
	private static final String PATIENT_MOY_TRANS_ARRIV_CODE = "sau_moy_trans_arriv_code";
	private static final String PATIENT_TRANSPORT_MED_CODE = "sau_transp_med_code";
	private static final String PATIENT_MOTIF_PASSAGE_CODE = "sau_motif_passage_code";
	private static final String PATIENT_CLASSIF_CCMU_PASSAGE_CODE = "sau_classif_ccmu_passage_code";
	private static final String PATIENT_TIME_SORTIE_SAU = "sau_date_time_sortie_pat_sau";
	private static final String PATIENT_ORIENTATION = "sau_orientation_patient_code";
	private static final String PATIENT_MOYEN_TRANSPORT_SORTIE = "sau_pat_mode_sortie";
	private static final String PATIENT_DESTINATION_CODE = "sau_destination_code";
	private static final String PATIENT_PASSAGE_ID = "sau_passage_id";
	private static final String PATIENT_CHARG_ID = "sau_charg_id";

	private static final DateFormat xmlDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static final DateFormat xmlDateFormatEntree = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat xmlDateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static final DateFormat xmlPatientDateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private static final DateFormat xmlDateNaissance = new SimpleDateFormat("dd/MM/yyyy");

	private RpuConnector transfo;

	private Date dateDebut;
	private Date dateFin;
	private String finessFilter;
	private int orderFilter;
	private String outpuFile;

	private List<PATIENT> patients;
	private List<DIAGNOSTIC> diags;
	private List<ACTE> actes;

	public RunRpuConnector(IRepositoryContext repositoryCtx, Transformation transformation, int bufferSize) {
		super(repositoryCtx, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		this.transfo = (RpuConnector) getTransformation();

		String stringDateDebut = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getDateDebut());
		dateDebut = parseDate(stringDateDebut);

		String stringDateFin = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getDateFin());
		dateFin = parseDate(stringDateFin);

		finessFilter = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getFinessFilter());

		String stringOrder = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getOrderFilter());
		orderFilter = Integer.parseInt(stringOrder);

		String sqlPatient = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getSqlPatient());
		patients = getPatients(sqlPatient);

		String sqlDiags = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getSqlDiag());
		diags = getDiagnostics(sqlDiags);

		String sqlActes = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getSqlActes());
		actes = getActes(sqlActes);

		outpuFile = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getOutputFile());
	}

	private Date parseDate(String stringDate) throws ParseException {
		return xmlDateFormatEntree.parse(stringDate);
	}

	@Override
	public void performRow() throws Exception {
		String dateDebutStr = xmlDateFormat.format(dateDebut);
		String dateFinStr = xmlDateFormat.format(dateFin);

		ObjectFactory factory = new ObjectFactory();

		ETABLISSEMENT etab = factory.createETABLISSEMENT();
		etab.setFINESS(finessFilter);
		etab.setORDRE(orderFilter);
		etab.setEXTRACT(xmlDateTimeFormat.format(new Date()));
		etab.setDATEDEBUT(dateDebutStr);
		etab.setDATEFIN(dateFinStr);

		PASSAGES passage = factory.createPASSAGES();

		int passageCount = 0;
		for (PATIENT pat : patients) {
			buildPatient(factory, pat);
			passage.addPATIENT(pat);

			passageCount++;
		}

		OSCOUR syrius = factory.createOSCOUR();
		syrius.setETABLISSEMENT(etab);
		syrius.setPASSAGES(passage);

		saveFile(syrius, passageCount);
	}

	private void buildPatient(ObjectFactory factory, PATIENT patient) throws Exception {
		List<ACTE> actes = getActes(this.actes, patient);
		if (actes != null) {
			LISTEACTES lstActes = factory.createLISTEACTES();
			for (ACTE acte : actes) {
				lstActes.add(acte.getValue());
			}
			patient.setLISTEACTES(lstActes);
		}

		List<DIAGNOSTIC> diags = getDiagnostics(this.diags, patient);
		if (diags != null) {
			LISTEDA lstDiagAss = factory.createLISTEDA();
			for (DIAGNOSTIC diag : diags) {
				if (diag.getType().equals(Type.PRINCIPAL)) {
					patient.setDP(diag.getValue());
				}
				else {
					lstDiagAss.add(diag.getValue());
				}
			}
			patient.setLISTEDA(lstDiagAss);
		}
	}

	private void saveFile(OSCOUR oscour, int passageCount) throws Exception {
		JAXBContext jaxbContext = null;
		try {
			jaxbContext = JAXBContext.newInstance(OSCOUR.class);
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}

		Marshaller jaxbMarshaller = null;
		try {
			jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		jaxbMarshaller.marshal(oscour, bos);

		String result = bos.toString("UTF-8");

		File folderFile = new File(outpuFile);
		if (!folderFile.exists()) {
			if (!folderFile.mkdirs()) {
				throw new Exception("Unable to create folder at " + outpuFile);
			}
		}
		else if (!folderFile.isDirectory()) {
			throw new Exception(outpuFile + " is not a folder.");
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");

		File file = new File(folderFile, "O" + finessFilter + "_" + orderFilter + "_" + dateFormat.format(new Date()) + ".xml"); //$NON-NLS-1$
		file.createNewFile();

		FileOutputStream out = new FileOutputStream(file);
		IOWriter.write(IOUtils.toInputStream(result, "UTF-8"), out, true, false);
		out.close();

		setEnd();

	}

	private List<PATIENT> getPatients(String sqlQuery) throws Exception {
		List<PATIENT> patients = new ArrayList<PATIENT>();

		DataBaseConnection con = (DataBaseConnection) transfo.getServer().getCurrentConnection(null);
		Statement stmt = null;
		ResultSet rs = null;
		try {
			if (!con.isOpened()) {
				con.connect(getTransformation().getDocument());
			}

			stmt = con.getSocket(getTransformation().getDocument()).createStatement();
			rs = stmt.executeQuery(sqlQuery);

			while (rs.next()) {
				String cp = rs.getString(PATIENT_CP);
				String commune = rs.getString(PATIENT_COMMUNE);
				Date dateNaissance = rs.getDate(PATIENT_DATE_NAISSANCE);
				String sexe = rs.getString(PATIENT_SEXE);
				Timestamp timePremContact = rs.getTimestamp(PATIENT_TIME_PREM_CONTACT);
				int modeEntree = rs.getInt(PATIENT_MODE_ENTREE);
				Integer provenance = rs.getString(PATIENT_PROV) != null ? Integer.parseInt(rs.getString(PATIENT_PROV)) : null;
				String moyTransportArriv = rs.getString(PATIENT_MOY_TRANS_ARRIV_CODE);
				String transportMedCode = rs.getString(PATIENT_TRANSPORT_MED_CODE);
				String motifPassageCode = rs.getString(PATIENT_MOTIF_PASSAGE_CODE);
				String classifPassageCode = rs.getString(PATIENT_CLASSIF_CCMU_PASSAGE_CODE);
				Timestamp timeSortieSAU = rs.getTimestamp(PATIENT_TIME_SORTIE_SAU);
				String orientation = rs.getString(PATIENT_ORIENTATION);
				int moyTransportSortie = rs.getInt(PATIENT_MOYEN_TRANSPORT_SORTIE);
				Integer destinationCode = rs.getString(PATIENT_DESTINATION_CODE) != null ? Integer.parseInt(rs.getString(PATIENT_DESTINATION_CODE)) : null;
				String entId = rs.getString(PATIENT_PASSAGE_ID);
				String charId = rs.getString(PATIENT_CHARG_ID);

				String naissance = xmlDateNaissance.format(dateNaissance);
				String entree = xmlPatientDateTimeFormat.format(timePremContact);
				String sortie = xmlPatientDateTimeFormat.format(timeSortieSAU);
				
				PATIENT patient = new PATIENT();
				patient.setCharId(charId);
				patient.setCP(cp);
				patient.setCOMMUNE(commune);
				patient.setNAISSANCE(naissance);
				patient.setDESTINATION(destinationCode);
				patient.setEntId(entId);
				patient.setENTREE(entree);
				patient.setGRAVITE(classifPassageCode);
				patient.setMODEENTREE(modeEntree);
				patient.setMODESORTIE(moyTransportSortie);
				patient.setMOTIF(motifPassageCode);
				patient.setORIENT(orientation);
				patient.setPROVENANCE(provenance);
				patient.setSEXE(sexe);
				patient.setSORTIE(sortie);
				patient.setTRANSPORT(moyTransportArriv);
				patient.setTRANSPORTPEC(transportMedCode);

				patients.add(patient);
			}
		} catch (Exception e) {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			throw e;
		}

		return patients;
	}

	private List<DIAGNOSTIC> getDiagnostics(String sqlQuery) throws Exception {
		List<DIAGNOSTIC> diags = new ArrayList<DIAGNOSTIC>();

		DataBaseConnection con = (DataBaseConnection) transfo.getServer().getCurrentConnection(null);
		Statement stmt = null;
		ResultSet rs = null;
		try {
			if (!con.isOpened()) {
				con.connect(getTransformation().getDocument());
			}

			stmt = con.getSocket(getTransformation().getDocument()).createStatement();
			rs = stmt.executeQuery(sqlQuery);

			while (rs.next()) {

				String charId = rs.getString(DIAG_CHG_ID);
				String entId = rs.getString(DIAG_ENT_ID);
				String value = rs.getString(DIAG_CODE);

				boolean principal = rs.getBoolean(DIAG_PRINCIPAL);
				Type type = Type.ASSOCIEE;
				if (principal) {
					type = Type.PRINCIPAL;
				}

				diags.add(new DIAGNOSTIC(charId, entId, type, value));
			}
		} catch (Exception e) {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			throw e;
		}

		return diags;
	}

	private List<ACTE> getActes(String sqlQuery) throws Exception {
		List<ACTE> actes = new ArrayList<ACTE>();

		DataBaseConnection con = (DataBaseConnection) transfo.getServer().getCurrentConnection(null);
		Statement stmt = null;
		ResultSet rs = null;
		try {
			if (!con.isOpened()) {
				con.connect(getTransformation().getDocument());
			}

			stmt = con.getSocket(getTransformation().getDocument()).createStatement();
			rs = stmt.executeQuery(sqlQuery);

			while (rs.next()) {

				String charId = rs.getString(ACTE_CHG_ID);
				String entId = rs.getString(ACTE_ENT_ID);
				String value = rs.getString(ACTE_CODE);

				actes.add(new ACTE(charId, entId, value));
			}
		} catch (Exception e) {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			throw e;
		}

		return actes;
	}

	private List<DIAGNOSTIC> getDiagnostics(List<DIAGNOSTIC> diags, final PATIENT patient) {
		Predicate<DIAGNOSTIC> diagPatientFilter = new Predicate<DIAGNOSTIC>() {
			public boolean apply(DIAGNOSTIC diag) {
				return diag.getCharId().equals(patient.getCharId()) && diag.getEntId().equals(patient.getEntId());
			}
		};

		return Lists.newArrayList(Iterables.filter(diags, diagPatientFilter));
	}

	private List<ACTE> getActes(List<ACTE> actes, final PATIENT patient) {
		Predicate<ACTE> actePatientFilter = new Predicate<ACTE>() {
			public boolean apply(ACTE acte) {
				return acte.getCharId().equals(patient.getCharId()) && acte.getEntId().equals(patient.getEntId());
			}
		};

		return Lists.newArrayList(Iterables.filter(actes, actePatientFilter));
	}

	@Override
	public void releaseResources() {
		// Nothing to do here

	}

}
