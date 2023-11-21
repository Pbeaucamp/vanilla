package bpm.gateway.core.veolia.patrimoine.xls;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import bpm.gateway.core.veolia.patrimoine.PatrimoineDAO;

@Entity
@Access(AccessType.FIELD)
@Table (name = PatrimoineDAO.OUVRAGE_CPT)
public class OuvrageCpt {

	@Id
	@Column(name = "ouvragecpt_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String ouvragecptOuvragesocleId;

	//TODO: Liste
	@Transient
	private String ouvragecptCodetype;

	//TODO: Liste
	@Transient
	private String ouvragecptFonctionnement;

	@Transient
	private String ouvragecptSituation;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecpt_ouvragesocle_id")
	public String getOuvragecptOuvragesocleId() {
		if (ouvragecptOuvragesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CPT + " - Champs ouvragecpt_ouvragesocle_id - Valeur 'Null' non permise.");
		}
		return ouvragecptOuvragesocleId;
	}

	public void setOuvragecptOuvragesocleId(String ouvragecptOuvragesocleId) {
		this.ouvragecptOuvragesocleId = ouvragecptOuvragesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecpt_codetype")
	public String getOuvragecptCodetype() {
		if (ouvragecptCodetype == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CPT + " - Champs ouvragecpt_codetype - Valeur 'Null' non permise.");
		}
		return ouvragecptCodetype;
	}

	public void setOuvragecptCodetype(String ouvragecptCodetype) {
		this.ouvragecptCodetype = ouvragecptCodetype;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecpt_fonctionnement")
	public String getOuvragecptFonctionnement() {
		if (ouvragecptFonctionnement == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CPT + " - Champs ouvragecpt_fonctionnement - Valeur 'Null' non permise.");
		}
		return ouvragecptFonctionnement;
	}

	public void setOuvragecptFonctionnement(String ouvragecptFonctionnement) {
		this.ouvragecptFonctionnement = ouvragecptFonctionnement;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecpt_situation")
	public String getOuvragecptSituation() {
		if (ouvragecptSituation == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CPT + " - Champs ouvragecpt_situation - Valeur 'Null' non permise.");
		}
		return ouvragecptSituation;
	}

	public void setOuvragecptSituation(String ouvragecptSituation) {
		this.ouvragecptSituation = ouvragecptSituation;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
}
