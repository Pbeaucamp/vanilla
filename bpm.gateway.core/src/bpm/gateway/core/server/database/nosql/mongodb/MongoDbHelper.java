package bpm.gateway.core.server.database.nosql.mongodb;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.inputs.MongoDbInputStream;

public class MongoDbHelper {
	private static String Host, login, yourDb;
	private static String password;
	private static int Port;
	private boolean pass;
	private static MongoDbConnection conn;

	public static boolean testConnection(MongoDbConnection connection, Boolean pass) throws Exception {

		Host = connection.getHost();
		Port = Integer.parseInt(connection.getPort());
		conn = connection;

		boolean verif = true;
		Mongo m = null;
		if (!conn.isPass()) {

			try {
				m = new Mongo(Host, Port);
			} catch (UnknownHostException e) {
				verif = false;
				e.printStackTrace();
			} catch (MongoException e) {
				verif = false;
				e.printStackTrace();
			}
		}
		else {
			password = connection.getPassword();
			login = connection.getLogin();
			yourDb = connection.getYourDb();

			try {
				m = new Mongo(Host, Port);
			} catch (UnknownHostException e) {
				verif = false;
				e.printStackTrace();
			} catch (MongoException e) {
				verif = false;
				e.printStackTrace();
			}
			DB db = m.getDB(yourDb);

			verif = db.authenticate(login, password.toCharArray());
		}
		m.close();
		return verif;
	}

	public static StreamDescriptor buildDescriptor(String tableName, String columnFamily, DataStream stream) throws Exception {

		if (stream == null || stream.getServer() == null || stream.getServer().getCurrentConnection(null) == null) {
			return new DefaultStreamDescriptor();
		}

		Mongo mongo = new Mongo(Host, Port);

		DB table = mongo.getDB(tableName);

		DBCollection collection = table.getCollection(columnFamily);
		DBCursor cursor = collection.find();

		/*
		 * we create the DataStreamDescriptor
		 */
		DefaultStreamDescriptor dsd = new DefaultStreamDescriptor();
		Exception thrown = null;
		try {

			Iterator<DBObject> it = cursor.iterator();
			boolean first = true;
			while (it.hasNext() && first) {

				DBObject d = it.next();

				Set<String> keyValues = d.keySet();

				// We add a column which will be representing the KEY column
				StreamElement key = new StreamElement();
				key.className = null;
				key.name = MongoDbInputStream.KEY_DEFINITION;
				key.tableName = tableName;
				key.transfoName = stream.getName();
				key.originTransfo = stream.getName();
				key.typeName = null;

				dsd.addColumn(key);

				for (String kv : keyValues) {
					StreamElement e = new StreamElement();
					e.className = null;
					e.name = kv;
					e.tableName = tableName;
					e.transfoName = stream.getName();
					e.typeName = null;

					dsd.addColumn(e);
				}

				first = false;
			}

			cursor.close();
			mongo.close();
		} catch (Exception e) {
			thrown = e;
		}

		if (thrown != null) {
			throw new ServerException("Error while getting DataStreamDescriptor for " + stream.getName(), thrown, null);
		}

		return dsd;
	}

	public static void createTable() {

	}

	public static List<List<Object>> getValues(MongoDbInputStream model, int i, String tablename) throws Exception, MongoException {
		Exception thrown = null;

		List<List<Object>> values = new ArrayList<List<Object>>();

		if (i == -1) {
			Mongo mongo = new Mongo(Host, Port);
			DB table = mongo.getDB(model.getTableName());
			DBCollection collection = table.getCollection(model.getDocumentFamily());
			DBCursor cursor = collection.find();
			Iterator<DBObject> it = cursor.iterator();

			while (it.hasNext()) {
				DBObject de = it.next();
				List<Object> row = new ArrayList<Object>();
				Set<String> list = de.keySet();
				for (String name : list) {
					if (name.contains("_id")) {
						row.add("Document id = " + de.get(name));
					}
					else {
						row.add("Title = " + name + "; Values = " + de.get(name));
					}
				}
				values.add(row);
			}
			cursor.close();
			mongo.close();
			return values;

		}
		else {

			HashMap<String, String> columnNames = new LinkedHashMap<String, String>();
			StreamDescriptor descriptor = model.getDescriptor(null);
			for (StreamElement el : descriptor.getStreamElements()) {
				if (!el.name.contains("KEY")) {
					columnNames.put(el.name, el.typeName);
				}

			}

			Mongo mongo = new Mongo(Host, Port);
			DB table = mongo.getDB(model.getTableName());
			DBCollection collection = table.getCollection(model.getDocumentFamily());
			DBCursor cursor = collection.find();

			Iterator<DBObject> it = cursor.iterator();

			int ia = 0;
			while (it.hasNext()) {
				DBObject de = it.next();
				List<Object> row = new ArrayList<Object>();
				Set<String> list = de.keySet();
				de.get("_id");
				row.clear();

				row.add(ia);
				for (String name : list) {
					row.add(de.get(name));
				}

				values.add(ia, row);
				ia++;
			}
			cursor.close();
			mongo.close();

			return values;
		}

	}

	public static void truncate(MongoDbConnection con, String tableName, String columnFamily) throws Exception {

		Mongo mongo = new Mongo(Host, Port);
		DB table = mongo.getDB(tableName);
		DBCollection collection = table.getCollection(columnFamily);
		DBCursor cursor = collection.find();
		while (cursor.hasNext()) {
			collection.remove(cursor.next());
		}
		cursor.close();
		mongo.close();

	}

}
