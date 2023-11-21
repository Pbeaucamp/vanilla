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
@Table (name = PatrimoineDAO.UNITE_FIL)
public class UniteFil {

	@Id
	@Column(name = "unitefil_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String unitefilUnitesocleId;

	@Transient
	private String unitefilMateriaufiltrant;

	@Transient
	private String unitefilGranulo;

	@Transient
	private Double unitefilNbfiltre;

	@Transient
	private Double unitefilSurfacefiltre;

	@Transient
	private String unitefilDimensionfiltre;

	@Transient
	private Double unitefilDebitfiltre;

	@Transient
	private Double unitefilVolumefiltre;

	@Transient
	private Double unitefilVitessefiltration;

	//TODO: Liste
	@Transient
	private String unitefilBicouche;
	
	@Column(name = "unitefil_hauteursable")
	private Double unitefilHauteursable;

	@Column(name = "unitefil_hauteurmatcatalytique")
	private Double unitefilHauteurmatcatalytique;

	@Transient
	private Double unitefilNbpompelavageeau;

	@Transient
	private Double unitefilDebitpompelavageeau;

	//TODO: Liste
	@Transient
	private String unitefilSecourspompelavageeau;

	@Transient
	private Double unitefilNnbsurpresseurlavageair;

	@Transient
	private Double unitefilDebitsurpresseurlavageair;

	//TODO: Liste
	@Transient
	private String unitefilSecourssurpresseurlavageair;

	@Transient
	private Double unitefilNbcrepine;

	//TODO: Liste
	@Transient
	private String unitefil_typecrepine;

	//TODO: Liste
	@Transient
	private String unitefilReserveeaufiltree;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_unitesocle_id")
	public String getUnitefilUnitesocleId() {
		if (unitefilUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_unitesocle_id - Valeur 'Null' non permise.");
		}
		return unitefilUnitesocleId;
	}

	public void setUnitefilUnitesocleId(String unitefilUnitesocleId) {
		this.unitefilUnitesocleId = unitefilUnitesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_materiaufiltrant")
	public String getUnitefilMateriaufiltrant() {
		if (unitefilMateriaufiltrant == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_materiaufiltrant - Valeur 'Null' non permise.");
		}
		return unitefilMateriaufiltrant;
	}

	public void setUnitefilMateriaufiltrant(String unitefilMateriaufiltrant) {
		this.unitefilMateriaufiltrant = unitefilMateriaufiltrant;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_granulo")
	public String getUnitefilGranulo() {
		if (unitefilGranulo == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_granulo - Valeur 'Null' non permise.");
		}
		return unitefilGranulo;
	}

	public void setUnitefilGranulo(String unitefilGranulo) {
		this.unitefilGranulo = unitefilGranulo;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_nbfiltre")
	public Double getUnitefilNbfiltre() {
		if (unitefilNbfiltre == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_nbfiltre - Valeur 'Null' non permise.");
		}
		return unitefilNbfiltre;
	}

	public void setUnitefilNbfiltre(Double unitefilNbfiltre) {
		this.unitefilNbfiltre = unitefilNbfiltre;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_surfacefiltre")
	public Double getUnitefilSurfacefiltre() {
		if (unitefilSurfacefiltre == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_surfacefiltre - Valeur 'Null' non permise.");
		}
		return unitefilSurfacefiltre;
	}

	public void setUnitefilSurfacefiltre(Double unitefilSurfacefiltre) {
		this.unitefilSurfacefiltre = unitefilSurfacefiltre;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_dimensionfiltre")
	public String getUnitefilDimensionfiltre() {
		if (unitefilDimensionfiltre == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_dimensionfiltre - Valeur 'Null' non permise.");
		}
		return unitefilDimensionfiltre;
	}

	public void setUnitefilDimensionfiltre(String unitefilDimensionfiltre) {
		this.unitefilDimensionfiltre = unitefilDimensionfiltre;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_debitfiltre")
	public Double getUnitefilDebitfiltre() {
		if (unitefilDebitfiltre == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_debitfiltre - Valeur 'Null' non permise.");
		}
		return unitefilDebitfiltre;
	}

	public void setUnitefilDebitfiltre(Double unitefilDebitfiltre) {
		this.unitefilDebitfiltre = unitefilDebitfiltre;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_volumefiltre")
	public Double getUnitefilVolumefiltre() {
		if (unitefilVolumefiltre == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_volumefiltre - Valeur 'Null' non permise.");
		}
		return unitefilVolumefiltre;
	}

	public void setUnitefilVolumefiltre(Double unitefilVolumefiltre) {
		this.unitefilVolumefiltre = unitefilVolumefiltre;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_vitessefiltration")
	public Double getUnitefilVitessefiltration() {
		if (unitefilVitessefiltration == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_vitessefiltration - Valeur 'Null' non permise.");
		}
		return unitefilVitessefiltration;
	}

	public void setUnitefilVitessefiltration(Double unitefilVitessefiltration) {
		this.unitefilVitessefiltration = unitefilVitessefiltration;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_bicouche")
	public String getUnitefilBicouche() {
		if (unitefilBicouche == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_bicouche - Valeur 'Null' non permise.");
		}
		return unitefilBicouche;
	}

	public void setUnitefilBicouche(String unitefilBicouche) {
		this.unitefilBicouche = unitefilBicouche;
	}

	public Double getUnitefilHauteursable() {
		return unitefilHauteursable;
	}

	public void setUnitefilHauteursable(Double unitefilHauteursable) {
		this.unitefilHauteursable = unitefilHauteursable;
	}

	public Double getUnitefilHauteurmatcatalytique() {
		return unitefilHauteurmatcatalytique;
	}

	public void setUnitefilHauteurmatcatalytique(Double unitefilHauteurmatcatalytique) {
		this.unitefilHauteurmatcatalytique = unitefilHauteurmatcatalytique;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_nbpompelavageeau")
	public Double getUnitefilNbpompelavageeau() {
		if (unitefilNbpompelavageeau == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_nbpompelavageeau - Valeur 'Null' non permise.");
		}
		return unitefilNbpompelavageeau;
	}

	public void setUnitefilNbpompelavageeau(Double unitefilNbpompelavageeau) {
		this.unitefilNbpompelavageeau = unitefilNbpompelavageeau;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_debitpompelavageeau")
	public Double getUnitefilDebitpompelavageeau() {
		if (unitefilDebitpompelavageeau == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_debitpompelavageeau - Valeur 'Null' non permise.");
		}
		return unitefilDebitpompelavageeau;
	}

	public void setUnitefilDebitpompelavageeau(Double unitefilDebitpompelavageeau) {
		this.unitefilDebitpompelavageeau = unitefilDebitpompelavageeau;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_secourspompelavageeau")
	public String getUnitefilSecourspompelavageeau() {
		if (unitefilSecourspompelavageeau == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_secourspompelavageeau - Valeur 'Null' non permise.");
		}
		return unitefilSecourspompelavageeau;
	}

	public void setUnitefilSecourspompelavageeau(String unitefilSecourspompelavageeau) {
		this.unitefilSecourspompelavageeau = unitefilSecourspompelavageeau;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_nbsurpresseurlavageair")
	public Double getUnitefilNnbsurpresseurlavageair() {
		if (unitefilNnbsurpresseurlavageair == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_nbsurpresseurlavageair - Valeur 'Null' non permise.");
		}
		return unitefilNnbsurpresseurlavageair;
	}

	public void setUnitefilNnbsurpresseurlavageair(Double unitefilNnbsurpresseurlavageair) {
		this.unitefilNnbsurpresseurlavageair = unitefilNnbsurpresseurlavageair;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_debitsurpresseurlavageair")
	public Double getUnitefilDebitsurpresseurlavageair() {
		if (unitefilDebitsurpresseurlavageair == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_debitsurpresseurlavageair - Valeur 'Null' non permise.");
		}
		return unitefilDebitsurpresseurlavageair;
	}

	public void setUnitefilDebitsurpresseurlavageair(Double unitefilDebitsurpresseurlavageair) {
		this.unitefilDebitsurpresseurlavageair = unitefilDebitsurpresseurlavageair;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_secourssurpresseurlavageair")
	public String getUnitefilSecourssurpresseurlavageair() {
		if (unitefilSecourssurpresseurlavageair == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_secourssurpresseurlavageair - Valeur 'Null' non permise.");
		}
		return unitefilSecourssurpresseurlavageair;
	}

	public void setUnitefilSecourssurpresseurlavageair(String unitefilSecourssurpresseurlavageair) {
		this.unitefilSecourssurpresseurlavageair = unitefilSecourssurpresseurlavageair;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_nbcrepine")
	public Double getUnitefilNbcrepine() {
		if (unitefilNbcrepine == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_nbcrepine - Valeur 'Null' non permise.");
		}
		return unitefilNbcrepine;
	}

	public void setUnitefilNbcrepine(Double unitefilNbcrepine) {
		this.unitefilNbcrepine = unitefilNbcrepine;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_typecrepine")
	public String getUnitefil_typecrepine() {
		if (unitefil_typecrepine == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_typecrepine - Valeur 'Null' non permise.");
		}
		return unitefil_typecrepine;
	}

	public void setUnitefil_typecrepine(String unitefil_typecrepine) {
		this.unitefil_typecrepine = unitefil_typecrepine;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitefil_reserveeaufiltree")
	public String getUnitefilReserveeaufiltree() {
		if (unitefilReserveeaufiltree == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_FIL + " - Champs unitefil_reserveeaufiltree - Valeur 'Null' non permise.");
		}
		return unitefilReserveeaufiltree;
	}

	public void setUnitefilReserveeaufiltree(String unitefilReserveeaufiltree) {
		this.unitefilReserveeaufiltree = unitefilReserveeaufiltree;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

}
