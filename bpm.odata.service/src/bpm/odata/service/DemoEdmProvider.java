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

public class DemoEdmProvider extends CsdlAbstractEdmProvider {

	// Service Namespace
	public static final String NAMESPACE = "OData.Demo";

	// EDM Container
	public static final String CONTAINER_NAME = "Container";
	public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

	// Entity Types Names
	public static final String ET_PRODUCT_NAME = "Product";
	public static final FullQualifiedName ET_PRODUCT_FQN = new FullQualifiedName(NAMESPACE, ET_PRODUCT_NAME);

	public static final String ET_CATEGORY_NAME = "Category";
	public static final FullQualifiedName ET_CATEGORY_FQN = new FullQualifiedName(NAMESPACE, ET_CATEGORY_NAME);

	// Entity Set Names
	public static final String ES_PRODUCTS_NAME = "Products";
	public static final String ES_CATEGORIES_NAME = "Categories";
	public static final String NAV_TO_CATEGORY = "Category";
	public static final String NAV_TO_PRODUCTS = "Products";

	@Override
	public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {

		if (entityTypeName.equals(ET_CATEGORY_FQN)) {
			// create EntityType properties
			CsdlProperty id = new CsdlProperty().setName("ID").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
			CsdlProperty name = new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

			// create PropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName("ID");

			// navigation property: one-to-many
			CsdlNavigationProperty navProp = new CsdlNavigationProperty()
				.setName(NAV_TO_PRODUCTS)
				.setType(ET_PRODUCT_FQN)
				.setCollection(true)
				.setPartner("Category");
			List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
			navPropList.add(navProp);

			// configure EntityType
			CsdlEntityType entityType = new CsdlEntityType();
			entityType.setName(ET_CATEGORY_NAME);
			entityType.setProperties(Arrays.asList(id, name));
			entityType.setKey(Arrays.asList(propertyRef));
			entityType.setNavigationProperties(navPropList);

			return entityType;
		}
		else if (entityTypeName.equals(ET_PRODUCT_FQN)) {

			// create EntityType properties
			CsdlProperty id = new CsdlProperty().setName("ID").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
			CsdlProperty name = new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty description = new CsdlProperty().setName("Description").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

			// create CsdlPropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName("ID");

			CsdlNavigationProperty navProp = new CsdlNavigationProperty()
				.setName("Category")
				.setType(ET_CATEGORY_FQN)
				.setNullable(false)
				.setPartner("Products");

			List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
			navPropList.add(navProp);

			// configure EntityType
			CsdlEntityType entityType = new CsdlEntityType();
			entityType.setName(ET_PRODUCT_NAME);
			entityType.setProperties(Arrays.asList(id, name, description));
			entityType.setKey(Collections.singletonList(propertyRef));
			entityType.setNavigationProperties(navPropList);

			return entityType;
		}

		return null;
	}

	@Override
	public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) throws ODataException {
		if (entityContainer.equals(CONTAINER)) {
			if (entitySetName.equals(ES_CATEGORIES_NAME)) {
				CsdlNavigationPropertyBinding navPropBinding = new CsdlNavigationPropertyBinding();
				navPropBinding.setTarget("Products"); // the target entity set, where the navigation property points to
				navPropBinding.setPath("Products"); // the path from entity type to navigation property
				List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();
				navPropBindingList.add(navPropBinding);

				CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_CATEGORIES_NAME);
				entitySet.setType(ET_CATEGORY_FQN);
				entitySet.setNavigationPropertyBindings(navPropBindingList);

				return entitySet;
			}
			else if (entitySetName.equals(ES_PRODUCTS_NAME)) {
				CsdlNavigationPropertyBinding navPropBinding = new CsdlNavigationPropertyBinding();
				// the path from entity type to navigation property
				navPropBinding.setPath("Category");
				// target entitySet, where the nav prop points to
				navPropBinding.setTarget("Categories");
				List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();
				navPropBindingList.add(navPropBinding);

				CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_PRODUCTS_NAME);
				entitySet.setType(ET_PRODUCT_FQN);
				entitySet.setNavigationPropertyBindings(navPropBindingList);

				return entitySet;
			}
		}

		return null;
	}

	@Override
	public CsdlEntityContainer getEntityContainer() throws ODataException {
		// create EntitySets
		List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
		entitySets.add(getEntitySet(CONTAINER, ES_PRODUCTS_NAME));
		entitySets.add(getEntitySet(CONTAINER, ES_CATEGORIES_NAME));

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
		entityTypes.add(getEntityType(ET_PRODUCT_FQN));
		entityTypes.add(getEntityType(ET_CATEGORY_FQN));
		schema.setEntityTypes(entityTypes);

		// add EntityContainer
		schema.setEntityContainer(getEntityContainer());

		// finally
		List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
		schemas.add(schema);

		return schemas;
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
}
