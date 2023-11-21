package bpm.metadata.jdbc.driver.remote;
import java.net.*;
import java.io.*;
import java.util.List;

import bpm.metadata.jdbc.driver.Connection;
import bpm.metadata.jdbc.driver.FmdtDatabaseMetadata;



public class RemoteFmdtJdbc implements Serializable{
	
	private Connection connection;
	private FmdtDatabaseMetadata metadata;
	
	public RemoteFmdtJdbc(Connection connection) {
		this.connection = connection;	
	}

	public Connection getConnection() {
		return connection;
	}
	
	public Object sendGetRequest(ActionJDBC action)
	{
		Object result = null;
		
		try{
			ObjectOutputStream oos = null;	

			HttpURLConnection httpConnection = (HttpURLConnection) new URL(this.connection.getUrl()).openConnection();
			httpConnection.setDoOutput(true);
			OutputStream os = httpConnection.getOutputStream();
			
			List<List<String>> resultQuery = null;
			
			serializeActionJDBC(action,os);
		
			int responseCode = httpConnection.getResponseCode();
			System.out.println("Response Code : " + responseCode);
			
			if(!(action.getType() == 1)){
				InputStream is = httpConnection.getInputStream();
				if (is != null){
					ObjectInputStream ois = new ObjectInputStream(is);
					Object o = ois.readObject();
					if (o instanceof FmdtDatabaseMetadata){
						result = (FmdtDatabaseMetadata) o;
					}else if (o instanceof List<?>) {
						result = o;
					}
				}
			}
		} catch(Exception e) {
	            System.err.println(e);
	            e.printStackTrace();
		}
		
		return result;
	}
	
	private static void serializeActionJDBC(ActionJDBC action, OutputStream outputStream) throws Exception{
		ObjectOutputStream oos = null;
		try{
			oos = new ObjectOutputStream(outputStream);
		}catch(Exception ex){
			throw new Exception("Unable to create an ObjectOutputStream " + ex.getMessage(), ex);
		}
		
		try {
			oos.writeObject(action);
			oos.flush();
		}finally{
			try{
				oos.close();
			}finally{
				outputStream.close();
			}
		}
	}
	
	private static void serializeRFJ(RemoteFmdtJdbc RFJ, OutputStream outputStream) throws Exception{
		ObjectOutputStream oos = null;
		try{
			oos = new ObjectOutputStream(outputStream);
		}catch(Exception ex){
			throw new Exception("Unable to create an ObjectOutputStream " + ex.getMessage(), ex);
			
		}
		
		try {
			oos.writeObject(RFJ);
			oos.flush();
		}finally{
			try{
				oos.close();
			}finally{
				outputStream.close();
			}
		}
	}

	public FmdtDatabaseMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(FmdtDatabaseMetadata metadata) {
		this.metadata = metadata;
	}
}