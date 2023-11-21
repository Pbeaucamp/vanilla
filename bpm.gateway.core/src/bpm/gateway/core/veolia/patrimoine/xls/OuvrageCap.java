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
@Table (name = PatrimoineDAO.OUVRAGE_CAP)
public class OuvrageCap {

	@Id
	@Column(name = "ouvragecap_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String ouvragecapOuvragesocleId;

	@Transient
	private Double ouvragecapCoteniveau;

	@Transient
	private String ouvragecapNbss;

	@Transient
	private String ouvragecapNsandre;

	@Transient
	private Double ouvragecapProfondeur;

	//TODO: Liste
	@Transient
	private String ouvragecapAquifere;

	@Transient
	private Date ouvragecapAutorisationprelevdate;

	@Transient
	private String ouvragecapAutorisationprelevcom;

	@Transient
	private String ouvragecapSuiviqualificatif;

	//TODO: Liste
	@Transient
	private String ouvragecapTypeouvrage;

	//TODO: Liste
	@Transient
	private String ouvragecapTypecaptage;

	@Transient
	private String ouvragecapPompage;

	@Transient
	private String ouvragecapEtat;

	//TODO: Liste
	@Transient
	private String ouvragecapPiezometre;

	@Transient
	private String ouvragecapPj1;

	@Transient
	private String ouvragecapPj2;
	
	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecap_ouvragesocle_id")
	public String getOuvragecapOuvragesocleId() {
		if (ouvragecapOuvragesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAP + " - Champs ouvragecap_ouvragesocle_id - Valeur 'Null' non permise.");
		}
		return ouvragecapOuvragesocleId;
	}

	public void setOuvragecapOuvragesocleId(String ouvragecapOuvragesocleId) {
		this.ouvragecapOuvragesocleId = ouvragecapOuvragesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecap_coteniveau")
	public Double getOuvragecapCoteniveau() {
		if (ouvragecapCoteniveau == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAP + " - Champs ouvragecap_coteniveau - Valeur 'Null' non permise.");
		}
		return ouvragecapCoteniveau;
	}

	public void setOuvragecapCoteniveau(Double ouvragecapCoteniveau) {
		this.ouvragecapCoteniveau = ouvragecapCoteniveau;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecap_nbss")
	public String getOuvragecapNbss() {
		if (ouvragecapNbss == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAP + " - Champs ouvragecap_nbss - Valeur 'Null' non permise.");
		}
		return ouvragecapNbss;
	}

	public void setOuvragecapNbss(String ouvragecapNbss) {
		this.ouvragecapNbss = ouvragecapNbss;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecap_nsandre")
	public String getOuvragecapNsandre() {
		if (ouvragecapNsandre == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAP + " - Champs ouvragecap_nsandre - Valeur 'Null' non permise.");
		}
		return ouvragecapNsandre;
	}

	public void setOuvragecapNsandre(String ouvragecapNsandre) {
		this.ouvragecapNsandre = ouvragecapNsandre;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecap_profondeur")
	public Double getOuvragecapProfondeur() {
		if (ouvragecapProfondeur == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAP + " - Champs ouvragecap_profondeur - Valeur 'Null' non permise.");
		}
		return ouvragecapProfondeur;
	}

	public void setOuvragecapProfondeur(Double ouvragecapProfondeur) {
		this.ouvragecapProfondeur = ouvragecapProfondeur;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecap_aquifere")
	public String getOuvragecapAquifere() {
		if (ouvragecapAquifere == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAP + " - Champs ouvragecap_aquifere - Valeur 'Null' non permise.");
		}
		return ouvragecapAquifere;
	}

	public void setOuvragecapAquifere(String ouvragecapAquifere) {
		this.ouvragecapAquifere = ouvragecapAquifere;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecap_autorisationprelevdate")
	public Date getOuvragecapAutorisationprelevdate() {
		if (ouvragecapAutorisationprelevdate == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAP + " - Champs ouvragecap_autorisationprelevdate - Valeur 'Null' non permise.");
		}
		return ouvragecapAutorisationprelevdate;
	}

	public void setOuvragecapAutorisationprelevdate(Date ouvragecapAutorisationprelevdate) {
		this.ouvragecapAutorisationprelevdate = ouvragecapAutorisationprelevdate;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecap_autorisationprelevcom")
	public String getOuvragecapAutorisationprelevcom() {
		if (ouvragecapAutorisationprelevcom == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAP + " - Champs ouvragecap_autorisationprelevcom - Valeur 'Null' non permise.");
		}
		return ouvragecapAutorisationprelevcom;
	}

	public void setOuvragecapAutorisationprelevcom(String ouvragecapAutorisationprelevcom) {
		this.ouvragecapAutorisationprelevcom = ouvragecapAutorisationprelevcom;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecap_suiviqualificatif")
	public String getOuvragecapSuiviqualificatif() {
		if (ouvragecapSuiviqualificatif == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAP + " - Champs ouvragecap_suiviqualificatif - Valeur 'Null' non permise.");
		}
		return ouvragecapSuiviqualificatif;
	}

	public void setOuvragecapSuiviqualificatif(String ouvragecapSuiviqualificatif) {
		this.ouvragecapSuiviqualificatif = ouvragecapSuiviqualificatif;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecap_typeouvrage")
	public String getOuvragecapTypeouvrage() {
		if (ouvragecapTypeouvrage == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAP + " - Champs ouvragecap_typeouvrage - Valeur 'Null' non permise.");
		}
		return ouvragecapTypeouvrage;
	}

	public void setOuvragecapTypeouvrage(String ouvragecapTypeouvrage) {
		this.ouvragecapTypeouvrage = ouvragecapTypeouvrage;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecap_typecaptage")
	public String getOuvragecapTypecaptage() {
		if (ouvragecapTypecaptage == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAP + " - Champs ouvragecap_typecaptage - Valeur 'Null' non permise.");
		}
		return ouvragecapTypecaptage;
	}

	public void setOuvragecapTypecaptage(String ouvragecapTypecaptage) {
		this.ouvragecapTypecaptage = ouvragecapTypecaptage;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecap_pompage")
	public String getOuvragecapPompage() {
		if (ouvragecapPompage == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAP + " - Champs ouvragecap_pompage - Valeur 'Null' non permise.");
		}
		return ouvragecapPompage;
	}

	public void setOuvragecapPompage(String ouvragecapPompage) {
		this.ouvragecapPompage = ouvragecapPompage;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecap_etat")
	public String getOuvragecapEtat() {
		if (ouvragecapEtat == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAP + " - Champs ouvragecap_etat - Valeur 'Null' non permise.");
		}
		return ouvragecapEtat;
	}

	public void setOuvragecapEtat(String ouvragecapEtat) {
		this.ouvragecapEtat = ouvragecapEtat;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecap_piezometre")
	public String getOuvragecapPiezometre() {
		if (ouvragecapPiezometre == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAP + " - Champs ouvragecap_piezometre - Valeur 'Null' non permise.");
		}
		return ouvragecapPiezometre;
	}

	public void setOuvragecapPiezometre(String ouvragecapPiezometre) {
		this.ouvragecapPiezometre = ouvragecapPiezometre;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecap_pj1")
	public String getOuvragecapPj1() {
		if (ouvragecapPj1 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAP + " - Champs ouvragecap_pj1 - Valeur 'Null' non permise.");
		}
		return ouvragecapPj1;
	}

	public void setOuvragecapPj1(String ouvragecapPj1) {
		this.ouvragecapPj1 = ouvragecapPj1;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragecap_pj2")
	public String getOuvragecapPj2() {
		if (ouvragecapPj2 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_CAP + " - Champs ouvragecap_pj2 - Valeur 'Null' non permise.");
		}
		return ouvragecapPj2;
	}

	public void setOuvragecapPj2(String ouvragecapPj2) {
		this.ouvragecapPj2 = ouvragecapPj2;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
}
