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
@Table (name = PatrimoineDAO.UNITE_CPT)
public class UniteCpt {

	@Id
	@Column(name = "unitecpt_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String unitecptUnitesocleId;

	//TODO: Liste
	@Transient
	private String unitecptTelegestiondebit;

	//TODO: Liste
	@Transient
	private String unitecptTelegestionpressionamont;

	//TODO: Liste
	@Transient
	private String unitecptTelegestionpressionaval;

	@Transient
	private String unitecptOrganeassocie;

	@Transient
	private String unitecptNomsecteurcompte;

	@Column(name = "unitecpt_commentaire1")
	private String unitecptCommentaire1;

	@Column(name = "unitecpt_commentaire2")
	private String unitecptCommentaire2;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecpt_unitesocle_id")
	public String getUnitecptUnitesocleId() {
		if (unitecptUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CPT + " - Champs unitecpt_unitesocle_id - Valeur 'Null' non permise.");
		}
		return unitecptUnitesocleId;
	}

	public void setUnitecptUnitesocleId(String unitecptUnitesocleId) {
		this.unitecptUnitesocleId = unitecptUnitesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecpt_telegestiondebit")
	public String getUnitecptTelegestiondebit() {
		if (unitecptTelegestiondebit == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CPT + " - Champs unitecpt_telegestiondebit - Valeur 'Null' non permise.");
		}
		return unitecptTelegestiondebit;
	}

	public void setUnitecptTelegestiondebit(String unitecptTelegestiondebit) {
		this.unitecptTelegestiondebit = unitecptTelegestiondebit;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecpt_telegestionpressionamont")
	public String getUnitecptTelegestionpressionamont() {
		if (unitecptTelegestionpressionamont == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CPT + " - Champs unitecpt_telegestionpressionamont - Valeur 'Null' non permise.");
		}
		return unitecptTelegestionpressionamont;
	}

	public void setUnitecptTelegestionpressionamont(String unitecptTelegestionpressionamont) {
		this.unitecptTelegestionpressionamont = unitecptTelegestionpressionamont;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecpt_telegestionpressionaval")
	public String getUnitecptTelegestionpressionaval() {
		if (unitecptTelegestionpressionaval == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CPT + " - Champs unitecpt_telegestionpressionaval - Valeur 'Null' non permise.");
		}
		return unitecptTelegestionpressionaval;
	}

	public void setUnitecptTelegestionpressionaval(String unitecptTelegestionpressionaval) {
		this.unitecptTelegestionpressionaval = unitecptTelegestionpressionaval;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecpt_organeassocie")
	public String getUnitecptOrganeassocie() {
		if (unitecptOrganeassocie == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CPT + " - Champs unitecpt_organeassocie - Valeur 'Null' non permise.");
		}
		return unitecptOrganeassocie;
	}

	public void setUnitecptOrganeassocie(String unitecptOrganeassocie) {
		this.unitecptOrganeassocie = unitecptOrganeassocie;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecpt_nomsecteurcompte")
	public String getUnitecptNomsecteurcompte() {
		if (unitecptNomsecteurcompte == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CPT + " - Champs unitecpt_nomsecteurcompte - Valeur 'Null' non permise.");
		}
		return unitecptNomsecteurcompte;
	}

	public void setUnitecptNomsecteurcompte(String unitecptNomsecteurcompte) {
		this.unitecptNomsecteurcompte = unitecptNomsecteurcompte;
	}

	public String getUnitecptCommentaire1() {
		return unitecptCommentaire1;
	}

	public void setUnitecptCommentaire1(String unitecptCommentaire1) {
		this.unitecptCommentaire1 = unitecptCommentaire1;
	}

	public String getUnitecptCommentaire2() {
		return unitecptCommentaire2;
	}

	public void setUnitecptCommentaire2(String unitecptCommentaire2) {
		this.unitecptCommentaire2 = unitecptCommentaire2;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
	
	
}
