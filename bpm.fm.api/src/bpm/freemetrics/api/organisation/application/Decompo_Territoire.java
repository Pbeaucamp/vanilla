package bpm.freemetrics.api.organisation.application;

public class Decompo_Territoire {
	
	private int id;
	
	private int parent_Assoc_Terr_TypDecTerr_ID;
	private int child_App_ID;
	
	
	/**
	 * 
	 */
	public Decompo_Territoire(){
		
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
	 * @return the child_App_ID
	 */
	public int getChild_App_ID() {
		return child_App_ID;
	}
	/**
	 * @param child_App_ID the child_App_ID to set
	 */
	public void setChild_App_ID(int child_App_ID) {
		this.child_App_ID = child_App_ID;
	}


	/**
	 * @return the parent_Assoc_Terr_TypDecTerr_ID
	 */
	public int getParent_Assoc_Terr_TypDecTerr_ID() {
		return parent_Assoc_Terr_TypDecTerr_ID;
	}


	/**
	 * @param parent_Assoc_Terr_TypDecTerr_ID the parent_Assoc_Terr_TypDecTerr_ID to set
	 */
	public void setParent_Assoc_Terr_TypDecTerr_ID(
			int parent_Assoc_Terr_TypDecTerr_ID) {
		this.parent_Assoc_Terr_TypDecTerr_ID = parent_Assoc_Terr_TypDecTerr_ID;
	}
	
}
