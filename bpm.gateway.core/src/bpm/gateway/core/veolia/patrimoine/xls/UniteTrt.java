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
@Table (name = PatrimoineDAO.UNITE_TRT)
public class UniteTrt {

	@Id
	@Column(name = "unitetrt_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String unitetrtUnitesocleId;

	//TODO: Liste
	@Transient
	private String unitetrtProcede;

	@Column(name = "unitetrt_centrinb")
	private Double unitetrtCentrinb;

	@Column(name = "unitetrt_pressenb")
	private Double unitetrtPressenb;

	//TODO: Liste
	@Transient
	private String unitetrtInjectionpolymere;

	@Column(name = "unitetrt_nompolymere")
	private String unitetrtNompolymere;

	//TODO: Liste
	@Transient
	private String unitetrt_presencechaulage;

	@Column(name = "unitetrt_chaulageetape")
	private String unitetrtChaulageetape;

	@Transient
	private String unitetrtTypechaulage;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitetrt_unitesocle_id")
	public String getUnitetrtUnitesocleId() {
		if (unitetrtUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_TRT + " - Champs unitetrt_unitesocle_id - Valeur 'Null' non permise.");
		}
		return unitetrtUnitesocleId;
	}

	public void setUnitetrtUnitesocleId(String unitetrtUnitesocleId) {
		this.unitetrtUnitesocleId = unitetrtUnitesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitetrt_procede")
	public String getUnitetrtProcede() {
		if (unitetrtProcede == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_TRT + " - Champs unitetrt_procede - Valeur 'Null' non permise.");
		}
		return unitetrtProcede;
	}

	public void setUnitetrtProcede(String unitetrtProcede) {
		this.unitetrtProcede = unitetrtProcede;
	}

	public Double getUnitetrtCentrinb() {
		return unitetrtCentrinb;
	}

	public void setUnitetrtCentrinb(Double unitetrtCentrinb) {
		this.unitetrtCentrinb = unitetrtCentrinb;
	}

	public Double getUnitetrtPressenb() {
		return unitetrtPressenb;
	}

	public void setUnitetrtPressenb(Double unitetrtPressenb) {
		this.unitetrtPressenb = unitetrtPressenb;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitetrt_injectionpolymere")
	public String getUnitetrtInjectionpolymere() {
		if (unitetrtInjectionpolymere == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_TRT + " - Champs unitetrt_injectionpolymere - Valeur 'Null' non permise.");
		}
		return unitetrtInjectionpolymere;
	}

	public void setUnitetrtInjectionpolymere(String unitetrtInjectionpolymere) {
		this.unitetrtInjectionpolymere = unitetrtInjectionpolymere;
	}

	public String getUnitetrtNompolymere() {
		return unitetrtNompolymere;
	}

	public void setUnitetrtNompolymere(String unitetrtNompolymere) {
		this.unitetrtNompolymere = unitetrtNompolymere;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitetrt_presencechaulage")
	public String getUnitetrt_presencechaulage() {
		if (unitetrt_presencechaulage == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_TRT + " - Champs unitetrt_presencechaulage - Valeur 'Null' non permise.");
		}
		return unitetrt_presencechaulage;
	}

	public void setUnitetrt_presencechaulage(String unitetrt_presencechaulage) {
		this.unitetrt_presencechaulage = unitetrt_presencechaulage;
	}

	public String getUnitetrtChaulageetape() {
		return unitetrtChaulageetape;
	}

	public void setUnitetrtChaulageetape(String unitetrtChaulageetape) {
		this.unitetrtChaulageetape = unitetrtChaulageetape;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "unitetrt_typechaulage")
	public String getUnitetrtTypechaulage() {
		if (unitetrtTypechaulage == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_TRT + " - Champs unitetrt_typechaulage - Valeur 'Null' non permise.");
		}
		return unitetrtTypechaulage;
	}

	public void setUnitetrtTypechaulage(String unitetrtTypechaulage) {
		this.unitetrtTypechaulage = unitetrtTypechaulage;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
	
	
}
