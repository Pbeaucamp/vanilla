package bpm.vanilla.server.commons.server;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.components.IVanillaComponent;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.server.commons.pool.Pool;
import bpm.vanilla.server.commons.server.exceptions.ServerException;
import bpm.vanilla.server.commons.server.tasks.TasksManager;

public abstract class Server {
	protected Logger logger;

	protected TasksManager taskManager;
	private Pool pool;

	private ServerConfig config;
	private String url;

	private boolean started = false;
	boolean inited = false;

	private FactoryServerConfig factoryConfig;
	private IVanillaComponent vanillaComponent;
	private TaskIdGenerator idGenerator = new TaskIdGenerator();

	/**
	 * 
	 * @param logger
	 *            : logger for this Server Object
	 * @param url
	 *            : the Url of this server(may be usefull, like in gatewayServer
	 *            to log activity)
	 */
	public Server(IVanillaComponent vanillaComponent, Logger logger, String url) {
		this.logger = logger;
		this.factoryConfig = new FactoryServerConfig();
		this.url = url;
		this.vanillaComponent = vanillaComponent;
	}

	/**
	 * set the FactoryConfigServer used when reseting server Configuration
	 * 
	 * Subclasses should have their own implementation of FactoryServerConfig
	 * and call this method in their constructor
	 * 
	 * @param factoryConfig
	 */
	protected void setFactoryConfig(FactoryServerConfig factoryConfig) {
		this.factoryConfig = factoryConfig;
	}

	public long generateTaskId() {
		return idGenerator.generate();
	}

	public FactoryServerConfig getFactoryServerConfig() {
		return factoryConfig;
	}

	/**
	 * 
	 * @return the Server's Pool
	 */
	public Pool getPool() {
		return pool;
	}

	/**
	 * create the Pool must be overriden
	 */
	abstract protected void createPool() throws Exception;

	/**
	 * start the server by starting its taskManager
	 * 
	 * @throws ServerException
	 */
	public void start() throws ServerException {
		if (inited == false) {
			throw new ServerException("Cannot start the server before it is inited");
		}
		taskManager.start();
		started = true;
		logger.info("Server started");
	}

	/**
	 * stop the server and its taskManager
	 * 
	 * @throws ServerException
	 */
	public void stop() throws ServerException {
		taskManager.stop(true);
		started = false;
		logger.info("Server stopped");
	}

	/**
	 * initialize the Server with the given config and create the TaskManager -
	 * call the customizedInit() method - call the CreatePool method - create
	 * the taskManager - set the server as inited
	 * 
	 * @param config
	 * @throws Exception
	 */
	public void init(ServerConfig config) throws Exception {
		if (isStarted()) {
			throw new ServerException("Cannot init a server while is running");
		}

		this.config = config;
		customizedInit();
		createPool();

		// taskManager = new MassTaskManager(getClass().getClassLoader(), 1000,
		// this, logger);
		taskManager = new TasksManager(getClass().getClassLoader(), 1000, this, logger);
		logger.info("Server's TaskManager created");
		inited = true;

	}

	/**
	 * allow the implementation to add some init steps without overring the
	 * init() method
	 * 
	 * @throws Exception
	 */
	protected abstract void customizedInit() throws Exception;

	public ServerConfig getConfig() {
		return config;
	}

	/**
	 * 
	 * @return true if the server is on
	 */
	public boolean isStarted() {
		return started;
	}

	public TasksManager getTaskManager() {
		return taskManager;
	}

	protected void setPool(Pool pool) {
		this.pool = pool;

	}

	public void reInit(ServerConfig config) throws Exception {
		if (isStarted()) {
			throw new ServerException("Cannot re-init a server while is running");
		}

		this.config = config;
		customizedInit();
		createPool();
		taskManager.reset(config.getMaxTasks());
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	public IVanillaComponentIdentifier getComponentIdentifier() {
		return vanillaComponent.getIdentifier();
	}
}
