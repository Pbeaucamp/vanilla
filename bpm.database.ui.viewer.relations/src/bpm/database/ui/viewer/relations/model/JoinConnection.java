package bpm.database.ui.viewer.relations.model;



public class JoinConnection {

	
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

	

	public void reconnect() {
		target.addConnection(this);
		source.addConnection(this);

		
	}

	

}
