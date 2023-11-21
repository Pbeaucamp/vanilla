package bpm.vanilla.platform.core.beans.ged.constant;

/**
 * List des champ d'indexation définis par défaut
 * les champs system sont:
 * 		* PATH
 * 		* PUBLICATIONDATE
 * 		* PREVIOUSVERSION
 * 		* DOCID
 * 
 * les champs requis sont (en plus des champs system)
 * 		* AUTHOR
 * 		* TITLE
 * 		* VERSION
 * 		* CATEGORY
 * 		* GROUP
 * 
 * @author vanilla
 *
 */

public enum RuntimeFields {
	
	AUTHOR(1, "author"),
	TITLE(2, "title"),
	PATH(3, "path"),
	CONTENT(4, "content"),
	PUBLICATIONDATE(5, "publicationdate"),
	VERSION(6, "version"),
	PREVIOUSVERSION(7, "previousversion"),
	CATEGORY(8, "category"),
	DOCID(9, "docid"),
	SUMMARY(10, "summary"),
	TOKENS(11, "tokens"),
	KEYWORDS(12, "keywords"),
	WEIGHTS(12, "weights")
	;
	

	private int id;
	private final String name;
	
	private RuntimeFields(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public final String getName() {
		return name;
	}

	
	
}
