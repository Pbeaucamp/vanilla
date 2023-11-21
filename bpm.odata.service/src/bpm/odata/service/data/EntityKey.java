package bpm.odata.service.data;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.server.api.uri.UriParameter;

public class EntityKey {

	private EdmEntityType edmEntityType;
	private List<UriParameter> keyParams;
	
	public EntityKey(EdmEntityType edmEntityType, List<UriParameter> keyParams) {
		this.edmEntityType = edmEntityType;
		this.keyParams = keyParams;
	}
	
	public EdmEntityType getEdmEntityType() {
		return edmEntityType;
	}
	
	public List<UriParameter> getKeyParams() {
		return keyParams;
	}
}
