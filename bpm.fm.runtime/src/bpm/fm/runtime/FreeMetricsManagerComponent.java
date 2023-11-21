package bpm.fm.runtime;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.api.model.AlertRaised;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.CalculatedFactTable;
import bpm.fm.api.model.CalculatedFactTableMetric;
import bpm.fm.api.model.Comment;
import bpm.fm.api.model.CommentAlert;
import bpm.fm.api.model.ComplexMap;
import bpm.fm.api.model.ComplexMapAxis;
import bpm.fm.api.model.ComplexMapLevel;
import bpm.fm.api.model.ComplexMapMetric;
import bpm.fm.api.model.ComplexObjectViewer;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.FactTableAxis;
import bpm.fm.api.model.FactTableObjectives;
import bpm.fm.api.model.GroupObservatory;
import bpm.fm.api.model.HasItemLinked;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.MetricAction;
import bpm.fm.api.model.MetricLinkedItem;
import bpm.fm.api.model.MetricMap;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.ObservatoryTheme;
import bpm.fm.api.model.Theme;
import bpm.fm.api.model.ThemeAxis;
import bpm.fm.api.model.ThemeMetric;
import bpm.fm.api.model.utils.AxisInfo;
import bpm.fm.api.model.utils.LevelMember;
import bpm.fm.api.model.utils.LoaderDataContainer;
import bpm.fm.api.model.utils.MapZoneValue;
import bpm.fm.api.model.utils.MetricSqlQueries;
import bpm.fm.api.model.utils.MetricValue;
import bpm.fm.runtime.dao.FreeMetricsDao;
import bpm.fm.runtime.dao.FreeMetricsJdbc;
import bpm.fm.runtime.utils.DatabaseElementSqlGenerator;
import bpm.fm.runtime.utils.LoaderValueCalculator;
import bpm.fm.runtime.utils.LoaderValueUpdater;
import bpm.fm.runtime.utils.MapValueCalculator;
import bpm.fm.runtime.utils.ValueCalculator;
import bpm.freemetrics.api.manager.FactoryManager;
import bpm.freemetrics.api.manager.IManager;
import bpm.freemetrics.api.organisation.metrics.Metric;
import bpm.freemetrics.api.organisation.metrics.MetricValues;
import bpm.freemetrics.api.utils.IConstants;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.file.MdmFileServer;
import bpm.gateway.core.transformations.inputs.FMDTInput;
import bpm.gateway.core.transformations.inputs.FileInputCSV;
import bpm.gateway.core.transformations.kml.KMLInput;
import bpm.gateway.core.transformations.mdm.MdmContractFileInput;
import bpm.gateway.core.transformations.outputs.DataBaseOutputStream;
import bpm.mdm.model.supplier.Contract;
import bpm.metadata.MetaDataReader;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.query.IQuery;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SavedQuery;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.IMapDefinitionService;
import bpm.vanilla.map.core.design.IZoneTerritoryMapping;
import bpm.vanilla.map.core.design.MapInformation;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapSpecificationEntity;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapSpecificationEntity;
import bpm.vanilla.map.core.design.openlayers.OpenLayersMapXmlParser;
import bpm.vanilla.map.core.design.openlayers.OpenLayersPoint;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.FMContext;
import bpm.vanilla.platform.core.beans.FMMapValueBean;
import bpm.vanilla.platform.core.beans.FMMapValueBean.MapZonePoint;
import bpm.vanilla.platform.core.beans.FMMetricBean;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.AlertKpi;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.impl.AbstractVanillaComponent;
import bpm.vanilla.platform.core.components.impl.VanillaHttpContext;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanilla.platform.logging.IVanillaLoggerService;

import com.thoughtworks.xstream.XStream;

public class FreeMetricsManagerComponent extends AbstractVanillaComponent implements IFreeMetricsManager {
	
	private IManager fmManager;
	private IMapDefinitionService mapService;
	private IVanillaLoggerService loggerService;
	private HttpService httpService;
	
	private IVanillaAPI api;
	
	private Status status = Status.UNDEFINED;
	private ComponentContext context = null;
	private FreeMetricsDao dao;
	private ValueCalculator valueCalculator;
	private LoaderValueCalculator loaderValueCalculator;
	private MapValueCalculator mapValueCalculator;
	private FreeMetricsJdbc jdbcManager;

	private XStream xstream = new XStream();
	
	@Override
	public List<FMMapValueBean> getFreeMetricsValues(int metric, int mapId, Date date, FMContext fmContext) throws Exception {
		List<FMMapValueBean> values = new ArrayList<FMMapValueBean>();
		if(!getManager().authentify(fmContext.getUser(), fmContext.getPassword(), fmContext.isEncrypted(), getManager())) {
			throw new Exception("User is not allowed to use VanillaKpi");
		}
		else {
			
			List<Integer> applicationIds = new ArrayList<Integer>();
			
			IMapDefinition mapDef = getMapService().getMapDefinition(mapId);
			if(mapDef.getFusionMapObjectId() != null) {
				List<IFusionMapSpecificationEntity> entities = mapDef.getFusionMapObject().getSpecificationsEntities();
				List<IZoneTerritoryMapping> mappings = getMapService().getZoneTerritoryMappingByMapId(mapId);
				
				for(IZoneTerritoryMapping mapping : mappings) {
					applicationIds.add(mapping.getTerritoryId());
				}
				
				
				
				for(int appID : applicationIds) {
					FMMapValueBean bean = createFmMapValue(appID, metric, date);
					if(bean != null) {
						
						String zoneId = findZoneId(appID, mappings, entities);
						bean.setZoneId(zoneId);
						
						values.add(bean);
					}
				}
				
			}
			
			
			else if(mapDef.getOpenLayersObjectId() != null) {
				List<IOpenLayersMapSpecificationEntity> entities = mapDef.getOpenLayersMapObject().getEntities();
				List<IZoneTerritoryMapping> mappings = getMapService().getZoneTerritoryMappingByMapId(mapId);
				
				for(IZoneTerritoryMapping mapping : mappings) {
					applicationIds.add(mapping.getTerritoryId());
				}
				
				String xml = mapDef.getOpenLayersMapObject().getXml();
				HashMap<String, List<OpenLayersPoint>> zonePoints = OpenLayersMapXmlParser.parseMapZone(xml);
				
				for(int appID : applicationIds) {
					FMMapValueBean bean = createFmMapValue(appID, metric, date);
					if(bean != null) {
						
						String zoneId = findZoneId(appID, mappings, entities);
						bean.setZoneId(zoneId);
						
						bean.setPoints(createPoints(zonePoints, zoneId, bean));
						
						values.add(bean);
						
					}
				}
			}
			
		}
		return values;
	}

	private List<MapZonePoint> createPoints(HashMap<String, List<OpenLayersPoint>> zonePoints, String zoneId, FMMapValueBean bean) {
		List<MapZonePoint> points = new ArrayList<MapZonePoint>();
		for(OpenLayersPoint point : zonePoints.get(zoneId)) {
			
			MapZonePoint po = bean.new MapZonePoint(point.getLatitude(), point.getLongitude());
			points.add(po);
		}
		return points;
	}

	private String findZoneId(int appID, List<IZoneTerritoryMapping> mappings, List entities) {
		
		for(IZoneTerritoryMapping mapping : mappings) {
			if(mapping.getTerritoryId().equals(appID)) {
				for(Object ent : entities) {
					if(ent instanceof IFusionMapSpecificationEntity) {
						if(mapping.getMapZoneId() == (((IFusionMapSpecificationEntity)ent).getId())) {
							return ((IFusionMapSpecificationEntity)ent).getFusionMapInternalId();
						}
					}
					else if(ent instanceof IOpenLayersMapSpecificationEntity) {
						if(mapping.getMapZoneId().intValue() == ((IOpenLayersMapSpecificationEntity)ent).getId().intValue()) {
							return ((IOpenLayersMapSpecificationEntity)ent).getInternalId();
						}
					}
				}
			}
		}
		
		return null;
	}

	private FMMapValueBean createFmMapValue(int appID, int metric, Date date) throws Exception {
		List<MetricValues> fmValues = getManager().getValuesForMetricAppPeridDate(appID, metric, date);
		if(fmValues != null && fmValues.size() > 0) {
			FMMapValueBean bean = new FMMapValueBean();
			bean.setApplicationId(appID);
			bean.setMetricId(metric);
			bean.setId(fmValues.get(0).getId());
			bean.setAssocId(fmValues.get(0).getMvGlAssoc_ID());
			double finalValue = 0.0;
			for(MetricValues val : fmValues) {
				finalValue += val.getMvValue();
			}
			bean.setValue(finalValue);

			return bean;
		}
		return null;
	}

	@Override
	public List<FMMetricBean> getMetrics(int mapId, FMContext fmContext) throws Exception {
		List<FMMetricBean> metrics = new ArrayList<FMMetricBean>();
		if(!getManager().authentify(fmContext.getUser(), fmContext.getPassword(), fmContext.isEncrypted(), getManager())) {
			throw new Exception("User is not allowed to use VanillaKpi");
		}
		else {
			List<Integer> applicationIds = new ArrayList<Integer>();
			
			List<IZoneTerritoryMapping> mappings = getMapService().getZoneTerritoryMappingByMapId(mapId);
			for(IZoneTerritoryMapping mapping : mappings) {
				applicationIds.add(mapping.getTerritoryId());
			}
			
			List<String> possibleYears = getManager().getPossibleYearsByApplicationIds(applicationIds);
			
			List<Metric> fmMetrics = getManager().getMetricsByApplicationIds(applicationIds, IConstants.METRIC_FILTER_COMPTEUR);
			for(Metric met : fmMetrics) {
				
				FMMetricBean bean = new FMMetricBean(met.getId(), met.getName(), met.getMdGlIsCompteur(), met.getMdCalculationTimeFrame());
				bean.setPossibleYears(possibleYears);
				metrics.add(bean);
			}
		}
		return metrics;
	}

	private IManager getManager() throws Exception {
		if(fmManager == null) {			
			FactoryManager.init("", 1);
			fmManager = FactoryManager.getManager();
		}
		return fmManager;
	}
	
	public void activate(ComponentContext ctx) throws Throwable {
		
		context = ctx;
		status = Status.STARTING;
		
		try {
			registerServlets();
			getLogger().info("FreeMetrics servlet registered");
			try {
				ServiceReference ref = ctx.getBundleContext().getServiceReference(HttpService.class.getName());
				String port = (String)ref.getProperty("http.port");
			
				registerInVanilla(VanillaComponentType.COMPONENT_FREEMETRICS, "freeMetricsRuntime", port);
				status = Status.STARTED;
			} catch (Exception e) {
				throw new RuntimeException("Excetion while regiter freeMetrics Service in vanilla", e);
			}
			try {
				
				ApplicationContext factory = new ClassPathXmlApplicationContext("/bpm/fm/runtime/dao/freemetrics_context.xml") {
					
				    protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
				        super.initBeanDefinitionReader(reader);
				        reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);
						// This the important line and available since Equinox 3.7
						ClassLoader loader = FreeMetricsManagerComponent.this.getClass().getClassLoader();
				        reader.setBeanClassLoader(loader);
				    }
				
				};
		
				dao = (FreeMetricsDao) factory.getBean("freeMetricsDao");
				
			} catch (Exception e) {
				throw new RuntimeException("Excetion while init dao", e);
			}
			
			try {
				
				String url = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_FREEMETRICS_DB_JDBCURL);
				String login = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_FREEMETRICS_DB_USERNAME);
				String password = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_FREEMETRICS_DB_PASSWORD);
				String driverClass = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_FREEMETRICS_DB_DRIVERCLASSNAME);
				
				jdbcManager = new FreeMetricsJdbc(url, login, password, driverClass, dao);
			} catch (Exception e) {
				throw new RuntimeException("Excetion while init jdbc manager", e);
			}
			
			this.valueCalculator = new ValueCalculator(this);
			this.loaderValueCalculator = new LoaderValueCalculator(this);
			this.mapValueCalculator = new MapValueCalculator(this);
			
			
		} catch (Throwable e) {
			getLogger().error("FreeMetrics component unable to start : ", e);
			status = Status.ERROR;
			throw e;
		}
	}
	
	public void deactivate(ComponentContext ctx) {
		status = Status.STOPPING;
		try {
			unregisterFromVanilla(getIdentifier());
			status = Status.STOPPED;
		} catch (Exception e) {
			status = Status.ERROR;
			throw new RuntimeException("Excetion while unregister freeMetrics Service from vanilla", e);
		}
	}
	
	public IMapDefinitionService getMapService() throws Exception {
		if(mapService == null) {
			VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
			mapService = new RemoteMapDefinitionService();
			mapService.configure(config.getVanillaServerUrl());
		}
		return mapService;
	}
	
	public IVanillaLogger getLogger() {
		return loggerService.getLogger(getClass().getName());
	}
	
	public void bind(IVanillaLoggerService service) {
		loggerService = service;
	}
	
	public void unbind(IVanillaLoggerService service) {
		loggerService = null;
	}
	
	public void bind(HttpService service) {
		httpService = service;
	}
	
	public void unbind(HttpService service) {
		httpService = null;
	}
	
	public void registerServlets() {
		try {
			
			
			VanillaHttpContext httpCtx = new VanillaHttpContext(getVanillaApi().getVanillaSecurityManager(), getVanillaApi().getVanillaSystemManager());
			httpService.registerServlet(VanillaConstants.FREEMETRICS_MANAGER_URL, new FreeMetricsServlet(this, getLogger()), null, httpCtx);
			httpService.registerServlet(VanillaConstants.FREEMETRICSEXCEL_MANAGER_URL, new KpitExcelServlet(this, getLogger()), null, null);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public IVanillaAPI getVanillaApi() {
		if(api == null) {
			VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
			
			api = new RemoteVanillaPlatform(config.getVanillaServerUrl(),
					config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN),
					config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));		
		}
		return api;
	}
	
	@Override
	public Status getStatus() {
		return status;
	}
	
	@Override
	protected void doStop() throws Exception {
		context.disableComponent(FreeMetricsManagerComponent.class.toString());
	}

	@Override
	protected void doStart() throws Exception {
		context.enableComponent(FreeMetricsManagerComponent.class.toString());
	}

	@Override
	public void notify(IVanillaEvent event) {}

	public FreeMetricsDao getDao() {
		return dao;
	}

	@Override
	public List<bpm.fm.api.model.Metric> getMetrics() throws Exception {
		return dao.getMetrics();
	}

	@Override
	public List<Axis> getAxis() throws Exception {
		return dao.getAxes();
	}

	@Override
	public bpm.fm.api.model.Metric addMetric(bpm.fm.api.model.Metric metric) throws Exception {
		
		if(metric.getMetricType().equals(bpm.fm.api.model.Metric.METRIC_TYPES.get(bpm.fm.api.model.Metric.TYPE_CLASSIC))) {
		
			FactTable table = new FactTable();
			int metricId = (Integer) dao.save(metric);
			metric.setId(metricId);
			
			table.setMetricId(metricId);
			
			int tableId = (Integer) dao.save(table);
			table.setId(tableId);
			
			metric.setFactTable(table);
			
			FactTableObjectives objectives = new FactTableObjectives();
			objectives.setFactTableId(tableId);
			
			dao.save(objectives);
			
			for(FactTableAxis axis : table.getFactTableAxis()) {
				axis.setFactTableId(tableId);
				dao.save(axis);
			}
		}
		
		else {
			CalculatedFactTable table = new CalculatedFactTable();
			int metricId = (Integer) dao.save(metric);
			metric.setId(metricId);
			table.setMetricId(metricId);
			dao.save(table);
		}
		return metric;
	}

	@Override
	public Axis addAxis(Axis axe) throws Exception {
		int axeId = 0;
		if(axe.getId() > 0) {
			dao.update(axe);
			axeId = axe.getId();
		}
		else {
			axeId = (Integer) dao.save(axe);
			axe.setId(axeId);
		}
		
		int index = 0;
		for(Level level : axe.getChildren()) {
			level.setParentId(axeId);
			level.setLevelIndex(index);
			index++;
		}
		
		dao.saveOrUpdateAll(axe.getChildren());
		return axe;
	}

	@Override
	public bpm.fm.api.model.Metric updateMetric(bpm.fm.api.model.Metric metric) throws Exception {
		if(metric.getMetricType().equals(bpm.fm.api.model.Metric.METRIC_TYPES.get(bpm.fm.api.model.Metric.TYPE_CLASSIC))) {
			for(FactTableAxis axis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
				axis.setFactTableId(((FactTable)metric.getFactTable()).getId());
			}
			if (((FactTable)metric.getFactTable()).getObjectives() != null) {
				dao.update(((FactTable)metric.getFactTable()).getObjectives());
			}
			dao.saveOrUpdateAll(((FactTable)metric.getFactTable()).getFactTableAxis());
			
			List<FactTableAxis> faxis = dao.getFactTableAxis(((FactTable)metric.getFactTable()).getId());
			List<FactTableAxis> toRemove = new ArrayList<FactTableAxis>();
			LOOK:for(FactTableAxis fa : faxis) {
				for(FactTableAxis axis : ((FactTable)metric.getFactTable()).getFactTableAxis()) {
					if(axis.getAxisId() == fa.getAxisId()) {
						continue LOOK;
					}
				}
				toRemove.add(fa);
			}
			dao.deleteAll(toRemove);
			
		}
		else {
			for(CalculatedFactTableMetric m : ((CalculatedFactTable)metric.getFactTable()).getMetrics()) {
				m.setFactId(((CalculatedFactTable)metric.getFactTable()).getId());
			}		
			dao.saveOrUpdateAll(((CalculatedFactTable)metric.getFactTable()).getMetrics());
		}
//		dao.saveOrUpdateAll(metric.getAlerts());
		dao.update(metric.getFactTable());
		dao.update(metric);
		
		List<MetricLinkedItem> existing = getMetric(metric.getId()).getLinkedItems();
		dao.deleteAll(existing);
		
		dao.saveOrUpdateAll(metric.getLinkedItems());
		
		return metric;
	}

	@Override
	public Axis updateAxis(Axis axe) throws Exception {
		
		//find if some levels have been removed
		List<Level> levels = getLevels(axe.getId());
		
		List<Level> toRemove = new ArrayList<>();
		
		LOOK:for(Level lvl : levels) {
			for(Level lvl2 : axe.getChildren()) {
				if(lvl2.getId() == lvl.getId()) {
					continue LOOK;
				}
			}
			toRemove.add(lvl);
		}
		
		dao.deleteAll(toRemove);
		
		//order the levels
		int index = 0;
		for(Level lvl : axe.getChildren()) {
			lvl.setLevelIndex(index);
			index++;
			lvl.setParentId(axe.getId());
		}
		
		dao.saveOrUpdateAll(axe.getChildren());
		dao.update(axe);
		
		return axe;
	}

	@Override
	public void deleteMetric(bpm.fm.api.model.Metric metric) throws Exception {
		try {
			dao.deleteAll(((FactTable)metric.getFactTable()).getFactTableAxis());
		} catch (Exception e) {
		}
		try {
			dao.delete(((FactTable)metric.getFactTable()).getObjectives());
		} catch (Exception e) {
		}
		try {
			dao.deleteAll(((CalculatedFactTable)metric.getFactTable()).getMetrics());
		} catch (Exception e) {
		}
		dao.delete(metric.getFactTable());
		
		dao.deleteAll(getThemeMetricByMetric(metric.getId()));
		
		dao.delete(metric);
	}

	@Override
	public void deleteAxis(Axis axe) throws Exception {
		dao.deleteAll(axe.getChildren());
		dao.deleteAll(getThemeAxisByAxis(axe.getId()));
		dao.delete(axe);
	}

	@Override
	public bpm.fm.api.model.Metric getMetric(int id) throws Exception {
		return dao.getMetric(id);
	}

	@Override
	public Axis getAxis(int id) throws Exception {
		return dao.getAxe(id);
	}

	@Override
	public List<Level> getLevels(int axeId) throws Exception {
		return dao.getLevels(axeId);
	}

	@Override
	public Level getLevel(int id) throws Exception {
		return dao.getLevel(id);
	}

	@Override
	public void addLevel(Level level) throws Exception {
		dao.save(level);
	}

	@Override
	public void deleteLevel(Level level) throws Exception {
		dao.delete(level);
	}

	@Override
	public void updateLevel(Level level) throws Exception {
		dao.update(level);
	}

	@Override
	public HashMap<bpm.fm.api.model.Metric, MetricValue> getValuesByDate(Date date, int groupId) throws Exception {
		HashMap<bpm.fm.api.model.Metric, MetricValue> result = new HashMap<bpm.fm.api.model.Metric, MetricValue>();
		
		HashMap<bpm.fm.api.model.Metric, List<MetricValue>> values = valueCalculator.getValues(date, null, false, groupId);
		
		for(bpm.fm.api.model.Metric key : values.keySet()) {
			try {
				result.put(key, values.get(key).get(0));
			} catch (Exception e) {
			}
		}
		
		return result;
	}

	@Override
	public MetricValue getValueByMetricAndDate(Date date, int metricId, int groupId) throws Exception {
		List<MetricValue> values = valueCalculator.getValues(date, null, getMetric(metricId), false, groupId);
		return values.get(0);
	}

	@Override
	public HashMap<bpm.fm.api.model.Metric, List<MetricValue>> getValuesByDateAndAxis(Date date, int axisId, int groupId) throws Exception {
		HashMap<bpm.fm.api.model.Metric, List<MetricValue>> values = valueCalculator.getValues(date, getAxis(axisId), false, groupId);
		return values;
	}

	@Override
	public HashMap<bpm.fm.api.model.Metric, List<MetricValue>> getValuesByDateAndAxisAndMetric(Date date, int axisId, int metricId, int groupId) throws Exception {
		bpm.fm.api.model.Metric metric = getMetric(metricId);
		List<MetricValue> values = valueCalculator.getValues(date, getAxis(axisId), metric, false, groupId);
		
		HashMap<bpm.fm.api.model.Metric, List<MetricValue>> result = new HashMap<bpm.fm.api.model.Metric, List<MetricValue>>();
		
		result.put(metric, values);
		
		return result;
	}

	@Override
	public HashMap<bpm.fm.api.model.Metric, List<MetricValue>> getValuesAndPreviousByDate(Date date, int groupId) throws Exception {
		HashMap<bpm.fm.api.model.Metric, List<MetricValue>> values = valueCalculator.getValues(date, null, true, groupId);
		
		return values;
	}

	@Override
	public List<MetricValue> getValueAndPreviousByMetricAndDate(Date date, int metricId, int groupId) throws Exception {
		List<MetricValue> values = valueCalculator.getValues(date, null, getMetric(metricId), true, groupId);
		return values;
	}

	@Override
	public HashMap<bpm.fm.api.model.Metric, List<MetricValue>> getValuesAndPreviousByDateAndAxis(Date date, int axisId, int groupId) throws Exception {
		HashMap<bpm.fm.api.model.Metric, List<MetricValue>> values = valueCalculator.getValues(date, getAxis(axisId), true, groupId);
		return values;
	}

	@Override
	public HashMap<bpm.fm.api.model.Metric, List<MetricValue>> getValuesAndPreviousByDateAndAxisAndMetric(Date date, int axisId, int metricId, int groupId) throws Exception {
		bpm.fm.api.model.Metric metric = getMetric(metricId);
		List<MetricValue> values = valueCalculator.getValues(date, getAxis(axisId), metric, true, groupId);
		
		HashMap<bpm.fm.api.model.Metric, List<MetricValue>> result = new HashMap<bpm.fm.api.model.Metric, List<MetricValue>>();
		
		result.put(metric, values);
		
		return result;
	}

	@Override
	public List<LoaderDataContainer> getValuesForLoader(List<Integer> metricIds, Date date) throws Exception {
		return getValuesForLoader(metricIds, date, null);
	}

	@Override
	public List<LoaderDataContainer> getValuesForLoader(List<Integer> metricIds, Date firstDate, Date lastDate) throws Exception {
		List<LoaderDataContainer> res = new ArrayList<>();
		for(int id : metricIds) {
			res.add(loaderValueCalculator.getLoaderData(getMetric(id), firstDate, lastDate));

		}
		return res;
	}

//	@Override
//	public List<MetricAlert> getAlerts(int metricId) throws Exception {
//		return dao.getAlerts(getMetric(metricId));
//	}
//
//	@Override
//	public void addAlert(MetricAlert alert) throws Exception {
//		int id = (Integer) dao.save(alert);
//		alert.getCondition().setAlertId(id);
//		dao.save(alert.getCondition());
//		alert.getEvent().setAlertId(id);
//		dao.save(alert.getEvent());
//	}
//
//	@Override
//	public void updateAlert(MetricAlert alert) throws Exception {
//		dao.update(alert);
//		dao.saveOrUpdate(alert.getCondition());
//		dao.saveOrUpdate(alert.getEvent());
//	}

	@Override
	public void deleteAlert(Alert alert) throws Exception {
		String repositoryId = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_PUBLIC_REPOSITORY_ID);
		String groupId = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_PUBLIC_GROUP_ID);
		
		Group group = api.getVanillaSecurityManager().getGroupById(Integer.parseInt(groupId));
		bpm.vanilla.platform.core.beans.Repository repository = api.getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(repositoryId));
		IRepositoryApi repoApi = new RemoteRepositoryApi(new BaseRepositoryContext(api.getVanillaContext(), group, repository));
		
		repoApi.getAlertService().removeAlert(alert);
	}

	@Override
	public List<Exception> updateValuesFromLoader(LoaderDataContainer values, String date) throws Exception {
		List<Exception> listException = LoaderValueUpdater.updateValues(values, this, date);
		return listException;
	}

	@Override
	public Theme addTheme(Theme theme) throws Exception {
		Integer id = (Integer) dao.save(theme);
		theme.setId(id);
		return theme;
	}

	@Override
	public void updateTheme(Theme theme) throws Exception {
		dao.update(theme);
	}

	@Override
	public void deleteTheme(Theme theme) throws Exception {
		dao.deleteAll(dao.getThemeAxisByTheme(theme.getId()));
		dao.deleteAll(dao.getThemeMetricByTheme(theme.getId()));		
		dao.delete(theme);
	}

	@Override
	public List<Theme> getThemes() throws Exception {
		return dao.getThemes();
	}

	@Override
	public List<Theme> getThemesByGroup(int groupId) throws Exception {
		return dao.getThemesByGroup(groupId);
	}

	@Override
	public void addThemeAxis(List<ThemeAxis> themeAxis) throws Exception {
		dao.saveOrUpdateAll(themeAxis);
	}

	@Override
	public void addThemeMetric(List<ThemeMetric> themeMetric) throws Exception {
		dao.saveOrUpdateAll(themeMetric);
	}

	@Override
	public void deleteThemeAxis(List<ThemeAxis> themeAxis) throws Exception {
		dao.deleteAll(themeAxis);
	}

	@Override
	public void deleteThemeMetric(List<ThemeMetric> themeMetrics) throws Exception {
		dao.deleteAll(themeMetrics);
	}

	@Override
	public List<ThemeAxis> getThemeAxisByAxis(int axisId) throws Exception {
		return dao.getThemeAxisByAxis(axisId);
	}

	@Override
	public List<ThemeMetric> getThemeMetricByMetric(int metricId) throws Exception {
		return dao.getThemeMetricByMetric(metricId);
	}

	@Override
	public Theme getThemeById(int id) throws Exception {
		return dao.getTheme(id);
	}

	@Override
	public MetricSqlQueries getCreateQueries(int metricId) throws Exception {
		return DatabaseElementSqlGenerator.generateCreateTableSql(dao.getMetric(metricId));
	}

	@Override
	public HashMap<String, Exception> executeQueries(MetricSqlQueries queries) throws Exception {
		return DatabaseElementSqlGenerator.executeQueries(queries);
	}

	@Override
	public HashMap<bpm.fm.api.model.Metric, List<MetricValue>> getValuesByMetricAxisAndDateInterval(int metricId, int axisId, Date startDate, Date endDate) throws Exception {
		bpm.fm.api.model.Metric metric = getMetric(metricId);
		List<MetricValue> values = valueCalculator.getValues(startDate, endDate, getAxis(axisId), metric, false, -1);
		
		HashMap<bpm.fm.api.model.Metric, List<MetricValue>> result = new HashMap<bpm.fm.api.model.Metric, List<MetricValue>>();
		
		result.put(metric, values);
		
		return result;
	}

	@Override
	public void addGroupObservatories(List<GroupObservatory> groupObservatory) throws Exception {
		dao.saveOrUpdateAll(groupObservatory);
	}

	@Override
	public void deleteGroupObservatories(List<GroupObservatory> groupObservatory) throws Exception {
		dao.deleteAll(groupObservatory);
	}

	@Override
	public List<GroupObservatory> getGroupObservatoryByObservatory(int observatoryId) throws Exception {
		return dao.find("From GroupObservatory where observatoryId = " + observatoryId);
	}

	@Override
	public List<Observatory> getObservatories() throws Exception {
		List<Observatory> obs = dao.find("From Observatory");
		for(Observatory ob : obs) {
			dao.fillObservatory(ob);
		}
		return obs;
	}

	@Override
	public List<Observatory> getObservatoriesByGroup(int groupId) throws Exception {
		return dao.getObservatoryByGroupId(groupId);
	}

	@Override
	public Observatory getObservatoryById(int id) throws Exception {
		return dao.getObservatoryById(id);
	}

	@Override
	public Observatory addObservatory(Observatory observatory) throws Exception {

		observatory.setThemes(dao.saveOrUpdateAll(observatory.getThemes()));
		
		Integer observatoryId = (Integer) dao.save(observatory);
		observatory.setId(observatoryId);
		
		for(Theme th : observatory.getThemes()) {
			ObservatoryTheme ot = new ObservatoryTheme();
			ot.setObservatoryId(observatory.getId());
			ot.setThemeId(th.getId());
			dao.save(ot);
		}
			
		return observatory;
	}

	@Override
	public void updateObservatory(Observatory observatory) throws Exception {
		dao.update(observatory);
		
		observatory.setThemes(dao.saveOrUpdateAll(observatory.getThemes()));
		
		//find if there's deleted themes		
		List<Theme> toRemove = new ArrayList<>();
		
		Observatory obs = dao.getObservatoryById(observatory.getId());
		LOOK:for(Theme th : obs.getThemes()) {
			for(Theme th2 : observatory.getThemes()) {
				if(th.getId() == th2.getId()) {
					continue LOOK;
				}
			}
			toRemove.add(th);
		}
		
		dao.deleteAll(toRemove);
		
		//check the relations between observatories and themes
		for(Theme th : toRemove) {
			ObservatoryTheme ot = (ObservatoryTheme) dao.find("From ObservatoryTheme where themeId = " + th.getId()).get(0);
			
			dao.delete(ot);
		}
		
		List<ObservatoryTheme> ots = dao.find("From ObservatoryTheme where observatoryId = " + observatory.getId());
		LOOK:for(Theme th : observatory.getThemes()) {
			for(ObservatoryTheme ot : ots) {
				if(th.getId() == ot.getThemeId()) {
					continue LOOK;
				}
			}
			ObservatoryTheme ot = new ObservatoryTheme();
			ot.setObservatoryId(observatory.getId());
			ot.setThemeId(th.getId());
			dao.save(ot);
		}
		
	}

	@Override
	public void deleteObservatory(Observatory observatory) throws Exception {
		
		for(Theme th : observatory.getThemes()) {
			deleteTheme(th);
		}
		
		dao.delete(observatory);
		
		List<GroupObservatory> go = getGroupObservatoryByObservatory(observatory.getId());
		dao.deleteAll(go);
		
		List<ObservatoryTheme> ots = dao.find("From ObservatoryTheme where observatoryId = " + observatory.getId());
		dao.deleteAll(ots);
	}

	@Override
	public void addComment(Comment comment) throws Exception {
		dao.save(comment);
	}

	@Override
	public List<Comment> getComments() throws Exception {
		List<Comment> comments = dao.find("From Comment");
		for(Comment com : comments) {
			com.setUser(getVanillaApi().getVanillaSecurityManager().getUserById(com.getUserId()));
		}
		return comments;
	}

	@Override
	public List<Comment> getCommentsByDateMetric(Date date, int metricId) throws Exception {
		List<Comment> comments = dao.find("From Comment where metricId = " + metricId);
		
		Date date2 = new Date(date.getTime());
		date2.setMonth(date.getMonth() + 1);
		date2.setDate(date.getDate() - 1);
		
		List<Comment> result = new ArrayList<>();
		for(Comment com : comments) {
			if(com.getValueDate().getTime() >= date.getTime() && com.getValueDate().getTime() <= date2.getTime()) {
				com.setUser(getVanillaApi().getVanillaSecurityManager().getUserById(com.getUserId()));
				result.add(com);
			}
		}
		
		return result;
	}

//	@Override
//	public void checkAlerts(int gatewayId) throws Exception {
//		for(bpm.fm.api.model.Metric metric : getMetrics()) {
//			if(metric.getEtlId() == gatewayId) {
//				for(Alert alert : metric.getAlerts()) {
//					//check the values for the previous period
//					Date date = getPreviousDate(new Date(), ((FactTable)metric.getFactTable()).getPeriodicity());
//					
//					boolean isRaised = AlertChecker.checkAlert(metric, alert, date, this);
//					if(isRaised) {
//						AlertRaised raised = new AlertRaised();
//						raised.setAlertId(alert.getId());
//						raised.setDate(date);
//						raised.setHasBeenHandled(false);
//						
//						dao.save(raised);
//					}
//					
//				}
//			}
//		}
//		
//	}

	public List<AlertRaised> getRaisedAlerts(MetricValue value) throws Exception {
		
		List<Alert> alerts = getAlertsbyMetric(value.getMetric().getId());
		
		if(alerts != null && !alerts.isEmpty()) {
			StringBuilder ids = new StringBuilder();
			boolean first = true;
			for(Alert alert : alerts) {
				if(first) {
					first = false;
				}
				else {
					ids.append(",");
				}
				ids.append(alert.getId());
				
			}
			
			List<AlertRaised> raiseds = null;
			
			String date = getQueryDate(value);
			
			try {
				raiseds = dao.find("From AlertRaised where alertId in (" + ids.toString() + ") and date LIKE " + date);
				
				for(AlertRaised raised : raiseds) {

					Alert alert = (Alert) dao.find("From MetricAlert where id = " + raised.getAlertId()).get(0);
					raised.setAlert(alert);
					//kmo 04/05/2016
					raised.setMetric(getMetric(((AlertKpi)alert.getEventObject()).getMetricId()));
				}
				
				return raiseds;
				
			} catch (Exception e) {
				raiseds = new ArrayList<>();
			}
			
		}
		
		return new ArrayList<>();
	}

	private List<Alert> getAlertsbyMetric(int id) throws Exception{
		String repositoryId = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_PUBLIC_REPOSITORY_ID);
		String groupId = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_PUBLIC_GROUP_ID);
		
		Group group = api.getVanillaSecurityManager().getGroupById(Integer.parseInt(groupId));
		bpm.vanilla.platform.core.beans.Repository repository = api.getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(repositoryId));
		IRepositoryApi repoApi = new RemoteRepositoryApi(new BaseRepositoryContext(api.getVanillaContext(), group, repository));
		
		List<Alert> alerts = repoApi.getAlertService().getAlerts();
		
		List<Alert> metAlerts = new ArrayList<Alert>();
		if (alerts != null) {
			for(Alert al : alerts){
				if(al.getEventObject() instanceof AlertKpi && ((AlertKpi)al.getEventObject()).getMetricId() == id){
					metAlerts.add(al);
				}
			}
		}
		return metAlerts;
	}

	private String getQueryDate(MetricValue value) {
		Date date = value.getDate();
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		
		if(((FactTable)value.getMetric().getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_YEARLY)) {
			
			return "'%" + cal.get(Calendar.YEAR) + "%'";
		}
		else if(((FactTable)value.getMetric().getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_MONTHLY)) {
			int month = cal.get(Calendar.MONTH) + 1;
			if(month < 10) {
				return "'%" + cal.get(Calendar.YEAR) + "-0" + month + "%'";
			}
			else {
				return "'%" + cal.get(Calendar.YEAR) + "-" + month + "%'";
			}
			
		}
		else if(((FactTable)value.getMetric().getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_DAILY)) {
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			if(month < 10) {
				if(day < 10) {
					return "'%" + cal.get(Calendar.YEAR) + "-0" + month + "-0" + day + "%'";
				}
				else {
					return "'%" + cal.get(Calendar.YEAR) + "-0" + month + "-" + day + "%'";
				}
			}
			else {
				if(day < 10) {
					return "'%" + cal.get(Calendar.YEAR) + "-" + month + "-0" + day + "%'";
				}
				else {
					return "'%" + cal.get(Calendar.YEAR) + "-" + month + "-" + day +  "%'";
				}
			}
			
		}
		
		return null;
	}

	@Override
	public void addAlertComment(AlertRaised alert, String comment, boolean resolve, int userId) throws Exception {
		if(resolve) {
			alert.setHasBeenHandled(true);
			alert.setResolutionDate(new Date());
			dao.update(alert);
		}
		
		CommentAlert com = new CommentAlert();
		com.setComment(comment);
		com.setDate(new Date());
		com.setRaisedId(alert.getId());
		com.setResolutionComment(resolve);
		com.setUserId(userId);
		dao.save(com);
	}

	@Override
	public List<AlertRaised> getRaisedAlert(boolean withResolved) throws Exception {
		List<AlertRaised> raised = dao.find("From AlertRaised where hasBeenHandled = " + withResolved);
		for(AlertRaised r : raised) {
			r.setAlert(getAlertById(r.getAlertId()));
			//kmo 04/05/2016
			r.setMetric(getMetric(((AlertKpi)r.getAlert().getEventObject()).getMetricId()));
		}
		return raised;
	}

	private Alert getAlertById(int alertId) throws Exception {
		
//		Alert alert = (Alert) dao.find("From MetricAlert where id = " + alertId).get(0);
//		
//		try {
//			bpm.fm.api.model.Metric metric = dao.getMetric(alert.getMetricId());
//			alert.setMetric(metric);
//			MetricAlertCondition cond = (MetricAlertCondition) dao.find("From MetricAlertCondition where alertId = " + alert.getId()).get(0);
//			alert.setCondition(cond);
//			MetricAlertEvent event = (MetricAlertEvent) dao.find("From MetricAlertEvent where alertId = " + alert.getId()).get(0);
//			alert.setEvent(event);
//		} catch (Exception e) {
//		}		
		String repositoryId = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_PUBLIC_REPOSITORY_ID);
		String groupId = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_PUBLIC_GROUP_ID);
		
		Group group = api.getVanillaSecurityManager().getGroupById(Integer.parseInt(groupId));
		bpm.vanilla.platform.core.beans.Repository repository = api.getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(repositoryId));
		IRepositoryApi repoApi = new RemoteRepositoryApi(new BaseRepositoryContext(api.getVanillaContext(), group, repository));
		
		Alert alert = repoApi.getAlertService().getAlert(alertId);
		
		return alert;
	}

	@Override
	public List<CommentAlert> getAlertComments(int raisedAlertId) throws Exception {
		List<CommentAlert> coms = dao.find("From CommentAlert where raisedId = " + raisedAlertId);
		for(CommentAlert com : coms) {
			com.setUser(getVanillaApi().getVanillaSecurityManager().getUserById(com.getUserId()));
		}
		return coms;
	}

	@Override
	public List<MetricValue> getValuesByDateAndLevelAndMetric(Date date, int levelId, int metricId, int groupId) throws Exception {
		
		Level level = getLevel(levelId);
		bpm.fm.api.model.Metric metric = getMetric(metricId);
		
		level.setParent(getAxis(level.getParentId()));
		
		return valueCalculator.getValues(date, null, level, metric, false, null, groupId);
	}

	@Override
	public List<LoaderDataContainer> getLoaderValuesForAxes(List<bpm.fm.api.model.Metric> metrics, List<Axis> axes, Date startDate, Date endDate) throws Exception {
		return loaderValueCalculator.getLoaderDataForAxes(metrics, axes, startDate, endDate);
	}
	
	@Override
	public  AxisInfo getLoaderAxe(int idAxis) throws Exception {
		return loaderValueCalculator.generateAxisInfo(getAxis(idAxis));
	}

	@Override
	public void addMetricAction(MetricAction action) throws Exception {
		if(action.getId() > 0) {
			dao.update(action);
		}
		else {
			dao.save(action);
		}
	}

	@Override
	public void deleteMetricAction(MetricAction action) throws Exception {
		dao.delete(action);
	}

	@Override
	public void addMetricMap(MetricMap map) throws Exception {
		if(map.getId() > 0) {
			dao.update(map);
		}
		else {
			dao.save(map);
		}
	}

	@Override
	public void deleteMetricMap(MetricMap map) throws Exception {
		dao.delete(map);
	}

	@Override
	public List<MapZoneValue> getMapZoneValues(int metricId, int levelId, Date startDate, Date endDate, int groupId) throws Exception {
		bpm.fm.api.model.Metric metric = getMetric(metricId);
		Level level = getLevel(levelId);
		
		Axis parent = getAxis(level.getParentId());
		level.setParent(parent);
		
		List<MetricValue> values = valueCalculator.getValues(startDate, endDate, level, metric, false, null, groupId);
		List<MapZoneValue> mapValues = mapValueCalculator.getMapValues(metric, level, values);
		
		return mapValues;
	}
	
	@Override
	public List<ComplexObjectViewer> getMultiMapZoneValues(List<ComplexMapMetric> metrics, List<ComplexMapLevel> levels, Date startDate, Date endDate, List<LevelMember> filters, int groupId) throws Exception {
		List<ComplexObjectViewer> viewersList = new ArrayList<ComplexObjectViewer>();
		for(ComplexMapLevel level : levels){
			for(ComplexMapMetric metric : metrics){
//				bpm.fm.api.model.Metric metric = getMetric(metricId);
//				Level level = getLevel(levelId);
				
				Axis parent = getAxis(level.getLevel().getParentId());
				level.getLevel().setParent(parent);
				
				List<MetricValue> values = valueCalculator.getValues(startDate, endDate, level.getLevel(), metric.getMetric(), false, filters, groupId);
				
				List<MapZoneValue> mapValues = mapValueCalculator.getMapValues(metric.getMetric(), level.getLevel(), values);
				ComplexObjectViewer cov = new ComplexObjectViewer(metric, level, mapValues);
				viewersList.add(cov);
			}
		}
		
		
		return viewersList;
	}
	
	@Override
	public List<ComplexMap> getAllComplexMaps() throws Exception {
		List<ComplexMap> maps = dao.find("From ComplexMap");
		
		for(ComplexMap map : maps){
			buildMap(map);
		}
		
		
		return maps;
	}
	
	private void buildMap(ComplexMap map) throws Exception {
		try {
			map.setMapInformation((MapInformation) xstream.fromXML(map.getMapModel()));
		} catch (Exception e) {
		}
		
		List<ComplexMapMetric> metricsList = getComplexMapMetricsByComplexMapId(map.getId());
		List<ComplexMapAxis> axisList  = getComplexMapAxisByComplexMapId(map.getId());
		List<ComplexMapLevel> levelsList  = getComplexMapLevelsByComplexMapId(map.getId());
		map.setComplexAxes(axisList);
		map.setComplexMetrics(metricsList);
		map.setComplexLevels(levelsList);
	}
	
	@Override
	public void updateComplexMap(ComplexMap map) throws Exception {
		if (map != null) {

//			List<MapDataSet> dataSetListBase = getMapDataSetByMapVanillaId(map.getId());
//			List<MapDataSet> dataSetListNew = map.getDataSetList();
//			List<MapDataSet> RemoveList = new ArrayList<MapDataSet>();
//			for(MapDataSet dtS : dataSetListNew){
//				dtS = saveMapDataSet(dtS);
//				updateMapDataSet(dtS);
//			}
//			
//			BASE:for(MapDataSet dtSBase : dataSetListBase) {
//				for(MapDataSet dtS : dataSetListNew){
//					if(dtSBase.getId() == dtS.getId()) {
//						continue BASE;
//					}
//				}
//				RemoveList.add(dtSBase);
//			}
//			for(MapDataSet dtS : RemoveList) {
//				deleteMapDataSet(dtS);
//			}
			List<ComplexMapMetric> metricsList = getComplexMapMetricsByComplexMapId(map.getId());
			List<ComplexMapAxis> axisList  = getComplexMapAxisByComplexMapId(map.getId());
			List<ComplexMapLevel> levelsList  = getComplexMapLevelsByComplexMapId(map.getId());
			
			for(ComplexMapMetric m : metricsList) {
				deleteComplexMapMetric(m);
			}
			for(ComplexMapAxis a : axisList) {
				deleteComplexMapAxe(a);
			}
			for(ComplexMapLevel a : levelsList) {
				deleteComplexMapLevel(a);
			}
			
			metricsList = map.getComplexMetrics();
			axisList = map.getComplexAxes();
			levelsList = map.getComplexLevels();

			Integer mapId = map.getId();
			
			for(ComplexMapMetric cpxm : metricsList) {
				cpxm.setIdMap(mapId);
				saveComplexMapMetric(cpxm);
			}
			for(ComplexMapAxis cpxa : axisList) {
				cpxa.setIdMap(mapId);
				saveComplexMapAxe(cpxa);
			}
			for(ComplexMapLevel cpxl : levelsList) {
				cpxl.setIdMap(mapId);
				saveComplexMapLevel(cpxl);
			}

			map.setMapModel(xstream.toXML(map.getMapInformation()));
			dao.update(map);
		}
		else {
			throw new Exception("Cannot update a null Map");
		}
		
	}
	
	@Override
	public void saveComplexMap(ComplexMap map) throws Exception {
		if (map == null) {
			throw new Exception("Cannot save a null Map");
			
		}

		List<ComplexMapMetric> metricsList = map.getComplexMetrics();
		List<ComplexMapAxis> axisList = map.getComplexAxes();
		List<ComplexMapLevel> levelsList = map.getComplexLevels();

		map.setMapModel(xstream.toXML(map.getMapInformation()));
		Integer mapId = (Integer) dao.save(map);
		map.setId(mapId);
		
		for(ComplexMapMetric cpxm : metricsList) {
			cpxm.setIdMap(mapId);
			saveComplexMapMetric(cpxm);
		}
		for(ComplexMapAxis cpxa : axisList) {
			cpxa.setIdMap(mapId);
			saveComplexMapAxe(cpxa);
		}
		
		for(ComplexMapLevel cpxl : levelsList) {
			cpxl.setIdMap(mapId);
			saveComplexMapLevel(cpxl);
		}
		
	}
	
	@Override
	public void deleteComplexMap(ComplexMap map) throws Exception {
	
		List<ComplexMapMetric> metricsList = getComplexMapMetricsByComplexMapId(map.getId());
		List<ComplexMapAxis> axisList  = getComplexMapAxisByComplexMapId(map.getId());
		List<ComplexMapLevel> levelsList  = getComplexMapLevelsByComplexMapId(map.getId());
		
		for(ComplexMapMetric m : metricsList) {
			deleteComplexMapMetric(m);
		}
		for(ComplexMapAxis a : axisList) {
			deleteComplexMapAxe(a);
		}
		for(ComplexMapLevel a : levelsList) {
			deleteComplexMapLevel(a);
		}
		
		dao.delete(map);
	}
	
	private void saveComplexMapMetric(ComplexMapMetric metric) throws Exception {
		if (metric == null) {
			throw new Exception("Cannot save a null ComplexMetric");
			
		}
		
		dao.save(metric);
	}
	
	private void saveComplexMapAxe(ComplexMapAxis axe) throws Exception {
		if (axe == null) {
			throw new Exception("Cannot save a null ComplexAxis");
			
		}
		
		dao.save(axe);
	}
	
	private void saveComplexMapLevel(ComplexMapLevel level) throws Exception {
		if (level == null) {
			throw new Exception("Cannot save a null ComplexLevel");
			
		}
		
		dao.save(level);
	}
	
	private List<ComplexMapMetric> getComplexMapMetricsByComplexMapId(int id) throws Exception {
		String path = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES);
		List<ComplexMapMetric> cpxmetricList = new ArrayList<ComplexMapMetric>();
		List<ComplexMapMetric> list =  (List<ComplexMapMetric>)dao.find("from ComplexMapMetric where idMap=" + id);
		for(ComplexMapMetric cpxmetric : list){
			bpm.fm.api.model.Metric metric = getMetric(cpxmetric.getIdMetric());
			if(metric != null){
				cpxmetric.setMetric(metric);
				cpxmetric.setIconUrl(path + "/KpiMap_Icons/" + cpxmetric.getIconUrl());
				cpxmetricList.add(cpxmetric);
			} else {
				deleteComplexMapMetric(cpxmetric);
			}
			
		}
		return cpxmetricList;
	}
	
	private List<ComplexMapAxis> getComplexMapAxisByComplexMapId(int id) throws Exception {
		List<ComplexMapAxis> cpxaxisList = new ArrayList<ComplexMapAxis>();
		List<ComplexMapAxis> list =  (List<ComplexMapAxis>)dao.find("from ComplexMapAxis where idMap=" + id);
		for(ComplexMapAxis cpxaxis : list){
			Axis axis = getAxis(cpxaxis.getIdAxis());
			if(axis != null){
				cpxaxis.setAxis(axis);
				cpxaxisList.add(cpxaxis);
			} else {
				deleteComplexMapAxe(cpxaxis);
			}
			
		}
		return cpxaxisList;
	}
	
	private List<ComplexMapLevel> getComplexMapLevelsByComplexMapId(int id) throws Exception {
		String path = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES);
		List<ComplexMapLevel> cpxLevelList = new ArrayList<ComplexMapLevel>();
		List<ComplexMapLevel> list =  (List<ComplexMapLevel>)dao.find("from ComplexMapLevel where idMap=" + id);
		for(ComplexMapLevel cpxLevel : list){
			Level level = getLevel(cpxLevel.getIdLevel());
			if(level != null){
				cpxLevel.setLevel(level);
				cpxLevel.setIconUrl(path + "/KpiMap_Icons/" + cpxLevel.getIconUrl());
				cpxLevelList.add(cpxLevel);
			} else {
				deleteComplexMapLevel(cpxLevel);
			}
			
		}
		return cpxLevelList;
	}
	
	private void deleteComplexMapMetric(ComplexMapMetric metric) throws Exception {
		List<ComplexMapMetric> metricList =  (List<ComplexMapMetric>)dao.find("from ComplexMapMetric where id=" + metric.getId());
		dao.delete(metricList.get(0));
	}
	
	private void deleteComplexMapAxe(ComplexMapAxis axe) throws Exception {
		List<ComplexMapAxis> axisList =  (List<ComplexMapAxis>)dao.find("from ComplexMapAxis where id=" + axe.getId());
		dao.delete(axisList.get(0));
	}
	
	private void deleteComplexMapLevel(ComplexMapLevel level) throws Exception {
		List<ComplexMapLevel> levelList =  (List<ComplexMapLevel>)dao.find("from ComplexMapLevel where id=" + level.getId());
		dao.delete(levelList.get(0));
	}

	public Datasource getDatasource(int datasourceId) throws Exception {
		return dao.getDatasource(datasourceId);
	}

	@Override
	public List<MetricValue> getValuesAndPreviousByDateAndLevelAndMetric(Date date, int metricId, int levelId, int groupId) throws Exception {
		Level level = getLevel(levelId);
		bpm.fm.api.model.Metric metric = getMetric(metricId);
		
		level.setParent(getAxis(level.getParentId()));
		
		return valueCalculator.getValues(date, null, level, metric, true, null, groupId);
	}

	@Override
	public List<MetricValue> getValuesByMetricAndLevelAndDateInterval(int metricId, int levelId, Date startDate, Date endDate, int groupId) throws Exception {
		Level level = getLevel(levelId);
		bpm.fm.api.model.Metric metric = getMetric(metricId);
		
		level.setParent(getAxis(level.getParentId()));
		
		return valueCalculator.getValues(startDate, endDate, level, metric, false, null, groupId);
	}

	@Override
	public List<MetricValue> getValuesByMetricAndDateInterval(int metricId, Date startDate, Date endDate, int groupId) throws Exception {
		bpm.fm.api.model.Metric metric = getMetric(metricId);
		return valueCalculator.getValues(startDate, endDate, null, metric, false, null, groupId);
	}
	
	public FreeMetricsJdbc getJdbcManager() {
		return jdbcManager;
	}

	@Override
	public List<ComplexMapMetric> getComplexMapMetricsByMetrics(List<bpm.fm.api.model.Metric> metrics) throws Exception {
		String path = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES);
		Set<Integer> set = new HashSet<Integer>();
		for(bpm.fm.api.model.Metric met : metrics){
			set.add(met.getId());
		}
		String ids = "";
		for(Integer id : set){
			ids += id+",";
		}
		ids = ids.substring(0, ids.length() -2);
		List<ComplexMapMetric> cpxmetricList = new ArrayList<ComplexMapMetric>();
		List<ComplexMapMetric> list =  (List<ComplexMapMetric>)dao.find("from ComplexMapMetric where idMetric IN(" + ids + ")");
		for(ComplexMapMetric cpxmetric : list){
//			for(bpm.fm.api.model.Metric met : metrics){ 			//pas utile de mettre le metric pour l'appel kpi user
//				if(met.getId() == cpxmetric.getIdMetric());
//				cpxmetric.setMetric(met);
//				break;
//			}
			
			cpxmetric.setIconUrl(path + "/KpiMap_Icons/" + cpxmetric.getIconUrl());
			cpxmetricList.add(cpxmetric);
			
		}
		return cpxmetricList;
	}
	
	@Override
	public ComplexMap getComplexMapById(int id) throws Exception {
		List<ComplexMap> maps = dao.find("From ComplexMap where id=" + id );
		ComplexMap result = new ComplexMap();
		if(maps.size() > 0){
				result = maps.get(0);
				
				buildMap(result);
		}
		
		return result;
	}

	@Override
	public String createAxeETL(String etlName, IRepositoryContext ctx, HasItemLinked hasItemLinked, Contract selectedContract, HashMap<String, Integer> columnsIndex) throws Exception {
		Transformation input = null;
		String format = getFormat(selectedContract);
		
		if (format.equalsIgnoreCase("csv")) {
			MdmContractFileInput mdmInput = new MdmContractFileInput();
			mdmInput.setName("ArchitectInput");
			mdmInput.setContract(selectedContract);
			
			FileInputCSV csv = createFileInputCSV(selectedContract.getId(), ctx, true);
			mdmInput.setFileTransfo(csv);
			
			input = mdmInput;
		}
		else if (format.equalsIgnoreCase("kml")) {
			KMLInput kml = createFileInputKML(selectedContract, ctx);
			
			input = kml;
		}
		
		Datasource datasource = null;
		String tableName = null;
		if (hasItemLinked instanceof Level) {
			Level level = (Level) hasItemLinked;
			
			datasource = level.getDatasource();
			tableName = level.getTableName();
		}
		else if (hasItemLinked instanceof bpm.fm.api.model.Metric) {
			bpm.fm.api.model.Metric metric = (bpm.fm.api.model.Metric) hasItemLinked;
			
			datasource = ((FactTable) metric.getFactTable()).getDatasource();
			tableName = ((FactTable) metric.getFactTable()).getTableName();
		}

		if (datasource != null && tableName != null) {
			DatasourceJdbc datasourceJdbc = (DatasourceJdbc) datasource.getObject();
	
			DataBaseOutputStream dataBase = new DataBaseOutputStream();
			dataBase.setName("DataBaseOutputStream");
			dataBase.setTableName(tableName);
			dataBase.setDefinition("select * from " + tableName);
			dataBase.setPositionX(200);
			dataBase.setPositionY(200);
			dataBase.setTruncate(true);
			
			DataBaseServer server = new DataBaseServer();
			server.setName("Axe_" + etlName.replace(" ", ""));
			DataBaseConnection dbCon = new DataBaseConnection();
			dbCon.setName("Axe_" + etlName.replace(" ", ""));
			dbCon.setLogin(datasourceJdbc.getUser());
			dbCon.setPassword(datasourceJdbc.getPassword());
			dbCon.setUseFullUrl(datasourceJdbc.getFullUrl());
			if (datasourceJdbc.getFullUrl()) {
				dbCon.setFullUrl(datasourceJdbc.getUrl());
			}
			else {
				dbCon.setHost(datasourceJdbc.getHost());
				dbCon.setPort(datasourceJdbc.getPort());
				dbCon.setDataBaseName(datasourceJdbc.getDatabaseName());
			}
			
			String driverName = findDriver(datasourceJdbc.getDriver());
			dbCon.setDriverName(driverName);
	
			//dbCon.setServer(server);
			server.addConnection(dbCon);
			server.setCurrentConnection(dbCon);
	
			dataBase.setServer(server);
			if (input instanceof KMLInput) {
				((KMLInput) input).setInited();
			}
			else {
				input.refreshDescriptor();
			}
			try {
				input.addOutput(dataBase);
				dataBase.addInput(input);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			DocumentGateway gateway = new DocumentGateway();
			gateway.setName(etlName);
			gateway.addTransformation(input);
			gateway.addTransformation(dataBase);
			gateway.addServer(server);
			
			dataBase.setDocumentGateway(gateway);
			dataBase.setInited();
			dataBase.refreshDescriptor();
			
			for(int i=0; i < dataBase.getDescriptor(input).getColumnCount(); i++){
				StreamDescriptor descriptor = dataBase.getDescriptor(input);
				
				String columnName = descriptor.getColumnName(i);
				Integer columnIndex = columnsIndex.get(columnName);
				if (columnIndex  != null && columnIndex != -1) {
					dataBase.createMapping(input, columnIndex, i);
				}
			}
	
			return gateway.getElement().asXML();
		}
		
		throw new Exception("Unable to generate ETL : Datasource is not correctly defined.");
	}
	
	@Override
	public String createAxeETL(String etlName, IRepositoryContext ctx, HasItemLinked hasItemLinked, RepositoryItem metadata, String queryName, HashMap<String, Integer> columnsIndex) throws Exception {
		IRepositoryApi repositoryApi = new RemoteRepositoryApi(ctx);
		String result = repositoryApi.getRepositoryService().loadModel(metadata);

		InputStream metadataInput = IOUtils.toInputStream(result, "UTF-8");

		// get models
		Collection<IBusinessModel> bmodels = MetaDataReader.read(ctx.getGroup().getName(), metadataInput, repositoryApi, false);
		
		IBusinessPackage pack = null;
		SavedQuery query = null;
		LOOP:for (IBusinessModel m : bmodels) {
			 for(IBusinessPackage p : m.getBusinessPackages("none")) { //$NON-NLS-1$
				 for(SavedQuery qu : p.getSavedQueries()) {
					 if(qu.getName().equals(queryName)) {
						 pack = p;
						 query = qu;
						 break LOOP;
					 }
				 }
			 }
		 }
		
		DocumentGateway gateway = new DocumentGateway();
		gateway.setRepositoryContext(ctx);
		gateway.setName(etlName);
		
		if (pack != null && query != null) {
			QuerySql sql = query.loadQuery("none", pack); //$NON-NLS-1$
	
			FMDTInput input = new FMDTInput();
			input.setName("MetadataInput");
			gateway.addTransformation(input);
			
			input.setBusinessModelName(pack.getBusinessModel().getName());
			input.setBusinessPackageName(pack.getName());
			input.setConnectionName("Default"); //$NON-NLS-1$
			input.setRepositoryItemId(metadata.getId());
			input.setDefinition(sql.getXml());
			
			Datasource datasource = null;
			String tableName = null;
			if (hasItemLinked instanceof Level) {
				Level level = (Level) hasItemLinked;
				
				datasource = level.getDatasource();
				tableName = level.getTableName();
			}
			else if (hasItemLinked instanceof bpm.fm.api.model.Metric) {
				bpm.fm.api.model.Metric metric = (bpm.fm.api.model.Metric) hasItemLinked;
				
				datasource = ((FactTable) metric.getFactTable()).getDatasource();
				tableName = ((FactTable) metric.getFactTable()).getTableName();
			}
	
			if (datasource != null && tableName != null) {
				DatasourceJdbc datasourceJdbc = (DatasourceJdbc) datasource.getObject();
		
				DataBaseOutputStream dataBase = new DataBaseOutputStream();
				dataBase.setName("DataBaseOutputStream");
				dataBase.setTableName(tableName);
				dataBase.setDefinition("select * from " + tableName);
				dataBase.setPositionX(200);
				dataBase.setPositionY(200);
				dataBase.setTruncate(true);
				
				DataBaseServer server = new DataBaseServer();
				server.setName("Axe_" + etlName.replace(" ", ""));
				DataBaseConnection dbCon = new DataBaseConnection();
				dbCon.setName("Axe_" + etlName.replace(" ", ""));
				dbCon.setLogin(datasourceJdbc.getUser());
				dbCon.setPassword(datasourceJdbc.getPassword());
				dbCon.setUseFullUrl(datasourceJdbc.getFullUrl());
				if (datasourceJdbc.getFullUrl()) {
					dbCon.setFullUrl(datasourceJdbc.getUrl());
				}
				else {
					dbCon.setHost(datasourceJdbc.getHost());
					dbCon.setPort(datasourceJdbc.getPort());
					dbCon.setDataBaseName(datasourceJdbc.getDatabaseName());
				}
				
				String driverName = findDriver(datasourceJdbc.getDriver());
				dbCon.setDriverName(driverName);
		
				//dbCon.setServer(server);
				server.addConnection(dbCon);
				server.setCurrentConnection(dbCon);
		
				dataBase.setServer(server);
				try {
					input.addOutput(dataBase);
					dataBase.addInput(input);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				gateway.addTransformation(dataBase);
				gateway.addServer(server);
				
				dataBase.setDocumentGateway(gateway);
				dataBase.setInited();
				dataBase.refreshDescriptor();
				
				for(int i=0; i < dataBase.getDescriptor(input).getColumnCount(); i++){
					StreamDescriptor descriptor = dataBase.getDescriptor(input);
					
					String columnName = descriptor.getColumnName(i);
					Integer columnIndex = columnsIndex.get(columnName);
					if (columnIndex  != null && columnIndex != -1) {
						dataBase.createMapping(input, columnIndex, i);
					}
				}
		
				return gateway.getElement().asXML();
			}
		}
		
		throw new Exception("Unable to generate ETL : Datasource is not correctly defined.");
	}
	
	private FileInputCSV createFileInputCSV(int docId, IRepositoryContext ctx, boolean skipFirstRow){
		FileInputCSV fileInput = new FileInputCSV(); 
		fileInput.setDefinition(String.valueOf(docId));
		fileInput.setSkipFirstRow(String.valueOf(skipFirstRow));
		MdmFileServer srv = new MdmFileServer("mdmserver", "", ctx.getVanillaContext().getVanillaUrl(), ctx.getVanillaContext().getLogin(),ctx.getVanillaContext().getPassword(), ctx.getRepository().getId() + "");
		fileInput.setServer(srv);
		return fileInput;
	}
	
	private KMLInput createFileInputKML(Contract selectedContract, IRepositoryContext ctx){
		KMLInput fileInput = new KMLInput();
		fileInput.setName("KMLInput");
		fileInput.setContract(selectedContract);
		fileInput.setDefinition(String.valueOf(selectedContract.getId()));
		fileInput.setUseMdm(true);
		MdmFileServer srv = new MdmFileServer("mdmserver", "", ctx.getVanillaContext().getVanillaUrl(), ctx.getVanillaContext().getLogin(),ctx.getVanillaContext().getPassword(), ctx.getRepository().getId() + "");
		fileInput.setFileServer(srv);
		fileInput.setServer(srv);
		return fileInput;
	}
	
	private String getFormat(Contract contract) {
		return contract.getFileVersions() != null ? contract.getFileVersions().getCurrentVersion(contract.getVersionId()).getFormat() : "";
	}

	private String findDriver(String driver) throws Exception {
		Collection<DriverInfo> infos = ListDriver.getInstance(bpm.studio.jdbc.management.config.IConstants.getJdbcDriverXmlFile()).getDriversInfo();
		if (infos != null) {
			for (DriverInfo info : infos) {
				if (driver.equals(info.getClassName())) {
					return info.getName();
				}
			}
		}
		return "MySQL";
	}
}
