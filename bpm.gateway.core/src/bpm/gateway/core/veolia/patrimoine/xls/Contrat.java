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
@Table (name = PatrimoineDAO.CONTRAT)
public class Contrat {

	@Id
	@Column(name = "contrat_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Transient
	private String contratVeId;

	@Transient
	private String contratDenomination;

	@Transient
	private String contratSecteur;

	@Transient
	private String contratExploitant;

	@Transient
	private Date contratDatedebut;

	@Transient
	private Date contratDatefin;

	@Transient
	private String contratAbrevnom;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "contrat_ve_id")
	public String getContratVeId() {
		if (contratVeId == null) {
			manageNullException("contrat_ve_id");
    	}
		return contratVeId;
	}

	public void setContratVeId(String contratVeId) {
		this.contratVeId = contratVeId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "contrat_denomination")
	public String getContratDenomination() {
//		return ListeContratDenomination.getValue(PatrimoineDAO.CONTRAT, "contrat_denomination", contratDenomination, true);
		return contratDenomination;
	}

	public void setContratDenomination(String value) {
//		this.contratDenomination = ListeContratDenomination.fromValue(PatrimoineDAO.CONTRAT, "contrat_denomination", value);
		this.contratDenomination = value;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "contrat_secteur")
	public String getContratSecteur() {
		if (contratSecteur == null) {
			manageNullException("contrat_secteur");
    	}
		return contratSecteur;
	}

	public void setContratSecteur(String contratSecteur) {
		this.contratSecteur = contratSecteur;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "contrat_exploitant")
	public String getContratExploitant() {
		if (contratExploitant == null) {
			manageNullException("contrat_exploitant");
    	}
		return contratExploitant;
	}

	public void setContratExploitant(String contratExploitant) {
		this.contratExploitant = contratExploitant;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "contrat_datedebut")
	public Date getContratDatedebut() {
		if (contratDatedebut == null) {
			manageNullException("contrat_datedebut");
    	}
		return contratDatedebut;
	}

	public void setContratDatedebut(Date contratDatedebut) {
		this.contratDatedebut = contratDatedebut;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "contrat_datefin")
	public Date getContratDatefin() {
		if (contratDatefin == null) {
			manageNullException("contrat_datefin");
    	}
		return contratDatefin;
	}

	public void setContratDatefin(Date contratDatefin) {
		this.contratDatefin = contratDatefin;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "contrat_abrevnom")
	public String getContratAbrevnom() {
		if (contratAbrevnom == null) {
			manageNullException("contrat_abrevnom");
    	}
		return contratAbrevnom;
	}
	
	public void setContratAbrevnom(String contratAbrevnom) {
		this.contratAbrevnom = contratAbrevnom;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

    private void manageNullException(String champ) {
    	throw new IllegalArgumentException("Table " + PatrimoineDAO.CONTRAT + " - Champs " + champ + " - Valeur 'Null' ou non permise.");
	}

}
