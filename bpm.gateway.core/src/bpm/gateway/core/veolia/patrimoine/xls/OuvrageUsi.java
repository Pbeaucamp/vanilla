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
@Table (name = PatrimoineDAO.OUVRAGE_USI)
public class OuvrageUsi {

	@Id
	@Column(name = "ouvrageusi_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String ouvrageusiOuvragesocleId;

	@Transient
	private Date ouvrageusiAutorisationprelevdate;

	@Transient
	private String ouvrageusiAutorisationprelevcom;

	@Transient
	private String ouvrageusiCapacitetraitement;

	@Transient
	private String ouvrageusiDebitprodhiver;

	@Transient
	private String ouvrageusiDebitprodinter;

	@Transient
	private String ouvrageusiDebitprodete;

	@Transient
	private String ouvrageusiSuiviqualitatif;

	@Transient
	private String ouvrageusiPj1;

	@Transient
	private String ouvrageusiPj2;

	@Transient
	private String ouvrageusiPj3;

	@Transient
	private String ouvrageusiPj4;

	@Transient
	private String ouvrageusiPj5;
	
	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvrageusi_ouvragesocle_id")
	public String getOuvrageusiOuvragesocleId() {
		if (ouvrageusiOuvragesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_USI + " - Champs ouvrageusi_ouvragesocle_id - Valeur 'Null' non permise.");
		}
		return ouvrageusiOuvragesocleId;
	}

	public void setOuvrageusiOuvragesocleId(String ouvrageusiOuvragesocleId) {
		this.ouvrageusiOuvragesocleId = ouvrageusiOuvragesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvrageusi_autorisationprelevdate")
	public Date getOuvrageusiAutorisationprelevdate() {
		if (ouvrageusiAutorisationprelevdate == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_USI + " - Champs ouvrageusi_autorisationprelevdate - Valeur 'Null' non permise.");
		}
		return ouvrageusiAutorisationprelevdate;
	}

	public void setOuvrageusiAutorisationprelevdate(Date ouvrageusiAutorisationprelevdate) {
		this.ouvrageusiAutorisationprelevdate = ouvrageusiAutorisationprelevdate;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvrageusi_autorisationprelevcom")
	public String getOuvrageusiAutorisationprelevcom() {
		if (ouvrageusiAutorisationprelevcom == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_USI + " - Champs ouvrageusi_autorisationprelevcom - Valeur 'Null' non permise.");
		}
		return ouvrageusiAutorisationprelevcom;
	}

	public void setOuvrageusiAutorisationprelevcom(String ouvrageusiAutorisationprelevcom) {
		this.ouvrageusiAutorisationprelevcom = ouvrageusiAutorisationprelevcom;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvrageusi_capacitetraitement")
	public String getOuvrageusiCapacitetraitement() {
		if (ouvrageusiCapacitetraitement == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_USI + " - Champs ouvrageusi_capacitetraitement - Valeur 'Null' non permise.");
		}
		return ouvrageusiCapacitetraitement;
	}

	public void setOuvrageusiCapacitetraitement(String ouvrageusiCapacitetraitement) {
		this.ouvrageusiCapacitetraitement = ouvrageusiCapacitetraitement;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvrageusi_debitprodhiver")
	public String getOuvrageusiDebitprodhiver() {
		if (ouvrageusiDebitprodhiver == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_USI + " - Champs ouvrageusi_debitprodhiver - Valeur 'Null' non permise.");
		}
		return ouvrageusiDebitprodhiver;
	}

	public void setOuvrageusiDebitprodhiver(String ouvrageusiDebitprodhiver) {
		this.ouvrageusiDebitprodhiver = ouvrageusiDebitprodhiver;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvrageusi_debitprodinter")
	public String getOuvrageusiDebitprodinter() {
		if (ouvrageusiDebitprodinter == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_USI + " - Champs ouvrageusi_debitprodinter - Valeur 'Null' non permise.");
		}
		return ouvrageusiDebitprodinter;
	}

	public void setOuvrageusiDebitprodinter(String ouvrageusiDebitprodinter) {
		this.ouvrageusiDebitprodinter = ouvrageusiDebitprodinter;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvrageusi_debitprodete")
	public String getOuvrageusiDebitprodete() {
		if (ouvrageusiDebitprodete == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_USI + " - Champs ouvrageusi_debitprodete - Valeur 'Null' non permise.");
		}
		return ouvrageusiDebitprodete;
	}

	public void setOuvrageusiDebitprodete(String ouvrageusiDebitprodete) {
		this.ouvrageusiDebitprodete = ouvrageusiDebitprodete;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvrageusi_suiviqualitatif")
	public String getOuvrageusiSuiviqualitatif() {
		if (ouvrageusiSuiviqualitatif == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_USI + " - Champs ouvrageusi_suiviqualitatif - Valeur 'Null' non permise.");
		}
		return ouvrageusiSuiviqualitatif;
	}

	public void setOuvrageusiSuiviqualitatif(String ouvrageusiSuiviqualitatif) {
		this.ouvrageusiSuiviqualitatif = ouvrageusiSuiviqualitatif;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvrageusi_pj1")
	public String getOuvrageusiPj1() {
		if (ouvrageusiPj1 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_USI + " - Champs ouvrageusi_pj1 - Valeur 'Null' non permise.");
		}
		return ouvrageusiPj1;
	}

	public void setOuvrageusiPj1(String ouvrageusiPj1) {
		this.ouvrageusiPj1 = ouvrageusiPj1;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvrageusi_pj2")
	public String getOuvrageusiPj2() {
		if (ouvrageusiPj2 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_USI + " - Champs ouvrageusi_pj2 - Valeur 'Null' non permise.");
		}
		return ouvrageusiPj2;
	}

	public void setOuvrageusiPj2(String ouvrageusiPj2) {
		this.ouvrageusiPj2 = ouvrageusiPj2;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvrageusi_pj3")
	public String getOuvrageusiPj3() {
		if (ouvrageusiPj3 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_USI + " - Champs ouvrageusi_pj3 - Valeur 'Null' non permise.");
		}
		return ouvrageusiPj3;
	}

	public void setOuvrageusiPj3(String ouvrageusiPj3) {
		this.ouvrageusiPj3 = ouvrageusiPj3;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvrageusi_pj4")
	public String getOuvrageusiPj4() {
		if (ouvrageusiPj4 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_USI + " - Champs ouvrageusi_pj4 - Valeur 'Null' non permise.");
		}
		return ouvrageusiPj4;
	}

	public void setOuvrageusiPj4(String ouvrageusiPj4) {
		this.ouvrageusiPj4 = ouvrageusiPj4;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvrageusi_pj5")
	public String getOuvrageusiPj5() {
		if (ouvrageusiPj5 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_USI + " - Champs ouvrageusi_pj5 - Valeur 'Null' non permise.");
		}
		return ouvrageusiPj5;
	}

	public void setOuvrageusiPj5(String ouvrageusiPj5) {
		this.ouvrageusiPj5 = ouvrageusiPj5;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
}
