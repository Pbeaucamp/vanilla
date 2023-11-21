package bpm.gateway.core;

import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.gateway.core.forms.Form;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.transformations.GlobalDefinitionInput;
import bpm.gateway.core.transformations.SqlLookup;
import bpm.gateway.core.transformations.SubTransformationHelper;
import bpm.gateway.core.transformations.inputs.FMDTHelper;
import bpm.gateway.core.transformations.mdm.MdmHelper;
import bpm.gateway.core.transformations.olap.OlapHelper;
import bpm.gateway.core.transformations.outputs.FreemetricsKPI;
import bpm.gateway.core.transformations.vanilla.FreeDashHelper;
import bpm.gateway.core.transformations.vanilla.VanillaLdapSynchro;
import bpm.gateway.core.transformations.webservice.WebServiceVanillaHelper;
import bpm.gateway.core.tsbn.rpu.RpuConnector;
import bpm.gateway.core.tsbn.syrius.SyriusConnector;
import bpm.gateway.runtime2.tools.StringParser;
import bpm.mdm.remote.MdmRemote;
import bpm.vanilla.platform.core.IRepositoryContext;

/**
 * This class is the gateway Model. All information for a model it is stored in
 * it
 * 
 * @author LCA
 * 
 */
public class DocumentGateway {
	public static final String[] TRANSFORMATION_TYPE = new String[] { "Standard Mode", "Linear Mode", "ELT Mode", "Distributed Mode",

	};

	public static final int MODE_STANDARD = 0;
	public static final int MODE_LINEAR = 1;
	public static final int MODE_ELT = 2;
	public static final int MODE_DISTRIBUTED = 3;

	public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private int mode = 0;
	private ResourceManager resourceManager = new ResourceManager();

	// private long
	private Integer id;
	private String name = "";
	private Date creationDate = Calendar.getInstance().getTime();
	private String author = "";
	private Date lastModificationDate = Calendar.getInstance().getTime();
	private String description = "";
	private Integer repositoryId;
	private String projectVersion = "";
	private String projectName = "";

	private List<Transformation> transformations = new ArrayList<Transformation>();

	private List<Variable> variables = new ArrayList<Variable>();

	private List<Comment> annotes = new ArrayList<Comment>();

	private List<Parameter> parameters = new ArrayList<Parameter>();
	private List<Form> forms = new ArrayList<Form>();

	private SubTransformationHelper subtransfoHelper;
	private FMDTHelper fmdtHelper;
	private OlapHelper olapHelper;
	private MdmHelper mdmHelper;
	private FreeDashHelper freeDashHelper;
	private WebServiceVanillaHelper webServiceVanillaHelper;

	private StringParser stringParser;
	private IRepositoryContext repCtx;

	/**
	 * 
	 */
	public DocumentGateway() {
		resourceManager.getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_BIGATEWAY_HOME).setValue(ResourceManager.getInstance().getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_BIGATEWAY_HOME).getValueAsString());
		resourceManager.getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_GATEWAY_TEMP).setValue(ResourceManager.getInstance().getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_GATEWAY_TEMP).getValueAsString());
		setRepositoryContext(null);
	}

	public boolean isRepositoryContextSet() {
		return repCtx != null;
	}

	public void setRepositoryContext(IRepositoryContext repContext) {
		this.repCtx = repContext;
		this.subtransfoHelper = new SubTransformationHelper(repContext);
		fmdtHelper = new FMDTHelper(repContext);
		olapHelper = new OlapHelper(repContext);
		freeDashHelper = new FreeDashHelper(repContext);
		webServiceVanillaHelper = new WebServiceVanillaHelper(repContext);

		// XXX : replace by a real implementation client/server
		try {

			MdmRemote remote = new MdmRemote(repContext.getVanillaContext().getLogin(), repContext.getVanillaContext().getPassword(), repContext.getVanillaContext().getVanillaUrl(), null, null);
			remote.loadModel();

			mdmHelper = new MdmHelper(remote, repCtx);
		} catch (Exception e) {
			Logger.getLogger(getClass()).warn("IRepositoryContext is not set");
		}

		if (repContext != null) {
			stringParser = new StringParser(repContext.getVanillaContext());
		}
		else {
			stringParser = new StringParser(null);
		}

	}

	public StringParser getStringParser() {
		return stringParser;
	}

	public FreeDashHelper getFreeDashHelper() {
		return freeDashHelper;
	}

	public SubTransformationHelper getSubTransformationHelper() {
		return subtransfoHelper;
	}

	public MdmHelper getMdmHelper() throws Exception {
		if (mdmHelper == null) {
			throw new Exception("You need to be connected to Vanilla");
		}
		return mdmHelper;
	}

	public FMDTHelper getFMDTHelper() {
		return fmdtHelper;
	}

	public OlapHelper getOlapHelper() {
		return olapHelper;
	}

	public WebServiceVanillaHelper getWebServiceVanillaHelper() {
		return webServiceVanillaHelper;
	}

	public void addVariable(Variable v) throws Exception {
		for (Variable vr : variables) {
			if (vr.getName().equals(v.getName())) {
				throw new Exception("A variable with the same name already exists in this Gateway Model");
			}
		}
		variables.add(v);
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public void removeVariable(Variable v) {
		variables.remove(v);
	}

	public List<Variable> getVariables() {
		return new ArrayList<Variable>(variables);
	}

	public Integer getId() {
		return id;
	}

	public void setId(String id) {
		try {
			this.id = Integer.parseInt(id);
		} catch (NumberFormatException e) {

		}

	}

	/**
	 * 
	 * @param annote
	 *            : a comment
	 */
	public void addAnnote(Comment annote) {
		annotes.add(annote);
	}

	/**
	 * 
	 * @param annote
	 *            : a comment
	 */
	public void removeAnnote(Comment annote) {
		annotes.remove(annote);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(String creationDate) {
		try {
			this.creationDate = DEFAULT_DATE_FORMAT.parse(creationDate);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the lastModificationDate
	 */
	public Date getLastModificationDate() {
		return lastModificationDate;
	}

	/**
	 * @param lastModificationDate
	 *            the lastModificationDate to set
	 */
	public void setLastModificationDate(Date lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}

	/**
	 * @param lastModificationDate
	 *            the lastModificationDate to set
	 */
	public void setLastModificationDate(String lastModificationDate) {
		try {
			this.lastModificationDate = DEFAULT_DATE_FORMAT.parse(lastModificationDate);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * no need to use this, this method only add a server to the resource
	 * Manager it is only existing to be called from digester
	 * 
	 * @param server
	 */
	public void addServer(Server server) {
		try {
			
			Server serv = ResourceManager.getInstance().getServer(server.getName());
			if(serv != null) {
				ResourceManager.getInstance().removeServer(serv);
			}
			
			ResourceManager.getInstance().addServer(server);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		try {
			resourceManager.addServer(server);
		} catch (Exception e) {

		}
	}

	/**
	 * add The given transformation to the model If its name is already in use
	 * in the model, it will be postfixed by a number
	 * 
	 * @param tr
	 */
	public void addTransformation(Transformation tr) {
		if (!transformations.contains(tr)) {

			/*
			 * look if this name is already present
			 */

			checkAndUpdateName(tr);

			transformations.add(tr);

			// tr.initDescriptor();
			((AbstractTransformation) tr).setDocumentGateway(this);

			if (tr instanceof SqlLookup) {
				((SqlLookup) tr).initDbStream(this);
			}
		}

		for (Transformation t : transformations) {
			if (tr.getContainer() != null && tr.getContainer().equals(t.getName()) && t instanceof GlobalDefinitionInput) {
				((GlobalDefinitionInput) t).addContent(tr);
			}
		}
		// ((AbstractTransformation)tr).setDocumentGateway(this);
	}

	protected void checkAndUpdateName(Transformation tr) {
		Integer maxFound = null;
		boolean found = false;
		for (Transformation t : transformations) {
			if (t.getName().equals(tr.getName()) && t != tr) {
				found = true;
				String postFix = "";

				int i = t.getName().length() - 1;

				while (Character.isDigit(t.getName().charAt(i))) {
					postFix = t.getName().charAt(i) + postFix;
					i--;
				}

				try {
					if (maxFound == null || maxFound < Integer.parseInt(postFix)) {
						maxFound = Integer.parseInt(postFix);
					}
				} catch (NumberFormatException e) {

				}

			}

		}

		if (maxFound == null && !found) {
			return;
		}
		else {
			if (maxFound == null) {
				tr.setName(tr.getName() + "_1");
				tr.setTemporaryFilename(tr.getTemporaryFilename() + "_1");
				checkAndUpdateName(tr);
				return;
			}
		}

		int i = tr.getName().length() - 1;

		while (Character.isDigit(tr.getName().charAt(i))) {

			i--;
		}

		tr.setName(tr.getName().substring(0, i + 1) + (maxFound + 1));
		tr.setTemporaryFilename(tr.getTemporaryFilename().substring(0, tr.getTemporaryFilename().length() - 1) + (maxFound + 1));
		checkAndUpdateName(tr);

	}

	public String getAsFormatedString() throws Exception {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		XMLWriter w = new XMLWriter(b, OutputFormat.createPrettyPrint());
		w.write(getElement());
		w.close();
		return b.toString("UTF-8");

	}

	/**
	 * 
	 * @return an element to save the entire document as XML
	 */
	public Element getElement() {
		Element el = DocumentHelper.createElement("gatewayDocument");

		/*
		 * general informations
		 */
		el.addElement("name").setText(getName());
		if (id != null) {
			el.addElement("id").setText(id + "");
		}

		el.addElement("description").setText(getDescription());
		el.addElement("author").setText(getAuthor());
		el.addElement("creationDate").setText(DEFAULT_DATE_FORMAT.format(creationDate));
		el.addElement("lastModificationDate").setText(DEFAULT_DATE_FORMAT.format(lastModificationDate));
		el.addElement("version").setText("");
		el.addElement("icon").setText("");
		el.addElement("flyover").setText("");
		el.addElement("projectName").setText(projectName);
		el.addElement("projectVersion").setText(projectVersion);

		el.addElement("mode").setText(mode + "");

		if (repositoryId != null) {
			el.addElement("id").setText("" + repositoryId);
		}

		/*
		 * servers
		 */
		Element servers = DocumentHelper.createElement("servers");

		List<Server> ls = new ArrayList<Server>();

		for (Server s : ResourceManager.getInstance().getServers()) {

			for (Transformation t : transformations) {
				if (t instanceof DataStream &&  ((DataStream) t).getServer() != null && ((DataStream) t).getServer().getName().equals(s.getName())) {
					if (!ls.contains(s)) {
						ls.add(s);
					}

					break;
				}
				else if (t instanceof FreemetricsKPI && ((FreemetricsKPI) t).getServer().getName().equals(s.getName())) {
					if (!ls.contains(s)) {
						ls.add(s);
					}
					break;
				}
				else if (t instanceof ServerHostable && ((ServerHostable) t).getServer().getName().equals(s.getName())) {
					if (!ls.contains(s)) {
						ls.add(s);
					}
					break;
				}
				else if (t instanceof VanillaLdapSynchro) {
					if (!ls.contains(((VanillaLdapSynchro) t).getLdapServer())) {
						ls.add(((VanillaLdapSynchro) t).getLdapServer());
					}
				}
				else if (t instanceof SyriusConnector) {
					if (!ls.contains(((SyriusConnector) t).getServer())) {
						ls.add(((SyriusConnector) t).getServer());
					}
				}
				else if (t instanceof RpuConnector) {
					if (!ls.contains(((RpuConnector) t).getServer())) {
						ls.add(((RpuConnector) t).getServer());
					}
				}
			}

		}

		for (Server s : ls) {
			servers.add(s.getElement());
		}

		el.add(servers);

		/*
		 * parameters
		 */
		Element parameters = DocumentHelper.createElement("parameters");

		for (Parameter p : getParameters()) {
			parameters.add(p.getElement());
		}
		el.add(parameters);

		/*
		 * localVariables
		 */
		Element localVariables = DocumentHelper.createElement("localVariables");

		for (Variable v : getVariables()) {
			localVariables.add(v.getElement());
		}
		el.add(localVariables);

		/*
		 * forms
		 */

		Element forms = DocumentHelper.createElement("forms");
		for (Form f : getForms()) {
			forms.add(f.getElement());
		}
		el.add(forms);

		/*
		 * transformations
		 */
		for (Transformation tr : transformations) {
			el.add(tr.getElement());
		}

		/*
		 * Links
		 */

		for (Transformation tr : transformations) {

			for (Transformation i : tr.getOutputs()) {
				Element l = el.addElement("link");
				l.addElement("from").setText(tr.getName());
				l.addElement("to").setText(i.getName());
				Element bp = l.addElement("bendPoints");
				for (Point p : tr.getBendPoints(i)) {
					Element ep = bp.addElement("point");
					ep.addElement("x").setText(p.x + "");
					ep.addElement("y").setText(p.y + "");
				}
			}

		}
		/*
		 * comments
		 */

		for (Comment c : annotes) {
			el.add(c.getElement());
		}
		return el;

	}

	/**
	 * 
	 * @return all the Transformations contained in this Model
	 */
	public List<Transformation> getTransformations() {
		return transformations;
	}

	/**
	 * 
	 * @param name
	 * @return the Transformation with the given or null if it odesnt exist
	 */
	public Transformation getTransformation(String name) {
		for (Transformation t : transformations) {
			if (t.getName().equals(name)) {
				return t;
			}
		}

		return null;
	}

	/**
	 * create a Link beatween tow Transformation
	 * 
	 * @param from
	 * @param to
	 * @deprecated (shouldnt be used, use Tr.addOUtput instead); this method is
	 *             used by the digester!!
	 */
	public void createLink(String from, String to, List<Point> bendPoints) {
		Transformation source = getTransformation(from);
		Transformation target = getTransformation(to);

		if (source != null && target != null && source != target) {
			source.addOutput(target);// , bendPoints);
			try {
				target.addInput(source);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * remove the given Transformation from the Model
	 * 
	 * @param transfo
	 */
	public void removeTransformation(Transformation transfo) {
		transformations.remove(transfo);

		if (transfo instanceof GlobalDefinitionInput) {
			transformations.removeAll(((GlobalDefinitionInput) transfo).getContent());
		}

	}

	public void setRepositoryid(Integer id) {
		repositoryId = id;
	}

	/**
	 * 
	 * @return all Comments for this model
	 */
	public List<Comment> getAnnotes() {
		return new ArrayList<Comment>(annotes);
	}

	/**
	 * 
	 * @return all Parameters for this model
	 */
	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>(parameters);
	}

	/**
	 * add a parameter for this model
	 * 
	 * @param p
	 * @throws Exception
	 * @see bpm.gateway.core.server.userdefined.Parameter
	 */
	public void addParameter(Parameter p) throws Exception {
		for (Parameter _p : parameters) {
			if (_p.getName().equals(p.getName())) {
				throw new Exception("A parameter named " + p.getName() + " already exists.");
			}
		}
		parameters.add(p);
		ResourceManager.getInstance().addParameter(p);
		resourceManager.addParameter(p);
	}

	/**
	 * remove the parameter P from the model all the associated Forms for that
	 * parameter will be removed too
	 * 
	 * @param p
	 */
	public void removeParameter(Parameter p) {
		parameters.remove(p);

		List<Form> toRemove = new ArrayList<Form>();

		for (Form f : forms) {

			for (String s : f.getMappings().keySet()) {
				if (s.equals(p.getName())) {
					toRemove.add(f);
				}
			}
		}

		for (Form f : toRemove) {
			removeForm(f);
		}

		ResourceManager.getInstance().deleteParameter(p);
		resourceManager.deleteParameter(p);
	}

	/**
	 * 
	 * @param parameterName
	 * @return the Parameter with the given Name or null
	 */
	public Parameter getParameter(String parameterName) {
		for (Parameter p : parameters) {
			if (p.getName().equals(parameterName)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * 
	 * @return the list of defined OrbeonForm
	 */
	public List<Form> getForms() {
		return new ArrayList<Form>(forms);
	}

	/**
	 * add a Form to the model
	 * 
	 * @param form
	 */
	public void addForm(Form form) {
		this.forms.add(form);

	}

	/**
	 * remove the given form from the model
	 * 
	 * @param form
	 */
	public void removeForm(Form form) {
		this.forms.remove(form);
	}

	/**
	 * 
	 * @return the execution Mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * set the execution MOde
	 * 
	 * @param mode
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	public void setMode(String mode) {
		this.mode = Integer.parseInt(mode);
	}

	/**
	 * @return a project Version to perform some crossing references inside the
	 *         vanilla plateform between BiObject
	 */
	public String getProjectVersion() {
		return projectVersion;
	}

	/**
	 * set a project Version to perform some crossing references inside the
	 * vanilla plateform between BiObject
	 * 
	 * @param projectVersion
	 */
	public void setProjectVersion(String projectVersion) {
		this.projectVersion = projectVersion;
	}

	/**
	 * @return a project Name to perform some crossing references inside the
	 *         vanilla plateform between BiObject projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * set a project Name to perform some crossing references inside the vanilla
	 * plateform between BiObject
	 * 
	 * @param projectName
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Variable getVariable(String vname) {
		for (Variable v : getVariables()) {
			if (v.getName().equals(vname)) {
				return v;
			}
		}
		return null;
	}

	//Throw exception if not
	public void checkWellFormed() throws Exception {
		DocumentHelper.createDocument(getElement());
	}

	/**
	 * write the XML into the given Stream this method use the PrettyFormat from
	 * DOM4J OutputFormat
	 * 
	 * @param stream
	 * @throws IOException
	 */
	public void write(OutputStream stream) throws IOException {
		XMLWriter writer = new XMLWriter(stream, OutputFormat.createPrettyPrint());
		Document doc = DocumentHelper.createDocument(getElement());
		doc.setXMLEncoding("UTF-8");
		writer.write(doc);
		writer.close();
	}

	/**
	 * ere
	 * 
	 * @return this.getElement().asXML();
	 */
	public String debug() {
		return this.getElement().asXML();
	}

	public IRepositoryContext getRepositoryContext() {
		return repCtx;
	}

}
