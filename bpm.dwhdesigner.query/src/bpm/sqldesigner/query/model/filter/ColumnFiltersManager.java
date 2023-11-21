package bpm.sqldesigner.query.model.filter;

public class ColumnFiltersManager {
	private static ColumnFilters INSTANCE = null;

	private ColumnFiltersManager() {
	}

	public synchronized static ColumnFilters getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ColumnFilters();
		return INSTANCE;
	}

	public static void dispose() {
		INSTANCE = null;
	}

}
