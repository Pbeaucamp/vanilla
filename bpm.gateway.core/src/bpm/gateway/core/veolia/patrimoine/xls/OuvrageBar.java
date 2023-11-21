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
@Table (name = PatrimoineDAO.OUVRAGE_BAR)
public class OuvrageBar {

	@Id
	@Column(name = "ouvragebar_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String ouvragebarOuvragesocleId;

	@Transient
	private Double ouvragebarVolumestocke;

	@Transient
	private String ouvragebarEpoqueconstruction;

	//TODO: Liste
	@Transient
	private String ouvragebarTypebarrage;

	@Transient
	private String ouvragebarTypebarragedetail;

	@Transient
	private String ouvragebarFruitparement;

	@Transient
	private Double ouvragebarHauteurtn;

	@Transient
	private Double ouvragebarHauteurfondation;

	@Transient
	private Double ouvragebarLongueurcrete;
	
	@Column(name = "ouvragebar_largeurcrete")
	private Double ouvragebarLargeurcrete;

	@Transient
	private Integer ouvragebarSurfaceplaneau;

	@Transient
	private Integer ouvragebarCapaciteretenue;

	@Transient
	private Integer ouvragebarSuperficiebv;

	@Transient
	private Integer ouvragebarDebitcrucent;

	@Transient
	private Integer ouvragebarDebitcrudec;

	@Transient
	private Integer ouvragebarDebitcrumil;

	@Transient
	private Integer ouvragebarDernierevidange;

	//TODO: Liste
	@Transient
	private String ouvragebarIrrigation;

	@Column(name = "ouvragebar_irrigationvolume")
	private Integer ouvragebarIrrigationvolume;

	//TODO: Liste
	@Column(name = "ouvragebar_irrigationtype")
	private String ouvragebarIrrigationtype;

	//TODO: Liste
	@Transient
	private String ouvragebarProdelec;

	@Transient
	private Double ouvragebarSoutienetiage;

	@Transient
	private Double ouvragebarCotecrete;

	@Transient
	private Double ouvragebarCotelegale;

	@Transient
	private Double ouvragebarCotephe;

	@Transient
	private Double ouvragebarCotehiver;

	@Transient
	private Double ouvragebarDebitreserve;

	@Transient
	private Double ouvragebarQmaxevacuation;

	//TODO: Liste
	@Transient
	private String ouvragebarPrebarrage;

	//TODO: Liste
	@Transient
	private String ouvragebarEutrophisation;

	//TODO: Liste
	@Transient
	private String ouvragebarClasse;

	@Transient
	private String ouvragebarPj1;

	@Transient
	private String ouvragebarPj2;

	@Transient
	private String ouvragebarPj3;

	@Transient
	private String ouvragebarPj4;

	@Transient
	private String ouvragebarPj5;

	@Transient
	private String ouvragebarPj6;
	
	@Column(name = "ouvragebar_pj7")
	private String ouvragebarPj7;
	
	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_ouvragesocle_id")
	public String getOuvragebarOuvragesocleId() {
		if (ouvragebarOuvragesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_ouvragesocle_id - Valeur 'Null' non permise.");
		}
		return ouvragebarOuvragesocleId;
	}

	public void setOuvragebarOuvragesocleId(String ouvragebarOuvragesocleId) {
		this.ouvragebarOuvragesocleId = ouvragebarOuvragesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_volumestocke")
	public Double getOuvragebarVolumestocke() {
		if (ouvragebarVolumestocke == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_volumestocke - Valeur 'Null' non permise.");
		}
		return ouvragebarVolumestocke;
	}

	public void setOuvragebarVolumestocke(Double ouvragebarVolumestocke) {
		this.ouvragebarVolumestocke = ouvragebarVolumestocke;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_epoqueconstruction")
	public String getOuvragebarEpoqueconstruction() {
		if (ouvragebarEpoqueconstruction == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_epoqueconstruction - Valeur 'Null' non permise.");
		}
		return ouvragebarEpoqueconstruction;
	}

	public void setOuvragebarEpoqueconstruction(String ouvragebarEpoqueconstruction) {
		this.ouvragebarEpoqueconstruction = ouvragebarEpoqueconstruction;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_typebarrage")
	public String getOuvragebarTypebarrage() {
		if (ouvragebarTypebarrage == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_typebarrage - Valeur 'Null' non permise.");
		}
		return ouvragebarTypebarrage;
	}

	public void setOuvragebarTypebarrage(String ouvragebarTypebarrage) {
		this.ouvragebarTypebarrage = ouvragebarTypebarrage;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_typebarragedetail")
	public String getOuvragebarTypebarragedetail() {
		if (ouvragebarTypebarragedetail == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_typebarragedetail - Valeur 'Null' non permise.");
		}
		return ouvragebarTypebarragedetail;
	}

	public void setOuvragebarTypebarragedetail(String ouvragebarTypebarragedetail) {
		this.ouvragebarTypebarragedetail = ouvragebarTypebarragedetail;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_fruitparement")
	public String getOuvragebarFruitparement() {
		if (ouvragebarFruitparement == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_fruitparement - Valeur 'Null' non permise.");
		}
		return ouvragebarFruitparement;
	}

	public void setOuvragebarFruitparement(String ouvragebarFruitparement) {
		this.ouvragebarFruitparement = ouvragebarFruitparement;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_hauteurtn")
	public Double getOuvragebarHauteurtn() {
		if (ouvragebarHauteurtn == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_hauteurtn - Valeur 'Null' non permise.");
		}
		return ouvragebarHauteurtn;
	}

	public void setOuvragebarHauteurtn(Double ouvragebarHauteurtn) {
		this.ouvragebarHauteurtn = ouvragebarHauteurtn;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_hauteurfondation")
	public Double getOuvragebarHauteurfondation() {
		if (ouvragebarHauteurfondation == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_hauteurfondation - Valeur 'Null' non permise.");
		}
		return ouvragebarHauteurfondation;
	}

	public void setOuvragebarHauteurfondation(Double ouvragebarHauteurfondation) {
		this.ouvragebarHauteurfondation = ouvragebarHauteurfondation;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_longueurcrete")
	public Double getOuvragebarLongueurcrete() {
		if (ouvragebarLongueurcrete == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_longueurcrete - Valeur 'Null' non permise.");
		}
		return ouvragebarLongueurcrete;
	}

	public void setOuvragebarLongueurcrete(Double ouvragebarLongueurcrete) {
		this.ouvragebarLongueurcrete = ouvragebarLongueurcrete;
	}
	
	public Double getOuvragebarLargeurcrete() {
		return ouvragebarLargeurcrete;
	}
	
	public void setOuvragebarLargeurcrete(Double ouvragebarLargeurcrete) {
		this.ouvragebarLargeurcrete = ouvragebarLargeurcrete;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_surfaceplaneau")
	public Integer getOuvragebarSurfaceplaneau() {
		if (ouvragebarSurfaceplaneau == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_surfaceplaneau - Valeur 'Null' non permise.");
		}
		return ouvragebarSurfaceplaneau;
	}

	public void setOuvragebarSurfaceplaneau(Integer ouvragebarSurfaceplaneau) {
		this.ouvragebarSurfaceplaneau = ouvragebarSurfaceplaneau;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_capaciteretenue")
	public Integer getOuvragebarCapaciteretenue() {
		if (ouvragebarCapaciteretenue == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_capaciteretenue - Valeur 'Null' non permise.");
		}
		return ouvragebarCapaciteretenue;
	}

	public void setOuvragebarCapaciteretenue(Integer ouvragebarCapaciteretenue) {
		this.ouvragebarCapaciteretenue = ouvragebarCapaciteretenue;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_superficiebv")
	public Integer getOuvragebarSuperficiebv() {
		if (ouvragebarSuperficiebv == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_superficiebv - Valeur 'Null' non permise.");
		}
		return ouvragebarSuperficiebv;
	}

	public void setOuvragebarSuperficiebv(Integer ouvragebarSuperficiebv) {
		this.ouvragebarSuperficiebv = ouvragebarSuperficiebv;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_debitcrucent")
	public Integer getOuvragebarDebitcrucent() {
		if (ouvragebarDebitcrucent == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_debitcrucent - Valeur 'Null' non permise.");
		}
		return ouvragebarDebitcrucent;
	}

	public void setOuvragebarDebitcrucent(Integer ouvragebarDebitcrucent) {
		this.ouvragebarDebitcrucent = ouvragebarDebitcrucent;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_debitcrudec")
	public Integer getOuvragebarDebitcrudec() {
		if (ouvragebarDebitcrudec == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_debitcrudec - Valeur 'Null' non permise.");
		}
		return ouvragebarDebitcrudec;
	}

	public void setOuvragebarDebitcrudec(Integer ouvragebarDebitcrudec) {
		this.ouvragebarDebitcrudec = ouvragebarDebitcrudec;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_debitcrumil")
	public Integer getOuvragebarDebitcrumil() {
		if (ouvragebarDebitcrumil == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_debitcrumil - Valeur 'Null' non permise.");
		}
		return ouvragebarDebitcrumil;
	}

	public void setOuvragebarDebitcrumil(Integer ouvragebarDebitcrumil) {
		this.ouvragebarDebitcrumil = ouvragebarDebitcrumil;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_dernierevidange")
	public Integer getOuvragebarDernierevidange() {
		if (ouvragebarDernierevidange == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_dernierevidange - Valeur 'Null' non permise.");
		}
		return ouvragebarDernierevidange;
	}

	public void setOuvragebarDernierevidange(Integer ouvragebarDernierevidange) {
		this.ouvragebarDernierevidange = ouvragebarDernierevidange;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_irrigation")
	public String getOuvragebarIrrigation() {
		if (ouvragebarIrrigation == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_irrigation - Valeur 'Null' non permise.");
		}
		return ouvragebarIrrigation;
	}

	public void setOuvragebarIrrigation(String ouvragebarIrrigation) {
		this.ouvragebarIrrigation = ouvragebarIrrigation;
	}

	public Integer getOuvragebarIrrigationvolume() {
		return ouvragebarIrrigationvolume;
	}

	public void setOuvragebarIrrigationvolume(Integer ouvragebarIrrigationvolume) {
		this.ouvragebarIrrigationvolume = ouvragebarIrrigationvolume;
	}

	public String getOuvragebarIrrigationtype() {
		return ouvragebarIrrigationtype;
	}

	public void setOuvragebarIrrigationtype(String ouvragebarIrrigationtype) {
		this.ouvragebarIrrigationtype = ouvragebarIrrigationtype;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_prodelec")
	public String getOuvragebarProdelec() {
		if (ouvragebarProdelec == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_prodelec - Valeur 'Null' non permise.");
		}
		return ouvragebarProdelec;
	}

	public void setOuvragebarProdelec(String ouvragebarProdelec) {
		this.ouvragebarProdelec = ouvragebarProdelec;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_soutienetiage")
	public Double getOuvragebarSoutienetiage() {
		if (ouvragebarSoutienetiage == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_soutienetiage - Valeur 'Null' non permise.");
		}
		return ouvragebarSoutienetiage;
	}

	public void setOuvragebarSoutienetiage(Double ouvragebarSoutienetiage) {
		this.ouvragebarSoutienetiage = ouvragebarSoutienetiage;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_cotecrete")
	public Double getOuvragebarCotecrete() {
		if (ouvragebarCotecrete == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_cotecrete - Valeur 'Null' non permise.");
		}
		return ouvragebarCotecrete;
	}

	public void setOuvragebarCotecrete(Double ouvragebarCotecrete) {
		this.ouvragebarCotecrete = ouvragebarCotecrete;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_cotelegale")
	public Double getOuvragebarCotelegale() {
		if (ouvragebarCotelegale == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_cotelegale - Valeur 'Null' non permise.");
		}
		return ouvragebarCotelegale;
	}

	public void setOuvragebarCotelegale(Double ouvragebarCotelegale) {
		this.ouvragebarCotelegale = ouvragebarCotelegale;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_cotephe")
	public Double getOuvragebarCotephe() {
		if (ouvragebarCotephe == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_cotephe - Valeur 'Null' non permise.");
		}
		return ouvragebarCotephe;
	}

	public void setOuvragebarCotephe(Double ouvragebarCotephe) {
		this.ouvragebarCotephe = ouvragebarCotephe;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_cotehiver")
	public Double getOuvragebarCotehiver() {
		if (ouvragebarCotehiver == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_cotehiver - Valeur 'Null' non permise.");
		}
		return ouvragebarCotehiver;
	}

	public void setOuvragebarCotehiver(Double ouvragebarCotehiver) {
		this.ouvragebarCotehiver = ouvragebarCotehiver;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_debitreserve")
	public Double getOuvragebarDebitreserve() {
		if (ouvragebarDebitreserve == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_debitreserve - Valeur 'Null' non permise.");
		}
		return ouvragebarDebitreserve;
	}

	public void setOuvragebarDebitreserve(Double ouvragebarDebitreserve) {
		this.ouvragebarDebitreserve = ouvragebarDebitreserve;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_qmaxevacuation")
	public Double getOuvragebarQmaxevacuation() {
		if (ouvragebarQmaxevacuation == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_qmaxevacuation - Valeur 'Null' non permise.");
		}
		return ouvragebarQmaxevacuation;
	}

	public void setOuvragebarQmaxevacuation(Double ouvragebarQmaxevacuation) {
		this.ouvragebarQmaxevacuation = ouvragebarQmaxevacuation;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_prebarrage")
	public String getOuvragebarPrebarrage() {
		if (ouvragebarPrebarrage == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_prebarrage - Valeur 'Null' non permise.");
		}
		return ouvragebarPrebarrage;
	}

	public void setOuvragebarPrebarrage(String ouvragebarPrebarrage) {
		this.ouvragebarPrebarrage = ouvragebarPrebarrage;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_eutrophisation")
	public String getOuvragebarEutrophisation() {
		if (ouvragebarEutrophisation == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_eutrophisation - Valeur 'Null' non permise.");
		}
		return ouvragebarEutrophisation;
	}

	public void setOuvragebarEutrophisation(String ouvragebarEutrophisation) {
		this.ouvragebarEutrophisation = ouvragebarEutrophisation;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_classe")
	public String getOuvragebarClasse() {
		if (ouvragebarClasse == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_classe - Valeur 'Null' non permise.");
		}
		return ouvragebarClasse;
	}

	public void setOuvragebarClasse(String ouvragebarClasse) {
		this.ouvragebarClasse = ouvragebarClasse;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_pj1")
	public String getOuvragebarPj1() {
		if (ouvragebarPj1 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_pj1 - Valeur 'Null' non permise.");
		}
		return ouvragebarPj1;
	}

	public void setOuvragebarPj1(String ouvragebarPj1) {
		this.ouvragebarPj1 = ouvragebarPj1;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_pj2")
	public String getOuvragebarPj2() {
		if (ouvragebarPj2 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_pj2 - Valeur 'Null' non permise.");
		}
		return ouvragebarPj2;
	}

	public void setOuvragebarPj2(String ouvragebarPj2) {
		this.ouvragebarPj2 = ouvragebarPj2;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_pj3")
	public String getOuvragebarPj3() {
		if (ouvragebarPj3 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_pj3 - Valeur 'Null' non permise.");
		}
		return ouvragebarPj3;
	}

	public void setOuvragebarPj3(String ouvragebarPj3) {
		this.ouvragebarPj3 = ouvragebarPj3;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_pj4")
	public String getOuvragebarPj4() {
		if (ouvragebarPj4 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_pj4 - Valeur 'Null' non permise.");
		}
		return ouvragebarPj4;
	}

	public void setOuvragebarPj4(String ouvragebarPj4) {
		this.ouvragebarPj4 = ouvragebarPj4;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_pj5")
	public String getOuvragebarPj5() {
		if (ouvragebarPj5 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_pj5 - Valeur 'Null' non permise.");
		}
		return ouvragebarPj5;
	}

	public void setOuvragebarPj5(String ouvragebarPj5) {
		this.ouvragebarPj5 = ouvragebarPj5;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebar_pj6")
	public String getOuvragebarPj6() {
		if (ouvragebarPj6 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAR + " - Champs ouvragebar_pj6 - Valeur 'Null' non permise.");
		}
		return ouvragebarPj6;
	}

	public void setOuvragebarPj6(String ouvragebarPj6) {
		this.ouvragebarPj6 = ouvragebarPj6;
	}

	public String getOuvragebarPj7() {
		return ouvragebarPj7;
	}

	public void setOuvragebarPj7(String ouvragebarPj7) {
		this.ouvragebarPj7 = ouvragebarPj7;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
}
