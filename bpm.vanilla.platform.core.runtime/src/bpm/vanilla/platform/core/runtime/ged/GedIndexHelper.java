package bpm.vanilla.platform.core.runtime.ged;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
//import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexableField;
//import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import bpm.vanilla.platform.core.beans.ged.Definition;
import bpm.vanilla.platform.core.beans.ged.DocCat;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.ged.Security;
import bpm.vanilla.platform.core.beans.ged.StoredField;
import bpm.vanilla.platform.core.beans.ged.constant.Formats;
import bpm.vanilla.platform.core.beans.ged.constant.RuntimeFields;
import bpm.vanilla.platform.core.components.ged.GedAdvancedSearchRuntimeConfig;
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
import bpm.vanilla.platform.logging.IVanillaLogger;

public class GedIndexHelper {

	private static IndexWriter writer;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	/**
	 * Used to search in Ged index.
	 * 
	 * @param config
	 * @param logger
	 * @param definitionDao
	 * @param documentDao
	 * @param securityDao
	 * @return
	 * @throws Exception
	 */
	public static List<GedDocument> search(GedSearchRuntimeConfig config, IVanillaLogger logger, DefinitionDAO definitionDao, DocumentDAO documentDao, SecurityDAO securityDao) throws Exception {

		String querystr = generateLuceneQuery(config, logger, definitionDao);

		boolean showAllDocument = false;
		try {
			showAllDocument = Boolean.parseBoolean(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_GED_SHOW_ALL_DOCUMENT));
			logger.info("The property " + VanillaConfiguration.P_GED_SHOW_ALL_DOCUMENT + " is set to " + showAllDocument);
		} catch (Exception e) {
			logger.info("No property " + VanillaConfiguration.P_GED_SHOW_ALL_DOCUMENT + " in the vanilla.properties file. We put the value to false.");
			showAllDocument = false;
		}
		logger.info("Performing search with query : " + querystr);

		List<GedDocument> defs = executeLuceneQuery(querystr, logger, documentDao);

		logger.info("Lucene return " + defs.size() + " documents");

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

	private static String generateLuceneQuery(GedSearchRuntimeConfig config, IVanillaLogger logger, DefinitionDAO definitionDao) {
		String operator = config.isAllOccurences() ? " AND " : " OR ";
		boolean first = true;
		String querystr = "";

		if (config.getKeywords() != null) {
			for (String k : config.getKeywords()) {
				if (first) {
					querystr += " (content:" + k.toLowerCase() + " ";
					querystr += " OR content:" + k + " ";
					querystr += " OR title:" + k + " ";
					querystr += " OR title:" + k.toLowerCase() + ") ";
					first = false;
				}
				else {
					querystr += " " + operator;
					querystr += " (content:" + k.toLowerCase() + " ";
					querystr += " OR content:" + k + " ";
					querystr += " OR title:" + k + " ";
					querystr += " OR title:" + k.toLowerCase() + ") ";

				}
			}

			first = config.getKeywords().isEmpty();
		}

		if (config != null) {
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
		}
		return querystr;
	}

	private static List<GedDocument> executeLuceneQuery(String query, IVanillaLogger logger, DocumentDAO documentDao) throws Exception {
		List<DocumentVersion> res = new ArrayList<DocumentVersion>();

		String rootDir = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_GED_INDEX_DIRECTORY);

		String path = rootDir + File.separator + "ged_index";
		path = path.replace("/\\", "/");
		File f = new File(path);
		if (!f.exists() || !f.isDirectory()) {
			f.mkdirs();
		}
		//Directory directory = FSDirectory.open(new File(rootDir + File.separator + "ged_index"));
		Directory directory = FSDirectory.open(new File(rootDir + File.separator + "ged_index").toPath());
		
		Analyzer analyzer = null;

		analyzer = new KeywordAnalyzer();

		//QueryParser qp = new QueryParser(Version.LUCENE_31, "content", analyzer);
		QueryParser qp = new QueryParser("content", analyzer);
		qp.setAllowLeadingWildcard(true);
		qp.setLowercaseExpandedTerms(false);
		qp.setMultiTermRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_QUERY_REWRITE);

		logger.debug("execute query " + query + " Analyzer " + analyzer.getClass());
		Query q = qp.parse(query);

		// 2. search
		IndexSearcher searcher = null;

		try {
			//searcher = new IndexSearcher(directory, true);
			IndexReader reader = DirectoryReader.open(directory);
			searcher = new IndexSearcher(reader);
		} catch (Exception ex) {
			//IndexWriter writer = new IndexWriter(directory, new StandardAnalyzer(Version.LUCENE_31), true, IndexWriter.MaxFieldLength.UNLIMITED);
			IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
			IndexWriter writer = new IndexWriter(directory, config);
			
			Document doc = new Document();
			writer.addDocument(doc);
			writer.close();
			IndexReader reader = DirectoryReader.open(directory);
			searcher = new IndexSearcher(reader);
		}

		//searcher.setDefaultFieldSortScoring(true, false);
		

		AccessibleHitCollector collector = new AccessibleTopHitCollector(4096, false, true);
		
		
		// XXX : nobodies know what this is for
		// is value to low, some errors happened
		org.apache.lucene.search.BooleanQuery.setMaxClauseCount(1024 * 8);
		searcher.search(q, collector);
		int resNum = collector.getTotalHits();

		// 3. display results
		logger.debug("Found " + resNum + " hits.");

		for (int i = 0; i < resNum; i++) {
			int docId = collector.getDocId(i);
			float score = collector.getScore(i);

			Document d = searcher.doc(docId);

			//if (d.getFieldable(RuntimeFields.VERSION.getName()) == null) {
			if (d.getField(RuntimeFields.VERSION.getName()) == null) {
				logger.error("We found a document in the index. However it is not related to a Version in the database. We ignore it.");
				continue;
			}
			//logger.debug("Pos " + i + " - " + score + " docID " + d.getFieldable(RuntimeFields.DOCID.getName()).stringValue() + " and versionID " + d.getFieldable(RuntimeFields.VERSION.getName()).stringValue());
			logger.debug("Pos " + i + " - " + score + " docID " + d.getField(RuntimeFields.DOCID.getName()).stringValue() + " and versionID " + d.getField(RuntimeFields.VERSION.getName()).stringValue());

			//Fieldable fd = d.getFieldable(RuntimeFields.VERSION.getName());
			IndexableField fd = d.getField(RuntimeFields.VERSION.getName());
			try {

				DocumentVersion version = documentDao.getDocumentVersionDAO().getDocumentVersionById(new Integer(fd.stringValue()));

				// try to get summary
				try {
					//Fieldable summaryField = d.getFieldable(RuntimeFields.SUMMARY.getName());
					IndexableField summaryField = d.getField(RuntimeFields.SUMMARY.getName());
					version.setSummary(summaryField.stringValue());
				} catch (Exception e) {
					logger.info("Failed to get summary for the document");
				}

				res.add(version);
			} catch (NumberFormatException e) {
				String msg = "NumberFormatException while searching for doc with id " + fd.stringValue();
				logger.error(msg, e);
			} catch (Exception e) {
				String msg = "Exception while searching for doc with id " + fd.stringValue() + ", reason " + e.getMessage();
				logger.error(msg, e);
			}

		}

		// searcher can only be closed when there
		// is no need to access the documents any more.
		//searcher.close();

		return documentDao.getDocumentsForVersions(res);
	}

	private static Formats findFormat(String extension) {
		for (Formats f : Formats.values()) {
			if (f.getExtension().equalsIgnoreCase(extension)) {
				return f;
			}
		}
		return Formats.UNKNOWN;
	}

	/**
	 * Used to index a file (it will also add the document in GED) Don't ask how
	 * it work... It doesn't
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
	 */
	public static Integer index(GedIndexRuntimeConfig runtimeConfig, InputStream is, IVanillaLogger logger, DocumentDAO documentDao, SecurityDAO securityDao, DefinitionDAO definitionDao, StoredFieldDAO storedFieldDao, CategoryDAO categoryDao, DocCatDAO docCatDao) throws Exception {

		logger.info("Indexing file using Lucene");

		// Find the path where the file will be create
		String rootDir = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_GED_INDEX_DIRECTORY);

		Formats format = findFormat(runtimeConfig.getFormat());
		Date date = Calendar.getInstance().getTime();
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		File file;
		String path;

		try {
			String yearDir = rootDir + "/" + "ged_documents" + "/" + year;
			File yearFile = new File(yearDir);
			if (!yearFile.exists()) {
				yearFile.mkdir();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		String relativPath = "/" + "ged_documents" + "/" + year + "/" + month + "/";
		String dir = rootDir + relativPath;
		String name = "";

		int index = 0;
		do {
			name = "doc_" + index + "." + runtimeConfig.getFormat();
			path = dir + name;
			path = path.replace("/\\", "/");
			file = new File(path);
			index++;
		} while (file.isFile());

		logger.info("creating file " + dir);
		File _dir = new File(dir);
		if (!_dir.exists()) {
			boolean b = _dir.mkdirs();
			if (!b)
				throw new Exception(dir);
		}

		FileOutputStream fos = new FileOutputStream(path);
		byte buffer[] = new byte[512 * 1024];
		int nbLecture;

		while ((nbLecture = is.read(buffer)) != -1) {
			fos.write(buffer, 0, nbLecture);

		}
		is.close();
		fos.close();

		// Create the GedDocument used to store none indexed information in the
		// DB
		GedDocument gdoc = null;
		DocumentVersion version = null;
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
				version.setDocumentPath(relativPath + name);
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
				version.setDocumentPath(relativPath + name);
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
			gdoc.setMdmAttached(runtimeConfig.isMdmAttached());

			version = new DocumentVersion();
			version.setVersion(1);
			version.setIsIndexed(1);
			version.setDocumentPath(relativPath + name);
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

		runtimeConfig.getComProps().setProperty(definitionDao.findByName(RuntimeFields.VERSION.getName()), String.valueOf(version.getId()));

		boolean create = testIndex(rootDir);
		logger.info("Create new index is at : " + create);
		Directory directory = FSDirectory.open(new File(rootDir + File.separator + "ged_index").toPath());

		if (writer != null && IndexWriter.isLocked(directory)) {
			logger.info("Index writer was not null or directory is locked, cleaning...");
			writer.close();
		}

		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		writer = new IndexWriter(directory, config);

		//logger.info("Opening index before add, we now have " + writer.getReader().numDocs() + " doc(s)");
		logger.info("Opening index before add, we now have " + writer.getChildResources().size() + " doc(s)");
		

		Document doc = new Document();

		// Store the fields in database
		for (String f : runtimeConfig.getComProps().getKeys()) {
			Definition d = definitionDao.findByName(f);

			if (runtimeConfig.getComProps().isMultivalued(f)) {
				if (!d.system()) {
					for (String s : runtimeConfig.getComProps().getValuesForField(f)) {
						logger.debug("indexing  field " + f + " with value " + s);
						Field _f = null;
						if (d.analized() && d.stored()) {
							_f = new Field(d.getName(), s, Field.Store.YES, Field.Index.ANALYZED);
							_f.setBoost(d.getBoostLvl());
						}
						else if (!d.analized() && d.stored()) {
							_f = new Field(d.getName(), s, Field.Store.YES, Field.Index.NOT_ANALYZED);
							_f.setBoost(d.getBoostLvl());
						}
						StoredField sf = new StoredField();
						sf.setDefinitionId(d.getId());
						sf.setDocumentId(version.getId());
						sf.setValue(s);
						storedFieldDao.save(sf);
						doc.add(_f);
					}
				}
			}
			else {
				if (!d.system()) {
					logger.debug("indexing  field " + f + " with value " + runtimeConfig.getComProps().getValueForField(f));
					Field _f = null;
					if (d.analized() && d.stored()) {
						_f = new Field(d.getName(), runtimeConfig.getComProps().getValueForField(f), Field.Store.YES, Field.Index.ANALYZED);
						_f.setBoost(d.getBoostLvl());
					}
					else if (!d.analized() && d.stored()) {
						_f = new Field(d.getName(), runtimeConfig.getComProps().getValueForField(f), Field.Store.YES, Field.Index.NOT_ANALYZED);
						_f.setBoost(d.getBoostLvl());
					}
					StoredField sf = new StoredField();
					sf.setDefinitionId(d.getId());
					sf.setDocumentId(version.getId());
					sf.setValue(runtimeConfig.getComProps().getValueForField(f));
					storedFieldDao.save(sf);
					doc.add(_f);
				}
			}
		}

		// find the content
		try {
			logger.info("Trying to analyze content, format is " + format + ".");
			try {
				String content = analyzeContent(path, format, doc, logger);

				if (runtimeConfig.getComProps().getValueForField(RuntimeFields.SUMMARY.getName()) == null) {
					if (content.length() > 150) {
						content = content.replaceAll("\\t", " ");
						content = content.replaceAll("\\n", " ");
						content = content.replaceAll("     ", "");
						String summary = content.substring(0, 149);
						logger.info("Summary of document is : " + summary);
						Field fSummary = new Field(RuntimeFields.SUMMARY.getName(), summary, Field.Store.YES, Field.Index.ANALYZED);
						doc.add(fSummary);

						try {
							version.setSummary(summary);
							documentDao.getDocumentVersionDAO().updateVersion(version);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					Field fSummary = new Field(RuntimeFields.SUMMARY.getName(), content, Field.Store.YES, Field.Index.ANALYZED);
					doc.add(fSummary);
				}
				else {
					Field fSummary = new Field(RuntimeFields.SUMMARY.getName(), runtimeConfig.getComProps().getValueForField(RuntimeFields.SUMMARY.getName()), Field.Store.YES, Field.Index.ANALYZED);
					doc.add(fSummary);
				}
			} catch (Exception ex) {
				logger.warn("IndexFile has failed to analyse content : " + ex.getMessage());

				if (runtimeConfig.getComProps().getValueForField(RuntimeFields.SUMMARY.getName()) == null) {
					Field fSummary = new Field(RuntimeFields.SUMMARY.getName(), "no summary", Field.Store.YES, Field.Index.ANALYZED);
					doc.add(fSummary);
				}
				else {
					Field fSummary = new Field(RuntimeFields.SUMMARY.getName(), runtimeConfig.getComProps().getValueForField(RuntimeFields.SUMMARY.getName()), Field.Store.YES, Field.Index.ANALYZED);
					doc.add(fSummary);
				}
			}
		} catch (Exception e) {
			logger.warn("IndexFile has failed to analyse content : " + e.getMessage());
			logger.debug("Debug error is : " + e.getMessage(), e);
		}

		Field fDate = new Field(RuntimeFields.PUBLICATIONDATE.getName(), sdf.format(date), Field.Store.YES, Field.Index.NOT_ANALYZED);
		StoredField sf = new StoredField();
		sf.setDefinitionId(definitionDao.findByName(RuntimeFields.PUBLICATIONDATE.getName()).getId());
		sf.setDocumentId(id);
		sf.setValue(sdf.format(date));
		storedFieldDao.save(sf);
		doc.add(fDate);

		Field fDocId = new Field(RuntimeFields.DOCID.getName(), id + "", Field.Store.YES, Field.Index.NOT_ANALYZED);
		doc.add(fDocId);

		Field fVersionId = new Field(RuntimeFields.VERSION.getName(), version.getId() + "", Field.Store.YES, Field.Index.NOT_ANALYZED);
		doc.add(fVersionId);

		// If the document was created before, no need to set the security
		if (runtimeConfig.getExistingDocId() == null) {

			Integer repositoryId = runtimeConfig.getRepositoryId();
			if (runtimeConfig.getGroupIds().isEmpty()) {
				// no group
				logger.info("set grant for user " + gdoc.getCreatedBy());

				Security d = new Security();
				d.setDocumentId(id);
				d.setRepositoryId(repositoryId);
				d.setUserId(runtimeConfig.getUserId());
				d.setGroupId(runtimeConfig.getGroupId());
				securityDao.save(d);

			}
			else {
				for (int g : runtimeConfig.getGroupIds()) {
					logger.info("set grant for group " + g);
					Security d = new Security();
					d.setDocumentId(id);
					d.setRepositoryId(repositoryId);
					d.setUserId(runtimeConfig.getUserId());
					d.setGroupId(g);
					securityDao.save(d);
				}
			}

		}

		// Finally add it to the index !!!!!!!
		writer.addDocument(doc);
		close();

		return id;
	}

	/**
	 * Parse the file with Tika to index the content
	 * 
	 * @param path
	 * @param format
	 * @param doc
	 * @param logger
	 * @return
	 * @throws TikaException
	 * @throws IOException
	 * @throws SAXException
	 */
	private static String analyzeContent(String path, Formats format, Document doc, IVanillaLogger logger) throws TikaException, IOException, SAXException {
		InputStream input = new FileInputStream(new File(path));
		Tika tika = new Tika();
		String content = tika.parseToString(input);
		doc.add(new Field(RuntimeFields.CONTENT.getName(), content, Field.Store.YES, Field.Index.ANALYZED));
		logger.info("content analyzed");
		return content;
	}

	/**
	 * Close the writer and commit the changes
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public static void close() throws CorruptIndexException, IOException {
		//writer.optimize(true);
		writer.commit();
		writer.close();
	}

	/**
	 * Check if an index is already create in indexDir
	 * 
	 * @param indexDir
	 * @return
	 */
	private static boolean testIndex(String indexDir) {
		String path = indexDir + File.separator + "ged_index";
		path = path.replace("/\\", "/");
		File f = new File(path);
		if (!f.exists() || !f.isDirectory()) {
			f.mkdirs();
			return true;
		}

		for (File _f : f.listFiles()) {
			if (_f.getAbsolutePath().indexOf("cfs") > -1) {
				return false;
			}
		}
		return true;
	}

	public static List<GedDocument> advancedSearch(GedAdvancedSearchRuntimeConfig config, IVanillaLogger logger, CategoryDAO categoryDao, DocCatDAO docCatDao, DocumentDAO documentDao, SecurityDAO securityDao, DefinitionDAO definitionDao) throws Exception {

		List<Security> secs = securityDao.findForGroupsAndRepository(config.getUserId(), config.getGroupIdsAsStrings(), config.getRepositoryId());

		List<GedDocument> documents = null;

		// if there's a query execute it
		if (config.getQuery() != null && !config.getQuery().equals("")) {
			// execute a query on the index
			GedSearchRuntimeConfig gedConf = new GedSearchRuntimeConfig();
			gedConf.setAllOccurences(true);
			gedConf.setKeywords(Arrays.asList(config.getQuery().split(" ")));

			String query = generateLuceneQuery(gedConf, logger, definitionDao);
			try {
				documents = executeLuceneQuery(query, logger, documentDao);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		// else just get back all documents
		if (documents == null) {
			documents = documentDao.findDocuments(secs);
		}

		return findDocumentsByCategories(config, logger, categoryDao, documentDao, securityDao, docCatDao, documents);
	}

	/**
	 * Used to find documents using categories This method will not search in
	 * the index
	 * 
	 * @param config
	 * @param logger
	 * @param categoryDao
	 * @param documentDao
	 * @param securityDao
	 * @param docCatDao
	 * @param documents
	 * @return
	 */
	private static List<GedDocument> findDocumentsByCategories(GedAdvancedSearchRuntimeConfig config, IVanillaLogger logger, CategoryDAO categoryDao, DocumentDAO documentDao, SecurityDAO securityDao, DocCatDAO docCatDao, List<GedDocument> documents) {
		logger.debug("find the documents with parent directoryId = " + config.getDirectoryId());
		logger.debug("find the documents with categories in " + config.getGedCategories());
		logger.debug("find the documents which respect the security for group in " + config.getGroupIds() + " and userId = " + config.getUserId());

		List<GedDocument> toKeep = new ArrayList<GedDocument>();

		for (GedDocument doc : documents) {

			// check for the directory
			if (config.getDirectoryId() != null && config.getDirectoryId() > -1) {
				if (doc.getDirectoryId() != null && doc.getDirectoryId() > -1) {
					if (doc.getDirectoryId().intValue() != config.getDirectoryId().intValue()) {
						continue;
					}
				}
			}

			// check the axes
			if (config.getGedCategories() != null && config.getGedCategories().size() > 0) {
				boolean valid = true;
				// find document categories
				List<DocCat> docCats = docCatDao.findForDocument(doc.getId());
				for (Integer catId : config.getGedCategories()) {
					boolean finded = false;
					for (DocCat cat : docCats) {
						if (catId.intValue() == cat.getCategoryId()) {
							finded = true;
							break;
						}
					}
					if (!finded) {
						valid = false;
						break;
					}
				}

				if (valid) {
					toKeep.add(doc);
				}
			}
			else {
				toKeep.add(doc);
			}

		}

		// return what's left
		return toKeep;
	}

}
