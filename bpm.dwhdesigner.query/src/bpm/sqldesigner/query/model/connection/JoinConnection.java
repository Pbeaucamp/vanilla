package bpm.sqldesigner.query.model.connection;

import bpm.sqldesigner.query.model.Node;

public class JoinConnection extends Node {

	public static final String RECONNECT = "JoinConnection Reconnect";
	private Node target;
	private Node source;

	public JoinConnection(Node source, Node target) {
		this.target = target;
		this.source = source;
	}

	public Node getTarget() {
		return target;
	}

	public Node getSource() {
		return source;
	}

	public void setTarget(Node target) {
		this.target = target;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	public void disconnect() {
		target.removeConnection(this);
		source.removeConnection(this);
	}

	public void reconnect() {
		target.addConnection(this);
		source.addConnection(this);

		getListeners().firePropertyChange(CONNECTIONS_PROP, null, null);

	}

	public void invert() {
		source.removeConnection(this);
		target.removeConnection(this);

		Node tampon = source;
		source = target;
		target = tampon;

		source.addConnection(this);
		target.addConnection(this);
	}

}
