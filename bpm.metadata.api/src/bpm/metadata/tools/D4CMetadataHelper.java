package bpm.metadata.tools;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import bpm.metadata.MetaData;
import bpm.metadata.MetaDataBuilder;
import bpm.metadata.digester.MetaDataDigester;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.SQLBusinessTable;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.sql.FactorySQLDataSource;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;
import bpm.vanilla.platform.core.utils.CkanHelper;

public class D4CMetadataHelper {

	public static String createMetadata(String ckanUrl, String ckanApi, String ckanOrg, String dbUrl, String dbLogin, String dbPass, String datasetString, boolean update, int id, IRepositoryApi sock) throws Exception {

		System.out.println("url " + ckanUrl);
		System.out.println("api " + ckanApi);
		System.out.println("org " + ckanOrg);
		System.out.println("dburl " + dbUrl);
		System.out.println("login " + dbLogin);
		System.out.println("passw " + dbPass);
		System.out.println("datasetlist " + datasetString);

		String[] datasets = datasetString.split(";");
		List<String> datasetList = Arrays.asList(datasets);

		CkanHelper helper = new CkanHelper(ckanUrl, ckanOrg, ckanApi, "", false);
		List<CkanPackage> packs = helper.getCkanPackages();
		
		MetaData fmdt = new MetaData();
		SQLDataSource sds = null;
		BusinessModel model = null;
		BusinessPackage pa = null;
		if(update) {
			String xml = sock.getRepositoryService().loadModel(sock.getRepositoryService().getDirectoryItem(id));
			MetaDataDigester dig = new MetaDataDigester(IOUtils.toInputStream(xml, "UTF-8"), new MetaDataBuilder(sock)); //$NON-NLS-1$
			fmdt = dig.getModel(sock);
			sds = (SQLDataSource) fmdt.getDataSources().iterator().next();
			
			for(IBusinessModel m : fmdt.getBusinessModels()) {
				if(m.getName().equals("D4CModel")) {
					model = (BusinessModel) m;
					for(IBusinessPackage p : m.getBusinessPackages("none")) {
						if(p.getName().equals("D4CPack")) {
							pa = (BusinessPackage) p;
							break;
						}
					}
					break;
				}
			}
		}
		else {
			SQLConnection scon = new SQLConnection();
			scon.setName("Default");
			scon.setPassword(dbPass);
			scon.setUsername(dbLogin);
			scon.setFullUrl(dbUrl);
			scon.setUseFullUrl(true);
			scon.setDriverName("PostgreSql");

			sds = FactorySQLDataSource.getInstance().createDataSource(scon);
			sds.setName("Default");
			sds.getConnection().connect();
			fmdt.addDataSource(sds);

			sds.securizeConnection("Default", "System", true);
			
			model = new BusinessModel();
			model.setName("D4CModel");

			pa = new BusinessPackage();
			pa.setName("D4CPack");
			model.addBusinessPackage(pa);

			fmdt.addBusinessModel(model);
		}



		for(CkanPackage p : packs) {
//			System.out.println("Package : " + p.getId());
			if(datasetList.contains(p.getId())) {
//				System.out.println("Found");
				for(CkanResource res : p.getResources()) {
					if(res.getFormat().equalsIgnoreCase("csv")) {
						try {
							
							if(model.getBusinessTable(res.getName()) != null) {
								continue;
							}
							
							String stringfields = helper.getResourceFields(res.getId());
							if (stringfields.startsWith("200")) {
								stringfields = stringfields.substring(4, stringfields.length());
							}
							JSONObject json = new JSONObject(stringfields);
							JSONArray fields = json.getJSONObject("result").getJSONArray("fields");
							
							
							IDataStream str = sds.add(sds.getConnection().getTable(res.getId()));
							SQLBusinessTable bt = new SQLBusinessTable(res.getName());

							for(IDataStreamElement e : str.getElements()) {
								e.setOutputName(Locale.getDefault(), e.getName().substring(e.getName().lastIndexOf(".") + 1));
								bt.addColumn(e);
								
								for (int i = 0; i < fields.length(); i++) {
									JSONObject f = fields.getJSONObject(i);
									if(f.getString("id").equals(e.getOuputName())) {
										System.out.println(f);
										try {
											String notes = f.getJSONObject("info").getString("notes");
											setTypesOnDatastreamElement(e, notes);
											if(e.getD4cTypes().isFacette() || e.getD4cTypes().isFacetteMultiple()) {
												Prompt pr = new Prompt();
												pr.setOrigin(e);
												pr.setOriginDataStreamElementName(e.getName());
												pr.setOriginDataStreamName(e.getDataStream().getName());
												pr.setName("pmpt_" + e.getName());
												pr.setOperator("=");
												model.addResource(pr);
												pa.addResource(pr);
											}
										} catch(Exception e1) {
										}
									}
									
								}
								
							}
							str.setName(res.getName().replace(".csv", ""));
							model.addBusinessTable(bt);
							pa.addBusinessTable(bt);

							break;
						} catch(Exception e) {
//							System.out.println(p.getName());
//							System.out.println(res.getName());
//							System.out.println(res.getId());
							e.printStackTrace();
						}
					}

				}
			}
		}

		SecurityHelper.grantGlobal(fmdt, "System", true);

		String xml = fmdt.getXml(false);

		return xml;

	}

	private static void setTypesOnDatastreamElement(IDataStreamElement e, String notes) {
//		System.out.println(e.getName());
//		System.out.println(notes);
		if(notes.contains("<!--facet-->")) {
			e.getD4cTypes().setFacette(true);
		}
		if(notes.contains("<!--table-->")) {
			e.getD4cTypes().setTableau(true);
		}
		if(notes.contains("<!--tooltip-->")) {
			e.getD4cTypes().setInfobulle(true);
		}
		if(notes.contains("<!--disjunctive-->")) {
			e.getD4cTypes().setFacetteMultiple(true);
		}
		if(notes.contains("<!--startDate-->")) {
			e.getD4cTypes().setDateDebut(true);
		}
		if(notes.contains("<!--endDate-->")) {
			e.getD4cTypes().setDateFin(true);
		}
		if(notes.contains("<!--timeserie_precision-->")) {
			e.getD4cTypes().setDateHeure(true);
		}
		if(notes.contains("<!--date-->")) {
			e.getD4cTypes().setDatePonctuelle(true);
		}
		if(notes.contains("<!--date_timeline-->")) {
			e.getD4cTypes().setFriseDate(true);
		}
		if(notes.contains("<!--descr_for_timeLine-->")) {
			e.getD4cTypes().setFriseDescription(true);
		}
		if(notes.contains("<!--images-->")) {
			e.getD4cTypes().setImages(true);
		}
		if(notes.contains("<!--wordcount-->")) {
			e.getD4cTypes().setNuageDeMot(true);
		}
		if(notes.contains("<!--wordcountNumber-->")) {
			e.getD4cTypes().setNuageDeMotNombre(true);
		}
		if(notes.contains("<!--sortable-->")) {
			e.getD4cTypes().setTri(true);
		}
	}

}
