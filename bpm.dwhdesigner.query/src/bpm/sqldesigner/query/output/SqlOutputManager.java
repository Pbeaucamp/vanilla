package bpm.sqldesigner.query.output;

import org.eclipse.swt.widgets.Composite;

public class SqlOutputManager {
	private static SqlOutput INSTANCE = null;

	private SqlOutputManager() {
	}

	public synchronized static SqlOutput getInstance(Composite parent, int style) {
		if (INSTANCE == null)
			INSTANCE = new SqlOutput(parent, style);
		return INSTANCE;
	}
	
	public static SqlOutput getInstanceIfExists(){
		return INSTANCE;
	}
	
	public static void dispose(){
		INSTANCE = null;
	}

}
