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
@Table (name = PatrimoineDAO.OUVRAGE_LAG)
public class OuvrageLag {

	@Id
	@Column(name = "ouvragelag_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String ouvragelagOuvragesocleId;

	@Transient
	private Double ouvragelagLargeur;

	@Transient
	private Double ouvragelagLongueur;

	@Transient
	private Double ouvragelagProfondeur;

	@Transient
	private Double ouvragelagSurfaceunitaire;

	@Transient
	private Double ouvragelagVolumenominal;

	@Transient
	private Double ouvragelagVolumeStocke;

	@Transient
	private Double ouvragelagHauteur;

	@Transient
	private String ouvragelagMateriauconstruction;

	@Column(name = "ouvragelag_materiauetancheite")
	private String ouvragelagMateriauetancheite;

	@Column(name = "ouvragelag_materiauisolation")
	private String ouvragelagMateriauisolation;

	@Column(name = "ouvragelag_tpssejour")
	private String ouvragelagTpssejour;
	
	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragelag_ouvragesocle_id")
	public String getOuvragelagOuvragesocleId() {
		if (ouvragelagOuvragesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_LAG + " - Champs ouvragelag_ouvragesocle_id - Valeur 'Null' non permise.");
		}
		return ouvragelagOuvragesocleId;
	}

	public void setOuvragelagOuvragesocleId(String ouvragelagOuvragesocleId) {
		this.ouvragelagOuvragesocleId = ouvragelagOuvragesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragelag_largeur")
	public Double getOuvragelagLargeur() {
		if (ouvragelagLargeur == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_LAG + " - Champs ouvragelag_largeur - Valeur 'Null' non permise.");
		}
		return ouvragelagLargeur;
	}

	public void setOuvragelagLargeur(Double ouvragelagLargeur) {
		this.ouvragelagLargeur = ouvragelagLargeur;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragelag_longueur")
	public Double getOuvragelagLongueur() {
		if (ouvragelagLongueur == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_LAG + " - Champs ouvragelag_longueur - Valeur 'Null' non permise.");
		}
		return ouvragelagLongueur;
	}

	public void setOuvragelagLongueur(Double ouvragelagLongueur) {
		this.ouvragelagLongueur = ouvragelagLongueur;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragelag_profondeur")
	public Double getOuvragelagProfondeur() {
		if (ouvragelagProfondeur == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_LAG + " - Champs ouvragelag_profondeur - Valeur 'Null' non permise.");
		}
		return ouvragelagProfondeur;
	}

	public void setOuvragelagProfondeur(Double ouvragelagProfondeur) {
		this.ouvragelagProfondeur = ouvragelagProfondeur;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragelag_surfaceunitaire")
	public Double getOuvragelagSurfaceunitaire() {
		if (ouvragelagSurfaceunitaire == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_LAG + " - Champs ouvragelag_surfaceunitaire - Valeur 'Null' non permise.");
		}
		return ouvragelagSurfaceunitaire;
	}

	public void setOuvragelagSurfaceunitaire(Double ouvragelagSurfaceunitaire) {
		this.ouvragelagSurfaceunitaire = ouvragelagSurfaceunitaire;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragelag_volumenominal")
	public Double getOuvragelagVolumenominal() {
		if (ouvragelagVolumenominal == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_LAG + " - Champs ouvragelag_volumenominal - Valeur 'Null' non permise.");
		}
		return ouvragelagVolumenominal;
	}

	public void setOuvragelagVolumenominal(Double ouvragelagVolumenominal) {
		this.ouvragelagVolumenominal = ouvragelagVolumenominal;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragelag_volume_stocke")
	public Double getOuvragelagVolumeStocke() {
		if (ouvragelagVolumeStocke == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_LAG + " - Champs ouvragelag_volume_stocke - Valeur 'Null' non permise.");
		}
		return ouvragelagVolumeStocke;
	}

	public void setOuvragelagVolumeStocke(Double ouvragelagVolumeStocke) {
		this.ouvragelagVolumeStocke = ouvragelagVolumeStocke;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragelag_hauteur")
	public Double getOuvragelagHauteur() {
		if (ouvragelagHauteur == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_LAG + " - Champs ouvragelag_hauteur - Valeur 'Null' non permise.");
		}
		return ouvragelagHauteur;
	}

	public void setOuvragelagHauteur(Double ouvragelagHauteur) {
		this.ouvragelagHauteur = ouvragelagHauteur;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragelag_materiauconstruction")
	public String getOuvragelagMateriauconstruction() {
		if (ouvragelagMateriauconstruction == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_LAG + " - Champs ouvragelag_materiauconstruction - Valeur 'Null' non permise.");
		}
		return ouvragelagMateriauconstruction;
	}

	public void setOuvragelagMateriauconstruction(String ouvragelagMateriauconstruction) {
		this.ouvragelagMateriauconstruction = ouvragelagMateriauconstruction;
	}

	public String getOuvragelagMateriauetancheite() {
		return ouvragelagMateriauetancheite;
	}

	public void setOuvragelagMateriauetancheite(String ouvragelagMateriauetancheite) {
		this.ouvragelagMateriauetancheite = ouvragelagMateriauetancheite;
	}

	public String getOuvragelagMateriauisolation() {
		return ouvragelagMateriauisolation;
	}

	public void setOuvragelagMateriauisolation(String ouvragelagMateriauisolation) {
		this.ouvragelagMateriauisolation = ouvragelagMateriauisolation;
	}
	
	public String getOuvragelagTpssejour() {
		return ouvragelagTpssejour;
	}
	
	public void setOuvragelagTpssejour(String ouvragelagTpssejour) {
		this.ouvragelagTpssejour = ouvragelagTpssejour;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
}
