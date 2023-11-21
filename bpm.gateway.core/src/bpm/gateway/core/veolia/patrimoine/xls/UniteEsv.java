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
@Table (name = PatrimoineDAO.UNITE_ESV)
public class UniteEsv {

	@Id
	@Column(name = "uniteesv_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String uniteesvUnitesocleId;

	@Transient
	private String uniteesvResponsabiliteentretien;
	
	@Column(name = "uniteesv_frequenceentretien")
	private String uniteesvFrequenceentretien;

	@Column(name = "uniteesv_dispositionsspecifiques1")
	private String uniteesvDispositionsspecifiques1;

	@Column(name = "uniteesv_dispositionsspecifiques2")
	private String uniteesvDispositionsspecifiques2;

	@Column(name = "uniteesv_commentaires1")
	private String uniteesvCommentaires1;

	@Column(name = "uniteesv_commentaires2")
	private String uniteesvCommentaires2;

	@Transient
	private Double uniteesvSuperficie;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteesv_unitesocle_id")
	public String getUniteesvUnitesocleId() {
		if (uniteesvUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ESV + " - Champs uniteesv_unitesocle_id - Valeur 'Null' non permise.");
		}
		return uniteesvUnitesocleId;
	}

	public void setUniteesvUnitesocleId(String uniteesvUnitesocleId) {
		this.uniteesvUnitesocleId = uniteesvUnitesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteesv_responsabiliteentretien")
	public String getUniteesvResponsabiliteentretien() {
		if (uniteesvResponsabiliteentretien == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ESV + " - Champs uniteesv_responsabiliteentretien - Valeur 'Null' non permise.");
		}
		return uniteesvResponsabiliteentretien;
	}

	public void setUniteesvResponsabiliteentretien(String uniteesvResponsabiliteentretien) {
		this.uniteesvResponsabiliteentretien = uniteesvResponsabiliteentretien;
	}

	public String getUniteesvFrequenceentretien() {
		return uniteesvFrequenceentretien;
	}

	public void setUniteesvFrequenceentretien(String uniteesvFrequenceentretien) {
		this.uniteesvFrequenceentretien = uniteesvFrequenceentretien;
	}

	public String getUniteesvDispositionsspecifiques1() {
		return uniteesvDispositionsspecifiques1;
	}

	public void setUniteesvDispositionsspecifiques1(String uniteesvDispositionsspecifiques1) {
		this.uniteesvDispositionsspecifiques1 = uniteesvDispositionsspecifiques1;
	}

	public String getUniteesvDispositionsspecifiques2() {
		return uniteesvDispositionsspecifiques2;
	}

	public void setUniteesvDispositionsspecifiques2(String uniteesvDispositionsspecifiques2) {
		this.uniteesvDispositionsspecifiques2 = uniteesvDispositionsspecifiques2;
	}

	public String getUniteesvCommentaires1() {
		return uniteesvCommentaires1;
	}

	public void setUniteesvCommentaires1(String uniteesvCommentaires1) {
		this.uniteesvCommentaires1 = uniteesvCommentaires1;
	}

	public String getUniteesvCommentaires2() {
		return uniteesvCommentaires2;
	}

	public void setUniteesvCommentaires2(String uniteesvCommentaires2) {
		this.uniteesvCommentaires2 = uniteesvCommentaires2;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteesv_superficie")
	public Double getUniteesvSuperficie() {
		if (uniteesvSuperficie == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ESV + " - Champs uniteesv_superficie - Valeur 'Null' non permise.");
		}
		return uniteesvSuperficie;
	}

	public void setUniteesvSuperficie(Double uniteesvSuperficie) {
		this.uniteesvSuperficie = uniteesvSuperficie;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
	
	
}
