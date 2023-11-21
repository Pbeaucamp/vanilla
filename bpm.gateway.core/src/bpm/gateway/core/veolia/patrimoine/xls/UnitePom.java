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
@Table (name = PatrimoineDAO.UNITE_POM)
public class UnitePom {

	@Id
	@Column(name = "unitepom_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String unitepomUnitesocleId;

	@Transient
	private Double unitepomNbpompes;

	@Transient
	private Double unitepomNbpmpesmaxfonctionnement;

	@Transient
	private String unitepomTyperegul;

	@Transient
	private String unitepomOrganecontrole;

	@Transient
	private String unitepomDebitotal;

	@Transient
	private Double unitepomHmt;

	@Transient
	private Double unitepomDebitmin;

	@Transient
	private Double unitepomHmtmin;

	@Transient
	private Double unitepomDebitmax;

	@Transient
	private Double unitepomHmtmax;

	@Transient
	private Double unitepomRdt;

	@Transient
	private String unitepomCourbefonctionnementpompage;

	@Transient
	private Double unitepomCotesol;

	@Transient
	private String unitepomSynoptiquehydraulique;

	@Transient
	private String unitepomPhoto;

	//TODO: Liste
	@Transient
	private String unitepomPeriodefonctionnement;

	//TODO: Liste
	@Transient
	private String unitepomGrpeelectrogene;

	@Transient
	private Integer unitepomEdfpuissancesouscrite;

	@Transient
	private Integer unitepomPuissanceabsorbeetransformateur;

	@Transient
	private String unitepomTypecontrat;

	@Transient
	private String unitepomPlateforme;

	@Transient
	private Integer unitepomPuissance;

	//TODO: Liste	
	@Transient
	private String unitepomAntibelier;

	@Transient
	private Double unitepomNbantibelier;

	@Transient
	private String unitepomUsageantibelier;

	@Transient
	private String unitepomCommentaire1;

	@Transient
	private String unitepomCommentaire2;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_unitesocle_id")
	public String getUnitepomUnitesocleId() {
		if (unitepomUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_unitesocle_id - Valeur 'Null' non permise.");
		}
		return unitepomUnitesocleId;
	}

	public void setUnitepomUnitesocleId(String unitepomUnitesocleId) {
		this.unitepomUnitesocleId = unitepomUnitesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_nbpompes")
	public Double getUnitepomNbpompes() {
		if (unitepomNbpompes == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_nbpompes - Valeur 'Null' non permise.");
		}
		return unitepomNbpompes;
	}

	public void setUnitepomNbpompes(Double unitepomNbpompes) {
		this.unitepomNbpompes = unitepomNbpompes;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_nbpmpesmaxfonctionnement")
	public Double getUnitepomNbpmpesmaxfonctionnement() {
		if (unitepomNbpmpesmaxfonctionnement == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_nbpmpesmaxfonctionnement - Valeur 'Null' non permise.");
		}
		return unitepomNbpmpesmaxfonctionnement;
	}

	public void setUnitepomNbpmpesmaxfonctionnement(Double unitepomNbpmpesmaxfonctionnement) {
		this.unitepomNbpmpesmaxfonctionnement = unitepomNbpmpesmaxfonctionnement;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_typeregul")
	public String getUnitepomTyperegul() {
		if (unitepomTyperegul == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_typeregul - Valeur 'Null' non permise.");
		}
		return unitepomTyperegul;
	}

	public void setUnitepomTyperegul(String unitepomTyperegul) {
		this.unitepomTyperegul = unitepomTyperegul;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_organecontrole")
	public String getUnitepomOrganecontrole() {
		if (unitepomOrganecontrole == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_organecontrole - Valeur 'Null' non permise.");
		}
		return unitepomOrganecontrole;
	}

	public void setUnitepomOrganecontrole(String unitepomOrganecontrole) {
		this.unitepomOrganecontrole = unitepomOrganecontrole;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_debitotal")
	public String getUnitepomDebitotal() {
		if (unitepomDebitotal == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_debitotal - Valeur 'Null' non permise.");
		}
		return unitepomDebitotal;
	}

	public void setUnitepomDebitotal(String unitepomDebitotal) {
		this.unitepomDebitotal = unitepomDebitotal;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_hmt")
	public Double getUnitepomHmt() {
		if (unitepomHmt == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_hmt - Valeur 'Null' non permise.");
		}
		return unitepomHmt;
	}

	public void setUnitepomHmt(Double unitepomHmt) {
		this.unitepomHmt = unitepomHmt;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_debitmin")
	public Double getUnitepomDebitmin() {
		if (unitepomDebitmin == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_debitmin - Valeur 'Null' non permise.");
		}
		return unitepomDebitmin;
	}

	public void setUnitepomDebitmin(Double unitepomDebitmin) {
		this.unitepomDebitmin = unitepomDebitmin;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_hmtmin")
	public Double getUnitepomHmtmin() {
		if (unitepomHmtmin == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_hmtmin - Valeur 'Null' non permise.");
		}
		return unitepomHmtmin;
	}

	public void setUnitepomHmtmin(Double unitepomHmtmin) {
		this.unitepomHmtmin = unitepomHmtmin;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_debitmax")
	public Double getUnitepomDebitmax() {
		if (unitepomDebitmax == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_debitmax - Valeur 'Null' non permise.");
		}
		return unitepomDebitmax;
	}

	public void setUnitepomDebitmax(Double unitepomDebitmax) {
		this.unitepomDebitmax = unitepomDebitmax;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_hmtmax")
	public Double getUnitepomHmtmax() {
		if (unitepomHmtmax == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_hmtmax - Valeur 'Null' non permise.");
		}
		return unitepomHmtmax;
	}

	public void setUnitepomHmtmax(Double unitepomHmtmax) {
		this.unitepomHmtmax = unitepomHmtmax;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_rdt")
	public Double getUnitepomRdt() {
		if (unitepomRdt == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_rdt - Valeur 'Null' non permise.");
		}
		return unitepomRdt;
	}

	public void setUnitepomRdt(Double unitepomRdt) {
		this.unitepomRdt = unitepomRdt;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_courbefonctionnementpompage")
	public String getUnitepomCourbefonctionnementpompage() {
		if (unitepomCourbefonctionnementpompage == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_courbefonctionnementpompage - Valeur 'Null' non permise.");
		}
		return unitepomCourbefonctionnementpompage;
	}

	public void setUnitepomCourbefonctionnementpompage(String unitepomCourbefonctionnementpompage) {
		this.unitepomCourbefonctionnementpompage = unitepomCourbefonctionnementpompage;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_cotesol")
	public Double getUnitepomCotesol() {
		if (unitepomCotesol == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_cotesol - Valeur 'Null' non permise.");
		}
		return unitepomCotesol;
	}

	public void setUnitepomCotesol(Double unitepomCotesol) {
		this.unitepomCotesol = unitepomCotesol;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_synoptiquehydraulique")
	public String getUnitepomSynoptiquehydraulique() {
		if (unitepomSynoptiquehydraulique == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_synoptiquehydraulique - Valeur 'Null' non permise.");
		}
		return unitepomSynoptiquehydraulique;
	}

	public void setUnitepomSynoptiquehydraulique(String unitepomSynoptiquehydraulique) {
		this.unitepomSynoptiquehydraulique = unitepomSynoptiquehydraulique;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_photo")
	public String getUnitepomPhoto() {
		if (unitepomPhoto == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_photo - Valeur 'Null' non permise.");
		}
		return unitepomPhoto;
	}

	public void setUnitepomPhoto(String unitepomPhoto) {
		this.unitepomPhoto = unitepomPhoto;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_periodefonctionnement")
	public String getUnitepomPeriodefonctionnement() {
		if (unitepomPeriodefonctionnement == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_periodefonctionnement - Valeur 'Null' non permise.");
		}
		return unitepomPeriodefonctionnement;
	}

	public void setUnitepomPeriodefonctionnement(String unitepomPeriodefonctionnement) {
		this.unitepomPeriodefonctionnement = unitepomPeriodefonctionnement;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_grpeelectrogene")
	public String getUnitepomGrpeelectrogene() {
		if (unitepomGrpeelectrogene == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_grpeelectrogene - Valeur 'Null' non permise.");
		}
		return unitepomGrpeelectrogene;
	}

	public void setUnitepomGrpeelectrogene(String unitepomGrpeelectrogene) {
		this.unitepomGrpeelectrogene = unitepomGrpeelectrogene;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_edfpuissancesouscrite")
	public Integer getUnitepomEdfpuissancesouscrite() {
		if (unitepomEdfpuissancesouscrite == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_edfpuissancesouscrite - Valeur 'Null' non permise.");
		}
		return unitepomEdfpuissancesouscrite;
	}

	public void setUnitepomEdfpuissancesouscrite(Integer unitepomEdfpuissancesouscrite) {
		this.unitepomEdfpuissancesouscrite = unitepomEdfpuissancesouscrite;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_puissanceabsorbeetransformateur")
	public Integer getUnitepomPuissanceabsorbeetransformateur() {
		if (unitepomPuissanceabsorbeetransformateur == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_puissanceabsorbeetransformateur - Valeur 'Null' non permise.");
		}
		return unitepomPuissanceabsorbeetransformateur;
	}

	public void setUnitepomPuissanceabsorbeetransformateur(Integer unitepomPuissanceabsorbeetransformateur) {
		this.unitepomPuissanceabsorbeetransformateur = unitepomPuissanceabsorbeetransformateur;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_typecontrat")
	public String getUnitepomTypecontrat() {
		if (unitepomTypecontrat == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_typecontrat - Valeur 'Null' non permise.");
		}
		return unitepomTypecontrat;
	}

	public void setUnitepomTypecontrat(String unitepomTypecontrat) {
		this.unitepomTypecontrat = unitepomTypecontrat;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_plateforme")
	public String getUnitepomPlateforme() {
		if (unitepomPlateforme == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_plateforme - Valeur 'Null' non permise.");
		}
		return unitepomPlateforme;
	}

	public void setUnitepomPlateforme(String unitepomPlateforme) {
		this.unitepomPlateforme = unitepomPlateforme;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_puissance")
	public Integer getUnitepomPuissance() {
		if (unitepomPuissance == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_puissance - Valeur 'Null' non permise.");
		}
		return unitepomPuissance;
	}

	public void setUnitepomPuissance(Integer unitepomPuissance) {
		this.unitepomPuissance = unitepomPuissance;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_antibelier")
	public String getUnitepomAntibelier() {
		if (unitepomAntibelier == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_antibelier - Valeur 'Null' non permise.");
		}
		return unitepomAntibelier;
	}

	public void setUnitepomAntibelier(String unitepomAntibelier) {
		this.unitepomAntibelier = unitepomAntibelier;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_nbantibelier")
	public Double getUnitepomNbantibelier() {
		if (unitepomNbantibelier == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_nbantibelier - Valeur 'Null' non permise.");
		}
		return unitepomNbantibelier;
	}

	public void setUnitepomNbantibelier(Double unitepomNbantibelier) {
		this.unitepomNbantibelier = unitepomNbantibelier;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_usageantibelier")
	public String getUnitepomUsageantibelier() {
		if (unitepomUsageantibelier == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_usageantibelier - Valeur 'Null' non permise.");
		}
		return unitepomUsageantibelier;
	}

	public void setUnitepomUsageantibelier(String unitepomUsageantibelier) {
		this.unitepomUsageantibelier = unitepomUsageantibelier;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_commentaire1")
	public String getUnitepomCommentaire1() {
		if (unitepomCommentaire1 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_commentaire1 - Valeur 'Null' non permise.");
		}
		return unitepomCommentaire1;
	}

	public void setUnitepomCommentaire1(String unitepomCommentaire1) {
		this.unitepomCommentaire1 = unitepomCommentaire1;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitepom_commentaire2")
	public String getUnitepomCommentaire2() {
		if (unitepomCommentaire2 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_POM + " - Champs unitepom_commentaire2 - Valeur 'Null' non permise.");
		}
		return unitepomCommentaire2;
	}

	public void setUnitepomCommentaire2(String unitepomCommentaire2) {
		this.unitepomCommentaire2 = unitepomCommentaire2;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
	
}
