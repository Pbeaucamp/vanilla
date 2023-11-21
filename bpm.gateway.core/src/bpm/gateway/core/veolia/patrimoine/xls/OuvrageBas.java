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
@Table (name = PatrimoineDAO.OUVRAGE_BAS)
public class OuvrageBas {

	@Id
	@Column(name = "ouvragebas_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String ouvragebasOuvragesocleId;

	@Transient
	private Double ouvragebasCoteradier;

	@Transient
	private Double ouvragebasCotetropplein;

	@Transient
	private Double ouvragebasHauteurtotale;

	@Transient
	private Double ouvragebasVolume;

	@Column(name = "ouvragebas_volumeutile")
	private Integer ouvragebasVolumeutile;

	//TODO: Liste
	@Transient
	private String ouvragebasPosition;

	//TODO: Liste
	@Transient
	private String ouvragebasAntenniste;
	
	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebas_ouvragesocle_id")
	public String getOuvragebasOuvragesocleId() {
		if (ouvragebasOuvragesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAS + " - Champs ouvragebas_ouvragesocle_id - Valeur 'Null' non permise.");
		}
		return ouvragebasOuvragesocleId;
	}

	public void setOuvragebasOuvragesocleId(String ouvragebasOuvragesocleId) {
		this.ouvragebasOuvragesocleId = ouvragebasOuvragesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebas_coteradier")
	public Double getOuvragebasCoteradier() {
		if (ouvragebasCoteradier == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAS + " - Champs ouvragebas_coteradier - Valeur 'Null' non permise.");
		}
		return ouvragebasCoteradier;
	}

	public void setOuvragebasCoteradier(Double ouvragebasCoteradier) {
		this.ouvragebasCoteradier = ouvragebasCoteradier;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebas_cotetropplein")
	public Double getOuvragebasCotetropplein() {
		if (ouvragebasCotetropplein == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAS + " - Champs ouvragebas_cotetropplein - Valeur 'Null' non permise.");
		}
		return ouvragebasCotetropplein;
	}

	public void setOuvragebasCotetropplein(Double ouvragebasCotetropplein) {
		this.ouvragebasCotetropplein = ouvragebasCotetropplein;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebas_hauteurtotale")
	public Double getOuvragebasHauteurtotale() {
		if (ouvragebasHauteurtotale == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAS + " - Champs ouvragebas_hauteurtotale - Valeur 'Null' non permise.");
		}
		return ouvragebasHauteurtotale;
	}

	public void setOuvragebasHauteurtotale(Double ouvragebasHauteurtotale) {
		this.ouvragebasHauteurtotale = ouvragebasHauteurtotale;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebas_volume")
	public Double getOuvragebasVolume() {
		if (ouvragebasVolume == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAS + " - Champs ouvragebas_volume - Valeur 'Null' non permise.");
		}
		return ouvragebasVolume;
	}

	public void setOuvragebasVolume(Double ouvragebasVolume) {
		this.ouvragebasVolume = ouvragebasVolume;
	}
	
	public Integer getOuvragebasVolumeutile() {
		return ouvragebasVolumeutile;
	}
	
	public void setOuvragebasVolumeutile(Integer ouvragebasVolumeutile) {
		this.ouvragebasVolumeutile = ouvragebasVolumeutile;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebas_position")
	public String getOuvragebasPosition() {
		if (ouvragebasPosition == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAS + " - Champs ouvragebas_position - Valeur 'Null' non permise.");
		}
		return ouvragebasPosition;
	}

	public void setOuvragebasPosition(String ouvragebasPosition) {
		this.ouvragebasPosition = ouvragebasPosition;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragebas_antenniste")
	public String getOuvragebasAntenniste() {
		if (ouvragebasAntenniste == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_BAS + " - Champs ouvragebas_antenniste - Valeur 'Null' non permise.");
		}
		return ouvragebasAntenniste;
	}

	public void setOuvragebasAntenniste(String ouvragebasAntenniste) {
		this.ouvragebasAntenniste = ouvragebasAntenniste;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
}
