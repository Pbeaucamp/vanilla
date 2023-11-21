package bpm.odata.service;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.EdmMetadataRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataServiceDocumentRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientServiceDocument;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

public class TestClient {

	private static final String ODATA_SERVICE = "http://services.odata.org/V4/Northwind/Northwind.svc";

	// private static final String ODATA_SERVICE = "http://localhost:9292/V4/ODataKpiService.svc/";

	public static void main(String[] args) {
		ODataClient client = ODataClientFactory.getClient();

		String serviceRoot = ODATA_SERVICE;
		ODataServiceDocumentRequest req = client.getRetrieveRequestFactory().getServiceDocumentRequest(serviceRoot);

		req.setAccept("application/json;odata.metadata=minimal");
		ODataRetrieveResponse<ClientServiceDocument> res = req.execute();

		ClientServiceDocument serviceDocument = res.getBody();

		Collection<String> entitySetNames = serviceDocument.getEntitySetNames();
		displayCollection("EntitySetNames", entitySetNames);

		Map<String, URI> entitySets = serviceDocument.getEntitySets();
		displayMap("EntitySets", entitySets);
		Map<String, URI> singletons = serviceDocument.getSingletons();
		displayMap("Singletons", singletons);
		Map<String, URI> functionImports = serviceDocument.getFunctionImports();
		displayMap("FunctionImports", functionImports);

		EdmMetadataRequest request = client.getRetrieveRequestFactory().getMetadataRequest(serviceRoot);
		ODataRetrieveResponse<Edm> response = request.execute();

		Edm edm = response.getBody();

		System.out.println("------------ Schema ---------------");
		List<EdmSchema> schemas = edm.getSchemas();
		for (EdmSchema schema : schemas) {
			String namespace = schema.getNamespace();
			System.out.println("Schema -> " + namespace + "\n");
			for (EdmComplexType complexType : schema.getComplexTypes()) {
				FullQualifiedName name = complexType.getFullQualifiedName();
				System.out.println("Complex Type -> " + name + "\n");
				displayEntity(edm, name);
			}
			for (EdmEntityType entityType : schema.getEntityTypes()) {
				FullQualifiedName name = entityType.getFullQualifiedName();
				System.out.println("Entity Type -> " + name + "\n");
				displayEntity(edm, name);
			}
		}
	}

	private static final void displayEntity(Edm edm, FullQualifiedName name) {
		System.out.println("		Properties\n");
		EdmEntityType customerType = edm.getEntityType(name);
		List<String> propertyNames = customerType.getPropertyNames();
		for (String propertyName : propertyNames) {
			EdmProperty property = customerType.getStructuralProperty(propertyName);
			FullQualifiedName typeName = property.getType().getFullQualifiedName();
			System.out.println("				" + propertyName + " -> Type : " + typeName + "\n");
		}
	}

	private static void displayCollection(String type, Collection<String> collection) {
		System.out.println("-------------  " + type + "  -----------------\n");
		if (collection != null) {
			for (String value : collection) {
				System.out.println(value);
			}
		}
		System.out.println("-------------  End of " + type + "  -----------------\n\n");
	}

	private static void displayMap(String type, Map<String, URI> map) {
		System.out.println("-------------  " + type + "  -----------------\n");
		if (map != null) {
			for (String key : map.keySet()) {
				System.out.println(key + "  -> " + map.get(key) + "\n");
			}
		}
		System.out.println("-------------  End of " + type + "  -----------------\n\n");
	}
}
