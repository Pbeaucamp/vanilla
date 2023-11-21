package bpm.odata.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
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
import org.apache.olingo.server.api.ODataApplicationException;

import bpm.odata.service.data.MetadataDataset;

public class MetadataEdmProvider extends CsdlAbstractEdmProvider {

	// Service Namespace
	public static final String NAMESPACE = "OData.Metadata";

	// EDM Container
	public static final String CONTAINER_NAME = "Container";
	public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

	// Entity Types Names
	public static final String ET_METADATA_NAME = "Metadata";
	public static final FullQualifiedName ET_METADATA_FQN = new FullQualifiedName(NAMESPACE, ET_METADATA_NAME);

	public static final String ET_BUSINESS_MODEL_NAME = "BusinessModel";
	public static final FullQualifiedName ET_BUSINESS_MODEL_FQN = new FullQualifiedName(NAMESPACE, ET_BUSINESS_MODEL_NAME);

	public static final String ET_BUSINESS_PACKAGE_NAME = "BusinessPackage";
	public static final FullQualifiedName ET_BUSINESS_PACKAGE_FQN = new FullQualifiedName(NAMESPACE, ET_BUSINESS_PACKAGE_NAME);

	public static final String ET_BUSINESS_TABLE_NAME = "BusinessTable";
	public static final FullQualifiedName ET_BUSINESS_TABLE_FQN = new FullQualifiedName(NAMESPACE, ET_BUSINESS_TABLE_NAME);

	public static final String ET_COLUMN_NAME = "Column";
	public static final FullQualifiedName ET_COLUMN_FQN = new FullQualifiedName(NAMESPACE, ET_COLUMN_NAME);

//	public static final String ET_TEST_NAME = "Test";
//	public static final FullQualifiedName ET_TEST_FQN = new FullQualifiedName(NAMESPACE, ET_TEST_NAME);

	// Entity Set Names
	public static final String ES_METADATAS_NAME = "Metadatas";
	public static final String ES_BUSINESS_MODELS_NAME = "BusinessModels";
	public static final String ES_BUSINESS_PACKAGES_NAME = "BusinessPackages";
	public static final String ES_BUSINESS_TABLES_NAME = "BusinessTables";
	public static final String ES_COLUMNS_NAME = "Columns";
//	public static final String ES_TESTS_NAME = "Tests";

	public static final String NAV_TO_BUSINESS_MODELS = "BusinessModels";
	public static final String NAV_TO_BUSINESS_PACKAGES = "BusinessPackages";
	public static final String NAV_TO_BUSINESS_TABLES = "BusinessTables";
	public static final String NAV_TO_COLUMNS = "Columns";

	public static final String ID = "ID";
	public static final String NAME = "Name";
	public static final String DESCRIPTION = "Description";
	public static final String DATA_CSV = "DataAsCsv";
	public static final String DATA_XLS = "DataAsXls";

	private OpenDataComponent openDataComponent;
	private List<MetadataDataset> datasets;

	public MetadataEdmProvider(OpenDataComponent openDataComponent) {
		this.openDataComponent = openDataComponent;
	}

	@Override
	public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) throws ODataException {
		if (entityContainerName == null || entityContainerName.equals(CONTAINER)) {
			try {
				this.datasets = openDataComponent.getDataManager().getMetadataManager().getDatasets(true);
			} catch (Exception e) {
				e.printStackTrace();
			}

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
		entitySets.add(getEntitySet(CONTAINER, ES_METADATAS_NAME));
//		entitySets.add(getEntitySet(CONTAINER, ES_TESTS_NAME));

		if (datasets != null) {
			for (MetadataDataset dataset : datasets) {
				entitySets.add(getEntitySet(CONTAINER, getDatasetName(dataset, true)));
			}
		}

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
		entityTypes.add(getEntityType(ET_METADATA_FQN));
		entityTypes.add(getEntityType(ET_BUSINESS_MODEL_FQN));
		entityTypes.add(getEntityType(ET_BUSINESS_PACKAGE_FQN));
		entityTypes.add(getEntityType(ET_BUSINESS_TABLE_FQN));
		entityTypes.add(getEntityType(ET_COLUMN_FQN));

//		List<CsdlComplexType> complexTypes = new ArrayList<CsdlComplexType>();
//		complexTypes.add(getComplexType(ET_TEST_FQN));
		if (datasets != null) {
			for (MetadataDataset dataset : datasets) {
//				complexTypes.add(getComplexType(new FullQualifiedName(NAMESPACE, getDatasetName(dataset, false))));
				entityTypes.add(getEntityType(new FullQualifiedName(NAMESPACE, getDatasetName(dataset, false))));
			}
		}

		schema.setEntityTypes(entityTypes);
//		schema.setComplexTypes(complexTypes);

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
			if (entitySetName.equals(ES_METADATAS_NAME)) {

				CsdlNavigationPropertyBinding navPropBinding = new CsdlNavigationPropertyBinding();
				navPropBinding.setTarget(ES_BUSINESS_MODELS_NAME); // the target entity set, where the navigation property points to
				navPropBinding.setPath(ES_BUSINESS_MODELS_NAME); // the path from entity type to navigation property
				List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();
				navPropBindingList.add(navPropBinding);

				CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_METADATAS_NAME);
				entitySet.setType(ET_METADATA_FQN);
				entitySet.setNavigationPropertyBindings(navPropBindingList);

				return entitySet;
			}
			else if (entitySetName.equals(ES_BUSINESS_MODELS_NAME)) {
				CsdlNavigationPropertyBinding navPropBindingMetadata = new CsdlNavigationPropertyBinding();
				navPropBindingMetadata.setPath(ET_METADATA_NAME);
				navPropBindingMetadata.setTarget(ES_METADATAS_NAME);

				CsdlNavigationPropertyBinding navPropBindingPackage = new CsdlNavigationPropertyBinding();
				navPropBindingPackage.setTarget(ES_BUSINESS_PACKAGES_NAME); // the target entity set, where the navigation property points to
				navPropBindingPackage.setPath(ES_BUSINESS_PACKAGES_NAME); // the path from entity type to navigation property

				List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();
				navPropBindingList.add(navPropBindingMetadata);
				navPropBindingList.add(navPropBindingPackage);

				CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_BUSINESS_MODELS_NAME);
				entitySet.setType(ET_BUSINESS_MODEL_FQN);
				entitySet.setNavigationPropertyBindings(navPropBindingList);

				return entitySet;
			}
			else if (entitySetName.equals(ES_BUSINESS_PACKAGES_NAME)) {
				CsdlNavigationPropertyBinding navPropBinding = new CsdlNavigationPropertyBinding();
				navPropBinding.setPath(ET_BUSINESS_MODEL_NAME);
				navPropBinding.setTarget(ES_BUSINESS_MODELS_NAME);

				CsdlNavigationPropertyBinding navPropBindingTable = new CsdlNavigationPropertyBinding();
				navPropBindingTable.setTarget(ES_BUSINESS_TABLES_NAME);
				navPropBindingTable.setPath(ES_BUSINESS_TABLES_NAME);

				List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();
				navPropBindingList.add(navPropBinding);
				navPropBindingList.add(navPropBindingTable);

				CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_BUSINESS_PACKAGES_NAME);
				entitySet.setType(ET_BUSINESS_PACKAGE_FQN);
				entitySet.setNavigationPropertyBindings(navPropBindingList);

				return entitySet;
			}
			else if (entitySetName.equals(ES_BUSINESS_TABLES_NAME)) {
				CsdlNavigationPropertyBinding navPropBinding = new CsdlNavigationPropertyBinding();
				navPropBinding.setPath(ET_BUSINESS_PACKAGE_NAME);
				navPropBinding.setTarget(ES_BUSINESS_PACKAGES_NAME);

				CsdlNavigationPropertyBinding navPropBindingColumn = new CsdlNavigationPropertyBinding();
				navPropBindingColumn.setTarget(ES_COLUMNS_NAME);
				navPropBindingColumn.setPath(ES_COLUMNS_NAME);

				List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();
				navPropBindingList.add(navPropBinding);
				navPropBindingList.add(navPropBindingColumn);

				CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_BUSINESS_TABLES_NAME);
				entitySet.setType(ET_BUSINESS_TABLE_FQN);
				entitySet.setNavigationPropertyBindings(navPropBindingList);

				return entitySet;
			}
			else if (entitySetName.equals(ES_COLUMNS_NAME)) {
				CsdlNavigationPropertyBinding navPropBinding = new CsdlNavigationPropertyBinding();
				navPropBinding.setPath(ET_COLUMN_NAME);
				navPropBinding.setTarget(ES_COLUMNS_NAME);
				List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();
				navPropBindingList.add(navPropBinding);

				CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_COLUMNS_NAME);
				entitySet.setType(ET_COLUMN_FQN);
				entitySet.setNavigationPropertyBindings(navPropBindingList);

				return entitySet;
			}
//			else if (entitySetName.equals(ES_TESTS_NAME)) {
//
//				CsdlEntitySet entitySet = new CsdlEntitySet();
//				entitySet.setName(ES_TESTS_NAME);
//				entitySet.setType(ET_TEST_FQN);
//
//				return entitySet;
//			}
			else {
				MetadataDataset dataset = findDataset(entitySetName, true);
				if (dataset != null) {

					CsdlEntitySet entitySet = new CsdlEntitySet();
					entitySet.setName(entitySetName);
					entitySet.setType(new FullQualifiedName(NAMESPACE, getDatasetName(dataset, false)));

					return entitySet;
				}
			}
		}

		return null;
	}

	@Override
	public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {
		CsdlEntityType entityType = null;
		if (entityTypeName.equals(ET_METADATA_FQN)) {

			// create EntityType properties
			CsdlProperty id = new CsdlProperty().setName(ID).setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
			CsdlProperty name = new CsdlProperty().setName(NAME).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty description = new CsdlProperty().setName(DESCRIPTION).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

			// create CsdlPropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName(ID);

			// navigation property: one-to-many
			CsdlNavigationProperty navProp = new CsdlNavigationProperty().setName(NAV_TO_BUSINESS_MODELS).setType(ET_BUSINESS_MODEL_FQN).setCollection(true).setPartner(ET_METADATA_NAME);

			List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
			navPropList.add(navProp);

			// configure EntityType
			entityType = new CsdlEntityType();
			entityType.setName(ET_METADATA_NAME);
			entityType.setProperties(Arrays.asList(id, name, description));
			entityType.setKey(Arrays.asList(propertyRef));
			entityType.setNavigationProperties(navPropList);
		}
		else if (entityTypeName.equals(ET_BUSINESS_MODEL_FQN)) {
			CsdlProperty name = new CsdlProperty().setName(NAME).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty description = new CsdlProperty().setName(DESCRIPTION).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

			// create CsdlPropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName(NAME);

			CsdlNavigationProperty navPropMetadata = new CsdlNavigationProperty().setName(ET_METADATA_NAME).setType(ET_METADATA_FQN).setNullable(false).setPartner(ES_BUSINESS_MODELS_NAME);

			// navigation property: one-to-many
			CsdlNavigationProperty navPropBusinessPackages = new CsdlNavigationProperty().setName(NAV_TO_BUSINESS_PACKAGES).setType(ET_BUSINESS_PACKAGE_FQN).setCollection(true).setPartner(ET_BUSINESS_MODEL_NAME);

			List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
			navPropList.add(navPropMetadata);
			navPropList.add(navPropBusinessPackages);

			// configure EntityType
			entityType = new CsdlEntityType();
			entityType.setName(ET_BUSINESS_MODEL_NAME);
			entityType.setProperties(Arrays.asList(name, description));
			entityType.setKey(Collections.singletonList(propertyRef));
			entityType.setNavigationProperties(navPropList);
		}
		else if (entityTypeName.equals(ET_BUSINESS_PACKAGE_FQN)) {
			CsdlProperty name = new CsdlProperty().setName(NAME).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty description = new CsdlProperty().setName(DESCRIPTION).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

			// create CsdlPropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName(NAME);

			CsdlNavigationProperty navPropModel = new CsdlNavigationProperty().setName(ET_BUSINESS_MODEL_NAME).setType(ET_BUSINESS_MODEL_FQN).setNullable(false).setPartner(ES_BUSINESS_PACKAGES_NAME);

			// navigation property: one-to-many
			CsdlNavigationProperty navPropBusinessTables = new CsdlNavigationProperty().setName(NAV_TO_BUSINESS_TABLES).setType(ET_BUSINESS_TABLE_FQN).setCollection(true).setPartner(ET_BUSINESS_PACKAGE_NAME);

			List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
			navPropList.add(navPropModel);
			navPropList.add(navPropBusinessTables);

			// configure EntityType
			entityType = new CsdlEntityType();
			entityType.setName(ET_BUSINESS_PACKAGE_NAME);
			entityType.setProperties(Arrays.asList(name, description));
			entityType.setKey(Collections.singletonList(propertyRef));
			entityType.setNavigationProperties(navPropList);
		}
		else if (entityTypeName.equals(ET_BUSINESS_TABLE_FQN)) {
			CsdlProperty name = new CsdlProperty().setName(NAME).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty description = new CsdlProperty().setName(DESCRIPTION).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty dataCsv = new CsdlProperty().setName(DATA_CSV).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty dataXls = new CsdlProperty().setName(DATA_XLS).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

			// create CsdlPropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName(NAME);

			CsdlNavigationProperty navProp = new CsdlNavigationProperty().setName(ET_BUSINESS_PACKAGE_NAME).setType(ET_BUSINESS_PACKAGE_FQN).setNullable(false).setPartner(ES_BUSINESS_TABLES_NAME);

			// navigation property: one-to-many
			CsdlNavigationProperty navPropColumns = new CsdlNavigationProperty().setName(NAV_TO_COLUMNS).setType(ET_COLUMN_FQN).setCollection(true).setPartner(ET_BUSINESS_TABLE_NAME);

			List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
			navPropList.add(navProp);
			navPropList.add(navPropColumns);

			// configure EntityType
			entityType = new CsdlEntityType();
			entityType.setName(ET_BUSINESS_TABLE_NAME);
			entityType.setProperties(Arrays.asList(name, description, dataCsv, dataXls));
			entityType.setKey(Collections.singletonList(propertyRef));
			entityType.setNavigationProperties(navPropList);
		}
		else if (entityTypeName.equals(ET_COLUMN_FQN)) {
			CsdlProperty name = new CsdlProperty().setName(NAME).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty description = new CsdlProperty().setName(DESCRIPTION).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

			// create CsdlPropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName(NAME);

			CsdlNavigationProperty navProp = new CsdlNavigationProperty().setName(ET_BUSINESS_TABLE_NAME).setType(ET_BUSINESS_TABLE_FQN).setNullable(false).setPartner(ES_COLUMNS_NAME);

			List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
			navPropList.add(navProp);

			// configure EntityType
			entityType = new CsdlEntityType();
			entityType.setName(ET_COLUMN_NAME);
			entityType.setProperties(Arrays.asList(name, description));
			entityType.setKey(Collections.singletonList(propertyRef));
			entityType.setNavigationProperties(navPropList);
		}
		else {
			MetadataDataset dataset = findDataset(entityTypeName.getName(), false);
			if (dataset != null) {
	
				String datasetName = getDatasetName(dataset, false);
				List<String> columns = getDatasetColumnNames(datasetName, dataset);
	
				List<CsdlProperty> props = new ArrayList<>();
				List<CsdlPropertyRef> keys = new ArrayList<>();
				for (String column : columns) {
					CsdlProperty prop = new CsdlProperty().setName(column).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
					props.add(prop);

					CsdlPropertyRef propertyRef = new CsdlPropertyRef();
					propertyRef.setName(column);
					
					keys.add(propertyRef);
				}
				
				CsdlPropertyRef propertyRef = new CsdlPropertyRef();
				propertyRef.setName(NAME);
	
				// configure EntityType
				entityType = new CsdlEntityType();
				entityType.setName(datasetName);
				entityType.setProperties(props);
				entityType.setKey(keys);
			}
		}

		return entityType;
	}
	
//	@Override
//	public CsdlComplexType getComplexType(FullQualifiedName complexTypeName) throws ODataException {
////		MetadataDataset dataset = findDataset(complexTypeName.getName(), false);
////		if (dataset != null) {
////
////			String datasetName = getDatasetName(dataset, false);
////			List<String> columns = getDatasetColumnNames(datasetName, dataset);
////
////			List<CsdlProperty> props = new ArrayList<>();
////			for (String column : columns) {
////				CsdlProperty prop = new CsdlProperty().setName(column).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
////				props.add(prop);
////			}
////
////			// configure EntityType
////			CsdlComplexType entityType = new CsdlComplexType();
////			entityType.setName(datasetName);
////			entityType.setProperties(props);
////			
////			return entityType;
////		}
//		if (complexTypeName.equals(ET_TEST_FQN)) {
//
//			// create EntityType properties
//			CsdlProperty id = new CsdlProperty().setName(ID).setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
//			CsdlProperty name = new CsdlProperty().setName(NAME).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
//			CsdlProperty description = new CsdlProperty().setName(DESCRIPTION).setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
//
//			// create CsdlPropertyRef for Key element
//			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
//			propertyRef.setName(ID);
//
//			// configure EntityType
//			CsdlComplexType entityType = new CsdlComplexType();
//			entityType.setName(ET_TEST_NAME);
////			entityType.setBaseType(ET_TEST_FQN);
//			entityType.setProperties(Arrays.asList(id, name, description));
//			
//			return entityType;
//		}
//		
//		return null;
//	}
	
	private MetadataDataset findDataset(String datasetName, boolean isList) {
		return openDataComponent.getDataManager().getMetadataManager().findDataset(datasets, datasetName, isList);
	}

	private String getDatasetName(MetadataDataset dataset, boolean isList) {
		return openDataComponent.getDataManager().getMetadataManager().getDatasetName(dataset, isList);
	}

	private List<String> getDatasetColumnNames(String datasetName, MetadataDataset dataset) throws ODataApplicationException {
		return openDataComponent.getDataManager().getMetadataManager().getDatasetColumnNames(datasetName, dataset);
	}
}
