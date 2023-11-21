package bpm.freemetrics.api.organisation.dashOrTheme;

public class DashboardRelation {
	
	private int id,child_Dash_Id,parent_Dash_Id;

	/**
	 * 
	 */
	public DashboardRelation() {
		super();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the child_Dash_Id
	 */
	public int getChild_Dash_Id() {
		return child_Dash_Id;
	}

	/**
	 * @param child_Dash_Id the child_Dash_Id to set
	 */
	public void setChild_Dash_Id(int child_Dash_Id) {
		this.child_Dash_Id = child_Dash_Id;
	}

	/**
	 * @return the parent_Dash_Id
	 */
	public int getParent_Dash_Id() {
		return parent_Dash_Id;
	}

	/**
	 * @param parent_Dash_Id the parent_Dash_Id to set
	 */
	public void setParent_Dash_Id(int parent_Dash_Id) {
		this.parent_Dash_Id = parent_Dash_Id;
	}
	
}
