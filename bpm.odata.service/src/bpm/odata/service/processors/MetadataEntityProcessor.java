package bpm.odata.service.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmNavigationPropertyBinding;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;

import bpm.odata.service.OpenDataComponent;
import bpm.odata.service.data.EntityKey;
import bpm.odata.service.data.SegmentResponse;
import bpm.odata.service.utils.Utils;

public class MetadataEntityProcessor implements EntityProcessor {

	private OpenDataComponent component;
	private OData odata;
	private ServiceMetadata serviceMetadata;

	public MetadataEntityProcessor(OpenDataComponent component) {
		this.component = component;
	}

	public void init(OData odata, ServiceMetadata serviceMetadata) {
		this.odata = odata;
		this.serviceMetadata = serviceMetadata;
	}

	@Override
	public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat) throws ODataApplicationException, SerializerException {
		// 1st step: retrieve the requested Entity:
		// can be "normal" read operation, or navigation (to-one)
		List<UriResource> resourceParts = uriInfo.getUriResourceParts();

		UriResource uriResource = resourceParts.get(0);
		if (!(uriResource instanceof UriResourceEntitySet)) {
			throw new ODataApplicationException("Only EntitySet is supported", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
		}

		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResource;
		EdmEntitySet startEdmEntitySet = uriResourceEntitySet.getEntitySet();
		
		SegmentResponse segmentResponse = manageSegments(resourceParts, startEdmEntitySet);

		if (segmentResponse.getEntity() == null) {
			throw new ODataApplicationException("Nothing found.", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
		}

		SelectOption selectOption = uriInfo.getSelectOption();

		ExpandOption expandOption = uriInfo.getExpandOption();
		if (expandOption != null) {
			// retrieve the EdmNavigationProperty from the expand expression
			// Note: in our example, we have only one NavigationProperty, so we can directly access it
			EdmNavigationProperty edmNavigationProperty = null;
			ExpandItem expandItem = expandOption.getExpandItems().get(0);
			if (expandItem.isStar()) {
				List<EdmNavigationPropertyBinding> bindings = segmentResponse.getEntitySet().getNavigationPropertyBindings();
				// we know that there are navigation bindings
				// however normally in this case a check if navigation bindings exists is done
				if (!bindings.isEmpty()) {
					// can in our case only be 'Category' or 'Products', so we can take the first
					EdmNavigationPropertyBinding binding = bindings.get(0);
					EdmElement property = segmentResponse.getEntitySet().getEntityType().getProperty(binding.getPath());
					// we don't need to handle error cases, as it is done in the Olingo library
					if (property instanceof EdmNavigationProperty) {
						edmNavigationProperty = (EdmNavigationProperty) property;
					}
				}
			}
			else {
				// can be 'Category' or 'Products', no path supported
				UriResource uriExpandResource = expandItem.getResourcePath().getUriResourceParts().get(0);
				// we don't need to handle error cases, as it is done in the Olingo library
				if (uriExpandResource instanceof UriResourceNavigation) {
					edmNavigationProperty = ((UriResourceNavigation) uriExpandResource).getProperty();
				}
			}

			// can be 'Category' or 'Products', no path supported
			// we don't need to handle error cases, as it is done in the Olingo library
			if (edmNavigationProperty != null) {
				EdmEntityType expandEdmEntityType = edmNavigationProperty.getType();
				String navPropName = edmNavigationProperty.getName();

				// build the inline data
				Link link = new Link();
				link.setTitle(navPropName);
				link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);
				link.setRel(Constants.NS_ASSOCIATION_LINK_REL + navPropName);

				if (edmNavigationProperty.isCollection()) { // in case of Categories(1)/$expand=Products
					// fetch the data for the $expand (to-many navigation) from backend
					// here we get the data for the expand
//					EntityCollection expandEntityCollection = component.getDataManager().getRelatedEntityCollection(responseEntity, expandEdmEntityType);
//					link.setInlineEntitySet(expandEntityCollection);
//					link.setHref(expandEntityCollection.getId().toASCIIString());
				}
				else { // in case of Products(1)?$expand=Category
					// fetch the data for the $expand (to-one navigation) from backend
					// here we get the data for the expand
//					Entity expandEntity = component.getDataManager().getRelatedEntity(responseEntity, expandEdmEntityType);
//					link.setInlineEntity(expandEntity);
//					link.setHref(expandEntity.getId().toASCIIString());
				}

				// set the link - containing the expanded data - to the current entity
				segmentResponse.getEntity().getNavigationLinks().add(link);
			}
		}
		
		EdmEntityType edmEntityType = segmentResponse.getEntitySet().getEntityType();

		String selectList = odata.createUriHelper().buildContextURLSelectList(edmEntityType, expandOption, selectOption);
		ContextURL contextUrl = ContextURL.with().entitySet(segmentResponse.getEntitySet()).selectList(selectList).suffix(Suffix.ENTITY).build();
		EntitySerializerOptions opts = EntitySerializerOptions.with()
				.contextURL(contextUrl)
				.select(selectOption)
				.expand(expandOption)
				.build();

		// 3. serialize
		ODataSerializer serializer = this.odata.createSerializer(responseFormat);
		SerializerResult serializerResult = serializer.entity(serviceMetadata, edmEntityType, segmentResponse.getEntity(), opts);

		// 4. configure the response object
		response.setContent(serializerResult.getContent());
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
	}

	private SegmentResponse manageSegments(List<UriResource> resourceParts, EdmEntitySet startEdmEntitySet) throws ODataApplicationException {
		EdmEntitySet entitySet = null;
		EdmEntityType entityType = null;
		Entity entity = null;
		
		List<EntityKey> keys = new ArrayList<>();

		if (resourceParts != null) {
			for (UriResource segment : resourceParts) {
				if (segment instanceof UriResourceEntitySet) {
					UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) segment;
					
					entitySet = startEdmEntitySet;
					entityType = entitySet.getEntityType();
					
					List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
					keys.add(new EntityKey(startEdmEntitySet.getEntityType(), keyPredicates));
				}
				else if (segment instanceof UriResourceNavigation) {
					UriResourceNavigation uriResourceNavigation = (UriResourceNavigation) segment;
					EdmNavigationProperty edmNavigationProperty = uriResourceNavigation.getProperty();
	
					entitySet = Utils.getNavigationTargetEntitySet(entitySet, edmNavigationProperty);
					entityType = entitySet.getEntityType();
					
					List<UriParameter> navKeyPredicates = uriResourceNavigation.getKeyPredicates();
					keys.add(new EntityKey(entityType, navKeyPredicates));
					
					if (navKeyPredicates.isEmpty()) {
						// e.g. DemoService.svc/Products(1)/Category
						throw new ODataApplicationException("This navigation is not supported", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
					}
				}
				else {
					throw new ODataApplicationException("Not supported", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
				}
			}
		}

		entity = component.getDataManager().readEntityData(entityType, keys);

		if (entitySet != null && entity != null) {
			return new SegmentResponse(entitySet, entity);
		}
		else {
			return null;
		}
	}

	@Override
	public void createEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat, ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
		throw new ODataApplicationException("Create entity is not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
	}

	@Override
	public void updateEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat, ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
		throw new ODataApplicationException("Update entity is not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
	}

	@Override
	public void deleteEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo) throws ODataApplicationException, ODataLibraryException {
		throw new ODataApplicationException("Delete entity is not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
	}
}