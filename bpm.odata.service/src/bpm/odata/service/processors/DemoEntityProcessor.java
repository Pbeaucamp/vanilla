package bpm.odata.service.processors;

import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
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

import bpm.odata.service.data.Storage;
import bpm.odata.service.utils.Utils;

public class DemoEntityProcessor implements EntityProcessor {

	private OData odata;
	private ServiceMetadata serviceMetadata;
	private Storage storage;

	public DemoEntityProcessor(Storage storage) {
		this.storage = storage;
	}

	public void init(OData odata, ServiceMetadata serviceMetadata) {
		this.odata = odata;
		this.serviceMetadata = serviceMetadata;
	}

	@Override
	public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat) throws ODataApplicationException, SerializerException {
		EdmEntityType responseEdmEntityType = null;
		Entity responseEntity = null;
		EdmEntitySet responseEdmEntitySet = null;

		// 1st step: retrieve the requested Entity:
		// can be "normal" read operation, or navigation (to-one)
		List<UriResource> resourceParts = uriInfo.getUriResourceParts();
		int segmentCount = resourceParts.size();

		UriResource uriResource = resourceParts.get(0);
		if (!(uriResource instanceof UriResourceEntitySet)) {
			throw new ODataApplicationException("Only EntitySet is supported", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
		}

		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResource;
		EdmEntitySet startEdmEntitySet = uriResourceEntitySet.getEntitySet();

		// Analyze the URI segments
		if (segmentCount == 1) { // no navigation
			responseEdmEntityType = startEdmEntitySet.getEntityType();
			responseEdmEntitySet = startEdmEntitySet; // since we have only one
														// segment

			// 2. step: retrieve the data from backend
			List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
			responseEntity = storage.readEntityData(startEdmEntitySet, keyPredicates);
		}
		else if (segmentCount == 2) { // navigation
			UriResource navSegment = resourceParts.get(1);
			if (navSegment instanceof UriResourceNavigation) {
				UriResourceNavigation uriResourceNavigation = (UriResourceNavigation) navSegment;
				EdmNavigationProperty edmNavigationProperty = uriResourceNavigation.getProperty();
				responseEdmEntityType = edmNavigationProperty.getType();
				responseEdmEntitySet = Utils.getNavigationTargetEntitySet(startEdmEntitySet, edmNavigationProperty);

				// 2nd: fetch the data from backend.
				// for: Products(1)/Category we have to find the correct
				// Category entity
				List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
				// e.g. for Products(1)/Category we have to find first the
				// Products(1)
				Entity sourceEntity = storage.readEntityData(startEdmEntitySet, keyPredicates);

				// now we have to check if the navigation is
				// a) to-one: e.g. Products(1)/Category
				// b) to-many with key: e.g. Categories(3)/Products(5)
				List<UriParameter> navKeyPredicates = uriResourceNavigation.getKeyPredicates();

				if (navKeyPredicates.isEmpty()) {
					// e.g. DemoService.svc/Products(1)/Category
					responseEntity = storage.getRelatedEntity(sourceEntity, responseEdmEntityType);
				}
				else { // e.g. DemoService.svc/Categories(3)/Products(5)
					responseEntity = storage.getRelatedEntity(sourceEntity, responseEdmEntityType, navKeyPredicates);
				}
			}
		}
		else {
			// this would be the case for e.g.
			// Products(1)/Category/Products(1)/Category
			throw new ODataApplicationException("Not supported", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
		}

		if (responseEntity == null) {
			// this is the case for e.g. DemoService.svc/Categories(4) or
			// DemoService.svc/Categories(3)/Products(999)
			throw new ODataApplicationException("Nothing found.", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
		}

		SelectOption selectOption = uriInfo.getSelectOption();

		// handle $expand
		ExpandOption expandOption = uriInfo.getExpandOption();
		// in our example: http://localhost:8080/DemoService/DemoService.svc/Categories(1)/$expand=Products
		// or http://localhost:8080/DemoService/DemoService.svc/Products(1)?$expand=Category
		if (expandOption != null) {
			// retrieve the EdmNavigationProperty from the expand expression
			// Note: in our example, we have only one NavigationProperty, so we can directly access it
			EdmNavigationProperty edmNavigationProperty = null;
			ExpandItem expandItem = expandOption.getExpandItems().get(0);
			if (expandItem.isStar()) {
				List<EdmNavigationPropertyBinding> bindings = responseEdmEntitySet.getNavigationPropertyBindings();
				// we know that there are navigation bindings
				// however normally in this case a check if navigation bindings exists is done
				if (!bindings.isEmpty()) {
					// can in our case only be 'Category' or 'Products', so we can take the first
					EdmNavigationPropertyBinding binding = bindings.get(0);
					EdmElement property = responseEdmEntitySet.getEntityType().getProperty(binding.getPath());
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
					EntityCollection expandEntityCollection = storage.getRelatedEntityCollection(responseEntity, expandEdmEntityType);
					link.setInlineEntitySet(expandEntityCollection);
					link.setHref(expandEntityCollection.getId().toASCIIString());
				}
				else { // in case of Products(1)?$expand=Category
					// fetch the data for the $expand (to-one navigation) from backend
					// here we get the data for the expand
					Entity expandEntity = storage.getRelatedEntity(responseEntity, expandEdmEntityType);
					link.setInlineEntity(expandEntity);
					link.setHref(expandEntity.getId().toASCIIString());
				}

				// set the link - containing the expanded data - to the current entity
				responseEntity.getNavigationLinks().add(link);
			}
		}

		String selectList = odata.createUriHelper().buildContextURLSelectList(responseEdmEntityType, expandOption, selectOption);
		ContextURL contextUrl = ContextURL.with().entitySet(responseEdmEntitySet).selectList(selectList).suffix(Suffix.ENTITY).build();
		EntitySerializerOptions opts = EntitySerializerOptions.with().contextURL(contextUrl).select(selectOption).expand(expandOption).build();

		// 3. serialize
		// ContextURL contextUrl = ContextURL.with().entitySet(responseEdmEntitySet).suffix(Suffix.ENTITY).build();
		// EntitySerializerOptions opts = EntitySerializerOptions.with().contextURL(contextUrl).build();

		ODataSerializer serializer = this.odata.createSerializer(responseFormat);
		SerializerResult serializerResult = serializer.entity(serviceMetadata, responseEdmEntityType, responseEntity, opts);

		// 4. configure the response object
		response.setContent(serializerResult.getContent());
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
	}

	@Override
	public void createEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat, ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat, ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo) throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub

	}
}