package bpm.gateway.core.server.database.nosql;


public interface ICassandraCQL {
	
	public String getCQLDefinition();
	
	public void setCQLDefinition(String cqlDefinition);
}
