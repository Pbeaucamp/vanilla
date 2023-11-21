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
@Table (name = PatrimoineDAO.UNITE_TLG)
public class UniteTlg {

	@Id
	@Column(name = "unitetlg_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String unitetlgUnitesocleId;

	//TODO: Liste
	@Transient
	private String unitetlgModealimentation;

	//TODO: Liste
	@Transient
	private String unitetlgPositionnement;

	//TODO: Liste
	@Transient
	private String unitetlgAntennedeportee;

	@Transient
	private String unitetlgListingdonneestele;

	//TODO: Liste
	@Column(name = "unitetlg_tarif")
	private String unitetlgTarif;

	@Column(name = "unitetlg_refcontratabt")
	private String unitetlgRefcontratabt;

	@Column(name = "unitetlg_nbpf")
	private Double unitetlgNbpf;

	@Column(name = "unitetlg_refpf1")
	private String unitetlgRefpf1;

	@Column(name = "unitetlg_refpf2")
	private String unitetlgRefpf2;

	@Column(name = "unitetlg_refpf3")
	private String unitetlgRefpf3;

	@Column(name = "unitetlg_refpf4")
	private String unitetlgRefpf4;

	@Column(name = "unitetlg_refpf5")
	private String unitetlgRefpf5;
	
	@Column(name = "unitetlg_photochambre")
	private String unitetlgPhotochambre;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitetlg_unitesocle_id")
	public String getUnitetlgUnitesocleId() {
		if (unitetlgUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_TLG + " - Champs unitetlg_unitesocle_id - Valeur 'Null' non permise.");
		}
		return unitetlgUnitesocleId;
	}

	public void setUnitetlgUnitesocleId(String unitetlgUnitesocleId) {
		this.unitetlgUnitesocleId = unitetlgUnitesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitetlg_modealimentation")
	public String getUnitetlgModealimentation() {
		if (unitetlgModealimentation == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_TLG + " - Champs unitetlg_modealimentation - Valeur 'Null' non permise.");
		}
		return unitetlgModealimentation;
	}

	public void setUnitetlgModealimentation(String unitetlgModealimentation) {
		this.unitetlgModealimentation = unitetlgModealimentation;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitetlg_positionnement")
	public String getUnitetlgPositionnement() {
		if (unitetlgPositionnement == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_TLG + " - Champs unitetlg_positionnement - Valeur 'Null' non permise.");
		}
		return unitetlgPositionnement;
	}

	public void setUnitetlgPositionnement(String unitetlgPositionnement) {
		this.unitetlgPositionnement = unitetlgPositionnement;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitetlg_antennedeportee")
	public String getUnitetlgAntennedeportee() {
		if (unitetlgAntennedeportee == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_TLG + " - Champs unitetlg_antennedeportee - Valeur 'Null' non permise.");
		}
		return unitetlgAntennedeportee;
	}

	public void setUnitetlgAntennedeportee(String unitetlgAntennedeportee) {
		this.unitetlgAntennedeportee = unitetlgAntennedeportee;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitetlg_listingdonneestele")
	public String getUnitetlgListingdonneestele() {
		if (unitetlgListingdonneestele == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_TLG + " - Champs unitetlg_listingdonneestele - Valeur 'Null' non permise.");
		}
		return unitetlgListingdonneestele;
	}

	public void setUnitetlgListingdonneestele(String unitetlgListingdonneestele) {
		this.unitetlgListingdonneestele = unitetlgListingdonneestele;
	}

	public String getUnitetlgTarif() {
		return unitetlgTarif;
	}

	public void setUnitetlgTarif(String unitetlgTarif) {
		this.unitetlgTarif = unitetlgTarif;
	}

	public String getUnitetlgRefcontratabt() {
		return unitetlgRefcontratabt;
	}

	public void setUnitetlgRefcontratabt(String unitetlgRefcontratabt) {
		this.unitetlgRefcontratabt = unitetlgRefcontratabt;
	}

	public Double getUnitetlgNbpf() {
		return unitetlgNbpf;
	}

	public void setUnitetlgNbpf(Double unitetlgNbpf) {
		this.unitetlgNbpf = unitetlgNbpf;
	}

	public String getUnitetlgRefpf1() {
		return unitetlgRefpf1;
	}

	public void setUnitetlgRefpf1(String unitetlgRefpf1) {
		this.unitetlgRefpf1 = unitetlgRefpf1;
	}

	public String getUnitetlgRefpf2() {
		return unitetlgRefpf2;
	}

	public void setUnitetlgRefpf2(String unitetlgRefpf2) {
		this.unitetlgRefpf2 = unitetlgRefpf2;
	}

	public String getUnitetlgRefpf3() {
		return unitetlgRefpf3;
	}

	public void setUnitetlgRefpf3(String unitetlgRefpf3) {
		this.unitetlgRefpf3 = unitetlgRefpf3;
	}

	public String getUnitetlgRefpf4() {
		return unitetlgRefpf4;
	}

	public void setUnitetlgRefpf4(String unitetlgRefpf4) {
		this.unitetlgRefpf4 = unitetlgRefpf4;
	}

	public String getUnitetlgRefpf5() {
		return unitetlgRefpf5;
	}

	public void setUnitetlgRefpf5(String unitetlgRefpf5) {
		this.unitetlgRefpf5 = unitetlgRefpf5;
	}
	
	public String getUnitetlgPhotochambre() {
		return unitetlgPhotochambre;
	}
	
	public void setUnitetlgPhotochambre(String unitetlgPhotochambre) {
		this.unitetlgPhotochambre = unitetlgPhotochambre;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
	
	
}
