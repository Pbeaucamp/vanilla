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
@Table (name = PatrimoineDAO.UNITE_REC)
public class UniteRec {

	@Id
	@Column(name = "uniterec_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String uniterecUnitesocleId;

	//TODO: Liste
	@Transient
	private String uniterecPresrecirculation1;

	@Column(name = "uniterec_etaperecirculation1")
	private String uniterecEtaperecirculation1;

	@Column(name = "uniterec_recirculationreactif1")
	private String uniterecRecirculationreactif1;

	@Column(name = "uniterec_recirculationdebit1")
	private Double uniterecRecirculationdebit1;

	@Column(name = "uniterec_recirculationpompage1")
	private String uniterecRecirculationpompage1;

	//TODO: Liste
	@Transient
	private String uniterecPresrecirculation2;

	@Column(name = "uniterec_etaperecirculation2")
	private String uniterecEtaperecirculation2;

	@Column(name = "uniterec_recirculationreactif2")
	private String uniterecRecirculationreactif2;

	@Column(name = "uniterec_recirculationdebit2")
	private Double uniterecRecirculationdebit2;

	@Column(name = "uniterec_recirculationpompage2")
	private String uniterecRecirculationpompage2;

	//TODO: Liste
	@Transient
	private String uniterecPresrecirculation3;

	@Column(name = "uniterec_etaperecirculation3")
	private String uniterecEtaperecirculation3;

	@Column(name = "uniterec_recirculationreactif3")
	private String uniterecRecirculationreactif3;

	@Column(name = "uniterec_recirculationdebit3")
	private Double uniterecRecirculationdebit3;

	@Column(name = "uniterec_recirculationpompage3")
	private String uniterecRecirculationpompage3;

	//TODO: Liste
	@Transient
	private String uniterecPresrecirculation4;

	@Column(name = "uniterec_etaperecirculation4")
	private String uniterecEtaperecirculation4;

	@Column(name = "uniterec_recirculationreactif4")
	private String uniterecRecirculationreactif4;

	@Column(name = "uniterec_recirculationdebit4")
	private Double uniterecRecirculationdebit4;

	@Column(name = "uniterec_recirculationpompage4")
	private String uniterecRecirculationpompage4;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniterec_unitesocle_id")
	public String getUniterecUnitesocleId() {
		if (uniterecUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_REC + " - Champs uniterec_unitesocle_id - Valeur 'Null' non permise.");
		}
		return uniterecUnitesocleId;
	}

	public void setUniterecUnitesocleId(String uniterecUnitesocleId) {
		this.uniterecUnitesocleId = uniterecUnitesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniterec_presrecirculation1")
	public String getUniterecPresrecirculation1() {
		if (uniterecPresrecirculation1 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_REC + " - Champs uniterec_presrecirculation1 - Valeur 'Null' non permise.");
		}
		return uniterecPresrecirculation1;
	}

	public void setUniterecPresrecirculation1(String uniterecPresrecirculation1) {
		this.uniterecPresrecirculation1 = uniterecPresrecirculation1;
	}

	public String getUniterecEtaperecirculation1() {
		return uniterecEtaperecirculation1;
	}

	public void setUniterecEtaperecirculation1(String uniterecEtaperecirculation1) {
		this.uniterecEtaperecirculation1 = uniterecEtaperecirculation1;
	}

	public String getUniterecRecirculationreactif1() {
		return uniterecRecirculationreactif1;
	}

	public void setUniterecRecirculationreactif1(String uniterecRecirculationreactif1) {
		this.uniterecRecirculationreactif1 = uniterecRecirculationreactif1;
	}

	public Double getUniterecRecirculationdebit1() {
		return uniterecRecirculationdebit1;
	}

	public void setUniterecRecirculationdebit1(Double uniterecRecirculationdebit1) {
		this.uniterecRecirculationdebit1 = uniterecRecirculationdebit1;
	}

	public String getUniterecRecirculationpompage1() {
		return uniterecRecirculationpompage1;
	}

	public void setUniterecRecirculationpompage1(String uniterecRecirculationpompage1) {
		this.uniterecRecirculationpompage1 = uniterecRecirculationpompage1;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniterec_presrecirculation2")
	public String getUniterecPresrecirculation2() {
		if (uniterecPresrecirculation2 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_REC + " - Champs uniterec_presrecirculation2 - Valeur 'Null' non permise.");
		}
		return uniterecPresrecirculation2;
	}

	public void setUniterecPresrecirculation2(String uniterecPresrecirculation2) {
		this.uniterecPresrecirculation2 = uniterecPresrecirculation2;
	}

	public String getUniterecEtaperecirculation2() {
		return uniterecEtaperecirculation2;
	}

	public void setUniterecEtaperecirculation2(String uniterecEtaperecirculation2) {
		this.uniterecEtaperecirculation2 = uniterecEtaperecirculation2;
	}

	public String getUniterecRecirculationreactif2() {
		return uniterecRecirculationreactif2;
	}

	public void setUniterecRecirculationreactif2(String uniterecRecirculationreactif2) {
		this.uniterecRecirculationreactif2 = uniterecRecirculationreactif2;
	}

	public Double getUniterecRecirculationdebit2() {
		return uniterecRecirculationdebit2;
	}

	public void setUniterecRecirculationdebit2(Double uniterecRecirculationdebit2) {
		this.uniterecRecirculationdebit2 = uniterecRecirculationdebit2;
	}

	public String getUniterecRecirculationpompage2() {
		return uniterecRecirculationpompage2;
	}

	public void setUniterecRecirculationpompage2(String uniterecRecirculationpompage2) {
		this.uniterecRecirculationpompage2 = uniterecRecirculationpompage2;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniterec_presrecirculation3")
	public String getUniterecPresrecirculation3() {
		if (uniterecPresrecirculation3 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_REC + " - Champs uniterec_presrecirculation3 - Valeur 'Null' non permise.");
		}
		return uniterecPresrecirculation3;
	}

	public void setUniterecPresrecirculation3(String uniterecPresrecirculation3) {
		this.uniterecPresrecirculation3 = uniterecPresrecirculation3;
	}

	public String getUniterecEtaperecirculation3() {
		return uniterecEtaperecirculation3;
	}

	public void setUniterecEtaperecirculation3(String uniterecEtaperecirculation3) {
		this.uniterecEtaperecirculation3 = uniterecEtaperecirculation3;
	}

	public String getUniterecRecirculationreactif3() {
		return uniterecRecirculationreactif3;
	}

	public void setUniterecRecirculationreactif3(String uniterecRecirculationreactif3) {
		this.uniterecRecirculationreactif3 = uniterecRecirculationreactif3;
	}

	public Double getUniterecRecirculationdebit3() {
		return uniterecRecirculationdebit3;
	}

	public void setUniterecRecirculationdebit3(Double uniterecRecirculationdebit3) {
		this.uniterecRecirculationdebit3 = uniterecRecirculationdebit3;
	}

	public String getUniterecRecirculationpompage3() {
		return uniterecRecirculationpompage3;
	}

	public void setUniterecRecirculationpompage3(String uniterecRecirculationpompage3) {
		this.uniterecRecirculationpompage3 = uniterecRecirculationpompage3;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniterec_presrecirculation4")
	public String getUniterecPresrecirculation4() {
		if (uniterecPresrecirculation4 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_REC + " - Champs uniterec_presrecirculation4 - Valeur 'Null' non permise.");
		}
		return uniterecPresrecirculation4;
	}

	public void setUniterecPresrecirculation4(String uniterecPresrecirculation4) {
		this.uniterecPresrecirculation4 = uniterecPresrecirculation4;
	}

	public String getUniterecEtaperecirculation4() {
		return uniterecEtaperecirculation4;
	}

	public void setUniterecEtaperecirculation4(String uniterecEtaperecirculation4) {
		this.uniterecEtaperecirculation4 = uniterecEtaperecirculation4;
	}

	public String getUniterecRecirculationreactif4() {
		return uniterecRecirculationreactif4;
	}

	public void setUniterecRecirculationreactif4(String uniterecRecirculationreactif4) {
		this.uniterecRecirculationreactif4 = uniterecRecirculationreactif4;
	}

	public Double getUniterecRecirculationdebit4() {
		return uniterecRecirculationdebit4;
	}

	public void setUniterecRecirculationdebit4(Double uniterecRecirculationdebit4) {
		this.uniterecRecirculationdebit4 = uniterecRecirculationdebit4;
	}

	public String getUniterecRecirculationpompage4() {
		return uniterecRecirculationpompage4;
	}

	public void setUniterecRecirculationpompage4(String uniterecRecirculationpompage4) {
		this.uniterecRecirculationpompage4 = uniterecRecirculationpompage4;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
	
	
}
