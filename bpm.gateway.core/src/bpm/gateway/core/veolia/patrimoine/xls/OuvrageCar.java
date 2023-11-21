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
@Table (name = PatrimoineDAO.OUVRAGE_CAR)
public class OuvrageCar {

	@Id
	@Column(name = "ouvragecar_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String ouvragecarOuvragesocleId;

	@Transient
	private Double ouvragecarSurfaceplaneau;

	@Transient
	private Double ouvragecarCapaciteretenue;

	@Transient
	private Double ouvragecarSuperficiebv;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecar_ouvragesocle_id")
	public String getOuvragecarOuvragesocleId() {
		if (ouvragecarOuvragesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAR + " - Champs ouvragecar_ouvragesocle_id - Valeur 'Null' non permise.");
		}
		return ouvragecarOuvragesocleId;
	}

	public void setOuvragecarOuvragesocleId(String ouvragecarOuvragesocleId) {
		this.ouvragecarOuvragesocleId = ouvragecarOuvragesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecar_surfaceplaneau")
	public Double getOuvragecarSurfaceplaneau() {
		if (ouvragecarSurfaceplaneau == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAR + " - Champs ouvragecar_surfaceplaneau - Valeur 'Null' non permise.");
		}
		return ouvragecarSurfaceplaneau;
	}

	public void setOuvragecarSurfaceplaneau(Double ouvragecarSurfaceplaneau) {
		this.ouvragecarSurfaceplaneau = ouvragecarSurfaceplaneau;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecar_capaciteretenue")
	public Double getOuvragecarCapaciteretenue() {
		if (ouvragecarCapaciteretenue == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAR + " - Champs ouvragecar_capaciteretenue - Valeur 'Null' non permise.");
		}
		return ouvragecarCapaciteretenue;
	}

	public void setOuvragecarCapaciteretenue(Double ouvragecarCapaciteretenue) {
		this.ouvragecarCapaciteretenue = ouvragecarCapaciteretenue;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecar_superficiebv")
	public Double getOuvragecarSuperficiebv() {
		if (ouvragecarSuperficiebv == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAR + " - Champs ouvragecar_superficiebv - Valeur 'Null' non permise.");
		}
		return ouvragecarSuperficiebv;
	}

	public void setOuvragecarSuperficiebv(Double ouvragecarSuperficiebv) {
		this.ouvragecarSuperficiebv = ouvragecarSuperficiebv;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
}
