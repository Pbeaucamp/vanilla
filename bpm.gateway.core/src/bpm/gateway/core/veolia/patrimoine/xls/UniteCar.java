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
@Table (name = PatrimoineDAO.UNITE_CAR)
public class UniteCar {

	@Id
	@Column(name = "unitecar_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String unitecarUnitesocleId;

	//TODO: Liste
	@Transient
	private String unitecarProcede;
	
	@Column(name = "unitecar_materiaufiltrant")
	private String unitecarMateriaufiltrant;
	
	@Column(name = "unitecar_granulo")
	private String unitecarGranulo;
	
	@Column(name = "unitecar_nbfiltre")
	private Double unitecarNbfiltre;
	
	@Column(name = "unitecar_surfacefiltre")
	private Double unitecarSurfacefiltre;
	
	@Column(name = "unitecar_dimensionfiltre")
	private Double unitecarDimensionfiltre;
	
	@Column(name = "unitecar_debitfiltre")
	private Double unitecarDebitfiltre;
	
	@Column(name = "unitecar_volumefiltre")
	private Double unitecarVolumefiltre;
	
	@Column(name = "unitecar_vitessefiltration")
	private Double unitecarVitessefiltration;

	//TODO: Liste
	@Column(name = "unitecar_bicouche")
	private String unitecarBicouche;
	
	@Column(name = "unitecar_hauteursable")
	private Double unitecarHauteursable;
	
	@Column(name = "unitecar_hauteurmatcatalytique")
	private Double unitecarHauteurmatcatalytique;
	
	@Column(name = "unitecar_typematcatalytique")
	private String unitecarTypematcatalytique;
	
	@Column(name = "unitecar_nbpompelavageeau")
	private Double unitecarNbpompelavageeau;
	
	@Column(name = "unitecar_debitpompelavageeau")
	private Double unitecarDebitpompelavageeau;

	//TODO: Liste
	@Column(name = "unitecar_secourspompelavageeau")
	private String unitecarSecourspompelavageeau;
	
	@Column(name = "unitecar_nbsurpresseurlavageair")
	private Double unitecarNbsurpresseurlavageair;
	
	@Column(name = "unitecar_debitsurpresseurlavageair")
	private Double unitecarDebitsurpresseurlavageair;

	//TODO: Liste
	@Column(name = "unitecar_secourssurpresseurlavageair")
	private String unitecarSecourssurpresseurlavageair;
	
	@Column(name = "unitecar_nbcrepine")
	private Double unitecarNbcrepine;

	//TODO: Liste
	@Column(name = "unitecar_typecrepine")
	private String unitecarTypecrepine;

	//TODO: Liste
	@Column(name = "unitecar_reserveeaufiltree")
	private String unitecarReserveeaufiltree;
	
	@Column(name = "unitecar_tpssejour")
	private Double unitecarTpssejour;
	
	@Column(name = "unitecar_nbcompartiment")
	private Double unitecarNbcompartiment;
	
	@Column(name = "unitecar_typemateriau")
	private String unitecarTypemateriau;
	
	@Column(name = "unitecar_surfacemodule")
	private Double unitecarSurfacemodule;

	//TODO: Liste
	@Column(name = "unitecar_typeextraction")
	private String unitecarTypeextraction;

	//TODO: Liste
	@Transient
	private String unitecarContactcap;
	
	@Column(name = "unitecar_contactcapprocede")
	private String unitecarContactcapprocede;
	
	@Column(name = "unitecar_contactcapdescripouv")
	private String unitecarContactcapdescripouv;

	//TODO: Liste
	@Column(name = "unitecar_contactcapassodecan")
	private String unitecarContactcapassodecan;
	
	@Column(name = "unitecar_decandescrip")
	private String unitecarDecandescrip;
	
	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecar_unitesocle_id")
	public String getUnitecarUnitesocleId() {
		if (unitecarUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CAR + " - Champs unitecar_unitesocle_id - Valeur 'Null' non permise.");
		}
		return unitecarUnitesocleId;
	}

	public void setUnitecarUnitesocleId(String unitecarUnitesocleId) {
		this.unitecarUnitesocleId = unitecarUnitesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecar_procede")
	public String getUnitecarProcede() {
		if (unitecarProcede == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CAR + " - Champs unitecar_procede - Valeur 'Null' non permise.");
		}
		return unitecarProcede;
	}

	public void setUnitecarProcede(String unitecarProcede) {
		this.unitecarProcede = unitecarProcede;
	}

	public String getUnitecarMateriaufiltrant() {
		return unitecarMateriaufiltrant;
	}

	public void setUnitecarMateriaufiltrant(String unitecarMateriaufiltrant) {
		this.unitecarMateriaufiltrant = unitecarMateriaufiltrant;
	}

	public String getUnitecarGranulo() {
		return unitecarGranulo;
	}

	public void setUnitecarGranulo(String unitecarGranulo) {
		this.unitecarGranulo = unitecarGranulo;
	}

	public Double getUnitecarNbfiltre() {
		return unitecarNbfiltre;
	}

	public void setUnitecarNbfiltre(Double unitecarNbfiltre) {
		this.unitecarNbfiltre = unitecarNbfiltre;
	}

	public Double getUnitecarSurfacefiltre() {
		return unitecarSurfacefiltre;
	}

	public void setUnitecarSurfacefiltre(Double unitecarSurfacefiltre) {
		this.unitecarSurfacefiltre = unitecarSurfacefiltre;
	}

	public Double getUnitecarDimensionfiltre() {
		return unitecarDimensionfiltre;
	}

	public void setUnitecarDimensionfiltre(Double unitecarDimensionfiltre) {
		this.unitecarDimensionfiltre = unitecarDimensionfiltre;
	}

	public Double getUnitecarDebitfiltre() {
		return unitecarDebitfiltre;
	}

	public void setUnitecarDebitfiltre(Double unitecarDebitfiltre) {
		this.unitecarDebitfiltre = unitecarDebitfiltre;
	}

	public Double getUnitecarVolumefiltre() {
		return unitecarVolumefiltre;
	}

	public void setUnitecarVolumefiltre(Double unitecarVolumefiltre) {
		this.unitecarVolumefiltre = unitecarVolumefiltre;
	}

	public Double getUnitecarVitessefiltration() {
		return unitecarVitessefiltration;
	}

	public void setUnitecarVitessefiltration(Double unitecarVitessefiltration) {
		this.unitecarVitessefiltration = unitecarVitessefiltration;
	}

	public String getUnitecarBicouche() {
		return unitecarBicouche;
	}

	public void setUnitecarBicouche(String unitecarBicouche) {
		this.unitecarBicouche = unitecarBicouche;
	}

	public Double getUnitecarHauteursable() {
		return unitecarHauteursable;
	}

	public void setUnitecarHauteursable(Double unitecarHauteursable) {
		this.unitecarHauteursable = unitecarHauteursable;
	}

	public Double getUnitecarHauteurmatcatalytique() {
		return unitecarHauteurmatcatalytique;
	}

	public void setUnitecarHauteurmatcatalytique(Double unitecarHauteurmatcatalytique) {
		this.unitecarHauteurmatcatalytique = unitecarHauteurmatcatalytique;
	}

	public String getUnitecarTypematcatalytique() {
		return unitecarTypematcatalytique;
	}

	public void setUnitecarTypematcatalytique(String unitecarTypematcatalytique) {
		this.unitecarTypematcatalytique = unitecarTypematcatalytique;
	}

	public Double getUnitecarNbpompelavageeau() {
		return unitecarNbpompelavageeau;
	}

	public void setUnitecarNbpompelavageeau(Double unitecarNbpompelavageeau) {
		this.unitecarNbpompelavageeau = unitecarNbpompelavageeau;
	}

	public Double getUnitecarDebitpompelavageeau() {
		return unitecarDebitpompelavageeau;
	}

	public void setUnitecarDebitpompelavageeau(Double unitecarDebitpompelavageeau) {
		this.unitecarDebitpompelavageeau = unitecarDebitpompelavageeau;
	}

	public String getUnitecarSecourspompelavageeau() {
		return unitecarSecourspompelavageeau;
	}

	public void setUnitecarSecourspompelavageeau(String unitecarSecourspompelavageeau) {
		this.unitecarSecourspompelavageeau = unitecarSecourspompelavageeau;
	}

	public Double getUnitecarNbsurpresseurlavageair() {
		return unitecarNbsurpresseurlavageair;
	}

	public void setUnitecarNbsurpresseurlavageair(Double unitecarNbsurpresseurlavageair) {
		this.unitecarNbsurpresseurlavageair = unitecarNbsurpresseurlavageair;
	}

	public Double getUnitecarDebitsurpresseurlavageair() {
		return unitecarDebitsurpresseurlavageair;
	}

	public void setUnitecarDebitsurpresseurlavageair(Double unitecarDebitsurpresseurlavageair) {
		this.unitecarDebitsurpresseurlavageair = unitecarDebitsurpresseurlavageair;
	}

	public String getUnitecarSecourssurpresseurlavageair() {
		return unitecarSecourssurpresseurlavageair;
	}

	public void setUnitecarSecourssurpresseurlavageair(String unitecarSecourssurpresseurlavageair) {
		this.unitecarSecourssurpresseurlavageair = unitecarSecourssurpresseurlavageair;
	}

	public Double getUnitecarNbcrepine() {
		return unitecarNbcrepine;
	}

	public void setUnitecarNbcrepine(Double unitecarNbcrepine) {
		this.unitecarNbcrepine = unitecarNbcrepine;
	}

	public String getUnitecarTypecrepine() {
		return unitecarTypecrepine;
	}

	public void setUnitecarTypecrepine(String unitecarTypecrepine) {
		this.unitecarTypecrepine = unitecarTypecrepine;
	}

	public String getUnitecarReserveeaufiltree() {
		return unitecarReserveeaufiltree;
	}

	public void setUnitecarReserveeaufiltree(String unitecarReserveeaufiltree) {
		this.unitecarReserveeaufiltree = unitecarReserveeaufiltree;
	}

	public Double getUnitecarTpssejour() {
		return unitecarTpssejour;
	}

	public void setUnitecarTpssejour(Double unitecarTpssejour) {
		this.unitecarTpssejour = unitecarTpssejour;
	}

	public Double getUnitecarNbcompartiment() {
		return unitecarNbcompartiment;
	}

	public void setUnitecarNbcompartiment(Double unitecarNbcompartiment) {
		this.unitecarNbcompartiment = unitecarNbcompartiment;
	}

	public String getUnitecarTypemateriau() {
		return unitecarTypemateriau;
	}

	public void setUnitecarTypemateriau(String unitecarTypemateriau) {
		this.unitecarTypemateriau = unitecarTypemateriau;
	}

	public Double getUnitecarSurfacemodule() {
		return unitecarSurfacemodule;
	}

	public void setUnitecarSurfacemodule(Double unitecarSurfacemodule) {
		this.unitecarSurfacemodule = unitecarSurfacemodule;
	}

	public String getUnitecarTypeextraction() {
		return unitecarTypeextraction;
	}

	public void setUnitecarTypeextraction(String unitecarTypeextraction) {
		this.unitecarTypeextraction = unitecarTypeextraction;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecar_contactcap")
	public String getUnitecarContactcap() {
		if (unitecarContactcap == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CAR + " - Champs unitecar_contactcap - Valeur 'Null' non permise.");
		}
		return unitecarContactcap;
	}

	public void setUnitecarContactcap(String unitecarContactcap) {
		this.unitecarContactcap = unitecarContactcap;
	}

	public String getUnitecarContactcapprocede() {
		return unitecarContactcapprocede;
	}

	public void setUnitecarContactcapprocede(String unitecarContactcapprocede) {
		this.unitecarContactcapprocede = unitecarContactcapprocede;
	}

	public String getUnitecarContactcapdescripouv() {
		return unitecarContactcapdescripouv;
	}

	public void setUnitecarContactcapdescripouv(String unitecarContactcapdescripouv) {
		this.unitecarContactcapdescripouv = unitecarContactcapdescripouv;
	}

	public String getUnitecarContactcapassodecan() {
		return unitecarContactcapassodecan;
	}

	public void setUnitecarContactcapassodecan(String unitecarContactcapassodecan) {
		this.unitecarContactcapassodecan = unitecarContactcapassodecan;
	}

	public String getUnitecarDecandescrip() {
		return unitecarDecandescrip;
	}

	public void setUnitecarDecandescrip(String unitecarDecandescrip) {
		this.unitecarDecandescrip = unitecarDecandescrip;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
	
	
}
