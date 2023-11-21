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
@Table (name = PatrimoineDAO.UNITE_HYD)
public class UniteHyd {

	@Id
	@Column(name = "unitehyd_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String unitehydUnitesocleId;

	@Transient
	private String unitehydDescriptif;

	//TODO: Liste
	@Transient
	private String unitehydEtat;

	@Transient
	private Date unitehydDate;

	@Transient
	private String unitehydCommentaire;

	@Transient
	private String unitehydEntretienparticulier;

	//TODO: Liste
	@Transient
	private String unitehydConformitesecurite;

	@Column(name = "unitehyd_descriptif2")
	private String unitehydDescriptif2;

	//TODO: Liste
	@Column(name = "unitehyd_etat2")
	private String unitehydEtat2;

	@Column(name = "unitehyd_date2")
	private Date unitehydDate2;

	@Column(name = "unitehyd_commentaire2")
	private String unitehydCommentaire2;

	@Column(name = "unitehyd_entretienparticulier2")
	private String unitehydEntretienparticulier2;

	//TODO: Liste
	@Column(name = "unitehyd_conformitesecurite2")
	private String unitehydConformitesecurite2;

	@Column(name = "unitehyd_descriptif3")
	private String unitehydDescriptif3;

	//TODO: Liste
	@Column(name = "unitehyd_etat3")
	private String unitehydEtat3;

	@Column(name = "unitehyd_date3")
	private Date unitehydDate3;

	@Column(name = "unitehyd_commentaire3")
	private String unitehydCommentaire3;

	@Column(name = "unitehyd_entretienparticulier3")
	private String unitehydEntretienparticulier3;

	//TODO: Liste
	@Column(name = "unitehyd_conformitesecurite3")
	private String unitehydConformitesecurite3;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitehyd_unitesocle_id")
	public String getUnitehydUnitesocleId() {
		if (unitehydUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_HYD + " - Champs unitehyd_unitesocle_id - Valeur 'Null' non permise.");
		}
		return unitehydUnitesocleId;
	}

	public void setUnitehydUnitesocleId(String unitehydUnitesocleId) {
		this.unitehydUnitesocleId = unitehydUnitesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitehyd_descriptif")
	public String getUnitehydDescriptif() {
		if (unitehydDescriptif == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_HYD + " - Champs unitehyd_descriptif - Valeur 'Null' non permise.");
		}
		return unitehydDescriptif;
	}

	public void setUnitehydDescriptif(String unitehydDescriptif) {
		this.unitehydDescriptif = unitehydDescriptif;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitehyd_etat")
	public String getUnitehydEtat() {
		if (unitehydEtat == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_HYD + " - Champs unitehyd_etat - Valeur 'Null' non permise.");
		}
		return unitehydEtat;
	}

	public void setUnitehydEtat(String unitehydEtat) {
		this.unitehydEtat = unitehydEtat;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitehyd_date")
	public Date getUnitehydDate() {
		if (unitehydDate == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_HYD + " - Champs unitehyd_date - Valeur 'Null' non permise.");
		}
		return unitehydDate;
	}

	public void setUnitehydDate(Date unitehydDate) {
		this.unitehydDate = unitehydDate;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitehyd_commentaire")
	public String getUnitehydCommentaire() {
		if (unitehydCommentaire == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_HYD + " - Champs unitehyd_commentaire - Valeur 'Null' non permise.");
		}
		return unitehydCommentaire;
	}

	public void setUnitehydCommentaire(String unitehydCommentaire) {
		this.unitehydCommentaire = unitehydCommentaire;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitehyd_entretienparticulier")
	public String getUnitehydEntretienparticulier() {
		if (unitehydEntretienparticulier == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_HYD + " - Champs unitehyd_entretienparticulier - Valeur 'Null' non permise.");
		}
		return unitehydEntretienparticulier;
	}

	public void setUnitehydEntretienparticulier(String unitehydEntretienparticulier) {
		this.unitehydEntretienparticulier = unitehydEntretienparticulier;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitehyd_conformitesecurite")
	public String getUnitehydConformitesecurite() {
		if (unitehydConformitesecurite == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_HYD + " - Champs unitehyd_conformitesecurite - Valeur 'Null' non permise.");
		}
		return unitehydConformitesecurite;
	}

	public void setUnitehydConformitesecurite(String unitehydConformitesecurite) {
		this.unitehydConformitesecurite = unitehydConformitesecurite;
	}

	public String getUnitehydDescriptif2() {
		return unitehydDescriptif2;
	}

	public void setUnitehydDescriptif2(String unitehydDescriptif2) {
		this.unitehydDescriptif2 = unitehydDescriptif2;
	}

	public String getUnitehydEtat2() {
		return unitehydEtat2;
	}

	public void setUnitehydEtat2(String unitehydEtat2) {
		this.unitehydEtat2 = unitehydEtat2;
	}

	public Date getUnitehydDate2() {
		return unitehydDate2;
	}

	public void setUnitehydDate2(Date unitehydDate2) {
		this.unitehydDate2 = unitehydDate2;
	}

	public String getUnitehydCommentaire2() {
		return unitehydCommentaire2;
	}

	public void setUnitehydCommentaire2(String unitehydCommentaire2) {
		this.unitehydCommentaire2 = unitehydCommentaire2;
	}

	public String getUnitehydEntretienparticulier2() {
		return unitehydEntretienparticulier2;
	}

	public void setUnitehydEntretienparticulier2(String unitehydEntretienparticulier2) {
		this.unitehydEntretienparticulier2 = unitehydEntretienparticulier2;
	}

	public String getUnitehydConformitesecurite2() {
		return unitehydConformitesecurite2;
	}

	public void setUnitehydConformitesecurite2(String unitehydConformitesecurite2) {
		this.unitehydConformitesecurite2 = unitehydConformitesecurite2;
	}

	public String getUnitehydDescriptif3() {
		return unitehydDescriptif3;
	}

	public void setUnitehydDescriptif3(String unitehydDescriptif3) {
		this.unitehydDescriptif3 = unitehydDescriptif3;
	}

	public String getUnitehydEtat3() {
		return unitehydEtat3;
	}

	public void setUnitehydEtat3(String unitehydEtat3) {
		this.unitehydEtat3 = unitehydEtat3;
	}

	public Date getUnitehydDate3() {
		return unitehydDate3;
	}

	public void setUnitehydDate3(Date unitehydDate3) {
		this.unitehydDate3 = unitehydDate3;
	}

	public String getUnitehydCommentaire3() {
		return unitehydCommentaire3;
	}

	public void setUnitehydCommentaire3(String unitehydCommentaire3) {
		this.unitehydCommentaire3 = unitehydCommentaire3;
	}

	public String getUnitehydEntretienparticulier3() {
		return unitehydEntretienparticulier3;
	}

	public void setUnitehydEntretienparticulier3(String unitehydEntretienparticulier3) {
		this.unitehydEntretienparticulier3 = unitehydEntretienparticulier3;
	}

	public String getUnitehydConformitesecurite3() {
		return unitehydConformitesecurite3;
	}

	public void setUnitehydConformitesecurite3(String unitehydConformitesecurite3) {
		this.unitehydConformitesecurite3 = unitehydConformitesecurite3;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
	
	
}
