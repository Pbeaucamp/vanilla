package bpm.sqldesigner.query.model.connection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bpm.sqldesigner.query.model.Column;

public class Connections {

	private List<JoinConnection> connections = new ArrayList<JoinConnection>();

	public void addConnection(JoinConnection s) {
		connections.add(s);
	}

	public void removeConnection(JoinConnection s) {
		connections.remove(s);
	}

	public List<JoinConnection> getConnections() {
		return connections;
	}

	public boolean contains(Column target, Column source) {
		Iterator<JoinConnection> it = connections.iterator();
		boolean found = false;
		
		while (it.hasNext() && !found) {
			JoinConnection j = it.next();
			if(j.getSource().equals(target) || j.getSource().equals(source)){
				found = j.getTarget().equals(target) || j.getTarget().equals(source);
			}
		}
		return found;
	}
}
