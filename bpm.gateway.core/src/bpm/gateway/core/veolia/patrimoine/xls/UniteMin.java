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
@Table (name = PatrimoineDAO.UNITE_MIN)
public class UniteMin {

	@Id
	@Column(name = "unitemin_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String uniteminUnitesocleId;

	@Transient
	private String uniteminNeuralisation;

	@Transient
	private String uniteminRemineralisation;

	@Transient
	private String uniteminDecarbonatation;

	//TODO: Liste
	@Transient
	private String uniteminPreremin;

	@Column(name = "unitemin_preremincommentaire")
	private String uniteminPreremincommentaire;

	@Column(name = "unitemin_prereminreactif")
	private String uniteminPrereminreactif;

	//TODO: Liste
	@Transient
	private String uniteminInterremin;

	@Column(name = "unitemin_interremincommentaire")
	private String uniteminInterremincommentaire;

	@Column(name = "unitemin_interreminreactif")
	private String uniteminInterreminreactif;

	//TODO: Liste
	@Transient
	private String uniteminPostremin;

	@Column(name = "unitemin_postremincommentaire")
	private String uniteminPostremincomentaire;

	@Column(name = "unitemin_postreminreactif")
	private String uniteminPostreminreactif;

	@Column(name = "unitemin_neutralisationreactif")
	private String uniteminNeutralisationreactif;

	@Column(name = "unitemin_typedecarbonatation")
	private String uniteminTypedecarbonatation;

	@Column(name = "unitemin_decarbonatationreactif")
	private String uniteminDecarbonatationreactif;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitemin_unitesocle_id")
	public String getUniteminUnitesocleId() {
		if (uniteminUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_MIN + " - Champs unitemin_unitesocle_id - Valeur 'Null' non permise.");
		}
		return uniteminUnitesocleId;
	}

	public void setUniteminUnitesocleId(String uniteminUnitesocleId) {
		this.uniteminUnitesocleId = uniteminUnitesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitemin_neuralisation")
	public String getUniteminNeuralisation() {
		if (uniteminNeuralisation == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_MIN + " - Champs unitemin_neuralisation - Valeur 'Null' non permise.");
		}
		return uniteminNeuralisation;
	}
	
	public void setUniteminNeuralisation(String uniteminNeuralisation) {
		this.uniteminNeuralisation = uniteminNeuralisation;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitemin_remineralisation")
	public String getUniteminRemineralisation() {
		if (uniteminRemineralisation == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_MIN + " - Champs unitemin_remineralisation - Valeur 'Null' non permise.");
		}
		return uniteminRemineralisation;
	}
	
	public void setUniteminRemineralisation(String uniteminRemineralisation) {
		this.uniteminRemineralisation = uniteminRemineralisation;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitemin_decarbonatation")
	public String getUniteminDecarbonatation() {
		if (uniteminDecarbonatation == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_MIN + " - Champs unitemin_decarbonatation - Valeur 'Null' non permise.");
		}
		return uniteminDecarbonatation;
	}
	
	public void setUniteminDecarbonatation(String uniteminDecarbonatation) {
		this.uniteminDecarbonatation = uniteminDecarbonatation;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitemin_preremin")
	public String getUniteminPreremin() {
//		if (uniteminPreremin == null) {
//			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_MIN + " - Champs unitemin_preremin - Valeur 'Null' non permise.");
//		}
		return uniteminPreremin;
	}

	public void setUniteminPreremin(String uniteminPreremin) {
		this.uniteminPreremin = uniteminPreremin;
	}

	public String getUniteminPreremincommentaire() {
		return uniteminPreremincommentaire;
	}

	public void setUniteminPreremincommentaire(String uniteminPreremincommentaire) {
		this.uniteminPreremincommentaire = uniteminPreremincommentaire;
	}

	public String getUniteminPrereminreactif() {
		return uniteminPrereminreactif;
	}

	public void setUniteminPrereminreactif(String uniteminPrereminreactif) {
		this.uniteminPrereminreactif = uniteminPrereminreactif;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitemin_interremin")
	public String getUniteminInterremin() {
//		if (uniteminInterremin == null) {
//			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_MIN + " - Champs unitemin_interremin - Valeur 'Null' non permise.");
//		}
		return uniteminInterremin;
	}

	public void setUniteminInterremin(String uniteminInterremin) {
		this.uniteminInterremin = uniteminInterremin;
	}

	public String getUniteminInterremincommentaire() {
		return uniteminInterremincommentaire;
	}

	public void setUniteminInterremincommentaire(String uniteminInterremincommentaire) {
		this.uniteminInterremincommentaire = uniteminInterremincommentaire;
	}

	public String getUniteminInterreminreactif() {
		return uniteminInterreminreactif;
	}

	public void setUniteminInterreminreactif(String uniteminInterreminreactif) {
		this.uniteminInterreminreactif = uniteminInterreminreactif;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitemin_postremin")
	public String getUniteminPostremin() {
//		if (uniteminPostremin == null) {
//			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_MIN + " - Champs unitemin_postremin - Valeur 'Null' non permise.");
//		}
		return uniteminPostremin;
	}

	public void setUniteminPostremin(String uniteminPostremin) {
		this.uniteminPostremin = uniteminPostremin;
	}

	public String getUniteminPostremincomentaire() {
		return uniteminPostremincomentaire;
	}

	public void setUniteminPostremincomentaire(String uniteminPostremincomentaire) {
		this.uniteminPostremincomentaire = uniteminPostremincomentaire;
	}

	public String getUniteminPostreminreactif() {
		return uniteminPostreminreactif;
	}

	public void setUniteminPostreminreactif(String uniteminPostreminreactif) {
		this.uniteminPostreminreactif = uniteminPostreminreactif;
	}
	
	public String getUniteminNeutralisationreactif() {
		return uniteminNeutralisationreactif;
	}
	
	public void setUniteminNeutralisationreactif(String uniteminNeutralisationreactif) {
		this.uniteminNeutralisationreactif = uniteminNeutralisationreactif;
	}
	
	public String getUniteminTypedecarbonatation() {
		return uniteminTypedecarbonatation;
	}
	
	public void setUniteminTypedecarbonatation(String uniteminTypedecarbonatation) {
		this.uniteminTypedecarbonatation = uniteminTypedecarbonatation;
	}
	
	public String getUniteminDecarbonatationreactif() {
		return uniteminDecarbonatationreactif;
	}
	
	public void setUniteminDecarbonatationreactif(String uniteminDecarbonatationreactif) {
		this.uniteminDecarbonatationreactif = uniteminDecarbonatationreactif;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
	
	
}
