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
@Table (name = PatrimoineDAO.UNITE_OXY)
public class UniteOxy {

	@Id
	@Column(name = "uniteoxy_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String uniteoxyUnitesocle_id;

	//TODO: Liste
	@Transient
	private String uniteoxyPrezonation;

	@Column(name = "uniteoxy_prezonationtpscontact")
	private String uniteoxyPrezonationtpscontact;

	@Column(name = "uniteoxy_prezonationnbporeux")
	private Double uniteoxyPrezonationnbporeux;

	@Column(name = "uniteoxy_prezonationcompart")
	private String uniteoxyPrezonationcompart;

	//TODO: Liste
	@Transient
	private String uniteoxyInterzonation;

	@Column(name = "uniteoxy_interzonationnbporeux")
	private Double uniteoxyInterzonationnbporeux;

	@Column(name = "uniteoxy_interzonationtpscontact")
	private Double uniteoxyInterzonationtpscontact;

	//TODO: Liste
	@Transient
	private String uniteoxyPostzonation;

	@Column(name = "uniteoxy_postzonationnbporeux")
	private Double uniteoxyPostzonationnbporeux;

	@Column(name = "uniteoxy_postzonationtpscontact")
	private Double uniteoxyPostzonationtpscontact;

	//TODO: Liste
	@Transient
	private String uniteoxyOzoneur;

	@Column(name = "uniteoxy_ozoneurcapaciteunit")
	private String uniteoxyOzoneurcapaciteunit;

	@Column(name = "uniteoxy_ozoneurnb")
	private Double uniteoxyOzoneurnb;

	@Transient
	private String uniteoxyOzoneursecours;

	@Transient
	private String uniteoxyDestructeurozoneur;

	@Column(name = "uniteoxy_destructeurozonecapacunit")
	private String uniteoxyDestructeurozonecapacunit;

	@Column(name = "uniteoxy_destructeurozonetype")
	private String uniteoxyDestructeurozonetype;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteoxy_unitesocle_id")
	public String getUniteoxyUnitesocle_id() {
		if (uniteoxyUnitesocle_id == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_OXY + " - Champs uniteoxy_unitesocle_id - Valeur 'Null' non permise.");
		}
		return uniteoxyUnitesocle_id;
	}

	public void setUniteoxyUnitesocle_id(String uniteoxyUnitesocle_id) {
		this.uniteoxyUnitesocle_id = uniteoxyUnitesocle_id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteoxy_prezonation")
	public String getUniteoxyPrezonation() {
		if (uniteoxyPrezonation == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_OXY + " - Champs uniteoxy_prezonation - Valeur 'Null' non permise.");
		}
		return uniteoxyPrezonation;
	}

	public void setUniteoxyPrezonation(String uniteoxyPrezonation) {
		this.uniteoxyPrezonation = uniteoxyPrezonation;
	}

	public String getUniteoxyPrezonationtpscontact() {
		return uniteoxyPrezonationtpscontact;
	}

	public void setUniteoxyPrezonationtpscontact(String uniteoxyPrezonationtpscontact) {
		this.uniteoxyPrezonationtpscontact = uniteoxyPrezonationtpscontact;
	}

	public Double getUniteoxyPrezonationnbporeux() {
		return uniteoxyPrezonationnbporeux;
	}

	public void setUniteoxyPrezonationnbporeux(Double uniteoxyPrezonationnbporeux) {
		this.uniteoxyPrezonationnbporeux = uniteoxyPrezonationnbporeux;
	}

	public String getUniteoxyPrezonationcompart() {
		return uniteoxyPrezonationcompart;
	}

	public void setUniteoxyPrezonationcompart(String uniteoxyPrezonationcompart) {
		this.uniteoxyPrezonationcompart = uniteoxyPrezonationcompart;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteoxy_interzonation")
	public String getUniteoxyInterzonation() {
		if (uniteoxyInterzonation == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_OXY + " - Champs uniteoxy_interzonation - Valeur 'Null' non permise.");
		}
		return uniteoxyInterzonation;
	}

	public void setUniteoxyInterzonation(String uniteoxyInterzonation) {
		this.uniteoxyInterzonation = uniteoxyInterzonation;
	}

	public Double getUniteoxyInterzonationnbporeux() {
		return uniteoxyInterzonationnbporeux;
	}

	public void setUniteoxyInterzonationnbporeux(Double uniteoxyInterzonationnbporeux) {
		this.uniteoxyInterzonationnbporeux = uniteoxyInterzonationnbporeux;
	}

	public Double getUniteoxyInterzonationtpscontact() {
		return uniteoxyInterzonationtpscontact;
	}

	public void setUniteoxyInterzonationtpscontact(Double uniteoxyInterzonationtpscontact) {
		this.uniteoxyInterzonationtpscontact = uniteoxyInterzonationtpscontact;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteoxy_postzonation")
	public String getUniteoxyPostzonation() {
		if (uniteoxyPostzonation == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_OXY + " - Champs uniteoxy_postzonation - Valeur 'Null' non permise.");
		}
		return uniteoxyPostzonation;
	}

	public void setUniteoxyPostzonation(String uniteoxyPostzonation) {
		this.uniteoxyPostzonation = uniteoxyPostzonation;
	}

	public Double getUniteoxyPostzonationnbporeux() {
		return uniteoxyPostzonationnbporeux;
	}

	public void setUniteoxyPostzonationnbporeux(Double uniteoxyPostzonationnbporeux) {
		this.uniteoxyPostzonationnbporeux = uniteoxyPostzonationnbporeux;
	}

	public Double getUniteoxyPostzonationtpscontact() {
		return uniteoxyPostzonationtpscontact;
	}

	public void setUniteoxyPostzonationtpscontact(Double uniteoxyPostzonationtpscontact) {
		this.uniteoxyPostzonationtpscontact = uniteoxyPostzonationtpscontact;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteoxy_ozoneur")
	public String getUniteoxyOzoneur() {
		if (uniteoxyOzoneur == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_OXY + " - Champs uniteoxy_ozoneur - Valeur 'Null' non permise.");
		}
		return uniteoxyOzoneur;
	}

	public void setUniteoxyOzoneur(String uniteoxyOzoneur) {
		this.uniteoxyOzoneur = uniteoxyOzoneur;
	}

	public String getUniteoxyOzoneurcapaciteunit() {
		return uniteoxyOzoneurcapaciteunit;
	}

	public void setUniteoxyOzoneurcapaciteunit(String uniteoxyOzoneurcapaciteunit) {
		this.uniteoxyOzoneurcapaciteunit = uniteoxyOzoneurcapaciteunit;
	}

	public Double getUniteoxyOzoneurnb() {
		return uniteoxyOzoneurnb;
	}

	public void setUniteoxyOzoneurnb(Double uniteoxyOzoneurnb) {
		this.uniteoxyOzoneurnb = uniteoxyOzoneurnb;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteoxy_ozoneursecours")
	public String getUniteoxyOzoneursecours() {
		if (uniteoxyOzoneursecours == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_OXY + " - Champs uniteoxy_ozoneursecours - Valeur 'Null' non permise.");
		}
		return uniteoxyOzoneursecours;
	}

	public void setUniteoxyOzoneursecours(String uniteoxyOzoneursecours) {
		this.uniteoxyOzoneursecours = uniteoxyOzoneursecours;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteoxy_destructeurozoneur")
	public String getUniteoxyDestructeurozoneur() {
		if (uniteoxyDestructeurozoneur == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_OXY + " - Champs uniteoxy_destructeurozoneur - Valeur 'Null' non permise.");
		}
		return uniteoxyDestructeurozoneur;
	}

	public void setUniteoxyDestructeurozoneur(String uniteoxyDestructeurozoneur) {
		this.uniteoxyDestructeurozoneur = uniteoxyDestructeurozoneur;
	}

	public String getUniteoxyDestructeurozonecapacunit() {
		return uniteoxyDestructeurozonecapacunit;
	}

	public void setUniteoxyDestructeurozonecapacunit(String uniteoxyDestructeurozonecapacunit) {
		this.uniteoxyDestructeurozonecapacunit = uniteoxyDestructeurozonecapacunit;
	}

	public String getUniteoxyDestructeurozonetype() {
		return uniteoxyDestructeurozonetype;
	}

	public void setUniteoxyDestructeurozonetype(String uniteoxyDestructeurozonetype) {
		this.uniteoxyDestructeurozonetype = uniteoxyDestructeurozonetype;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
	
	
}
