package bpm.vanilla.platform.core.beans;
/**
 * 
 * @author ely
 *
 */
public class ListeCommentaire {

	private int idCommentaire;
	private int idCommentableObject;
	private int idListe;
	
	public int getIdListe() {
		return idListe;
	}
	public void setIdListe(int idListe) {
		this.idListe = idListe;
	}
	public int getIdCommentaire() {
		return idCommentaire;
	}
	public void setIdCommentaire(int idCommentaire) {
		this.idCommentaire = idCommentaire;
	}
	public int getIdCommentableObject() {
		return idCommentableObject;
	}
	public void setIdCommentableObject(int idCommentableObject) {
		this.idCommentableObject = idCommentableObject;
	}

}
