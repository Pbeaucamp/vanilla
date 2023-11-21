package bpm.fm.designer.web.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.FactTableAxis;
import bpm.fm.api.model.GroupObservatory;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricAction;
import bpm.fm.api.model.MetricMap;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.fm.api.model.ThemeAxis;
import bpm.fm.api.model.ThemeMetric;
import bpm.fm.api.model.utils.AxisInfo;
import bpm.fm.api.model.utils.MetricSqlQueries;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.helper.MetadataHelper;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.shared.fmdt.metadata.ColumnJoin;
import bpm.gwt.commons.shared.fmdt.metadata.ColumnJoin.Outer;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataModel;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataPackage;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataQueries;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataQuery;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataRelation;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataResource;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataResources;
import bpm.gwt.commons.shared.fmdt.metadata.TableRelation;
import bpm.metadata.MetaData;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MetricServiceImpl extends RemoteServiceServlet implements MetricService {

	private static final long serialVersionUID = -660218264074823448L;

	private FreeMetricsSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), FreeMetricsSession.class);
	}

	@Override
	public Axis addAxe(Axis axe) throws Exception {
		if (axe.getId() > 0) {
			return getSession().getManager().updateAxis(axe);
		}
		else {
			return getSession().getManager().addAxis(axe);
		}
	}

	@Override
	public void addLevel(Level level) throws Exception {
		if (level.getId() > 0) {
			getSession().getManager().updateLevel(level);
		}
		else {
			getSession().getManager().addLevel(level);
		}
	}

	@Override
	public void deleteLevel(Level level) throws Exception {
		getSession().getManager().deleteLevel(level);
	}

	@Override
	public Axis getAxe(int id) throws Exception {
		return getSession().getManager().getAxis(id);
	}

	@Override
	public void deleteAxe(Axis axe) throws Exception {
		getSession().getManager().deleteAxis(axe);
	}

	@Override
	public List<Axis> getAxes(int obsId, int themeId) throws Exception {
		if (obsId <= 0) {
			if (themeId <= 0) {
				return getSession().getManager().getAxis();
			}
			else {
				return getSession().getManager().getThemeById(themeId).getAxis();
			}
		}
		else {
			if (themeId <= 0) {
				Observatory obs = getSession().getManager().getObservatoryById(obsId);
				Set<Axis> axis = new HashSet<Axis>();
				for (Theme th : obs.getThemes()) {
					axis.addAll(th.getAxis());
				}
				return new ArrayList<Axis>(axis);
			}
			else {
				return getSession().getManager().getThemeById(themeId).getAxis();
			}
		}
	}

	@Override
	public List<Metric> getMetrics(int obsId, int themeId) throws Exception {
		if (obsId <= 0) {
			if (themeId <= 0) {
				return getSession().getManager().getMetrics();
			}
			else {
				return getSession().getManager().getThemeById(themeId).getMetrics();
			}
		}
		else {
			if (themeId <= 0) {
				Observatory obs = getSession().getManager().getObservatoryById(obsId);
				Set<Metric> metrics = new HashSet<Metric>();
				for (Theme th : obs.getThemes()) {
					metrics.addAll(th.getMetrics());
				}
				return new ArrayList<Metric>(metrics);
			}
			else {
				return getSession().getManager().getThemeById(themeId).getMetrics();
			}
		}
	}

	@Override
	public Metric addMetric(Metric metric) throws Exception {
		if (metric.getId() > 0) {
			return getSession().getManager().updateMetric(metric);
		}
		else {
			return getSession().getManager().addMetric(metric);
		}
	}

	@Override
	public void deleteMetric(Metric ds) throws Exception {
		getSession().getManager().deleteMetric(ds);
	}

	@Override
	public Metric getMetric(int id) throws Exception {
		return getSession().getManager().getMetric(id);
	}

	@Override
	public void updateMetric(Metric metric) throws Exception {
		getSession().getManager().updateMetric(metric);
	}

	// @Override
	// public void addAlert(MetricAlert alert) throws Exception {
	// if(alert.getId() > 0) {
	// getSession().getManager().updateAlert(alert);
	// }
	// else {
	// getSession().getManager().addAlert(alert);
	// }
	//
	// }
	//
	// @Override
	// public List<Alert> getAlerts(int metricId) throws Exception {
	// return getSession().getManager().getAlerts(metricId);
	// }

	@Override
	public List<DatabaseTable> getDatabaseStructure(int datasourceId) throws Exception {
		Datasource datasource = getSession().getVanillaApi().getVanillaPreferencesManager().getDatasourceById(datasourceId);
		return getSession().getVanillaApi().getVanillaPreferencesManager().getDatabaseStructure(datasource, true);
	}

	@Override
	public void addTheme(Theme theme) throws Exception {
		if (theme.getId() > 0) {
			getSession().getManager().updateTheme(theme);
		}
		else {
			getSession().getManager().addTheme(theme);
		}
	}

	@Override
	public List<Theme> getThemes() throws Exception {
		return getSession().getManager().getThemes();
	}

	@Override
	public void updateGroupForTheme(List<Group> groups, Theme them) throws Exception {
	}

	@Override
	public void updateThemeForAxis(List<Theme> themes, Axis axeMetric) throws Exception {
		List<ThemeAxis> existing = getSession().getManager().getThemeAxisByAxis(axeMetric.getId());
		List<ThemeAxis> toRemove = new ArrayList<ThemeAxis>();
		List<ThemeAxis> toAdd = new ArrayList<ThemeAxis>();
		for (Theme g : themes) {
			boolean finded = false;
			for (ThemeAxis gt : existing) {
				if (gt.getThemeId() == g.getId()) {
					finded = true;
					break;
				}
			}
			if (!finded) {
				ThemeAxis gt = new ThemeAxis();
				gt.setThemeId(g.getId());
				gt.setAxisId(axeMetric.getId());
				toAdd.add(gt);
			}
		}

		for (ThemeAxis gt : existing) {
			boolean finded = false;
			for (Theme g : themes) {
				if (gt.getThemeId() == g.getId()) {
					finded = true;
					break;
				}
			}
			if (!finded) {
				toRemove.add(gt);
			}
		}

		getSession().getManager().deleteThemeAxis(toRemove);
		getSession().getManager().addThemeAxis(toAdd);

	}

	@Override
	public void updateThemeForMetric(List<Theme> themes, Metric axeMetric) throws Exception {
		List<ThemeMetric> existing = getSession().getManager().getThemeMetricByMetric(axeMetric.getId());
		List<ThemeMetric> toRemove = new ArrayList<ThemeMetric>();
		List<ThemeMetric> toAdd = new ArrayList<ThemeMetric>();
		for (Theme g : themes) {
			boolean finded = false;
			for (ThemeMetric gt : existing) {
				if (gt.getThemeId() == g.getId()) {
					finded = true;
					break;
				}
			}
			if (!finded) {
				ThemeMetric gt = new ThemeMetric();
				gt.setThemeId(g.getId());
				gt.setMetricId(axeMetric.getId());
				toAdd.add(gt);
			}
		}

		for (ThemeMetric gt : existing) {
			boolean finded = false;
			for (Theme g : themes) {
				if (gt.getThemeId() == g.getId()) {
					finded = true;
					break;
				}
			}
			if (!finded) {
				toRemove.add(gt);
			}
		}

		getSession().getManager().deleteThemeMetric(toRemove);
		getSession().getManager().addThemeMetric(toAdd);

	}

	@Override
	public MetricSqlQueries getCreateQueries(Metric metric) throws Exception {
		return getSession().getManager().getCreateQueries(metric.getId());
	}

	@Override
	public HashMap<String, Exception> executeQueries(MetricSqlQueries queries) throws Exception {
		HashMap<String, Exception> result = getSession().getManager().executeQueries(queries);

		return result;
	}

	@Override
	public void addObservatory(Observatory observatory) throws Exception {
		if (observatory.getId() > 0) {
			getSession().getManager().updateObservatory(observatory);
		}
		else {
			getSession().getManager().addObservatory(observatory);
		}
	}

	@Override
	public void updateGroupForObservatory(List<Group> groups, Observatory observatory) throws Exception {
		// check if we need to add or delete some groupTheme
		List<GroupObservatory> existing = getSession().getManager().getGroupObservatoryByObservatory(observatory.getId());
		List<GroupObservatory> toRemove = new ArrayList<GroupObservatory>();
		List<GroupObservatory> toAdd = new ArrayList<GroupObservatory>();
		for (Group g : groups) {
			boolean finded = false;
			for (GroupObservatory gt : existing) {
				if (gt.getGroupId() == g.getId()) {
					finded = true;
					break;
				}
			}
			if (!finded) {
				GroupObservatory gt = new GroupObservatory();
				gt.setGroupId(g.getId());
				gt.setObservatoryId(observatory.getId());
				toAdd.add(gt);
			}
		}

		for (GroupObservatory gt : existing) {
			boolean finded = false;
			for (Group g : groups) {
				if (gt.getGroupId() == g.getId()) {
					finded = true;
					break;
				}
			}
			if (!finded) {
				toRemove.add(gt);
			}
		}

		getSession().getManager().deleteGroupObservatories(toRemove);
		getSession().getManager().addGroupObservatories(toAdd);

	}

	@Override
	public List<Observatory> getObservatories() throws Exception {
		return getSession().getManager().getObservatories();
	}

	@Override
	public void updateObservatories(List<Observatory> observatories) throws Exception {
		for (Observatory obs : observatories) {
			getSession().getManager().updateObservatory(obs);
		}

	}

	@Override
	public void deleteAlert(Alert alert) throws Exception {
		getSession().getManager().deleteAlert(alert);
	}

	@Override
	public void deleteObservatory(Observatory ds) throws Exception {
		getSession().getManager().deleteObservatory(ds);
	}

	@Override
	public boolean testConnection(Datasource ds) throws Exception {
		return getSession().getVanillaApi().getVanillaPreferencesManager().testConnection(ds);
	}

	@Override
	public void addMetricAction(MetricAction action) throws Exception {
		getSession().getManager().addMetricAction(action);
	}

	@Override
	public void deleteMetricAction(MetricAction action) throws Exception {
		getSession().getManager().deleteMetricAction(action);
	}

	@Override
	public void deleteMetricMap(MetricMap map) throws Exception {
		getSession().getManager().deleteMetricMap(map);
	}

	@Override
	public void addMetricMap(MetricMap map) throws Exception {
		getSession().getManager().addMetricMap(map);
	}

	@Override
	public AxisInfo browseAxis(Axis axis) throws Exception {
		return getSession().getManager().getLoaderAxe(axis.getId());
	}

	@Override
	public int createMetadata(Theme theme, String metadataName, List<Metric> metrics, RepositoryDirectory target) throws Exception {
		FreeMetricsSession session = getSession();
		Group group = session.getCurrentGroup();

		Datasource datasource = null;
		if (metrics != null) {
			for (Metric metric : metrics) {
				if (metric.getFactTable() instanceof FactTable) {
					Datasource checkDS = ((FactTable) metric.getFactTable()).getDatasource();
					if (datasource == null) {
						datasource = checkDS;
					}
					else if (datasource.getId() != checkDS.getId()) {
						throw new ServiceException("Metrics have different datasources.");
					}
				}
			}
		}

		if (datasource == null) {
			throw new ServiceException("No datasource available.");
		}

		if (datasource.getType() != DatasourceType.JDBC) {
			throw new ServiceException("Metadata creation only support JDBC datasource.");
		}

		List<DatabaseTable> tables = getDatabaseStructure(datasource.getId());
		datasource.setTables(tables);

		List<MetadataResource> resources = new ArrayList<MetadataResource>();
		List<MetadataQuery> queries = new ArrayList<MetadataQuery>();
		tables = buildTables(metrics, tables);
		List<TableRelation> relations = buildRelations(metrics, tables);

		MetadataPackage pack = new MetadataPackage("Default pack", "", tables, new MetadataResources(resources), new MetadataQueries(queries));
		MetadataModel model = new MetadataModel("Default model", pack, tables, new MetadataRelation(relations));

		// MetadataPackage pack = new MetadataPackage("KPIPack", "", tables, new
		// MetadataResources(resources), null);
		// MetadataModel model = new MetadataModel("KPIModel", pack, tables,
		// null);

		Metadata metadata = new Metadata(metadataName, "", datasource, model);
		metadata.setDatasource(datasource);

		List<String> groupNames = new ArrayList<String>();
		groupNames.add(group.getName());

		MetaData classicMetadata = MetadataHelper.convertMetadata(metadata, tables, groupNames);

		try {
			String xmlMetadata = classicMetadata.getXml(false);
			RepositoryItem item = session.getRepositoryConnection().getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FMDT_TYPE, -1, target, metadata.getName(), "", "", "", xmlMetadata, true);
			return item.getId();
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServiceException("Unable to create metadata: " + e1.getMessage());
		}
	}

	private List<DatabaseTable> buildTables(List<Metric> metrics, List<DatabaseTable> tables) {
		List<DatabaseTable> filterTables = new ArrayList<DatabaseTable>();
		if (metrics != null) {
			for (Metric metric : metrics) {
				FactTable metricFT = ((FactTable) metric.getFactTable());

				DatabaseTable tableMetric = getTable(tables, metricFT.getTableName());
				if (tableMetric != null && !filterTables.contains(tableMetric)) {
					filterTables.add(tableMetric);
				}

				DatabaseTable tableObjective = getTable(tables, metricFT.getObjectives().getTableName());
				if (tableObjective != null && !filterTables.contains(tableObjective)) {
					filterTables.add(tableObjective);
				}

				List<Axis> axis = metric.getLinkedAxis();
				if (axis != null) {
					for (Axis ax : axis) {
						List<Level> levels = ax.getChildren();
						if (levels != null) {
							for (int i = 0; i < levels.size(); i++) {
								Level level = levels.get(i);

								DatabaseTable tableParent = getTableParent(tables, levels, level.getParentId());
								if (tableParent != null && !filterTables.contains(tableParent)) {
									filterTables.add(tableParent);
								}

								DatabaseTable table = getTable(tables, level.getTableName());
								if (table != null && !filterTables.contains(table)) {
									filterTables.add(table);
								}
							}
						}
					}
				}
			}
		}
		return filterTables;
	}

	private List<TableRelation> buildRelations(List<Metric> metrics, List<DatabaseTable> tables) {
		List<TableRelation> relations = new ArrayList<TableRelation>();
		if (metrics != null) {
			for (Metric metric : metrics) {
				FactTable metricFT = ((FactTable) metric.getFactTable());

				List<Axis> axis = metric.getLinkedAxis();
				List<FactTableAxis> factTableAxis = metricFT.getFactTableAxis();

				if (axis != null) {
					for (Axis ax : axis) {
						List<Level> levels = ax.getChildren();
						if (levels != null) {
							for (int i = 0; i < levels.size(); i++) {
								Level level = levels.get(i);
								if (level.getParentColumnId() != null && !level.getParentColumnId().isEmpty()) {
									DatabaseTable tableParent = getTableParent(tables, levels, level.getParentId());
									DatabaseTable table = getTable(tables, level.getTableName());

									DatabaseColumn columnParent = getColumn(tableParent, level.getParentColumnId());
									DatabaseColumn column = getColumn(table, level.getColumnId());

									TableRelation relation = new TableRelation(tableParent, table);
									relation.addJoin(new ColumnJoin(columnParent, column, Outer.INNER));
									if (!checkIfRelationExist(relations, relation)) {
										relations.add(relation);
									}
								}
							}

							Level level = getLastLevel(levels);
							if (level != null) {
								FactTableAxis axisFT = getFactTableAxis(factTableAxis, ax);

								DatabaseTable table = getTable(tables, level.getTableName());
								DatabaseColumn column = getColumn(table, level.getColumnId());

								DatabaseTable tableMetric = getTable(tables, metricFT.getTableName());
								DatabaseColumn columnMetric = getColumn(tableMetric, axisFT.getColumnId());

								TableRelation relationMetric = new TableRelation(tableMetric, table);
								relationMetric.addJoin(new ColumnJoin(columnMetric, column, Outer.INNER));
								if (!checkIfRelationExist(relations, relationMetric)) {
									relations.add(relationMetric);
								}

								DatabaseTable tableObjective = getTable(tables, metricFT.getObjectives().getTableName());
								DatabaseColumn columnObjective = getColumn(tableMetric, axisFT.getObjectiveColumnId());

								TableRelation relationObjective = new TableRelation(tableObjective, table);
								relationObjective.addJoin(new ColumnJoin(columnObjective, column, Outer.INNER));
								if (!checkIfRelationExist(relations, relationObjective)) {
									relations.add(relationObjective);
								}
							}
						}
					}
				}
			}
		}
		return relations;
	}

	private boolean checkIfRelationExist(List<TableRelation> relations, TableRelation newRelation) {
		for (TableRelation relation : relations) {
			if (relation.getRightTable().getName().equals(newRelation.getRightTable().getName()) && relation.getLeftTable().getName().equals(newRelation.getLeftTable().getName())) {
				if (relation.getJoins() != null && newRelation.getJoins() != null && relation.getJoins().size() == newRelation.getJoins().size()) {
					boolean exist = true;
					for (ColumnJoin newJoin : newRelation.getJoins()) {
						boolean found = false;
						for (ColumnJoin join : relation.getJoins()) {
							if (newJoin.getLeftColumn().getName().equals(join.getLeftColumn().getName()) && newJoin.getRightColumn().getName().equals(join.getRightColumn().getName()) && newJoin.getOuter() == join.getOuter()) {
								found = true;
								break;
							}
						}
						if (!found) {
							exist = false;
							break;
						}
					}

					return exist;
				}
			}
		}
		return false;
	}

	private Level getLastLevel(List<Level> levels) {
		Level lastLevel = null;
		if (levels != null) {
			for (Level level : levels) {
				if (lastLevel == null || lastLevel.getLevelIndex() < level.getLevelIndex()) {
					lastLevel = level;
				}
			}
		}
		return lastLevel;
	}

	private FactTableAxis getFactTableAxis(List<FactTableAxis> factTableAxis, Axis ax) {
		if (factTableAxis != null) {
			for (FactTableAxis axisFT : factTableAxis) {
				if (axisFT.getAxisId() == ax.getId()) {
					return axisFT;
				}
			}
		}
		return null;
	}

	private DatabaseColumn getColumn(DatabaseTable table, String columnId) {
		if (table.getColumns() != null) {
			for (DatabaseColumn column : table.getColumns()) {
				if (column.getOriginName().equals(columnId) || column.getName().equals(columnId)) {
					return column;
				}
			}
		}
		return null;
	}

	private DatabaseTable getTableParent(List<DatabaseTable> tables, List<Level> levels, int parentId) {
		if (levels != null) {
			for (Level level : levels) {
				if (level.getId() == parentId) {
					return getTable(tables, level.getTableName());
				}
			}
		}
		return null;
	}

	private DatabaseTable getTable(List<DatabaseTable> tables, String tableName) {
		if (tables != null) {
			for (DatabaseTable table : tables) {
				if (table.getName().equals(tableName)) {
					return table;
				}
			}
		}
		return null;
	}
}
