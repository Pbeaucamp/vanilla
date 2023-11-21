package bpm.forms.dao.managers;



import javax.sql.DataSource;

import org.apache.log4j.Logger;

import bpm.forms.core.design.IDefinitionService;
import bpm.forms.core.design.IServiceProvider;
import bpm.forms.core.runtime.IDataSourceProvider;
import bpm.forms.core.runtime.IInstanceService;
import bpm.forms.core.tools.IFactoryModelElement;
import bpm.forms.model.services.FactoryModelElement;


public abstract class DatabaseServiceProvider implements IServiceProvider, IDataSourceProvider{
	
	public static final String PLUGIN_ID = "bpm.forms.dao";
	
	public static final String BEAN_DEFINITION_MANAGER_ID = "definitionManager";
	public static final String BEAN_RUNTIME_MANAGER_ID = "runtimeManager";
	public static final String BEAN_DATASOURCE = "dataSource";
	
	
	private IFactoryModelElement factory = new FactoryModelElement();
	
	private boolean inited = false;
	private DatabaseInstanceService runtimeManager = null;
	private DatabaseDefinitionService definitionManager = null;
	private DataSource dataSource = null;
	
	
	
	public DatabaseServiceProvider() throws Exception{
		init();
	}
	
	
	protected boolean isInited(){
		return inited;
	}
	
	protected void setInited(){
		this.inited = true;
	}
	
	protected DatabaseInstanceService getRuntimeManager() {
		return runtimeManager;
	}
	
	protected DatabaseDefinitionService getDefinitionManager() {
		return definitionManager;
	}
	

	@Override
	public void configure(Object object) {
		
		
	}
	
	final protected void setDefinitionManager(DatabaseDefinitionService definitionService){
		this.definitionManager = definitionService;
		this.definitionManager.configure(this);
	}
	
	final protected void setDataSource(DataSource datasource){
		this.dataSource = datasource;
	}
	
	final protected void setRuntimeManager(DatabaseInstanceService runtimeManager){
		this.runtimeManager = runtimeManager;
		this.runtimeManager.configure(this);
	}
	
	
	@Override
	public IDefinitionService getDefinitionService() {
		if (!isInited()){
			try {
				init();
			} catch (Exception e) {
				Logger.getLogger(getClass()).error("Error initing Spring Beans - " + e.getMessage(), e);
			}
		}
		return definitionManager;
	}

	@Override
	public IInstanceService getInstanceService() {
		if (!isInited()){
			try {
				init();
			} catch (Exception e) {
				Logger.getLogger(getClass()).error("Error initing Spring Beans - " + e.getMessage(), e);
			}
		}
		return runtimeManager;
	}

	@Override
	public DataSource getDataSource() {
		if (!isInited()){
			try {
				init();
			} catch (Exception e) {
				Logger.getLogger(getClass()).error("Error initing Spring Beans - " + e.getMessage(), e);
			}
		}
		return dataSource;
	}
	
	
	abstract protected void init() throws Exception;
	
	public IFactoryModelElement getFactoryModelElement() {
		return factory;
	}

}
