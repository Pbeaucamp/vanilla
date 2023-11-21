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
@Table (name = PatrimoineDAO.UNITE_REG)
public class UniteReg {

	@Id
	@Column(name = "unitereg_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String uniteregUnitesocleId;

	//TODO: Liste
	@Transient
	private String uniteregType;

	//TODO: Liste
	@Transient
	private String uniteregTelegestiondebit;

	//TODO: Liste
	@Transient
	private String uniteregTelegestionpressionamont;

	//TODO: Liste
	@Transient
	private String uniteregTelegestionpressionaval;

	@Transient
	private String uniteregOrganeassocie;

	@Transient
	private String uniteregNomsecteurregule;

	@Transient
	private String uniteregPlanassocie;

	@Column(name = "unitereg_commentaire1")
	private String uniteregCommentaire1;

	@Column(name = "unitereg_commentaire2")
	private String uniteregCommentaire2;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitereg_unitesocle_id")
	public String getUniteregUnitesocleId() {
		if (uniteregUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_REG + " - Champs unitereg_unitesocle_id - Valeur 'Null' non permise.");
		}
		return uniteregUnitesocleId;
	}

	public void setUniteregUnitesocleId(String uniteregUnitesocleId) {
		this.uniteregUnitesocleId = uniteregUnitesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitereg_type")
	public String getUniteregType() {
		if (uniteregType == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_REG + " - Champs unitereg_type - Valeur 'Null' non permise.");
		}
		return uniteregType;
	}

	public void setUniteregType(String uniteregType) {
		this.uniteregType = uniteregType;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitereg_telegestiondebit")
	public String getUniteregTelegestiondebit() {
		if (uniteregTelegestiondebit == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_REG + " - Champs unitereg_telegestiondebit - Valeur 'Null' non permise.");
		}
		return uniteregTelegestiondebit;
	}

	public void setUniteregTelegestiondebit(String uniteregTelegestiondebit) {
		this.uniteregTelegestiondebit = uniteregTelegestiondebit;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitereg_telegestionpressionamont")
	public String getUniteregTelegestionpressionamont() {
		if (uniteregTelegestionpressionamont == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_REG + " - Champs unitereg_telegestionpressionamont - Valeur 'Null' non permise.");
		}
		return uniteregTelegestionpressionamont;
	}

	public void setUniteregTelegestionpressionamont(String uniteregTelegestionpressionamont) {
		this.uniteregTelegestionpressionamont = uniteregTelegestionpressionamont;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitereg_telegestionpressionaval")
	public String getUniteregTelegestionpressionaval() {
		if (uniteregTelegestionpressionaval == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_REG + " - Champs unitereg_telegestionpressionaval - Valeur 'Null' non permise.");
		}
		return uniteregTelegestionpressionaval;
	}

	public void setUniteregTelegestionpressionaval(String uniteregTelegestionpressionaval) {
		this.uniteregTelegestionpressionaval = uniteregTelegestionpressionaval;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitereg_organeassocie")
	public String getUniteregOrganeassocie() {
		if (uniteregOrganeassocie == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_REG + " - Champs unitereg_organeassocie - Valeur 'Null' non permise.");
		}
		return uniteregOrganeassocie;
	}

	public void setUniteregOrganeassocie(String uniteregOrganeassocie) {
		this.uniteregOrganeassocie = uniteregOrganeassocie;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitereg_nomsecteurregule")
	public String getUniteregNomsecteurregule() {
		if (uniteregNomsecteurregule == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_REG + " - Champs unitereg_nomsecteurregule - Valeur 'Null' non permise.");
		}
		return uniteregNomsecteurregule;
	}

	public void setUniteregNomsecteurregule(String uniteregNomsecteurregule) {
		this.uniteregNomsecteurregule = uniteregNomsecteurregule;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitereg_planassocie")
	public String getUniteregPlanassocie() {
		if (uniteregPlanassocie == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_REG + " - Champs unitereg_planassocie - Valeur 'Null' non permise.");
		}
		return uniteregPlanassocie;
	}

	public void setUniteregPlanassocie(String uniteregPlanassocie) {
		this.uniteregPlanassocie = uniteregPlanassocie;
	}

	public String getUniteregCommentaire1() {
		return uniteregCommentaire1;
	}

	public void setUniteregCommentaire1(String uniteregCommentaire1) {
		this.uniteregCommentaire1 = uniteregCommentaire1;
	}

	public String getUniteregCommentaire2() {
		return uniteregCommentaire2;
	}

	public void setUniteregCommentaire2(String uniteregCommentaire2) {
		this.uniteregCommentaire2 = uniteregCommentaire2;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
	
	
}
