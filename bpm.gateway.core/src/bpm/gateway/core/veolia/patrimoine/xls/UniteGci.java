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
@Table (name = PatrimoineDAO.UNITE_GCI)
public class UniteGci {

	@Id
	@Column(name = "unitegci_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String unitegciUnitesocleId;

	//TODO: Liste
	@Transient
	private String unitegciAppreciationetat;

	//TODO: Liste
	@Transient
	private String unitegciEtancheite;

	@Transient
	private Double unitegciVolume;

	@Transient
	private Double unitegciHauteur;

	@Transient
	private Double unitegciLongueur;

	@Transient
	private Double unitegciLargeur;

	@Transient
	private Double unitegciSurface;

	@Transient
	private String unitegciMateriauconstruction;

	@Transient
	private String unitegciMateriauisolationthermique;

	@Transient
	private String unitegciMateriauisolationphonique;

	@Transient
	private String unitegciMateriauetancheite;

	//TODO: Liste
	@Transient
	private String unitegciRezchaussee;

	//TODO: Liste
	@Transient
	private String unitegciNiveau1;

	//TODO: Liste
	@Transient
	private String unitegciNiveau2;

	//TODO: Liste
	@Transient
	private String unitegciSoussol;

	@Transient
	private String unitegciTypetoiture;

	//TODO: Liste
	@Transient
	private String unitegciVidecave;

	@Column(name = "unitegci_diametreint")
	private Double unitegciDiametreint;

	@Column(name = "unitegci_hauteureau")
	private Double unitegciHauteureau;

	@Column(name = "unitegci_concentrationmax")
	private Double unitegciConcentrationmax;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitegci_unitesocle_id")
	public String getUnitegciUnitesocleId() {
		if (unitegciUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_GCI + " - Champs unitegci_unitesocle_id - Valeur 'Null' non permise.");
		}
		return unitegciUnitesocleId;
	}

	public void setUnitegciUnitesocleId(String unitegciUnitesocleId) {
		this.unitegciUnitesocleId = unitegciUnitesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitegci_appreciationetat")
	public String getUnitegciAppreciationetat() {
		if (unitegciAppreciationetat == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_GCI + " - Champs unitegci_appreciationetat - Valeur 'Null' non permise.");
		}
		return unitegciAppreciationetat;
	}

	public void setUnitegciAppreciationetat(String unitegciAppreciationetat) {
		this.unitegciAppreciationetat = unitegciAppreciationetat;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitegci_etancheite")
	public String getUnitegciEtancheite() {
		if (unitegciEtancheite == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_GCI + " - Champs unitegci_etancheite - Valeur 'Null' non permise.");
		}
		return unitegciEtancheite;
	}

	public void setUnitegciEtancheite(String unitegciEtancheite) {
		this.unitegciEtancheite = unitegciEtancheite;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitegci_volume")
	public Double getUnitegciVolume() {
		if (unitegciVolume == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_GCI + " - Champs unitegci_volume - Valeur 'Null' non permise.");
		}
		return unitegciVolume;
	}

	public void setUnitegciVolume(Double unitegciVolume) {
		this.unitegciVolume = unitegciVolume;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitegci_hauteur")
	public Double getUnitegciHauteur() {
		if (unitegciHauteur == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_GCI + " - Champs unitegci_hauteur - Valeur 'Null' non permise.");
		}
		return unitegciHauteur;
	}

	public void setUnitegciHauteur(Double unitegciHauteur) {
		this.unitegciHauteur = unitegciHauteur;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitegci_longueur")
	public Double getUnitegciLongueur() {
		if (unitegciLongueur == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_GCI + " - Champs unitegci_longueur - Valeur 'Null' non permise.");
		}
		return unitegciLongueur;
	}

	public void setUnitegciLongueur(Double unitegciLongueur) {
		this.unitegciLongueur = unitegciLongueur;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitegci_largeur")
	public Double getUnitegciLargeur() {
		if (unitegciLargeur == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_GCI + " - Champs unitegci_largeur - Valeur 'Null' non permise.");
		}
		return unitegciLargeur;
	}

	public void setUnitegciLargeur(Double unitegciLargeur) {
		this.unitegciLargeur = unitegciLargeur;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitegci_surface")
	public Double getUnitegciSurface() {
		if (unitegciSurface == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_GCI + " - Champs unitegci_surface - Valeur 'Null' non permise.");
		}
		return unitegciSurface;
	}

	public void setUnitegciSurface(Double unitegciSurface) {
		this.unitegciSurface = unitegciSurface;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitegci_materiauconstruction")
	public String getUnitegciMateriauconstruction() {
		if (unitegciMateriauconstruction == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_GCI + " - Champs unitegci_materiauconstruction - Valeur 'Null' non permise.");
		}
		return unitegciMateriauconstruction;
	}

	public void setUnitegciMateriauconstruction(String unitegciMateriauconstruction) {
		this.unitegciMateriauconstruction = unitegciMateriauconstruction;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitegci_materiauisolationthermique")
	public String getUnitegciMateriauisolationthermique() {
		if (unitegciMateriauisolationthermique == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_GCI + " - Champs unitegci_materiauisolationthermique - Valeur 'Null' non permise.");
		}
		return unitegciMateriauisolationthermique;
	}

	public void setUnitegciMateriauisolationthermique(String unitegciMateriauisolationthermique) {
		this.unitegciMateriauisolationthermique = unitegciMateriauisolationthermique;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitegci_materiauisolationphonique")
	public String getUnitegciMateriauisolationphonique() {
		if (unitegciMateriauisolationphonique == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_GCI + " - Champs unitegci_materiauisolationphonique - Valeur 'Null' non permise.");
		}
		return unitegciMateriauisolationphonique;
	}

	public void setUnitegciMateriauisolationphonique(String unitegciMateriauisolationphonique) {
		this.unitegciMateriauisolationphonique = unitegciMateriauisolationphonique;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitegci_materiauetancheite")
	public String getUnitegciMateriauetancheite() {
		if (unitegciMateriauetancheite == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_GCI + " - Champs unitegci_materiauetancheite - Valeur 'Null' non permise.");
		}
		return unitegciMateriauetancheite;
	}

	public void setUnitegciMateriauetancheite(String unitegciMateriauetancheite) {
		this.unitegciMateriauetancheite = unitegciMateriauetancheite;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitegci_rezchaussee")
	public String getUnitegciRezchaussee() {
		if (unitegciRezchaussee == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_GCI + " - Champs unitegci_rezchaussee - Valeur 'Null' non permise.");
		}
		return unitegciRezchaussee;
	}

	public void setUnitegciRezchaussee(String unitegciRezchaussee) {
		this.unitegciRezchaussee = unitegciRezchaussee;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitegci_niveau1")
	public String getUnitegciNiveau1() {
		if (unitegciNiveau1 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_GCI + " - Champs unitegci_niveau1 - Valeur 'Null' non permise.");
		}
		return unitegciNiveau1;
	}

	public void setUnitegciNiveau1(String unitegciNiveau1) {
		this.unitegciNiveau1 = unitegciNiveau1;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitegci_niveau2")
	public String getUnitegciNiveau2() {
		if (unitegciNiveau2 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_GCI + " - Champs unitegci_niveau2 - Valeur 'Null' non permise.");
		}
		return unitegciNiveau2;
	}

	public void setUnitegciNiveau2(String unitegciNiveau2) {
		this.unitegciNiveau2 = unitegciNiveau2;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitegci_soussol")
	public String getUnitegciSoussol() {
		if (unitegciSoussol == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_GCI + " - Champs unitegci_soussol - Valeur 'Null' non permise.");
		}
		return unitegciSoussol;
	}

	public void setUnitegciSoussol(String unitegciSoussol) {
		this.unitegciSoussol = unitegciSoussol;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitegci_typetoiture")
	public String getUnitegciTypetoiture() {
		if (unitegciTypetoiture == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_GCI + " - Champs unitegci_typetoiture - Valeur 'Null' non permise.");
		}
		return unitegciTypetoiture;
	}

	public void setUnitegciTypetoiture(String unitegciTypetoiture) {
		this.unitegciTypetoiture = unitegciTypetoiture;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitegci_videcave")
	public String getUnitegciVidecave() {
		if (unitegciVidecave == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_GCI + " - Champs unitegci_videcave - Valeur 'Null' non permise.");
		}
		return unitegciVidecave;
	}

	public void setUnitegciVidecave(String unitegciVidecave) {
		this.unitegciVidecave = unitegciVidecave;
	}

	public Double getUnitegciDiametreint() {
		return unitegciDiametreint;
	}

	public void setUnitegciDiametreint(Double unitegciDiametreint) {
		this.unitegciDiametreint = unitegciDiametreint;
	}

	public Double getUnitegciHauteureau() {
		return unitegciHauteureau;
	}

	public void setUnitegciHauteureau(Double unitegciHauteureau) {
		this.unitegciHauteureau = unitegciHauteureau;
	}

	public Double getUnitegciConcentrationmax() {
		return unitegciConcentrationmax;
	}

	public void setUnitegciConcentrationmax(Double unitegciConcentrationmax) {
		this.unitegciConcentrationmax = unitegciConcentrationmax;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
	
	
}
