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
@Table (name = PatrimoineDAO.OUVRAGE_PRE)
public class OuvragePre {

	@Id
	@Column(name = "ouvragepre_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String ouvragepreOuvragesocleId;

	@Column(name = "ouvragepre_descriptif")
	private String ouvragepreDescriptif;

	@Transient
	private String ouvragepreNomcourseau;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepre_ouvragesocle_id")
	public String getOuvragepreOuvragesocleId() {
		if (ouvragepreOuvragesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PRE + " - Champs ouvragepre_ouvragesocle_id - Valeur 'Null' non permise.");
		}
		return ouvragepreOuvragesocleId;
	}

	public void setOuvragepreOuvragesocleId(String ouvragepreOuvragesocleId) {
		this.ouvragepreOuvragesocleId = ouvragepreOuvragesocleId;
	}

	public String getOuvragepreDescriptif() {
		return ouvragepreDescriptif;
	}

	public void setOuvragepreDescriptif(String ouvragepreDescriptif) {
		this.ouvragepreDescriptif = ouvragepreDescriptif;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepre_nomcourseau")
	public String getOuvragepreNomcourseau() {
		if (ouvragepreNomcourseau == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PRE + " - Champs ouvragepre_nomcourseau - Valeur 'Null' non permise.");
		}
		return ouvragepreNomcourseau;
	}

	public void setOuvragepreNomcourseau(String ouvragepreNomcourseau) {
		this.ouvragepreNomcourseau = ouvragepreNomcourseau;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
}
