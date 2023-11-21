package bpm.odata.service.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;

import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.api.RemoteFreeMetricsManager;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.fm.api.model.utils.LevelMember;
import bpm.fm.api.model.utils.MetricValue;
import bpm.odata.service.KpiEdmProvider;
import bpm.odata.service.utils.KpiExpressionVisitor;
import bpm.odata.service.utils.Utils;
import bpm.vanilla.platform.core.IVanillaContext;

public class KpiManager {

	private IFreeMetricsManager fmApi;

	private List<Observatory> observatories;
	
	private int groupId;

	public KpiManager(IVanillaContext context, int groupId) {
		this.fmApi = new RemoteFreeMetricsManager(context);
		this.groupId = groupId;
	}

	private List<Observatory> getObservatories(boolean reload) throws ODataApplicationException {
		if (observatories == null || reload) {
			try {
				this.observatories = fmApi.getObservatories();
			} catch (Exception e) {
				e.printStackTrace();
				throw new ODataApplicationException("Unable to browse observatories: " + e.getMessage(), HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
			}
		}
		return observatories;
	}

	public EntityCollection getObservatoryCollection() throws ODataApplicationException {
		List<Entity> entities = new ArrayList<>();

		List<Observatory> observatories = getObservatories(true);
		if (observatories != null) {
			for (Observatory observatory : observatories) {
				entities.add(buildEntity(observatory));
			}
		}
		return buildCollection(entities);
	}

	public EntityCollection getThemeCollection(List<EntityKey> keys) throws ODataApplicationException {
		int observatoryId = extractObservatoryId(keys);

		List<Entity> entities = new ArrayList<>();

		Observatory observatory = getObservatory(observatoryId);
		List<Theme> items = observatory.getThemes();
		if (items != null) {
			for (Theme theme : items) {
				entities.add(buildEntity(theme));
			}
		}
		return buildCollection(entities);
	}

	public EntityCollection getMetricCollection(List<EntityKey> keys) throws ODataApplicationException {
		int observatoryId = extractObservatoryId(keys);
		int themeId = extractThemeId(keys);

		List<Entity> entities = new ArrayList<>();

		Theme theme = getTheme(observatoryId, themeId);
		List<Metric> items = theme.getMetrics();
		if (items != null) {
			for (Metric metric : items) {
				entities.add(buildEntity(metric));
			}
		}
		return buildCollection(entities);
	}

	public EntityCollection getAxeCollection(List<EntityKey> keys) throws ODataApplicationException {
		int observatoryId = extractObservatoryId(keys);
		int themeId = extractThemeId(keys);
		int metricId = extractMetricId(keys);

		List<Entity> entities = new ArrayList<>();

		Metric metric = getMetric(observatoryId, themeId, metricId);
		List<Axis> items = metric.getLinkedAxis();
		if (items != null) {
			for (Axis axis : items) {
				entities.add(buildEntity(axis));
			}
		}
		return buildCollection(entities);
	}

	public EntityCollection getLevelCollection(List<EntityKey> keys) throws ODataApplicationException {
		int observatoryId = extractObservatoryId(keys);
		int themeId = extractThemeId(keys);
		int metricId = extractMetricId(keys);
		int axisId = extractAxeId(keys);

		List<Entity> entities = new ArrayList<>();

		Axis axis = getAxis(observatoryId, themeId, metricId, axisId);
		List<Level> levels = axis.getChildren();
		if (levels != null) {
			for (Level level : levels) {
				entities.add(buildEntity(level));
			}
		}
		return buildCollection(entities);
	}

	public EntityCollection getValueCollection(List<EntityKey> keys, Expression filterExpression) throws ODataApplicationException {
		int metricId = extractMetricId(keys);
		Integer levelId = extractLevelId(keys, false);

		FilterResult filterResult = parseFilter(filterExpression);

		List<Entity> entities = new ArrayList<>();

		try {
			List<MetricValue> values = null;
			if (filterResult.getEndDate() != null) {
				if (levelId != null) {
					values = fmApi.getValuesByMetricAndLevelAndDateInterval(metricId, levelId, filterResult.getStartDate(), filterResult.getEndDate(), groupId);
				}
				else {
					values = fmApi.getValuesByMetricAndDateInterval(metricId, filterResult.getStartDate(), filterResult.getEndDate(), groupId);
				}
			}
			else {
				if (levelId != null) {
					values = fmApi.getValuesAndPreviousByDateAndLevelAndMetric(filterResult.getStartDate(), levelId, metricId, groupId);
				}
				else {
					values = fmApi.getValueAndPreviousByMetricAndDate(filterResult.getStartDate(), metricId, groupId);
				}
			}

			if (values != null) {
				for (MetricValue value : values) {
					entities.add(buildEntity(value));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ODataApplicationException("Unable to get data for metric with id '" + metricId + "' and date '" + new Date() + "'", HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
		}
		return buildCollection(entities);
	}

	private FilterResult parseFilter(Expression filterExpression) throws ODataApplicationException {
		if (filterExpression == null) {
			return new FilterResult(new Date(), null);
		}

		FilterResult filterResult = null;
		try {
			Object result = filterExpression.accept(new KpiExpressionVisitor());
			filterResult = result instanceof FilterResult ? (FilterResult) result : null;
		} catch (ExpressionVisitException e) {
			e.printStackTrace();
		}

		if (filterResult == null) {
			throw new ODataApplicationException("Filter expression is not valid. It should looks like date('yyyy-MM-dd'T'HH:mm', 'yyyy-MM-dd'T'HH:mm') or date('yyyy-MM-dd'T'HH:mm')", HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
		}

		return filterResult;
	}

	private EntityCollection buildCollection(List<Entity> entities) {
		EntityCollection retEntitySet = new EntityCollection();
		for (Entity metadataEntity : entities) {
			retEntitySet.getEntities().add(metadataEntity);
		}

		return retEntitySet;
	}

	public Entity getObservatoryEntity(List<EntityKey> keys) throws ODataApplicationException {
		int observatoryId = extractObservatoryId(keys);

		Observatory item = getObservatory(observatoryId);
		return buildEntity(item);
	}

	public Entity getThemeEntity(List<EntityKey> keys) throws ODataApplicationException {
		int observatoryId = extractObservatoryId(keys);
		int themeId = extractThemeId(keys);

		Theme item = getTheme(observatoryId, themeId);
		return buildEntity(item);
	}

	public Entity getMetricEntity(List<EntityKey> keys) throws ODataApplicationException {
		int observatoryId = extractObservatoryId(keys);
		int themeId = extractThemeId(keys);
		int metricId = extractMetricId(keys);

		Metric item = getMetric(observatoryId, themeId, metricId);
		return buildEntity(item);
	}

	public Entity getAxeEntity(List<EntityKey> keys) throws ODataApplicationException {
		int observatoryId = extractObservatoryId(keys);
		int themeId = extractThemeId(keys);
		int metricId = extractMetricId(keys);
		int axisId = extractAxeId(keys);

		Axis item = getAxis(observatoryId, themeId, metricId, axisId);
		return buildEntity(item);
	}

	public Entity getLevelEntity(List<EntityKey> keys) throws ODataApplicationException {
		int observatoryId = extractObservatoryId(keys);
		int themeId = extractThemeId(keys);
		int metricId = extractMetricId(keys);
		int axisId = extractAxeId(keys);
		int levelId = extractLevelId(keys, true);

		Level item = getLevel(observatoryId, themeId, metricId, axisId, levelId);
		return buildEntity(item);
	}

	private Observatory getObservatory(int observatoryId) throws ODataApplicationException {
		List<Observatory> observatories = getObservatories(false);
		for (Observatory obs : observatories) {
			if (obs.getId() == observatoryId) {
				return obs;
			}
		}

		throw new ODataApplicationException("The observatory '" + observatoryId + "' can not be found.", HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
	}

	private Theme getTheme(int observatoryId, int themeId) throws ODataApplicationException {
		Observatory observatory = getObservatory(observatoryId);
		List<Theme> themes = observatory.getThemes();
		for (Theme theme : themes) {
			if (theme.getId() == themeId) {
				return theme;
			}
		}

		throw new ODataApplicationException("The theme '" + themeId + "' can not be found.", HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
	}

	private Metric getMetric(int observatoryId, int themeId, int metricId) throws ODataApplicationException {
		Theme theme = getTheme(observatoryId, themeId);
		List<Metric> metrics = theme.getMetrics();
		for (Metric metric : metrics) {
			if (metric.getId() == metricId) {
				return metric;
			}
		}

		throw new ODataApplicationException("The metric '" + metricId + "' can not be found.", HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
	}

	private Axis getAxis(int observatoryId, int themeId, int metricId, int axisId) throws ODataApplicationException {
		Metric metric = getMetric(observatoryId, themeId, metricId);
		List<Axis> axis = metric.getLinkedAxis();
		for (Axis ax : axis) {
			if (ax.getId() == axisId) {
				return ax;
			}
		}

		throw new ODataApplicationException("The axis '" + axisId + "' can not be found.", HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
	}

	private Level getLevel(int observatoryId, int themeId, int metricId, int axisId, int levelId) throws ODataApplicationException {
		Axis axis = getAxis(observatoryId, themeId, metricId, axisId);
		List<Level> levels = axis.getChildren();
		for (Level level : levels) {
			if (level.getId() == levelId) {
				return level;
			}
		}

		throw new ODataApplicationException("The level '" + levelId + "' can not be found.", HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
	}

	private Entity buildEntity(Observatory item) {
		Entity entity = new Entity();
		entity.addProperty(new Property(null, KpiEdmProvider.ID, ValueType.PRIMITIVE, item.getId()));
		entity.addProperty(new Property(null, KpiEdmProvider.NAME, ValueType.PRIMITIVE, item.getName()));
		entity.setType(KpiEdmProvider.ET_OBSERVATORY_FQN.getFullQualifiedNameAsString());
		entity.setId(Utils.createId(entity, KpiEdmProvider.ID));
		return entity;
	}

	private Entity buildEntity(Theme item) {
		Entity entity = new Entity();
		entity.addProperty(new Property(null, KpiEdmProvider.ID, ValueType.PRIMITIVE, item.getId()));
		entity.addProperty(new Property(null, KpiEdmProvider.NAME, ValueType.PRIMITIVE, item.getName()));
		entity.setType(KpiEdmProvider.ET_THEME_FQN.getFullQualifiedNameAsString());
		entity.setId(Utils.createId(entity, KpiEdmProvider.ID));
		return entity;
	}

	private Entity buildEntity(Metric item) {
		Entity entity = new Entity();
		entity.addProperty(new Property(null, KpiEdmProvider.ID, ValueType.PRIMITIVE, item.getId()));
		entity.addProperty(new Property(null, KpiEdmProvider.NAME, ValueType.PRIMITIVE, item.getName()));
		entity.setType(KpiEdmProvider.ET_METRIC_FQN.getFullQualifiedNameAsString());
		entity.setId(Utils.createId(entity, KpiEdmProvider.ID));
		return entity;
	}

	private Entity buildEntity(Axis item) {
		Entity entity = new Entity();
		entity.addProperty(new Property(null, KpiEdmProvider.ID, ValueType.PRIMITIVE, item.getId()));
		entity.addProperty(new Property(null, KpiEdmProvider.NAME, ValueType.PRIMITIVE, item.getName()));
		entity.setType(KpiEdmProvider.ET_AXIS_FQN.getFullQualifiedNameAsString());
		entity.setId(Utils.createId(entity, KpiEdmProvider.ID));
		return entity;
	}

	private Entity buildEntity(Level item) {
		Entity entity = new Entity();
		entity.addProperty(new Property(null, KpiEdmProvider.ID, ValueType.PRIMITIVE, item.getId()));
		entity.addProperty(new Property(null, KpiEdmProvider.NAME, ValueType.PRIMITIVE, item.getName()));
		entity.setType(KpiEdmProvider.ET_LEVEL_FQN.getFullQualifiedNameAsString());
		entity.setId(Utils.createId(entity, KpiEdmProvider.ID));
		return entity;
	}

	private Entity buildEntity(MetricValue item) {
		Entity entity = new Entity();
		entity.addProperty(new Property(null, KpiEdmProvider.NAME, ValueType.PRIMITIVE, item.getMetric().getName()));
		entity.addProperty(new Property(null, KpiEdmProvider.DATE, ValueType.PRIMITIVE, item.getDate().toString()));
		entity.addProperty(new Property(null, KpiEdmProvider.VALUE, ValueType.PRIMITIVE, item.getValue()));
		entity.addProperty(new Property(null, KpiEdmProvider.OBJECTIVE, ValueType.PRIMITIVE, item.getObjective()));
		entity.addProperty(new Property(null, KpiEdmProvider.MINIMUM, ValueType.PRIMITIVE, item.getMinimum()));
		entity.addProperty(new Property(null, KpiEdmProvider.MAXIMUM, ValueType.PRIMITIVE, item.getMaximum()));
		entity.addProperty(new Property(null, KpiEdmProvider.TOLERANCE, ValueType.PRIMITIVE, item.getTolerance()));
		entity.addProperty(new Property(null, KpiEdmProvider.TENDANCY, ValueType.PRIMITIVE, item.getTendancy()));
		entity.addProperty(new Property(null, KpiEdmProvider.HEALTH, ValueType.PRIMITIVE, item.getHealth()));

		List<ComplexValue> entities = new ArrayList<>();
		List<LevelMember> levels = item.getAxis();
		if (levels != null) {
			for (LevelMember level : levels) {
				ComplexValue levelValues = new ComplexValue();
				levelValues.getValue().add(new Property(null, KpiEdmProvider.NAME, ValueType.PRIMITIVE, level.getLevel().getName()));
				levelValues.getValue().add(new Property(null, KpiEdmProvider.VALUE, ValueType.PRIMITIVE, level.getLabel()));

				entities.add(levelValues);
			}
		}
		entity.addProperty(new Property(null, KpiEdmProvider.LEVEL_VALUES, ValueType.COLLECTION_COMPLEX, entities));

		entity.setType(KpiEdmProvider.ET_VALUE_FQN.getFullQualifiedNameAsString());
		entity.setId(Utils.createId(entity, KpiEdmProvider.NAME));
		return entity;
	}

	private int extractObservatoryId(List<EntityKey> keys) throws ODataApplicationException {
		return extractId(KpiEdmProvider.ET_OBSERVATORY_NAME, keys, true);
	}

	private int extractThemeId(List<EntityKey> keys) throws ODataApplicationException {
		return extractId(KpiEdmProvider.ET_THEME_NAME, keys, true);
	}

	private int extractMetricId(List<EntityKey> keys) throws ODataApplicationException {
		return extractId(KpiEdmProvider.ET_METRIC_NAME, keys, true);
	}

	private int extractAxeId(List<EntityKey> keys) throws ODataApplicationException {
		return extractId(KpiEdmProvider.ET_AXIS_NAME, keys, true);
	}

	private Integer extractLevelId(List<EntityKey> keys, boolean throwException) throws ODataApplicationException {
		return extractId(KpiEdmProvider.ET_LEVEL_NAME, keys, throwException);
	}

	private Integer extractId(String entityType, List<EntityKey> keys, boolean throwException) throws ODataApplicationException {
		if (keys != null) {
			for (EntityKey key : keys) {
				if (key.getEdmEntityType().getName().equals(entityType) && key.getKeyParams() != null) {
					for (UriParameter param : key.getKeyParams()) {
						if (param.getName().equals(KpiEdmProvider.ID)) {
							try {
								return Integer.parseInt(param.getText());
							} catch (Exception e) {
								if (throwException) {
									throw new ODataApplicationException("The ID parameter for '" + entityType + "' '" + param.getText() + "' is not valid.", HttpStatusCode.UNAUTHORIZED.getStatusCode(), Locale.ROOT);
								}
								else {
									return null;
								}
							}
						}
					}
				}
			}
		}

		if (throwException) {
			throw new ODataApplicationException("The 'ID' parameter for '" + entityType + "' can not be found.", HttpStatusCode.UNAUTHORIZED.getStatusCode(), Locale.ROOT);
		}

		return null;
	}

}
