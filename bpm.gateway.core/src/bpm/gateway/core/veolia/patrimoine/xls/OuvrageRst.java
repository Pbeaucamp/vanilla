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
@Table (name = PatrimoineDAO.OUVRAGE_RST)
public class OuvrageRst {

	@Id
	@Column(name = "ouvragerst_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String ouvragerstOuvragesocleId;

	@Transient
	private Double ouvragerstCoteradier;

	@Transient
	private Double ouvragerstCotetropplein;

	@Transient
	private Double ouvragerstHauteurtour;

	@Transient
	private Double ouvragerstHauteurtotale;

	@Transient
	private Integer ouvragerstVolume;

	@Transient
	private Integer ouvragerstVolumeutile;

	//TODO: Liste
	@Transient
	private String ouvragerstAntenniste;

	@Transient
	private String ouvragerstPjvolume;

	@Column(name = "ouvragerst_antennisteliste")
	private String ouvragerstAntennisteliste;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragerst_ouvragesocle_id")
	public String getOuvragerstOuvragesocleId() {
		if (ouvragerstOuvragesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_RST + " - Champs ouvragerst_ouvragesocle_id - Valeur 'Null' non permise.");
		}
		return ouvragerstOuvragesocleId;
	}

	public void setOuvragerstOuvragesocleId(String ouvragerstOuvragesocleId) {
		this.ouvragerstOuvragesocleId = ouvragerstOuvragesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragerst_coteradier")
	public Double getOuvragerstCoteradier() {
		if (ouvragerstCoteradier == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_RST + " - Champs ouvragerst_coteradier - Valeur 'Null' non permise.");
		}
		return ouvragerstCoteradier;
	}

	public void setOuvragerstCoteradier(Double ouvragerstCoteradier) {
		this.ouvragerstCoteradier = ouvragerstCoteradier;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragerst_cotetropplein")
	public Double getOuvragerstCotetropplein() {
		if (ouvragerstCotetropplein == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_RST + " - Champs ouvragerst_cotetropplein - Valeur 'Null' non permise.");
		}
		return ouvragerstCotetropplein;
	}

	public void setOuvragerstCotetropplein(Double ouvragerstCotetropplein) {
		this.ouvragerstCotetropplein = ouvragerstCotetropplein;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragerst_hauteurtour")
	public Double getOuvragerstHauteurtour() {
		if (ouvragerstHauteurtour == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_RST + " - Champs ouvragerst_hauteurtour - Valeur 'Null' non permise.");
		}
		return ouvragerstHauteurtour;
	}

	public void setOuvragerstHauteurtour(Double ouvragerstHauteurtour) {
		this.ouvragerstHauteurtour = ouvragerstHauteurtour;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragerst_hauteurtotale")
	public Double getOuvragerstHauteurtotale() {
		if (ouvragerstHauteurtotale == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_RST + " - Champs ouvragerst_hauteurtotale - Valeur 'Null' non permise.");
		}
		return ouvragerstHauteurtotale;
	}

	public void setOuvragerstHauteurtotale(Double ouvragerstHauteurtotale) {
		this.ouvragerstHauteurtotale = ouvragerstHauteurtotale;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragerst_volume")
	public Integer getOuvragerstVolume() {
		if (ouvragerstVolume == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_RST + " - Champs ouvragerst_volume - Valeur 'Null' non permise.");
		}
		return ouvragerstVolume;
	}

	public void setOuvragerstVolume(Integer ouvragerstVolume) {
		this.ouvragerstVolume = ouvragerstVolume;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragerst_volumeutile")
	public Integer getOuvragerstVolumeutile() {
		if (ouvragerstVolumeutile == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_RST + " - Champs ouvragerst_volumeutile - Valeur 'Null' non permise.");
		}
		return ouvragerstVolumeutile;
	}

	public void setOuvragerstVolumeutile(Integer ouvragerstVolumeutile) {
		this.ouvragerstVolumeutile = ouvragerstVolumeutile;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragerst_antenniste")
	public String getOuvragerstAntenniste() {
		if (ouvragerstAntenniste == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_RST + " - Champs ouvragerst_antenniste - Valeur 'Null' non permise.");
		}
		return ouvragerstAntenniste;
	}

	public void setOuvragerstAntenniste(String ouvragerstAntenniste) {
		this.ouvragerstAntenniste = ouvragerstAntenniste;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragerst_pjvolume")
	public String getOuvragerstPjvolume() {
		if (ouvragerstPjvolume == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_RST + " - Champs ouvragerst_pjvolume - Valeur 'Null' non permise.");
		}
		return ouvragerstPjvolume;
	}

	public void setOuvragerstPjvolume(String ouvragerstPjvolume) {
		this.ouvragerstPjvolume = ouvragerstPjvolume;
	}
	
	public String getOuvragerstAntennisteliste() {
		return ouvragerstAntennisteliste;
	}
	
	public void setOuvragerstAntennisteliste(String ouvragerstAntennisteliste) {
		this.ouvragerstAntennisteliste = ouvragerstAntennisteliste;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
}
