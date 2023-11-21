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
@Table (name = PatrimoineDAO.UNITE_SET)
public class UniteSet {

	@Id
	@Column(name = "uniteset_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String unitesetUnitesocleId;

	//TODO: Liste
	@Transient
	private String unitesetModesechage;

	@Transient
	private String unitesetDescriptif;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteset_unitesocle_id")
	public String getUnitesetUnitesocleId() {
		if (unitesetUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SET + " - Champs uniteset_unitesocle_id - Valeur 'Null' non permise.");
		}
		return unitesetUnitesocleId;
	}

	public void setUnitesetUnitesocleId(String unitesetUnitesocleId) {
		this.unitesetUnitesocleId = unitesetUnitesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteset_modesechage")
	public String getUnitesetModesechage() {
		if (unitesetModesechage == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SET + " - Champs uniteset_modesechage - Valeur 'Null' non permise.");
		}
		return unitesetModesechage;
	}

	public void setUnitesetModesechage(String unitesetModesechage) {
		this.unitesetModesechage = unitesetModesechage;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteset_descriptif")
	public String getUnitesetDescriptif() {
		if (unitesetDescriptif == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SET + " - Champs uniteset_descriptif - Valeur 'Null' non permise.");
		}
		return unitesetDescriptif;
	}

	public void setUnitesetDescriptif(String unitesetDescriptif) {
		this.unitesetDescriptif = unitesetDescriptif;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
	
	
}
