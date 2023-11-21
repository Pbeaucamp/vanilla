package bpm.sqldesigner.query.model.connection;

public class ConnectionsManager {
	private static Connections INSTANCE = null;

	private ConnectionsManager() {
	}

	public synchronized static Connections getInstance() {
		if (INSTANCE == null)
			INSTANCE = new Connections();
		return INSTANCE;
	}

	public static void dispose() {
		INSTANCE = null;	
	}

}
