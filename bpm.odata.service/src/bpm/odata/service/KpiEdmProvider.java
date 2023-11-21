package bpm.odata.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.ex.ODataException;

public class KpiEdmProvider extends CsdlAbstractEdmProvider {

	// Service Namespace
	public static final String NAMESPACE = "OData.Kpi";

	// EDM Container
	public static final String CONTAINER_NAME = "Container";
	public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

	// Entity Types Names
	public static final String ET_OBSERVATORY_NAME = "Observatory";
	public static final FullQualifiedName ET_OBSERVATORY_FQN = new FullQualifiedName(NAMESPACE, ET_OBSERVATORY_NAME);
	
	public static final String ET_THEME_NAME = "Theme";
	public static final FullQualifiedName ET_THEME_FQN = new FullQualifiedName(NAMESPACE, ET_THEME_NAME);

	public static final String ET_METRIC_NAME = "Metric";
	public static final FullQualifiedName ET_METRIC_FQN = new FullQualifiedName(NAMESPACE, ET_METRIC_NAME);

	public static final String ET_AXIS_NAME = "Axis";
	public static final FullQualifiedName ET_AXIS_FQN = new FullQualifiedName(NAMESPACE, ET_AXIS_NAME);

	public static final String ET_LEVEL_NAME = "Level";
	public static final FullQualifiedName ET_LEVEL_FQN = new FullQualifiedName(NAMESPACE, ET_LEVEL_NAME);

	public static final String ET_VALUE_NAME = "Value";
	public static final FullQualifiedName ET_VALUE_FQN = new FullQualifiedName(NAMESPACE, ET_VALUE_NAME);

	public static final String ET_LEVEL_VALUE_NAME = "LevelValue";
	public static final FullQualifiedName ET_LEVEL_VALUE_FQN = new FullQualifiedName(NAMESPACE, ET_LEVEL_VALUE_NAME);

	// Entity Set Names
	public static final String ES_OBSERVATORIES_NAME = "Observatories";
	public static final String ES_THEMES_NAME = "Themes";
	public static final String ES_METRICS_NAME = "Metrics";
	public static final String ES_AXES_NAME = "Axes";
	public static final String ES_LEVELS_NAME = "Levels";
	public static final String ES_VALUES_NAME = "Values";

	public static final String NAV_TO_THEMES = "Themes";
	public static final String NAV_TO_METRICS = "Metrics";
	public static final String NAV_TO_AXES = "Axes";
	public static final String NAV_TO_LEVELS = "Levels";
	public static final String NAV_TO_VALUES = "Values";

	public static final String ID = "ID";
	public static final String NAME = "Name";

	public static final String DATE = "Date";
	public static final String VALUE = "Value";
	public static final String OBJECTIVE = "Objective";
	public static final String MINIMUM = "Minimum";
	public static final String MAXIMUM = "Maximum";
	public static final String TOLERANCE = "Tolerance";
	public static final String TENDANCY = "Tendancy";
	public static final String HEALTH = "Health";
	public static final String LEVEL_VALUES = "LevelValues";


	public KpiEdmProvider(OpenDataComponent openDataComponent) {
		
	}

	@Override
	public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) throws ODataException {
		if (entityContainerName == null || entityContainerName.equals(CONTAINER)) {
			CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
			entityContainerInfo.setContainerName(CONTAINER);
			return entityContainerInfo;
		}

		return null;
	}

	@Override
	public CsdlEntityContainer getEntityContainer() throws ODataException {
		// create EntitySets
		List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
		entitySets.add(getEntitySet(CONTAINER, ES_OBSERVATORIES_NAME));

		// create EntityContainer
		CsdlEntityContainer entityContainer = new CsdlEntityContainer();
		entityContainer.setName(CONTAINER_NAME);
		entityContainer.setEntitySets(entitySets);

		return entityContainer;
	}

	@Override
	public List<CsdlSchema> getSchemas() throws ODataException {
		// create Schema
		CsdlSchema schema = new CsdlSchema();
		schema.setNamespace(NAMESPACE);

		// add EntityTypes
		List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
		entityTypes.add(getEntityType(ET_OBSERVATORY_FQN));
		entityTypes.add(getEntityType(ET_THEME_FQN));
		entityTypes.add(getEntityType(ET_METRIC_FQN));
		entityTypes.add(getEntityType(ET_AXIS_FQN));
		entityTypes.add(getEntityType(ET_LEVEL_FQN));
		entityTypes.add(getEntityType(ET_VALUE_FQN));
		
		List<CsdlComplexType> complexTypes = new ArrayList<>();
		complexTypes.add(getComplexType(ET_LEVEL_VALUE_FQN));

		schema.setEntityTypes(entityTypes);
		schema.setComplexTypes(complexTypes);

		// add EntityContainer
		schema.setEntityContainer(getEntityContainer());

		// finally
		List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
		schemas.add(schema);

		return schemas;
	}

	@Override
	public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) throws ODataException {
		if (entityContainer.equals(CONTAINER)) {
			if (entitySetName.equals(ES_OBSERVATORIES_NAME)) {
				CsdlNavigationPropertyBinding navPropBinding = new CsdlNavigationPropertyBinding();
				navPropBinding.setTarget(ES_THEMES_NAME); // the target entity set, where the navigation property points to
				navPropBinding.setPath(ES_THEMES_NAME); // the path from entity type to navigation property
				
				List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();
				navPropBindingList.add(navPropBinding);

				CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_OBSERVATORIES_NAME);
				entitySet.setType(ET_OBSERVATORY_FQN);
				entitySet.setNavigationPropertyBindings(navPropBindingList);

				return entitySet;
			}
			else if (entitySetName.equals(ES_THEMES_NAME)) {
				CsdlNavigationPropertyBinding navPropBindingObservatory = new CsdlNavigationPropertyBinding();
				navPropBindingObservatory.setPath(ET_OBSERVATORY_NAME);
				navPropBindingObservatory.setTarget(ES_OBSERVATORIES_NAME);

				CsdlNavigationPropertyBinding navPropBindingKpi = new CsdlNavigationPropertyBinding();
				navPropBindingKpi.setTarget(ES_METRICS_NAME); // the target entity set, where the navigation property points to
				navPropBindingKpi.setPath(ES_METRICS_NAME); // the path from entity type to navigation property

				List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();
				navPropBindingList.add(navPropBindingObservatory);
				navPropBindingList.add(navPropBindingKpi);

				CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_THEMES_NAME);
				entitySet.setType(ET_THEME_FQN);
				entitySet.setNavigationPropertyBindings(navPropBindingList);

				return entitySet;
			}
			else if (entitySetName.equals(ES_METRICS_NAME)) {
				CsdlNavigationPropertyBinding navPropBindingTheme = new CsdlNavigationPropertyBinding();
				navPropBindingTheme.setPath(ET_THEME_NAME);
				navPropBindingTheme.setTarget(ES_THEMES_NAME);

				CsdlNavigationPropertyBinding navPropBindingAxe = new CsdlNavigationPropertyBinding();
				navPropBindingAxe.setTarget(ES_AXES_NAME); // the target entity set, where the navigation property points to
				navPropBindingAxe.setPath(ES_AXES_NAME); // the path from entity type to navigation property

				CsdlNavigationPropertyBinding navPropBindingValues = new CsdlNavigationPropertyBinding();
				navPropBindingValues.setTarget(ES_VALUES_NAME); // the target entity set, where the navigation property points to
				navPropBindingValues.setPath(ES_VALUES_NAME); // the path from entity type to navigation property

				List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();
				navPropBindingList.add(navPropBindingTheme);
				navPropBindingList.add(navPropBindingAxe);
				navPropBindingList.add(navPropBindingValues);

				CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_METRICS_NAME);
				entitySet.setType(ET_METRIC_FQN);
				entitySet.setNavigationPropertyBindings(navPropBindingList);

				return entitySet;
			}
			else if (entitySetName.equals(ES_AXES_NAME)) {
				CsdlNavigationPropertyBinding navPropBinding = new CsdlNavigationPropertyBinding();
				navPropBinding.setPath(ET_METRIC_NAME);
				navPropBinding.setTarget(ES_METRICS_NAME);

				CsdlNavigationPropertyBinding navPropBindingTable = new CsdlNavigationPropertyBinding();
				navPropBindingTable.setTarget(ES_LEVELS_NAME);
				navPropBindingTable.setPath(ES_LEVELS_NAME);

				List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();
				navPropBindingList.add(navPropBinding);
				navPropBindingList.add(navPropBindingTable);

				CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_AXES_NAME);
				entitySet.setType(ET_AXIS_FQN);
				entitySet.setNavigationPropertyBindings(navPropBindingList);

				return entitySet;
			}
			else if (entitySetName.equals(ES_LEVELS_NAME)) {
				CsdlNavigationPropertyBinding navPropBindingTheme = new CsdlNavigationPropertyBinding();
				navPropBindingTheme.setPath(ET_AXIS_NAME);
				navPropBindingTheme.setTarget(ES_AXES_NAME);

				CsdlNavigationPropertyBinding navPropBindingValues = new CsdlNavigationPropertyBinding();
				navPropBindingValues.setTarget(ES_VALUES_NAME); // the target entity set, where the navigation property points to
				navPropBindingValues.setPath(ES_VALUES_NAME); // the path from entity type to navigation property

				List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();
				navPropBindingList.add(navPropBindingTheme);
				navPropBindingList.add(navPropBindingValues);

				CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_LEVELS_NAME);
				entitySet.setType(ET_LEVEL_FQN);
				entitySet.setNavigationPropertyBindings(navPropBindingList);

				return entitySet;
			}
			else if (entitySetName.equals(ES_VALUES_NAME)) {
				CsdlNavigationPropertyBinding navPropBinding = new CsdlNavigationPropertyBinding();
				navPropBinding.setPath(ET_METRIC_NAME);
				navPropBinding.setTarget(ES_METRICS_NAME);
				
				List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();
				navPropBindingList.add(navPropBinding);

				CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_VALUES_NAME);
				entitySet.setType(ET_VALUE_FQN);
				entitySet.setNavigationPropertyBindings(navPropBindingList);

				return entitySet;
			}
		}

		return null;
	}

	@Override
	public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {
		CsdlEntityType entityType = null;
		if (entityTypeName.equals(ET_OBSERVATORY_FQN)) {

			// create EntityType properties
			CsdlProperty id = new CsdlProperty().setName(ID).setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
			CsdlProperty name = new CsdlProperty().setName(NAME).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

			// create CsdlPropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName(ID);

			// navigation property: one-to-many
			CsdlNavigationProperty navProp = new CsdlNavigationProperty().setName(NAV_TO_THEMES).setType(ET_THEME_FQN).setCollection(true).setPartner(ET_OBSERVATORY_NAME);

			List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
			navPropList.add(navProp);

			// configure EntityType
			entityType = new CsdlEntityType();
			entityType.setName(ET_OBSERVATORY_NAME);
			entityType.setProperties(Arrays.asList(id, name));
			entityType.setKey(Arrays.asList(propertyRef));
			entityType.setNavigationProperties(navPropList);
		}
		else if (entityTypeName.equals(ET_THEME_FQN)) {

			// create EntityType properties
			CsdlProperty id = new CsdlProperty().setName(ID).setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
			CsdlProperty name = new CsdlProperty().setName(NAME).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

			// create CsdlPropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName(ID);
			
			CsdlNavigationProperty navPropObservatory = new CsdlNavigationProperty().setName(ET_OBSERVATORY_NAME).setType(ET_OBSERVATORY_FQN).setNullable(false).setPartner(ES_THEMES_NAME);

			// navigation property: one-to-many
			CsdlNavigationProperty navProp = new CsdlNavigationProperty().setName(NAV_TO_METRICS).setType(ET_METRIC_FQN).setCollection(true).setPartner(ET_THEME_NAME);

			List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
			navPropList.add(navPropObservatory);
			navPropList.add(navProp);

			// configure EntityType
			entityType = new CsdlEntityType();
			entityType.setName(ET_THEME_NAME);
			entityType.setProperties(Arrays.asList(id, name));
			entityType.setKey(Arrays.asList(propertyRef));
			entityType.setNavigationProperties(navPropList);
		}
		else if (entityTypeName.equals(ET_METRIC_FQN)) {
			CsdlProperty id = new CsdlProperty().setName(ID).setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
			CsdlProperty name = new CsdlProperty().setName(NAME).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

			// create CsdlPropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName(ID);

			CsdlNavigationProperty navPropTheme = new CsdlNavigationProperty().setName(ET_THEME_NAME).setType(ET_THEME_FQN).setNullable(false).setPartner(ES_METRICS_NAME);

			// navigation property: one-to-many
			CsdlNavigationProperty navPropAxes = new CsdlNavigationProperty().setName(NAV_TO_AXES).setType(ET_AXIS_FQN).setCollection(true).setPartner(ET_METRIC_NAME);
			CsdlNavigationProperty navPropValues = new CsdlNavigationProperty().setName(NAV_TO_VALUES).setType(ET_VALUE_FQN).setCollection(true).setPartner(ET_METRIC_NAME);

			List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
			navPropList.add(navPropTheme);
			navPropList.add(navPropAxes);
			navPropList.add(navPropValues);

			// configure EntityType
			entityType = new CsdlEntityType();
			entityType.setName(ET_METRIC_NAME);
			entityType.setProperties(Arrays.asList(id, name));
			entityType.setKey(Collections.singletonList(propertyRef));
			entityType.setNavigationProperties(navPropList);
		}
		else if (entityTypeName.equals(ET_AXIS_FQN)) {
			CsdlProperty id = new CsdlProperty().setName(ID).setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
			CsdlProperty name = new CsdlProperty().setName(NAME).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

			// create CsdlPropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName(ID);

			CsdlNavigationProperty navPropModel = new CsdlNavigationProperty().setName(ET_METRIC_NAME).setType(ET_METRIC_FQN).setNullable(false).setPartner(ES_AXES_NAME);

			// navigation property: one-to-many
			CsdlNavigationProperty navPropLevels = new CsdlNavigationProperty().setName(NAV_TO_LEVELS).setType(ET_LEVEL_FQN).setCollection(true).setPartner(ET_AXIS_NAME);

			List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
			navPropList.add(navPropModel);
			navPropList.add(navPropLevels);

			// configure EntityType
			entityType = new CsdlEntityType();
			entityType.setName(ET_AXIS_NAME);
			entityType.setProperties(Arrays.asList(id, name));
			entityType.setKey(Collections.singletonList(propertyRef));
			entityType.setNavigationProperties(navPropList);
		}
		else if (entityTypeName.equals(ET_LEVEL_FQN)) {
			CsdlProperty id = new CsdlProperty().setName(ID).setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
			CsdlProperty name = new CsdlProperty().setName(NAME).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

			// create CsdlPropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName(ID);

			CsdlNavigationProperty navPropAxis = new CsdlNavigationProperty().setName(ET_AXIS_NAME).setType(ET_AXIS_FQN).setNullable(false).setPartner(ES_LEVELS_NAME);

			// navigation property: one-to-many
			CsdlNavigationProperty navPropValues = new CsdlNavigationProperty().setName(NAV_TO_VALUES).setType(ET_VALUE_FQN).setCollection(true).setPartner(ET_LEVEL_NAME);

			List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
			navPropList.add(navPropAxis);
			navPropList.add(navPropValues);

			// configure EntityType
			entityType = new CsdlEntityType();
			entityType.setName(ET_LEVEL_NAME);
			entityType.setProperties(Arrays.asList(id, name));
			entityType.setKey(Collections.singletonList(propertyRef));
			entityType.setNavigationProperties(navPropList);
		}
		else if (entityTypeName.equals(ET_VALUE_FQN)) {
			CsdlProperty name = new CsdlProperty().setName(NAME).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty date = new CsdlProperty().setName(DATE).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty value = new CsdlProperty().setName(VALUE).setType(EdmPrimitiveTypeKind.Double.getFullQualifiedName());
			CsdlProperty objective = new CsdlProperty().setName(OBJECTIVE).setType(EdmPrimitiveTypeKind.Double.getFullQualifiedName());
			CsdlProperty minimum = new CsdlProperty().setName(MINIMUM).setType(EdmPrimitiveTypeKind.Double.getFullQualifiedName());
			CsdlProperty maximum = new CsdlProperty().setName(MAXIMUM).setType(EdmPrimitiveTypeKind.Double.getFullQualifiedName());
			CsdlProperty tolerance = new CsdlProperty().setName(TOLERANCE).setType(EdmPrimitiveTypeKind.Double.getFullQualifiedName());
			CsdlProperty tendancy = new CsdlProperty().setName(TENDANCY).setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName());
			CsdlProperty health = new CsdlProperty().setName(HEALTH).setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName());
			CsdlProperty levelValues = new CsdlProperty().setName(LEVEL_VALUES).setCollection(true).setType(ET_LEVEL_VALUE_FQN);

			// create CsdlPropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName(NAME);

			CsdlNavigationProperty navPropMetric = new CsdlNavigationProperty().setName(ET_METRIC_NAME).setType(ET_METRIC_FQN).setNullable(false).setPartner(ES_VALUES_NAME);
			CsdlNavigationProperty navPropLevel = new CsdlNavigationProperty().setName(ET_LEVEL_NAME).setType(ET_LEVEL_FQN).setNullable(false).setPartner(ES_VALUES_NAME);

			List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
			navPropList.add(navPropMetric);
			navPropList.add(navPropLevel);

			// configure EntityType
			entityType = new CsdlEntityType();
			entityType.setName(ET_VALUE_NAME);
			entityType.setProperties(Arrays.asList(name, date, value, objective, minimum, maximum, tolerance, tendancy, health, levelValues));
			entityType.setKey(Collections.singletonList(propertyRef));
			entityType.setNavigationProperties(navPropList);
		}

		return entityType;
	}
	
	@Override
	public CsdlComplexType getComplexType(FullQualifiedName complexTypeName) throws ODataException {
		CsdlComplexType entityType = null;
		if (complexTypeName.equals(ET_LEVEL_VALUE_FQN)) {
			CsdlProperty name = new CsdlProperty().setName(NAME).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty value = new CsdlProperty().setName(VALUE).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			
			// create CsdlPropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName(NAME);

//			CsdlNavigationProperty navPropMetric = new CsdlNavigationProperty().setName(ET_METRIC_NAME).setType(ET_METRIC_FQN).setNullable(false).setPartner(ES_VALUES_NAME);
//			CsdlNavigationProperty navPropLevel = new CsdlNavigationProperty().setName(ET_LEVEL_NAME).setType(ET_LEVEL_FQN).setNullable(false).setPartner(ES_VALUES_NAME);
//
//			List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
//			navPropList.add(navPropMetric);
//			navPropList.add(navPropLevel);

			// configure EntityType
			entityType = new CsdlComplexType();
			entityType.setName(ET_LEVEL_VALUE_NAME);
			entityType.setProperties(Arrays.asList(name, value));
//			entityType.setKey(Collections.singletonList(propertyRef));
//			entityType.setNavigationProperties(navPropList);
//			entityType.setOpenType(true);
		}
		
		return entityType;
	}
}
