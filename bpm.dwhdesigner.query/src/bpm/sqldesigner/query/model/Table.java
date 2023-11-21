package bpm.sqldesigner.query.model;


public class Table extends Node {

	public static final String CTE_VISIBLE = "Table cte visible";
	public static final String CTE_NOVISIBLE = "Table cte no visible";


	public void canBeCreated(boolean b) {
		if (b)
			getListeners().firePropertyChange(CTE_VISIBLE, null, null);
		else
			getListeners().firePropertyChange(CTE_NOVISIBLE, null, null);

	}

}
