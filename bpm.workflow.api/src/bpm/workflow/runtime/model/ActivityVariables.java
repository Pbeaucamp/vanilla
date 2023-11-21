package bpm.workflow.runtime.model;

/**
 * The abstract class of the variables
 * @author CHARBONNIER, MARTIN
 *
 */
public class ActivityVariables {


	public String NomInterne;
	public int Type;

	/**
	 * 
	 * @return the internal name of the variable
	 */
	public String getNomInterne() {
		return NomInterne;
	}
	/**
	 * Set the internal name of the variable
	 * @param nomInterne
	 */
	public void setNomInterne(String nomInterne) {
		NomInterne = nomInterne;
	}
	
	/**
	 * 
	 * @return the type of the variable (0 : String, ...)
	 * et les autres types???!!!???
	 */
	public int getType() {
		return Type;
	}
	/**
	 * Set the type of the variable
	 * @param i : type
	 */
	public void setType(int i) {
		Type = i;
	}
	
}
