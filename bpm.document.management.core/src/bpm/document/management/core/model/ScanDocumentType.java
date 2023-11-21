package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScanDocumentType implements Serializable

{

	private static final long serialVersionUID = 1L;

	private int id;
	
	private String name;
	
	/*Link*/
	private Type type;
	private int idLink;
	
	
	private int nbPages;
	
	private int idOcrForm;
	private int idWorkflow;
	
	private String referent;
	private int idReferent;
	
	private Destination typeDest;
	private String destination;
//	private int idDestinationDir;
	
	private Intercalaire typeIntercalaire;
	private String codeBarre;
	
	private List<ScanKeyWord> keyWords;
	
	private boolean isChorusType = false;
	
	private String idFournisseur;
	
	private boolean createFolderAuto = false;
	private String folderNamePattern;
	private int idFolderParent;
	private String patterns;
	
	public ScanDocumentType() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getTypeType() {
		return type;
	}
	
	public int getType() {
		if(type == null) {
			return -1;
		}
		return type.getType();
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public void setType(int type) {
		this.type = Type.valueOf(type);
	}

	public int getIdLink() {
		return idLink;
	}

	public void setIdLink(int idLink) {
		this.idLink = idLink;
	}

	public int getNbPages() {
		return nbPages;
	}

	public void setNbPages(int nbPages) {
		this.nbPages = nbPages;
	}

	public int getIdOcrForm() {
		return idOcrForm;
	}

	public void setIdOcrForm(int idOcrForm) {
		this.idOcrForm = idOcrForm;
	}

	public int getIdWorkflow() {
		return idWorkflow;
	}

	public void setIdWorkflow(int idWorkflow) {
		this.idWorkflow = idWorkflow;
	}

	public String getReferent() {
		return referent;
	}

	public void setReferent(String referent) {
		this.referent = referent;
	}

	public int getIdReferent() {
		return idReferent;
	}

	public void setIdReferent(int idReferent) {
		this.idReferent = idReferent;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

//	public int getIdDestinationDir() {
//		return idDestinationDir;
//	}
//
//	public void setIdDestinationDir(int idDestinationDir) {
//		this.idDestinationDir = idDestinationDir;
//	}

	public Intercalaire getTypeIntercalaireType() {
		return typeIntercalaire;
	}
	
	public int getTypeIntercalaire() {
		if(typeIntercalaire == null) {
			return -1;
		}
		return typeIntercalaire.getIntercalaire();
	}

	public void setTypeIntercalaire(Intercalaire typeIntercalaire) {
		this.typeIntercalaire = typeIntercalaire;
	}
	
	public void setTypeIntercalaire(int typeIntercalaire) {
		this.typeIntercalaire = Intercalaire.valueOf(typeIntercalaire);
	}

	public String getCodeBarre() {
		return codeBarre;
	}

	public void setCodeBarre(String codeBarre) {
		this.codeBarre = codeBarre;
	}

	public Destination getTypeDestType() {
		return typeDest;
	}
	
	public int getTypeDest() {
		if(typeDest == null) {
			return -1;
		}
		return typeDest.getDestination();
	}

	public void setTypeDest(Destination typeDest) {
		this.typeDest = typeDest;
	}
	
	public void setTypeDest(int typeDest) {
		this.typeDest = Destination.valueOf(typeDest);
	}
	
	public List<ScanKeyWord> getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(List<ScanKeyWord> keyWords) {
		this.keyWords = keyWords;
	}



	public enum Type {
		FREE(0), 
		AKLABOX(1);

		private int type;

		private static Map<Integer, Type> map = new HashMap<Integer, Type>();
		static {
	        for (Type type : Type.values()) {
	            map.put(type.getType(), type);
	        }
	    }
		
		private Type(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
		
		public static Type valueOf(int type) {
	        return map.get(type);
	    }
	}
	
	public enum Destination {
		AKLABOX(1), 
		FTP(2),
		MAIL(3);

		private int dest;

		private static Map<Integer, Destination> map = new HashMap<Integer, Destination>();
		static {
	        for (Destination dest : Destination.values()) {
	            map.put(dest.getDestination(), dest);
	        }
	    }
		
		private Destination(int dest) {
			this.dest = dest;
		}
		
		public int getDestination() {
			return dest;
		}
		
		public static Destination valueOf(int dest) {
	        return map.get(dest);
	    }
	}
	
	public enum Intercalaire {
		PAGE_BLANCHE(1), 
		TITRE(2),
		CODE2D(3),
		CODE3D(4);

		private int page;

		private static Map<Integer, Intercalaire> map = new HashMap<Integer, Intercalaire>();
		static {
	        for (Intercalaire page : Intercalaire.values()) {
	            map.put(page.getIntercalaire(), page);
	        }
	    }
		
		private Intercalaire(int page) {
			this.page = page;
		}
		
		public int getIntercalaire() {
			return page;
		}
		
		public static Intercalaire valueOf(int page) {
	        return map.get(page);
	    }
	}

	public boolean isChorusType() {
		return isChorusType;
	}

	public void setChorusType(boolean isChorusType) {
		this.isChorusType = isChorusType;
	}

	public String getIdFournisseur() {
		return idFournisseur;
	}

	public void setIdFournisseur(String idFournisseur) {
		this.idFournisseur = idFournisseur;
	}
	
	public boolean isIsChorusType() {
		return isChorusType;
	}
	
	public void setIsChorusType(boolean isChorusType) {
		this.isChorusType = isChorusType;
	}

	public boolean isCreateFolderAuto() {
		return createFolderAuto;
	}

	public void setCreateFolderAuto(boolean createFolderAuto) {
		this.createFolderAuto = createFolderAuto;
	}

	public String getFolderNamePattern() {
		return folderNamePattern;
	}

	public void setFolderNamePattern(String folderNamePattern) {
		this.folderNamePattern = folderNamePattern;
	}

	public int getIdFolderParent() {
		return idFolderParent;
	}

	public void setIdFolderParent(int idFolderParent) {
		this.idFolderParent = idFolderParent;
	}

	public List<String> getPatternsAsList() {
		if(patterns == null) return new ArrayList<String>();
		return new ArrayList<String>(Arrays.asList(patterns.split("_\\$!\\$_")));
	}
	
	public String getPatterns() {
		return patterns;
	}

	public void setPatterns(String patterns) {
		this.patterns = patterns;
	}
	
	public void setPatterns(List<String> patterns) {
		if(patterns == null || patterns.isEmpty()){
			this.patterns = null;
		} else {
			this.patterns = "";
			for(String p : patterns){
				this.patterns += p + "_$!$_";
			}
			this.patterns = this.patterns.substring(0, this.patterns.length()-5);
		}
	}
}
