package bpm.vanilla.platform.core.runtime.ged.solr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.tika.Tika;

import bpm.vanilla.platform.core.beans.ged.Definition;
import bpm.vanilla.platform.core.beans.ged.DocCat;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.ged.Security;
import bpm.vanilla.platform.core.beans.ged.StoredField;
import bpm.vanilla.platform.core.beans.ged.constant.Formats;
import bpm.vanilla.platform.core.beans.ged.constant.RuntimeFields;
import bpm.vanilla.platform.core.components.ged.GedIndexRuntimeConfig;
import bpm.vanilla.platform.core.components.ged.GedSearchRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.runtime.dao.ged.CategoryDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.DefinitionDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.DocCatDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.DocumentDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.SecurityDAO;
import bpm.vanilla.platform.core.runtime.dao.ged.StoredFieldDAO;
import bpm.vanilla.platform.core.runtime.ged.hdfs.HdfsHelper;
import bpm.vanilla.platform.core.runtime.ged.r.RServer;
import bpm.vanilla.platform.logging.IVanillaLogger;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
//import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;

/**
 * Represents a SolrServer.
 * 
 * What we need to do is making it support multiple cores for clustering. The
 * cores are configured in a file named solr.xml (use the property
 * "solr.solr.solrxml.path" to find it).
 * 
 * @author Marc Lanquetin
 * 
 */
public class SolrServer {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	private static SolrServer server;
	//private org.apache.solr.client.solrj.SolrServer solrServer;
	private org.apache.solr.client.solrj.SolrClient solrServer;

	private SolrServer() {
		try {

			String embedded = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.solr.embedded");

			if (embedded != null && Boolean.parseBoolean(embedded)) {
				String configPath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.solr.configpath");
				File f = new File(configPath, "solr.xml");
//				CoreContainer.Initializer initializer = new CoreContainer.Initializer();
//				CoreContainer coreContainer = initializer.initialize();
//				CoreContainer coreContainer = new CoreContainer();
//			
//				coreContainer.load(configPath, f);
//				solrServer = new EmbeddedSolrServer(coreContainer, "");
			}
			else {
				String url = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.solr.url");
//				String url = "http://localhost:8983/solr/collection1";
				//solrServer = new CommonsHttpSolrServer(url);
				solrServer = new HttpSolrClient(url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static SolrServer getInstance() {
		//if (server == null) {
			server = new SolrServer();
		//}
		return server;
	}

	/**
	 * Search in solr index. May search in clustered solr servers if vanilla is
	 * configured for it.
	 * 
	 * @param config
	 * @param logger
	 * @param definitionDao
	 * @param documentDao
	 * @param securityDao
	 * @return
	 * @throws Exception
	 */
	public List<GedDocument> search(GedSearchRuntimeConfig config, IVanillaLogger logger, DefinitionDAO definitionDao, DocumentDAO documentDao, SecurityDAO securityDao) throws Exception {

		boolean showAllDocument = false;
		try {
			showAllDocument = Boolean.parseBoolean(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_GED_SHOW_ALL_DOCUMENT));
			logger.info("The property " + VanillaConfiguration.P_GED_SHOW_ALL_DOCUMENT + " is set to " + showAllDocument);
		} catch (Exception e) {
			logger.info("No property " + VanillaConfiguration.P_GED_SHOW_ALL_DOCUMENT + " in the vanilla.properties file. We put the value to false.");
			showAllDocument = false;
		}

		// create the query
		String queryText = generateQuery(config, definitionDao);

		List<GedDocument> defs = executeSolrQuery(queryText, logger, documentDao);

		logger.info("Solr return " + defs.size() + " documents");

		List<Integer> autorized = securityDao.findAuthorizedDocumentIds(config.getGroupId(), config.getRepositoryId());
		List<GedDocument> res = new ArrayList<GedDocument>();

		for (GedDocument d : defs) {
			boolean granted = false;
			for (Integer i : autorized) {
				if (i != null && i.intValue() == d.getId()) {
					granted = true;
					break;
				}
			}

			if (showAllDocument) {
				if (granted) {
					logger.debug("Ged DocDef with id = " + d.getId() + " has been included, allowed by security");
				}
				else {
					logger.debug("Ged DocDef with id = " + d.getId() + " has been included, however the document is not granted due to security restrictions");
				}
				d.setGranted(granted);
				res.add(d);
			}
			else {
				if (granted) {
					logger.debug("Ged DocDef with id = " + d.getId() + " has been included, allowed by security");
					d.setGranted(granted);
					res.add(d);
				}
				else {
					logger.debug("Ged DocDef with id = " + d.getId() + " has been filtered out due to security restrictions");
				}
			}

		}
		return res;
	}

	private String generateQuery(GedSearchRuntimeConfig config, DefinitionDAO definitionDao) {
		String operator = config.isAllOccurences() ? " AND " : " OR ";
		boolean first = true;
		String querystr = "";

		if (config.getKeywords() != null) {
			for (String k : config.getKeywords()) {
				if (first) {
					querystr += " (content:*" + k.toLowerCase() + "* ";
					querystr += " OR content:*" + k + "* ";
					querystr += " OR title:*" + k + "* ";
					querystr += " OR title:*" + k.toLowerCase() + "*) ";
					first = false;
				}
				else {
					querystr += " " + operator;
					querystr += " (content:*" + k + "* ";
					querystr += " OR title:*" + k + "*) ";

				}
			}

			first = config.getKeywords().isEmpty();
		}

		for (String s : config.getProperties().getKeys()) {
			Definition d = definitionDao.findByName(s);
			if (config.getProperties().isMultivalued(s)) {
				for (String value : config.getProperties().getValuesForField(s)) {
					if (first) {
						if (d != null && d.analized())
							value = value.toLowerCase();

						querystr += s + ":" + value + "";
						first = false;
					}
					else {
						if (d != null && d.analized())
							value = value.toLowerCase();

						querystr += " " + operator + " " + s + ":" + value + "";
					}
				}
			}
			else {
				if (first) {
					String value = config.getProperties().getValueForField(s);
					if (d != null && d.analized())
						value = value.toLowerCase();

					querystr += s + ":" + value + "";
					first = false;
				}
				else {
					String value = config.getProperties().getValueForField(s);
					if (d != null && d.analized())
						value = value.toLowerCase();

					querystr += " " + operator + " " + s + ":" + value + "";
				}
			}
		}
		return querystr;
	}

	private List<GedDocument> executeSolrQuery(String queryText, IVanillaLogger logger, DocumentDAO documentDao) throws Exception {
		logger.info("query executed : " + queryText);
		SolrQuery query = new SolrQuery(queryText);

		// loop on the matching results
		QueryResponse solrResponse = solrServer.query(query);
		SolrDocumentList results = solrResponse.getResults();

		List<DocumentVersion> res = new ArrayList<DocumentVersion>();

		logger.debug("Found " + results.getNumFound() + " hits.");
		for (int i = 0; i < results.getNumFound(); i++) {
			SolrDocument solrDoc = results.get(i);

			String docVersionId = (String) solrDoc.getFieldValue(RuntimeFields.VERSION.getName());
			try {
				DocumentVersion version = documentDao.getDocumentVersionDAO().getDocumentVersionById(new Integer(docVersionId));

				// try to get summary
				try {
					String summaryField = (String) solrDoc.getFieldValue(RuntimeFields.SUMMARY.getName());
					version.setSummary(summaryField);
				} catch (Exception e) {
					logger.info("Failed to get summary for the document");
				}

				res.add(version);
			} catch (NumberFormatException e) {
				String msg = "NumberFormatException while searching for doc with id " + docVersionId;
				logger.error(msg, e);
			} catch (Exception e) {
				String msg = "Exception while searching for doc with id " + docVersionId + ", reason " + e.getMessage();
				logger.error(msg, e);
			}
		}

		return documentDao.getDocumentsForVersions(res);
	}

	/**
	 * Used to index, create the file in ged directory. Also add document,
	 * security, fields, categories in the vanilla database.
	 * 
	 * @param runtimeConfig
	 * @param is
	 * @param logger
	 * @param documentDao
	 * @param securityDao
	 * @param definitionDao
	 * @param storedFieldDao
	 * @param categoryDao
	 * @param docCatDao
	 * @return
	 * @throws Exception
	 */
	public synchronized Integer index(GedIndexRuntimeConfig runtimeConfig, InputStream is, IVanillaLogger logger, DocumentDAO documentDao, SecurityDAO securityDao, DefinitionDAO definitionDao, StoredFieldDAO storedFieldDao, CategoryDAO categoryDao, DocCatDAO docCatDao) throws Exception {

		try {
			logger.info("Indexing file using Solr");

			boolean useHdfs = Boolean.parseBoolean(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_HDFS_USE));
			String filePath;
			if (useHdfs) {
				filePath = HdfsHelper.addDocument(is, runtimeConfig.getFormat());
			}
			else {
				filePath = createFileInGed(is, logger, runtimeConfig);
			}

			GedDocument gdoc = null;
			DocumentVersion version = null;

			logger.info("Create document to index");
			if (runtimeConfig.getExistingDocId() != null) {
				gdoc = documentDao.findByPrimaryKey(runtimeConfig.getExistingDocId());
				version = documentDao.getDocumentVersionDAO().getDocumentVersionForDocumentAndVersion(gdoc, runtimeConfig.getVersion());

				if (version == null) {
					version = new DocumentVersion();
					version.setModificationDate(new Date());
					version.setModifiedBy(gdoc.getCreatedBy());
					version.setDocumentId(gdoc.getId());

					int maxVersion = 0;
					for (DocumentVersion v : gdoc.getDocumentVersions()) {
						if (v.getVersion() > maxVersion) {
							maxVersion = v.getVersion();
						}
					}
					version.setVersion(maxVersion + 1);
					version.setDocumentPath(filePath);
					version.setIsIndexed(1);

					if (runtimeConfig.getPeremptionDate() != null) {
						version.setPeremptionDate(runtimeConfig.getPeremptionDate());
					}

					gdoc.addDocumentVersion(version);

					documentDao.getDocumentVersionDAO().addVersion(version);
				}
				else {
					version.setModificationDate(new Date());
					version.setModifiedBy(gdoc.getCreatedBy());
					version.setDocumentPath(filePath);
					version.setIsIndexed(1);
					if (runtimeConfig.getPeremptionDate() != null) {
						version.setPeremptionDate(runtimeConfig.getPeremptionDate());
					}
					documentDao.getDocumentVersionDAO().updateVersion(version);
				}
			}
			else {
				gdoc = new GedDocument();
				gdoc.setName(runtimeConfig.getComProps().getValueForField(RuntimeFields.TITLE.getName()));
				gdoc.setCreationDate(new Date());
				gdoc.setCreatedBy(runtimeConfig.getUserId());

				version = new DocumentVersion();
				version.setVersion(1);
				version.setIsIndexed(1);
				version.setDocumentPath(filePath);
				version.setModificationDate(new Date());
				version.setModifiedBy(gdoc.getCreatedBy());
				if (runtimeConfig.getPeremptionDate() != null) {
					version.setPeremptionDate(runtimeConfig.getPeremptionDate());
				}

				gdoc.addDocumentVersion(version);
			}

			gdoc.setAccessCounter(0l);

			logger.info("Creating GedDocument to store in Vanilla database");
			int id = -1;
			if (runtimeConfig.getExistingDocId() != null) {
				id = gdoc.getId();
			}
			else {
				id = documentDao.save(gdoc);
				version.setDocumentId(id);
				if (runtimeConfig.getPeremptionDate() != null) {
					version.setPeremptionDate(runtimeConfig.getPeremptionDate());
				}
				version = documentDao.getDocumentVersionDAO().addVersion(version);
				gdoc.setId(id);
			}

			// index the document
			SolrInputDocument doc = new SolrInputDocument();

			logger.info("set document fields");
			addFieldsToDocument(gdoc, doc, runtimeConfig, definitionDao, storedFieldDao);

			logger.info("analyse document content");
			analyseDocumentContent(filePath, runtimeConfig, gdoc, doc, definitionDao, storedFieldDao, version);

			if (runtimeConfig.getExistingDocId() == null) {
				// apply vanilla security
				applyVanillaSecurity(runtimeConfig, gdoc, securityDao);

				// add document to category
				addDocumentToCategory(runtimeConfig, gdoc, docCatDao);
			}
			
			
			documentDao.getDocumentVersionDAO().update(version);
			
			solrServer.add(doc);
			solrServer.commit();
			
			Thread t = new Thread(){
				
				@Override
				public void run() {
					analyseDocumentKeywords();
					
				}
			};
			t.start();
			
			
			
			//analyseDocumentKeywords(String.valueOf(gdoc.getId()));

			return id;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * This method create the file in the ged directory and return the path of
	 * the file. The path will be <GedDirectory>/year/month/<document>.
	 * 
	 * @param is
	 * @param logger
	 * @param runtimeConfig
	 * @return
	 * @throws Exception
	 */
	private String createFileInGed(InputStream is, IVanillaLogger logger, GedIndexRuntimeConfig runtimeConfig) throws Exception {

		// Find the directory and file paths
		String gedRootDirectory = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_GED_INDEX_DIRECTORY);
		String documentRelativePath = File.separator + "ged_documents" + File.separator + Calendar.getInstance().get(Calendar.YEAR) + File.separator + Calendar.getInstance().get(Calendar.MONTH) + File.separator;
		String documentName = "document_" + Calendar.getInstance().getTimeInMillis() + "." + runtimeConfig.getFormat();

		String documentParentDirectory = gedRootDirectory + documentRelativePath;
		String filePath = documentParentDirectory + documentName;

		// create the directory if not existing
		logger.debug("creating ged directory " + documentParentDirectory);
		File _dir = new File(documentParentDirectory);
		if (!_dir.exists()) {
			boolean b = _dir.mkdirs();
			if (!b)
				throw new Exception(documentParentDirectory);
		}

		// create the document
		FileOutputStream fos = new FileOutputStream(filePath);
		byte buffer[] = new byte[512 * 1024];
		int nbLecture;

		while ((nbLecture = is.read(buffer)) != -1) {
			fos.write(buffer, 0, nbLecture);

		}
		is.close();
		fos.close();

		return documentRelativePath + documentName;
	}

	private void addDocumentToCategory(GedIndexRuntimeConfig runtimeConfig, GedDocument document, DocCatDAO docCatDao) {
		try {
			for (String c : runtimeConfig.getComProps().getValuesForField(RuntimeFields.CATEGORY.getName())) {
				int catId = new Integer(c);
				DocCat d = new DocCat();
				d.setCategoryId(catId);
				d.setDocumentId(document.getId());
				docCatDao.save(d);
			}
		} catch (Exception e) {
		}
	}

	private void applyVanillaSecurity(GedIndexRuntimeConfig runtimeConfig, GedDocument document, SecurityDAO securityDao) {
		Integer repositoryId = runtimeConfig.getRepositoryId();
		if (runtimeConfig.getGroupIds().isEmpty()) {
			// no group
			Security d = new Security();
			d.setDocumentId(document.getId());
			d.setRepositoryId(repositoryId);
			d.setUserId(runtimeConfig.getUserId());
			d.setGroupId(runtimeConfig.getGroupId());
			securityDao.save(d);
		}
		else {
			for (int g : runtimeConfig.getGroupIds()) {
				Security d = new Security();
				d.setDocumentId(document.getId());
				d.setRepositoryId(repositoryId);
				d.setUserId(runtimeConfig.getUserId());
				d.setGroupId(g);
				securityDao.save(d);
			}
		}
	}

	private static Formats findFormat(String extension) {
		for (Formats f : Formats.values()) {
			if (f.getExtension().equalsIgnoreCase(extension)) {
				return f;
			}
		}
		return Formats.UNKNOWN;
	}

	private void analyseDocumentContent(String fileIndexedPath, GedIndexRuntimeConfig runtimeConfig, GedDocument document, SolrInputDocument doc, DefinitionDAO definitionDao, StoredFieldDAO storedFieldDao, DocumentVersion version) {

		String gedRootDirectory = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_GED_INDEX_DIRECTORY);

		// find the content
		try {
			if (findFormat(runtimeConfig.getFormat()) != Formats.UNKNOWN) {

				boolean useHdfs = Boolean.parseBoolean(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_HDFS_USE));
				InputStream input;
				if (useHdfs) {
					input = new FileInputStream(new File("temp." + runtimeConfig.getFormat()));
				}
				else {
					input = new FileInputStream(new File(gedRootDirectory + fileIndexedPath));
				}

				Tika tika = new Tika();
				String content = tika.parseToString(input);
				doc.addField(RuntimeFields.CONTENT.getName(), content);
				
				////////// modif kevin ////////////
				doc.addField(RuntimeFields.TOKENS.getName(), content);
				
				////////////////////////////

				if (runtimeConfig.getComProps().getValueForField(RuntimeFields.SUMMARY.getName()) == null) {
					if (content.length() > 150) {
						content = content.replaceAll("\\t", " ");
						content = content.replaceAll("\\n", " ");
						content = content.replaceAll("     ", "");
						String summary = content.substring(0, 149);
						doc.addField(RuntimeFields.SUMMARY.getName(), summary);
						version.setSummary(summary);
					}
					else {
						doc.addField(RuntimeFields.SUMMARY.getName(), content);
						version.setSummary(content);
					}
				}
				else {
					doc.addField(RuntimeFields.SUMMARY.getName(), runtimeConfig.getComProps().getValueForField(RuntimeFields.SUMMARY.getName()));
					version.setSummary(runtimeConfig.getComProps().getValueForField(RuntimeFields.SUMMARY.getName()));
				}
			}
			else {
				if (runtimeConfig.getComProps().getValueForField(RuntimeFields.SUMMARY.getName()) == null) {
					doc.addField(RuntimeFields.SUMMARY.getName(), "no summary");
					version.setSummary( "no summary");
				}
				else {
					doc.addField(RuntimeFields.SUMMARY.getName(), runtimeConfig.getComProps().getValueForField(RuntimeFields.SUMMARY.getName()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Date date = Calendar.getInstance().getTime();

		StoredField sf = new StoredField();
		sf.setDefinitionId(definitionDao.findByName(RuntimeFields.PUBLICATIONDATE.getName()).getId());
		sf.setDocumentId(document.getId());
		sf.setValue(sdf.format(date));
		storedFieldDao.save(sf);
		doc.addField(RuntimeFields.PUBLICATIONDATE.getName(), sdf.format(date));

		doc.addField(RuntimeFields.DOCID.getName(), document.getId() + "");
		doc.addField(RuntimeFields.VERSION.getName(), version.getId() + "");
	}

	private void addFieldsToDocument(GedDocument document, SolrInputDocument doc, GedIndexRuntimeConfig runtimeConfig, DefinitionDAO definitionDao, StoredFieldDAO storedFieldDao) {
		// Store the fields in database
		for (String f : runtimeConfig.getComProps().getKeys()) {
			Definition d = definitionDao.findByName(f);
			if (runtimeConfig.getComProps().isMultivalued(f)) {
				if (!d.system()) {
					doc.addField(d.getName(), runtimeConfig.getComProps().getValuesForField(f), d.getBoostLvl());

					for (String s : runtimeConfig.getComProps().getValuesForField(f)) {
						StoredField sf = new StoredField();
						sf.setDefinitionId(d.getId());
						sf.setDocumentId(document.getId());
						sf.setValue(s);
						storedFieldDao.save(sf);
					}
				}
			}
			else {
				if (!d.system()) {
					doc.addField(d.getName(), runtimeConfig.getComProps().getValueForField(f), d.getBoostLvl());

					StoredField sf = new StoredField();
					sf.setDefinitionId(d.getId());
					sf.setDocumentId(document.getId());
					sf.setValue(runtimeConfig.getComProps().getValueForField(f));
					storedFieldDao.save(sf);
				}
			}
		}
		doc.setField("id", document.getId() + "");
	}
	
	/* 
	 * Fait par kevin	
	 */
	private void analyseDocumentKeywords() {
		/*
		 * Objectif:
		 * Après l'ajout d'un doc sur Solr, on requete sur tout les documents indexés afin de récuperer
		 * les mots les caractérisant.
		 * On effectue ensuite un traitement statistique R sur ses mots afin d'en dégager les classes, clusters de documents
		 * Enfin on réinsère les documents avec la liste de mots caractérisant sa classe.
		 * 
		 */
		HashMap<String,List<SolrKeyword>> preMatrice = new HashMap<String,List<SolrKeyword>>();
		
		//calcul total docs
		QueryResponse response;
		long totalDocs = 0;
		try {
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.set("q", "*:*");
			response = solrServer.query(solrQuery);
			totalDocs = response.getResults().getNumFound();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//-- On recupere les documents --//
		String mQueryString = "*:*";
		SolrQuery parameters = new SolrQuery();
		parameters.setQuery(mQueryString);
		parameters.setParam("tv.tf", true);
		parameters.setParam("tv.df", true);
		
		parameters.setRequestHandler("/tvrh");
		
		try {
			response = solrServer.query(parameters);
			
			//-- recup de la frequence de termes de tous les docs--//
			preMatrice = analyseFrequencies(response, totalDocs);
			
			//on met tout ça dans une matrice pour R
			SolrMatrix matrice = new SolrMatrix(preMatrice);
			matrice.exportXML(); //non obligatoire
			
			//on envoie la matrice a R qui nous renvoie les docs par classe
			RServer rServ = new RServer();
			HashMap<String,List<String>> classifiedMatrice;
			classifiedMatrice = rServ.solrClassification(matrice);
			
			
			//pour chaque doc on ajoute les mots-cles et on envoie sur solr
//			for(Entry<String, List<String>> entry : classifiedMatrice.entrySet()) { 
//			   //-- mofif du doc --//
//				updateDocument(response, entry.getKey(), entry.getValue());
//			}
//			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
			
	}

	private HashMap<String,List<SolrKeyword>> analyseFrequencies(QueryResponse response, long totalDocs) {
		
				Object vectorResponse = response.getResponse().get("termVectors");
				List<SolrKeyword> list = new ArrayList<SolrKeyword>();
				HashMap<String,List<SolrKeyword>> preMatrice = new HashMap<String,List<SolrKeyword>>();
				Gson gson= new Gson();
				String vectorJson = gson.toJson(vectorResponse);
				JsonReader reader = new JsonReader(new StringReader(vectorJson));
				String name;
				
				try {
					reader.beginObject();
					while (reader.hasNext()) {
						name = reader.nextName();
						System.out.println(name);
						if(name.equals("nvPairs")){
							reader.beginArray();
							name = reader.nextString(); //"uniqueKeyFieldName"
							name = reader.nextString(); //"id"
							while (reader.hasNext()) {
								name = reader.nextString();
					        	System.out.println(name);
					        	list = analyseFrequency(reader, name, totalDocs);
					        	preMatrice.put(name,list);
							}
							reader.endArray();
						}      
					}
					reader.endObject();
					System.out.println(list);
					
					reader.close();
					
					
				} catch (IOException e) {
					e.printStackTrace();
				}
		return preMatrice;
	}
	
	private List<SolrKeyword> analyseFrequency(JsonReader reader,String Id, long totalDocs) throws IOException {
		String name;
		int tf = 0, df = 0, cpt = 0;
		List<SolrKeyword> list = new ArrayList<SolrKeyword>();
		
		reader.beginObject();
		while (reader.hasNext()) {
			name = reader.nextName();
	        	System.out.println(name);
	        	if(name.equals("nvPairs")){
	        		reader.beginArray();
        		while (reader.hasNext()) {
        			name = reader.nextString();
 		        	System.out.println(name);
 		        	if(name.equals("tokens")){
 		        		reader.beginObject();
		        		while (reader.hasNext()) {
		        			name = reader.nextName();
		 		        	System.out.println(name);
		 		        	if(name.equals("nvPairs")){
		 		        		//traitement
		 		        		reader.beginArray();
		 		        		while (reader.hasNext()) {
		 		        			name = reader.nextString();
				 		        	//System.out.println(name);
				 		        	reader.beginObject();
				 		        	reader.nextName();
				 		        	reader.beginArray();
				 		        	while (reader.hasNext()) {
				 		        		String res = reader.nextString();
				 		        		if(res.equals("tf")){
				 		        			tf = reader.nextInt();
				 		        		} else if (res.equals("df")){
				 		        			df = reader.nextInt();
				 		        		} else {
				 		        			break;
				 		        		}
				 		        	}
				 		        	reader.endArray();
				 		        	reader.endObject();
				 		        	SolrKeyword keyword = new SolrKeyword(name, tf, df, totalDocs);
				 		        	list.add(keyword);
				 		        	cpt++;
		 		        		}
		 		        		reader.endArray();
		 		        	}
		        		}
		        		reader.endObject();
 		        	}
        		}
        		reader.endArray();
	        	}
		}
		reader.endObject();
		
		for(SolrKeyword key : list){
			key.normalizeTf(cpt);
		}
		
		return list;
	}
	
	private void updateDocument(QueryResponse response, String Id, List<String> keywordsNames)  {
		SolrDocument resulDoc = null;
		SolrInputDocument newDoc = new SolrInputDocument();
		
		for(SolrDocument solrDoc : response.getResults()){
			if(solrDoc.getFieldValue(RuntimeFields.DOCID.getName()).equals(Id)){
				resulDoc = solrDoc;
				break;
			}
		}
		if(resulDoc == null)
			return;
		
		newDoc = ClientUtils.toSolrInputDocument(resulDoc);
		
		newDoc.addField(RuntimeFields.KEYWORDS.getName(), keywordsNames);
		try {
			solrServer.add(newDoc);
			solrServer.commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}