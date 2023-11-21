package bpm.odata.service.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;

import bpm.odata.service.FilterExpressionVisitor;
import bpm.odata.service.MetadataEdmProvider;
import bpm.odata.service.OpenDataComponent;
import bpm.odata.service.data.EntityKey;
import bpm.odata.service.data.SegmentResponse;
import bpm.odata.service.utils.Utils;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class MetadataEntityCollectionProcessor implements EntityCollectionProcessor {

	private OpenDataComponent component;
	private OData odata;
	private ServiceMetadata serviceMetadata;

	public MetadataEntityCollectionProcessor(OpenDataComponent component) {
		this.component = component;
	}

	private IVanillaLogger getLogger() {
		return component.getLogger();
	}

	@Override
	public void init(OData odata, ServiceMetadata serviceMetadata) {
		this.odata = odata;
		this.serviceMetadata = serviceMetadata;

		getLogger().info("Init MetadataEntityCollectionProcessor.");
	}

	@Override
	public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat) throws ODataApplicationException, SerializerException {
		// 1st retrieve the requested EntitySet from the uriInfo
		List<UriResource> resourceParts = uriInfo.getUriResourceParts();

		UriResource uriResource = resourceParts.get(0); // the first segment is the EntitySet
		if (!(uriResource instanceof UriResourceEntitySet)) {
			throw new ODataApplicationException("Only EntitySet is supported", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
		}

		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResource;
		EdmEntitySet startEdmEntitySet = uriResourceEntitySet.getEntitySet();
		
		SegmentResponse segmentResponse = manageSegments(resourceParts, startEdmEntitySet);

		EntityCollection returnEntityCollection = applyOptions(uriInfo, segmentResponse.getEntityCollection());

		// 3rd: apply system query options
		// Note: $select is handled by the lib, we only configure ContextURL + SerializerOptions
		// for performance reasons, it might be necessary to implement the $select manually
		SelectOption selectOption = uriInfo.getSelectOption();

		EdmEntityType edmEntityType = segmentResponse.getEntitySet().getEntityType();

		// 3rd: create and configure a serializer
		String selectList = odata.createUriHelper().buildContextURLSelectList(edmEntityType, null, selectOption);
		ContextURL contextUrl = ContextURL.with().entitySet(segmentResponse.getEntitySet()).selectList(selectList).build();
		final String id = request.getRawBaseUri() + "/" + segmentResponse.getEntitySet().getName();

		CountOption countOption = uriInfo.getCountOption();
		EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with().contextURL(contextUrl).id(id).count(countOption).select(selectOption).build();

		ODataSerializer serializer = odata.createSerializer(responseFormat);
		SerializerResult serializerResult = null;
		try {
			serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, returnEntityCollection, opts);
		} catch(Exception e) {
			e.printStackTrace();
		}

		// 4th: configure the response object: set the body, headers and status code
		response.setContent(serializerResult.getContent());
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
	}

	private SegmentResponse manageSegments(List<UriResource> resourceParts, EdmEntitySet startEdmEntitySet) throws ODataApplicationException {
		EdmEntitySet entitySet = null;
		EntityCollection entityCollection = null;
		
		List<EntityKey> keys = new ArrayList<>();

		if (resourceParts != null) {
			for (UriResource segment : resourceParts) {
				if (segment instanceof UriResourceEntitySet) {
					UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) segment;
					
					entitySet = startEdmEntitySet;
					
					List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
					keys.add(new EntityKey(startEdmEntitySet.getEntityType(), keyPredicates));
				}
				else if (segment instanceof UriResourceNavigation) {
					UriResourceNavigation uriResourceNavigation = (UriResourceNavigation) segment;
					EdmNavigationProperty edmNavigationProperty = uriResourceNavigation.getProperty();
					EdmEntityType targetEntityType = edmNavigationProperty.getType();
					entitySet = Utils.getNavigationTargetEntitySet(entitySet, edmNavigationProperty);
					
					List<UriParameter> navKeyPredicates = uriResourceNavigation.getKeyPredicates();
					keys.add(new EntityKey(targetEntityType, navKeyPredicates));
				}
				else {
					throw new ODataApplicationException("Not supported", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
				}
			}
		}

		entityCollection = component.getDataManager().readEntitySetData(entitySet, keys, null);

		if (entitySet != null && entityCollection != null) {
			return new SegmentResponse(entitySet, entityCollection);
		}
		else {
			return null;
		}
	}

	private EntityCollection applyOptions(UriInfo uriInfo, EntityCollection entityCollection) throws ODataApplicationException {
		List<Entity> entityList = entityCollection.getEntities();
		EntityCollection returnEntityCollection = new EntityCollection();

		// handle $skip
		SkipOption skipOption = uriInfo.getSkipOption();
		if (skipOption != null) {
			int skipNumber = skipOption.getValue();
			if (skipNumber >= 0) {
				if (skipNumber <= entityList.size()) {
					entityList = entityList.subList(skipNumber, entityList.size());
				}
				else {
					// The client skipped all entities
					entityList.clear();
				}
			}
			else {
				throw new ODataApplicationException("Invalid value for $skip", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
			}
		}

		// handle $top
		TopOption topOption = uriInfo.getTopOption();
		if (topOption != null) {
			int topNumber = topOption.getValue();
			if (topNumber >= 0) {
				if (topNumber <= entityList.size()) {
					entityList = entityList.subList(0, topNumber);
				} // else the client has requested more entities than available
					// => return what we have
			}
			else {
				throw new ODataApplicationException("Invalid value for $top", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
			}
		}

		// handle $count: return the original number of entities, ignore $top
		// and $skip
		CountOption countOption = uriInfo.getCountOption();
		if (countOption != null) {
			boolean isCount = countOption.getValue();
			if (isCount) {
				returnEntityCollection.setCount(entityList.size());
			}
		}
		FilterOption filterOption = uriInfo.getFilterOption();
		if (filterOption != null) {
			Expression filterExpression = filterOption.getExpression();
			try {
				Iterator<Entity> entityIterator = entityList.iterator();

				// Evaluate the expression for each entity
				// If the expression is evaluated to "true", keep the entity otherwise remove it from
				// the entityList
				while (entityIterator.hasNext()) {
					// To evaluate the the expression, create an instance of the Filter Expression
					// Visitor and pass the current entity to the constructor
					Entity currentEntity = entityIterator.next();
					FilterExpressionVisitor expressionVisitor = new FilterExpressionVisitor(currentEntity);

					// Evaluating the expression
					Object visitorResult = filterExpression.accept(expressionVisitor);
					// The result of the filter expression must be of type Edm.Boolean
					if (visitorResult instanceof Boolean) {
						if (!Boolean.TRUE.equals(visitorResult)) {
							// The expression evaluated to false (or null), so we have to remove the
							// currentEntity from entityList
							entityIterator.remove();
						}
					}
					else {
						throw new ODataApplicationException("A filter expression must evaulate to type Edm.Boolean", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
					}
				} // End while
			} catch (ExpressionVisitException e) {
				throw new ODataApplicationException("Exception in filter evaluation", HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
			}
		}

		// 3rd apply $orderby
		OrderByOption orderByOption = uriInfo.getOrderByOption();
		if (orderByOption != null) {
			List<OrderByItem> orderItemList = orderByOption.getOrders();
			final OrderByItem orderByItem = orderItemList.get(0); // we support only one
			Expression expression = orderByItem.getExpression();
			if (expression instanceof Member) {
				UriInfoResource resourcePath = ((Member) expression).getResourcePath();
				UriResource uriResource = resourcePath.getUriResourceParts().get(0);
				if (uriResource instanceof UriResourcePrimitiveProperty) {
					EdmProperty edmProperty = ((UriResourcePrimitiveProperty) uriResource).getProperty();
					final String sortPropertyName = edmProperty.getName();

					// do the sorting for the list of entities
					Collections.sort(entityList, new Comparator<Entity>() {

						// delegate the sorting to native sorter of Integer and String
						public int compare(Entity entity1, Entity entity2) {
							int compareResult = 0;

							if (sortPropertyName.equals(MetadataEdmProvider.ID)) {
								Integer integer1 = (Integer) entity1.getProperty(sortPropertyName).getValue();
								Integer integer2 = (Integer) entity2.getProperty(sortPropertyName).getValue();

								compareResult = integer1.compareTo(integer2);
							}
							else {
								String propertyValue1 = (String) entity1.getProperty(sortPropertyName).getValue();
								String propertyValue2 = (String) entity2.getProperty(sortPropertyName).getValue();

								compareResult = propertyValue1.compareTo(propertyValue2);
							}

							// if 'desc' is specified in the URI, change the order
							if (orderByItem.isDescending()) {
								return -compareResult; // just reverse order
							}

							return compareResult;
						}
					});
				}
			}
		}

		// after applying the query options, create EntityCollection based on
		// the reduced list
		for (Entity entity : entityList) {
			returnEntityCollection.getEntities().add(entity);
		}

		return returnEntityCollection;
	}
}
