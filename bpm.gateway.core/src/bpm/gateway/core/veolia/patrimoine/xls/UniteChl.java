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
@Table (name = PatrimoineDAO.UNITE_CHL)
public class UniteChl {

	@Id
	@Column(name = "unitechl_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String unitechlUnitesocleId;

	//TODO: Liste
	@Transient
	private String unitechlTypedesinfectantstocke;

	//TODO: Liste
	@Transient
	private String unitechlFonction;

	@Transient
	private String unitechlPositionptinjection;

	@Transient
	private String unitechlModeregulation;

	@Transient
	private String unitechlPeriodefonctionnement;

	//TODO: Liste
	@Column(name = "unitechl_telegestconcentrationamont")
	private String unitechlTelegestconcentrationamont;

	//TODO: Liste
	@Transient
	private String unitechlTelegestconcentrationaval;

	@Column(name = "unitechl_typecuve")
	private String unitechlTypecuve;

	@Column(name = "unitechl_nbpompedoseuse")
	private Double unitechlNbpompedoseuse;

	@Column(name = "unitechl_volumecuve")
	private Double unitechlVolumecuve;

	@Column(name = "unitechl_volumeretention")
	private Double unitechlVolumeretention;

	@Column(name = "unitechl_airedepotage")
	private String unitechlAiredepotage;

	//TODO: Liste
	@Transient
	private String unitechlLocal;

	//TODO: Liste
	@Transient
	private String unitechlConformitereglementaire;

	@Column(name = "unitechl_nbbouteille")
	private Double unitechlNbbouteille;

	@Column(name = "unitechl_poidsbouteille")
	private Double unitechlPoidsbouteille;

	//TODO: Liste
	@Column(name = "unitechl_typeinversion")
	private String unitechlTypeinversion;

	@Transient
	private String unitechlPlandetaille;

	@Column(name = "unitechl_commentaire1")
	private String unitechlCommentaire1;

	@Column(name = "unitechl_commentaire2")
	private String unitechlCommentaire2;

	@Column(name = "unitechl_presencemasque")
	private String unitechlPresencemasque;

	@Column(name = "unitechl_positionbouteille")
	private String unitechlPositionbouteille;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitechl_unitesocle_id")
	public String getUnitechlUnitesocleId() {
		if (unitechlUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CHL + " - Champs unitechl_unitesocle_id - Valeur 'Null' non permise.");
		}
		return unitechlUnitesocleId;
	}

	public void setUnitechlUnitesocleId(String unitechlUnitesocleId) {
		this.unitechlUnitesocleId = unitechlUnitesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitechl_typedesinfectantstocke")
	public String getUnitechlTypedesinfectantstocke() {
		if (unitechlTypedesinfectantstocke == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CHL + " - Champs unitechl_typedesinfectantstocke - Valeur 'Null' non permise.");
		}
		return unitechlTypedesinfectantstocke;
	}

	public void setUnitechlTypedesinfectantstocke(String unitechlTypedesinfectantstocke) {
		this.unitechlTypedesinfectantstocke = unitechlTypedesinfectantstocke;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitechl_fonction")
	public String getUnitechlFonction() {
		if (unitechlFonction == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CHL + " - Champs unitechl_fonction - Valeur 'Null' non permise.");
		}
		return unitechlFonction;
	}

	public void setUnitechlFonction(String unitechlFonction) {
		this.unitechlFonction = unitechlFonction;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitechl_positionptinjection")
	public String getUnitechlPositionptinjection() {
		if (unitechlPositionptinjection == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CHL + " - Champs unitechl_positionptinjection - Valeur 'Null' non permise.");
		}
		return unitechlPositionptinjection;
	}

	public void setUnitechlPositionptinjection(String unitechlPositionptinjection) {
		this.unitechlPositionptinjection = unitechlPositionptinjection;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitechl_moderegulation")
	public String getUnitechlModeregulation() {
		if (unitechlModeregulation == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CHL + " - Champs unitechl_moderegulation - Valeur 'Null' non permise.");
		}
		return unitechlModeregulation;
	}

	public void setUnitechlModeregulation(String unitechlModeregulation) {
		this.unitechlModeregulation = unitechlModeregulation;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitechl_periodefonctionnement")
	public String getUnitechlPeriodefonctionnement() {
		if (unitechlPeriodefonctionnement == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CHL + " - Champs unitechl_periodefonctionnement - Valeur 'Null' non permise.");
		}
		return unitechlPeriodefonctionnement;
	}

	public void setUnitechlPeriodefonctionnement(String unitechlPeriodefonctionnement) {
		this.unitechlPeriodefonctionnement = unitechlPeriodefonctionnement;
	}

	public String getUnitechlTelegestconcentrationamont() {
		return unitechlTelegestconcentrationamont;
	}

	public void setUnitechlTelegestconcentrationamont(String unitechlTelegestconcentrationamont) {
		this.unitechlTelegestconcentrationamont = unitechlTelegestconcentrationamont;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitechl_telegestconcentrationaval")
	public String getUnitechlTelegestconcentrationaval() {
		if (unitechlTelegestconcentrationaval == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CHL + " - Champs unitechl_telegestconcentrationaval - Valeur 'Null' non permise.");
		}
		return unitechlTelegestconcentrationaval;
	}

	public void setUnitechlTelegestconcentrationaval(String unitechlTelegestconcentrationaval) {
		this.unitechlTelegestconcentrationaval = unitechlTelegestconcentrationaval;
	}

	public String getUnitechlTypecuve() {
		return unitechlTypecuve;
	}

	public void setUnitechlTypecuve(String unitechlTypecuve) {
		this.unitechlTypecuve = unitechlTypecuve;
	}

	public Double getUnitechlNbpompedoseuse() {
		return unitechlNbpompedoseuse;
	}

	public void setUnitechlNbpompedoseuse(Double unitechlNbpompedoseuse) {
		this.unitechlNbpompedoseuse = unitechlNbpompedoseuse;
	}

	public Double getUnitechlVolumecuve() {
		return unitechlVolumecuve;
	}

	public void setUnitechlVolumecuve(Double unitechlVolumecuve) {
		this.unitechlVolumecuve = unitechlVolumecuve;
	}

	public Double getUnitechlVolumeretention() {
		return unitechlVolumeretention;
	}

	public void setUnitechlVolumeretention(Double unitechlVolumeretention) {
		this.unitechlVolumeretention = unitechlVolumeretention;
	}

	public String getUnitechlAiredepotage() {
		return unitechlAiredepotage;
	}

	public void setUnitechlAiredepotage(String unitechlAiredepotage) {
		this.unitechlAiredepotage = unitechlAiredepotage;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitechl_local")
	public String getUnitechlLocal() {
		if (unitechlLocal == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CHL + " - Champs unitechl_local - Valeur 'Null' non permise.");
		}
		return unitechlLocal;
	}

	public void setUnitechlLocal(String unitechlLocal) {
		this.unitechlLocal = unitechlLocal;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitechl_conformitereglementaire")
	public String getUnitechlConformitereglementaire() {
		if (unitechlConformitereglementaire == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CHL + " - Champs unitechl_conformitereglementaire - Valeur 'Null' non permise.");
		}
		return unitechlConformitereglementaire;
	}

	public void setUnitechlConformitereglementaire(String unitechlConformitereglementaire) {
		this.unitechlConformitereglementaire = unitechlConformitereglementaire;
	}

	public Double getUnitechlNbbouteille() {
		return unitechlNbbouteille;
	}

	public void setUnitechlNbbouteille(Double unitechlNbbouteille) {
		this.unitechlNbbouteille = unitechlNbbouteille;
	}

	public Double getUnitechlPoidsbouteille() {
		return unitechlPoidsbouteille;
	}

	public void setUnitechlPoidsbouteille(Double unitechlPoidsbouteille) {
		this.unitechlPoidsbouteille = unitechlPoidsbouteille;
	}

	public String getUnitechlTypeinversion() {
		return unitechlTypeinversion;
	}

	public void setUnitechlTypeinversion(String unitechlTypeinversion) {
		this.unitechlTypeinversion = unitechlTypeinversion;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitechl_plandetaille")
	public String getUnitechlPlandetaille() {
		if (unitechlPlandetaille == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_CHL + " - Champs unitechl_plandetaille - Valeur 'Null' non permise.");
		}
		return unitechlPlandetaille;
	}

	public void setUnitechlPlandetaille(String unitechlPlandetaille) {
		this.unitechlPlandetaille = unitechlPlandetaille;
	}

	public String getUnitechlCommentaire1() {
		return unitechlCommentaire1;
	}

	public void setUnitechlCommentaire1(String unitechlCommentaire1) {
		this.unitechlCommentaire1 = unitechlCommentaire1;
	}

	public String getUnitechlCommentaire2() {
		return unitechlCommentaire2;
	}

	public void setUnitechlCommentaire2(String unitechlCommentaire2) {
		this.unitechlCommentaire2 = unitechlCommentaire2;
	}
	
	public String getUnitechlPresencemasque() {
		return unitechlPresencemasque;
	}
	
	public void setUnitechlPresencemasque(String unitechlPresencemasque) {
		this.unitechlPresencemasque = unitechlPresencemasque;
	}
	
	public String getUnitechlPositionbouteille() {
		return unitechlPositionbouteille;
	}
	
	public void setUnitechlPositionbouteille(String unitechlPositionbouteille) {
		this.unitechlPositionbouteille = unitechlPositionbouteille;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
}
