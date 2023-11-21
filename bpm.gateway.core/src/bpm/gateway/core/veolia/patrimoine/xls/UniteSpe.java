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
@Table (name = PatrimoineDAO.UNITE_SPE)
public class UniteSpe {

	@Id
	@Column(name = "unitespe_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String unitespeUnitesocleId;

	@Transient
	private Double unitespeNbeqptproduction;

	@Transient
	private String unitespeTypeeqpt;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitespe_unitesocle_id")
	public String getUnitespeUnitesocleId() {
		if (unitespeUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SPE + " - Champs unitespe_unitesocle_id - Valeur 'Null' non permise.");
		}
		return unitespeUnitesocleId;
	}

	public void setUnitespeUnitesocleId(String unitespeUnitesocleId) {
		this.unitespeUnitesocleId = unitespeUnitesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitespe_nbeqptproduction")
	public Double getUnitespeNbeqptproduction() {
		if (unitespeNbeqptproduction == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SPE + " - Champs unitespe_nbeqptproduction - Valeur 'Null' non permise.");
		}
		return unitespeNbeqptproduction;
	}

	public void setUnitespeNbeqptproduction(Double unitespeNbeqptproduction) {
		this.unitespeNbeqptproduction = unitespeNbeqptproduction;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitespe_typeeqpt")
	public String getUnitespeTypeeqpt() {
		if (unitespeTypeeqpt == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SPE + " - Champs unitespe_typeeqpt - Valeur 'Null' non permise.");
		}
		return unitespeTypeeqpt;
	}

	public void setUnitespeTypeeqpt(String unitespeTypeeqpt) {
		this.unitespeTypeeqpt = unitespeTypeeqpt;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
	
	
}
