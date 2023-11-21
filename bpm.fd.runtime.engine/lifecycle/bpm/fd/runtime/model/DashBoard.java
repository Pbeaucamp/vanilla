package bpm.fd.runtime.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.FdProjectDescriptor;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.DrillDrivenComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.slicer.ComponentSlicer;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.runtime.engine.I18NReader;
import bpm.fd.runtime.model.controler.QueryProvider;
import bpm.fd.runtime.model.ui.jsp.JSPCanvasGenerator;
import bpm.fd.runtime.model.ui.jsp.JSPRenderer;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DashBoard {
	private DashBoardMeta meta;
	private HashMap<String, DashInstance> instances = new HashMap<String, DashInstance>();
	private IRenderer renderer = new JSPRenderer();
	private List<ComponentRuntime> components = new ArrayList<ComponentRuntime>();
	private QueryProvider queryProvider;

	private HashMap<ComponentParameter, ComponentRuntime> designParameterProvider = new HashMap<ComponentParameter, ComponentRuntime>();
	private String relativeUrl;
	private I18NReader i18Reader;
	private FdProject fdProject;
	private File jspFile;
	private IObjectIdentifier identifier;

	/**
	 * This map stores each dashboard dependancies Id and its modification It allows to check if a Dashboard model/resources files has been update since the deployment of the dashboard
	 * 
	 */
	private HashMap<Integer, Date> itemIdModificationDates = new HashMap<Integer, Date>();
	/**
	 * we store the repository context for conveniance, other wise some lookup will be needed to rebuilt the IRepositoryApi when checking a dashboard update status
	 */
	private IRepositoryContext ctx;

	public DashBoard(IObjectIdentifier identifier, File jspFile, FdProject project, IRepositoryContext iRepositoryContext) throws Exception {
		ctx = iRepositoryContext;
		this.fdProject = project;
		this.jspFile = jspFile;
		this.identifier = identifier;
		queryProvider = new QueryProvider(getProject().getFdModel());
		setMeta(new DashBoardMeta(new Date(), identifier, getProject()));
		i18Reader = new I18NReader(getFdProject());
		if(!jspFile.exists()) {
			jspFile.mkdirs();
			Logger.getLogger(getClass()).info("Folder " + jspFile.getAbsolutePath() + " created to deploy Dashboard");
		}
	}

	protected List<ComponentRuntime> getComponents() {
		return new ArrayList<ComponentRuntime>(components);
	}

	protected void setMeta(DashBoardMeta meta) {
		this.meta = meta;
	}

	/**
	 * init the JSP(creation, file saving) Must be called just after the constructor to build the JSP
	 */
	public void init(boolean override) throws Exception {
		components.clear();
		if(getProject() instanceof MultiPageFdProject) {

			for(IBaseElement e : getProject().getFdModel().getContent()) {
				if(e instanceof Folder) {
					ComponentContainer<Folder> componentFolder = new ComponentContainer<Folder>((Folder) e);
					
					//FIXME : add all the components as target of the folder
					//so we can reload only what is needed (instead of all the non displayed components....)
					
					componentFolder.addTarget(componentFolder);
					addComponent(componentFolder);				
				}
				else if(e instanceof StackableCell) {
					ComponentContainer<StackableCell> componentFolder = new ComponentContainer<StackableCell>((StackableCell) e);
					componentFolder.addTarget(componentFolder);
					addComponent(componentFolder);
				}
			}
		}

		HashMap<IComponentDefinition, ComponentConfig> c = getProject().getComponents();

		boolean freeLayout = FdProjectDescriptor.API_DESIGN_VERSION.equals(getProject().getProjectDescriptor().getInternalApiDesignVersion());
		for(IComponentDefinition d : c.keySet()) {
			addComponent(new Component(d, c.get(d), freeLayout));
		}

		for(ComponentConfig conf : c.values()) {
			for(ComponentParameter p : conf.getParameters()) {
				String providerName = conf.getComponentNameFor(p);
				if(providerName != null) {
					try {
						ComponentRuntime target = getComponent(conf.getTargetComponent().getName());
						ComponentRuntime provider = getComponent(providerName);
						provider.addTarget(target);
						designParameterProvider.put(p, provider);
					} catch(Exception ex) {
						throw new Exception("Some parameters are not set or the providers are not within the DashBoard (" + conf.getTargetComponent().getName() + ")", ex);
					}
				}
			}
			if(conf instanceof DrillDrivenComponentConfig) {
				IComponentDefinition controlerDef = ((DrillDrivenComponentConfig) conf).getController();

				ComponentRuntime controler = getComponent(controlerDef.getName());

				ComponentContainer<DrillDrivenStackableCell> componentContainer = new ComponentContainer<DrillDrivenStackableCell>(((DrillDrivenComponentConfig) conf).getDrillDrivenCell());
				controler.addTarget(componentContainer);

				addComponent(componentContainer);

			}
		}

		/*
		 * we add the slicers dependancies All component using the slicer's dataset are dependants
		 */
		for(ComponentRuntime component : getComponents()) {
			if(component.getElement() instanceof ComponentSlicer) {
				DataSet lookedDataSet = ((ComponentSlicer) component.getElement()).getDatas().getDataSet();

				if(lookedDataSet != null) {
					for(ComponentRuntime cc : getComponents()) {
						if(cc.getElement() instanceof IComponentDefinition && cc != component) {
							IComponentDatas dt = ((IComponentDefinition) cc.getElement()).getDatas();
							if(dt != null && dt.getDataSet() == lookedDataSet) {
								component.addTarget(cc);
							}
						}
					}

				}

			}
		}

		/*
		 * copy resources files
		 */

		for(IResource r : getProject().getFdModel().getProject().getResources()) {
			try {

				File deploymentFolder = new File(jspFile, meta.getIdentifierString());
				if(!deploymentFolder.exists()) {
					deploymentFolder.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(new File(deploymentFolder, r.getName()));
				FileInputStream fis = new FileInputStream(r.getFile());

				byte[] buffer = new byte[1024];
				int sz;

				while((sz = (fis.read(buffer))) > 0) {
					fos.write(buffer, 0, sz);

				}

				fos.close();
				fis.close();
			} catch(Exception e) {
				e.printStackTrace();
			}

		}

		String jspCode = generateJspCode();
		
		String jspName = getProject().getFdModel().getName().replace(" ", "_");
		relativeUrl = meta.getIdentifierString() + "/" + jspName + ".jsp";
		File f = new File(jspFile, meta.getIdentifierString());
		if(!f.exists()) {
			f.mkdirs();
		}
		PrintWriter fw = new PrintWriter(jspFile + "/" + relativeUrl, "UTF-8");
		fw.write(jspCode);
		fw.close();

		/*
		 * set Modification Dates
		 */
		if(!override) {
			try {
//				VanillaConfiguration cf = ConfigurationManager.getInstance().getVanillaConfiguration();
//				IVanillaContext vCtx = new BaseVanillaContext(cf.getVanillaServerUrl(), cf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), cf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
//				IVanillaAPI vApi = new RemoteVanillaPlatform(vCtx);

//				Repository repDef = vApi.getVanillaRepositoryManager().getRepositoryById(identifier.getRepositoryId());
//				Group dummy = new Group();
//				//TODO: Attention remplacer le groupe par celui de l'utilisateur
//				dummy.setId(Integer.parseInt(cf.getProperty(VanillaConfiguration.P_PUBLIC_GROUP_ID)));
//				ctx = new BaseRepositoryContext(vCtx, dummy, repDef);
				IRepositoryApi repApi = new RemoteRepositoryApi(ctx);

				RepositoryItem item = repApi.getRepositoryService().getDirectoryItem(identifier.getDirectoryItemId());
				itemIdModificationDates.put(item.getId(), item.getDateModification() == null ? item.getDateCreation() : item.getDateModification());

				for(RepositoryItem it : repApi.getRepositoryService().getNeededItems(item.getId())) {
					itemIdModificationDates.put(it.getId(), it.getDateModification() == null ? it.getDateCreation() : it.getDateModification());
				}
			} catch(Exception ex) {
				throw new Exception("Unable to get the DashBoard items dates " + ex.getMessage(), ex);
			}
		}
		if(ctx == null) {
			VanillaConfiguration cf = ConfigurationManager.getInstance().getVanillaConfiguration();
			IVanillaContext vCtx = new BaseVanillaContext(cf.getVanillaServerUrl(), cf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), cf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
			IVanillaAPI vApi = new RemoteVanillaPlatform(vCtx);

			Repository repDef = vApi.getVanillaRepositoryManager().getRepositoryById(identifier.getRepositoryId());
			Group dummy = new Group();
			//TODO: Attention remplacer le groupe par celui de l'utilisateur
			dummy.setId(Integer.parseInt(cf.getProperty(VanillaConfiguration.P_PUBLIC_GROUP_ID)));
			ctx = new BaseRepositoryContext(vCtx, dummy, repDef);
		}

	}

	public synchronized void performUpdate(FdProject project) throws Exception {
		HashMap<Integer, Date> currentDates = new HashMap<Integer, Date>();

		IRepositoryApi repApi = new RemoteRepositoryApi(ctx);
		RepositoryItem item = repApi.getRepositoryService().getDirectoryItem(identifier.getDirectoryItemId());
		currentDates.put(item.getId(), item.getDateModification() == null ? item.getDateCreation() : item.getDateModification());

		for(RepositoryItem it : repApi.getRepositoryService().getNeededItems(item.getId())) {
			currentDates.put(it.getId(), it.getDateModification() == null ? it.getDateCreation() : it.getDateModification());
		}

		boolean changed = false;
		if(itemIdModificationDates.size() == currentDates.size()) {
			for(Integer k : itemIdModificationDates.keySet()) {
				if(itemIdModificationDates.get(k).before(currentDates.get(k.intValue()))) {
					changed = true;
					break;
				}
			}
		}
		else {
			changed = true;
		}

		if(changed) {
			this.fdProject = project;
			queryProvider = new QueryProvider(this.fdProject.getFdModel());
			setMeta(new DashBoardMeta(new Date(), identifier, this.fdProject));

			instances.clear();
			init(false);
		}

	}

	protected FdProject getFdProject() {
		return fdProject;
	}

	protected String generateJspCode() throws Exception {
		JSPCanvasGenerator canva = new JSPCanvasGenerator(meta, fdProject.getFdModel());
		return canva.getJsp();
	}

	public I18NReader getI18Reader() {
		return i18Reader;
	}

	public FdProject getProject() {
		return fdProject;
	}

	protected String getRelativeUrl() {
		return relativeUrl;
	}

	private void addComponent(ComponentRuntime c) {
		for(ComponentRuntime cr : components) {
			if(c == cr) {
				return;
			}

		}
		components.add(c);
	}

	public ComponentRuntime getDesignParameterProvider(ComponentParameter p) {
		return designParameterProvider.get(p);
	}

	public QueryProvider getQueryProvider() {
		return queryProvider;
	}

	public DashBoardMeta getMeta() {
		return meta;
	}

	/**
	 * create a new DashInstance for the given objects
	 * 
	 * @param group
	 * @param user
	 * @param languageLocale
	 * @return
	 */
	public DashInstance createInstance(Group group, User user, String languageLocale) {
		DashInstance instance = new DashInstance(this, group, user, languageLocale);

		synchronized(instances) {
			instances.put(instance.getUuid(), instance);
		}

		return instance;
	}

	/**
	 * return the component with the given name
	 * 
	 * @param componentName
	 * @return
	 * @throws Exception
	 *             : if the given name do not match to a component
	 */
	public ComponentRuntime getComponent(String componentName) throws Exception {
		for(ComponentRuntime c : components) {
			if(c.getName().equals(componentName)) {
				return c;
			}
		}

		throw new Exception("The Dashboard does not contains any component named " + componentName);
	}

	/**
	 * return the components that are providing a parameter to the given component
	 * 
	 * @param c
	 * @return
	 */
	public List<ComponentRuntime> getComponentProvider(Component c) {
		List<ComponentRuntime> l = new ArrayList<ComponentRuntime>();
		for(ComponentRuntime cc : components) {
			if(cc.getTargets(false).contains(c)) {
				l.add(cc);
			}
		}
		return l;
	}

	public IRenderer getRenderer() {
		return renderer;
	}

	public DashInstance getInstance(String uuid) {
		return instances.get(uuid);
	}

	public Collection<String> getInstancesUuids() {
		return instances.keySet();
	}

	public File getJspFile() {
		return jspFile;
	}
	
	public IRepositoryContext getCtx() {
		return ctx;
	}

}
