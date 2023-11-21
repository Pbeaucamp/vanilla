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
@Table (name = PatrimoineDAO.SITE)
public class Site {

	@Id
	@Column(name = "site_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String siteVeId;

	@Transient
	private String siteVeContrat;

	@Transient
	private String siteDenomination;

	//TODO: Liste
	@Transient
	private String siteCompetence;

	//TODO: Liste
	@Transient
	private String siteProd;

	//TODO: Liste
	@Transient
	private String siteSecteur;

	//TODO: Liste
	@Transient
	private String siteCommune;

	//TODO: Liste
	@Transient
	private String siteInsee;

	@Transient
	private String siteNordwgs84;

	@Transient
	private String siteOuestwgs84;

	@Column(name = "site_section")
	private String siteSection;

	@Column(name = "site_numsection")
	private Integer siteNumsection;

	//TODO: Liste
	@Transient
	private String siteNomProprietaire;

	//TODO: Liste
	@Transient
	private String siteStatut;

	//TODO: Liste
	@Transient
	private String siteFonctionnement;

	//TODO: Liste
	@Transient
	private String siteInondable;

	//TODO: Liste
	@Transient
	private String siteProtection;
	
	@Column(name = "site_pprdate")
	private Date sitePprdate;
	
	@Column(name = "site_commentaire")
	private String siteCommentaire;

	@Transient
	private String sitePj1;

	@Transient
	private String sitePj2;

	@Transient
	private String sitePj3;

	@Transient
	private String sitePj4;

	@Transient
	private String sitePj6;
	
	@Column(name = "site_tiers")
	private String siteTiers;
	
	@Column(name = "site_convention")
	private String siteConvention;
	
	@Column(name = "site_servitudes")
	private String siteServitudes;
	
	@Column(name = "site_pjservitudes")
	private String sitePjservitudes;
	
	@Column(name = "site_pj10")
	private String sitePj10;
	
	@Column(name = "site_pj11")
	private String sitePj11;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_ve_id")
	public String getSiteVEID() {
		if (siteVeId == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_ve_id - Valeur 'Null' non permise.");
    	}
		return siteVeId;
	}

	public void setSiteVEID(String siteVeId) {
		this.siteVeId = siteVeId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_contrat_id")
	public String getSiteVeContrat() {
		if (siteVeContrat == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_contrat_id - Valeur 'Null' non permise.");
    	}
		return siteVeContrat;
	}

	public void setSiteVeContrat(String siteVeContrat) {
		this.siteVeContrat = siteVeContrat;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_denomination")
	public String getSiteDenomination() {
		if (siteDenomination == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_denomination - Valeur 'Null' non permise.");
    	}
		return siteDenomination;
	}

	public void setSiteDenomination(String siteDenomination) {
		this.siteDenomination = siteDenomination;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_competence")
	public String getSiteCompetence() {
		if (siteCompetence == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_competence - Valeur 'Null' non permise.");
    	}
		return siteCompetence;
	}

	public void setSiteCompetence(String siteCompetence) {
		this.siteCompetence = siteCompetence;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_prod")
	public String getSiteProd() {
		if (siteProd == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_prod - Valeur 'Null' non permise.");
    	}
		return siteProd;
	}

	public void setSiteProd(String siteProd) {
		this.siteProd = siteProd;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_secteur")
	public String getSiteSecteur() {
		if (siteSecteur == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_secteur - Valeur 'Null' non permise.");
    	}
		return siteSecteur;
	}

	public void setSiteSecteur(String siteSecteur) {
		this.siteSecteur = siteSecteur;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_commune")
	public String getSiteCommune() {
		if (siteCommune == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_commune - Valeur 'Null' non permise.");
    	}
		return siteCommune;
	}

	public void setSiteCommune(String siteCommune) {
		this.siteCommune = siteCommune;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_insee")
	public String getSiteInsee() {
		if (siteInsee == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_insee - Valeur 'Null' non permise.");
    	}
		return siteInsee;
	}

	public void setSiteInsee(String siteInsee) {
		this.siteInsee = siteInsee;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_nordwgs84")
	public String getSiteNordwgs84() {
		if (siteNordwgs84 == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_nordwgs84 - Valeur 'Null' non permise.");
    	}
		return siteNordwgs84;
	}

	public void setSiteNordwgs84(String siteNordwgs84) {
		this.siteNordwgs84 = siteNordwgs84;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_ouestwgs84")
	public String getSiteOuestwgs84() {
		if (siteOuestwgs84 == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_ouestwgs84 - Valeur 'Null' non permise.");
    	}
		return siteOuestwgs84;
	}

	public void setSiteOuestwgs84(String siteOuestwgs84) {
		this.siteOuestwgs84 = siteOuestwgs84;
	}

	public String getSiteSection() {
		return siteSection;
	}

	public void setSiteSection(String siteSection) {
		this.siteSection = siteSection;
	}

	public Integer getSiteNumsection() {
		return siteNumsection;
	}

	public void setSiteNumsection(Integer siteNumsection) {
		this.siteNumsection = siteNumsection;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_nom_proprietaire")
	public String getSiteNomProprietaire() {
		if (siteNomProprietaire == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_nom_proprietaire - Valeur 'Null' non permise.");
    	}
		return siteNomProprietaire;
	}

	public void setSiteNomProprietaire(String siteNomProprietaire) {
		this.siteNomProprietaire = siteNomProprietaire;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_statut")
	public String getSiteStatut() {
		if (siteStatut == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_statut - Valeur 'Null' non permise.");
    	}
		return siteStatut;
	}

	public void setSiteStatut(String siteStatut) {
		this.siteStatut = siteStatut;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_fonctionnement")
	public String getSiteFonctionnement() {
		if (siteFonctionnement == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_fonctionnement - Valeur 'Null' non permise.");
    	}
		return siteFonctionnement;
	}

	public void setSiteFonctionnement(String siteFonctionnement) {
		this.siteFonctionnement = siteFonctionnement;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_inondable")
	public String getSiteInondable() {
		if (siteInondable == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_inondable - Valeur 'Null' non permise.");
    	}
		return siteInondable;
	}

	public void setSiteInondable(String siteInondable) {
		this.siteInondable = siteInondable;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_protection")
	public String getSiteProtection() {
		if (siteProtection == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_protection - Valeur 'Null' non permise.");
    	}
		return siteProtection;
	}

	public void setSiteProtection(String siteProtection) {
		this.siteProtection = siteProtection;
	}

	public Date getSitePprdate() {
		return sitePprdate;
	}

	public void setSitePprdate(Date sitePprdate) {
		this.sitePprdate = sitePprdate;
	}

	public String getSiteCommentaire() {
		return siteCommentaire;
	}

	public void setSiteCommentaire(String siteCommentaire) {
		this.siteCommentaire = siteCommentaire;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_pj1")
	public String getSitePj1() {
		if (sitePj1 == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_pj1 - Valeur 'Null' non permise.");
    	}
		return sitePj1;
	}

	public void setSitePj1(String sitePj1) {
		this.sitePj1 = sitePj1;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_pj2")
	public String getSitePj2() {
		if (sitePj2 == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_pj2 - Valeur 'Null' non permise.");
    	}
		return sitePj2;
	}

	public void setSitePj2(String sitePj2) {
		this.sitePj2 = sitePj2;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_pj3")
	public String getSitePj3() {
		if (sitePj3 == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_pj3 - Valeur 'Null' non permise.");
    	}
		return sitePj3;
	}

	public void setSitePj3(String sitePj3) {
		this.sitePj3 = sitePj3;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_pj4")
	public String getSitePj4() {
		if (sitePj4 == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_pj4 - Valeur 'Null' non permise.");
    	}
		return sitePj4;
	}

	public void setSitePj4(String sitePj4) {
		this.sitePj4 = sitePj4;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "site_pj6")
	public String getSitePj6() {
		if (sitePj6 == null) {
    		throw new IllegalArgumentException("Table " + PatrimoineDAO.SITE + " - Champs site_pj6 - Valeur 'Null' non permise.");
    	}
		return sitePj6;
	}

	public void setSitePj6(String sitePj6) {
		this.sitePj6 = sitePj6;
	}

	public String getSiteTiers() {
		return siteTiers;
	}

	public void setSiteTiers(String siteTiers) {
		this.siteTiers = siteTiers;
	}

	public String getSiteConvention() {
		return siteConvention;
	}

	public void setSiteConvention(String siteConvention) {
		this.siteConvention = siteConvention;
	}

	public String getSiteServitudes() {
		return siteServitudes;
	}

	public void setSiteServitudes(String siteServitudes) {
		this.siteServitudes = siteServitudes;
	}

	public String getSitePjservitudes() {
		return sitePjservitudes;
	}

	public void setSitePjservitudes(String sitePjservitudes) {
		this.sitePjservitudes = sitePjservitudes;
	}

	public String getSitePj10() {
		return sitePj10;
	}

	public void setSitePj10(String sitePj10) {
		this.sitePj10 = sitePj10;
	}

	public String getSitePj11() {
		return sitePj11;
	}
	
	public void setSitePj11(String sitePj11) {
		this.sitePj11 = sitePj11;
	}
	
	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
}
