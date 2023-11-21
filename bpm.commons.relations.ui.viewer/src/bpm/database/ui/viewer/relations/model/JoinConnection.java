package bpm.database.ui.viewer.relations.model;



public class JoinConnection {

	
	private Node target;
	private Node source;
	private String cardinality;

	public JoinConnection(Node source, Node target, String cardinality) {
		this.target = target;
		this.source = source;
		this.cardinality=cardinality;
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

	public String getCardinality() {
		return cardinality;
	}

	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}

	public void reconnect() {
		target.addConnection(this);
		source.addConnection(this);

		
	}

	

}
