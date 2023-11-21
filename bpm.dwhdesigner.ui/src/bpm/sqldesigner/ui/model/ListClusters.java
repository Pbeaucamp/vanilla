package bpm.sqldesigner.ui.model;

import java.util.HashMap;
import java.util.Iterator;

import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Node;

public class ListClusters extends Node {

	HashMap<String, DatabaseCluster> clusters = new HashMap<String, DatabaseCluster>();

	@Override
	public Object[] getChildren() {
		return clusters.values().toArray();
	}

	public HashMap<String, DatabaseCluster> getClusters() {
		return clusters;
	}

	public void addCluster(DatabaseCluster databaseCluster) {
		clusters.put(databaseCluster.getName(), databaseCluster);
	}

	public void removeCluster(DatabaseCluster databaseCluster) {
		clusters.remove(databaseCluster.getName());
	}

	public DatabaseCluster getCluster(String clusterName) {
		return clusters.get(clusterName);
	}

	public DatabaseCluster dbExists(String host, String port) {

		Iterator<String> it = clusters.keySet().iterator();
		boolean found = false;
		DatabaseCluster cluster = null;

		while (it.hasNext() && !found) {
			cluster = clusters.get(it.next());

			if (cluster.getDatabaseConnection() != null)
				found = cluster.getDatabaseConnection().getHost().equals(host)
						&& cluster.getDatabaseConnection().getPort().equals(
								port);

		}
		if (found)
			return cluster;
		else
			return null;
	}

	public DatabaseCluster mpExists(String name) {

		Iterator<String> it = clusters.keySet().iterator();
		boolean found = false;
		DatabaseCluster cluster = null;

		while (it.hasNext() && !found) {
			cluster = clusters.get(it.next());

			if (cluster.getDatabaseConnection() == null)
				found = cluster.getName().equals(name);

		}
		if (found)
			return cluster;
		else
			return null;
	}
}
