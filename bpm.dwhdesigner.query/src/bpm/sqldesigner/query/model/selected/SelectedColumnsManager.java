package bpm.sqldesigner.query.model.selected;

public class SelectedColumnsManager {
	private static SelectedColumns INSTANCE = null;

	private SelectedColumnsManager() {
	}

	public synchronized static SelectedColumns getInstance() {
		if (INSTANCE == null)
			INSTANCE = new SelectedColumns();
		return INSTANCE;
	}

	public static void dispose() {
		INSTANCE = null;
	}

}
