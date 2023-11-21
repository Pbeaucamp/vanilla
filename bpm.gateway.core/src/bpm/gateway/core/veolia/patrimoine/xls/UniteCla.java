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
@Table (name = PatrimoineDAO.UNITE_CLA)
public class UniteCla {

	@Id
	@Column(name = "unitecla_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String uniteclaUnitesocleId;

	//TODO: Liste
	@Transient
	private String uniteclaCoaginjectionreactif;

	//TODO: Liste
	@Transient
	private String uniteclaCoagreglageph;

	//TODO: Liste
	@Transient
	private String uniteclaFlocinjectionreactif;

	@Transient
	private Double uniteclaCoagflocnbagitateur;

	@Transient
	private Double uniteclaCoagflocnbagitateurfile;

	@Transient
	private Double uniteclaFlotdebitpressurisation;

	//TODO: Liste
	@Transient
	private String uniteclaFlotfiltrationeaupress;

	@Transient
	private Double uniteclaFlotdebitair;

	@Transient
	private Double uniteclaFlotvitesse;

	@Transient
	private Double uniteclaFlottempssejour;

	//TODO: Liste
	@Column(name = "unitecla_typedecantation")
	private String uniteclaTypedecantation;

	@Transient
	private String uniteclaVitessemiroir;

	@Transient
	private String uniteclaVitessehazen;

	@Transient
	private String uniteclaReserveeaupress;

	@Transient
	private String uniteclaBallonpression;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecla_unitesocle_id")
	public String getUniteclaUnitesocleId() {
		if (uniteclaUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CLA + " - Champs unitecla_unitesocle_id - Valeur 'Null' non permise.");
		}
		return uniteclaUnitesocleId;
	}

	public void setUniteclaUnitesocleId(String uniteclaUnitesocleId) {
		this.uniteclaUnitesocleId = uniteclaUnitesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecla_coaginjectionreactif")
	public String getUniteclaCoaginjectionreactif() {
		if (uniteclaCoaginjectionreactif == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CLA + " - Champs unitecla_coaginjectionreactif - Valeur 'Null' non permise.");
		}
		return uniteclaCoaginjectionreactif;
	}

	public void setUniteclaCoaginjectionreactif(String uniteclaCoaginjectionreactif) {
		this.uniteclaCoaginjectionreactif = uniteclaCoaginjectionreactif;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecla_coagreglageph")
	public String getUniteclaCoagreglageph() {
		if (uniteclaCoagreglageph == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CLA + " - Champs unitecla_coagreglageph - Valeur 'Null' non permise.");
		}
		return uniteclaCoagreglageph;
	}

	public void setUniteclaCoagreglageph(String uniteclaCoagreglageph) {
		this.uniteclaCoagreglageph = uniteclaCoagreglageph;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecla_flocinjectionreactif")
	public String getUniteclaFlocinjectionreactif() {
		if (uniteclaFlocinjectionreactif == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CLA + " - Champs unitecla_flocinjectionreactif - Valeur 'Null' non permise.");
		}
		return uniteclaFlocinjectionreactif;
	}

	public void setUniteclaFlocinjectionreactif(String uniteclaFlocinjectionreactif) {
		this.uniteclaFlocinjectionreactif = uniteclaFlocinjectionreactif;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecla_coagflocnbagitateur")
	public Double getUniteclaCoagflocnbagitateur() {
		if (uniteclaCoagflocnbagitateur == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CLA + " - Champs unitecla_coagflocnbagitateur - Valeur 'Null' non permise.");
		}
		return uniteclaCoagflocnbagitateur;
	}

	public void setUniteclaCoagflocnbagitateur(Double uniteclaCoagflocnbagitateur) {
		this.uniteclaCoagflocnbagitateur = uniteclaCoagflocnbagitateur;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecla_coagflocnbagitateurfile")
	public Double getUniteclaCoagflocnbagitateurfile() {
		if (uniteclaCoagflocnbagitateurfile == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CLA + " - Champs unitecla_coagflocnbagitateurfile - Valeur 'Null' non permise.");
		}
		return uniteclaCoagflocnbagitateurfile;
	}

	public void setUniteclaCoagflocnbagitateurfile(Double uniteclaCoagflocnbagitateurfile) {
		this.uniteclaCoagflocnbagitateurfile = uniteclaCoagflocnbagitateurfile;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecla_flotdebitpressurisation")
	public Double getUniteclaFlotdebitpressurisation() {
		if (uniteclaFlotdebitpressurisation == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CLA + " - Champs unitecla_flotdebitpressurisation - Valeur 'Null' non permise.");
		}
		return uniteclaFlotdebitpressurisation;
	}

	public void setUniteclaFlotdebitpressurisation(Double uniteclaFlotdebitpressurisation) {
		this.uniteclaFlotdebitpressurisation = uniteclaFlotdebitpressurisation;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecla_flotfiltrationeaupress")
	public String getUniteclaFlotfiltrationeaupress() {
		if (uniteclaFlotfiltrationeaupress == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CLA + " - Champs unitecla_flotfiltrationeaupress - Valeur 'Null' non permise.");
		}
		return uniteclaFlotfiltrationeaupress;
	}

	public void setUniteclaFlotfiltrationeaupress(String uniteclaFlotfiltrationeaupress) {
		this.uniteclaFlotfiltrationeaupress = uniteclaFlotfiltrationeaupress;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecla_flotdebitair")
	public Double getUniteclaFlotdebitair() {
		if (uniteclaFlotdebitair == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CLA + " - Champs unitecla_flotdebitair - Valeur 'Null' non permise.");
		}
		return uniteclaFlotdebitair;
	}

	public void setUniteclaFlotdebitair(Double uniteclaFlotdebitair) {
		this.uniteclaFlotdebitair = uniteclaFlotdebitair;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecla_flotvitesse")
	public Double getUniteclaFlotvitesse() {
		if (uniteclaFlotvitesse == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CLA + " - Champs unitecla_flotvitesse - Valeur 'Null' non permise.");
		}
		return uniteclaFlotvitesse;
	}

	public void setUniteclaFlotvitesse(Double uniteclaFlotvitesse) {
		this.uniteclaFlotvitesse = uniteclaFlotvitesse;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecla_flottempssejour")
	public Double getUniteclaFlottempssejour() {
		if (uniteclaFlottempssejour == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CLA + " - Champs unitecla_flottempssejour - Valeur 'Null' non permise.");
		}
		return uniteclaFlottempssejour;
	}

	public void setUniteclaFlottempssejour(Double uniteclaFlottempssejour) {
		this.uniteclaFlottempssejour = uniteclaFlottempssejour;
	}

	public String getUniteclaTypedecantation() {
		return uniteclaTypedecantation;
	}

	public void setUniteclaTypedecantation(String uniteclaTypedecantation) {
		this.uniteclaTypedecantation = uniteclaTypedecantation;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecla_vitessemiroir")
	public String getUniteclaVitessemiroir() {
		if (uniteclaVitessemiroir == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CLA + " - Champs unitecla_vitessemiroir - Valeur 'Null' non permise.");
		}
		return uniteclaVitessemiroir;
	}

	public void setUniteclaVitessemiroir(String uniteclaVitessemiroir) {
		this.uniteclaVitessemiroir = uniteclaVitessemiroir;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecla_vitessehazen")
	public String getUniteclaVitessehazen() {
		if (uniteclaVitessehazen == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CLA + " - Champs unitecla_vitessehazen - Valeur 'Null' non permise.");
		}
		return uniteclaVitessehazen;
	}

	public void setUniteclaVitessehazen(String uniteclaVitessehazen) {
		this.uniteclaVitessehazen = uniteclaVitessehazen;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecla_reserveeaupress")
	public String getUniteclaReserveeaupress() {
		if (uniteclaReserveeaupress == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CLA + " - Champs unitecla_reserveeaupress - Valeur 'Null' non permise.");
		}
		return uniteclaReserveeaupress;
	}

	public void setUniteclaReserveeaupress(String uniteclaReserveeaupress) {
		this.uniteclaReserveeaupress = uniteclaReserveeaupress;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitecla_ballonpression")
	public String getUniteclaBallonpression() {
		if (uniteclaBallonpression == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CLA + " - Champs unitecla_ballonpression - Valeur 'Null' non permise.");
		}
		return uniteclaBallonpression;
	}

	public void setUniteclaBallonpression(String uniteclaBallonpression) {
		this.uniteclaBallonpression = uniteclaBallonpression;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
	
	
}
