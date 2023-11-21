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
@Table(name = PatrimoineDAO.OUVRAGE_SOCLE)
public class OuvrageSocle {

	@Id
	@Column(name = "ouvragesocle_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String ouvragesocleVeId;

	@Transient
	private String ouvragesocleSiteId;

	@Transient
	private String ouvragesocleDenomination;

	//TODO: Liste
	@Transient
	private String ouvragesocleTypeLong;

	@Transient
	private String ouvragesocleDescription;

	@Transient
	private Integer ouvragesocleDateconstruction;

	@Transient
	private Date ouvragesocleMiseenservice;
	
	@Column(name = "ouvragesocle_daterehab")
	private Date ouvragesocleDaterehab;

	@Column(name = "ouvragesocle_datefermeture")
	private Date ouvragesocleDatefermeture;

	@Column(name = "ouvragesocle_commentaire")
	private String ouvragesocleCommentaire;

	//TODO: Liste
	@Transient
	private String ouvragesocleStatut;

	@Transient
	private Double ouvragesocleCotesol;

	@Transient
	private Double ouvragesocleXcc47;
	
	@Transient
	private Double ouvragesocleYcc47;
	
	@Transient
	private Double ouvragesocleXcc47recal;
	
	@Transient
	private Double ouvragesocleYcc47recal;

	@Column(name = "ouvragesocle_pj1")
	private String ouvragesoclePj1;

	@Column(name = "ouvragesocle_pj2")
	private String ouvragesoclePj2;

	@Column(name = "ouvragesocle_pj3")
	private String ouvragesoclePj3;

	@Column(name = "ouvragesocle_pj5")
	private String ouvragesoclePj5;

	@Column(name = "ouvragesocle_pj6")
	private String ouvragesoclePj6;

	@Column(name = "ouvragesocle_pj7")
	private String ouvragesoclePj7;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragesocle_ve_id")
	public String getOuvragesocleVeId() {
		if (ouvragesocleVeId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_SOCLE + " - Champs ouvragesocle_ve_id - Valeur 'Null' non permise.");
		}
		return ouvragesocleVeId;
	}

	public void setOuvragesocleVeId(String ouvragesocleVeId) {
		this.ouvragesocleVeId = ouvragesocleVeId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragesocle_site_id")
	public String getOuvragesocleSiteId() {
		if (ouvragesocleSiteId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_SOCLE + " - Champs ouvragesocle_site_id - Valeur 'Null' non permise.");
		}
		return ouvragesocleSiteId;
	}

	public void setOuvragesocleSiteId(String ouvragesocleSiteId) {
		this.ouvragesocleSiteId = ouvragesocleSiteId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragesocle_denomination")
	public String getOuvragesocleDenomination() {
		if (ouvragesocleDenomination == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_SOCLE + " - Champs ouvragesocle_denomination - Valeur 'Null' non permise.");
		}
		return ouvragesocleDenomination;
	}

	public void setOuvragesocleDenomination(String ouvragesocleDenomination) {
		this.ouvragesocleDenomination = ouvragesocleDenomination;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragesocle_type_long")
	public String getOuvragesocleTypeLong() {
		if (ouvragesocleTypeLong == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_SOCLE + " - Champs ouvragesocle_type_long - Valeur 'Null' non permise.");
		}
		return ouvragesocleTypeLong;
	}

	public void setOuvragesocleTypeLong(String ouvragesocleTypeLong) {
		this.ouvragesocleTypeLong = ouvragesocleTypeLong;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragesocle_description")
	public String getOuvragesocleDescription() {
		if (ouvragesocleDescription == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_SOCLE + " - Champs ouvragesocle_description - Valeur 'Null' non permise.");
		}
		return ouvragesocleDescription;
	}

	public void setOuvragesocleDescription(String ouvragesocleDescription) {
		this.ouvragesocleDescription = ouvragesocleDescription;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragesocle_dateconstruction")
	public Integer getOuvragesocleDateconstruction() {
		if (ouvragesocleDateconstruction == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_SOCLE + " - Champs ouvragesocle_dateconstruction - Valeur 'Null' non permise.");
		}
		return ouvragesocleDateconstruction;
	}

	public void setOuvragesocleDateconstruction(Integer ouvragesocleDateconstruction) {
		this.ouvragesocleDateconstruction = ouvragesocleDateconstruction;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragesocle_miseenservice")
	public Date getOuvragesocleMiseenservice() {
		if (ouvragesocleMiseenservice == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_SOCLE + " - Champs ouvragesocle_miseenservice - Valeur 'Null' non permise.");
		}
		return ouvragesocleMiseenservice;
	}

	public void setOuvragesocleMiseenservice(Date ouvragesocleMiseenservice) {
		this.ouvragesocleMiseenservice = ouvragesocleMiseenservice;
	}
	
	public Date getOuvragesocleDaterehab() {
		return ouvragesocleDaterehab;
	}
	
	public void setOuvragesocleDaterehab(Date ouvragesocleDaterehab) {
		this.ouvragesocleDaterehab = ouvragesocleDaterehab;
	}

	public Date getOuvragesocleDatefermeture() {
		return ouvragesocleDatefermeture;
	}

	public void setOuvragesocleDatefermeture(Date ouvragesocleDatefermeture) {
		this.ouvragesocleDatefermeture = ouvragesocleDatefermeture;
	}

	public String getOuvragesocleCommentaire() {
		return ouvragesocleCommentaire;
	}

	public void setOuvragesocleCommentaire(String ouvragesocleCommentaire) {
		this.ouvragesocleCommentaire = ouvragesocleCommentaire;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragesocle_statut")
	public String getOuvragesocleStatut() {
		if (ouvragesocleStatut == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_SOCLE + " - Champs ouvragesocle_statut - Valeur 'Null' non permise.");
		}
		return ouvragesocleStatut;
	}

	public void setOuvragesocleStatut(String ouvragesocleStatut) {
		this.ouvragesocleStatut = ouvragesocleStatut;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragesocle_cotesol")
	public Double getOuvragesocleCotesol() {
		if (ouvragesocleCotesol == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_SOCLE + " - Champs ouvragesocle_cotesol - Valeur 'Null' non permise.");
		}
		return ouvragesocleCotesol;
	}

	public void setOuvragesocleCotesol(Double ouvragesocleCotesol) {
		this.ouvragesocleCotesol = ouvragesocleCotesol;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragesocle_xcc47")
	public Double getOuvragesocleXcc47() {
		return ouvragesocleXcc47;
	}

	public void setOuvragesocleXcc47(Double ouvragesocleXcc47) {
		this.ouvragesocleXcc47 = ouvragesocleXcc47;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragesocle_ycc47")
	public Double getOuvragesocleYcc47() {
		return ouvragesocleYcc47;
	}

	public void setOuvragesocleYcc47(Double ouvragesocleYcc47) {
		this.ouvragesocleYcc47 = ouvragesocleYcc47;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragesocle_xcc47recal")
	public Double getOuvragesocleXcc47recal() {
		return ouvragesocleXcc47recal;
	}

	public void setOuvragesocleXcc47recal(Double ouvragesocleXcc47recal) {
		this.ouvragesocleXcc47recal = ouvragesocleXcc47recal;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragesocle_ycc47recal")
	public Double getOuvragesocleYcc47recal() {
		return ouvragesocleYcc47recal;
	}

	public void setOuvragesocleYcc47recal(Double ouvragesocleYcc47recal) {
		this.ouvragesocleYcc47recal = ouvragesocleYcc47recal;
	}

	public String getOuvragesoclePj1() {
		return ouvragesoclePj1;
	}

	public void setOuvragesoclePj1(String ouvragesoclePj1) {
		this.ouvragesoclePj1 = ouvragesoclePj1;
	}

	public String getOuvragesoclePj2() {
		return ouvragesoclePj2;
	}

	public void setOuvragesoclePj2(String ouvragesoclePj2) {
		this.ouvragesoclePj2 = ouvragesoclePj2;
	}

	public String getOuvragesoclePj3() {
		return ouvragesoclePj3;
	}

	public void setOuvragesoclePj3(String ouvragesoclePj3) {
		this.ouvragesoclePj3 = ouvragesoclePj3;
	}

	public String getOuvragesoclePj5() {
		return ouvragesoclePj5;
	}

	public void setOuvragesoclePj5(String ouvragesoclePj5) {
		this.ouvragesoclePj5 = ouvragesoclePj5;
	}

	public String getOuvragesoclePj6() {
		return ouvragesoclePj6;
	}

	public void setOuvragesoclePj6(String ouvragesoclePj6) {
		this.ouvragesoclePj6 = ouvragesoclePj6;
	}

	public String getOuvragesoclePj7() {
		return ouvragesoclePj7;
	}

	public void setOuvragesoclePj7(String ouvragesoclePj7) {
		this.ouvragesoclePj7 = ouvragesoclePj7;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

}
