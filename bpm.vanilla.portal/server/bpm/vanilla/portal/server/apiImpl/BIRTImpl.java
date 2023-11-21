package bpm.vanilla.portal.server.apiImpl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.portal.server.security.PortalSession;


/**
 * Used to forward to web viewer
 * 
 * @author dunno
 *
 */
public class BIRTImpl {
	public static final int TYPE_STRING = 1; 
	public static final int TYPE_FLOAT = 2;
	public static final int TYPE_DECIMAL = 3;
	public static final int TYPE_DATE_TIME = 4;
	public static final int TYPE_BOOLEAN = 5;
	public static final int TYPE_INTEGER = 6;
	public static final int TYPE_DATE = 7;
	public static final int TYPE_TIME = 8;

	//private static EngineConfig config = null;
	public static String pathEngine = null;
	
	public static final String GROUP_NAME = "groupname";
	public static final String GED_NAME = "gedname";
	public static final String PATH = "path";
	public static final String OUTPUTTYPE = "ooutputtype";
	public static final String VANILLA_URL = "vanillaurl";
	

	/**
	 * 
	 * @param sock
	 * @param itemId
	 * @param groupName
	 * @param dirToWrite ==
	 *            birtViewerPath pour l'utilsation du viewer
	 * @param session 
	 * @return nom du model généré
	 */
	public static String writeBIRTFromRep(String dirToWrite, IRepositoryApi sock, int itemId, String groupName, IRepository irep, PortalSession session) {
		PrintWriter writer = null;
		String name = null;

		String dir = "";
		try {
			RepositoryItem item = irep.getItem(itemId);

			String model = sock.getRepositoryService().loadModel(item);

			String xml = setGroupOnBirtReport(sock, groupName, model, session);

			Object o = new Object();
			name = "birt_" + o.hashCode() + ".rptdesign";

			dir = dirToWrite + File.separator + name;

			writer = new PrintWriter(dir, "UTF-8");
			writer.write(xml, 0, xml.length());

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}

		}

		return name;
	}

	/**
	 * change the groupName of a Birt Report must be used once the report is
	 * loaded from the repository and before the WebViewer servlet is called to
	 * generate the report
	 * 
	 * @param groupName
	 * @param session 
	 * @param birtStream
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 */
	public static String setGroupOnBirtReport(IRepositoryApi sock, String groupName, String birtXml, PortalSession session) throws DocumentException, IOException {
		Document doc = DocumentHelper.parseText(birtXml);
		doc.setXMLEncoding("UTF-8");
		Element root = doc.getRootElement();
		

		Element dataSources = root.element("data-sources");

		if (dataSources == null) {
			return doc.asXML();
		}

		for (Object o : dataSources.elements("oda-data-source")) {

			Element dataSource = (Element) o;

			if (dataSource.attribute("extensionID") != null && dataSource.attribute("extensionID").getStringValue().equals("bpm.metadata.birt.oda.runtime")) {

				for (Object _o : dataSource.elements("property")) {
					if (((Element) _o).attribute("name").getStringValue().equals("GROUP_NAME")) {
						((Element) _o).setText(groupName);
					}
					else if (((Element) _o).attribute("name").getStringValue().equals("USER")) {
						((Element) _o).setText(session.getUser().getLogin());
					}
					else if (((Element) _o).attribute("name").getStringValue().equals("PASSWORD")) {
						((Element) _o).setText(session.getUser().getPassword());
					}
					
//					dsHandle.setProperty("IS_ENCRYPTED", encrypted + "");
				}
//				if (((RepositoryConnection) sock).isEncrypted()) {
//					Element e = dataSource.addElement("property").addAttribute("name", "IS_ENCRYPTED");
//					e.setText(((RepositoryConnection) sock).isEncrypted() + "");
//				}

			}

		}

		return doc.asXML();

	}

//	private static EngineConfig getEngineConfig(String path) {
//		if (config == null) {
//			config = new EngineConfig();
//			config.setEngineHome(path);
//			config.setLogConfig(path + File.separator + "logs", Level.SEVERE);
//		}
//		return config;
//	}

//	private static IReportEngine getReportEngin(String path) {
//		IReportEngine engine = null;
//		EngineConfig conf = getEngineConfig(path);
//		try {
//
//			Platform.startup(conf);
//			IReportEngineFactory factory = (IReportEngineFactory) Platform
//					.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
//			engine = factory.createReportEngine(conf);
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//		return engine;
//	}


	/**
	 * USE public static String generateBirtFile(IRepositoryConnection sock, HashMap<String, String> param, IDirectoryItem item, Properties properties)
	 * 
	 * @param sock :
	 *            connection au repository
	 * @param param :
	 *            parametres et leurs valeurs
	 * @param path :
	 *            current location, pour acceder au report engine et pour
	 *            specifier ou ecrire le rptdesign
	 * @param outputType
	 * @param id :
	 *            id de l'item (IDirectoryItem)
	 * @param groupName:
	 *            nom du groupe
	 * @return "\Reports\name.outputType" : chemin relatif d'accès au rapport
	 *         chemin complet = path + return
	 * @throws Exception
	 */
//	@Deprecated
//	public static String generateBirtFile(IRepositoryConnection sock, HashMap<String, String> param, String path, String outputType, int id, String groupName, IRepository irep, boolean addtoged, String nameforged) throws Exception {
//		HashMap<String, Integer> parameters = new HashMap<String, Integer>();
//		String name = "birt_" + new Object().hashCode();
//
//		Integer pvalue = new Integer(4);
//		parameters.put(name, pvalue);
//
//		EngineConfig conf = null;
//		IReportEngine engine = null;
//		
//		if (pathEngine == null) {
//			throw new Exception("The report engin path is not set");
//		}
//		else {
//			conf = getEngineConfig(pathEngine);
//			engine = getReportEngin(pathEngine);
//		}
//
//		try {
//			IRenderOption renderOption = null;
//
//			IReportRunnable design = null;
//
//			IDirectoryItem item = irep.getItem(id);
//			String model = sock.loadModel(IRepositoryConnection.CUST_TYPE, item);
//
//			String xml = setGroupOnBirtReport(sock, groupName, model);
//			
//			design = engine.openReportDesign(new ByteArrayInputStream(xml.getBytes("UTF-8")));
//			// Create task to run and render the report,
//			IRunAndRenderTask task = engine.createRunAndRenderTask(design);
//
//			// IRenderOption options = null;
//			if (outputType.equals("html")) {
//				
//				renderOption = new HTMLRenderOption();
//				renderOption.setActionHandler(new HTMLActionHandler());
//				renderOption.setImageHandler(new HTMLEmbeddedImageHandler());
//				renderOption.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_HTML);
//
//				renderOption.setActionHandler(new HTMLActionHandler());
//
//
//			} else if (outputType.equals("pdf")) {
//				renderOption = new PDFRenderOption();
//				renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
//				HashMap<String, IRenderOption> contextMap = new HashMap<String, IRenderOption>();
//				contextMap.put(EngineConstants.APPCONTEXT_PDF_RENDER_CONTEXT, renderOption);
//				task.setAppContext(contextMap);
//				renderOption.setOutputFormat(PDFRenderOption.OUTPUT_FORMAT_PDF);
//				
//				renderOption.setAppBaseURL("http://localhost:8080/vanilla/");
//				renderOption.setBaseURL("http://localhost:8080/vanilla/");
//				renderOption.setImageHandler(new HTMLServerImageHandler());
//				
//			} else {
//				renderOption = new PDFRenderOption();
//				renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
//				HashMap<String, IRenderOption> contextMap = new HashMap<String, IRenderOption>();
//				contextMap.put(EngineConstants.APPCONTEXT_PDF_RENDER_CONTEXT, renderOption);
//				task.setAppContext(contextMap);
//				renderOption.setOutputFormat(PDFRenderOption.OUTPUT_FORMAT_PDF);
//			}
//
//			task.setParameterValues(parameters);
//
//			Set<String> keys = param.keySet();
//
//			for (String key : keys) {
//				String value = param.get(key);
//				if (key.equalsIgnoreCase("Date_periode")) {
//					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//					Date d = sdf.parse(value);
//					java.sql.Date sql = new java.sql.Date(d.getTime());
//					task.setParameterValue(key, sql);
//				}
//				else {
//					task.setParameterValue(key, value);
//				}
//			}
//
//			task.validateParameters();
//
//			String dir = path + File.separator + "Reports" + File.separator + name + "." + outputType;
//			renderOption.setOutputFileName(path + File.separator + "Reports" + File.separator + name + "." + outputType);
//
//			renderOption.setOutputFormat(outputType);
//			task.setRenderOption(renderOption);
//
//			task.run();
//			task.close();
//
//			if (addtoged) {
//				File f = new File(dir);
//				String refname = "";
//				if (nameforged != null && !nameforged.equalsIgnoreCase("")) {
//					refname = nameforged;
//				}
//				else {
//					refname = item.getName();
//				}
//				sock.sendReportFileFor(refname, item, outputType, f,	groupName);
//			}
//
//		} catch (EngineException e) {
//			e.printStackTrace();
//		}
//
//		engine.shutdown();
//
//		return "Reports" + File.separator + name + "." + outputType;
//	}


//	public static String runBIRTReportJNR(IRepositoryConnection sock, HashMap<String, String> param, String path, String outputType, int id, String groupName) throws Exception {
//		HashMap<String, Integer> parameters = new HashMap<String, Integer>();
//		String name = "birt_" + new Object().hashCode();
//
//		Integer pvalue = new Integer(4);
//		parameters.put(name, pvalue);
//
//		EngineConfig conf = null;
//		IReportEngine engine = null;
//		
//		if (pathEngine == null) {
//			throw new Exception("The report engin path is not set");
//		}
//		else {
//			conf = getEngineConfig(pathEngine);
//			engine = getReportEngin(pathEngine);
//		}
//
//		try {
//			IRenderOption renderOption = null;
//
//			renderOption = new HTMLRenderOption();
//			renderOption.setActionHandler(new HTMLActionHandler());
//			HTMLServerImageHandler imageHandler = new HTMLServerImageHandler();
//			renderOption.setImageHandler(imageHandler);
//			conf.getEmitterConfigs().put("html", renderOption); //$NON-NLS-1$
//
//			IReportRunnable design = null;
//
//			// Open the report design
//			String dir = "";
//			if (id == 0) {
//				dir = System.getProperty("user.dir")+ "/resources/chart.rptdesign";
//			} else {
//				dir = path + File.separator	+ writeBIRTFromRep(path, sock, id, groupName, sock.getRepository());
//			}
//			design = engine.openReportDesign(dir);
//
//			// Create task to run and render the report,
//			IRunAndRenderTask task = engine.createRunAndRenderTask(design);
//
//			// IRenderOption options = null;
//			if (outputType.equals("html")) {
//				HTMLRenderContext renderContext = new HTMLRenderContext();
//				renderContext.setSupportedImageFormats("JPG;PNG;BMP;SVG");
//				renderContext.setBaseImageURL("../");
//				renderContext.setImageDirectory("../webapps/vanilla.war/");
//
//				HashMap<String, HTMLRenderContext> contextMap = new HashMap<String, HTMLRenderContext>();
//				contextMap.put(EngineConstants.APPCONTEXT_HTML_RENDER_CONTEXT, renderContext);
//				task.setAppContext(contextMap);
//				renderOption.setActionHandler(new HTMLActionHandler());
//
//				conf.getEmitterConfigs().put("html", renderOption); //$NON-NLS-1$
//
//			} else if (outputType.equals("pdf")) {
//				renderOption = new PDFRenderOption();
//				renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
//				HashMap<String, IRenderOption> contextMap = new HashMap<String, IRenderOption>();
//				contextMap.put(EngineConstants.APPCONTEXT_HTML_RENDER_CONTEXT, renderOption);
//				task.setAppContext(contextMap);
//			} else {
//				renderOption = new PDFRenderOption();
//				renderOption.setSupportedImageFormats("PNG;GIF;JPG;BMP;SVG");
//				HashMap<String, IRenderOption> contextMap = new HashMap<String, IRenderOption>();
//				contextMap.put(EngineConstants.APPCONTEXT_HTML_RENDER_CONTEXT, renderOption);
//				task.setAppContext(contextMap);
//			}
//
//			task.setParameterValues(parameters);
//
//			Set<String> keys = param.keySet();
//
//			for (String key : keys) {
//				String value = param.get(key);
//				task.setParameterValue(key, value);
//				System.out.println(" for task parameterValue: " + value + "parameterName: " + key);
//			}
//
//			task.validateParameters();
//
//			renderOption.setOutputFileName("../webapps/vanilla.war/" + name	+ "." + outputType);
//
//			if (outputType.equalsIgnoreCase("html")) {
//				renderOption.setOutputFormat("html");
//			} else if (outputType.equalsIgnoreCase("pdf")) {
//				renderOption.setOutputFormat("pdf");
//			}
//
//			task.setRenderOption(renderOption);
//
//			task.run();
//			task.close();
//
//			// File f = new File(dir);
//			// f.delete();
//		} catch (EngineException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		engine.shutdown();
//
//		// Platform.shutdown();
//		System.out.println("../webapps/vanilla.war/" + name + "." + outputType);
//
//		return "../webapps/vanilla.war/" + name + "." + outputType;
//	}
	
	
//	private static HashMap<String, IScalarParameterDefn> getParamatersDefinitions(IReportEngine engine, IReportRunnable design) {
//		HashMap<String, IScalarParameterDefn> res = new HashMap<String, IScalarParameterDefn>();
//		
//		IGetParameterDefinitionTask parametersTask = engine.createGetParameterDefinitionTask( design );
//		Collection<IParameterDefnBase> params = parametersTask.getParameterDefns(true);
//		
//		//Iterate over all parameters
//		for (IParameterDefnBase param : params) {
//			//Group section found
//			if (param instanceof IParameterGroupDefn) {
//				//Get Group Name
//				IParameterGroupDefn group = (IParameterGroupDefn) param;
//				BiPortalServiceImpl.logger.debug("Parameter Group: " + group.getName( ) );
//				
//				//Get the parameters within a group
//				Iterator<IScalarParameterDefn> i2 = group.getContents().iterator( );
//				while (i2.hasNext()) {
//					IScalarParameterDefn scalar = i2.next( );
//					BiPortalServiceImpl.logger.debug("def found	" + scalar.getName());
//					res.put(scalar.getName(), scalar);
////					scalar.getParameterType();
//				}
//				        
//			}
//			else {
//				//Parameters are not in a group
//				IScalarParameterDefn scalar = (IScalarParameterDefn) param;
//				res.put(scalar.getName(), scalar);
//				BiPortalServiceImpl.logger.debug("def found	" + scalar.getName());
////				System.out.println("param name " + param.getName());
////				System.out.println("scalar name " + scalar.getName());
////				System.out.println("scalar type " + scalar.getTypeName());
////				System.out.println("scalar display format " + scalar.getDisplayFormat());
////				System.out.println("scalar data type " + scalar.getDataType());
////				System.out.println("scalar default value " + scalar.getDefaultValue());
//				
//				
//				//Parameter is a List Box 
////				if(scalar.getControlType() ==  IScalarParameterDefn.LIST_BOX)
////				{
////				    Collection selectionList = parametersTask.getSelectionList( param.getName() );
////				    //Selection contains data    
////					if ( selectionList != null )
////					{
////						for ( Iterator sliter = selectionList.iterator( ); sliter.hasNext( ); )
////						{
////							//Print out the selection choices
////							IParameterSelectionChoice selectionItem = (IParameterSelectionChoice) sliter.next( );
////							String value = (String)selectionItem.getValue( );
////							String label = selectionItem.getLabel( );
////							System.out.println( label + "--" + value);
////						}
////					}		        
////				}   
//			}
//		}
//				
//		parametersTask.close();
//		return res;
//	}
	
	
	
//	static public class HTMLEmbeddedImageHandler extends HTMLCompleteImageHandler{
//
//		/* (non-Javadoc)
//		 * @see org.eclipse.birt.report.engine.api.HTMLCompleteImageHandler#handleImage(org.eclipse.birt.report.engine.api.IImage, java.lang.Object, java.lang.String, boolean)
//		 */
//		@Override
//		protected String handleImage(IImage image, Object context, String prefix, boolean needMap) {
//			Base64 base64  = new Base64();
//			byte[] encoded = base64.encode(image.getImageData());
//
//			image.getRenderOption().setOutputFormat("PNG");
//			/* Remove the point in the image name */
//			String extension = image.getExtension().substring(1, image.getExtension().length());
//			StringBuffer sb = new StringBuffer("data:image/" + extension + ";base64,");
//			String stringEncoded = new String(encoded);
//			sb.append(stringEncoded);
//			return sb.toString();
//		}
//
//	}

	
	
//	public static void main(String[] args) {
//		EngineConfig conf = null;
//		IReportEngine engine = null;
//		
//		String p = "D:\\vanilla-tomcat-mysql-2.2.1\\webapps\\FreeWebReport\\WEB-INF\\ReportEngine\\";
//		conf = getEngineConfig(p);
//		engine = getReportEngin(p);
//		
//
//		try {
//			IReportRunnable design = null;
//
//			design = engine.openReportDesign("E:\\bpm - projets\\Parkeon\\parkeon - nou\\biw - 09-03-10\\Daily_Collection_Report.rptdesign");
//			
//			new BIRTImpl().getParamatersDefinitions(engine, design);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}


}
