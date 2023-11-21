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
@Table (name = PatrimoineDAO.UNITE_SEC)
public class UniteSec {

	@Id
	@Column(name = "unitesec_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String unitesecUnitesocleId;

	@Column(name = "unitesec_commentaire1")
	private String unitesecCommentaire1;

	@Column(name = "unitesec_commentaire2")
	private String unitesecCommentaire2;

	@Transient
	private String unitesecConformitecloture;
	
	@Column(name = "unitesec_dateconformitecloture")
	private Date unitesecDateconformitecloture;

	@Transient
	private String unitesecConformiteportail;

	@Column(name = "unitesec_dateconformiteportail")
	private Date unitesecDateconformiteportail;
	
	//TODO: Liste
	@Transient
	private String unitesecControleacces;

	//TODO: Liste
	@Column(name = "unitesec_antiintrusionrdc")
	private String unitesecAntiintrusionrdc;

	//TODO: Liste
	@Column(name = "unitesec_antiintrusionrdctype")
	private String unitesecAntiintrusionrdctype;

	//TODO: Liste
	@Column(name = "unitesec_alarmedesactivation")
	private String unitesecAlarmedesactivation;

	//TODO: Liste
	@Column(name = "unitesec_liaisonalarme")
	private String unitesecLiaisonalarme;

	//TODO: Liste
	@Column(name = "unitesec_antiintrusionfut")
	private String unitesecAntiintrusionfut;

	//TODO: Liste
	@Column(name = "unitesec_antiintrusionfuttype")
	private String unitesecAntiintrusionfuttype;

	//TODO: Liste
	@Column(name = "unitesec_futalarmedesactivation")
	private String unitesecFutalarmedesactivation;

	//TODO: Liste
	@Column(name = "unitesec_futliaisonalarme")
	private String unitesecFutliaisonalarme;

	//TODO: Liste
	@Column(name = "unitesec_antiintrusionsouscuve")
	private String unitesecAntiintrusionsouscuve;

	//TODO: Liste
	@Column(name = "unitesec_antiintrusionsouscuvetype")
	private String unitesecAntiintrusionsouscuvetype;

	//TODO: Liste
	@Column(name = "unitesec_souscuvealarmedesactivation")
	private String unitesecSouscuvealarmedesactivation;

	//TODO: Liste
	@Column(name = "unitesec_souscuveliaisonalarme")
	private String unitesecSouscuveliaisonalarme;

	//TODO: Liste
	@Column(name = "unitesec_antiintrusiontype")
	private String unitesecAntiintrusiontype;

	@Column(name = "unitesec_accestiersmodeop")
	private String unitesecAccestiersmodeop;

	//TODO: Liste
	@Column(name = "unitesec_antiintrusionechelleext")
	private String unitesecAntiintrusionechelleext;

	//TODO: Liste
	@Column(name = "unitesec_antiintrusioncuve1")
	private String unitesecAntiintrusioncuve1;

	//TODO: Liste
	@Transient
	private String unitesecSurvvideo;

	@Column(name = "unitesec_nbcamera")
	private Double unitesecNbcamera;

	@Column(name = "unitesec_positioncamera")
	private String unitesecPositioncamera;

	//TODO: Liste
	@Transient
	private String unitesecCentraleacquisition;

	@Transient
	private String unitesecEnregistrement;

	@Transient
	private String unitesecRapportsecurite;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitesec_unitesocle_id")
	public String getUnitesecUnitesocleId() {
		if (unitesecUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SEC + " - Champs unitesec_unitesocle_id - Valeur 'Null' non permise.");
		}
		return unitesecUnitesocleId;
	}

	public void setUnitesecUnitesocleId(String unitesecUnitesocleId) {
		this.unitesecUnitesocleId = unitesecUnitesocleId;
	}

	public String getUnitesecCommentaire1() {
		return unitesecCommentaire1;
	}

	public void setUnitesecCommentaire1(String unitesecCommentaire1) {
		this.unitesecCommentaire1 = unitesecCommentaire1;
	}

	public String getUnitesecCommentaire2() {
		return unitesecCommentaire2;
	}

	public void setUnitesecCommentaire2(String unitesecCommentaire2) {
		this.unitesecCommentaire2 = unitesecCommentaire2;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitesec_conformitecloture")
	public String getUnitesecConformitecloture() {
		if (unitesecConformitecloture == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SEC + " - Champs unitesec_conformitecloture - Valeur 'Null' non permise.");
		}
		return unitesecConformitecloture;
	}

	public void setUnitesecConformitecloture(String unitesecConformitecloture) {
		this.unitesecConformitecloture = unitesecConformitecloture;
	}
	
	public Date getUnitesecDateconformitecloture() {
		return unitesecDateconformitecloture;
	}
	
	public void setUnitesecDateconformitecloture(Date unitesecDateconformitecloture) {
		this.unitesecDateconformitecloture = unitesecDateconformitecloture;
	}
	

	@Access(AccessType.PROPERTY)
	@Column(name = "unitesec_conformiteportail")
	public String getUnitesecConformiteportail() {
		if (unitesecConformiteportail == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SEC + " - Champs unitesec_conformiteportail - Valeur 'Null' non permise.");
		}
		return unitesecConformiteportail;
	}

	public void setUnitesecConformiteportail(String unitesecConformiteportail) {
		this.unitesecConformiteportail = unitesecConformiteportail;
	}
	
	public Date getUnitesecDateconformiteportail() {
		return unitesecDateconformiteportail;
	}
	
	public void setUnitesecDateconformiteportail(Date unitesecDateconformiteportail) {
		this.unitesecDateconformiteportail = unitesecDateconformiteportail;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitesec_controleacces")
	public String getUnitesecControleacces() {
		if (unitesecControleacces == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SEC + " - Champs unitesec_controleacces - Valeur 'Null' non permise.");
		}
		return unitesecControleacces;
	}

	public void setUnitesecControleacces(String unitesecControleacces) {
		this.unitesecControleacces = unitesecControleacces;
	}

	public String getUnitesecAntiintrusionrdc() {
		return unitesecAntiintrusionrdc;
	}

	public void setUnitesecAntiintrusionrdc(String unitesecAntiintrusionrdc) {
		this.unitesecAntiintrusionrdc = unitesecAntiintrusionrdc;
	}

	public String getUnitesecAntiintrusionrdctype() {
		return unitesecAntiintrusionrdctype;
	}

	public void setUnitesecAntiintrusionrdctype(String unitesecAntiintrusionrdctype) {
		this.unitesecAntiintrusionrdctype = unitesecAntiintrusionrdctype;
	}

	public String getUnitesecAlarmedesactivation() {
		return unitesecAlarmedesactivation;
	}

	public void setUnitesecAlarmedesactivation(String unitesecAlarmedesactivation) {
		this.unitesecAlarmedesactivation = unitesecAlarmedesactivation;
	}

	public String getUnitesecLiaisonalarme() {
		return unitesecLiaisonalarme;
	}

	public void setUnitesecLiaisonalarme(String unitesecLiaisonalarme) {
		this.unitesecLiaisonalarme = unitesecLiaisonalarme;
	}

	public String getUnitesecAntiintrusionfut() {
		return unitesecAntiintrusionfut;
	}

	public void setUnitesecAntiintrusionfut(String unitesecAntiintrusionfut) {
		this.unitesecAntiintrusionfut = unitesecAntiintrusionfut;
	}

	public String getUnitesecAntiintrusionfuttype() {
		return unitesecAntiintrusionfuttype;
	}

	public void setUnitesecAntiintrusionfuttype(String unitesecAntiintrusionfuttype) {
		this.unitesecAntiintrusionfuttype = unitesecAntiintrusionfuttype;
	}

	public String getUnitesecFutalarmedesactivation() {
		return unitesecFutalarmedesactivation;
	}

	public void setUnitesecFutalarmedesactivation(String unitesecFutalarmedesactivation) {
		this.unitesecFutalarmedesactivation = unitesecFutalarmedesactivation;
	}

	public String getUnitesecFutliaisonalarme() {
		return unitesecFutliaisonalarme;
	}

	public void setUnitesecFutliaisonalarme(String unitesecFutliaisonalarme) {
		this.unitesecFutliaisonalarme = unitesecFutliaisonalarme;
	}

	public String getUnitesecAntiintrusionsouscuve() {
		return unitesecAntiintrusionsouscuve;
	}

	public void setUnitesecAntiintrusionsouscuve(String unitesecAntiintrusionsouscuve) {
		this.unitesecAntiintrusionsouscuve = unitesecAntiintrusionsouscuve;
	}

	public String getUnitesecAntiintrusionsouscuvetype() {
		return unitesecAntiintrusionsouscuvetype;
	}

	public void setUnitesecAntiintrusionsouscuvetype(String unitesecAntiintrusionsouscuvetype) {
		this.unitesecAntiintrusionsouscuvetype = unitesecAntiintrusionsouscuvetype;
	}

	public String getUnitesecSouscuvealarmedesactivation() {
		return unitesecSouscuvealarmedesactivation;
	}

	public void setUnitesecSouscuvealarmedesactivation(String unitesecSouscuvealarmedesactivation) {
		this.unitesecSouscuvealarmedesactivation = unitesecSouscuvealarmedesactivation;
	}

	public String getUnitesecSouscuveliaisonalarme() {
		return unitesecSouscuveliaisonalarme;
	}

	public void setUnitesecSouscuveliaisonalarme(String unitesecSouscuveliaisonalarme) {
		this.unitesecSouscuveliaisonalarme = unitesecSouscuveliaisonalarme;
	}

	public String getUnitesecAntiintrusiontype() {
		return unitesecAntiintrusiontype;
	}

	public void setUnitesecAntiintrusiontype(String unitesecAntiintrusiontype) {
		this.unitesecAntiintrusiontype = unitesecAntiintrusiontype;
	}

	public String getUnitesecAccestiersmodeop() {
		return unitesecAccestiersmodeop;
	}

	public void setUnitesecAccestiersmodeop(String unitesecAccestiersmodeop) {
		this.unitesecAccestiersmodeop = unitesecAccestiersmodeop;
	}

	public String getUnitesecAntiintrusionechelleext() {
		return unitesecAntiintrusionechelleext;
	}

	public void setUnitesecAntiintrusionechelleext(String unitesecAntiintrusionechelleext) {
		this.unitesecAntiintrusionechelleext = unitesecAntiintrusionechelleext;
	}

	public String getUnitesecAntiintrusioncuve1() {
		return unitesecAntiintrusioncuve1;
	}

	public void setUnitesecAntiintrusioncuve1(String unitesecAntiintrusioncuve1) {
		this.unitesecAntiintrusioncuve1 = unitesecAntiintrusioncuve1;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitesec_survvideo")
	public String getUnitesecSurvvideo() {
		if (unitesecSurvvideo == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SEC + " - Champs unitesec_survvideo - Valeur 'Null' non permise.");
		}
		return unitesecSurvvideo;
	}

	public void setUnitesecSurvvideo(String unitesecSurvvideo) {
		this.unitesecSurvvideo = unitesecSurvvideo;
	}

	public Double getUnitesecNbcamera() {
		return unitesecNbcamera;
	}

	public void setUnitesecNbcamera(Double unitesecNbcamera) {
		this.unitesecNbcamera = unitesecNbcamera;
	}

	public String getUnitesecPositioncamera() {
		return unitesecPositioncamera;
	}

	public void setUnitesecPositioncamera(String unitesecPositioncamera) {
		this.unitesecPositioncamera = unitesecPositioncamera;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitesec_centraleacquisition")
	public String getUnitesecCentraleacquisition() {
		if (unitesecCentraleacquisition == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SEC + " - Champs unitesec_centraleacquisition - Valeur 'Null' non permise.");
		}
		return unitesecCentraleacquisition;
	}

	public void setUnitesecCentraleacquisition(String unitesecCentraleacquisition) {
		this.unitesecCentraleacquisition = unitesecCentraleacquisition;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitesec_enregistrement")
	public String getUnitesecEnregistrement() {
		if (unitesecEnregistrement == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SEC + " - Champs unitesec_enregistrement - Valeur 'Null' non permise.");
		}
		return unitesecEnregistrement;
	}

	public void setUnitesecEnregistrement(String unitesecEnregistrement) {
		this.unitesecEnregistrement = unitesecEnregistrement;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitesec_rapportsecurite")
	public String getUnitesecRapportsecurite() {
		if (unitesecRapportsecurite == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_SEC + " - Champs unitesec_rapportsecurite - Valeur 'Null' non permise.");
		}
		return unitesecRapportsecurite;
	}

	public void setUnitesecRapportsecurite(String unitesecRapportsecurite) {
		this.unitesecRapportsecurite = unitesecRapportsecurite;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

}
