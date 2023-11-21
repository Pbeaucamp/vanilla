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
@Table (name = PatrimoineDAO.OUVRAGE_STP)
public class OuvrageStp {

	@Id
	@Column(name = "ouvragestp_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String ouvragestpOuvragesocleId;

	//TODO: Liste
	@Transient
	private String ouvragestpTypestationpompage;
	
	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragestp_ouvragesocle_id")
	public String getOuvragestpOuvragesocleId() {
		if (ouvragestpOuvragesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_STP + " - Champs ouvragestp_ouvragesocle_id - Valeur 'Null' non permise.");
		}
		return ouvragestpOuvragesocleId;
	}

	public void setOuvragestpOuvragesocleId(String ouvragestpOuvragesocleId) {
		this.ouvragestpOuvragesocleId = ouvragestpOuvragesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragestp_typestationpompage")
	public String getOuvragestpTypestationpompage() {
		if (ouvragestpTypestationpompage == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_STP + " - Champs ouvragestp_typestationpompage - Valeur 'Null' non permise.");
		}
		return ouvragestpTypestationpompage;
	}

	public void setOuvragestpTypestationpompage(String ouvragestpTypestationpompage) {
		this.ouvragestpTypestationpompage = ouvragestpTypestationpompage;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
}
