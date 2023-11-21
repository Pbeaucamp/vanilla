package bpm.gateway.runtime2.transformation.tsbn;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.net.ssl.SSLContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.log4j.Logger;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.tsbn.atih.ACTE;
import bpm.gateway.core.tsbn.atih.DIAGNOSTIC;
import bpm.gateway.core.tsbn.atih.DIAGNOSTIC.Type;
import bpm.gateway.core.tsbn.atih.ETABLISSEMENT;
import bpm.gateway.core.tsbn.atih.LISTEACTES;
import bpm.gateway.core.tsbn.atih.LISTEDA;
import bpm.gateway.core.tsbn.atih.ObjectFactory;
import bpm.gateway.core.tsbn.atih.PASSAGES;
import bpm.gateway.core.tsbn.atih.PATIENT;
import bpm.gateway.core.tsbn.atih.SYRIUS;
import bpm.gateway.core.tsbn.syrius.SyriusConnector;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.utils.IOWriter;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class RunSyriusConnector extends RuntimeStep {
	
	private static final String DIAG_CHG_ID = "dgn_chg_id";
	private static final String DIAG_ENT_ID = "dgn_ent_id";
	private static final String DIAG_CODE = "dgn_code";
	private static final String DIAG_PRINCIPAL = "dgn_principal";

	private static final String ACTE_CHG_ID = "act_chg_id";
	private static final String ACTE_ENT_ID = "act_ent_id";
	private static final String ACTE_CODE = "act_code";

	private static final String PATIENT_CP = "sau_patient_cp_residence";
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
	private static final DateFormat xmlDateFormatEntree2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final DateFormat xmlDateFormatEntreeSortie = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private static final DateFormat xmlDateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private SyriusConnector transfo;

	private int year;
	private int period;
	private String finessFilter;
	private int orderFilter;

	private List<PATIENT> patients;
	private List<DIAGNOSTIC> diags;
	private List<ACTE> actes;
	private HashMap<String, String> mappingCpCodeGeo = new HashMap<String, String>();

	public RunSyriusConnector(IRepositoryContext repositoryCtx, Transformation transformation, int bufferSize) {
		super(repositoryCtx, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		this.transfo = (SyriusConnector) getTransformation();

		String stringYear = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getYear());
		year = Integer.parseInt(stringYear);

		String stringPeriod = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getPeriod());
		period = Integer.parseInt(stringPeriod);

		finessFilter = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getFinessFilter());

		String stringOrder = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getOrderFilter());
		orderFilter = Integer.parseInt(stringOrder);

		String sqlPatient = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getSqlPatient());
		patients = getPatients(sqlPatient);

		String sqlDiags = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getSqlDiag());
		diags = getDiagnostics(sqlDiags);

		String sqlActes = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getSqlActes());
		actes = getActes(sqlActes);
	}

	@Override
	public void performRow() throws Exception {
		// create the dates
		Date deb = new Date();
		deb.setDate(1);
		deb.setMonth(0);
		deb.setYear(year - 1900);
		String dateDebut = xmlDateFormat.format(deb);

		Date end = new Date();
		end.setDate(1);
		end.setMonth(period - 1);
		end.setYear(year - 1900);
		Calendar cal = Calendar.getInstance();
		cal.setTime(end);
		int day = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		end.setDate(day);
		String dateFin = xmlDateFormat.format(end);

		ObjectFactory factory = new ObjectFactory();
		
		ETABLISSEMENT etab = factory.createETABLISSEMENT();
		etab.setFINESS(finessFilter);
		etab.setORDRE(orderFilter);
		etab.setEXTRACT(xmlDateTimeFormat.format(new Date()));
		etab.setDATEDEBUT(dateDebut);
		etab.setDATEFIN(dateFin);

		PASSAGES passage = factory.createPASSAGES();
		
		int passageCount = 0;
		for(PATIENT pat : patients) {
			buildPatient(factory, pat);
			passage.addPATIENT(pat);
			
			passageCount++;
		}
		
		SYRIUS syrius = factory.createSYRIUS();
		syrius.setETABLISSEMENT(etab);
		syrius.setPASSAGES(passage);
		
		sendFile(syrius, passageCount);
	}

	private void buildPatient(ObjectFactory factory, PATIENT patient) throws Exception {
		// calculate age and codegeo
		String codeGeo = calculateCodeGeo(patient.getCp());

		List<ACTE> actes = getActes(this.actes, patient);
		if(actes != null) {
			LISTEACTES lstActes = factory.createLISTEACTES();
			for(ACTE acte : actes) {
				lstActes.add(acte.getValue());
			}
			patient.setLISTEACTES(lstActes);
		}

		List<DIAGNOSTIC> diags = getDiagnostics(this.diags, patient);
		if(diags != null) {
			LISTEDA lstDiagAss = factory.createLISTEDA();
			for(DIAGNOSTIC diag : diags) {
				if(diag.getType().equals(Type.PRINCIPAL)) {
					patient.setDP(diag.getValue());
				}
				else {
					lstDiagAss.add(diag.getValue());
				}
			}
			patient.setLISTEDA(lstDiagAss);
		}
		
		// If one of them is unparsable, we do some treatment in catch
		Date dateNaissance = null;
		Date dateEntree = null;
		try {
			dateEntree = xmlDateFormatEntree2.parse(patient.getENTREE());
			dateNaissance = xmlDateFormatEntree.parse(patient.getDateNaissance());

			int age = dateEntree.getYear() - dateNaissance.getYear();
			if (age == 0) {
				long diffJours = (dateEntree.getTime() - dateNaissance.getTime()) / (1000 * 3600 * 24);
				patient.setAGEJOURS(new Long(diffJours).intValue());
			}
			else {
				patient.setAGEJOURS(0);
			}
			patient.setAGE(age);
		} catch (Exception e) {
			// if the dateEntree is null, we stop the big else, we put default
			// values in age
			if (dateEntree == null) {
				throw e;
			}
			else {
				patient.setAGEJOURS(0);
				patient.setAGE(0);

				e.printStackTrace();
			}
		}

		//We transform the date of ENTREE and SORTIE
		try {
			if(dateEntree != null) {
				patient.setENTREE(xmlDateFormatEntreeSortie.format(dateEntree));
			}
			
			Date dateSortie = null;
			if(patient.getSORTIE() != null && !patient.getSORTIE().isEmpty()) {
				dateSortie = xmlDateFormatEntree2.parse(patient.getSORTIE());
				
				if(dateSortie != null) {
					patient.setSORTIE(xmlDateFormatEntreeSortie.format(dateSortie));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (codeGeo == null) {
			patient.setCODEGEO(null);
			System.out.println("codegeo -> " + patient.getCp());
		}
		else {
			patient.setCODEGEO(codeGeo);
		}
	}

	private void sendFile(SYRIUS syrius, int passageCount) throws Exception {

		// URL du service ATIH

		String uploadUrl = transfo.getServiceUrl();

		// Paramètres HTTP inclus dans l'appel

		String userId = transfo.getUserId();

		String userPwd = transfo.getPassword();

		String year = String.valueOf(this.year);

		String period = String.valueOf(this.period);
		String finess = String.valueOf(this.finessFilter);

		String rpuCount = String.valueOf(passageCount);

		String check = "1"; // 1:sans transmission de fichier; 0:avec fichier

		String ordre = String.valueOf(this.orderFilter);

		JAXBContext jaxbContext = null;
		try {
			jaxbContext = JAXBContext.newInstance(SYRIUS.class);
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}
		
		Marshaller jaxbMarshaller = null;
		try {
			jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		jaxbMarshaller.marshal(syrius, bos);
		
		String result = bos.toString("UTF-8");

		File zipFile = new File("temp/test.zip");
		if (!zipFile.exists()) {
			zipFile.createNewFile();
		}

		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");

		ZipEntry entry = new ZipEntry(finessFilter + "_" + orderFilter + dateFormat.format(new Date()) + ".rpu.xml"); //$NON-NLS-1$
		out.putNextEntry(entry);
		IOWriter.write(IOUtils.toInputStream(result, "UTF-8"), out, true, false);

		out.close();

		// //Create file
		String dataFilePath = "temp/test.zip";

		// XXX comment this if you don't want to send the file (don't forget to uncomment)
		String checkResult = sendTheFile(check, userId, userPwd, year, period, finess, rpuCount, ordre, dataFilePath, uploadUrl);
		if (checkResult.contains("OK")) {
			check = "0";
			checkResult = sendTheFile(check, userId, userPwd, year, period, finess, rpuCount, ordre, dataFilePath, uploadUrl);
			System.out.println("Check with file result : " + checkResult);
			Logger.getLogger(this.getClass()).debug("Check with file result : " + checkResult);
		}
		else {
			throw new Exception("The check without file fails because : " + checkResult);
		}
//		// XXX

		setEnd();

	}

	private String sendTheFile(String check, String userId, String userPwd, String year, String period, String finess, String rpuCount, String ordre, String dataFilePath, String uploadUrl) throws Exception {
//		MultipartEntity multipart = new MultipartEntity();
		MultipartEntityBuilder multipart = MultipartEntityBuilder.create(); 
		
		multipart.addPart("userId", new StringBody(userId));
		multipart.addPart("userPwd", new StringBody(userPwd));
		multipart.addPart("year", new StringBody(year));
		multipart.addPart("period", new StringBody(period));
		multipart.addPart("finess", new StringBody(finess));
		multipart.addPart("rpuCount", new StringBody(rpuCount));
		multipart.addPart("ordre", new StringBody(ordre));
		multipart.addPart("check", new StringBody(check));
		if (check.equals("0")) {
			FileBody fileBody = new FileBody(new File(dataFilePath), "application/octect-stream");
			multipart.addPart("theFile", fileBody);
		}

		HttpPost post = new HttpPost(uploadUrl);
		post.setEntity(multipart.build());

		
		

//		SSLContext sslContext = SSLContexts.createDefault();
//
//		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
//		        new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"},
//		        null,
//		        new NoopHostnameVerifier());
//
//		CloseableHttpClient client = HttpClients.custom()
//		        .setSSLSocketFactory(sslsf)
//		        .build();
		
        SSLContext sslContext = null;//new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build();

        CloseableHttpClient client = HttpClients.custom()
                  .setSSLContext(sslContext)
                  .setSSLHostnameVerifier(new NoopHostnameVerifier())
                  .build();
		
		
		
		//HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(post);
		HttpEntity entity = response.getEntity();

		System.out.println(response.getStatusLine().toString());
		BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));

		String line = null;

		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			sb.append(line);

			sb.append("\n");

		}

		reader.close();

		Logger.getLogger(this.getClass()).debug(sb.toString());

		return sb.toString();
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
				String dateNaissance = rs.getString(PATIENT_DATE_NAISSANCE);
				String sexe = rs.getString(PATIENT_SEXE);
				String timePremContact = rs.getString(PATIENT_TIME_PREM_CONTACT);
				int modeEntree = rs.getInt(PATIENT_MODE_ENTREE);
				int provenance = rs.getInt(PATIENT_PROV);
				String moyTransportArriv = rs.getString(PATIENT_MOY_TRANS_ARRIV_CODE);
				String transportMedCode = rs.getString(PATIENT_TRANSPORT_MED_CODE);
				String motifPassageCode = rs.getString(PATIENT_MOTIF_PASSAGE_CODE);
				String classifPassageCode = rs.getString(PATIENT_CLASSIF_CCMU_PASSAGE_CODE);
				String timeSortieSAU = rs.getString(PATIENT_TIME_SORTIE_SAU);
				String orientation = rs.getString(PATIENT_ORIENTATION);
				int moyTransportSortie = rs.getInt(PATIENT_MOYEN_TRANSPORT_SORTIE);
				int destinationCode = rs.getInt(PATIENT_DESTINATION_CODE);
				String entId = rs.getString(PATIENT_PASSAGE_ID);
				String charId = rs.getString(PATIENT_CHARG_ID);
				
				PATIENT patient = new PATIENT();
				patient.setCharId(charId);
				patient.setCp(cp);
				patient.setDateNaissance(dateNaissance);
				patient.setDESTINATION(destinationCode);
				patient.setEntId(entId);
				patient.setENTREE(timePremContact);
				patient.setGRAVITE(classifPassageCode);
				patient.setMODEENTREE(modeEntree);
				patient.setMODESORTIE(moyTransportSortie);
				patient.setMOTIF(motifPassageCode);
				patient.setORIENT(orientation);
				patient.setPROVENANCE(provenance);
				patient.setSEXE(sexe);
				patient.setSORTIE(timeSortieSAU);
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
				if(principal) {
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

	private String calculateCodeGeo(String cp) throws Exception {

		if (mappingCpCodeGeo.isEmpty()) {
			DataBaseConnection con = (DataBaseConnection) transfo.getServer().getCurrentConnection(null);
			Statement stmt = null;
			ResultSet rs = null;
			try {
				if (!con.isOpened()) {
					con.connect(getTransformation().getDocument());
				}

				stmt = con.getSocket(getTransformation().getDocument()).createStatement();
				rs = stmt.executeQuery(transfo.getSqlGeo());

				while (rs.next()) {

					mappingCpCodeGeo.put(rs.getString(transfo.getColumnCp()), rs.getString(transfo.getColumnCodeGeo()));
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
		}
		String codeGeo = mappingCpCodeGeo.get(cp);

		if (codeGeo == null) {
			if (cp == null) {
				codeGeo = "99999";
			}
			else {
				try {
					String cp2 = cp.substring(0, 2);
					if (Integer.parseInt(cp2) < 96 && Integer.parseInt(cp2) > 0) {
						codeGeo = cp2 + "999";
					}
					else {
						codeGeo = "99999";
					}
				} catch (Exception e) {
					try {
						String cp2 = cp.substring(0, 2);
						if (cp2.toLowerCase().equals("2a") || cp2.toLowerCase().equals("2b")) {
							codeGeo = cp2 + "999";
						}
						else {
							codeGeo = "99999";
						}
					} catch (Exception ex) {
						codeGeo = "99999";
					}

				}
			}
		}

		return codeGeo;
	}

	@Override
	public void releaseResources() {
		// Nothing to do here

	}

}
