package bpm.gateway.core.veolia.patrimoine.xls;

import java.util.Date;

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
@Table (name = PatrimoineDAO.UNITE_SOCLE)
public class UniteSocle {

	@Id
	@Column(name = "unitesocle_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String unitesocleVeId;

	@Transient
	private String unitesocleOuvragesocleId;

	@Transient
	private String unitesocleDenomination;

	//TODO: Liste
	@Transient
	private String unitesocleType;

	@Transient
	private String unitesocleCaracteristique;

	@Transient
	private String unitesocleConsignes;

	@Column(name = "unitesocle_commentaire")
	private String unitesocleCommentaire;

	//TODO: Liste
	@Transient
	private String unitesocleStatut;

	@Transient
	private Date unitesocleMiseenservice;

	@Column(name = "unitesocle_datefermeture")
	private Date unitesocleDatefermeture;
	
	//TODO: Liste
	@Transient
	private String unitesocleEtat;

	@Column(name = "unitesocle_daterehabilitation1")
	private Date unitesocleDaterehabilitation1;

	@Column(name = "unitesocle_contenurehabilitation1")
	private String unitesocleContenurehabilitation1;

	@Column(name = "unitesocle_daterehabilitation2")
	private Date unitesocleDaterehabilitation2;

	@Column(name = "unitesocle_contenurehabilitation2")
	private String unitesocleContenurehabilitation2;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitesocle_ve_id")
	public String getUnitesocleVeId() {
		if (unitesocleVeId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SOCLE + " - Champs unitesocle_ve_id - Valeur 'Null' non permise.");
		}
		return unitesocleVeId;
	}

	public void setUnitesocleVeId(String unitesocleVeId) {
		this.unitesocleVeId = unitesocleVeId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitesocle_ouvragesocle_id")
	public String getUnitesocleOuvragesocleId() {
		if (unitesocleOuvragesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SOCLE + " - Champs unitesocle_ouvragesocle_id - Valeur 'Null' non permise.");
		}
		return unitesocleOuvragesocleId;
	}

	public void setUnitesocleOuvragesocleId(String unitesocleOuvragesocleId) {
		this.unitesocleOuvragesocleId = unitesocleOuvragesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitesocle_denomination")
	public String getUnitesocleDenomination() {
		if (unitesocleDenomination == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SOCLE + " - Champs unitesocle_denomination - Valeur 'Null' non permise.");
		}
		return unitesocleDenomination;
	}

	public void setUnitesocleDenomination(String unitesocleDenomination) {
		this.unitesocleDenomination = unitesocleDenomination;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitesocle_type")
	public String getUnitesocleType() {
		if (unitesocleType == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SOCLE + " - Champs unitesocle_type - Valeur 'Null' non permise.");
		}
		return unitesocleType;
	}

	public void setUnitesocleType(String unitesocleType) {
		this.unitesocleType = unitesocleType;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitesocle_caracteristique")
	public String getUnitesocleCaracteristique() {
		if (unitesocleCaracteristique == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SOCLE + " - Champs unitesocle_caracteristique - Valeur 'Null' non permise.");
		}
		return unitesocleCaracteristique;
	}

	public void setUnitesocleCaracteristique(String unitesocleCaracteristique) {
		this.unitesocleCaracteristique = unitesocleCaracteristique;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitesocle_consignes")
	public String getUnitesocleConsignes() {
		if (unitesocleConsignes == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SOCLE + " - Champs unitesocle_consignes - Valeur 'Null' non permise.");
		}
		return unitesocleConsignes;
	}

	public void setUnitesocleConsignes(String unitesocleConsignes) {
		this.unitesocleConsignes = unitesocleConsignes;
	}

	public String getUnitesocleCommentaire() {
		return unitesocleCommentaire;
	}

	public void setUnitesocleCommentaire(String unitesocleCommentaire) {
		this.unitesocleCommentaire = unitesocleCommentaire;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitesocle_statut")
	public String getUnitesocleStatut() {
		if (unitesocleStatut == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SOCLE + " - Champs unitesocle_statut - Valeur 'Null' non permise.");
		}
		return unitesocleStatut;
	}

	public void setUnitesocleStatut(String unitesocleStatut) {
		this.unitesocleStatut = unitesocleStatut;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitesocle_miseenservice")
	public Date getUnitesocleMiseenservice() {
		if (unitesocleMiseenservice == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SOCLE + " - Champs unitesocle_miseenservice - Valeur 'Null' non permise.");
		}
		return unitesocleMiseenservice;
	}

	public void setUnitesocleMiseenservice(Date unitesocleMiseenservice) {
		this.unitesocleMiseenservice = unitesocleMiseenservice;
	}

	public Date getUnitesocleDatefermeture() {
		return unitesocleDatefermeture;
	}

	public void setUnitesocleDatefermeture(Date unitesocleDatefermeture) {
		this.unitesocleDatefermeture = unitesocleDatefermeture;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitesocle_etat")
	public String getUnitesocleEtat() {
		if (unitesocleEtat == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SOCLE + " - Champs unitesocle_etat - Valeur 'Null' non permise.");
		}
		return unitesocleEtat;
	}

	public void setUnitesocleEtat(String unitesocleEtat) {
		this.unitesocleEtat = unitesocleEtat;
	}

	public Date getUnitesocleDaterehabilitation1() {
		return unitesocleDaterehabilitation1;
	}

	public void setUnitesocleDaterehabilitation1(Date unitesocleDaterehabilitation1) {
		this.unitesocleDaterehabilitation1 = unitesocleDaterehabilitation1;
	}

	public String getUnitesocleContenurehabilitation1() {
		return unitesocleContenurehabilitation1;
	}

	public void setUnitesocleContenurehabilitation1(String unitesocleContenurehabilitation1) {
		this.unitesocleContenurehabilitation1 = unitesocleContenurehabilitation1;
	}

	public Date getUnitesocleDaterehabilitation2() {
		return unitesocleDaterehabilitation2;
	}

	public void setUnitesocleDaterehabilitation2(Date unitesocleDaterehabilitation2) {
		this.unitesocleDaterehabilitation2 = unitesocleDaterehabilitation2;
	}

	public String getUnitesocleContenurehabilitation2() {
		return unitesocleContenurehabilitation2;
	}

	public void setUnitesocleContenurehabilitation2(String unitesocleContenurehabilitation2) {
		this.unitesocleContenurehabilitation2 = unitesocleContenurehabilitation2;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
	
	
}
