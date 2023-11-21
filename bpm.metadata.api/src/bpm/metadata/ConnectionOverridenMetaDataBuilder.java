package bpm.metadata;

import java.util.List;

import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.physical.IConnection;
import bpm.vanilla.platform.core.IRepositoryApi;

public class ConnectionOverridenMetaDataBuilder extends MetaDataBuilder{
	
	/**
	 * This class is used to set the given Connection for the specified DataSource
	 * The connection will be stored in the model when saving only if saveConnectionInModel=true
	 * @author ludo
	 *
	 */
	public static class DataSourceConnectionOverrider{
		private IDataSource dataSource;
		private IConnection connection;
		private boolean saveConnectionInModel;
		
		
		
		/**
		 * @param dataSource
		 * @param connection
		 * @param saveConnectionInModel
		 */
		public DataSourceConnectionOverrider(IDataSource dataSource,
				IConnection connection, boolean saveConnectionInModel) {
			super();
			this.dataSource = dataSource;
			this.connection = connection;
			this.saveConnectionInModel = saveConnectionInModel;
		}
		/**
		 * @return the dataSource
		 */
		public IDataSource getDataSource() {
			return dataSource;
		}
		/**
		 * @return the connection
		 */
		public IConnection getConnection() {
			return connection;
		}
		/**
		 * @return the saveConnectionInModel
		 */
		public boolean isSaveConnectionInModel() {
			return saveConnectionInModel;
		}
		
		
	}
	
	private List<DataSourceConnectionOverrider> overriders;
	
	
	
	
	/**
	 * @param overriders
	 */
	public ConnectionOverridenMetaDataBuilder(List<DataSourceConnectionOverrider> overriders, IRepositoryApi sock) {
		super(sock);
		this.overriders = overriders;
	}




	/**
	 * 
	 * add the connections within the overriders then call  build(MetaData model, IRepositoryConnection sock, String groupName)
	 * 
	 * if an Exception is thrown, the overriden connection are removed
	 * 
	 * @param model
	 * @param sock
	 * @param groupName
	 * @param overriders
	 * @throws BuilderException
	 * @throws Exception
	 */
	public void build(MetaData model, IRepositoryApi sock, String groupName) throws BuilderException, Exception{
		
		
		for(IDataSource ds : model.getDataSources()){
			
			for(DataSourceConnectionOverrider o : overriders){
				if (o.getDataSource().getName().equals(ds.getName())){
					ds.addAlternateConnection(o.getConnection(), o.isSaveConnectionInModel());
					ds.setDefaultConnection(o.getConnection());
				}
			}
		}
		
		try{
			super.build(model, sock, groupName);
		}catch(Exception ex){
			
			for(IDataSource ds : model.getDataSources()){
				
				for(DataSourceConnectionOverrider o : overriders){
					if (o == ds){
						ds.removeConnection(o.getConnection());
					}
				}
			}
			
			throw ex;
		}
		
	}
}
