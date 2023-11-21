package bpm.document.management.core.model;

import java.util.Date;
import java.util.List;

import bpm.document.management.core.model.aklademat.Classification;

public class Deed implements IAdminDematObject {

	public static final String DEFAULT_FORM_NAME = "DefaultFormActe";
	
	public static final String P_DEED_NATURE = "acte_nature";
	public static final String P_DEED_NUMBER = "numero_de_lacte";
	public static final String P_DATE = "date_de_lacte";
	public static final String P_OBJECT = "objet";
	public static final String P_FILE_ARRETE = "arrete";
	public static final String P_CLASSIFICATION = "classification";
	
	public static final String[] DEED_PARAMS = { P_DEED_NATURE, P_DEED_NUMBER, P_DATE, P_OBJECT, P_CLASSIFICATION };
	
	public static final String REGEX_NUMBER = "#^[0-9A-Z_]{1,15}$#";

	private static final long serialVersionUID = 1L;

	public enum Nature {
		DELIBERATION("Délibérations", 1), 
		ARRETE_REGULATION("Arrêtés réglementaires", 2), 
		ARRETE_INDIVIDUAL("Arrêtés Individuels", 3), 
		CONTRACT_AND_CONVENTION("Contrats et conventions", 4),
		DOCUMENT_BUDGET_AND_FINANCE("Documents budgétaires et financiers", 5),
		AUTRES("Autres", 6);
		
		private String label;
		private int index;
		
		private Nature(String label, int index) {
			this.label = label;
			this.index = index;
		}
		
		public String getLabel() {
			return label;
		}
		
		public int getIndex() {
			return index;
		}
	}

	private String pastellId;
	private Nature nature;
	private String number;
	private String object;

	private Date date;
	
	private Classification classification;

	/* Transcient: Don't forget to set them to null before saving the object in xstream*/
	private AkladematAdminEntity<Deed> parent;
	private Documents linkFile;
	private List<Documents> annexes;

	public Deed() {
		super();
	}

	public Deed(Nature nature, String number, String object, Date date) {
		super();
		this.nature = nature;
		this.number = number;
		this.object = object;
		this.date = date;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void setParent(AkladematAdminEntity<? extends IAdminDematObject> parent) {
		this.parent = (AkladematAdminEntity<Deed>) parent;
	}
	
	public AkladematAdminEntity<Deed> getParent() {
		return parent;
	}

	public String getPastellId() {
		return pastellId;
	}

	public void setPastellId(String pastellId) {
		this.pastellId = pastellId;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public Classification getClassification() {
		return classification;
	}
	
	public void setClassification(Classification classification) {
		this.classification = classification;
	}

	public Documents getLinkFile() {
		return linkFile;
	}

	public void setLinkFile(Documents linkFile) {
		this.linkFile = linkFile;
	}

	public List<Documents> getAnnexes() {
		return annexes;
	}

	public void setAnnexes(List<Documents> annexes) {
		this.annexes = annexes;
	}

	public Nature getNature() {
		return nature;
	}

	public void setNature(Nature nature) {
		this.nature = nature;
	}
}
