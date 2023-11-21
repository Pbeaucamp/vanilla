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
@Table (name = PatrimoineDAO.OUVRAGE_PBA)
public class OuvragePba {

	@Id
	@Column(name = "ouvragepba_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String ouvragepbaOuvragesocleId;

	@Transient
	private Double ouvragepbaVolumestocke;

	@Transient
	private String ouvragepbaEpoqueconstruction;

	@Transient
	private Integer ouvragepbaDatemes;

	//TODO: Liste
	@Transient
	private String ouvragepbaTypeprebarrage;

	@Transient
	private String ouvragepbaTypeprebarragedetail;

	@Transient
	private String ouvragepbaFruitparement;

	@Transient
	private Double ouvragepbaHauteurtn;

	@Transient
	private Double ouvragepbaHauteurfondation;

	@Transient
	private Double ouvragepbaLongueurcrete;

	@Column(name = "ouvragepba_largeurcrete")
	private Double ouvragepbaLargeurcrete;

	@Transient
	private Double ouvragepbaSurfaceplaneau;

	@Transient
	private Double ouvragepbaCapaciteretenue;

	@Transient
	private Double ouvragepbaSuperficiebv;

	@Transient
	private Date ouvragepbaDernierevidange;

	@Transient
	private Double ouvragepbaCotecrete;

	@Transient
	private Double ouvragepbaCotelegale;

	@Transient
	private Double ouvragepbaCotephe;

	@Transient
	private Double ouvragepbaCotehiver;

	@Transient
	private Double ouvragepbaDebitreserve;

	@Transient
	private Double ouvragepbaQmaxevacuation;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_ouvragesocle_id")
	public String getOuvragepbaOuvragesocleId() {
		if (ouvragepbaOuvragesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_ouvragesocle_id - Valeur 'Null' non permise.");
		}
		return ouvragepbaOuvragesocleId;
	}

	public void setOuvragepbaOuvragesocleId(String ouvragepbaOuvragesocleId) {
		this.ouvragepbaOuvragesocleId = ouvragepbaOuvragesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_volumestocke")
	public Double getOuvragepbaVolumestocke() {
		if (ouvragepbaVolumestocke == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_volumestocke - Valeur 'Null' non permise.");
		}
		return ouvragepbaVolumestocke;
	}

	public void setOuvragepbaVolumestocke(Double ouvragepbaVolumestocke) {
		this.ouvragepbaVolumestocke = ouvragepbaVolumestocke;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_epoqueconstruction")
	public String getOuvragepbaEpoqueconstruction() {
		if (ouvragepbaEpoqueconstruction == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_epoqueconstruction - Valeur 'Null' non permise.");
		}
		return ouvragepbaEpoqueconstruction;
	}

	public void setOuvragepbaEpoqueconstruction(String ouvragepbaEpoqueconstruction) {
		this.ouvragepbaEpoqueconstruction = ouvragepbaEpoqueconstruction;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_datemes")
	public Integer getOuvragepbaDatemes() {
		if (ouvragepbaDatemes == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_datemes - Valeur 'Null' non permise.");
		}
		return ouvragepbaDatemes;
	}

	public void setOuvragepbaDatemes(Integer ouvragepbaDatemes) {
		this.ouvragepbaDatemes = ouvragepbaDatemes;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_typeprebarrage")
	public String getOuvragepbaTypeprebarrage() {
		if (ouvragepbaTypeprebarrage == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_typeprebarrage - Valeur 'Null' non permise.");
		}
		return ouvragepbaTypeprebarrage;
	}

	public void setOuvragepbaTypeprebarrage(String ouvragepbaTypeprebarrage) {
		this.ouvragepbaTypeprebarrage = ouvragepbaTypeprebarrage;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_typeprebarragedetail")
	public String getOuvragepbaTypeprebarragedetail() {
		if (ouvragepbaTypeprebarragedetail == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_typeprebarragedetail - Valeur 'Null' non permise.");
		}
		return ouvragepbaTypeprebarragedetail;
	}

	public void setOuvragepbaTypeprebarragedetail(String ouvragepbaTypeprebarragedetail) {
		this.ouvragepbaTypeprebarragedetail = ouvragepbaTypeprebarragedetail;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_fruitparement")
	public String getOuvragepbaFruitparement() {
		if (ouvragepbaFruitparement == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_fruitparement - Valeur 'Null' non permise.");
		}
		return ouvragepbaFruitparement;
	}

	public void setOuvragepbaFruitparement(String ouvragepbaFruitparement) {
		this.ouvragepbaFruitparement = ouvragepbaFruitparement;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_hauteurtn")
	public Double getOuvragepbaHauteurtn() {
		if (ouvragepbaHauteurtn == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_hauteurtn - Valeur 'Null' non permise.");
		}
		return ouvragepbaHauteurtn;
	}

	public void setOuvragepbaHauteurtn(Double ouvragepbaHauteurtn) {
		this.ouvragepbaHauteurtn = ouvragepbaHauteurtn;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_hauteurfondation")
	public Double getOuvragepbaHauteurfondation() {
		if (ouvragepbaHauteurfondation == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_hauteurfondation - Valeur 'Null' non permise.");
		}
		return ouvragepbaHauteurfondation;
	}

	public void setOuvragepbaHauteurfondation(Double ouvragepbaHauteurfondation) {
		this.ouvragepbaHauteurfondation = ouvragepbaHauteurfondation;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_longueurcrete")
	public Double getOuvragepbaLongueurcrete() {
		if (ouvragepbaLongueurcrete == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_longueurcrete - Valeur 'Null' non permise.");
		}
		return ouvragepbaLongueurcrete;
	}

	public void setOuvragepbaLongueurcrete(Double ouvragepbaLongueurcrete) {
		this.ouvragepbaLongueurcrete = ouvragepbaLongueurcrete;
	}
	
	public Double getOuvragepbaLargeurcrete() {
		return ouvragepbaLargeurcrete;
	}
	
	public void setOuvragepbaLargeurcrete(Double ouvragepbaLargeurcrete) {
		this.ouvragepbaLargeurcrete = ouvragepbaLargeurcrete;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_surfaceplaneau")
	public Double getOuvragepbaSurfaceplaneau() {
		if (ouvragepbaSurfaceplaneau == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_surfaceplaneau - Valeur 'Null' non permise.");
		}
		return ouvragepbaSurfaceplaneau;
	}

	public void setOuvragepbaSurfaceplaneau(Double ouvragepbaSurfaceplaneau) {
		this.ouvragepbaSurfaceplaneau = ouvragepbaSurfaceplaneau;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_capaciteretenue")
	public Double getOuvragepbaCapaciteretenue() {
		if (ouvragepbaCapaciteretenue == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_capaciteretenue - Valeur 'Null' non permise.");
		}
		return ouvragepbaCapaciteretenue;
	}

	public void setOuvragepbaCapaciteretenue(Double ouvragepbaCapaciteretenue) {
		this.ouvragepbaCapaciteretenue = ouvragepbaCapaciteretenue;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_superficiebv")
	public Double getOuvragepbaSuperficiebv() {
		if (ouvragepbaSuperficiebv == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_superficiebv - Valeur 'Null' non permise.");
		}
		return ouvragepbaSuperficiebv;
	}

	public void setOuvragepbaSuperficiebv(Double ouvragepbaSuperficiebv) {
		this.ouvragepbaSuperficiebv = ouvragepbaSuperficiebv;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_dernierevidange")
	public Date getOuvragepbaDernierevidange() {
		if (ouvragepbaDernierevidange == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_dernierevidange - Valeur 'Null' non permise.");
		}
		return ouvragepbaDernierevidange;
	}

	public void setOuvragepbaDernierevidange(Date ouvragepbaDernierevidange) {
		this.ouvragepbaDernierevidange = ouvragepbaDernierevidange;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_cotecrete")
	public Double getOuvragepbaCotecrete() {
		if (ouvragepbaCotecrete == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_cotecrete - Valeur 'Null' non permise.");
		}
		return ouvragepbaCotecrete;
	}

	public void setOuvragepbaCotecrete(Double ouvragepbaCotecrete) {
		this.ouvragepbaCotecrete = ouvragepbaCotecrete;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_cotelegale")
	public Double getOuvragepbaCotelegale() {
		if (ouvragepbaCotelegale == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_cotelegale - Valeur 'Null' non permise.");
		}
		return ouvragepbaCotelegale;
	}

	public void setOuvragepbaCotelegale(Double ouvragepbaCotelegale) {
		this.ouvragepbaCotelegale = ouvragepbaCotelegale;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_cotephe")
	public Double getOuvragepbaCotephe() {
		if (ouvragepbaCotephe == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_cotephe - Valeur 'Null' non permise.");
		}
		return ouvragepbaCotephe;
	}

	public void setOuvragepbaCotephe(Double ouvragepbaCotephe) {
		this.ouvragepbaCotephe = ouvragepbaCotephe;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_cotehiver")
	public Double getOuvragepbaCotehiver() {
		if (ouvragepbaCotehiver == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_cotehiver - Valeur 'Null' non permise.");
		}
		return ouvragepbaCotehiver;
	}

	public void setOuvragepbaCotehiver(Double ouvragepbaCotehiver) {
		this.ouvragepbaCotehiver = ouvragepbaCotehiver;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_debitreserve")
	public Double getOuvragepbaDebitreserve() {
		if (ouvragepbaDebitreserve == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_debitreserve - Valeur 'Null' non permise.");
		}
		return ouvragepbaDebitreserve;
	}

	public void setOuvragepbaDebitreserve(Double ouvragepbaDebitreserve) {
		this.ouvragepbaDebitreserve = ouvragepbaDebitreserve;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepba_qmaxevacuation")
	public Double getOuvragepbaQmaxevacuation() {
		if (ouvragepbaQmaxevacuation == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PBA + " - Champs ouvragepba_qmaxevacuation - Valeur 'Null' non permise.");
		}
		return ouvragepbaQmaxevacuation;
	}

	public void setOuvragepbaQmaxevacuation(Double ouvragepbaQmaxevacuation) {
		this.ouvragepbaQmaxevacuation = ouvragepbaQmaxevacuation;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
}
