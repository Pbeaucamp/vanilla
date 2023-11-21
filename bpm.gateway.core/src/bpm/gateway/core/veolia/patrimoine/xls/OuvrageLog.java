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
@Table (name = PatrimoineDAO.OUVRAGE_LOG)
public class OuvrageLog {

	@Id
	@Column(name = "ouvragelog_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String ouvragelogOuvragesocleId;

	//TODO: Liste
	@Transient
	private String ouvragelogFonction;

	@Transient
	private String ouvragelogTypefondation;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragelog_ouvragesocle_id")
	public String getOuvragelogOuvragesocleId() {
		if (ouvragelogOuvragesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_LOG + " - Champs ouvragelog_ouvragesocle_id - Valeur 'Null' non permise.");
		}
		return ouvragelogOuvragesocleId;
	}

	public void setOuvragelogOuvragesocleId(String ouvragelogOuvragesocleId) {
		this.ouvragelogOuvragesocleId = ouvragelogOuvragesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragelog_fonction")
	public String getOuvragelogFonction() {
		if (ouvragelogFonction == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_LOG + " - Champs ouvragelog_fonction - Valeur 'Null' non permise.");
		}
		return ouvragelogFonction;
	}

	public void setOuvragelogFonction(String ouvragelogFonction) {
		this.ouvragelogFonction = ouvragelogFonction;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragelog_typefondation")
	public String getOuvragelogTypefondation() {
		if (ouvragelogTypefondation == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_LOG + " - Champs ouvragelog_typefondation - Valeur 'Null' non permise.");
		}
		return ouvragelogTypefondation;
	}

	public void setOuvragelogTypefondation(String ouvragelogTypefondation) {
		this.ouvragelogTypefondation = ouvragelogTypefondation;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
}
