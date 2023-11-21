package bpm.nosql.oda.runtime.cassandraUtility;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.model.AllOneConsistencyLevelPolicy;
import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.cassandra.model.CqlRows;
import me.prettyprint.cassandra.serializers.ByteBufferSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.FailoverPolicy;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.ddl.ColumnDefinition;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;

import org.apache.cassandra.thrift.AuthenticationException;
import org.apache.cassandra.thrift.AuthorizationException;
import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.CfDef;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnDef;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.KeySlice;
import org.apache.cassandra.thrift.KsDef;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class CassandraInterface {

	  private TTransport transport;
	  private TProtocol protocol;
	  private Cassandra.Client client;
	  private ConsistencyLevel consistencyLevel;
	  private String host;
	  private String username;
	  private String password;
	  private int port;
	  private List<KeySlice> ls;
	  private String keyspace;
	  private Map<String, String> credentials;
	private ByteBuffer val;
	  public static final String UTF8 = "UTF8";
	  public static Charset charset = Charset.forName("UTF-8");
	  public static CharsetEncoder encoder = charset.newEncoder();
	  public static CharsetDecoder decoder = charset.newDecoder();
	  public static KsDef ksdef;
	  
	  public CassandraInterface(String host, int port, String keyspace, String username, String password) throws InvalidRequestException, TException, AuthenticationException, AuthorizationException
	  {
	    this.host = host;
	    this.port = port;
	    this.keyspace = keyspace;
	    this.transport = new TFramedTransport(new TSocket(host, port));
	    
	    this.protocol = new TBinaryProtocol(this.transport);
	    this.client = new Cassandra.Client(this.protocol);
	    this.transport.open();
	    if(ls != null){
	    	ls.clear();
	    }
	    this.client.set_keyspace(keyspace);

	    this.consistencyLevel = ConsistencyLevel.ONE;
	  }

	  public void initCassandra() throws InvalidRequestException, TException {
	    this.transport = new TFramedTransport(new TSocket(this.host, this.port));
	    this.protocol = new TBinaryProtocol(this.transport);
	    this.client = new Cassandra.Client(this.protocol);
	    this.transport.open();
	    this.client.set_keyspace(this.keyspace);

	    this.consistencyLevel = ConsistencyLevel.ONE;
	  }

	  public List<CassandraRow> fetchRows(String columnFamil)
	    throws InvalidRequestException, UnavailableException, TimedOutException, TException
	  {
	    List<CassandraRow> cRowList = new ArrayList<CassandraRow>();
		
		Cluster myCluster = HFactory.getOrCreateCluster("Test_Cluster_" + new Object().hashCode(), 
				this.host + ":" + this.port);
		Keyspace ksp = null;
		if(this.username != null && this.username .isEmpty() && this.password != null && !this.password.isEmpty()){
			HashMap<String, String> accessMap = new HashMap<String, String>();
			accessMap.put("username", this.username);
			accessMap.put("password", this.password);

			ksp = HFactory.createKeyspace(this.keyspace, myCluster, new AllOneConsistencyLevelPolicy(),
					FailoverPolicy.ON_FAIL_TRY_ALL_AVAILABLE, accessMap);
		}
		else {
			ksp = HFactory.createKeyspace(this.keyspace, myCluster);
		}
		CqlQuery<String, String, ByteBuffer> cqlQuery = new CqlQuery<String, String, ByteBuffer>(
				ksp, new StringSerializer(), new StringSerializer(), new ByteBufferSerializer());
		cqlQuery.setQuery("select * from "+this.keyspace+"."+columnFamil);
		
		QueryResult<CqlRows<String, String, ByteBuffer>> result = cqlQuery.execute();
		
		for(Row<String, String, ByteBuffer> rs : result.get()){
			CassandraRow cRow = new CassandraRow();
			Map<String, String> nameValueList = new HashMap<String, String>();
			cRow.setKey(rs.getKey());
				for(HColumn<String, ByteBuffer> slice : rs.getColumnSlice().getColumns()){
					if(bb_to_str(slice.getValueBytes()) == ""){
						nameValueList.put(bb_to_str(slice.getNameBytes()), 
								String.valueOf(slice.getValue().getLong()));
					}else{
						nameValueList.put(bb_to_str(slice.getNameBytes()), 
								bb_to_str(slice.getValueBytes()));	
					}

				}
				cRow.setColumns(nameValueList);
				cRowList.add(cRow);
		}
		

	    return cRowList;
	  }

	  public static String bb_to_str(java.nio.ByteBuffer buffer) {
	    String data = "";
	    try {
	      int old_position = buffer.position();
	      data = decoder.decode(buffer).toString();
	      buffer.position(old_position);
	    } catch (Exception e) {
	      e.printStackTrace();
	      return "";
	    }
	    return data;
	  }

}
