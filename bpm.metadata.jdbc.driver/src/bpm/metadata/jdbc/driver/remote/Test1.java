package bpm.metadata.jdbc.driver.remote;

import java.util.List;

import bpm.metadata.jdbc.driver.Connection;
import bpm.metadata.jdbc.driver.FmdtDatabaseMetadata;
import bpm.metadata.jdbc.driver.remote.QueryJDBC;
import bpm.metadata.jdbc.driver.remote.RemoteFmdtJdbc;

public class Test1 {

	public static void main(String[] args){
		try {
			QueryJDBC query = new QueryJDBC
			("select products.productcode, products.buyprice from products where products.buyprice in (49,75,69);");
			
			Connection con = new Connection("http://localhost:9292/vanilla/fmdtJdbcServlet", null);
			RemoteFmdtJdbc RFJ = new RemoteFmdtJdbc(con);
			ActionJDBC action = new ActionJDBC(1,"system","system","System",1,1,"Retail","Sales",query, null);
		
			RFJ.sendGetRequest(action);
		
			action.setType(2);
			FmdtDatabaseMetadata metadata = (FmdtDatabaseMetadata) RFJ.sendGetRequest(action);
			
			action.setType(3);
			action.setMetadata(metadata);
			
			List<List<String>> resultQuery = (List<List<String>>) RFJ.sendGetRequest(action);
			
			int size = 0;
			for(int i = 0; i<resultQuery.size(); i++) {
				size = size + resultQuery.get(i).size();
				for(int y = 0; y<resultQuery.get(i).size(); y++) {
					System.out.println(resultQuery.get(i).get(y));
				}
			}
			System.out.println("Size = " + resultQuery.size() + " Total size = " + size);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}