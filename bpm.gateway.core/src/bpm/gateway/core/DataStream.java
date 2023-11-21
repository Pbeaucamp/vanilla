package bpm.gateway.core;



/**
 * A useful interface to manipulate object thar need Server and a deifntion
 * like DataBase stream
 * @author LCA
 *
 */
public interface DataStream extends Transformation{


	public DocumentGateway getDocument();
	/**
	 * 
	 * @return the InputStream Server where he comes from
	 */
	public Server getServer();
	
	/**
	 * set the server owning the datastream
	 * @param s
	 */
	public void setServer(Server s);
	
	
	/**
	 * set the definition of the stream
	 */
	public void setDefinition(String definition);
	
	/**
	 * 
	 * @return the dataStream defintion
	 */
	public String getDefinition();
	
	
	
	/**
	 * @return the DataStream
	 */
	public String getName();
	
	
	
}
