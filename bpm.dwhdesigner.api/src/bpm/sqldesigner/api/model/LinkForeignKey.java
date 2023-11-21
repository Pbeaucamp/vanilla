package bpm.sqldesigner.api.model;

public class LinkForeignKey extends Node {

	public static final String RECONNECT = "JoinConnection Reconnect";
	public static final String CONNECTIONS_PROP = "connProp";
	private Column primaryKey;
	private Column foreignKey;

	public LinkForeignKey(Column source, Column target) {
		primaryKey = target;
		foreignKey = source;

//		System.out.println("primary key : " + target.getPath() + " - "
//				+ "foreign key : " + source.getPath());
	}

	public Column getTarget() {
		return primaryKey;
	}

	public Column getSource() {
		return foreignKey;
	}

	public void setTarget(Column target) {
		primaryKey = target;
	}

	public void setSource(Column source) {
		foreignKey = source;
	}

	public void disconnect() {
		primaryKey.removeForeignKey(this);
		foreignKey.setTargetPrimaryKey(null);

		getListeners().firePropertyChange(CONNECTIONS_PROP, null, null);

	}

	public void reconnect() {
		primaryKey.addSourceForeignKey(this);
		foreignKey.setTargetPrimaryKey(this);

		getListeners().firePropertyChange(CONNECTIONS_PROP, null, null);

	}

}
